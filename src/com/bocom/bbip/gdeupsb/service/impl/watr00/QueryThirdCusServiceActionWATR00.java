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
import com.bocom.jump.bp.service.id.seq.StepSequenceFactory;

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
		context.setData("accountdate", DateUtils.format((Date)context.getData(ParamKeys.AC_DATE), DateUtils.STYLE_yyyyMMdd));
		
		StepSequenceFactory s = context.getService("logNoService");
		String logNo = s.create().toString();
		context.setData("waterno", "JH"+logNo);//流水号生成
		
		context.setData("bankcode", "JT");
		context.setData("salesdepart",((String)context.getData(ParamKeys.BR)).substring(2, 8));
		context.setData("salesperson", ((String)context.getData(ParamKeys.TELLER)).substring(3));
		context.setData("busitime", DateUtils.format(new Date(),DateUtils.STYLE_yyyyMMddHHmmss));
		context.setData("thdRspCde", "0");
		context.setData("zprice", "");
		context.setData("months", "");
		context.setData("operano", "");
		context.setData("password", "        ");
		context.setData("md5digest", " ");
		
		context.setData("hno", context.getData("thdCusNo"));
		
		Map<String,Object> thdReturnMessage = callThdTradeManager.trade(context);
		
		if(context.getState().equals(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL)){
			CommThdRspCdeAction cRspCdeAction = new CommThdRspCdeAction();
			String responseCode = cRspCdeAction.getThdRspCde(thdReturnMessage, 	context.getData(ParamKeys.EUPS_BUSS_TYPE).toString());
			logger.info("responseCode:["+responseCode+"]");
			if(Constants.RESPONSE_CODE_SUCC.equals(responseCode)){
				logger.info("QueryThirdCusServiceActionWATR00 success!");
				context.setDataMap(thdReturnMessage);
				String hno = context.getData("hno");
				String month = context.getData("month");
				String name = context.getData("name");
				String addr = context.getData("addr");
				String newaddr = context.getData("newaddr");
				logger.info("thdCusNo:["+hno+"]month:["+month+"]thdCusNme:["+name+"]address:["+addr+"]newAddress:["+newaddr+"]");
				context.setData("thdCusNo", hno);
				context.setData("month", month);
				context.setData("thdCusNme", name);
				context.setData("addr", addr);
				context.setData("newaddr", newaddr);
				
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
