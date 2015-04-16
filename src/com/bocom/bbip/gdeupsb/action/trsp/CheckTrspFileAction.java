package com.bocom.bbip.gdeupsb.action.trsp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.comp.btp.BTPService;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.action.common.OperateFTPAction;
import com.bocom.bbip.eups.action.common.OperateFileAction;
import com.bocom.bbip.eups.adaptor.ThirdPartyAdaptor;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsThdFtpConfig;
import com.bocom.bbip.eups.repository.EupsThdFtpConfigRepository;
import com.bocom.bbip.file.reporting.impl.VelocityTemplatedReportRender;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GDEupsbTrspFeeInfo;
import com.bocom.bbip.gdeupsb.entity.TrspCheckTmp;
import com.bocom.bbip.gdeupsb.repository.GDEupsbTrspFeeInfoRepository;
import com.bocom.bbip.gdeupsb.repository.TrspCheckTmpRepository;
import com.bocom.bbip.service.Result;
import com.bocom.bbip.thd.org.apache.commons.collections.CollectionUtils;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class CheckTrspFileAction extends BaseAction {
	private final static Log logger = LogFactory
			.getLog(CheckTrspFileAction.class);
	@Autowired
	TrspCheckTmpRepository trspCheckTmpRepository;
	@Autowired
	GDEupsbTrspFeeInfoRepository gdEupsbTrspFeeInfoRepository;
	@Autowired
	OperateFTPAction operateFTPAction;
	@Autowired
	EupsThdFtpConfigRepository eupsThdFtpConfigRepository;
	@Autowired
	OperateFileAction operateFileAction;
	@Autowired
	@Qualifier("callThdTradeManager")
	ThirdPartyAdaptor callThdTradeManager;

	public void execute(Context context) throws CoreException,
			CoreRuntimeException {
		logger.info("=================Start  CheckTrspFile");

		 String  tChkNo=((BTPService)get("BTPService")).applyBatchNo(ParamKeys.BUSINESS_CODE_COLLECTION);
		// 交易上锁
		Result ret = get(BBIPPublicService.class).tryLock(tChkNo, (long) 0,(long) 600);
		int status = ret.getStatus();
		if (status != 0) {
			log.info("交易并发，请稍后在做");
			context.setData(ParamKeys.RSP_CDE, "329999");
			context.setData(ParamKeys.RSP_MSG, "交易并发，请稍后在做 !!");
			throw new CoreException("交易并发，请稍后在做 ");
		}

		if (null == context.getData(GDParamKeys.END_DATE)) {
			context.setData(GDParamKeys.END_DATE,DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMdd));
		}
		// 外发第三方
		callThd(context);
//		context.setData("fileName", "trsptrsp.txt");
		// 清除对账表中的信息
		get(TrspCheckTmpRepository.class).deleteAll("1");
		// 文件内容添加到对账表
		fileInsertTrspCheckTmp(context, tChkNo);

		// 错误数量
		int numErr = 0;
		// 错误金额
		BigDecimal AmtErr = new BigDecimal("0.00");
		// 错误信息
		String chkErr = "";
		// TODO 文件名
		String fileName = context.getData("fileName").toString();
		List<TrspCheckTmp> list = trspCheckTmpRepository.findByTChkNo(tChkNo);
		int chkFlg = -1;
		// 轮询对账
		List<GDEupsbTrspFeeInfo> detailList = new ArrayList<GDEupsbTrspFeeInfo>();
		for (TrspCheckTmp trspCheckTmp : list) {
			// 根据流水得到每条数据
			GDEupsbTrspFeeInfo gdEupsbTrspFeeInfo = gdEupsbTrspFeeInfoRepository.findOne(trspCheckTmp.getSqn());
			if (gdEupsbTrspFeeInfo == null) {
				chkErr = "企业多账";
				chkFlg = 3;
				// 跟新表中对账状态
				// 更新银行单边账 设置3 = chkFlg
				gdEupsbTrspFeeInfoRepository.updateChkFlg(tChkNo);
			} else {
				if (!gdEupsbTrspFeeInfo.getCarNo().equals(
						trspCheckTmp.getCarNo())
						|| !gdEupsbTrspFeeInfo.getCarTyp().equals(
								trspCheckTmp.getCarTyp())) {
					chkErr = "企业车牌号或车辆类型不符";
					chkFlg = 2;
				} else if (!gdEupsbTrspFeeInfo.getInvNo().equals(
						trspCheckTmp.getInvNo())) {
					chkErr = "交易状态不符";
					chkFlg = 2;
				} else if (gdEupsbTrspFeeInfo.getTxnAmt().compareTo(
						trspCheckTmp.getTxnAmt()) != 0
						|| !gdEupsbTrspFeeInfo.getPayMon().equals(
								trspCheckTmp.getPayMon())) {
					chkErr = "金额或月数不符";
					chkFlg = 2;
				} else {
					chkErr = "核对成功";
					chkFlg = 1;
				}
				// 临时使用实体类 定义类型和数据 使其生成文件使用
				GDEupsbTrspFeeInfo gdEupsbTrspFeeInfoNew = new GDEupsbTrspFeeInfo();
				gdEupsbTrspFeeInfoNew.setTcusNm(chkErr); // 错误
				gdEupsbTrspFeeInfoNew.setThdKey(trspCheckTmp.getSqn()); // 流水
				gdEupsbTrspFeeInfoNew.setTxnAmt(gdEupsbTrspFeeInfo.getTxnAmt()); // 金额
				gdEupsbTrspFeeInfoNew.setPayMon(gdEupsbTrspFeeInfo.getPayMon()); // 月份
				gdEupsbTrspFeeInfoNew.setCarTyp(gdEupsbTrspFeeInfo.getCarTyp()); // 车类型
				gdEupsbTrspFeeInfoNew.setCarNo(gdEupsbTrspFeeInfo.getCarNo()); // 车牌号
				gdEupsbTrspFeeInfoNew.setCarDzs(DateUtils.format(gdEupsbTrspFeeInfo.getPayDat(),DateUtils.STYLE_yyyyMMdd)); // 缴费日期
				gdEupsbTrspFeeInfoNew.setInvNo(gdEupsbTrspFeeInfo.getInvNo()); // 发票
				gdEupsbTrspFeeInfoNew.setActNo(gdEupsbTrspFeeInfo.getActNo()); // 账号
				gdEupsbTrspFeeInfoNew.setPayTlr(gdEupsbTrspFeeInfo.getPayTlr()); // 操作柜员
				gdEupsbTrspFeeInfoNew.setPayNod(gdEupsbTrspFeeInfo.getPayNod()); // 网点
				// 是否打印
				gdEupsbTrspFeeInfoNew.setStatus("0");

				detailList.add(gdEupsbTrspFeeInfoNew);
				// TODO
				if (1 != chkFlg) {
					numErr = numErr + 1;
					AmtErr = AmtErr.add(trspCheckTmp.getTxnAmt());
					// 银行交易日期|银行流水号|发生额|发票号|缴费月数|车辆类型|车牌号|状态|
				}
				// 跟新对账标志
				String checkFlg = chkFlg + "";
				gdEupsbTrspFeeInfo.setChkFlg(checkFlg);
				gdEupsbTrspFeeInfo.setTchkNo(tChkNo);
				gdEupsbTrspFeeInfoRepository.update(gdEupsbTrspFeeInfo);
			}
		}

		context.setData("detailList", detailList);
		context.setData(ParamKeys.TXN_AMT, AmtErr);
		log.info("============开始拼装文件");
		// 拼装文件
		EupsThdFtpConfig eupsThdFtpConfig = get(EupsThdFtpConfigRepository.class).findOne("trspCheckFile");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(ParamKeys.EUPS_FILE_DETAIL, BeanUtils.toMaps(detailList));
		get(OperateFileAction.class).createCheckFile(eupsThdFtpConfig,"trspCheckFile", fileName, map);

		log.info("============是否全部对账");
		// 判断是否全部对账

		List<GDEupsbTrspFeeInfo> gdEupsbTrspFeeInfoList = gdEupsbTrspFeeInfoRepository.findNotCheck(tChkNo);
		if (CollectionUtils.isNotEmpty(gdEupsbTrspFeeInfoList)) {
			context.setData(ParamKeys.RSP_CDE, "329999");
			context.setData(ParamKeys.RSP_MSG, "系统错误");
			printDetail(context, tChkNo, gdEupsbTrspFeeInfoList);
			throw new CoreException("系统错误");
		}
		context.setData(GDParamKeys.MSGTYP, "N");
		context.setData(ParamKeys.RSP_CDE, "000000");
		context.setData(ParamKeys.RSP_MSG, "交易成功");

		ret = get(BBIPPublicService.class).unlock(tChkNo);

		// 放到指定位置
		sendFile(context, fileName);
		printDetail(context, tChkNo, detailList);
		// 清除对账表中的信息
		get(TrspCheckTmpRepository.class).deleteAll(tChkNo);
		logger.info("============对账结束");
	}

	/**
	 * 外发第三方
	 */
	public void callThd(Context context) {
		logger.info("~~~~~~~~~~~Start  CheckTrspFile   callThd");
		
		context.setData("thdTxnCde", "GetChk");
		String txnDte=DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMdd);
		context.setData(ParamKeys.TXN_DTE, txnDte);
		context.setData(ParamKeys.SEQUENCE, get(BBIPPublicService.class).getBBIPSequence());
		try {
			Map<String, Object> rspMap = callThdTradeManager.trade(context);
			String responseCode = rspMap.get(ParamKeys.THIRD_RETURN_CODE).toString();
			logger.info("==================Call Third  Success");
			context.setData(GDParamKeys.RETCOD, responseCode);
//			context.setData("fileName", rspMap.get("fileName"));
			String fileName=(String)rspMap.get("fileName");
			context.setData("fileName", fileName);
			if (!Constants.RESPONSE_CODE_SUCC.equals(responseCode)) {
				context.setData(GDParamKeys.MSGTYP, "E");
				context.setData(ParamKeys.RSP_CDE, "329999");
				context.setData(ParamKeys.RSP_MSG, "调用路桥方时:系统错误或超时或未发送,请检查后重做");
				throw new CoreException("调用路桥方时:系统错误或超时或未发送,请检查后重做");
			}
		} catch (CoreException e) {
			logger.info("CheckTrspFile   callThd  error=" + e);
		}
		
		System.out.println();
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		System.out.println(context.getData("fileName"));
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		System.out.println();
	}

	/**
	 * 将文件拆分入对账表 更改交易表中对账状态
	 */
	public void fileInsertTrspCheckTmp(Context context, String tChkNo)
			throws CoreException {
		logger.info("~~~~~~~~~~~Start  CheckTrspFile   fileInsertTrspCheckTmp");
		String ftpNo = "trspCheckFile";
		EupsThdFtpConfig eupsThdFtpConfig = get(EupsThdFtpConfigRepository.class).findOne(ftpNo);
		//获取文件
		eupsThdFtpConfig.setFtpDir("1");
		// String path=eupsThdFtpConfig.getLocDir();
		String fileName = context.getData("fileName").toString().trim();
		eupsThdFtpConfig.setLocFleNme(fileName);
		eupsThdFtpConfig.setRmtFleNme(fileName);
		eupsThdFtpConfig.setLocDir("/home/bbipadm/common/");
		eupsThdFtpConfig.setRmtWay("/home/bbipadm/common/");
		operateFTPAction.getFileFromFtp(eupsThdFtpConfig);

		// 文件解析入库
		List<Map<String, Object>> mapList = operateFileAction.pareseFile(eupsThdFtpConfig, "trspCheckFile");
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~`");
		System.out.println(mapList);
		
		if(CollectionUtils.isEmpty(mapList)){
					throw new CoreException(context.getData("startDate")+"~~"+context.getData("endDate")+"时段内，没有发生缴费");
		}
		GDEupsbTrspFeeInfo gdEupsbTrspFeeInfo = new GDEupsbTrspFeeInfo();
		gdEupsbTrspFeeInfo.setChkFlg("0");
		// 设置对账批次
		gdEupsbTrspFeeInfo.setTchkNo(tChkNo);
		for (Map<String, Object> map : mapList) {
			TrspCheckTmp trspCheckTmp = new TrspCheckTmp();

//			trspCheckTmp.setTxnDat(DateUtils.parse(
//					map.get("payDat").toString());
			trspCheckTmp.setTxnDat(DateUtils.parse(map.get("payDat").toString()));
			trspCheckTmp.setSqn(map.get("thdKey").toString());
			gdEupsbTrspFeeInfo.setThdKey(map.get("thdKey").toString());

			BigDecimal txnAmt = new BigDecimal(map.get("txnAmt").toString()).scaleByPowerOfTen(-2);
			trspCheckTmp.setTxnAmt(txnAmt);
			trspCheckTmp.setTchkNo(tChkNo);
			trspCheckTmp.setInvNo(map.get("invNo").toString());
			trspCheckTmp.setPayMon(map.get("payMon").toString());
			trspCheckTmp.setCarTyp(map.get("carTyp").toString());
			trspCheckTmp.setCarNo(map.get("carNo").toString());
			trspCheckTmp.setStatue(map.get("status").toString());

			// 更改对账状态
			gdEupsbTrspFeeInfoRepository.update(gdEupsbTrspFeeInfo);
			// 插入对账表
			trspCheckTmpRepository.insert(trspCheckTmp);
		}
		logger.info("~~~~~~~~~~~End  CheckTrspFile   fileInsertTrspCheckTmp");
	}

	/**
	 * 发送文件
	 */
	public void sendFile(Context context, String fileName) throws CoreException {
		logger.info("~~~~~~~~~~~Start  CheckTrspFile   printFile");
		// 获取FTP信息,发送文件到指定路径
		EupsThdFtpConfig eupsThdFtpConfig = eupsThdFtpConfigRepository.findOne("trspCheckFile");
		eupsThdFtpConfig.setFtpDir("0");
		eupsThdFtpConfig.setLocFleNme(fileName);
		eupsThdFtpConfig.setRmtFleNme("new" + fileName);
		operateFTPAction.putCheckFile(eupsThdFtpConfig);
	}

	/**
	 * 打印清单
	 */
	public void printDetail(Context context, String tChkNo,
			List<GDEupsbTrspFeeInfo> detailList) throws CoreException {
		logger.info("~~~~~~~~~~~Start  CheckTrspFile   printDetail");
		// 报表模式
		int i = Integer.parseInt(context.getData(GDParamKeys.JOURNAL_MODEL).toString());
		// context.setData("rptFil", rptFil);

		String rptFmt = "rptFmt";
		List<Map<String, Object>> list = gdEupsbTrspFeeInfoRepository.findSumForTxnAmt(tChkNo);
		// 得到总笔数 总金额
		if (CollectionUtils.isEmpty(list)) {
			context.setData(ParamKeys.RSP_CDE, "329999");
			context.setData(ParamKeys.RSP_MSG, "查询统计信息失败");
			throw new CoreException("查询统计信息失败");
		} else {
			int totCnt = Integer.parseInt(list.get(0).get("COUNT") + "");
			BigDecimal totAmt = new BigDecimal(list.get(0).get("SUMTXNAMT")
					+ "");
			context.setData(ParamKeys.TOT_CNT, totCnt);
			context.setData(ParamKeys.TOT_AMT, totAmt);
		}

		if (0 == i) { // ~~~~~~~~汇总方式
			rptFmt = rptFmt + 0;
		} else if (1 == i) {// ~~~~~~~~~~~~~清单方式
			rptFmt = rptFmt + 1;
		} else if (2 == i) {// ~~~~~~~~~~~~~更改发票清单
			rptFmt = rptFmt + 2;
		} else if (3 == i) {// ~~~~~~~~~~~~~未打印发票清单
			rptFmt = rptFmt + 3;
		} else {
			context.setData(GDParamKeys.MSGTYP, "E");
			context.setData(ParamKeys.RSP_CDE, "329999");
			context.setData(ParamKeys.RSP_MSG, "统计模式错误");
			throw new CoreException("统计模式错误");
		}
		String path = "config/report/zhTransport/" + rptFmt + ".vm";
		context.setData(rptFmt, path);
		// TODO 报表生成
		VelocityTemplatedReportRender render = new VelocityTemplatedReportRender();
		try {
			render.afterPropertiesSet();
		} catch (Exception e) {
			e.printStackTrace();
		}
		String rptFil = "Car"
				+ context.getData("tlr").toString()
				+ context.getData(GDParamKeys.START_DATE).toString()
						.substring(5) + ".dat";
		// 拼装文件
		Map<String, String> map = new HashMap<String, String>();
		map.put(rptFil, path);
		render.setReportNameTemplateLocationMapping(map);
		context.setData("eles", detailList);
		String result = render.renderAsString(rptFil, context);
		System.out
				.println("~~~~~~~~~~~~~result~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		System.out.println(result);
		// 文件路径
		StringBuffer rpFmts = new StringBuffer();
		rpFmts.append("F:\\");
		File file = new File(rptFil.toString());
		if (!file.exists()) {
			file.mkdirs();
		}
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(rpFmts
					.append(rptFil).toString());
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
					fileOutputStream, "GBK");
			BufferedWriter bufferedWriter = new BufferedWriter(
					outputStreamWriter);
			PrintWriter printWriter = new PrintWriter(bufferedWriter);
			printWriter.write(result);
			// 关闭
			printWriter.close();
			bufferedWriter.close();
			outputStreamWriter.close();
			fileOutputStream.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// TODO 没有确定下名字rptFil 还是 fileName
		// String fileName=context.getData("fileName").toString();
		System.out
				.println("~~~~~~~~~~~~~sendFile~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		// sendFile(context,rptFmt);
	}
}
