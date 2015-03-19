package com.bocom.bbip.gdeupsb.service.impl.watr00;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
/**
 * 第三方发起批量扣款请求
 * 1.第三方接收到银行发起的批量扣款查询请求
 * 2.第三方将批量扣款文件放到FTP服务器，发起批量扣款查询结果请求
 * 3.银行返回批量扣款查询结果请求应答报文
 * @author hefengwen
 *
 */
public class BatchQueryResultServiceActionWATR00 extends BaseAction {
	
	private static Logger logger = LoggerFactory.getLogger(BatchQueryResultServiceActionWATR00.class);
	
	@Override
	public void execute(Context context) throws CoreException,	CoreRuntimeException {
		logger.info("BatchQueryResultServiceActionWATR00 start ... ...");
		String path = context.getData("path");
		String filename = context.getData("filename");
		String filesize = context.getData("filesize");
		logger.info("path["+path+"]filename["+filename+"]filesize["+filesize+"]");
		context.setData("TransCode", "Y008");
		context.setData("ErrorNum", "0000");
		context.setData(ParamKeys.RESPONSE_CODE, "000000");
//		Map<String,Object> map = new HashMap<String,Object>();
//		map.put("jopSchedulingData", "汕头水费批扣交易");
//		context.getAttribute("");// setVariable("PARAMETERS", map);
		
		//异步调用文件批量处理交易
		get(BBIPPublicService.class).asynExecute("eups.fileBatchPayCreateDataProcess", context);
		logger.info("BatchQueryResultServiceActionWATR00 end ... ...");
	}

}
