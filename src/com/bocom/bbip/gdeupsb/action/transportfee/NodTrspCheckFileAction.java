package com.bocom.bbip.gdeupsb.action.transportfee;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.action.common.OperateFTPAction;
import com.bocom.bbip.eups.action.common.OperateFileAction;
import com.bocom.bbip.eups.adaptor.ThirdPartyAdaptor;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsThdFtpConfig;
import com.bocom.bbip.eups.repository.EupsThdFtpConfigRepository;
import com.bocom.bbip.file.MftpTransfer;
import com.bocom.bbip.file.reporting.impl.VelocityTemplatedReportRender;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.repository.GDEupsbTrspFeeInfoRepository;
import com.bocom.bbip.gdeupsb.repository.GDEupsbTrspInvChgInfoRepository;
import com.bocom.bbip.gdeupsb.repository.TrspCheckTmpRepository;
import com.bocom.bbip.thd.org.apache.commons.collections.CollectionUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class NodTrspCheckFileAction extends BaseAction {
	@Autowired
	VelocityTemplatedReportRender render;
	@Autowired
	TrspCheckTmpRepository trspCheckTmpRepository;
	@Autowired
	GDEupsbTrspFeeInfoRepository gdEupsbTrspFeeInfoRepository;
	@Autowired
	OperateFTPAction operateFTPAction;
	@Autowired
	EupsThdFtpConfigRepository eupsThdFtpConfigRepository;
	@Autowired
	OperateFileAction operateFileAction;
	@Autowired
	GDEupsbTrspInvChgInfoRepository gdEupsbTrspInvChgInfoRepository;
	@Autowired
	BBIPPublicService bbipPublicService;

	@Autowired
	@Qualifier("callThdTradeManager")
	ThirdPartyAdaptor callThdTradeManager;
	private final static Logger log = LoggerFactory
			.getLogger(NodTrspCheckFileAction.class);

	public void execute(Context ctx) throws CoreException, CoreRuntimeException {
		log.info("NodTrspCheckFileAction start......");
		ctx.setData("chkFlg", "F");

		String journalModel = (String) ctx.getData("journalModel");
		String nodNo = null;
		Map<String, Object> map = new HashMap<String, Object>();

		Date startDate = DateUtils.parse((String) ctx.getData("startDate"));
		if (ctx.getData("endDate") == null) {
			ctx.setData("endDate",
					DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMdd));
		}

		if (StringUtils.isBlank((String) ctx.getData("nodNo"))) {
			nodNo = ctx.getData(ParamKeys.BR);
		}
		nodNo = (String) ctx.getData("nodNo");

		Date endDate = DateUtils.parse((String) ctx.getData("endDate"));
		map.put("startDate", startDate);
		map.put("endDate", endDate);
		map.put("nodNo", nodNo);
		map.put("bk", ctx.getData(ParamKeys.BK).toString());
		if ("0".equals(journalModel)) { // 汇总
			// 得到搜索的集合
			List<Map<String, Object>> list = gdEupsbTrspFeeInfoRepository
					.find0(map);
			if (CollectionUtils.isEmpty(list)) {
				throw new CoreException(ErrorCodes.EUPS_QUERY_NO_DATA);
			}
			ctx.setData("ornCnt", list.get(0).get("ORNCNT"));
			ctx.setData("ornAmt", list.get(0).get("ORNAMT"));
			ctx.setData("sucCnt", list.get(0).get("SUCCNT"));
			ctx.setData("sucAmt", list.get(0).get("SUCAMT"));
			log.info(">>>>>>>>>>..context:" + ctx);
			// 清单打印
			printDetail(ctx, list, map);
		}
		if ("1".equals(journalModel)) { // 清单方式
			List<Map<String, Object>> list = gdEupsbTrspFeeInfoRepository
					.find1(map);
			if (CollectionUtils.isEmpty(list)) {
				throw new CoreException(ErrorCodes.EUPS_QUERY_NO_DATA);
			}

			ctx.setData("totCnt", list.get(0).get("ORNCNT"));
			ctx.setData("totAmt", list.get(0).get("ORNAMT"));
			ctx.setData("sucTotCnt", list.get(0).get("SUCCNT"));
			ctx.setData("sucTotAmt", list.get(0).get("SUCAMT"));
			log.info(">>>>>>>>>>..context:" + ctx);
			printDetail(ctx, list, map);
		}

		if ("2".equals(journalModel)) {// 更改发票清单
			List<Map<String, Object>> list = gdEupsbTrspInvChgInfoRepository
					.find2(map);
			if (CollectionUtils.isEmpty(list)) {
				throw new CoreException(ErrorCodes.EUPS_QUERY_NO_DATA);
			}
			ctx.setData("totCnt", list.get(0).get("TOLNUM"));
			log.info(">>>>>>>>>>..context:" + ctx);
			printDetail(ctx, list, map);
		}

		// if ("3".equals(journalModel)) { //未打印发票清单
		// List<Map<String, Object>> list = gdEupsbTrspFeeInfoRepository
		// .find3(map);
		// if (CollectionUtils.isEmpty(list)) {
		// throw new CoreException(ErrorCodes.EUPS_QUERY_NO_DATA);
		// }
		// printDetail(ctx, list, map);
		// }
		ctx.setData("chkFlg", "S");

	}

	/**
	 * 打印清单
	 */
	public void printDetail(Context context,
			List<Map<String, Object>> detailList, Map<String, Object> mapSum)
			throws CoreException {
		log.info("~~~~~~~~~~~Start  CheckTrspFile   printDetail");
		// 报表模式
		int i = Integer.parseInt(context.getData(GDParamKeys.JOURNAL_MODEL)
				.toString());
		// context.setData("rptFil", rptFil);

		String rptFmt = "rptFmt";
		/**
		 * // 重写 得到总笔数 总金额 List<Map<String, Object>> list =
		 * gdEupsbTrspFeeInfoRepository .findSum(mapSum);
		 * System.out.println("@@@list=" + list); // 得到总笔数 总金额 if
		 * (CollectionUtils.isEmpty(list)) { context.setData(ParamKeys.RSP_CDE,
		 * "329999"); context.setData(ParamKeys.RSP_MSG, "查询统计信息失败"); throw new
		 * CoreException("查询统计信息失败"); } else { System.out.println("@@@@@@@@@@@@"
		 * + list.get(0)); int ornCnt =
		 * Integer.parseInt(list.get(0).get("ORNCNT") + ""); BigDecimal ornAmt =
		 * new BigDecimal(list.get(0).get("ORNAMT") + ""); int sucCnt =
		 * Integer.parseInt(list.get(0).get("SUCCNT") + ""); BigDecimal sucAmt =
		 * new BigDecimal(list.get(0).get("SUCAMT") + "");
		 * context.setData("ornCnt", ornCnt); context.setData("ornAmt", ornAmt);
		 * context.setData("sucCnt", sucCnt); context.setData("sucAmt", sucAmt);
		 * }
		 */
		if (0 == i) { // ~~~~~~~~汇总方式
			rptFmt = rptFmt + "00";
		} else if (1 == i) {// ~~~~~~~~~~~~~清单方式
			rptFmt = rptFmt + "01";
		} else if (2 == i) {// ~~~~~~~~~~~~~更改发票清单
			rptFmt = rptFmt + "02";
		} else if (3 == i) {// ~~~~~~~~~~~~~未打印发票清单 协办行对账  无此交易 
			rptFmt = rptFmt + "03";
		} else {
			context.setData(GDParamKeys.MSGTYP, "E");
			context.setData(ParamKeys.RSP_CDE, "329999");
			context.setData(ParamKeys.RSP_MSG, "统计模式错误");
			throw new CoreException("统计模式错误");
		}
		String vmPath = "config/report/zhTransport/" + rptFmt + ".vm";

		Map<String, String> mapping = new HashMap<String, String>();
		mapping.put("sample", vmPath);

		try {
			render.afterPropertiesSet();
		} catch (Exception e) {
			log.info("create report file error:", e);
			throw new CoreException(ErrorCodes.EUPS_FILE_CREATE_FAIL);
		}
		context.setData("eles", detailList);
		render.setReportNameTemplateLocationMapping(mapping);

		EupsThdFtpConfig ftpConfig = get(EupsThdFtpConfigRepository.class)
				.findOne("sendFileToBBOS");

		String filPath = ftpConfig.getLocDir();
		String result = null;
		String rptFil = "Car"
				+ context.getData("tlr").toString()
				+ context.getData(GDParamKeys.START_DATE).toString()
						.substring(4) + ".dat";

		result = render.renderAsString("sample", context);
		log.info("====================== result =================");
		log.info(result);

		String JYPath = filPath + rptFil;
		log.info("====================== JYPath =================");
		log.info(JYPath);

		// BufferedOutputStream outStream = null;

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
							.append(rptFil).toString()), "GBK")));
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

		// 上传FTP
		// FTPTransfer tFTPTransfer = new FTPTransfer();
		// FTP上传设置
		// tFTPTransfer.setHost("182.53.15.187");
		// tFTPTransfer.setPort(21);
		// tFTPTransfer.setUserName("weblogic");
		// tFTPTransfer.setPassword("123456");
		//
		// try {
		// tFTPTransfer.logon();
		// Resource tResource = new FileSystemResource(JYPath);
		// tFTPTransfer.putResource(tResource,
		// "/home/weblogic/JumpServer/WEB-INF/data/mftp_recv/", rptFil);
		//
		// } catch (Exception e) {
		// throw new CoreException("文件上传失败");
		// } finally {
		// tFTPTransfer.logout();
		// }
		//
		// context.setData("filNam", rptFil);
		// log.info("文件上传完成，等待打印！" + context);
		//

		try {
			bbipPublicService.sendFileToBBOS(new File(filPath, rptFil), rptFil,
					MftpTransfer.FTYPE_NORMAL);
		} catch (Exception e) {
			throw new CoreException(ErrorCodes.EUPS_FTP_FILEPUT_NFAIL);
		}
		
		context.setData("filNam", rptFil);
		log.info("文件上传完成，等待打印！" + context);

	}
}
