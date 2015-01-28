package com.bocom.bbip.gdeupsb.service.impl.watr00;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
import com.bocom.jump.bp.service.sqlmap.SqlMap;
/**
 * 第三方发起明细对账请求
 * @author hefengwen
 *
 */
public class CheckDetailAccountServiceActionWATR00 extends BaseAction {
	
	private static Logger logger = LoggerFactory.getLogger(CheckDetailAccountServiceActionWATR00.class);
	
	@Override
	public void execute(Context context) throws CoreException,	CoreRuntimeException {
		logger.info("CheckDetailAccountServiceActionWATR00 execute start ... ...");
		String hno = context.getData("hno");
		String beginDate = context.getData("beginDate");
		String endDate = context.getData("endDate");
		context.setData("beginDate", DateUtils.format(DateUtils.parse(beginDate, DateUtils.STYLE_yyyyMMdd),DateUtils.STYLE_SIMPLE_DATE));
		context.setData("endDate", DateUtils.format(DateUtils.parse(endDate, DateUtils.STYLE_yyyyMMdd),DateUtils.STYLE_SIMPLE_DATE));
		logger.info("hno:["+hno+"]beginDate:["+beginDate+"]endDate:["+endDate+"]");
		context.setData("TransCode", "Y006");
		context.setData(ParamKeys.ACCOUNT_DATE, null);
		
		get(BBIPPublicService.class).asynExecute("eups.sendCheckDetailAccount", context);
		
		logger.info("CheckDetailAccountServiceActionWATR00 execute end ... ...");
	}

}
