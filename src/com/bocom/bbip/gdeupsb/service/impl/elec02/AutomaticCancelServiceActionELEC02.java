package com.bocom.bbip.gdeupsb.service.impl.elec02;

import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bocom.bbip.eups.spi.service.online.AutomaticCancelService;
import com.bocom.bbip.eups.spi.vo.CancelDomain;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

/**
 * 自动区分抹账：根据流水表第三方状态和主机状态
 * @author hefengwen
 *
 */
public class AutomaticCancelServiceActionELEC02 implements	AutomaticCancelService {
	
	private static Logger logger = LoggerFactory.getLogger(AutomaticCancelServiceActionELEC02.class);


	/**
	 * 第三方抹账前处理
	 */
	@Override
	public Map<String, Object> preCancel(CancelDomain canceldomain,Context context) throws CoreException {
		logger.info("AutomaticCancelServiceActionWATR00 preCancel  start ... ...");
		// TODO :第三方抹帐前报文接口字段处理
		String date = DateUtils.format(new Date(), "yyyyMMdd");
		String time = DateUtils.format(new Date(),"yyyyMMddHHmmss");
		context.setData("AppTradeCode", "12");//业务交易码
		context.setData("StartAddr", "");
		context.setData("DestAddr", "");
		context.setData("MesgID", "");
		context.setData("WorkDate", date);
		context.setData("SendTime", time);
		context.setData("mesgPRI", "9");
		context.setData("recordNum", "0");
		context.setData("FileName", "");
		context.setData("zipFlag", "0");
		
		
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
