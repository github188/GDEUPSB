package com.bocom.bbip.gdeupsb.strategy.efek.payFeeOnline;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ParamKeys;
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
		double i=Double.parseDouble(context.getData(ParamKeys.TXN_AMT).toString());
		double d=i/100;
		DecimalFormat df=new DecimalFormat("#.00");
		BigDecimal txnAmt=new BigDecimal(df.format(d));
		context.setData(ParamKeys.TXN_AMT,txnAmt );
		
		String rspCod=context.getData(ParamKeys.RSP_CDE).toString().trim();
		if(rspCod.equals(Constants.HOST_RESPONSE_CODE_SUCC)){
					context.setData(ParamKeys.TXN_STS, "S");
					context.setData("ApCode", "46");
					context.setData("OFmtCd", "999");
		}else{
					throw new CoreException("失败~~~"+context.getData(GDParamKeys.SUCFLG));
		}
		String thdTxnDte=context.getData(ParamKeys.THD_TXN_DATE).toString();
		context.setData(ParamKeys.THD_TXN_DATE, DateUtils.parse(thdTxnDte,DateUtils.STYLE_yyyyMMdd));
		context.setData(ParamKeys.THD_TXN_TIME, DateUtils.parse(thdTxnDte+context.getData(ParamKeys.TXN_TME).toString(),DateUtils.STYLE_yyyyMMddHHmmss));
		String txnDte=context.getData("oldTxnDte").toString();
//		context.setData("txnDte", txnDte);
//		context.setData("txnTme", txnDte+context.getData("oldTxnTme").toString());
//		if(context.getData(ParamKeys.PAY_MDE).toString().equals("4")){
				context.setData("txnDte", DateUtils.parse(txnDte,DateUtils.STYLE_yyyyMMdd));
				context.setData("txnTme", DateUtils.parse((txnDte+context.getData("oldTxnTme").toString()),DateUtils.STYLE_yyyyMMddHHmmss));
//		}
		
	}
}
