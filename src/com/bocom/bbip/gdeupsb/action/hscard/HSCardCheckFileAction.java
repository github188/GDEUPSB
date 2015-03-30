package com.bocom.bbip.gdeupsb.action.hscard;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class HSCardCheckFileAction extends BaseAction{
	
	private final static Log log = LogFactory.getLog(HSCardCheckFileAction.class);

	public void execute(Context ctx) throws CoreException,CoreRuntimeException{
		log.info("HSCardCheckFileAction.....");
		String EXECUTE_PROCESS = "eups.checkBankFileToThird";
		ctx.setData(ParamKeys.EUPS_BUSS_TYPE, "HSSC00");
		
//		Date ttxnDt = com.bocom.bbip.thd.org.apache.commons.lang.time.DateUtils.addDays(new Date(), -1);
		
//		ctx.setData(GDParamKeys.TTXN_DT, ttxnDt);
		ctx.setData("ftpNo", "hsscCheckFile");
//		ctx.setData(ParamKeys.BAK_FLD1, "5291353");
		get(BBIPPublicService.class).synExecute(EXECUTE_PROCESS, ctx);
	}
}
