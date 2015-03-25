package com.bocom.bbip.gdeupsb.action.common;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
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
import com.bocom.bbip.eups.entity.EupsThdBaseInfo;
import com.bocom.bbip.eups.entity.EupsTransJournal;
import com.bocom.bbip.eups.entity.MFTPConfigInfo;
import com.bocom.bbip.eups.repository.EupsThdBaseInfoRepository;
import com.bocom.bbip.eups.repository.EupsThdFtpConfigRepository;
import com.bocom.bbip.eups.repository.EupsTransJournalRepository;
import com.bocom.bbip.file.reporting.impl.VelocityTemplatedReportRender;
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
		// 报表类型 (全部0，成功1，失败2) prtFlg 必输
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
		EupsTransJournal eupsJnl = new EupsTransJournal();
		eupsJnl.setEupsBusTyp((String) context
				.getData(ParamKeys.EUPS_BUSS_TYPE));
		eupsJnl.setBr((String) context.getData(ParamKeys.BR));
		if (StringUtils.isNotBlank((String) context
				.getData(ParamKeys.COMPANY_NO))) {
			eupsJnl.setComNo((String) context.getData(ParamKeys.COMPANY_NO));
		}
		eupsJnl.setTxnDte(DateUtils.parse((String) context.getData("prtDte"),
				DateUtils.STYLE_SIMPLE_DATE));

		String prtTtl = null;
		if ("0".equals(context.getData("prtTyp"))) {
			prtTtl = (String) context.getData(ParamKeys.COMPANY_NAME)
					.toString().trim()
					+ "全部交易报表";
		}
		if ("1".equals(context.getData("prtTyp"))) {
			eupsJnl.setTxnSts("S");
			prtTtl = (String) context.getData(ParamKeys.COMPANY_NAME)
					.toString().trim()
					+ "成功交易报表";
		}
		if ("2".equals(context.getData("prtTyp"))) {
			eupsJnl.setTxnSts("F");
			prtTtl = (String) context.getData(ParamKeys.COMPANY_NAME)
					.toString().trim()
					+ "失败交易报表";
		}
		context.setData("prtTtl", prtTtl);
		fileName = new StringBuffer((new StringBuilder(prtTtl + "_" + br + "_"
				+ prtDte + ".txt").toString()));

		logger.info("==============fileName:" + fileName);

		List<EupsTransJournal> prtList = get(EupsTransJournalRepository.class)
				.find(eupsJnl);
		if (null == prtList || CollectionUtils.isEmpty(prtList)) {
			logger.info("There are no records for select check trans journal ");
			throw new CoreException(ErrorCodes.EUPS_QUERY_NO_DATA);
		}
		logger.info("================prtList.size:" + prtList.size());

		context.setData("sumCnt", prtList.size());

		int succCnt = 0;
		int failCnt = 0;

		BigDecimal sumAmt = new BigDecimal(0.00);
		BigDecimal sussAmt = new BigDecimal(0.00);
		BigDecimal failAmt = new BigDecimal(0.00);
		
		for (EupsTransJournal perJnl : prtList) {
			if (!("C".equals(perJnl.getTxnTyp()))) { // 剔除已冲正交易
				if ("S".equals(perJnl.getTxnSts())) {
					succCnt++;
					sussAmt = sussAmt.add(perJnl.getTxnAmt());
				}
				if ("F".equals(perJnl.getTxnSts())) {
					failCnt++;
					failAmt = failAmt.add(perJnl.getTxnAmt());
				}
				sumAmt = sumAmt.add(perJnl.getTxnAmt());
			}
		}

		context.setData("succCnt", succCnt);
		context.setData("failCnt", failCnt);

		context.setData("sumAmt", sumAmt);
		context.setData("sussAmt", sussAmt);
		context.setData("failAmt", failAmt);

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
			map.put("commonPrtRpt",
					"config/report/common/commonPrintReport_all.vm");
			render.setReportNameTemplateLocationMapping(map);
			context.setData("eles", prtList);
			result = render.renderAsString("commonPrtRpt", context);
			logger.info(result);
		} else {
			map.put("commonRpt", "config/report/common/commonPrintReport.vm");
			render.setReportNameTemplateLocationMapping(map);
			context.setData("eles", prtList);
			result = render.renderAsString("commonRpt", context);
			logger.info(result);
		}
		// if("2".equals(context.getData(ParamKeys.PRT_FLG))){
		// map.put("commonFailRpt",
		// "config/report/common/commonPrintReport_fail.vm");
		// render.setReportNameTemplateLocationMapping(map);
		// context.setData("eles", prtList);
		// result = render.renderAsString("commonFailRpt", context);
		// logger.info(result);
		// }
		PrintWriter printWriter = null;

		// TODO 拼装本地路径
		StringBuffer sbLocDir = new StringBuffer();
		sbLocDir.append("D:/testGash/checkFilTest/");
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
