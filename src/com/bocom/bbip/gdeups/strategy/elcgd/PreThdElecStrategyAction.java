package com.bocom.bbip.gdeups.strategy.elcgd;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeups.common.GDConstants;
import com.bocom.bbip.gdeups.common.GDParamKeys;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
import com.bocom.jump.bp.core.Executable;

/**
 * 
 * @author qc.w
 *
 */
public class PreThdElecStrategyAction implements Executable {

	private final static Logger log = LoggerFactory.getLogger(PreThdElecStrategyAction.class);

	@Override
	public void execute(Context context) throws CoreException, CoreRuntimeException {
		log.info("PreThdElecStrategyAction start!..");
		
//	
//	      <Set>TraTyp=JF</Set>
//	      <Set>TxnAmt=ADDCHAR(DELSPACE($TxnAmt,all),12,0,1)</Set> <!--交易金额-->
//	      <Set>Fee=ADDCHAR(DELSPACE($Fee,all),12,0,1)</Set>       <!--手续费-->
//	      <Set>TCusId=ADDCHAR(DELSPACE($TCusId,all),21, ,1)</Set> <!--客户编号-->
//	      <Set>ChkNum=ADDCHAR(DELSPACE($ChkNum,all),25, ,1)</Set> <!--支票号码-->
//	      <Set>OData=STRCAT(ADDCHAR($TCusId,21, ,1),$LChkTm,01,SPACE(12),$PayTyp,ADDCHAR($ChkNum,25, ,1))</Set>  <!--附加数据-->
//	      <Set>RsFld1=$OData</Set>                                <!--数据备用-->  
//	      <Set>MacFlag=0</Set>          <!--发送MAC生成-->
//	      <Quote name="MakeMac"/>
//	      <Set>MsgMac=$MAC</Set>
		
		
		
		context.setData(ParamKeys.MESSAGE_TYPE, "0200");
		String ttrmId=(String)context.getData(ParamKeys.TERMINAL);
		ttrmId=StringUtils.rightPad(ttrmId, 8,' ');
		
		String crpID=context.getData(ParamKeys.TELLER);
		crpID=StringUtils.rightPad(crpID, 15,' ');
		
		context.setData(GDParamKeys.GZ_ELE_DEAL_CODE, GDConstants.GZ_ELE_DEAL_CODE);
		context.setData(GDParamKeys.GZ_ELE_RCS_NO, GDConstants.GZ_ELE_DEAL_ORG_CODE);
		context.setData(GDParamKeys.GZ_ELE_CCY_COD, GDConstants.GZ_ELE_CCY);
		context.setData(GDParamKeys.GZ_ELE_TTRM_ID, ttrmId);
		context.setData(GDParamKeys.GZ_ELE_TDL_ID, crpID);
		
		//TODO:电力方清算日期
		String eleTxnDteStr=DateUtils.format(new Date(), "MMDD");
		context.setData(GDParamKeys.GZ_ELE_TXN_DTE, eleTxnDteStr);
		
		//TODO
		
	}
	
}
