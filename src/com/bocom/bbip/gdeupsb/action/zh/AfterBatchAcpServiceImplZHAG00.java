package com.bocom.bbip.gdeupsb.action.zh;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Date;
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
import com.bocom.bbip.eups.entity.EupsThdFtpConfig;
import com.bocom.bbip.eups.repository.EupsThdFtpConfigRepository;
import com.bocom.bbip.eups.spi.service.batch.AfterBatchAcpService;
import com.bocom.bbip.eups.spi.vo.AfterBatchAcpDomain;
import com.bocom.bbip.file.MftpTransfer;
import com.bocom.bbip.gdeupsb.entity.GDEupsZhAGBatchTemp;
import com.bocom.bbip.gdeupsb.repository.GDEupsZHAGBatchTempRepository;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

public class AfterBatchAcpServiceImplZHAG00 extends BaseAction implements AfterBatchAcpService {
	private static final Log logger = LogFactory.getLog(AfterBatchAcpServiceImplZHAG00.class);
	@Autowired
	GDEupsZHAGBatchTempRepository gdEupsZHAGBatchTempRepository;
	@Autowired
	OperateFTPAction operateFTPAction;
	@Autowired
	BBIPPublicService bbipPublicService;
	@Autowired
	OperateFileAction operateFileAction;
	@Override
	public void afterBatchDeal(AfterBatchAcpDomain arg0, Context context)
			throws CoreException {
		logger.info("================返盘文件处理开始");
		String batNos=context.getData("batNo").toString();
		String comNos=context.getData("comNo").toString();
		EupsThdFtpConfig eupsThdFtpConfig=get(EupsThdFtpConfigRepository.class).findOne("zhag00");
		String resultName=batNos+".result";
		List<Map<String, Object>> mapList=operateFileAction.pareseFile(eupsThdFtpConfig, "batchReturn");
		//成功 失败金额
		BigDecimal sucTotAmt=new BigDecimal("0.00");
		BigDecimal falTotAmt=new BigDecimal("0.00");
		//成功失败笔数
		int sucTotCnt=0;
		int falTotCnt=0;
		if(comNos.equals("4440001488") || comNos.equals("4440000101")){
	        for (int i=1;i<mapList.size();i++) {
	        	Map<String, Object> map=mapList.get(i);
	        	String sqn=(String)map.get("sqn");
	        	map.put("sqn", sqn);
	        	BigDecimal txnAmt=new BigDecimal((String)map.get("txnAmt"));
	        	String txnAmts=txnAmt.scaleByPowerOfTen(2).intValue()+"";
	        	while(txnAmts.length()<12){
	        			txnAmts="0"+txnAmts;
	        	}
	        	 map.put("txnAmt",txnAmts);
				 String sts=(String)map.get("sts");
				 if(sts.equals("S")){
					 map.put("rsvFld2","Y");
				 }else{
					 	String errMsg=(String)map.get("errMsg");
					 	if(errMsg.length()>6){
					 		errMsg=errMsg.substring(0,6);
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
        	for (int i=1;i<mapList.size();i++) {
        			Map<String, Object> map=mapList.get(i);
        			String sqn=(String)map.get("sqn");
        			String sts=(String)map.get("sts");
        			GDEupsZhAGBatchTemp gdEupsZhAGBatchTemp=gdEupsZHAGBatchTempRepository.findOne(sqn);
        			BigDecimal txnAmt=new BigDecimal(gdEupsZhAGBatchTemp.getTxnAmt());
        			String rsvFld5=gdEupsZhAGBatchTemp.getRsvFld5();
        			if(sts.equals("S")){
        				rsvFld5="1"+rsvFld5;
        				sucTotAmt=sucTotAmt.add(txnAmt);
        				sucTotCnt++;
        			}else{
        				falTotAmt=falTotAmt.add(txnAmt);
        				falTotCnt++;
        				String errMsg=(String)map.get("errMsg");
					 	if(errMsg.length()>6){
					 		errMsg=errMsg.substring(0,6);
					 	}
        				if(errMsg.equals("TPM050")){
        					rsvFld5="2"+rsvFld5;
				 		}else if(errMsg.equals("TPM055")){
				 			rsvFld5="3"+rsvFld5;
				 		}else if(errMsg.equals("PDM252")){
				 			rsvFld5="4"+rsvFld5;
				 		}else{
				 			rsvFld5="5"+rsvFld5;
				 		}
        			}
        			gdEupsZhAGBatchTemp.setRsvFld5(rsvFld5);
        			gdEupsZHAGBatchTempRepository.updateTemp(gdEupsZhAGBatchTemp);
        	}
        }
        EupsThdFtpConfig config=get(EupsThdFtpConfigRepository.class).findOne("zhag00");
        
      //拼装Map文件
        Map<String, Object> mapHeader=new HashMap<String, Object>();
        mapHeader.put("rsvFld1", "75");
        mapHeader.put("rsvFld2", "006");
        mapHeader.put("rsvFld3", mapList.size()-1);//记录条数
        mapHeader.put("STotCnt", sucTotCnt);//成功笔数
        mapHeader.put("STotAmt", sucTotAmt);//成功金额
        mapHeader.put("FTotCnt", falTotCnt);//失败笔数
        mapHeader.put("FTotAmt", sucTotAmt);//失败金额
        
		Map<String, Object> resultMap = createFileMap(context,mapHeader);
		
		String formatOut=findFormat(comNos);
		String fileName=comNos+"_"+DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMdd);
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
        context.setData("comNo",  comNos);
        context.setData("subDte",  null);
        context.setData("comNme", fileName );
       context.setData("batSts",  null);
        context.setData("totCnt",  null);
        context.setData("totAmt",  null);
        context.setData("sucTotCnt"  ,null);
        context.setData("sucTotAmt", null);
       
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
		public Map<String, Object> createFileMap(Context context,Map<String, Object> map){
				logger.info("===============Start  BatchDataResultFileAction  createFileMap");	
				Map<String, Object> resultMap=new HashMap<String, Object>();		
				resultMap.put(ParamKeys.EUPS_FILE_HEADER, map);
				//文件内容  
				GDEupsZhAGBatchTemp gdEupsZhAGBatchTemp=new GDEupsZhAGBatchTemp();
				gdEupsZhAGBatchTemp.setComNo((String)map.get("comNo"));
				List<GDEupsZhAGBatchTemp> detailList=gdEupsZHAGBatchTempRepository.find(gdEupsZhAGBatchTemp);
				resultMap.put(ParamKeys.EUPS_FILE_DETAIL, BeanUtils.toMaps(detailList));
				logger.info("===============End   BatchDataResultFileAction  createFileMap");	
				return resultMap;
		}
		
}
