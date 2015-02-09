package com.bocom.bbip.gdeupsb.action.lot;

import java.util.Date;
import java.util.Map;

import com.bocom.bbip.eups.action.BaseAction;
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
        context.setData("eupsBusTyp", "LOTR01");
        context.setData("action", "212");
        // context.setData("usrPam", "tangdi"); 测试
        // context.setData("usrPas", "123456");

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
        
        if(!Constants.RESPONSE_CODE_SUCC.equals(resultMap.get("resultCode"))){
            log.info("QueryLot Fail!");
            context.setData("msgTyp", Constants.RESPONSE_TYPE_FAIL);
            context.setData(ParamKeys.RSP_CDE, "LOT999");
            context.setData(ParamKeys.RSP_MSG, "系统角色登录失败!!!");
            return;
        }
        
        //系统对时
        context.setData("action", "200");
        Transport transport = context.getService("STHDLOT1");
        Map<String,Object> map = null;//申请当前期号，奖期信息下载
        try {
            map = (Map<String, Object>) transport.submit(context.getDataMap(), context);
            context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
        } catch (CommunicationException e1) {
            e1.printStackTrace();
        } catch (JumpException e1) {
            e1.printStackTrace();
        }
       // Map<String, Object> map= get(ThirdPartyAdaptor.class).trade(context);

        if(!Constants.RESPONSE_CODE_SUCC.equals(map.get("resultCode"))){
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
