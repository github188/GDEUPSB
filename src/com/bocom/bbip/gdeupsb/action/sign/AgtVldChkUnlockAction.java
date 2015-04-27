package com.bocom.bbip.gdeupsb.action.sign;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.gdeupsb.common.GDErrorCodes;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.service.Result;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

/**
 * 签约一站通 -协议校验-解锁等处理
 * 
 * @author qc.w
 * 
 */
public class AgtVldChkUnlockAction extends BaseAction {

	@Override
	public void execute(Context context) throws CoreException, CoreRuntimeException {
		log.info("AgtValidCheckAction start!..");

//		String gdsBId = context.getData(GDParamKeys.SIGN_STATION_BID); // 业务类型

		// 交易解锁
//		Result ret1 = get(BBIPPublicService.class).unlock(gdsBId);
//		int status1 = ret1.getStatus();
//		if (status1 != 0) {
//			throw new CoreException(GDErrorCodes.EUPS_UNLOCK_FAIL, "交易解锁失败!!!");
//		}
	}

}
