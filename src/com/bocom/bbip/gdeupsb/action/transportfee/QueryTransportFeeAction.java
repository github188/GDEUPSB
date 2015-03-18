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
import com.bocom.bbip.eups.adaptor.ThirdPartyAdaptor;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GDEupsbTrspPayInfo;
import com.bocom.bbip.gdeupsb.repository.GDEupsbTrspPayInfoRepository;
import com.bocom.bbip.gdeupsb.utils.ConnectThdUtils;
import com.bocom.bbip.utils.DateUtils;
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
	
//	@Autowired
//    ThirdPartyAdaptor callThdTradeManager;
	
	@Autowired
	GDEupsbTrspPayInfoRepository gdEupsbTrspPayInfoRepository;
	
	@Autowired
	@Qualifier("TRSP00Transport")
	DefaultTransport trspTransport;
	
	@Autowired
	@Qualifier("trspGateWay")
	SocketGateway gateway;
	
	@SuppressWarnings({ "unchecked", "unused" })
	public void execute(Context ctx) throws CoreException,CoreRuntimeException{
		logger.info("QueryTransportFeeAction start......");
		
		ctx.setData(ParamKeys.THD_TXN_CDE, GDConstants.QRY_CAR);
		
		ctx.setData(GDParamKeys.ACT_DAT, new Date());
		ctx.setData(GDParamKeys.TACT_DT, new Date());
		ctx.setData(GDParamKeys.TLOG_NO, ctx.getData(GDParamKeys.SQN));
		ctx.setData(GDParamKeys.THD_KEY, ctx.getData(GDParamKeys.SQN));
		
		
		
		String enCodePath="packet://WEB-INF/classes/config/stream/TRSP00/f484011.xml";
		String deCodePath="packet://WEB-INF/classes/config/stream/TRSP00/p484011.xml";
		trspTransport.setEncodeTransforms(new Transform[] { new EncoderTransform(enCodePath), new RequestTransform() });
		trspTransport.setDecodeTransforms(new Transform[] { new DecoderTransform(deCodePath), new ResponseTransform() });
		trspTransport.setGateway(gateway);
		
		Map responseMessage=new HashMap();
		
		try {
			 responseMessage = (Map)trspTransport.submit(ctx.getDataMap(), ctx);
		} catch (CommunicationException e) {
			e.printStackTrace();
		} catch (JumpException e) {
			e.printStackTrace();
		}

//		ConnectThdUtils connectThdUtils = new ConnectThdUtils();
//		Map<String,Object> responseMessage = connectThdUtils.getThdReosponse(enCodePath,deCodePath,ctx);
		System.out.println();
		System.out.println(enCodePath);
		System.out.println(deCodePath);
		System.out.println(ctx);
//		Map<String,Object> responseMessage = get(ConnectThdUtils.class).getThdResponse(enCodePath, deCodePath, ctx);
//		Map<String,Object> thdReturnMessage = callThdTradeManager.trade(ctx);
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
			if(!"000".equals(responseMessage.get(GDParamKeys.TRSP_CD))){
				ctx.setData(ParamKeys.RSP_MSG, "查询路桥方返回错误");
				//TODO:<Set>RspMsg=STRCAT(路桥方返回: [,$TRspCd,],$TRspMsg)</Set>
				//TODO: <Set>RspCod=RBF999</Set>
				throw new CoreRuntimeException(ErrorCodes.EUPS_FAIL);
			}else{

				GDEupsbTrspPayInfo gdEupsbTrspPayInfo = new GDEupsbTrspPayInfo();
				gdEupsbTrspPayInfo.setBrNo(ctx.getData(ParamKeys.BR).toString());
				gdEupsbTrspPayInfo.setCarTyp(ctx.getData(GDParamKeys.CAR_TYP).toString());
				gdEupsbTrspPayInfo.setCarNo(ctx.getData(GDParamKeys.CAR_NO).toString());
				gdEupsbTrspPayInfoRepository.delete1(gdEupsbTrspPayInfo);
				
				ctx.setDataMap(responseMessage);
				ctx.setData(GDParamKeys.BEG_DAT, DateUtils.parse((String)ctx.getData(GDParamKeys.BEG_DAT)));
				ctx.setData(GDParamKeys.END_DAT, DateUtils.parse((String)ctx.getData(GDParamKeys.END_DAT)));
				ctx.setData("feeStd", new BigDecimal((String)ctx.getData("feeStd")));
				ctx.setData("corpus", new BigDecimal((String)ctx.getData("corpus")));
				ctx.setData("lateFee", new BigDecimal((String)ctx.getData("lateFee")));
				ctx.setData("txnAmt", new BigDecimal((String)ctx.getData("txnAmt")));
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
