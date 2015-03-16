package com.bocom.bbip.gdeupsb.service.impl.elec02;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.action.common.OperateFileAction;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.spi.service.batch.BatchAcpService;
import com.bocom.bbip.eups.spi.vo.PrepareBatchAcpDomain;
import com.bocom.bbip.gdeupsb.action.common.BatchFileCommon;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.entity.GDEupsBatchConsoleInfo;
import com.bocom.bbip.gdeupsb.entity.GDEupsZhAGBatchTemp;
import com.bocom.bbip.gdeupsb.repository.GDEupsBatchConsoleInfoRepository;
import com.bocom.bbip.gdeupsb.repository.GDEupsZHAGBatchTempRepository;
import com.bocom.bbip.utils.Assert;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.ContextUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.support.CollectionUtils;

public class BatchAcpServiceImplELEC02 extends BaseAction implements BatchAcpService {
	private static final Log logger=LogFactory.getLog(BatchAcpServiceImplELEC02.class);
	@Override
	public void prepareBatchDeal(PrepareBatchAcpDomain domain, Context context)throws CoreException {
		context.setData("batNo", "123");
		final String comNo=ContextUtils.assertDataHasLengthAndGetNNR(context, ParamKeys.COMPANY_NO, ErrorCodes.EUPS_FIELD_EMPTY);
        /**上锁*/
		((BatchFileCommon)get(GDConstants.BATCH_FILE_COMMON_UTILS)).Lock(comNo);
		/**批量前检查和初始化批量控制表 生成批次号batNo*/
		((BatchFileCommon)get(GDConstants.BATCH_FILE_COMMON_UTILS)).BeforeBatchProcess(context);
		logger.info("开始解析批量文件-------------");
		
		List<Map<String,Object>> result=((OperateFileAction)get("opeFile")).pareseFileByPath("D:\\batch.txt", "", "fileId");
		Assert.isFalse(null==result||0==result.size(), ErrorCodes.EUPS_FILE_PARESE_FAIL, "解析文件出错");
		List <GDEupsZhAGBatchTemp>list=(List<GDEupsZhAGBatchTemp>) BeanUtils.toObjects(result, GDEupsZhAGBatchTemp.class);
        /**插入临时表中*/
		get(GDEupsZHAGBatchTempRepository.class).batchInsert(list);
		
		List <GDEupsZhAGBatchTemp>lst=get(GDEupsZHAGBatchTempRepository.class).findByBatNo(comNo);
		for(GDEupsZhAGBatchTemp temp:lst){
			temp.setCusAc(temp.getCusAc()==null?temp.getThdCusNo():temp.getCusAc());
			temp.setCusNme(temp.getCusNme()==null?temp.getThdCusNme():temp.getCusNme());
			temp.setThdCusNo(temp.getCusAc()==null?temp.getThdCusNo():temp.getCusAc());
			temp.setThdCusNme(temp.getCusNme()==null?temp.getThdCusNme():temp.getCusNme());
			
		}
		List<Map<String,Object>> detail=(List<Map<String, Object>>) BeanUtils.toMaps(lst);
		Map<String, Object> header = CollectionUtils.createMap();
		header.put(ParamKeys.COMPANY_NO, comNo);
		Map<String, Object> temp = CollectionUtils.createMap();
		temp.put(ParamKeys.EUPS_FILE_HEADER, header);
		temp.put(ParamKeys.EUPS_FILE_DETAIL, detail);
		context.setVariable("agtFileMap", temp);
		((BatchFileCommon)get(GDConstants.BATCH_FILE_COMMON_UTILS)).sendBatchFileToACP(context);
		GDEupsBatchConsoleInfo console=new GDEupsBatchConsoleInfo();
		/**更新批次状态为待提交*/
		console.setBatSts(GDConstants.BATCH_STATUS_WAIT);
		get(GDEupsBatchConsoleInfoRepository.class).updateConsoleInfo(console);
		((BatchFileCommon)get(GDConstants.BATCH_FILE_COMMON_UTILS)).unLock(comNo);
		logger.info("批量文件数据准备结束-------------");
	}

}
