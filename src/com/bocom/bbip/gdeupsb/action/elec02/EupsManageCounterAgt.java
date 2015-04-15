package com.bocom.bbip.gdeupsb.action.elec02;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.comp.BBIPPublicServiceImpl;
import com.bocom.bbip.comp.CommonRequest;
import com.bocom.bbip.comp.account.AccountService;
import com.bocom.bbip.comp.account.support.CardInfo;
import com.bocom.bbip.comp.account.support.CusActInfResult;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.action.common.CommThdRspCdeAction;
import com.bocom.bbip.eups.adaptor.ThirdPartyAdaptor;
import com.bocom.bbip.eups.adaptor.support.CallThdService;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsThdBaseInfo;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GdeupsAgtElecTmp;
import com.bocom.bbip.gdeupsb.repository.GdeupsAgtElecTmpRepository;
import com.bocom.bbip.service.BGSPServiceAccessObject;
import com.bocom.bbip.service.Result;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.euif.component.util.StringUtil;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

public class EupsManageCounterAgt extends BaseAction {
	private static Logger logger = LoggerFactory.getLogger(EupsManageCounterAgt.class);
	private static final int ADD=0;
	private static final int UPDATE=3;
	private static final int QUERY=5;
	private static final int DELETE=9;
	
	@Autowired
	BBIPPublicService bbipPublicService;
	
	@Qualifier("callThdTradeManager")
    ThirdPartyAdaptor callThdTradeManager;
	
