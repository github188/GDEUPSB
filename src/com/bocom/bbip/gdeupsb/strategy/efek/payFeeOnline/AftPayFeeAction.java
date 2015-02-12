package com.bocom.bbip.gdeupsb.strategy.efek.payFeeOnline;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
import com.bocom.jump.bp.core.Executable;

public class AftPayFeeAction implements Executable{
	private final static Log logger=LogFactory.getLog(AftPayFeeAction.class);
	/**
	 * 记账后处理
	 */
	@Override
	public void execute(Context context) throws CoreException,
			CoreRuntimeException {
		// TODO Auto-generated method stub
		logger.info("============Start AftPayFeeAction");

		String rspCod=context.getData(ParamKeys.RSP_CDE).toString().trim();
		if(rspCod.equals(GDConstants.SUCCESS_CODE)){
					context.setData(ParamKeys.TXN_STS, "S");
					context.setData("ApCode", "46");
					context.setData("OFmtCd", "999");
		}else{
					throw new CoreException("~~~~~~~~~~失败~~~"+context.getData(GDParamKeys.SUCFLG));
		}
		
		Date txnDte=DateUtils.parse(context.getData(ParamKeys.TXN_DTE).toString());
		Date txnTme=DateUtils.parse(context.getData(ParamKeys.TXN_DTE).toString()+context.getData(ParamKeys.TXN_DTE).toString(),DateUtils.STYLE_yyyyMMddHHmmss);
		context.setData(ParamKeys.TXN_DTE, txnDte);
		context.setData(ParamKeys.TXN_TME, txnTme);
		
		Date thdTxnDte=DateUtils.parse(context.getData(ParamKeys.THD_TXN_DATE).toString());
		Date thdTxnTme=DateUtils.parse(context.getData(ParamKeys.THD_TXN_DATE).toString()+context.getData(ParamKeys.THD_TXN_TIME).toString(),DateUtils.STYLE_yyyyMMddHHmmss);
		context.setData(ParamKeys.THD_TXN_DATE, thdTxnDte);
		context.setData(ParamKeys.THD_TXN_TIME, thdTxnTme);

	}
}
