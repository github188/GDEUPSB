package com.bocom.bbip.gdeupsb.action.tbc;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.comp.CommonRequest;
import com.bocom.bbip.comp.account.AccountService;
import com.bocom.bbip.comp.account.support.CusActInfResult;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsThdTranCtlInfo;
import com.bocom.bbip.eups.repository.EupsThdTranCtlInfoRepository;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GdTbcCusAgtInfo;
import com.bocom.bbip.gdeupsb.repository.GdTbcCusAgtInfoRepository;
import com.bocom.bbip.gdeupsb.utils.CodeSwitchUtils;
import com.bocom.bbip.service.BGSPServiceAccessObject;
import com.bocom.bbip.service.Result;
import com.bocom.euif.component.util.StringUtil;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;


/**
 * 广东烟草公司查询客户余额
 * 
 * @version 1.0.0 
 * DateTime 2015-01-16
 * @author GuiLin.Li
 * 
 */
public class QryRemainingAction extends BaseAction {
    
    @Autowired
    BGSPServiceAccessObject serviceAccess;
    @Autowired
    AccountService accountService;
    @Autowired
    private BBIPPublicService bbipPublicService;
    @Autowired
    GdTbcCusAgtInfoRepository cusAgtInfoRepository;
    
    @Override
    public void execute(Context context) throws CoreException {
        log.info("QryRemaining Action start!");
        context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
        
        //转换
        context.setData("txnTme", context.getData("TRAN_TIME"));
        context.setData("bk", context.getData("BANK_ID"));
        context.setData("dptId", context.getData("DPT_ID"));
        context.setData("custId", context.getData("CUST_ID"));
        context.setData("devId", context.getData("DEV_ID"));
        context.setData("teller", context.getData("TELLER"));
        context.setData(ParamKeys.TXN_DTE, context.getData(ParamKeys.TXN_TME).toString().substring(0,8));

        String cAgtNo = CodeSwitchUtils.codeGenerator("GDYC_DPTID",  context.getData("dptId").toString());
        if (null == cAgtNo) {
            cAgtNo ="4410000560";
        }
        context.setData("cAgtNo", cAgtNo);
        EupsThdTranCtlInfo resultThdTranCtlInfo = get(EupsThdTranCtlInfoRepository.class).findOne(cAgtNo);
        if (resultThdTranCtlInfo == null) {
            throw new CoreException(ErrorCodes.THD_CHL_NOT_FOUND);
        } 
        if (resultThdTranCtlInfo.getTxnCtlSts().equals(Constants.TXN_CTL_STS_SIGNOUT)) {
            throw new CoreException(ErrorCodes.THD_CHL_ALDEAY_SIGN_OUT);
        } else if (resultThdTranCtlInfo.getTxnCtlSts().equals(Constants.TXN_CTL_STS_CHECKBILL_ING)) {
            throw new CoreException(ErrorCodes.THD_CHL_SIGNIN_NOT_ALLOWWED);
        } else {
            String cusAc = qryCusAcNo(context.getData("custId").toString());
            if (StringUtil.isEmptyOrNull(cusAc)) {
                context.setData(GDParamKeys.RSP_CDE,"9999");
                context.setData(GDParamKeys.RSP_MSG,"无此用户信息 !");
                return;
            }
            //   检查该客户是否已签约
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("cusAc",cusAc);
            
            Result accessObject =  serviceAccess.callServiceFlatting("queryListAgentCollectAgreement", map);
            if (CollectionUtils.isEmpty(accessObject.getPayload())) {
                context.setData(ParamKeys.RSP_CDE,"9999");
                context.setData(ParamKeys.RSP_MSG,"该客户未开户!");
                context.setData("bal", "0");
                return;
            } 
            context.setData("tCusNm", accessObject.getData("cusNme"));
            context.setData("actNo", accessObject.getData("cusAc"));
            context.setData("dbPasWrd", accessObject.getData("pwd"));
            //构成网点号
            String brNo = cAgtNo.substring(0,3)+"999";
            String nodNo = CodeSwitchUtils.codeGenerator("GDYC_nodSwitch", brNo);
            if (null == nodNo) {
                nodNo ="441800";
            }
            context.setData("nodNo",nodNo);
            // 取电子柜员号
            String bankId = context.getData("brNo").toString();
            String tlr = get(BBIPPublicService.class).getETeller(bankId);
            context.setData(ParamKeys.TELLER,tlr);
            context.setData("tTxnCd","483803");
            //查询账户余额信息   
            log.info("查询账户余额信息  start......");
            
            context.setData("traceNo", bbipPublicService.getTraceNo());
            context.setData(ParamKeys.BK, bbipPublicService.getParam("BBIP", "BK"));
            context.setData(ParamKeys.BR, "01441131999");
            
            context.setData(ParamKeys.TELLER, "ABIR148");
            context.setData(ParamKeys.CHANNEL, "00");
//          ctx.setData(ParamKeys.TRACE_NO, ctx.getData(ParamKeys.SEQUENCE));
            CommonRequest comRequest=CommonRequest.build(context);
            CusActInfResult acResult = accountService.getAcInf(comRequest, cusAc);
            BigDecimal actBal = acResult.getActBal();
            BigDecimal avaBal = acResult.getAvaBal();
            log.info("所剩余额：" + actBal);
            context.setData("avaBal", avaBal);
            context.setData("actBal", actBal);
            context.setData(ParamKeys.RSP_CDE,"0000");
            context.setData(ParamKeys.RSP_MSG,Constants.RESPONSE_MSG);
            context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
            
        }
    }
    //协议临时表查询客户账户
    private String qryCusAcNo(String custId){
        GdTbcCusAgtInfo cusAgtInfo = cusAgtInfoRepository.findOne(custId);
        return cusAgtInfo.getActNo();
    }
}
