package com.bocom.bbip.gdeupsb.strategy.sign;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.action.common.OperateFileAction;
import com.bocom.bbip.eups.entity.EupsThdFtpConfig;
import com.bocom.bbip.eups.repository.EupsThdFtpConfigRepository;
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
		eupsThdFtpConfig.setLocFleNme(filNam);

		List<Map<String, Object>> watrFileList = operateFileAction.pareseFile(eupsThdFtpConfig, "waterAgtUpdFmt");
		String gdsAId = new String(); // 协议号
		for (int i = 0; i < watrFileList.size(); i++) {
			Map<String, Object> wtrDtlMap = watrFileList.get(i);

			batchId = (String) wtrDtlMap.get("filNam");
			String result = (String) wtrDtlMap.get("result");
			String tAgtSt = new String();
			String updAll = new String();
			String errFlg = new String(); // 错误标志
			String rspCod = new String(); // 返回码

			if (GDConstants.SIGN_STATION_THIRD_CHECK_SUC.equals(result)) {
				tAgtSt = "S";
			} else {
				tAgtSt = "F";
			}
			// 水务的签约每次最多可以签两个协议，对公的是两个协议，对私的是一个协议
			Map<String, Object> agtSbinMap = new HashMap<String, Object>();
			agtSbinMap.put("agtStb", gdsRunCtl.getAgtStb()); // 子表
			agtSbinMap.put("tAgtSt", tAgtSt); // 状态
			agtSbinMap.put("terMsg", wtrDtlMap.get("errMsg")); // 错误码
			agtSbinMap.put("gdsBId", gdsRunCtl.getGdsBid()); // 业务种类
			agtSbinMap.put("actNo", wtrDtlMap.get("actNo")); // 帐号

			String gdsAId1 = (String) wtrDtlMap.get("gdsAId1");
			if (StringUtils.isNotEmpty(gdsAId1)) {
				gdsAId = gdsAId1;
				// 更新第一个协议号
				agtSbinMap.put("gdsAId", gdsAId); // 协议号
				try {
					gdsAgtWaterRepository.updateAgtWtrDelStsAll(agtSbinMap);
				} catch (Exception e) {
					errFlg = "1";
					rspCod = "GDS999";
					log.info("文件【" + filNam + "】第【" + i + 1 + "】行更新协议【" + gdsAId + "】状态失败");
				}
				updAll = "0";
			}
			if (StringUtils.isNotEmpty(gdsAId1)) {
				String gdsAId2 = (String) wtrDtlMap.get("gdsAId2");
				gdsAId = gdsAId2;
				// 更新第二个协议号(协议号非空才更新)
				agtSbinMap.put("gdsAId", gdsAId); // 协议号
				try {
					gdsAgtWaterRepository.updateAgtWtrDelSts(agtSbinMap);
				} catch (Exception e) {
					errFlg = "1";
					rspCod = "GDS999";
					log.info("文件【" + filNam + "】第【" + i + 1 + "】行更新协议【" + gdsAId + "】状态失败");
				}
				updAll = "0";
			}
			// 若updAll标志为1则更新两个协议
			if ("1".equals(updAll)) {
				try {
					// UpdAgt44101ResultAll，更新所有协议信息
					gdsAgtWaterRepository.updateAgtWtrDelStsAll(agtSbinMap);
				} catch (Exception e) {
					errFlg = "1";
					rspCod = "GDS999";
					log.info("文件【" + filNam + "】第【" + i + 1 + "】行更新协议【全部】状态失败");
				}
			}
			recCnt++;
		}

		// 如果批次号不为空，即本次有数据，则需要判断本批是否都回应了
		if (StringUtils.isNotEmpty(batchId)) {
			// 统计批次数量 statAgt44101Batch
			Map<String,Object> inpara=new HashMap<String, Object>();
			inpara.put("batchId", batchId);
			inpara.put("agtStb", gdsRunCtl.getAgtStb());
			List<Integer> cntList = gdsAgtWaterRepository.findBatchAgtCntAll(inpara);
			int tolCnt = cntList.get(0);

			String unusbMsg = new String();
			// 若总数和文件中处理的数目不同说明有不同的数据
			if (recCnt != tolCnt) {
				// 查询本批回盘标识还是Y的数据qryAgt44101Usbflg
				List<String> actList = new ArrayList<String>();
				try {
					Map<String,Object> usbFlgPara=new HashMap<String,Object>();
					usbFlgPara.put("batchId", batchId);
					usbFlgPara.put("agtStb", gdsRunCtl.getAgtStb());
					actList = gdsAgtWaterRepository.findActInfUsbFlg(usbFlgPara);
				} catch (Exception e) {
					Result ret1 = bbipPublicService.unlock(gdsRunCtl.getGdsBid());
					int status1 = ret1.getStatus();
					if (status1 != 0) {
						throw new CoreException(GDErrorCodes.EUPS_UNLOCK_FAIL, "交易解锁失败!!!");
					}
					throw new CoreException(GDErrorCodes.EUPS_SIGN_DEFAULT_ERROR, "游标定义失败!!!");
				}

				for (String actNo : actList) {
					unusbMsg = unusbMsg + actNo + '\n';
				}
				rstMsg = "本次导入的回盘批次[" + batchId + "]应有[" + tolCnt + "]条数据，实际上导入[" + recCnt + "]条记录，以下签约账号未有回盘信息:" + "'\n" +
						unusbMsg + "'\n" + "请跟踪以上未回盘的账号信息.";
			}

			Map<String, Object> inparaMap = new HashMap<String, Object>();
			inparaMap.put("agtStb", gdsRunCtl.getAgtStb());
			inparaMap.put("gdsBId", gdsRunCtl.getGdsBid());
			inparaMap.put("batchId", batchId);

			// 更新本批次为可制盘 UpdAgt44101UsbFlg
			gdsAgtWaterRepository.updateBatchUsbFlg(inparaMap);
		}
		context.setData("rstMsg", rstMsg);

		return null;
	}
}
