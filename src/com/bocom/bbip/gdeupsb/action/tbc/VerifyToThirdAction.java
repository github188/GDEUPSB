package com.bocom.bbip.gdeupsb.action.tbc;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsThdTranCtlInfo;
import com.bocom.bbip.eups.entity.EupsTransJournal;
import com.bocom.bbip.eups.repository.EupsThdTranCtlInfoRepository;
import com.bocom.bbip.eups.repository.EupsTransJournalRepository;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.utils.CodeSwitchUtils;
import com.bocom.bbip.service.BGSPServiceAccessObject;
import com.bocom.bbip.service.Result;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

/**
 * 对分别是开户、销户和扣款信息进行查询 校正
 * @author GuiLin.Li
 * @version 1.0.0
 * 2015-1-15
 *
 */
public class VerifyToThirdAction extends BaseAction {

    @Autowired
    EupsTransJournalRepository eupsTransJournalRepository;
    @Autowired
    BGSPServiceAccessObject serviceAccess;

    @Override
    public void execute(Context context) throws CoreException  {
        log.info(" VerifyToThirdAction is start!");
        context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
        //转换
        context.setData("txnTme", context.getData("TRAN_TIME"));
        context.setData("bk", context.getData("BANK_ID"));
        context.setData("dptId", context.getData("DPT_ID"));
        //上面公共报文头，下面报文体
        context.setData("oTLogNo", context.getData("TRADE_SEQ_OLD"));
        context.setData("oTTxnCd", context.getData("TRADE_ID_OLD"));
        context.setData("custId", context.getData("CUST_ID_OLD"));
        context.setData("DevId", context.getData("DEV_ID"));
        context.setData("teller", context.getData("TELLER"));
        context.setData(ParamKeys.TXN_DTE, context.getData(ParamKeys.TXN_TME).toString().substring(0,8));
        
        String cAgtNo = CodeSwitchUtils.codeGenerator("GDYC_DPTID",  context.getData("dptId").toString());
        if (null == cAgtNo) {
            cAgtNo ="4410000560";
        }
        context.setData("cAgtNo", cAgtNo);
        EupsThdTranCtlInfo eupsThdTranCtlInfo = get(EupsThdTranCtlInfoRepository.class).findOne(cAgtNo);
        if (null == eupsThdTranCtlInfo) {
            throw new CoreException(ErrorCodes.THD_CHL_NOT_FOUND);
        } 
        if (eupsThdTranCtlInfo.getTxnCtlSts().equals(Constants.TXN_CTL_STS_SIGNOUT)) {
            throw new CoreException(ErrorCodes.THD_CHL_ALDEAY_SIGN_OUT);
        }
        String txnCode = context.getData("oTTxnCd");
        if("8912".equals(txnCode)) {  //检查开户
            //   检查该客户是否已签约
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("cusAc", context.getData("actNo").toString());
            Result accessObject =  serviceAccess.callServiceFlatting("queryListAgentCollectAgreement", map);
            if (CollectionUtils.isEmpty(accessObject.getPayload())) {
                context.setData(ParamKeys.RSP_CDE,"9999");
                context.setData(ParamKeys.RSP_MSG,"客户已注销或未开户!!");
                context.setData(ParamKeys.OLD_TXN_SEQUENCE, null);
            } else {
                context.setData(ParamKeys.RSP_CDE,Constants.RESPONSE_CODE_SUCC);
                context.setData(ParamKeys.RSP_MSG,Constants.RESPONSE_MSG);
                context.setData(ParamKeys.OLD_TXN_SEQUENCE, null);
            }
        } else if("8913".equals(txnCode)) { // 检查销户
            //   检查该客户是否已签约
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("cusAc", context.getData("actNo").toString());
            Result accessObject = serviceAccess.callServiceFlatting("queryListAgentCollectAgreement", map);
            if (CollectionUtils.isEmpty(accessObject.getPayload())) {
                context.setData(ParamKeys.RSP_CDE,"9999");
                context.setData(ParamKeys.RSP_MSG,"客户未注销!!");
                context.setData(ParamKeys.OLD_TXN_SEQUENCE, null);
            } else {
                context.setData(ParamKeys.RSP_CDE,Constants.RESPONSE_CODE_SUCC);
                context.setData(ParamKeys.RSP_MSG,Constants.RESPONSE_MSG);
                context.setData(ParamKeys.OLD_TXN_SEQUENCE, null);
            }
        } else if ("8914".equals(txnCode)){ // <!--扣款-->
            EupsTransJournal eupsTransJournal = eupsTransJournalRepository.findOne(context.getData("oTLogNo").toString());
            if (null == eupsTransJournal) {//   <!--没此交易记录--> 
                context.setData(GDParamKeys.RSP_CDE,GDConstants.RSP_FAIL_COD);
                context.setData(GDParamKeys.RSP_MSG,"此交易不存在!");
                context.setData(GDParamKeys.BANK_SEQ_OLD, null);
                return;
            } else {
                 if (eupsTransJournal.getMfmTxnSts().equals("S")) {
                     context.setData(GDParamKeys.RET_CODE_OLD,Constants.RESPONSE_CODE_SUCC);
                     context.setData(GDParamKeys.RSP_MSG_OLD,Constants.RESPONSE_MSG);
                     context.setData(GDParamKeys.BANK_SEQ_OLD,eupsTransJournal.getSqn());
                 } else if (eupsTransJournal.getMfmTxnSts().equals("T")) {
                     context.setData(GDParamKeys.RET_CODE_OLD,GDConstants.RSP_FAIL_COD);
                     context.setData(GDParamKeys.RSP_MSG_OLD,"原交易记账超时!");
                     context.setData(GDParamKeys.BANK_SEQ_OLD, null);
                 } else if (eupsTransJournal.getMfmTxnSts().equals("F")) {
                     context.setData(GDParamKeys.RET_CODE_OLD,GDConstants.RSP_FAIL_COD);
                     context.setData(GDParamKeys.RSP_MSG_OLD,"原交易记账失败!");
                     context.setData(GDParamKeys.BANK_SEQ_OLD, null);
                 } else if (eupsTransJournal.getMfmTxnSts().equals("U")) {
                     context.setData(GDParamKeys.RET_CODE_OLD,GDConstants.RSP_FAIL_COD);
                     context.setData(GDParamKeys.RSP_MSG_OLD,"原交易记账失败!");
                     context.setData(GDParamKeys.BANK_SEQ_OLD, null);
                 }
            }
        } else {
            context.setData(GDParamKeys.RSP_CDE,GDConstants.RSP_FAIL_COD);
            context.setData(GDParamKeys.RSP_MSG,"原交易码不存在!");
            context.setData(GDParamKeys.BANK_SEQ_OLD, null);
            context.setData(GDParamKeys.RET_CODE_OLD,null);
            context.setData(GDParamKeys.RSP_MSG_OLD,null);
            return;
        }
        context.setData(GDParamKeys.RSP_CDE,Constants.RESPONSE_CODE_SUCC);
        context.setData(GDParamKeys.RSP_MSG,"交易处理成功 !");
        context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
        
    }
}
