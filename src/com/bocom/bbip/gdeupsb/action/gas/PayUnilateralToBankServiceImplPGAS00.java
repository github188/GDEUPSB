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
 * @author WangMQ
 *
 */
public class PayUnilateralToBankServiceImplPGAS00 implements PayUnilateralToBankService{
	private Logger logger = LoggerFactory.getLogger(PayUnilateralToBankServiceImplPGAS00.class);
	
	@Autowired
	BBIPPublicService bbipPublicService;
	@Autowired
	BGSPServiceAccessObject bgspServiceAccessObject;
	@Autowired
	EupsTransJournalRepository eupsTransJournalRepository;
	
	//交易前处理
	@Override
	public Map<String, Object> prePayToBank(CommHeadDomain commHeadDomain,
			PayFeeOnlineDomain payFeeOnlineDomain, Context context) throws CoreException {
		logger.info("Enter in PayUnilateralToBankServiceImplPGAS00@prePayToBank!....");
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
		logger.info("=============context=" + context);
		
		//交易日期，时间
		context.setData(ParamKeys.TXN_DATE, DateUtils.format(new Date(), DateUtils.STYLE_SIMPLE_DATE));
		context.setData(ParamKeys.TXN_TME, DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMddHHmmss));
		
		//交易费用，初始化为0
		BigDecimal txnAmt = new BigDecimal(0.0);
		BigDecimal txnAmt1 = new BigDecimal(0.0);
		context.setData(ParamKeys.TXN_AMT, txnAmt);
		context.setData(GDParamKeys.TXN_AMT1, txnAmt1);
		
		//预置返回第三方状态为失败 (使用备用字段2)
		context.setData(ParamKeys.BAK_FLD2, GDConstants.THD_STS_B3);
		//预置状物状态status为0 (使用备用字段1)
		context.setData(ParamKeys.BAK_FLD1, "0");
		
		context.setData(ParamKeys.RSP_MSG, "扣款失败");
		context.setData(GDParamKeys.ERR_MSG, "扣款失败");
		
		//取流水号
		context.setData(ParamKeys.SEQUENCE, bbipPublicService.getBBIPSequence());
		
		
		//查询用户信息（签约状态）select ActNam from Gascusall491 where UserNo='%s' and ActNo='%s'
		Map<String, Object> cusMap = new HashMap<String, Object>();
		cusMap.put(ParamKeys.CUS_NO, context.getData("cusNo"));
		cusMap.put(ParamKeys.CUS_AC, context.getData("cusAc"));
		Result accessObject = bgspServiceAccessObject.callServiceFlatting("queryDetailAgentCollectAgreement", cusMap);
		if(CollectionUtils.isEmpty(accessObject.getPayload())){		//accessObject为空，未签约
			context.setData(ParamKeys.MESSAGE_TYPE, "E");
			context.setData(ParamKeys.RSP_CDE, GDConstants.GAS_ERROR_CODE);
			context.setData(ParamKeys.RSP_MSG, "该用户未签约，交易失败");
			throw new CoreException("该用户未签约，交易失败");
		}
		
		//将交易数据入 流水表，预置为交易失败F
		EupsTransJournal eupsTxnJnl = BeanUtils.toObject(context.getDataMap(), EupsTransJournal.class);
		eupsTxnJnl.setTxnSts("F");
		eupsTransJournalRepository.insert(eupsTxnJnl);
		
//TODO		<Set>TActNo=491800012620190029499</Set>
		context.setData("TActNo", GDConstants.GAS_THD_TXN_NO);
		
//TODO	
//		<Exec func="PUB:ReadModuleCfg">
//        <Arg name="Application" value="GAS_DB"/><!--可以使用单位编号-->
//        <Arg name="Transaction" value="460710"/><!--可以用交易码或者模块名-->
//      </Exec>
		
		
		
//		 <Set>TxnAmt=ADDCHAR(MUL(100,$PayAmt),12,0,1)</Set>  <!-- payAmt*100,左补0共12位-->
//		payAmt为应缴费用 reqTxnAmt
		BigDecimal reqTxnAmt = new BigDecimal((String)context.getData("reqTxnAmt"));
		reqTxnAmt = reqTxnAmt.multiply(new BigDecimal(100));
		int len = 12;
		char des = '0';
		char LorR = '1';
		String txnAmt2 = GdExpCommonUtils.AddChar(String.valueOf(reqTxnAmt), len, des, LorR);
		
//	      <Set>TCusNm=$ActNam</Set>
//	      <Set>PayMod=0</Set>
//	      <Set>CnlTyp=L</Set><!--交易渠道类型：L第三方系统-->
//	      <Set>VchChk=1</Set><!--监督标志由业务上确定-->
//	      <Set>VchCod=00000000</Set>
//	      <Set>MstChk=1</Set>  
//	      <Set>FRspCd= </Set>  
//	      <Set>ItgTyp=0</Set>  
//	      <Set>TxnTyp=N</Set> 
//	      <Set>TlrId=ERQTDT1</Set>
//	      <Set>NodNo=491800</Set>
//	      <Set>CcyTyp=0</Set>
//	      <Set>TTxnCd=460710</Set>
		
		
		
		
		
		return null;
	}

	@Override
	public Map<String, Object> prepareCheckDeal(CommHeadDomain commHeadDomain,
			PayFeeOnlineDomain payFeeOnlineDomain, Context context) throws CoreException {

		
		
		
		return null;
	}
	
	@Override
	public Map<String, Object> aftPayToBank(CommHeadDomain commHeadDomain,
			PayFeeOnlineDomain payFeeOnlineDomain, Context context) throws CoreException {
		
		
		
		return null;
	}

}
