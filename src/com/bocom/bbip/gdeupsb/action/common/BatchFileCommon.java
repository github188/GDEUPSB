package com.bocom.bbip.gdeupsb.action.common;

import java.util.Date;
import java.util.List;
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
import com.bocom.bbip.eups.entity.EupsActSysPara;
import com.bocom.bbip.eups.entity.EupsThdFtpConfig;
import com.bocom.bbip.eups.repository.EupsActSysParaRepository;
import com.bocom.bbip.eups.repository.EupsThdFtpConfigRepository;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.entity.GDEupsBatchConsoleInfo;
import com.bocom.bbip.gdeupsb.repository.GDEupsBatchConsoleInfoRepository;
import com.bocom.bbip.service.Result;
import com.bocom.bbip.utils.Assert;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.ContextUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.SystemConfig;
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
		String fleNme=ContextUtils.assertDataNotEmptyAndGet(context, ParamKeys.FLE_NME, ErrorCodes.EUPS_COM_NO_NOTEXIST);
		String eupsBusTyp=ContextUtils.assertDataNotEmptyAndGet(context, ParamKeys.EUPS_BUSS_TYPE, ErrorCodes.EUPS_COM_NO_NOTEXIST);

		/** 检查批次是否存在 */
		GDEupsBatchConsoleInfo info = new GDEupsBatchConsoleInfo();
		info.setFleNme(fleNme);
		info.setBusKnd(eupsBusTyp);
		List<GDEupsBatchConsoleInfo> ret =get(GDEupsBatchConsoleInfoRepository.class).find(info);
		Assert.isTrue(null==ret||0==ret.size(), "批次信息已经存在");
		/** 插入批次控制表 */
		String batNo =((BTPService)get("BTPService")).applyBatchNo(ParamKeys.BUSINESS_CODE_COLLECTION);
		info.setBatNo(batNo);
		info.setBatSts(GDConstants.BATCH_STATUS_INIT);
		info.setFleNme(fleNme);
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
		final String comNo=(String)context.getData(ParamKeys.COMPANY_NO);
		final String tlr=(String)context.getData(ParamKeys.TELLER);
		final String br=(String)context.getData(ParamKeys.BR);
        final String AcDate=DateUtils.format(
        	((BBIPPublicServiceImpl)get(GDConstants.BBIP_PUBLIC_SERVICE)).getAcDate(),DateUtils.STYLE_yyyyMMdd);
        final String systemCode=((SystemConfig)get(SystemConfig.class)).getSystemCode();;
        final String busTyp=(String)context.getData("busTyp");
        final String dir="/home/bbipadm/data/mftp/BBIP/"+systemCode+"/"+br+"/"+tlr+"/"+AcDate+"/";
        EupsActSysPara eupsActSysPara = new EupsActSysPara();
        eupsActSysPara.setActSysTyp("0");
        eupsActSysPara.setComNo(comNo);
        List resultList = ((EupsActSysParaRepository)get(EupsActSysParaRepository.class)).find(eupsActSysPara);
        Assert.isNotEmpty(resultList, ErrorCodes.EUPS_QUERY_NO_DATA);
        final String comNoAcps =((EupsActSysPara)resultList.get(0)).getSplNo();
        final String fleNme="BATC"+comNoAcps+busTyp+".txt";

        Map<String, Object> fileMap = (Map<String, Object>) ContextUtils.assertVariableNotNullAndGet(context, "agtFileMap","agtFileMap不能为空");
		EupsThdFtpConfig config = get(EupsThdFtpConfigRepository.class).findOne("BatchFileFtpNo");
		Assert.isFalse(null==config, ErrorCodes.EUPS_FTP_INFO_NOTEXIST,"代收付FTP配置信息不存在");
		config.setLocFleNme(fleNme);
		config.setRmtFleNme(fleNme);
		config.setRmtWay(dir);
		/** 产生代收付格式文件 */
		((OperateFileAction)get("opeFile")).createCheckFile(config, GDConstants.BATCH_FILE_FORMAT, fleNme, fileMap);
		/** 发送到指定路径 */
		((OperateFTPAction)get("opeFTP")).putCheckFile(config);
		context.setData(ParamKeys.FLE_NME, fleNme);
		context.setData(ParamKeys.TXN_MDE, Constants.TXN_MDE_FILE);
		context.setData(ParamKeys.TOT_CNT, context.getData(ParamKeys.TOT_CNT));
		context.setData(ParamKeys.TOT_AMT, context.getData(ParamKeys.TOT_AMT));
	}
	public void afterBatchProcess(Context context)throws CoreException{
		final String batNo=ContextUtils.assertDataNotEmptyAndGet(context, ParamKeys.BAT_NO, ErrorCodes.EUPS_QUERY_NO_DATA);
		GDEupsBatchConsoleInfo info = new GDEupsBatchConsoleInfo();
		info.setBatNo(batNo);
		GDEupsBatchConsoleInfo ret =get(GDEupsBatchConsoleInfoRepository.class).findConsoleInfo(info);
		Assert.isFalse(null==ret, "批次信息不存在");
		ret.setBatSts(GDConstants.BATCH_STATUS_COMPLETE);
		get(GDEupsBatchConsoleInfoRepository.class).updateConsoleInfo(ret);
	}
}
