package com.bocom.bbip.gdeupsb.strategy.elcgd;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
import com.bocom.jump.bp.core.Executable;

public class aftCclToBankStrategyAction implements Executable {
	
	private static final Log logger=LogFactory.getLog(aftCclToBankStrategyAction.class);

	@Override
	public void execute(Context context) throws CoreException,CoreRuntimeException {
	   logger.info("单笔缴费冲正后处理：缴费号：【" + context.getData(ParamKeys.CUS_AC) + "】");
		

       logger.info("单笔缴费冲正后处理：缴费号：【"+context.getData(ParamKeys.CUS_AC)+"】结束");   
	}
}

