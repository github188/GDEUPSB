package com.bocom.bbip.gdeupsb.action.gas;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.CollectionUtils;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.repository.EupsThdBaseInfoRepository;
import com.bocom.bbip.file.reporting.impl.VelocityTemplatedReportRender;
import com.bocom.bbip.file.transfer.ftp.FTPTransfer;
import com.bocom.bbip.gdeupsb.repository.GDEupsBatchConsoleInfoRepository;
import com.bocom.bbip.gdeupsb.repository.GdEupsTransJournalRepository;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.SystemConfig;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class PrintReportServiceActionPGAS00 extends BaseAction {

	@Autowired
	private VelocityTemplatedReportRender render;
	@Autowired
	private BBIPPublicService bbipPublicService;
	@Autowired
	private SystemConfig systemConfig;

	private static Logger logger = LoggerFactory
			.getLogger(PrintReportServiceActionPGAS00.class);

	public void execute(Context context) throws CoreException,
			CoreRuntimeException {
		logger.info("Enter in PrintReportServiceActionPGAS00....");
		logger.info("===============context:" + context);

		String prtFlg = context.getData(ParamKeys.PRT_FLG);

		// 配VM文件
		StringBuffer fileName = null;
		String br = context.getData(ParamKeys.BR);
		String beginDate = context.getData("beginDate");
		String endDate = context.getData("endDate");
		Date beginDte = DateUtils.parse(beginDate, DateUtils.STYLE_SIMPLE_DATE);
		Date endDte = DateUtils.parse(endDate, DateUtils.STYLE_SIMPLE_DATE);

		logger.info("=============context:" + context);

		Map<String, Object> detailMap = new HashMap<String, Object>();
		detailMap.put("comNo", "4910000430");
		detailMap.put("beginDte", beginDte);
		detailMap.put("endDte", endDte);

		context.setData("beginDte", beginDte);
		context.setData("endDte", endDte);

		String comNme = get(EupsThdBaseInfoRepository.class).findOne(
				"4910000430").getComNme();
		context.setData("comNme", comNme);

		List<Map<String, Object>> prtList = new ArrayList<Map<String, Object>>();

		if ("1".equals(prtFlg)) {
			prtList = get(GdEupsTransJournalRepository.class)
					.findGasAllJnlInfo(detailMap);
			if (null == prtList || CollectionUtils.isEmpty(prtList)) {
				logger.info("There are no records for select check trans journal ");
				throw new CoreException(ErrorCodes.EUPS_QUERY_NO_DATA);
			}
			context.setData("TOTCNT", prtList.get(0).get("TOTCNT"));
			context.setData("TOTAMT", prtList.get(0).get("TOTAMT"));

			context.setData("SUCCCNT", prtList.get(0).get("SUCCCNT"));
			context.setData("TOTSUCCAMT", prtList.get(0).get("TOTSUCCAMT"));

			context.setData("FAILCNT", prtList.get(0).get("FAILCNT"));
			context.setData("TOTFAILAMT", prtList.get(0).get("TOTFAILAMT"));

			context.setData("DOUBTCNT", prtList.get(0).get("DOUBATCNT"));
			context.setData("TOTDOUBTAMT", prtList.get(0).get("TOTDOUBATAMT"));

			context.setData("OTHERCNT", prtList.get(0).get("OTHERCNT"));
			context.setData("TOTOTHERAMT", prtList.get(0).get("TOTOTHERAMT"));

			fileName = new StringBuffer(
					(new StringBuilder("PGAS00DKAll_" + "_" + beginDate
							+ "_" + endDate).append(".txt").toString()));
		}
		if ("2".equals(prtFlg)) {
			prtList = get(GdEupsTransJournalRepository.class)
					.findGasSucJnlInfo(detailMap);
			if (null == prtList || CollectionUtils.isEmpty(prtList)) {
				logger.info("There are no records for select check trans journal ");
				throw new CoreException(ErrorCodes.EUPS_QUERY_NO_DATA);
			}
			context.setData("sumCnt", prtList.get(0).get("SUCCCNT"));
			context.setData("sumAmt", prtList.get(0).get("TOTSUCCAMT"));
			fileName = new StringBuffer(
					(new StringBuilder("PGAS00DKSUC_" + br + "_" + beginDate
							+ "_" + endDate).append(".txt").toString()));
		}
		if ("3".equals(prtFlg)) {
			prtList = get(GdEupsTransJournalRepository.class)
					.findGasFalJnlInfo(detailMap);
			if (null == prtList || CollectionUtils.isEmpty(prtList)) {
				logger.info("There are no records for select check trans journal ");
				throw new CoreException(ErrorCodes.EUPS_QUERY_NO_DATA);
			}
			context.setData("sumCnt", prtList.get(0).get("FAILCNT"));
			context.setData("sumAmt", prtList.get(0).get("TOTFAILAMT"));
			fileName = new StringBuffer(
					(new StringBuilder("PGAS00DKFAL_" + br + "_" + beginDate
							+ "_" + endDate).append(".txt").toString()));
		}
		if ("4".equals(prtFlg)) {
			prtList = get(GDEupsBatchConsoleInfoRepository.class)
					.findGasBatAllRecord(detailMap);
			if (null == prtList || CollectionUtils.isEmpty(prtList)) {
				logger.info("There are no records for select check trans journal ");
				throw new CoreException(ErrorCodes.EUPS_QUERY_NO_DATA);
			}
			context.setData("sumCnt", prtList.get(0).get("SUM_TOT_CNT"));
			context.setData("sumAmt", prtList.get(0).get("SUM_TOT_AMT"));
			context.setData("sumSucCnt", prtList.get(0).get("SUM_SUC_TOT_CNT"));
			context.setData("sumSucAmt", prtList.get(0).get("SUM_SUC_TOT_AMT"));
			context.setData("sumFalCnt", prtList.get(0).get("SUM_FAL_TOT_CNT"));
			context.setData("sumFalAmt", prtList.get(0).get("SUM_FAL_TOT_AMT"));

			fileName = new StringBuffer(
					(new StringBuilder("PGAS00BATALL_" + br + "_" + beginDate
							+ "_" + endDate).append(".txt").toString()));
		}
		if ("5".equals(prtFlg)) {
			prtList = get(GDEupsBatchConsoleInfoRepository.class)
					.findGasBatSucRecord(detailMap);
			if (null == prtList || CollectionUtils.isEmpty(prtList)) {
				logger.info("There are no records for select check trans journal ");
				throw new CoreException(ErrorCodes.EUPS_QUERY_NO_DATA);
			}
			context.setData("sumCnt", prtList.get(0).get("SUM_SUC_TOT_CNT"));
			context.setData("sumAmt", prtList.get(0).get("SUM_SUC_TOT_AMT"));
			fileName = new StringBuffer(
					(new StringBuilder("PGAS00BATSUC_" + br + "_" + beginDate
							+ "_" + endDate).append(".txt").toString()));
		}
		if ("6".equals(prtFlg)) {
			prtList = get(GDEupsBatchConsoleInfoRepository.class)
					.findGasBatFalRecord(detailMap);
			if (null == prtList || CollectionUtils.isEmpty(prtList)) {
				logger.info("There are no records for select check trans journal ");
				throw new CoreException(ErrorCodes.EUPS_QUERY_NO_DATA);
			}
			context.setData("sumCnt", prtList.get(0).get("SUM_FAL_TOT_CNT"));
			context.setData("sumAmt", prtList.get(0).get("SUM_FAL_TOT_AMT"));
			fileName = new StringBuffer(
					(new StringBuilder("PGAS00BATFAL_" + br + "_" + beginDate
							+ "_" + endDate).append(".txt").toString()));
		}

		logger.info("================prtList.size:" + prtList.size());

		String filPath = "/home/bbipadm/data/GDEUPSB/report/";
		String sampleFile = null;
		String result = null;
		Map<String, String> map = new HashMap<String, String>();
		if ("1".equals(prtFlg)) {
			sampleFile = "config/report/pgas/PrintgasAllReport.vm";
		}
		if ("2".equals(prtFlg) || "3".equals(prtFlg)) {
			sampleFile = "config/report/pgas/printTransJournal.vm";
		}
		if ("4".equals(prtFlg)) {
			sampleFile = "config/report/pgas/printBatInfoAll.vm";
		}
		if ("5".equals(prtFlg) || "6".equals(prtFlg)) {
			sampleFile = "config/report/pgas/printBatInfo.vm";
		}

		map.put("sample", sampleFile);

		try {
			render.afterPropertiesSet();
		} catch (Exception e) {
		}
		context.setData("eles", prtList);
		render.setReportNameTemplateLocationMapping(map);
		result = render.renderAsString("sample", context);
		logger.info("====================== result =================");
		logger.info(result);

		String JYPath = filPath + fileName.toString();
		logger.info("====================== JYPath =================");
		logger.info(JYPath);

		PrintWriter printWriter = null;
		StringBuffer sbLocDir = new StringBuffer();
		sbLocDir.append(filPath);
		try {
			File file = new File(sbLocDir.toString());
			if (!file.exists()) {
				file.mkdirs();
			}
			printWriter = new PrintWriter(new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(sbLocDir
							.append(fileName.toString()).toString()), "GBK")));
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
		logger.info("报表文件生成！！NEXT 上传FTP");

		// 上传FTP
		FTPTransfer tFTPTransfer = new FTPTransfer();
		//FTP上传设置
		tFTPTransfer.setHost("182.53.15.187");
		tFTPTransfer.setPort(21);
		tFTPTransfer.setUserName("weblogic");
		tFTPTransfer.setPassword("123456");

		try {
			tFTPTransfer.logon();
			Resource tResource = new FileSystemResource(JYPath);
			tFTPTransfer.putResource(tResource,
					"/home/weblogic/JumpServer/WEB-INF/data/mftp_recv/",
					fileName.toString());

		} catch (Exception e) {
			throw new CoreException("文件上传失败");
		} finally {
			tFTPTransfer.logout();
		}

		context.setData("rspMsg", fileName.toString());
		logger.info("文件上传完成，等待打印！" + context);

		logger.info("PrintReportServiceActionPGAS00 execute end ... ...");

	}
}
