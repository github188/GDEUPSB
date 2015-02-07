package com.bocom.bbip.gdeupsb.strategy.elcgd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.eups.action.common.OperateFTPAction;
import com.bocom.bbip.eups.action.common.OperateFileAction;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.entity.EupsBatchConsoleInfo;
import com.bocom.bbip.eups.entity.EupsBatchInfoDetail;
import com.bocom.bbip.eups.entity.EupsThdFtpConfig;
import com.bocom.bbip.eups.repository.EupsBatchConsoleInfoRepository;
import com.bocom.bbip.eups.repository.EupsBatchInfoDetailRepository;
import com.bocom.bbip.eups.repository.EupsThdFtpConfigRepository;
import com.bocom.bbip.eups.spi.service.batch.AfterBatchAcpService;
import com.bocom.bbip.eups.spi.vo.AfterBatchAcpDomain;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

/**
 * 电力文件批扣-批扣结果处理
 * 
 * @author qc.w
 * 
 */
public class BatchAcpElecResultDealServiceAction implements AfterBatchAcpService {

	@Autowired
	EupsBatchConsoleInfoRepository eupsBatchConsoleInfoRepository;

	@Autowired
	EupsBatchInfoDetailRepository eupsBatchInfoDetailRepository;

	@Autowired
	EupsThdFtpConfigRepository eupsThdFtpConfigRepository;

	@Autowired
	OperateFileAction operateFile;

	@Autowired
	OperateFTPAction operateFTP;

	private final static Logger log = LoggerFactory.getLogger(BatchAcpElecResultDealServiceAction.class);

	@Override
	public Map<String, Object> afterBatchDeal(AfterBatchAcpDomain afterbatchacpdomain, Context context) throws CoreException {

		// 原ICS代码根据不同机构号，分别查找对应ftpid

		Map<String, Object> resultMap = new HashMap<String, Object>();
		// 生成返回头信息
		String batNo = (String) context.getData("batNo");
		log.info((new StringBuilder("========>>>>>>>>Acp Return BatNo:")).append(batNo).toString());
		EupsBatchConsoleInfo eupsBatchConsoleInfo = eupsBatchConsoleInfoRepository.findOne(batNo);
		Map<String, Object> resultMapHead = BeanUtils.toMap(eupsBatchConsoleInfo); // 供电局批号，使用RSV_FLD2字段，划款日期，使用SUB_DTE

		resultMap.put("header", resultMapHead);
		String orgFileName = eupsBatchConsoleInfo.getFleNme(); // TODO：文件名，待确认此处文件名是第三方发过来的文件名，不是生成的代收付的文件名

		// 生成返回明细信息
		List<Map<String, Object>> detailList = new ArrayList<Map<String, Object>>();
		EupsBatchInfoDetail eupsBatchInfoDetail = new EupsBatchInfoDetail();
		eupsBatchInfoDetail.setBatNo(batNo);
		List<EupsBatchInfoDetail> batchDetailList = eupsBatchInfoDetailRepository.find(eupsBatchInfoDetail);
		for (EupsBatchInfoDetail batchDetail : batchDetailList) {
			Map<String, Object> detailMap = new HashMap<String, Object>();
			detailMap.put("TCusId", batchDetail.getAgtSrvCusId());// 第三方客户标识

			detailMap.put("ActNo", batchDetail.getCusAc());// 账号

			detailMap.put("TxnAmt", batchDetail.getTxnAmt());// 交易金额

			String rmk2 = batchDetail.getRmk2();// 备注2格式：电费月份|区号|供电企业账号|原文件名

			String[] detail = rmk2.split("\\|");

			detailMap.put("SetDat", detail[0]);
			detailMap.put("RsFld2", detail[1]);
			detailMap.put("OldAct", detail[2]);

			// 响应代码：目前标准版批次明细表中没有返回码，只有返回状态及描述
			String sts = batchDetail.getSts(); // 状态
			if (Constants.TRADE_STATUS_SUCCESS.equals(sts)) {
				detailMap.put("RspFlg", "0000");
			}
			else {
				// TODO:待确认，目前生产有余额不足，帐号信息错误及其他信息错误三类。标准版拿不到返回码
				detailMap.put("RspFlg", "0009");
			}
			detailList.add(detailMap);
		}
		resultMap.put("detail", detailList);

		EupsThdFtpConfig eupsThdFtpConfig = eupsThdFtpConfigRepository.findOne("eleGzBatchResult");

		String fileName = "1001" + orgFileName.substring(4, 26) + ".cb";

		// 生成文件
		operateFile.createCheckFile(eupsThdFtpConfig, "eleGzBatchResult", fileName, resultMap);

		// 将生成的文件上传至指定服务器
		eupsThdFtpConfig.setLocFleNme(fileName);
		eupsThdFtpConfig.setRmtFleNme(fileName);
		operateFTP.putCheckFile(eupsThdFtpConfig);

		return null;
	}

}
