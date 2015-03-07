package com.bocom.bbip.gdeupsb.strategy.gash;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.taglibs.standard.tag.el.sql.SetDataSourceTag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.spi.service.single.PayUnilateralToBankService;
import com.bocom.bbip.eups.spi.vo.CommHeadDomain;
import com.bocom.bbip.eups.spi.vo.PayFeeOnlineDomain;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GdGasCusAll;
import com.bocom.bbip.gdeupsb.repository.GdGasCusAllRepository;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.bbip.utils.StringUtils;
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
	GdGasCusAllRepository gdGasCusAllRepository;

	// 交易前服务处理
	@Override
	public Map<String, Object> prepareCheckDeal(CommHeadDomain commHeadDomain,
			PayFeeOnlineDomain payFeeOnlineDomain, Context context)
			throws CoreException {
		logger.info("PayUnilateralToBankServiceImplPGAS00@prepareCheckDeal start!");
		logger.info("======context:" + context);

		context.setData(ParamKeys.TELLER, "ABIR148");
		context.setData(ParamKeys.BR, "01441131999");
		context.setData(ParamKeys.BK, "01441999999");

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
//		context.setData("BusTyp", "GASH1");
		context.setData("Mask", "GASH");

		// 预置返回第三方状态为失败B3 (使用备用字段2)
		context.setData(ParamKeys.BAK_FLD2, GDConstants.THD_STS_B3);
		// 预置账务状态status为0 (使用备用字段1)
		context.setData(ParamKeys.BAK_FLD1, "0");
		// 预置交易扣款失败
//		context.setData(ParamKeys.RSP_MSG, "扣款失败");
		context.setData(ParamKeys.BAK_FLD5, "扣款失败");

		// 查询用户信息（签约状态）select ActNam from Gascusall491 where UserNo='%s' and
		// ActNo='%s' cusAc UserNo='%s' cusNo
		GdGasCusAll qryCusInf = new GdGasCusAll();
		qryCusInf.setCusAc(context.getData(ParamKeys.CUS_AC).toString());
		qryCusInf.setCusNo(context.getData(ParamKeys.THD_CUS_NO).toString());
		List<GdGasCusAll> cusInfLst = gdGasCusAllRepository.find(qryCusInf);

		if (CollectionUtils.isEmpty(cusInfLst)) { // cusInfLst为空，未签约
			context.setData(ParamKeys.BAK_FLD2, "B2");
			context.setData(ParamKeys.MESSAGE_TYPE, "E");
			context.setData(ParamKeys.RSP_CDE, GDConstants.GAS_ERROR_CODE);
			context.setData(ParamKeys.RSP_MSG, "该用户未签约，交易失败");
			// throw new CoreException("该用户未签约，交易失败");
		}

		// 将交易数据入 流水表，预置为交易失败F EUPS已实现相应逻辑但交易状态为U(预置)
		// EupsTransJournal eupsTxnJnl =
		// BeanUtils.toObject(context.getDataMap(),
		// EupsTransJournal.class);
		// eupsTxnJnl.setTxnSts("F");
		// eupsTransJournalRepository.insert(eupsTxnJnl);

		// <Set>TActNo=491800012620190029499</Set>
		context.setData("TActNo", GDConstants.GAS_THD_ACT_NO);

		// <Set>TxnAmt=ADDCHAR(MUL(100,$PayAmt),12,0,1)</Set> <!--
		// payAmt*100,左补0共12位-->
		// payAmt为应缴费用 reqTxnAmt
		// BigDecimal reqTxnAmt = new BigDecimal(
		// (String) context.getData("reqTxnAmt"));
		// reqTxnAmt = reqTxnAmt.multiply(new BigDecimal(100));
		// int len = 12;
		// char des = '0';
		// char LorR = '1';
		// String txnAmt2 = GdExpCommonUtils.AddChar(String.valueOf(reqTxnAmt),
		// len, des, LorR);
		// context.setData(ParamKeys.TXN_AMOUNT, txnAmt2);
		// <Set>TCusNm=$ActNam</Set>
		context.setData(ParamKeys.THD_CUS_NME, context.getData("cusNme"));
		// <Set>CnlTyp=L</Set><!--交易渠道类型：L第三方系统-->
		// context.setData(ParamKeys.CHL_TYP, "L");

//		 <Set>PayMod=0</Set>
		// context.setData(ParamKeys.PAY_MODE, "0");//payMod=0,现金，会将cusAc转换为00000000 
		
		// <Set>VchChk=1</Set><!--监督标志由业务上确定-->
		context.setData(GDParamKeys.GAS_VCH_CHK, "1");
		// <Set>VchCod=00000000</Set>
		context.setData(GDParamKeys.GAS_VCH_COD, GDConstants.GAS_VCH_COD);
		// <Set>MstChk=1</Set>
		context.setData(GDParamKeys.GAS_MST_CHK, "1");
		// <Set>FRspCd= </Set>
		context.setData(GDParamKeys.GAS_F_RSP_CD, "");
		// <Set>ItgTyp=0</Set>
		context.setData(ParamKeys.ITG_TYP, "0");
		// <Set>TxnTyp=N</Set>
		context.setData(ParamKeys.TXN_TYP, "N");
		// <Set>TlrId=ERQTDT1</Set>
		context.setData(ParamKeys.TELLER_ID, "ERQTDT1");
		// <Set>NodNo=491800</Set>
		context.setData(GDParamKeys.GAS_NOD_NO, GDConstants.GAS_NOD_NO);
		// <Set>CcyTyp=0</Set>
		context.setData(GDParamKeys.CCY_TYP, "0");

		// <Set>TTxnCd=460710</Set>
		// context.setData(ParamKeys.THD_TXN_CDE, "460710");
		logger.info("=========PayUnilateralToBankServiceImplPGAS00@prePayToBank end with conetxt:" + context);
		return null;
	}

	// 缴费后处理
	@Override
	public Map<String, Object> aftPayToBank(CommHeadDomain commHeadDomain,
			PayFeeOnlineDomain payFeeOnlineDomain, Context context)
			throws CoreException {

		logger.info("Enter in PayUnilateralToBankServiceImplPGAS00@aftPayToBank!....");
		logger.info("======context:" + context);

		context.setData(GDParamKeys.TXN_TME1, DateUtils.format(
				(Date) context.getData(ParamKeys.TXN_TME),
				"yyyy-MM-dd HH:mm:ss"));
		// context.setData(ParamKeys.BK,
		// context.getData(ParamKeys.BK).toString().substring(2, 8));
		context.setData(GDParamKeys.BR1, context.getData(ParamKeys.BR).toString()
				.substring(2));
		
		
		

		// B0为扣费成功 B1为金额不足扣费失败 B2为无此帐号或账号与用户编号匹配错误扣费失败 B3其它原因扣费失败
		// TODO 区分交易结果
//		&& "S".equals(context.getData(ParamKeys.MFM_TXN_STS))  && "S".equals(context.getData(ParamKeys.THD_TXN_STS))
		if (GDConstants.GAS_MFM_RSP_CD.equals(context.getData(ParamKeys.MFM_RSP_CDE))
				&& "S".equals(context.getData(ParamKeys.TXN_STS))) { // 扣款成功
																			// :MFM_RSP_CDE=SC0000
																			// TXN_STS=S

			// context.setData(ParamKeys.TXN_AMOUNT,
			// context.getData("reqTxnAmt"));
			//
			// BigDecimal reqTxnAmt = new BigDecimal(
			// (String) context.getData("reqTxnAmt"));
			// reqTxnAmt = reqTxnAmt.multiply(new BigDecimal(100));
			// int len = 12;
			// char des = '0';
			// char LorR = '1';
			// String optAmt1 = GdExpCommonUtils.AddChar(
			// String.valueOf(reqTxnAmt), len, des, LorR);
			// context.setData(ParamKeys.BAK_FLD6, optAmt1); // 使用备用字段6

			context.setData(ParamKeys.BAK_FLD2, "B0");
			// context.setData(ParamKeys.MFM_TXN_STS, "S");
			// context.setData(ParamKeys.MFM_RSP_CDE,
			// GDConstants.GAS_MFM_RSP_CD);
			context.setData(ParamKeys.RSP_MSG, "扣款成功");
			context.setData(ParamKeys.BAK_FLD5, "扣款成功");

			// 更新流水 BBIP有相应处理
			logger.info("=========交易成功了啊！！！！！！！！！！！！！！！context=" + context);
		} else if (StringUtils.isNotEmpty(context.getData(ParamKeys.RESPONSE_MESSAGE).toString()) 
				&& "余额不足".contains(context.getData(ParamKeys.RESPONSE_MESSAGE).toString())) { // B1为金额不足扣费失败
			context.setData(ParamKeys.BAK_FLD2, "B1");
			logger.info("=========交易失败了啊！！！余额不足啊！！！！！！！！！！！！context=" + context);
		} else if (StringUtils.isNotEmpty(context.getData(ParamKeys.RESPONSE_MESSAGE).toString()) 
				&& "客户账号不存在".equals(context.getData(ParamKeys.RESPONSE_MESSAGE).toString().trim())
				&& !(GDConstants.GAS_TXN_BY_CASH.equals(context.getData(ParamKeys.CUS_AC)))) {
			// B2为无此帐号或账号与用户编号匹配错误扣费失败
			// "RESPONSE_MESSAGE" :
			// "客户账号不存在                                            ",
			// "客户账号不存在                                            ":payMod为0时（即现金交易，也会返回此消息，需要区分开这情况,检测cusAc不为00000000且RESPONSE_MESSAGE为"客户账号不存在                                            "）
			context.setData(ParamKeys.BAK_FLD2, "B2");
			logger.info("=========交易失败了啊！！！！！客户账号不存在啊！！！！！快去签约！！！！！context="
					+ context);
		} else { // B3其它原因扣费失败
			context.setData(ParamKeys.BAK_FLD2, "B3");
			logger.info("=========交易失败了啊！！！！！！！！坑坑坑！！context=" + context);
		}
		
		context.setData("reMark1", context.getData(ParamKeys.RSP_MSG).toString());
		
		if(context.getData(ParamKeys.RSP_MSG).toString().length() >= 60){
			context.setData("reMark1", context.getData(ParamKeys.RSP_MSG).toString().substring(0, 60));
		}else{
			context.setData("reMark1", context.getData(ParamKeys.RSP_MSG).toString());
		}
		
		return null;
	}
}
