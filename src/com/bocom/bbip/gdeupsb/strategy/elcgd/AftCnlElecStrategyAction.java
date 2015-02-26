package com.bocom.bbip.gdeupsb.strategy.elcgd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
import com.bocom.jump.bp.core.Executable;

/**
 * 联机抹帐-广州电力，抹第三方帐后处理
 * 
 * @author qc.w
 * 
 */
public class AftCnlElecStrategyAction implements Executable {

	@Override
	public void execute(Context context) throws CoreException, CoreRuntimeException {
		
		context.setData("eleClrDte", context.getData("pwrtxnDate15")); // 供电公司清算日期

		context.setData("thdSqn", context.getData("eleThdSqn37")); // 第三方流水号
	}

}
