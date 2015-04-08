package com.bocom.bbip.gdeupsb.strategy.sign;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.comp.btp.BTPService;
import com.bocom.bbip.eups.action.common.OperateFileAction;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsThdFtpConfig;
import com.bocom.bbip.eups.repository.EupsThdFtpConfigRepository;
import com.bocom.bbip.gdeupsb.common.GDErrorCodes;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GdsRunCtl;
import com.bocom.bbip.gdeupsb.expand.AgtFileSendImlService;
import com.bocom.bbip.gdeupsb.repository.GdsAgtWaterRepository;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

/**
 * 协议数据拷盘-珠江数码
 * 
 * @author qc.w
 * 
 */
public class DigitAgtFileSendImlAction implements AgtFileSendImlService {

	private final static Logger log = LoggerFactory.getLogger(DigitAgtFileSendImlAction.class);

	@Autowired
	BBIPPublicService bbipPublicService;

	@Autowired
	BTPService btpService;

	@Autowired
	GdsAgtWaterRepository gdsAgtWaterRepository;

	@Autowired
	EupsThdFtpConfigRepository eupsThdFtpConfigRepository;

	@Autowired
	OperateFileAction operateFileAction;

	@Override
	public Map<String, Object> agtFleSndDelService(Context context) throws CoreException {
		log.info("珠江数码协议数据拷盘开始！..");

		// 获取runctl
		GdsRunCtl gdsRunCtl = context.getVariable(GDParamKeys.SIGN_STATION_RUN_CTL_INFO);
		String agtMtb = gdsRunCtl.getAgtMtb(); // 主表
		String agtSTb = gdsRunCtl.getAgtStb(); // 子表
		String gdsBId = gdsRunCtl.getGdsBid(); // 业务类型
		String begDat = context.getData("begDat"); // 起始日期
		String endDat = context.getData("endDat"); // 结束日期

		String batNo = btpService.applyBatchNo("TPO"); // 申请批次号
		batNo = batNo.substring(batNo.length() - 2, batNo.length());

		String tSeqNo = batNo;

		String actDat = bbipPublicService.getAcDateAsString();

		String fileName = context.getData("filNam");
		// 删除原有文件，此处若生成则自动复写，不需要删除
		// String isExport = "N";

		Map<String, Object> inpara = new HashMap<String, Object>();
		inpara.put("actDat", actDat);
		inpara.put("tSeqNo", tSeqNo);
		inpara.put("agtMTb", agtMtb);
		inpara.put("agtSTb", agtSTb);
		inpara.put("gdsBId", gdsBId);
		inpara.put("begDat", begDat);
		inpara.put("endDat", endDat);

		// 查找拷盘数据(珠江数码与广州有线查询语句一样)
		List<Map<String, Object>> fleSndList = gdsAgtWaterRepository.findFileSndInfoTel(inpara);
		for (Map<String, Object> fleSndMap : fleSndList) {
			fleSndMap.put("filNam", fileName.substring(6, fileName.indexOf(".")));
			fleSndMap.put("busNam", context.getData("busNam"));
			fleSndMap.put("bnkDes", "交通银行");
			fleSndMap.put("brNo", context.getData(ParamKeys.BK)); // 分行号
			fleSndMap.put("nodNo", context.getData(ParamKeys.BR)); // 机构号
			fleSndMap.put("tlrId", context.getData(ParamKeys.TELLER)); // 柜员号
		}
		log.info("查找的返盘信息为:" + fleSndList);
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("detail", fleSndList);

		EupsThdFtpConfig eupsThdFtpConfig = eupsThdFtpConfigRepository.findOne("digAgtFileSnd");
		eupsThdFtpConfig.setLocFleNme(fileName);
		eupsThdFtpConfig.setRmtFleNme(fileName);

		operateFileAction.createCheckFile(eupsThdFtpConfig, "datFleSndTel", fileName, resultMap);

		// 更新协议的批次号与制盘标志：UpdAgtBatchId
		String fileName1 = fileName.substring(6, fileName.indexOf("."));
		inpara.put("batchId", fileName1);
		inpara.put("usbFlg", "N");

		gdsAgtWaterRepository.updateBchUsbFlg(inpara);
		if (0 == fleSndList.size()) {
			throw new CoreException(GDErrorCodes.EUPS_SIGN_NO_RECORD_FOUND);
		}
		context.setData("filNam", fileName.substring(6, fileName.indexOf(".")));
		log.info("珠江数码协议数据拷盘处理结束!..");
		return null;
	}
}
