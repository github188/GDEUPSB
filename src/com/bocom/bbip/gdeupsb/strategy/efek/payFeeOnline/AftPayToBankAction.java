package com.bocom.bbip.gdeupsb.strategy.efek.payFeeOnline;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
import com.bocom.jump.bp.core.Executable;
/**
 * @author lyw_i7iiiiii
 */
public class AftPayToBankAction implements Executable{
	private final static Log logger=LogFactory.getLog(AftPayToBankAction.class);
	@Override
	public void execute(Context context) throws CoreException,
			CoreRuntimeException {
			logger.info("==============Start   AftPayToBankAction");
			context.setData(ParamKeys.TXN_DTE, DateUtils.format((Date)context.getData(ParamKeys.TXN_DTE),DateUtils.STYLE_yyyyMMdd));
			String txnDte=context.getData(ParamKeys.TXN_DTE).toString();
			String txnTme=DateUtils.formatAsHHmmss((Date)context.getData(ParamKeys.TXN_TME));;
			context.setData(ParamKeys.TXN_TME, DateUtils.parse((txnDte+txnTme),DateUtils.STYLE_yyyyMMddHHmmss));
			context.setData(GDParamKeys.BAG_TYPE, "1");
			
			BigDecimal respTxnAmt=new BigDecimal(context.getData(ParamKeys.TXN_AMT).toString());
			context.setData(ParamKeys.TXN_AMT, respTxnAmt.scaleByPowerOfTen(2));
			BigDecimal hfe=new BigDecimal(context.getData("hfe").toString());
			BigDecimal withholdMoney=respTxnAmt.add(hfe).scaleByPowerOfTen(2);
			context.setData(GDParamKeys.WITHHOLD_MONEY, withholdMoney);
	}
}
