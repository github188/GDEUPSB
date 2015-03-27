package com.bocom.bbip.gdeupsb.action.hscard;

import java.math.BigDecimal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.comp.BBIPPublicService;
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
	
	 @Autowired
	 private BBIPPublicService bbipPublicService;

	public void execute(Context ctx) throws CoreException, CoreRuntimeException {
		log.info("HSCardFeeQueryAction start......");
		
		ctx.setData("traceNo", bbipPublicService.getTraceNo());
		// 查询内部账户余额
		//TODO:此处为了测试，暂时写死，后续需要修改
		ctx.setData(ParamKeys.BK, bbipPublicService.getParam("BBIP", "BK"));
		ctx.setData(ParamKeys.BR, "01441131999");
		
		ctx.setData(ParamKeys.TELLER, "ABIR148");
		ctx.setData(ParamKeys.CHANNEL, "00");
//		ctx.setData(ParamKeys.TRACE_NO, ctx.getData(ParamKeys.SEQUENCE));
		String cusAc = ctx.getData(ParamKeys.CUS_AC);
		CommonRequest comRequest=CommonRequest.build(ctx);
		CusActInfResult acResult = accountService.getAcInf(comRequest, cusAc);
		BigDecimal actBal = acResult.getActBal();
		BigDecimal avaBal = acResult.getAvaBal();
		ctx.setData("avaBal", avaBal);
		ctx.setData("actBal", actBal);
		log.info("actBal=" + actBal);
	}

}
