package com.bocom.bbip.gdeupsb.action.elec02;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsThdTranCtlInfo;
import com.bocom.bbip.eups.repository.EupsThdTranCtlInfoRepository;
import com.bocom.bbip.utils.ContextUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class SignInOutAction extends BaseAction{

	 @Override
	    public void execute(Context context) throws CoreException, CoreRuntimeException {
		 context.setData("thdRspCde", "00");
	    context.setData("rspMsg", "交易成功");
	    context.setData("DlTxt", "交易成功");
		 
//		if("gdeupsb.signInOutThird".equals(context.getData("TransCode"))){
//			context.setData(ParamKeys.TXN_TYP, Constants.SIGN_SET_TYPE_SIGNIN);
//		}else{
//			context.setData(ParamKeys.TXN_TYP, Constants.SIGN_SET_TYPE_SIGNOUT);
//		}
//	        String txnTyp = context.<String> getData(ParamKeys.TXN_TYP);
//	        // 根据报文中操作选项（txnTyp）判断签到或者签退：0-签到；1-签退
//	        if (Constants.SIGN_SET_TYPE_SIGNIN.equals(txnTyp)) {
//	            chlSignIn(context);
//	        } else if (Constants.SIGN_SET_TYPE_SIGNOUT.equals(txnTyp)) {
//	            chlSignOut(context);
//	        } else {
//	            throw new CoreException(ErrorCodes.EUPS_OPR_TYP_ERR);
//	        }
	    }

	    /*
	     * 签到.
	     */
	    private void chlSignIn(Context context) throws CoreException, CoreRuntimeException {
	    	
	    	
	    	context.setData("thdRspCde", "00");
	    	context.setData("rspMsg", "交易成功");
	    	context.setData("DlTxt", "交易成功");

//	        EupsThdTranCtlInfo eupsThdTranCtlInfo = ContextUtils.getDataAsObject(context, EupsThdTranCtlInfo.class);
//	        EupsThdTranCtlInfo resultThdTranCtlInfo = get(EupsThdTranCtlInfoRepository.class).findOne(eupsThdTranCtlInfo.getComNo());
//	        context.setData(ParamKeys.CONSOLE_THD_TRANS_CONTROL_INFO_LIST, resultThdTranCtlInfo);
//	        
//	        if (resultThdTranCtlInfo == null) {
//	            throw new CoreException(ErrorCodes.EUPS_THD_TRANS_CTLINFO_NOTEXIST);
//	        } else if (resultThdTranCtlInfo.isTxnCtlStsSignin()) {
//	            throw new CoreException(ErrorCodes.THD_CHL_ALDEAY_SIGN_IN);
//	        } else if (resultThdTranCtlInfo.isTxnCtlStsCheckBillIng()) {
//	            throw new CoreException(ErrorCodes.THD_CHL_SIGNIN_NOT_ALLOWWED);
//	        } 
	    }

	    /*签退
	     */
	    private void chlSignOut(Context context) throws CoreException, CoreRuntimeException {
	        EupsThdTranCtlInfo eupsThdTranCtlInfo = ContextUtils.getDataAsObject(context, EupsThdTranCtlInfo.class);
	        EupsThdTranCtlInfo resultThdTranCtlInfo = get(EupsThdTranCtlInfoRepository.class).findOne(eupsThdTranCtlInfo.getComNo());
	        if (resultThdTranCtlInfo == null) {
	            throw new CoreException(ErrorCodes.EUPS_THD_TRANS_CTLINFO_NOTEXIST);
	        } else if (resultThdTranCtlInfo.isTxnCtlStsSignout()) {
	            throw new CoreException(ErrorCodes.THD_CHL_ALDEAY_SIGN_OUT);
	        } else if (resultThdTranCtlInfo.isTxnCtlStsCheckBillIng()) {
	            throw new CoreException(ErrorCodes.THD_CHL_SIGNOUT_NOT_ALLOWWED);
	        }
	    }
}
