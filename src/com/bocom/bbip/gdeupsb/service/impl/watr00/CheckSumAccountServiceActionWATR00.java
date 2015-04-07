package com.bocom.bbip.gdeupsb.service.impl.watr00;

import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.bbip.utils.NumberUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
import com.bocom.jump.bp.service.id.seq.StepSequenceFactory;
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
		
		context.setData("accountdate", DateUtils.format((Date)context.getData(ParamKeys.AC_DATE), DateUtils.STYLE_yyyyMMdd));
		
		StepSequenceFactory s = context.getService("logNoService");
		String logNo = s.create().toString();
		context.setData("waterno", "JH"+logNo);//流水号生成
		
//		context.setData("bankcode", "交行");
//		context.setData("salesdepart",((String)context.getData(ParamKeys.BR)).substring(2, 8));
//		context.setData("salesperson", ((String)context.getData(ParamKeys.TELLER)).substring(4, 7));
//		context.setData("busitime", DateUtils.format(new Date(),DateUtils.STYLE_yyyyMMddHHmmss));
//		
//		context.setData("zprice", "");
//		context.setData("months", "");
//		context.setData("operano", "");
//		context.setData("password", "        ");
//		context.setData("md5digest", " ");
		
		Map<String,Object> map =((SqlMap)get("sqlMap")).queryForObject("watr00.findCountAmt", context.getDataMap());//查询总金额、总笔数
		
		
		String je = String.valueOf(map.get("JE"));
		if(je==null||"null".equals(je)){
			je = "0";
		}
		String count = String.valueOf( map.get("COUNT"));
		context.setData("je", NumberUtils.yuanToCentString(je));
		context.setData("count", count);
		logger.info("je:["+je+"]count:["+count+"]");
		context.setData("thdRspCde", "0000");
		context.setData(ParamKeys.RESPONSE_CODE, "000000");
		logger.info("CheckSumAccountServiceActionWATR00 execute end ... ...");
	}

}
