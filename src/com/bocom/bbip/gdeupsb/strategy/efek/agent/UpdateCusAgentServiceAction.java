package com.bocom.bbip.gdeupsb.strategy.efek.agent;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.action.common.CommThdRspCdeAction;
import com.bocom.bbip.eups.adaptor.ThirdPartyAdaptor;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.service.BGSPServiceAccessObject;
import com.bocom.bbip.service.Result;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
/**
 *银行到供电变更代扣协议业务 
 *@author liyawei
 */
public class UpdateCusAgentServiceAction extends BaseAction {
	@Autowired
    @Qualifier("callThdTradeManager")
    ThirdPartyAdaptor callThdTradeManager;
	@Autowired
	BGSPServiceAccessObject bgspServiceAccessObject;
    private final static Log logger=LogFactory.getLog(UpdateCusAgentServiceAction.class);
   @Override
   public void execute(Context context) throws CoreException,
		CoreRuntimeException {
	   logger.info("=============Start  UpdateCusAgentServiceAction ");
		
		logger.info("=============End   UpdateCusAgentServiceAction ");
    }
}