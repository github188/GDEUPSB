package com.bocom.bbip.gdeupsb.strategy.efek.payFeeOnline;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
import com.bocom.jump.bp.core.Executable;

public class AftPayToBankAction implements Executable{
	private final static Log logger=LogFactory.getLog(AftPayToBankAction.class);
	@Override
	public void execute(Context context) throws CoreException,
			CoreRuntimeException {
			logger.info("==============Start   AftPayToBankAction");
			context.setData(ParamKeys.TXN_DTE, DateUtils.format((Date)context.getData(ParamKeys.TXN_DTE),DateUtils.STYLE_yyyyMMdd));
			context.setData(ParamKeys.TXN_TME, DateUtils.formatAsHHmmss((Date)context.getData(ParamKeys.TXN_TME)));
	}
}
