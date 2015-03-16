package com.bocom.bbip.gdeupsb.action.zh;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bocom.bbip.comp.bua.BUAResult;
import com.bocom.bbip.comp.bua.message.AccountInfo;
import com.bocom.bbip.comp.bua.message.BalanceQueryRequest;
import com.bocom.bbip.comp.bua.service.BUAServiceAccessObject;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.utils.Assert;
import com.bocom.bbip.utils.ContextUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
/** 481219 查询活期帐户余额*/
public class EupsQueryBalance  extends BaseAction {
	private static final Log logger=LogFactory.getLog(EupsQueryBalance.class);
	
 
	public void process(Context context)throws CoreException,CoreRuntimeException,IOException{
		logger.info("Eups查询活期帐户余额 start!!");
		
		final String actNo=ContextUtils.assertDataHasLengthAndGetNNR(context, ParamKeys.TXN_AMT, ErrorCodes.EUPS_FIELD_EMPTY);
		/**交易渠道*/
		final String txnChl=ContextUtils.assertDataHasLengthAndGetNNR(context, ParamKeys.TXN_CHL, ErrorCodes.EUPS_FIELD_EMPTY);

		/** 判断是否为新账号 21位 */
		Assert.isFalse(actNo.trim().length() != 21,"GDErrorCodes.EUPS_ZH_FILE_IS_NEW_ACTNO");
		AccountInfo info = new AccountInfo();
		info.setAcoAc(actNo);
		BUAResult result = ((BUAServiceAccessObject)get("cardBINService")).balanceQuery(new BalanceQueryRequest(info)
		,((BUAServiceAccessObject)get("buaServer")).ACP_CHN);
		Assert.isNotNull( result, ErrorCodes.EUPS_QUERY_NO_DATA);

		context.setData(ParamKeys.CCY_NO, Constants.CCY_CDE_CNY);
		context.setData("balance", result.getBalanceDefault());
		logger.info("Eups查询活期帐户余额成功!!");
	}

	
	
	
}
