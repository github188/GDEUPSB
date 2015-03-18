package com.bocom.bbip.gdeupsb.action.zh;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bocom.bbip.data.domain.Page;
import com.bocom.bbip.data.domain.PageRequest;
import com.bocom.bbip.data.domain.Pageable;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.entity.GDEupsZhAGBatchTemp;
import com.bocom.bbip.gdeupsb.repository.GDEupsZHAGBatchTempRepository;
import com.bocom.bbip.service.BGSPServiceAccessObject;
import com.bocom.bbip.service.Result;
import com.bocom.bbip.utils.Assert;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.Executable;

public class EupsQueryBatchDetails extends BaseAction implements Executable {
	private static final Log logger = LogFactory.getLog(EupsQueryBatchDetails.class);
	public void execute(Context context) throws CoreException {
       logger.info("----start query batch details----");
       final String batNo=context.getData(ParamKeys.BAT_NO);
       final int pageSize=context.getData(ParamKeys.PAGE_SIZE);
       final int pageNum=context.getData(ParamKeys.PAGE_NUM);
       Pageable pageable=new PageRequest(pageNum,pageSize);
      Page<GDEupsZhAGBatchTemp>list= get(GDEupsZHAGBatchTempRepository.class).getDetails(pageable, batNo);
      Assert.isNotEmpty(list.getElements(), ErrorCodes.EUPS_QUERY_NO_DATA);
      List<Map<String,Object>>ret=(List<Map<String, Object>>) BeanUtils.toMaps(list.getElements());
      context.setData("detailList", ret);
      context.setData(ParamKeys.EUPS_BUSS_TYPE, "ZHAG");
      context.setData("fleNme", "BATC46464600.txt");
      context.setData("comNo", "123456");
      Result respData = ((BGSPServiceAccessObject)get(BGSPServiceAccessObject.class))
      .callServiceFlatting("submitAcpBatchData", context.getDataMap());
	}
}
