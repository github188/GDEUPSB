package com.bocom.bbip.gdeupsb.action.elec02;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.action.common.OperateFileAction;
import com.bocom.bbip.eups.adaptor.ThirdPartyAdaptor;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.entity.EupsBatchConsoleInfo;
import com.bocom.bbip.eups.entity.EupsBatchInfoDetail;
import com.bocom.bbip.eups.entity.EupsThdFtpConfig;
import com.bocom.bbip.eups.repository.EupsBatchConsoleInfoRepository;
import com.bocom.bbip.eups.repository.EupsBatchInfoDetailRepository;
import com.bocom.bbip.eups.repository.EupsThdFtpConfigRepository;
import com.bocom.bbip.eups.spi.service.batch.AfterBatchAcpService;
import com.bocom.bbip.eups.spi.vo.AfterBatchAcpDomain;
import com.bocom.bbip.gdeupsb.action.common.BatchFileCommon;
import com.bocom.bbip.gdeupsb.action.common.FileFtpUtils;
import com.bocom.bbip.gdeupsb.action.common.OperateFTPActionExt;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.entity.GDEupsBatchConsoleInfo;
import com.bocom.bbip.gdeupsb.entity.GDEupsbElecstBatchTmp;
import com.bocom.bbip.gdeupsb.repository.GDEupsBatchConsoleInfoRepository;
import com.bocom.bbip.gdeupsb.repository.GDEupsbElecstBatchTmpRepository;
import com.bocom.bbip.utils.Assert;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

/**
 * @author wuyh
 * @date 2015-3-07 上午09:06:35
 * @Company TD
 */
