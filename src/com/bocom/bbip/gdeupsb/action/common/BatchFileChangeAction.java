package com.bocom.bbip.gdeupsb.action.common;

import java.util.List;

import org.python.parser.ast.listcompType;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.comp.btp.BTPService;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsActSysPara;
import com.bocom.bbip.eups.repository.EupsActSysParaRepository;
import com.bocom.bbip.gdeupsb.entity.GDEupsBatchConsoleInfo;
import com.bocom.bbip.gdeupsb.repository.GDEupsBatchConsoleInfoRepository;
import com.bocom.bbip.utils.CollectionUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class BatchFileChangeAction extends BaseAction{
	@Autowired
	EupsActSysParaRepository eupsActSysParaRepository;
	@Autowired
	BBIPPublicService bbipPublicService;
	@Override
	public void execute(Context context) throws CoreException,
			CoreRuntimeException {
		log.info("====================Start   BatchFileChangeAction");
		String eupsBusTyp=context.getData(ParamKeys.EUPS_BUSS_TYPE).toString();
		String batNo =((BTPService)get("BTPService")).applyBatchNo(ParamKeys.BUSINESS_CODE_COLLECTION);
		context.setData("batNo", batNo);
		if(eupsBusTyp.equals("FSAG00")){
			log.info("====================Start   BatchFileChangeAction  FSAG00");
			bbipPublicService.synExecute("eups.fileBatchPayCreateDataProcess",context);
			GDEupsBatchConsoleInfo gdEupsBatchConsoleInfo=new GDEupsBatchConsoleInfo();
			gdEupsBatchConsoleInfo.setFleNme((String)context.getData("fleNme"));
			gdEupsBatchConsoleInfo.setEupsBusTyp("FSAG00");
			List<GDEupsBatchConsoleInfo> list=get(GDEupsBatchConsoleInfoRepository.class).find(gdEupsBatchConsoleInfo);
			if(CollectionUtils.isEmpty(list)){
					throw new CoreException("批次信息错误");
			}
			GDEupsBatchConsoleInfo gdEupsBatchConsoleInfos=list.get(0);
			context.setData("totCnt",gdEupsBatchConsoleInfos.getTotCnt());
			context.setData("totAmt",gdEupsBatchConsoleInfos.getTotAmt());
			context.setData("rsvFld7",gdEupsBatchConsoleInfos.getRsvFld7());
		}else if(eupsBusTyp.equals("ZHAG00")){
			log.info("====================Start   BatchFileChangeAction  ZHAG00");
			bbipPublicService.asynExecute("eups.fileBatchPayCreateDataProcess",context);
		}else if(eupsBusTyp.equals("ZHAG01")){
			log.info("====================Start   BatchFileChangeAction  ZHAG01");
			bbipPublicService.asynExecute("eups.fileBatchPayCreateDataProcess",context);
		}else if(eupsBusTyp.equals("ZHAG02")){
			log.info("====================Start   BatchFileChangeAction  ZHAG02");
			bbipPublicService.asynExecute("eups.fileBatchPayCreateDataProcess",context);
		}else{
			throw new CoreException("不支持该业务类型的交易");
		}
		
		String comNo=context.getData("comNo").toString();
		EupsActSysPara eupsActSysParas=new EupsActSysPara();
		eupsActSysParas.setComNo(comNo);
		String comNoAcps=eupsActSysParaRepository.find(eupsActSysParas).get(0).getSplNo();
		String fileName="BATC"+comNoAcps+"0.txt";
		context.setData(ParamKeys.FLE_NME, fileName);
		log.info("====================End   BatchFileChangeAction");
	}
}
