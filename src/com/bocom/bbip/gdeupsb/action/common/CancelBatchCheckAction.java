package com.bocom.bbip.gdeupsb.action.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bocom.bbip.comp.BBIPPublicServiceImpl;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsBatchConsoleInfo;
import com.bocom.bbip.eups.repository.EupsBatchConsoleInfoRepository;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDErrorCodes;
import com.bocom.bbip.gdeupsb.entity.GDEupsBatchConsoleInfo;
import com.bocom.bbip.gdeupsb.repository.GDEupsBatchConsoleInfoRepository;
import com.bocom.bbip.service.Result;
import com.bocom.bbip.utils.Assert;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.ContextUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
/**
 * 
 * @author wuyh
 *
 */
public class CancelBatchCheckAction extends BaseAction {
	private static final Log logger=LogFactory.getLog(CancelBatchCheckAction.class);

/**
 * 批次撤销
 */
    public void check(Context context)throws Exception{
		final String batNo=ContextUtils.assertDataHasLengthAndGetNNR(context, ParamKeys.BAT_NO, ErrorCodes.EUPS_FIELD_EMPTY,"batNo");
		//上锁
		Result result = ((BBIPPublicServiceImpl)get(GDConstants.BBIP_PUBLIC_SERVICE)).tryLock(batNo, 60*1000L, 60*1000L);
		
		GDEupsBatchConsoleInfo gdEupsBatchConsoleInfo = get(GDEupsBatchConsoleInfoRepository.class).findOne(batNo);

		Assert.isNotNull(gdEupsBatchConsoleInfo, ErrorCodes.EUPS_BAT_CTL_INFO_NOT_EXIST);
		logger.info("批次信息:"+BeanUtils.toFlatMap(gdEupsBatchConsoleInfo));
		String fleNme=gdEupsBatchConsoleInfo.getRsvFld8();
		EupsBatchConsoleInfo eupsBatchConsoleInfos=new EupsBatchConsoleInfo();
		eupsBatchConsoleInfos.setFleNme(fleNme);
		EupsBatchConsoleInfo eupsBatchConsoleInfo=get(EupsBatchConsoleInfoRepository.class).find(eupsBatchConsoleInfos).get(0);
		/**只有状态为I或W，才可以撤销批次*/
		if(eupsBatchConsoleInfo.getBatSts().equals("I") || eupsBatchConsoleInfo.getBatSts().equals("W")){
					eupsBatchConsoleInfo.setBatSts("C");
					get(EupsBatchConsoleInfoRepository.class).update(eupsBatchConsoleInfo);
					gdEupsBatchConsoleInfo.setBatSts("C");
					get(GDEupsBatchConsoleInfoRepository.class).updateConsoleInfo(gdEupsBatchConsoleInfo);
					context.setData("cancelResult", "批次撤销完成");
		}else if(eupsBatchConsoleInfo.getBatSts().equals("S")){
					context.setData("cancelResult", "批次已完成，不能撤销");
		}else if(gdEupsBatchConsoleInfo.getBatSts().equals("C")){
					context.setData("cancelResult", "批次已撤销，不能再次撤销");
		}else{
				throw new CoreException("批次batNo获取状态错误");
		}
		
		context.setData("BatchConsoleInfo", gdEupsBatchConsoleInfo);
		
		
		unLock(context);
    }
    /**
     * 解锁
     */
    public void unLock(Context context)throws CoreException{
    	final String batNo=ContextUtils.assertDataHasLengthAndGetNNR(context, ParamKeys.BAT_NO, ErrorCodes.EUPS_FIELD_EMPTY);
    	Result result = ((BBIPPublicServiceImpl)get(GDConstants.BBIP_PUBLIC_SERVICE)).unlock(batNo);
		Assert.isTrue(result.isSuccess(), GDErrorCodes.EUPS_UNLOCK_FAIL);
    }
}
