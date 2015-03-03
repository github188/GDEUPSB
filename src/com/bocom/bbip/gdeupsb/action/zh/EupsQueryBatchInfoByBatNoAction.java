package com.bocom.bbip.gdeupsb.action.zh;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsBatchConsoleInfo;
import com.bocom.bbip.eups.repository.EupsBatchConsoleInfoRepository;
import com.bocom.bbip.utils.Assert;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.ContextUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
/** 481208  批次号查询批次信息（嵌套）**/
public class EupsQueryBatchInfoByBatNoAction extends BaseAction {
	private static final Log logger = LogFactory.getLog(EupsQueryBatchInfoByBatNoAction.class);
    @Autowired
	private EupsBatchConsoleInfoRepository Repository;
    public void execute(Context context)throws CoreException{
	   logger.info("批次号查询批次信息start");
	   
	   /**批次号*/
       final String batNo=ContextUtils.assertDataHasLengthAndGetNNR(context, ParamKeys.BAT_NO, ErrorCodes.EUPS_FIELD_EMPTY);
     

       EupsBatchConsoleInfo info = Repository.findOne(batNo);

		Assert.isFalse(null == info, ErrorCodes.EUPS_BAT_CTL_INFO_NOT_EXIST);
		logger.info("批次["+batNo+"]控制信息："+BeanUtils.toFlatMap(info));
		ContextUtils.setDataIfNotBlank(context, ParamKeys.EUPS_BUSS_TYPE, info.getBusKnd());
		ContextUtils.setDataIfNotBlank(context, ParamKeys.COMPANY_NAME, info.getComNme());
		ContextUtils.setDataIfNotBlank(context, ParamKeys.COMPANY_NO, info.getComNme());
		ContextUtils.setDataIfNotBlank(context, ParamKeys.TOT_AMT, String.valueOf(info.getTotAmt()));
		//总笔数
		ContextUtils.setDataIfNotBlank(context, ParamKeys.TOTAL_ELEMETS, String.valueOf(info.getTotCnt()));
		logger.info("批次号查询批次信息end");

   }
   
}
