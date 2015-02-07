package com.bocom.bbip.gdeupsb.action.efek;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class QryCusMsgAction extends BaseAction{
	/**
	 * 银行查询客户信息
	 */
	public void execute(Context context)throws CoreException,CoreRuntimeException{
		log.info("==============Start  QryCusMsgAction");
		String eupsBusTyp=context.getData(ParamKeys.EUPS_BUSS_TYPE).toString();
		String payNo=context.getData(GDParamKeys.PAY_NO).toString();
		
		//TODO 查询记录   签约表
		
	}
}
