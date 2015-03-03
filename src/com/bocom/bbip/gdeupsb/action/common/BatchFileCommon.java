package com.bocom.bbip.gdeupsb.action.common;

import java.util.Date;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bocom.bbip.comp.BBIPPublicServiceImpl;
import com.bocom.bbip.comp.btp.BTPService;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.action.common.OperateFTPAction;
import com.bocom.bbip.eups.action.common.OperateFileAction;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsThdFtpConfig;
import com.bocom.bbip.eups.repository.EupsThdFtpConfigRepository;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.entity.GDEupsBatchConsoleInfo;
import com.bocom.bbip.gdeupsb.repository.GDEupsBatchConsoleInfoRepository;
import com.bocom.bbip.service.Result;
import com.bocom.bbip.utils.Assert;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.ContextUtils;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

/**
 * @author wuyh
 * @date 2015-2-13 上午09:06:35
 * @Company TD
 */
public class BatchFileCommon extends BaseAction {
	
	private static final Log logger = LogFactory.getLog(BatchFileCommon.class);
	public void BeforeBatchProcess(final Context context)throws CoreException {
		String comNo=ContextUtils.assertDataNotEmptyAndGet(context, ParamKeys.COMPANY_NO, ErrorCodes.EUPS_COM_NO_NOTEXIST);
		String fleNme="BATC"+comNo+"0.txt";
		/** 检查批次是否存在 */
		GDEupsBatchConsoleInfo info = new GDEupsBatchConsoleInfo();
		info.setFleNme(fleNme);
		GDEupsBatchConsoleInfo ret =get(GDEupsBatchConsoleInfoRepository.class).findConsoleInfo(info);
		Assert.isNotNull(ret, "批次信息已经存在");
		/** 插入批次控制表 */
		String batNo =((BTPService)get("BTPService")).applyBatchNo(ParamKeys.BUSINESS_CODE_COLLECTION);
		info.setBatNo(batNo);
		info.setBatSts(GDConstants.BATCH_STATUS_INIT);
		info.setFleNme(fleNme);
		info.setComNo((String) context.getData(ParamKeys.COMPANY_NO));
		info.setSubDte(new Date());
		info.setTxnTlr((String) context.getData(ParamKeys.TELLER));
		info.setTxnMde(Constants.TXN_MDE_FILE);
		info.setTxnOrgCde((String) context.getData(ParamKeys.BR));
		get(GDEupsBatchConsoleInfoRepository.class).insertConsoleInfo(info);
		context.getDataMapDirectly().putAll(BeanUtils.toFlatMap(info));
	}

	public void unLock(final String lockKey)throws CoreException {
		Assert.isFalse(StringUtils.isBlank(lockKey), "lockKey必须输入");
		Result result = ((BBIPPublicServiceImpl)get(GDConstants.BBIP_PUBLIC_SERVICE)).unlock(lockKey);
		Assert.isTrue(result.isSuccess(), "批次解锁失败");
	}

	public void Lock(final String lockKey)throws CoreException {
		Assert.isFalse(StringUtils.isBlank(lockKey), "lockKey必须输入");
		Result result = ((BBIPPublicServiceImpl)get(GDConstants.BBIP_PUBLIC_SERVICE)).tryLock(lockKey, 60*1000L, 600L);
		Assert.isTrue(result.isSuccess(), "批次加锁失败");
	}

	public void sendBatchFileToACP(final Context context) throws CoreException {
		String fileName = ContextUtils.assertVariableNotEmptyAndGet(context,ParamKeys.FLE_NME, ErrorCodes.EUPS_FILE_CREATE_NAME_ISEMPTY);
		Map<String, Object> fileMap = (Map<String, Object>) ContextUtils.assertVariableNotNullAndGet(context, "agtFileMap","agtFileMap不能为空");
		EupsThdFtpConfig config = get(EupsThdFtpConfigRepository.class).findOne("BatchFileFtpNo");
		Assert.isNotNull(config, ErrorCodes.EUPS_FTP_INFO_NOTEXIST,"第三方配置信息不存在");
		config.setLocFleNme(fileName);
		config.setRmtFleNme(fileName);
		/** 产生代收付格式文件 */
		((OperateFileAction)get("opeFile")).createCheckFile(config, GDConstants.BATCH_FILE_FORMAT, fileName, fileMap);
		/** 发送到指定路径 */
		((OperateFTPAction)get("opeFTP")).putCheckFile(config);
	}
}
