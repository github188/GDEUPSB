package com.bocom.bbip.gdeupsb.action.transportfee;

import java.util.Date;
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
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class FeeMonQryAction extends BaseAction{
	
	Log log = LogFactory.getLog(FeeMonQryAction.class);
	
	@Autowired
	GDEupsbTrspFeeInfoRepository gdEupsbTrspFeeInfoRepository;
	public void execute(Context ctx) throws CoreRuntimeException,CoreException{
		log.info("FeeMonQryAction start.......");
		

		GDEupsbTrspFeeInfo gdeups = new GDEupsbTrspFeeInfo();
		gdeups.setPayLog(ctx.getData(ParamKeys.OLD_TXN_SQN).toString());
		List<GDEupsbTrspFeeInfo> feeInfoList = gdEupsbTrspFeeInfoRepository.find(gdeups);
		if(CollectionUtils.isEmpty(feeInfoList)){
			ctx.setData(ParamKeys.RSP_MSG, "对应自助通缴费流水号无对应发票打印记录");
			throw new CoreRuntimeException(ErrorCodes.EUPS_FIND_ISEMPTY);
		}
//			Date begDat = DateUtils.parse(DateUtils.format(feeInfoList.get(0).getBegDat(), DateUtils.STYLE_SIMPLE_DATE), DateUtils.STYLE_SIMPLE_DATE);
//			Date endDat = DateUtils.parse(DateUtils.format(feeInfoList.get(0).getEndDat(), DateUtils.STYLE_SIMPLE_DATE), DateUtils.STYLE_SIMPLE_DATE);
		log.info("@@@"+ctx);
		ctx.setData(GDParamKeys.BEG_DAT, feeInfoList.get(0).getBegDat());
		ctx.setData(GDParamKeys.END_DAT, feeInfoList.get(0).getEndDat());
		ctx.setData(GDParamKeys.CAR_NO, feeInfoList.get(0).getCarNo());
	}
}
