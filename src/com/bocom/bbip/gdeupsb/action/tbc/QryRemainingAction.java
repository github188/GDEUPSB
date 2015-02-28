package com.bocom.bbip.gdeupsb.action.tbc;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsThdTranCtlInfo;
import com.bocom.bbip.eups.repository.EupsThdTranCtlInfoRepository;
import com.bocom.bbip.service.BGSPServiceAccessObject;
import com.bocom.bbip.service.Result;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;


/**
 * 广东烟草公司查询客户余额
 * 
 * @version 1.0.0 
 * DateTime 2015-01-16
 * @author GuiLin.Li
 * 
 */
public class QryRemainingAction extends BaseAction {
    
    @Autowired
    BGSPServiceAccessObject serviceAccess;
    
    @Override
    public void execute(Context context) throws CoreException {
        log.info("QryRemaining Action start!");
        context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
        
        //转换
        context.setData("txnTme", context.getData("TRAN_TIME"));
        context.setData("bk", context.getData("BANK_ID"));
        context.setData("dptId", context.getData("DPT_ID"));
        context.setData("custId", context.getData("CUST_ID"));
        //TODO;以此确定comNo 现在测试直接传的是comNo
        context.setData("DevId", context.getData("DEV_ID"));
        context.setData("teller", context.getData("TELLER"));
        context.setData("brNo",context.getData("dptId").toString().substring(0, 3)+"999");
        
        EupsThdTranCtlInfo resultThdTranCtlInfo = get(EupsThdTranCtlInfoRepository.class).findOne(context.getData(ParamKeys.COMPANY_NO).toString());
        if (resultThdTranCtlInfo == null) {
            throw new CoreException(ErrorCodes.THD_CHL_NOT_FOUND);
        } 
        if (resultThdTranCtlInfo.getTxnCtlSts().equals(Constants.TXN_CTL_STS_SIGNOUT)) {
            throw new CoreException(ErrorCodes.THD_CHL_ALDEAY_SIGN_OUT);
        } else if (resultThdTranCtlInfo.getTxnCtlSts().equals(Constants.TXN_CTL_STS_CHECKBILL_ING)) {
            throw new CoreException(ErrorCodes.THD_CHL_SIGNIN_NOT_ALLOWWED);
        } else {
            //   检查该客户是否已签约
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("cusAc", context.getData("custId").toString());
            Result accessObject =  serviceAccess.callServiceFlatting("queryListAgentCollectAgreement", map);
            if (null == accessObject) {
                context.setData(ParamKeys.RSP_CDE,"9999");
                context.setData(ParamKeys.RSP_MSG,"该客户未开户!");
                context.setData("bal", "0");
                return;
            } 
            context.setData("tCusNm", accessObject.getData("cusNme"));
            context.setData("actNo", accessObject.getData("cusAc"));
            context.setData("dbPasWrd", accessObject.getData("pwd"));
            
            /*
             *     <!--构成网点号-->
     <Switch  expression="$BrNo">
       <Case value="441999">
         <Set>NodNo=441800</Set>
         <Break/>
       </Case>
       <Case value="444999">
         <Set>NodNo=444800</Set>
         <Break/>
       </Case>
       <Case value="446999">
         <Set>NodNo=446800</Set>
         <Break/>
       </Case>
       <Case value="445999">
         <Set>NodNo=445800</Set>
         <Break/>
       </Case>
       <Case value="483999">
         <Set>NodNo=483800</Set>
         <Break/>
       </Case>
       <Case value="484999">
         <Set>NodNo=484800</Set>
         <Break/>
       </Case>
       <Case value="485999">
         <Set>NodNo=485800</Set>
         <Break/>
       </Case>
       <Case value="491999">
         <Set>NodNo=491800</Set>
         <Break/>
       </Case>
       <Case value="476999">
         <Set>NodNo=476800</Set>
         <Break/>
       </Case>
     </Switch>
      <!--取电子柜员号-->
      <Set>TxnCnl=TBC</Set>       <!--取电子柜员号用-->
      <Set>TxnObj=OFRTTBCA</Set>
      <Exec func="PUB:GetVirtualTeller">
        <Arg name="TxnCnl" value="TBC"/>
      </Exec>
      <Set>TlrId=$TlrId</Set>
      <Set>TTxnCd=483803</Set>
           
      <!--校验各分行账号-->
      <!--441999广东省分行-->
      <!--444999珠海分行-->
      <!--446999佛山分行-->
      <!--445999汕头分行-->
      <!--483999东莞分行-->
      <!--484999中山分行-->
      <!--485999揭阳支行-->
      <!--491999惠州分行-->
      <!--761999江门分行-->
      <!--只有广州、珠海、佛山和中山有Acjud_行号函数，其他分行要加-->
      <Set>AcJudFunc=STRCAT(AcJud_,$BrNo)</Set>
      <Call function="$AcJudFunc">
        <Input name="ActNo|NewFlg|"/>
        <Output name="OActNo|ActCls|"/>
      </Call>
      <!--向主机查询账户信息-->
      <Switch expression="$ActCls">
        <Case value="2" /> <!-- 对私 -->
        <Case value="3" />
        <Case value="5" >
          <If condition="INTCMP($ActCls,3,3)">
             <Set>ActFlg=4</Set>  <!--对私卡号-->
             <Set>ActTyp=4</Set>
          </If>
          <Else>
             <Set>ActFlg=2</Set>  <!--对私帐号-->
             <Set>ActTyp=2</Set>
          </Else>
          <Set>CcyCod=CNY</Set>

          <!--上主机查询客户资料-->
          <Exec func="PUB:CallHostOther" error="IGNORE">
            <Arg name="HTxnCd" value="476520"/>
            <Arg name="ObjSvr" value="SHSTPUB1"/>
          </Exec>
          <If condition="$MsgTyp!=N">
            <Set>RspCod=460399</Set>
            <Set>RspMsg=帐户不存在</Set>
            <Set>Bal=0</Set>
            <Return/>
          </If>
          <Break/>
        </Case>
        <Case value="0" /> <!-- 对公 -->
        <Case value="4" >
          <Set>ActTyp=1</Set>
          <!--上主机查询客户资料-->
          <Exec func="PUB:CallHostOther" error="IGNORE">
            <Arg name="HTxnCd" value="109000"/>
            <Arg name="ObjSvr" value="SHSTPUB1"/>
          </Exec>
          <If condition="$MsgTyp!=N">
            <Set>RspCod=9999</Set>
            <Set>RspMsg=帐户不存在</Set>
            <Set>Bal=0</Set>
            <Return/>
          </If>
          <Break/>
        </Case>
        <Default>
            <Set>RspCod=9999</Set>
            <Set>RspMsg=帐户类型不存在</Set>
            <Set>Bal=0</Set>
            <Return/>
        </Default>
      </Switch>*/
            context.setData(ParamKeys.RSP_CDE,"0000");
            context.setData(ParamKeys.RSP_MSG,Constants.RESPONSE_MSG);
            context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
            
        }
    }
}
