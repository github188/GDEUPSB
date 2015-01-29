package com.bocom.bbip.gdeupsb.service.impl.elec02;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.bocom.bbip.eups.action.common.CommThdRspCdeAction;
import com.bocom.bbip.eups.adaptor.ThirdPartyAdaptor;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.spi.service.online.QueryDealService;
import com.bocom.bbip.eups.spi.vo.CommHeadDomain;
import com.bocom.bbip.eups.spi.vo.PreQryDomain;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.euif.component.util.StringUtil;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

/**
 * 查询欠费信息
 * @author hefengwen
 *
 */
public class QueryDealServiceActionELEC02 implements QueryDealService {
	
	private static Logger logger = LoggerFactory.getLogger(QueryDealServiceActionELEC02.class);

	@Autowired
	@Qualifier("callThdTradeManager")
	ThirdPartyAdaptor callThdTradeManager;
	/**
	 * 查询欠费信息前处理，暂不需实现
	 */
	@Override
	public Map<String, Object> prepareQueryDeal(CommHeadDomain commheaddomain,PreQryDomain preqrydomain, Context context) throws CoreException {
		logger.info("QueryDealServiceActionELEC02 prepareQueryDeal start ... ...");
		
		logger.info("QueryDealServiceActionELEC02 prepareQueryDeal end ... ...");
		return null;
	}

	@Override
	public Map<String, Object> qryDeal(CommHeadDomain commheaddomain,PreQryDomain preqrydomain, Context context) throws CoreException {
		logger.info("QueryDealServiceActionELEC02 qryDeal start ... ...");
		logger.info("SQN:"+context.getData("sqn"));
		Map<String,Object> callThdMap = new HashMap<String,Object>();//拼接外发第三方Map
		
		String date = DateUtils.format(new Date(), "yyyyMMdd");
		String time = DateUtils.format(new Date(),"yyyyMMddHHmmss");
		context.setData("AppTradeCode", "10");//业务交易码
		context.setData("StartAddr", "");
		context.setData("DestAddr", "");
		context.setData("MesgID", "");
		context.setData("WorkDate", date);
		context.setData("SendTime", time);
		context.setData("mesgPRI", "9");
		context.setData("recordNum", "0");
		context.setData("FileName", "");
		context.setData("zipFlag", "0");
		
		context.setData("WTC", "");
		context.setData("TMN", context.getData("brno"));
		context.setData("WD0", date);
		context.setData("CLZ", "");
		context.setData("ECD", "");
		context.setData("8ED", context.getData("rsvFld1"));
		context.setData("JFH", context.getData("thdCusNo"));
		context.setData("FYM", context.getData("rsvFld2"));
		context.setData("QFG", context.getData("rsvFld3"));
		context.setData("STO", context.getData("tlr"));
		
		context.setData("owePrd", context.getData("rsvFld2"));
		context.setData("bakFld1", context.getData("rsvFld1"));
		context.setData("bakFld3", context.getData("rsvFld3"));
		
		Map<String,Object> thdReturnMessage = callThdTradeManager.trade(context);
		
		if(context.getState().equals(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL)){
			CommThdRspCdeAction cRspCdeAction = new CommThdRspCdeAction();
			String responseCode = cRspCdeAction.getThdRspCde(thdReturnMessage, 	context.getData(ParamKeys.EUPS_BUSS_TYPE).toString());
			logger.info("responseCode:["+responseCode+"]");
			if(Constants.RESPONSE_CODE_SUCC.equals(responseCode)){
				logger.info("QryDealStrategyActionELEC00 success!");
			}else{
				if(StringUtil.isEmpty(responseCode)){
					responseCode = ErrorCodes.EUPS_THD_RSP_CODE_ERROR;
				}
				throw new CoreException(responseCode);
			}
			String txnAmt = thdReturnMessage.get("QFM").toString().trim();
			callThdMap.put(ParamKeys.OWE_FEE_AMT, txnAmt);
			context.setData(ParamKeys.OWE_FEE_AMT, txnAmt);
		}else{
			logger.error("QueryDealServiceImplELEC00 return has error!");
			throw new CoreException(ErrorCodes.EUPS_THD_SYS_ERROR);
		}
		logger.info("QueryDealServiceActionELEC02 qryDeal end ... ...");
		return null;
	}

}
