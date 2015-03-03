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
 * 柜员验密
 * @author hefengwen
 *
 */
public class CheckTlrPasswdServiceActionPROF00 extends BaseAction {
	private static Logger logger = LoggerFactory.getLogger(CheckTlrPasswdServiceActionPROF00.class);
	
	@Override
	public void execute(Context context) throws CoreException,	CoreRuntimeException {
		logger.info("CheckTlrPasswdServiceActionPROF00 start ... ...");
		String passwd = context.getData("passwd");
		String nodno = context.getData("br");
		logger.info("passwd["+passwd+"]");
		GdeupsTlNoManager gdeupsTlNoManager = new GdeupsTlNoManager();
		gdeupsTlNoManager.setGydm(nodno);
//		gdeupsTlNoManager.setPasswd(passwd);
		List<GdeupsTlNoManager> gdeupsTlNoManagers = get(GdeupsTlNoManagerRepository.class).find(gdeupsTlNoManager);
		if(CollectionUtils.isEmpty(gdeupsTlNoManagers)){
			logger.error("柜员不存在");
			throw new CoreException("");
		}
		gdeupsTlNoManager = gdeupsTlNoManagers.get(0);
		if(!gdeupsTlNoManager.getPasswd().equals(passwd)){
			logger.error("密码错误");
			throw new CoreException("");
		}
		logger.info("CheckTlrPasswdServiceActionPROF00 end ... ...");
	}
}
