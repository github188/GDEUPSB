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
import com.bocom.bbip.comp.BBIPPublicServiceImpl;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.action.common.OperateFTPAction;
import com.bocom.bbip.eups.action.common.OperateFileAction;
import com.bocom.bbip.eups.adaptor.ThirdPartyAdaptor;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsBatchConsoleInfo;
import com.bocom.bbip.eups.entity.EupsBatchInfoDetail;
import com.bocom.bbip.eups.entity.EupsThdFtpConfig;
import com.bocom.bbip.eups.repository.EupsBatchConsoleInfoRepository;
import com.bocom.bbip.eups.repository.EupsBatchInfoDetailRepository;
import com.bocom.bbip.eups.repository.EupsThdFtpConfigRepository;
import com.bocom.bbip.eups.spi.service.batch.AfterBatchAcpService;
import com.bocom.bbip.eups.spi.vo.AfterBatchAcpDomain;
import com.bocom.bbip.gdeupsb.action.common.BatchFileCommon;
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
		logger.info("电力返盘文件处理开始");

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
		gdEupsBatchConSoleInfo.setTotAmt(eupsBatchConSoleInfo.getTotAmt());
		gdEupsBatchConSoleInfo.setTotCnt(eupsBatchConSoleInfo.getTotCnt());
		gdEupsBatchConSoleInfo
				.setSucTotAmt(eupsBatchConSoleInfo.getSucTotAmt());
		gdEupsBatchConSoleInfo
				.setSucTotCnt(eupsBatchConSoleInfo.getSucTotCnt());
		gdEupsBatchConSoleInfo
				.setFalTotAmt(eupsBatchConSoleInfo.getFalTotAmt());
		gdEupsBatchConSoleInfo
				.setFalTotCnt(eupsBatchConSoleInfo.getFalTotCnt());
		gdEupsBatchConSoleInfo.setExeDte((Date) eupsBatchConSoleInfo
				.getExeDte());
		gdEupsBatchConSoleInfo.setBatSts("S");
		get(GDEupsBatchConsoleInfoRepository.class).updateConsoleInfo(
				gdEupsBatchConSoleInfo);
		((BatchFileCommon) get(GDConstants.BATCH_FILE_COMMON_UTILS))
				.unLock(batNo);

		Map<String, Object> ret = new HashMap<String, Object>();
		// final List<EupsBatchInfoDetail>
		// result=(List<EupsBatchInfoDetail>)context.getVariable("detailList");
		EupsBatchInfoDetail eupsBatchInfoDetail = new EupsBatchInfoDetail();
		eupsBatchInfoDetail.setBatNo(batNo);
		final List<EupsBatchInfoDetail> result = get(
				EupsBatchInfoDetailRepository.class).find(eupsBatchInfoDetail);
		Assert.isNotEmpty(result, ErrorCodes.EUPS_QUERY_NO_DATA);

		logger.info("=====================got result!!");

		String tmpSqn = null;
		String sts = null;
		String errMsg = null;
		for (EupsBatchInfoDetail dtl : result) {
			tmpSqn = dtl.getRmk1();
			GDEupsbElecstBatchTmp elec02batchTmp = new GDEupsbElecstBatchTmp();
			elec02batchTmp.setSqn(tmpSqn);
			List<GDEupsbElecstBatchTmp> findOneInfo = gdEupsbElecstBatchTmpRepository
					.findBySqn(elec02batchTmp);
			logger.info(".....................after findBySqn!!!");
			elec02batchTmp = findOneInfo.get(0);
			sts = dtl.getSts();
			errMsg = dtl.getErrMsg();

			elec02batchTmp.setRsvFld17(DateUtils.format(new Date(),
					"yyyyMMddHHmmss"));
			elec02batchTmp.setRsvFld15(sts);// TODO
			elec02batchTmp.setRsvFld16(errMsg);

			/**
			 * TODO if("S".equals(sts)){ elec02batchTmp.setRsvFld15("1"); }else{
			 * errMsg = dtl.getErrMsg(); elec02batchTmp.setRsvFld16(errMsg);
			 * //TODO 根据errMsg确定rsvFld15,回盘用！ // 1-已扣 // 2-余额不足 // 3-帐号不符 //
			 * 4-帐号已销 // 5-坏帐号及其他, 除“已扣”，其他扣不到款 // 8-直接借记支付中的金额超过事先规定限额 //
			 * 9-直接借记无授权记录
			 * 
			 * 
			 * }
			 */
			if ("S".equals(sts)) {
				elec02batchTmp.setRsvFld15("1");
			}
			if ("F".equals(sts)) {
				String errCde = dtl.getErrMsg().toString().substring(0, 6);
				if ("TPM050".equals(errCde)) {
					elec02batchTmp.setRsvFld15("2");
				}
			}

			gdEupsbElecstBatchTmpRepository.update(elec02batchTmp);
		}

		// 更新临时表后取数据组成回盘文件

		List<GDEupsbElecstBatchTmp> tempList = gdEupsbElecstBatchTmpRepository
				.findByBatNo(rsvFld9);
		Assert.isNotEmpty(tempList, ErrorCodes.EUPS_QUERY_NO_DATA);

		GDEupsBatchConsoleInfo batchConsoleInfo = get(
				GDEupsBatchConsoleInfoRepository.class).findOne(rsvFld9);
		int totCnt = Integer.parseInt((String) batchConsoleInfo.getRsvFld3());
		int sucCnt = batchConsoleInfo.getSucTotCnt();// 已更新为上代收付后的成功笔数
		int failCnt = totCnt - sucCnt;

		BigDecimal totAmt = new BigDecimal(batchConsoleInfo.getRsvFld2());
		BigDecimal sucAmt = batchConsoleInfo.getSucTotAmt();// 元
		sucAmt = sucAmt.multiply(new BigDecimal(100));// 分
		BigDecimal failAmt = totAmt.subtract(sucAmt);

		batchConsoleInfo.setTotCnt(totCnt);
		batchConsoleInfo.setSucTotCnt(sucCnt);
		batchConsoleInfo.setFalTotCnt(failCnt);
		batchConsoleInfo.setTotAmt(totAmt);
		batchConsoleInfo.setSucTotAmt(sucAmt);
		batchConsoleInfo.setFalTotAmt(failAmt);

		batchConsoleInfo.setRsvFld6("RMB");

		get(GDEupsBatchConsoleInfoRepository.class).updateConsoleInfo(
				batchConsoleInfo);

		batchConsoleInfo.setRsvFld8("1");
		batchConsoleInfo.setRsvFld2("0000");
		batchConsoleInfo.setRsvFld3("交易成功");

		// ret.put("header", context.getDataMapDirectly());
		ret.put("header", batchConsoleInfo);
		ret.put("detail", tempList);

		EupsThdFtpConfig config = get(EupsThdFtpConfigRepository.class)
				.findOne("elec02BatchThdFileTest");
		String backFlieName = config.getLocFleNme();
		backFlieName = backFlieName.subSequence(0, 10) + "fh";
		String fliTme = DateUtils.format(new Date(), "yyyyMMdd_HHmmss");
		String fileName = backFlieName + fliTme + ".txt";
		config.setLocFleNme(fileName);
		config.setRmtFleNme(fileName);

		// EupsThdFtpConfig config = get(EupsThdFtpConfigRepository.class)
		// .findOne("");
		// config.setLocFleNme("BatchBack.txt");
		// config.setRmtFleNme("BatchBack.txt");

		Assert.isFalse(null == config, ErrorCodes.EUPS_THD_FTP_CONFIG_NOTEXIST);

		((OperateFileAction) get("opeFile")).createCheckFile(config,
				"ELEC02BatchBack", config.getLocFleNme(), ret);

		config.setFtpDir("0");// 0-外发
		((OperateFTPAction) get("opeFTP")).putCheckFile(config);
		/** 通知第三方 */

		context.setData("AppTradeCode", "23");
		context.setData("StartAddr", "301");
		context.setData("DestAddr", "0500");

		String br = context.getData(ParamKeys.BR);
		String sqnTmp = bbipPublicService.getBBIPSequence();
		sqnTmp = sqnTmp.substring(12);
		String msgId = br + " " + sqnTmp;

		context.setData("MesgID", msgId);
		context.setData("WorkDate", DateUtils.format(new Date(), "yyyyMmdd"));
		context.setData("SendTime",
				DateUtils.format(new Date(), "yyyyMmddHHmmss"));

		context.setData("mesgPRI", "9");
		context.setData("recordNum", totCnt);
		context.setData("FileName", config.getRmtFleNme());
		context.setData("zipFlag", "0");

		context.setData("TransCode", "23");
		context.setData("WD0",
				DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMdd));
		String logNo = ((BBIPPublicServiceImpl) get(GDConstants.BBIP_PUBLIC_SERVICE))
				.getBBIPSequence();
		context.setData("LogNo", StringUtils.substring(logNo, 4));
		context.setData("TMN", context.getData(ParamKeys.BK));// 经办网点
		context.setData("STO", context.getData(ParamKeys.TELLER).toString()
				.substring(1));
		context.setData("HAN", sucCnt);
		context.setData("HAM", sucAmt);
		context.setData("LSN", failCnt);
		context.setData("LSM", failAmt);

		Map<String, Object> thdResult = callThdTradeManager.trade(context);
		logger.info("电力返盘文件处理结束");
	}

}
