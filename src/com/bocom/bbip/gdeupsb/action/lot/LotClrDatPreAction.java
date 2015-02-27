package com.bocom.bbip.gdeupsb.action.lot;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

/**
 * 清算轧差准备-获取系统配置
 * 
 * @author qc.w
 * 
 */
public class LotClrDatPreAction extends BaseAction {

    CommonLotAction commonLotAction =new CommonLotAction();
	@Override
	public void execute(Context context) throws CoreException, CoreRuntimeException {
		log.info("LotClearAcDealAction start!..");
		// 获取系统配置
        commonLotAction.GetSysCfg(context);
	}
}
