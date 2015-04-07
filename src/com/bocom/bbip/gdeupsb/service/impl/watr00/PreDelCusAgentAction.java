package com.bocom.bbip.gdeupsb.service.impl.watr00;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class PreDelCusAgentAction extends BaseAction{

private static Logger logger = LoggerFactory.getLogger(PreDelCusAgentAction.class);
	
	@Override
	public void execute(Context context) throws CoreException,	CoreRuntimeException {
		logger.info("PreDelCusAgentAction start......");
//		Map<String, Object> agentMap = new HashMap<String, Object>();
////		agentMap.put("agdAgrNo", (String)context.getData("agdAgrNo"));
////		agentMap.put("cusAc", (String)context.getData("cusAc"));
//		agentMap.put("acoAc", (String)context.getData("cusAc"));
//		agentMap.put("pwd", (String)context.getData("pwd"));
////		agentMap.put("bvCde", (String)context.getData("agtCllCusId"));
////		agentMap.put("bvNo", (String)context.getData("agtCllCusId"));
//		agentMap.put("comNo", "4450000685");
//		agentMap.put("comNum", "汕头自来水公司");
//		
//		agentMap.put("busTyp","0");
//		agentMap.put("busKnd", "A115");
////		agentMap.put("busKndNme", (String)context.getData("agtCllCusId"));
//		agentMap.put("ccy", "CNY");
//		agentMap.put("cusFeeDerFlg", "0");  //个人账户收费减免标志
//		agentMap.put("agtSrvCusId", (String)context.getData("thdCusNo"));
//		agentMap.put("agtSrvCusPnm", (String)context.getData("thdCusNme"));
//		agentMap.put("agrVldDte", DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMdd));
//		agentMap.put("agrExpDte", "99991231");
////		agentMap.put("agrVldDte", (String)context.getData("agtCllCusId"));
////		agentMap.put("des1", (String)context.getData("agtCllCusId"));
////		agentMap.put("des2", (String)context.getData("agtCllCusId"));
////		agentMap.put("des3", (String)context.getData("agtCllCusId"));
////		agentMap.put("des4", (String)context.getData("agtCllCusId"));
////		agentMap.put("des5", (String)context.getData("agtCllCusId"));
////		agentMap.put("selId", (String)context.getData("agtCllCusId"));
////		agentMap.put("selNme", (String)context.getData("agtCllCusId"));
////		agentMap.put("rcoId", (String)context.getData("agtCllCusId"));
////		agentMap.put("rcoNme", (String)context.getData("agtCllCusId"));
////		agentMap.put("pedAgrSts", (String)context.getData("agtCllCusId"));
////		agentMap.put("mkiEvtNo", (String)context.getData("agtCllCusId"));
////		agentMap.put("ageBr", (String)context.getData("agtCllCusId"));
////		agentMap.put("agrBr", (String)context.getData("agtCllCusId"));
////		agentMap.put("agrTlr", (String)context.getData("agtCllCusId"));
////		agentMap.put("athTlr", (String)context.getData("agtCllCusId"));
////		agentMap.put("agrTme", (String)context.getData("agtCllCusId"));
////		agentMap.put("cmuTel", (String)context.getData("agtCllCusId"));
////		agentMap.put("eml", (String)context.getData("agtCllCusId"));
//		
//		
//		List<Map<String,Object>> agentCollectAgreement = new ArrayList<Map<String,Object>>();
//		agentCollectAgreement.add(agentMap);
//		context.setData(ParamKeys.AGENT_COLLECT_AGREEMENT, agentCollectAgreement);//上代收付用
//		
//		
//		
//		Map<String, Object> infoMap = new HashMap<String, Object>();
//		List<Map<String,Object>> customerInfo = new ArrayList<Map<String,Object>>();
////		infoMap.put("agtCllCusId", "");//TODO:修改协议时必输
//		infoMap.put("cusTyp", (String)context.getData("cusTyp"));
////		infoMap.put("cusAc", "");
//		infoMap.put("ccy", "CNY");
//		infoMap.put("idTyp", context.getData("idTyp"));
//		infoMap.put("idNo", context.getData("idNo"));
//		
//		customerInfo.add(infoMap);
//		context.setData("agrChl", "1");//签约渠道设为公共事业缴费，上代收付用
//		context.setData("customerInfo", customerInfo);
//	
//		context.setData("ccy", "CNY");
//		context.setData("bvCde", "008");
//		context.setData("cusNo", context.getData("thdCusNo"));
		context.setState("BP_STATE_NORMAL");
		logger.error("PreDelCusAgentAction  end!");
	}
}
