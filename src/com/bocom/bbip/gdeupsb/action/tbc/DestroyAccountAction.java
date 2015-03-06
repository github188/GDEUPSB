package com.bocom.bbip.gdeupsb.action.tbc;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.entity.EupsThdTranCtlInfo;
import com.bocom.bbip.eups.repository.EupsThdTranCtlInfoRepository;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.service.BGSPServiceAccessObject;
import com.bocom.bbip.service.Result;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

/**
 * 烟草销户
 * Date 2015-1-13
 * @author GuiLin.Li
 * @version 1.0.0
 */
public class DestroyAccountAction extends BaseAction {
   
    @Autowired
    BGSPServiceAccessObject serviceAccess;
    
    @Override
     public void execute(Context context) throws CoreException {
        log.info("DestroyAccount Action start!...");
        context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);

        //转换
        context.setData("txnTme", context.getData("TRAN_TIME"));
        context.setData("bk", context.getData("BANK_ID"));
        context.setData("dptId", context.getData("DPT_ID"));
        //上面公共报文头，下面报文体
        context.setData("custId", context.getData("CUST_ID"));
        context.setData("tCusNm", context.getData("CUST_NAME"));
        context.setData("cusTyp", context.getData("CUST_TYPE"));
        context.setData("pasId", context.getData("PASS_ID"));
        context.setData("liceId", context.getData("LICE_ID"));
        context.setData("accTyp", context.getData("ACC_TYPE"));
        context.setData("actNo", context.getData("ACC"));
        context.setData("devId", context.getData("DEV_ID"));
        context.setData("teller", context.getData("TELLER"));

        String comNo = context.getData("dptId").toString();
        //检查系统签到状态
        EupsThdTranCtlInfo eupsThdTranCtlInfo = get(EupsThdTranCtlInfoRepository.class).findOne(comNo);
        if (null == eupsThdTranCtlInfo) {
            throw new CoreException(ErrorCodes.THD_CHL_NOT_FOUND);
        } 
        if (eupsThdTranCtlInfo.getTxnCtlSts().equals(Constants.TXN_CTL_STS_SIGNOUT)) {
            throw new CoreException(ErrorCodes.THD_CHL_ALDEAY_SIGN_OUT);
        }
        //客户签约状态
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("cusAc", context.getData("actNo").toString());
        Result accessObject = serviceAccess.callServiceFlatting("queryListAgentCollectAgreement", map);
        if (CollectionUtils.isEmpty(accessObject.getPayload())){
            context.setData(GDParamKeys.RSP_MSG,"账号已注销!！！");
            return; 
        }
        
      //检查用户名是否匹配
        String cusNme = context.getData("cusNme").toString().trim();
        String tCusNm = accessObject.getData("tCusNm").toString().trim();
        if (!cusNme.equals(tCusNm)) {
            context.setData("MsgTyp",Constants.RESPONSE_TYPE_FAIL);
            context.setData(GDParamKeys.RSP_CDE,"9999");
            context.setData(GDParamKeys.RSP_MSG,"客户姓名不符!！！");
            return;
        }
    
        String pasId = context.getData("pasId").toString().trim();
        String idNo =accessObject.getData("idNo").toString().trim();
        if (!idNo.equals(pasId)) {
            context.setData("MsgTyp",Constants.RESPONSE_TYPE_FAIL);
            context.setData(GDParamKeys.RSP_CDE,"9999");
            context.setData(GDParamKeys.RSP_MSG,"身份证号码不符!！！");
            return;
        }
        String actNo = context.getData("actNo").toString().trim();
        String cusAc =accessObject.getData("cusAc").toString().trim();
        if (!cusAc.equals(actNo)) {
            context.setData("MsgTyp",Constants.RESPONSE_TYPE_FAIL);
            context.setData(GDParamKeys.RSP_CDE,"9999");
            context.setData(GDParamKeys.RSP_MSG,"卡/账户号码不符!！！");
            return;
        }
       
        Map<String, Object> destroyMap = new HashMap<String, Object>();
        destroyMap.put("agdAgrNo", accessObject.getData("agdAgrNo").toString());
        try {
            serviceAccess.callServiceFlatting("deleteAgentCollectAgreement", destroyMap);
        } catch (Exception e) {
            context.setData("MsgTyp",Constants.RESPONSE_TYPE_FAIL);
            context.setData(GDParamKeys.RSP_CDE,"9999");
            context.setData(GDParamKeys.RSP_MSG,"数据库处理失败!！！");
            return;
        }

        context.setData(GDParamKeys.RSP_CDE,Constants.RESPONSE_CODE_SUCC);
        context.setData(GDParamKeys.RSP_MSG,Constants.RESPONSE_MSG);
        context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
    }
}
