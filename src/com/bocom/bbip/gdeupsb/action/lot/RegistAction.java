package com.bocom.bbip.gdeupsb.action.lot;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.comp.BBIPPublicService;
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
import com.bocom.bbip.service.BGSPServiceAccessObject;
import com.bocom.bbip.utils.CollectionUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.euif.component.util.StringUtil;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

/**
 * 福彩用户注册 485404
 * 
 * @version 1.0.0 Date 2015-01-26
 * @author GuiLin.Li
 */
public class RegistAction extends BaseAction {

    @Autowired
    BGSPServiceAccessObject serviceAccess;

    @Override
    public void execute(Context context) throws CoreException {
        log.info("==》registAction Start !!》》》》》》");
        context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
        GdLotCusInf lotCusInf = new GdLotCusInf();
        lotCusInf.setCrdNo(context.getData("crdNo").toString());
        lotCusInf.setStatus("1");

        List<GdLotCusInf> lotCusInfs = get(GdLotCusInfRepository.class).find(lotCusInf);
        if (!CollectionUtils.isEmpty(lotCusInfs)) {
            log.info("此卡号:"+context.getData("crdNo").toString()+"已注册 !");
            throw new CoreException(GDErrorCodes.EUPS_LOT_CAR_HAV_REG);
        }
        //手机号注册检查>>>
        GdLotCusInf gdeupsbLotCusInf = new GdLotCusInf();
        gdeupsbLotCusInf.setMobTel(context.getData("mobTel").toString());
        gdeupsbLotCusInf.setStatus("1");

        List<GdLotCusInf> gdeupsblotCusInfs = get(GdLotCusInfRepository.class).find(gdeupsbLotCusInf);
        if (!CollectionUtils.isEmpty(gdeupsblotCusInfs)) {
            log.info("手机号:"+context.getData("mobTel").toString()+"已注册 !");
            throw new CoreException(GDErrorCodes.EUPS_LOT_MOB_HAV_REG);
        }

        // 保留上送的身份证号
        context.setData("oIdNo", context.getData("idNo"));
        context.setData("tTxnCd", "207120");
        String teller = get(BBIPPublicService.class).getETeller(ParamKeys.BK);
        context.setData(ParamKeys.TELLER_ID, teller);
        // Map<String, Object> establishMap = new HashMap<String, Object>();

       /*
          Result operateAcpAgtResult =
          serviceAccess.callServiceFlatting("queryCustomeNoInfo",
          establishMap); log.info("===========respMap: " +
          operateAcpAgtResult.getPayload() + "==========="); if
          (!operateAcpAgtResult.isSuccess()) { if (Status.SEND_ERROR ==
          operateAcpAgtResult.getStatus()) {
          context.setData("msgTyp",Constants.RESPONSE_TYPE_FAIL);
          context.setData(ParamKeys.RSP_CDE,"LOT999");
          context.setData(ParamKeys.RSP_MSG,"查询客户开卡信息出错! "); throw new
          CoreException("查询客户开卡信息出错!"); } 
          // 连接错误或等待超时,但不知道是否已上送,这里交易已处于未知状态
          context.setState(BPState.BUSINESS_PROCESSNIG_STATE_UNKOWN_FAIL); if
          (Status.TIMEOUT == operateAcpAgtResult.getStatus()) {
	           context.setData("msgTyp",Constants.RESPONSE_TYPE_FAIL);
	           context.setData(ParamKeys.RSP_CDE,"LOT999");
	           context.setData(ParamKeys.RSP_MSG,"Call acp servcie occur time out."); 
	           throw new CoreException("Call acp servcie occur time out.");
           } 
        }
        // <Set>CusNam=DELBOTHSPACE($ActNam)</Set> 前台及程序都没有 ActNam
        String idNo = context.getData("idNo").toString().trim();
        String oIdNo = context.getData("oIdNo").toString().trim();
        if (!idNo.equals(oIdNo)) {
        	 log.info("身份证号码不符!");
             throw new CoreException(GDErrorCodes.EUPS_LOT_IDEN_NOEXIST);
        }*/
        // --登记福彩用户表--
        GdLotCusInf inputLotCusInf = new GdLotCusInf();
        inputLotCusInf.setBrNo(context.getData(ParamKeys.BK).toString());
        inputLotCusInf.setCusNam(context.getData("cusNam").toString().trim());
        inputLotCusInf.setCrdNo(context.getData("crdNo").toString().trim());
        inputLotCusInf.setActNo(context.getData("actNo").toString().trim());
        if (null != context.getData("nodNo").toString().trim()) {
            inputLotCusInf.setActNod(context.getData("nodNo").toString().trim());
        }
        inputLotCusInf.setIdTyp(context.getData("idTyp").toString().trim());
        inputLotCusInf.setIdNo(context.getData("idNo").toString().trim());
        String mobPh= context.getData("mobTel").toString().trim();
        inputLotCusInf.setMobTel(mobPh);
        String seqrecNo =((BTPService)get("BTPService")).applyBatchNo(ParamKeys.BUSINESS_CODE_COLLECTION);
        String seqrecNoSub = seqrecNo.replace(ParamKeys.BUSINESS_CODE_COLLECTION,"");
        if (seqrecNoSub.length()<10) {
        	seqrecNoSub =GdExpCommonUtils.AddChar(seqrecNoSub, 10, '0', '1');
        }
        String lotNam ="LOT"+seqrecNoSub.substring(0,6)+seqrecNoSub.substring(seqrecNoSub.length()-4, seqrecNoSub.length());
        inputLotCusInf.setLotNam(lotNam);
        context.setData("lotNam",lotNam);
        if (null != context.getData("fixTel").toString()) {
            inputLotCusInf.setFixTel(context.getData("fixTel").toString().trim());
        }
        if (null != context.getData("email").toString()) {
            inputLotCusInf.setEmail(context.getData("email").toString().trim());
        }
        inputLotCusInf.setLotPsw("000000");
        inputLotCusInf.setBthday("20101111");
        Date regTim = new Date();
        inputLotCusInf.setRegTim(DateUtils.format(regTim, DateUtils.STYLE_yyyyMMddHHmmss));
        inputLotCusInf.setStatus("1");

        String subNod = context.getData(ParamKeys.BR).toString().substring(2, 5);
        String cityId = CodeSwitchUtils.codeGenerator("SubNod2CityId", subNod);
        if (StringUtil.isEmpty(cityId)) {
        	log.info( "地市编码转换出错!");
        	throw new CoreException(GDErrorCodes.EUPS_LOT_SWIC_CITYNO_ERROR);
        }
        context.setData("city_id", cityId);
        String sex = "0";
        String gender ="1";// gender 在何处赋值 TODO
        if (gender.equals("0") || StringUtil .isEmptyOrNull(gender)) {
            sex="1";
        }
        inputLotCusInf.setCityId(cityId);
        inputLotCusInf.setSex(sex);
        context.setData("sex", sex);
        try {
            get(GdLotCusInfRepository.class).insert(inputLotCusInf);
        } catch (Exception e) {
        	log.info( "登记福彩用户表失败!");
        	throw new CoreException(GDErrorCodes.EUPS_LOT_INSERT_USERINFO_FAIL);
        }
        // 向福彩中心发出彩民注册
        context.setData(ParamKeys.EUPS_BUSS_TYPE, "LOTR01");
        context.setData("action", "201");
        context.setData("version", "");
        context.setData("lotPsw","000000");
        context.setData("sent_time", DateUtils.format(new Date(), DateUtils.STYLE_FULL));
        context.setData("dealId", "141");
        context.setData("regTim", DateUtils.format(regTim, DateUtils.STYLE_FULL));
        String idTyp = context.getData("idTyp");
        String lotIdTyp = CodeSwitchUtils.codeGenerator("IdTyp2LotIdTyp", idTyp);
        if (null ==lotIdTyp) {
            lotIdTyp="1";
        }
        context.setData("real_name",context.getData("cusNam").toString().trim());
        context.setData("lotIdTyp", lotIdTyp);
        Map<String, Object> resultMap = new HashMap<String, Object>(); 
        resultMap = get(ThirdPartyAdaptor.class).trade(context);
        String responseCode = resultMap.get(GDParamKeys.LOT_RESULT_CODE).toString();
        if (BPState.isBPStateOvertime(context)) {
            throw new CoreException(ErrorCodes.TRANSACTION_ERROR_TIMEOUT);
        } else if (!"0".equals(responseCode)) {
            if (StringUtils.isEmpty(responseCode)) {
                throw new CoreException(GDErrorCodes.EUPS_THD_SYS_ERROR);
            }
            if ("1500".equals(responseCode)) {
            	log.error("用户名已注册！");
                throw new CoreException(GDErrorCodes.EUPS_LOT_USER_HAV_REG);
            }
            log.info("Regist Fail!");
            throw new CoreException(GDErrorCodes.EUPS_LOT_REG_FAIL);
        }
        context.setData("msgTyp", Constants.RESPONSE_TYPE_SUCC);
        context.setData("rspCod", Constants.RESPONSE_CODE_SUCC);
        context.setData(ParamKeys.RSP_MSG, Constants.RESPONSE_MSG);
        context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
    }
}
