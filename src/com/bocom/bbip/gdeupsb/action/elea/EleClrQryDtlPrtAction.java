package com.bocom.bbip.gdeupsb.action.elea;

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

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsThdFtpConfig;
import com.bocom.bbip.eups.repository.EupsThdFtpConfigRepository;
import com.bocom.bbip.file.reporting.impl.VelocityTemplatedReportRender;
import com.bocom.bbip.file.transfer.ftp.FTPTransfer;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDErrorCodes;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GdElecClrInf;
import com.bocom.bbip.gdeupsb.entity.GdEupsTransJournal;
import com.bocom.bbip.gdeupsb.repository.GdElecClrInfRepository;
import com.bocom.bbip.gdeupsb.repository.GdEupsTransJournalRepository;
import com.bocom.bbip.utils.CollectionUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class EleClrQryDtlPrtAction extends BaseAction {

	@Override
	public void execute(Context context) throws CoreException, CoreRuntimeException {
		log.info("电力清算信息查询打印开始..");
		String delFlg = context.getData("delFlg"); // 操作类型
		String detailFlg = context.getData(GDParamKeys.GZ_ELE_DETAIL_FLAG); // 明细类型

		// 获取电力清算信息表信息
		// GdElecClrInf gdElecClrInf = new GdElecClrInf();
		// gdElecClrInf.setBrNo(GDConstants.GZ_ELE_BK_GZ);
		// List<GdElecClrInf> gdElecClrInfList =
		// get(GdElecClrInfRepository.class).find(gdElecClrInf);
		// if (CollectionUtils.isEmpty(gdElecClrInfList)) {
		// log.error("不存在清算参数信息");
		// throw new CoreException(GDErrorCodes.EUPS_ELE_GZ_CLEAR_INFO_ERROR);
		// // 不存在清算参数信息
		// } else {
		// gdElecClrInf = gdElecClrInfList.get(0);
		// }
		// String thdClrDte = gdElecClrInf.getClrDat(); // 第三方约定的日期

		String thdClrDte = context.getData("thdClrDte"); // 第三方约定的日期

		// 查找当日缴费及划扣金额，条件为主机及交易状态均为成功
		GdEupsTransJournal transJnl = new GdEupsTransJournal();
		transJnl.setBakFld1(thdClrDte);

		// 如果是查询汇总信息
		if (GDConstants.GZ_ELE_PRINT_FLAG_ALL.equals(delFlg)) {
			BigDecimal sucJfAmt = new BigDecimal("0.00");
			int sucJfCnt = 0;
			List<Map<String, Object>> sucJfTotList = get(GdEupsTransJournalRepository.class).findGzSucJnlJF(transJnl);
			if (CollectionUtils.isNotEmpty(sucJfTotList)) {
				Map<String, Object> sucJfTotInf = sucJfTotList.get(0);
				if (null != sucJfTotInf.get("TXNAMT")) {
					sucJfAmt = (BigDecimal) sucJfTotInf.get("TXNAMT");
				}
				if (null != sucJfTotInf.get("TXNCNT")) {
					sucJfCnt = (Integer) sucJfTotInf.get("TXNCNT");
				}
				log.info("当前的成功缴费清算金额为:" + sucJfAmt + ",当前的成功缴费清算笔数为:" + sucJfCnt);
			}

			BigDecimal sucHkAmt = new BigDecimal("0.00");
			int sucHkCnt = 0;
			List<Map<String, Object>> sucHkTotList = get(GdEupsTransJournalRepository.class).findGzSucJnlHK(transJnl);
			if (CollectionUtils.isNotEmpty(sucHkTotList)) {
				Map<String, Object> sucHkTotInf = sucHkTotList.get(0);
				if (null != sucHkTotInf.get("TXNAMT")) {
					sucHkAmt = (BigDecimal) sucHkTotInf.get("TXNAMT");
				}
				if (null != sucHkTotInf.get("TXNCNT")) {
					sucHkCnt = (Integer) sucHkTotInf.get("TXNCNT");
				}
				log.info("当前的成功划扣清算金额为:" + sucHkAmt + ",当前的成功划扣清算笔数为:" + sucHkCnt);
			}
			BigDecimal sucAmt = sucHkAmt.add(sucJfAmt);
			int sucCnt = sucHkCnt + sucJfCnt;

			log.info("当前的总成功清算金额为:" + sucAmt + ",当前的总成功清算笔数为:" + sucCnt);

			// 查找失败金额，笔数(主机状态及交易状态均为失败)
			BigDecimal falAmt = new BigDecimal("0.00");
			int falCnt = 0;
			List<Map<String, Object>> falTotList = get(GdEupsTransJournalRepository.class).findGzFalJnl(transJnl);
			if (CollectionUtils.isNotEmpty(falTotList)) {
				Map<String, Object> falTotInf = falTotList.get(0);
				Integer txnCnt = (Integer) falTotInf.get("TXNCNT");
				if (null != txnCnt) {
					falCnt = (Integer) falTotInf.get("TXNCNT");
				}
				BigDecimal txnAmt = (BigDecimal) falTotInf.get("TXNAMT");
				if (null != txnAmt) {
					falAmt = (BigDecimal) falTotInf.get("TXNAMT");
				}
			}
			log.info("明确失败的金额为:" + falAmt + ",明确失败的笔数为:" + falCnt);

			// 查找存疑笔数，金额
			BigDecimal unSureAmt = new BigDecimal("0.00");
			int unSureCnt = 0;
			List<Map<String, Object>> unSurTotList = get(GdEupsTransJournalRepository.class).findGzUnsJnl(transJnl);
			if (CollectionUtils.isNotEmpty(unSurTotList)) {
				Map<String, Object> unSurTotInf = unSurTotList.get(0);
				Integer txnCnt = (Integer) unSurTotInf.get("TXNCNT");
				if (null != txnCnt) {
					unSureCnt = txnCnt;
				}
				BigDecimal txnAmt = (BigDecimal) unSurTotInf.get("TXNAMT");
				if (null != txnAmt) {
					unSureAmt = txnAmt;
				}
			}
			log.info("当前存疑的金额为:" + unSureAmt + ",当前存疑的笔数为:" + unSureCnt);

			context.setData("sucCnt", String.valueOf(sucCnt));
			context.setData("sucAmt", sucAmt.toString());

			context.setData("sucHkCnt", String.valueOf(sucHkCnt));
			context.setData("sucHkAmt", sucHkAmt.toString());

			context.setData("sucJfCnt", String.valueOf(sucJfCnt));
			context.setData("sucJfAmt", sucJfAmt.toString());

			context.setData("falCnt", String.valueOf(falCnt));
			context.setData("falAmt", falAmt.toString());

			context.setData("unSureCnt", String.valueOf(unSureCnt));
			context.setData("unSureAmt", unSureAmt.toString());

		}
		// 如果是查询明细信息
		else if (GDConstants.GZ_ELE_PRINT_FLAG_DETAIL.equals(delFlg)) {

			// detailFlg为0表示成功，1表示存疑，A表示全部
			if ("A".equals(detailFlg)) {
				List<Map<String, Object>> allDtlJnl = get(GdEupsTransJournalRepository.class).findGdJnlDetail(transJnl);
				if (CollectionUtils.isEmpty(allDtlJnl)) {
					throw new CoreException(GDErrorCodes.EUPS_ELE_GZ_NO_RECORD);
				}
				context.setData("detailList", allDtlJnl);
				log.info("查询的汇总信息为:" + allDtlJnl);
			}
			else if ("0".equals(detailFlg)) {
				List<Map<String, Object>> allDtlJnl = new ArrayList<Map<String, Object>>();
				// 查询缴费流水
				List<Map<String, Object>> allDtlJnlHk = get(GdEupsTransJournalRepository.class).findGdJnlSucHkDetail(transJnl);
				if (CollectionUtils.isNotEmpty(allDtlJnlHk)) {
					for (Map<String, Object> detail : allDtlJnlHk) {
						allDtlJnl.add(detail);
					}
				}

				List<Map<String, Object>> allDtlJnlJf = get(GdEupsTransJournalRepository.class).findGdJnlSucJfDetail(transJnl);
				if (CollectionUtils.isNotEmpty(allDtlJnlJf)) {
					for (Map<String, Object> detail : allDtlJnlHk) {
						allDtlJnl.add(detail);
					}
				}

				if (CollectionUtils.isEmpty(allDtlJnl)) {
					throw new CoreException(GDErrorCodes.EUPS_ELE_GZ_NO_RECORD);
				}
				context.setData("detailList", allDtlJnl);
				log.info("查询的成功明细信息为:" + allDtlJnl);
			}
			else if ("1".equals(detailFlg)) {
				List<Map<String, Object>> allDtlJnl = get(GdEupsTransJournalRepository.class).findGdJnlUnsDetail(transJnl);
				if (CollectionUtils.isEmpty(allDtlJnl)) {
					throw new CoreException(GDErrorCodes.EUPS_ELE_GZ_NO_RECORD);
				}
				context.setData("detailList", allDtlJnl);
				log.info("查询的存疑明细信息为:" + allDtlJnl);
			}

		}
		// 打印明细
		else if (GDConstants.GZ_ELE_PRINT_FLAG_PRINT.equals(delFlg)) {
			// detailFlg为0表示成功，1表示存疑，A表示全部
			context.setData("prtDte", DateUtils.format(new Date(), "yyyy-MM-dd"));
			
			int allCnt = 0;   //流水总笔数
			BigDecimal allAmt = new BigDecimal("0.00");
			
			List<Map<String, Object>> allJnl = get(GdEupsTransJournalRepository.class).findGzAllCntAmt(transJnl);
			if (CollectionUtils.isNotEmpty(allJnl)) {
				Map<String, Object> allJnlDetail = allJnl.get(0);
				if (null != allJnlDetail.get("ALLAMT")) {
					allAmt = (BigDecimal) allJnlDetail.get("ALLAMT");
				}
				if (null != allJnlDetail.get("ALLCNT")) {
					allCnt = (Integer) allJnlDetail.get("ALLCNT");
				}
				log.info("当前的总笔数为:" + allCnt + ",当前的总金额为:" + allAmt);
			}
			
			BigDecimal sucJfAmt = new BigDecimal("0.00");
			int sucJfCnt = 0;
			List<Map<String, Object>> sucJfTotList = get(GdEupsTransJournalRepository.class).findGzSucJnlJF(transJnl);
			if (CollectionUtils.isNotEmpty(sucJfTotList)) {
				Map<String, Object> sucJfTotInf = sucJfTotList.get(0);
				if (null != sucJfTotInf.get("TXNAMT")) {
					sucJfAmt = (BigDecimal) sucJfTotInf.get("TXNAMT");
				}
				if (null != sucJfTotInf.get("TXNCNT")) {
					sucJfCnt = (Integer) sucJfTotInf.get("TXNCNT");
				}
				log.info("当前的成功缴费清算金额为:" + sucJfAmt + ",当前的成功缴费清算笔数为:" + sucJfCnt);
			}

			BigDecimal sucHkAmt = new BigDecimal("0.00");
			int sucHkCnt = 0;
			List<Map<String, Object>> sucHkTotList = get(GdEupsTransJournalRepository.class).findGzSucJnlHK(transJnl);
			if (CollectionUtils.isNotEmpty(sucHkTotList)) {
				Map<String, Object> sucHkTotInf = sucHkTotList.get(0);
				if (null != sucHkTotInf.get("TXNAMT")) {
					sucHkAmt = (BigDecimal) sucHkTotInf.get("TXNAMT");
				}
				if (null != sucHkTotInf.get("TXNCNT")) {
					sucHkCnt = (Integer) sucHkTotInf.get("TXNCNT");
				}
				log.info("当前的成功划扣清算金额为:" + sucHkAmt + ",当前的成功划扣清算笔数为:" + sucHkCnt);
			}
			BigDecimal sucAmt = sucHkAmt.add(sucJfAmt);
			int sucCnt = sucHkCnt + sucJfCnt;

			log.info("当前的总成功清算金额为:" + sucAmt + ",当前的总成功清算笔数为:" + sucCnt);

			// 查找失败金额，笔数(主机状态及交易状态均为失败)
			BigDecimal falAmt = new BigDecimal("0.00");
			int falCnt = 0;
			List<Map<String, Object>> falTotList = get(GdEupsTransJournalRepository.class).findGzFalJnl(transJnl);
			if (CollectionUtils.isNotEmpty(falTotList)) {
				Map<String, Object> falTotInf = falTotList.get(0);
				Integer txnCnt = (Integer) falTotInf.get("TXNCNT");
				if (null != txnCnt) {
					falCnt = (Integer) falTotInf.get("TXNCNT");
				}
				BigDecimal txnAmt = (BigDecimal) falTotInf.get("TXNAMT");
				if (null != txnAmt) {
					falAmt = (BigDecimal) falTotInf.get("TXNAMT");
				}
			}
			log.info("明确失败的金额为:" + falAmt + ",明确失败的笔数为:" + falCnt);

			// 查找存疑笔数，金额
			BigDecimal unSureAmt = new BigDecimal("0.00");
			int unSureCnt = 0;
			List<Map<String, Object>> unSurTotList = get(GdEupsTransJournalRepository.class).findGzUnsJnl(transJnl);
			if (CollectionUtils.isNotEmpty(unSurTotList)) {
				Map<String, Object> unSurTotInf = unSurTotList.get(0);
				Integer txnCnt = (Integer) unSurTotInf.get("TXNCNT");
				if (null != txnCnt) {
					unSureCnt = txnCnt;
				}
				BigDecimal txnAmt = (BigDecimal) unSurTotInf.get("TXNAMT");
				if (null != txnAmt) {
					unSureAmt = txnAmt;
				}
			}
			log.info("当前存疑的金额为:" + unSureAmt + ",当前存疑的笔数为:" + unSureCnt);
			

			context.setData("allCnt", allCnt);   //所有的笔数
			context.setData("sucHkCnt", sucHkCnt);   //成功划扣总笔数
			context.setData("sucJfCnt", sucJfCnt);   //成功缴费总笔数
			context.setData("failCnt", falCnt);
			context.setData("doubCnt", unSureCnt);

			context.setData("allAmt", allAmt);   //所有的金额
			context.setData("sucHkAmt", sucHkAmt);   //成功划扣总金额
			context.setData("sucJfAmt", sucJfAmt);   //成功缴费总金额
			context.setData("failAmt", falAmt);
			context.setData("doubAmt", unSureAmt);

			Map<String, String> map = new HashMap<String, String>();
			String result = null;
			// 生成报表
			VelocityTemplatedReportRender render = new VelocityTemplatedReportRender();
			try {
				render.afterPropertiesSet();
			} catch (Exception e) {
				e.printStackTrace();
			}

			String fileName = new String();

			if ("0".equals(detailFlg)) {
				// 成功

				List<Map<String, Object>> allDtlJnl = new ArrayList<Map<String, Object>>();
				// 查询缴费流水
				List<Map<String, Object>> allDtlJnlHk = get(GdEupsTransJournalRepository.class).findGdJnlSucHkDetail(transJnl);
				if (CollectionUtils.isNotEmpty(allDtlJnlHk)) {
					for (Map<String, Object> detail : allDtlJnlHk) {
						System.out.println("TxnTme=" + detail.get("TXN_TME"));
						String txnTme = DateUtils.format((Date)detail.get("TXN_TME"), "HH:mm:ss");
						detail.put("TXN_TME", txnTme);
						allDtlJnl.add(detail);
					}
				}

				List<Map<String, Object>> allDtlJnlJf = get(GdEupsTransJournalRepository.class).findGdJnlSucJfDetail(transJnl);
				if (CollectionUtils.isNotEmpty(allDtlJnlJf)) {
					for (Map<String, Object> detail : allDtlJnlHk) {
						System.out.println("TxnTme=" + detail.get("TXN_TME"));
						String txnTme = DateUtils.format((Date)detail.get("TXN_TME"), "HH:mm:ss");
						detail.put("TXN_TME", txnTme);
						allDtlJnl.add(detail);
					}
				}

				// List<Map<String, Object>> allDtlJnlSuc =
				// get(GdEupsTransJournalRepository.class).findGdJnlSucDetail(transJnl);

				context.setData("allDtlJnl", allDtlJnl);
				log.info("查询的成功明细信息为:" + allDtlJnl);

				map.put("commonPrtRpt", "config/report/elec01/commonPrintReport_suc.vm");
				render.setReportNameTemplateLocationMapping(map);
				context.setData("eles", allDtlJnl);
				result = render.renderAsString("commonPrtRpt", context);

				log.info(result);

				fileName = "gdElecClrJnl_SUC.txt"; // 文件名:成功

			} else if ("1".equals(detailFlg)) { // 1表示存疑
				// 存疑
				List<Map<String, Object>> allDtlJnlUns = get(GdEupsTransJournalRepository.class).findGdJnlUnsDetail(transJnl);
				context.setData("allDtlJnl", allDtlJnlUns);
				log.info("查询的存疑明细信息为:" + allDtlJnlUns);

				map.put("commonPrtRpt", "config/report/elec01/commonPrintReport_uns.vm");
				render.setReportNameTemplateLocationMapping(map);
				context.setData("eles", allDtlJnlUns);
				result = render.renderAsString("commonPrtRpt", context);
				log.info(result);

				fileName = "gdElecClrJnl_UNS.txt"; // 文件名:存疑

			}
			else if ("2".equals(detailFlg)) { // 失败
				// 失败
				List<Map<String, Object>> allDtlJnlFal = get(GdEupsTransJournalRepository.class).findGdJnlFalDetail(transJnl);
				context.setData("allDtlJnl", allDtlJnlFal);
				log.info("查询的失败明细信息为:" + allDtlJnlFal);

				map.put("commonPrtRpt", "config/report/elec01/commonPrintReport_fal.vm");
				render.setReportNameTemplateLocationMapping(map);
				context.setData("eles", allDtlJnlFal);
				result = render.renderAsString("commonPrtRpt", context);
				log.info(result);

				fileName = "gdElecClrJnl_FAL.txt"; // 文件名:失败
			}
			else if ("3".equals(detailFlg)) { // 其他
				// 其他
				List<Map<String, Object>> allDtlJnlOth = get(GdEupsTransJournalRepository.class).findGdJnlOthDetail(transJnl);
				context.setData("allDtlJnl", allDtlJnlOth);
				log.info("查询的其他情况明细信息为:" + allDtlJnlOth);

				map.put("commonPrtRpt", "config/report/elec01/commonPrintReport_fal.vm");
				render.setReportNameTemplateLocationMapping(map);
				context.setData("eles", allDtlJnlOth);
				result = render.renderAsString("commonPrtRpt", context);
				log.info(result);

				fileName = "gdElecClrJnl_OTH.txt"; // 文件名:其他
			}
			else if ("A".equals(detailFlg)) { // 全部

				List<Map<String, Object>> allDtlJnl = get(GdEupsTransJournalRepository.class).findGdJnlAllDetail(transJnl);

				context.setData("allDtlJnl", allDtlJnl);
				log.info("查询的所有明细信息为:" + allDtlJnl);

				map.put("commonPrtRpt", "config/report/elec01/commonPrintReport_all.vm");
				render.setReportNameTemplateLocationMapping(map);
				context.setData("eles", allDtlJnl);
				result = render.renderAsString("commonPrtRpt", context);

				log.info(result);

				fileName = "gdElecClrJnl_All.txt"; // 文件名:成功

			}

//			String filPath = "/home/bbipadm/data/GDEUPSB/report/";
			String filPath="D:/dat/lshb/";

			String JYPath = filPath + fileName;

			log.info("====================== JYPath =================");
			log.info(JYPath);

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
			log.info("报表文件生成！！NEXT 上传FTP");

//			// 上传FTP
//			EupsThdFtpConfig sendFileToBBOSConfig = get(EupsThdFtpConfigRepository.class).findOne("sendFileToBBOS");
//			// FTP上传设置
//			FTPTransfer tFTPTransfer = new FTPTransfer();
//			tFTPTransfer.setHost(sendFileToBBOSConfig.getThdIpAdr());
//			tFTPTransfer.setPort(Integer.parseInt(sendFileToBBOSConfig.getBidPot()));
//			tFTPTransfer.setUserName(sendFileToBBOSConfig.getOppNme());
//			tFTPTransfer.setPassword(sendFileToBBOSConfig.getOppUsrPsw());
//
//			try {
//				tFTPTransfer.logon();
//				Resource tResource = new FileSystemResource(JYPath);
//				tFTPTransfer.putResource(tResource,
//						"/home/weblogic/JumpServer/WEB-INF/data/mftp_recv/",
//						fileName);
//
//			} catch (Exception e) {
//				throw new CoreException("文件上传失败");
//			} finally {
//				tFTPTransfer.logout();
//			}
//
//			context.setData("fleNme", fileName);
//			log.info("文件上传完成，等待打印！" + context);

		}

	}

}
