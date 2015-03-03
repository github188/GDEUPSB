package com.bocom.bbip.gdeupsb.service.impl.prof00;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.gdeupsb.entity.GdeupsTlNoManager;
import com.bocom.bbip.gdeupsb.repository.GdeupsTlNoManagerRepository;
import com.bocom.bbip.thd.org.apache.commons.collections.CollectionUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
/**
 * 发票柜员检验
 * @author hefengwen
 *
 */
public class CheckTlrInfServiceActionPROF00  extends BaseAction{
	private static Logger logger = LoggerFactory.getLogger(CheckTlrInfServiceActionPROF00.class);
	
	@Override
	public void execute(Context context) throws CoreException,	CoreRuntimeException {
		logger.info("CheckTlrInfServiceActionPROF00 start ... ...");
		String br = context.getData("br");
		String tlr = context.getData("tlr");
		//TODO:查询柜员信息表
		
		
		
		logger.info("CheckTlrInfServiceActionPROF00 end ... ...");
	}
}
