package com.bocom.bbip.gdeupsb.action.sign;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.gdeupsb.common.GDErrorCodes;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GdsRunCtl;
import com.bocom.bbip.gdeupsb.repository.GdsAgtWaterRepository;
import com.bocom.bbip.gdeupsb.repository.GdsRunCtlRepository;
import com.bocom.bbip.utils.CollectionUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

/**
 * 协议数据拷盘异常处理
 * 
 * @author qc.w
 * 
 */
public class AgtMdyRstUnsureDealAction extends BaseAction {

	@Override
	public void execute(Context context) throws CoreException, CoreRuntimeException {
		log.info("AgtMdyRstUnsureDealAction start!..");
		GdsRunCtlRepository gdsRunCtlRepository = get(GdsRunCtlRepository.class);
		GdsAgtWaterRepository gdsAgtWaterRepository = get(GdsAgtWaterRepository.class);

		String gdsBid = context.getData(GDParamKeys.SIGN_STATION_BID); // 业务类型

		// 验证该业务是否存在
		GdsRunCtl gdsRunCtl = gdsRunCtlRepository.findOne(gdsBid);
		if (null == gdsRunCtl) {
			throw new CoreException(GDErrorCodes.EUPS_SIGN_GDSBID_NOT_EXIST);
		}
		String agtSTb = gdsRunCtl.getAgtStb(); // 子表
		String batchId = context.getData("batchId"); // 批次号
		// 查询是否有该批次的数据
		Map<String, Object> inpara = new HashMap<String, Object>();
		inpara.put("agtSTb", agtSTb);
		inpara.put("batchId", batchId);
		List<Integer> batCntList = gdsAgtWaterRepository.findBatchCnt(inpara);
		if (CollectionUtils.isEmpty(batCntList)) {
			throw new CoreException(GDErrorCodes.EUPS_SIGN_NO_RECORD_FOUND);
		}
		else {
			int cnt = batCntList.get(0);
			if (0 == cnt) {
				throw new CoreException(GDErrorCodes.EUPS_SIGN_NO_RECORD_FOUND);
			}
			log.info("record found!");
		}
//		String fileName1 = batchId.substring(6, batchId.indexOf("."));
//		inpara.put("batchId", batchId);
		// 更新批次制盘标志
		gdsAgtWaterRepository.updateFleSndInf(inpara);
	}

}
