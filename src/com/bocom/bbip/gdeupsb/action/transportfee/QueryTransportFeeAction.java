package com.bocom.bbip.gdeupsb.action.transportfee;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.adaptor.ThirdPartyAdaptor;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GDEupsbTrspPayInfo;
import com.bocom.bbip.gdeupsb.repository.GDEupsbTrspPayInfoRepository;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;


public class QueryTransportFeeAction extends BaseAction{

	public final static Log logger = LogFactory.getLog(QueryTransportFeeAction.class);
	
	@Autowired
    ThirdPartyAdaptor callThdTradeManager;
	
	@Autowired
	GDEupsbTrspPayInfoRepository gdEupsbTrspPayInfoRepository;
	
	public void execute(Context ctx) throws CoreException,CoreRuntimeException{
		logger.info("QueryTransportFeeAction start......");
		
		ctx.setData(ParamKeys.THD_TXN_CDE, GDConstants.QRY_CAR);
		ctx.setData("transCode", "484001");
		ctx.setData(GDParamKeys.ACT_DAT, new Date());
		ctx.setData(GDParamKeys.TACT_DT, new Date());
		ctx.setData(GDParamKeys.TLOG_NO, ctx.getData(GDParamKeys.SQN));
		ctx.setData(GDParamKeys.THD_KEY, ctx.getData(GDParamKeys.SQN));

		Map<String,Object> thdReturnMessage = callThdTradeManager.trade(ctx);
		logger.info("call third start....[the state is" + ctx.getState() + "]");
		if(ctx.getState().equals(BPState.BUSINESS_PROCESSNIG_STATE_OVERTIME)){
			ctx.setData(ParamKeys.RSP_MSG, "查询路桥方交易超时");
			//TODO: <Set>RspCod=RBF999</Set>
			throw new CoreRuntimeException(ErrorCodes.TRANSACTION_ERROR_TIMEOUT);
		}else if(ctx.getState().equals(BPState.BUSINESS_PROCESSNIG_STATE_FAIL)){
			ctx.setData(ParamKeys.RSP_MSG, "查询路桥方交易失败");
			//TODO: <Set>RspCod=RBF999</Set>
			throw new CoreRuntimeException(ErrorCodes.TRANSACTION_ERROR_OTHER_ERROR);
		}else{
			if(!"000".equals(thdReturnMessage.get(GDParamKeys.TRSP_CD))){
				ctx.setData(ParamKeys.RSP_MSG, "查询路桥方返回错误");
				//TODO:<Set>RspMsg=STRCAT(路桥方返回: [,$TRspCd,],$TRspMsg)</Set>
				//TODO: <Set>RspCod=RBF999</Set>
				throw new CoreRuntimeException(ErrorCodes.EUPS_FAIL);
			}else{

				GDEupsbTrspPayInfo gdEupsbTrspPayInfo = new GDEupsbTrspPayInfo();
				gdEupsbTrspPayInfo.setBrNo("443999");
				gdEupsbTrspPayInfo.setCarTyp(ctx.getData(GDParamKeys.CAR_TYP).toString());
				gdEupsbTrspPayInfo.setCarNo(ctx.getData(GDParamKeys.CAR_NO).toString());
				gdEupsbTrspPayInfoRepository.delete(gdEupsbTrspPayInfo);
				
				ctx.setDataMap(thdReturnMessage);
				System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@"+ctx);
				gdEupsbTrspPayInfo.setPayMon(ctx.getData(GDParamKeys.PAY_MON).toString());
				gdEupsbTrspPayInfo.setTcusNm(ctx.getData(GDParamKeys.TCUS_NM).toString());
				gdEupsbTrspPayInfo.setActDat((Date)ctx.getData(GDParamKeys.ACT_DAT));
				gdEupsbTrspPayInfo.setThdKey(ctx.getData(GDParamKeys.THD_KEY).toString());
				gdEupsbTrspPayInfo.setTactDt(DateUtils.parse((String)ctx.getData(GDParamKeys.TACT_DT), DateUtils.STYLE_yyyyMMdd));
				gdEupsbTrspPayInfo.setTxnAmt(new BigDecimal(ctx.getData(ParamKeys.TXN_AMOUNT).toString()));
				gdEupsbTrspPayInfoRepository.insert(gdEupsbTrspPayInfo);
			}
		}
	}
}
