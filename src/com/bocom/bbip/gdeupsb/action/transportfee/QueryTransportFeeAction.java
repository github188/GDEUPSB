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
//		<Set>TLogNo=$LogNo</Set>

		ctx.setData(GDParamKeys.THD_KEY, ctx.getData(GDParamKeys.SQN));
//		TODO:<Arg name="ObjSvr" value="@PARA.ThdSvr"/>
		Map<String,Object> thdReturnMessage = callThdTradeManager.trade(ctx);
		logger.info("call third start....[the state is" + ctx.getState() + "]");
		if(ctx.getState().equals(BPState.BUSINESS_PROCESSNIG_STATE_OVERTIME)){
			ctx.setData(ParamKeys.RSP_MSG, "查询路桥方交易超时");
			throw new CoreRuntimeException(ErrorCodes.TRANSACTION_ERROR_TIMEOUT);
		}else if(ctx.getState().equals(BPState.BUSINESS_PROCESSNIG_STATE_FAIL)){
			ctx.setData(ParamKeys.RSP_MSG, "查询路桥方交易失败");
			throw new CoreRuntimeException(ErrorCodes.TRANSACTION_ERROR_OTHER_ERROR);
		}else{
			if(!"000".equals(thdReturnMessage.get(GDParamKeys.TRSP_CD))){
				ctx.setData(ParamKeys.RSP_MSG, "查询路桥方返回错误");
				throw new CoreRuntimeException(ErrorCodes.EUPS_FAIL);
			}else{

				GDEupsbTrspPayInfo gdEupsbTrspPayInfo = new GDEupsbTrspPayInfo();
				gdEupsbTrspPayInfo.setBrNo("443999");
				gdEupsbTrspPayInfo.setCarTyp(ctx.getData(GDParamKeys.CAR_TYP).toString());
				gdEupsbTrspPayInfo.setCarNo(ctx.getData(GDParamKeys.CAR_NO).toString());
				gdEupsbTrspPayInfoRepository.delete(gdEupsbTrspPayInfo);

				ctx.setDataMap(thdReturnMessage);
				gdEupsbTrspPayInfo.setPayMon(thdReturnMessage.get(GDParamKeys.PAY_MON).toString());
				gdEupsbTrspPayInfo.setTcusNm(thdReturnMessage.get(GDParamKeys.TCUS_NM).toString());
				gdEupsbTrspPayInfo.setActDat((Date)ctx.getData(GDParamKeys.ACT_DAT));
				gdEupsbTrspPayInfo.setThdKey(ctx.getData(GDParamKeys.THD_KEY).toString());
				gdEupsbTrspPayInfo.setTactDt(DateUtils.parse(thdReturnMessage.get(GDParamKeys.TACT_DT).toString()));
				gdEupsbTrspPayInfo.setTxnAmt(new BigDecimal(thdReturnMessage.get(ParamKeys.TXN_AMOUNT).toString()));
				gdEupsbTrspPayInfoRepository.insert(gdEupsbTrspPayInfo);
			}
		}
	}
}
