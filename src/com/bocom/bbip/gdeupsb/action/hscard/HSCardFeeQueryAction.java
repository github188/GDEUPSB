package com.bocom.bbip.gdeupsb.action.hscard;

import java.math.BigDecimal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.comp.CommonRequest;
import com.bocom.bbip.comp.account.AccountService;
import com.bocom.bbip.comp.account.support.CusActInfResult;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class HSCardFeeQueryAction extends BaseAction {

	private final static Log log = LogFactory.getLog(HSCardFeeQueryAction.class);

	@Autowired
	AccountService accountService;

	public void execute(Context ctx) throws CoreException, CoreRuntimeException {
		log.info("HSCardFeeQueryAction start......");
		// 判断账户是否余额不足
		
		// 查询内部账户余额
		String cusAc = ctx.getData(ParamKeys.CUS_AC);
		CommonRequest comRequest=CommonRequest.build(ctx);
		CusActInfResult acResult = accountService.getAcInf(comRequest, cusAc);
		BigDecimal actBal = acResult.getActBal();
		log.info("actBal=" + actBal);

	}

}
