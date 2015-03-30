package com.bocom.bbip.gdeupsb.action.lot;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.adaptor.ThirdPartyAdaptor;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.common.GDErrorCodes;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GdLotCusInf;
import com.bocom.bbip.gdeupsb.repository.GdLotCusInfRepository;
import com.bocom.bbip.utils.CollectionUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.bbip.utils.StringUtils;
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
            log.info("卡号:"+context.getData("crdNo").toString()+"没注册 !");
            throw new CoreException(GDErrorCodes.EUPS_LOT_CAR_NOT_REG);
        }
        
        //=====================手机号注册检查>>>
        GdLotCusInf gdLotCusInf = new GdLotCusInf();
        gdLotCusInf.setMobTel(context.getData("mobTel").toString());
        gdLotCusInf.setStatus("1");
        
        List<GdLotCusInf> gdlotCusInfs =get(GdLotCusInfRepository.class).find(gdLotCusInf);
        if (!CollectionUtils.isEmpty(gdlotCusInfs)) {
        	 log.info("手机号:"+context.getData("mobTel").toString()+"已注册 !");
             throw new CoreException(GDErrorCodes.EUPS_LOT_MOB_HAV_REG);
        }
        
        if (null == lotCusInf.getLotPsw()) {
            context.setData("lotPsw"," ");
        }
        // 向福彩中心发彩民注销 
        context.setData("eupsBusTyp", "LOTR01");
        context.setData("action", "219");
        Map<String,Object> resultMap = new HashMap<String, Object>();
        resultMap =get(ThirdPartyAdaptor.class).trade(context);
        String responseCode = resultMap.get(GDParamKeys.LOT_RESULT_CODE).toString();
        if (BPState.isBPStateOvertime(context)) {
            throw new CoreException(ErrorCodes.TRANSACTION_ERROR_TIMEOUT);
        } else if (!"0".equals(responseCode)) {
            if (StringUtils.isEmpty(responseCode)) {
                throw new CoreException(GDErrorCodes.EUPS_THD_SYS_ERROR);
            }
            log.info("LogOut Fail!");
            throw new CoreException(GDErrorCodes.EUPS_LOT_LOGOUT_FAIL);
        }
        //向福彩中心发用户注册
        context.setData("lotNam", context.getData("mobTel"));
        context.setData("regTim", DateUtils.format(new Date(),  DateUtils.STYLE_yyyyMMddHHmmss));
        context.setData("action", "201");

        Map<String,Object> map = new HashMap<String, Object>();
        map =get(ThirdPartyAdaptor.class).trade(context);
        String resCode = map.get(GDParamKeys.LOT_RESULT_CODE).toString();
        if (BPState.isBPStateOvertime(context)) {
            throw new CoreException(ErrorCodes.TRANSACTION_ERROR_TIMEOUT);
        } else if (!"0".equals(resCode)) {
            if (StringUtils.isEmpty(resCode)) {
                throw new CoreException(GDErrorCodes.EUPS_THD_SYS_ERROR);
            }
            log.info("Regist Fail!");
            throw new CoreException(GDErrorCodes.EUPS_LOT_REG_FAIL);
        }
        //更新用戶表
        GdLotCusInf lotCusInfInput = new GdLotCusInf();
        lotCusInfInput.setCrdNo(context.getData("crdNo").toString());
        lotCusInfInput.setCusNam(context.getData("cusNam").toString());
        lotCusInfInput.setIdTyp(context.getData("idTyp").toString());
        lotCusInfInput.setIdNo(context.getData("idNo").toString());
        lotCusInfInput.setRegTim(DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMddHHmmss));
        lotCusInfInput.setMobTel(context.getData("mobTel").toString());
        get(GdLotCusInfRepository.class).update(lotCusInfInput);
        context.setData("MsgTyp",Constants.RESPONSE_TYPE_SUCC);
        context.setData(ParamKeys.RSP_CDE,Constants.RESPONSE_CODE_SUCC);
        context.setData(ParamKeys.RSP_MSG,Constants.RESPONSE_MSG);
        context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
    }
}
