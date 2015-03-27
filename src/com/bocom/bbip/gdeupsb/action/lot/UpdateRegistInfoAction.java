package com.bocom.bbip.gdeupsb.action.lot;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.adaptor.ThirdPartyAdaptor;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.entity.GdLotCusInf;
import com.bocom.bbip.gdeupsb.repository.GdLotCusInfRepository;
import com.bocom.bbip.utils.CollectionUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.JumpException;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

/**
 * 福彩更改用户注册 485405
 * @version 1.0.0
 * Date 2015-01-26
 * @author GuiLin.Li
 */
public class UpdateRegistInfoAction extends BaseAction {

    @Override
    public void execute (Context context) throws CoreException {
        log.info("==》》》》》》UpdateRegistInfoAction Start !!==》》》》》》");
        context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
        
        GdLotCusInf lotCusInf =get(GdLotCusInfRepository.class).findOne(context.getData("crdNo").toString());
        if (null == lotCusInf || lotCusInf.getStatus().equals("0")) {
            context.setData("MsgTyp",Constants.RESPONSE_TYPE_FAIL);
            context.setData(ParamKeys.RSP_CDE,"LOT999");
            context.setData(ParamKeys.RSP_MSG,"卡号:"+context.getData("crdNo").toString()+"未注册 !!");
            return;
        }
        
        //=====================手机号注册检查>>>
        GdLotCusInf gdLotCusInf = new GdLotCusInf();
        gdLotCusInf.setMobTel(context.getData("mobTel").toString());
        gdLotCusInf.setStatus("1");
        
        List<GdLotCusInf> gdlotCusInfs =get(GdLotCusInfRepository.class).find(gdLotCusInf);
        if (!CollectionUtils.isEmpty(gdlotCusInfs)) {
            context.setData("MsgTyp",Constants.RESPONSE_TYPE_FAIL);
            context.setData(ParamKeys.RSP_CDE,"LOT999");
            context.setData(ParamKeys.RSP_MSG,"手机号:"+context.getData("mobTel").toString()+"已注册 !!");
            return;
        }
        
        if (null == lotCusInf.getLotPsw()) {
            context.setData("lotPsw"," ");
        }
        // 向福彩中心发彩民注销 
        context.setData("eupsBusTyp", "LOTR01");
        context.setData("action", "219");
        Map<String,Object> resultMap = null;//申请当前期号，奖期信息下载
        try {
            resultMap =get(ThirdPartyAdaptor.class).trade(context);
            context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
        } catch (JumpException e1) {
            e1.printStackTrace();
        }  
        if(!Constants.RESPONSE_CODE_SUCC.equals(resultMap.get("resultCode"))){
            log.info("UpdateRegist Fail!");
            context.setData("msgTyp", Constants.RESPONSE_TYPE_FAIL);
            context.setData(ParamKeys.RSP_CDE, "LOT999");
            context.setData(ParamKeys.RSP_MSG, "彩民注销失败!!!");
            return;
        }
        //向福彩中心发用户注册
        context.setData("lotNam", context.getData("mobTel"));
        context.setData("regTim", DateUtils.format(new Date(),  DateUtils.STYLE_yyyyMMddHHmmss));
        context.setData("action", "201");

        Map<String,Object> map = null;//申请当前期号，奖期信息下载
        try {
            map =get(ThirdPartyAdaptor.class).trade(context);
            context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
        } catch (JumpException e1) {
            e1.printStackTrace();
        }  
        if(!Constants.RESPONSE_CODE_SUCC.equals(map.get("resultCode"))){
            log.info("Regist Fail!");
            context.setData("msgTyp", Constants.RESPONSE_TYPE_FAIL);
            context.setData(ParamKeys.RSP_CDE, "LOT999");
            context.setData(ParamKeys.RSP_MSG, "彩民注册失败!!!");
            return;
        }

        GdLotCusInf lotCusInfInput = new GdLotCusInf();
        lotCusInfInput.setCrdNo(context.getData("crdNo").toString());
        lotCusInfInput.setCusNam(context.getData("cusNam").toString());
        lotCusInfInput.setIdTyp(context.getData("idTyp").toString());
        lotCusInfInput.setIdNo(context.getData("idNo").toString());
        lotCusInfInput.setRegTim(DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMddHHmmss));
        lotCusInfInput.setMobTel(context.getData("mobTel").toString());
        
        try {
        get(GdLotCusInfRepository.class).update(lotCusInfInput);
        } catch (Exception e) {
            context.setData("MsgTyp",Constants.RESPONSE_TYPE_FAIL);
            context.setData(ParamKeys.RSP_CDE,"LOT999");
            context.setData(ParamKeys.RSP_MSG,"数据库操作错误 !!");
            return;
        }
        
        context.setData("MsgTyp",Constants.RESPONSE_TYPE_SUCC);
        context.setData(ParamKeys.RSP_CDE,Constants.RESPONSE_CODE_SUCC);
        context.setData(ParamKeys.RSP_MSG,Constants.RESPONSE_MSG);
        context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);

    }
}
