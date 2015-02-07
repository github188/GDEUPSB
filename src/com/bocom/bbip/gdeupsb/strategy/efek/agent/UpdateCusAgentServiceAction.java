package com.bocom.bbip.gdeupsb.strategy.efek.agent;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.bocom.bbip.eups.action.common.CommThdRspCdeAction;
import com.bocom.bbip.eups.adaptor.ThirdPartyAdaptor;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.spi.service.agent.CommUpdateCusAgentService;
import com.bocom.bbip.eups.spi.vo.CusAgentCollectDomain;
import com.bocom.bbip.eups.spi.vo.CustomerDomain;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
/**
 *银行到供电变更代扣协议业务 
 */
public class UpdateCusAgentServiceAction implements CommUpdateCusAgentService{
	@Autowired
    @Qualifier("callThdTradeManager")
    ThirdPartyAdaptor callThdTradeManager;
    private final static Log logger=LogFactory.getLog(UpdateCusAgentServiceAction.class);
   @Override
    public Map<String, Object> preUpdateCusAgent(CustomerDomain customerdomain,
		List<CusAgentCollectDomain> list, Context context)
		throws CoreException {
	   logger.info("=============Start  UpdateCusAgentServiceAction ");
		    Date txnDte=DateUtils.parse(DateUtils.formatAsSimpleDate(new Date()));
			context.setData(ParamKeys.TXN_DAT, txnDte);
			if(context.getData(GDParamKeys.NET_NAME) !=null){
				context.setData(ParamKeys.BR, context.getData(GDParamKeys.NET_NAME));
		}
			context.setData(GDParamKeys.AGT_STS, "2");
			//TODO 核对方向
			context.setData("ChkFlg", "U");
			context.setData(GDParamKeys.SVRCOD, "30");
			
			constantOfSoapUI(context);
	return null;
}
    /**
     * 外发第三方
     */
    @Override
    public Map<String, Object> callThd(CustomerDomain arg0,
    		List<CusAgentCollectDomain> arg1, Context context)
    		throws CoreException {
		logger.info("===============Start   UpdateCusAgentServiceAction   callThdOther");
		
		try{
			Map<String, Object> rspMap = callThdTradeManager.trade(context);
			
				if(BPState.isBPStateNormal(context)){
						if(null !=rspMap){
							 	context.setDataMap(rspMap);
				                context.setData(ParamKeys.THIRD_RETURN_MESSAGE, rspMap);
				                //第三方返回码
				                CommThdRspCdeAction rspCdeAction = new CommThdRspCdeAction();
				                String responseCode = rspCdeAction.getThdRspCde(rspMap, context.getData(ParamKeys.EUPS_BUSS_TYPE).toString());
				                logger.info("third response code="+responseCode);
				                if(StringUtils.isEmpty(responseCode)){
				                	responseCode=ErrorCodes.EUPS_THD_SYS_ERROR;
				                }
				                context.setData(ParamKeys.RESPONSE_CODE, responseCode);
				                
				             // 第三方交易成功
					                if (Constants.RESPONSE_CODE_SUCC.equals(responseCode)) {
					                    logger.info("The third process response successful.");
					                    context.setData(ParamKeys.TXN_STS, Constants.TXNSTS_SUCCESS);
					                    context.setData(ParamKeys.THD_TXN_STS, Constants.THD_TXNSTS_SUCCESS);
					                    context.setData(ParamKeys.RSP_CDE, Constants.HOST_RESPONSE_CODE_SUCC);
					                    context.setData(ParamKeys.RSP_MSG, "交易成功");
					                }else if(BPState.isBPStateReversalFail(context)){
					                	context.setData(ParamKeys.THD_TXN_STS,Constants.THD_TXNSTS_FAIL);
					                	context.setData(GDParamKeys.MSGTYP, "E");
					                	context.setData(ParamKeys.RSP_CDE, "EFE999");
					                	context.setData(ParamKeys.RSP_MSG, "交易失败");
					                }else if(BPState.isBPStateOvertime(context)){
					                	context.setData(ParamKeys.THD_TXN_STS,Constants.THD_TXNSTS_FAIL);
					                	context.setData(GDParamKeys.MSGTYP, "E");
					                	context.setData(ParamKeys.RSP_CDE, "EFE999");
					                	context.setData(ParamKeys.RSP_MSG, "交易超时");
					                }else if(BPState.isBPStateSystemError(context)){
					                	context.setData(ParamKeys.THD_TXN_STS,Constants.THD_TXNSTS_FAIL);
					                	context.setData(GDParamKeys.MSGTYP, "E");
					                	context.setData(ParamKeys.RSP_CDE, "EFE999");
					                	context.setData(ParamKeys.RSP_MSG, "系统错误");
					                }else if(BPState.isBPStateTransFail(context)){
					                	context.setData(ParamKeys.THD_TXN_STS,Constants.THD_TXNSTS_FAIL);
					                	context.setData(GDParamKeys.MSGTYP, "E");
					                	context.setData(ParamKeys.RSP_CDE, "EFE999");
					                	context.setData(ParamKeys.RSP_MSG, "发送失败");
					                }else{
					                	context.setData(ParamKeys.THD_TXN_STS,Constants.THD_TXNSTS_FAIL);
					                	context.setData(GDParamKeys.MSGTYP, "E");
					                	context.setData(ParamKeys.RSP_CDE, "EFE999");
					                	context.setData(ParamKeys.RSP_MSG, "交易失败，其他未知情况");
					                }
						}
				}else{
						logger.info("~~~~~~~~~~~发送失败");
					    context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
		                context.setData(ParamKeys.TXN_STS, Constants.TXNSTS_REVERSE);
		                context.setData(ParamKeys.THD_TXN_STS, Constants.TXNSTS_FAIL);
		                context.setData(ParamKeys.RESPONSE_TYPE, Constants.RESPONSE_TYPE_FAIL);
		                context.setData(ParamKeys.RESPONSE_CODE, ErrorCodes.EUPS_THD_SYS_ERROR);
		                context.setData(ParamKeys.RESPONSE_MESSAGE, Constants.RESPONSE_MSG_FAIL);
		                context.setData(ParamKeys.THD_RSP_MSG,Constants.RESPONSE_MSG_FAIL);
		                throw new CoreException("发送失败");
				}
		}catch(CoreException e){
			logger.info("Bypass call THIRD response failed or unknow error.");
			context.setData(ParamKeys.TXN_STS, Constants.TXNSTS_REVERSE);
			context.setData(ParamKeys.THD_TXN_STS, Constants.TXNSTS_FAIL);
			context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
		}
    	
    	return null;
    }
    /**
	 *报文信息 
	*/
	public void constantOfSoapUI(Context context){
			//报文头常量
			context.setData(GDParamKeys.TREATY_VERSION, "1.0.0");//协议版本
			context.setData(GDParamKeys.TRADE_PERSON_IDENTIFY, "301_030600");//交易人标识
			context.setData(GDParamKeys.BAG_TYPE, "0");//数据包类型
			context.setData(GDParamKeys.TRADE_START, "301");//交易发起方
			
			context.setData(GDParamKeys.TRADE_SEND_DATE,DateUtils.format(new Date(),DateUtils.STYLE_yyyyMMdd));//交易发送日期
			context.setData(GDParamKeys.TRADE_SEND_TIME, DateUtils.formatAsHHmmss(new Date()));//交易发送时间
			context.setData(GDParamKeys.TRADE_PRIORITY, "2");//交易优先
			context.setData(GDParamKeys.REDUCE_SIGN, "0");//压缩标志
			context.setData(GDParamKeys.TRADE_RETURN_CODE, "00");//交易返回代码
			
					context.setData(GDParamKeys.NET_NAME, "@BCFG.BrNam");//网点名称
					context.setData(GDParamKeys.SECRETKEY_INDEX, "0");//密钥索引
					context.setData(GDParamKeys.SECRETKEY_INIT, "");//密钥初始向量
					context.setData(GDParamKeys.TRADE_RECEIVE, "030600");//交易接收方
					context.setData(GDParamKeys.TRADE_SOURCE_ADD, "");//交易源地址
					context.setData(GDParamKeys.TRADE_AIM_ADD, "");//交易目标地址
		}
}

