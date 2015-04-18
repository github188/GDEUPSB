package com.bocom.bbip.gdeupsb.action.transportfee;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.action.common.OperateFTPAction;
import com.bocom.bbip.eups.action.common.OperateFileAction;
import com.bocom.bbip.eups.adaptor.ThirdPartyAdaptor;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.repository.EupsThdFtpConfigRepository;
import com.bocom.bbip.file.reporting.impl.VelocityTemplatedReportRender;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.repository.GDEupsbTrspFeeInfoRepository;
import com.bocom.bbip.gdeupsb.repository.GDEupsbTrspInvChgInfoRepository;
import com.bocom.bbip.gdeupsb.repository.TrspCheckTmpRepository;
import com.bocom.bbip.thd.org.apache.commons.collections.CollectionUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class NodTrspCheckFileAction extends BaseAction {
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
	@Qualifier("callThdTradeManager")
	ThirdPartyAdaptor callThdTradeManager;
	private final static Log log = LogFactory
			.getLog(NodTrspCheckFileAction.class);

	public void execute(Context ctx) throws CoreException, CoreRuntimeException {
		log.info("NodTrspCheckFileAction start......");

		// nodCheckTrspFile 输入 交易请求头 requestHeader REF
		// nodCheckTrspFile 输入 业务类型 eupsBusTyp CHAR 10 Y
		// nodCheckTrspFile 输入 起始日期 startDate CHAR 8 Y
		// nodCheckTrspFile 输入 终止日期 endDate CHAR 8 Y
		// nodCheckTrspFile 输入 网点号 nodNo CHAR 11
		// nodCheckTrspFile 输入 统计方式 journalModel VARCHAR 1 Y
		// nodCheckTrspFile 输出 交易返回头 responseHeader REF
		// nodCheckTrspFile 输出 对账结果标志 chkFlg CHAR 1
		// nodCheckTrspFile 输出 总笔数 sucCnt CHAR 8
		// nodCheckTrspFile 输出 总金额 sucAmt CHAR 17
		// nodCheckTrspFile 输出 报表文件名 filNam CHAR 20

		// String
		// tChkNo=((BTPService)get("BTPService")).applyBatchNo(ParamKeys.BUSINESS_CODE_COLLECTION);
		String journalModel = (String) ctx.getData("journalModel");
		Map<String, Object> map = new HashMap<String, Object>();

		Date startDate = DateUtils.parse((String) ctx.getData("startDate"));
		if (ctx.getData("endDate") == null) {
			ctx.setData("endDate",
					DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMdd));
		}
		Date endDate = DateUtils.parse((String) ctx.getData("endDate"));
		map.put("startDate", startDate);
		map.put("endDate", endDate);
		map.put("nodNo", (String) ctx.getData("nodNo"));
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
		// 重写 得到总笔数 总金额
		List<Map<String, Object>> list = gdEupsbTrspFeeInfoRepository
				.findSum(mapSum);
		System.out.println("@@@list=" + list);
		// 得到总笔数 总金额
		if (CollectionUtils.isEmpty(list)) {
			context.setData(ParamKeys.RSP_CDE, "329999");
			context.setData(ParamKeys.RSP_MSG, "查询统计信息失败");
			throw new CoreException("查询统计信息失败");
		} else {
			System.out.println("@@@@@@@@@@@@" + list.get(0));
			int ornCnt = Integer.parseInt(list.get(0).get("ORNCNT") + "");
			BigDecimal ornAmt = new BigDecimal(list.get(0).get("ORNAMT") + "");
			int sucCnt = Integer.parseInt(list.get(0).get("SUCCNT") + "");
			BigDecimal sucAmt = new BigDecimal(list.get(0).get("SUCAMT") + "");
			context.setData("ornCnt", ornCnt);
			context.setData("ornAmt", ornAmt);
			context.setData("sucCnt", sucCnt);
			context.setData("sucAmt", sucAmt);
		}
*/
		if (0 == i) { // ~~~~~~~~汇总方式
			rptFmt = rptFmt + "00";
		} else if (1 == i) {// ~~~~~~~~~~~~~清单方式
			rptFmt = rptFmt + "01";
		} else if (2 == i) {// ~~~~~~~~~~~~~更改发票清单
			rptFmt = rptFmt + "02";
		} else if (3 == i) {// ~~~~~~~~~~~~~未打印发票清单
			rptFmt = rptFmt + "03";
		} else {
			context.setData(GDParamKeys.MSGTYP, "E");
			context.setData(ParamKeys.RSP_CDE, "329999");
			context.setData(ParamKeys.RSP_MSG, "统计模式错误");
			throw new CoreException("统计模式错误");
		}
		String path = "config/report/zhTransport/" + rptFmt + ".vm";
		
		log.info(">>>>>>>>>>>>>rptFmt=" + rptFmt);
		log.info(">>>>>>>>>>>>>path=" + path);
		
		context.setData(rptFmt, path);
		// TODO 报表生成
		VelocityTemplatedReportRender render = new VelocityTemplatedReportRender();
		try {
			render.afterPropertiesSet();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String rptFil = "Car"
				+ context.getData("tlr").toString()
				+ context.getData(GDParamKeys.START_DATE).toString().substring(4) + ".dat";
		// 拼装文件
		Map<String, String> map = new HashMap<String, String>();
		map.put(rptFil, path);
		render.setReportNameTemplateLocationMapping(map);
		context.setData("eles", detailList);
		String result = render.renderAsString(rptFil, context);
		System.out
				.println("~~~~~~~~~~~~~result~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		System.out.println(result);
		// 文件路径
		StringBuffer rpFmts = new StringBuffer();
		rpFmts.append("D:/test/trspTest/");
		File file = new File(rptFil.toString());
		if (!file.exists()) {
			file.mkdirs();
		}
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(rpFmts
					.append(rptFil).toString());
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
					fileOutputStream, "GBK");
			BufferedWriter bufferedWriter = new BufferedWriter(
					outputStreamWriter);
			PrintWriter printWriter = new PrintWriter(bufferedWriter);
			printWriter.write(result);
			// 关闭
			printWriter.close();
			bufferedWriter.close();
			outputStreamWriter.close();
			fileOutputStream.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// TODO 没有确定下名字rptFil 还是 fileName
		// String fileName=context.getData("fileName").toString();
		System.out
				.println("~~~~~~~~~~~~~sendFile~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		// sendFile(context,rptFmt);
	}
}
