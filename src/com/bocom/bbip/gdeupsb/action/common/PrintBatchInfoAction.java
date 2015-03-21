package com.bocom.bbip.gdeupsb.action.common;

import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.entity.GDEupsBatchConsoleInfo;
import com.bocom.bbip.gdeupsb.repository.GDEupsBatchConsoleInfoRepository;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class PrintBatchInfoAction extends BaseAction{
	@Autowired
	GDEupsBatchConsoleInfoRepository gdEupsBatchConsoleInfoRepository;
		@Override
		public void execute(Context context) throws CoreException,
				CoreRuntimeException {
				String batNo=context.getData(ParamKeys.BAT_NO).toString();
				GDEupsBatchConsoleInfo gdEupsBatchConsoleInfo=gdEupsBatchConsoleInfoRepository.findOne(batNo);
				
				
		}
}
