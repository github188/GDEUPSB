package com.bocom.bbip.gdeupsb.strategy.hscard;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;








import org.springframework.util.CollectionUtils;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.service.BGSPServiceAccessObject;
import com.bocom.bbip.service.Result;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
import com.bocom.jump.bp.core.Executable;

public class PrePayToBankStrategyAction implements Executable{
	private final static  Log logger = LogFactory.getLog(PrePayToBankStrategyAction.class);
	
	@Autowired
	BGSPServiceAccessObject bgspServiceAccessObject;
	
	@Autowired
	BBIPPublicService bbipPublicService;
	@SuppressWarnings("unchecked")
	public void execute(Context context) throws CoreException,CoreRuntimeException{
		
		logger.info("PrePayToBankStrategyAction start......");
		
//		产生银行处理流水号,返回给第三方


	      context.setData(ParamKeys.MFM_VCH_NO, bbipPublicService.getBBIPSequence());
//			TODO:<Set>BnkId=STRCAT(SUBSTR($BrNo,1,3),$ActDat,$SelVal)</Set>
	      
	      
//	     TODO:
//	      context.setData(ParamKeys.TXN_CHL,"CPL" );
	      context.setData(ParamKeys.CCY,Constants.CCY_CDE_CNY );
//	      context.setData(ParamKeys.AC_TYP,"4" );   //ICS中是ACT_TYP,paramkeys里还有acc_typ,还不确定用哪个
	      
	      context.setData(ParamKeys.ACT_NO, context.getData(ParamKeys.CUS_AC));  
//	      TODO:
//	      <Set>BusTyp=PCL52</Set>
//	      <Set>CnlTyp=L</Set>
//	      <Set>Mask=9199</Set>
//	      <Set>ActFlg=4</Set>
//	      <Set>ActNo=$CardNo</Set>
//	      <Set>PayMod=0</Set>
//	      <Set>TActNo=$CrpAct</Set>
//	      <Set>CcyTyp=1</Set>
//	      <Set>VchChk=0</Set>
//	      <Set>CAgtNo=9999999999</Set>
//	      <Set>GthFlg=N</Set>
//	      <Set>TckNo=ZZZZZZZZZZZ</Set>
//	      <Set>TxnSts=U</Set>
//	      <Set>VchCod=00000000</Set>
//	      <Delete>CTRL_A</Delete>
//	      <Delete>CashNo</Delete>
//	      <Delete>HLogNo</Delete>
	     
	      //卡协议验证
	      context.setData(ParamKeys.TELLER, "ABIR148");
	      context.setData(ParamKeys.BR, "01441131999");
	      context.setData(ParamKeys.BK, "01441999999");
	      context.setData(ParamKeys.CHANNEL, "00");
//	      Map<String, Object> agtMap = new HashMap<String, Object>();
//	      agtMap.put(ParamKeys.CUS_AC, context.getData(ParamKeys.CUS_AC));
//	      Result accessObject = bgspServiceAccessObject.callServiceFlatting("queryListAgentCollectAgreement",agtMap);
//	      Map<String,Object> resultMap=accessObject.getPayload();
//	      if (CollectionUtils.isEmpty(resultMap)) {
//	            logger.info("There are no records for select check trans journal ");
////	            context.setData(ParamKeys.RESPONSE_CODE, "000001");
//        		context.setData(ParamKeys.RSP_MSG, "未查到协议信息");
//        		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
//	            throw new CoreException(ErrorCodes.EUPS_QUERY_NO_DATA);
//	        }else{
//	        	List<Map<String,Object>> customerInfoList=(List<Map<String,Object>>)resultMap.get("customerInfo");
//	        	Map<String,Object> map=customerInfoList.get(0);
//	        	String cusNme = (String)map.get(ParamKeys.CUS_NME);
//	        	String idNo = (String)map.get(ParamKeys.ID_NO);
//	        	if(!context.getData(ParamKeys.CUS_NME).toString().equals(cusNme)){
//	        		context.setData(ParamKeys.RESPONSE_CODE, "000001");
//	        		context.setData(ParamKeys.RSP_MSG, "姓名匹配失败");
//	        		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
//	        		throw new CoreException(ErrorCodes.EUPS_AGENT_CHK_ERROR);
//	        	}
//	        	if(!context.getData(ParamKeys.ID_NO).toString().equals(idNo)){
//	        		context.setData(ParamKeys.RESPONSE_CODE, "000001");
//	        		context.setData(ParamKeys.RSP_MSG, "证件号码匹配失败");
//	        		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
//	        		throw new CoreException(ErrorCodes.EUPS_AGENT_CHK_ERROR);
//	        	}
//	        	
//	        }
	}
}