public class AfterBatchAcpServiceImplELEC02 extends BaseAction implements
		AfterBatchAcpService {
	private static final Log logger = LogFactory
			.getLog(AfterBatchAcpServiceImplELEC02.class);

	@Autowired
	BBIPPublicService bbipPublicService;
	@Autowired
	GDEupsbElecstBatchTmpRepository gdEupsbElecstBatchTmpRepository;
	@Autowired
	EupsBatchConsoleInfoRepository eupsBatchConsoleInfoRepository;
	@Autowired
	GDEupsBatchConsoleInfoRepository gdeupsBatchConsoleInfoRepository;
	@Autowired
	ThirdPartyAdaptor callThdTradeManager;

	@Override
	public void afterBatchDeal(AfterBatchAcpDomain arg0, Context context)
			throws CoreException {
		logger.info("电力返盘文件处理开始 with context : " + context);

		String batNo = context.getData("batNo").toString();
		((BatchFileCommon) get(GDConstants.BATCH_FILE_COMMON_UTILS))
				.Lock(batNo);
		EupsBatchConsoleInfo eupsBatchConSoleInfo = get(
				EupsBatchConsoleInfoRepository.class).findOne(batNo);
		String rsvFld9 = eupsBatchConSoleInfo.getRsvFld1();// 本地batNo
		GDEupsBatchConsoleInfo gdEupsBatchConsoleInfos = new GDEupsBatchConsoleInfo();
		gdEupsBatchConsoleInfos.setRsvFld9(rsvFld9);

		GDEupsBatchConsoleInfo gdEupsBatchConSoleInfo = get(
				GDEupsBatchConsoleInfoRepository.class).find(
				gdEupsBatchConsoleInfos).get(0);
//		gdEupsBatchConSoleInfo.setTotAmt(eupsBatchConSoleInfo.getTotAmt());
//		gdEupsBatchConSoleInfo.setTotCnt(eupsBatchConSoleInfo.getTotCnt());
		
		// 由于本地协议原因，代收付文件中的总笔数不一定为真正笔数，回盘处理中，总笔数=RsvFld3， 总金额=RsvFld2，失败笔数=RsvFld3-代收付成功笔数，失败金额=RsvFld2-代收付成功金额
		int sucCntAfterAcp = eupsBatchConSoleInfo.getSucTotCnt();
		int failCntAfterAcp = Integer.parseInt(gdEupsBatchConSoleInfo.getRsvFld3().toString().trim()) - sucCntAfterAcp;
		BigDecimal sucAmtAfterAcp = eupsBatchConSoleInfo.getSucTotAmt();
		BigDecimal failAmtAfterAcp = new BigDecimal(gdEupsBatchConSoleInfo.getRsvFld2().toString().trim()).subtract(sucAmtAfterAcp);
		
		gdEupsBatchConSoleInfo.setSucTotAmt(sucAmtAfterAcp);
		gdEupsBatchConSoleInfo.setSucTotCnt(sucCntAfterAcp);
		
		gdEupsBatchConSoleInfo.setFalTotAmt(failAmtAfterAcp);
		gdEupsBatchConSoleInfo.setFalTotCnt(failCntAfterAcp);
		
		gdEupsBatchConSoleInfo.setExeDte((Date) eupsBatchConSoleInfo.getExeDte());
		gdEupsBatchConSoleInfo.setBatSts("S");
		get(GDEupsBatchConsoleInfoRepository.class).updateConsoleInfo(
				gdEupsBatchConSoleInfo);
		((BatchFileCommon) get(GDConstants.BATCH_FILE_COMMON_UTILS))
				.unLock(batNo);

		String br = gdEupsBatchConSoleInfo.getTxnOrgCde();
		String tlr = gdEupsBatchConSoleInfo.getTxnTlr();

		Map<String, Object> ret = new HashMap<String, Object>();
		EupsBatchInfoDetail eupsBatchInfoDetail = new EupsBatchInfoDetail();
		eupsBatchInfoDetail.setBatNo(batNo);
		List<EupsBatchInfoDetail> result = get(
				EupsBatchInfoDetailRepository.class).find(eupsBatchInfoDetail);
		Assert.isNotEmpty(result, ErrorCodes.EUPS_QUERY_NO_DATA);

		logger.info("=====================got result!!");

		String tmpSqn = null;
		String sts = null;
		String errCde = null;
		log.info("得到批扣result，更新进临时表");
		for (EupsBatchInfoDetail dtl : result) {
			tmpSqn = dtl.getRmk1();
			GDEupsbElecstBatchTmp elec02batchTmp = new GDEupsbElecstBatchTmp();
			elec02batchTmp.setSqn(tmpSqn);
			List<GDEupsbElecstBatchTmp> findOneInfo = gdEupsbElecstBatchTmpRepository
					.findBySqn(elec02batchTmp);
			logger.info(".....................after findBySqn!!!");
			elec02batchTmp = findOneInfo.get(0);
			sts = dtl.getSts();

			elec02batchTmp.setRsvFld17(DateUtils.format(new Date(),
					"yyyyMMddHHmmss"));

			/**
			 * TODO if("S".equals(sts)){ elec02batchTmp.setRsvFld15("1"); }else{
			 * errMsg = dtl.getErrMsg(); elec02batchTmp.setRsvFld16(errMsg);
			 * //TODO 根据errMsg确定rsvFld15,回盘用！ // 1-已扣 // 2-余额不足 // 3-帐号不符 //
			 * 4-帐号已销 // 5-坏帐号及其他, 除“已扣”，其他扣不到款 // 8-直接借记支付中的金额超过事先规定限额 //
			 * 9-直接借记无授权记录 }
			 */
			if ("S".equals(sts)) {
				elec02batchTmp.setRsvFld15("1");
			}
			if ("F".equals(sts)) {
				errCde = dtl.getErrMsg().toString().substring(0, 6);
				if ("TPM050".equals(errCde) || "TP0017".equals(errCde)) {
					elec02batchTmp.setRsvFld15("2");
				} else if ("CB1004".equals(errCde)) {
					elec02batchTmp.setRsvFld15("4");
				}
				// if ("PDM252".equals(errCde)) {
				// elec02batchTmp.setRsvFld15("4");
				// }
				else if ("SDM015".equals(errCde)) {// SDM015记账账号状态字为全部法律冻结、全部内部止付、冻结暂封、存款证明止付、质押或者保全冻结
					elec02batchTmp.setRsvFld15("5");
				} else if ("CDSS04".equals(errCde)) {// CDSS04核心错误码，请联系核心
					elec02batchTmp.setRsvFld15("5");
				} else {
					elec02batchTmp.setRsvFld15("5");
				}
				elec02batchTmp.setRsvFld16(dtl.getErrMsg());
			}

			gdEupsbElecstBatchTmpRepository.update(elec02batchTmp);
		}

		// 更新临时表后取数据组成回盘文件

		List<GDEupsbElecstBatchTmp> tempList = gdEupsbElecstBatchTmpRepository
				.findByBatNo(rsvFld9);
		Assert.isNotEmpty(tempList, ErrorCodes.EUPS_QUERY_NO_DATA);

		GDEupsBatchConsoleInfo batchConsoleInfo = get(
				GDEupsBatchConsoleInfoRepository.class).findOne(rsvFld9);
//		由于本地协议原因，代收付文件中的总笔数不一定为真正笔数，回盘处理中，总笔数=RsvFld3， 总金额=RsvFld2，失败笔数=总笔数-代收付成功笔数，失败金额=总金额-代收付成功金额
//		已更新为上代收付后的成功笔数,失败笔数
		int totCnt = Integer.parseInt(batchConsoleInfo.getRsvFld3().toString().trim());
		int sucCnt = batchConsoleInfo.getSucTotCnt();
		int failCnt = batchConsoleInfo.getFalTotCnt();

		BigDecimal totAmt= new BigDecimal((String)batchConsoleInfo.getRsvFld2().trim()).multiply(new BigDecimal(100));
		BigDecimal sucAmt = batchConsoleInfo.getSucTotAmt().multiply(new BigDecimal(100));
		BigDecimal failAmt = batchConsoleInfo.getFalTotAmt().multiply(new BigDecimal(100));
		
		batchConsoleInfo.setTotCnt(totCnt);
		batchConsoleInfo.setSucTotCnt(sucCnt);
		batchConsoleInfo.setFalTotCnt(failCnt);
		
		batchConsoleInfo.setTotAmt(totAmt);
		batchConsoleInfo.setSucTotAmt(sucAmt);
		batchConsoleInfo.setFalTotAmt(failAmt);

		batchConsoleInfo.setRsvFld6("RMB");
		batchConsoleInfo.setRsvFld8("1");// 收付标志,固定为收1
		batchConsoleInfo.setRsvFld2("0000");// retCode
		batchConsoleInfo.setRsvFld3("交易成功");// retMsg

		ret.put("header", batchConsoleInfo);
		ret.put("detail", tempList);

		OperateFTPActionExt operateFTP = new OperateFTPActionExt();

		EupsThdFtpConfig config = get(EupsThdFtpConfigRepository.class)
				.findOne("elec02BatchThdFileTest");

		String backFlieName = batchConsoleInfo.getFleNme();
		backFlieName = backFlieName.replace('s', 'h');
		config.setLocFleNme(backFlieName);
		config.setRmtFleNme(backFlieName);

		log.info("上送给第三方的文件名为:" + config.getRmtFleNme());

		Assert.isFalse(null == config, ErrorCodes.EUPS_THD_FTP_CONFIG_NOTEXIST);

		((OperateFileAction) get("opeFile")).createCheckFile(config,
				"ELEC02BatchBack", config.getLocFleNme(), ret);
		logger.info("=========== createCheckFile successfully ======== ");

		config.setFtpDir("0");// 0-外发
		logger.info("====== send file to ELEC02 =======");
		operateFTP.putCheckFile(config);

		/** 通知第三方 */
		logger.info("=== send msg to ELEC02 ===");
		context.setData("AppTradeCode", "23");
		context.setData("StartAddr", "301");
		context.setData("DestAddr", "0500");

		// String br = context.getData(ParamKeys.BR);
		String sqnTmp = bbipPublicService.getBBIPSequence();
		sqnTmp = sqnTmp.substring(12);
		String msgId = br + " " + sqnTmp;

		context.setData("MesgID", msgId);
		context.setData("WorkDate", DateUtils.format(new Date(), "yyyyMMdd"));
		context.setData("SendTime",
				DateUtils.format(new Date(), "yyyyMMddHHmmss"));

		context.setData("mesgPRI", "9");
		context.setData("recordNum", totCnt);
		context.setData("FileName", config.getRmtFleNme());
		context.setData("zipFlag", "0");

		context.setData("TransCode", "23");
		context.setData("WD0",
				DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMdd));
		String logNo = gdEupsBatchConSoleInfo.getRsvFld7();
		context.setData("LogNo", StringUtils.substring(logNo, 4));
		context.setData("TMN", br);// 经办网点

		// tlr截取后5位
		context.setData("STO", tlr.substring(2));
		context.setData("HAN", sucCnt);
		context.setData("HAM", sucAmt);
		context.setData("LSN", failCnt);
		context.setData("LSM", failAmt);
		// 执行到此，表示批扣返盘完成，返回第三方00表示23报文成功
		context.setData("responseCodeTHD", "00");

		Map<String, Object> thdResult = callThdTradeManager.trade(context);
		if (BPState.isBPStateOvertime(context)) {
			throw new CoreException(ErrorCodes.TRANSACTION_ERROR_TIMEOUT);
		}
		context.setDataMap(thdResult);
//----------------------------------------------------------------------
		// 回盘文件同时上传汕头分行FTP
		logger.info("elec02批扣返盘文件上传到汕头指定FTP");
//		String filPath = config.getLocDir();
//		EupsThdFtpConfig sendFileToElec02 = get(
//				EupsThdFtpConfigRepository.class).findOne("sendFileToElec02");
//		sendFileToElec02.setLocDir(filPath);
//		sendFileToElec02.setLocFleNme(backFlieName);
//		sendFileToElec02.setRmtFleNme(backFlieName);
//		logger.info("====== send file to ShanTou FTP =======");
//		operateFTP.putCheckFile(sendFileToElec02);

		
//		-------------------------------------------------------------------
		//注释  at 2015-05-21 10:20
		//郑表示不再需要该文件上传至汕头FTP
//		String filPath = config.getLocDir();
//		EupsThdFtpConfig eupsThdFtpConfigA =get(
//				EupsThdFtpConfigRepository.class).findOne("sendFileToElec02");
//		String stwatIpA = eupsThdFtpConfigA.getThdIpAdr();
//		String userNameA = eupsThdFtpConfigA.getOppNme();
//		String passwordA = eupsThdFtpConfigA.getOppUsrPsw();
//		String rmtDirA = eupsThdFtpConfigA.getRmtWay();
//		String[] shellArgA = {"GDEUPSBFtpPutFile.sh",stwatIpA,userNameA,passwordA,rmtDirA,backFlieName,filPath,"bin",backFlieName}; 
//		logger.info("ftp args="+shellArgA.toString());
//		try{
//			int result1 = FileFtpUtils.systemAndWait(shellArgA,true);
//			if(result1==0){
//				logger.info("put remote file success......");
//			}else{
//				throw new CoreException(ErrorCodes.EUPS_FAIL);
//			}
//		} catch (Exception e){
//			throw new CoreException(ErrorCodes.EUPS_FAIL);
//		}

		
//		-----------------------------------------------------------------
		logger.info("======= context after put file to thd ftp:" + context);

		logger.info("电力返盘文件处理结束 with conetxt : " + context);
	}
}
