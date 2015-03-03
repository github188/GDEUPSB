package com.bocom.bbip.gdeupsb.action.transportfee;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.adaptor.ThirdPartyAdaptor;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.common.GDErrorCodes;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GDEupsbTrspInvChgInfo;
import com.bocom.bbip.gdeupsb.repository.GDEupsbTrspInvChgInfoRepository;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class PrintAddInvCancelAction extends BaseAction {
	private final static Log log = LogFactory.getLog(PrintAddInvCancelAction.class);

	@Autowired
	GDEupsbTrspInvChgInfoRepository gdEupsbTrspInvChgInfoRepository;
	@Autowired
	BBIPPublicService bbipPublicService;

	@Autowired
	ThirdPartyAdaptor callThdTradeManager;

	public void execute(Context ctx) throws CoreException, CoreRuntimeException {
		log.info("PrintAddInvCancelAction start......");
		// Date today = bbipPublicService.getAcDate();
		Date today = new Date();
		// SELECT OLogNo,InvNo,OInvNO,TCusId,CarTyp,TLogNo,TxnDat,NodNo,TlrId
		// FROM rbfbInvChg444
		// WHERE LogNo='%s'
		GDEupsbTrspInvChgInfo gdEupsbTrspInvChgInfo = new GDEupsbTrspInvChgInfo();
		gdEupsbTrspInvChgInfo.setSqn(ctx.getData(GDParamKeys.SQN).toString());
		List<GDEupsbTrspInvChgInfo> invChgInfoList = gdEupsbTrspInvChgInfoRepository.find(gdEupsbTrspInvChgInfo);
		// TODO:OLogNo找不到 ,且应该还再查出actDat
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@" + today);
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@" + invChgInfoList.get(0).getTactDt());
		if (!DateUtils.isSameDate(today, invChgInfoList.get(0).getTactDt())) { // 此处ics中是actDat，还未明确
			ctx.setData(ParamKeys.RSP_MSG, "禁止跨日抹帐");
			throw new CoreRuntimeException(GDErrorCodes.DATE_ERROR);
		} else {
			ctx.setData(GDParamKeys.INV_NO, invChgInfoList.get(0).getInvNo());
			ctx.setData(GDParamKeys.OINV_NO, invChgInfoList.get(0).getOinvNo());
			ctx.setData(GDParamKeys.CAR_NO, invChgInfoList.get(0).getCarNo());
			ctx.setData(GDParamKeys.CAR_TYP, invChgInfoList.get(0).getCarTyp());
			ctx.setData(GDParamKeys.TLOG_NO, invChgInfoList.get(0).getTlogNo());
			ctx.setData(GDParamKeys.NOD_NO, invChgInfoList.get(0).getNodNo());
			ctx.setData(GDParamKeys.TLR_ID, invChgInfoList.get(0).getTlrId());
			ctx.setData(GDParamKeys.ACT_DAT, invChgInfoList.get(0).getActDat());

		}

		Map<String, Object> thdReturnMessage = callThdTradeManager.trade(ctx);
		if (ctx.getState().equals(BPState.BUSINESS_PROCESSNIG_STATE_OVERTIME)) {
			ctx.setData(ParamKeys.RSP_MSG, "路桥方交易超时");
			throw new CoreRuntimeException(ErrorCodes.TRANSACTION_ERROR_TIMEOUT);

		} else if (ctx.getState().equals(BPState.BUSINESS_PROCESSNIG_STATE_TRANS_FAIL)) {
			ctx.setData(ParamKeys.RSP_MSG, "路桥方交易失败");
			throw new CoreRuntimeException(ErrorCodes.TRANSACTION_ERROR_OTHER_ERROR);
		} else {
			if (!"000000".equals(thdReturnMessage.get("trspCd"))) {
				ctx.setData(ParamKeys.RSP_MSG, "路桥方返回：" + ctx.getData(GDParamKeys.TRSP_CD));
				throw new CoreRuntimeException(ErrorCodes.TRANSACTION_ERROR_OTHER_ERROR);

			} else {
				ctx.setData(ParamKeys.RSP_CDE, Constants.RESPONSE_CODE_SUCC);
			}
		}

	}

}
