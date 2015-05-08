package com.bocom.bbip.gdeupsb.strategy.elcgd;

import java.math.BigDecimal;
import java.util.ArrayList;
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
import com.bocom.bbip.gdeupsb.utils.CodeSwitchUtils;
import com.bocom.bbip.utils.CollectionUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.bbip.utils.NumberUtils;
import com.bocom.bbip.utils.StringUtils;
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

		String dpTyp = context.getData(GDParamKeys.GZ_ELE_DPT_TYP);
		String comNo = CodeSwitchUtils.codeGenerator("eleGzComNoGen", dpTyp);
		log.info("after codeSwitch, dptTyp change from [" + dpTyp + "],to [" + comNo + "]");
		if (StringUtils.isEmpty(comNo)) {
			throw new CoreException(ErrorCodes.EUPS_COM_NO_NOTEXIST);
		}

		// 检查签到签退
		EupsThdTranCtlInfo eupsThdTranCtlInfo = eupsThdTranCtlInfoRepository.findOne(comNo);
		if (!eupsThdTranCtlInfo.isTxnCtlStsSignout()) {
			log.info("未签退，无法发起对账");
			throw new CoreException(ErrorCodes.THD_CHL_TRADE_NOT_ALLOWWED);
		}

		log.info("已签退，可以进行对账");
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
		eupsTransJournal.setBakFld1(clearDteStr); // 使用备用字段(第三方约定的交易日期，原老系统“清算日期”)作为对账条件
		eupsTransJournal.setMfmTxnSts(Constants.FIN_TXNSTS_SUCCESS); // 主机交易状态为成功

		eupsTransJournal.setThdTxnCde("HK"); // 第三方交易码为划扣
		Map<String, Object> headerInfoHK = new HashMap<String, Object>(); // 划账map头

		// 统计划扣总体信息
		List<Map<String, Object>> hkHeaderResult = gdEupsTransJournalRepository.findGzEleChkHKInfo(eupsTransJournal);
		if (CollectionUtils.isNotEmpty(hkHeaderResult)) {
			headerInfoHK = hkHeaderResult.get(0);
			BigDecimal orgAmtS = (BigDecimal) headerInfoHK.get("TOTALFEE");
			if (null == orgAmtS) {
				orgAmtS = new BigDecimal("0");
			}
			headerInfoHK.put("TOTALFEE", NumberUtils.yuanToCentString(orgAmtS));
		}
		else { // 无数据
			headerInfoHK.put("TOTALDEAL", "0");
			headerInfoHK.put("TOTALFEE", "0");
		}
		String thdClearDteStr = clearDteStr.substring(2);
		headerInfoHK.put("clrDat", thdClearDteStr);
		headerInfoHK.put("batNo", context.getData(ParamKeys.SEQUENCE));
		checkFileHK.put("header", headerInfoHK);

		// 查询划扣明细信息
		List<Map<String, Object>> hkDetailResult = gdEupsTransJournalRepository.findGzEleChkHKDetail(eupsTransJournal);

		List<Map<String, Object>> hkDetailResultReal = new ArrayList<Map<String, Object>>();
		if (CollectionUtils.isNotEmpty(hkHeaderResult)) {
			for (Map<String, Object> hkDMap : hkDetailResult) {
				Date tactDt = (Date) hkDMap.get("TACTDT");
				if (null != tactDt) {
					String tactDtS = DateUtils.format(tactDt, "yyyyMMdd");
					tactDtS = tactDtS.substring(2);
					hkDMap.put("TACTDT", tactDtS);
				}

				Date actDt = (Date) hkDMap.get("ACTDAT");
				if (null != actDt) {
					String actDts = DateUtils.format(actDt, "yyyy-MM-dd");
					actDts = actDts.replace("-", "");
					hkDMap.put("ACTDAT", actDts);
				}

				Date nkTme = (Date) hkDMap.get("BKTIM");
				if (null != nkTme) {
					String nkTmeS = DateUtils.format(nkTme, "yyyy-MM-dd HH:mm:ss");
					nkTmeS = nkTmeS.substring(11).replace(":", "");
					hkDMap.put("BKTIM", nkTmeS);
				}

				// 电费月份处理
				String rmkTmp = (String) hkDMap.get("RMKTMP");
				if (StringUtils.isNotEmpty(rmkTmp)) {
					rmkTmp = rmkTmp.substring(23, 31);
					hkDMap.put("RMKTMP", rmkTmp);
				}

				// 第三方交易日期，时间处理
				Date thdTme = (Date) hkDMap.get("DLTIM");
				if (null != thdTme) {
					String thdTmeS = DateUtils.format(thdTme, "yyyy-MM-dd HH:mm:ss");
					// 交易日期:
					String dLDate = thdTmeS.substring(0, 10);
					dLDate = dLDate.substring(5).replace("-", "");
					hkDMap.put("DLDAT", dLDate);

					// 交易时间：
					String dLTim = thdTmeS.substring(11, 19);
					dLTim = dLTim.replace(":", "");
					hkDMap.put("DLTIM", dLTim);
				}

				hkDetailResultReal.add(hkDMap);
			}

			checkFileHK.put("detail", hkDetailResultReal);
		}

		Map<String, Object> checkFileJF = new HashMap<String, Object>(); // 缴费对账文件
		// 统计缴费总体信息
		eupsTransJournal.setThdTxnCde("JF"); // 第三方交易码为划扣
		Map<String, Object> headerInfoJF = new HashMap<String, Object>(); // 划账map头

		List<Map<String, Object>> jfHeaderResult = gdEupsTransJournalRepository.findGzEleChkJFInfo(eupsTransJournal);
		if (CollectionUtils.isNotEmpty(jfHeaderResult)) {
			headerInfoJF = jfHeaderResult.get(0);
			String orgAmtS = (String) headerInfoHK.get("TOTALFEE");
			headerInfoHK.put("TOTALFEE", NumberUtils.yuanToCentString(orgAmtS));
		}
		else { // 无数据
			headerInfoJF.put("TOTALDEAL", "0");
			headerInfoJF.put("TOTALFEE", "0");
		}
		headerInfoJF.put("clrDat", thdClearDteStr);
		headerInfoJF.put("batNo", context.getData(ParamKeys.SEQUENCE));

		checkFileJF.put("header", headerInfoJF);

		// 查询缴费明细信息
		List<Map<String, Object>> jfDetailResultReal = new ArrayList<Map<String, Object>>();

		List<Map<String, Object>> jfDetailResult = gdEupsTransJournalRepository.findGzEleChkJFDetail(eupsTransJournal);
		if (CollectionUtils.isNotEmpty(jfHeaderResult)) {
			for (Map<String, Object> jfDMap : jfDetailResult) {

				Date tactDt = (Date) jfDMap.get("TACTDT");
				if (null != tactDt) {
					String tactDtS = DateUtils.format(tactDt, "yyyyMMdd");
					tactDtS = tactDtS.substring(2);
					jfDMap.put("TACTDT", tactDtS);
				}

				Date actDt = (Date) jfDMap.get("ACTDAT");
				if (null != actDt) {
					String actDts = DateUtils.format(actDt, "yyyy-MM-dd");
					actDts = actDts.replace("-", "");
					jfDMap.put("ACTDAT", actDts);
				}

				Date nkTme = (Date) jfDMap.get("BKTIM");
				String nkTmeS = DateUtils.format(nkTme, "yyyy-MM-dd HH:mm:ss");
				nkTmeS = nkTmeS.substring(11).replace(":", "");
				jfDMap.put("BKTIM", nkTmeS);

				// 电费月份处理
				String rmkTmp = (String) jfDMap.get("RMKTMP");
				if (StringUtils.isNotEmpty(rmkTmp)) {
					rmkTmp = rmkTmp.substring(23, 31);
					jfDMap.put("RMKTMP", rmkTmp);
				}

				// 第三方交易日期，时间处理
				Date thdTme = (Date) jfDMap.get("DLTIM");
				if (null != thdTme) {
					String thdTmeS = DateUtils.format(thdTme, "yyyy-MM-dd HH:mm:ss");
					// 交易日期:
					String dLDate = thdTmeS.substring(0, 10);
					dLDate = dLDate.substring(5).replace("-", "");
					jfDMap.put("DLDAT", dLDate);

					// 交易时间：
					String dLTim = thdTmeS.substring(11, 19);
					dLTim = dLTim.replace(":", "");
					jfDMap.put("DLTIM", dLTim);
				}
				jfDetailResultReal.add(jfDMap);
			}

			checkFileJF.put("detail", jfDetailResultReal);
		}

		EupsThdFtpConfig eupsThdFtpConfig = eupsThdFtpConfigRepository.findOne("eleGzCheckHK");

		operateFile.createCheckFile(eupsThdFtpConfig, "eleGzCheckHKFmt", fileNameDk, checkFileHK);

		// ftpput划扣文件
		eupsThdFtpConfig.setRmtFleNme(fileNameDk);
		eupsThdFtpConfig.setLocFleNme(fileNameDk);
		operateFTPAction.putCheckFile(eupsThdFtpConfig);

		eupsThdFtpConfig = eupsThdFtpConfigRepository.findOne("eleGzCheckJF");

		log.info("缴费的文件为:" + fileNameJf + checkFileJF);
		eupsThdFtpConfig.setRmtFleNme(fileNameJf);
		eupsThdFtpConfig.setLocFleNme(fileNameJf);

		operateFile.createCheckFile(eupsThdFtpConfig, "eleGzCheckJFFmt", fileNameJf, checkFileJF);

		// ftpput缴费文件
		eupsThdFtpConfig.setRmtFleNme(fileNameJf);
		eupsThdFtpConfig.setLocFleNme(fileNameJf);

		log.info("putCheckFile config,rmt path=[" + eupsThdFtpConfig.getRmtWay() + "],rmt file name=[" + eupsThdFtpConfig.getRmtFleNme() + "]"
				+ ",local path=[" + eupsThdFtpConfig.getLocDir() + "],local file name=[" + eupsThdFtpConfig.getLocFleNme() + "]");
		operateFTPAction.putCheckFile(eupsThdFtpConfig);

		return null;
	}
}
