package com.bocom.bbip.gdeupsb.action.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsBatchConsoleInfo;
import com.bocom.bbip.eups.repository.EupsBatchConsoleInfoRepository;
import com.bocom.bbip.gdeupsb.entity.GDEupsBatchConsoleInfo;
import com.bocom.bbip.gdeupsb.repository.GDEupsBatchConsoleInfoRepository;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class QryBatchStatueAction extends BaseAction{
		@Autowired
		EupsBatchConsoleInfoRepository eupsBatchConsoleInfoRepository;
		@Autowired
		GDEupsBatchConsoleInfoRepository gdEupsBatchConsoleInfoRepository;
		private final static Log logger=LogFactory.getLog(QryBatchStatueAction.class);
		
		public void execute(Context context)throws CoreException,CoreRuntimeException{
			logger.info("=============Start QryBatchStatueAction");
			String batNo=context.getData(ParamKeys.BAT_NO).toString();
			GDEupsBatchConsoleInfo gdEupsBatchConsoleInfo=gdEupsBatchConsoleInfoRepository.findOne(batNo);
			String fleNme=gdEupsBatchConsoleInfo.getRsvFld8();
			EupsBatchConsoleInfo eupsBatchConsoleInfos=new EupsBatchConsoleInfo();
			eupsBatchConsoleInfos.setFleNme(fleNme);
			EupsBatchConsoleInfo eupsBatchConsoleInfo=eupsBatchConsoleInfoRepository.find(eupsBatchConsoleInfos).get(0);
			context.setData(ParamKeys.COMPANY_NO, eupsBatchConsoleInfo.getComNo());
			context.setData(ParamKeys.COMPANY_NAME, eupsBatchConsoleInfo.getComNme());
			context.setData(ParamKeys.TOT_CNT, eupsBatchConsoleInfo.getTotCnt());
			context.setData(ParamKeys.TOT_AMT, eupsBatchConsoleInfo.getTotAmt());
			context.setData("batSts", eupsBatchConsoleInfo.getBatSts());
			context.setData(ParamKeys.FLE_NME, eupsBatchConsoleInfo.getFleNme());
			context.setData(ParamKeys.THD_BAT_NO, gdEupsBatchConsoleInfo.getRsvFld9());
			logger.info("=============End QryBatchStatueAction");
		}
}
