package com.bocom.bbip.gdeupsb.action.transportfee;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class PreInvCancelAction extends BaseAction{

	private final static Log log = LogFactory.getLog(PreInvCancelAction.class);
	
	public void execute(Context ctx) throws CoreRuntimeException,CoreException{
		log.info("PreInvCancelAction start.......");
//		<Set>BrNo=444999</Set>
//        <Exec func="PUB:InitTransaction"></Exec>
//        <Set>ToDay=$ActDat</Set>
//        <Set>OLogNo=$LogNo</Set>
//        <!--按照前置流水号查询交易明细和扩展表数据-->
//        <Exec func="PUB:ReadRecordExt">
//           <Arg name="TblNam" value="rbfbtxnjnl444"/>
//           <Arg name="CndSts" value="RbfbQry"/>
//        </Exec>
//        <If condition="STRCMP($ActDat,$ToDay)">
//        <!--禁止跨日抹帐-->
//           <Set>RspCod=478699</Set>
//           <Set>RvsSts=8</Set>
//           <Set>RspMsg=该笔交易帐务日期与此时帐务日期不等，不能交易</Set>
//           <Return/>
//        </If>
		ctx.setData(GDParamKeys.BR_NO, GDConstants.BR_NO);
		
	}
}
