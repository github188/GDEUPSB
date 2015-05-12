package com.bocom.bbip.gdeupsb.strategy.sign;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.action.common.OperateFileAction;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.entity.EupsThdFtpConfig;
import com.bocom.bbip.eups.repository.EupsThdFtpConfigRepository;
import com.bocom.bbip.file.MftpTransfer;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDErrorCodes;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GdsAgtWater;
import com.bocom.bbip.gdeupsb.entity.GdsRunCtl;
import com.bocom.bbip.gdeupsb.expand.AgtDataUpdImlService;
import com.bocom.bbip.gdeupsb.repository.GdsAgtWaterRepository;
import com.bocom.bbip.service.Result;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

/**
 * 协议更新-珠江数码结果文件流程
 * 
 * @author qc.w
 * 
 */
public class DegDateUpdImlAction implements AgtDataUpdImlService {

	private final static Logger log = LoggerFactory.getLogger(DegDateUpdImlAction.class);

	@Autowired
	OperateFileAction operateFileAction;

	@Autowired
	EupsThdFtpConfigRepository eupsThdFtpConfigRepository;

	@Autowired
	GdsAgtWaterRepository gdsAgtWaterRepository;

	@Autowired
	BBIPPublicService bbipPublicService;

	@Override
	public Map<String, Object> agtDateUpdService(Context context) throws CoreException {
		log.info("water agtDateUpdService start!..");

		GdsRunCtl gdsRunCtl = context.getVariable(GDParamKeys.SIGN_STATION_RUN_CTL_INFO);

		String rstMsg = new String(); // 返回信息
		String filNam = context.getData("filNam"); // 结果文件
		int recCnt = 0;
		String batchId = null;

		// 按照RCV44101 读取广州自来水处理结果文件
		EupsThdFtpConfig eupsThdFtpConfig = eupsThdFtpConfigRepository.findOne("watrAgtResult");
		
		try {			
			bbipPublicService.getFileFromBBOS(new File(eupsThdFtpConfig.getLocDir(),filNam), filNam, MftpTransfer.FTYPE_NORMAL);			
		}catch (Exception e) {
			throw new CoreException(ErrorCodes.EUPS_MFTP_FILEDOWN_FAIL);
		}
		
		eupsThdFtpConfig.setLocFleNme(filNam);

		List<Map<String, Object>> watrFileList = operateFileAction.pareseFile(eupsThdFtpConfig, "degAgtUpdFmt");

		log.info("watrFileList=" + watrFileList);
		String gdsAId = new String(); // 协议号
		for (int i = 0; i < watrFileList.size(); i++) {
			Map<String, Object> wtrDtlMap = watrFileList.get(i);

			batchId = (String) wtrDtlMap.get("filNam");
			String result = (String) wtrDtlMap.get("result");
			String tAgtSt = new String();
			String updAll = new String();
			String errFlg = new String(); // 错误标志
			String rspCod = new String(); // 返回码

			log.info("当前的结果文件中，处理结果为:[" + result + "]");
			if (GDConstants.SIGN_STATION_THIRD_CHECK_SUC.equals(result)) {
				tAgtSt = "S";
			} else {
				tAgtSt = "F";
			}
		
			Map<String, Object> agtSbinMap = new HashMap<String, Object>();
			agtSbinMap.put("agtStb", gdsRunCtl.getAgtStb()); // 子表
			agtSbinMap.put("tAgtSt", tAgtSt); // 状态
			agtSbinMap.put("terMsg", wtrDtlMap.get("errMsg")); // 错误码
			agtSbinMap.put("gdsBId", gdsRunCtl.getGdsBid()); // 业务种类
			agtSbinMap.put("actNo", wtrDtlMap.get("actNo")); // 帐号
			agtSbinMap.put("tCusId", wtrDtlMap.get("tCusId")); // 客户标志
			
			
			String gdsAId1 = (String) wtrDtlMap.get("gdsAId1");

			log.info("agtSbinMap=" + agtSbinMap);

			// 错误信息条件设置
			Map<String, Object> inparaMap = new HashMap<String, Object>();
			inparaMap.put("agtStb", gdsRunCtl.getAgtStb());
			inparaMap.put("gdsBId", gdsRunCtl.getGdsBid());
			inparaMap.put("batchId", batchId);

			// TODO:原ics代码中，需要判断gdsAId1是否为空，但是0505日拿到的返回结果中该字段为空，待确认！！。。
			// if (StringUtils.isNotEmpty(gdsAId1)) {
			log.info("gdsAId1 is not empty!..gdsAId1=[" + gdsAId1 + "]");
			
				gdsAId = gdsAId1;
				// 更新第一个协议号
				agtSbinMap.put("gdsAId", gdsAId); // 协议号
				try {
					log.info("开始处理更新状态1..");
					gdsAgtWaterRepository.updateAgtDegDelSts(agtSbinMap);
				} catch (Exception e) {
					// 更新本批次为可制盘 UpdAgt44101UsbFlg
					
					inparaMap.put("tCusId", wtrDtlMap.get("tCusId"));
					inparaMap.put("actNo", wtrDtlMap.get("actNo"));
					
					updBatUsbFlg(inparaMap);
					
					errFlg = "1";
					rspCod = "GDS999";
					log.info("文件【" + filNam + "】第【" + i + 1 + "】行更新协议【" + gdsAId + "】状态失败");
				}
				updAll = "0";
			recCnt++;
		}

		context.setData("rstMsg", rstMsg);

		return null;
	}

	private void updBatUsbFlg(Map<String, Object> inparaMap) {
		log.info("更新制盘标志为可制盘！..当前的map为:[" + inparaMap + "]");
		gdsAgtWaterRepository.updateBatchUsbFlgTel(inparaMap);
	}

}
