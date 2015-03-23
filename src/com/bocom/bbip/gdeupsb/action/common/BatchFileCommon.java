package com.bocom.bbip.gdeupsb.action.common;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.comp.BBIPPublicServiceImpl;
import com.bocom.bbip.comp.btp.BTPService;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.action.common.OperateFTPAction;
import com.bocom.bbip.eups.action.common.OperateFileAction;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsActSysPara;
import com.bocom.bbip.eups.entity.EupsBatchConsoleInfo;
import com.bocom.bbip.eups.entity.EupsThdFtpConfig;
import com.bocom.bbip.eups.repository.EupsActSysParaRepository;
import com.bocom.bbip.eups.repository.EupsBatchConsoleInfoRepository;
import com.bocom.bbip.eups.repository.EupsThdFtpConfigRepository;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDErrorCodes;
import com.bocom.bbip.gdeupsb.entity.GDEupsBatchConsoleInfo;
import com.bocom.bbip.gdeupsb.repository.GDEupsBatchConsoleInfoRepository;
import com.bocom.bbip.service.Result;
import com.bocom.bbip.utils.Assert;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.CollectionUtils;
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
	@Autowired
	OperateFTPAction operateFTPAction;
	private static final Log logger = LogFactory.getLog(BatchFileCommon.class);
	/**
	 * 检查批量是否可以进行
	 */
	public void BeforeBatchProcess(final Context context)throws CoreException {
		logger.info("----before batch check----");
		String comNo=ContextUtils.assertDataNotEmptyAndGet(context, ParamKeys.COMPANY_NO, ErrorCodes.EUPS_COM_NO_NOTEXIST);
		String fleNme=ContextUtils.assertDataNotEmptyAndGet(context, ParamKeys.FLE_NME, ErrorCodes.EUPS_FIELD_EMPTY,"fleNme");
		String eupsBusTyp=ContextUtils.assertDataNotEmptyAndGet(context, ParamKeys.EUPS_BUSS_TYPE, ErrorCodes.EUPS_BUS_TYP_ISEMPTY);

		/** 检查批次是否存在 */
		GDEupsBatchConsoleInfo info = new GDEupsBatchConsoleInfo();
		info.setFleNme(fleNme);
		GDEupsBatchConsoleInfo ret =get(GDEupsBatchConsoleInfoRepository.class).findConsoleInfo(info);
		Assert.isTrue(null==ret, GDErrorCodes.EUPS_BATCH_INFO_EXIST);
		/** 插入批次控制表 */
		String batNo =((BTPService)get("BTPService")).applyBatchNo(ParamKeys.BUSINESS_CODE_COLLECTION);
		info.setBatNo(batNo);
		info.setEupsBusTyp(eupsBusTyp);
		info.setBatSts(GDConstants.BATCH_STATUS_INIT);
		info.setFleNme(fleNme);
		info.setSubDte(new Date());
		info.setTxnTlr((String) context.getData(ParamKeys.TELLER));
		info.setTxnMde(Constants.TXN_MDE_FILE);
		info.setTxnOrgCde((String) context.getData(ParamKeys.BR));
		/**该字段保存ParamKeys.THD_BAT_NO*/
		info.setRsvFld9(((BBIPPublicServiceImpl)get(GDConstants.BBIP_PUBLIC_SERVICE)).getBBIPSequence());
		context.setData(ParamKeys.THD_BAT_NO, info.getRsvFld9());
		/**查询代收付ComNo*/
		     String comNoAcps=null;
		    EupsActSysPara eupsActSysPara = new EupsActSysPara();
		    eupsActSysPara.setActSysTyp("0");
		    eupsActSysPara.setComNo(comNo);
		    List resultList = ((EupsActSysParaRepository)get(EupsActSysParaRepository.class)).find(eupsActSysPara);
		    if (CollectionUtils.isNotEmpty(resultList)) {
		       comNoAcps = ((EupsActSysPara)resultList.get(0)).getSplNo();
		    }
		context.setData("comNoAcps", comNoAcps);
		//文本代收付 fileId 反盘文件时使用
		info.setRsvFld7((String)context.getData("fileId"));
		//文件名   和eups控制表关联  必须有
		info.setRsvFld8("BATC"+comNoAcps+"0.txt");
		//保存到控制表  
		get(GDEupsBatchConsoleInfoRepository.class).insertConsoleInfo(info);
		context.getDataMapDirectly().putAll(BeanUtils.toFlatMap(info));
	}
