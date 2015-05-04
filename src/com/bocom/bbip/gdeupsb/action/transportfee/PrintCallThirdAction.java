package com.bocom.bbip.gdeupsb.action.transportfee;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.action.common.CommThdRspCdeAction;
import com.bocom.bbip.eups.adaptor.ThirdPartyAdaptor;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GDEupsbTrspFeeInfo;
import com.bocom.bbip.gdeupsb.entity.GDEupsbTrspTxnJnl;
import com.bocom.bbip.gdeupsb.repository.GDEupsbTrspFeeInfoRepository;
import com.bocom.bbip.gdeupsb.repository.GDEupsbTrspTxnJnlRepository;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.euif.component.util.StringUtil;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class PrintCallThirdAction extends BaseAction{
	private final static Log log = LogFactory.getLog(PrintCallThirdAction.class);
	
	
	@Autowired
	GDEupsbTrspFeeInfoRepository gdEupsbTrspFeeInfoRepository;
	@Autowired
	GDEupsbTrspTxnJnlRepository gdEupsbTrspTxnJnlRepository;
	
	@Autowired
	ThirdPartyAdaptor callThdTradeManager;
	

	public void execute(Context ctx) throws CoreException,CoreRuntimeException{
		log.info("PrintCallThirdAction start.......");
		ctx.setState("fail");
		GDEupsbTrspTxnJnl gdEupsbTrspTxnJnl = new GDEupsbTrspTxnJnl();
		ctx.setData(ParamKeys.THD_TXN_CDE, "PayCar");

		String sqn = ctx.getData(ParamKeys.SEQUENCE).toString();
		ctx.setData(GDParamKeys.TLOG_NO, StringUtils.substring(sqn, 2, 8)+StringUtils.substring(sqn, 14, 20));
		Map<String,Object> thdReturnMessage = callThdTradeManager.trade(ctx);
		log.info("call third start....[the state is" + ctx.getState() + "]");
		if(ctx.getState().equals(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL)){
			CommThdRspCdeAction cRspCdeAction = new CommThdRspCdeAction();
			String responseCode = cRspCdeAction.getThdRspCde(thdReturnMessage, 	ctx.getData(ParamKeys.EUPS_BUSS_TYPE).toString());
			log.info("responseCode:["+responseCode+"]");
			if(Constants.RESPONSE_CODE_SUCC.equals(responseCode)){	
				ctx.setDataMap(thdReturnMessage);
				ctx.setData(GDParamKeys.TXN_ST, "S");
				ctx.setData(GDParamKeys.TTXN_ST, "S");
				gdEupsbTrspTxnJnl.setTtxnSt(ctx.getData(GDParamKeys.TTXN_ST).toString());
				gdEupsbTrspTxnJnl.setTxnSt(ctx.getData(GDParamKeys.TXN_ST).toString());
				gdEupsbTrspTxnJnl.setNodNo(ctx.getData(ParamKeys.BR).toString());
				gdEupsbTrspTxnJnl.setInvNo(ctx.getData(GDParamKeys.INV_NO).toString());
				gdEupsbTrspTxnJnl.setSqn(ctx.getData(ParamKeys.OLD_TXN_SQN).toString());
				gdEupsbTrspTxnJnlRepository.updateSt(gdEupsbTrspTxnJnl);

		        Date tactDt = new Date();
		        
		        //更新路桥方记账信息
				GDEupsbTrspFeeInfo gdEupsbTrspFeeInfo = new GDEupsbTrspFeeInfo();

				gdEupsbTrspFeeInfo.setInvNo(ctx.getData(GDParamKeys.INV_NO).toString());
				gdEupsbTrspFeeInfo.setBegDat(DateUtils.parse(ctx.getData(GDParamKeys.BEG_DAT).toString())); 
				gdEupsbTrspFeeInfo.setEndDat(DateUtils.parse(ctx.getData(GDParamKeys.END_DAT).toString()));  
				gdEupsbTrspFeeInfo.setCarName((String)ctx.getData(GDParamKeys.CAR_NAME));
				gdEupsbTrspFeeInfo.setCarDzs((String)ctx.getData(GDParamKeys.CAR_DZS));
				gdEupsbTrspFeeInfo.setCntStd((String)ctx.getData(GDParamKeys.CNT_STD));
				gdEupsbTrspFeeInfo.setFeeStd(new BigDecimal((String)ctx.getData(GDParamKeys.FEE_STD)));
				gdEupsbTrspFeeInfo.setCorpus(new BigDecimal((String)ctx.getData(GDParamKeys.CORPUS)));
				gdEupsbTrspFeeInfo.setLateFee(new BigDecimal((String)ctx.getData(GDParamKeys.LATE_FEE)));
				gdEupsbTrspFeeInfo.setClgs((String)ctx.getData(GDParamKeys.CLGS));
				gdEupsbTrspFeeInfo.setYybz((String)ctx.getData(GDParamKeys.YYBZ));
				
				gdEupsbTrspFeeInfo.setTlogNo(ctx.getData(GDParamKeys.TLOG_NO).toString());
				gdEupsbTrspFeeInfo.setTactDt(tactDt);
				
			
				gdEupsbTrspFeeInfo.setPrtNod(ctx.getData(ParamKeys.BR).toString());     //打印网点号
				gdEupsbTrspFeeInfo.setPrtTlr(ctx.getData(ParamKeys.TELLER).toString());  //打印柜员号
				gdEupsbTrspFeeInfo.setPayLog(ctx.getData(ParamKeys.OLD_TXN_SQN).toString());
				gdEupsbTrspFeeInfoRepository.updateStatus(gdEupsbTrspFeeInfo);
				ctx.setState("complete");
			}else{
				ctx.setData(GDParamKeys.TXN_ST, "F");
				ctx.setData(GDParamKeys.TTXN_ST, "F");
				gdEupsbTrspTxnJnl.setTtxnSt(ctx.getData(GDParamKeys.TTXN_ST).toString());
				gdEupsbTrspTxnJnl.setTxnSt(ctx.getData(GDParamKeys.TXN_ST).toString());
				gdEupsbTrspTxnJnl.setNodNo(ctx.getData(ParamKeys.BR).toString());
				gdEupsbTrspTxnJnl.setInvNo(ctx.getData(GDParamKeys.INV_NO).toString());
				gdEupsbTrspTxnJnl.setSqn(ctx.getData(ParamKeys.OLD_TXN_SQN).toString());
				gdEupsbTrspTxnJnlRepository.updateSt(gdEupsbTrspTxnJnl);
				

				ctx.setState("fail");
				if(StringUtil.isEmpty(responseCode)){
					responseCode = ErrorCodes.EUPS_THD_RSP_CODE_ERROR;
				}
				throw new CoreException(responseCode);
			}
		}else if(ctx.getState().equals(BPState.BUSINESS_PROCESSNIG_STATE_TRANS_FAIL)){
			ctx.setData(GDParamKeys.TXN_ST, "X");
			ctx.setData(GDParamKeys.TTXN_ST, "X");
			gdEupsbTrspTxnJnl.setTtxnSt(ctx.getData(GDParamKeys.TTXN_ST).toString());
			gdEupsbTrspTxnJnl.setTxnSt(ctx.getData(GDParamKeys.TXN_ST).toString());
			gdEupsbTrspTxnJnl.setNodNo(ctx.getData(ParamKeys.BR).toString());
			gdEupsbTrspTxnJnl.setInvNo(ctx.getData(GDParamKeys.INV_NO).toString());
			gdEupsbTrspTxnJnl.setSqn(ctx.getData(ParamKeys.OLD_TXN_SQN).toString());
			gdEupsbTrspTxnJnlRepository.updateSt(gdEupsbTrspTxnJnl);
			


			ctx.setData(ParamKeys.RSP_CDE, ErrorCodes.TRANSACTION_ERROR_OTHER_ERROR);
			ctx.setData(ParamKeys.RSP_MSG, "交易失败");
			throw new CoreException(ErrorCodes.EUPS_THD_SYS_ERROR);
		}else {
			ctx.setData(GDParamKeys.TXN_ST, "U");
			ctx.setData(GDParamKeys.TTXN_ST, "T");
			//更新路桥方记账信息
			gdEupsbTrspTxnJnl.setTtxnSt(ctx.getData(GDParamKeys.TTXN_ST).toString());
			gdEupsbTrspTxnJnl.setTxnSt(ctx.getData(GDParamKeys.TXN_ST).toString());
			gdEupsbTrspTxnJnl.setNodNo(ctx.getData(ParamKeys.BR).toString()); //银行网点号
			gdEupsbTrspTxnJnl.setInvNo(ctx.getData(GDParamKeys.INV_NO).toString());
			gdEupsbTrspTxnJnl.setSqn(ctx.getData(ParamKeys.OLD_TXN_SQN).toString());
			gdEupsbTrspTxnJnlRepository.updateSt(gdEupsbTrspTxnJnl);
	
			ctx.setData(ParamKeys.RSP_CDE, ErrorCodes.TRANSACTION_ERROR_TIMEOUT);
			ctx.setData(ParamKeys.RSP_MSG, "路桥方交易超时");
			//TODO:此处throw待考虑是否放开
			throw new CoreException(ErrorCodes.EUPS_THD_SYS_ERROR);
		}

	}

}
