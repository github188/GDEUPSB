package com.bocom.bbip.gdeupsb.strategy.sign;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.comp.btp.BTPService;
import com.bocom.bbip.eups.action.common.OperateFTPAction;
import com.bocom.bbip.eups.action.common.OperateFileAction;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsThdFtpConfig;
import com.bocom.bbip.eups.repository.EupsThdFtpConfigRepository;
import com.bocom.bbip.file.MftpTransfer;
import com.bocom.bbip.gdeupsb.common.GDErrorCodes;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GdsRunCtl;
import com.bocom.bbip.gdeupsb.expand.AgtFileSendImlService;
import com.bocom.bbip.gdeupsb.repository.GdsAgtWaterRepository;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

public class WaterAgtFileSendImlAction implements AgtFileSendImlService {

	private final static Logger log = LoggerFactory.getLogger(WaterAgtFileSendImlAction.class);

	@Autowired
	EupsThdFtpConfigRepository eupsThdFtpConfigRepository;

	@Autowired
	BBIPPublicService bbipPublicService;

	@Autowired
	BTPService btpService;

	@Autowired
	GdsAgtWaterRepository gdsAgtWaterRepository;

	@Autowired
	OperateFileAction operateFileAction;

	@Autowired
	OperateFTPAction operateFTPAction;

	@Override
	public Map<String, Object> agtFleSndDelService(Context context) throws CoreException {
		log.info("水务协议数据拷盘开始！..");

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

		batNo = actDat + tSeqNo;

		String fileName = "JH" + batNo;
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

		List<Map<String, Object>> fileList = new ArrayList<Map<String, Object>>();
		String otcusId = new String();
		String tmpDat = new String();
		// 查找拷盘数据
		List<Map<String, Object>> fleSndList = gdsAgtWaterRepository.findFileSndInfo(inpara);
		for (Map<String, Object> fileMap : fleSndList) {
			// 获取客户标志，生成tmpDat
			String tcusId = (String) fileMap.get("TCUSID");
			String gdsAid = (String) fileMap.get("GDSAID"); // gdsAid
			// if (!tcusId.equals(otcusId)) {
			//
			// }
			// else {
			// tmpDat = gdsAid;
			// }

			if (tcusId.equals(otcusId)) {
				tmpDat = gdsAid;
			}
			otcusId = tcusId;
			fileMap.put("tmp1", "301581000019");
			fileMap.put("tmp2", "交通银行广东省分行");
			fileMap.put("tmp3", "1");
			fileMap.put("tmpDat", tmpDat);
			fileMap.put("BNKDAT", DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMdd)); // 自然日
			fileList.add(fileMap);
		}
		if (0 == fleSndList.size()) {
			throw new CoreException(GDErrorCodes.EUPS_SIGN_NO_RECORD_FOUND);
		}

		log.info("经过处理过后，fileList=" + fileList);
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("detail", fileList);

		// 按照RCV44101 读取广州自来水处理结果文件
		EupsThdFtpConfig eupsThdFtpConfig = eupsThdFtpConfigRepository.findOne("watAgtFileSnd");
		eupsThdFtpConfig.setLocFleNme(fileName);
		eupsThdFtpConfig.setRmtFleNme(fileName);
		operateFileAction.createCheckFile(eupsThdFtpConfig, "datFleSndWat", fileName, resultMap);

//		String br = context.getData(ParamKeys.BR);// 机构号
//		String tlr = context.getData(ParamKeys.TELLER); // 柜员号
//
//		// 设置ftp参数
//		EupsThdFtpConfig ftpConf = eupsThdFtpConfigRepository.findOne("frtUsbWrtSgnFilSnd");
//
//		String rmtWay = "/home/weblogic/JumpServer/WEB-INF/save/tfiles/" + br + "/" + tlr + "/";
//		ftpConf.setRmtWay(rmtWay);
//		ftpConf.setLocFleNme(fileName);
//		ftpConf.setRmtFleNme(fileName);
//		operateFTPAction.putCheckFile(ftpConf);
//		log.info("将文件存放到服务器上，服务器上的文件目录为:[" + rmtWay + "],文件名为:[" + fileName + "]" + ",本地的文件目录为:[" + ftpConf.getLocDir() + "],本地文件名为:[" + fileName
//				+ "]");
		try {
			bbipPublicService.sendFileToBBOS(new File(eupsThdFtpConfig.getLocDir(),fileName), fileName, MftpTransfer.FTYPE_NORMAL);			
		}catch (Exception e) {
			throw new CoreException(ErrorCodes.EUPS_MFTP_FILEPUT_FAIL);
		}
		

		if ("N".equals(isExport)) {
			// 更新协议的批次号与制盘标志：UpdAgtBatchId
			inpara.put("batchId", fileName);
			inpara.put("usbFlg", "N");

			gdsAgtWaterRepository.updateBchUsbFlg(inpara);
		}

		// 设置返回的批次号，文件名：
		context.setData("filNam", fileName);
		context.setData("batchId", fileName);

		log.info("WaterAgtFileSendImlAction end!..");
		return null;
	}
}
