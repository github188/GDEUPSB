package com.bocom.bbip.gdeupsb.action.transportfee;

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
import com.bocom.bbip.gdeupsb.common.GDErrorCodes;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GDEupsbTrspFeeInfo;
import com.bocom.bbip.gdeupsb.entity.GDEupsbTrspTxnJnl;
import com.bocom.bbip.gdeupsb.repository.GDEupsbTrspFeeInfoRepository;
import com.bocom.bbip.gdeupsb.repository.GDEupsbTrspTxnJnlRepository;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class PrintCallThirdAction extends BaseAction{
	private final static Log log = LogFactory.getLog(PrintCallThirdAction.class);
	
	@Autowired
	ThirdPartyAdaptor callThdTradeManager;
	@Autowired
	GDEupsbTrspFeeInfoRepository gdEupsbTrspFeeInfoRepository;
	@Autowired
	GDEupsbTrspTxnJnlRepository gdEupsbTrspTxnJnlRepository;
	public void execute(Context ctx) throws CoreException,CoreRuntimeException{
		log.info("PrintCallThirdAction start.......");
		ctx.setState("fail");
		GDEupsbTrspTxnJnl gdEupsbTrspTxnJnl = new GDEupsbTrspTxnJnl();
		
		Map<String,Object> thdReturnMessage = callThdTradeManager.trade(ctx);
		log.info("call third start....[the state is" + ctx.getState() + "]");
		

		if(ctx.getState().equals(BPState.BUSINESS_PROCESSNIG_STATE_OVERTIME)){
			ctx.setData(GDParamKeys.TXN_ST, "U");
			ctx.setData(GDParamKeys.TTXN_ST, "T");
			//更新路桥方记账信息
			gdEupsbTrspTxnJnl.setTtxnSt(ctx.getData(GDParamKeys.TTXN_ST).toString());
			gdEupsbTrspTxnJnl.setTxnSt(ctx.getData(GDParamKeys.TXN_ST).toString());
			gdEupsbTrspTxnJnl.setNodNo(ctx.getData(GDParamKeys.NOD_NO).toString());
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
//			throw new CoreRuntimeException( ErrorCodes.TRANSACTION_ERROR_TIMEOUT);
		}else if(ctx.getState().equals(BPState.BUSINESS_PROCESSNIG_STATE_TRANS_FAIL)){
			ctx.setData(GDParamKeys.TXN_ST, "X");
			ctx.setData(GDParamKeys.TTXN_ST, "X");
			gdEupsbTrspTxnJnl.setTtxnSt(ctx.getData(GDParamKeys.TTXN_ST).toString());
			gdEupsbTrspTxnJnl.setTxnSt(ctx.getData(GDParamKeys.TXN_ST).toString());
			gdEupsbTrspTxnJnl.setNodNo(ctx.getData(GDParamKeys.NOD_NO).toString());
			gdEupsbTrspTxnJnl.setInvNo(ctx.getData(GDParamKeys.INV_NO).toString());
			gdEupsbTrspTxnJnl.setSqn(ctx.getData(ParamKeys.OLD_TXN_SQN).toString());
			gdEupsbTrspTxnJnlRepository.updateSt(gdEupsbTrspTxnJnl);
			
//			TODO: <If condition="IS_NOEQUAL_STRING($TxnCnl,TRM)">
//          <Exec func="PUB:SetNoResponse"/>
//       </If>
//       <Exec func="PUB:DefaultErrorProc"/>

			ctx.setData(ParamKeys.RSP_CDE, ErrorCodes.TRANSACTION_ERROR_OTHER_ERROR);
			ctx.setData(ParamKeys.RSP_MSG, "交易失败");
//			throw new CoreRuntimeException( ErrorCodes.TRANSACTION_ERROR_OTHER_ERROR);
		}else{
			if(!"000".equals(thdReturnMessage.get(GDParamKeys.TRSP_CD))){				
				ctx.setData(GDParamKeys.TXN_ST, "F");
				ctx.setData(GDParamKeys.TTXN_ST, "F");
				gdEupsbTrspTxnJnl.setTtxnSt(ctx.getData(GDParamKeys.TTXN_ST).toString());
				gdEupsbTrspTxnJnl.setTxnSt(ctx.getData(GDParamKeys.TXN_ST).toString());
				gdEupsbTrspTxnJnl.setNodNo(ctx.getData(GDParamKeys.NOD_NO).toString());
				gdEupsbTrspTxnJnl.setInvNo(ctx.getData(GDParamKeys.INV_NO).toString());
				gdEupsbTrspTxnJnl.setSqn(ctx.getData(ParamKeys.OLD_TXN_SQN).toString());
				gdEupsbTrspTxnJnlRepository.updateSt(gdEupsbTrspTxnJnl);
				
//				TODO: <If condition="IS_NOEQUAL_STRING($TxnCnl,TRM)">
//	          <Exec func="PUB:SetNoResponse"/>
//	       </If>
//	       <Exec func="PUB:DefaultErrorProc"/>

				ctx.setData(ParamKeys.RSP_CDE, ErrorCodes.TRANSACTION_ERROR_OTHER_ERROR);
				ctx.setData(ParamKeys.RSP_MSG, "路桥方返回：" + thdReturnMessage.get(GDParamKeys.TRSP_CD));
//				throw new CoreRuntimeException( ErrorCodes.TRANSACTION_ERROR_OTHER_ERROR);
				ctx.setState("error");

			}else{
				ctx.setData(GDParamKeys.TXN_ST, "S");
				ctx.setData(GDParamKeys.TTXN_ST, "S");
				gdEupsbTrspTxnJnl.setTtxnSt(ctx.getData(GDParamKeys.TTXN_ST).toString());
				gdEupsbTrspTxnJnl.setTxnSt(ctx.getData(GDParamKeys.TXN_ST).toString());
				gdEupsbTrspTxnJnl.setNodNo(ctx.getData(GDParamKeys.NOD_NO).toString());
				gdEupsbTrspTxnJnl.setInvNo(ctx.getData(GDParamKeys.INV_NO).toString());
				gdEupsbTrspTxnJnl.setSqn(ctx.getData(ParamKeys.OLD_TXN_SQN).toString());
				gdEupsbTrspTxnJnlRepository.updateSt(gdEupsbTrspTxnJnl);
//		        <Exec func="PUB:ExecSql" error="IGNORE"><!--更新路桥方记账信息-->
//		           <Arg name="SqlCmd" value="updateInvInf"/>
//		        </Exec>
//				UPDATE rbfbtxnbok444
//		           SET    2InvNo='%s',2BegDat='%s',2EndDat='%s',2CarName='%s',2CarDzs='%s',2CntStd='%s',FeeStd='%s',
//		                  Corpus='%s',LateFee='%s',CLGS='%s',YYBZ='%s',TLogNo='%s',TActDt='%s',PrtNod='%s',PrtTlr='%s',Status='1'
//		           WHERE  PayLog='%s' and Status='0'
//		         </Sentence>
//		         <Fields>InvNo|BegDat|EndDat|CarName|CarDzs|CntStd|FeeStd|Corpus|LateFee|CLGS|YYBZ|TLogNo|TActDt|NodNo|TlrId|OLogNo|</Fields>
		        Date tactDt = new Date();
				GDEupsbTrspFeeInfo gdEupsbTrspFeeInfo = new GDEupsbTrspFeeInfo();
				gdEupsbTrspFeeInfo.setTlogNo(ctx.getData(GDParamKeys.SQN).toString());
				gdEupsbTrspFeeInfo.setTactDt(tactDt);
				
				//下面两个字段的值还没确定出处
				gdEupsbTrspFeeInfo.setPrtNod(ctx.getData(GDParamKeys.NOD_NO).toString());
				gdEupsbTrspFeeInfo.setPrtTlr(ctx.getData(GDParamKeys.TLR_ID).toString());
				gdEupsbTrspFeeInfo.setThdKey(ctx.getData(ParamKeys.OLD_TXN_SQN).toString());
				gdEupsbTrspFeeInfoRepository.updateStatus(gdEupsbTrspFeeInfo);
				ctx.setState("complete");

			}
		}

		
		
	}

}
