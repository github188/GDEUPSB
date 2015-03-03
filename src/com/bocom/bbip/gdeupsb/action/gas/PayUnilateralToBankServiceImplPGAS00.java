package com.bocom.bbip.gdeupsb.action.gas;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsTransJournal;
import com.bocom.bbip.eups.repository.EupsTransJournalRepository;
import com.bocom.bbip.eups.spi.service.single.PayUnilateralToBankService;
import com.bocom.bbip.eups.spi.vo.CommHeadDomain;
import com.bocom.bbip.eups.spi.vo.PayFeeOnlineDomain;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.utils.GdExpCommonUtils;
import com.bocom.bbip.service.BGSPServiceAccessObject;
import com.bocom.bbip.service.Result;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.bbip.utils.NumberUtils;
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
	BBIPPublicService bbipPublicService;
	@Autowired
	BGSPServiceAccessObject bgspServiceAccessObject;
	@Autowired
	EupsTransJournalRepository eupsTransJournalRepository;

	// 交易前服务处理
	@Override
	public Map<String, Object> prepareCheckDeal(CommHeadDomain commHeadDomain,
			PayFeeOnlineDomain payFeeOnlineDomain, Context context)
			throws CoreException {
		logger.info("PayUnilateralToBankServiceImplPGAS00@prepareCheckDeal start!");
		logger.info("======context:" + context);
		logger.info("PayUnilateralToBankServiceImplPGAS00@prepareCheckDeal end!");
		return null;
	}

	// 缴费前处理
	@Override
	public Map<String, Object> prePayToBank(CommHeadDomain commHeadDomain,
			PayFeeOnlineDomain payFeeOnlineDomain, Context context)
			throws CoreException {
		logger.info("Enter in PayUnilateralToBankServiceImplPGAS00@prePayToBank!....");
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
		logger.info("=============context=" + context);

		// 交易日期，时间
		// context.setData(ParamKeys.TXN_DATE, DateUtils.format(new Date(),
		// DateUtils.STYLE_SIMPLE_DATE));
		// context.setData(ParamKeys.TXN_TME, DateUtils.format(new Date(),
		// DateUtils.STYLE_yyyyMMddHHmmss));

		// 交易费用，初始化为0
		// BigDecimal txnAmt = new BigDecimal(0.0);
		// BigDecimal optAmt1 = new BigDecimal(0.0);
		// context.setData(ParamKeys.TXN_AMT, txnAmt);
		// context.setData(ParamKeys.BAK_FLD6, optAmt1); //使用备用字段6

		// 预置返回第三方状态为失败B3 (使用备用字段2)
		context.setData(ParamKeys.BAK_FLD2, GDConstants.THD_STS_B3);
		// 预置账务状态status为0 (使用备用字段1)
		context.setData(ParamKeys.BAK_FLD1, "0");

		context.setData(ParamKeys.RSP_MSG, "扣款失败");
		context.setData(ParamKeys.BAK_FLD5, "扣款失败");


		// 查询用户信息（签约状态）select ActNam from Gascusall491 where UserNo='%s' and
		// ActNo='%s'

		// Map<String, Object> cusMap = new HashMap<String, Object>();
		// cusMap.put(ParamKeys.CUS_NO, context.getData("cusNo"));
		// cusMap.put(ParamKeys.CUS_AC, context.getData("cusAc"));
		//
		// Result accessObject = bgspServiceAccessObject.callServiceFlatting(
		// "queryDetailAgentCollectAgreement", cusMap);
		// if (CollectionUtils.isEmpty(accessObject.getPayload())) { //
		// accessObject为空，未签约
		// context.setData(ParamKeys.MESSAGE_TYPE, "E");
		// context.setData(ParamKeys.RSP_CDE, GDConstants.GAS_ERROR_CODE);
		// context.setData(ParamKeys.RSP_MSG, "该用户未签约，交易失败");
		// throw new CoreException("该用户未签约，交易失败");
		// }

		// 将交易数据入 流水表，预置为交易失败F
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
		context.setData(ParamKeys.CHL_TYP, "L");

		context.setData(ParamKeys.PAY_MODE, "0");
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
		context.setData(ParamKeys.CCY, "0");
		// <Set>TTxnCd=460710</Set>
		context.setData(ParamKeys.THD_TXN_CDE, "460710");

		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);

		return null;
	}

	// 缴费后处理
	@Override
	public Map<String, Object> aftPayToBank(CommHeadDomain commHeadDomain,
			PayFeeOnlineDomain payFeeOnlineDomain, Context context)
			throws CoreException {

		logger.info("Enter in PayUnilateralToBankServiceImplPGAS00@aftPayToBank!....");
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
		logger.info("======context:" + context);
//		B0为扣费成功 B1为金额不足扣费失败 B2为无此帐号或账号与用户编号匹配错误扣费失败 B3其它原因扣费失败
		if ("000000".equals(context.getData(ParamKeys.RSP_CDE))) { // 扣款成功
			context.setData(ParamKeys.TXN_AMOUNT, context.getData("reqTxnAmt"));

			BigDecimal reqTxnAmt = new BigDecimal(
					(String) context.getData("reqTxnAmt"));
			reqTxnAmt = reqTxnAmt.multiply(new BigDecimal(100));
			int len = 12;
			char des = '0';
			char LorR = '1';
			String optAmt1 = GdExpCommonUtils.AddChar(
					String.valueOf(reqTxnAmt), len, des, LorR);
			context.setData(ParamKeys.BAK_FLD6, optAmt1); // 使用备用字段6

			context.setData(ParamKeys.BAK_FLD2, "B0");
			context.setData(ParamKeys.MFM_TXN_STS, "S");
			context.setData(ParamKeys.MFM_RSP_CDE, GDConstants.GAS_MFM_RSP_CD);
			context.setData(ParamKeys.RSP_MSG, "扣款成功");
			context.setData(ParamKeys.BAK_FLD5, "扣款成功");
			
			

			// 更新流水
			// EupsTransJournal etj = context.getData(ParamKeys.CONSOLE_LCLJNL_LIST);
			// etj.setTxnTyp(context.getData(ParamKeys.TXN_TYP).toString()); //TxnTyp='%s'
			// etj.setCusNme(context.getData(ParamKeys.CUS_NME).toString()); //ActNam='%s'
			// etj.setTxnAmt(new BigDecimal(context.getData(ParamKeys.TXN_AMOUNT).toString())); // OptAmt='%s'
			// etj.setBakFld6(optAmt1); // OptAmt1='%s'
			// etj.setBakFld2(context.getData(ParamKeys.BAK_FLD2).toString());// ThdSts='%s'
			// etj.setBakFld1(context.getData(ParamKeys.BAK_FLD1).toString());// Status='%s'
			
			// HTxnCd='%s' 主机交易码
			
			// etj.setMfmRspCde(context.getData(ParamKeys.MFM_RSP_CDE).toString());// HRspCd='%s'
			// etj.setMfmTxnSts(context.getData(ParamKeys.MFM_TXN_STS).toString());// HTxnSt='%s'
			
			// HLogNo='%s' 主机流水号
			
			// etj.setMfmVchNo(context.getData(ParamKeys.MFM_VCH_NO).toString());// TckNo='%s' 会计流水号
			
			// etj.setBakFld5(context.getData(ParamKeys.BAK_FLD5).toString());// ErrMsg='%s'
			// etj.setSqn(context.getData(ParamKeys.SEQUENCE).toString()); // LogNo='%s'

			// eupsTransJournalRepository.update(etj);

		}
		
//TODO 区分交易结果		
//		else if(){	// B1为金额不足扣费失败
//			context.setData(ParamKeys.BAK_FLD2, "B1");
//		}
//		else if(){ //  B2为无此帐号或账号与用户编号匹配错误扣费失败
//			context.setData(ParamKeys.BAK_FLD2, "B2");
//		}
		
		else{	//	B3其它原因扣费失败
			context.setData(ParamKeys.BAK_FLD2, "B3");
		}

		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
		return null;
	}

}
