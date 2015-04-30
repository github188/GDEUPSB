package com.bocom.bbip.gdeupsb.action.transportfee;

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
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.euif.component.util.StringUtil;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class InvoiceFinalAction extends BaseAction{
	private final static Log log = LogFactory.getLog(InvoiceFinalAction.class);
	@Autowired
	GDEupsbTrspTxnJnlRepository gdEupsbTrspTxnJnlRepository;
	
	@Autowired
	GDEupsbTrspFeeInfoRepository gdEupsbTrspFeeInfoRepository;
	@Autowired
	ThirdPartyAdaptor callThdReturnMessage;
	
	public void execute(Context ctx) throws CoreException,CoreRuntimeException{
		log.info("InvoiceFinalAction start......");
		GDEupsbTrspFeeInfo gdEupsbTrspFeeInfo = new GDEupsbTrspFeeInfo();
		ctx.setData(ParamKeys.THD_TXN_CDE, "CancPC");
		
		if(!"S".equals(ctx.getData("otTxnSt"))){
			ctx.setData("tactDt", DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMdd));
			String sqn = ctx.getData(ParamKeys.SEQUENCE);
			ctx.setData(GDParamKeys.TLOG_NO, StringUtils.substring(sqn, 2, 8)+StringUtils.substring(sqn, 14, 20));
			Map<String, Object> thdReturnMessage = callThdReturnMessage.trade(ctx);
			GDEupsbTrspTxnJnl gDEupsbTrspTxnJnl=BeanUtils.toObject(ctx.getDataMap(), GDEupsbTrspTxnJnl.class);
			if(ctx.getState().equals(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL)){
				CommThdRspCdeAction cRspCdeAction = new CommThdRspCdeAction();
				String responseCode = cRspCdeAction.getThdRspCde(thdReturnMessage, 	ctx.getData(ParamKeys.EUPS_BUSS_TYPE).toString());
				log.info("responseCode:["+responseCode+"]");
				if(Constants.RESPONSE_CODE_SUCC.equals(responseCode)){	
					ctx.setData(GDParamKeys.TACT_DT, DateUtils.parse((String)ctx.getData(GDParamKeys.TACT_DT)));
					ctx.setData(GDParamKeys.TTXN_ST, "S");  //主机交易状态设为成功
					ctx.setData(GDParamKeys.TXN_ST, "S");   //交易状态设为成功
					gDEupsbTrspTxnJnl.setTtxnSt("S");
					gDEupsbTrspTxnJnl.setTxnSt("S");
					gdEupsbTrspTxnJnlRepository.update(gDEupsbTrspTxnJnl);
					
					//更新缴费记录表
//					UPDATE rbfbtxnbok444
//			           SET    RvsLog='%s',RvsDat='%s',RvsNod='%s',RvsTlr='%s',RvsTck='%s',Status='3'
//			           WHERE  BrNo='%s' and ThdKey='%s' and Status='1'
//			         </Sentence>
//			         <Fields>LogNo|TActDt|NodNo|TlrId|TckNo|BrNo|ThdKey|</Fields>
					gdEupsbTrspFeeInfo.setRvsLog(ctx.getData(GDParamKeys.SQN).toString());  //退费流水号
					gdEupsbTrspFeeInfo.setRvsDat((Date)ctx.getData(GDParamKeys.TACT_DT));  //退费日期
					gdEupsbTrspFeeInfo.setRvsNod(ctx.getData(GDParamKeys.NOD_NO).toString());  //退费网点号
					gdEupsbTrspFeeInfo.setRvsTlr(ctx.getData(GDParamKeys.TLR_ID).toString());  //退费柜员号
					gdEupsbTrspFeeInfo.setRvsTck((String)ctx.getData(ParamKeys.MFM_VCH_NO));  //退费主机流水
					gdEupsbTrspFeeInfo.setThdKey(ctx.getData(GDParamKeys.THD_KEY).toString());
					gdEupsbTrspFeeInfo.setStatus("3");  //缴费状态设为退票
					gdEupsbTrspFeeInfoRepository.updateThdFeeInfo(gdEupsbTrspFeeInfo);
				}else{
					ctx.setData(GDParamKeys.TTXN_ST, "F");  //主机交易状态设为失败
					ctx.setData(GDParamKeys.TXN_ST, "F");   //交易状态设为失败
					gDEupsbTrspTxnJnl.setTtxnSt("F");
					gDEupsbTrspTxnJnl.setTxnSt("F");
					gdEupsbTrspTxnJnlRepository.update(gDEupsbTrspTxnJnl);
					if(StringUtil.isEmpty(responseCode)){
						responseCode = ErrorCodes.EUPS_THD_RSP_CODE_ERROR;
					}
					throw new CoreException(responseCode);
				}
			}else if(ctx.getState().equals(BPState.BUSINESS_PROCESSNIG_STATE_FAIL)){
				ctx.setData(GDParamKeys.TTXN_ST, "X");  //主机交易状态设为未发送
				ctx.setData(GDParamKeys.TXN_ST, "X");   //交易状态设为初始状态
				gDEupsbTrspTxnJnl.setTtxnSt("X");
				gDEupsbTrspTxnJnl.setTxnSt("X");
				gdEupsbTrspTxnJnlRepository.update(gDEupsbTrspTxnJnl);
				ctx.setData(ParamKeys.RSP_MSG, "路桥方交易失败");
				throw new CoreException(ErrorCodes.EUPS_THD_SYS_ERROR);
			}else {
				ctx.setData(GDParamKeys.TTXN_ST, "T");  //主机交易状态设为超时
				ctx.setData(GDParamKeys.TXN_ST, "U");   //交易状态设为初始状态
				gDEupsbTrspTxnJnl.setTtxnSt("T");
				gDEupsbTrspTxnJnl.setTxnSt("U");
				gdEupsbTrspTxnJnlRepository.update(gDEupsbTrspTxnJnl);
				ctx.setData(ParamKeys.RSP_MSG, "路桥方交易超时");
				throw new CoreException(ErrorCodes.EUPS_THD_SYS_ERROR);
			}
			
		}
	}

}
