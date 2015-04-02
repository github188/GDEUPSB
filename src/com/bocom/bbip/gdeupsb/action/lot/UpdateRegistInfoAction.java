package com.bocom.bbip.gdeupsb.action.lot;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bocom.bbip.comp.btp.BTPService;
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
import com.bocom.bbip.gdeupsb.utils.CodeSwitchUtils;
import com.bocom.bbip.gdeupsb.utils.GdExpCommonUtils;
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
        
        GdLotCusInf gdLotCusInfo = new GdLotCusInf();
        gdLotCusInfo.setCrdNo(context.getData("crdNo").toString());
        gdLotCusInfo.setStatus("1");
        List<GdLotCusInf> lotCusInfos =get(GdLotCusInfRepository.class).find(gdLotCusInfo);
        if (CollectionUtils.isEmpty(lotCusInfos)) {
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
        //注销数据准备
        if (null == lotCusInfos.get(0).getLotPsw()) {
            context.setData("gambler_pwd","000000");
        } else {
        	context.setData("gambler_pwd",lotCusInfos.get(0).getLotPsw());
        }
        context.setData("gambler_name", lotCusInfos.get(0).getLotNam());
        String modifyTime = DateUtils.format(new Date(), DateUtils.STYLE_FULL);
        context.setData("sent_time", modifyTime);
        context.setData("modify_time", modifyTime);
        context.setData("version", "");
        // 向福彩中心发彩民注销 
        context.setData(ParamKeys.EUPS_BUSS_TYPE, "LOTR01");
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
            if ("1721".equals(responseCode)) {
            	 log.info("用户未注册请先注册");
                throw new CoreException(GDErrorCodes.EUPS_LOT_NOT_REG);
            }
            log.info("LogOut Fail!");
            throw new CoreException(GDErrorCodes.EUPS_LOT_LOGOUT_FAIL);
        }
        
        //向福彩中心发用户注册
        context.setData("real_name",context.getData("cusNam").toString().trim());
        String idTyp = context.getData("idTyp");
        String lotIdTyp = CodeSwitchUtils.codeGenerator("IdTyp2LotIdTyp", idTyp);
        if (null ==lotIdTyp) {
            lotIdTyp="1";
        }

        context.setData("lotIdTyp", lotIdTyp);
        context.setData("sex",lotCusInfos.get(0).getSex());
        context.setData("city_id",lotCusInfos.get(0).getCityId());
        context.setData("version", "");
        context.setData("dealId", "141");
        String seqrecNo =((BTPService)get("BTPService")).applyBatchNo(ParamKeys.BUSINESS_CODE_COLLECTION);
        String seqrecNoSub = seqrecNo.replace(ParamKeys.BUSINESS_CODE_COLLECTION,"");
        if (seqrecNoSub.length()<10) {
        	seqrecNoSub =GdExpCommonUtils.AddChar(seqrecNoSub, 10, '0', '1');
        }
        String lotNam ="LOT"+seqrecNoSub.substring(0, 10);
        context.setData("lotNam",lotNam);
        Date regDate = new Date();
        String regTime= DateUtils.format(regDate, DateUtils.STYLE_FULL);
        context.setData("sent_time",regTime);
        context.setData("regTim", regTime);
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
        lotCusInfInput.setRegTim(DateUtils.format(regDate, DateUtils.STYLE_yyyyMMddHHmmss));
        lotCusInfInput.setMobTel(context.getData("mobTel").toString());
        get(GdLotCusInfRepository.class).update(lotCusInfInput);
        context.setData("MsgTyp",Constants.RESPONSE_TYPE_SUCC);
        context.setData("rspCod",Constants.RESPONSE_CODE_SUCC);
        context.setData(ParamKeys.RSP_MSG,Constants.RESPONSE_MSG);
        context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
    }
}
