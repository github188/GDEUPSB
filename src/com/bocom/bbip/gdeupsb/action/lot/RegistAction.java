package com.bocom.bbip.gdeupsb.action.lot;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.entity.GdLotCusInf;
import com.bocom.bbip.gdeupsb.repository.GdLotCusInfRepository;
import com.bocom.bbip.gdeupsb.utils.CodeSwitchUtils;
import com.bocom.bbip.service.BGSPServiceAccessObject;
import com.bocom.bbip.service.Result;
import com.bocom.bbip.service.Status;
import com.bocom.bbip.utils.CollectionUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.euif.component.util.StringUtil;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

/**
 * 福彩用户注册
 * @version 1.0.0
 * Date 2015-01-26
 * @author GuiLin.Li
 */
public class RegistAction extends BaseAction{

    @Autowired
    BGSPServiceAccessObject serviceAccess;
    @Override
    public void execute (Context context) throws CoreException {
        log.info("==》》》》》》registAction Start !!==》》》》》》");
        context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
        GdLotCusInf lotCusInf = new GdLotCusInf();
        lotCusInf.setCrdNo(context.getData("crdNo").toString());
        lotCusInf.setStatus("1");
        
        List<GdLotCusInf> lotCusInfs =get(GdLotCusInfRepository.class).find(lotCusInf);
        if (!CollectionUtils.isEmpty(lotCusInfs)) {
            context.setData("msgTyp",Constants.RESPONSE_TYPE_FAIL);
            context.setData(ParamKeys.RSP_CDE,"LOT999");
            context.setData(ParamKeys.RSP_MSG,"卡号:"+context.getData("crdNo").toString()+"已注册 !!");
            return;
        }
        
        //=====================手机号注册检查>>>
        GdLotCusInf gdeupsbLotCusInf = new GdLotCusInf();
        gdeupsbLotCusInf.setMobTel(context.getData("mobTel").toString());
        gdeupsbLotCusInf.setStatus("1");
        
        List<GdLotCusInf> gdeupsblotCusInfs =get(GdLotCusInfRepository.class).find(gdeupsbLotCusInf);
        if (!CollectionUtils.isEmpty(gdeupsblotCusInfs)) {
            context.setData("msgTyp",Constants.RESPONSE_TYPE_FAIL);
            context.setData(ParamKeys.RSP_CDE,"LOT999");
            context.setData(ParamKeys.RSP_MSG,"手机号:"+context.getData("mobTel").toString()+"已注册 !!");
            return;
        }

        // <Set>OIdNo=$IdNo</Set> <!--保留上送的身份证号码-->
        context.setData("oIdNo", context.getData("idNo"));
        context.setData("tTxnCd", "207120");
        String teller = get(BBIPPublicService.class).getETeller(ParamKeys.BK);
        context.setData(ParamKeys.TELLER_ID, teller);
      //  Map<String, Object> establishMap = new HashMap<String, Object>();
        
      /*  Result operateAcpAgtResult = serviceAccess.callServiceFlatting("queryCustomeNoInfo", establishMap);
        log.info("===========respMap: " + operateAcpAgtResult.getPayload() + "===========");
        if (!operateAcpAgtResult.isSuccess()) {
            if (Status.SEND_ERROR == operateAcpAgtResult.getStatus()) {
                context.setData("msgTyp",Constants.RESPONSE_TYPE_FAIL);
                context.setData(ParamKeys.RSP_CDE,"LOT999");
                context.setData(ParamKeys.RSP_MSG,"查询客户开卡信息出错! ");
                throw new CoreException("查询客户开卡信息出错!");
            }
            // 连接错误或等待超时,但不知道是否已上送,这里交易已处于未知状态
            context.setState(BPState.BUSINESS_PROCESSNIG_STATE_UNKOWN_FAIL);
            if (Status.TIMEOUT == operateAcpAgtResult.getStatus()) {
                context.setData("msgTyp",Constants.RESPONSE_TYPE_FAIL);
                context.setData(ParamKeys.RSP_CDE,"LOT999");
                context.setData(ParamKeys.RSP_MSG,"Call acp servcie occur time out.");
                throw new CoreException("Call acp servcie occur time out.");
            }
        }*/
        //         <Set>CusNam=DELBOTHSPACE($ActNam)</Set> 前台及程序都没有 ActNam
        context.setData("cusNam",context.getData("cusNam").toString().trim());
        String idNo = context.getData("idNo").toString().trim();
        String oIdNo = context.getData("oIdNo").toString().trim();
        if (!idNo.equals(oIdNo)) {
            context.setData("msgTyp",Constants.RESPONSE_TYPE_FAIL);
            context.setData(ParamKeys.RSP_CDE,"LOT999");
            context.setData(ParamKeys.RSP_MSG,"身份证号码不符!!!");
            return;
        }
        //--登记福彩用户表--
        GdLotCusInf inputLotCusInf = new GdLotCusInf();
        inputLotCusInf.setBrNo(context.getData(ParamKeys.BK).toString());
        inputLotCusInf.setCusNam(context.getData("cusNam").toString().trim());
        inputLotCusInf.setCrdNo(context.getData("crdNo").toString().trim());
        inputLotCusInf.setActNo(context.getData("actNo").toString().trim());
        if (null != context.getData("nodNo").toString().trim()){
            inputLotCusInf.setActNod(context.getData("nodNo").toString().trim());
        }
        inputLotCusInf.setIdTyp(context.getData("idTyp").toString().trim());
        inputLotCusInf.setIdNo(context.getData("idNo").toString().trim());
        inputLotCusInf.setMobTel(context.getData("mobTel").toString().trim());
       
        if (null != context.getData("fixTel").toString()) {
            inputLotCusInf.setFixTel(context.getData("fixTel").toString().trim());
        }
        if (null != context.getData("email").toString()) {
            inputLotCusInf.setEmail(context.getData("email").toString().trim());
        }
        inputLotCusInf.setLotPsw("000000");
        inputLotCusInf.setBthday("20101111");
      
        inputLotCusInf.setRegTim(DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMddHHmmss));
        inputLotCusInf.setStatus("1");
        
