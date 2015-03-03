package com.bocom.bbip.gdeupsb.action.tbc;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsThdTranCtlInfo;
import com.bocom.bbip.eups.repository.EupsThdTranCtlInfoRepository;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.utils.CodeSwitchUtils;
import com.bocom.bbip.service.BGSPServiceAccessObject;
import com.bocom.bbip.service.Result;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
/**
 * 广东烟草客户余额查询  8924
 * @author GuiLin.Li
 * Date 2015-1-8
 *@version 1.0.1
 */
public class FindClientRemainingMoney extends BaseAction {

    @Autowired
    BGSPServiceAccessObject serviceAccess;
    
    @Override
    public void execute(Context context) throws CoreException {
        log.info("FindClientRemainingMoney Action start!");
        context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);

        //转换
        context.setData("txnTme", context.getData("TRAN_TIME"));
        context.setData(ParamKeys.BK, context.getData("BANK_ID"));
        context.setData("dptId", context.getData("DPT_ID"));
        //上面公共报文头，下面报文体
        context.setData("custId", context.getData("CUST_ID"));
        //TODO;以此确定comNo 现在测试直接传的是comNo
        context.setData("DevId", context.getData("DEV_ID"));
        context.setData("teller", context.getData("TELLER"));
        context.setData(ParamKeys.TXN_DTE, context.getData(ParamKeys.TXN_TME).toString().substring(0,8));
        
        String cAgtNo = CodeSwitchUtils.codeGenerator("GDYC_DPTID",  context.getData("dptId").toString());
        if (null == cAgtNo) {
            cAgtNo ="441";
        }
        String comNo = cAgtNo.substring(0,3)+"999";
        //检查系统签到状态
        EupsThdTranCtlInfo eupsThdTranCtlInfo = get(EupsThdTranCtlInfoRepository.class).findOne(comNo);
        if (eupsThdTranCtlInfo == null) {
            throw new CoreException(ErrorCodes.THD_CHL_NOT_FOUND);
        } 
        if (eupsThdTranCtlInfo.getTxnCtlSts().equals(Constants.TXN_CTL_STS_SIGNOUT)) {
            throw new CoreException(ErrorCodes.THD_CHL_ALDEAY_SIGN_OUT);
        } else {
            //客户签约状态
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("cusAc", context.getData("actNo").toString());
            try {
                Result accessObject = serviceAccess.callServiceFlatting("queryListAgentCollectAgreement", map);
                if (null == accessObject) {
                    context.setData(GDParamKeys.RSP_CDE,"9999");
                    context.setData(GDParamKeys.RSP_MSG,"该客户未开户 !!");
                    context.setData("bal","0");
                    return ;
                }
            } catch(Exception e) {
                throw new CoreException("数据库操作错误!!");
            }

                context.setData("nodNo","483800");
                // 获取电子柜员
                String bankId = context.getData(ParamKeys.BK).toString();
                String tlr = get(BBIPPublicService.class).getETeller(bankId);
                context.setData(ParamKeys.TELLER,tlr);
                context.setData(ParamKeys.BK,"483803");

                context.setData(GDParamKeys.RSP_CDE,Constants.RESPONSE_CODE_SUCC);
                context.setData(GDParamKeys.RSP_MSG,Constants.RESPONSE_MSG);
            }
            context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
    }
}
