package com.bocom.bbip.gdeupsb.action.common;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsThdBaseInfo;
import com.bocom.bbip.eups.repository.EupsThdBaseInfoRepository;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class QryComInfoAction extends BaseAction{
	@Autowired
	BBIPPublicService bbipPublicService;
	@Autowired
	EupsThdBaseInfoRepository eupsThdBaseInfoRepository;
	private final static Log logger = LogFactory.getLog(QryComInfoAction.class);
	
	public void execute(Context context) throws CoreException,CoreRuntimeException{
		logger.info("===============Start   QryComInfoAction");
		 Map<String,Object> rspMap = new HashMap<String, Object>();
		 String  comNo=context.getData(ParamKeys.COMPANY_NO).toString();
		 rspMap.put(ParamKeys.COMPANY_NO, comNo);
		 rspMap.put("inqBusLstFlg", "N");
		
		 EupsThdBaseInfo eupsThdBaseInfo=eupsThdBaseInfoRepository.findOne(comNo);
		 context.setData("comNme", eupsThdBaseInfo.getComNme());
		 logger.info("===============End   QryComInfoAction");
	}
}
