package com.bocom.bbip.gdeupsb.strategy.elcgd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
import com.bocom.jump.bp.core.Executable;

/**
 * 联机抹帐-广州电力，抹第三方帐后处理
 * 
 * @author qc.w
 * 
 */
public class AftCnlElecStrategyAction implements Executable {

	private final static Logger log = LoggerFactory.getLogger(AftCnlElecStrategyAction.class);

	@Override
	public void execute(Context context) throws CoreException, CoreRuntimeException {

		log.info("AftCnlElecStrategyAction start!..");
		// 获取sqn，经过处理后作为银行方流水号发送给第三方
		// String transJnl=context.getData(ParamKeys.SEQUENCE);
		// transJnl=transJnl.substring(2,8)+transJnl.substring(transJnl.length()-4,transJnl.length());
		//
		// log.info("after sub deal,transJnl="+transJnl);
		// context.setData("transJournal11", transJnl);

		context.setData("eleClrDte", context.getData("pwrtxnDate15")); // 供电公司清算日期
		context.setData("thdSqn", context.getData("eleThdSqn37")); // 第三方流水号
	}

}
