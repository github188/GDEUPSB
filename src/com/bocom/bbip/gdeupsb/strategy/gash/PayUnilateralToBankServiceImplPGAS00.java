package com.bocom.bbip.gdeupsb.strategy.gash;

import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.adaptor.ThirdPartyAdaptor;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.spi.service.single.PayUnilateralToBankService;
import com.bocom.bbip.eups.spi.vo.CommHeadDomain;
import com.bocom.bbip.eups.spi.vo.PayFeeOnlineDomain;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GdGasCusAll;
import com.bocom.bbip.gdeupsb.repository.GdGasCusAllRepository;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

/**
 * 燃气单笔托收
 * 
 * @author WangMQ
 * 
 */
public class PayUnilateralToBankServiceImplPGAS00 implements
		PayUnilateralToBankService {
	private Logger logger = LoggerFactory
			.getLogger(PayUnilateralToBankServiceImplPGAS00.class);
	
	@Autowired
	ThirdPartyAdaptor callThdTradeManager;

	@Autowired
	GdGasCusAllRepository gdGasCusAllRepository;
	
	@Autowired
	BBIPPublicService bbipPublicService;

	// 交易前服务处理
	@Override
	public Map<String, Object> prepareCheckDeal(CommHeadDomain commHeadDomain,
			PayFeeOnlineDomain payFeeOnlineDomain, Context context)
			throws CoreException {
		logger.info("PayUnilateralToBankServiceImplPGAS00@prepareCheckDeal start!");
		logger.info("======context:" + context);

		context.setData(ParamKeys.THD_TXN_STS, "S");
		String bk = "01491999999";
		String br = "01491001999";
		context.setData(ParamKeys.BR, br);
		context.setData(ParamKeys.BK, bk);//分行号01491999999
		String trl = bbipPublicService.getETeller(bk);
		context.setData(ParamKeys.TELLER, trl);
		context.setData("extFields", br);
		
		logger.info("=====context after set tlr :" + context);
		
		context.setData("br1", context.getData(ParamKeys.BR).toString().substring(2, 8));
		context.setData("txnTme1", DateUtils.format(new Date(), DateUtils.STYLE_FULL));
		context.setData("reMark1", "扣款失败"); //预置失败
		logger.info("PayUnilateralToBankServiceImplPGAS00@prepareCheckDeal end!");
		return null;
	}

	// 缴费前处理
	@Override
	public Map<String, Object> prePayToBank(CommHeadDomain commHeadDomain,
			PayFeeOnlineDomain payFeeOnlineDomain, Context context)
			throws CoreException {
		logger.info("Enter in PayUnilateralToBankServiceImplPGAS00@prePayToBank!....");
		logger.info("=============context=" + context);

		context.setData("AplCls", "207");
		context.setData("Mask", "GASH");

		// 预置返回第三方状态为失败B3 (使用备用字段2)
		context.setData(ParamKeys.BAK_FLD2, GDConstants.THD_STS_B3);
		// 预置账务状态status为0 (使用备用字段1)
		context.setData(ParamKeys.BAK_FLD1, "0");
		// 预置交易扣款失败
		context.setData(ParamKeys.BAK_FLD2, "B3");
		context.setData(ParamKeys.BAK_FLD5, "扣款失败");
		
		
		GdGasCusAll cusInfo = gdGasCusAllRepository.findOne((String)context.getData(ParamKeys.THD_CUS_NO));
		context.setData(ParamKeys.CUS_NME, cusInfo.getCusNme());
		context.setData(ParamKeys.THD_CUS_NME, cusInfo.getThdCusNme());
		logger.info("=================== the cusNme is :" + context.getData(ParamKeys.CUS_NME));
		logger.info("=================== the thdCusNme is :" + context.getData(ParamKeys.THD_CUS_NME));

		context.setData("TActNo", GDConstants.GAS_THD_ACT_NO);

		// <Set>VchChk=1</Set><!--监督标志由业务上确定-->
		context.setData(GDParamKeys.GAS_VCH_CHK, "1");
		context.setData(GDParamKeys.GAS_VCH_COD, GDConstants.GAS_VCH_COD);
		context.setData(GDParamKeys.GAS_MST_CHK, "1");
		context.setData(GDParamKeys.GAS_F_RSP_CD, "");
		// <Set>ItgTyp=0</Set>
		context.setData(ParamKeys.ITG_TYP, "0");
		// <Set>TxnTyp=N</Set>
		context.setData(ParamKeys.TXN_TYP, "N");
		context.setData(GDParamKeys.GAS_NOD_NO, GDConstants.GAS_NOD_NO);
		context.setData(GDParamKeys.CCY_TYP, "0");

		logger.info("=========PayUnilateralToBankServiceImplPGAS00@prePayToBank end with conetxt:"
				+ context);
		return null;
	}

	// 缴费后处理
	@Override
	public Map<String, Object> aftPayToBank(CommHeadDomain commHeadDomain,
			PayFeeOnlineDomain payFeeOnlineDomain, Context context)
			throws CoreException {

		logger.info("Enter in PayUnilateralToBankServiceImplPGAS00@aftPayToBank!....");
		logger.info("======context:" + context);

		logger.info("=================context.state:" + context.getData("state"));
			
		logger.info("=================context.responseCode:" + context.getData(ParamKeys.RESPONSE_CODE));
		logger.info("=================context.responseType:" + context.getData(ParamKeys.RESPONSE_TYPE));
		if ("000000".equals(context.getData(ParamKeys.RESPONSE_CODE)) || "S".equals(context.getData(ParamKeys.MFM_TXN_STS))) {
			context.setData(ParamKeys.BAK_FLD2, "B0");
			context.setData("TransCode", "B0");
			context.setData(ParamKeys.RSP_MSG, "扣款成功");
			context.setData(ParamKeys.BAK_FLD5, "扣款成功");
			context.setData("reMark1", "扣款成功 ");
			// 更新流水 BBIP有相应处理
			logger.info("=========交易成功了啊！！！！！！！！！！！！！！！context=" + context);
		} else {
			if ("GEMS0004TP0017".equals(context.getData(ParamKeys.RESPONSE_CODE))) { // B1为金额不足扣费失败
				context.setData(ParamKeys.BAK_FLD2, "B1");
				context.setData("TransCode", "B1");
				logger.info("=========交易失败了啊！！！余额不足啊！！！！！！！！！！！！context=" + context);
			} else if ("GEMS0004CB0042".equals(context.getData(ParamKeys.RESPONSE_CODE)) 
					&& !(GDConstants.GAS_TXN_BY_CASH.equals(context.getData(ParamKeys.CUS_AC)))) {
				// B2为无此帐号或账号与用户编号匹配错误扣费失败
				// "RESPONSE_MESSAGE" :
				// "客户账号不存在                                            ",
				context.setData(ParamKeys.BAK_FLD2, "B2");
				context.setData("TransCode", "B2");
				logger.info("=========交易失败了啊！！！！！客户账号不存在啊！context="
						+ context);
			} else { // B3其它原因扣费失败
				context.setData(ParamKeys.BAK_FLD2, "B3");
				context.setData("TransCode", "B3");
				logger.info("=========交易失败了啊！！！！！！！！context=" + context);
			}
		} 
		context.setData(ParamKeys.RSV_FLD5, "cnjt");
		
		return null;
	}
}
