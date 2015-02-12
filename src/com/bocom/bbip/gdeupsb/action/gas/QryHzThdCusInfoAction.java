package com.bocom.bbip.gdeupsb.action.gas;

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
import com.bocom.bbip.gdeupsb.entity.GdEupsCusAgent;
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
		
//		select * from gascusall491 where (userno='%s' or '%s'='') and (actno='%s' or '%s'='') 
//        and (actnam='%s' or '%s'='') and (idno='%s' or '%s'='') and OPTDAT &gt;='%s' and OPTDAT &lt;='%s'
		
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
		
		//惠州燃气代扣用户信息查询  查询明细
		Result accessObject = get(BGSPServiceAccessObject.class).callServiceFlatting("queryDetailAgentCollectAgreement", map);

		if(CollectionUtils.isEmpty(accessObject.getPayload())){			//无明细信息
			context.setData(GDParamKeys.GAS_MSG_TYP, "E");
			throw new CoreRuntimeException("没有明细信息");
		}
		
		List<Map<String, Object>> resultMapList = (List<Map<String, Object>>) BeanUtils.toMap(accessObject);
		
		context.setData("loopDtl", resultMapList);
		
		context.setData(GDParamKeys.GAS_AP_CDE, "32");
	    context.setData(GDParamKeys.GAS_OFMT_COD, "z01");
	    context.setData(GDParamKeys.GAS_PAGE_NO, "0001");
	    context.setData(GDParamKeys.GAS_VAR_SIZE, "3");
	    context.setData(GDParamKeys.GAS_TXN_TTL, "代扣用户信息浏览");
	    context.setData(GDParamKeys.GAS_SUB_TTL, "查询内容");
			
		context.setData(GDParamKeys.GAS_MSG_TYP, "N");
		context.setData(GDParamKeys.GDEUPSB_RSP_MSG, "交易成功");
		context.setData(GDParamKeys.GAS_RSP_COD, GDConstants.GDEUPSB_TXN_SUCC_CODE);
		
		
		
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
	}
}
