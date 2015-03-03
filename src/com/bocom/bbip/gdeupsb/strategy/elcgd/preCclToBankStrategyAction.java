package com.bocom.bbip.gdeupsb.strategy.elcgd;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
import com.bocom.jump.bp.core.Executable;

public class preCclToBankStrategyAction implements Executable {
	
	private static final Log logger=LogFactory.getLog(preCclToBankStrategyAction.class);
    @Autowired
    private BBIPPublicService service;
	@Override
	public void execute(Context context) throws CoreException,CoreRuntimeException {
		logger.info("单笔缴费冲正前处理：缴费号：【" + context.getData(ParamKeys.CUS_AC) + "】");

		context.setData(ParamKeys.CHL_TYP, Constants.CHL_TYPE_THIRD_SYSTEM);/** 渠道第三方 */
		context.setData(ParamKeys.CHANNEL, Constants.CHL_TYPE_THIRD_SYSTEM);
		context.setData(ParamKeys.BR, "444999");/** 网点号需要处理 */
		context.setData(ParamKeys.SEQUENCE, service.getBBIPSequence());

		logger.info("单笔缴费冲正前处理：缴费号：【" + context.getData(ParamKeys.CUS_AC)+ "】结束"); 
	}
}

