package com.bocom.bbip.gdeupsb.action.gzag.batch;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.entity.GDEupsBatchConsoleInfo;
import com.bocom.bbip.gdeupsb.repository.GDEupsBatchConsoleInfoRepository;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class QryBatchStatueAction extends BaseAction{
		private final static Log logger=LogFactory.getLog(QryBatchStatueAction.class);
		@Autowired
		GDEupsBatchConsoleInfoRepository gdEupsBatchConsoleInfoRepository;
		/**
		 * 广州文本 批量状态查询
		 */
		public void execute(Context context)throws CoreException,CoreRuntimeException{
			logger.info("=============Start QryBatchStatueAction");
						String batNo=context.getData(ParamKeys.BAT_NO).toString();
						GDEupsBatchConsoleInfo gdEupsBatchConsoleInfo=gdEupsBatchConsoleInfoRepository.findOne(batNo);
						
						context.setData(ParamKeys.COMPANY_NO, gdEupsBatchConsoleInfo.getComNo());
						context.setData(ParamKeys.FILE_DATE, gdEupsBatchConsoleInfo.getSubDte());
						context.setData(ParamKeys.COMPANY_NAME, gdEupsBatchConsoleInfo.getComNme());
						context.setData("batSts", gdEupsBatchConsoleInfo.getBatSts());
						context.setData("HostbatSts", gdEupsBatchConsoleInfo.getBatSts());
						context.setData(ParamKeys.TOT_CNT, gdEupsBatchConsoleInfo.getTotCnt());
						context.setData(ParamKeys.TOT_AMT, gdEupsBatchConsoleInfo.getTotAmt());
						
		}
}
