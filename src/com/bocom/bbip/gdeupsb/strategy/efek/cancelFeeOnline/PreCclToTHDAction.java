package com.bocom.bbip.gdeupsb.strategy.efek.cancelFeeOnline;

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
			context.setData(ParamKeys.TXN_DAT, DateUtils.formatAsSimpleDate(new Date()));
			context.setData(ParamKeys.TXN_TME, DateUtils.formatAsTranstime(new Date()));
			System.out.println(context.getData(ParamKeys.TXN_TME));
			context.setData(ParamKeys.THD_CUS_NO, context.getData(GDParamKeys.PAY_NO));

		}
}
