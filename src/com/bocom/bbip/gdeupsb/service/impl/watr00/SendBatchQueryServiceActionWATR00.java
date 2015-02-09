package com.bocom.bbip.gdeupsb.service.impl.watr00;

import java.util.Date;
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
import com.bocom.bbip.utils.DateUtils;
import com.bocom.euif.component.util.StringUtil;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
import com.bocom.jump.bp.service.id.seq.StepSequenceFactory;
/**
 * 银行发起批量扣款查询请求
 * 1.银行发起批量扣款查询请求，第三方返回应答报文
 * 2.第三方将扣款明细文件放到FTP服务器，发起批量扣款请求，银行返回应答报文
 * 3.银行扣款完成后，将扣款结果文件放到FTP服务器，发起批量扣款结果请求，第三方返回应答报文
 * @author hefengwen
 *
 */
public class SendBatchQueryServiceActionWATR00 extends BaseAction {
	
	private static Logger logger = LoggerFactory.getLogger(SendBatchQueryServiceActionWATR00.class);

	@Autowired
	@Qualifier("callThdTradeManager")
	ThirdPartyAdaptor callThdTradeManager;
	
	@Override
	public void execute(Context context) throws CoreException,	CoreRuntimeException {
		logger.info("SendBatchQueryServiceActionWATR00 start ... ...");
//		context.setData("type", "Y011");
//		context.setData("accountdate", "20150123");
//		context.setData("waterno", "JH201501230000000001");//TODO:流水号生成
//		context.setData("bankcode", "COMM");
//		context.setData("salesdepart", "327103");
//		context.setData("salesperson", "327103");
//		context.setData("busitime", "20150123104100");
//		context.setData("thdRspCde", "0");
//		context.setData("zprice", "10000");
//		context.setData("months", "3");
//		context.setData("operano", "0001");
//		context.setData("password", "123123");
//		context.setData("md5digest", "0000000");
		// 外发第三方接口字段赋值
		context.setData("type", "Y011");
		context.setData("accountdate", DateUtils.format((Date)context.getData(ParamKeys.AC_DATE), DateUtils.STYLE_yyyyMMdd));
		
		StepSequenceFactory s = context.getService("logNoService");
		String logNo = s.create().toString();
		context.setData("waterno", "JH"+logNo);//流水号生成
		
		context.setData("bankcode", "JT");
		context.setData("salesdepart",context.getData(ParamKeys.BR));
		context.setData("salesperson", ((String)context.getData(ParamKeys.TELLER)).substring(4, 7));
		context.setData("busitime", DateUtils.format(new Date(),DateUtils.STYLE_yyyyMMddHHmmss));
		context.setData("thdRspCde", "0");
		context.setData("zprice", "");
		context.setData("months", "");
		context.setData("operano", "");
		context.setData("password", "        ");
		context.setData("md5digest", " ");
		
		Map<String,Object> thdReturnMessage = callThdTradeManager.trade(context);
		
		if(context.getState().equals(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL)){
			CommThdRspCdeAction cRspCdeAction = new CommThdRspCdeAction();
			String responseCode = cRspCdeAction.getThdRspCde(thdReturnMessage, 	context.getData(ParamKeys.EUPS_BUSS_TYPE).toString());
			logger.info("responseCode:["+responseCode+"]");
			if(Constants.RESPONSE_CODE_SUCC.equals(responseCode)){
				logger.info("SendBatchQueryServiceActionWATR00 success!");
				
			}else{
				if(StringUtil.isEmpty(responseCode)){
					responseCode = ErrorCodes.EUPS_THD_RSP_CODE_ERROR;
				}
				throw new CoreException(responseCode);
			}
		}else{
			logger.error("SendBatchQueryServiceActionWATR00 return has error!");
			throw new CoreException(ErrorCodes.EUPS_THD_SYS_ERROR);
		}
		logger.info("SendBatchQueryServiceActionWATR00 end ... ...");
	}

}
