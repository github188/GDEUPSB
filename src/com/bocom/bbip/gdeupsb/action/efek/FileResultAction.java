package com.bocom.bbip.gdeupsb.action.efek;

import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class FileResultAction extends BaseAction{
	@Autowired
	BBIPPublicService bbipPublicService;
		@Override
		public void execute(Context context) throws CoreException,
				CoreRuntimeException {
				log.info("=====================Start   FileResultAction");
				context.setData(ParamKeys.EUPS_BUSS_TYPE, "ELEC00");
				context.setData("batNo", "150408TPO300300169");
				bbipPublicService.synExecute("eups.commNotifyBatchStatus", context);
				log.info("=====================End   FileResultAction");
				
				
		}
}
