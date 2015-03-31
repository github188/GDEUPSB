package com.bocom.bbip.gdeupsb.strategy.efek.payFeeOnline;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.repository.EupsThdTranCtlInfoRepository;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
import com.bocom.jump.bp.core.Executable;

public class PreCheckDealAction implements Executable{
	private final static Log logger=LogFactory.getLog(PreCheckDealAction.class);
	@Autowired
	EupsThdTranCtlInfoRepository eupsThdTranCtlInfoRepository;
	/**
	 * 交易前策略处理
	 */
	@Override
	public void execute(Context context) throws CoreException,
			CoreRuntimeException {
		logger.info("============Start  PreCheckDealAction");
		
			context.setData(ParamKeys.THD_CUS_NO, context.getData(GDParamKeys.PAY_NO));
			context.setData(ParamKeys.RSV_FLD4, context.getData(GDParamKeys.BUS_TYPE));
			context.setData(ParamKeys.RSV_FLD5, context.getData(GDParamKeys.PAY_TYPE));
			context.setData(ParamKeys.RSV_FLD6, context.getData(GDParamKeys.ELECTRICITY_YEARMONTH));
			context.setData(ParamKeys.BANK_NO, "301");
			context.setData("thdObkCde", context.getData(ParamKeys.BANK_NO));
				context.setData(GDParamKeys.TOTNUM, "1");
				//日期时间格式修改
				context.setData(ParamKeys.CCY, GDConstants.RENMINBI);
				
//				context.setData(ParamKeys.PAYFEE_TYPE, Constants.TXN_PAYFEE_TYPE_PAYMENT);
				//TODO  试用
				context.setData("extFields", "01441800999");
				
				logger.info("~~~~~~~~~~~交易日期："+context.getData(ParamKeys.TXN_DATE)+"~~~~~~~~~~~交易时间："+context.getData(ParamKeys.TXN_TIME));
	}
}
