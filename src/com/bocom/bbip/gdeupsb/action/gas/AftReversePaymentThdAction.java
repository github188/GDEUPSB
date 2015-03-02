package com.bocom.bbip.gdeupsb.action.gas;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ch.qos.logback.classic.Logger;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsTransJournal;
import com.bocom.bbip.eups.repository.EupsTransJournalRepository;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class AftReversePaymentThdAction extends BaseAction{

	private static final Log logger=LogFactory.getLog(AftReversePaymentThdAction.class);


	private EupsTransJournal etj;
	
	public void execute(Context context) throws CoreException, CoreRuntimeException{
		
//		logger.info("there is nothing to show u");
 		//    <If condition="IS_EQUAL_STRING(~RetCod,0)">    <!--上主机冲正成功-->
		//    <Set>HTxnSt=C</Set>   <!--  U-预计,S-成功,F-失败,T-超时,R-已冲正,C-抹账  -->
		//    <Set>TxnSts=C</Set>
		//    <Set>Result=UPay</Set>
		//    <Set>TxnAmt1=$TxnAmt</Set>
		//    <Set>Status=3</Set>
		//    <Exec func="PUB:ExecSql" error="IGNORE">
		//       <Arg name="SqlCmd"   value="UdpEfeaSts"/>   <!--更新流水表-->
		//    </Exec>
		//    <If condition="~RetCod=-1">
		//      <Set>MsgTyp=E</Set>
		//      <Set>RspCod=GAS999</Set>
		//      <Set>RspMsg=数据库处理失败!</Set>
		//      <Return/>
		//    </If>
		//  </If> 
		//  <Else>
		//    <Set>MsgTyp=E</Set>
		//    <Set>RspCod=$HRspCd</Set>
		//    <Set>RspMsg=冲正不成功</Set>      	
		//  </Else>
		//</FlowCtrl>
		//</Transaction>	
		
		
		//上主机冲正成功则更新流水表,更新失败同为冲正失败
		/*context.setData(ParamKeys.MFM_TXN_STS, GDConstants.GAS_TXN_STS_C);
		context.setData(ParamKeys.THD_TXN_STS, GDConstants.GAS_TXN_STS_C);
		context.setData(GDParamKeys.GAS_RESULT, "UPay");
		//context.setData(ParamKeys.TXN_AMOUNT, arg1);  //<Set>TxnAmt1=$TxnAmt</Set>
		context.setData(ParamKeys.TXN_STS, "3");  //<Set>Status=3</Set> 此处应为交易状态TXN_STS，而不是 BAK_FLD5
		
		etj = BeanUtils.toObject(context.getDataMap(), EupsTransJournal.class);
		try{
			get(EupsTransJournalRepository.class).update(etj);
		}catch(Exception e){
			context.setData(ParamKeys.MESSAGE_TYPE, "E");
			context.setData(ParamKeys.RSP_CDE, GDConstants.GAS_ERROR_CODE);
			context.setData(ParamKeys.RSP_MSG, "数据库处理失败，冲正不成功!");
		}
		*/
		
		//
		//
//		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
	
	}
}