/**
 * 上锁
 */
	public void unLock(final String lockKey)throws CoreException {
		Assert.isFalse(StringUtils.isBlank(lockKey), ErrorCodes.EUPS_FIELD_EMPTY, "lockKey");
		Result result = ((BBIPPublicServiceImpl)get(GDConstants.BBIP_PUBLIC_SERVICE)).unlock(lockKey);
		Assert.isTrue(result.isSuccess(), GDErrorCodes.EUPS_UNLOCK_FAIL);
	}
	/**
	 * 解锁
	 */
	public void Lock(final String lockKey)throws CoreException {
		Assert.isFalse(StringUtils.isBlank(lockKey), ErrorCodes.EUPS_FIELD_EMPTY ,"lockKey");
		Result result = ((BBIPPublicServiceImpl)get(GDConstants.BBIP_PUBLIC_SERVICE)).tryLock(lockKey, 60*1000L, 600L);
		Assert.isTrue(result.isSuccess(), GDErrorCodes.EUPS_LOCK_FAIL);
	}
	/**
	 * 生成代收付文件 上传到服务器
	 */
	public void sendBatchFileToACP(final Context context) throws CoreException {
		final String comNo=(String)context.getData(ParamKeys.COMPANY_NO);
		final String tlr=(String)context.getData(ParamKeys.TELLER);
		final String br=(String)context.getData(ParamKeys.BR);
        final String AcDate=DateUtils.format(
        	((BBIPPublicServiceImpl)get(GDConstants.BBIP_PUBLIC_SERVICE)).getAcDate(),DateUtils.STYLE_yyyyMMdd);
        final String systemCode=((SystemConfig)get(SystemConfig.class)).getSystemCode();
        final String dir="/home/bbipadm/data/mftp/BBIP/"+systemCode+"/"+br+"/"+tlr+"/"+AcDate+"/";
        context.setData("dir", dir);
        
        EupsActSysPara eupsActSysPara = new EupsActSysPara();
        eupsActSysPara.setActSysTyp("0");
        eupsActSysPara.setComNo(comNo);
        List resultList = ((EupsActSysParaRepository)get(EupsActSysParaRepository.class)).find(eupsActSysPara);
        Assert.isNotEmpty(resultList, ErrorCodes.EUPS_QUERY_NO_DATA);
        final String comNoAcps =((EupsActSysPara)resultList.get(0)).getSplNo();
        final String fleNme="BATC"+comNoAcps+"0.txt";
        Map<String, Object> fileMap = (Map<String, Object>) ContextUtils.assertVariableNotNullAndGet(context, "agtFileMap", ErrorCodes.EUPS_FIELD_EMPTY,"agtFileMap");
		EupsThdFtpConfig config = get(EupsThdFtpConfigRepository.class).findOne(ParamKeys.FTPID_BATCH_PAY_FILE_TO_ACP);
		Assert.isFalse(null==config, ErrorCodes.EUPS_FTP_INFO_NOTEXIST);
		//设置文件名 文件路径
		config.setFtpDir("0");
		config.setLocFleNme(fleNme);
		config.setLocDir(dir);
		config.setRmtFleNme(fleNme);
		config.setRmtWay(dir);
		logger.info("===============生成代收付文件");
		/** 产生代收付格式文件 */
		if(context.getData(ParamKeys.EUPS_BUSS_TYPE).equals("ELEC00") || context.getData(ParamKeys.EUPS_BUSS_TYPE).equals("GZAG00") ){
			((OperateFileAction)get("opeFile")).createCheckFile(config, "agtFileBatchFmt", fleNme, fileMap);
		}else{
			((OperateFileAction)get("opeFile")).createCheckFile(config, "BatchFmt", fleNme, fileMap);
		}
		operateFTPAction.putCheckFile(config);
		//为得到代收付文件 更改ftpDir
		config.setFtpDir("1");
		config.setRmtWay("/home/bbipadm/data/mftp/BBIP/");
		get(EupsThdFtpConfigRepository.class).update(config);
		logger.info("==============End sendBatchFileToACP and putCheckFile");
	}
	/**
	 * 代收付返回文件 部分处理
	 */
	public void afterBatchProcess(Context context)throws CoreException{
		final String thdbatNo=ContextUtils.assertDataNotEmptyAndGet(context, ParamKeys.THD_BAT_NO,  ErrorCodes.EUPS_FIELD_EMPTY,"thdBatNo");
		GDEupsBatchConsoleInfo info = new GDEupsBatchConsoleInfo();
		info.setRsvFld9(thdbatNo);
		List<GDEupsBatchConsoleInfo> result =get(GDEupsBatchConsoleInfoRepository.class).find(info);
		Assert.isNotEmpty(result, "BBIP0004EU0706", "代收付返回的批次号不存在");
		//成功笔数金额  失败笔数金额
		GDEupsBatchConsoleInfo ret=result.get(0);
		Integer totCnt = ret.getTotCnt();
	    BigDecimal totAmt = ret.getTotAmt();
		Integer sucTotCnt = (Integer)context.getData("sucTotCnt");
	    BigDecimal sucTotAmt = (BigDecimal)context.getData("sucTotAmt");

	    Integer falTotCnt = Integer.valueOf(totCnt.intValue() - sucTotCnt.intValue());
	    double falTotAmtdouble = totAmt.doubleValue() - sucTotAmt.doubleValue();
	    BigDecimal falTotAmt = new BigDecimal(falTotAmtdouble);

	    ret.setSucTotCnt(sucTotCnt);
	    ret.setSucTotAmt(sucTotAmt);
	    ret.setFalTotCnt(falTotCnt);
	    ret.setFalTotAmt(falTotAmt);
	    ret.setExeDte(new Date());
		ret.setBatSts(GDConstants.BATCH_STATUS_COMPLETE);
		//更改批次表
		get(GDEupsBatchConsoleInfoRepository.class).updateConsoleInfo(ret);
		context.getDataMapDirectly().putAll(BeanUtils.toMap(ret));
	}
/**
 * EUPS_BATCH_CONSOLE_INFO和GDEUPS_BATCH_CONSOLE_INFO关联起来
 */
	public GDEupsBatchConsoleInfo  eupsBatchConSoleInfoAndgdEupsBatchConSoleInfo(Context context){
			String batNo=context.getData("batNo").toString();
			EupsBatchConsoleInfo eupsBatchConSoleInfo=get(EupsBatchConsoleInfoRepository.class).findOne(batNo);
			String fleNme=eupsBatchConSoleInfo.getFleNme();
			GDEupsBatchConsoleInfo gdEupsBatchConsoleInfos=new GDEupsBatchConsoleInfo();
			gdEupsBatchConsoleInfos.setRsvFld8(fleNme);
			GDEupsBatchConsoleInfo gdEupsBatchConSoleInfo=get(GDEupsBatchConsoleInfoRepository.class).find(gdEupsBatchConsoleInfos).get(0);
			return gdEupsBatchConSoleInfo;
	}
}
