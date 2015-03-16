package com.bocom.bbip.gdeupsb.action.gas;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.action.eupsreport.ReportHelper;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsTransJournal;
import com.bocom.bbip.eups.entity.MFTPConfigInfo;
import com.bocom.bbip.eups.repository.EupsThdFtpConfigRepository;
import com.bocom.bbip.eups.repository.EupsTransJournalRepository;
import com.bocom.bbip.file.reporting.impl.VelocityTemplatedReportRender;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class PrintReportServiceActionPGAS00 extends BaseAction {

	private static Logger logger = LoggerFactory
			.getLogger(PrintReportServiceActionPGAS00.class);

	public void execute(Context context) throws CoreException,
			CoreRuntimeException {
		logger.info("Enter in PrintReportServiceActionPGAS00....");
		logger.info("===============context:" + context);
		
//		前端传入开始日期结束日期报表类型
//		开始日期	beginDate
//		结束日期	endDate
//		报表类型	prtFlg
		
		//报表类型非1非2，直接报错，输入错误
		if(!("1".equals(context.getData(ParamKeys.PRT_FLG)) || "2".equals(context.getData(ParamKeys.PRT_FLG)))){
			throw new CoreException("wrong input");
		}
		
		////////////////////FOR  TEST       //////////////
		context.setData("pNodNo1", "CNJT");
		context.setData("pTlrId1", "CNJT0001");
		context.setData("txnDte1", DateUtils.format((Date)context.getData(ParamKeys.TXN_DTE), DateUtils.STYLE_SIMPLE_DATE));
		//////////////////////////////////////////////////
		
		//TODO 判断需打印的报表类型，1—代扣成功报表 2—代扣失败报表   					DONE
		//配VM文件
		StringBuffer fileName = null;
		String br = context.getData(ParamKeys.BR);
		String beginDate = context.getData("beginDate");
		String endDate = context.getData("endDate");
		
		context.setData("beginDateY", beginDate.substring(0, 4));
		context.setData("beginDateM", beginDate.substring(5, 7));
		context.setData("beginDateD", beginDate.substring(8));
		
		context.setData("endDateY", endDate.substring(0, 4));
		context.setData("endDateM", endDate.substring(5, 7));
		context.setData("endDateD", endDate.substring(8));
		
		logger.info("=============context:" + context);
		EupsTransJournal eupsJnl = new EupsTransJournal();
		eupsJnl.setEupsBusTyp((String) context.getData(ParamKeys.EUPS_BUSS_TYPE));
		eupsJnl.setBr((String)context.getData(ParamKeys.BR));
		eupsJnl.setBeginDate(DateUtils.parse(beginDate, DateUtils.STYLE_SIMPLE_DATE));
		eupsJnl.setEndDate(DateUtils.parse(endDate, DateUtils.STYLE_SIMPLE_DATE));
		if("1".equals(context.getData(ParamKeys.PRT_FLG))){
			eupsJnl.setTxnSts("S");
			fileName = new StringBuffer((new StringBuilder("(惠州分行)燃气代扣成功报表_"+ br + "_" + beginDate + "_" + endDate).append(".txt").toString()));
		}
		if("2".equals(context.getData(ParamKeys.PRT_FLG))){
			eupsJnl.setTxnSts("F");
			fileName = new StringBuffer((new StringBuilder("(惠州分行)燃气代扣失败报表_"+ br + "_" + beginDate + "_" + endDate).append(".txt").toString()));
		}
		logger.info("==============fileName:" + fileName);
		
		List<EupsTransJournal> prtList = get(EupsTransJournalRepository.class).findLocalJournal(eupsJnl);
		if (null == prtList || CollectionUtils.isEmpty(prtList)) {
			logger.info("There are no records for select check trans journal ");
			throw new CoreException(ErrorCodes.EUPS_QUERY_NO_DATA);
		}
		logger.info("================prtList.size:" + prtList.size());
		
		context.setData("sumCnt", prtList.size());
		
		BigDecimal sunAmt = new BigDecimal(0.00);
		List<Map<String, Object>> rptMapsList = new ArrayList<Map<String,Object>>();
		for(EupsTransJournal perJnl : prtList){
			Map<String, Object> listTmp = BeanUtils.toMap(perJnl);
			listTmp.put(ParamKeys.TXN_DTE, DateUtils.format(perJnl.getTxnDte(), DateUtils.STYLE_SIMPLE_DATE));
			//TODO 清算日期，EUPS无该字段，暂用会计日期acDte
			listTmp.put(ParamKeys.AC_DATE, DateUtils.format(perJnl.getAcDte(), DateUtils.STYLE_SIMPLE_DATE));
			rptMapsList.add(listTmp);
			sunAmt = sunAmt.add(perJnl.getTxnAmt());
		}
		context.setData("sumAmt", sunAmt);
		
		
		EupsThdFtpConfigRepository eupsThdFtpConfigRepository = get(EupsThdFtpConfigRepository.class);
		ReportHelper reportHelper = get(ReportHelper.class);
		MFTPConfigInfo mftpConfigInfo = reportHelper
				.getMFTPConfigInfo(eupsThdFtpConfigRepository);
		logger.info((new StringBuilder("mftpConfigInfo:>>>>").append(BeanUtils
				.toMap(mftpConfigInfo))).toString());

		VelocityTemplatedReportRender render = new VelocityTemplatedReportRender();
		try {
			render.afterPropertiesSet();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String result = null;
		Map<String, String> map = new HashMap<String, String>();
		if("1".equals(context.getData(ParamKeys.PRT_FLG))){
			map.put("gasSuccRpt", "config/report/pgas/printTransJournal_succ.vm");
			render.setReportNameTemplateLocationMapping(map);
			context.setData("eles", rptMapsList);
			result = render.renderAsString("gasSuccRpt", context);
			logger.info(result);
		}
		if("2".equals(context.getData(ParamKeys.PRT_FLG))){
			map.put("gasFailRpt", "config/report/pgas/printTransJournal_fail.vm");
			render.setReportNameTemplateLocationMapping(map);
			context.setData("eles", rptMapsList);
			result = render.renderAsString("gasFailRpt", context);
			logger.info(result);
		}
		PrintWriter printWriter = null;
		
		// TODO 拼装本地路径
		StringBuffer sbLocDir = new StringBuffer();
		sbLocDir.append("D:/testGash/checkFilTest/");
		try {
			File file = new File(sbLocDir.toString());
			if (!file.exists()) {
				file.mkdirs();
			}
			printWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
							new FileOutputStream(sbLocDir.append(fileName).toString()), "GBK")));
			printWriter.write(result);
			
		} catch (IOException e) {
			throw new CoreException(ErrorCodes.EUPS_FILE_CREATE_FAIL);
		} finally {
			if (null != printWriter) {
				try {
					printWriter.close();
				} catch (Exception e) {
					throw new CoreException(ErrorCodes.EUPS_FILE_CREATE_FAIL);
				}
			}
		}
    
//    bbipPublicService.sendFileToBBOS(new File(TransferUtils.resolveFilePath(mftploca, reportFileName)), reportFileName, MftpTransfer.FTYPE_NORMAL);
	

//		reportHelper.createFileAndSendMFTP(context, result, fileName, mftpConfigInfo);
//		context.setData("filName", fileName);
		
		
		
		logger.info("PrintReportServiceActionPGAS00 execute end ... ...");

	}

}
