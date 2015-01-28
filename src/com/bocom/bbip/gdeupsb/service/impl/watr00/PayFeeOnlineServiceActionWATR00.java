package com.bocom.bbip.gdeupsb.service.impl.watr00;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bocom.bbip.eups.entity.EupsAmountInfo;
import com.bocom.bbip.eups.spi.service.online.PayFeeOnlineService;
import com.bocom.bbip.eups.spi.vo.CommHeadDomain;
import com.bocom.bbip.eups.spi.vo.PayFeeOnlineDomain;
import com.bocom.bbip.eups.spi.vo.PayFeeOnlineRetDomain;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

/**
 * 联机缴费
 * @author hefengwen
 *
 */
public class PayFeeOnlineServiceActionWATR00 implements PayFeeOnlineService {
	
	private static Logger logger = LoggerFactory.getLogger(PayFeeOnlineServiceActionWATR00.class);

	@Override
	public Map<String, Object> aftThdDeal(CommHeadDomain commheaddomain,	PayFeeOnlineRetDomain payfeeonlineretdomain, Context context)	throws CoreException {
		// TODO :第三方记账完成后处理
		logger.info("PayFeeOnlineServiceActionWATR00 aftThdDeal start ... ...");
		logger.info("PayFeeOnlineServiceActionWATR00 aftThdDeal end ... ...");
		return null;
	}

	/**
	 * 交易前特色处理
	 */
	@Override
	public Map<String, Object> preCheckDeal(CommHeadDomain commheaddomain,PayFeeOnlineDomain payfeeonlinedomain, Context context) throws CoreException {
		//暂时不需实现
		logger.info("PayFeeOnlineServiceActionWATR00 preCheckDeal start ... ...");
		
		logger.info("PayFeeOnlineServiceActionWATR00 preCheckDeal end ... ...");
		return null;
	}

	/**
	 * 
	 */
	@Override
	public Map<String, Object> preHostDeal(CommHeadDomain commheaddomain,PayFeeOnlineDomain payfeeonlinedomain, Context context)	throws CoreException {
		// TODO:使用标准版即可，如果前端接口为自定义，需比对接口字段是否一致
		logger.info("PayFeeOnlineServiceActionWATR00 preHostDeal start ... ...");
		context.setData("tmlNo", context.getData("tlrTmlId"));//终端号转换
		logger.info("PayFeeOnlineServiceActionWATR00 preHostDeal end ... ...");
		return null;
	}

	@Override
	public Map<String, Object> preThdDeal(CommHeadDomain commheaddomain,	PayFeeOnlineDomain payfeeonlinedomain,EupsAmountInfo eupsamountinfo, Context context)	throws CoreException {
		logger.info("PayFeeOnlineServiceActionWATR00 preThdDeal start ... ...");
		// TODO:为第三方接口报文字段赋值，发送请求至第三方
		context.setData("type", "Y002");
		context.setData("accountdate", "20150123");
		context.setData("waterno", "JH201501230000000001");//TODO:流水号生成
		context.setData("bankcode", "COMM");
		context.setData("salesdepart", "327103");
		context.setData("salesperson", "327103");
		context.setData("busitime", "20150123104100");
		context.setData("thdRspCde", "0000");
		context.setData("zprice", "10000");
		context.setData("months", "3");
		context.setData("operano", "0001");
		context.setData("password", "123123");
		context.setData("md5digest", "0000000");
		
		context.setData("hno", context.getData("thdCusNo"));
		context.setData("je", context.getData("txnAmt"));
		context.setData("jffs", "0");
		context.setData("thdSqn", context.getData("waterno"));
		logger.info("PayFeeOnlineServiceActionWATR00 preThdDeal end ... ...");
		return null;
	}

}
