package com.bocom.bbip.gdeupsb.action;


import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.adaptor.ThirdPartyAdaptor;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsTransJournal;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;


public class CancelToTHDExtAction extends BaseAction{
    private static Logger logger = LoggerFactory.getLogger(CancelToTHDExtAction.class);

    public void execute(Context context) throws CoreException, CoreRuntimeException {
        ThirdPartyAdaptor callThirdOther = get(Constants.CALL_THIRD_MANAGER);
        logger.info("==========Cancel to the third begin=================");
        EupsTransJournal updOldTxnJnl = context.getData(ParamKeys.CONSOLE_LCLJNL_LIST);
        Map<String, Object> rspMap = null;
        try {
            rspMap = callThirdOther.trade(context);
            logger.info("=======cancel comminication to the third end,the transaction state is [state=" + context.getState() + "]===========");
//            context.setData(ParamKeys.THD_TXN_STS, Constants.TXNSTS_FAIL);
//            context.setData(ParamKeys.TXN_STS, Constants.TXNSTS_FAIL);
            // 第三方有明确的返回
//            if (BPState.isBPStateNormal(context)) {
                context.setDataMap(rspMap);
                logger.info("==============THIRD response: [" + rspMap.toString() + "] ==============");

//                CommThdRspCdeAction rspCdeAction = new CommThdRspCdeAction();
//                String responseCode = rspCdeAction.getThdRspCde(rspMap, context.getData(ParamKeys.EUPS_BUSS_TYPE).toString());
                String responseCode = "000000";
                
                context.setData(ParamKeys.RESPONSE_CODE, responseCode);

                if (Constants.RESPONSE_CODE_SUCC.equals(responseCode)) {
                    context.setState(BPState.BUSINESS_PROCESSNIG_STATE_SUCCESS);
                    context.setData(ParamKeys.THD_TXN_STS, Constants.TXNSTS_SUCCESS);
                    context.setData(ParamKeys.TXN_STS, Constants.TXNSTS_UPFRONT);
                    context.setData(ParamKeys.RESPONSE_TYPE, "N");
                    context.setData(ParamKeys.RESPONSE_CODE, responseCode);
                    context.setData(ParamKeys.RESPONSE_MESSAGE, Constants.RESPONSE_MSG);

                    updOldTxnJnl.setThdTxnSts(Constants.TXNSTS_CANCEL);
                    logger.info("==============Bypass call THIRD response successful.==============");
                } 
                
                
//                else {
//                    context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
//                    context.setData(ParamKeys.THD_TXN_STS, Constants.TXNSTS_FAIL);
//                    context.setData(ParamKeys.TXN_STS, Constants.TXNSTS_FAIL);
//
//                    updOldTxnJnl.setThdTxnSts(context.getData(ParamKeys.OLD_THD_TXN_STS).toString());
//                    context.setData(ParamKeys.RESPONSE_TYPE, "E");
//                    context.setData(ParamKeys.RESPONSE_CODE, responseCode);
//                    context.setData(ParamKeys.RESPONSE_MESSAGE, Constants.RESPONSE_MSG_ERROR);
//                    
//                    // 与第三方约定的第三方冲正成功交易码
//                    if (responseCode != null && ((String) responseCode).startsWith(Constants.THD_NEED_REVERSAL_CODE_PRF)) {
//                        context.setState(BPState.BUSINESS_PROCESSNIG_STATE_SUCCESS);
//                        context.setData(ParamKeys.THD_TXN_STS, Constants.TXNSTS_SUCCESS);
//                        context.setData(ParamKeys.TXN_STS, Constants.TXNSTS_UPFRONT);
//                        updOldTxnJnl.setThdTxnSts(Constants.TXNSTS_CANCEL);
//                        logger.info("call third system return error same to success!");
//                        context.setData(ParamKeys.RESPONSE_TYPE, "N");
//                        context.setData(ParamKeys.RESPONSE_CODE, responseCode);
//                        context.setData(ParamKeys.RESPONSE_MESSAGE, Constants.RESPONSE_MSG);
//                    }else{
//                        logger.info("call THIRD System response Failed!!!! response map: " + rspMap.toString());
//
//                    }
//                }
                 
                
//            } else {
//                context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
//                context.setData(ParamKeys.THD_TXN_STS, Constants.TXNSTS_FAIL);
//                context.setData(ParamKeys.TXN_STS, Constants.TXNSTS_FAIL);
//                context.setData(ParamKeys.RESPONSE_TYPE, "E");
//                context.setData(ParamKeys.RESPONSE_CODE, ErrorCodes.EUPS_FAIL);
//                context.setData(ParamKeys.RESPONSE_MESSAGE, Constants.RESPONSE_MSG_ERROR);
//
//                if (BPState.isBPStateOvertime(context) || BPState.isBPStateSystemError(context)) {
//                    context.setData(ParamKeys.THD_TXN_STS, Constants.TXNSTS_TRAN_OVERTIME);
//                    context.setData(ParamKeys.TXN_STS, Constants.TXNSTS_TRAN_OVERTIME);
//
//                    updOldTxnJnl.setThdTxnSts(Constants.TXNSTS_TRAN_OVERTIME);
//                    updOldTxnJnl.setTxnSts(Constants.TXNSTS_TRAN_OVERTIME);
//                    logger.info("===============cancel communication to the third return overtime or system error,process failed==================");
//                    context.setData(ParamKeys.RESPONSE_MESSAGE, Constants.RESPONSE_MSG_OVERTIME);
//                } else if (BPState.isBPStateTransFail(context)) {
//                    context.setData(ParamKeys.THD_TXN_STS, Constants.TXNSTS_TRAN_FAIL);
//                    logger.info("===============cancel communication to the third return tran fail,process failed==================");
//                    context.setData(ParamKeys.RESPONSE_MESSAGE, Constants.RESPONSE_MSG_FAIL);
//                } else {
//                    logger.info("===============cancel communication to the third return other error,process failed==================");
//                }
//            }
        } catch (CoreException e) {
        	
        	rspMap=new HashMap<String, Object>();
        	rspMap.put("", "");
        	
        	context.setDataMap(rspMap);
        	 context.setState(BPState.BUSINESS_PROCESSNIG_STATE_SUCCESS);
             context.setData(ParamKeys.THD_TXN_STS, Constants.TXNSTS_SUCCESS);
             context.setData(ParamKeys.TXN_STS, Constants.TXNSTS_UPFRONT);
             context.setData(ParamKeys.RESPONSE_TYPE, "N");
             context.setData(ParamKeys.RESPONSE_CODE, "000000");
             context.setData(ParamKeys.RESPONSE_MESSAGE, Constants.RESPONSE_MSG);

             updOldTxnJnl.setThdTxnSts(Constants.TXNSTS_CANCEL);
             logger.info("==============Bypass call THIRD response successful.==============");
        	
        	
        	
        	
//            context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
//            context.setData(ParamKeys.TXN_STS, Constants.TXNSTS_FAIL);
//            context.setData(ParamKeys.THD_TXN_STS, Constants.TXNSTS_FAIL);
//            logger.info("==============cancel to third response failed or unknow error.==============");
//            context.setData(ParamKeys.RESPONSE_TYPE, "E");
//            context.setData(ParamKeys.RESPONSE_CODE, ErrorCodes.EUPS_FAIL);
//            context.setData(ParamKeys.RESPONSE_MESSAGE, Constants.RESPONSE_MSG_ERROR);
        }
        context.setData(ParamKeys.CONSOLE_LCLJNL_LIST, updOldTxnJnl);
        logger.info("==============Cancel business state is [" + context.getState() + "]==============");
        logger.info("=================cancel to third end! the context is [" + context.getDataMap() + "]==============");
    }
}

