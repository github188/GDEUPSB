package com.bocom.bbip.gdeupsb.action.tbc;

import java.math.BigDecimal;
import java.util.Date;
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
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GdTbcBasInf;
import com.bocom.bbip.gdeupsb.entity.GdTbcCusAgtInfo;
import com.bocom.bbip.gdeupsb.repository.GdTbcBasInfRepository;
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
    BBIPPublicService publicService;
    @Autowired
    GdTbcCusAgtInfoRepository cusAgtInfoRepository;
    
    
    @Override
    public void execute(Context context) throws CoreException {
        log.info("QryRemaining Action start!");
        context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
        context.setData("ACCOUNT", "0"); //预置为0， 接口文档：<ACCOUNT>余额(错误时返回0)</ACCOUNT>   -- add by MQ
        //转换
        context.setData("txnTme", context.getData("TRAN_TIME"));
        context.setData("bk", context.getData("BANK_ID"));
        context.setData("dptId", context.getData("DPT_ID"));
        context.setData("custId", context.getData("CUST_ID"));
        context.setData("devId", context.getData("DEV_ID"));
        context.setData("teller", context.getData("TELLER"));
        context.setData(ParamKeys.TXN_DTE, context.getData(ParamKeys.TXN_TME).toString().substring(0,8));
        
        //检查系统签到状态
        GdTbcBasInf resultTbcBasInfo = get(GdTbcBasInfRepository.class).findOne(context.getData("dptId").toString());
        if (resultTbcBasInfo == null) {
            throw new CoreException(ErrorCodes.THD_CHL_NOT_FOUND);
        } 
        if (resultTbcBasInfo.getSigSts().equals(Constants.TXN_CTL_STS_SIGNOUT)) {
            throw new CoreException(ErrorCodes.THD_CHL_ALDEAY_SIGN_OUT);
        } else if (resultTbcBasInfo.getSigSts().equals(Constants.TXN_CTL_STS_CHECKBILL_ING)) {
            throw new CoreException(ErrorCodes.THD_CHL_SIGNIN_NOT_ALLOWWED);
        } else {
            String cusAc = qryCusAcNo(context.getData("custId").toString(),context);
            if (StringUtil.isEmptyOrNull(cusAc)) {
                context.setData(GDParamKeys.RSP_CDE,"9999");
                context.setData(GDParamKeys.RSP_MSG,"无此用户信息 !");
//              return;
                throw new CoreException(GDParamKeys.RSP_MSG);
            }
            //   检查该客户是否已签约
            Map<String, Object> map = getMap();
            map.put("cusAc",cusAc);
            map.put("bk", context.getData(ParamKeys.BK));
            map.put("br", context.getData(ParamKeys.BR));

            
            Result accessObject =  serviceAccess.callServiceFlatting("queryListAgentCollectAgreement", map);
            if (CollectionUtils.isEmpty(accessObject.getPayload())) {
                context.setData(ParamKeys.RSP_CDE,"9999");
                context.setData(ParamKeys.RSP_MSG,"该客户未开户!");
                context.setData("bal", "0");
//              return;
                throw new CoreException(GDParamKeys.RSP_MSG);
            } 
            context.setData("tCusNm", accessObject.getData("cusNme"));
            context.setData("actNo", accessObject.getData("cusAc"));
            context.setData("dbPasWrd", accessObject.getData("pwd"));
            //构成网点号
            String cAgtNo = CodeSwitchUtils.codeGenerator("GDYC_DPTID",  context.getData("dptId").toString());
            if (null == cAgtNo) {
                cAgtNo ="4410000560";
            }
            context.setData("cAgtNo", cAgtNo);
            String brNo = cAgtNo.substring(0,3)+"999";
            String nodNo = CodeSwitchUtils.codeGenerator("GDYC_nodSwitch", brNo);
            if (null == nodNo) {
                nodNo ="01441800999";
            }
            context.setData("nodNo",nodNo);
            // 取电子柜员号 获取不到tlr
           // String bankId = context.getData("brNo").toString();
           // String tlr = get(BBIPPublicService.class).getETeller(bankId);
          //  context.setData(ParamKeys.TELLER,tlr);
            context.setData("tTxnCd","483803");
            //查询账户余额信息   
            log.info("查询账户余额信息  start......");
            
            context.setData("traceNo", publicService.getTraceNo());
            
            context.setData(ParamKeys.TELLER, "ABIR148");
            context.setData(ParamKeys.CHANNEL, "00");
//          ctx.setData(ParamKeys.TRACE_NO, ctx.getData(ParamKeys.SEQUENCE));
            CommonRequest comRequest=CommonRequest.build(context);
            CusActInfResult acResult = accountService.getAcInf(comRequest, cusAc);
            BigDecimal actBal = acResult.getActBal();
            BigDecimal avaBal = acResult.getAvaBal();
            log.info("所剩余额：" + actBal);
            context.setData("avaBal", avaBal);
            context.setData("ACCOUNT", actBal);
            context.setData(GDParamKeys.RSP_CDE,"0000");
            context.setData(GDParamKeys.RSP_MSG,Constants.RESPONSE_MSG);
            context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
            
        }
    }
    //协议临时表查询客户账户
    private String qryCusAcNo(String custId,Context context){
        GdTbcCusAgtInfo cusAgtInfo = cusAgtInfoRepository.findOne(custId);
        if (cusAgtInfo == null) {
        	context.setData(ParamKeys.RSP_CDE,"9999");
            context.setData(ParamKeys.RSP_MSG,"该客户未开户!");
            return null;
        }
        else {
            return cusAgtInfo.getActNo();
        }

    }
    
    private Map<String, Object> getMap(){
        Map<String, Object> map =  new HashMap<String, Object>();
        map.put("traceNo", publicService.getTraceNo());
        map.put("traceSrc", "GDEUPSB");
        map.put("version","GDEUPSB-1.0.0");
        map.put("reqTme", new Date());
        map.put("reqJrnNo",  publicService.getBBIPSequence());
        map.put("reqSysCde", "SGRT00");
        map.put("tlr", "4411417");
        map.put("chn", "00");
        return map;
    }
}
