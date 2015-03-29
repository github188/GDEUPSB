package com.bocom.bbip.gdeupsb.action.common;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.action.eupsreport.ReportHelper;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsThdBaseInfo;
import com.bocom.bbip.eups.entity.MFTPConfigInfo;
import com.bocom.bbip.eups.repository.EupsThdBaseInfoRepository;
import com.bocom.bbip.eups.repository.EupsThdFtpConfigRepository;
import com.bocom.bbip.file.reporting.impl.VelocityTemplatedReportRender;
import com.bocom.bbip.gdeupsb.entity.GdEupsTransJournal;
import com.bocom.bbip.gdeupsb.repository.GdEupsTransJournalRepository;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class PrintEupsbRptsAction extends BaseAction {

	private static Logger logger = LoggerFactory
			.getLogger(PrintEupsbRptsAction.class);

	public void execute(Context context) throws CoreException,
			CoreRuntimeException {
		logger.info("Enter in PrintEupsbRptsAction....");
		logger.info("===============context:" + context);

		// 业务类型eupsBusTyp 必输
		// 单位编码comNo 非必输
		// 需打印交易日期（交易发起前一天？）prtDte 必输yyyy-MM-dd
		// 报表类型 (全部0，成功1，失败2,可疑3) prtFlg 必输
		// 输出：
		// 成功（打印报表）/失败

		context.setData(ParamKeys.TXN_DTE,
				DateUtils.format(new Date(), DateUtils.STYLE_SIMPLE_DATE));

		// 配VM文件
		StringBuffer fileName = null;
		String br = context.getData(ParamKeys.BR);
		String prtDte = context.getData("prtDte");

		EupsThdBaseInfo baseInfo = new EupsThdBaseInfo();
		baseInfo.setEupsBusTyp((String) context
				.getData(ParamKeys.EUPS_BUSS_TYPE));
		if (StringUtils.isNotBlank((String) context
				.getData(ParamKeys.COMPANY_NO))) {
			baseInfo.setComNo((String) context.getData(ParamKeys.COMPANY_NO));
		}
		List<EupsThdBaseInfo> infoList = get(EupsThdBaseInfoRepository.class)
				.find(baseInfo);
		String comNme = infoList.get(0).getComNme();
		context.setData(ParamKeys.COMPANY_NAME, comNme);

		logger.info("=============context:" + context);
		GdEupsTransJournal eupsJnl = new GdEupsTransJournal();
		eupsJnl.setEupsBusTyp((String) context
				.getData(ParamKeys.EUPS_BUSS_TYPE));
		eupsJnl.setBr((String) context.getData(ParamKeys.BR));
		if (StringUtils.isNotBlank((String) context
				.getData(ParamKeys.COMPANY_NO))) {
			eupsJnl.setComNo((String) context.getData(ParamKeys.COMPANY_NO));
		}
		eupsJnl.setTxnDte(DateUtils.parse((String) context.getData("prtDte"),
				DateUtils.STYLE_SIMPLE_DATE));
		
		List<Map<String, Object>> prtList = new ArrayList<Map<String,Object>>(); 
		
		logger.info("======================context:" + context);
		
		String prtTtl = null;
		if ("0".equals(context.getData("prtTyp"))) {
			prtList = get(GdEupsTransJournalRepository.class).findAllTxnList(eupsJnl);
			
			context.setData("TOTCNT", prtList.get(0).get("TOTCNT"));
			context.setData("TOTAMT", prtList.get(0).get("TOTAMT"));
			context.setData("SUCCCNT", prtList.get(0).get("SUCCCNT"));
			context.setData("TOTSUCCAMT", prtList.get(0).get("TOTSUCCAMT"));
			context.setData("FAILCNT", prtList.get(0).get("FAILCNT"));
			context.setData("TOTFAILAMT", prtList.get(0).get("TOTFAILAMT"));
			context.setData("DOUBTCNT", prtList.get(0).get("DOUBTCNT"));
			context.setData("TOTDOUBTAMT", prtList.get(0).get("TOTDOUBTAMT"));
			context.setData("OTHERCNT", prtList.get(0).get("OTHERCNT"));
			context.setData("TOTOTHERAMT", prtList.get(0).get("TOTOTHERAMT"));
			
			prtTtl = (String) context.getData(ParamKeys.COMPANY_NAME)
					.toString().trim()
					+ "全部交易清单报表";
		}
		if ("1".equals(context.getData("prtTyp"))) {
			prtList = get(GdEupsTransJournalRepository.class).findSuccTxnList(eupsJnl);
			context.setData("SUCCCNT", prtList.get(0).get("SUCCCNT"));
			context.setData("TOTSUCCAMT", prtList.get(0).get("TOTSUCCAMT"));
			prtTtl = (String) context.getData(ParamKeys.COMPANY_NAME)
					.toString().trim()
					+ "成功交易清单报表";
		}
		if ("2".equals(context.getData("prtTyp"))) {
			prtList = get(GdEupsTransJournalRepository.class).findFailTxnList(eupsJnl);
			context.setData("FAILCNT", prtList.get(0).get("FAILCNT"));
			context.setData("TOTFAILAMT", prtList.get(0).get("TOTFAILAMT"));
			prtTtl = (String) context.getData(ParamKeys.COMPANY_NAME)
					.toString().trim()
					+ "失败交易清单报表";
		}
		if ("3".equals(context.getData("prtTyp"))) {
			prtList =get(GdEupsTransJournalRepository.class).findDoubtTxnList(eupsJnl);
			context.setData("DOUBTCNT", prtList.get(0).get("DOUBTCNT"));
			context.setData("TOTDOUBTAMT", prtList.get(0).get("TOTDOUBTAMT"));
			prtTtl = (String) context.getData(ParamKeys.COMPANY_NAME)
					.toString().trim()
					+ "可疑交易清单报表";
		}
		if ("4".equals(context.getData("prtTyp"))) {
			prtList = get(GdEupsTransJournalRepository.class).findOthTxnList(eupsJnl);
			context.setData("OTHERCNT", prtList.get(0).get("OTHERCNT"));
			context.setData("TOTOTHERAMT", prtList.get(0).get("TOTOTHERAMT"));
			prtTtl = (String) context.getData(ParamKeys.COMPANY_NAME)
					.toString().trim()
					+ "其他情况清单报表";
		}
		
		context.setData("prtTtl", prtTtl);
		fileName = new StringBuffer((new StringBuilder(prtTtl + "_" + br + "_"
				+ prtDte + ".txt").toString()));

		logger.info("==============fileName:" + fileName);

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

		if ("0".equals(context.getData("prtTyp"))) {
			map.put("commonPrtRpt", "config/report/common/commonPrintReport_all.vm");
			render.setReportNameTemplateLocationMapping(map);
			context.setData("eles", prtList);
			result = render.renderAsString("commonPrtRpt", context);
			logger.info(result);
		} else if ("1".equals(context.getData("prtTyp"))) {
			map.put("commonPrtRpt", "config/report/common/commonPrintReport_succ.vm");
			render.setReportNameTemplateLocationMapping(map);
			context.setData("eles", prtList);
			result = render.renderAsString("commonPrtRpt", context);
			logger.info(result);
		} else if ("2".equals(context.getData("prtTyp"))) {
			map.put("commonPrtRpt", "config/report/common/commonPrintReport_fail.vm");
			render.setReportNameTemplateLocationMapping(map);
			context.setData("eles", prtList);
			result = render.renderAsString("commonPrtRpt", context);
			logger.info(result);
		} else if ("3".equals(context.getData("prtTyp"))) {
			map.put("commonPrtRpt", "config/report/common/commonPrintReport_doubt.vm");
			render.setReportNameTemplateLocationMapping(map);
			context.setData("eles", prtList);
			result = render.renderAsString("commonPrtRpt", context);
			logger.info(result);
		}
		else if ("4".equals(context.getData("prtTyp"))) {
			map.put("commonPrtRpt", "config/report/common/commonPrintReport_oth.vm");
			render.setReportNameTemplateLocationMapping(map);
			context.setData("eles", prtList);
			result = render.renderAsString("commonPrtRpt", context);
			logger.info(result);
		}

		logger.info("=============ready to print report list=============");

		PrintWriter printWriter = null;

		// TODO 拼装本地路径
		StringBuffer sbLocDir = new StringBuffer();
		sbLocDir.append("D:/testGash/checkFilTest/").append(DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMdd)).append("/");
		try {
			File file = new File(sbLocDir.toString());
			if (!file.exists()) {
				file.mkdirs();
			}
			printWriter = new PrintWriter(new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(sbLocDir
							.append(fileName).toString()), "GBK")));
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

		// bbipPublicService.sendFileToBBOS(new
		// File(TransferUtils.resolveFilePath(mftploca, reportFileName)),
		// reportFileName, MftpTransfer.FTYPE_NORMAL);

		// reportHelper.createFileAndSendMFTP(context, result, fileName,
		// mftpConfigInfo);
		// context.setData("filName", fileName);

		logger.info("PrintReportServiceActionPGAS00 execute end ... ...");

	}
}
