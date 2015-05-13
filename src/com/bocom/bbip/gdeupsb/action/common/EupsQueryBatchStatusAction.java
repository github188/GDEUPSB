package com.bocom.bbip.gdeupsb.action.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.gdeupsb.entity.GDEupsBatchConsoleInfo;
import com.bocom.bbip.gdeupsb.repository.GDEupsBatchConsoleInfoRepository;
import com.bocom.bbip.service.BGSPServiceAccessObject;
import com.bocom.bbip.service.Result;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.ContextUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

public class EupsQueryBatchStatusAction extends BaseAction {
	private static final Log logger=LogFactory.getLog(EupsQueryBatchStatusAction.class);
	@Autowired
	BGSPServiceAccessObject bgspServiceAccessObject;
/**
 * 查询批次信息	
 */
	public void execute(Context context)throws CoreException{
		logger.info("----批次信息查询开始----");
		final String batNo=ContextUtils.assertDataNotEmptyAndGet(context, "batNo", ErrorCodes.EUPS_FIELD_EMPTY, "批次号");

		GDEupsBatchConsoleInfo gdEupsBatchConsoleInfo=get(GDEupsBatchConsoleInfoRepository.class).findOne(batNo);
		logger.info("批次信息:"+BeanUtils.toFlatMap(gdEupsBatchConsoleInfo));
		context.setData("batNo",gdEupsBatchConsoleInfo.getBatNo());
		context.setData("comNo",gdEupsBatchConsoleInfo.getComNo());
		
		logger.info("===============Start   QryComInfoAction");
		 context.setData("inqBusLstFlg","N");
		 //代收付搜索单位信息
		 Result comResult= bgspServiceAccessObject.callServiceFlatting("queryCorporInfo",context.getDataMap());
		 log.info("==========="+comResult);
		 String comNme=comResult.getPayload().get("comNum").toString();
		 logger.info("===============End   QryComInfoAction");
		context.setData("comNme",comNme);
		context.setData("eupsBusTyp",gdEupsBatchConsoleInfo.getEupsBusTyp());
		context.setData("totCnt",gdEupsBatchConsoleInfo.getTotCnt());
		context.setData("totAmt",gdEupsBatchConsoleInfo.getTotAmt());
		context.setData("batSts",gdEupsBatchConsoleInfo.getBatSts());
		context.setData("fleNme",gdEupsBatchConsoleInfo.getRsvFld8());
		context.setData("thdBatNo",gdEupsBatchConsoleInfo.getRsvFld7());
		
		 logger.info("===============End   EupsQueryBatchStatusAction");
	}

}
