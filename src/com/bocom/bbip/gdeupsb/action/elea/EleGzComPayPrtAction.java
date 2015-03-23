package com.bocom.bbip.gdeupsb.action.elea;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.action.eupsreport.ReportHelper;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.MFTPConfigInfo;
import com.bocom.bbip.eups.repository.EupsThdFtpConfigRepository;
import com.bocom.bbip.file.reporting.impl.VelocityTemplatedReportRender;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.repository.GdEupsTransJournalRepository;
import com.bocom.bbip.gdeupsb.utils.CodeSwitchUtils;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

/**
 * 电力对公缴费记账回执打印
 * 
 * @author qc.w
 * 
 */
public class EleGzComPayPrtAction extends BaseAction {

	@Override
	public void execute(Context context) throws CoreException, CoreRuntimeException {
		log.info("EleGzComPayPrtAction start!..");
		// 转换为转入账号
		String dpTyp = context.getData(GDParamKeys.GZ_ELE_DPT_TYP);
		String inAct = CodeSwitchUtils.codeGenerator("DL_IActNo", dpTyp);
		context.setData("inAct", inAct);

		log.info("转入帐号经过转换后，由[" + dpTyp + "]，变为[" + inAct + "]");

		// 转换为转入账号名称
		String inActNm = CodeSwitchUtils.codeGenerator("DL_IActNm", dpTyp);
		log.info("转入帐号名称经过转换后，由[" + dpTyp + "]，变为[" + inActNm + "]");
		context.setData("inActNm", inActNm);

		// 内部账户转换-电力发起划账
		String cAgtNo = CodeSwitchUtils.codeGenerator("DptTypToCAgtNo_DL", dpTyp);
		log.info("划账内部账户经过转换后，由[" + dpTyp + "]，变为[" + cAgtNo + "]");
		String cAgtNoDl = cAgtNo;

		// 内部账户转换-银行发起缴费
		cAgtNo = CodeSwitchUtils.codeGenerator("DptTypToCAgtNo_YH", dpTyp);
		log.info("划账内部账户经过转换后，由[" + dpTyp + "]，变为[" + cAgtNo + "]");
		String cAgtNoYh = cAgtNo;

		// 内部账户转换-批量交易
		cAgtNo = CodeSwitchUtils.codeGenerator("DptTypToCAgtNo_PL", dpTyp);
		log.info("划账内部账户经过转换后，由[" + dpTyp + "]，变为[" + cAgtNo + "]");
		String cAgtNoPl = cAgtNo;

		cAgtNo = null;

		String bgnDte = context.getData("bgnDat"); // 开始日期
		String endDte = context.getData("endDat"); // 结束日期

		context.setData(ParamKeys.TXN_DAT, DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMdd));

		// 会计流水号
		String vchNo = context.getData(ParamKeys.MFM_VCH_NO);
		if (StringUtils.isEmpty(vchNo)) {
			vchNo = "";
		}
		// 客户账号
		String cusAc = context.getData(ParamKeys.CUS_AC);
		if (StringUtils.isEmpty(cusAc)) {
			cusAc = "";
		}

		StringBuffer rptName = new StringBuffer();
		rptName.append("电力对公缴费记账回执");
		rptName.append(bgnDte);
		rptName.append("-");
		rptName.append(endDte);
		rptName.append(".rpt");

		// 生成对公回执报表
		Map<String, Object> inparaMap = new HashMap<String, Object>();
		inparaMap.put("cAgtNoDl", cAgtNoDl);
		inparaMap.put("cAgtNoYh", cAgtNoYh);
		inparaMap.put("cAgtNoPl", cAgtNoPl);
		inparaMap.put("endDte", DateUtils.parse(endDte, DateUtils.STYLE_yyyyMMdd));
		inparaMap.put("bgnDte", DateUtils.parse(bgnDte, DateUtils.STYLE_yyyyMMdd));
		inparaMap.put("tckNo", vchNo); // 会计流水号
		inparaMap.put("cusAc", cusAc);
		log.info("经过组装，最终的查询条件inparaMap=" + inparaMap);

		List<Map<String, Object>> conPayList = get(GdEupsTransJournalRepository.class).findComPayInfo(inparaMap);

		Map<String, Object> conPayMap = conPayList.get(0);
		BigDecimal txnAmtB = (BigDecimal) conPayMap.get("TXN_AMT");
		context.setData("txnAmt", txnAmtB.toString());

		Date acDteD = (Date) conPayMap.get("AC_DTE"); // 会计日期
		context.setData("acDte", DateUtils.format(acDteD, DateUtils.STYLE_yyyyMMdd));

		String txnTlr = (String) conPayMap.get("TXN_TLR"); // 交易柜员
		context.setData("txnTlr", txnTlr);

		String mfmVchNo = (String) conPayMap.get("MFM_VCH_NO"); // 会计流水号
		context.setData("mfmVchNo", mfmVchNo);

		String cusNm = (String) conPayMap.get("CUS_NME"); // 会计流水号
		context.setData("cusNm", cusNm);
		
		String jnlCusAc = (String) conPayMap.get("CUS_AC"); // 会计流水号
		context.setData("jnlCusAc", jnlCusAc);

		log.info("交易金额为=" + txnAmtB.toString());

		log.info("conPayList查询结果为:" + conPayList);
		context.setData("eles", conPayList);

		EupsThdFtpConfigRepository eupsThdFtpConfigRepository = get(EupsThdFtpConfigRepository.class);
		ReportHelper reportHelper = get(ReportHelper.class);
		MFTPConfigInfo mftpConfigInfo =
				reportHelper.getMFTPConfigInfo(eupsThdFtpConfigRepository);
		log.info((new StringBuilder("mftpConfigInfo:>>>>").append(BeanUtils.toMap(mftpConfigInfo))).toString());

		VelocityTemplatedReportRender render = new
				VelocityTemplatedReportRender();
		try {
			render.afterPropertiesSet();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put("sample", "config/report/elec01/eleGzComPay.vm");
		render.setReportNameTemplateLocationMapping(map);
		String result = render.renderAsString("sample", context);
		log.info(result);
		String date = DateUtils.format(new Date(), DateUtils.STYLE_HHmmss);
		reportHelper.createFileAndSendMFTP(context, result, rptName,
				mftpConfigInfo);
		context.setData("filName", rptName);
		log.info("QueryAndPrintTransJournalServiceActionWATR00 execute end ... ...");
	}

}