	public void process(Context context) throws CoreException {
     logger.info("协议维护");
     context.setData("ActDat", DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMdd));
     context.setData("LogNo", StringUtils.substring(
    		 ((BBIPPublicServiceImpl)get(GDConstants.BBIP_PUBLIC_SERVICE)).getBBIPSequence(),4));
     
    
     final int oprType=Integer.parseInt((String)context.getData("CHT"));
		switch (oprType) {
		case ADD:
            addAgentDeal(context);
			break;
		case UPDATE:
            updateAgentDeal(context);
			break;
		case QUERY:
             queryAgentDeal(context);
			break;
		case DELETE:
            deleteAgentDeal(context);
			break;
		}
	}

	private void addAgentDeal(Context context) throws CoreException {

//		CusActInfResult acInfo=get(AccountService.class).getAcInf(CommonRequest.build(context), cusAc);
//		String idNo=acInfo.getIdNo();
//		get(AccountService.class).getCardInfoByCardNo(card);
		
		
		 	List<Map<String,Object>> agentCollectAgreement=new ArrayList<Map<String,Object>>();
		 	List<Map<String,Object>>customerInfo=new ArrayList<Map<String,Object>>();
		 	context.setData("agrChl", "01");
		 	context.setData("oprTyp", "0");
	   
	       	Map<String,Object>agentCollectAgreementMap=setAgentCollectAgreementMap(context,(String)context.getData("OAC"));
	       	Map<String,Object>customerInfoMap=setCustomerInfoMap(context,(String)context.getData("OAC"));

	       	customerInfo.add(customerInfoMap);
	       	agentCollectAgreement.add(agentCollectAgreementMap);
	       	
	       	//参数里放集合
	       	context.setData("agentCollectAgreement", agentCollectAgreement);
	       	context.setData("customerInfo", customerInfo);
	       	
	       	//参数里把map,不放回出问题
	       	context.setData("cusTyp", "0"); //客户类型
			context.setData("idTyp", "01");
			context.setData("idNo", (String)context.getData("TIdNo"));
			
			context.setData("ccy", "CNY");
			context.setData("agrVldDte", "20150409");
			context.setData("agrExpDte", "99991231");
			context.setData("agtSrvCusId",context.getData("JFH"));
			context.setData("bvCde", "007");   //凭证代码 "007-磁条卡；008-IC卡；009-磁条和IC复合卡；704-储蓄存折；
			context.setData("cusNo", (String)context.getData("JFH"));     			//客户号
			context.setData(ParamKeys.COMPANY_NO, "4450000002");
			context.setData(ParamKeys.CUS_AC, (String)context.getData("OAC"));
			context.setData("buyTyp", "0");
			context.setData("busKnd", "A089");
			
			
			//header 设定
			context.setData("traceNo", context.getData(ParamKeys.TRACE_NO));
			context.setData("traceSrc", context.getData(ParamKeys.TRACE_SOURCE));
			context.setData("version", context.getData(ParamKeys.VERSION));
			context.setData("reqTme", new Date());
			context.setData("reqJrnNo", get(BBIPPublicService.class).getBBIPSequence());
			context.setData("reqSysCde", context.getData(ParamKeys.REQ_SYS_CDE));
			context.setData("tlr", context.getData(ParamKeys.TELLER));
			context.setData("chn", context.getData(ParamKeys.CHANNEL));
			context.setData("bk", context.getData(ParamKeys.BK));
			context.setData("br", context.getData(ParamKeys.BR));
			context.setData("obkBk", "301");
			
			Result respData = ((BGSPServiceAccessObject)get(BGSPServiceAccessObject.class)).
			callServiceFlatting("maintainAgentCollectAgreement", context.getDataMap());
			Map <String,Object> mapResult = back(context,respData);
			
			//返回协议编号
//			getAgdAgrNoByMap(context, mapResult);
//			
//			//外发第三方
//			callThd(context);
//			
//			//冲正代收付 
//			if(GDConstants.SUCCESS_CODE.toString().equals((String)context.getData(ParamKeys.RSP_CDE))){
//				respData = ((BGSPServiceAccessObject)get(BGSPServiceAccessObject.class)).
//		   			     callServiceFlatting("deleteAgentCollectAgreement", context.getDataMap());
//		       	back(context,respData);
//			}
			
			//代收付签约成功后，新增本地库
//		    addGdeupsAgtElecTmp( context);
	}

	private void updateAgentDeal(Context context) throws CoreException {
			List<Map<String,Object>> agentCollectAgreement=new ArrayList<Map<String,Object>>();
			List<Map<String,Object>>customerInfo=new ArrayList<Map<String,Object>>();
		 
			String cusAcOld=(String)context.getData("OAC");  //旧的卡号
			String cusAcNew=(String)context.getData("ActNo");	 //卡号
		   

		    Map<String,Object>agentCollectAgreementMap= new HashMap<String, Object>();
		    Map<String,Object>customerInfoMap=new HashMap<String, Object>();

		    		
			//参数里放map       
		    context.setData("cusTyp", "0"); //客户类型
			context.setData("idTyp", "01");
			context.setData("idNo", (String)context.getData("TIdNo"));
			
			context.setData("ccy", "CNY");
			context.setData("agrVldDte", "20150409");
			context.setData("agrExpDte", "99991231");
			context.setData("agtSrvCusId",context.getData("JFH"));
			context.setData("bvCde", "007");   //凭证代码 "007-磁条卡；008-IC卡；009-磁条和IC复合卡；704-储蓄存折；
			context.setData("cusNo", (String)context.getData("JFH"));     			//客户号
			context.setData(ParamKeys.COMPANY_NO, "4450000002");  //可以从本地库里查询
			context.setData(ParamKeys.CUS_AC, context.getData("OAC"));
			context.setData("buyTyp", "0");
			context.setData("busKnd", "A089");
	    	   
    	   //修改账号的时候，先删除协议，再新增协议    
    	   if(!StringUtil.isEmptyOrNull(cusAcNew) && !cusAcNew.trim().equals(cusAcOld.trim())){
    		   
    		   	 Map<String,Object>map=context.getDataMap();
			     String agdAgrNo=getAgdAgrNoByCusAc(context,cusAcOld);  //获得协议编号
			     List agdAgrNoList = new ArrayList<String>();
			     agdAgrNoList.add(agdAgrNo);
			     map.put("agdAgrNo", agdAgrNoList);
			     Result  respData = ((BGSPServiceAccessObject)get(BGSPServiceAccessObject.class)).
			     callServiceFlatting("deleteAgentCollectAgreement", map);
			     back(context,respData);
			     
			     //删除本地协议
//			     delGdeupsAgtElecTmp( context);
			     
			     context.setData("oprTyp", "0");
			     context.setData("agrChl", "01");
	    		 context.setData("cusAc", cusAcNew);
	    		 
	    		 customerInfoMap = setCustomerInfoMap(context,cusAcNew);
	    		 customerInfo.add(customerInfoMap);
	    		 agentCollectAgreementMap = setAgentCollectAgreementMap(context, cusAcNew);
	    		 agentCollectAgreement.add(agentCollectAgreementMap);
   		    	 //新增的两个集合
	    		 context.setData("agentCollectAgreement", agentCollectAgreement);
	    		 context.setData("customerInfo", customerInfo);
	    		   
			     respData = ((BGSPServiceAccessObject)get(BGSPServiceAccessObject.class)).
	 			 callServiceFlatting("maintainAgentCollectAgreement", context.getDataMap());
	 			 back(context,respData);
	 			 //新增本地协议
//			     addGdeupsAgtElecTmp( context);

    		  
	       //不修改账号的时候
	       }else{
//	    	   context.setData("agrChl", "01");
//    		   context.setData("oprTyp", "1");
//    		   context.setData("cusAc", cusAcOld);
//    		   
//    		   agentCollectAgreementMap = setAgentCollectAgreementMap(context,cusAcOld);
//    		   customerInfoMap = setCustomerInfoMap(context,cusAcOld);
//    		   customerInfo.add(customerInfoMap);
//    		   agentCollectAgreement.add(agentCollectAgreementMap);
//   		    	//新增的两个集合
//    		   context.setData("agentCollectAgreement", agentCollectAgreement);
//    		   context.setData("customerInfo", customerInfo);
//    		   
//		       Result respData = ((BGSPServiceAccessObject)get(BGSPServiceAccessObject.class)).
//		       callServiceFlatting("maintainAgentCollectAgreement", context.getDataMap());
//		       back(context,respData);
//		       
//		       //修改本地协议
//			   modifyGdeupsAgtElecTmp( context);
		       
	       } 
	}

	private void queryAgentDeal(Context context) throws CoreException {
	    	context.setData("agrChl", "01");
			Map<String,Object>map=context.getDataMap();
			map.put("cusAc", (String)context.getData("OAC"));
			Result respData = ((BGSPServiceAccessObject)get(BGSPServiceAccessObject.class)).
			callServiceFlatting("queryListAgentCollectAgreement", map);
			Map ret=back(context,respData);
			//查询结果数据处理
			setResponseResultFromAgts(context,respData);
			
	}

	//删除交易
	private void deleteAgentDeal(Context context) throws CoreException {
			 Map<String,Object>map=context.getDataMap();
		     
		     
		     final String agdAgrNo=(String)getAgdAgrNoByCusAc(context, (String)context.getData("OAC"));
//		     List agdAgrNoList = new ArrayList<Map<String,Object>>();
//		     Map agdAgrNoMap = new HashMap<String,Object>();
//		     agdAgrNoMap.put("agdAgrNo", agdAgrNo);
//		     agdAgrNoList.add(agdAgrNoMap);
		     
		     List agdAgrNoList = new ArrayList<String>();
		     agdAgrNoList.add(agdAgrNo);
		     
		     map.put("agdAgrNo", agdAgrNoList);
		     Result respData = ((BGSPServiceAccessObject)get(BGSPServiceAccessObject.class)).
		     callServiceFlatting("deleteAgentCollectAgreement", map);
		     Map ret=back(context,respData);
		     //删除本地库
//		      delGdeupsAgtElecTmp( context);
	}
	
	//此方法没有修改
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
	private Map<String, Object> setCustomerInfoMap(Context context,String cusAc) {
		Map<String, Object> customerInfoMap = new HashMap<String, Object>();
		   customerInfoMap.put("cusTyp", "0");   			//客户类型
	       customerInfoMap.put("cusNo", (String)context.getData("JFH"));     			//客户号
	       customerInfoMap.put("agtCllCusId", (String)context.getData("JFH"));			//客户id
	       customerInfoMap.put("cusAc",cusAc );	//客户账号
	       customerInfoMap.put("cusNme", (String)context.getData("UsrNam"));//客户名称   
	       customerInfoMap.put("idTyp",(String)context.getData("TIdTyp"));
	       customerInfoMap.put("ccy", "CNY");
	       customerInfoMap.put("idNo",(String)context.getData("TIdNo"));
	       customerInfoMap.put("drwMde", "");  		//支取方式
	       customerInfoMap.put("bvCde", "007");   	//凭证代码
	       customerInfoMap.put("bvNo", "");    		//凭证.号码
	       customerInfoMap.put("ourOthFlg", "0");    //本他行标志
	       customerInfoMap.put("obkBk",(String)context.getData("OKH")); 	//新的开户行号
		return customerInfoMap;
	}

	private Map<String, Object> setAgentCollectAgreementMap(Context context,String cusAc) {
		   Map<String, Object> agentCollectAgreementMap = new HashMap<String, Object>();
		   agentCollectAgreementMap.put("cusAc", cusAc);
	       agentCollectAgreementMap.put("acoAc", cusAc);
	       agentCollectAgreementMap.put("pwd", (String)context.getData("Pin"));
	       agentCollectAgreementMap.put("bvCde", "007");   		//凭证代码
	       agentCollectAgreementMap.put("bvNo", "");    		//凭证号码
	       agentCollectAgreementMap.put("comNo", "4450000002");
	       agentCollectAgreementMap.put("cusFeeDerFlg", "0"); 
	       agentCollectAgreementMap.put("ccy", "RMB");
	       agentCollectAgreementMap.put("agrVldDte", DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMdd));
	       agentCollectAgreementMap.put("agrExpDte", "99991231");
	       agentCollectAgreementMap.put("busTyp", "0");
	       agentCollectAgreementMap.put("busKnd", "A089");  //业务种类是什么 代确认
	      
	       agentCollectAgreementMap.put("agtSrvCusId", (String)context.getData("JFH"));
	       agentCollectAgreementMap.put("agtSrvCusPnm", (String)context.getData("busKnd")); //代理服务客户姓名 这个是必输项
	       
	       agentCollectAgreementMap.put("pedAgrSts", "");
	       agentCollectAgreementMap.put("cnlSts", "");
	       agentCollectAgreementMap.put("agrChl", "01");
	       
	       agentCollectAgreementMap.put("cmuTel", (String)context.getData("MOB"));
	       return agentCollectAgreementMap;

	}

	//新增本地协议
	private void addGdeupsAgtElecTmp(Context context){
		
		GdeupsAgtElecTmp agtElecTmp = toGdeupsAgtElecTmp(context);
		get(GdeupsAgtElecTmpRepository.class).insert(agtElecTmp);
		
	}
	
	//修改本地协议
	private void modifyGdeupsAgtElecTmp(Context context ){
		
		GdeupsAgtElecTmp agtElecTmp = toGdeupsAgtElecTmp(context);
		get(GdeupsAgtElecTmpRepository.class).save(agtElecTmp);
	}
	
	//删除本地协议
	private void delGdeupsAgtElecTmp(Context context){
		
		GdeupsAgtElecTmp agtElecTmp = new GdeupsAgtElecTmp();
		
		agtElecTmp.setActNo( (String)context.getData("OAC"));
		agtElecTmp.setFeeNum( (String)context.getData("JFH"));
		get(GdeupsAgtElecTmpRepository.class).delete(agtElecTmp);
		
	}
	
	
	//封装GdeupsAgtElecTmp对象
	private GdeupsAgtElecTmp toGdeupsAgtElecTmp( Context context){
		
		GdeupsAgtElecTmp agtElecTmp = new GdeupsAgtElecTmp();
		
		agtElecTmp.setChangeType( (String)context.getData("CHT"));
		agtElecTmp.setComCode( (String)context.getData("ECD"));
		agtElecTmp.setFeeCode( (String)context.getData("EDD"));
		agtElecTmp.setFeeNum( (String)context.getData("JFH"));
		agtElecTmp.setUserName( (String)context.getData("UsrNam"));
		agtElecTmp.setOldBankNum( (String)context.getData("OKH"));
		agtElecTmp.setOldCardNo( (String)context.getData("OAC"));
		agtElecTmp.setNewBankNum( (String)context.getData("KKB"));
		agtElecTmp.setActNo( (String)context.getData("ActNo"));
		agtElecTmp.setAcountName( (String)context.getData("ActNm"));
		agtElecTmp.setActType( (String)context.getData("ACT"));	
		agtElecTmp.setPerComFlag( (String)context.getData("GPF"));
		agtElecTmp.setIdType( (String)context.getData("IdTyp"));
		agtElecTmp.setIdNo( (String)context.getData("TIdNo"));
		agtElecTmp.setCheckSendType( (String)context.getData("ZPF"));
		agtElecTmp.setInvoiceSnedType( (String)context.getData("FPF"));
		agtElecTmp.setPointNum( (String)context.getData("JLD"));
		agtElecTmp.setInvoiceSnedMan( (String)context.getData("FPM"));
		agtElecTmp.setInvoiceSendZip( (String)context.getData("FPC"));
		agtElecTmp.setInvoiceSendAddr( (String)context.getData("FPA"));
		agtElecTmp.setCheckSendMan( (String)context.getData("ZPM"));
		agtElecTmp.setCheckSendZip( (String)context.getData("ZPC"));
		agtElecTmp.setCheckSendAddr( (String)context.getData("ZPA"));
		agtElecTmp.setNotifyType( (String)context.getData("YBZ"));
		agtElecTmp.setEmail( (String)context.getData("EML"));
		agtElecTmp.setPhoneNum( (String)context.getData("MOB"));
		agtElecTmp.setTelNum( (String)context.getData("TEL"));
		agtElecTmp.setRemark( (String)context.getData("TXT"));
		agtElecTmp.setPrcessPassword( (String)context.getData("Pin"));

		return  agtElecTmp;
	}
	
	/**
	 * 协议编号查询
	 */
	private String  getAgdAgrNoByCusAc(Context context,String cusAc) throws CoreException{
		//列表查询 获得协议编号
		Map<String, Object> map=new HashMap<String, Object>();
		map.put("cusAc", cusAc);
		//header 设定
		map.put("traceNo", context.getData(ParamKeys.TRACE_NO));
		map.put("traceSrc", context.getData(ParamKeys.TRACE_SOURCE));
		map.put("version", context.getData(ParamKeys.VERSION));
		map.put("reqTme", new Date());
		map.put("reqJrnNo", get(BBIPPublicService.class).getBBIPSequence());
		map.put("reqSysCde", context.getData(ParamKeys.REQ_SYS_CDE));
		map.put("tlr", context.getData(ParamKeys.TELLER));
		map.put("chn", context.getData(ParamKeys.CHANNEL));
		map.put("bk", context.getData(ParamKeys.BK));
		map.put("br", context.getData(ParamKeys.BR));
		map.put("obkBk", "301");
		
		map.put("cusAc", cusAc);
//		map.put("cusAc", "6222620710012838064");
		logger.info("~~~~~~~~~~requestHeader~~~~map~~~~~ "+map);
		logger.info("~~~~~~~~~~列表查询开始 ");
		//上代收付取协议编号
		Result accessObjList = ((BGSPServiceAccessObject)get(BGSPServiceAccessObject.class)).
			       callServiceFlatting("queryListAgentCollectAgreement",map);
		if(!accessObjList.isSuccess()){
					throw new CoreException(accessObjList.getPayload().get("responseMessage").toString());
		}
		logger.info("~~~~~~~~~~列表查询结束~~~~"+accessObjList);
		if(accessObjList.getPayload().get("agentCollectAgreement") ==null	){
			context.setData("thdRspCde", "80");
		}
		List<Map<String,Object>> list=(List<Map<String, Object>>)accessObjList.getPayload().get("agentCollectAgreement");
		String agdAgrNo=(String) list.get(0).get("agdAgrNo");
		logger.info("~~~~~~~~~~~~~~~协议编号： "+agdAgrNo);
		context.setData("agdAgrNo", agdAgrNo);
		
		return agdAgrNo;
	}
	
	
	/**
	 * 协议编号查询
	 */
	private String  getAgdAgrNoByMap(Context context,Map<String,Object> map) throws CoreException{
		
		if(map.get("agentCollectAgreement") ==null	){
			context.setData("thdRspCde", "80");
		}
		List<Map<String,Object>> list=(List<Map<String, Object>>)map.get("agentCollectAgreement");
		String agdAgrNo=(String) list.get(0).get("agdAgrNo");
		logger.info("~~~~~~~~~~~~~~~协议编号： "+agdAgrNo);
		context.setData("agdAgrNo", agdAgrNo);
		
		return agdAgrNo;
	}
	
	
	//为查询返回报文复制
	private void setResponseResultFromAgts(Context context ,Result respData){
		
		if(respData.getPayload().get("agentCollectAgreement") ==null ){
			context.setData("thdRspCde", "80");
		}
		//协议信息集合
		List<Map<String,Object>> agreementlist=(List<Map<String, Object>>)respData.getPayload().get("agentCollectAgreement");
		
		Map<String, Object> customerMap = respData.getPayload();

		Map<String,Object>  agreementMap = agreementlist.get(0);

		
		context.setData("PAgtNo",(String) agreementMap.get("agdAgrNo"));  //协议编号
		context.setData("comNo", "4450000002");		//单位编号
		context.setData("CLM", (String)agreementMap.get("agtSrvCusPnm"));		//第三方客户名称
		
		

		context.setData("JFH", (String)agreementMap.get("agtSrvCusId"));		//缴费号码
		context.setData("ACN", (String)agreementMap.get("cusAc"));		//账号
		context.setData("ActNm",(String) customerMap.get("cusNme"));		//mingcheng
		context.setData("IdTyp", (String)customerMap.get("idTyp"));		//证件类型
		context.setData("TIdNo", (String)customerMap.get("idNo"));		//证件号码
		context.setData("EML", (String)agreementMap.get("eml"));		//EML
		context.setData("MOB", (String)agreementMap.get("cmuTel"));		//联系手机号

	}
	
	

	/**
	 * 外发第三方
	 */
	private void callThd(Context context) throws CoreException{
		Date txnDte=(Date)context.getData(ParamKeys.TXN_DTE);
		Date txnTme=DateUtils.parse(context.getData("txnTme").toString());
		
		context.setData(ParamKeys.TXN_DTE, DateUtils.format(txnDte,DateUtils.STYLE_yyyyMMdd));
		context.setData(ParamKeys.TXN_TME, DateUtils.format(txnTme,DateUtils.STYLE_HHmmss));
		context.setData(GDParamKeys.SVRCOD, "30");
		
		Map<String, Object> rspMap = callThdTradeManager.trade(context);
			if(BPState.isBPStateNormal(context)){
				if(null !=rspMap){
					 	context.setDataMap(rspMap);
		                context.setData(ParamKeys.THIRD_RETURN_MESSAGE, rspMap);
		                //第三方返回码
		                CommThdRspCdeAction rspCdeAction = new CommThdRspCdeAction();
		                String responseCode = rspCdeAction.getThdRspCde(rspMap, context.getData(ParamKeys.EUPS_BUSS_TYPE).toString());
		                logger.info("third response code="+responseCode);
		                if(StringUtils.isEmpty(responseCode)){
		                	responseCode=ErrorCodes.EUPS_THD_SYS_ERROR;
		                }
		                context.setData(ParamKeys.RESPONSE_CODE, responseCode);
		             // 第三方交易成功
			                if (GDConstants.SUCCESS_CODE.equals(responseCode)) {
			                    logger.info("The third process response successful.");
			                    context.setData(ParamKeys.TXN_STS, Constants.TXNSTS_SUCCESS);
			                    context.setData(ParamKeys.THD_TXN_STS, Constants.THD_TXNSTS_SUCCESS);
			                    context.setData(ParamKeys.RSP_CDE, GDConstants.SUCCESS_CODE);
			                    context.setData(ParamKeys.RESPONSE_MESSAGE, "交易成功");
			                }else if(BPState.isBPStateReversalFail(context)){
			                	context.setData(ParamKeys.THD_TXN_STS,Constants.THD_TXNSTS_FAIL);
			                	context.setData(GDParamKeys.MSGTYP, "E");
			                	context.setData(ParamKeys.RSP_CDE, "EFE999");
			                	context.setData(ParamKeys.RESPONSE_MESSAGE, "交易失败");
			                }else if(BPState.isBPStateOvertime(context)){
			                	context.setData(ParamKeys.THD_TXN_STS,Constants.THD_TXNSTS_FAIL);
			                	context.setData(GDParamKeys.MSGTYP, "E");
			                	context.setData(ParamKeys.RSP_CDE, "EFE999");
			                	context.setData(ParamKeys.RESPONSE_MESSAGE, "交易超时");
			                }else if(BPState.isBPStateSystemError(context)){
			                	context.setData(ParamKeys.THD_TXN_STS,Constants.THD_TXNSTS_FAIL);
			                	context.setData(GDParamKeys.MSGTYP, "E");
			                	context.setData(ParamKeys.RSP_CDE, "EFE999");
			                	context.setData(ParamKeys.RESPONSE_MESSAGE, "系统错误");
			                }else if(BPState.isBPStateTransFail(context)){
			                	context.setData(ParamKeys.THD_TXN_STS,Constants.THD_TXNSTS_FAIL);
			                	context.setData(GDParamKeys.MSGTYP, "E");
			                	context.setData(ParamKeys.RSP_CDE, "EFE999");
			                	context.setData(ParamKeys.RESPONSE_MESSAGE, "发送失败");
			                }else{
			                	context.setData(ParamKeys.THD_TXN_STS,Constants.THD_TXNSTS_FAIL);
			                	context.setData(GDParamKeys.MSGTYP, "E");
			                	context.setData(ParamKeys.RSP_CDE, "EFE999");
			                	context.setData(ParamKeys.RESPONSE_MESSAGE, "交易失败，其他未知情况");
			                }
				}
		}else{
				logger.info("~~~~~~~~~~~发送失败");
			    context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
                context.setData(ParamKeys.TXN_STS, Constants.TXNSTS_REVERSE);
                context.setData(ParamKeys.THD_TXN_STS, Constants.TXNSTS_FAIL);
                context.setData(ParamKeys.RESPONSE_TYPE, Constants.RESPONSE_TYPE_FAIL);
                context.setData(ParamKeys.RESPONSE_CODE, ErrorCodes.EUPS_THD_SYS_ERROR);
                context.setData(ParamKeys.RESPONSE_MESSAGE, Constants.RESPONSE_MSG_FAIL);
                context.setData(ParamKeys.THD_RSP_MSG,Constants.RESPONSE_MSG_FAIL);
                throw new CoreException("发送失败");
		}
	}

	
}
