package com.bocom.bbip.gdeupsb.action.zh;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.action.common.OperateFTPAction;
import com.bocom.bbip.eups.action.common.OperateFileAction;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsBatchInfoDetail;
import com.bocom.bbip.eups.entity.EupsThdFtpConfig;
import com.bocom.bbip.eups.repository.EupsBatchInfoDetailRepository;
import com.bocom.bbip.eups.repository.EupsThdFtpConfigRepository;
import com.bocom.bbip.eups.spi.service.batch.AfterBatchAcpService;
import com.bocom.bbip.eups.spi.vo.AfterBatchAcpDomain;
import com.bocom.bbip.gdeupsb.action.common.BatchFileCommon;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.entity.GDEupsBatchConsoleInfo;
import com.bocom.bbip.gdeupsb.entity.GDEupsZhAGBatchTemp;
import com.bocom.bbip.gdeupsb.repository.GDEupsZHAGBatchTempRepository;
import com.bocom.bbip.utils.Assert;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

public class AfterBatchAcpServiceImplZHAG00 extends BaseAction implements AfterBatchAcpService {
	private static final Log logger = LogFactory.getLog(AfterBatchAcpServiceImplZHAG00.class);
	@Autowired
	EupsBatchInfoDetailRepository eupsBatchInfoDetailRepository;
	@Autowired
	GDEupsZHAGBatchTempRepository gdEupsZHAGBatchTempRepository;
	@Autowired
	OperateFTPAction operateFTPAction;
	@Override
	public void afterBatchDeal(AfterBatchAcpDomain arg0, Context context)
			throws CoreException {
		logger.info("================返盘文件处理开始");
		String batNos=context.getData("batNo").toString();
		GDEupsBatchConsoleInfo gdEupsBatchConsoleInfo=((BatchFileCommon)get(GDConstants.BATCH_FILE_COMMON_UTILS)).eupsBatchConSoleInfoAndgdEupsBatchConSoleInfo(context);
		//返回结果集合
		EupsBatchInfoDetail eupsBatchInfoDetails=new EupsBatchInfoDetail();
		eupsBatchInfoDetails.setBatNo(batNos);
        List<EupsBatchInfoDetail>list= eupsBatchInfoDetailRepository.find(eupsBatchInfoDetails);
        Assert.isNotEmpty(list, ErrorCodes.EUPS_QUERY_NO_DATA);
        for (EupsBatchInfoDetail eupsBatchInfoDetail : list) {
        	Map<String , Object > map=new HashMap<String, Object>();
        	String sqn=eupsBatchInfoDetail.getRmk1();
        	map.put("sqn", sqn);
        	String txnAmt=eupsBatchInfoDetail.getTxnAmt().scaleByPowerOfTen(2)+"";
        	if(txnAmt.length()<12){
        			txnAmt="0"+txnAmt;
        	}
        	 map.put("txnAmt",txnAmt);
			 String sts=eupsBatchInfoDetail.getSts();
			 if(sts.equals("S")){
				 map.put("rsvFld2","Y");
			 }else{
				 	String errMsg=eupsBatchInfoDetail.getErrMsg();
				 	if(errMsg.length()>6){
				 		errMsg=eupsBatchInfoDetail.getErrMsg().substring(0,6);
				 	}
				 	if(errMsg.equals("TPM050")){
				 			map.put("rsvFld2","E");
				 	}else if(errMsg.equals("SDM015")){
				 			map.put("rsvFld2","B");
				 	}else if(errMsg.equals("CB1004") || errMsg.equals("PDM252")){
				 			map.put("rsvFld2","A");
				 	}else{
				 			map.put("rsvFld2","O");
				 	}
			 }
			 gdEupsZHAGBatchTempRepository.updateRsvFld2(map);
		}
        EupsThdFtpConfig config=get(EupsThdFtpConfigRepository.class).findOne("zhag00");
        
      //拼装Map文件
		Map<String, Object> resultMap = createFileMap(context,gdEupsBatchConsoleInfo);
		
		String formatOut=findFormat(gdEupsBatchConsoleInfo.getComNo());
		String fileName=gdEupsBatchConsoleInfo.getComNo()+"_"+DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMdd)+".txt";
		config.setLocFleNme(fileName);
        ((OperateFileAction)get("opeFile")).createCheckFile(config, formatOut, fileName, resultMap);
        config.setRmtFleNme(fileName);
        String path="/home/weblogic/JumpServer/WEB-INF/save/tfiles/" + context.getData(ParamKeys.BR)+ "/" ;
        config.setRmtWay(path);
        //放置到前台文件
        operateFTPAction.putCheckFile(config);
        
        context.setData("ApFmt",  "48211");
        context.setData("batNo",  gdEupsBatchConsoleInfo.getBatNo());
        context.setData("comNo",  gdEupsBatchConsoleInfo.getComNo());
        context.setData("subDte",  gdEupsBatchConsoleInfo.getSubDte());
        context.setData("comNme", fileName );
       context.setData("batSts",  gdEupsBatchConsoleInfo.getBatSts());
        context.setData("totCnt",  gdEupsBatchConsoleInfo.getTotCnt());
        context.setData("totAmt",  gdEupsBatchConsoleInfo.getTotAmt());
        context.setData("sucTotCnt"  ,gdEupsBatchConsoleInfo.getSucTotCnt());
        context.setData("sucTotAmt", gdEupsBatchConsoleInfo.getSucTotAmt());
       
		 logger.info("================返盘文件处理结束");

	}
	  private String findFormat(final String comNo) {
		  InputStream location=null;
		try {
			location = getClass().getClassLoader().
			getResourceAsStream("config/fmt/fileFmt/zh/transOut.properties");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		  Properties prop = new Properties();
		    Map<String,String> propMap = new HashMap<String,String>();
		    try {
		      prop.load(location);
		    } catch (IOException e) {
		      e.printStackTrace();
		    }
		    for (Iterator localIterator = prop.keySet().iterator(); localIterator.hasNext(); ) { Object key = localIterator.next();
		      String keyStr = key.toString();
		      String value = prop.getProperty(keyStr);
		      propMap.put(keyStr, value);
		    }
		  
		  return propMap.get(comNo);
	  }
		/**
		 * 拼装Map返回文件
		 */
		public Map<String, Object> createFileMap(Context context,GDEupsBatchConsoleInfo gdEupsBatchConsoleInfo){
				logger.info("===============Start  BatchDataResultFileAction  createFileMap");	
				Map<String, Object> resultMap=new HashMap<String, Object>();		
				resultMap.put(ParamKeys.EUPS_FILE_HEADER, BeanUtils.toMap(gdEupsBatchConsoleInfo));
				//文件内容 
				List<GDEupsZhAGBatchTemp> detailList=gdEupsZHAGBatchTempRepository.findByBatNo(gdEupsBatchConsoleInfo.getBatNo());
				resultMap.put(ParamKeys.EUPS_FILE_DETAIL, BeanUtils.toMaps(detailList));
				logger.info("===============End   BatchDataResultFileAction  createFileMap");	
				return resultMap;
		}
		
}
