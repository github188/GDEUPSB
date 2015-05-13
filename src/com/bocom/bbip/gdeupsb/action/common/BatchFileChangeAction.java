package com.bocom.bbip.gdeupsb.action.common;

import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsActSysPara;
import com.bocom.bbip.eups.repository.EupsActSysParaRepository;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class BatchFileChangeAction extends BaseAction{
	@Autowired
	EupsActSysParaRepository eupsActSysParaRepository;
	@Override
	public void execute(Context context) throws CoreException,
			CoreRuntimeException {
		BBIPPublicService bbipPublicService=get(BBIPPublicService.class);
		log.info("====================Start   BatchFileChangeAction");
		String eupsBusTyp=context.getData(ParamKeys.EUPS_BUSS_TYPE).toString();
		if(eupsBusTyp.equals("FSAG00")){
			log.info("====================Start   BatchFileChangeAction  FSAG00");
			bbipPublicService.synExecute("eups.fileBatchPayCreateDataProcess",context);
		}else if(eupsBusTyp.equals("ZHAG00") || eupsBusTyp.equals("ZHAG01") || eupsBusTyp.equals("ZHAG02")){
			log.info("====================Start   BatchFileChangeAction  "+eupsBusTyp);
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
