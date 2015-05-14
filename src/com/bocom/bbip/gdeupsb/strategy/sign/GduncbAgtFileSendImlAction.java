package com.bocom.bbip.gdeupsb.strategy.sign;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.comp.btp.BTPService;
import com.bocom.bbip.gdeupsb.common.GDErrorCodes;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GdsRunCtl;
import com.bocom.bbip.gdeupsb.expand.AgtFileSendImlService;
import com.bocom.bbip.gdeupsb.repository.GdsAgtWaterRepository;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

public class GduncbAgtFileSendImlAction implements AgtFileSendImlService {

	private final static Logger log = LoggerFactory.getLogger(GduncbAgtFileSendImlAction.class);

	@Autowired
	BBIPPublicService bbipPublicService;

	@Autowired
	BTPService btpService;

	@Autowired
	GdsAgtWaterRepository gdsAgtWaterRepository;

	@Override
	public Map<String, Object> agtFleSndDelService(Context context) throws CoreException {
		log.info("WaterAgtFileSendImlAction agtFleSndByGdsId start!..");

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

		String fileName = "JH" + actDat + tSeqNo;
		// 删除原有文件，此处若生成则自动复写，不需要删除
		String isExport = "N";

		Map<String, Object> inpara = new HashMap<String, Object>();
		inpara.put("actDat", actDat);
		inpara.put("tSeqNo", tSeqNo);
		inpara.put("agtMTb", agtMtb);
		inpara.put("agtSTb", agtSTb);
		inpara.put("gdsBId", gdsBId);
		inpara.put("begDat", begDat);
		inpara.put("endDat", endDat);

		// 查找拷盘数据
		List<Map<String, Object>> fleSndList = gdsAgtWaterRepository.findFileSndInfo(inpara);

		// TODO:生成返盘文件
		while (true) {
			break;
		}

		if ("N".equals(isExport)) {
			// 更新协议的批次号与制盘标志：UpdAgtBatchId
			inpara.put("batchId", batNo);
			inpara.put("usbFlg", "N");

			gdsAgtWaterRepository.updateBchUsbFlg(inpara);
		}
		if (0 == fleSndList.size()) {
			throw new CoreException(GDErrorCodes.EUPS_SIGN_NO_RECORD_FOUND);
		}
		log.info("WaterAgtFileSendImlAction end!..");
		return null;
	}
}
