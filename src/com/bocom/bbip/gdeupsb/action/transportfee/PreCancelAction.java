package com.bocom.bbip.gdeupsb.action.transportfee;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDErrorCodes;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GDEupsbTrspFeeInfo;
import com.bocom.bbip.gdeupsb.entity.GDEupsbTrspTxnJnl;
import com.bocom.bbip.gdeupsb.repository.GDEupsbTrspFeeInfoRepository;
import com.bocom.bbip.gdeupsb.repository.GDEupsbTrspTxnJnlRepository;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.CollectionUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class PreCancelAction extends BaseAction{
	private final static Log log = LogFactory.getLog(PreCancelAction.class);
	
	@Autowired
	GDEupsbTrspFeeInfoRepository gdEupsbTrspFeeInfoRepository;
	
	@Autowired
	GDEupsbTrspTxnJnlRepository gdEupsbTrspTxnJnlRepository;
	public void execute(Context ctx) throws CoreException,CoreRuntimeException{
		log.info("PreCancelAction start......");
		ctx.setData(GDParamKeys.BR_NO, ctx.getData(ParamKeys.BK));
		
		Date actDt = DateUtils.parse(DateUtils.format(new Date(), "yyyy-MM-dd"));
		
		ctx.setData(GDParamKeys.ACT_DAT, actDt);
		ctx.setData(GDParamKeys.NOD_NO, ctx.getData(ParamKeys.BR));
		ctx.setData(GDParamKeys.TLR_ID, ctx.getData(ParamKeys.TELLER));
		Date payDat = DateUtils.parse(ctx.getData(GDParamKeys.PAY_DAT).toString(), "yyyy-MM-dd");
		ctx.setData(GDParamKeys.PAY_DAT, payDat);
		GDEupsbTrspFeeInfo gdEupsbTrspFeeInfo = new GDEupsbTrspFeeInfo();
		gdEupsbTrspFeeInfo.setBrNo(ctx.getData(GDParamKeys.BR_NO).toString());
		gdEupsbTrspFeeInfo.setCarNo(ctx.getData(GDParamKeys.CAR_NO).toString());
		gdEupsbTrspFeeInfo.setPayDat(payDat);
		gdEupsbTrspFeeInfo.setPayLog(ctx.getData(GDParamKeys.PAY_LOG).toString());
		List<GDEupsbTrspFeeInfo> feeInfoList = gdEupsbTrspFeeInfoRepository.findForCancel(gdEupsbTrspFeeInfo);
		if(CollectionUtils.isEmpty(feeInfoList)){
//			ctx.setData(ParamKeys.RSP_MSG, "无该车主的对应的缴费记录");
			throw new CoreRuntimeException("BBIP4400EU0730");
		}else{
			GDEupsbTrspFeeInfo gdeupsb = feeInfoList.get(0);
			ctx.setData(GDParamKeys.STATUS,gdeupsb.getStatus() );
			ctx.setData(ParamKeys.TXN_AMOUNT,gdeupsb.getTxnAmt());
			ctx.setData(GDParamKeys.ACT_NO,gdeupsb.getActNo() );
			ctx.setData(ParamKeys.CUS_AC,gdeupsb.getActNo() );
			ctx.setData(GDParamKeys.ACT_TYP,gdeupsb.getActTyp() );
			ctx.setData(GDParamKeys.THD_KEY,gdeupsb.getThdKey() );
			ctx.setData(GDParamKeys.CAR_TYP,gdeupsb.getCarTyp() );
			ctx.setData(GDParamKeys.PAY_MON,gdeupsb.getPayMon() );
			ctx.setData(ParamKeys.OLD_TXN_SQN, gdeupsb.getPayLog());
	
		}
		
		if(!GDConstants.JF.equals(ctx.getData(GDParamKeys.STATUS))){
//			ctx.setData(ParamKeys.RSP_MSG, "状态信息错,此笔费用状态非缴费");
			throw new CoreRuntimeException("BBIP4400EU0731");
		}
		

		GDEupsbTrspTxnJnl gdeupsb = new GDEupsbTrspTxnJnl();

		gdeupsb = BeanUtils.toObject(ctx.getDataMap(), GDEupsbTrspTxnJnl.class);
		gdEupsbTrspTxnJnlRepository.insert(gdeupsb);
		log.info("@@@@@@@@@@@context="+ctx);
	}
	
}
