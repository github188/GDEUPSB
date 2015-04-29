package com.bocom.bbip.gdeupsb.action.transportfee;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.common.GDErrorCodes;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GDEupsbTrspFeeInfo;
import com.bocom.bbip.gdeupsb.repository.GDEupsbTrspFeeInfoRepository;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class QueryFeeInfoAction extends BaseAction{
	private final static Log log = LogFactory.getLog(QueryFeeInfoAction.class);
	
	@Autowired
	GDEupsbTrspFeeInfoRepository gdEupsbTrspFeeInfoRepository;
	
	public void execute(Context ctx) throws CoreException,CoreRuntimeException{
		log.info("QueryFeeInfoAction start......");
		GDEupsbTrspFeeInfo gdEupsbTrspFeeInfo = new GDEupsbTrspFeeInfo();
		gdEupsbTrspFeeInfo.setPayLog(ctx.getData(ParamKeys.OLD_TXN_SQN).toString());
		List<GDEupsbTrspFeeInfo> feeInfoList = gdEupsbTrspFeeInfoRepository.find(gdEupsbTrspFeeInfo);
		if(CollectionUtils.isEmpty(feeInfoList)){
//			ctx.setData(ParamKeys.RSP_CDE, GDErrorCodes.FEE_INFO_EMPTY);
//			ctx.setData(ParamKeys.RSP_MSG, "缴费记录不存在或者已作废");
			throw new CoreException("BBIP4400EU0730");
		}else if(!"0".equals(feeInfoList.get(0).getStatus())){
//			ctx.setData(ParamKeys.RSP_CDE, GDErrorCodes.FEE_STATUS_ERROR);
//			ctx.setData(ParamKeys.RSP_MSG, "状态信息错,此笔费用状态非缴费");
			throw new CoreException("BBIP4400EU0731");
		}
		GDEupsbTrspFeeInfo gdEupsbTrspFeeInfo1 = feeInfoList.get(0);
		ctx.setData(GDParamKeys.CAR_NO, gdEupsbTrspFeeInfo1.getCarNo());
		ctx.setData(GDParamKeys.TCUS_NM, gdEupsbTrspFeeInfo1.getTcusNm());
		ctx.setData(GDParamKeys.TXN_AMT, gdEupsbTrspFeeInfo1.getTxnAmt());
		ctx.setData(GDParamKeys.ACT_DAT, gdEupsbTrspFeeInfo1.getPayDat());
	}
}
