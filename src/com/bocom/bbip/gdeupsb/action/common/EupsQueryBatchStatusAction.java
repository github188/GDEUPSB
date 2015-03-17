package com.bocom.bbip.gdeupsb.action.common;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.entity.GDEupsBatchConsoleInfo;
import com.bocom.bbip.gdeupsb.repository.GDEupsBatchConsoleInfoRepository;
import com.bocom.bbip.utils.Assert;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.ContextUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

public class EupsQueryBatchStatusAction extends BaseAction {
	private static final Log logger=LogFactory.getLog(EupsQueryBatchStatusAction.class);
	
	public void execute(Context context)throws CoreException{
		logger.info("----批次信息查询开始----");
		final String batNo=ContextUtils.assertDataNotEmptyAndGet(context, "batNo", ErrorCodes.EUPS_FIELD_EMPTY, "批次号");
		final String eupsBusTyp=context.getData(ParamKeys.EUPS_BUSS_TYPE);
		GDEupsBatchConsoleInfo info = new GDEupsBatchConsoleInfo();
		info.setBatNo(batNo);
		info.setEupsBusTyp(eupsBusTyp);
		List<GDEupsBatchConsoleInfo> ret = get(GDEupsBatchConsoleInfoRepository.class).find(info);
		Assert.isNotEmpty(ret, ErrorCodes.EUPS_BAT_CTL_INFO_NOT_EXIST);
		logger.info("批次信息:"+BeanUtils.toFlatMap(ret.get(0)));
		context.getDataMapDirectly().putAll(BeanUtils.toFlatMap(ret.get(0)));
	}

}
