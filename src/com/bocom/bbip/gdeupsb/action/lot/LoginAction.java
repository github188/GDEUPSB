package com.bocom.bbip.gdeupsb.action.lot;

import java.util.Date;
import java.util.Map;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.action.common.CommThdRspCdeAction;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.entity.GdLotSysCfg;
import com.bocom.bbip.gdeupsb.repository.GdLotSysCfgRepository;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.JumpException;
import com.bocom.jump.bp.channel.CommunicationException;
import com.bocom.jump.bp.channel.Transport;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

/**
 * 福彩用户登录
 * 
 * @version 1.0.0 Date 2015-01-26
 * @author GuiLin.Li
 */
public class LoginAction extends BaseAction {
    // dealId 运营商 141
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void execute(Context context) throws CoreException {
        log.info("LoginAction Start !!");
        context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
        context.setData("nodNo", "441800");
        GdLotSysCfg lotSysCfgInfo = get(GdLotSysCfgRepository.class).findOne(GDConstants.LOT_DEAL_ID);
        if (null == lotSysCfgInfo) {
            context.setData("msgTyp", Constants.RESPONSE_TYPE_FAIL);
            context.setData(ParamKeys.RSP_CDE, "LOT999");
            context.setData(ParamKeys.RSP_MSG, "查询参数表失败!!!");
            return;
        }
        //向福彩中心发出系统角色登录
        context.setData("type", "3");
        context.setData("action", "212");
        context.setData("version", "0");
        context.setData("dealer_id", GDConstants.LOT_DEAL_ID);
        context.setData("terminal_id", "0");
        context.setData("mobile", "0");
        context.setData("phone", "0");
        context.setData("sent_time", DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMddHHmmss));
       // context.setData("user", context.getData("usrPam"));
       // context.setData("pwd", context.getData("usrPas"));
        context.setData("usrPam", "tangdi");
        context.setData("usrPas", "123456");

        Transport ts = context.getService("STHDLOT1");
        Map<String,Object> thdReturnMessage = null;//申请当前期号，奖期信息下载
        try {
            thdReturnMessage = (Map<String, Object>) ts.submit(context.getDataMap(), context);
            context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
        } catch (CommunicationException e1) {
            e1.printStackTrace();
        } catch (JumpException e1) {
            e1.printStackTrace();
        }

        CommThdRspCdeAction cRspCdeAction = new CommThdRspCdeAction();
        String responseCode = cRspCdeAction.getThdRspCde(thdReturnMessage,  context.getData(ParamKeys.EUPS_BUSS_TYPE).toString());
        log.info("responseCode:["+responseCode+"]");
        if(!Constants.RESPONSE_CODE_SUCC.equals(responseCode)){
            log.info("QueryLot Fail!");
            context.setData("msgTyp", Constants.RESPONSE_TYPE_FAIL);
            context.setData(ParamKeys.RSP_CDE, "LOT999");
            context.setData(ParamKeys.RSP_MSG, "系统角色登录失败!!!");
            return;
        }
        
        //系统对时
        context.setData("action", "200");
        Transport transport = context.getService("STHDLOT1");
        Map<String,Object> thdMessage = null;//申请当前期号，奖期信息下载
        try {
            thdMessage = (Map<String, Object>) transport.submit(context.getDataMap(), context);
            context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
        } catch (CommunicationException e1) {
            e1.printStackTrace();
        } catch (JumpException e1) {
            e1.printStackTrace();
        }
        CommThdRspCdeAction cRspCde = new CommThdRspCdeAction();
        String thdResponsCode = cRspCde.getThdRspCde(thdMessage,  context.getData(ParamKeys.EUPS_BUSS_TYPE).toString());
        log.info("responseCode:["+thdResponsCode+"]");
        if(!Constants.RESPONSE_CODE_SUCC.equals(thdResponsCode)){
            log.info("Check Systerm Time  Fail!");
            context.setData("msgTyp", Constants.RESPONSE_TYPE_FAIL);
            context.setData(ParamKeys.RSP_CDE, "LOT999");
            context.setData(ParamKeys.RSP_MSG, "系统对时失败!!!");
            return;
        }
        
        //获取时间差函数调用，设置时间差
        String sigTim = DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMddHHmmss);
        String lclTim = sigTim;
        String nodNo = context.getData("nodNo");
        String brNo = context.getData("brNo");
        String lotTim = context.getData("lotTim");
        CommonLotAction commonLotAction = new CommonLotAction();
        String diffTm = commonLotAction.difTime(nodNo, brNo, lotTim, lclTim);
        context.setData("diffTm", diffTm);
        GdLotSysCfg lotSysCfgInfoInput = BeanUtils.toObject(context.getDataMap(), GdLotSysCfg.class);
        lotSysCfgInfoInput.setDealId(GDConstants.LOT_DEAL_ID);
        try {
            get(GdLotSysCfgRepository.class).update(lotSysCfgInfoInput);
        } catch (Exception e) {
            context.setData("MsgTyp", Constants.RESPONSE_TYPE_FAIL);
            context.setData(ParamKeys.RSP_CDE, "LOT999");
            context.setData(ParamKeys.RSP_MSG, "更新参数表失败!!!");
            return;
        }
        context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
    }
}
