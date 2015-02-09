package com.bocom.bbip.gdeupsb.service.impl.watr00;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.action.common.CommThdRspCdeAction;
import com.bocom.bbip.eups.adaptor.ThirdPartyAdaptor;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.euif.component.util.StringUtil;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
/**
 * 银行发起批量扣款处理结果请求
 * 1.银行批量扣款完成后，将结果文件放到FTP服务器
 * 2.发起批量扣款结果请求
 * @author hefengwen
 *
 */
public class SendBatchResultServiceActionWATR00 extends BaseAction {
	
	private static Logger logger = LoggerFactory.getLogger(SendBatchResultServiceActionWATR00.class);

	@Autowired
	@Qualifier("callThdTradeManager")
	ThirdPartyAdaptor callThdTradeManager;
	
	@Override
	public void execute(Context context) throws CoreException,	CoreRuntimeException {
		logger.info("SendBatchResultServiceActionWATR00 start ... ...");
		
		//TODO:执行扣款操作
		
		
		//TODO:将结果文件放到FTP服务器
		
		
		context.setData("type", "Y004");
		context.setData("accountdate", "20150123");
		context.setData("waterno", "JH201501230000000001");//TODO:流水号生成
		context.setData("bankcode", "COMM");
		context.setData("salesdepart", "327103");
		context.setData("salesperson", "327103");
		context.setData("busitime", "20150123104100");
		context.setData("thdRspCde", "0");
		context.setData("zprice", "10000");
		context.setData("months", "3");
		context.setData("operano", "0001");
		context.setData("password", "123123");
		context.setData("md5digest", "0000000");
		
		context.setData("path", "");
		context.setData("filename", "");
		context.setData("filesize", "");
		
		Map<String,Object> thdReturnMessage = callThdTradeManager.trade(context);
		
		if(context.getState().equals(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL)){
			CommThdRspCdeAction cRspCdeAction = new CommThdRspCdeAction();
			String responseCode = cRspCdeAction.getThdRspCde(thdReturnMessage, 	context.getData(ParamKeys.EUPS_BUSS_TYPE).toString());
			logger.info("responseCode:["+responseCode+"]");
			if(Constants.RESPONSE_CODE_SUCC.equals(responseCode)){
				logger.info("SendBatchResultServiceActionWATR00 success!");
				
			}else{
				if(StringUtil.isEmpty(responseCode)){
					responseCode = ErrorCodes.EUPS_THD_RSP_CODE_ERROR;
				}
				throw new CoreException(responseCode);
			}
		}else{
			logger.error("SendBatchResultServiceActionWATR00 return has error!");
			throw new CoreException(ErrorCodes.EUPS_THD_SYS_ERROR);
		}
		logger.info("SendBatchResultServiceActionWATR00 end ... ...");
	}

}
