package com.bocom.bbip.gdeupsb.action.transportfee;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.action.common.CommThdRspCdeAction;
import com.bocom.bbip.eups.adaptor.ThirdPartyAdaptor;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GDEupsbTrspPayInfo;
import com.bocom.bbip.gdeupsb.repository.GDEupsbTrspPayInfoRepository;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.euif.component.util.StringUtil;
import com.bocom.jump.bp.JumpException;
import com.bocom.jump.bp.channel.CommunicationException;
import com.bocom.jump.bp.channel.DefaultTransport;
import com.bocom.jump.bp.channel.Transform;
import com.bocom.jump.bp.channel.interceptors.DecoderTransform;
import com.bocom.jump.bp.channel.interceptors.EncoderTransform;
import com.bocom.jump.bp.channel.interceptors.RequestTransform;
import com.bocom.jump.bp.channel.interceptors.ResponseTransform;
import com.bocom.jump.bp.channel.tcp.SocketGateway;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;


public class QueryTransportFeeAction extends BaseAction{

	public final static Log logger = LogFactory.getLog(QueryTransportFeeAction.class);
	

	
	@Autowired
	GDEupsbTrspPayInfoRepository gdEupsbTrspPayInfoRepository;
	
	@Autowired
	ThirdPartyAdaptor callThdTradeManager;
	
	
	public void execute(Context ctx) throws CoreException,CoreRuntimeException{
		logger.info("QueryTransportFeeAction start......");
		
		ctx.setData(ParamKeys.THD_TXN_CDE, GDConstants.QRY_CAR);
		String sqn = ctx.getData(GDParamKeys.SQN).toString();
		Date date = new Date();
		ctx.setData(GDParamKeys.ACT_DAT, date);
		ctx.setData(GDParamKeys.TACT_DT, DateUtils.format(date, DateUtils.STYLE_yyyyMMdd));
		ctx.setData(GDParamKeys.TLOG_NO, StringUtils.substring(sqn, 2, 8)+StringUtils.substring(sqn, 14, 20));
		ctx.setData(GDParamKeys.THD_KEY, sqn);
		

		Map<String,Object> thdReturnMessage = callThdTradeManager.trade(ctx);
		logger.info("call third start....[the state is" + ctx.getState() + "]");
		if(ctx.getState().equals(BPState.BUSINESS_PROCESSNIG_STATE_OVERTIME)){
			ctx.setData(ParamKeys.RSP_MSG, "查询路桥方交易超时");
			
			throw new CoreException(ErrorCodes.EUPS_THD_SYS_ERROR);
		}else if(ctx.getState().equals(BPState.BUSINESS_PROCESSNIG_STATE_FAIL)){
			ctx.setData(ParamKeys.RSP_MSG, "查询路桥方交易失败");
		
			throw new CoreException(ErrorCodes.EUPS_THD_SYS_ERROR);
		}else if(ctx.getState().equals(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL)){
			CommThdRspCdeAction cRspCdeAction = new CommThdRspCdeAction();
			String responseCode = cRspCdeAction.getThdRspCde(thdReturnMessage, 	ctx.getData(ParamKeys.EUPS_BUSS_TYPE).toString());
			logger.info("responseCode:["+responseCode+"]");
			if(Constants.RESPONSE_CODE_SUCC.equals(responseCode)){
				GDEupsbTrspPayInfo gdEupsbTrspPayInfo = new GDEupsbTrspPayInfo();
				gdEupsbTrspPayInfo.setBrNo(ctx.getData(ParamKeys.BK).toString());
				gdEupsbTrspPayInfo.setCarTyp(ctx.getData(GDParamKeys.CAR_TYP).toString());
				gdEupsbTrspPayInfo.setCarNo(ctx.getData(GDParamKeys.CAR_NO).toString());
				gdEupsbTrspPayInfoRepository.delete1(gdEupsbTrspPayInfo);
				
				ctx.setDataMap(thdReturnMessage);
				ctx.setData(GDParamKeys.BEG_DAT, DateUtils.parse((String)ctx.getData(GDParamKeys.BEG_DAT)));
				ctx.setData(GDParamKeys.END_DAT, DateUtils.parse((String)ctx.getData(GDParamKeys.END_DAT)));
				ctx.setData("feeStd", new BigDecimal((String)ctx.getData("feeStd")));
				ctx.setData("corpus", new BigDecimal((String)ctx.getData("corpus")));
				ctx.setData("lateFee", new BigDecimal((String)ctx.getData("lateFee")));
				ctx.setData("txnAmt", new BigDecimal((String)ctx.getData("txnAmt")));
				
				gdEupsbTrspPayInfo.setPayMon(ctx.getData(GDParamKeys.PAY_MON).toString());
				gdEupsbTrspPayInfo.setTcusNm(ctx.getData(GDParamKeys.TCUS_NM).toString());
				gdEupsbTrspPayInfo.setActDat((Date)ctx.getData(GDParamKeys.ACT_DAT));
				gdEupsbTrspPayInfo.setThdKey(ctx.getData(GDParamKeys.THD_KEY).toString());
				gdEupsbTrspPayInfo.setTactDt(DateUtils.parse((String)ctx.getData(GDParamKeys.TACT_DT), DateUtils.STYLE_yyyyMMdd));
				gdEupsbTrspPayInfo.setTxnAmt(new BigDecimal(ctx.getData(ParamKeys.TXN_AMOUNT).toString()));
				gdEupsbTrspPayInfoRepository.insert(gdEupsbTrspPayInfo);
			}else{
				if(StringUtil.isEmpty(responseCode)){
					responseCode = ErrorCodes.EUPS_THD_RSP_CODE_ERROR;
				}
				throw new CoreException(responseCode);
				
			}
		}else{
			throw new CoreException(ErrorCodes.EUPS_THD_SYS_ERROR);
		}
	}
}
