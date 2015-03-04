package com.bocom.bbip.gdeupsb.service.impl.watr00;

import java.util.List;
import java.util.Map;

import com.bocom.bbip.eups.spi.service.agent.DelCusAgentService;
import com.bocom.bbip.eups.spi.vo.CusAgentCollectDomain;
import com.bocom.bbip.eups.spi.vo.CustomerDomain;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

public class CommDelCusAgentServiceActionWATR00 implements DelCusAgentService{

	@Override
	public Map<String, Object> callThd(CustomerDomain customerdomain,
			List<CusAgentCollectDomain> list, Context context)
			throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> preDel(CustomerDomain customerdomain,
			List<CusAgentCollectDomain> list, Context context)
			throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}

}
