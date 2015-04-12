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
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GDEupsbTrspFeeInfo;
import com.bocom.bbip.gdeupsb.repository.GDEupsbTrspFeeInfoRepository;
import com.bocom.bbip.gdeupsb.repository.GDEupsbTrspInvChgInfoRepository;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class AddPrintCallThirdAction extends BaseAction {
	private final static Log log = LogFactory
			.getLog(AddPrintCallThirdAction.class);

	@Autowired
	GDEupsbTrspInvChgInfoRepository gdEupsbTrspInvChgInfoRepository;

	@Autowired
	ThirdPartyAdaptor callThdTradeManager;

	@Autowired
	GDEupsbTrspFeeInfoRepository gdEupsbTrspFeeInfoRepository;

	public void execute(Context ctx) throws CoreException, CoreRuntimeException {
		log.info("CallThirdAction start......");

		// 新发票号码需发送路桥方更改号码
		ctx.setState("fail");

		// <!--车辆缴费打发票-->
		// <Exec func="PUB:CallThirdOther" error="IGNORE"><!-- 调用路桥方交易-->
		// <Arg name="HTxnCd" value="@PARA.TTxnCd"/>
		// <Arg name="ObjSvr" value="@PARA.ThdSvr"/>
		// </Exec>
		String tactDt = DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMdd);
		ctx.setData(GDParamKeys.TACT_DT, tactDt);
		ctx.setData(ParamKeys.THD_TXN_CDE, "ChgIPC");
		Map<String, Object> thdReturnMessage = callThdTradeManager.trade(ctx);
		log.info("callThdTradeManager start......");
		if(ctx.getState().equals(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL)){
			if ("000".equals(thdReturnMessage.get("trspCd"))) {
				ctx.setData(ParamKeys.RSP_CDE, "000");

				GDEupsbTrspFeeInfo gdEupsbTrspFeeInfo = new GDEupsbTrspFeeInfo();

				gdEupsbTrspFeeInfo.setInvNo((String) ctx
						.getData(GDParamKeys.INV_NO));
				gdEupsbTrspFeeInfo.setThdKey((String) ctx
						.getData(GDParamKeys.THD_KEY));
				gdEupsbTrspFeeInfo.setPrtTlr((String) ctx
						.getData(GDParamKeys.TLR_ID));
				gdEupsbTrspFeeInfo.setPrtNod((String) ctx
						.getData(GDParamKeys.NOD_NO));
				gdEupsbTrspFeeInfoRepository
						.updateThdFeeInfo(gdEupsbTrspFeeInfo);
				ctx.setState("complete");

			} else {
				ctx.setData(ParamKeys.RSP_MSG,
						"路桥方返回：" + thdReturnMessage.get("trspCd"));
				// throw new CoreRuntimeException(
				// ErrorCodes.TRANSACTION_ERROR_OTHER_ERROR);
				System.out.println("路桥方返回：" + thdReturnMessage.get("trspCd"));
				throw new CoreRuntimeException(
						ErrorCodes.TRANSACTION_ERROR_OTHER_ERROR);
				// ctx.setState("error");
			}
		}else if (ctx.getState().equals(
				BPState.BUSINESS_PROCESSNIG_STATE_TRANS_FAIL)) {
			ctx.setData(ParamKeys.RSP_MSG, "路桥方交易失败");
			throw new CoreRuntimeException(
					ErrorCodes.TRANSACTION_ERROR_OTHER_ERROR);
		} else {
			ctx.setData(ParamKeys.RSP_MSG, "路桥方交易超时");
			throw new CoreRuntimeException(ErrorCodes.TRANSACTION_ERROR_TIMEOUT);

		} 
		

		// <Set>OLogNo=$PayLog</Set>
		// <Set>FilNam=STRCAT(INVO,$TlrId,00)</Set>

		// <Exec func="PUB:GenerateReport">
		// <Arg name="ObjFil" value="STRCAT($TSDir,$FilNam)"/>
		// <Arg name="FmtFil" value="etc/BRBFINV_RPT.XML"/>
		// <Arg name="OLogNo" value="$OLogNo"/>
		// <Arg name="NodNo" value="$NodNo"/>
		// </Exec>
	}

}
