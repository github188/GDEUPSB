package com.bocom.bbip.gdeupsb.service.impl.watr00;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.bbip.utils.NumberUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
import com.bocom.jump.bp.service.sqlmap.SqlMap;

/**
 * 第三方发起对总账
 * @author hefengwen
 *
 */
public class CheckSumAccountServiceActionWATR00 extends BaseAction {
	
	private static Logger logger = LoggerFactory.getLogger(CheckSumAccountServiceActionWATR00.class);
	
	@Override
	public void execute(Context context) throws CoreException,	CoreRuntimeException {
		logger.info("CheckSumAccountServiceActionWATR00 execute start ... ...");
		String beginDate = context.getData("beginDate");
		String endDate = context.getData("endDate");
		context.setData("beginDate", DateUtils.format(DateUtils.parse(beginDate, DateUtils.STYLE_yyyyMMdd),DateUtils.STYLE_SIMPLE_DATE));
		context.setData("endDate", DateUtils.format(DateUtils.parse(endDate, DateUtils.STYLE_yyyyMMdd),DateUtils.STYLE_SIMPLE_DATE));
		logger.info("beginDate:["+beginDate+"]endDate:["+endDate+"]");
		context.setData("eupeBusTyp", "WATR00");
		context.setData("txnSts", "S");
		context.setData("txnTyp", "N");
		context.setData("TransCode", "Y005");
		
		Map<String,Object> map =((SqlMap)get("sqlMap")).queryForObject("watr00.findCountAmt", context.getDataMap());//查询总金额、总笔数
		
		String je = String.valueOf(map.get("JE"));
		if(je==null||"null".equals(je)){
			je = "0";
		}
		String count = String.valueOf( map.get("COUNT"));
		context.setData("je", NumberUtils.yuanToCentString(je));
		context.setData("count", count);
		logger.info("je:["+je+"]count:["+count+"]");
		
		logger.info("CheckSumAccountServiceActionWATR00 execute end ... ...");
	}

}
