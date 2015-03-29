package com.bocom.bbip.gdeupsb.action.sign;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bocom.bbip.data.domain.Page;
import com.bocom.bbip.data.domain.PageRequest;
import com.bocom.bbip.data.domain.Pageable;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDErrorCodes;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GdsRunCtl;
import com.bocom.bbip.gdeupsb.repository.GdsAgtInfRepository;
import com.bocom.bbip.gdeupsb.repository.GdsAgtWaterRepository;
import com.bocom.bbip.gdeupsb.repository.GdsRunCtlRepository;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

/**
 * 签约一站通-协议浏览或打印
 * 
 * @author qc.w
 * 
 */
public class AgtScanPrintAction extends BaseAction {

	@Override
	public void execute(Context context) throws CoreException, CoreRuntimeException {
		log.info("AgtScanPrintAction start!..");

		GdsAgtInfRepository gdsAgtInfRepository = get(GdsAgtInfRepository.class);
		GdsRunCtlRepository gdsRunCtlRepository = get(GdsRunCtlRepository.class);

		String gdsBId = context.getData(GDParamKeys.SIGN_STATION_BID); // 业务标识
		String actNo = context.getData(ParamKeys.ACT_NO); // 帐号
		String agtDat = context.getData(GDParamKeys.SIGN_STATION_AGT_DAT); // 日期
		if (StringUtils.isEmpty(agtDat)) {
			agtDat = null;
		}

		// 44101时，拼装动态sql
		String extSql = new String();
		if ("44101".equals(gdsBId)) {
			extSql = ",AreaId AreaId,BatchId batchId,usbflg UsbFlg ";
		}

		// 不同的功能选择
		String func = context.getData(GDParamKeys.SIGN_STATION_FUNC);
		if (GDConstants.SIGN_STATION_FUNC_ALL.equals(func)) { // 汇总

			Map<String, Object> inpara = new HashMap<String, Object>();
			inpara.put("actNo", actNo);
			inpara.put("gdsBId", gdsBId);

			// 多页查询汇总信息
			Pageable pageable = BeanUtils.toObject(context.getDataMap(), PageRequest.class);
			Page<Map<String, Object>> result = gdsAgtInfRepository.findAgentTotInf(pageable, inpara);
			log.info("汇总信息查询结果=" + result);
			if (result.getTotalElements() == 0) {
				log.error("agt total info query is null!");
				throw new CoreException(ErrorCodes.EUPS_CONSOLE_INFO_NOTEXIST); // 查无记录
			}
			context.setData(ParamKeys.TOTAL_ELEMETS, result.getTotalElements());
			context.setData(ParamKeys.TOTAL_PAGES, result.getTotalPages());

			List<Map<String, Object>> etllist = result.getElements();
			context.setData(GDParamKeys.SIGN_STATION_AGT_TOT_LIST, etllist); // 查询汇总返回信息

		} else if (GDConstants.SIGN_STATION_FUNC_DETAIL.equals(func)) { // 明细
			// 查找代理业务控制表信息
			GdsRunCtl gdsRunCtlInf = gdsRunCtlRepository.findOne(gdsBId);
			if (null == gdsRunCtlInf) {
				throw new CoreException(GDErrorCodes.EUPS_SIGN_GDSBID_NOT_EXIST);
			}
			String agtSTb = gdsRunCtlInf.getAgtStb(); // 获得子表名称

			// 多页查询-协议签订明细
			Map<String, Object> inputMap = new HashMap<String, Object>();
			inputMap.put("extSql", extSql);
			inputMap.put("agtSTb", agtSTb);
			inputMap.put("gdsBId", gdsBId);
			inputMap.put("actNo", actNo);
			inputMap.put("effDat", agtDat);
			inputMap.put("ivdDat", agtDat);

			List<Map<String, Object>> signDetail = get(GdsAgtWaterRepository.class).findSignDeatilInfo(inputMap); // 查询明细
			log.info("明细信息查询结果=" + signDetail);
			context.setData("signDetail", signDetail);

		} else if (GDConstants.SIGN_STATION_FUNC_PRINT.equals(func)) { // 打印

			List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();

			// 多页查询-按卡号查询协议信息

			// 先查询该卡签订了业务类型，再查询这些业务类型对应的子表，最后查询每一个子表的协议明细
			// List<Map<String, Object>> agtSTb = new ArrayList<Map<String,
			// Object>>(); // 先查询该卡签订了哪些业务种类

			Map<String, Object> cardBIdIn = new HashMap<String, Object>();
			cardBIdIn.put("actNo", actNo); // 卡号

			List<Map<String, Object>> cardBIdList = gdsRunCtlRepository.findSignCardBId(cardBIdIn); // 业务种类列表
			for (Map<String, Object> cardBidMap : cardBIdList) {
				String gdsBid = (String) cardBidMap.get("GDSBID"); // 业务种类
				String agtStb = (String) cardBidMap.get("AGTSTB"); // 签订子表
				// 拼装 extSql
				cardBidMap.put("extSql", extSql);
				// 根据业务种类，卡号，分别查询对应子表下的协议明细信息
				Pageable pageable = BeanUtils.toObject(context.getDataMap(), PageRequest.class);
				Page<Map<String, Object>> result = get(GdsAgtWaterRepository.class).findSignDeatilList(pageable, cardBidMap);
				// 多页查询汇总信息
				if (result.getTotalElements() == 0) {
					log.error("agt detail info query is null!");
					throw new CoreException(ErrorCodes.EUPS_CONSOLE_INFO_NOTEXIST); // 查无记录
				}
				context.setData(ParamKeys.TOTAL_ELEMETS, result.getTotalElements());
				context.setData(ParamKeys.TOTAL_PAGES, result.getTotalPages());

				// TODO:根据机构号查询机构名称：
				// select OrgNam from CbsOrgAcd where OrgCod='%s' fetch first 1
				// rows only

				List<Map<String, Object>> etllist = result.getElements();
				for (Map<String, Object> etlDetail : etllist) {
					resultList.add(etlDetail);
				}

			}
			context.setData(GDParamKeys.SIGN_STATION_AGT_DETAIL_LIST, resultList); // 明细信息返回

		}

	}
}
