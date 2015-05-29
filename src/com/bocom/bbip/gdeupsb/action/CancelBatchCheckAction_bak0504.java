package com.bocom.bbip.gdeupsb.action;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bocom.bbip.comp.BBIPPublicServiceImpl;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.entity.GdBatchConsoleInfo;
import com.bocom.bbip.gdeupsb.repository.GdBatchConsoleInfoRepository;
import com.bocom.bbip.service.Result;
import com.bocom.bbip.utils.Assert;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.ContextUtils;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

public class CancelBatchCheckAction_bak0504 extends BaseAction {
	private static final Log logger=LogFactory.getLog(CancelBatchCheckAction_bak0504.class);

	
    public void execute(Context context)throws CoreException{
        logger.info("cancel batch before check start");
		final String batNo=ContextUtils.assertDataHasLengthAndGetNNR(context, ParamKeys.BAT_NO, ErrorCodes.EUPS_FIELD_EMPTY);
		Result result = ((BBIPPublicServiceImpl)get(GDConstants.BBIP_PUBLIC_SERVICE)).tryLock(batNo, 10L, 60 * 10L);
		Assert.isTrue(result.isSuccess(), "加锁失败");
		GdBatchConsoleInfo ret = get(GdBatchConsoleInfoRepository.class).findOne(batNo);
		Assert.isNotNull(ret, ErrorCodes.EUPS_BAT_CTL_INFO_NOT_EXIST);
		logger.info("批次信息:"+BeanUtils.toFlatMap(ret));
		Assert.isFalse(StringUtils.isBlank(ret.getBatSts()), "批次状态不存在");
		/**只有状态为I或W，才可以撤销批次*/
		boolean canCancel=ret.getBatSts().equalsIgnoreCase(GDConstants.BATCH_STATUS_INIT)||
		ret.getBatSts().equalsIgnoreCase(GDConstants.BATCH_STATUS_WAIT);
		Assert.isTrue(canCancel, "批次状态为已经完成或已经撤销，不可以撤销");
		logger.info("cancel batch before check end");
    }
  
}
