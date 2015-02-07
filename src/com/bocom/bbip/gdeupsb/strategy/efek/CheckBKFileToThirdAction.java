package com.bocom.bbip.gdeupsb.strategy.efek;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
import com.bocom.jump.bp.core.Executable;

public class CheckBKFileToThirdAction implements Executable {
	private final static Log logger=LogFactory.getLog(CheckBKFileToThirdAction.class);
	/**
	 * 对账信息查询
	 */
	@Override
	public void execute(Context arg0) throws CoreException,
			CoreRuntimeException {
			logger.info("=========Start CheckBKFileToThirdAction");
	}

}
