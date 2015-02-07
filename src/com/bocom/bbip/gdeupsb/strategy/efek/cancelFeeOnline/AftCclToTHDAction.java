package com.bocom.bbip.gdeupsb.strategy.efek.cancelFeeOnline;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.eups.repository.EupsThdTranCtlDetailRepository;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
import com.bocom.jump.bp.core.Executable;

public class AftCclToTHDAction implements Executable {
	private final static Log logger=LogFactory.getLog(AftCclToTHDAction.class);
	@Autowired
	EupsThdTranCtlDetailRepository eupsThdTranCtlDetailRepository;
	/**
	 * 联机单边抹账  后处理
	 */
	@Override
	public void execute(Context context) throws CoreException,
			CoreRuntimeException {
			logger.info("=========Start AftCclToTHDAction");
	}
}
