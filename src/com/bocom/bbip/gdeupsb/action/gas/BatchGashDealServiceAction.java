package com.bocom.bbip.gdeupsb.action.gas;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.comp.CommonRequest;
import com.bocom.bbip.comp.account.AccountService;
import com.bocom.bbip.comp.account.support.CusActInfResult;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.action.common.OperateFTPAction;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsBatchConsoleInfo;
import com.bocom.bbip.eups.entity.EupsThdFtpConfig;
import com.bocom.bbip.eups.repository.EupsBatchConsoleInfoRepository;
import com.bocom.bbip.eups.repository.EupsThdFtpConfigRepository;
import com.bocom.bbip.eups.spi.service.batch.BatchAcpService;
import com.bocom.bbip.eups.spi.vo.PrepareBatchAcpDomain;
import com.bocom.bbip.file.Marshaller;
import com.bocom.bbip.file.transfer.TransferUtils;
import com.bocom.bbip.gdeupsb.action.common.BatchFileCommon;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GDEupsBatchConsoleInfo;
import com.bocom.bbip.gdeupsb.entity.GdGasCusAll;
import com.bocom.bbip.gdeupsb.entity.GdGashBatchTmp;
import com.bocom.bbip.gdeupsb.repository.GDEupsBatchConsoleInfoRepository;
import com.bocom.bbip.gdeupsb.repository.GdGasCusAllRepository;
import com.bocom.bbip.gdeupsb.repository.GdGashBatchTmpRepository;
import com.bocom.bbip.utils.Assert;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.ContextUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.JumpException;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

/**
 * 惠州燃气文件批扣数据准备 燃气发起
 * 
 * @author WangMQ
 * 
 */
