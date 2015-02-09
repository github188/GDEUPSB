package com.bocom.bbip.gdeupsb.action.lot;

import java.util.List;
import java.util.Map;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.entity.GdLotCusInf;
import com.bocom.bbip.gdeupsb.repository.GdLotCusInfRepository;
import com.bocom.bbip.utils.CollectionUtils;
import com.bocom.jump.bp.JumpException;
import com.bocom.jump.bp.channel.CommunicationException;
import com.bocom.jump.bp.channel.Transport;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

/**
 * 福彩用户注册查询  485406
 * @version 1.0.0
 * Date 2015-01-28
 * @author GuiLin.Li
 */
public class QueryRegisterAction  extends BaseAction{

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void execute (Context context) throws CoreException {
        log.info("==》》》》》》QueryRegisterAction Start !!==》》》》》》");
        context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
        
        GdLotCusInf lotCusInf = new GdLotCusInf();
        lotCusInf.setCrdNo(context.getData("crdNo").toString());
        lotCusInf.setStatus("1");
        
        List<GdLotCusInf> lotCusInfs =get(GdLotCusInfRepository.class).find(lotCusInf);
        if (CollectionUtils.isEmpty(lotCusInfs)) {
            context.setData("MsgTyp",Constants.RESPONSE_TYPE_FAIL);
            context.setData(ParamKeys.RSP_CDE,"LOT999");
            context.setData(ParamKeys.RSP_MSG,"卡号:"+context.getData("crdNo").toString()+"没注册 !!");
            return;
        }

        //向福彩中心发送彩民信息查询
        context.setData("action", "209");
        context.setData("eupsBusTyp", "LOTR01");
        context.setData("gambler_name", context.getData("lotNam"));
        context.setData("gambler_pwd", context.getData("lotPsw"));
        context.setData("modify_time", context.getData("fTXNTm"));

        Transport ts = context.getService("STHDLOT1");
        Map<String,Object> resultMap = null;//申请当前期号，奖期信息下载
        try {
            resultMap = (Map<String, Object>) ts.submit(context.getDataMap(), context);
            context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
        } catch (CommunicationException e1) {
            e1.printStackTrace();
        } catch (JumpException e1) {
            e1.printStackTrace();
        }  
        if(BPState.isBPStateOvertime(context)){
            throw new CoreException(ErrorCodes.TRANSACTION_ERROR_TIMEOUT);
        }else if(!((Constants.RESPONSE_CODE_SUCC).equals(resultMap.get("resultCode")))){
            log.info("QueryLot Fail!");
            context.setData("msgTyp", Constants.RESPONSE_TYPE_FAIL);
            context.setData(ParamKeys.RSP_CDE, "LOT999");
            context.setData(ParamKeys.RSP_MSG, "彩民查询失败!!!");
            return;
        }
        
        context.setData("MsgTyp",Constants.RESPONSE_TYPE_SUCC);
        context.setData(ParamKeys.RSP_CDE,Constants.RESPONSE_CODE_SUCC);
        context.setData(ParamKeys.RSP_MSG,Constants.RESPONSE_MSG);
        context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);

    } 
}