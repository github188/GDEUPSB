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
import com.bocom.bbip.gdeupsb.repository.GDEupsbTrspFeeInfoRepository;
import com.bocom.bbip.gdeupsb.repository.GDEupsbTrspInvChgInfoRepository;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.euif.component.util.StringUtil;
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
			CommThdRspCdeAction cRspCdeAction = new CommThdRspCdeAction();
			String responseCode = cRspCdeAction.getThdRspCde(thdReturnMessage, 	ctx.getData(ParamKeys.EUPS_BUSS_TYPE).toString());
			log.info("responseCode:["+responseCode+"]");
			if (Constants.RESPONSE_CODE_SUCC.equals(responseCode)) {
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
//				ctx.setData(ParamKeys.RSP_MSG,
//						"路桥方返回：" + thdReturnMessage.get("thdRspCde"));
//				// throw new CoreRuntimeException(
//				// ErrorCodes.TRANSACTION_ERROR_OTHER_ERROR);
//				System.out.println("路桥方返回：" + thdReturnMessage.get("trspCd"));
				if(StringUtil.isEmpty(responseCode)){
					responseCode = ErrorCodes.EUPS_THD_RSP_CODE_ERROR;
				}
				throw new CoreException(responseCode);
				// ctx.setState("error");
			}
		}else if (ctx.getState().equals(
				BPState.BUSINESS_PROCESSNIG_STATE_TRANS_FAIL)) {
			ctx.setData(ParamKeys.RSP_MSG, "路桥方交易失败");
			throw new CoreException(ErrorCodes.EUPS_THD_SYS_ERROR);
		} else {
			ctx.setData(ParamKeys.RSP_MSG, "路桥方交易超时");
			throw new CoreException(ErrorCodes.EUPS_THD_SYS_ERROR);

		} 
	}

}
