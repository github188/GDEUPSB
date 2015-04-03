package com.bocom.bbip.gdeupsb.action.tbc;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.common.GDErrorCodes;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GdTbcBasInf;
import com.bocom.bbip.gdeupsb.repository.GdTbcBasInfRepository;
import com.bocom.bbip.gdeupsb.repository.GdTbcCusAgtInfoRepository;
import com.bocom.bbip.service.BGSPServiceAccessObject;
import com.bocom.bbip.service.Result;
import com.bocom.bbip.service.Status;
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
    @Autowired
    BBIPPublicService publicService;
    @Autowired
    GdTbcCusAgtInfoRepository cusAgtInfoRepository;
    
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
        context.setData("cusNam", context.getData("CUST_NAME"));
        context.setData("cusTyp", context.getData("CUST_TYPE"));
        context.setData("pasId", context.getData("PASS_ID"));
        context.setData("liceId", context.getData("LICE_ID"));
        context.setData("accTyp", context.getData("ACC_TYPE"));
        context.setData("actNo", context.getData("ACC"));
        context.setData("devId", context.getData("DEV_ID"));
        context.setData("teller", context.getData("TELLER"));

        //检查系统签到状态
        GdTbcBasInf resultTbcBasInfo = get(GdTbcBasInfRepository.class).findOne(context.getData("dptId").toString());
        if (null == resultTbcBasInfo) {
            throw new CoreException(ErrorCodes.THD_CHL_NOT_FOUND);
        } 
        if (resultTbcBasInfo.getSigSts().equals(Constants.TXN_CTL_STS_SIGNOUT)) {
            throw new CoreException(ErrorCodes.THD_CHL_ALDEAY_SIGN_OUT);
        }
        //客户签约状态
        Map<String, Object> map = getMap();
        map.put("cusAc", context.getData("actNo").toString());
        map.put("bk", context.getData(ParamKeys.BK));
        map.put("br", context.getData(ParamKeys.BR));
        Result accessObject = serviceAccess.callServiceFlatting("queryListAgentCollectAgreement", map);
        if (accessObject.getResponseCode().equals("BBIP0004AGPM66")){
            context.setData(GDParamKeys.RSP_CDE,"9999");
            context.setData(GDParamKeys.RSP_MSG,"账号已注销!！！");
            return; 
        }
        
        //检查用户名是否匹配
        String cusNme = context.getData("cusNam").toString().trim();
        String tCusNm = accessObject.getData("cusNme").toString().trim();
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
       
        Map<String, Object> destroyMap = getMap();
        destroyMap.put("bk", context.getData(ParamKeys.BK));
        destroyMap.put("br", context.getData(ParamKeys.BR));
        destroyMap.put("agdAgrNo", accessObject.getData("agdAgrNo").toString());
        
        Result result = serviceAccess.callServiceFlatting("deleteAgentCollectAgreement", destroyMap);
        log.info("===========respMap: " + result.getPayload() + "===========");
        
        if (!result.isSuccess()) {
            Throwable e = result.getException();
            if (Status.SEND_ERROR == result.getStatus()) {
                context.setData("MsgTyp",Constants.RESPONSE_TYPE_FAIL);
                context.setData(GDParamKeys.RSP_CDE,"9999");
                context.setData(GDParamKeys.RSP_MSG,GDErrorCodes.TBC_COM_OTHER_ERROR);
                log.error(GDErrorCodes.TBC_COM_OTHER_ERROR,e);
                throw new CoreException(GDErrorCodes.TBC_COM_OTHER_ERROR);
            }
            // 连接错误或等待超时,但不知道是否已上送,这里交易已处于未知状态
            context.setState(BPState.BUSINESS_PROCESSNIG_STATE_UNKOWN_FAIL);
            if (Status.TIMEOUT == result.getStatus()) {
                context.setData("MsgTyp",Constants.RESPONSE_TYPE_FAIL);
                context.setData(GDParamKeys.RSP_CDE,"9999");
                context.setData(GDParamKeys.RSP_MSG,GDErrorCodes.TBC_OUT_TIME_ERROR);
                log.error(GDErrorCodes.TBC_OUT_TIME_ERROR,e);
                throw new CoreException(GDErrorCodes.TBC_OUT_TIME_ERROR);
            }
        }
        //GDEUPS协议临时表删除数据
        cusAgtInfoRepository.delete(context.getData("custId").toString());
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
        map.put("tlr", "4411417");
        map.put("chn", "00");
        return map;
    }
}
