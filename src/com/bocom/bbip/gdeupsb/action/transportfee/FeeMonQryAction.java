package com.bocom.bbip.gdeupsb.action.transportfee;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GDEupsbTrspFeeInfo;
import com.bocom.bbip.gdeupsb.repository.GDEupsbTrspFeeInfoRepository;
import com.bocom.bbip.utils.CollectionUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class FeeMonQryAction extends BaseAction{
	
	Log log = LogFactory.getLog(FeeMonQryAction.class);
	
	@Autowired
	GDEupsbTrspFeeInfoRepository gdEupsbTrspFeeInfoRepository;
	public void execute(Context ctx) throws CoreRuntimeException,CoreException{
		log.info("FeeMonQryAction start.......");
		
//		 select BegDat,EndDat,TCusId
//         FROM   rbfbtxnbok444  
//         WHERE  PayLog='%s'
		GDEupsbTrspFeeInfo gdeups = new GDEupsbTrspFeeInfo();
		gdeups.setPayLog(ctx.getData(ParamKeys.OLD_TXN_SQN).toString());
		List<GDEupsbTrspFeeInfo> feeInfoList = gdEupsbTrspFeeInfoRepository.find(gdeups);
		if(CollectionUtils.isEmpty(feeInfoList)){
			ctx.setData(ParamKeys.RSP_MSG, "对应自助通缴费流水号无对应发票打印记录");
			throw new CoreRuntimeException(ErrorCodes.EUPS_FIND_ISEMPTY);
		}
		ctx.setData(GDParamKeys.BEG_DAT, feeInfoList.get(0).getBegDat());
		ctx.setData(GDParamKeys.END_DAT, feeInfoList.get(0).getEndDat());
		ctx.setData(GDParamKeys.CAR_NO, feeInfoList.get(0).getCarNo());
	}
}
