package com.bocom.bbip.gdeupsb.strategy.elcgd;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.utils.NumberUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
import com.bocom.jump.bp.core.Executable;

/**
 * 
 * @author qc.w
 * 
 */
public class PreEleCheckDealStrategyAction implements Executable {
	private final static Logger log = LoggerFactory.getLogger(PreEleCheckDealStrategyAction.class);

	@Override
	public void execute(Context context) throws CoreException, CoreRuntimeException {
		log.info("PreEleCheckDealStrategyAction start!..");

		String txnAmt = context.getData("thdTxnAmt");
		BigDecimal realAmt = NumberUtils.centToYuan(txnAmt);
		context.setData(ParamKeys.TXN_AMOUNT, realAmt);

	}
}
