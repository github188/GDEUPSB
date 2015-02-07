package com.bocom.bbip.gdeupsb.strategy.efek.cancelFeeOnline;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
import com.bocom.jump.bp.core.Executable;

public class AftCancelFeeAction implements Executable{
	private final static Log logger=LogFactory.getLog(AftCancelFeeAction.class);

	/**
	 * 抹账后处理
	 */
	@Override
	public void execute(Context context)throws CoreException,CoreRuntimeException{
		logger.info("==========Start  AftCancelFeeAction");
		
		context.setData("TIATyp", "C");      //不明 TIATyp 是什么
		//TODO	    <Set>TTxnCd=STRCAT(SUBSTR($OTTxnCd,1,5),9)</Set>	    <Set>HTxnCd=STRCAT(SUBSTR($OHTxnCd,1,5),9)</Set>
		String txnCd=(String)context.getData(ParamKeys.TXN_CODE);
		
		context.setData(ParamKeys.THD_TXN_CDE, txnCd.substring(1, 6)+"9");
		context.setData(ParamKeys.TXN_CODE, txnCd.substring(1, 6)+"9");
		
	}
}
