package com.bocom.bbip.gdeupsb.action.efek;

import java.util.Date;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class AdvanceTradeTimeAction extends BaseAction{
		public void execute(Context context)throws CoreException,CoreRuntimeException {
				log.info("==========Start AdvanceTradeTimeAction");
				String txnDte=DateUtils.format((Date)context.getData(ParamKeys.TXN_DTE),DateUtils.STYLE_yyyyMMdd);
				context.setData(ParamKeys.TXN_DTE, txnDte);
				String txnTme=context.getData(ParamKeys.TXN_TME).toString();
				String txnTmeSend=txnTme.substring(0,6);
				context.setData(ParamKeys.TXN_TME, txnTmeSend);
		}
}
