package com.bocom.bbip.gdeupsb.service.impl.elec02;

import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bocom.bbip.eups.entity.EupsAmountInfo;
import com.bocom.bbip.eups.spi.service.online.PayFeeOnlineService;
import com.bocom.bbip.eups.spi.vo.CommHeadDomain;
import com.bocom.bbip.eups.spi.vo.PayFeeOnlineDomain;
import com.bocom.bbip.eups.spi.vo.PayFeeOnlineRetDomain;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

/**
 * 联机缴费
 * @author hefengwen
 *
 */
public class PayFeeOnlineServiceActionELEC02 implements PayFeeOnlineService {
	
	private static Logger logger = LoggerFactory.getLogger(PayFeeOnlineServiceActionELEC02.class);

	@Override
	public Map<String, Object> aftThdDeal(CommHeadDomain commheaddomain,	PayFeeOnlineRetDomain payfeeonlineretdomain, Context context)	throws CoreException {
		// TODO :第三方记账完成后处理
		logger.info("PayFeeOnlineServiceActionELEC02 aftThdDeal start ... ...");
		logger.info("PayFeeOnlineServiceActionELEC02 aftThdDeal end ... ...");
		return null;
	}

	/**
	 * 交易前特色处理
	 */
	@Override
	public Map<String, Object> preCheckDeal(CommHeadDomain commheaddomain,PayFeeOnlineDomain payfeeonlinedomain, Context context) throws CoreException {
		//暂时不需实现
		logger.info("PayFeeOnlineServiceActionELEC02 preCheckDeal start ... ...");
		
		logger.info("PayFeeOnlineServiceActionELEC02 preCheckDeal end ... ...");
		return null;
	}

	/**
	 * 
	 */
	@Override
	public Map<String, Object> preHostDeal(CommHeadDomain commheaddomain,PayFeeOnlineDomain payfeeonlinedomain, Context context)	throws CoreException {
		// TODO:使用标准版即可，如果前端接口为自定义，需比对接口字段是否一致
		logger.info("PayFeeOnlineServiceActionELEC02 preHostDeal start ... ...");
		context.setData("tmlNo", context.getData("tlrTmlId"));//终端号转换
		logger.info("PayFeeOnlineServiceActionELEC02 preHostDeal end ... ...");
		return null;
	}

	@Override
	public Map<String, Object> preThdDeal(CommHeadDomain commheaddomain,	PayFeeOnlineDomain payfeeonlinedomain,EupsAmountInfo eupsamountinfo, Context context)	throws CoreException {
		logger.info("PayFeeOnlineServiceActionELEC02 preThdDeal start ... ...");
		// TODO:为第三方接口报文字段赋值，发送请求至第三方
		String date = DateUtils.format(new Date(), "yyyyMMdd");
		String time = DateUtils.format(new Date(),"yyyyMMddHHmmss");
		context.setData("AppTradeCode", "11");//业务交易码
		context.setData("StartAddr", "");
		context.setData("DestAddr", "");
		context.setData("MesgID", "");
		context.setData("WorkDate", date);
		context.setData("SendTime", time);
		context.setData("mesgPRI", "9");
		context.setData("recordNum", "0");
		context.setData("FileName", "");
		context.setData("zipFlag", "0");
		
		context.setData("WTC", "");
		context.setData("TMN", context.getData("brno"));
		context.setData("WD0", date);
		context.setData("CLZ", "");
		context.setData("ECD", "");
		context.setData("8ED", context.getData("rsvFld1"));
		context.setData("JFH", context.getData("thdCusNo"));
		context.setData("FYM", context.getData("rsvFld2"));
		context.setData("QFG", context.getData("rsvFld3"));
		context.setData("STO", context.getData("tlr"));
		
		context.setData("txnSqn", context.getData("sqn"));//保存缴费交易流水号，返回给前端，用于抹帐
		logger.info("PayFeeOnlineServiceActionELEC02 preThdDeal end ... ...");
		return null;
	}

}
