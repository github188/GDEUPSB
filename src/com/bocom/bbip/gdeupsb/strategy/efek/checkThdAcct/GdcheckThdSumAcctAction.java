package com.bocom.bbip.gdeupsb.strategy.efek.checkThdAcct;

import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class GdcheckThdSumAcctAction extends BaseAction{
	@Autowired
	BBIPPublicService bbipPublicService;
	@Override
	public void execute(Context context) throws CoreException,
			CoreRuntimeException {
		log.info("===========Start   GdcheckThdSumAcctAction");
		context.setData(ParamKeys.EUPS_BUSS_TYPE, "ELEC00");
//		context.setData(ParamKeys.FTP_ID, arg1);
		String mothed="eups.checkThdSumAcct";
		bbipPublicService.synExecute(mothed, context);
		log.info("===========End   GdcheckThdSumAcctAction");
	}
}
