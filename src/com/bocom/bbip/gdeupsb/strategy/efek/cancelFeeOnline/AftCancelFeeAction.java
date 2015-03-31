package com.bocom.bbip.gdeupsb.strategy.efek.cancelFeeOnline;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsTransJournal;
import com.bocom.bbip.eups.repository.EupsTransJournalRepository;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
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
		
				Date thdTxnDte=DateUtils.parse(context.getData(ParamKeys.THD_TXN_DATE).toString());
				Date thdTxnTme=DateUtils.parse(context.getData(ParamKeys.THD_TXN_DATE).toString()+context.getData(ParamKeys.THD_TXN_TIME).toString(),DateUtils.STYLE_yyyyMMddHHmmss);
				context.setData(ParamKeys.THD_TXN_DATE, thdTxnDte);
				context.setData(ParamKeys.THD_TXN_TIME, thdTxnTme);
				context.setData(ParamKeys.TXN_DTE, DateUtils.formatAsSimpleDate((Date)context.getData(ParamKeys.TXN_DTE)));
		
				EupsTransJournal eupsTransJournal=eupsTransJournalRepository.findOne(context.getData(ParamKeys.OLD_TXN_SQN).toString());
				if(null != eupsTransJournal){
						String txnSts=eupsTransJournal.getTxnSts();
						if("c".equals(txnSts) || "C".equals(txnSts)){
								context.setData(GDParamKeys.MSGTYP, "N");
								context.setData(ParamKeys.RSP_CDE, GDConstants.SUCCESS_CODE);
								context.setData(ParamKeys.RSP_MSG, "原记录【"+context.getData(ParamKeys.OLD_TXN_SQN).toString()+"】已经抹账");
						}else if("b".equals(txnSts) || "B".equals(txnSts)){
						}else if("s".equals(txnSts) || "S".equals(txnSts)){
								context.setData(ParamKeys.RESPONSE_MESSAGE, "交易成功");
								context.setData(ParamKeys.RESPONSE_CODE, GDConstants.SUCCESS_CODE);
								context.setData(ParamKeys.RSP_MSG, "原记录【"+context.getData(ParamKeys.OLD_TXN_SQN).toString()+"】准备抹账");
						}else{
							context.setData(GDParamKeys.MSGTYP, "E");
							context.setData(ParamKeys.RSP_CDE, "EFE999");
							context.setData(ParamKeys.RSP_MSG, "原记录【"+context.getData(ParamKeys.OLD_TXN_SQN).toString()+"】状态【"+txnSts+"】未明，不进行抹账");
							throw new CoreRuntimeException("原记录【"+context.getData(ParamKeys.OLD_TXN_SQN).toString()+"】状态【"+txnSts+"】未明，不进行抹账");
						}
				}else{
						context.setData(GDParamKeys.MSGTYP, "N");
						context.setData(ParamKeys.RSP_CDE, GDConstants.SUCCESS_CODE);
						context.setData(ParamKeys.RSP_MSG, "原记录【"+context.getData(ParamKeys.OLD_TXN_SQN).toString()+"】不存在");
						throw new CoreRuntimeException("原记录【"+context.getData(ParamKeys.OLD_TXN_SQN).toString()+"】不存在");
					}
	}
}
