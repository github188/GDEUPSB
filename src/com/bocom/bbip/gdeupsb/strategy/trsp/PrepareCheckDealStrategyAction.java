package com.bocom.bbip.gdeupsb.strategy.trsp;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
import com.bocom.jump.bp.core.Executable;

public class PrepareCheckDealStrategyAction implements Executable{
	
	private final static Log log = LogFactory.getLog(PrepareCheckDealStrategyAction.class);
	public void execute(Context ctx) throws CoreException,CoreRuntimeException{
		log.info("PrepareCheckDealStrategyAction start.......");
		String actNo = (String)ctx.getData(GDParamKeys.ACT_NO);
		String carNo = ctx.getData(GDParamKeys.CAR_NO).toString();
		String logNo = ctx.getData(ParamKeys.SEQUENCE).toString();
		ctx.setData(ParamKeys.CUS_AC, actNo);
		ctx.setData(ParamKeys.THD_CUSTOMER_NO, carNo);
		ctx.setData(GDParamKeys.THD_KEY, logNo);
		ctx.setData(GDParamKeys.BR_NO, "443999");
		ctx.setData(ParamKeys.AC_DATE, new Date());
	}
}
