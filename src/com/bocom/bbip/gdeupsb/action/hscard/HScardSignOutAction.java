package com.bocom.bbip.gdeupsb.action.hscard;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsThdTranCtlInfo;
import com.bocom.bbip.eups.repository.EupsThdTranCtlInfoRepository;
import com.bocom.bbip.utils.Assert;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class HScardSignOutAction extends BaseAction{
	@Override

	public void execute(Context context) throws CoreException,CoreRuntimeException{
		context.setData(ParamKeys.TXN_TYP, Constants.SIGN_SET_TYPE_SIGNOUT);
		Assert.hasLengthInData(context, ParamKeys.COMPANY_NO, ErrorCodes.EUPS_FIELD_EMPTY, "单位编号");
		Assert.hasLengthInData(context, ParamKeys.TXN_TYP, ErrorCodes.EUPS_FIELD_EMPTY, "交易状态");
	
		
		
		EupsThdTranCtlInfo eupsThdTranCtlInfo = BeanUtils.toObject(context.getDataMap(), EupsThdTranCtlInfo.class);
		EupsThdTranCtlInfo resultThdTranCtlInfo = get(EupsThdTranCtlInfoRepository.class).findOne(eupsThdTranCtlInfo.getComNo());
		Assert.isNotNull(resultThdTranCtlInfo, ErrorCodes.THD_CHL_NOT_FOUND);
        Assert.isTrue(!Constants.TXN_CTL_STS_SIGNOUT.equals(resultThdTranCtlInfo.getTxnCtlSts()), ErrorCodes.THD_CHL_ALDEAY_SIGN_OUT);
		Assert.isTrue(!Constants.TXN_CTL_STS_CHECKBILL_ING.equals(resultThdTranCtlInfo.getTxnCtlSts()),ErrorCodes.THD_CHL_SIGNOUT_NOT_ALLOWWED);
		if (resultThdTranCtlInfo.getTxnCtlSts().equals(Constants.SIGN_SET_TYPE_SIGNIN)){
//		    TODO://标准版里没此方法eupsThdTranCtlInfo.signOutKeepKey();
		    get(EupsThdTranCtlInfoRepository.class).update(eupsThdTranCtlInfo);
		}
	
}
}
