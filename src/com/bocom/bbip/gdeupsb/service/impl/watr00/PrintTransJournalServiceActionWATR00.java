package com.bocom.bbip.gdeupsb.service.impl.watr00;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.action.common.OperateFTPAction;
import com.bocom.bbip.eups.action.common.OperateFileAction;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.entity.EupsBatchConsoleInfo;
import com.bocom.bbip.eups.entity.EupsBatchInfoDetail;
import com.bocom.bbip.eups.entity.EupsThdFtpConfig;
import com.bocom.bbip.eups.repository.EupsBatchConsoleInfoRepository;
import com.bocom.bbip.eups.repository.EupsBatchInfoDetailRepository;
import com.bocom.bbip.eups.repository.EupsThdFtpConfigRepository;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.CollectionUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
/**
 * 汕头水费历史交易查询打印
 * @author hefengwen
 *
 */
public class PrintTransJournalServiceActionWATR00 extends BaseAction {
	
	private static Logger logger = LoggerFactory.getLogger(PrintTransJournalServiceActionWATR00.class);
	
	
	@Autowired
	OperateFileAction operateFile;
	@Autowired
	EupsThdFtpConfigRepository eupsThdFtpConfigRepository;
	
	@Override
	public void execute(Context context) throws CoreException,	CoreRuntimeException {
		logger.info("QueryAndPrintTransJournalServiceActionWATR00 execute start ... ...");
		String txnDat = context.getData("txnDat").toString();
		String br = context.getData("br");
        logger.info("txnDat["+txnDat+"]br["+br+"]");
        EupsBatchConsoleInfo eupsBatchConsoleInfo = new EupsBatchConsoleInfo();
        eupsBatchConsoleInfo.setExeDte(DateUtils.parse(txnDat));
        eupsBatchConsoleInfo.setComNo("4450000685");
        List<EupsBatchConsoleInfo> infoList = get(EupsBatchConsoleInfoRepository.class).find(eupsBatchConsoleInfo);
        String batNo;
        if(CollectionUtils.isEmpty(infoList)){
        	throw new CoreException(ErrorCodes.EUPS_ACT_NO_ISEXIST);
        }else{
        	EupsBatchConsoleInfo eupsBatchConsoleInfoA = infoList.get(0);
        	 batNo=eupsBatchConsoleInfoA.getBatNo();
        }
//        GdeupsWatBatInfTmp tmp = new GdeupsWatBatInfTmp();
        EupsBatchInfoDetail tmp = new EupsBatchInfoDetail();
        tmp.setBatNo(batNo);
        
        List<EupsBatchInfoDetail> ret=get(EupsBatchInfoDetailRepository.class).find(tmp);
        List<Map<String,Object>> retMap=(List<Map<String, Object>>) BeanUtils.toMaps(ret);
        BigDecimal totAmt = new BigDecimal("0.0");
        int sumCnt=0;
        for(Map map:retMap){
        	
//        	map.put("thdCusNo", map.get("agtSrvCusId"));
//        	map.put("txnAmt", NumberUtils.centToYuanAsString(map.get("je").toString()));
//        	map.put("bakFld1", "S");
        	map.put("txnDte", txnDat);
//        	totAmt+=Double.valueOf(map.get("txnAmt").toString());
        	if("S".equals(map.get("sts"))){
        		totAmt=totAmt.add(new BigDecimal(map.get("txnAmt").toString()));
        		sumCnt+=1;
        	}
        	
        	
        }
        Map<String, Object> floorMap = new HashMap<String, Object>();
        Map<String, Object> resultMap = new HashMap<String, Object>();
        
        logger.info("@@@@@@@@="+totAmt.toString());
        floorMap.put("sumCnt", ret.size());
        floorMap.put("sumAmt", totAmt.toString());
        resultMap.put("floor", floorMap);
        resultMap.put("detail", retMap);
        
        
        EupsThdFtpConfig eupsThdFtpConfig = eupsThdFtpConfigRepository.findOne("watTransJnlFile");
        String fileName = "wat"+txnDat;
        // 生成文件
     	operateFile.createCheckFile(eupsThdFtpConfig, "watTransJnl", fileName, resultMap);
     	
     // 将生成的文件上传至指定服务器
     		eupsThdFtpConfig.setLocFleNme(fileName);
     		eupsThdFtpConfig.setRmtFleNme(fileName);
     		logger.info("@@@@@@@@@@@@eupsThdFtpConfig=" + eupsThdFtpConfig);
     		OperateFTPAction operateFTP = new OperateFTPAction();
     		operateFTP.putCheckFile(eupsThdFtpConfig);
//        EupsThdFtpConfigRepository eupsThdFtpConfigRepository = get(EupsThdFtpConfigRepository.class);
//		ReportHelper reportHelper = get(ReportHelper.class);
//		//MFTPConfigInfo mftpConfigInfo = reportHelper.getMFTPConfigInfo(eupsThdFtpConfigRepository);
//		//logger.info((new StringBuilder("mftpConfigInfo:>>>>").append(BeanUtils.toMap(mftpConfigInfo))).toString());
//		
//		VelocityTemplatedReportRender render = new VelocityTemplatedReportRender();
//		try {
//			render.afterPropertiesSet();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		Map<String,String> map = new HashMap<String,String>();
//		map.put("sample", "config/report/watr00/watr00_printTransJournal.vm");
//		render.setReportNameTemplateLocationMapping(map);
//		context.setData("eles", retMap);
//		String result = render.renderAsString("sample", context);
//		logger.info(result);
//		String date = DateUtils.format(new Date(), DateUtils.STYLE_HHmmss);
//		StringBuffer fileName = new StringBuffer((new StringBuilder("WATR00"+br+txnDat).append(date).toString()));
////		reportHelper.createFileAndSendMFTP(context, result, fileName, mftpConfigInfo);
       context.setData("filNam", fileName);
		logger.info("QueryAndPrintTransJournalServiceActionWATR00 execute end ... ...");
	}
	


}
