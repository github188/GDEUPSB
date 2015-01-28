package com.bocom.bbip.gdeupsb.service.impl.watr00;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bocom.bbip.eups.spi.service.online.AutomaticCancelService;
import com.bocom.bbip.eups.spi.vo.CancelDomain;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

/**
 * 自动区分抹账：根据流水表第三方状态和主机状态
 * @author hefengwen
 *
 */
public class AutomaticCancelServiceActionWATR00 implements	AutomaticCancelService {
	
	private static Logger logger = LoggerFactory.getLogger(AutomaticCancelServiceActionWATR00.class);


	/**
	 * 第三方抹账前处理
	 */
	@Override
	public Map<String, Object> preCancel(CancelDomain canceldomain,Context context) throws CoreException {
		logger.info("AutomaticCancelServiceActionWATR00 preCancel  start ... ...");
		// TODO :第三方抹帐前报文接口字段处理
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
		
		
//		context.setData("hno", journal.getThdCusNo());
//		context.setData("je", journal.getTxnAmt());
//		context.setData("lsh", journal.getThdSqn());
		logger.info("AutomaticCancelServiceActionWATR00 preCancel  end ... ...");
		return null;
	}
	/**
	 * 第三方抹账后处理
	 */
	@Override
	public Map<String, Object> aftCancel(CancelDomain canceldomain,Context context) throws CoreException {
		logger.info("AutomaticCancelServiceActionWATR00 aftCancel  start ... ...");
		
		logger.info("AutomaticCancelServiceActionWATR00 aftCancel  end ... ...");
		return null;
	}

}
