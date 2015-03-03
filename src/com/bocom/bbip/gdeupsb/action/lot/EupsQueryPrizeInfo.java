package com.bocom.bbip.gdeupsb.action.lot;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.action.eupsreport.ReportHelper;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.repository.EupsThdFtpConfigRepository;
import com.bocom.bbip.utils.ContextUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

/** 485450 查询奖期信息*/
public class EupsQueryPrizeInfo  extends BaseAction {
	private static final Log logger=LogFactory.getLog(EupsQueryPrizeInfo.class);

	 @Autowired
	    private EupsThdFtpConfigRepository epository;
	    @Autowired
	    private ReportHelper reportHelper;
	    @Autowired
	    private BBIPPublicService service;
	public void execute(Context context)throws CoreException,CoreRuntimeException{
		logger.info("查询奖期信息 start!!");
        context.setData("GameId", "4545");
        context.setData("SalEnd", "76768");

		final String GameId=ContextUtils.assertDataHasLengthAndGetNNR(context, "GameId", ErrorCodes.EUPS_FIELD_EMPTY);
		final String SalEnd=ContextUtils.assertDataHasLengthAndGetNNR(context, "SalEnd", ErrorCodes.EUPS_FIELD_EMPTY);
	    /** 获得前台的数据*/
		Map <String,String>params=new HashMap<String, String>();
		params.put("GameId", GameId);
		params.put("SalEnd", SalEnd);
		params.put("bk", "1");
		//List<PrizeInfo> page=get(LoteryRepository.class).findPrizeDetail(params);
		//Assert.isTrue(page.size()==1, ErrorCodes.EUPS_QUERY_NO_DATA);

		//PrizeInfo info=page.get(GDConstants.FIRST_ELEMENT_OF_COLLECTION);
		//ContextUtils.setDataMapAsFlatMap(context, info);
		//logger.info(context.getDataMap());

		//logger.info(BeanUtils.toMap(info));
		//MFTPConfigInfo mftpConfigInfo = reportHelper.getMFTPConfigInfo(epository);
        //log.info("mftpConfigInfo:>>>>"+BeanUtils.toMap(mftpConfigInfo));
       // List<Map<String,String>> list=get(LoteryRepository.class).findPrizeCheck(params);
		//Assert.isFalse(null==list||list.size()==0, ErrorCodes.EUPS_QUERY_NO_DATA);
		//logger.info("keys:---"+list.get(0).keySet());
		//logger.info("values:---"+list.get(0).values());
   
          
		logger.info("查询奖期信息成功!!");
	}

	
}
