package com.bocom.bbip.gdeupsb.action.transportfee;

import com.bocom.bbip.comp.account.AccountService;
import com.bocom.bbip.comp.bua.BUAResult;
import com.bocom.bbip.comp.bua.message.ReversalRequest;
import com.bocom.bbip.comp.bua.service.BUAServiceAccessObject;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.SystemConfig;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BuaPaymentCancelUsingReversalTr extends BaseAction
{
  private static final Log logger = LogFactory.getLog(BuaPaymentCancelUsingReversalTr.class);

  public void execute(Context context) throws CoreException, CoreRuntimeException {
    logger.info("Enter in BuaPaymentCancelUsingReversal component...");
    logger.debug("BBIP process context:" + context);
    context.setData("reqTyp", "C");

    this.log.info("oldTxnSqn is:" + context.getData("oldTxnSqn"));

    context.setData("reqJrnNo", context.getData("sqn"));

    Map reqMap = context.getDataMap();
    String payMde = (String)context.getData("payMde");
    String actSysTyp = (String)context.getData("actSysTyp");
    String cusAc = (String)context.getData("cusAc");
    AccountService cardBINService = (AccountService)get(AccountService.class);
    String cardTyp = null;

    if (StringUtils.isEmpty(actSysTyp)) {
      if ("4".equals(payMde)) {
        if (cardBINService.isOurBankCard(cusAc)) {
          if (cardBINService.isCreditCard(cusAc))
            actSysTyp = "2";
          else
            actSysTyp = "0";
        }
        else
          actSysTyp = "1";
      }
      else {
        actSysTyp = "0";
      }
    }
    this.log.info("记账系统类型:" + actSysTyp);

    SystemConfig systemConfig = (SystemConfig)get(SystemConfig.class);
    reqMap.put("reqSysCde", systemConfig.getSystemCode());

    Date reqTme = (Date)reqMap.get("reqTme");
    if (reqTme == null) {
      reqMap.put("reqTme", new Date());
    }

    if ("0".equals(actSysTyp)) {
      this.log.info("call acp system!");
      cardTyp = "01";

      ReversalRequest request = reversalKeySetForBua(reqMap);
      callBuaReversal("01", context, request);
    }
    else if ("1".equals(actSysTyp)) {
      this.log.info("call unionpay system!");
      cardTyp = (String)context.getData("cardType");

      ReversalRequest request = reversalKeySetForBua(reqMap);
      callBuaReversal("03", context, request);
    }
    else if ("2".equals(actSysTyp)) {
      this.log.info("call cps system!");
      cardTyp = "02";
      reqMap = context.getDataMap();

      ReversalRequest request = reversalKeySetForBua(reqMap);
      callBuaReversal("02", context, request);
    }
    context.setData("cardType", cardTyp);
    context.setData("actSysTyp", actSysTyp);
  }

  private void callBuaReversal(String hostOpr, Context context, ReversalRequest request)
    throws CoreException
  {
    logger.info("start callBuaReversal!");
    Map rspMap = new HashMap();

    BUAResult response = ((BUAServiceAccessObject)get(BUAServiceAccessObject.class)).reversal(request, hostOpr);

    rspMap = response.getPayload();
    logger.info("===========respMap: " + rspMap + "===========");
    String hostRspCode = response.getResponseCode();

    if (-10 == response.getStatus()) {
      context.setState("BP_STATE_FAIL");
      logger.info("business error ,error message is thd return business error");
    }

    if (-1 == response.getStatus())
    {
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
      logger.info("call host reversal suc!context=" + context.getDataMap());
    } else if (3 == response.getStatus()) {
      context.setState("BP_STATE_FAIL");
      context.setData(ParamKeys.REVERSE_RESULT_CODE, hostRspCode);
      logger.info("Call BBIP payment service response Failed!!!! response map: " + rspMap);
    } else {
      logger.info("Call bua payment service unkown error, response is null.");
      context.setState("BP_STATE_FAIL");
    }
  }

  private ReversalRequest reversalKeySetForBua(Map<String, Object> reqMap)
  {
    ReversalRequest request = new ReversalRequest();
    request.setReqSysCde((String)reqMap.get("reqSysCde"));
    request.setOldReqJrnNo((String)reqMap.get("oldTxnSqn"));
    request.setTraceNo((String)reqMap.get("traceNo"));

    return request;
  }
}