        //TODO; 前台及程序都没有actNod
        String subNod =context.getData(ParamKeys.BR).toString().substring(2,5);
        //CodeSwitching
        String cityId = CodeSwitchUtils.codeGenerator("SubNod2CityId", subNod);
        if (StringUtil.isEmpty(cityId)) {
            context.setData("msgTyp",Constants.RESPONSE_TYPE_FAIL);
            context.setData(ParamKeys.RSP_CDE,"LOT999");
            context.setData(ParamKeys.RSP_MSG,"地市编码转换出错!");
            throw new CoreException("地市编码转换出错!");
        }
        String sex = "0";
        //gender 在何处赋值 TODO 
       // String gender = context.getData("gender").toString();
       /* if (gender.equals("0") || StringUtil .isEmptyOrNull(gender)) {
            sex="1";
        }else {
            sex="0";
        }*/
        inputLotCusInf.setCityId(cityId);
        inputLotCusInf.setSex(sex);
      
        try {
            get(GdLotCusInfRepository.class).insert(inputLotCusInf);
        } catch(Exception e) {
            context.setData("msgTyp",Constants.RESPONSE_TYPE_FAIL);
            context.setData(ParamKeys.RSP_CDE,"LOT999");
            context.setData(ParamKeys.RSP_MSG,"登记福彩用户表失败!!!");
            return;
        }
        //PUB:CallThirdOther 向福彩中心发出彩民注册
        //为了测试TODO
   /*     context.setData("eupsBusTyp", "LOTR01");
        context.setData("action", "201");
       
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
            log.info("Regist Fail!");
            context.setData("msgTyp", Constants.RESPONSE_TYPE_FAIL);
            context.setData(ParamKeys.RSP_CDE, "LOT999");
            context.setData(ParamKeys.RSP_MSG, "彩民注册失败!!!");
            return;
        }*/
   
        context.setData("msgTyp",Constants.RESPONSE_TYPE_SUCC);
        context.setData("lotNam",context.getData("mobTel"));
        context.setData(ParamKeys.RSP_CDE,Constants.RESPONSE_CODE_SUCC);
        context.setData(ParamKeys.RSP_MSG,Constants.RESPONSE_MSG);
        context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
    }
}
