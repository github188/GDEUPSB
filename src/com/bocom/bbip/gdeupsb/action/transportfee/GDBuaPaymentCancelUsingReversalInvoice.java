package com.bocom.bbip.gdeupsb.action.transportfee;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.comp.account.AccountService;
import com.bocom.bbip.comp.bua.BUAResult;
import com.bocom.bbip.comp.bua.message.ReversalRequest;
import com.bocom.bbip.comp.bua.service.BUAServiceAccessObject;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GDEupsbTrspFeeInfo;
import com.bocom.bbip.gdeupsb.entity.GDEupsbTrspTxnJnl;
import com.bocom.bbip.gdeupsb.repository.GDEupsbTrspFeeInfoRepository;
import com.bocom.bbip.gdeupsb.repository.GDEupsbTrspTxnJnlRepository;
import com.bocom.bbip.service.Status;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.SystemConfig;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class GDBuaPaymentCancelUsingReversalInvoice extends BaseAction{
	private final static Log logger = LogFactory.getLog(GDBuaPaymentCancelUsingReversalInvoice.class);
	
	@Autowired
	GDEupsbTrspFeeInfoRepository gdEupsbTrspFeeInfoRepository;
	
	@Autowired
	GDEupsbTrspTxnJnlRepository gdEupsbTrspTxnJnlRepository;
	
	
	 public void execute(Context context) throws CoreException, CoreRuntimeException {
		 log.info("GDBuaPaymentCancelUsingReversalInvoiceExt start......");
			if("S".equals(context.getData("ohTxnSt"))){
				return;
			}
//			<Set>PActNo=$TActNo</Set>
//	        <Set>Mask=STRCAT(9,$BBusTyp)</Set>
//	        <Set>CcyTyp=0</Set>
//	        <Set>CashNo=121</Set>
//	        <Set>VchChk=0</Set>
//	        <Set>HTxnCd=@PARA.HTxnCd_C2P</Set>
			context.setData(GDParamKeys.TLOG_NO, context.getData(GDParamKeys.SQN));
			GDEupsbTrspFeeInfo gdEupsbTrspFeeInfo = new GDEupsbTrspFeeInfo();
			//登记退费流水号
			gdEupsbTrspFeeInfo.setThdKey(context.getData(GDParamKeys.THD_KEY).toString());
			gdEupsbTrspFeeInfo.setRvsLog(context.getData(GDParamKeys.SQN).toString());
			gdEupsbTrspFeeInfoRepository.updateThdFeeInfo(gdEupsbTrspFeeInfo);
			
			gdEupsbTrspTxnJnlRepository.insert(BeanUtils.toObject(context.getDataMap(), GDEupsbTrspTxnJnl.class));
	        logger.debug("BBIP process context:" + context);
	        context.setData(ParamKeys.REQUEST_TYPE, Constants.REQUEST_TYPE_REVERSAL);// 单笔代收付记账请求类型，置为“抹账：C”
	        
	        // 抹账交易登记流水，抹账流水号使用原流水号oldTxnSqn
	        log.info("oldTxnSqn is:"+context.getData(ParamKeys.OLD_TXN_SQN));
	        // if(!Constants.CHL_TYPE_THIRD_SYSTEM.equals(context.getData(ParamKeys.CHANNEL))){ 
	        //     context.setData(ParamKeys.OLD_TXN_SQN,context.getData(ParamKeys.SEQUENCE));//自动冲正取原交易流水号作为抹账流水          
	        // }

	        // 取当前sqn
	        context.setData(ParamKeys.REQ_JRN_NO, context.getData(ParamKeys.SEQUENCE));

	        // try {

	        // 判断记账系统类型：若未指定则需要判断
	        Map<String, Object> reqMap = context.getDataMap();
	        String payMde = (String) context.getData(ParamKeys.PAY_MDE);
	        String actSysTyp = (String) context.getData(ParamKeys.ACT_SYS_TYP);
	        String cusAc = (String) context.getData(ParamKeys.CUS_AC);
	        AccountService cardBINService = get(AccountService.class);
	        String cardTyp = null;

	        if (StringUtils.isEmpty(actSysTyp)) {
	            if (Constants.PAY_MDE_4.equals(payMde)) {
	                if (cardBINService.isOurBankCard(cusAc)) {
	                    if (cardBINService.isCreditCard(cusAc)) {
	                        actSysTyp = Constants.ACT_SYS_TYP_2; // 本行贷记卡CPS
	                    } else {
	                        actSysTyp = Constants.ACT_SYS_TYP_0; // 本行借记卡代收付
	                    }
	                } else {
	                    actSysTyp = Constants.ACT_SYS_TYP_1; // 他行卡地方银联
	                }
	            } else {
	                actSysTyp = Constants.ACT_SYS_TYP_0; // 非卡类默认代收付
	            }
	        }
	        log.info("记账系统类型:" + actSysTyp);

	        // 替换请求系统码为本地系统码
	        SystemConfig systemConfig=get(SystemConfig.class);
	        reqMap.put(ParamKeys.REQ_SYS_CDE, systemConfig.getSystemCode());
	        // 无请求时间情况下（如第三方发起），添加请求时间
	        Date reqTme = (Date) reqMap.get(ParamKeys.BBIP_HEAD_REQ_TME);// 服务点输入方式码
	        if (null==reqTme) {
	            reqMap.put(ParamKeys.BBIP_HEAD_REQ_TME, new Date());
	        }

	        // 记账系统代收付
	        if (Constants.ACT_SYS_TYP_0.equals(actSysTyp)) {
	            log.info("call acp system!");
	            cardTyp = Constants.CARD_TYPE_01;

	            ReversalRequest request = reversalKeySetForBua(reqMap);
	            callBuaReversal(BUAServiceAccessObject.ACP_CHN, context, request);
	        }
	        // 记账系统地方银联
	        else if (Constants.ACT_SYS_TYP_1.equals(actSysTyp)) {
	            log.info("call unionpay system!");
	            cardTyp = context.getData(ParamKeys.CARD_TYPE);

	            ReversalRequest request = reversalKeySetForBua(reqMap);
	            callBuaReversal(BUAServiceAccessObject.UNION_CHN, context, request);
	        }
	        // 记账系统CPS
	        else if (Constants.ACT_SYS_TYP_2.equals(actSysTyp)) {
	            log.info("call cps system!");
	            cardTyp = Constants.CARD_TYPE_02;
	            reqMap = context.getDataMap();

	            ReversalRequest request = reversalKeySetForBua(reqMap);
	            callBuaReversal(BUAServiceAccessObject.CPS_CHN, context, request);
	        }
	        context.setData(ParamKeys.CARD_TYPE, cardTyp);
	        context.setData(ParamKeys.ACT_SYS_TYP, actSysTyp);

	        // }

	        // catch (Exception e) {
	        // logger.info("BbipPayment call BBIP has error：" + e.getMessage());
	        // context.setData(ParamKeys.RESPONSE_TYPE, Constants.RESPONSE_TYPE_FAIL);
	        // context.setData(ParamKeys.RESPONSE_CODE, ErrorCodes.TRANSACTION_ERROR_COMM_ERROR);
	        // context.setData(ParamKeys.RESPONSE_MESSAGE, Constants.RESPONSE_MSG_FAIL);
	        // // throw new CoreException(ErrorCodes.TRANSACTION_ERROR_COMM_ERROR);
	        // }
	        
	        if(context.getState().equals(BPState.BUSINESS_PROCESSNIG_STATE_OVERTIME)){
	        	gdEupsbTrspTxnJnlRepository.update(BeanUtils.toObject(context.getDataMap(), GDEupsbTrspTxnJnl.class));
	        	context.setData(ParamKeys.RSP_MSG, "主机交易超时");
	        	throw new CoreRuntimeException(ErrorCodes.TRANSACTION_ERROR_TIMEOUT);
	        }else if(context.getState().equals(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL)){
	        	gdEupsbTrspTxnJnlRepository.update(BeanUtils.toObject(context.getDataMap(), GDEupsbTrspTxnJnl.class));
	        	context.setData(ParamKeys.RSP_MSG, "主机交易成功");    	
	        }else{
	        	gdEupsbTrspTxnJnlRepository.update(BeanUtils.toObject(context.getDataMap(), GDEupsbTrspTxnJnl.class));
	        	context.setData(ParamKeys.RSP_MSG, "主机交易失败");   
	        	throw new CoreRuntimeException(ErrorCodes.TRANSACTION_ERROR_OTHER_ERROR);
	        }
	    }

	    /**
	     * 调用不同渠道进行冲正
	     * 
	     * @param hostOpr
	     * @param context
	     * @param reqMap
	     * @throws CoreException
	     */
	    private void callBuaReversal(String hostOpr, Context context, ReversalRequest request) throws CoreException {
	        logger.info("start callBuaReversal!");
	        Map<String, Object> rspMap = new HashMap<String, Object>();

//	        BUAResult response = get(BUAServiceAccessObject.class).reversal(request, hostOpr);

//	        rspMap = response.getPayload();
//	        logger.info("===========respMap: " + rspMap + "===========");
//	        String hostRspCode = response.getResponseCode();
//
//	        if (Status.SEND_ERROR == response.getStatus()) {
//	            context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
//	            logger.info("business error ,error message is thd return business error");
//	        }
//
//	        if (Status.TIMEOUT == response.getStatus()) {
//	            // 连接错误或等待超时,但不知道是否已上送,这里交易已处于未知状态
//	            context.setState(BPState.BUSINESS_PROCESSNIG_STATE_UNKOWN_FAIL);
//	            logger.info("business error,error message is timeOut or connect error ");
//	        }
//	        if (Status.SYSTEM_ERROR == response.getStatus()) {
//	            context.setState(BPState.BUSINESS_PROCESSNIG_STATE_UNKOWN_FAIL);
//	            logger.info("business error,unknow reason");
//	        } else if (Status.SUCCESS != response.getStatus() && Status.FAIL != response.getStatus()) {
//	            context.setState(BPState.BUSINESS_PROCESSNIG_STATE_UNKOWN_FAIL);
//	            logger.info("business error,business is not success ");
//	        }
//
//	        context.setData(ParamKeys.AGTS_ACCOUNT_JOURNAL_NO, response.getAcoJrnNo());
//	        context.setData(ParamKeys.AGTS_ACCOUNT_VOUCHER_NO, response.getAcoVchNo());
//	        context.setData(ParamKeys.AGTS_ACCOUNT_DATE, response.getAcDte());
//	        context.setData(ParamKeys.AGTS_BBIP_JOURNAL_NO, response.getTxnSqn());
//	        context.setData(ParamKeys.MFM_RSP_MSG, response.getResponseMessage());
//	        logger.info("===========bua payment reversal response: " + rspMap);
//
//	        if (Status.SUCCESS == response.getStatus()) {
//	            logger.info("Call BBIP payment service response successful.");
//	            context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
//	            context.setData(ParamKeys.REVERSE_RESULT_CODE, Constants.RESPONSE_CODE_SUCC_HOST);
//	            logger.info("call host reversal suc!context=" + context.getDataMap());
//	        } else if (Status.FAIL == response.getStatus()) {
//	            context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
//	            context.setData(ParamKeys.REVERSE_RESULT_CODE, hostRspCode);
//	            logger.info("Call BBIP payment service response Failed!!!! response map: " + rspMap);
//	        } else {
//	            logger.info("Call bua payment service unkown error, response is null.");
//	            context.setState(BPState.BUSINESS_PROCESSNIG_STATE_UNKOWN_FAIL);
//	        }
	        context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
	    }

	    /**
	     * cps沖正设置
	     * 
	     * @param reqMap
	     */
	    private ReversalRequest reversalKeySetForBua(Map<String, Object> reqMap) {
	        ReversalRequest request = new ReversalRequest();
	        request.setReqSysCde((String) reqMap.get(ParamKeys.REQ_SYS_CDE));
	        request.setOldReqJrnNo((String) reqMap.get(ParamKeys.OLD_TXN_SQN));
	        request.setTraceNo((String) reqMap.get(ParamKeys.TRACE_NO));

	        return request;
	    }
}
