package com.bocom.bbip.gdeupsb.action.lot;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bocom.bbip.data.domain.Page;
import com.bocom.bbip.data.domain.PageRequest;
import com.bocom.bbip.data.domain.Pageable;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.entity.GdLotDrwTbl;
import com.bocom.bbip.gdeupsb.repository.GdLotDrwTblRepository;
import com.bocom.bbip.utils.Assert;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.ContextUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

/** 485454 清算奖期查询*/
public class EupsQueryClrPrize  extends BaseAction {
	private static final Log logger=LogFactory.getLog(EupsQueryClrPrize.class);

	
	public void execute(Context context)throws CoreException,CoreRuntimeException{
		logger.info("清算奖期查询start!!");
		final String GameId=ContextUtils.assertDataHasLengthAndGetNNR(context, "GameId", ErrorCodes.EUPS_FIELD_EMPTY);
		/** 清算日期*/
		final String ClrDat=ContextUtils.assertDataHasLengthAndGetNNR(context, "ClrDat", ErrorCodes.EUPS_FIELD_EMPTY);
        final int pageSize=ContextUtils.assertDataNotNullAndGet(context, ParamKeys.PAGE_SIZE, ErrorCodes.EUPS_FIELD_EMPTY);
        final int pageNum=ContextUtils.assertDataNotNullAndGet(context, ParamKeys.PAGE_NUM, ErrorCodes.EUPS_FIELD_EMPTY);
	    Pageable pableable=new PageRequest(pageNum, pageSize);
	    GdLotDrwTbl info=new GdLotDrwTbl();
	    info.setGameId(GameId);
	    
		Page<GdLotDrwTbl> page=get(GdLotDrwTblRepository.class).find(pableable, info);
		Assert.isFalse(0==page.getTotalElements(), ErrorCodes.EUPS_QUERY_NO_DATA);

		ContextUtils.setDataMapAsFlatMap(context, page);

		logger.info(BeanUtils.toMap(page));
          
		logger.info("清算奖期查询!!");
	}
	
	
}
