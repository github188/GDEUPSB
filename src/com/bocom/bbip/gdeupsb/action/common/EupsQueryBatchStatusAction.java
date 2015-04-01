package com.bocom.bbip.gdeupsb.action.common;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsBatchConsoleInfo;
import com.bocom.bbip.eups.repository.EupsBatchConsoleInfoRepository;
import com.bocom.bbip.gdeupsb.entity.GDEupsBatchConsoleInfo;
import com.bocom.bbip.gdeupsb.repository.GDEupsBatchConsoleInfoRepository;
import com.bocom.bbip.utils.Assert;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.ContextUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

public class EupsQueryBatchStatusAction extends BaseAction {
	private static final Log logger=LogFactory.getLog(EupsQueryBatchStatusAction.class);
/**
 * 查询批次信息	
 */
	public void execute(Context context)throws CoreException{
		logger.info("----批次信息查询开始----");
		final String batNo=ContextUtils.assertDataNotEmptyAndGet(context, "batNo", ErrorCodes.EUPS_FIELD_EMPTY, "批次号");
		final String eupsBusTyp=context.getData(ParamKeys.EUPS_BUSS_TYPE);
		GDEupsBatchConsoleInfo info = new GDEupsBatchConsoleInfo();
		info.setBatNo(batNo);
		info.setEupsBusTyp(eupsBusTyp);
		List<GDEupsBatchConsoleInfo> ret = get(GDEupsBatchConsoleInfoRepository.class).find(info);
		//EUPS 表中数据
		GDEupsBatchConsoleInfo gdEupsBatchConsoleInfo=ret.get(0);
		EupsBatchConsoleInfo eupsBatchConsoleInfos=new EupsBatchConsoleInfo();
		eupsBatchConsoleInfos.setFleNme(gdEupsBatchConsoleInfo.getFleNme());
		EupsBatchConsoleInfo eupsBatchConsoleInfo=get(EupsBatchConsoleInfoRepository.class).find(eupsBatchConsoleInfos).get(0);
		gdEupsBatchConsoleInfo.setBatSts(eupsBatchConsoleInfo.getBatSts());
		gdEupsBatchConsoleInfo.setFleNme(eupsBatchConsoleInfo.getFleNme());
		Assert.isNotEmpty(ret, ErrorCodes.EUPS_BAT_CTL_INFO_NOT_EXIST);
		logger.info("批次信息:"+BeanUtils.toFlatMap(gdEupsBatchConsoleInfo));
		context.getDataMapDirectly().putAll(BeanUtils.toFlatMap(gdEupsBatchConsoleInfo));
	}

}
