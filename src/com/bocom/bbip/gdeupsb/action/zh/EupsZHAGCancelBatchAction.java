package com.bocom.bbip.gdeupsb.action.zh;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.repository.GDEupsBatchConsoleInfoRepository;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
import com.bocom.jump.bp.core.Executable;

public class EupsZHAGCancelBatchAction extends BaseAction implements Executable  {
	private static final Log logger=LogFactory.getLog(EupsZHAGCancelBatchAction.class);

	@Override
	public void execute(Context context) throws CoreException,CoreRuntimeException {
		final String batNo=context.getData(ParamKeys.BAT_NO);
		get(GDEupsBatchConsoleInfoRepository.class).deleteConsoleInfo(batNo);
		logger.info("批次："+batNo+" 成功撤销");

	}

}
