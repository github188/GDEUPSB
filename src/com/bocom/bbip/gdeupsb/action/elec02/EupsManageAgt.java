package com.bocom.bbip.gdeupsb.action.elec02;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bocom.bbip.comp.BBIPPublicServiceImpl;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.service.BGSPServiceAccessObject;
import com.bocom.bbip.service.Result;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

public class EupsManageAgt extends BaseAction {
	private static Logger logger = LoggerFactory.getLogger(EupsManageAgt.class);
	private static final int ADD=0;
	private static final int UPDATE=3;
	private static final int QUERY=5;
	private static final int DELETE=9;
	public void process(Context context) throws CoreException {
     logger.info("协议维护");
     context.setData("ActDat", DateUtils.format(new Date(), DateUtils.REGEXP_yyyyMMdd));
     context.setData("LogNo", StringUtils.substring(
    		 ((BBIPPublicServiceImpl)get(GDConstants.BBIP_PUBLIC_SERVICE)).getBBIPSequence(),4));
     final int oprType=Integer.parseInt((String)context.getData("CHT"));
		switch (oprType) {
		case ADD:
            add(context);
			break;
		case UPDATE:
            update(context);
			break;
		case QUERY:
             query(context);
			break;
		case DELETE:
            delete(context);
			break;
		}
	}

	private void add(Context context) throws CoreException {
		 Map<String,Object>map=new HashMap<String,Object>();
		 Map<String,Object>agentCollectAgreement=new HashMap<String,Object>();
		 Map<String,Object>customerInfo=new HashMap<String,Object>();
	       map.put("agrChl", "01");
	       map.put("oprTyp", "0");
	       customerInfo.put("cusTyp", "0");
	       customerInfo.put("cusAc", (String)context.getData("OAC"));
	       customerInfo.put("idTyp",(String)context.getData("TIdTyp"));
	       customerInfo.put("cusNme", (String)context.getData("UsrNam"));
	       customerInfo.put("ccy", "RMB");
	       customerInfo.put("idNo",(String)context.getData("TIdNo"));
	       customerInfo.put("agtCllCusId", "");
	       
	       agentCollectAgreement.put("cusAc", (String)context.getData("OAC"));
	       agentCollectAgreement.put("ccy", "RMB");
	       agentCollectAgreement.put("agrVldDte", DateUtils.format(new Date(), DateUtils.REGEXP_yyyyMMdd));
	       agentCollectAgreement.put("agrExpDte", "99991231");
	       agentCollectAgreement.put("agtSrvCusId", (String)context.getData("JFH"));
	       agentCollectAgreement.put("cusFeeDerFlg", "0");
	       agentCollectAgreement.put("comNo", (String)context.getData("ECD"));
	       agentCollectAgreement.put("busTyp", "0");
	       agentCollectAgreement.put("busKnd", "");
	       map.put("agentCollectAgreement", agentCollectAgreement);
	       map.put("customerInfo", customerInfo);
	  Result respData = ((BGSPServiceAccessObject)get(BGSPServiceAccessObject.class)).
	  callServiceFlatting("maintainAgentCollectAgreement", map);
       back(context,respData);
	}

	private void update(Context context) throws CoreException {
		 Map<String,Object>map=new HashMap<String,Object>();
		 Map<String,Object>agentCollectAgreement=new HashMap<String,Object>();
		 Map<String,Object>customerInfo=new HashMap<String,Object>();
	       map.put("agrChl", "01");
	       map.put("oprTyp", "1");
	       customerInfo.put("cusTyp", "0");
	       customerInfo.put("cusAc", (String)context.getData("TActNo"));
	       customerInfo.put("idTyp",(String)context.getData("TIdTyp"));
	       customerInfo.put("cusNme", (String)context.getData("TActNm"));
	       customerInfo.put("ccy", "RMB");
	       customerInfo.put("idNo",(String)context.getData("TIdNo"));
	       customerInfo.put("agtCllCusId", "");
	       
	       agentCollectAgreement.put("cusAc", (String)context.getData("TActNo"));
	       agentCollectAgreement.put("ccy", "RMB");
	       agentCollectAgreement.put("agrVldDte", DateUtils.format(new Date(), DateUtils.REGEXP_yyyyMMdd));
	       agentCollectAgreement.put("agrExpDte", "99991231");
	       agentCollectAgreement.put("agtSrvCusId", (String)context.getData("JFH"));
	       agentCollectAgreement.put("cusFeeDerFlg", "0");
	       agentCollectAgreement.put("comNo", (String)context.getData("ECD"));
	       agentCollectAgreement.put("busTyp", "0");
	       agentCollectAgreement.put("busKnd", "");
	       map.put("agentCollectAgreement", agentCollectAgreement);
	       map.put("customerInfo", customerInfo);
		  Result respData = ((BGSPServiceAccessObject)get(BGSPServiceAccessObject.class)).
		  callServiceFlatting("maintainAgentCollectAgreement", map);
	       back(context,respData);
	}

	private void query(Context context) throws CoreException {
		 Map<String,Object>map=new HashMap<String,Object>();
	       map.put("cusAc", (String)context.getData("OAC"));
	       Result respData = ((BGSPServiceAccessObject)get(BGSPServiceAccessObject.class)).
	 	   callServiceFlatting("queryListAgentCollectAgreement", map);
	        Map ret=back(context,respData);
	}

	private void delete(Context context) throws CoreException {
		 Map<String,Object>map=new HashMap<String,Object>();
	       map.put("cusAc", (String)context.getData("OAC"));
	       Result respData = ((BGSPServiceAccessObject)get(BGSPServiceAccessObject.class)).
	 	   callServiceFlatting("queryListAgentCollectAgreement", map);
	        Map ret=back(context,respData);
	        final String agdAgrNo=(String)ret.get("agdAgrNo");
	        map.put("agdAgrNo", agdAgrNo);
	         respData = ((BGSPServiceAccessObject)get(BGSPServiceAccessObject.class)).
	        callServiceFlatting("deleteAgentCollectAgreement", map);
	         ret=back(context,respData);
	}
	private Map back(Context context,Result respData) throws CoreException {
	    Map rspMap = respData.getPayload();
	    this.logger.info("========the response map: " + rspMap);
	    if ((respData.isSuccess()) && (respData.getStatus() == 0)) {
	      this.logger.info("Call acp AcpCoreService(submitAcpBatchData) success!!!!");
	      return rspMap;
	    } else {
	      if (-10 == respData.getStatus()) {
	        this.logger.info("Call acp AcpCoreService(submitAcpBatchData)- send error !!!!");
	        throw new CoreException("BBIP0004EU0042");
	      }if (-2 == respData.getStatus()) {
	        this.logger.info("Call acp AcpCoreService(submitAcpBatchData) system error !!!! ");
	        throw new CoreException("BBIP0004EU0042");
	      }if (-1 == respData.getStatus()) {
	        this.logger.info("Call acp AcpCoreService(submitAcpBatchData) Timeout !!!! ");
	        throw new CoreException("BBIP0004EU0044");
	      }if (3 == respData.getStatus()) {
	        String responseCode = (String)rspMap.get("responseCode");
	        String responseMsg = (String)rspMap.get("responseMessage");
	        this.logger.info("Call acp AcpCoreService(submitAcpBatchData) Failed!!!! ");
	        throw new CoreException(responseCode, responseMsg);
	      }
	      this.logger.info("Call acp AcpCoreService(submitAcpBatchData) Other error!!!!");
	      throw new CoreException("BBIP0004EU0045");
	    }
	}
}
