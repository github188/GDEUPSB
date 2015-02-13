package com.bocom.bbip.gdeupsb.action.efek;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class AdvanceTradeStrCastDateAction extends BaseAction{
		public void execute(Context context)throws CoreException,CoreRuntimeException {
				log.info("==========StartAdvanceTradeStrCastDateAction");
				String txnDte=context.getData(ParamKeys.TXN_DTE).toString();
				context.setData(ParamKeys.TXN_DTE, DateUtils.parse(txnDte,DateUtils.STYLE_yyyyMMdd));
				String txnTme=context.getData(ParamKeys.TXN_TME).toString();
				txnTme=txnDte+txnTme;
				context.setData(ParamKeys.TXN_TME, DateUtils.parse(txnTme,DateUtils.STYLE_yyyyMMddHHmmss));
				
				String thdTxnDate=context.getData(ParamKeys.THD_TXN_DATE).toString();
				String thdTxnTime=thdTxnDate+context.getData(ParamKeys.THD_TXN_TIME).toString();
				context.setData(ParamKeys.THD_TXN_DATE, DateUtils.parse(thdTxnDate,DateUtils.STYLE_yyyyMMdd));
				context.setData(ParamKeys.THD_TXN_TIME, DateUtils.parse(thdTxnTime,DateUtils.STYLE_yyyyMMddHHmmss));

		}
}
