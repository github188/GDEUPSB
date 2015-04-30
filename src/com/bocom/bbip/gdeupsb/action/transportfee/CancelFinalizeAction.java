package com.bocom.bbip.gdeupsb.action.transportfee;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class CancelFinalizeAction extends BaseAction{
	private static final Log log = LogFactory.getLog(CancelFinalizeAction.class);
	
	public void execute(Context ctx) throws CoreException,CoreRuntimeException{
		log.info("CancelFinalizeAction start......");
		log.info("CancelFinalizeAction end......");
	}

}
