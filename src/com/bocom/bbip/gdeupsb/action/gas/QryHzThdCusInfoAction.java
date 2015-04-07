package com.bocom.bbip.gdeupsb.action.gas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.repository.GdGasCusAllRepository;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

/**
 * 惠州燃气代扣用户信息查询
 * @author WangMQ
 *
 */
public class QryHzThdCusInfoAction extends BaseAction{
	private static Logger logger = LoggerFactory.getLogger(QryHzThdCusInfoAction.class);
	
	public void execute(Context context) throws CoreException, CoreRuntimeException{
		logger.info("Enter in QryHzThdCusInfoAction!.......");
		logger.info("context=" + context);
		
		//由于存在燃气协议表，所以无需上代收付查协议，直接查燃气协议表即可
		
		Map<String, Object> map = new HashMap<String, Object>();
		if(!(null==context.getData("cusNo") || "".equals(context.getData("cusNo")) || "?".equals(context.getData("cusNo"))  )){
			map.put("cusNo", context.getData("cusNo").toString());
		}
		if(!(null==context.getData("cusAc") || "".equals(context.getData("cusAc")) || "?".equals(context.getData("cusAc"))  )){
			map.put("cusAc", context.getData("cusAc").toString());
		}
		if(!(null==context.getData("cusNme") || "".equals(context.getData("cusNme")) || "?".equals(context.getData("cusNme"))  )){
			map.put("cusNme", context.getData("cusNme").toString());
		}
		if(!(null==context.getData("idNo") || "".equals(context.getData("idNo")) || "?".equals(context.getData("idNo"))  )){
			map.put("idNo", context.getData("idNo").toString());
		}
		map.put("beginDate", context.getData("beginDate").toString());
		map.put("endDate", context.getData("endDate").toString());
		
		
		List<Map<String, Object>> gasCusAllList = get(GdGasCusAllRepository.class).findDataByOptDate(map);
		if(CollectionUtils.isEmpty(gasCusAllList)){			//无明细信息
			context.setData(GDParamKeys.GAS_MSG_TYP, "E");
			context.setData(GDParamKeys.GAS_AP_CDE, "SC");
			context.setData(GDParamKeys.GAS_OFMT_COD, "D04");
			context.setData(GDParamKeys.GAS_IN_POS, "0001");
			context.setData(GDParamKeys.GDEUPSB_RSP_MSG, "无明细信息");
			throw new CoreRuntimeException("无明细信息");
		}
		logger.info("gasCusAllList.size()=" + gasCusAllList.size());
		logger.info("==========context=" + context);
		
		List<Map<String, Object>> gasCusAllListDtl = new ArrayList<Map<String,Object>>();
		for(Map<String, Object> maps:gasCusAllList){
			Map<String, Object> cusAllDtlTemp = new HashMap<String, Object>();
			cusAllDtlTemp.put("cusNo", maps.get("CUS_NO"));
			cusAllDtlTemp.put("cusAc", maps.get("CUS_AC"));
			cusAllDtlTemp.put("cusNme", maps.get("CUS_NME"));
			cusAllDtlTemp.put("acTyp", maps.get("CUS_TYP"));
			cusAllDtlTemp.put("creDte", maps.get("OPT_DAT"));
			cusAllDtlTemp.put("agrTme", maps.get("OPT_NOD"));
			cusAllDtlTemp.put("idTyp", maps.get("ID_TYP"));
			cusAllDtlTemp.put("idNo", maps.get("ID_NO"));
			cusAllDtlTemp.put("thdCusNme", maps.get("THD_CUS_NME"));
			cusAllDtlTemp.put("cmuTel", maps.get("CMU_TEL"));
			gasCusAllListDtl.add(cusAllDtlTemp);
		}
		
		context.setData("loopDtl", gasCusAllListDtl);
//		context.setData(GDParamKeys.GAS_AP_CDE, "32");
//	    context.setData(GDParamKeys.GAS_OFMT_COD, "z01");
//	    context.setData(GDParamKeys.GAS_PAGE_NO, "0001");
//	    context.setData(GDParamKeys.GAS_VAR_SIZE, "3");
//	    context.setData(GDParamKeys.GAS_TXN_TTL, "代扣用户信息浏览");
//	    context.setData(GDParamKeys.GAS_SUB_TTL, "查询内容");
//		context.setData(GDParamKeys.GAS_MSG_TYP, "N");
//		context.setData(GDParamKeys.GDEUPSB_RSP_MSG, "交易成功");
//		context.setData(GDParamKeys.GAS_RSP_COD, GDConstants.GDEUPSB_TXN_SUCC_CODE);
		logger.info("==========context=" + context);
		
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
	}
}
