package com.bocom.bbip.gdeupsb.action.lot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.gdeupsb.entity.GdLotDrwTbl;
import com.bocom.bbip.gdeupsb.repository.GdLotDrwTblRepository;
import com.bocom.bbip.utils.Assert;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.ContextUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

/** 485450 查询奖期信息*/
public class EupsQueryPrizeInfo  extends BaseAction {
	private static final Log logger=LogFactory.getLog(EupsQueryPrizeInfo.class);

	 
	public void process (Context context)throws CoreException,CoreRuntimeException{
		logger.info("查询奖期信息 start!!");
     

		final String GameId=ContextUtils.assertDataHasLengthAndGetNNR(context, "GameId", ErrorCodes.EUPS_FIELD_EMPTY);
		final String SalEnd=ContextUtils.assertDataHasLengthAndGetNNR(context, "SalEnd", ErrorCodes.EUPS_FIELD_EMPTY);
	    /** 获得前台的数据*/
		Map <String,String>params=new HashMap<String, String>();
		params.put("GameId", GameId);
		params.put("SalEnd", SalEnd);
		List<GdLotDrwTbl> list=get(GdLotDrwTblRepository.class).queryPrizeInfo( params);		
		Assert.isFalse(null==list||0==list.size(), ErrorCodes.EUPS_QUERY_NO_DATA);
        context.getDataMapDirectly().putAll(BeanUtils.toFlatMap(list.get(0)));
        logger.info(BeanUtils.toFlatMap(list.get(0)));
		logger.info("查询奖期信息成功!!");
	}

	
}
