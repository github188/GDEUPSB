package com.bocom.bbip.gdeupsb.strategy.tbc;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsThdTranCtlInfo;
import com.bocom.bbip.eups.repository.EupsThdTranCtlInfoRepository;
import com.bocom.bbip.gdeupsb.action.tbc.FindClientRemainingMoney;
import com.bocom.bbip.service.BGSPServiceAccessObject;
import com.bocom.bbip.service.Result;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.Executable;
/***
 * 烟草划扣
 * @version 1.0.0
 * @author GuiLin.Li
 * @Date 2015-01-11
 */
public class DeductMoneyStrategyAction implements Executable{
    @Autowired
    EupsThdTranCtlInfoRepository eupsThdTranCtlInfoRepository;
    @Autowired
    BGSPServiceAccessObject serviceAccess;
    
    private final static Logger log = LoggerFactory.getLogger(FindClientRemainingMoney.class);
    @Override
    public void execute(Context context) throws CoreException {
        log.info("DeductMoneyStrategyAction start!");
        
        //转换
        context.setData("txnTme", context.getData("TRAN_TIME"));
        context.setData("bk", context.getData("BANK_ID"));
        context.setData("dptId", context.getData("DPT_ID"));
        //上面公共报文头，下面报文体
        context.setData("custId", context.getData("CUST_ID"));
        context.setData("tCusNm", context.getData("CUST_NAME"));
        context.setData("cusTyp", context.getData("CUST_TYPE"));
        context.setData("pasId", context.getData("PASS_ID"));
        context.setData("liceId", context.getData("LICE_ID"));
        context.setData("accTyp", context.getData("ACC_TYPE"));
        context.setData("actNo", context.getData("ACC"));
        //TODO;以此确定comNo 现在测试直接传的是comNo
        context.setData("DevId", context.getData("DEV_ID"));
        context.setData("teller", context.getData("TELLER"));

        context.setData(ParamKeys.THD_TXN_CDE,"483805");
        //TODO;
        context.setData("tCusId",context.getData("cusId"));
        //TODO; 待确定
        context.setData("rsFld2",context.getData("dptId"));
        context.setData(ParamKeys.TXN_DATE, context.getData(ParamKeys.TXN_TIME).toString().substring(0, 8));

        //检查状态 
        EupsThdTranCtlInfo eupsThdTranCtlInfo = eupsThdTranCtlInfoRepository.findOne(context.getData(ParamKeys.COMPANY_NO).toString());
        if (eupsThdTranCtlInfo == null) {
            throw new CoreException(ErrorCodes.THD_CHL_NOT_FOUND);
        } 
        if (!eupsThdTranCtlInfo.getTxnCtlSts().equals(Constants.TXN_CTL_STS_SIGNIN)) {
            throw new CoreException(ErrorCodes.EUPS_THD_TRANS_CTLINFO_NOTEXIST);
        } else {
            //协议检查 取客户信息资料
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("cusAc", context.getData("actNo").toString());
            Result accessObject =  serviceAccess.callServiceFlatting("queryListAgentCollectAgreement", map);
            if (null != accessObject) {
                context.setDataMap(accessObject.getPayload());
            } else {
                throw new CoreException("用户未开户！");
            }
            
            context.setData("payMod","0");
            context.setData(ParamKeys.CHL_TYP,"L");//<!--交易渠道类型：L第三方系统-->
            context.setData("vchChk","1");//<!--监督标志由业务上确定-->
            context.setData("cchCod","00000000");
            context.setData("aplCls","438");
            context.setData("mstChk","1");
            context.setData("itgTyp","0");
            context.setData(ParamKeys.TXN_TYP,Constants.RESPONSE_TYPE_SUCC);
            
            //TODO;
           /* <Call package="AFE" function="AFE_SglThdPay"/>
            <If condition="$RspCod=000000">
              <Set>RspCod=0000</Set>
              <Set>RspMsg=交易成功</Set>
              <Return/>
            </If>
            <!--主机记账超时-->
            <ElseIf condition="IS_EQUAL_STRING($HTxnSt,T)">
                    <Goto>CRCHOST</Goto>
            </ElseIf> 
    
            <Exec func="PUB:CodeSwitching">
            String comNo=CodeSwitchUtils.codeGenerator("eleGzComNoGen", "777");
              <Arg name="DatSrc"  value="OPFCSW"/>
              <Arg name="SourNam" value="RspCod"/>
              <Arg name="DestNam" value="RspMsg"/>
              <Arg name="TblNam"  value="RspCod2TRspMsg_YC"/>
            </Exec>
            <Set>RspCod=9999</Set>
            <Return/> <!--程序结束-->
                <Label>CRCHOST</Label> <!--只上主机冲正-->
                <Set>OLogNo=$LogNo</Set>
                <If condition="OR(INTCMP($ActTyp,3,0),INTCMP($ActTyp,3,2),INTCMP($ActTyp,3,4))"><!--对私-->
                  <Set>HTxnCd=471149</Set>
                </If>
                <ElseIf condition="INTCMP($ActTyp,3,1)"> <!--对公-->
                  <Set>HTxnCd=451619</Set>
                </ElseIf>
                <Else>                                   <!--第三方-->
                  <Return/>
                </Else>
             
                <Set>TIATyp=C</Set>
                <Exec func="PUB:BeginWork"/>    <!--启动完整性控制--> 
                <Exec func="PUB:CallHostAcc" error="IGNORE"><!--上送主机进行冲正交易-->
                  <Arg name="HTxnCd" value="959999"/>
                  <Arg name="ObjSvr" value="SHSTPUB1"/>
                </Exec>
                
               <If condition="IS_EQUAL_STRING(~RetCod,0)">    <!--上主机冲正成功-->
                 <Set>HTxnSt=C</Set>
                 <Set>TxnSts=C</Set>
                 <Exec func="PUB:UpdateJournal">
                 </Exec>
                 <If condition="~RetCod!=0">
                   <Set>RspCod=9999</Set>
                   <Set>RspMsg=数据库操作错误</Set>
                   <Return/>
                 </If>       
                 <Return/>
               </If>
               <Else>    <!--不成功登记自动冲正-->
                 <Exec func="PUB:DefaultErrorProc"/>            
               </Else>*/
        }
    }
}
