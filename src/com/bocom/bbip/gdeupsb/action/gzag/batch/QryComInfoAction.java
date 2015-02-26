package com.bocom.bbip.gdeupsb.action.gzag.batch;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.CollectionUtils;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.service.BGSPServiceAccessObject;
import com.bocom.bbip.service.Result;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class QryComInfoAction extends BaseAction{
	
	private final static Log logger = LogFactory.getLog(QryComInfoAction.class);
	
	public void execute(Context context) throws CoreException,CoreRuntimeException{
		logger.info("===============Start   QryComInfoAction");
		 Map<String,Object> rspMap = new HashMap<String, Object>();
		 String  comNo=context.getData(ParamKeys.COMPANY_NO).toString();
		 rspMap.put(ParamKeys.COMPANY_NO, comNo);
		// 查询代收单位协议信息
		 Result respData = get(BGSPServiceAccessObject.class).callServiceFlatting("queryCorporInfo", rspMap);
		
		 if(CollectionUtils.isEmpty(respData.getPayload())){			
				throw new CoreRuntimeException("该单位未签约");
		}

		 context.setDataMap(rspMap);
		 logger.info("===============End   QryComInfoAction");
	}
}
