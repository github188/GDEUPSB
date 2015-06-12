package com.bocom.bbip.gdeupsb.strategy.sgrt00;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.comp.BBIPPublicServiceImpl;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsThdBaseInfo;
import com.bocom.bbip.eups.entity.EupsTransJournal;
import com.bocom.bbip.eups.repository.EupsThdBaseInfoRepository;
import com.bocom.bbip.eups.repository.EupsTransJournalRepository;
import com.bocom.bbip.eups.spi.service.single.PayUnilateralToBankService;
import com.bocom.bbip.eups.spi.vo.CommHeadDomain;
import com.bocom.bbip.eups.spi.vo.PayFeeOnlineDomain;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GdTbcCusAgtInfo;
import com.bocom.bbip.gdeupsb.repository.GdTbcCusAgtInfoRepository;
import com.bocom.bbip.service.BGSPServiceAccessObject;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.bbip.utils.NumberUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

public class PayUnilateralToBankServiceActionSGRT00 implements
		PayUnilateralToBankService {

	private final static Logger log = LoggerFactory
			.getLogger(PayUnilateralToBankServiceActionSGRT00.class);

	@Autowired
	BGSPServiceAccessObject serviceAccess;
	@Autowired
	BBIPPublicServiceImpl publicService;
	@Autowired
	GdTbcCusAgtInfoRepository cusAgtInfoRepository;
	@Autowired
	BBIPPublicService bbipPublicService;
	@Autowired
	EupsThdBaseInfoRepository eupsThdBaseInfoRepository;
	@Autowired
	EupsTransJournalRepository eupsTransJournalRepository;

	@Override
	public Map<String, Object> aftPayToBank(CommHeadDomain commheaddomain,
			PayFeeOnlineDomain payfeeonlinedomain, Context context)
			throws CoreException {
		log.info("~~~~~~~~~~~~~ SGRT00 aftPayToBank start with context~~~~~~~~~~~~~~\n" + context);
		context.setData(GDParamKeys.RSP_CDE, "9999");
		context.setData(GDParamKeys.RSP_MSG, "交易失败");
		String hstRspString = context.getData("responseCode").toString();

		if (Constants.RESPONSE_CODE_SUCC.equals(hstRspString)
				|| Constants.RESPONSE_CODE_SUCC_HOST.equals(hstRspString)) {
			context.setData(GDParamKeys.RSP_MSG, Constants.RESPONSE_MSG);
			context.setData(GDParamKeys.RSP_CDE, "0000");
		} else {
			context.setData(GDParamKeys.RSP_MSG,
					context.getData("responseMessage").toString().trim());
		}
		// context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
		context.setData("BANK_SEQ", context.getData("sqn"));
		log.info("~~~~~~~~~~~~~ SGRT00 aftPayToBank end with context~~~~~~~~~~~~~~\n" + context);
		return null;
	}

	@Override
	public Map<String, Object> prePayToBank(CommHeadDomain commheaddomain,
			PayFeeOnlineDomain payfeeonlinedomain, Context context)
			throws CoreException {
		log.info("~~~~~~~~~~~~~ SGRT00 prePayToBank start with context ~~~~~~~~~~~~~~\n" + context);
		String bk = "01441999999";
		String br = "01441800999";
		String tlr = null;
		context.setData(ParamKeys.BK, bk);
		context.setData(ParamKeys.BR, br);
		tlr = bbipPublicService.getETeller(bk);
		context.setData(ParamKeys.TELLER, tlr);
		log.info("~~~~~~~~~~ context after set bk, br, tlr in prePayToBank: \n" + context);
		
		context.setData("payMod", "0");
		context.setData(ParamKeys.CHL_TYP, "L");// <!--交易渠道类型：L第三方系统-->
		context.setData("vchChk", "1");// <!--监督标志由业务上确定-->
		context.setData("cchCod", "00000000");
		context.setData("aplCls", "438");
		context.setData("mstChk", "1");
		context.setData("itgTyp", "0");
//		context.setData(ParamKeys.TXN_TYP, Constants.RESPONSE_TYPE_SUCC);
		
		
		GdTbcCusAgtInfo cusAgtInfo = cusAgtInfoRepository.findOne(context
				.getData("CUST_ID").toString());

		EupsTransJournal jnl = new EupsTransJournal();
		jnl.setSqn(context.getData(ParamKeys.SEQUENCE).toString().trim());
				
		
		if (cusAgtInfo == null) {
			context.setData(GDParamKeys.RSP_CDE, "9999");
			context.setData(GDParamKeys.RSP_MSG, "客户未签约");
			jnl.setRspMsg("客户未签约");
			eupsTransJournalRepository.update(jnl);
			throw new CoreException(GDParamKeys.RSP_MSG);
		}
		// 校验协议状态 --add by MQ
		// 同时解决生产测试之初只允许部分客户交易的问题，数据库将不允许交易的客户status置1
		if (!"0".equals(cusAgtInfo.getStatus())) { // 非0，协议状态为删除状态，不允许交易
			context.setData(GDParamKeys.RSP_CDE, "9999");
			context.setData(GDParamKeys.RSP_MSG, "该客户处于不允许交易状态");
			jnl.setRspMsg("该客户处于不允许交易状态");
			eupsTransJournalRepository.update(jnl);
			throw new CoreException(GDParamKeys.RSP_MSG);
		}
		context.setData(ParamKeys.CUS_AC, cusAgtInfo.getActNo());

		// String txnAmt = context.getData("amount");
		// BigDecimal realAmt = NumberUtils.centToYuan(txnAmt);
		// context.setData(ParamKeys.TXN_AMOUNT, realAmt);

		String thdAmt = context.getData("QTY_TRADE");
		BigDecimal realAmt = new BigDecimal("0.00");

		if (!thdAmt.contains(".")) {
			realAmt = NumberUtils.centToYuan(thdAmt);
		} else {
			context.setData(GDParamKeys.RSP_CDE, "9999");
			context.setData(GDParamKeys.RSP_MSG, "交易金额格式错误！");
			throw new CoreException("9999");
		}

		context.setData(ParamKeys.TXN_AMT, realAmt);
		
		EupsThdBaseInfo baseInfo = new EupsThdBaseInfo();
		baseInfo.setEupsBusTyp(context.getData(ParamKeys.EUPS_BUSS_TYPE).toString().trim());
		baseInfo.setUseSts("0");
		List<EupsThdBaseInfo> baseList = eupsThdBaseInfoRepository.find(baseInfo);
		String comNotoAcp = baseList.get(0).getComNo().toString().trim();
		context.setData(ParamKeys.COMPANY_NO, comNotoAcp);
		
		log.info("~~~~~~~~~~~~~ SGRT00 prePayToBank end with context ~~~~~~~~~~~~~~\n" + context);
		return null;
	}

	@Override
	public Map<String, Object> prepareCheckDeal(CommHeadDomain commheaddomain,
			PayFeeOnlineDomain payfeeonlinedomain, Context context)
			throws CoreException {
		log.info("PayUnilateralToBankServiceActionSGRT00 prepareCheckDeal start with context : \n" + context);
		context.setData(GDParamKeys.RSP_CDE, "9999"); // 初始化为交易失败
		context.setData(GDParamKeys.RSP_MSG, "交易失败");

		context.setData("acoBr", "01441800999");

		String bk = "01441999999";
		String br = "01441800999";
		String tlr = null;
		context.setData(ParamKeys.BK, bk);
		context.setData(ParamKeys.BR, br);
		tlr = bbipPublicService.getETeller(bk);
		context.setData(ParamKeys.TELLER, tlr);
		
		log.info("~~~~~~~~~~ context after set bk, br, tlr in prepareCheckDeal: \n" + context);

		if (null != context.getData("TRAN_TIME")) {
			context.setData("txnTme", DateUtils.parse(
					context.getData("TRAN_TIME").toString(),
					DateUtils.STYLE_yyyyMMddHHmmss));
			context.setData(
					ParamKeys.TXN_DATE,
					DateUtils.parse(context.getData("TRAN_TIME").toString()
							.substring(0, 8), DateUtils.STYLE_yyyyMMdd));
		} else {
			context.setData("txnTme", new Date());
			context.setData(ParamKeys.TXN_DATE, new Date());
		}

		context.setData(ParamKeys.THD_TXN_CDE, "483805");
		context.setData(ParamKeys.RSV_FLD2, context.getData("DPT_ID")); 
		context.setData(ParamKeys.THD_CUS_NO, context.getData("CUST_ID"));
		
		log.info("~~~~~~~~~~~~~ SGRT00 prepareCheckDeal end with context~~~~~~~~~~~~~~\n" + context);
		return null;
	}
}
