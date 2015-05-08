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
import org.springframework.beans.factory.annotation.Qualifier;

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
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GDEupsBatchConsoleInfo;
import com.bocom.bbip.gdeupsb.entity.GdGashBatchTmp;
import com.bocom.bbip.gdeupsb.repository.GDEupsBatchConsoleInfoRepository;
import com.bocom.bbip.gdeupsb.repository.GdGashBatchTmpRepository;
import com.bocom.bbip.utils.Assert;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

/**
 * 惠州燃气 文件批量托收完成通知燃气
 * 
 * @author WangMQ
 * 
 */
public class MsgToGasAftBatchAction extends BaseAction implements
		AfterBatchAcpService {
	@Autowired
	OperateFTPAction operateFTPAction;
	@Autowired
	OperateFileAction operateFileAction;
	@Autowired
	EupsBatchConsoleInfoRepository eupsBatchConsoleInfoRepository;
	@Autowired
	EupsThdFtpConfigRepository eupsThdFtpConfigRepository;
	@Autowired
	GdGashBatchTmpRepository gdGashBatchTmpRepository;
	@Autowired
	GDEupsBatchConsoleInfoRepository gdeupsBatchConsoleInfoRepository;
	@Autowired
	EupsBatchInfoDetailRepository eupsBatchInfoDetailRepository;
	@Autowired
	@Qualifier("callThdTradeManager")
	ThirdPartyAdaptor callThdTradeManager;

	private Logger logger = LoggerFactory
			.getLogger(MsgToGasAftBatchAction.class);

	public void afterBatchDeal(AfterBatchAcpDomain afterbatchacpdomain,
			Context context) throws CoreException {
		logger.info("===============Start  BatchDataResultFileAction  afterBatchDeal", context);

		// 第三方 rsvFld7
		String batNo = context.getData(ParamKeys.BAT_NO).toString();
		EupsBatchConsoleInfo eupsBatchConsoleInfo = eupsBatchConsoleInfoRepository
				.findOne(batNo);
		String sqns = eupsBatchConsoleInfo.getRsvFld2();
		GDEupsBatchConsoleInfo Info = new GDEupsBatchConsoleInfo();
		Info.setRsvFld7(sqns);
		GDEupsBatchConsoleInfo gdeupsBatchConsoleInfo = gdeupsBatchConsoleInfoRepository
				.find(Info).get(0);
		String gdBatNo = gdeupsBatchConsoleInfo.getBatNo();
		context.setData("gdBatNo", gdBatNo);
		// 更改控制表
		GDEupsBatchConsoleInfo gdEupsBatchConsoleInfoUpdate = updateInfo(
				context, gdeupsBatchConsoleInfo, eupsBatchConsoleInfo);

		String fileName = gdeupsBatchConsoleInfo.getFleNme();
		EupsThdFtpConfig gasFtpCfg = eupsThdFtpConfigRepository
				.findOne("PGAS00BatBack");
		gasFtpCfg.setLocFleNme(fileName);
		gasFtpCfg.setRmtFleNme(fileName);

		try {
			Map<String, Object> resultMap = createFileMap(context,
					gdEupsBatchConsoleInfoUpdate);
			gasFtpCfg.setFtpDir("0");
			operateFileAction.createCheckFile(gasFtpCfg, "msgToGasFileFmt",
					fileName, resultMap);
		} catch (CoreException e) {
			logger.info("~~~~~~~~~~~Error  Message", e);
		}

		operateFTPAction.putCheckFile(gasFtpCfg);

		//通知第三方
		callThd(context);
		logger.info("===============End  BatchDataResultFileAction  afterBatchDeal");
	}

	private void callThd(Context context) throws CoreException {
		logger.info("=================start MsgToGasAftBatchAction callThd with context======="
				, context);

		String oldProcessId = context.getProcessId();

		context.setProcessId("eups.fileBatchPayCreateDataProcess");
		String gdBatNo = context.getData("gdBatNo");
		GDEupsBatchConsoleInfo gdbat = new GDEupsBatchConsoleInfo();
		gdbat.setBatNo(gdBatNo);
		List<GDEupsBatchConsoleInfo> gdbatBatchConsoleInfos = gdeupsBatchConsoleInfoRepository
				.find(gdbat);
		String fleNme = gdbatBatchConsoleInfos.get(0).getFleNme();
		context.setData("fleNmeBak", fleNme);
		context.setData("TransCode", "SMPCPAYTXT");

		Map<String, Object> rspMap = callThdTradeManager.trade(context);

		context.setProcessId(oldProcessId);
		logger.info("=================end MsgToGasAftBatchAction callThd with context======="
				+ context);
	}

	/**
	 * 把信息保存到控制表
	 */
	public GDEupsBatchConsoleInfo updateInfo(Context context,
			GDEupsBatchConsoleInfo gdeupsBatchConsoleInfo,
			EupsBatchConsoleInfo eupsBatchConsoleInfo) {
		logger.info("===============Start  BatchDataResultFileAction  updateInfo");
		Integer totCnt = Integer.parseInt(eupsBatchConsoleInfo.getTotCnt()
				.toString());
		// 成功笔数
		Integer sucTotCnt = Integer.parseInt(context.getData(
				GDParamKeys.SUC_TOT_CNT).toString());
		gdeupsBatchConsoleInfo.setSucTotCnt(sucTotCnt);
		// 失败笔数
		Integer falTotCnt = totCnt - sucTotCnt;
		gdeupsBatchConsoleInfo.setFalTotCnt(falTotCnt);

		BigDecimal totAmt = eupsBatchConsoleInfo.getTotAmt();
		// 成功总金额
		BigDecimal sucTotAmt = new BigDecimal(context.getData("sucTotAmt")
				.toString());
		gdeupsBatchConsoleInfo.setSucTotAmt(sucTotAmt);
		// 失败总金额
		BigDecimal falTotAmt = totAmt.subtract(sucTotAmt);
		gdeupsBatchConsoleInfo.setFalTotAmt(falTotAmt);
		// 更改状态
		gdeupsBatchConsoleInfo.setBatSts("S");
		// 更新批次表
		gdeupsBatchConsoleInfoRepository
				.updateConsoleInfo(gdeupsBatchConsoleInfo);
		logger.info("===============End  BatchDataResultFileAction  updateInfo");
		return gdeupsBatchConsoleInfo;
	}

	/**
	 * 拼装map文件
	 * @throws CoreException 
	 */
	public Map<String, Object> createFileMap(Context context,
			GDEupsBatchConsoleInfo gdEupsBatchConsoleInfoUpdate) throws CoreException {
		logger.info("===============Start  BatchDataResultFileAction  createFileMap");

		String gdBatNo = context.getData("gdBatNo");
		String cusAc = null;
		String cusNo = null;
		String thdSts = null;
		String sts = null;
		
		String batNo = context.getData(ParamKeys.BAT_NO);
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
		
		EupsBatchInfoDetail eupsBatchInfoDetail = new EupsBatchInfoDetail();
		eupsBatchInfoDetail.setBatNo(batNo);
		List<EupsBatchInfoDetail> result = get(
				EupsBatchInfoDetailRepository.class).find(eupsBatchInfoDetail);
		Assert.isNotEmpty(result, ErrorCodes.EUPS_QUERY_NO_DATA);

		log.info("得到批扣result，更新进临时表");
		GdGashBatchTmp findInfo = new GdGashBatchTmp();
		List<GdGashBatchTmp> listTmps = new ArrayList<GdGashBatchTmp>();
		for (EupsBatchInfoDetail detail : result) {
			GdGashBatchTmp gdGashBatchTmp = new GdGashBatchTmp();

			// B0,B1,B2,B3 根据detail.getSts()/detail.getErrMsg()设定状态thdSts
			sts = detail.getSts();
			if ("S".equals(sts)) {
				thdSts = "B0";
				gdGashBatchTmp.setBatSts("S");

			} else {
				String errCode = detail.getErrMsg().toString().substring(0, 6);
				if (errCode.equals("TPM050")) {
					thdSts = "B1";
				} else if (errCode.equals("CB1004")
						|| "不存在代扣协议".contains(detail.getErrMsg())) {
					thdSts = "B2";
				} else {
					thdSts = "B3";
				}
				gdGashBatchTmp.setBatSts("F");
			}

			cusAc = detail.getCusAc();
			cusNo = detail.getAgtSrvCusId();
			findInfo.setThdSqn((String) detail.getRmk1());
			findInfo.setCusAc(cusAc);
			findInfo.setCusNo(cusNo);
			findInfo.setBatNo(gdBatNo);
			listTmps = gdGashBatchTmpRepository.find(findInfo);

			gdGashBatchTmp.setSqn(listTmps.get(0).getSqn());
			gdGashBatchTmp.setThdSqn(detail.getRmk1());
			gdGashBatchTmp.setCusNo(cusNo);
			gdGashBatchTmp.setPayMon(listTmps.get(0).getPayMon());
			gdGashBatchTmp.setTxnAmt(String.valueOf(detail.getTxnAmt()));
			gdGashBatchTmp.setTxnDte(listTmps.get(0).getTxnDte());
			gdGashBatchTmp.setTmpFld5(thdSts);

			// Update 临时表
			gdGashBatchTmpRepository.update(gdGashBatchTmp);
		}
		// 内容主体
		List<GdGashBatchTmp> list = new ArrayList<GdGashBatchTmp>();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		GdGashBatchTmp resultBatchTmp = new GdGashBatchTmp();
		resultBatchTmp.setBatNo(gdBatNo);
		list = get(GdGashBatchTmpRepository.class).find(resultBatchTmp);
		Assert.isNotEmpty(list, ErrorCodes.EUPS_QUERY_NO_DATA);
		resultMap.put(ParamKeys.EUPS_FILE_DETAIL, BeanUtils.toMaps(list));
		logger.info("===============End  BatchDataResultFileAction  createFileMap");
		return resultMap;
	}
}
