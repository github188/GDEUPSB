package com.bocom.bbip.gdeupsb.service.impl.prof00;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.gdeupsb.entity.GdeupsTlNoManager;
import com.bocom.bbip.gdeupsb.repository.GdeupsTlNoManagerRepository;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
/**
 * 发票柜员改密
 * @author hefengwen
 *
 */
public class UpdateTlrPasswdServiceActionPROF00 extends BaseAction {
	private static Logger logger = LoggerFactory.getLogger(UpdateTlrPasswdServiceActionPROF00.class);
	
	@Override
	public void execute(Context context) throws CoreException,	CoreRuntimeException {
		logger.info("UpdateTlrPasswdServiceActionPROF00 start ... ...");
		String newPwd = context.getData("newPwd");
		String nodno = context.getData("br");
		logger.info("newPwd["+newPwd+"]");
		GdeupsTlNoManager gdeupsTlNoManager = new GdeupsTlNoManager();
		gdeupsTlNoManager.setGydm(nodno);
		gdeupsTlNoManager.setPasswd(newPwd);
		get(GdeupsTlNoManagerRepository.class).updatePwd(gdeupsTlNoManager);
		logger.info("UpdateTlrPasswdServiceActionPROF00 end ... ...");
	}
}
