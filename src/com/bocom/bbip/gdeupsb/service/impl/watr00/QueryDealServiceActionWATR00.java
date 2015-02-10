package com.bocom.bbip.gdeupsb.service.impl.watr00;

import java.util.Date;
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
import com.bocom.jump.bp.service.id.seq.StepSequenceFactory;

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
		// 外发第三方接口字段赋值
		context.setData("type", "Y001");
		context.setData("accountdate", DateUtils.format((Date)context.getData(ParamKeys.AC_DATE), DateUtils.STYLE_yyyyMMdd));
		
		StepSequenceFactory s = context.getService("logNoService");
		String logNo = s.create().toString();
		context.setData("waterno", "JH"+logNo);//流水号生成
		
		context.setData("bankcode", "JT");
		context.setData("salesdepart",((String)context.getData(ParamKeys.BR)).substring(2, 8));
		context.setData("salesperson", ((String)context.getData(ParamKeys.TELLER)).substring(4, 7));
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
				logger.info("QryDealStrategyActionWATR00 success!");
				context.setDataMap(thdReturnMessage);
//				String txnAmt = thdReturnMessage.get("zprice").toString().trim();
//				context.setData(ParamKeys.OWE_FEE_AMT, txnAmt);
			}else{
				if(StringUtil.isEmpty(responseCode)){
					responseCode = ErrorCodes.EUPS_THD_RSP_CODE_ERROR;
				}
				throw new CoreException(responseCode);
			}
		}else{
			logger.error("QryDealStrategyActionWATR00 return has error!");
			throw new CoreException(ErrorCodes.EUPS_THD_SYS_ERROR);
		}
		logger.info("QueryDealServiceActionWATR00 qryDeal end ... ...");
		return null;
	}

}
