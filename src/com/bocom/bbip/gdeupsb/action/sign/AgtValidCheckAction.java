package com.bocom.bbip.gdeupsb.action.sign;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.common.GDErrorCodes;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GdsRunCtl;
import com.bocom.bbip.gdeupsb.repository.GdsAgtInfRepository;
import com.bocom.bbip.gdeupsb.repository.GdsAgtWaterRepository;
import com.bocom.bbip.service.Result;
import com.bocom.bbip.utils.CollectionUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

/**
 * 签约一站通 -协议校验-公共处理
 * 
 * @author qc.w
 * 
 */
public class AgtValidCheckAction extends BaseAction {

	@Override
	public void execute(Context context) throws CoreException, CoreRuntimeException {
		log.info("AgtValidCheckAction start!..");

		String gdsBId = context.getData(GDParamKeys.SIGN_STATION_BID); // 业务类型

		// 交易上锁
		Result ret = get(BBIPPublicService.class).tryLock(gdsBId, (long) 0, (long) 1000 * 60 * 20);
		int status = ret.getStatus();
		if (status != 0) {
			throw new CoreException(GDErrorCodes.EUPS_LOCK_FAIL, "交易加锁失败!!!");
		}

	}

}
