package com.bocom.bbip.gdeups.strategy.elcgd;

import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
import com.bocom.jump.bp.core.Executable;

/**
 * 
 * @author qc.w
 *
 */
public class PreInitElecStrategyAction implements Executable{

	@Override
	public void execute(Context context) throws CoreException, CoreRuntimeException {
		
		context.setData(ParamKeys.PAYFEE_TYPE, Constants.TXN_PAYFEE_TYPE_PAYMENT);
		
		//TODO：清算信息校验
	}
}
