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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsThdBaseInfo;
import com.bocom.bbip.eups.entity.EupsThdFtpConfig;
import com.bocom.bbip.eups.repository.EupsThdBaseInfoRepository;
import com.bocom.bbip.eups.repository.EupsThdFtpConfigRepository;
import com.bocom.bbip.file.MftpTransfer;
import com.bocom.bbip.file.reporting.impl.VelocityTemplatedReportRender;
import com.bocom.bbip.gdeupsb.entity.GdEupsTransJournal;
import com.bocom.bbip.gdeupsb.repository.GDEupsBatchConsoleInfoRepository;
import com.bocom.bbip.gdeupsb.repository.GdEupsTransJournalRepository;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.SystemConfig;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class PrintEupsbRptsActionBak extends BaseAction {

	@Autowired
	VelocityTemplatedReportRender render;
	@Autowired
	BBIPPublicService bbipPublicService;
	@Autowired
	SystemConfig systemConfig;

	private static Logger logger = LoggerFactory
			.getLogger(PrintEupsbRptsActionBak.class);

	public void execute(Context context) throws CoreException,
			CoreRuntimeException {
		logger.info("Enter in PrintEupsbRptsActionBak....");
		logger.info("===============context:" + context);

		String eupsBusTyp = (String) context.getData(ParamKeys.EUPS_BUSS_TYPE);

		context.setData(ParamKeys.TXN_DTE,
				DateUtils.format(new Date(), DateUtils.STYLE_SIMPLE_DATE));

		// 配VM文件
		String comNo = null;
		String fileName = null;
		String br = context.getData(ParamKeys.BR);
		String tlr = context.getData(ParamKeys.TELLER);
		String prtDte = context.getData("prtDte");
		String prtTyp = context.getData("prtTyp");
		Date prtDate = DateUtils.parse(prtDte, DateUtils.STYLE_SIMPLE_DATE);
		String ttlDate = DateUtils.format(prtDate, "yyyyMMdd");

		EupsThdBaseInfo baseInfo = new EupsThdBaseInfo();
		baseInfo.setEupsBusTyp(eupsBusTyp);
		if (StringUtils.isNotBlank((String) context
				.getData(ParamKeys.COMPANY_NO))) {
			comNo = (String) context.getData(ParamKeys.COMPANY_NO);
			baseInfo.setComNo(comNo);
		}
		List<EupsThdBaseInfo> infoList = get(EupsThdBaseInfoRepository.class)
				.find(baseInfo);
		String comNme = infoList.get(0).getComNme();
		comNo = infoList.get(0).getComNo();
		context.setData(ParamKeys.COMPANY_NAME, comNme);
		context.setData(ParamKeys.COMPANY_NO, comNo);

		logger.info("=============context:" + context);
		GdEupsTransJournal eupsJnl = new GdEupsTransJournal();
		Map<String, Object> baseMap = new HashMap<String, Object>();
		eupsJnl.setEupsBusTyp((String) context
				.getData(ParamKeys.EUPS_BUSS_TYPE));
		// eupsJnl.setBr(br);
		baseMap.put(ParamKeys.EUPS_BUSS_TYPE, eupsBusTyp);
		if (StringUtils.isNotBlank((String) context
				.getData(ParamKeys.COMPANY_NO))) {
			eupsJnl.setComNo((String) context.getData(ParamKeys.COMPANY_NO));
			baseMap.put(ParamKeys.COMPANY_NO,
					(String) context.getData(ParamKeys.COMPANY_NO));
		}
		eupsJnl.setTxnDte(prtDate);
		baseMap.put(ParamKeys.TXN_DTE, prtDate);

		List<Map<String, Object>> prtList = new ArrayList<Map<String, Object>>();

		logger.info("======================context:" + context);

		String prtTtl = null;
		if ("0".equals(prtTyp)) {
			if ("ELEC00".equals(eupsBusTyp)) {
				prtList = get(GdEupsTransJournalRepository.class)
						.findElec00AllTxnList(eupsJnl);
			} else if ("PGAS00".equals(eupsBusTyp)) {
				prtList = get(GdEupsTransJournalRepository.class)
						.findAllTxnList(eupsJnl);
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
				context.setData("DOUBTCNT", prtList.get(0).get("DOUBTCNT"));
				context.setData("TOTDOUBTAMT", prtList.get(0)
						.get("TOTDOUBTAMT"));
				context.setData("OTHERCNT", prtList.get(0).get("OTHERCNT"));
				context.setData("TOTOTHERAMT", prtList.get(0)
						.get("TOTOTHERAMT"));
			}

			prtTtl = (String) context.getData(ParamKeys.COMPANY_NAME)
					.toString().trim()
					+ "_汇总报表";

			fileName = ttlDate + context.getData(ParamKeys.EUPS_BUSS_TYPE)
					+ "JnlAll" + "_p_" + br + "_" + tlr + ".txt";
		}
		if ("1".equals(prtTyp)) { // 成功
			if (eupsBusTyp.equals("PGAS00")) {
				prtList = get(GdEupsTransJournalRepository.class)
						.findSuccTxnList(eupsJnl);
			}
			if (eupsBusTyp.equals("ELEC00")) {
				prtList = get(GdEupsTransJournalRepository.class).findElec00SusList(eupsJnl);
			}
			if (null == prtList || CollectionUtils.isEmpty(prtList)) {
				logger.info("There are no records for select check trans journal ");
				throw new CoreException(ErrorCodes.EUPS_QUERY_NO_DATA);
			}

			context.setData("SUCCCNT", prtList.get(0).get("SUCCCNT"));
			context.setData("TOTSUCCAMT", prtList.get(0).get("TOTSUCCAMT"));
			prtTtl = (String) context.getData(ParamKeys.COMPANY_NAME)
					.toString().trim()
					+ "_成功交易清单报表";
			fileName = ttlDate + context.getData(ParamKeys.EUPS_BUSS_TYPE)
					+ "jnlSus" + "_p_" + br + "_" + tlr + ".txt";
		}
		if ("2".equals(prtTyp)) {
			if ("ELEC00".equals(eupsBusTyp)) {
				prtList = get(GdEupsTransJournalRepository.class)
						.findElecFailTxnList(eupsJnl);
			} else if("PGAS00".equals(eupsBusTyp)) {
				prtList = get(GdEupsTransJournalRepository.class)
						.findFailTxnList(eupsJnl);
			}
			if (null == prtList || CollectionUtils.isEmpty(prtList)) {
				logger.info("There are no records for select check trans journal ");
				throw new CoreException(ErrorCodes.EUPS_QUERY_NO_DATA);
			}

			context.setData("FAILCNT", prtList.get(0).get("FAILCNT"));
			context.setData("TOTFAILAMT", prtList.get(0).get("TOTFAILAMT"));
			prtTtl = (String) context.getData(ParamKeys.COMPANY_NAME)
					.toString().trim()
					+ "_失败交易清单报表";
			fileName = ttlDate + context.getData(ParamKeys.EUPS_BUSS_TYPE)
					+ "jnlFal" + "_p_" + br + "_" + tlr + ".txt";
		}
		if ("3".equals(prtTyp)) {
			if ("ELEC00".equals(eupsBusTyp)) {
				prtList = get(GdEupsTransJournalRepository.class)
						.findElecDoubtTxnList(eupsJnl);
			} else if("PGAS00".equals(eupsBusTyp)) {
				prtList = get(GdEupsTransJournalRepository.class)
						.findDoubtTxnList(eupsJnl);
			}
			if (null == prtList || CollectionUtils.isEmpty(prtList)) {
				logger.info("There are no records for select check trans journal ");
				throw new CoreException(ErrorCodes.EUPS_QUERY_NO_DATA);
			}

			context.setData("DOUBTCNT", prtList.get(0).get("DOUBTCNT"));
			context.setData("TOTDOUBTAMT", prtList.get(0).get("TOTDOUBTAMT"));
			prtTtl = (String) context.getData(ParamKeys.COMPANY_NAME)
					.toString().trim()
					+ "_可疑交易清单报表";
			fileName = ttlDate + context.getData(ParamKeys.EUPS_BUSS_TYPE)
					+ "jnlDoubt" + "_p_" + br + "_" + tlr + ".txt";
		}
		if ("4".equals(prtTyp)) {

			if ("ELEC00".equals(eupsBusTyp)) {
				prtList = get(GdEupsTransJournalRepository.class)
						.findElecOthTxnList(eupsJnl);
			} else if("PGAS00".equals(eupsBusTyp)) {
				prtList = get(GdEupsTransJournalRepository.class)
						.findOthTxnList(eupsJnl);
			}
			if (null == prtList || CollectionUtils.isEmpty(prtList)) {
				logger.info("There are no records for select check trans journal ");
				throw new CoreException(ErrorCodes.EUPS_QUERY_NO_DATA);
			}

			context.setData("OTHERCNT", prtList.get(0).get("OTHERCNT"));
			context.setData("TOTOTHERAMT", prtList.get(0).get("TOTOTHERAMT"));
			prtTtl = (String) context.getData(ParamKeys.COMPANY_NAME)
					+ "_其他情况清单报表";
			fileName = ttlDate + context.getData(ParamKeys.EUPS_BUSS_TYPE)
					+ "jnlOth" + "_p_" + br + "_" + tlr + ".txt";
		}

		if ("5".equals(prtTyp)) { // 批量报表 SQL
			if (eupsBusTyp.equals("ELEC02")) {
				prtList = get(GDEupsBatchConsoleInfoRepository.class)
						.findElec02BatchRptInfo(baseMap);
			} else if (eupsBusTyp.equals("WATR00")) {
				prtList = get(GDEupsBatchConsoleInfoRepository.class)
						.findWatrBatchRptInfo(baseMap);
			} else {
				prtList = get(GDEupsBatchConsoleInfoRepository.class)
						.findBatchRptInfo(baseMap);
			}
			if (null == prtList || CollectionUtils.isEmpty(prtList)) {
				logger.info("There are no records for select check trans journal ");
				throw new CoreException(ErrorCodes.EUPS_QUERY_NO_DATA);
			}

			context.setData("TOTCNT", prtList.get(0).get("TOTCNT"));
			context.setData("TOTAMT", prtList.get(0).get("TOTAMT"));
			context.setData("SUCTOTCNT", prtList.get(0).get("SUCTOTCNT"));
			context.setData("SUCTOTAMT", prtList.get(0).get("SUCTOTAMT"));
			context.setData("FALTOTCNT", prtList.get(0).get("FALTOTCNT"));
			context.setData("FALTOTAMT", prtList.get(0).get("FALTOTAMT"));

			prtTtl = (String) context.getData(ParamKeys.COMPANY_NAME)
					.toString().trim()
					+ "_批量交易报表";
			if (eupsBusTyp.equals("ELEC02")) {
				fileName = "301586003BatchReport"
						+ DateUtils
								.format(new Date(), DateUtils.STYLE_yyyyMMdd)
						+ ".txt";
			} else {
				fileName = ttlDate + context.getData(ParamKeys.EUPS_BUSS_TYPE)
						+ "Batch" + "_p_" + br + "_" + tlr + ".txt";
			}

		}

		context.setData("prtTtl", prtTtl);
		String sampleFile = null;
		String result = null;

		EupsThdFtpConfig sendFileToBBOSConfig = get(
				EupsThdFtpConfigRepository.class).findOne("sendFileToBBOS");
		String filPath = sendFileToBBOSConfig.getLocDir().trim();
		Map<String, String> map = new HashMap<String, String>();

		if ("0".equals(prtTyp)) {// 单笔全部
			if ("ELEC00".equals(eupsBusTyp)) {
				sampleFile = "config/report/common/elec00PrintReport_all.vm";
			} else {
				sampleFile = "config/report/common/commonPrintReport_all.vm";
			}
		} else if ("1".equals(prtTyp)) {// 单笔成功
			sampleFile = "config/report/common/commonPrintReport_succ.vm";
		} else if ("2".equals(prtTyp)) {// 单笔失败
			sampleFile = "config/report/common/commonPrintReport_fail.vm";
		} else if ("3".equals(prtTyp)) {// 单笔存疑
			sampleFile = "config/report/common/commonPrintReport_doubt.vm";
		} else if ("4".equals(prtTyp)) {// 单笔其他
			sampleFile = "config/report/common/commonPrintReport_oth.vm";
		} else if ("5".equals(prtTyp)) {// 批量报表 VM
			if (eupsBusTyp.equals("ELEC02")) {
				sampleFile = "config/report/common/Elec02batchRpt.vm";
			} else {
				sampleFile = "config/report/common/commonPrintReport_batch.vm";
			}
		}

		logger.info("=============ready to print report list=============");

		map.put("sample", sampleFile);

		try {
			render.afterPropertiesSet();
		} catch (Exception e) {
			logger.info("render afterPropertiesSet error : ", e);
		}
		context.setData("eles", prtList);
		render.setReportNameTemplateLocationMapping(map);
		result = render.renderAsString("sample", context);
		logger.info("====================== result =================");
		logger.info(result);

		String JYPath = filPath + fileName;
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
							.append(fileName).toString()), "GBK")));
			printWriter.write(result);

		} catch (IOException e) {
			logger.info("create file error:", e);
			throw new CoreException(ErrorCodes.EUPS_FILE_CREATE_FAIL);
		} finally {
			if (null != printWriter) {
				try {
					printWriter.close();
				} catch (Exception e) {
					logger.info("close printWriter error:", e);
				}
			}
		}
		logger.info("报表文件生成！！NEXT 上传FTP");

		// 上传前端FTP
		// FTP上传设置
		// FTPTransfer tFTPTransfer = new FTPTransfer();
		// tFTPTransfer.setHost(sendFileToBBOSConfig.getThdIpAdr());
		// tFTPTransfer
		// .setPort(Integer.parseInt(sendFileToBBOSConfig.getBidPot()));
		// tFTPTransfer.setUserName(sendFileToBBOSConfig.getOppNme());
		// tFTPTransfer.setPassword(sendFileToBBOSConfig.getOppUsrPsw());
		// try {
		// tFTPTransfer.logon();
		// Resource tResource = new FileSystemResource(JYPath);
		// tFTPTransfer.putResource(tResource, sendFileToBBOSConfig
		// .getRmtWay().trim(), fileName);
		//
		// } catch (Exception e) {
		// throw new CoreException("文件上传失败");
		// } finally {
		// tFTPTransfer.logout();
		// }

		try {
			bbipPublicService.sendFileToBBOS(new File(filPath, fileName),
					fileName, MftpTransfer.FTYPE_NORMAL);
		} catch (Exception e) {
			throw new CoreException(ErrorCodes.EUPS_FTP_FILEPUT_NFAIL);
		}

		// elec02对账文件上传到汕头指定服务器
		if ("ELEC02".equals(eupsBusTyp)) {
			/**
			 * 配置ftp 参考 watr00BatchResulfA
			 */
			// OperateFTPActionExt operateFTP = new OperateFTPActionExt();
			//
			// logger.info("elec02对账文件上传到汕头指定服务器");
			// EupsThdFtpConfig sendFileToElec02 = get(
			// EupsThdFtpConfigRepository.class).findOne(
			// "sendFileToElec02");
			// sendFileToElec02.setLocDir(filPath);
			// sendFileToElec02.setLocFleNme(fileName);
			// sendFileToElec02.setRmtFleNme(fileName);
			// operateFTP.putCheckFile(sendFileToElec02);
			//

			// -------------------------------------------------------------------
			// String filPath = config.getLocDir();
			EupsThdFtpConfig eupsThdFtpConfigA = get(
					EupsThdFtpConfigRepository.class).findOne(
					"sendFileToElec02");
			String stwatIpA = eupsThdFtpConfigA.getThdIpAdr();
			String userNameA = eupsThdFtpConfigA.getOppNme();
			String passwordA = eupsThdFtpConfigA.getOppUsrPsw();
			String rmtDirA = eupsThdFtpConfigA.getRmtWay();
			String[] shellArgA = { "GDEUPSBFtpPutFile.sh", stwatIpA, userNameA,
					passwordA, rmtDirA, fileName, filPath, "bin", fileName };
			logger.info("ftp args=" + shellArgA.toString());
			// ftp放文件
			try {
				int result1 = FileFtpUtils.systemAndWait(shellArgA, true);
				if (result1 == 0) {
					logger.info("put remote file success......");
				} else {
					throw new CoreException(ErrorCodes.EUPS_FAIL);
				}
			} catch (Exception e) {
				throw new CoreException(ErrorCodes.EUPS_FAIL);
			}

			// -----------------------------------------------------------------

		}

		context.setData("fleNme", fileName);
		logger.info("文件上传完成，等待打印！", context);

		/*****************************************************************************************************************************/

		logger.info("PrintEupsbRptsActionBak execute end ... ...");

	}
}
