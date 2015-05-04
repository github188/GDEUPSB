package com.bocom.bbip.gdeupsb.action.transportfee;

import java.util.Date;
import java.util.List;

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
import com.bocom.bbip.gdeupsb.entity.GDEupsbTrspTxnJnl;
import com.bocom.bbip.gdeupsb.repository.GDEupsbTrspFeeInfoRepository;
import com.bocom.bbip.gdeupsb.repository.GDEupsbTrspTxnJnlRepository;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class PreBankCancelAction extends BaseAction{

	
private final static Log log = LogFactory.getLog(PreBankCancelAction.class);
	
	@Autowired
	GDEupsbTrspTxnJnlRepository gdEupsbTrspTxnJnlRepository;
	
	@Autowired
	GDEupsbTrspFeeInfoRepository gdEupsbTrspFeeInfoRepository;
	
	public void execute(Context ctx) throws CoreException,CoreRuntimeException{
		log.info("PreCclToBankStrategyAction start.......");
//		TODO:<Set>BrNo=444999</Set>
		Date today = new Date();
		String exiFlg = "exiFlg";
		GDEupsbTrspTxnJnl gdEupsbTrspTxnJnl = new GDEupsbTrspTxnJnl();
		gdEupsbTrspTxnJnl.setSqn(ctx.getData(ParamKeys.OLD_TXN_SQN).toString());
		List<GDEupsbTrspTxnJnl> txnJnlList = gdEupsbTrspTxnJnlRepository.find(gdEupsbTrspTxnJnl);
		
		ctx.setDataMap(BeanUtils.toMap(txnJnlList.get(0)));
		log.info("11111111111111111111111111111111111111111111"+ ctx.getData(ParamKeys.TXN_AMT).toString());
		String actDat = ctx.getData(GDParamKeys.ACT_DAT).toString();
		//TODO:此处不对！！！！！！！！！！！！！！
		if(!today.equals(actDat)){
			ctx.setData(ParamKeys.RSP_CDE, GDErrorCodes.DATE_ERROR);
			ctx.setData(ParamKeys.RSP_MSG, "该笔交易帐务日期与此时帐务日期不等，不能交易");
			//TODO:待考虑是否要抛异常！！！！！！！！！！！！！！！！！！！！！！
//			throw new CoreException(arg0);
			log.info("2222222222222222222222222222222222222222222222222222dateError");
		}
		
		GDEupsbTrspFeeInfo gdEupsbTrspFeeInfo = new GDEupsbTrspFeeInfo();
		gdEupsbTrspFeeInfo.setThdKey(ctx.getData(ParamKeys.OLD_TXN_SQN).toString());
		List<GDEupsbTrspFeeInfo> feeInfoList = gdEupsbTrspFeeInfoRepository.find(gdEupsbTrspFeeInfo);
		if(!CollectionUtils.isEmpty(feeInfoList)){
		
			ctx.setData(exiFlg, "1");
			if(!feeInfoList.get(0).getChkFlg().equals("0")){
				ctx.setData(ParamKeys.RSP_CDE, GDErrorCodes.REVERS_ERROR);
				ctx.setData(ParamKeys.RSP_MSG, "该笔交易已经对账");
				return;
			}
		}else{
			ctx.setData(exiFlg, "0");
		}
		

		ctx.setData(GDParamKeys.TRSP_CD, null);

		if(!ctx.getData(GDParamKeys.HTXN_ST).toString().equals("U") && !ctx.getData(GDParamKeys.HTXN_ST).toString().equals("S")&&
		     !ctx.getData(GDParamKeys.HTXN_ST).toString().equals("T")){
			ctx.setData(ParamKeys.RSP_CDE, ErrorCodes.EUPS_CHECK_TXN_STS_FAIL);
			ctx.setData(ParamKeys.RSP_MSG, "主机交易状态不能抹账");
		}
			
			

		String ohTxnSt = "ohTxnSt";
		String oTxnSt = "oTxnSt";
		ctx.setData(ohTxnSt, ctx.getData(GDParamKeys.HTXN_ST).toString());
		ctx.setData(oTxnSt, ctx.getData(GDParamKeys.TXN_ST).toString());
		ctx.setData(GDParamKeys.TTXN_CD, ctx.getData(GDParamKeys.TXN_COD));
	}
}
