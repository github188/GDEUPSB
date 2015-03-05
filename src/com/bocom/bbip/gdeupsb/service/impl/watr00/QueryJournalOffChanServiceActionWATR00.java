package com.bocom.bbip.gdeupsb.service.impl.watr00;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsTransJournal;
import com.bocom.bbip.eups.repository.EupsTransJournalRepository;
import com.bocom.bbip.thd.org.apache.commons.collections.CollectionUtils;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
/**
 * 根据会计流水号查询流水信息
 * @author hefengwen
 *
 */
public class QueryJournalOffChanServiceActionWATR00 extends BaseAction {
	private static Logger logger = LoggerFactory.getLogger(QueryJournalOffChanServiceActionWATR00.class);
	
	@Override
	public void execute(Context context) throws CoreException,	CoreRuntimeException {
		logger.info("QueryJournalOffChanServiceActionWATR00 start ... ...");
		String mfmVchNo = context.getData("mfmVchNo");
		logger.info("mfmVchNo["+mfmVchNo+"]");
		EupsTransJournal eupsTransJournal = new EupsTransJournal();
		eupsTransJournal.setMfmVchNo(mfmVchNo);
		eupsTransJournal.setTxnDte(new Date());
		eupsTransJournal.setEupsBusTyp((String)context.getData(ParamKeys.EUPS_BUSS_TYPE));
		List<EupsTransJournal> eupsTransJournals = get(EupsTransJournalRepository.class).find(eupsTransJournal);
		if(CollectionUtils.isEmpty(eupsTransJournals)){
			//不存在流水信息
			logger.error("流水信息不存在");
			throw new CoreException(ErrorCodes.EUPS_SQN_IS_EMPTY);
		}
		eupsTransJournal = eupsTransJournals.get(0);
		String oldTxnSqn = eupsTransJournal.getSqn();
		String thdCusNo = eupsTransJournal.getThdCusNo();
		String thdCusNme = eupsTransJournal.getThdCusNme();
		String txnSts = eupsTransJournal.getTxnSts();
		String txnTlr = eupsTransJournal.getTxnTlr();
		BigDecimal txnAmt = eupsTransJournal.getTxnAmt();
		String txnTme = DateUtils.format(eupsTransJournal.getTxnTme(),DateUtils.STYLE_TRANS_TIME);
		String comNo = eupsTransJournal.getComNo();
		String cusAc = eupsTransJournal.getCusAc();
		logger.info("oldTxnSqn["+oldTxnSqn+"]thdCusNo["+thdCusNo+"]thdCusNme["+thdCusNme+"]txnSts["+txnSts+"]txnTlr["+txnTlr+"]" +
				"txnAmt["+txnAmt+"]txnTme["+txnTme+"]comNo["+comNo+"]cusAc["+cusAc+"]");
		context.setDataMap(BeanUtils.toMap(eupsTransJournal));
		context.setData("oldTxnSqn", oldTxnSqn);
		logger.info("QueryJournalOffChanServiceActionWATR00 end ... ...");
	}

}
