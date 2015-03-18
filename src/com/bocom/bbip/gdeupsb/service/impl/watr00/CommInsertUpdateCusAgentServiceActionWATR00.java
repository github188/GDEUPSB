package com.bocom.bbip.gdeupsb.service.impl.watr00;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
/**
 * 协议新增修改
 * @author hefengwen
 *
 */
public class CommInsertUpdateCusAgentServiceActionWATR00 extends BaseAction{
	
	private static Logger logger = LoggerFactory.getLogger(CommInsertUpdateCusAgentServiceActionWATR00.class);
	
	@Override
	public void execute(Context context) throws CoreException,	CoreRuntimeException {
		String oprTyp = context.getData("oprTyp");//操作类型：0-新增，1-修改
		logger.info("oprTyp["+oprTyp+"]");
		if("0".equals(oprTyp)){
			get(BBIPPublicService.class).synExecute("eups.commInsertCusAgent", context);
		}else if("1".equals(oprTyp)){
			get(BBIPPublicService.class).synExecute("eups.commUpdateCusAgent", context);
		}else{
			throw new CoreException("操作类型有误!");
		}
	}
	
}
