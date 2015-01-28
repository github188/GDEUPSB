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

/**
 * 汕头水费户号查询
 * @author hefengwen
 *
 */
public class QueryThirdCusServiceActionWATR00 extends BaseAction{
	
	private static Logger logger = LoggerFactory.getLogger(QueryThirdCusServiceActionWATR00.class);

	@Autowired
	@Qualifier("callThdTradeManager")
	ThirdPartyAdaptor callThdTradeManager;
	@Override
	public void execute(Context context) throws CoreException{
		logger.info("QueryThirdCusServiceActionWATR00 execute start ... ...");
		
		logger.info("thdCusNo:["+context.getData("thdCusNo")+"]");
		context.setData("type", "Y015");
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
		
		context.setData("hno", context.getData("thdCusNo"));
		
		Map<String,Object> thdReturnMessage = callThdTradeManager.trade(context);
		
		if(context.getState().equals(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL)){
			CommThdRspCdeAction cRspCdeAction = new CommThdRspCdeAction();
			String responseCode = cRspCdeAction.getThdRspCde(thdReturnMessage, 	context.getData(ParamKeys.EUPS_BUSS_TYPE).toString());
			logger.info("responseCode:["+responseCode+"]");
			if(Constants.RESPONSE_CODE_SUCC.equals(responseCode)){
				logger.info("QueryThirdCusServiceActionWATR00 success!");
				
				String hno = (String) thdReturnMessage.get("hno");
				String month = (String) thdReturnMessage.get("month");
				String name = (String) thdReturnMessage.get("name");
				String addr = (String) thdReturnMessage.get("addr");
				String newaddr = (String) thdReturnMessage.get("newaddr");
				logger.info("thdCusNo:["+hno+"]month:["+month+"]thdCusNme:["+name+"]address:["+addr+"]newAddress:["+newaddr+"]");
				context.setData("thdCusNo", hno);
				context.setData("month", month);
				context.setData("thdCusNme", name);
				context.setData("address", addr);
				context.setData("newAddress", newaddr);
				
			}else{
				if(StringUtil.isEmpty(responseCode)){
					responseCode = ErrorCodes.EUPS_THD_RSP_CODE_ERROR;
				}
				throw new CoreException(responseCode);
			}
		}else{
			logger.error("QueryThirdCusServiceActionWATR00 return has error!");
			throw new CoreException(ErrorCodes.EUPS_THD_SYS_ERROR);
		}
		
		logger.info("QueryThirdCusServiceActionWATR00 execute end ... ...");
	}

}
