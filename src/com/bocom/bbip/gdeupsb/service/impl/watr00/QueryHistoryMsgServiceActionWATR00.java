package com.bocom.bbip.gdeupsb.service.impl.watr00;

import java.util.Date;
import java.util.List;
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
 * 汕头水费历史水费查询打印
 * @author hefengwen
 *
 */
public class QueryHistoryMsgServiceActionWATR00 extends BaseAction  {
	
	private static Logger logger = LoggerFactory.getLogger(QueryHistoryMsgServiceActionWATR00.class);

	@Autowired
	@Qualifier("callThdTradeManager")
	ThirdPartyAdaptor callThdTradeManager;

	@SuppressWarnings("unchecked")
	@Override
	public void execute(Context context) throws CoreException,	CoreRuntimeException {
		logger.info("QueryAndPrintHistoryMsgServiceActionWATR00 execute start ... ...");
		// TODO:外发第三方接口字段赋值
				context.setData("type", "Y012");
				context.setData("accountdate", DateUtils.format((Date)context.getData(ParamKeys.AC_DATE), DateUtils.STYLE_yyyyMMdd));
				
				StepSequenceFactory s = context.getService("logNoService");
				String logNo = s.create().toString();
				context.setData("waterno", "JH"+logNo);//流水号生成
				
				context.setData("bankcode", "交行");
				context.setData("salesdepart",((String)context.getData(ParamKeys.BR)).substring(2, 8));
				context.setData("salesperson", ((String)context.getData(ParamKeys.TELLER)).substring(3));
				context.setData("busitime", DateUtils.format(new Date(),DateUtils.STYLE_yyyyMMddHHmmss));
				context.setData("thdRspCde", "");
				context.setData("zprice", "0");
				context.setData("months", "0");
				context.setData("operano", "");
				context.setData("password", "        ");
				context.setData("md5digest", " ");
				
				context.setData("hno", context.getData("thdCusNo"));
				context.setData("starttime", context.getData("beginDate"));
				context.setData("endtime", context.getData("endDate"));
				
				Map<String,Object> thdReturnMessage = callThdTradeManager.trade(context);
				
				if(context.getState().equals(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL)){
					CommThdRspCdeAction cRspCdeAction = new CommThdRspCdeAction();
					String responseCode = cRspCdeAction.getThdRspCde(thdReturnMessage, 	context.getData(ParamKeys.EUPS_BUSS_TYPE).toString());
					logger.info("responseCode:["+responseCode+"]");
					if(Constants.RESPONSE_CODE_SUCC.equals(responseCode)){
						logger.info("QueryAndPrintHistoryMsgServiceActionWATR00 success!");
						
						List<Map<String,Object>> rspList = (List<Map<String, Object>>) thdReturnMessage.get("rspList");
						if(rspList==null||rspList.size()==0){
							//TODO:无记录，报错返回
							logger.info("eups query null!");
				        	throw new CoreException(ErrorCodes.EUPS_QUERY_NO_DATA);
						}
				        context.setData("historyFeeList", rspList);
				        context.setData("retNum", rspList.size());
					}else{
						if(StringUtil.isEmpty(responseCode)){
							responseCode = ErrorCodes.EUPS_THD_RSP_CODE_ERROR;
						}
						throw new CoreException(responseCode);
					}
				}else{
					logger.error("QueryAndPrintHistoryMsgServiceActionWATR00 return has error!");
					throw new CoreException(ErrorCodes.EUPS_THD_SYS_ERROR);
				}
		logger.info("QueryAndPrintHistoryMsgServiceActionWATR00 execute end ... ...");
	}

}
