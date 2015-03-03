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
		ctx.setData(GDParamKeys.BR_NO, GDConstants.BR_NO);
		Date actDt = DateUtils.parse(DateUtils.format(new Date(), "yyyy-MM-dd"));
		System.out.println(actDt);
		ctx.setData(GDParamKeys.ACT_DAT, actDt);
		ctx.setData(GDParamKeys.NOD_NO, "123");
		ctx.setData(GDParamKeys.TLR_ID, "123");
		ctx.setData(GDParamKeys.TCK_NO, "123456789");
		Date payDat = DateUtils.parse(ctx.getData(GDParamKeys.PAY_DAT).toString(), "yyyy-MM-dd");
		ctx.setData(GDParamKeys.PAY_DAT, payDat);
		GDEupsbTrspFeeInfo gdEupsbTrspFeeInfo = new GDEupsbTrspFeeInfo();
		gdEupsbTrspFeeInfo.setBrNo(ctx.getData(GDParamKeys.BR_NO).toString());
		gdEupsbTrspFeeInfo.setCarNo(ctx.getData(GDParamKeys.CAR_NO).toString());
		gdEupsbTrspFeeInfo.setPayDat(payDat);
		gdEupsbTrspFeeInfo.setPayLog(ctx.getData(GDParamKeys.PAY_LOG).toString());
		List<GDEupsbTrspFeeInfo> feeInfoList = gdEupsbTrspFeeInfoRepository.findForCancel(gdEupsbTrspFeeInfo);
		if(CollectionUtils.isEmpty(feeInfoList)){
			ctx.setData(ParamKeys.RSP_MSG, "无该车主的对应的缴费记录");
			throw new CoreRuntimeException(ErrorCodes.EUPS_FIND_ISEMPTY);
		}else{
			GDEupsbTrspFeeInfo gdeupsb = feeInfoList.get(0);
			ctx.setData(GDParamKeys.STATUS,gdeupsb.getStatus() );
			ctx.setData(ParamKeys.TXN_AMOUNT,gdeupsb.getTxnAmt());
			ctx.setData(GDParamKeys.ACT_NO,gdeupsb.getActNo() );
			ctx.setData(GDParamKeys.ACT_TYP,gdeupsb.getActTyp() );
			ctx.setData(GDParamKeys.THD_KEY,gdeupsb.getThdKey() );
			ctx.setData(GDParamKeys.CAR_TYP,gdeupsb.getCarTyp() );
			ctx.setData(GDParamKeys.PAY_MON,gdeupsb.getPayMon() );
//			TODO:ACTflg此字段表中没有		
		}
		
		if(!GDConstants.JF.equals(ctx.getData(GDParamKeys.STATUS))){
			ctx.setData(ParamKeys.RSP_MSG, "状态信息错,此笔费用状态非缴费");
			throw new CoreRuntimeException(GDErrorCodes.FEE_STATUS_ERROR);
		}
		
//		TODO:<Set>PActNo=$TActNo</Set>
//        <Set>Mask=STRCAT(9,$BBusTyp)</Set>
//        <Set>CcyTyp=0</Set>
//        <Set>CashNo=121</Set>
//        <Set>VchChk=0</Set>
//        <Set>HTxnCd=@PARA.HTxnCd_C2P</Set>
		GDEupsbTrspTxnJnl gdeupsb = new GDEupsbTrspTxnJnl();

		gdeupsb = BeanUtils.toObject(ctx.getDataMap(), GDEupsbTrspTxnJnl.class);
		gdEupsbTrspTxnJnlRepository.insert(gdeupsb);

	}
	
}
