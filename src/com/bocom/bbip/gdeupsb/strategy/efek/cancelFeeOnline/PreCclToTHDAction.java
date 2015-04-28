package com.bocom.bbip.gdeupsb.strategy.efek.cancelFeeOnline;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.repository.EupsThdTranCtlInfoRepository;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
import com.bocom.jump.bp.core.Executable;

public class PreCclToTHDAction implements Executable {
	private final static Log logger=LogFactory.getLog(PreCclToTHDAction.class);
	@Autowired
	EupsThdTranCtlInfoRepository eupsThdTranCtlInfoRepository;
	/**
	 * 联机单边抹账 前处理
	 */
	@Override
	public void execute(Context context) throws CoreException,
			CoreRuntimeException {
			logger.info("==========Start  PreCclToTHDAction");
			context.setData(GDParamKeys.TOTNUM, "1");
			context.setData(ParamKeys.TXN_DTE, DateUtils.parse(DateUtils.formatAsSimpleDate(new Date())));
			context.setData(ParamKeys.TXN_TME, DateUtils.parse(DateUtils.formatAsTranstime(new Date())));
			context.setData(ParamKeys.THD_CUS_NO, context.getData(GDParamKeys.PAY_NO));
			
			BigDecimal txnAmt=new BigDecimal(context.getData(ParamKeys.TXN_AMT).toString()).scaleByPowerOfTen(-2);
			context.setData(ParamKeys.TXN_AMT,txnAmt );
			
			context.setData("thdObkCde",context.getData(ParamKeys.BANK_NO));
			context.setData(ParamKeys.RSV_FLD4,context.getData(GDParamKeys.BUS_TYPE));
			context.setData(ParamKeys.RSV_FLD5,context.getData(GDParamKeys.PAY_TYPE));
			context.setData(ParamKeys.RSV_FLD6,context.getData(GDParamKeys.ELECTRICITY_YEARMONTH));
			context.setData(ParamKeys.CUS_NME, context.getData(GDParamKeys.SETTLE_ACCOUNTS_NAME));
			context.setData(ParamKeys.RSV_FLD1, context.getData("traPerIdenty"));
	}
}
