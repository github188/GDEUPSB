package com.bocom.bbip.gdeupsb.action.lot;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.entity.GdLotTxnJnl;
import com.bocom.bbip.gdeupsb.repository.GdLotTxnJnlRepository;
import com.bocom.bbip.service.BGSPServiceAccessObject;
import com.bocom.bbip.service.Result;
import com.bocom.bbip.service.Status;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.JumpException;
import com.bocom.jump.bp.channel.CommunicationException;
import com.bocom.jump.bp.channel.Transport;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

public class CathecticExeAction extends BaseAction{

    @Autowired
    BGSPServiceAccessObject serviceAccess;
    @Autowired
    BBIPPublicService publicService;
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void execute(Context context) throws CoreException {
        context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
        context.setData("tTxnCd", context.getData("cTTxnCd"));
        //客户签约状态
        Map<String, Object> map =getMap();
        map.put("cusAc", context.getData("crdNo").toString());
        Result accessObject = serviceAccess.callServiceFlatting("queryListAgentCollectAgreement", map);
        if (accessObject.getResponseCode().equals("BBIP0004AGPM66")) {
            context.setData(ParamKeys.RSP_CDE,"LOT999");
            context.setData(ParamKeys.RSP_MSG,"送主机记账失败（无法获取代收协议信息）");
            context.setData("tzCod", "TZ9006");
            throw new CoreException("送主机记账失败（无法获取代收协议信息）");
        }
        context.setData("busTyp","CBS52");
        context.setData("cnlTyp","L");
        context.setData("actFlg","4");
        context.setData("mask","9145");
        context.setData("actNo",context.getData("crdNo"));
        context.setData("vchTyp","000");
        context.setData("vchCod","00000000");
        context.setData("payMod","0");
        context.setData("ccyCod","CNY");
        context.setData("ccyTyp","1");
        context.setData("vchChk","0");
        context.setData("actSeq",context.getData("crdNo").toString().substring(13,18));
        context.setData("txnAmt",context.getData("betAmt"));
        context.setData("smr","彩票投注");
        context.setData("cAgtNo",context.getData("dSCAgtNo"));

        //取前置流水号
        String sqn = get(BBIPPublicService.class).getBBIPSequence();
        if (StringUtils.isEmpty(sqn)) {
            log.info("送主机记账失败（无法获取前置流水号）!!!");
            context.setData("MsgTyp",Constants.RESPONSE_TYPE_FAIL);
            context.setData(ParamKeys.RSP_CDE,"LOT999");
            context.setData(ParamKeys.RSP_MSG,"送主机记账失败（无法获取前置流水号）");
            context.setData("tzCod", "TZ9007");
            return;
        }
        // 上主机记账
        context.setData("accStatus", "N");
        Map<String, Object> inmap =getMap();
        inmap.put("busKnd", "test"); // 业务种类
        inmap.put("comNo", ParamKeys.BK); // 单位编号
        inmap.put("tfrDir", "1"); // 代付
        inmap.put("tfiCusAc",context.getData("crdNo")); // 收款帐号-轧差入账帐号
        inmap.put("tfiCusNme", " "); // 收款帐号户名
        inmap.put("fudDir", "1"); // 转出方向为转对公账户
        inmap.put("tfaAmt", context.getData("betAmt")); // 转账金额
        try {
            Result dsResult = get(BGSPServiceAccessObject.class).callServiceFlatting("acpFundsTransfer", inmap);
            if (dsResult.isSuccess()) {
                context.setData("accStatus","S");
                context.setData("txnSts","A");
                context.setData("tzCod", "TZ9011");
                context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
            } else if (Status.FAIL == dsResult.getStatus()) {
                context.setData("accStatus","F");
                context.setData("txnSts","F");
                context.setData("tzCod", "TZ9009");
                context.setData("AccMsg","卡可用余额不足");
                context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
            } else if (Status.SEND_ERROR == dsResult.getStatus() || Status.SYSTEM_ERROR == dsResult.getStatus()) {
                context.setData("accStatus","F");
                context.setData("txnSts","F");
                context.setData("tzCod", "TZ9008");
                context.setData("AccMsg","送主机记账失败（系统错误)");
                context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
  
            } else if (Status.TIMEOUT == dsResult.getStatus()) {
                context.setData("accStatus","T");
                context.setData("txnSts","T");
                context.setData("tzCod", "TZ9010");
                context.setData("AccMsg","送主机记账超时");
                context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
            } else {
                context.setData("accStatus","F");
                context.setData("txnSts","F");
                context.setData("tzCod", "TZ9012");
                context.setData("AccMsg","送主机记账失败（未知错误）");
                context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
            }
        } catch (Exception e) {
            log.info("未知异常");
        }

        String accStatus = context.getData("accStatus").toString();
  
        //更新扣账结果
        context.setData("hTxnSt",accStatus);
        GdLotTxnJnl lotTxnJnlInput = BeanUtils.toObject(context.getDataMap(), GdLotTxnJnl.class);
        try {
            get(GdLotTxnJnlRepository.class).update(lotTxnJnlInput);
        } catch (Exception e) {
            log.info("更新购彩流水异常!!!（账务结果）");
            context.setData("msgTyp",Constants.RESPONSE_TYPE_FAIL);
            context.setData(ParamKeys.RSP_CDE,"LOT999");
            context.setData(ParamKeys.RSP_MSG,"更新购彩流水异常!!!");
            context.setData("tzCod", "TZ9013");
            return;
        }
 
        if (!accStatus.equals("S")) {
            log.info("账务处理未成功");
            context.setData("MsgTyp",Constants.RESPONSE_TYPE_FAIL);
            context.setData(ParamKeys.RSP_CDE,"LOT999");
            context.setData(ParamKeys.RSP_MSG,context.getData("accMsg"));
            return;
        }

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
