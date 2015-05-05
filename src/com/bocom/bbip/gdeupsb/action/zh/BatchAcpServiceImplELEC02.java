package com.bocom.bbip.gdeupsb.action.zh;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.action.common.OperateFTPAction;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsThdFtpConfig;
import com.bocom.bbip.eups.repository.EupsThdFtpConfigRepository;
import com.bocom.bbip.eups.spi.service.batch.BatchAcpService;
import com.bocom.bbip.eups.spi.vo.PrepareBatchAcpDomain;
import com.bocom.bbip.file.Marshaller;
import com.bocom.bbip.file.transfer.TransferUtils;
import com.bocom.bbip.gdeupsb.action.common.BatchFileCommon;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.entity.GDEupsBatchConsoleInfo;
import com.bocom.bbip.gdeupsb.entity.GDEupsbElecstBatchTmp;
import com.bocom.bbip.gdeupsb.entity.GdeupsAgtElecTmp;
import com.bocom.bbip.gdeupsb.repository.GDEupsBatchConsoleInfoRepository;
import com.bocom.bbip.gdeupsb.repository.GDEupsbElecstBatchTmpRepository;
import com.bocom.bbip.gdeupsb.repository.GdBatchConsoleInfoRepository;
import com.bocom.bbip.gdeupsb.repository.GdeupsAgtElecTmpRepository;
import com.bocom.bbip.utils.Assert;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.CollectionUtils;
import com.bocom.bbip.utils.ContextUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.JumpException;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class BatchAcpServiceImplELEC02 extends BaseAction implements BatchAcpService {
	@Autowired
	BBIPPublicService bbipPublicService;
	@Autowired
	GDEupsbElecstBatchTmpRepository gdEupsbElecstBatchTmpRepository;
	@Autowired
	GdBatchConsoleInfoRepository gdBatchConsoleInfoRepository;

	private static final Log logger = LogFactory.getLog(BatchAcpServiceImplELEC02.class);

	@SuppressWarnings("unchecked")
	@Override
	public void prepareBatchDeal(PrepareBatchAcpDomain domain, Context context) throws CoreException {
	logger.info("start BatchAcpServiceImplELEC02 @ prepareBatchDeal with context " + context);
		String fileNme = (String)context.getData("FilNam");
		context.setData(ParamKeys.FLE_NME, fileNme);
		final String comNo = ContextUtils.assertDataHasLengthAndGetNNR(context, ParamKeys.COMPANY_NO, ErrorCodes.EUPS_FIELD_EMPTY);
		Date tmpDate = new Date();
		String eleTmpSqn = "301" + DateUtils.format(tmpDate, "yyMMdd");
		String tmpSqn = null;

		String bk = "01445999999";
		String br = "01445012999";
		context.setData("extFields", br);
		context.setData(ParamKeys.BK, bk);
		context.setData("br", br);
		String tlr = bbipPublicService.getETeller(bk);
		context.setData(ParamKeys.TELLER, tlr);
		logger.info("=========>>>>>>>>>>context after get tlr: " + context);
		
		
		/** 批量前检查和初始化批量控制表 生成批次号batNo */
		((BatchFileCommon) get(GDConstants.BATCH_FILE_COMMON_UTILS)).BeforeBatchProcess(context);
		logger.info("开始解析批量文件-------------with conetxt: " + context);

		
//		EupsThdFtpConfig config = get(EupsThdFtpConfigRepository.class).findOne("elec02BatchThdFile");
		EupsThdFtpConfig config = get(EupsThdFtpConfigRepository.class).findOne("elec02BatchThdFileTest");
		Assert.isFalse(null == config, ErrorCodes.EUPS_FTP_INFO_NOTEXIST, "FTP配置不存在");
		
		//ftp到第三方服务器获取文件
		config.setLocFleNme(fileNme);
		config.setRmtFleNme(fileNme);
		get(EupsThdFtpConfigRepository.class).update(config);
		
		get(OperateFTPAction.class).getFileFromFtp(config);

		// 解析文件
		Map<String, Object> result = pareseFileByPath(config.getLocDir(), config.getLocFleNme(), "ELEC02Batch");

		Assert.isFalse(null == result || 0 == result.size(), ErrorCodes.EUPS_FILE_PARESE_FAIL);
		Map<String, Object> head = (Map<String, Object>) result.get("header");
		List<Map<String, Object>> batchDetailLst = (List<Map<String, Object>>) result.get("detail");
		context.setDataMap(head);
		// context.getDataMapDirectly().putAll(head);

		// 获取批次号
		String batchNo = context.getData(ParamKeys.THD_BAT_NO);
		// context.setData(ParamKeys.BAT_NO, batchNo);
		logger.info("=============>>>>>>>>>>>>>the batNo is : " + batchNo );


		/** 插入临时表中 */
		List<GDEupsbElecstBatchTmp> listToBatchTmp = (List<GDEupsbElecstBatchTmp>) BeanUtils.toObjects(batchDetailLst, GDEupsbElecstBatchTmp.class);
		int i = 0;
		BigDecimal amtTot = new BigDecimal("0.00");
		for (GDEupsbElecstBatchTmp tmp : listToBatchTmp) {
			// 判断在本地是否存在协议,若存在，则本地批次表更新为0，否则更新为1
			String cusAc = tmp.getCusAc();
			String feeNum = tmp.getThdCusNo();
			GdeupsAgtElecTmp agtElec = new GdeupsAgtElecTmp();
			agtElec.setActNo(cusAc);
			agtElec.setFeeNum(feeNum);
			List<GdeupsAgtElecTmp> checkR = get(GdeupsAgtElecTmpRepository.class).find(agtElec);
			if (CollectionUtils.isEmpty(checkR)) {
				tmp.setRsvFld12("1"); // 不存在本地协议信息
				//直接用某字段表示批扣状态，失败
				tmp.setRsvFld15("3");//
				tmp.setRsvFld16("不存在代扣协议");
				
			} else {
				tmp.setRsvFld12("0");
			}
			tmpSqn = eleTmpSqn + bbipPublicService.getBBIPSequence().toString().substring(13);
			tmp.setTxnTlr(tlr);
			tmp.setSqn(tmpSqn);
			tmp.setBatNo(batchNo);
			tmp.setRsvFld17(DateUtils.format(new Date(), "yyyyMMddHHmmss")); //实时
			gdEupsbElecstBatchTmpRepository.insert(tmp);
		}
		List<Map<String, Object>> agtFileDetail = new ArrayList<Map<String, Object>>(); // 代收付文件detail
		List<GDEupsbElecstBatchTmp> toAcpList = gdEupsbElecstBatchTmpRepository.findByBatNoAndSigned(batchNo);
		if (null == toAcpList || CollectionUtils.isEmpty(toAcpList)) {
			logger.info("There are no records for select check trans journal ");
			throw new CoreException(ErrorCodes.EUPS_QUERY_NO_DATA);
		}
		for(GDEupsbElecstBatchTmp batTmp : toAcpList ){
			Map<String, Object> detailMap = new HashMap<String, Object>();
			BigDecimal amtB = new BigDecimal(batTmp.getTxnAmt());
			amtB = amtB.divide(new BigDecimal("100"));
			amtTot = amtTot.add(amtB);
			amtTot = amtTot.setScale(2);
			i++;
		
			//金额转换
			batTmp.setTxnAmt(amtB.toString());
			detailMap = BeanUtils.toMap(batTmp);
			detailMap.put("OUROTHFLG", "0");
			// 将临时表中的sqn set进代收付文件中的RMK1 即可解决代收付明细表与临时表数据匹配问题
			detailMap.put("RMK1", batTmp.getSqn());
			agtFileDetail.add(detailMap);
		}
		
		Map<String, Object> headAgt = new HashMap<String, Object>();
		headAgt.put("totCnt", i);
		headAgt.put("totAmt", amtTot.toString());
		headAgt.put("comNo", context.getData("comNoAcps"));
		
		// 更新总笔数，总金额等信息,备用字段2，3分别表示其真正的笔数，金额
		GDEupsBatchConsoleInfo info = ContextUtils.getDataAsObject(context, GDEupsBatchConsoleInfo.class);
		info.setRsvFld2(info.getTotAmt().toString());
		info.setRsvFld3(info.getTotCnt().toString());
		info.setTotAmt(amtTot);
		info.setTotCnt(i);
		get(GDEupsBatchConsoleInfoRepository.class).updateConsoleInfo(info);

		// 生成代收付文件
		context.setData(ParamKeys.COMPANY_NO, (String) context.getData("comNoAcps"));
		Map<String, Object> temp = new HashMap<String, Object>();
		temp.put(ParamKeys.EUPS_FILE_HEADER, headAgt);
		// 查找本地存在协议的批量信息，拼凑成代收付文件detail
		temp.put(ParamKeys.EUPS_FILE_DETAIL, agtFileDetail);
		context.setVariable("agtFileMap", temp);
		
		((BatchFileCommon) get(GDConstants.BATCH_FILE_COMMON_UTILS)).sendBatchFileToACP(context);

//		((BatchFileCommon) get(GDConstants.BATCH_FILE_COMMON_UTILS)).unLock(comNo);

		logger.info("批量文件数据准备结束-------------");

		// 同步提交
		Date date = new Date();
		context.setData("reqTme", DateUtils.formatAsSimpleDate(date) + "T" + DateUtils.format(date, "HH:mm:ss"));

		// 判空todo
//		String totCnt = context.getData("totCnt").toString();
//		String totAmt = context.getData("totAmt").toString();
//		context.setData("totCnt", Integer.valueOf(totCnt));
//		context.setData("totAmt", new BigDecimal(totAmt));
		context.setData("totCnt", i);
		context.setData("totAmt", amtTot);
		
		// 提交
		bbipPublicService.synExecute("eups.batchPaySubmitDataProcess", context);
		
		String rspMsg=context.getData("rspMsg");
		if(StringUtils.isEmpty(rspMsg)){
			context.setData("rspMsg", "交易成功");
		}
		
		//执行到此，表示批扣准备完成，返回第三方00表示22报文成功
		context.setData("responseCodeTHD", "00");
		
		context.setData("thdRspCde", "00");
	}

	/**
	 * 解析文件并返回map
	 * 
	 * @param filePath
	 * @param fileName
	 * @param fileId
	 * @return
	 * @throws CoreException
	 * @throws CoreRuntimeException
	 */
	private Map<String, Object> pareseFileByPath(String filePath, String fileName, String fileId)
			throws CoreException, CoreRuntimeException
	{
		Resource resource;
		Map map = new HashMap();
		logger.info("this is path:" + TransferUtils.resolveFilePath(filePath, fileName));
		try {
			resource = new FileSystemResource(TransferUtils.resolveFilePath(filePath, fileName));
			map = (Map) ((Marshaller) get(Marshaller.class)).unmarshal(fileId, resource, Map.class);
		} catch (JumpException e) {
			logger.error("BBIP0004EU0015");
			throw new CoreException("BBIP0004EU0015");
		}
		return map;
	}

	/**
	 * 同步调用process 批量代扣数据提交
	 */
	// public void userProcessToSubmit(Context context) throws CoreException {
	// logger.info("==========Start  BatchDataFileAction  userProcessToSubmit");
	//
	// Date date = new Date();
	// context.setData("reqTme", DateUtils.formatAsSimpleDate(date) + "T" +
	// DateUtils.format(date, "HH:mm:ss"));
	// // 提交
	// String mothed = "eups.batchPaySubmitDataProcess";
	// // context.setData(ParamKeys.RSV_FLD3,
	// // context.getData(ParamKeys.THD_SQN));
	// // context.setData(ParamKeys.RSV_FLD2,
	// // context.getData(ParamKeys.SEQUENCE));
	// bbipPublicService.synExecute(mothed, context);
	// String rsvFld3 = context.getData(ParamKeys.THD_SQN).toString();
	// EupsBatchConsoleInfo eupsBatchConsoleInfo = new EupsBatchConsoleInfo();
	// eupsBatchConsoleInfo.setRsvFld3(rsvFld3);
	// String batNo =
	// get(EupsBatchConsoleInfoRepository.class).find(eupsBatchConsoleInfo).get(0).getBatNo();
	// context.setData(ParamKeys.BAT_NO, batNo);
	// context.setData("PKGCNT", "000000");
	// logger.info("==========End  BatchDataFileAction  userProcessToSubmit");
	//
	// }

	
}
