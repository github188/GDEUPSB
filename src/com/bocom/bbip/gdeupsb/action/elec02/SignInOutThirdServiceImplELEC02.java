package com.bocom.bbip.gdeupsb.action.elec02;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsThdTranCtlInfo;
import com.bocom.bbip.eups.repository.EupsThdTranCtlInfoRepository;
import com.bocom.bbip.eups.spi.service.basemanage.SignInOutThirdService;
import com.bocom.bbip.eups.spi.vo.SignInOutDomain;
import com.bocom.bbip.utils.ContextUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
/**
 * @author wuyh
 * @date 2015-2-13 上午09:06:35
 * @Company TD
 */
public class SignInOutThirdServiceImplELEC02 extends BaseAction implements SignInOutThirdService {
	private static final Log logger = LogFactory.getLog(EupsCancelBatchAction.class);

	@Override
	public Map<String, Object> signInOutDeal(SignInOutDomain arg0, Context context)
			throws CoreException {
		
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
		
		
		return null;
	}
      
}
