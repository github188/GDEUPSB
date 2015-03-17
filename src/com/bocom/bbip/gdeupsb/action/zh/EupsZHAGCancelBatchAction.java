package com.bocom.bbip.gdeupsb.action.zh;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.entity.GDEupsBatchConsoleInfo;
import com.bocom.bbip.gdeupsb.entity.GDEupsZhAGBatchTemp;
import com.bocom.bbip.gdeupsb.repository.GDEupsBatchConsoleInfoRepository;
import com.bocom.bbip.gdeupsb.repository.GDEupsZHAGBatchTempRepository;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
import com.bocom.jump.bp.core.Executable;

public class EupsZHAGCancelBatchAction extends BaseAction implements Executable  {
	private static final Log logger=LogFactory.getLog(EupsZHAGCancelBatchAction.class);

	@Override
	public void execute(Context context) throws CoreException,CoreRuntimeException {
		final String batNo=context.getData(ParamKeys.BAT_NO);
   	    logger.info("---批次："+batNo+"开始撤销---");
   		GDEupsBatchConsoleInfo info=new GDEupsBatchConsoleInfo();
   		info.setBatNo(batNo);
   		info.setBatSts(GDConstants.BATCH_STATUS_CANCEL);
   		/**更新批次状态为已经撤销*/
   		get(GDEupsBatchConsoleInfoRepository.class).updateConsoleInfo(info);
   		/**清楚批量临时表中该批次的信息*/
   		
   		get(GDEupsZHAGBatchTempRepository.class).deleteByBatNo(batNo);
   		logger.info("---批次："+batNo+"成功撤销---");

	}

}
