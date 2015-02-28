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
import com.bocom.bbip.service.BGSPServiceAccessObject;
import com.bocom.bbip.service.Result;
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
    
    @Override
    public void execute(Context context) throws CoreException {
        log.info("QryRemaining Action start!");
        context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
        
        //转换
        context.setData("txnTme", context.getData("TRAN_TIME"));
        context.setData("bk", context.getData("BANK_ID"));
        context.setData("dptId", context.getData("DPT_ID"));
        context.setData("custId", context.getData("CUST_ID"));
        //TODO;以此确定comNo 现在测试直接传的是comNo
        context.setData("DevId", context.getData("DEV_ID"));
        context.setData("teller", context.getData("TELLER"));
        context.setData("brNo",context.getData("dptId").toString().substring(0, 3)+"999");
        
        EupsThdTranCtlInfo resultThdTranCtlInfo = get(EupsThdTranCtlInfoRepository.class).findOne(context.getData(ParamKeys.COMPANY_NO).toString());
        if (resultThdTranCtlInfo == null) {
            throw new CoreException(ErrorCodes.THD_CHL_NOT_FOUND);
        } 
        if (resultThdTranCtlInfo.getTxnCtlSts().equals(Constants.TXN_CTL_STS_SIGNOUT)) {
            throw new CoreException(ErrorCodes.THD_CHL_ALDEAY_SIGN_OUT);
        } else if (resultThdTranCtlInfo.getTxnCtlSts().equals(Constants.TXN_CTL_STS_CHECKBILL_ING)) {
            throw new CoreException(ErrorCodes.THD_CHL_SIGNIN_NOT_ALLOWWED);
        } else {
            //   检查该客户是否已签约
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("cusAc", context.getData("custId").toString());
            Result accessObject =  serviceAccess.callServiceFlatting("queryListAgentCollectAgreement", map);
            if (null == accessObject) {
                context.setData(ParamKeys.RSP_CDE,"9999");
                context.setData(ParamKeys.RSP_MSG,"该客户未开户!");
                context.setData("bal", "0");
                return;
            } 
            context.setData("tCusNm", accessObject.getData("cusNme"));
            context.setData("actNo", accessObject.getData("cusAc"));
            context.setData("dbPasWrd", accessObject.getData("pwd"));
            //构成网点号
            int brNo = context.getData("brNo");
            int nodNo = 0;
            switch (brNo) {
                case 441999:
                    nodNo=441800;
                    break;
                case 444999:
                    nodNo=444800;
                    break;
                case 446999:
                    nodNo=446800;
                    break;
                case 445999:
                    nodNo=445800;
                    break;
                case 483999:
                    nodNo=483800;
                    break;
                case 484999:
                    nodNo=484800;
                    break;
                case 485999:
                    nodNo=485800;
                    break;
                case 476999:
                    nodNo=476800;
                    break;
                case 491999:
                    nodNo=491800;
                    break;
            }
            context.setData("nodNo",nodNo);
            // 取电子柜员号
            String bankId = context.getData("brNo").toString();
            String tlr = get(BBIPPublicService.class).getETeller(bankId);
            context.setData(ParamKeys.TELLER,tlr);
            context.setData("tTxnCd","483803");
            //向主机查询账户信息  已经查询 且存放在accessObject
            

            context.setData(ParamKeys.RSP_CDE,"0000");
            context.setData(ParamKeys.RSP_MSG,Constants.RESPONSE_MSG);
            context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
            
        }
    }
}
