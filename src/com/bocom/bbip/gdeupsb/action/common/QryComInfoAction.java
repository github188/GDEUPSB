package com.bocom.bbip.gdeupsb.action.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.service.BGSPServiceAccessObject;
import com.bocom.bbip.service.Result;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class QryComInfoAction extends BaseAction{
	@Autowired
	BGSPServiceAccessObject bgspServiceAccessObject;
	private final static Log logger = LogFactory.getLog(QryComInfoAction.class);
	
	public void execute(Context context) throws CoreException,CoreRuntimeException{
		logger.info("===============Start   QryComInfoAction");
		 context.setData("inqBusLstFlg","N");
		 //代收付搜索单位信息
		 Result comResult= bgspServiceAccessObject.callServiceFlatting("queryCorporInfo",context.getDataMap());
		 log.info("==========="+comResult);
		 if(!comResult.isSuccess()){
			 		throw new CoreException("单位未签约");
		 }
		 String comNme=comResult.getPayload().get("comNum").toString();
		 context.setData(ParamKeys.COMPANY_NAME, comNme);
		 logger.info("===============End   QryComInfoAction");
	}
}
