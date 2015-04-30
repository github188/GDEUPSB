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
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsBatchConsoleInfo;
import com.bocom.bbip.eups.entity.EupsBatchInfoDetail;
import com.bocom.bbip.eups.entity.EupsThdFtpConfig;
import com.bocom.bbip.eups.repository.EupsBatchConsoleInfoRepository;
import com.bocom.bbip.eups.repository.EupsBatchInfoDetailRepository;
import com.bocom.bbip.eups.repository.EupsThdFtpConfigRepository;
import com.bocom.bbip.eups.spi.service.batch.AfterBatchAcpService;
import com.bocom.bbip.eups.spi.vo.AfterBatchAcpDomain;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GDEupsBatchConsoleInfo;
import com.bocom.bbip.gdeupsb.entity.GdGashBatchTmp;
import com.bocom.bbip.gdeupsb.repository.GDEupsBatchConsoleInfoRepository;
import com.bocom.bbip.gdeupsb.repository.GdGashBatchTmpRepository;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.CollectionUtils;
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
		logger.info("===============Start  BatchDataResultFileAction  afterBatchDeal");
		logger.info(">>>>>Start  Down  AGTS  FileResult <<<<<<");

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
		// 文件名
		// String fileName = context.getData("fleNmeBak");
		// EupsThdFtpConfig ftpCfg =
		// eupsThdFtpConfigRepository.findOne("batchPayFileToAcp");
		// ftpCfg.setLocFleNme(fileName);
		// ftpCfg.setRmtFleNme(fileName);

		String fileName = "filecnjt"
				+ DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMdd)
				+ ".txt";
		EupsThdFtpConfig gasFtpCfg = eupsThdFtpConfigRepository
				.findOne("PGAS00Bat");
		gasFtpCfg.setLocFleNme(fileName);
		gasFtpCfg.setRmtFleNme(fileName);

		// EupsThdFtpConfig ftpCfg =
		// eupsThdFtpConfigRepository.findOne("batchPayFileToAcp");
		// String name = context.getData(ParamKeys.BAT_NO) + ".result";
		// ftpCfg.setLocFleNme(fileName);
		// ftpCfg.setRmtFleNme(fileName);
		// ftpCfg.setLocDir("C:/home/bbipadm/data/mftp/BBIP/GDEUPSB/01491800999/EFC0000/20150323/");
		// ftpCfg.setRmtWay("/home/bbipadm/data/mftp/BBIP/GDEUPSB/01491800999/EFC0000/20150323/");

		try {
			Map<String, Object> resultMap = createFileMap(context,
					gdEupsBatchConsoleInfoUpdate);
			gasFtpCfg.setFtpDir("0");

			// gasFtpCfg.setLocDir("/home/bbipadm/data/mftp/BBIP/GDEUPSB/01441800999/EFC0000/20150323/");
			// ftpCfg.setRmtWay("/home/bbipadm/data/mftp/BBIP/GDEUPSB/01441800999/EFC0000/20150323/");
			operateFileAction.createCheckFile(gasFtpCfg, "msgToGasFileFmt",
					fileName, resultMap);
		} catch (CoreException e) {
			logger.info("~~~~~~~~~~~Error  Message", e);
		}

		gasFtpCfg.setFtpDir("0");
		gasFtpCfg.setRmtWay("/BANK/CNJT/reckoning");
		operateFTPAction.putCheckFile(gasFtpCfg);

		// TODO 通知第三方
		callThd(context);
		logger.info("===============End  BatchDataResultFileAction  afterBatchDeal");
	}

	private void callThd(Context context) throws CoreException {
		logger.info("=================start MsgToGasAftBatchAction callThd with context======="
				+ context);

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
	 */
	public Map<String, Object> createFileMap(Context context,
			GDEupsBatchConsoleInfo gdEupsBatchConsoleInfoUpdate) {
		logger.info("===============Start  BatchDataResultFileAction  createFileMap");
		// 代收付文件内容
		List<EupsBatchInfoDetail> mapList = context.getVariable("detailList");
		//
		logger.info(">>>>>>>>>>>>>>>>>>>>> mapList.size(): "
				+ CollectionUtils.isEmpty(mapList));
		logger.info(">>>>>>>>>>>>>>>>>>>>> mapList.size(): "
				+ context.getVariable("detailList"));

		// 内容主体
		List<GdGashBatchTmp> list = new ArrayList<GdGashBatchTmp>();

		String gdBatNo = context.getData("gdBatNo");
		String cusAc = null;
		String cusNo = null;
		String thdSts = null;
		String sts = null;

		GdGashBatchTmp findInfo = new GdGashBatchTmp();
		List<GdGashBatchTmp> listTmps = new ArrayList<GdGashBatchTmp>();
		for (EupsBatchInfoDetail detail : mapList) {
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

			gdGashBatchTmp.setBk("cnjt");

			cusAc = detail.getCusAc();
			cusNo = detail.getAgtSrvCusId();
			findInfo.setThdSqn((String) detail.getRmk1());
			findInfo.setCusAc(cusAc);
			findInfo.setCusNo(cusNo);
			findInfo.setBatNo(gdBatNo);
			listTmps = gdGashBatchTmpRepository.find(findInfo);

			gdGashBatchTmp.setSqn(listTmps.get(0).getSqn());
			gdGashBatchTmp.setThdSqn(listTmps.get(0).getThdSqn());
			gdGashBatchTmp.setCusNo(cusNo);
			gdGashBatchTmp.setPayMon(listTmps.get(0).getPayMon());
			gdGashBatchTmp.setTxnAmt(String.valueOf(detail.getTxnAmt()));
			gdGashBatchTmp.setTxnDte(listTmps.get(0).getTxnDte());
			gdGashBatchTmp.setTmpFld5(thdSts);

			// Update 临时表，为对账做准备
			gdGashBatchTmpRepository.update(gdGashBatchTmp);
			list.add(gdGashBatchTmp);
		}
		Map<String, Object> resultMap = new HashMap<String, Object>();
		// resultMap.put(ParamKeys.EUPS_FILE_HEADER, headMap);
		// System.out.println(resultMap.get(ParamKeys.EUPS_FILE_HEADER));
		resultMap.put(ParamKeys.EUPS_FILE_DETAIL, BeanUtils.toMaps(list));
		logger.info("===============End  BatchDataResultFileAction  createFileMap");
		return resultMap;
	}
}
