package com.bocom.bbip.gdeupsb.action.gas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.python.parser.ast.listcompType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GdEupsCusAgent;
import com.bocom.bbip.gdeupsb.entity.GdGasCusAll;
import com.bocom.bbip.gdeupsb.entity.GdGasCusDay;
import com.bocom.bbip.gdeupsb.repository.GdGasCusAllRepository;
import com.bocom.bbip.gdeupsb.repository.GdGasCusDayRepository;
import com.bocom.bbip.service.BGSPServiceAccessObject;
import com.bocom.bbip.service.Result;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.DateUtils;
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
		//拼装外发代收付map
//		Map<String, Object> map = new HashMap<String, Object>();
//		if(!(null==context.getData("thdCusNo") || "".equals(context.getData("thdCusNo")) || "?".equals(context.getData("thdCusNo"))  )){
//			map.put("thdCusNo", context.getData("thdCusNo").toString());
//		}
//		if(!(null==context.getData("cusAc") || "".equals(context.getData("cusAc")) || "?".equals(context.getData("cusAc"))  )){
//			map.put("cusAc", context.getData("cusAc").toString());
//		}
//		if(!(null==context.getData("cusNme") || "".equals(context.getData("cusNme")) || "?".equals(context.getData("cusNme"))  )){
//			map.put("cusNme", context.getData("cusNme").toString());
//		}
//		if(!(null==context.getData("idNo") || "".equals(context.getData("idNo")) || "?".equals(context.getData("idNo"))  )){
//			map.put("idNo", context.getData("idNo").toString());
//		}
		
		//惠州燃气代扣用户信息查询  查询明细
//		Result accessObject = get(BGSPServiceAccessObject.class).callServiceFlatting("queryDetailAgentCollectAgreement", map);
		
//		select * from gascusall491 where (userno='%s' or '%s'='') and (actno='%s' or '%s'='') 
//        and (actnam='%s' or '%s'='') and (idno='%s' or '%s'='') and OPTDAT &gt;='%s' and OPTDAT &lt;='%s'
		
		
//		GdGasCusAll cusAll = new GdGasCusAll();
		
		Map<String, Object> map = new HashMap<String, Object>();
		if(!(null==context.getData("thdCusNo") || "".equals(context.getData("thdCusNo")) || "?".equals(context.getData("thdCusNo"))  )){
			map.put("thdCusNo", context.getData("thdCusNo").toString());
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
		for(int i=0; i<gasCusAllList.size(); i++){
			Map<String, Object> cusAllDtlTemp = new HashMap<String, Object>();
			logger.info("gasCusAllList["+ i + "]:CUS_NO:" + gasCusAllList.get(i).get("CUS_NO"));
			cusAllDtlTemp.put("cusNo", gasCusAllList.get(i).get("CUS_NO"));
			cusAllDtlTemp.put("cusAc", gasCusAllList.get(i).get("CUS_AC"));
			cusAllDtlTemp.put("cusNme", gasCusAllList.get(i).get("CUS_NME"));
			cusAllDtlTemp.put("acTyp", gasCusAllList.get(i).get("CUS_TYP"));
			cusAllDtlTemp.put("creDte", gasCusAllList.get(i).get("OPT_DAT"));
			cusAllDtlTemp.put("agrTme", gasCusAllList.get(i).get("OPT_NOD"));
			cusAllDtlTemp.put("idTyp", gasCusAllList.get(i).get("ID_TYP"));
			cusAllDtlTemp.put("idNo", gasCusAllList.get(i).get("ID_NO"));
			cusAllDtlTemp.put("thdCusNme", gasCusAllList.get(i).get("THD_CUS_NME"));
			cusAllDtlTemp.put("cmuTel", gasCusAllList.get(i).get("CMU_TEL"));
			gasCusAllListDtl.add(cusAllDtlTemp);
			logger.info("gasCusAllListDtl["+ i + "]:cusNo:" + gasCusAllListDtl.get(i).get("cusNo"));
		}
		
		context.setData("loopDtl", gasCusAllListDtl);
		context.setData(GDParamKeys.GAS_AP_CDE, "32");
	    context.setData(GDParamKeys.GAS_OFMT_COD, "z01");
	    context.setData(GDParamKeys.GAS_PAGE_NO, "0001");
	    context.setData(GDParamKeys.GAS_VAR_SIZE, "3");
	    context.setData(GDParamKeys.GAS_TXN_TTL, "代扣用户信息浏览");
	    context.setData(GDParamKeys.GAS_SUB_TTL, "查询内容");
		context.setData(GDParamKeys.GAS_MSG_TYP, "N");
		context.setData(GDParamKeys.GDEUPSB_RSP_MSG, "交易成功");
		context.setData(GDParamKeys.GAS_RSP_COD, GDConstants.GDEUPSB_TXN_SUCC_CODE);
		logger.info("==========context=" + context);
		
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
	}
}
