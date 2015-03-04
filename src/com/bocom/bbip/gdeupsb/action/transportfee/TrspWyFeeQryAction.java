package com.bocom.bbip.gdeupsb.action.transportfee;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.adaptor.ThirdPartyAdaptor;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class TrspWyFeeQryAction extends BaseAction{
	private final static Log log = LogFactory.getLog(TrspWyFeeQryAction.class);
	
//	private final String  TRANSPORT_FEE_QUERY_PROCESS= "gdeupsb.transportFeeQuery";
	
	@Autowired
	ThirdPartyAdaptor callThdTradeManager;
	
	public void execute(Context ctx) throws CoreRuntimeException,CoreException{
		log.info("TrspWyFeeQryAction start......");
//		<Set>BrNo=444999</Set>
//        <Set>CCSCod=TLU6</Set>
//        <Set>TTxnCd=484001</Set>
//        <Set>FeCod=484001</Set>
//        <Set>TrmNo=DVID</Set>

//        <Set>NodTrc=$tranFlowNo</Set>
//        <Set>TIATyp=T</Set>
//        <Set>AthLvl=00</Set>
//        <Set>CprInd=0</Set>
//        <Set>EnpInd=0</Set>
//        <Set>NodNo=444800</Set>
//        <Set>TrmVer=v0000001</Set>

//        <!--发送包体-->
//        <Set>plateNo_2=$plateNo</Set><!--缴费号码-->
//        <Set>plateType=$plateType</Set><!--车牌类别-->
//        <Set>monthCount=$monthCount</Set><!--缴费月数-->
		
//		TODO:
//			<Exec func="PUB:GetCommInf" error="IGNORE">
//        <Arg name="CndId"  value="STRCAT(sfbb_,car,_,$BrNo)"/>
//      </Exec>
//      <If condition="~RetCod!=0">
//        <Set>responseType=E</Set>
//        <Set>responseCode=SFA999</Set>
//        <Set>replyMessage=没对应分行IP设置</Set>
//        <Return/>
//      </If>
//       !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!get(BBIPPublicService.class).synExecute(TRANSPORT_FEE_QUERY_PROCESS, ctx);
       
		ctx.setData("transCode", "485007");
        Map<String, Object> thdReturnMessage = callThdTradeManager.trade(ctx);
        if("000000".equals(thdReturnMessage.get(GDParamKeys.TRSP_CD))){
//        	<Set>display_zone=STRCAT(该车辆具体信息如下：&lt;br&gt;车牌号码：[,$plateNo,] &lt;br&gt;车牌类别：[,$plateType,] 
//        	&lt;br&gt;缴费月数：[,$monthCount,] &lt;br&gt;有效起始日期：[,$BegDat,] &lt;br&gt;)</Set>
//         	  <Set>display_zone=STRCAT($display_zone,有效
//            <Set>RspMsg=交易成功</Set>结束日期：[,$EndDat,] &lt;br&gt;车型说明：[,$CarName,] &lt;br&gt;
//        	吨数或座数：[,$CarDzs,] &lt;br&gt;标准年收费额：[,$FeeStd,] &lt;br&gt;本金：[,$Corpus,] &lt;br&gt;
//        	滞纳金：[,AMTSIMPLEFMT($delayFee),] &lt;br&gt;总金额：[,AMTSIMPLEFMT($oweAmount),] &lt;br&gt;
//        	车主姓名：[,$custName,] &lt;br&gt;)</Set>
//            <Set>MsgTyp=N</Set>
        	 String plateNo = (String)thdReturnMessage.get("plateNo");
             String plateType = (String)thdReturnMessage.get("plateType");
             String monthCount = (String)thdReturnMessage.get("monthCount");
             String begDat = (String)thdReturnMessage.get("begDat");
             String endDat = (String)thdReturnMessage.get("endDat");
             String carName = (String)thdReturnMessage.get("carName");
            String sumMon = (String)thdReturnMessage.get("monthCount");
            String carOwner = (String)thdReturnMessage.get("custName");
        	ctx.setData("display_zone", "该车辆具体信息如下：车牌号码：" + plateNo + ",车牌类别:" + plateType + ",缴费月数:" + monthCount
        			+ ",有效起始日期:" + begDat + ",有效结束日期:" + endDat + ",车型说明:" + carName);
        	ctx.setData(GDParamKeys.CAR_NO, plateNo);
        	ctx.setData(GDParamKeys.CAR_TYP, plateType);
        	ctx.setData("sumMon", sumMon);
        	ctx.setData("carOwner", carOwner);
        }
        
//        <Set>MsgTyp=E</Set>
//        <If condition="IS_EQUAL_STRING($RspCod,000000)">
//        	  <Set>display_zone=STRCAT(该车辆具体信息如下：&lt;br&gt;车牌号码：[,$plateNo,] &lt;br&gt;车牌类别：[,$plateType,] &lt;br&gt;缴费月数：[,$monthCount,] &lt;br&gt;有效起始日期：[,$BegDat,] &lt;br&gt;)</Set>
//        	  <Set>display_zone=STRCAT($display_zone,有效
//           <Set>RspMsg=交易成功</Set>结束日期：[,$EndDat,] &lt;br&gt;车型说明：[,$CarName,] &lt;br&gt;吨数或座数：[,$CarDzs,] &lt;br&gt;标准年收费额：[,$FeeStd,] &lt;br&gt;本金：[,$Corpus,] &lt;br&gt;滞纳金：[,AMTSIMPLEFMT($delayFee),] &lt;br&gt;总金额：[,AMTSIMPLEFMT($oweAmount),] &lt;br&gt;车主姓名：[,$custName,] &lt;br&gt;)</Set>
//           <Set>MsgTyp=N</Set>
//           <!--返回包体-->
//        </If>

//        <Set>responseType=$MsgTyp</Set>
//        <Set>responseCode=$RspCod</Set>
//        <Set>replyMessage=$RspMsg</Set>
//        <!-- 如果是核心返回错误则返回说明 等于 返回码-->
//        <If condition="INTCMP(STRLEN($RspMsg),5,10)">
//          <If condition="SUBCMP($RspMsg,1,10,银行主机错)=0">
//              <Set>replyMessage=$RspCod</Set>
//          </If>
//        </If>
        
	}

}
