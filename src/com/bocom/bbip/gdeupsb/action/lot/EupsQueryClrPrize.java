package com.bocom.bbip.gdeupsb.action.lot;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bocom.bbip.comp.BBIPPublicServiceImpl;
import com.bocom.bbip.data.domain.Page;
import com.bocom.bbip.data.domain.PageRequest;
import com.bocom.bbip.data.domain.Pageable;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.entity.GdLotDrwTbl;
import com.bocom.bbip.gdeupsb.repository.GdLotDrwTblRepository;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.ContextUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
import com.bocom.jump.bp.support.CollectionUtils;

/** 485454 清算奖期查询*/
public class EupsQueryClrPrize  extends BaseAction {
	private static final Log logger=LogFactory.getLog(EupsQueryClrPrize.class);

	
	public void process(Context context)throws CoreException,CoreRuntimeException{
		logger.info("清算奖期查询start!!");
	
		final String GameId=ContextUtils.assertDataHasLengthAndGetNNR(context, "GameId", ErrorCodes.EUPS_FIELD_EMPTY);
		/** 清算日期*/
		final String ClrDat=ContextUtils.assertDataHasLengthAndGetNNR(context, "ClrDat", ErrorCodes.EUPS_FIELD_EMPTY);
        final int pageSize=ContextUtils.assertDataNotNullAndGet(context, ParamKeys.PAGE_SIZE, ErrorCodes.EUPS_FIELD_EMPTY);
        final int pageNum=ContextUtils.assertDataNotNullAndGet(context, ParamKeys.PAGE_NUM, ErrorCodes.EUPS_FIELD_EMPTY);
	    Pageable pageable=new PageRequest(pageNum, pageSize);
	    Map<String,String>map=CollectionUtils.createMap();
	    map.put("GameId",GameId);
	    map.put("ClrDat",ClrDat);
		Page<GdLotDrwTbl> page=get(GdLotDrwTblRepository.class).queryClearPrize(pageable, map);
		//Assert.isFalse(0==page.getTotalElements(), ErrorCodes.EUPS_QUERY_NO_DATA);

		List<Map<String, Object>> list=(List<Map<String, Object>>) BeanUtils.toMaps(page.getElements());
		context.setData("clearPrizeList", list);
  
		((BBIPPublicServiceImpl)get(GDConstants.BBIP_PUBLIC_SERVICE))
		.synExecute("gdeups.downloadPrizeInfo", context);
		 //test(context);
		logger.info(BeanUtils.toMap(page));
          
		logger.info("清算奖期查询 end!!");
	}
	
}
