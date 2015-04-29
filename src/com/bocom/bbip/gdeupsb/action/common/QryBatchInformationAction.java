package com.bocom.bbip.gdeupsb.action.common;

import java.util.Date;
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
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.repository.GDEupsBatchConsoleInfoRepository;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
/**
 * 批量明细查询
 * @author WangMQ 
 * @since 2015-04-29
 *
 */
public class QryBatchInformationAction extends BaseAction{

	
	private Logger logger = LoggerFactory.getLogger(QryBatchInformationAction.class);
	
	public void execute(Context context) throws CoreException{
		logger.info("======>>>>> enter in QryBatchInformationAction @ execute with contexr <<<<<======" + context);
		
		String eupsBusTyp = context.getData(ParamKeys.EUPS_BUSS_TYPE);
		String beginDte = context.getData("beginDte");
		String endDte = context.getData("endDte");
		Date beginDate = DateUtils.parse(beginDte);
		Date endDate = DateUtils.parse(endDte);
		
		
		Map<String, Object> baseMap = new HashMap<String, Object>();
		baseMap.put("eupsBusTyp", eupsBusTyp);
		baseMap.put("beginDate", beginDate);
		baseMap.put("endDate", endDate);

		List<Map<String, Object>> gasCusAllList = get(
				GDEupsBatchConsoleInfoRepository.class).findBatInformation(baseMap);
		if (null == gasCusAllList || CollectionUtils.isEmpty(gasCusAllList)) {
			logger.info("There are no records for select batch detail ");
			throw new CoreException(ErrorCodes.EUPS_QUERY_NO_DATA);
		}
		
		Pageable pageable = BeanUtils.toObject(context.getDataMap(), PageRequest.class);
		Page<Map<String, Object>> page = get(GDEupsBatchConsoleInfoRepository.class).findBatInformation(pageable, baseMap);
		setResponseFromPage(context, "batchDtl", page);
	
		logger.info("============== contxt after set batchDtl : " + context);
		
	}
	
}
