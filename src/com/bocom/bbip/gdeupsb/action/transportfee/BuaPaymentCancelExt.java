package com.bocom.bbip.gdeupsb.action.transportfee;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bocom.bbip.comp.account.AccountService;
import com.bocom.bbip.comp.bua.BUAResult;
import com.bocom.bbip.comp.bua.message.AccountInfo;
import com.bocom.bbip.comp.bua.message.CancelPaymentRequest;
import com.bocom.bbip.comp.bua.service.BUAServiceAccessObject;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.action.eupspayment.BuaPaymentCancel;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsActSysPara;
import com.bocom.bbip.eups.repository.EupsActSysParaRepository;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.utils.CollectionUtils;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.SystemConfig;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

/**
 * CPS消费抹账组件
 * 
 * @version 1.0.0,2015-5-22
 * @author wang.hq
 * @since 1.0.0
 */

public class BuaPaymentCancelExt extends BaseAction{
	 private static final Log logger = LogFactory.getLog(BuaPaymentCancel.class);

	    public void execute(Context context) throws CoreException, CoreRuntimeException {
	        logger.info("#########Enter in BuaPaymentCancelExt component... ");
	        logger.debug("BBIP process context:" + context);

	        context.setData(ParamKeys.REQUEST_TYPE, Constants.REQUEST_TYPE_REVERSAL);// 单笔代收付记账请求类型，置为“抹账：C”
	        // context.setData(ParamKeys.TXN_STS, Constants.REQUEST_TYPE_REVERSAL);// 交易状态，置为“抹账：C”
	        context.setData(ParamKeys.OLD_TXN_SQN,context.getData(ParamKeys.SEQUENCE));// 取原交易流水号作为抹账流水
	        context.setData(ParamKeys.REQ_JRN_NO, context.getData(ParamKeys.OLD_TXN_SQN));// 抹账用旧流水号

	        // 支付方式为卡，需要判断卡类型
	        context.setData(ParamKeys.PASSWORD_CHECK_FLAG, Constants.PASSWORD_CHECK_FLAG_NO);
	        context.setData(ParamKeys.BUS_TYP, context.getData(ParamKeys.RAP_TYPE));

	        // 判断记账系统类型：若未指定则需要判断
	        Map<String, Object> reqMap = new HashMap<String, Object>();
	        String payMde = (String) context.getData(ParamKeys.AC_TYP); // 缴费类型
	        String actSysTyp = (String) context.getData(ParamKeys.ACT_SYS_TYP);
	        String cusAc = (String) context.getData(GDParamKeys.ACT_NO);
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

	        if (StringUtils.isEmpty((String) context.getData(ParamKeys.POS_SPL))) {
	            // 查找商户号等信息
	            EupsActSysPara eupsActSysPara = new EupsActSysPara();
	            eupsActSysPara.setComNo((String) context.getData(ParamKeys.COMPANY_NO));
	            eupsActSysPara.setActSysTyp(actSysTyp);
	            List<EupsActSysPara> resultList = get(EupsActSysParaRepository.class).find(eupsActSysPara);
	            if (CollectionUtils.isNotEmpty(resultList)) {
	                eupsActSysPara = resultList.get(0);
	                context.setData(ParamKeys.POS_SPL, eupsActSysPara.getSplNo());
	                reqMap.put(ParamKeys.POS_SPL, eupsActSysPara.getSplNo());
	                context.setData(ParamKeys.POS_TML, eupsActSysPara.getSplTerNo());
	                reqMap.put(ParamKeys.POS_TML, eupsActSysPara.getSplTerNo());
	            } else {
	                throw new CoreException(ErrorCodes.EUPS_ACT_SYS_PARA_INFO_ERROR);
	            }
	        }

	        // 替换请求系统码为本地系统码
	        SystemConfig systemConfig=get(SystemConfig.class);
	        reqMap.put(ParamKeys.REQ_SYS_CDE, systemConfig.getSystemCode());
	        // 无请求时间情况下（如第三方发起），添加请求时间
	        Date reqTme = (Date) reqMap.get(ParamKeys.BBIP_HEAD_REQ_TME);
	        if (null==reqTme) {
	            reqMap.put(ParamKeys.BBIP_HEAD_REQ_TME, new Date());
	        } 

	        // 记账系统代收付
	        if (Constants.ACT_SYS_TYP_0.equals(actSysTyp)) {
	            log.info("call bua acp system!");
	            cardTyp = Constants.CARD_TYPE_01;
	            reqMap = context.getDataMap();
	            reqMap.remove(ParamKeys.TRK_3);
	/*            reqMap.put(ParamKeys.REQ_SYS_CDE, sysCodeLocal);*/

	            CancelPaymentRequest request = cancleKeySetForBua(reqMap);
	            callBuaCancelPayment(BUAServiceAccessObject.ACP_CHN, context, request);
	        }
	        // 记账系统地方银联
	        else if (Constants.ACT_SYS_TYP_1.equals(actSysTyp)) {
	            log.info("call bua unionpay system!");
	            cardTyp = context.getData(ParamKeys.CARD_TYPE);

	            CancelPaymentRequest request = cancleKeySetForBua(reqMap);
	            callBuaCancelPayment(BUAServiceAccessObject.UNION_CHN, context, request);
	        }
	        // 记账系统CPS
	        else if (Constants.ACT_SYS_TYP_2.equals(actSysTyp)) {
	            log.info("call bua cps system!");
	            cardTyp = Constants.CARD_TYPE_02;

	            CancelPaymentRequest request = cancleKeySetForBua(reqMap);
	            callBuaCancelPayment(BUAServiceAccessObject.CPS_CHN, context, request);
	        }
	        context.setData(ParamKeys.CARD_TYPE, cardTyp);
	        context.setData(ParamKeys.ACT_SYS_TYP, actSysTyp);
	        // }
	        // catch (Exception e) {
	        // logger.info("BbipPayment call bua has error：" + e.getMessage());
	        // context.setData(ParamKeys.RESPONSE_TYPE, Constants.RESPONSE_TYPE_FAIL);
	        // context.setData(ParamKeys.RESPONSE_CODE, ErrorCodes.TRANSACTION_ERROR_COMM_ERROR);
	        // context.setData(ParamKeys.RESPONSE_MESSAGE, Constants.RESPONSE_MSG_FAIL);
	        // }
	    }

