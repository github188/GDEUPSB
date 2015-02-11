package com.bocom.bbip.gdeupsb.action.fbpe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.eups.action.common.OperateFTPAction;
import com.bocom.bbip.eups.action.common.OperateFileAction;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsThdFtpConfig;
import com.bocom.bbip.eups.repository.EupsThdFtpConfigRepository;
import com.bocom.bbip.eups.spi.service.batch.AfterBatchAcpService;
import com.bocom.bbip.eups.spi.vo.AfterBatchAcpDomain;
import com.bocom.bbip.gdeupsb.entity.GDEupsBatchConsoleInfo;
import com.bocom.bbip.gdeupsb.entity.GdFbpeFileBatchTmp;
import com.bocom.bbip.gdeupsb.repository.GDEupsBatchConsoleInfoRepository;
import com.bocom.bbip.gdeupsb.repository.GdFbpeFileBatchTmpRepository;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

/**
 * 电力文件批扣-批扣结果处理
 * @version 1.0.0
 * @author Guilin.Li
 * Date 2015-02-12
 */
public class BatchFbpeResultDealAction implements AfterBatchAcpService {

	@Autowired
	GDEupsBatchConsoleInfoRepository eupsBatchConsoleInfoRepository;

	@Autowired
	GdFbpeFileBatchTmpRepository fileRepository;

	@Autowired
	EupsThdFtpConfigRepository eupsThdFtpConfigRepository;

	@Autowired
	OperateFileAction operateFile;

	@Autowired
	OperateFTPAction operateFTP;

	private final static Logger log = LoggerFactory.getLogger(BatchFbpeResultDealAction.class);

	@Override
	public Map<String, Object> afterBatchDeal(AfterBatchAcpDomain afterbatchacpdomain, Context context) throws CoreException {
	    log.info("BatchFbpeResultDealAction Start! ");

		Map<String, Object> resultMap = new HashMap<String, Object>();
		// 生成返回头信息
		String batNo = (String) context.getData("dskNo");
		GDEupsBatchConsoleInfo eupsBatchConsoleInfo = eupsBatchConsoleInfoRepository.findOne(batNo);
		Map<String, Object> resultMapHead = BeanUtils.toMap(eupsBatchConsoleInfo); // 供电局批号，使用RSV_FLD2字段，划款日期，使用SUB_DTE

		resultMap.put(ParamKeys.EUPS_FILE_HEADER, resultMapHead);
		String orgFileName = eupsBatchConsoleInfo.getFleNme(); // 
		// 生成返回明细信息
		List<Map<String, Object>> detailList = new ArrayList<Map<String, Object>>();
		GdFbpeFileBatchTmp fileBatchTmp = new GdFbpeFileBatchTmp();
		fileBatchTmp.setRsvFld8(batNo);
		List<GdFbpeFileBatchTmp> batchDetailList = fileRepository.find(fileBatchTmp);
		for (GdFbpeFileBatchTmp batchDetail : batchDetailList) {
			Map<String, Object> detailMap = new HashMap<String, Object>();
			detailMap.put("TCusId", batchDetail.getCusNo());// 第三方客户标识

		
		/* // 响应代码：目前标准版批次明细表中没有返回码，只有返回状态及描述
			String sts = batchDetail.getSts(); // 状态
			if (Constants.TRADE_STATUS_SUCCESS.equals(sts)) {
				detailMap.put("RspFlg", "0000");
			}
			else {
				// TODO:待确认，目前生产有余额不足，帐号信息错误及其他信息错误三类。标准版拿不到返回码
				detailMap.put("RspFlg", "0009");
			}*/
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
