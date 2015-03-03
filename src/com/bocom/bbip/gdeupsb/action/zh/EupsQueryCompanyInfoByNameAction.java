package com.bocom.bbip.gdeupsb.action.zh;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsThdBaseInfo;
import com.bocom.bbip.eups.repository.EupsThdBaseInfoRepository;
import com.bocom.bbip.utils.Assert;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.ContextUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
/** 481207  单位编号查询单位信息（嵌套）**/
public class EupsQueryCompanyInfoByNameAction extends BaseAction {
	private static final Log logger = LogFactory.getLog(EupsQueryCompanyInfoByNameAction.class);

    public void execute(Context context)throws CoreException{
	   logger.info("单位编号查询单位信息start");
	   /**单位编号*/
       final String comNo=ContextUtils.assertDataHasLengthAndGetNNR(context, ParamKeys.COMPANY_NO, ErrorCodes.EUPS_FIELD_EMPTY);
     
		EupsThdBaseInfo info = get(EupsThdBaseInfoRepository.class).findOne(comNo);

		Assert.isNotNull(info, ErrorCodes.EUPS_QUERY_NO_DATA);
		logger.info("单位["+comNo+"]信息："+BeanUtils.toFlatMap(info));
		ContextUtils.setDataIfNotBlank(context, ParamKeys.EUPS_BUSS_TYPE, info.getEupsBusTyp());
		ContextUtils.setDataIfNotBlank(context, ParamKeys.COMPANY_NAME, info.getComNme());
		logger.info("单位编号查询单位信息end");

   }
   
}
