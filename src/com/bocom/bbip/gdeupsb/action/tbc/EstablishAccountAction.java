package com.bocom.bbip.gdeupsb.action.tbc;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.bocom.bbip.service.Result;
import com.bocom.bbip.comp.BBIPPublicService;
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
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

/**
 * 烟草开户
 * Date 2015-1-29
 * @author GuiLin.Li
 * @version 1.0.0
 */
public class EstablishAccountAction extends BaseAction {
   
    @Autowired
    BGSPServiceAccessObject serviceAccess;
    @Autowired
    GdTbcCusAgtInfoRepository cusAgtInfoRepository;
    @Autowired
    BBIPPublicService publicService;
    
    @Override
     public void execute(Context context) throws CoreException {
        log.info("EstablishAccount Action start!");
        context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
        
        //转换
        context.setData("txnTme", context.getData("TRAN_TIME"));
        context.setData(ParamKeys.BK, context.getData("BANK_ID"));
        context.setData("dptId", context.getData("DPT_ID"));
        //上面公共报文头，下面报文体
        context.setData("custId", context.getData("CUST_ID"));
        context.setData("comId", context.getData("COM_ID"));
        context.setData("tCusNm", context.getData("CUST_NAME"));
        context.setData("cusTyp", context.getData("CUST_TYPE"));
        context.setData("pasTyp", context.getData("PASS_TYPE"));
        context.setData("pasId", context.getData("PASS_ID"));
        context.setData("liceId", context.getData("LICE_ID"));
        context.setData("accTyp", context.getData("ACC_TYPE"));
        context.setData("actNo", context.getData("ACC"));
        context.setData("pasFlg", context.getData("PASS_FLAG"));
        context.setData("pasWrd", context.getData("PASSWORD"));
        context.setData("addres", context.getData("ADDR"));
        context.setData("telNum", context.getData("TEL"));
        context.setData("devId", context.getData("DEV_ID"));
        context.setData("teller", context.getData("TELLER"));

        String cAgtNo = CodeSwitchUtils.codeGenerator("GDYC_DPTID",  context.getData("dptId").toString());
        if (null == cAgtNo) {
            cAgtNo ="4410000560";
        }
        context.setData("cAgtNo", cAgtNo);
        //检查系统签到状态
        EupsThdTranCtlInfo eupsThdTranCtlInfo = get(EupsThdTranCtlInfoRepository.class).findOne(cAgtNo);
        if (null == eupsThdTranCtlInfo) {
            throw new CoreException(ErrorCodes.THD_CHL_NOT_FOUND);
        } 
        if (eupsThdTranCtlInfo.getTxnCtlSts().equals(Constants.TXN_CTL_STS_SIGNOUT)) {
            throw new CoreException(ErrorCodes.THD_CHL_ALDEAY_SIGN_OUT);
        }
        
        //构成网点号
        String brNo = cAgtNo.substring(0,3)+"999";
        String nodNo = CodeSwitchUtils.codeGenerator("GDYC_nodSwitch",brNo);
        if (null == nodNo) {
            nodNo ="441800";
        }
        context.setData("nodNo",nodNo);
        // 获取电子柜员
        String bankId = context.getData(ParamKeys.BK).toString();
        String tlr = get(BBIPPublicService.class).getETeller(bankId);
        context.setData(ParamKeys.TELLER,tlr);
        context.setData("tTxnCd","483803");

        context.setData("txnCtlSts", "0");
        //   检查该客户是否已签约
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("cusAc", context.getData("actNo").toString());
        String traceNo = publicService.getTraceNo();
        //map.put("traceNo", traceNo);
        map.put("traceNo", "2004440020150318154000055129");
       // context.setData("traceNo", traceNo);
        context.setData("traceNo", "2004440020150318154000055129");
        Result accessObject =  serviceAccess.callServiceFlatting("queryListAgentCollectAgreement", map);
        if (CollectionUtils.isEmpty(accessObject.getPayload())) {
            Map<String, Object> establishMap = new HashMap<String, Object>();
            establishMap.put("agrChl","01");
            establishMap.put("oprTyp", "0");
            establishMap.put("cusTyp","0");
            establishMap.put("cusNo",context.getData("custId"));
            establishMap.put("cusNme",context.getData("tCusNm"));
            establishMap.put("idNo",context.getData("pasId"));
            establishMap.put("idTyp",context.getData("pasTyp"));
            establishMap.put("ccy","156");
            establishMap.put("cusAc",context.getData("actNo"));
            
            //GDEUPS协议临时表添加数据
            GdTbcCusAgtInfo  cusAgtInfo =new  GdTbcCusAgtInfo();
            cusAgtInfo.setActNo(context.getData("actNo").toString());
            cusAgtInfo.setCustId(context.getData("custId").toString());
            cusAgtInfo.setCusNm(context.getData("tCusNm").toString());
            cusAgtInfo.setPasId(context.getData("pasId").toString());
            cusAgtInfo.setComId(context.getData("comId").toString());
            cusAgtInfo.setAccTyp(context.getData("accTyp").toString());
            cusAgtInfoRepository.insert(cusAgtInfo);
            //代收付系统接口调用增加协议
            try {
                serviceAccess.callServiceFlatting("maintainAgentCollectAgreement", establishMap);
            } catch(Exception e) {
                context.setData("MsgTyp",Constants.RESPONSE_TYPE_FAIL);
                context.setData(GDParamKeys.RSP_CDE,"9999");
                context.setData(GDParamKeys.RSP_MSG,"数据库处理失败!！！");
                return;
            }
        } else {
            Map<String, Object> establishMap = new HashMap<String, Object>();
            establishMap.put("agrChl","01");
            establishMap.put("oprTyp", "1");
            establishMap.put("cusTyp","0");
            establishMap.put("cusNo",context.getData("custId"));
            establishMap.put("cusNme",context.getData("tCusNm"));
            establishMap.put("idNo",context.getData("pasId"));
            establishMap.put("idTyp",context.getData("pasTyp"));
            establishMap.put("ccy","156");
            establishMap.put("cusAc",context.getData("actNo"));
            try {
                serviceAccess.callServiceFlatting("maintainAgentCollectAgreement", establishMap);
            } catch(Exception e) {
                context.setData("MsgTyp",Constants.RESPONSE_TYPE_FAIL);
                context.setData(GDParamKeys.RSP_CDE,"9999");
                context.setData(GDParamKeys.RSP_MSG,"数据库处理失败!！！");
                return;
            }
        }
        context.setData(GDParamKeys.RSP_CDE,Constants.RESPONSE_CODE_SUCC);
        context.setData(GDParamKeys.RSP_MSG,Constants.RESPONSE_MSG);
        context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
    }
}
