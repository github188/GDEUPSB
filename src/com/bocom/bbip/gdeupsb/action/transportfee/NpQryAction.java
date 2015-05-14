package com.bocom.bbip.gdeupsb.action.transportfee;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.data.domain.Page;
import com.bocom.bbip.data.domain.PageRequest;
import com.bocom.bbip.data.domain.Pageable;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GDEupsbTrspNpManag;
import com.bocom.bbip.gdeupsb.repository.GDEupsbTrspNpManagRepository;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class NpQryAction extends BaseAction {
	private final static Log log = LogFactory.getLog(NpQryAction.class);

	@Autowired
	GDEupsbTrspNpManagRepository gdEupsbTrspNpManagRepository;

	public void execute(Context ctx) throws CoreException, CoreRuntimeException {
		log.info("NpQryAction start......");
//---------------------------------------------------------------------------------------------
		// 统计总数,成功或失败
		GDEupsbTrspNpManag gdEupsbTrspNpManag = new GDEupsbTrspNpManag();
		gdEupsbTrspNpManag.setBegDat((Date) ctx.getData(GDParamKeys.BEG_DAT));
		gdEupsbTrspNpManag.setEndDat((Date) ctx.getData(GDParamKeys.END_DAT));
		gdEupsbTrspNpManag.setNodNo((String)ctx.getData(GDParamKeys.NOD_NO));
		gdEupsbTrspNpManag.setStatus(ctx.getData(GDParamKeys.STATUS).toString());
		gdEupsbTrspNpManag.setIdNo(ctx.getData("idNo").toString());
		gdEupsbTrspNpManag.setCarNo(ctx.getData(GDParamKeys.CAR_NO).toString());
		List<Integer> tolNum = gdEupsbTrspNpManagRepository.findCountSum(gdEupsbTrspNpManag);
		if (0 == tolNum.get(0)) {
			ctx.setData(ParamKeys.RSP_MSG, "无记录");
		} else {
			ctx.setData(ParamKeys.RSP_MSG, "交易成功");
		}

		Pageable pageable = BeanUtils.toObject(ctx.getDataMap(), PageRequest.class);
		Page<GDEupsbTrspNpManag> TrspNpManagPage = get(GDEupsbTrspNpManagRepository.class).findNpInfo(pageable, gdEupsbTrspNpManag);
		setResponseFromPage(ctx, "npResultList", TrspNpManagPage);
//		
//		--------------------------------------------------------------------------------------
//		List<Map<String, Object>>  npResultList = new ArrayList<Map<String,Object>>();
//		Map<String, Object> map = new HashMap<String, Object>();
//		map.put("nodNo", "1243");
//		map.put("carNo", "1243");
//		map.put("sqn", "1243");
//		map.put("invNo", "1243");
//		map.put("idNo", "1243");
//		map.put("status", "1243");
//		map.put("tlrId", "1243");
//		npResultList.add(map);
//		ctx.setData("npResultList", npResultList);
//		ctx.setData("ttl", "0");
//		ctx.setData("subTtl", "0");
		
		
//		Map<String, Object> inpara = new HashMap<String, Object>();
//		inpara.put("begDat", (Date) ctx.getData(GDParamKeys.BEG_DAT));
//		inpara.put("endDat", (Date) ctx.getData(GDParamKeys.END_DAT));
//		inpara.put("nodNo", ctx.getData(ParamKeys.BR).toString());
//		inpara.put("carNo", ctx.getData(GDParamKeys.CAR_NO).toString());
//		inpara.put("status", ctx.getData(GDParamKeys.STATUS).toString());
//		inpara.put("idNo", ctx.getData("idNo").toString());
//
//		// 多页查询汇总信息
//		Pageable pageable = BeanUtils.toObject(ctx.getDataMap(), PageRequest.class);
//		Page<gdeupsb> result = get(GDEupsbTrspNpManagRepository.class).findNpInfo(pageable, inpara);
//		log.info("汇总信息查询结果=" + result);
//		if (result.getTotalElements() == 0) {
//			log.error("agt total info query is null!");
//			throw new CoreException(ErrorCodes.EUPS_CONSOLE_INFO_NOTEXIST); // 查无记录
//		}
//		ctx.setData(ParamKeys.TOTAL_ELEMETS, result.getTotalElements());
//		ctx.setData(ParamKeys.TOTAL_PAGES, result.getTotalPages());
//
//		List<Map<String, Object>> npResultList = result.getElements();
//		
//		ctx.setData("npResultList", npResultList); // 查询汇总返回信息
//		log.info("@@@@@@resultList="+ctx.getData("npResultList"));
		
	}

}
