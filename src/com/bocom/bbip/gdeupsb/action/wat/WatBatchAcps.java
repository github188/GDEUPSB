package com.bocom.bbip.gdeupsb.action.wat;

import java.io.File;
import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.comp.account.AccountService;
import com.bocom.bbip.comp.btp.BTPService;
import com.bocom.bbip.eups.action.BaseAction;
//import com.bocom.bbip.eups.action.common.OperateFTPAction;
import com.bocom.bbip.eups.action.common.OperateFileAction;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsThdFtpConfig;
import com.bocom.bbip.eups.repository.EupsActSysParaRepository;
import com.bocom.bbip.eups.repository.EupsThdFtpConfigRepository;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.service.impl.watr00.BatchAcpServiceImplWATR00;
import com.bocom.bbip.utils.Assert;
import com.bocom.bbip.utils.ContextUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class WatBatchAcps extends BaseAction {
	
	private static Logger logger = LoggerFactory.getLogger(BatchAcpServiceImplWATR00.class);
	/**代扣文件指定存放路径前缀*/
	private static final String FILE_DIR_PREFIX = "/home/bbipadm/data/mftp/BBIP/GDEUPSB/";
	/**批量加锁KEY*/
	private static final String lockKey = "BatchAcpServiceImplWATR00";
	@Autowired
	EupsThdFtpConfigRepository eupsThdFtpConfigRepository;
	
	@Autowired
	BBIPPublicService service;
	@Autowired
	AccountService accountService;
	@Autowired
	BTPService BTPservice;
	
	@Autowired
	EupsActSysParaRepository eupsActSysParaRepository;
	
	@SuppressWarnings("unchecked")
	@Override

	public void execute(Context context) throws CoreException,
			CoreRuntimeException {
		logger.info("WatBatchAcps prepareBatchDeal start ... ..."
				+ context);
		String fileName = ContextUtils.assertVariableNotEmptyAndGet(context,
				ParamKeys.FLE_NME, ErrorCodes.EUPS_FILE_CREATE_NAME_ISEMPTY);

		String br = context.getData("br");
		String tlr = context.getData("tlr");
		String comNo = ContextUtils.assertDataHasLengthAndGetNNR(context, ParamKeys.COMPANY_NO, ErrorCodes.EUPS_FIELD_EMPTY);//代理单位号
		Date date=get(BBIPPublicService.class).getAcDate();
		
		
		String acDate = DateUtils.format(date, DateUtils.STYLE_yyyyMMdd);//会计日期
//		EupsThdFtpConfig configB = get(EupsThdFtpConfigRepository.class)
//				.findOne("BatchFileFtpNo");
//		
//		configB.setLocFleNme(fileName);
//		configB.setRmtFleNme(fileName);
//		String dir1 = configB.getLocDir().toString();
//		dir1 = dir1 + "GDEUPSB/" + "wat/";

//		configB.setLocDir(dir1);

		Map<String, Object> fileMap = (Map<String, Object>) ContextUtils
				.assertVariableNotNullAndGet(context, "agtFileMap",
						"agtFileMap不能为空");
		EupsThdFtpConfig config = get(EupsThdFtpConfigRepository.class)
				.findOne("BatchFileFtpNo");
		Assert.isNotNull(config, ErrorCodes.EUPS_FTP_INFO_NOTEXIST,
				"第三方配置信息不存在");
		config.setLocFleNme(fileName);
		config.setRmtFleNme(fileName);
		String dir = config.getLocDir().toString();
		dir = dir + "GDEUPSB/" + br + "/" + tlr + "/" + acDate + "/";
//		config.setRmtWay(dir);
		config.setLocDir(dir);
		File file = new File(dir);
		if (!file.exists()) {
			file.mkdirs();
		}
		logger.info("dir_filNam[" + dir + context.getVariable(ParamKeys.FLE_NME) + "]");
		/** 产生代收付格式文件 */
//		((OperateFileAction) get("opeFile")).createCheckFile(configB,
//				GDConstants.BATCH_FILE_FORMAT, fileName, fileMap);
		//TODO:待测试！。。
		((OperateFileAction) get("opeFile")).createCheckFile(config,	GDConstants.BATCH_FILE_FORMAT, fileName, fileMap);
		/** 发送到指定路径 */
//		((OperateFTPAction) get("opeFTP")).putCheckFile(config);

		context.setData(ParamKeys.TXN_MDE, "0");
		context.setData(ParamKeys.TXN_CHL, "1");
		// context.setData(ParamKeys.THD_BAT_NO, context.getData("sqn"));
		context.setData(ParamKeys.BUS_TYP, "0");
		context.setData("reqTyp", "");
		context.setData("sup1Id", "");
		context.setData("sup2Id", "");
		context.setData("authResnTbl", "");
		context.setData("reqTme", new Date());

		get(BBIPPublicService.class).synExecute(
				"eups.batchPaySubmitDataProcess", context);
		// Result result =
		// get(BGSPServiceAccessObject.class).callServiceFlatting("",
		// context.getDataMap());
		// get(BatchFileCommon.class).sendBatchFileToACP(context);
		// BatchFileCommon com = get("eups.batchFileCommon");
		// com.sendBatchFileToACP(context);
		// //设置接下来context中必须的值
		// context.setData(ParamKeys.TXN_MDE, Constants.TXN_MDE_FILE);
		// context.setData(ParamKeys.FLE_NME, filNam);
		// context.setData("thdBatNo",
		// BTPservice.applyBatchNo(ParamKeys.BUSINESS_CODE_COLLECTION)+"_"+filNam);
		// //自己的eupsBusTyp
		// context.setData(ParamKeys.EUPS_BUSS_TYPE, "WATR00");
		// context.setData(ParamKeys.TOT_CNT, map.get(ParamKeys.TOT_CNT));
		// context.setData(ParamKeys.TOT_AMT, map.get(ParamKeys.TOT_AMT));
		service.unlock(lockKey);
		logger.info("WatBatchAcps prepareBatchDeal end ... ..."
				+ context);
	}
}
