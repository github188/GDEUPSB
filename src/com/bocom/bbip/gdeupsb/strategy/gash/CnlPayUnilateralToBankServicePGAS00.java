package com.bocom.bbip.gdeupsb.strategy.gash;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsTransJournal;
import com.bocom.bbip.eups.repository.EupsTransJournalRepository;
import com.bocom.bbip.eups.spi.service.single.CancelUnilateralToBankService;
import com.bocom.bbip.eups.spi.vo.CancelDomain;
import com.bocom.bbip.eups.spi.vo.CommHeadDomain;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.utils.CollectionUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

/**
 * 惠州燃气 单笔冲正
 * 
 * @author WangMQ
 * 
 */
public class CnlPayUnilateralToBankServicePGAS00 implements
		CancelUnilateralToBankService {

	@Autowired
	EupsTransJournalRepository eupsTransJournalRepository;

	private static final Log logger = LogFactory
			.getLog(CnlPayUnilateralToBankServicePGAS00.class);

	@Override
	public Map<String, Object> aftCclToBank(CommHeadDomain commheaddomain,
			CancelDomain canceldomain, Context context) throws CoreException {
		logger.info("CnlPayUnilateralToBankServicePGAS00@aftPayToBank started....");
//		update Gastxnjnl491 set 
//		Status='3',OptAmt='0',OptAmt1='0',TTxnCd='UPay',HTxnSt='C',ErrMsg='第三方冲正' 
//		where CLogNo='%s' and  Status='1' and OptAmt='%s' and ThdSts='B0' 
//			and HRspCd='SC0000' and HTxnSt='S' and TTxnCd='SMPCPAY'
		
		//TODO 区分冲正结果，确定返回燃气的result字段
		if (context.getState().equals(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL)) {  //冲正成功
//			context.setData(ParamKeys.MFM_TXN_STS, "C");
//			context.setData(ParamKeys.TXN_STS, "C");
			context.setData("TransCode", "Upay");
			context.setData(ParamKeys.BAK_FLD5, "冲正成功");
//			context.setData(ParamKeys.BAK_FLD4,
//					context.getData(ParamKeys.TXN_AMOUNT));
//			context.setData(ParamKeys.BAK_FLD1, "3");
			// EupsTransJournal etj = context.getData("lclJnlList");
			// eupsTransJournalRepository.update(etj);
		} else {
			context.setData("TransCode", "NoPay");
			context.setData(ParamKeys.BAK_FLD5, "冲正失败");
//			context.setData(ParamKeys.MESSAGE_TYPE, "E");
//			context.setData(ParamKeys.RSP_CDE,
//					context.getData(ParamKeys.MFM_RSP_CDE));
//			context.setData(ParamKeys.RSP_MSG, "冲正不成功");
		}

		logger.info("CnlPayUnilateralToBankServicePGAS00@aftPayToBank end....");
		return null;
	}

	@Override
	public Map<String, Object> preCclToBank(CommHeadDomain commheaddomain,
			CancelDomain canceldomain, Context context) throws CoreException {
		logger.info("CnlPayUnilateralToBankServicePGAS00@preCclToBank start!");
		logger.info("======context:" + context);

		context.setData(GDParamKeys.GAS_APL_CLS, "207");
		context.setData(ParamKeys.BUS_TYP, GDParamKeys.EUPS_BUS_TYP_GAS);
		context.setData(GDParamKeys.GAS_RESULT, "NoPay");// 默认冲正未成功
		BigDecimal txnAmt1 = new BigDecimal(0.0);
		context.setData(ParamKeys.BAK_FLD4, String.valueOf(txnAmt1));

		// TODO <Set>TActNo=491800012620190029499</Set> 燃气账号?
		context.setData("TActNo", "491800012620190029499");

//		context.setData(ParamKeys.BAK_FLD1, "1");// satus = "1"
//		context.setData(ParamKeys.BAK_FLD2, "B0");// ThdSts='B0'
//		context.setData(ParamKeys.MFM_RSP_CDE, GDConstants.GAS_MFM_RSP_CD);// HRspCd='SC0000'
//		context.setData(ParamKeys.MFM_TXN_STS, "S");// HTxnSt='S'
		
		
		/*
		 * 根据第三方发送过来的燃气托收流水（冲正流水）查找eups流水表，得原交易流水号并将其设置为旧流水号
		 */
		EupsTransJournal qryJnl = new EupsTransJournal();
		qryJnl.setThdSqn(context.getData(ParamKeys.THD_SQN).toString());
		List<EupsTransJournal> upPayJnlList = eupsTransJournalRepository.find(qryJnl);
		if(CollectionUtils.isEmpty(upPayJnlList)){
			context.setData("TransCode", "NoPay");
			throw new CoreException(ErrorCodes.EUPS_QUERY_NO_DATA);
		}
		context.setData(ParamKeys.OLD_TXN_SQN, upPayJnlList.get(0).getSqn());
		context.setData(ParamKeys.BUS_TYP, upPayJnlList.get(0).getRapTyp());
		logger.info("===============upPayJnlList.size()=" + upPayJnlList.size());
		logger.info("===============sqn=" + upPayJnlList.get(0).getSqn());
		logger.info("===============context=" + context);
		logger.info("CnlPayUnilateralToBankServicePGAS00@preCclToBank end!");
		
		return null;
	}
}
