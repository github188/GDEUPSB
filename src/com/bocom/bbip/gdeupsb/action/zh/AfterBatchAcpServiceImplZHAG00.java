package com.bocom.bbip.gdeupsb.action.zh;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.comp.BBIPPublicService;
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
import com.bocom.bbip.file.MftpTransfer;
import com.bocom.bbip.gdeupsb.action.common.BatchFileCommon;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.entity.GDEupsBatchConsoleInfo;
import com.bocom.bbip.gdeupsb.entity.GDEupsZhAGBatchTemp;
import com.bocom.bbip.gdeupsb.repository.GDEupsZHAGBatchTempRepository;
import com.bocom.bbip.utils.Assert;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.StringUtils;
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
	@Autowired
	BBIPPublicService bbipPublicService;
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
        String comNos=gdEupsBatchConsoleInfo.getComNo();
        if(comNos.equals("4440001488") || comNos.equals("4440000101")){
	        for (EupsBatchInfoDetail eupsBatchInfoDetail : list) {
	        	Map<String , Object > map=new HashMap<String, Object>();
	        	String sqn=eupsBatchInfoDetail.getRmk1();
	        	map.put("sqn", sqn);
	        	String txnAmts=eupsBatchInfoDetail.getTxnAmt().scaleByPowerOfTen(2).intValue()+"";
	        	while(txnAmts.length()<12){
	        			txnAmts="0"+txnAmts;
	        	}
	        	 map.put("txnAmt",txnAmts);
				 String sts=eupsBatchInfoDetail.getSts();
				 if(sts.equals("S")){
					 map.put("rsvFld2","Y");
				 }else{
					 	String errMsg=eupsBatchInfoDetail.getErrMsg();
					 	if(StringUtils.isNotEmpty(errMsg)){
							 	if(errMsg.length()>6){
							 		errMsg=eupsBatchInfoDetail.getErrMsg().substring(0,6);
							 	}
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
        }else if(comNos.equals("4440000166")){
        	for (EupsBatchInfoDetail eupsBatchInfoDetail : list) {
        			String sqn=eupsBatchInfoDetail.getRmk1();
        			String sts=eupsBatchInfoDetail.getSts();
        			GDEupsZhAGBatchTemp gdEupsZhAGBatchTemp=gdEupsZHAGBatchTempRepository.findOne(sqn);
        			String rsvFld4="1";
        			if(sts.equals("S")){
        				rsvFld4="1";
        			}else{
        				String errMsg=eupsBatchInfoDetail.getErrMsg();
					 	if(errMsg.length()>6){
					 		errMsg=eupsBatchInfoDetail.getErrMsg().substring(0,6);
					 	}
        				if(errMsg.equals("TPM050")){
        					rsvFld4="2";
				 		}else if(errMsg.equals("TPM055")){
				 			rsvFld4="3";
				 		}else if(errMsg.equals("PDM252")){
				 			rsvFld4="4";
				 		}else{
				 			rsvFld4="5";
				 		}
        			}
        			gdEupsZhAGBatchTemp.setRsvFld4(rsvFld4);
        			gdEupsZHAGBatchTempRepository.updateTemp(gdEupsZhAGBatchTemp);
        	}
        }
        EupsThdFtpConfig config=get(EupsThdFtpConfigRepository.class).findOne("zhag00");
        
      //拼装Map文件
		Map<String, Object> resultMap = createFileMap(context,gdEupsBatchConsoleInfo);
		
		String formatOut=findFormat(gdEupsBatchConsoleInfo.getComNo());
		String fileName=gdEupsBatchConsoleInfo.getRsvFld1();
		config.setLocFleNme(fileName);
        ((OperateFileAction)get("opeFile")).createCheckFile(config, formatOut, fileName, resultMap);
        config.setRmtFleNme(fileName);
        //放置到前台文件
		try {			
			bbipPublicService.sendFileToBBOS(new File(config.getLocDir(),fileName), fileName, MftpTransfer.FTYPE_NORMAL);		
		}catch (Exception e) {
			throw new CoreException(ErrorCodes.EUPS_MFTP_FILEDOWN_FAIL);
		}
		
        
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
				Map<String, Object> mapHeader=new HashMap<String, Object>();
				mapHeader.put("rsvFld1", "75");
				mapHeader.put("rsvFld2", "006");
				mapHeader.put("totCnt", gdEupsBatchConsoleInfo.getTotCnt());
				mapHeader.put("STotCnt", gdEupsBatchConsoleInfo.getSucTotCnt());
				mapHeader.put("STotAmt", gdEupsBatchConsoleInfo.getSucTotAmt());
				mapHeader.put("FTotCnt", gdEupsBatchConsoleInfo.getFalTotCnt());
				mapHeader.put("FTotAmt", gdEupsBatchConsoleInfo.getFalTotAmt());
				resultMap.put(ParamKeys.EUPS_FILE_HEADER, mapHeader);
				//文件内容 
				List<GDEupsZhAGBatchTemp> detailList=gdEupsZHAGBatchTempRepository.findByBatNo(gdEupsBatchConsoleInfo.getBatNo());
				resultMap.put(ParamKeys.EUPS_FILE_DETAIL, BeanUtils.toMaps(detailList));
				logger.info("===============End   BatchDataResultFileAction  createFileMap");	
				return resultMap;
		}
		
}
