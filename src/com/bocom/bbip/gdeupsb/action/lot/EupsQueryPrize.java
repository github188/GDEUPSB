package com.bocom.bbip.gdeupsb.action.lot;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.utils.ContextUtils;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

/** 485451 查询奖期*/
public class EupsQueryPrize  extends BaseAction {
	private static final Log logger=LogFactory.getLog(EupsQueryPrize.class);

	
	public void execute(Context context)throws CoreException,CoreRuntimeException{
		logger.info("查询奖期start!!");
        
	    /** 获得前台的数据*/
		Map <String,String>params=buildParams(context);
	//
		//List<PrizeInfo> page=get(LoteryRepository.class).findPrizeDetail(params);
		//Assert.isTrue(page.size()==1, ErrorCodes.EUPS_FIELD_EMPTY);

		//PrizeInfo info=page.get(GDConstants.FIRST_ELEMENT_OF_COLLECTION);
		//ContextUtils.setDataMapAsFlatMap(context, info);
		logger.info(context.getDataMap());

		//logger.info(BeanUtils.toMap(info));
          
		logger.info("查询奖期成功!!");
	}
	private Map<String,String> buildParams(Context context) throws CoreException{
		Map <String,String>params=new HashMap<String, String>();
		final String GameId=ContextUtils.assertDataHasLengthAndGetNNR(context, "GameId", ErrorCodes.EUPS_FIELD_EMPTY);
		final String DrawId=ContextUtils.assertDataHasLengthAndGetNNR(context, "DrawId", ErrorCodes.EUPS_FIELD_EMPTY);
		final String KenoId=ContextUtils.assertDataHasLengthAndGetNNR(context, "KenoId", ErrorCodes.EUPS_FIELD_EMPTY);
		final String txtSts=ContextUtils.assertDataHasLengthAndGetNNR(context, ParamKeys.TXN_STS, ErrorCodes.EUPS_FIELD_EMPTY);
		
		final String HTxnSt=ContextUtils.assertDataHasLengthAndGetNNR(context, ParamKeys.MFM_TXN_STS, ErrorCodes.EUPS_FIELD_EMPTY);
		final String TTxnSt=ContextUtils.assertDataHasLengthAndGetNNR(context, ParamKeys.THD_TXN_STS, ErrorCodes.EUPS_FIELD_EMPTY);
		final String PrzFlg=ContextUtils.assertDataHasLengthAndGetNNR(context, "PrzFlg", ErrorCodes.EUPS_FIELD_EMPTY);
		final String sqn=ContextUtils.assertDataHasLengthAndGetNNR(context, ParamKeys.SEQ_NO, ErrorCodes.EUPS_FIELD_EMPTY);
		
		final String TckNo=ContextUtils.assertDataHasLengthAndGetNNR(context, ParamKeys.MFM_VCH_NO, ErrorCodes.EUPS_FIELD_EMPTY);
		final String CrdNo=ContextUtils.assertDataHasLengthAndGetNNR(context, ParamKeys.ACT_NO, ErrorCodes.EUPS_FIELD_EMPTY);
		final String CusNam=ContextUtils.assertDataHasLengthAndGetNNR(context, ParamKeys.CUS_NME, ErrorCodes.EUPS_FIELD_EMPTY);
		final String BetMod=ContextUtils.assertDataHasLengthAndGetNNR(context, "BetMod", ErrorCodes.EUPS_FIELD_EMPTY);
		
		final String BetTyp=ContextUtils.assertDataHasLengthAndGetNNR(context, "BetTyp", ErrorCodes.EUPS_FIELD_EMPTY);
		final String AddCon=ContextUtils.assertDataHasLengthAndGetNNR(context, "AddCon", ErrorCodes.EUPS_FIELD_EMPTY);
		 String TxnDatStr=context.getData("TxnDatStr");
		 if(StringUtils.isBlank(TxnDatStr)){
			 TxnDatStr="19000101";
		 }
		 String TxnDatEnd=context.getData("TxnDatEnd");
		 if(StringUtils.isBlank(TxnDatEnd)){
			 TxnDatStr="20991331";
		 }
		
		params.put("TxnDatEnd", TxnDatEnd);
		params.put("TxnDatStr", TxnDatStr);
		params.put("AddCon", AddCon);
		params.put("BetTyp", BetTyp);
		params.put("BetMod", BetMod);
		params.put("PrzFlg", PrzFlg);
		
		
		params.put("GameId", GameId);
		params.put("DrawId", DrawId);
		params.put("KenoId", KenoId);
		params.put(ParamKeys.MFM_VCH_NO, TckNo);
		params.put(ParamKeys.CUS_NME, CusNam);
		params.put(ParamKeys.ACT_NO, CrdNo);
		
		params.put(ParamKeys.SEQ_NO, sqn);
		params.put(ParamKeys.THD_TXN_STS, TTxnSt);
		params.put(ParamKeys.MFM_TXN_STS, HTxnSt);
		
		params.put(ParamKeys.TXN_STS, txtSts);

		return params;
	}
	
}
