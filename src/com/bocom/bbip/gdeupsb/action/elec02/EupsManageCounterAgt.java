package com.bocom.bbip.gdeupsb.action.elec02;



import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.adaptor.ThirdPartyAdaptor;
import com.bocom.bbip.gdeupsb.entity.GdeupsAgtElecTmp;
import com.bocom.bbip.gdeupsb.repository.GdeupsAgtElecTmpRepository;
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
		GdeupsAgtElecTmp agtElecTmp = toGdeupsAgtElecTmp(context);
		get(GdeupsAgtElecTmpRepository.class).updateByAc(agtElecTmp);
		
	}

	private void queryAgentDeal(Context context) throws CoreException {
			GdeupsAgtElecTmp agtElecTmp = new GdeupsAgtElecTmp();
				
			String feeNum = (String)context.getData("JFH");
			String actNo = (String)context.getData("ActNo");
			if(feeNum != null && feeNum.trim()!= ""){
				agtElecTmp.setFeeNum(feeNum );      //
			}
			
			if(actNo != null && actNo.trim()!= ""){
				agtElecTmp.setActNo(actNo );      //
			}
			
			List<GdeupsAgtElecTmp> list = get(GdeupsAgtElecTmpRepository.class).findBase(agtElecTmp);
			if(list.size()>0){
				agtElecTmp = list.get(0);
				//查询结果数据处理
				setResponseResultFromAgts(context,agtElecTmp);
			}else{
				log.info("没有查询到协议信息！");
				 throw new CoreException("没有查询到协议信息！");
			}

	}

	//删除交易
	private void deleteAgentDeal(Context context) throws CoreException {
		GdeupsAgtElecTmp agtElecTmp = toGdeupsAgtElecTmp(context);
		get(GdeupsAgtElecTmpRepository.class).deleteByAc(agtElecTmp);
		
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
