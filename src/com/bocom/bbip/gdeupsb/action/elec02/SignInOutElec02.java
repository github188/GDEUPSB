package com.bocom.bbip.gdeupsb.action.elec02;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsThdTranCtlInfo;
import com.bocom.bbip.eups.repository.EupsThdTranCtlInfoRepository;
import com.bocom.bbip.utils.ContextUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class SignInOutElec02 extends BaseAction{

	private static final Log logger = LogFactory.getLog(SignInOutElec02.class);
	public void execute(Context context) throws CoreException,CoreRuntimeException{
		final String txnDte=context.getData(ParamKeys.TXN_DAT);
		final String txnTyp=context.getData(ParamKeys.TXN_TYP);
		EupsThdTranCtlInfo info = (EupsThdTranCtlInfo)ContextUtils.getDataAsObject(context, EupsThdTranCtlInfo.class);
		EupsThdTranCtlInfo ret=new EupsThdTranCtlInfo();
		if ("0".equals(txnTyp)) {
			ret.setTxnCtlSts(Constants.SIGN_SET_TYPE_SIGNIN);
			logger.info("签到交易开始");

		}
		if ("1".equals(txnTyp)) {
			ret.setTxnCtlSts(Constants.SIGN_SET_TYPE_SIGNOUT);
			logger.info("签退交易开始");

		}
		ret.setComNo(info.getComNo());
		ret.setTxnDte(DateUtils.parse(txnDte));
		get(EupsThdTranCtlInfoRepository.class).update(ret);
	}
}
