package com.bocom.bbip.gdeupsb.strategy.elcgd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
import com.bocom.jump.bp.core.Executable;

/**
 * 调用第三方接口后信息设置
 * 
 * @author qc.w
 *
 */
public class AftThdElecStrategyAction implements Executable {

	private final static Logger log = LoggerFactory.getLogger(AftThdElecStrategyAction.class);

	@Override
	public void execute(Context context) throws CoreException, CoreRuntimeException {
		log.info("AftThdElecStrategyAction start!..");
		// TODO:确认是否还有其他功能，字段设置等
	}

}
