package com.bocom.bbip.gdeupsb.service.impl.watr00;

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
import com.bocom.euif.component.util.StringUtil;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

/**
 * 查询欠费信息
 * @author hefengwen
 *
 */
public class QueryDealServiceActionWATR00 implements QueryDealService {
	
	private static Logger logger = LoggerFactory.getLogger(QueryDealServiceActionWATR00.class);

	@Autowired
	@Qualifier("callThdTradeManager")
	ThirdPartyAdaptor callThdTradeManager;
	/**
	 * 查询欠费信息前处理，暂不需实现
	 */
	@Override
	public Map<String, Object> prepareQueryDeal(CommHeadDomain commheaddomain,PreQryDomain preqrydomain, Context context) throws CoreException {
		logger.info("QueryDealServiceActionWATR00 prepareQueryDeal start ... ...");
		
		logger.info("QueryDealServiceActionWATR00 prepareQueryDeal end ... ...");
		return null;
	}

	@Override
	public Map<String, Object> qryDeal(CommHeadDomain commheaddomain,PreQryDomain preqrydomain, Context context) throws CoreException {
		logger.info("QueryDealServiceActionWATR00 qryDeal start ... ...");
		// TODO:外发第三方接口字段赋值
		context.setData("type", "Y001");
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
				logger.info("QryDealStrategyActionWATR00 success!");
			}else{
				if(StringUtil.isEmpty(responseCode)){
					responseCode = ErrorCodes.EUPS_THD_RSP_CODE_ERROR;
				}
				throw new CoreException(responseCode);
			}
			String txnAmt = thdReturnMessage.get("zprice").toString().trim();
			context.setData(ParamKeys.OWE_FEE_AMT, txnAmt);
		}else{
			logger.error("QryDealStrategyActionWATR00 return has error!");
			throw new CoreException(ErrorCodes.EUPS_THD_SYS_ERROR);
		}
		logger.info("QueryDealServiceActionWATR00 qryDeal end ... ...");
		return null;
	}

}
