package com.bocom.bbip.gdeupsb.action.gas;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import com.bocom.bbip.data.domain.Page;
import com.bocom.bbip.data.domain.PageRequest;
import com.bocom.bbip.data.domain.Pageable;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.gdeupsb.entity.GdGasCusAll;
import com.bocom.bbip.gdeupsb.repository.GdGasCusAllRepository;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

/**
 * 惠州燃气代扣用户信息查询
 * 
 * @author WangMQ
 * 
 */
public class QryHzThdCusInfoAction extends BaseAction {
	private static Logger logger = LoggerFactory
			.getLogger(QryHzThdCusInfoAction.class);

	public void execute(Context context) throws CoreException,
			CoreRuntimeException {
		logger.info("Enter in QryHzThdCusInfoAction!.......");
		logger.info("context=" + context);

		// 由于存在燃气协议表，所以无需上代收付查协议，直接查燃气协议表即可

		Map<String, Object> map = new HashMap<String, Object>();
		if (!(null == context.getData("cusNo")
				|| "".equals(context.getData("cusNo")) || "?".equals(context
				.getData("cusNo")))) {
			map.put("cusNo", context.getData("cusNo").toString());
		}
		if (!(null == context.getData("cusAc")
				|| "".equals(context.getData("cusAc")) || "?".equals(context
				.getData("cusAc")))) {
			map.put("cusAc", context.getData("cusAc").toString());
		}
		if (!(null == context.getData("cusNme")
				|| "".equals(context.getData("cusNme")) || "?".equals(context
				.getData("cusNme")))) {
			map.put("cusNme", context.getData("cusNme").toString());
		}
		if (!(null == context.getData("idNo")
				|| "".equals(context.getData("idNo")) || "?".equals(context
				.getData("idNo")))) {
			map.put("idNo", context.getData("idNo").toString());
		}
		map.put("beginDate", context.getData("beginDate").toString());
		map.put("endDate", context.getData("endDate").toString());

		List<GdGasCusAll> gasCusAllList = get(
				GdGasCusAllRepository.class).findDataByOptDate(map);
		if (null == gasCusAllList || CollectionUtils.isEmpty(gasCusAllList)) {
			logger.info("There are no records for select check trans journal ");
			throw new CoreException(ErrorCodes.EUPS_QUERY_NO_DATA);
		}
		
		Pageable pageable = BeanUtils.toObject(context.getDataMap(), PageRequest.class);
		Page<GdGasCusAll> page = get(GdGasCusAllRepository.class).findDataByOptDate(pageable, map);
		setResponseFromPage(context, "loopDtl", page);
		logger.info("========== context after set loopDtl =" + context);
		
//		logger.info("gasCusAllList.size()=" + gasCusAllList.size());
//		logger.info("==========context=" + context);
//
//		List<Map<String, Object>> gasCusAllListDtl = new ArrayList<Map<String, Object>>();
//		for (Map<String, Object> maps : gasCusAllList) {
//			Map<String, Object> cusAllDtlTemp = new HashMap<String, Object>();
//			cusAllDtlTemp.put("cusNo", maps.get("CUS_NO"));
//			cusAllDtlTemp.put("cusAc", maps.get("CUS_AC"));
//			cusAllDtlTemp.put("cusNme", maps.get("CUS_NME"));
//			cusAllDtlTemp.put("acTyp", maps.get("CUS_TYP"));
//			cusAllDtlTemp.put("creDte", (String) maps.get("OPT_DAT"));
//			cusAllDtlTemp.put("agrTme", maps.get("OPT_NOD"));
//			cusAllDtlTemp.put("idTyp", maps.get("ID_TYP"));
//			cusAllDtlTemp.put("idNo", maps.get("ID_NO"));
//			cusAllDtlTemp.put("thdCusNme", maps.get("THD_CUS_NME"));
//			cusAllDtlTemp.put("cmuTel", maps.get("CMU_TEL"));
//			gasCusAllListDtl.add(cusAllDtlTemp);
//		}
//
//		context.setData("loopDtl", gasCusAllListDtl);


//		List<Map<String, Object>> pageableRequest = context
//				.getData("pageableRequest");
//		int pageNum = (Integer) pageableRequest.get(0).get("pageNum");
//		int pageSize = (Integer) pageableRequest.get(0).get("pageSize");
		
//		int pageNum = (Integer) context.getData("pageNum");
//		int pageSize = (Integer) context.getData("pageSize");
//
//		int totalElements = gasCusAllListDtl.size();
//		int totalPages = 0;
//		if (totalElements % pageSize == 0) {
//			totalPages = totalElements / pageSize;
//		} else {
//			totalPages = totalElements / pageSize + 1;
//		}
//
//		List<Map<String, Object>> pageableResponse = new ArrayList<Map<String, Object>>();
//		Map<String, Object> pageMap = new HashMap<String, Object>();
//		pageMap.put("totalElements", totalElements);
//		pageMap.put("totalPages", totalPages);
//		pageableResponse.add(pageMap);
//		context.setData("pageableResponse", pageableResponse);
//		
//		context.setData("totalElements", totalElements);
//		context.setData("totalPages", totalPages);
		

		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
	}
}
