package com.bocom.bbip.gdeupsb.strategy.elcgd;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.eups.action.common.OperateFTPAction;
import com.bocom.bbip.eups.action.common.OperateFileAction;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsThdFtpConfig;
import com.bocom.bbip.eups.entity.EupsThdTranCtlInfo;
import com.bocom.bbip.eups.repository.EupsThdFtpConfigRepository;
import com.bocom.bbip.eups.repository.EupsThdTranCtlInfoRepository;
import com.bocom.bbip.eups.spi.service.check.CheckBkFileToThirdService;
import com.bocom.bbip.eups.spi.vo.CheckDomain;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GdEupsTransJournal;
import com.bocom.bbip.gdeupsb.repository.GdEupsTransJournalRepository;
import com.bocom.bbip.utils.CollectionUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

/**
 * 银行发起银行代扣代收电费对帐交易
 * 
 * @author qc.w
 * 
 */
public class CheckBkEleGzFileToThirdAction implements CheckBkFileToThirdService {

	private final static Logger log = LoggerFactory.getLogger(CheckBkEleGzFileToThirdAction.class);

	@Autowired
	EupsThdTranCtlInfoRepository eupsThdTranCtlInfoRepository;

	@Autowired
	GdEupsTransJournalRepository gdEupsTransJournalRepository;

	@Autowired
	OperateFileAction operateFile;

	@Autowired
	OperateFTPAction operateFTPAction;

	@Autowired
	EupsThdFtpConfigRepository eupsThdFtpConfigRepository;

	@Override
	public Map<String, Object> checkBkFileToThird(CheckDomain checkdomain, Context context) throws CoreException {

		String eupsBusTyp = context.getData(ParamKeys.EUPS_BUSS_TYPE); // 业务类型

		String clearDteStr = (String) context.getData("clrDat"); // 电费对账日期

		if (null == clearDteStr) { // 对账为自动发起
			clearDteStr = DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMdd);
		}

		Date clearDte = DateUtils.parse(clearDteStr);

		// TODO：单位编号根据配型部类型查找
		String comNo = "ELEC01";

		// 检查签到签退
		EupsThdTranCtlInfo eupsThdTranCtlInfo = eupsThdTranCtlInfoRepository.findOne(comNo);
		if (!eupsThdTranCtlInfo.isTxnCtlStsSignout()) {
			throw new CoreException(ErrorCodes.THD_CHL_TRADE_NOT_ALLOWWED);
		}
		log.info("已签退，可以进行对账");

		String dptTyp = "0000";

		// 分别生成划扣及缴费对账文件
		String fileNameDk = "00" + "01_" + clearDteStr + "_315810" + "_001.bil"; // 代扣文件
		String fileNameJf = "00" + "02_" + clearDteStr + "_315810" + "_001.bil"; // 缴费文件

		String tmpTxnCodDk = "460230";
		String tmpTxncodJf = "460245";

		// TODO:switch code,单位编号根据适配类型做table转换
		String cAgtNoDk = "4410001273";
		String cAgtNoJf = "4410001273";

		Map<String, Object> checkFileHK = new HashMap<String, Object>(); // 划扣对账文件
		// 查询代扣的数据
		GdEupsTransJournal eupsTransJournal = new GdEupsTransJournal();
		eupsTransJournal.setEupsBusTyp(eupsBusTyp);
		eupsTransJournal.setTxnDte(clearDte);
		eupsTransJournal.setMfmTxnSts(Constants.FIN_TXNSTS_SUCCESS); // 主机交易状态为成功

		eupsTransJournal.setThdTxnCde("HK"); // 第三方交易码为划扣
		Map<String, Object> headerInfoHK = new HashMap<String, Object>(); // 划账map头
		// 统计划扣总体信息
		List<Map<String, Object>> hkHeaderResult = gdEupsTransJournalRepository.findGzEleChkHKInfo(eupsTransJournal);
		if (CollectionUtils.isNotEmpty(hkHeaderResult)) {
			headerInfoHK = hkHeaderResult.get(0);
		}
		else { // 无数据
			headerInfoHK.put("clrDat", clearDteStr);
			headerInfoHK.put("totalDeal", "0");
			headerInfoHK.put("totalFee", "0");
			headerInfoHK.put("batNo", "");
		}
		checkFileHK.put("header", headerInfoHK);

		// 查询划扣明细信息
		List<Map<String, Object>> hkDetailResult = gdEupsTransJournalRepository.findGzEleChkHKDetail(eupsTransJournal);
		if (CollectionUtils.isNotEmpty(hkHeaderResult)) {
			checkFileHK.put("detail", hkDetailResult);
		}

		Map<String, Object> checkFileJF = new HashMap<String, Object>(); // 缴费对账文件
		// 统计缴费总体信息
		eupsTransJournal.setThdTxnCde("JF"); // 第三方交易码为划扣
		Map<String, Object> headerInfoJF = new HashMap<String, Object>(); // 划账map头

		List<Map<String, Object>> jfHeaderResult = gdEupsTransJournalRepository.findGzEleChkJFInfo(eupsTransJournal);
		if (CollectionUtils.isNotEmpty(jfHeaderResult)) {
			headerInfoJF = jfHeaderResult.get(0);
		}
		else { // 无数据
			headerInfoJF.put("clrDat", clearDteStr);
			headerInfoJF.put("totalDeal", "0");
			headerInfoJF.put("totalFee", "0");
			headerInfoJF.put("batNo", "");
		}
		checkFileJF.put("header", headerInfoJF);

		// 查询缴费明细信息
		List<Map<String, Object>> jfDetailResult = gdEupsTransJournalRepository.findGzEleChkJFDetail(eupsTransJournal);
		if (CollectionUtils.isNotEmpty(jfDetailResult)) {
			checkFileJF.put("detail", jfDetailResult);
		}

		EupsThdFtpConfig eupsThdFtpConfig = eupsThdFtpConfigRepository.findOne("eleGzCheckHK");

		operateFile.createCheckFile(eupsThdFtpConfig, "eleGzCheckHKFmt", fileNameDk, checkFileHK);

		// ftpput划扣文件
		eupsThdFtpConfig.setRmtFleNme(fileNameDk);
		eupsThdFtpConfig.setLocFleNme(fileNameDk);
		operateFTPAction.putCheckFile(eupsThdFtpConfig);

		eupsThdFtpConfig = eupsThdFtpConfigRepository.findOne("eleGzCheckJF");

		operateFile.createCheckFile(eupsThdFtpConfig, "eleGzCheckJFFmt", fileNameJf, checkFileJF);

		// ftpput缴费文件
		eupsThdFtpConfig.setRmtFleNme(fileNameJf);
		eupsThdFtpConfig.setLocFleNme(fileNameJf);
		operateFTPAction.putCheckFile(eupsThdFtpConfig);

		return null;
	}
}
