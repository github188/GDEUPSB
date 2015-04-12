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
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.SystemConfig;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class GDBuaPaymentCancelUsingReversalInvoice extends BaseAction {
	private final static Log logger = LogFactory
			.getLog(GDBuaPaymentCancelUsingReversalInvoice.class);

	@Autowired
	GDEupsbTrspFeeInfoRepository gdEupsbTrspFeeInfoRepository;

	@Autowired
	GDEupsbTrspTxnJnlRepository gdEupsbTrspTxnJnlRepository;

	public void execute(Context context) throws CoreException,
			CoreRuntimeException {
		log.info("GDBuaPaymentCancelUsingReversalInvoiceExt start......");
		if ("S".equals(context.getData("ohTxnSt"))) {
			throw new CoreException("");
		}
		
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
		
		GDEupsbTrspFeeInfo gdEupsbTrspFeeInfo = new GDEupsbTrspFeeInfo();
		// 登记退费流水号
		gdEupsbTrspFeeInfo.setThdKey(context.getData(GDParamKeys.THD_KEY)
				.toString());
		gdEupsbTrspFeeInfo.setRvsLog(context.getData(GDParamKeys.SQN)
				.toString());
		gdEupsbTrspFeeInfoRepository.updateThdFeeInfo(gdEupsbTrspFeeInfo);

		gdEupsbTrspTxnJnlRepository.insert(BeanUtils.toObject(
				context.getDataMap(), GDEupsbTrspTxnJnl.class));
		logger.debug("BBIP process context:" + context);
		context.setData(ParamKeys.REQUEST_TYPE, Constants.REQUEST_TYPE_REVERSAL);// 单笔代收付记账请求类型，置为“抹账：C”

		// 抹账交易登记流水，抹账流水号使用原流水号oldTxnSqn
		this.log.info("oldTxnSqn is:" + context.getData("oldTxnSqn"));

		context.setData("reqJrnNo", context.getData("sqn"));

		Map reqMap = context.getDataMap();
		String payMde = (String) context.getData("payMde");
		String actSysTyp = (String) context.getData("actSysTyp");
		String cusAc = (String) context.getData("cusAc");
		AccountService cardBINService = (AccountService) get(AccountService.class);
		String cardTyp = null;

		if (StringUtils.isEmpty(actSysTyp)) {
			if ("4".equals(payMde)) {
				if (cardBINService.isOurBankCard(cusAc)) {
					if (cardBINService.isCreditCard(cusAc))
						actSysTyp = "2";
					else
						actSysTyp = "0";
				} else
					actSysTyp = "1";
			} else {
				actSysTyp = "0";
			}
		}
		this.log.info("记账系统类型:" + actSysTyp);

		SystemConfig systemConfig = (SystemConfig) get(SystemConfig.class);
		reqMap.put("reqSysCde", systemConfig.getSystemCode());

		Date reqTme = (Date) reqMap.get("reqTme");
		if (reqTme == null) {
			reqMap.put("reqTme", new Date());
		}

		if ("0".equals(actSysTyp)) {
			this.log.info("call acp system!");
			cardTyp = "01";

			ReversalRequest request = reversalKeySetForBua(reqMap);
			callBuaReversal("01", context, request);
		} else if ("1".equals(actSysTyp)) {
			this.log.info("call unionpay system!");
			cardTyp = (String) context.getData("cardType");

			ReversalRequest request = reversalKeySetForBua(reqMap);
			callBuaReversal("03", context, request);
		} else if ("2".equals(actSysTyp)) {
			this.log.info("call cps system!");
			cardTyp = "02";
			reqMap = context.getDataMap();

			ReversalRequest request = reversalKeySetForBua(reqMap);
			callBuaReversal("02", context, request);
		}
		context.setData("cardType", cardTyp);
		context.setData("actSysTyp", actSysTyp);

		

		if (context.getState().equals(
				BPState.BUSINESS_PROCESSNIG_STATE_OVERTIME)) {
			gdEupsbTrspTxnJnlRepository.update(BeanUtils.toObject(
					context.getDataMap(), GDEupsbTrspTxnJnl.class));
			context.setData(ParamKeys.RSP_MSG, "主机交易超时");
			throw new CoreRuntimeException(ErrorCodes.TRANSACTION_ERROR_TIMEOUT);
		} else if (context.getState().equals(
				BPState.BUSINESS_PROCESSNIG_STATE_NORMAL)) {
			gdEupsbTrspTxnJnlRepository.update(BeanUtils.toObject(
					context.getDataMap(), GDEupsbTrspTxnJnl.class));
			context.setData(ParamKeys.RSP_MSG, "主机交易成功");
		} else {
			gdEupsbTrspTxnJnlRepository.update(BeanUtils.toObject(
					context.getDataMap(), GDEupsbTrspTxnJnl.class));
			context.setData(ParamKeys.RSP_MSG, "主机交易失败");
			throw new CoreRuntimeException(
					ErrorCodes.TRANSACTION_ERROR_OTHER_ERROR);
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
	private void callBuaReversal(String hostOpr, Context context,
			ReversalRequest request) throws CoreException {
		logger.info("start callBuaReversal!");
		Map rspMap = new HashMap();

		BUAResult response = ((BUAServiceAccessObject) get(BUAServiceAccessObject.class))
				.reversal(request, hostOpr);

		rspMap = response.getPayload();
		logger.info("===========respMap: " + rspMap + "===========");
		String hostRspCode = response.getResponseCode();

		if (-10 == response.getStatus()) {
			context.setState("BP_STATE_FAIL");
			logger.info("business error ,error message is thd return business error");
		}

		if (-1 == response.getStatus()) {
			context.setState("BP_STATE_UNKOWN_FAIL");
			logger.info("business error,error message is timeOut or connect error ");
		}
		if (-2 == response.getStatus()) {
			context.setState("BP_STATE_UNKOWN_FAIL");
			logger.info("business error,unknow reason");
		} else if ((response.getStatus() != 0) && (3 != response.getStatus())) {
			context.setState("BP_STATE_UNKOWN_FAIL");
			logger.info("business error,business is not success ");
		}

		context.setData("accJournalNo", response.getAcoJrnNo());
		context.setData("accVoucherNo", response.getAcoVchNo());
		context.setData("accountDate", response.getAcDte());
		context.setData("bbipJournalNo", response.getTxnSqn());
		context.setData("mfmRspMsg", response.getResponseMessage());
		logger.info("===========bua payment reversal response: " + rspMap);

		if (response.getStatus() == 0) {
			logger.info("Call BBIP payment service response successful.");
			context.setState("BP_STATE_NORMAL");
			context.setData(ParamKeys.REVERSE_RESULT_CODE, "SC0000");
			logger.info("call host reversal suc!context="
					+ context.getDataMap());
		} else if (3 == response.getStatus()) {
			context.setState("BP_STATE_FAIL");
			context.setData(ParamKeys.REVERSE_RESULT_CODE, hostRspCode);
			logger.info("Call BBIP payment service response Failed!!!! response map: "
					+ rspMap);
		} else {
			logger.info("Call bua payment service unkown error, response is null.");
			context.setState("BP_STATE_FAIL");
		}
	}

	private ReversalRequest reversalKeySetForBua(Map<String, Object> reqMap) {
		ReversalRequest request = new ReversalRequest();
		request.setReqSysCde((String) reqMap.get("reqSysCde"));
		request.setOldReqJrnNo((String) reqMap.get("oldTxnSqn"));
		request.setTraceNo((String) reqMap.get("traceNo"));

		return request;
	}
}
