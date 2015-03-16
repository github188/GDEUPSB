package com.bocom.bbip.gdeupsb.action.elec02;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bocom.bbip.data.domain.Page;
import com.bocom.bbip.data.domain.PageRequest;
import com.bocom.bbip.data.domain.Pageable;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.entity.EupsTransJournalELEC02Check;
import com.bocom.bbip.gdeupsb.repository.EupsTransJournalELEC02CheckRepository;
import com.bocom.bbip.utils.Assert;
import com.bocom.bbip.utils.ContextUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
/**460265*/
public class queryCheckInfoAction extends BaseAction {
	private static final Log logger = LogFactory.getLog(queryCheckInfoAction.class);
	public void process(Context context) throws CoreException,
			CoreRuntimeException {
		logger.info("query checkinfo start!!");
		context.setData("BgnDat", "20150301");
		context.setData("EndDat", "20150309");
		context.setData(ParamKeys.PAGE_NUM, 1);
		context.setData(ParamKeys.PAGE_SIZE, 10);
		final String NodNo = ContextUtils.assertDataHasLengthAndGetNNR(context,
				ParamKeys.BK,  ErrorCodes.EUPS_FIELD_EMPTY);
		//Assert.isFalse(NodNo.equalsIgnoreCase(GDConstants.MANAGE_ORG_445012),  ErrorCodes.EUPS_FIELD_EMPTY,
		//		"非管理机构不能执行本交易");
		final String brNo = ContextUtils.assertDataHasLengthAndGetNNR(context,
				ParamKeys.BR,  ErrorCodes.EUPS_FIELD_EMPTY);
		final String BgnDat = ContextUtils.assertDataHasLengthAndGetNNR(
				context, "BgnDat",  ErrorCodes.EUPS_FIELD_EMPTY);
		final String EndDat = ContextUtils.assertDataHasLengthAndGetNNR(
				context, "EndDat",  ErrorCodes.EUPS_FIELD_EMPTY);
		Pageable pageable=ContextUtils.getDataAsObject(context, PageRequest.class);
		Assert.isFalse(null==pageable, ErrorCodes.EUPS_FIELD_EMPTY);
		Map map=new HashMap();
		Date start=DateUtils.parse(BgnDat);
		Date end=DateUtils.parse(EndDat);
		map.put("EupsBusType", "ELEC02");
		map.put("startDate", start);
		map.put("endDate", end);
		Page<EupsTransJournalELEC02Check> page=get(EupsTransJournalELEC02CheckRepository.class).getCheckInfo(pageable, map);
		
		logger.info("query checkinfo success!!");
		
	
		
	}

}
