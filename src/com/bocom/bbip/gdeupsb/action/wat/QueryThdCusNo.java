package com.bocom.bbip.gdeupsb.action.wat;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.entity.GdEupsWatAgtInf;
import com.bocom.bbip.gdeupsb.repository.GdEupsWatAgtInfRepository;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class QueryThdCusNo extends BaseAction{
	
	private final static Log log = LogFactory.getLog(QueryThdCusNo.class);
	
	@Autowired
	GdEupsWatAgtInfRepository gdEupsWatAgtInfRepository;
	
	public void execute(Context ctx) throws CoreException,CoreRuntimeException{
		log.info("QueryThdCusNo start......");
		GdEupsWatAgtInf gdEupsWatAgtInf = new GdEupsWatAgtInf();
		gdEupsWatAgtInf.setCusAc(ctx.getData(ParamKeys.CUS_AC).toString());
		List<GdEupsWatAgtInf> infoList = gdEupsWatAgtInfRepository.find(gdEupsWatAgtInf);
		ctx.setData("cusInfoList", infoList);
	}

}
