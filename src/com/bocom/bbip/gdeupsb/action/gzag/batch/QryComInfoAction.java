package com.bocom.bbip.gdeupsb.action.gzag.batch;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDErrorCodes;
import com.bocom.bbip.gdeupsb.entity.GdLotSysCfg;
import com.bocom.bbip.gdeupsb.repository.GdLotSysCfgRepository;
import com.bocom.bbip.service.BGSPServiceAccessObject;
import com.bocom.bbip.service.Result;
import com.bocom.bbip.utils.Assert;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class QryComInfoAction extends BaseAction{
	
	private final static Log logger = LogFactory.getLog(QryComInfoAction.class);
	
	public void execute(Context context) throws CoreException,CoreRuntimeException{
		logger.info("===============Start   QryComInfoAction");

		// 查询代收单位协议信息
		 Result respData = get(BGSPServiceAccessObject.class).callServiceFlatting("queryCorporInfo", context.getDataMap());
		            Map<String,Object> rspMap = new HashMap<String, Object>();
		            rspMap = respData.getPayload();
		            String  comNo=context.getData(ParamKeys.COMPANY_NO).toString();
//		            Assert.hasLength(comNo, GDErrorCodes.EUPS_SCH_TO_EBNK_SCHCDE_IS_NULL, "该单位未签代扣协议");
	}
}
