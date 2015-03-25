package com.bocom.bbip.gdeupsb.action.lot;

import java.util.HashMap;
import java.util.Map;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.entity.GdLotTxnJnl;
import com.bocom.bbip.gdeupsb.repository.GdLotTxnJnlRepository;
import com.bocom.bbip.service.BGSPServiceAccessObject;
import com.bocom.bbip.service.Result;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.jump.bp.JumpException;
import com.bocom.jump.bp.channel.CommunicationException;
import com.bocom.jump.bp.channel.Transport;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class CathecticCallThdAction extends BaseAction{

    @Override
    public void execute(Context context) throws CoreException, CoreRuntimeException {
        context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
        // TODO 向福彩中心发出购彩
/*        String lotTxnTim = DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMddHHmmss);
        context.setData("lotTxnTim", lotTxnTim);
        context.setData("action", "231");
        
        //向福彩中心发出购彩
        context.setData("eupsBusTyp", "LOTR01");
        Transport ts = context.getService("STHDLOT1");
        Map<String,Object> resultMap = null;
        try {
            resultMap = (Map<String, Object>) ts.submit(context.getDataMap(), context);
            context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
        } catch (CommunicationException e1) {
            e1.printStackTrace();
        } catch (JumpException e1) {
            e1.printStackTrace();
        }  
        if(!Constants.RESPONSE_CODE_SUCC.equals(resultMap.get("resultCode"))){
            log.info("向福彩中心发出购彩信息失败!");
            context.setData("msgTyp", Constants.RESPONSE_TYPE_FAIL);
            context.setData(ParamKeys.RSP_CDE, "LOT999");
            context.setData(ParamKeys.RSP_MSG, "向福彩中心发出购彩信息失败!");
            return;
        }
        */
        // 测试 Start 
        Map<String,Object> resultMap =new HashMap<String, Object>();
        resultMap.put("resultCode", "000000");
        //测试 end
        String sndStatus = "F";
        if(context.getState().equals(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL)){
            if(Constants.RESPONSE_CODE_SUCC.equals(resultMap.get("resultCode"))){
                log.info("QueryLot success!");
                sndStatus = "S";
                context.setData("sndMsg", "发送购彩成功");
                context.setData("txnSts", "S");
                context.setData("tTxnSt", "S");
            }else {
                sndStatus = "F";
                context.setData("sndMsg", resultMap.get("rRspMsg")+"["+resultMap.get("resultCode")+"]");
                if (resultMap.get("resultCode").equals("1618")){
                    context.setData("sndMsg", "快乐十分该期结束，请稍后购买");
                }
                context.setData("txnSts", "F");
                context.setData("tTxnSt", "F");
                context.setData("tzCod", "TZ9015");
            }
        }else{
            log.error("QueryLot return has error!");
            throw new CoreException(ErrorCodes.EUPS_THD_SYS_ERROR);
        }
       
        if(sndStatus.equals("F")&& null != context.getData("tckNo").toString()){
            context.setData("tzCod", "TZ9017");
            context.setData("tTxnCd", context.getData("cTTxnCd"));
            context.setData("hTxnCd", "471149");
            context.setData("oHLogNo", context.getData("hLogNo"));
            context.setData("oTckNo", context.getData("tckNo"));
            context.setData("tIATyp", "C");
            context.setData("oTTxnCd", context.getData("cTTxnCd"));
            context.setData("hLogNo", "");
            //上主机
            Map<String, Object> inmap = new HashMap<String, Object>();
            inmap.put("busKnd", "test"); // 业务种类
            inmap.put("comNo", ParamKeys.BK); // 单位编号
            inmap.put("tfrDir", "1"); // 代付
            inmap.put("tfiCusAc",context.getData("crdNo")); // 收款帐号-轧差入账帐号
            inmap.put("tfiCusNme", " "); // 收款帐号户名
            inmap.put("fudDir", "1"); // 转出方向为转对公账户
            inmap.put("tfaAmt", context.getData("betAmt")); // 转账金额

            Result dsResult = get(BGSPServiceAccessObject.class).callServiceFlatting("acpFundsTransfer", inmap);
            if (!dsResult.isSuccess()) {
                context.setData("accStatus","S");
            } else {
                context.setData("accStatus","F");
            }
        }
        context.setData("rRspCd", context.getData("rRspCod"));
        // 更新购彩结果
        GdLotTxnJnl inputLotTxnJnl = BeanUtils.toObject(context.getDataMap(), GdLotTxnJnl.class);
        try {
            get(GdLotTxnJnlRepository.class).update(inputLotTxnJnl);
        } catch (Exception e) {
            log.info("更新购彩流水状态为"+context.getData("txnSts").toString()+"$TxnSts,时失败");
            context.setData("MsgTyp",Constants.RESPONSE_TYPE_FAIL);
            context.setData(ParamKeys.RSP_CDE,"LOT999");
            context.setData(ParamKeys.RSP_MSG,"更新购彩流水状态为"+context.getData("txnSts").toString()+"$TxnSts,时失败");
            context.setData("tzCod", "TZ9016");
            return;
        }
        String txnStatus = context.getData("txnSts").toString();
        if (txnStatus.equals("S")) {
            context.setData("MsgTyp",Constants.RESPONSE_TYPE_SUCC);
            context.setData(ParamKeys.RSP_CDE,Constants.RESPONSE_CODE_SUCC);
            context.setData(ParamKeys.RSP_MSG,Constants.RESPONSE_MSG);

        }else {
            context.setData("MsgTyp",Constants.RESPONSE_TYPE_FAIL);
            context.setData(ParamKeys.RSP_CDE,"LOT999");
            context.setData(ParamKeys.RSP_MSG,"购彩失败");
        }
        context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
    }

}
