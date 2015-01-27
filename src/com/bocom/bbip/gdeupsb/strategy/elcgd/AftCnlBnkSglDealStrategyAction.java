package com.bocom.bbip.gdeupsb.strategy.elcgd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
import com.bocom.jump.bp.core.Executable;

public class AftCnlBnkSglDealStrategyAction implements Executable {

	private final static Logger log = LoggerFactory.getLogger(AftCnlBnkSglDealStrategyAction.class);

	
	@Override
	public void execute(Context context) throws CoreException, CoreRuntimeException {
		log.info("AftCnlBnkSglDealStrategyAction start!..");
		
		//TODO:银行方单边扣款销帐返回字段处理
		context.setData("transJournal11", "");   
		
	}
}