	    private void callBuaCancelPayment(String hostOpr, Context context, CancelPaymentRequest request) throws CoreException {
	        Map<String, Object> rspMap = new HashMap<String, Object>();

	        BUAResult response = get(BUAServiceAccessObject.class).cancelPayment(request, hostOpr);

//	        rspMap = response.getPayload();
//	        logger.info("===========respMap: " + rspMap + "===========");
//	        // if (!response.isSuccess())
//	        Throwable e = response.getException();
//	        if (Status.SEND_ERROR == response.getStatus()) {
//	            context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
////	            throw new CoreException(ErrorCodes.TRANSACTION_ERROR_OTHER_ERROR, "Call acp transfor or other error: ", e);
//	        }
//
//	        if (Status.TIMEOUT == response.getStatus()) {
//	            // 连接错误或等待超时,但不知道是否已上送,这里交易已处于未知状态
//	            context.setState(BPState.BUSINESS_PROCESSNIG_STATE_OVERTIME);
////	            throw new CoreException(ErrorCodes.TRANSACTION_ERROR_TIMEOUT, "Call acp servcie occur time out.");
//	        }
//	        if (Status.SYSTEM_ERROR == response.getStatus()) {
//	            context.setState(BPState.BUSINESS_PROCESSNIG_STATE_UNKOWN_FAIL);
////	            throw new CoreException(ErrorCodes.TRANSACTION_ERROR_JUMP_ERROR, "Call acp transfor or other error: ", e);
//	        } else if (Status.SUCCESS != response.getStatus() && Status.FAIL != response.getStatus()) {
//	            context.setState(BPState.BUSINESS_PROCESSNIG_STATE_UNKOWN_FAIL);
////	            throw new CoreException(ErrorCodes.TRANSACTION_ERROR_OTHER, "Call acp other error: ", e);
//	        }
//	        // throw new CoreException(ErrorCodes.TRANSACTION_ERROR_COMM_ERROR, "Call acp services error: ");
//
//	        context.setData(ParamKeys.AGTS_ACCOUNT_JOURNAL_NO, response.getAcoJrnNo());
//	        context.setData(ParamKeys.AGTS_ACCOUNT_VOUCHER_NO, response.getAcoVchNo());
//	        context.setData(ParamKeys.AGTS_ACCOUNT_DATE, response.getAcDte());
//	        context.setData(ParamKeys.AGTS_BBIP_JOURNAL_NO, response.getTxnSqn());
//	        context.setData(ParamKeys.MFM_RSP_MSG, response.getResponseMessage());
//	        logger.info("===========BBIP payment reversal response: " + rspMap);
//
//	        Object responseType = response.getResponseType();
//	        if (Constants.RESPONSE_TYPE_SUCC.equals(responseType)) {
//	            logger.info("Call BBIP payment service response successful.");
//	            context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
//	            context.setData(ParamKeys.REVERSE_RESULT_CODE, Constants.RESPONSE_CODE_SUCC_HOST);
//	        } else if (Constants.RESPONSE_TYPE_FAIL.equals(responseType)) {
//	            String responseCode = (String) response.getResponseCode();
//	            String responseMsg = (String) response.getResponseMessage();
//	            context.setData(ParamKeys.RESPONSE_CODE, responseCode);
//	            context.setData(ParamKeys.RESPONSE_MESSAGE, responseMsg);
//	            context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
//	            context.setData(ParamKeys.RESPONSE_TYPE, Constants.RESPONSE_TYPE_FAIL);
//	            logger.info("Call BBIP payment service response Failed!!!! response map: " + rspMap);
//	        } else {
//	            logger.info("Call c payment service unkown error, response is null.");
//	            context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
//	            context.setData(ParamKeys.RESPONSE_CODE, ErrorCodes.TRANSACTION_RESPONSE_ISNULL);
//	            context.setData(ParamKeys.RESPONSE_TYPE, Constants.RESPONSE_TYPE_FAIL);
//	        }
	        context.setState(BPState.BUSINESS_PROCESSNIG_STATE_OVERTIME);
	    }

	    /**
	     * cps沖正设置
	     * 
	     * @param reqMap
	     */
	    private CancelPaymentRequest cancleKeySetForBua(Map<String, Object> reqMap) {

	        CancelPaymentRequest request = new CancelPaymentRequest();
	        request.setOldReqJrnNo(reqMap.get(ParamKeys.OLD_TXN_SQN).toString());
	        request.setReqSysCde((String) reqMap.get(ParamKeys.REQ_SYS_CDE));

	        BigDecimal txnAmt = new BigDecimal(reqMap.get(ParamKeys.TXN_AMOUNT).toString());
	        request.setTxnAmt(txnAmt); // 发生额

	        AccountInfo account = new AccountInfo();
	        account.setCusAc(reqMap.get(GDParamKeys.ACT_NO).toString()); // 客户账号

	        request.setAccount(account);
	        return request;
	    }
}
