package com.bocom.bbip.gdeupsb.strategy.efek.checkThdAcct;

import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class GdcheckThdDetlAcctAction extends BaseAction{
	@Autowired
	BBIPPublicService bbipPublicService;
	@Override
	public void execute(Context context) throws CoreException,
			CoreRuntimeException {
		log.info("===========Start   GdcheckThdDetlAcctAction");
		context.setData(ParamKeys.EUPS_BUSS_TYPE, "ELEC00");
		context.setData("sqns", context.getData("sqn"));
		String mothed="eups.checkThdDetlAcct";
		bbipPublicService.synExecute(mothed, context);
		log.info("===========End   GdcheckThdDetlAcctAction");
	}
}
