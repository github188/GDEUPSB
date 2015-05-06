package com.bocom.bbip.gdeupsb.action.transportfee;

import java.util.List;
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
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GDEupsbTrspFeeInfo;
import com.bocom.bbip.gdeupsb.entity.GDEupsbTrspTxnJnl;
import com.bocom.bbip.gdeupsb.repository.GDEupsbTrspFeeInfoRepository;
import com.bocom.bbip.gdeupsb.repository.GDEupsbTrspTxnJnlRepository;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.CollectionUtils;
import com.bocom.euif.component.util.StringUtil;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;


/**
 * 
 * @author li haoxun  
 *
 */
public class PrintInvCancelAction extends BaseAction{
	private final static Log log = LogFactory.getLog(PrintInvCancelAction.class);
	@Autowired
	GDEupsbTrspTxnJnlRepository gdEupsbTrspTxnJnlRepository;
	@Autowired
	GDEupsbTrspFeeInfoRepository gdEupsbTrspFeeInfoRepository;
	@Autowired
	ThirdPartyAdaptor callThdReturnMessage;
	
	public void execute(Context ctx) throws CoreException,CoreRuntimeException{
		log.info("PrintInvCancelAction start......");
		

//		<Delete>TRspCd</Delete>
		GDEupsbTrspTxnJnl gdEupsbTrspTxnJnl = new GDEupsbTrspTxnJnl();
		gdEupsbTrspTxnJnl.setSqn(ctx.getData(GDParamKeys.SQN).toString());
		List<GDEupsbTrspTxnJnl> txnJnlList = gdEupsbTrspTxnJnlRepository.find(gdEupsbTrspTxnJnl);
		if(!CollectionUtils.isEmpty(txnJnlList)){
			ctx.setDataMap(BeanUtils.toMap(txnJnlList.get(0)));
		}
		ctx.setData(GDParamKeys.TRSP_CD, null);
		if("U".equals(ctx.getData(GDParamKeys.TTXN_ST)) | "S".equals(ctx.getData(GDParamKeys.TTXN_ST)) | "T".equals(ctx.getData(GDParamKeys.TTXN_ST))){

			ctx.setData("otTxnSt", ctx.getData(GDParamKeys.TTXN_ST));

			ctx.setData(ParamKeys.THD_TXN_CDE, "RVSPPC");
			Map<String, Object> thdReturnMessage = callThdReturnMessage.trade(ctx);
			if(ctx.getState().equals(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL)){
				CommThdRspCdeAction cRspCdeAction = new CommThdRspCdeAction();
				String responseCode = cRspCdeAction.getThdRspCde(thdReturnMessage, 	ctx.getData(ParamKeys.EUPS_BUSS_TYPE).toString());
				log.info("responseCode:["+responseCode+"]");
				if(Constants.RESPONSE_CODE_SUCC.equals(responseCode)){	
					ctx.setData(GDParamKeys.TTXN_ST, "C");
					gdEupsbTrspTxnJnl.setTtxnSt(ctx.getData(GDParamKeys.TTXN_ST).toString());
					gdEupsbTrspTxnJnlRepository.update(gdEupsbTrspTxnJnl);
					
					//更新缴费状态
					GDEupsbTrspFeeInfo gdEupsbTrspFeeInfo = new GDEupsbTrspFeeInfo();
					gdEupsbTrspFeeInfo.setStatus("0");
					gdEupsbTrspFeeInfo.setTlogNo(ctx.getData("oldTxnSqn").toString());
					gdEupsbTrspFeeInfoRepository.updateSt(gdEupsbTrspFeeInfo);
					
				}else{
					if(StringUtil.isEmpty(responseCode)){
						responseCode = ErrorCodes.EUPS_THD_RSP_CODE_ERROR;
					}
					throw new CoreException(responseCode);
				}
				
			}else if(ctx.getState().equals(BPState.BUSINESS_PROCESSNIG_STATE_FAIL)){
//				if(!"000000".equals(thdReturnMessage.get(GDParamKeys.TRSP_CD))){
//					ctx.setData(GDParamKeys.TTXN_ST, ctx.getData("otTxnSt"));
//					ctx.setData(ParamKeys.RSP_MSG, "路桥方返回："+ thdReturnMessage.get(GDParamKeys.TRSP_CD));
//					gdEupsbTrspTxnJnl.setTtxnSt(ctx.getData(GDParamKeys.TTXN_ST).toString());
//					gdEupsbTrspTxnJnlRepository.update(gdEupsbTrspTxnJnl);
//					throw new CoreRuntimeException(ErrorCodes.EUPS_THD_RSP_CODE_ERROR);
//				}else{
//					ctx.setData(GDParamKeys.TTXN_ST, "C");
//					gdEupsbTrspTxnJnl.setTtxnSt(ctx.getData(GDParamKeys.TTXN_ST).toString());
//					gdEupsbTrspTxnJnlRepository.update(gdEupsbTrspTxnJnl);
//				}
//				<If condition="INTCMP(GETSTRPOS(@PARA.TRsp_Suc,$inTRspCd),1,0)">
//                <Set>RspCod=478614</Set><!--抹账失败-->
//                <Set>RspMsg=STRCAT(路桥方方返回: [,$TRspCd,],$TRspMsg)</Set>
//                <Set>TTxnSt=$OTTxnSt</Set>
//                <Exec func="PUB:ExecSql">
//                   <Arg name="SqlCmd" value="SetTTxnSt"/>
//                </Exec>
//                <Return/>
//             </If>
//             <Else>
//                <Set>RspCod=000000</Set>
//                <Set>TTxnSt=C</Set>
//                <Exec func="PUB:ExecSql">
//                   <Arg name="SqlCmd" value="SetTTxnSt"/>
//                </Exec>
//             </Else>
			}else{
				ctx.setData(GDParamKeys.TTXN_ST, ctx.getData("otTxnSt"));
				gdEupsbTrspTxnJnl.setTtxnSt(ctx.getData(GDParamKeys.TTXN_ST).toString());
				gdEupsbTrspTxnJnlRepository.update(gdEupsbTrspTxnJnl);
				throw new CoreRuntimeException(ErrorCodes.EUPS_THD_RSP_CODE_ERROR);
			}
			
			
		}
		
	}

}
