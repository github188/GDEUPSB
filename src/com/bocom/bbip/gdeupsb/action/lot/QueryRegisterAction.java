package com.bocom.bbip.gdeupsb.action.lot;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.action.common.CommThdRspCdeAction;
import com.bocom.bbip.eups.adaptor.ThirdPartyAdaptor;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.entity.GdLotCusInf;
import com.bocom.bbip.gdeupsb.repository.GdLotCusInfRepository;
import com.bocom.bbip.utils.CollectionUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

/**
 * 福彩用户注册查询  485406
 * @version 1.0.0
 * Date 2015-01-28
 * @author GuiLin.Li
 */
public class QueryRegisterAction  extends BaseAction{

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
        context.setData("type", "3");
        context.setData("action", "209");
        context.setData("version", "0");
        context.setData("dealer_id", GDConstants.LOT_DEAL_ID);
        context.setData("terminal_id", "0");
        context.setData("mobile", "0");
        context.setData("phone", "0");
        context.setData("sent_time", DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMddHHmmss));
        context.setData("gambler_name", context.getData("lotNam"));
        context.setData("gambler_pwd", context.getData("lotPsw"));
        context.setData("modify_time", context.getData("fTXNTm"));
        Map<String, Object> resultMap= get(ThirdPartyAdaptor.class).trade(context);
        CommThdRspCdeAction cRspCdeAction = new CommThdRspCdeAction();
        String responseCode = cRspCdeAction.getThdRspCde(resultMap,  context.getData(ParamKeys.EUPS_BUSS_TYPE).toString());
        log.info("responseCode:["+responseCode+"]");
        if(!Constants.RESPONSE_CODE_SUCC.equals(responseCode)){
            log.info("QueryLot Fail!");
            context.setData("msgTyp", Constants.RESPONSE_TYPE_FAIL);
            context.setData(ParamKeys.RSP_CDE, "LOT999");
            context.setData(ParamKeys.RSP_MSG, "系统角色登录失败!!!");
            return;
        }
          /* <Exec func="PUB:CallThirdOther"  error="IGNORE">
             <Arg name="HTxnCd" value="209"/>
             <Arg name="ObjSvr" value="STHDLOTA"/>
           </Exec>
           <If condition="~RetCod=-1">
              <Exec func="PUB:RollbackWork" error="IGNORE"/>
              <Set>MsgTyp=E</Set>
              <Set>RspCod=LOT999</Set>
              <Set>RspMsg=彩民查询超时</Set>
              <Return/>
           </If>
           <If condition="~RetCod!=0">
              <Exec func="PUB:RollbackWork" error="IGNORE"/>
              <Set>MsgTyp=E</Set>
              <Set>RspCod=LOT999</Set>
              <Set>RspMsg=彩民查询失败</Set>
              <Return/>
           </If>*/
        context.setData("MsgTyp",Constants.RESPONSE_TYPE_SUCC);
        context.setData(ParamKeys.RSP_CDE,Constants.RESPONSE_CODE_SUCC);
        context.setData(ParamKeys.RSP_MSG,Constants.RESPONSE_MSG);
        context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);

    } 
}