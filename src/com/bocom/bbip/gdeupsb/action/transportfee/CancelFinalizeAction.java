package com.bocom.bbip.gdeupsb.action.transportfee;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class CancelFinalizeAction extends BaseAction{
	private static final Log log = LogFactory.getLog(CancelFinalizeAction.class);
	
	public void execute(Context ctx) throws CoreException,CoreRuntimeException{
		log.info("CancelFinalizeAction start......");
	}
//	TODO: <Set>FilNam=STRCAT(INVO,$TlrId,00)</Set>
//     <Exec func="PUB:GenerateReport" error="IGNORE">
//        <Arg name="ObjFil"  value="STRCAT($TSDir,$FilNam)"/>
//        <Arg name="FmtFil"  value="etc/BRBF_RPT001.XML"/>
//        <Arg name="LogNo"   value="$LogNo"/>
//        <Arg name="PrtDat"  value="GETDATE()"/>
//     </Exec>
//     <If condition="~RetCod=-1">
//        <Exec func="PUB:DefaultErrorProc"/>
//        <Set>MsgTyp=E</Set>
//        <Set>RspCod=329999</Set>
//        <Set>RspMsg=报表文件生成错误</Set>
//        <Return/>
//     </If>
//     
//     <Exec func="PUB:SendFileMessage2" error="IGNORE"><!--发出文件指令-->
//        <Arg name="MsgTyp" value="3"/><!--3不打印 4打印-->
//        <Arg name="ObjNod" value="$NodNo"/><!-- 目标网点-->
//        <Arg name="BusTyp" value="SUBSTR($TxnCod,1,2)"/><!--业务类型-->
//        <Arg name="AplCod" value="SUBSTR($TxnCod,3,4)"/><!-- 应用码-->
//        <Arg name="FilNam" value="$FilNam"/><!-- 文件名-->
//        <Arg name="Summary" value="路桥退费凭证"/><!-- 消息内容-->
//        <Arg name="ObjTlr" value="$TlrId"/><!-- 目标柜员-->
//     </Exec>
//     <If condition="~RetCod!=0">
//        <Exec func="PUB:DefaultErrorProc"/>
//        <Set>MsgTyp=E</Set>
//        <Set>RspCod=329999</Set>
//        <Set>RspMsg=发送文件失败</Set>
//        <Return/>
//     </If>
//     
//     <Set>MsgTyp=N</Set>
//     <Set>RspCod=000000</Set>
//     <Set>RspMsg=交易成功</Set>
}
