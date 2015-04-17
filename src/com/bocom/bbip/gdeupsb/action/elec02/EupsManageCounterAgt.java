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
	
	@Autowired
	ThirdPartyAdaptor callThdTradeManager;
	
	public void process(Context context) throws CoreException {
     logger.info("协议维护");
     
  
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
		GdeupsAgtElecTmp agtElecTmp = toGdeupsAgtElecTmp(context);
		get(GdeupsAgtElecTmpRepository.class).insert(agtElecTmp);
	}

	private void updateAgentDeal(Context context) throws CoreException {
			
	}

	private void queryAgentDeal(Context context) throws CoreException {
			GdeupsAgtElecTmp agtElecTmp = new GdeupsAgtElecTmp();
				
			String feeNum = (String)context.getData("JFH");
			if(feeNum != null && feeNum.trim()!= ""){
				agtElecTmp.setFeeNum(feeNum );      //
			}

			agtElecTmp.setActNo( (String)context.getData("ActNo"));      //账号
			
			agtElecTmp = get(GdeupsAgtElecTmpRepository.class).findBase(agtElecTmp);
			
			//查询结果数据处理
			setResponseResultFromAgts(context,agtElecTmp);
			
	}

	//删除交易
	private void deleteAgentDeal(Context context) throws CoreException {
		GdeupsAgtElecTmp agtElecTmp = toGdeupsAgtElecTmp(context);
		get(GdeupsAgtElecTmpRepository.class).deleteByAc(agtElecTmp);
		
	}
	

	//修改本地协议
	private void modifyGdeupsAgtElecTmp(Context context ){
		
		GdeupsAgtElecTmp agtElecTmp = toGdeupsAgtElecTmp(context);
		get(GdeupsAgtElecTmpRepository.class).save(agtElecTmp);
	}
	
	
	
	
	//封装GdeupsAgtElecTmp对象
	private GdeupsAgtElecTmp toGdeupsAgtElecTmp( Context context){
		
		GdeupsAgtElecTmp agtElecTmp = new GdeupsAgtElecTmp();
		agtElecTmp.setFeeNum( (String)context.getData("JFH"));      //
		agtElecTmp.setUserName( (String)context.getData("UsrNam"));
		agtElecTmp.setActNo( (String)context.getData("ActNo"));      //账号
		agtElecTmp.setAcountName( (String)context.getData("ActNm")); //账户名称
		agtElecTmp.setActType( (String)context.getData("ACT"));	    //账号类型
		agtElecTmp.setPerComFlag( (String)context.getData("GPF"));   //个人/对公
		agtElecTmp.setPhoneNum( (String)context.getData("MOB"));
		agtElecTmp.setTelNum( (String)context.getData("TEL"));
		agtElecTmp.setPrcessPassword( (String)context.getData("Pin"));

		return  agtElecTmp;
	}
	
	
	//为查询返回报文复制
	private void setResponseResultFromAgts(Context context,GdeupsAgtElecTmp agtElecTmp){
		
	
		
		context.setData("JFH",agtElecTmp.getFeeNum());  //协议编号
		context.setData("UsrNam", agtElecTmp.getUserName());		//单位编号
		context.setData("ActNo", agtElecTmp.getActNo());		//第三方客户名称
		context.setData("ActNm", agtElecTmp.getAcountName());		//缴费号码
		context.setData("ACT", agtElecTmp.getActType());		//账号
		context.setData("GPF",agtElecTmp.getPerComFlag());		//名称
		context.setData("MOB", agtElecTmp.getPhoneNum());		//证件类型
		context.setData("TEL",agtElecTmp.getTelNum());		//证件号码
		

	}
	
	
	
}
