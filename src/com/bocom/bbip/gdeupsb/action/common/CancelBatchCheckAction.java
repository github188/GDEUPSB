package com.bocom.bbip.gdeupsb.action.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bocom.bbip.comp.BBIPPublicServiceImpl;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDErrorCodes;
import com.bocom.bbip.gdeupsb.entity.GDEupsBatchConsoleInfo;
import com.bocom.bbip.gdeupsb.repository.GDEupsBatchConsoleInfoRepository;
import com.bocom.bbip.service.Result;
import com.bocom.bbip.utils.Assert;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.ContextUtils;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

public class CancelBatchCheckAction extends BaseAction {
	private static final Log logger=LogFactory.getLog(CancelBatchCheckAction.class);


    public void check(Context context)throws Exception{
        context.setData("batNo", "150508TPO300300011");
		final String batNo=ContextUtils.assertDataHasLengthAndGetNNR(context, ParamKeys.BAT_NO, ErrorCodes.EUPS_FIELD_EMPTY);
		Result result = ((BBIPPublicServiceImpl)get(GDConstants.BBIP_PUBLIC_SERVICE)).tryLock(batNo, 60*1000L, 6000L);
		Assert.isTrue(result.isSuccess(), "加锁失败");
		GDEupsBatchConsoleInfo info = new GDEupsBatchConsoleInfo();
		info.setBatNo(batNo);
		GDEupsBatchConsoleInfo ret = get(GDEupsBatchConsoleInfoRepository.class).findConsoleInfo(info);
		Assert.isNotNull(ret, ErrorCodes.EUPS_BAT_CTL_INFO_NOT_EXIST);
		logger.info("批次信息:"+BeanUtils.toFlatMap(ret));
		Assert.isFalse(StringUtils.isBlank(ret.getBatSts()), "批次状态不存在");
		/**只有状态为I或W，才可以撤销批次*/
		boolean canCancel=ret.getBatSts().equalsIgnoreCase(GDConstants.BATCH_STATUS_INIT)||
		ret.getBatSts().equalsIgnoreCase(GDConstants.BATCH_STATUS_WAIT);
		Assert.isTrue(canCancel, GDErrorCodes.EUPS_BATCH_STATUS_ERROR);
		context.setData("BatchConsoleInfo", ret);
    }
    public void unLock(Context context)throws CoreException{
    	final String batNo=ContextUtils.assertDataHasLengthAndGetNNR(context, ParamKeys.BAT_NO, ErrorCodes.EUPS_FIELD_EMPTY);
    	Result result = ((BBIPPublicServiceImpl)get(GDConstants.BBIP_PUBLIC_SERVICE)).unlock(batNo);
		Assert.isTrue(result.isSuccess(), "解锁失败");
    }
}
