package com.bocom.bbip.gdeupsb.action.common;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.entity.EupsBatchConsoleInfo;
import com.bocom.bbip.eups.repository.EupsBatchConsoleInfoRepository;
import com.bocom.bbip.gdeupsb.entity.GDEupsBatchConsoleInfo;
import com.bocom.bbip.gdeupsb.repository.GDEupsBatchConsoleInfoRepository;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class CommNotifyBatchStatusAction extends BaseAction{
		@Autowired
		GDEupsBatchConsoleInfoRepository gdEupsBatchConsoleInfoRepository;
		@Autowired
		BBIPPublicService bbipPublicService;
		@Override
		public void execute(Context context) throws CoreException,
				CoreRuntimeException {
				log.info("==============Start  CommNotifyBatchStatusAction");
				String batNo=context.getData("batNo").toString().trim();
				//获取总行批次信息
				EupsBatchConsoleInfo eupsBatchConsoleInfo=get(EupsBatchConsoleInfoRepository.class).findOne(batNo);
				String batNos=eupsBatchConsoleInfo.getRsvFld1();
				eupsBatchConsoleInfo.setPayCnt(null);
				//更改总行控制 使其可以手动调用反盘文件
				get(EupsBatchConsoleInfoRepository.class).update(eupsBatchConsoleInfo);
				log.info("==============update  eupsBatchConsoleInfo  set  payCnt = null ; batNo = "+batNo);
				log.info("============================"+eupsBatchConsoleInfo);
				//异步调用 反盘文件
				String mothed="eups.commNotifyBatchStatus";
				bbipPublicService.asynExecute(mothed, context);
				//返回文件名
				GDEupsBatchConsoleInfo gdEupsBatchConsoleInfo=gdEupsBatchConsoleInfoRepository.findOne(batNos);
				String fileName=gdEupsBatchConsoleInfo.getComNo()+"_"+DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMdd)+".txt";
				//返回字段配置
		        context.setData("ApFmt",  "48211");
		        context.setData("batNo",  gdEupsBatchConsoleInfo.getBatNo());
		        context.setData("comNo",  gdEupsBatchConsoleInfo.getComNo());
		        context.setData("subDte",  gdEupsBatchConsoleInfo.getSubDte());
		        context.setData("comNme", fileName );
		        context.setData("batSts",  gdEupsBatchConsoleInfo.getBatSts());
		        context.setData("totCnt",  gdEupsBatchConsoleInfo.getTotCnt());
		        context.setData("totAmt",  gdEupsBatchConsoleInfo.getTotAmt());
		        context.setData("sucTotCnt"  ,gdEupsBatchConsoleInfo.getSucTotCnt());
		        context.setData("sucTotAmt", gdEupsBatchConsoleInfo.getSucTotAmt());
		        log.info("==============End  CommNotifyBatchStatusAction");
		}
}
