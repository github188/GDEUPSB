package com.bocom.bbip.gdeupsb.action.tbc;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.service.Result;
import com.bocom.bbip.service.Status;
import com.bocom.bbip.comp.BBIPPublicService;
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
import com.bocom.bbip.utils.DateUtils;
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

        GdTbcBasInf resultTbcBasInfo = get(GdTbcBasInfRepository.class).findOne(context.getData("dptId").toString());
        if (null == resultTbcBasInfo) {
            throw new CoreException(ErrorCodes.THD_CHL_NOT_FOUND);
        } 
        if (resultTbcBasInfo.getSigSts().equals(Constants.TXN_CTL_STS_SIGNOUT)) {
            throw new CoreException(ErrorCodes.THD_CHL_ALDEAY_SIGN_OUT);
        }
        
        //构成网点号
        String cAgtNo = CodeSwitchUtils.codeGenerator("GDYC_DPTID",  context.getData("dptId").toString());
        if (null == cAgtNo) {
            cAgtNo ="4410000560";
        }
        context.setData("cAgtNo", cAgtNo);
        String brNo = cAgtNo.substring(0,3)+"999";
        String nodNo = CodeSwitchUtils.codeGenerator("GDYC_nodSwitch",brNo);
        if (null == nodNo) {
            nodNo ="441800";
        }
        context.setData("nodNo",nodNo);
        // 获取电子柜员
        String bankId = context.getData(ParamKeys.BK).toString();
        context.setData("tTxnCd","483803");

        context.setData("txnCtlSts", "0");
        //   检查该客户是否已签约
        Map <String,Object> total= getMap();
        total.put("cusAc",  context.getData("actNo"));
        total.put("bk", bankId);
        total.put("br", context.getData(ParamKeys.BR));

        Result accessObject =  serviceAccess.callServiceFlatting("queryListAgentCollectAgreement", total);
        if (accessObject.getResponseCode().equals("BBIP0004AGPM66")) {
            Map<String, Object> establishMap = getMap();
            establishMap.put("agrChl","01");
            establishMap.put("oprTyp", "0");
            establishMap.put("cusTyp","1");
            establishMap.put("cusNo",context.getData("custId"));
            establishMap.put("cusNme",context.getData("tCusNm"));
            establishMap.put("idNo",context.getData("pasId"));
            establishMap.put("idTyp",context.getData("pasTyp"));
            establishMap.put("ccy","CNY");
            establishMap.put("bvCde","007");
            establishMap.put("bvNo","12345678");
            establishMap.put("ourOthFlg","0");
            establishMap.put("obkBk","441999");
            establishMap.put("cmuTel",context.getData("telNum").toString());
            establishMap.put("comNo",cAgtNo);
            establishMap.put("cusAc",context.getData("actNo"));
            establishMap.put("acTyp","3");
            establishMap.put("bk", bankId);
            establishMap.put("busKnd", "A087");//busKnd
            establishMap.put("busTyp", "0");
            establishMap.put("cusFeeDerFlg", "0");
            establishMap.put("agtSrvCusId", context.getData("telNum").toString());
            establishMap.put("agrVldDte", DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMdd));
            establishMap.put("agrExpDte", "21151230");
            establishMap.put("br", context.getData(ParamKeys.BR));

            //代收付系统接口调用增加协议
          
                Result operateAcpAgtResult = serviceAccess.callServiceFlatting("maintainAgentCollectAgreement", establishMap);
                log.info("===========respMap: " + operateAcpAgtResult.getPayload() + "===========");
                if (!operateAcpAgtResult.isSuccess()) {
                    Throwable e = operateAcpAgtResult.getException();
                    if (Status.SEND_ERROR == operateAcpAgtResult.getStatus()) {
                        context.setData("MsgTyp",Constants.RESPONSE_TYPE_FAIL);
                        context.setData(GDParamKeys.RSP_CDE,"9999");
                        context.setData(GDParamKeys.RSP_MSG,"Call acp transfor or other error: "+e);
                        return;
                    }
                    // 连接错误或等待超时,但不知道是否已上送,这里交易已处于未知状态
                    context.setState(BPState.BUSINESS_PROCESSNIG_STATE_UNKOWN_FAIL);
                    if (Status.TIMEOUT == operateAcpAgtResult.getStatus()) {
                        context.setData("MsgTyp",Constants.RESPONSE_TYPE_FAIL);
                        context.setData(GDParamKeys.RSP_CDE,"9999");
                        context.setData(GDParamKeys.RSP_MSG,"Call acp servcie occur time out.");
                        return;
                    }
                }
            //GDEUPS协议临时表添加数据
            GdTbcCusAgtInfo  cusAgtInfo =new  GdTbcCusAgtInfo();
            cusAgtInfo.setActNo(context.getData("actNo").toString());
            cusAgtInfo.setCustId(context.getData("custId").toString());
            cusAgtInfo.setCusNm(context.getData("tCusNm").toString());
            cusAgtInfo.setPasId(context.getData("pasId").toString());
            cusAgtInfo.setComId(context.getData("comId").toString());
            cusAgtInfo.setAccTyp(context.getData("accTyp").toString());
            cusAgtInfoRepository.insert(cusAgtInfo);
        } else {
            Map<String, Object> establishMap = getMap();
            establishMap.put("agrChl","01");
            establishMap.put("oprTyp", "1");//0新增 1修改 
            establishMap.put("cusTyp","0");
            establishMap.put("cusNo",context.getData("custId"));
            establishMap.put("cusNme",context.getData("tCusNm"));
            establishMap.put("idNo",context.getData("pasId"));
            establishMap.put("idTyp",context.getData("pasTyp"));
            establishMap.put("ccy","CNY");
            establishMap.put("bvCde","007");
            establishMap.put("bvNo","12345678");
            establishMap.put("ourOthFlg","0");
            establishMap.put("obkBk",bankId);
            establishMap.put("cmuTel",context.getData("telNum").toString());
            establishMap.put("comNo",cAgtNo);
            establishMap.put("cusAc",context.getData("actNo"));
            establishMap.put("acTyp","3");
            establishMap.put("bk", bankId);
            establishMap.put("busKnd", "A087");
            establishMap.put("busTyp", "0");
            establishMap.put("cusFeeDerFlg", "0");
            establishMap.put("agtSrvCusId", context.getData("telNum").toString());
            establishMap.put("agrVldDte", DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMdd));
            establishMap.put("agrExpDte", "21151230");
            establishMap.put("br", context.getData(ParamKeys.BR));

           
            Result result = serviceAccess.callServiceFlatting("maintainAgentCollectAgreement", establishMap);
            log.info("===========respMap: " + result.getPayload() + "===========");
            if (!result.isSuccess()) {
                Throwable e = result.getException();
                if (Status.SEND_ERROR == result.getStatus()) {
                    context.setData("MsgTyp",Constants.RESPONSE_TYPE_FAIL);
                    context.setData(GDParamKeys.RSP_CDE,"9999");
                    context.setData(GDParamKeys.RSP_MSG,"Call acp transfor or other error: "+e);
                    return;
                }
                // 连接错误或等待超时,但不知道是否已上送,这里交易已处于未知状态
                context.setState(BPState.BUSINESS_PROCESSNIG_STATE_UNKOWN_FAIL);
                if (Status.TIMEOUT == result.getStatus()) {
                    context.setData("MsgTyp",Constants.RESPONSE_TYPE_FAIL);
                    context.setData(GDParamKeys.RSP_CDE,"9999");
                    context.setData(GDParamKeys.RSP_MSG,"Call acp servcie occur time out.");
                    return;
                }
            }
            //GDEUPS协议临时表更改数据
            GdTbcCusAgtInfo  cusAgtInfo =new  GdTbcCusAgtInfo();
            cusAgtInfo.setActNo(context.getData("actNo").toString());
            cusAgtInfo.setCustId(context.getData("custId").toString());
            cusAgtInfo.setCusNm(context.getData("tCusNm").toString());
            cusAgtInfo.setPasId(context.getData("pasId").toString());
            cusAgtInfo.setComId(context.getData("comId").toString());
            cusAgtInfo.setAccTyp(context.getData("accTyp").toString());
            cusAgtInfoRepository.update(cusAgtInfo);
        }
        context.setData(GDParamKeys.RSP_CDE,Constants.RESPONSE_CODE_SUCC);
        context.setData(GDParamKeys.RSP_MSG,Constants.RESPONSE_MSG);
        context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
    }
    
    private Map<String, Object> getMap(){
        Map<String, Object> map =  new HashMap<String, Object>();
        map.put("traceNo", publicService.getTraceNo());
        map.put("traceSrc", "GDEUPSB");
        map.put("version","GDEUPSB-1.0.0");
        map.put("reqTme", new Date());
        map.put("reqJrnNo",  publicService.getBBIPSequence());
        map.put("reqSysCde", "SGRT00");
        map.put("tlr", "ABIR148");
        map.put("chn", "00");
        return map;
    }
}
