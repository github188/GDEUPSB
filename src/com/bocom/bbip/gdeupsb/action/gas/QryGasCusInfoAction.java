package com.bocom.bbip.gdeupsb.action.gas;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.action.common.CommThdRspCdeAction;
import com.bocom.bbip.eups.adaptor.ThirdPartyAdaptor;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.common.GDErrorCodes;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class QryGasCusInfoAction extends BaseAction {

	public static Logger logger = LoggerFactory
			.getLogger(QryGasCusInfoAction.class);

	@Autowired
	@Qualifier("callThdTradeManager")
	ThirdPartyAdaptor callThdTradeManager;

	public void execute(Context context) throws CoreException {
		logger.info("=====================QryGasCusInfoAction start!....");
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
		logger.info("发燃气第三方，查看有无该用户编号信息");
		context.setData("TransCode", "QryUser");
		context.setData("gasBk", "cnjt");
		Map<String, Object> thdRspMsg = callThdTradeManager.trade(context);
		
		CommThdRspCdeAction rspCdeAction = new CommThdRspCdeAction();
		String responseCode = rspCdeAction.getThdRspCde(thdRspMsg, context.getData(ParamKeys.EUPS_BUSS_TYPE).toString());
		if (BPState.isBPStateOvertime(context)) {
			throw new CoreException(ErrorCodes.TRANSACTION_ERROR_TIMEOUT);
		} else if (!Constants.RESPONSE_CODE_SUCC.equals(responseCode)) {
			if (StringUtils.isEmpty(responseCode)) {
				throw new CoreException(GDErrorCodes.EUPS_ELE_GZ_UNKNOWN_ERROR);
			}
			throw new CoreException(responseCode);
		}
		
		context.setData("responseCode", Constants.RESPONSE_CODE_SUCC); 
		context.setDataMap(thdRspMsg);
		logger.info("======================context after callThd qryUser========"
				+ context);

		String transCode = context.getData("TransCode").toString().trim();
		if ("NOUser".equals(transCode)) { // 燃气返回无该用户信息
			throw new CoreRuntimeException(GDErrorCodes.GAS_QRY_AGT_ERR_NO);
		} else if ("UserStop".equals(transCode)) { // UserStop为此用户已报停
			throw new CoreRuntimeException(GDErrorCodes.GAS_QRY_AGT_ERR_STOP);
		}
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
	}
}
