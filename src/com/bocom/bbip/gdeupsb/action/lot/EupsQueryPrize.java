package com.bocom.bbip.gdeupsb.action.lot;

import java.util.HashMap;
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
import com.bocom.bbip.gdeupsb.entity.GdLotDrwTbl;
import com.bocom.bbip.gdeupsb.repository.GdLotDrwTblRepository;
import com.bocom.bbip.utils.Assert;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.ContextUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

/** 485451 查询奖期*/
public class EupsQueryPrize  extends BaseAction {
	private static final Log logger=LogFactory.getLog(EupsQueryPrize.class);

	
	public void process(Context context)throws CoreException,CoreRuntimeException{
		logger.info("查询奖期start!!");
        
	    /** 获得前台的数据*/
		Map <String,String>params=buildParams(context);
	    final int pageNum=context.getData(ParamKeys.PAGE_NUM);
	    final int pageSize=context.getData(ParamKeys.PAGE_SIZE);
	    Pageable pageable=new PageRequest(pageNum, pageSize);
	    Page<GdLotDrwTbl> page=get(GdLotDrwTblRepository.class).queryPrize(pageable, params);
		Assert.isFalse(0==page.getTotalElements(), ErrorCodes.EUPS_QUERY_NO_DATA);
		List<Map<String, Object>>list=(List<Map<String, Object>>) BeanUtils.toMaps(page.getElements());
        context.setData("prizeList", list);
		logger.info(BeanUtils.toMap(page));
          
		logger.info("查询奖期成功!!");
	}
	private Map<String,String> buildParams(Context context) throws CoreException{
		Map <String,String>params=new HashMap<String, String>();
		final String GameId=ContextUtils.assertDataHasLengthAndGetNNR(context, "GameId", ErrorCodes.EUPS_FIELD_EMPTY);
		final String DrawId=ContextUtils.assertDataHasLengthAndGetNNR(context, "DrawId", ErrorCodes.EUPS_FIELD_EMPTY);
		final String DrawNm=ContextUtils.assertDataHasLengthAndGetNNR(context, "DrawNm", ErrorCodes.EUPS_FIELD_EMPTY);

		final String KenoId=ContextUtils.assertDataHasLengthAndGetNNR(context, "KenoId", ErrorCodes.EUPS_FIELD_EMPTY);
		final String KenoNm=ContextUtils.assertDataHasLengthAndGetNNR(context, "KenoNm", ErrorCodes.EUPS_FIELD_EMPTY);

		final String SalStrF=ContextUtils.assertDataHasLengthAndGetNNR(context, "SalStrF", ErrorCodes.EUPS_FIELD_EMPTY);
		
		final String SalEndF=ContextUtils.assertDataHasLengthAndGetNNR(context, "SalEndF", ErrorCodes.EUPS_FIELD_EMPTY);
		
		final String ChkFlg=ContextUtils.assertDataHasLengthAndGetNNR(context, "ChkFlg", ErrorCodes.EUPS_FIELD_EMPTY);
		final String PayFlg=ContextUtils.assertDataHasLengthAndGetNNR(context, "PayFlg", ErrorCodes.EUPS_FIELD_EMPTY);

		final String AddCon=ContextUtils.assertDataHasLengthAndGetNNR(context, "AddCon", ErrorCodes.EUPS_FIELD_EMPTY);
		

		String conditon1=AddCon.substring(0,1).equalsIgnoreCase("1")?"Y":"N";
		String conditon2=AddCon.substring(1,2).equalsIgnoreCase("1")?"Y":"N";
		String conditon3=AddCon.substring(2,3).equalsIgnoreCase("1")?"Y":"N";
		params.put("PayFlg", PayFlg);
		params.put("ChkFlg", ChkFlg);
		params.put("SalStr", SalStrF);
		params.put("SalEnd", SalEndF);
		
		params.put("GameId", GameId);
		params.put("DrawId", DrawId);
		params.put("KenoId", KenoId);
		params.put("DrawNm", DrawNm);
		params.put("KenoNm", KenoNm);
		params.put("conditon1", conditon1);
		params.put("conditon2", conditon2);
		params.put("conditon3", conditon3);

		return params;
	}
	
}