public class BatchGashDealServiceAction extends BaseAction implements
		BatchAcpService {

	private final static Logger logger = LoggerFactory
			.getLogger(BatchGashDealServiceAction.class);

	@Autowired
	BBIPPublicService bbipPublicService;

	@Autowired
	GdGasCusAllRepository gdGasCusAllRepository;

	@Autowired
	AccountService accountService;

	@SuppressWarnings("unchecked")
	@Override
	public void prepareBatchDeal(PrepareBatchAcpDomain arg0, Context context)
			throws CoreException {
		logger.info("批次前准备数据");

		// TODO: for test, 此处为了测试写死，后续需要修改
		// context.setData(ParamKeys.TELLER, "ABIR148");
		// context.setData(ParamKeys.BR, "01441131999");
		// context.setData(ParamKeys.BK, "01441999999");

		context.setData("extFields", "01491800999");
		
		String bk = "01491999999";//分行号01441999999 ?
		String br = "01491800999";//网点号、机构号01491800999?
		String tlr = null;//"4910153"
		// TODO get tlr
		context.setData(ParamKeys.BK, bk);// 分行号01491999999
		context.setData(ParamKeys.BR, br);// 机构号 "01441131999"
		tlr = bbipPublicService.getETeller(bk);
		context.setData(ParamKeys.TELLER, tlr);

		context.setData(ParamKeys.FTP_ID, "PGAS00Bat"); 

		logger.info("============context:" + context);

		String fleNme = (String) context.getData(ParamKeys.FLE_NME);
		logger.info("=================fleNme:" + fleNme);

		/** 加锁 */
		String comNo = (String) context.getData(ParamKeys.COMPANY_NO);
		logger.info("==============comNo : " + comNo);
		// ((BatchFileCommon)get(GDConstants.BATCH_FILE_COMMON_UTILS)).Lock(comNo);

		// 检查批次是否重复录入，不是重复录入则插入一条数据进批次控制表
		((BatchFileCommon) get(GDConstants.BATCH_FILE_COMMON_UTILS))
				.BeforeBatchProcess(context);

		String batNo1 = context.getData(ParamKeys.BAT_NO);
		context.setData("batNo1", batNo1);
		EupsThdFtpConfig config = get(EupsThdFtpConfigRepository.class)
				.findOne((String) context.getData(ParamKeys.FTP_ID));
		Assert.isFalse(null == config, ErrorCodes.EUPS_THD_FTP_CONFIG_NOTEXIST);

		config.setRmtFleNme(fleNme);
		config.setLocFleNme(fleNme);
		get(OperateFTPAction.class).getFileFromFtp(config);

		Map<String, Map<String, Object>> result = pareseFile(config,
				"PGAS00BatFmt");
		Assert.isFalse(null == result || 0 == result.size(),
				ErrorCodes.EUPS_FILE_PARESE_FAIL);
		Map<String, Object> header = new HashMap<String, Object>();

		List<Map<String, Object>> detail = (List<Map<String, Object>>) result
				.get("detail");
		GDEupsBatchConsoleInfo batInfo = ContextUtils.getDataAsObject(context,
				GDEupsBatchConsoleInfo.class);
		get(GDEupsBatchConsoleInfoRepository.class).updateConsoleInfo(batInfo);

		Date txnDate = new Date();
		// 将解析文件得到的数据放入临时表
		String cusNo = null;
		GdGasCusAll gdGasCusAll = new GdGasCusAll();
		List<GdGasCusAll> infoList = new ArrayList<GdGasCusAll>();
		List<GdGashBatchTmp> list = (List<GdGashBatchTmp>) BeanUtils.toObjects(
				detail, GdGashBatchTmp.class);
		for (GdGashBatchTmp tmp : list) {
			tmp.setSqn(get(BBIPPublicService.class).getBBIPSequence());
			tmp.setBatNo(batNo1);
			cusNo = tmp.getCusNo();
			logger.info("==============cusNo:" + cusNo);
			gdGasCusAll.setCusNo(cusNo);
			infoList = (List<GdGasCusAll>) gdGasCusAllRepository
					.find(gdGasCusAll);
			// 从协议表中取
			tmp.setTmpFld4(infoList.get(0).getCusNme());
			tmp.setTmpFld3(infoList.get(0).getThdCusNme());// thdCusNme
			tmp.setBk(bk);
			tmp.setTmpFld2((String) detail.get(0).get("gasBk"));
			tmp.setTxnDte(txnDate);
			logger.info("============cusNme:" + tmp.getCusNme()
					+ "===============thdCusNme:" + tmp.getTmpFld3());

			get(GdGashBatchTmpRepository.class).insert(tmp);
		}

		List<GdGashBatchTmp> lt = get(GdGashBatchTmpRepository.class)
				.findByBatNo((String) context.getData(ParamKeys.BAT_NO));

		List<Map<String, Object>> gasBatDetail = new ArrayList<Map<String, Object>>();

		String isOurBnk = new String(); // 初始化

		// to sum totAmt
		BigDecimal totAmt = new BigDecimal(0.0);
		String cusAc = null;
		String OBKBK = null;
		for (GdGashBatchTmp tmp : lt) {
			Map<String, Object> tmpMap = new HashMap<String, Object>();
			// TODO：我行卡查询，开户行查询
			cusAc = tmp.getCusAc();
			//TODO
//			if (accountService.isOurBankCard(cusAc)) {
//				isOurBnk = "0"; // 我行卡
//				CusActInfResult actInf = accountService.getAcInf(
//						CommonRequest.build(context), cusAc);
//				OBKBK = actInf.getOpnBk();
//				tmpMap.put("OBKBK", OBKBK);
//			} else {
//				isOurBnk = "1"; // 他行卡
//				CusActInfResult actInf = accountService.getAcInf(
//						CommonRequest.build(context), cusAc);
//				OBKBK = actInf.getOpnBk();
//				tmpMap.put("OBKBK", OBKBK);
//			}

			tmpMap.put("cusAc", tmp.getCusAc());
			tmpMap.put("cusNme", tmp.getTmpFld4());
			tmpMap.put("txnAmt", tmp.getTxnAmt());
			tmpMap.put("thdCusNo", tmp.getCusNo());
//TODO			tmpMap.put("OUROTHFLG", isOurBnk);
			tmpMap.put("OUROTHFLG", "0");
			tmpMap.put("thdCusNme", tmp.getTmpFld3());
			// tmpMap.put("OBKBK", value);
			tmpMap.put("RMK1", tmp.getThdSqn());
			tmpMap.put("RMK2", comNo);
			totAmt = totAmt.add(new BigDecimal(tmp.getTxnAmt()));
			gasBatDetail.add(tmpMap);
		}

		Map<String, Object> temp = new HashMap<String, Object>();

		header.put("comNo", comNo);
		header.put("totCnt", lt.size());
		header.put("totAmt", totAmt);

		context.setData(ParamKeys.TOT_CNT, lt.size());
		context.setData(ParamKeys.TOT_AMT, totAmt);

		temp.put(ParamKeys.EUPS_FILE_HEADER, header);
		temp.put(ParamKeys.EUPS_FILE_DETAIL, gasBatDetail);

		context.setVariable(GDParamKeys.COM_BATCH_AGT_FILE_NAME, fleNme);// 原始文件名
		context.setVariable(GDParamKeys.COM_BATCH_AGT_FILE_MAP, temp);
		GDEupsBatchConsoleInfo console = new GDEupsBatchConsoleInfo();
		console.setBatNo((String) context.getData(ParamKeys.BAT_NO));
		/** 更新批次状态为待提交 */
		console.setBatSts(GDConstants.BATCH_STATUS_WAIT);
		get(GDEupsBatchConsoleInfoRepository.class).updateConsoleInfo(console);
		((BatchFileCommon) get(GDConstants.BATCH_FILE_COMMON_UTILS))
				.sendBatchFileToACP(context);

		// ((BatchFileCommon)get(GDConstants.BATCH_FILE_COMMON_UTILS)).unLock(comNo);
		logger.info("批量文件数据准备结束-------------");

		logger.info("==============context:" + context);

		context.setData("fleNmeBak", fleNme);
		// 提交代收付
		userProcessToSubmit(context);
//		logger.info("===================开始休眠2min===================");
//		try {
//			Thread.sleep(120000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
		// 得到反盘文件
//		 userProcessToGet(context);
		// 处理成第三方格式返回

		logger.info("==========End  BatchDataFileAction  prepareBatchDeal");

	}

	private Map<String, Map<String, Object>> pareseFile(
			EupsThdFtpConfig eupsThdFtpConfig, String fileId)
			throws CoreException, CoreRuntimeException {
		Map map = null;
		logger.info("this is path:"
				+ TransferUtils.resolveFilePath(eupsThdFtpConfig.getLocDir()
						.trim(), eupsThdFtpConfig.getLocFleNme().trim()));
		try {
			Resource resource = new FileSystemResource(
					TransferUtils.resolveFilePath(eupsThdFtpConfig.getLocDir()
							.trim(), eupsThdFtpConfig.getLocFleNme().trim()));
			map = (Map) ((Marshaller) get(Marshaller.class)).unmarshal(fileId,
					resource, Map.class);
		} catch (JumpException e) {
			logger.error("文件解析错误");
			throw new CoreException("BBIP0004EU0015");
		}
		return map;
	}

	/**
	 * 异步调用process 批量代扣数据提交
	 */
	public void userProcessToSubmit(Context context) throws CoreException {
		
		logger.info("==========Start  eups.batchPaySubmitDataProcess  =============");
		//生成代收付文件
		get(BatchFileCommon.class).sendBatchFileToACP(context);
		Date date=new Date();
		context.setData("reqTme",DateUtils.formatAsSimpleDate(date)+"T"+DateUtils.format(date, "HH:mm:ss"));
		//提交
		String mothed="eups.batchPaySubmitDataProcess";
//		context.setData(ParamKeys.RSV_FLD3, context.getData(ParamKeys.THD_SQN));
		context.setData(ParamKeys.RSV_FLD2, context.getData("rsvFld7"));
		bbipPublicService.synExecute(mothed, context);
//		String	rsvFld3=context.getData(ParamKeys.THD_SQN).toString();
		EupsBatchConsoleInfo eupsBatchConsoleInfo =new EupsBatchConsoleInfo();
		eupsBatchConsoleInfo.setRsvFld2((String) context.getData("rsvFld7"));
//		eupsBatchConsoleInfo.setRsvFld3(rsvFld3);
		
		String batNo=get(EupsBatchConsoleInfoRepository.class).find(eupsBatchConsoleInfo).get(0).getBatNo();
		context.setData(ParamKeys.BAT_NO, batNo);
		
//		String mothed = "eups.batchPaySubmitDataProcess";
//
//		Date date = new Date();
//		context.setData("reqTme", DateUtils.formatAsSimpleDate(date) + "T"
//				+ DateUtils.format(date, "HH:mm:ss"));
//
//		context.setData(ParamKeys.RSV_FLD2, context.getData("rsvFld7"));
//		bbipPublicService.synExecute(mothed, context);
//		String rsvFld2 = context.getData(ParamKeys.SEQUENCE).toString();
//		EupsBatchConsoleInfo eupsBatchConsoleInfo = new EupsBatchConsoleInfo();
//		eupsBatchConsoleInfo.setRsvFld2(rsvFld2);
//		String batNo = get(EupsBatchConsoleInfoRepository.class)
//				.find(eupsBatchConsoleInfo).get(0).getBatNo();
//		context.setData(ParamKeys.BAT_NO, batNo);

		logger.info("==========End   eups.batchPaySubmitDataProcess with context: "
				+ context);
	}

	/**
	 * 异步调用process 代收付回调函数：解析回盘文件并入库
	 */
	public void userProcessToGet(Context context) throws CoreException {
		logger.info("==========Start eups.commNotifyBatchStatus=================");
		// String batNo1 = context.getData("batNo1");
		// context.setData(ParamKeys.BAT_NO, batNo1);
		// logger.info("==================batNo:【" +
		// context.getData(ParamKeys.BAT_NO) + "】");

		String mothed = "eups.commNotifyBatchStatus";
		bbipPublicService.synExecute(mothed, context);
		logger.info("==========End  eups.commNotifyBatchStatus=================");
	}

}
