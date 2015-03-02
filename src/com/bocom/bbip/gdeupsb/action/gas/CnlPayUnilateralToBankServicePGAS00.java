package com.bocom.bbip.gdeupsb.action.gas;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsTransJournal;
import com.bocom.bbip.eups.repository.EupsTransJournalRepository;
import com.bocom.bbip.eups.spi.service.online.AutomaticCancelService;
import com.bocom.bbip.eups.spi.service.single.PayUnilateralToBankService;
import com.bocom.bbip.eups.spi.vo.CancelDomain;
import com.bocom.bbip.eups.spi.vo.CommHeadDomain;
import com.bocom.bbip.eups.spi.vo.PayFeeOnlineDomain;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

/**
 * 惠州燃气 单笔冲正
 * 
 * @author WangMQ
 * 
 */
public class CnlPayUnilateralToBankServicePGAS00 implements
		PayUnilateralToBankService {

	@Autowired
	EupsTransJournalRepository eupsTransJournalRepository;
	
	private static final Log logger = LogFactory
			.getLog(PayUnilateralToBankService.class);

	/**
	 * public void execute(Context context) throws CoreException,
	 * CoreRuntimeException {
	 * logger.info("============Enter in PreReversePaymentThdAction....");
	 * context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
	 * 
	 * // TODO 应用类别码 AplCls 不知道具体有何用 context.setData(GDParamKeys.GAS_APL_CLS,
	 * "207"); context.setData(ParamKeys.BUS_TYP, GDParamKeys.EUPS_BUS_TYP_GAS);
	 * context.setData(GDParamKeys.GAS_RESULT, "NoPay");// 默认冲正未成功 BigDecimal
	 * txnAmt1 = new BigDecimal(0.0); context.setData(ParamKeys.BAK_FLD4,
	 * String.valueOf(txnAmt1));
	 * 
	 * // 无需校验单位协议
	 * 
	 * // TODO <Set>TActNo=491800012620190029499</Set> 燃气账号
	 * context.setData("TActNo", "491800012620190029499");
	 * 
	 * // 根据燃气托收流水、账务状态（正常扣款）、已扣费用、返回第三方状态（B0扣款成功）、主机返回码（SC0000）、主机交易状态（S）、第三方交易码查询流水表 
	 * // CTD会传： 交易码thdTxnCde、托收流水thdSqn、银行标识BK、冲正金额txnAmt 
	 * // select * from Gastxnjnl491 where CLogNo='%s' and Status='1' and // OptAmt='%s'
	 * and ThdSts='B0' and HRspCd='SC0000' and HTxnSt='S' and //
	 * TTxnCd='SMPCPAY'
	 * 
	 * String bakFld1 = "1"; // satus = "1" String bakFld2 = "B0"; //
	 * ThdSts='B0' String mfmRspCde = "SC0000"; // HRspCd='SC0000' String
	 * mfmTxnSts = "S"; // HTxnSt='S'
	 * 
	 * EupsTransJournal etj = new EupsTransJournal();
	 * etj.setThdSqn(context.getData("thdSqn").toString()); etj.setTxnAmt(new
	 * BigDecimal(context.getData("txnAmt").toString()));
	 * etj.setBakFld1(bakFld1); etj.setBakFld2(bakFld2);
	 * etj.setMfmRspCde(mfmRspCde); etj.setMfmTxnSts(mfmTxnSts);
	 * etj.setThdTxnCde(context.getData("thdTxnCde").toString());
	 * 
	 * List<EupsTransJournal> etjList = get(EupsTransJournalRepository.class)
	 * .find(etj); if (CollectionUtils.isEmpty(etjList) && etjList == null) {
	 * context.setData(ParamKeys.MESSAGE_TYPE, "E");
	 * context.setData(ParamKeys.RSP_CDE, GDConstants.GAS_ERROR_CODE);
	 * context.setData(ParamKeys.RSP_MSG, "无该笔流水信息"); throw new
	 * CoreRuntimeException("无该笔流水信息"); }
	 * context.setData(ParamKeys.CONSOLE_LCLJNL_LIST, etjList);
	 * 
	 * // TODO 校验各分行账号
	 * 
	 * // TODO ReadModuleCfg // <Exec func="PUB:ReadModuleCfg"> // <Arg
	 * name="Application" value="GAS_DB"/> // <Arg name="Transaction"
	 * value="460711"/> // </Exec>
	 * 
	 * // TODO 判断对公对私
	 * 
	 * // <Set>TTxnCd=460711</Set> // <Set>TIATyp=C</Set> //
	 * <Set>TlrId=ERQTDT1</Set>
	 * 
	 * // TODO 启动完整性控制 // <Exec func="PUB:BeginWork"/> <!--启动完整性控制-->
	 * 
	 * // TODO 上送主机进行冲正 调银行冲正 // <Exec func="PUB:CallHostAcc"
	 * error="IGNORE"><!--上送主机进行冲正交易--> // <Arg name="HTxnCd" value="959999"/>
	 * // <Arg name="ObjSvr" value="SHSTPUB1"/> // </Exec> //
	 * context.setData("HTxnCd", "959999"); context.setData("ObjSvr",
	 * "SHSTPUB1");
	 * 
	 * logger.info("燃气冲正开始, context:" + context);
	 * get(BBIPPublicService.class).synExecute("eups.cancelUnilateralToBank",
	 * context);
	 * 
	 * // 上主机冲正成功则更新流水表,更新失败同为冲正失败
	 * 
	 * // Test Test Test Test Test Test Test // if("0".equals("0")){//模拟冲正成功
	 * 
	 * if (context.getState().equals(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL))
	 * { context.setData(ParamKeys.MFM_TXN_STS, "C");
	 * context.setData(ParamKeys.TXN_STS, "C");
	 * context.setData(GDParamKeys.GAS_RESULT, "Upay");
	 * context.setData(ParamKeys.BAK_FLD4,
	 * context.getData(ParamKeys.TXN_AMOUNT));
	 * context.setData(ParamKeys.BAK_FLD1, "3");
	 * 
	 * etj = BeanUtils.toObject(context.getDataMap(), EupsTransJournal.class);
	 * get(EupsTransJournalRepository.class).update(etj); } else {
	 * context.setData(ParamKeys.MESSAGE_TYPE, "E");
	 * context.setData(ParamKeys.RSP_CDE,
	 * context.getData(ParamKeys.MFM_RSP_CDE));
	 * context.setData(ParamKeys.RSP_MSG, "冲正不成功"); }
	 * 
	 * // context.setData(ParamKeys.MFM_TXN_STS, GDConstants.GAS_TXN_STS_C); //
	 * context.setData(ParamKeys.THD_TXN_STS, GDConstants.GAS_TXN_STS_C); //
	 * context.setData(GDParamKeys.GAS_RESULT, "UPay"); //
	 * context.setData(ParamKeys.TXN_AMOUNT, arg1); //
	 * //<Set>TxnAmt1=$TxnAmt</Set> // context.setData(ParamKeys.TXN_STS, "3");
	 * //<Set>Status=3</Set> // 此处应为交易状态TXN_STS，而不是 BAK_FLD5 // // etj =
	 * BeanUtils.toObject(context.getDataMap(), // EupsTransJournal.class); //
	 * try{ // get(EupsTransJournalRepository.class).update(etj); //
	 * }catch(Exception e){ // context.setData(ParamKeys.MESSAGE_TYPE, "E"); //
	 * context.setData(ParamKeys.RSP_CDE, GDConstants.GAS_ERROR_CODE); //
	 * context.setData(ParamKeys.RSP_MSG, "数据库处理失败，冲正不成功!"); // } //
	 * 
	 * // // context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
	 * 
	 * }
	 */

	// 交易前特色处理
	@Override
	public Map<String, Object> prePayToBank(CommHeadDomain commHeadDomain,
			PayFeeOnlineDomain payFeeOnlineDomain, Context context)
			throws CoreException {
		logger.info("CnlPayUnilateralToBankServicePGAS00@prePayToBank started....");
		logger.info("[now context]=" + context);

		context.setData(GDParamKeys.GAS_APL_CLS, "207");
		context.setData(ParamKeys.BUS_TYP, GDParamKeys.EUPS_BUS_TYP_GAS);
		context.setData(GDParamKeys.GAS_RESULT, "NoPay");// 默认冲正未成功
		BigDecimal txnAmt1 = new BigDecimal(0.0);
		context.setData(ParamKeys.BAK_FLD4, String.valueOf(txnAmt1));

		// TODO <Set>TActNo=491800012620190029499</Set> 燃气账号?
		context.setData("TActNo", "491800012620190029499");

		context.setData(ParamKeys.BAK_FLD1, "1");// satus = "1"
		context.setData(ParamKeys.BAK_FLD2, "B0");// ThdSts='B0'
		context.setData(ParamKeys.MFM_RSP_CDE, GDConstants.GAS_MFM_RSP_CD);// HRspCd='SC0000'
		context.setData(ParamKeys.MFM_TXN_STS, "S");// HTxnSt='S'

		logger.info("CnlPayUnilateralToBankServicePGAS00@prePayToBank end....");
		return null;
	}

	// 记账前特色处理
	@Override
	public Map<String, Object> prepareCheckDeal(CommHeadDomain commHeadDomain,
			PayFeeOnlineDomain payFeeOnlineDomain, Context context)
			throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}

	// 记账后特色处理
	// 第三方要求返回冲正结果【Upay或NoPay Upay为冲账成功 NoPay 为冲账失败】、燃气托收流水、冲正金额
	@Override
	public Map<String, Object> aftPayToBank(CommHeadDomain commHeadDomain,
			PayFeeOnlineDomain payFeeOnlineDomain, Context context)
			throws CoreException {

		if (context.getState().equals(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL)) {
			context.setData(ParamKeys.MFM_TXN_STS, "C");
			context.setData(ParamKeys.TXN_STS, "C");
			context.setData(GDParamKeys.GAS_RESULT, "Upay");
			context.setData(ParamKeys.BAK_FLD4,
					context.getData(ParamKeys.TXN_AMOUNT));
			context.setData(ParamKeys.BAK_FLD1, "3");
			EupsTransJournal etj = context.getData("lclJnlList");
			eupsTransJournalRepository.update(etj);
		} else {
			context.setData(ParamKeys.MESSAGE_TYPE, "E");
			context.setData(ParamKeys.RSP_CDE,
					context.getData(ParamKeys.MFM_RSP_CDE));
			context.setData(ParamKeys.RSP_MSG, "冲正不成功");
		}

		return null;
	}
}
