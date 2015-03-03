package com.bocom.bbip.gdeupsb.action.elec02;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bocom.bbip.data.domain.PageRequest;
import com.bocom.bbip.data.domain.Pageable;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.utils.Assert;
import com.bocom.bbip.utils.ContextUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
/**460265*/
public class queryCheckInfoAction extends BaseAction {
	private static final Log logger = LogFactory.getLog(queryCheckInfoAction.class);
	public void process(Context context) throws CoreException,
			CoreRuntimeException {
		logger.info("query checkinfo start!!");
		final String NodNo = ContextUtils.assertDataHasLengthAndGetNNR(context,
				ParamKeys.BK,  ErrorCodes.EUPS_FIELD_EMPTY);
		Assert.isFalse(NodNo.equalsIgnoreCase(GDConstants.MANAGE_ORG_445012),  ErrorCodes.EUPS_FIELD_EMPTY,
				"非管理机构不能执行本交易");
		final String brNo = ContextUtils.assertDataHasLengthAndGetNNR(context,
				ParamKeys.BR,  ErrorCodes.EUPS_FIELD_EMPTY);
		final String BgnDat = ContextUtils.assertDataHasLengthAndGetNNR(
				context, "BgnDat",  ErrorCodes.EUPS_FIELD_EMPTY);
		final String EndDat = ContextUtils.assertDataHasLengthAndGetNNR(
				context, "EndDat",  ErrorCodes.EUPS_FIELD_EMPTY);
		Pageable pageable=ContextUtils.getDataAsObject(context, PageRequest.class);
		Assert.isFalse(null==pageable, ErrorCodes.EUPS_FIELD_EMPTY,"pageable is null!!");
		//ElecCheckInfo elecCheckInfo=new ElecCheckInfo();
		//elecCheckInfo.setBrNo(brNo);
		//elecCheckInfo.setActDat(BgnDat);
		//TODO database implement
        /*Page<ElecCheckInfo> page=get(EfeinfctRepository.class).findAll(pageable, elecCheckInfo);
		Assert.isFalse(0==page.getTotalElements(), "errorCode1","pageable's size zero!!");
		setResponseFromPage(context,"data", page);*/
		
		logger.info("query checkinfo success!!");
		
	
		
	}

}
