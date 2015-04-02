package com.bocom.bbip.gdeupsb.strategy.efek.agent;

import java.util.Date;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.action.common.CommThdRspCdeAction;
import com.bocom.bbip.eups.adaptor.ThirdPartyAdaptor;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.service.BGSPServiceAccessObject;
import com.bocom.bbip.service.Result;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
/**
 *银行到供电变更代扣协议业务 
 *@author liyawei
 */
public class UpdateCusAgentServiceAction extends BaseAction {
	@Autowired
    @Qualifier("callThdTradeManager")
    ThirdPartyAdaptor callThdTradeManager;
	@Autowired
	BGSPServiceAccessObject bgspServiceAccessObject;
    private final static Log logger=LogFactory.getLog(UpdateCusAgentServiceAction.class);
   @Override
   public void execute(Context context) throws CoreException,
		CoreRuntimeException {
	   logger.info("=============Start  UpdateCusAgentServiceAction ");
	   
		Date txnDte=(Date)context.getData(ParamKeys.TXN_DTE);
		Date txnTme=DateUtils.parse(context.getData("txnTme").toString());
		String cusAc=context.getData("cusAc").toString();
		context.setData(ParamKeys.CUS_AC, context.getData(GDParamKeys.NEWCUSAC));
		Result editCusAgtResult = bgspServiceAccessObject.callServiceFlatting("maintainAgentCollectAgreement",context.getDataMap());
		
		if(editCusAgtResult.isSuccess()){
				if(context.getData("callThd").toString().equals("callThd")){
						try{
							context.setData(ParamKeys.CUS_AC, cusAc);
							context.setData(ParamKeys.THD_CUS_NO,  context.getData("cusNo"));
							context.setData(ParamKeys.TXN_DTE, DateUtils.format(txnDte,DateUtils.STYLE_yyyyMMdd));
							context.setData(ParamKeys.TXN_TME, DateUtils.format(txnTme,DateUtils.STYLE_HHmmss));
							
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
									                if (GDConstants.SUCCESS_CODE.equals(responseCode)) {
									                    logger.info("The third process response successful.");
									                    context.setData(ParamKeys.TXN_STS, Constants.TXNSTS_SUCCESS);
									                    context.setData(ParamKeys.THD_TXN_STS, Constants.THD_TXNSTS_SUCCESS);
									                    context.setData(ParamKeys.RSP_CDE, GDConstants.SUCCESS_CODE);
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
						}finally{
							context.setData(ParamKeys.TXN_DTE, txnDte);
							context.setData(ParamKeys.TXN_TME, txnTme);
						}
				}
		}
		logger.info("=============End   UpdateCusAgentServiceAction ");
    }
}