package com.bocom.bbip.gdeupsb.action.transportfee;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.bocom.bbip.eups.action.BaseAction;
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
		gdEupsbTrspFeeInfo.setThdKey(ctx.getData(ParamKeys.OLD_TXN_SQN).toString());
		List<GDEupsbTrspFeeInfo> feeInfoList = gdEupsbTrspFeeInfoRepository.find(gdEupsbTrspFeeInfo);
		if(CollectionUtils.isEmpty(feeInfoList)){
			ctx.setData(ParamKeys.RSP_CDE, GDErrorCodes.FEE_INFO_EMPTY);
			ctx.setData(ParamKeys.RSP_MSG, "缴费记录不存在或者已作废");
		}else if(feeInfoList.get(0).getStatus()!="0"){
			ctx.setData(ParamKeys.RSP_CDE, GDErrorCodes.FEE_STATUS_ERROR);
			ctx.setData(ParamKeys.RSP_MSG, "状态信息错,此笔费用状态非缴费");
		}
	}
}
