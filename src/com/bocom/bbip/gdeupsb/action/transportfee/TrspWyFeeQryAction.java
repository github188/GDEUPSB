package com.bocom.bbip.gdeupsb.action.transportfee;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class TrspWyFeeQryAction extends BaseAction{
	private final static Log log = LogFactory.getLog(TrspWyFeeQryAction.class);
	
	private final String  TRANSPORT_FEE_QUERY_PROCESS= "gdeupsb.transportFeeQuery";
	
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
        get(BBIPPublicService.class).synExecute(TRANSPORT_FEE_QUERY_PROCESS, ctx);
	}

}
