package com.bocom.bbip.gdeupsb.action.sign;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.common.GDErrorCodes;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GdsRunCtl;
import com.bocom.bbip.gdeupsb.repository.GdsRunCtlRepository;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

/**
 * 签约一站通 -协议数据更新-获取配置参数（GetRunCtl）
 * 
 * @author qc.w
 * 
 */
public class AgtDateUpdPreAction extends BaseAction {

	@Override
	public void execute(Context context) throws CoreException, CoreRuntimeException {
		log.info("AgtDateUpdPreAction start!..");

		GdsRunCtlRepository gdsRunCtlRepository = get(GdsRunCtlRepository.class);

		String gdsBid = context.getData(GDParamKeys.SIGN_STATION_BID); // 业务类型

		GdsRunCtl gdsRunCtl = gdsRunCtlRepository.findOne(gdsBid);
		if (null == gdsRunCtl) {
			throw new CoreException(GDErrorCodes.EUPS_SIGN_GDSBID_NOT_EXIST);
		}
		context.setVariable(GDParamKeys.SIGN_STATION_RUN_CTL_INFO, gdsRunCtl); // 将签约控制信息表信息放到context中，便于后续取值

		log.info("AgtDateUpdPreAction end,start to do impl!..");
	}

}
