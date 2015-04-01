package com.bocom.bbip.gdeupsb.strategy.efek.cancelFeeOnline;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsTransJournal;
import com.bocom.bbip.eups.repository.EupsTransJournalRepository;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
import com.bocom.jump.bp.core.Executable;

public class AftCancelFeeAction implements Executable{
	private final static Log logger=LogFactory.getLog(AftCancelFeeAction.class);
	@Autowired
	EupsTransJournalRepository eupsTransJournalRepository;
	/**
	 * 抹账后处理
	 */
	@Override
	public void execute(Context context)throws CoreException,CoreRuntimeException{
		logger.info("==========Start  AftCancelFeeAction");
				if(context.getData(ParamKeys.RESPONSE_CODE).toString().equals("000000")){
						Date thdTxnDte=DateUtils.parse(context.getData(ParamKeys.THD_TXN_DATE).toString());
						Date thdTxnTme=DateUtils.parse(context.getData(ParamKeys.THD_TXN_DATE).toString()+context.getData(ParamKeys.THD_TXN_TIME).toString(),DateUtils.STYLE_yyyyMMddHHmmss);
						context.setData(ParamKeys.THD_TXN_DATE, thdTxnDte);
						context.setData(ParamKeys.THD_TXN_TIME, thdTxnTme);
						context.setData(ParamKeys.TXN_DTE, DateUtils.formatAsSimpleDate((Date)context.getData(ParamKeys.TXN_DTE)));
						EupsTransJournal eupsTransJournal=eupsTransJournalRepository.findOne(context.getData(ParamKeys.OLD_TXN_SQN).toString());
						eupsTransJournalRepository.update(eupsTransJournal);
						context.setDataMap(BeanUtils.toMap(eupsTransJournal));
				}
				context.setData(ParamKeys.TXN_DTE, DateUtils.format((Date)context.getData(ParamKeys.TXN_DTE),DateUtils.STYLE_yyyyMMdd));
				context.setData(ParamKeys.TXN_TME, DateUtils.formatAsHHmmss((Date)context.getData(ParamKeys.TXN_TME)));
	}
}
