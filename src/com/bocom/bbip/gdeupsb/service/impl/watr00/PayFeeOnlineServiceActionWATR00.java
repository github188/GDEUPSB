package com.bocom.bbip.gdeupsb.service.impl.watr00;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsAmountInfo;
import com.bocom.bbip.eups.spi.service.online.PayFeeOnlineService;
import com.bocom.bbip.eups.spi.vo.CommHeadDomain;
import com.bocom.bbip.eups.spi.vo.PayFeeOnlineDomain;
import com.bocom.bbip.eups.spi.vo.PayFeeOnlineRetDomain;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.bbip.utils.NumberUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.service.id.seq.StepSequenceFactory;

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
//		context.setData("oldTxnSqn", context.getData("txnSqn"));
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
		context.setData("accountdate", DateUtils.format((Date)context.getData(ParamKeys.AC_DATE), DateUtils.STYLE_yyyyMMdd));
		StepSequenceFactory s = context.getService("logNoService");
		String logNo = s.create().toString();
		context.setData("waterno", "JH"+logNo);//流水号生成
		context.setData("bankcode", "JT");
		context.setData("salesdepart",((String)context.getData(ParamKeys.BR)).substring(2, 8));
		context.setData("salesperson", ((String)context.getData(ParamKeys.TELLER)).substring(4, 7));
		context.setData("busitime", DateUtils.format(new Date(),DateUtils.STYLE_yyyyMMddHHmmss));
		context.setData("thdRspCde", "0");
		context.setData("zprice", "");
		context.setData("months", "");
		context.setData("operano", "");
		context.setData("password", "        ");
		context.setData("md5digest", " ");
		
		context.setData("hno", context.getData("thdCusNo"));
		BigDecimal txnAmt = context.getData("txnAmt");
		context.setData("je", NumberUtils.yuanToCentString(txnAmt));
		context.setData("jffs", "0");
		context.setData("thdSqn", context.getData("waterno"));
		
		context.setData("txnSqn", context.getData("sqn"));//保存缴费交易流水号，返回给前端，用于抹帐
		logger.info("PayFeeOnlineServiceActionWATR00 preThdDeal end ... ...");
		return null;
	}

}
