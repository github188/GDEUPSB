package com.bocom.bbip.gdeupsb.strategy.elcgd;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.comp.account.AccountService;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsTransJournal;
import com.bocom.bbip.eups.repository.EupsTransJournalRepository;
import com.bocom.bbip.utils.CollectionUtils;
import com.bocom.bbip.utils.NumberUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
import com.bocom.jump.bp.core.Executable;

/**
 * 银行方单边抹帐(供电局发起的划扣抹帐)
 * 
 * @author qc.w
 * 
 */
public class PreCnlBnkSglDealStrategyAction implements Executable {

	private final static Logger log = LoggerFactory.getLogger(PreCnlBnkSglDealStrategyAction.class);

	@Autowired
	EupsTransJournalRepository eupsTransJournalRepository;

	@Override
	public void execute(Context context) throws CoreException, CoreRuntimeException {
		log.info("PreCnlBnkSglDealStrategyAction start!..");
		String ttxnAmt = context.getData("thdTxnAmt");
		context.setData(ParamKeys.TXN_AMOUNT, NumberUtils.centToYuan(ttxnAmt)); // 将金额转化为bigdecimal

		String remarkData = context.getData("remarkData"); // 48域附加数据
		String thdCusNo = remarkData.substring(0, 21); // 客户编号
		String eleMonth = remarkData.substring(21, 27); // 电费月份
		String proCde = remarkData.substring(27, 29); // 产品代码
		String oldAcSqn = remarkData.substring(29, 41); // 原系统参考号-对应缴费时送给第三方的会计流水号

		context.setData(ParamKeys.THD_CUS_NO, thdCusNo); // 第三方客户标志
		context.setData("eleMonth", eleMonth); // 电费月份
		// context.setData(ParamKeys.OLD_TXN_SEQUENCE, oldAcSqn); //原会计流水号

		// 根据会计流水号查询原交易流水号
		EupsTransJournal eupsTransJournal = new EupsTransJournal();
		eupsTransJournal.setMfmVchNo(oldAcSqn);
		eupsTransJournal.setThdCusNo(thdCusNo);
		eupsTransJournal.setTxnSts(Constants.TRADE_STATUS_SUCCESS); // 交易成功
		eupsTransJournal.setTxnTyp(Constants.EUPS_TXN_TYP_NOMAL);

		List<EupsTransJournal> list = eupsTransJournalRepository.find(eupsTransJournal);
		if (CollectionUtils.isEmpty(list)) {
			throw new CoreException(ErrorCodes.BIZ_ERROR_CANCEL_PAYMENT_ORIGINAL_PAYMENT_JOURNAL_NOT_FOUND);
		}
		else {
			eupsTransJournal = list.get(0);
		}
		String oldSqn = eupsTransJournal.getSqn();
		context.setData(ParamKeys.OLD_TXN_SEQUENCE, oldSqn); // 原交易流水号

	}

}
