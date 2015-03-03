package com.bocom.bbip.gdeupsb.action.transportfee;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GDEupsbTrspFeeInfo;
import com.bocom.bbip.gdeupsb.entity.GDEupsbTrspTxnJnl;
import com.bocom.bbip.gdeupsb.repository.GDEupsbTrspFeeInfoRepository;
import com.bocom.bbip.gdeupsb.repository.GDEupsbTrspTxnJnlRepository;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class AftBankCancelAction extends BaseAction{
	private final static Log log = LogFactory.getLog(AftBankCancelAction.class);
	
	@Autowired
	GDEupsbTrspTxnJnlRepository gdEupsbTrspTxnJnlRepository;
	
	@Autowired
	GDEupsbTrspFeeInfoRepository gdEupsbTrspFeeInfoRepository;
	
	public void execute(Context ctx) throws CoreException,CoreRuntimeException{
		log.info("AftBankCancelAction start.......");
		String ohTxnSt = "ohTxnSt";
		//TODO:for test
//		String oTxnSt = "oTxnSt";
		String oTxnSt=ctx.getData("oTxnSt");
		GDEupsbTrspTxnJnl gdEupsbTrspTxnJnl = new GDEupsbTrspTxnJnl();
		if(ctx.getState().equals(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL)){
			ctx.setData(GDParamKeys.TXN_ST, Constants.TXNSTS_CANCEL);
			ctx.setData(GDParamKeys.HTXN_ST, Constants.TXNSTS_CANCEL);
			gdEupsbTrspTxnJnl.setSqn(ParamKeys.OLD_TXN_SQN);
			gdEupsbTrspTxnJnl.setHtxnSt(ctx.getData(GDParamKeys.HTXN_ST).toString());
			gdEupsbTrspTxnJnl.setTxnSt(ctx.getData(GDParamKeys.TXN_ST).toString());
			gdEupsbTrspTxnJnlRepository.update(gdEupsbTrspTxnJnl);
			ctx.setData(ParamKeys.RSP_CDE, Constants.RESPONSE_CODE_SUCC);
		}else if(ctx.getState().equals(BPState.BUSINESS_PROCESSNIG_STATE_FAIL)){
			//TODO:for test
			if("aaaaaa".equals(ctx.getData(GDParamKeys.HRSP_CD).toString())){
				ctx.setData(GDParamKeys.TXN_ST, Constants.TXNSTS_CANCEL);
				ctx.setData(GDParamKeys.HTXN_ST, Constants.TXNSTS_CANCEL);
				gdEupsbTrspTxnJnl.setSqn(ParamKeys.OLD_TXN_SQN);
				gdEupsbTrspTxnJnl.setHtxnSt(ctx.getData(GDParamKeys.HTXN_ST).toString());
				gdEupsbTrspTxnJnl.setTxnSt(ctx.getData(GDParamKeys.TXN_ST).toString());
				gdEupsbTrspTxnJnlRepository.update(gdEupsbTrspTxnJnl);
				ctx.setData(ParamKeys.RSP_CDE, Constants.RESPONSE_CODE_SUCC);
			}else{
				ctx.setData(GDParamKeys.TXN_ST, oTxnSt);
				ctx.setData(GDParamKeys.HTXN_ST,ctx.getData(ohTxnSt));
				gdEupsbTrspTxnJnl.setSqn(ParamKeys.OLD_TXN_SQN);
				gdEupsbTrspTxnJnl.setHtxnSt(ctx.getData(GDParamKeys.HTXN_ST).toString());
				gdEupsbTrspTxnJnl.setTxnSt(ctx.getData(GDParamKeys.TXN_ST).toString());
				gdEupsbTrspTxnJnlRepository.update(gdEupsbTrspTxnJnl);
				ctx.setData(ParamKeys.RSP_CDE, ctx.getData(GDParamKeys.HRSP_CD).toString());
				ctx.setData(ParamKeys.RSP_MSG, "请重新抹账");
			}
		}else{
			ctx.setData(GDParamKeys.TXN_ST, oTxnSt);
			ctx.setData(GDParamKeys.HTXN_ST,ctx.getData(ohTxnSt));
			gdEupsbTrspTxnJnl.setSqn(ParamKeys.OLD_TXN_SQN);
			gdEupsbTrspTxnJnl.setHtxnSt(ctx.getData(GDParamKeys.HTXN_ST).toString());
			gdEupsbTrspTxnJnl.setTxnSt(ctx.getData(GDParamKeys.TXN_ST).toString());
			gdEupsbTrspTxnJnlRepository.update(gdEupsbTrspTxnJnl);		
			if(!"TRM".equals(ctx.getData(GDParamKeys.TXN_CNL))){
				ctx.setData(GDParamKeys.HRSP_CD, null);
				ctx.setData(ParamKeys.RSP_CDE, ErrorCodes.EUPS_FAIL);
				ctx.setData(ParamKeys.RSP_MSG, "请重新抹账");
			}
			
		}



		if(Constants.RESPONSE_CODE_SUCC.equals(ctx.getData(ParamKeys.RSP_CDE).toString())){
			ctx.setData(GDParamKeys.RVS_RSP, ctx.getData(GDParamKeys.HRSP_CD).toString());
			gdEupsbTrspTxnJnl.setSqn(ParamKeys.OLD_TXN_SQN);
			gdEupsbTrspTxnJnl.setRvsRsp(ctx.getData(GDParamKeys.RVS_RSP).toString());
			gdEupsbTrspTxnJnlRepository.update(gdEupsbTrspTxnJnl);
			//TODO: for test
			String exiFlg = ctx.getData("exiFlg");
			if(exiFlg.toString().equals("1")){
			GDEupsbTrspFeeInfo gdEupsbTrspFeeInfo = new GDEupsbTrspFeeInfo();
			gdEupsbTrspFeeInfo.setThdKey(ctx.getData(ParamKeys.OLD_TXN_SQN).toString());
			gdEupsbTrspFeeInfo.setStatus(GDConstants.TF);
			gdEupsbTrspFeeInfoRepository.update(gdEupsbTrspFeeInfo);
			}
		}else{
			if(!StringUtils.isEmpty((String)ctx.getData(GDParamKeys.HRSP_CD))){
				ctx.setData(GDParamKeys.RVS_RSP, ctx.getData(GDParamKeys.HRSP_CD).toString());
				gdEupsbTrspTxnJnl.setSqn(ParamKeys.OLD_TXN_SQN);
				gdEupsbTrspTxnJnl.setRvsRsp(ctx.getData(GDParamKeys.RVS_RSP).toString());
				gdEupsbTrspTxnJnlRepository.update(gdEupsbTrspTxnJnl);
			}
		}
		
	}

}
