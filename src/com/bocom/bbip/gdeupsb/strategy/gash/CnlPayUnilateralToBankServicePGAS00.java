package com.bocom.bbip.gdeupsb.strategy.gash;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsTransJournal;
import com.bocom.bbip.eups.repository.EupsTransJournalRepository;
import com.bocom.bbip.eups.spi.service.single.CancelUnilateralToBankService;
import com.bocom.bbip.eups.spi.vo.CancelDomain;
import com.bocom.bbip.eups.spi.vo.CommHeadDomain;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.repository.GdGasCusAllRepository;
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
	BBIPPublicService bbipPublicService;

	@Autowired
	EupsTransJournalRepository eupsTransJournalRepository;

	@Autowired
	GdGasCusAllRepository gdGasCusAllRepository;

	private static final Log logger = LogFactory
			.getLog(CnlPayUnilateralToBankServicePGAS00.class);

	@Override
	public Map<String, Object> aftCclToBank(CommHeadDomain commheaddomain,
			CancelDomain canceldomain, Context context) throws CoreException {
		logger.info("CnlPayUnilateralToBankServicePGAS00@aftPayToBank started....");

		// TODO 区分冲正结果，确定返回燃气的result字段
		if (context.getState().equals(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL)) { // 冲正成功
			context.setData("TransCode", "UPay");
			context.setData(ParamKeys.BAK_FLD5, "冲正成功");
		} else {
			context.setData("TransCode", "NoPay");
			context.setData(ParamKeys.BAK_FLD5, "冲正失败");
		}

		logger.info("CnlPayUnilateralToBankServicePGAS00@aftPayToBank end....");
		return null;
	}

	@Override
	public Map<String, Object> preCclToBank(CommHeadDomain commheaddomain,
			CancelDomain canceldomain, Context context) throws CoreException {
		logger.info("CnlPayUnilateralToBankServicePGAS00@preCclToBank start!");
		logger.info("======context:" + context);
		context.setData("TransCode", "NoPay");//预置冲正失败
		
		String bk = "01491999999";
		String br = "01491800999";
		context.setData(ParamKeys.BR, br);
		context.setData(ParamKeys.BK, bk);// 分行号01491999999
		String trl = bbipPublicService.getETeller(bk);
		context.setData(ParamKeys.TELLER, trl);
		context.setData("extFields", br);
		
		context.setData(GDParamKeys.GAS_APL_CLS, "207");
		context.setData(ParamKeys.BUS_TYP, GDParamKeys.EUPS_BUS_TYP_GAS);
		context.setData(GDParamKeys.GAS_RESULT, "NoPay");// 默认冲正未成功
		BigDecimal txnAmt1 = new BigDecimal(0.0);
		context.setData(ParamKeys.BAK_FLD4, String.valueOf(txnAmt1));

		/*
		 * 根据第三方发送过来的燃气托收流水（冲正流水）查找eups流水表，得原交易流水号并将其设置为旧流水号
		 */
		EupsTransJournal qryJnl = new EupsTransJournal();
		qryJnl.setThdSqn(context.getData(ParamKeys.THD_SQN).toString());
		List<EupsTransJournal> upPayJnlList = eupsTransJournalRepository
				.find(qryJnl);
		if (CollectionUtils.isEmpty(upPayJnlList)) {
			context.setData("TransCode", "NoPay");
			throw new CoreException(ErrorCodes.EUPS_QUERY_NO_DATA);
		}

		// TODO 验证对应的sqn是否已抹帐

		// context.setData(ParamKeys.BUS_TYP, upPayJnlList.get(0).getRapTyp());
		// //TODO
		// logger.info("===============upPayJnlList.size()=" +
		// upPayJnlList.size());
		logger.info("===============sqn=" + upPayJnlList.get(0).getSqn());
		context.setData(ParamKeys.OLD_TXN_SQN, upPayJnlList.get(0).getSqn());
		logger.info("===============context=" + context);
		logger.info("CnlPayUnilateralToBankServicePGAS00@preCclToBank end!");

		return null;
	}
}
