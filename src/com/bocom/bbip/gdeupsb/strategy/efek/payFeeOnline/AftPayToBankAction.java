package com.bocom.bbip.gdeupsb.strategy.efek.payFeeOnline;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
		}
}
