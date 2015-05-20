package com.bocom.bbip.gdeupsb.service.impl.watr00;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.gdeupsb.entity.GdEupsWatAgtInf;
import com.bocom.bbip.gdeupsb.repository.GdEupsWatAgtInfRepository;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class PreCallThdAction extends BaseAction{
	private final static Log log = LogFactory.getLog(PreCallThdAction.class);

	public void execute(Context ctx) throws CoreException,CoreRuntimeException{
		log.info("PreCallThdAction start......");
		GdEupsWatAgtInf gdeups = new GdEupsWatAgtInf();
		gdeups.setAgtSts("F");  
		gdeups.setAgdAgrNo((String)ctx.getData("agdAgrNo"));
		get(GdEupsWatAgtInfRepository.class).update(gdeups);
		log.info("PreCallThdAction end......");
		
	}
}
