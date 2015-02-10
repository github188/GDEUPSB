package com.bocom.bbip.gdeupsb.action.lot;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.entity.GdLotCusInf;
import com.bocom.bbip.gdeupsb.repository.GdLotCusInfRepository;
import com.bocom.bbip.utils.CollectionUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.JumpException;
import com.bocom.jump.bp.channel.CommunicationException;
import com.bocom.jump.bp.channel.Transport;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

/**
 * 福彩用户注册
 * @version 1.0.0
 * Date 2015-01-26
 * @author GuiLin.Li
 */
public class RegistAction extends BaseAction{

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void execute (Context context) throws CoreException {
        log.info("==》》》》》》registAction Start !!==》》》》》》");
        context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
        GdLotCusInf lotCusInf = new GdLotCusInf();
        lotCusInf.setCrdNo(context.getData("crdNo").toString());
        lotCusInf.setStatus("1");
        
        List<GdLotCusInf> lotCusInfs =get(GdLotCusInfRepository.class).find(lotCusInf);
        if (!CollectionUtils.isEmpty(lotCusInfs)) {
            context.setData("MsgTyp",Constants.RESPONSE_TYPE_FAIL);
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
            context.setData("MsgTyp",Constants.RESPONSE_TYPE_FAIL);
            context.setData(ParamKeys.RSP_CDE,"LOT999");
            context.setData(ParamKeys.RSP_MSG,"手机号:"+context.getData("mobTel").toString()+"已注册 !!");
            return;
        }

        // <Set>OIdNo=$IdNo</Set> <!--保留上送的身份证号码-->
        context.setData("oIdNo", context.getData("idNo"));
        context.setData("tTxnCd", "207120");
        String teller = get(BBIPPublicService.class).getETeller("PIN");
        context.setData(ParamKeys.TELLER_ID, teller);
        //TODO  PUB:CallHostOther
        /*
        <Exec func="PUB:CallHostOther" error="IGNORE">
           <Arg name="HTxnCd" value="207120"/>
           <Arg name="ObjSvr" value="SHSTSSAA"/>
        </Exec>
        <If condition="~RetCod!=0">
           <Set>MsgTyp=E</Set>
           <Set>RspCod=LOT999</Set>
           <Set>RspMsg=查询客户开卡信息出错</Set>
           <Return/>
        </If>*/
        //         <Set>CusNam=DELBOTHSPACE($ActNam)</Set> 前台及程序都没有 ActNam
        context.setData("cusNam",context.getData("cusNam").toString().trim());
        String idNo = context.getData("idNo").toString().trim();
        String oIdNo = context.getData("oIdNo").toString().trim();
        if (!idNo.equals(oIdNo)) {
            context.setData("MsgTyp",Constants.RESPONSE_TYPE_FAIL);
            context.setData(ParamKeys.RSP_CDE,"LOT999");
            context.setData(ParamKeys.RSP_MSG,"身份证号码不符!!!");
            return;
        }
        //--登记福彩用户表--
        GdLotCusInf inputLotCusInf = new GdLotCusInf();
        inputLotCusInf.setBrNo(context.getData("bk").toString());
        inputLotCusInf.setCusNam(context.getData("cusNam").toString());
        inputLotCusInf.setCrdNo(context.getData("crdNo").toString());
        inputLotCusInf.setActNo(context.getData("actNo").toString());
        if (null != context.getData("nodNo").toString()){
            inputLotCusInf.setActNod(context.getData("nodNo").toString());
        }
        inputLotCusInf.setIdTyp(context.getData("idTyp").toString());
        inputLotCusInf.setIdNo(context.getData("idNo").toString());
        inputLotCusInf.setMobTel(context.getData("mobTel").toString());
       
        if (null != context.getData("fixTel").toString()) {
            inputLotCusInf.setFixTel(context.getData("fixTel").toString());
        }
        if (null != context.getData("email").toString()) {
            inputLotCusInf.setEmail(context.getData("email").toString());
        }
        inputLotCusInf.setLotPsw("000000");
        inputLotCusInf.setBthday("20101111");
        inputLotCusInf.setCityId("41");
        inputLotCusInf.setSex("1");
        inputLotCusInf.setRegTim(DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMddHHmmss));
        inputLotCusInf.setStatus("1");
        
        //TODO; 前台及程序都没有actNod
       // context.setData("subNod",context.getData("nodNo").toString().substring(0,3));
        //TODO;PUB:CodeSwitching
       /* <Exec func="PUB:CodeSwitching" error="IGNORE">
        <Arg name="DatSrc"  value="OPFCSW"/>
        <Arg name="SourNam" value="SubNod"/>
        <Arg name="DestNam" value="CityId"/>
        <Arg name="TblNam"  value="SubNod2CityId"/>
      </Exec>
      <If condition="~RetCod!=0">
        <Set>MsgTyp=E</Set>
        <Set>RspCod=LOT999</Set>
        <Set>RspMsg=地市编码转换出错</Set>
        <Return/>
      </If>
      <Exec func="PUB:CodeSwitching" error="IGNORE">
        <Arg name="DatSrc"  value="OPFCSW"/>
        <Arg name="SourNam" value="Gender"/>
        <Arg name="DestNam" value="Sex"/>
        <Arg name="TblNam"  value="Gender2Sex"/>
      </Exec>
      <If condition="~RetCod!=0">
        <Set>MsgTyp=E</Set>
        <Set>RspCod=LOT999</Set>
        <Set>RspMsg=性别转换出错</Set>
        <Return/>
      </If>*/
        try {
            get(GdLotCusInfRepository.class).insert(inputLotCusInf);
        } catch(Exception e) {
            context.setData("MsgTyp",Constants.RESPONSE_TYPE_FAIL);
            context.setData(ParamKeys.RSP_CDE,"LOT999");
            context.setData(ParamKeys.RSP_MSG,"登记福彩用户表失败!!!");
            return;
        }
        //PUB:CallThirdOther 向福彩中心发出彩民注册
        context.setData("eupsBusTyp", "LOTR01");
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
        }
   
        context.setData("MsgTyp",Constants.RESPONSE_TYPE_SUCC);
        context.setData(ParamKeys.RSP_CDE,Constants.RESPONSE_CODE_SUCC);
        context.setData(ParamKeys.RSP_MSG,Constants.RESPONSE_MSG);
        context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
    }
}
