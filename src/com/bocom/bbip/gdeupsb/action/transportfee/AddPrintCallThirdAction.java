package com.bocom.bbip.gdeupsb.action.transportfee;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.adaptor.ThirdPartyAdaptor;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GDEupsbTrspFeeInfo;
import com.bocom.bbip.gdeupsb.entity.GDEupsbTrspInvChgInfo;
import com.bocom.bbip.gdeupsb.repository.GDEupsbTrspFeeInfoRepository;
import com.bocom.bbip.gdeupsb.repository.GDEupsbTrspInvChgInfoRepository;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class AddPrintCallThirdAction extends BaseAction{
	private final static Log log = LogFactory.getLog(AddPrintCallThirdAction.class);
	
	@Autowired
	GDEupsbTrspInvChgInfoRepository gdEupsbTrspInvChgInfoRepository;
	
	@Autowired
	ThirdPartyAdaptor callThdTradeManager;
	
	@Autowired
	GDEupsbTrspFeeInfoRepository gdEupsbTrspFeeInfoRepository;
	
	public void execute(Context ctx) throws CoreException,CoreRuntimeException{
		log.info("CallThirdAction start......");
		
		//新发票号码需发送路桥方更改号码
				ctx.setState("fail");
					
//		         <!--车辆缴费打发票-->
//		         <Exec func="PUB:CallThirdOther" error="IGNORE"><!-- 调用路桥方交易-->
//		            <Arg name="HTxnCd" value="@PARA.TTxnCd"/>
//		            <Arg name="ObjSvr" value="@PARA.ThdSvr"/>
//		         </Exec>
					Map<String,Object> thdReturnMessage = callThdTradeManager.trade(ctx);
					log.info("callThdTradeManager start......");
					if(ctx.getState().equals(BPState.BUSINESS_PROCESSNIG_STATE_OVERTIME)){
						ctx.setData(ParamKeys.RSP_MSG, "路桥方交易超时");
//						throw new CoreRuntimeException(ErrorCodes.TRANSACTION_ERROR_TIMEOUT);			
						System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@"+ctx.getState());
					}else if(ctx.getState().equals(BPState.BUSINESS_PROCESSNIG_STATE_TRANS_FAIL)){
						ctx.setData(ParamKeys.RSP_MSG, "路桥方交易失败");
//						throw new CoreRuntimeException( ErrorCodes.TRANSACTION_ERROR_OTHER_ERROR);
					}else{
						if(!"000000".equals(thdReturnMessage.get("trspCd"))){
							ctx.setData(ParamKeys.RSP_MSG, "路桥方返回：" + thdReturnMessage.get("trspCd"));
//							throw new CoreRuntimeException( ErrorCodes.TRANSACTION_ERROR_OTHER_ERROR);
							System.out.println("路桥方返回：" + thdReturnMessage.get("trspCd"));
							ctx.setState("error");
							
						}else{
							ctx.setData(ParamKeys.RSP_CDE, Constants.RESPONSE_CODE_SUCC);
							
							GDEupsbTrspFeeInfo gdEupsbTrspFeeInfo = new GDEupsbTrspFeeInfo();

							gdEupsbTrspFeeInfo.setInvNo(ctx.getData(GDParamKeys.INV_NO).toString());
							gdEupsbTrspFeeInfo.setThdKey(ctx.getData(GDParamKeys.THD_KEY).toString());
							gdEupsbTrspFeeInfo.setPrtTlr(ctx.getData(GDParamKeys.TLR_ID).toString());
							gdEupsbTrspFeeInfo.setPrtNod(ctx.getData(GDParamKeys.NOD_NO).toString());
							gdEupsbTrspFeeInfoRepository.updateThdFeeInfo(gdEupsbTrspFeeInfo);
							ctx.setState("complete");
						}
					}
					           
				
//				<Set>OLogNo=$PayLog</Set>
//		        <Set>FilNam=STRCAT(INVO,$TlrId,00)</Set>
				
//				 <Exec func="PUB:GenerateReport">
//		         <Arg name="ObjFil"  value="STRCAT($TSDir,$FilNam)"/>
//		         <Arg name="FmtFil"  value="etc/BRBFINV_RPT.XML"/>
//		         <Arg name="OLogNo"  value="$OLogNo"/>
//		         <Arg name="NodNo"   value="$NodNo"/>
//		      </Exec>
	}

}
