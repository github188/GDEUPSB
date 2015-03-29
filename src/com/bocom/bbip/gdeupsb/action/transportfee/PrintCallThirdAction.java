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
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GDEupsbTrspFeeInfo;
import com.bocom.bbip.gdeupsb.entity.GDEupsbTrspTxnJnl;
import com.bocom.bbip.gdeupsb.repository.GDEupsbTrspFeeInfoRepository;
import com.bocom.bbip.gdeupsb.repository.GDEupsbTrspTxnJnlRepository;
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

public class PrintCallThirdAction extends BaseAction{
	private final static Log log = LogFactory.getLog(PrintCallThirdAction.class);
	
	
	@Autowired
	GDEupsbTrspFeeInfoRepository gdEupsbTrspFeeInfoRepository;
	@Autowired
	GDEupsbTrspTxnJnlRepository gdEupsbTrspTxnJnlRepository;
	
	@Autowired
	ThirdPartyAdaptor callThdTradeManager;
	
//	@Autowired
//	@Qualifier("TRSP00Transport")
//	DefaultTransport trspTransport;
//	
//	@Autowired
//	@Qualifier("trspGateWay")
//	SocketGateway gateway;
//	
//	@SuppressWarnings({ "unused", "unchecked" })
	public void execute(Context ctx) throws CoreException,CoreRuntimeException{
		log.info("PrintCallThirdAction start.......");
		ctx.setState("fail");
		GDEupsbTrspTxnJnl gdEupsbTrspTxnJnl = new GDEupsbTrspTxnJnl();
		
//		String enCodePath="packet://WEB-INF/classes/config/stream/TRSP00/f484014.xml";
//		String deCodePath="packet://WEB-INF/classes/config/stream/TRSP00/p484004.xml";
//		trspTransport.setEncodeTransforms(new Transform[] { new EncoderTransform(enCodePath), new RequestTransform() });
//		trspTransport.setDecodeTransforms(new Transform[] { new DecoderTransform(deCodePath), new ResponseTransform() });
//		trspTransport.setGateway(gateway);
//		
//		
//		Map responseMessage=new HashMap();
//		
//		try {
//			 responseMessage = (Map)trspTransport.submit(ctx.getDataMap(), ctx);
//		} catch (CommunicationException e) {
//			e.printStackTrace();
//		} catch (JumpException e) {
//			e.printStackTrace();
//		}
//		ConnectThdUtils connectThdUtils = new ConnectThdUtils();
//		Map<String,Object> thdReturnMessage = connectThdUtils.getThdResponse(enCodePath,deCodePath,ctx);
		
		Map<String,Object> thdReturnMessage = callThdTradeManager.trade(ctx);
		log.info("call third start....[the state is" + ctx.getState() + "]");
		

		if(ctx.getState().equals(BPState.BUSINESS_PROCESSNIG_STATE_OVERTIME)){
			ctx.setData(GDParamKeys.TXN_ST, "U");
			ctx.setData(GDParamKeys.TTXN_ST, "T");
			//更新路桥方记账信息
			gdEupsbTrspTxnJnl.setTtxnSt(ctx.getData(GDParamKeys.TTXN_ST).toString());
			gdEupsbTrspTxnJnl.setTxnSt(ctx.getData(GDParamKeys.TXN_ST).toString());
			gdEupsbTrspTxnJnl.setNodNo(ctx.getData(ParamKeys.BR).toString()); //银行网点号
			gdEupsbTrspTxnJnl.setInvNo(ctx.getData(GDParamKeys.INV_NO).toString());
			gdEupsbTrspTxnJnl.setSqn(ctx.getData(ParamKeys.OLD_TXN_SQN).toString());
			gdEupsbTrspTxnJnlRepository.updateSt(gdEupsbTrspTxnJnl);
			
//			TODO: <If condition="IS_NOEQUAL_STRING($TxnCnl,TRM)">
//          <Exec func="PUB:SetNoResponse"/>
//       </If>
//       <Exec func="PUB:DefaultErrorProc"/>

			ctx.setData(ParamKeys.RSP_CDE, ErrorCodes.TRANSACTION_ERROR_TIMEOUT);
			ctx.setData(ParamKeys.RSP_MSG, "路桥方交易超时");
			//TODO:此处throw待考虑是否放开
			throw new CoreRuntimeException( ErrorCodes.TRANSACTION_ERROR_TIMEOUT);
		}else if(ctx.getState().equals(BPState.BUSINESS_PROCESSNIG_STATE_TRANS_FAIL)){
			ctx.setData(GDParamKeys.TXN_ST, "X");
			ctx.setData(GDParamKeys.TTXN_ST, "X");
			gdEupsbTrspTxnJnl.setTtxnSt(ctx.getData(GDParamKeys.TTXN_ST).toString());
			gdEupsbTrspTxnJnl.setTxnSt(ctx.getData(GDParamKeys.TXN_ST).toString());
			gdEupsbTrspTxnJnl.setNodNo(ctx.getData(ParamKeys.BR).toString());
			gdEupsbTrspTxnJnl.setInvNo(ctx.getData(GDParamKeys.INV_NO).toString());
			gdEupsbTrspTxnJnl.setSqn(ctx.getData(ParamKeys.OLD_TXN_SQN).toString());
			gdEupsbTrspTxnJnlRepository.updateSt(gdEupsbTrspTxnJnl);
			
//			TODO: <If condition="IS_NOEQUAL_STRING($TxnCnl,TRM)">
//          <Exec func="PUB:SetNoResponse"/>
//       </If>
//       <Exec func="PUB:DefaultErrorProc"/>

			ctx.setData(ParamKeys.RSP_CDE, ErrorCodes.TRANSACTION_ERROR_OTHER_ERROR);
			ctx.setData(ParamKeys.RSP_MSG, "交易失败");
			throw new CoreRuntimeException( ErrorCodes.TRANSACTION_ERROR_OTHER_ERROR);
		}else{
			if(!"000".equals(thdReturnMessage.get(GDParamKeys.TRSP_CD))){				
				ctx.setData(GDParamKeys.TXN_ST, "F");
				ctx.setData(GDParamKeys.TTXN_ST, "F");
				gdEupsbTrspTxnJnl.setTtxnSt(ctx.getData(GDParamKeys.TTXN_ST).toString());
				gdEupsbTrspTxnJnl.setTxnSt(ctx.getData(GDParamKeys.TXN_ST).toString());
				gdEupsbTrspTxnJnl.setNodNo(ctx.getData(ParamKeys.BR).toString());
				gdEupsbTrspTxnJnl.setInvNo(ctx.getData(GDParamKeys.INV_NO).toString());
				gdEupsbTrspTxnJnl.setSqn(ctx.getData(ParamKeys.OLD_TXN_SQN).toString());
				gdEupsbTrspTxnJnlRepository.updateSt(gdEupsbTrspTxnJnl);
				
//				TODO: <If condition="IS_NOEQUAL_STRING($TxnCnl,TRM)">
//	          <Exec func="PUB:SetNoResponse"/>
//	       </If>
//	       <Exec func="PUB:DefaultErrorProc"/>

				ctx.setData(ParamKeys.RSP_CDE, ErrorCodes.TRANSACTION_ERROR_OTHER_ERROR);
				ctx.setData(ParamKeys.RSP_MSG, "路桥方返回：" + thdReturnMessage.get(GDParamKeys.TRSP_CD));
				throw new CoreRuntimeException( ErrorCodes.TRANSACTION_ERROR_OTHER_ERROR);
				

			}else{
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
				
				gdEupsbTrspFeeInfo.setTlogNo(ctx.getData(GDParamKeys.SQN).toString());
				gdEupsbTrspFeeInfo.setTactDt(tactDt);
				
			
				gdEupsbTrspFeeInfo.setPrtNod(ctx.getData(ParamKeys.BR).toString());     //打印网点号
				gdEupsbTrspFeeInfo.setPrtTlr(ctx.getData(ParamKeys.TELLER).toString());  //打印柜员号
				gdEupsbTrspFeeInfo.setPayLog(ctx.getData(ParamKeys.OLD_TXN_SQN).toString());
				gdEupsbTrspFeeInfoRepository.updateStatus(gdEupsbTrspFeeInfo);
				ctx.setState("complete");

			}
		}

		
		
	}

}
