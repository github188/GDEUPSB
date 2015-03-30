package com.bocom.bbip.gdeupsb.action.hscard;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class CheckPrepareAction extends BaseAction{

	private static final Log log = LogFactory.getLog(CheckPrepareAction.class);
	
	public void execute(Context ctx) throws CoreRuntimeException,CoreException{
		log.info("CheckPrepareAction start.......");
		ctx.setData(ParamKeys.EUPS_BUSS_TYPE, "HSSC00");
		ctx.setData("ftpNo", "hsscCheckFile");
	}
}
