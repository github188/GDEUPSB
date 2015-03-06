package com.bocom.bbip.gdeupsb.ext;

import java.util.HashMap;
import java.util.Map;

import com.bocom.bbip.eups.spi.service.basemanage.ChlAutoSignInOutService;
import com.bocom.bbip.eups.spi.support.ServiceDelegatorAction;
import com.bocom.bbip.eups.spi.support.ServiceRegistry;
import com.bocom.bbip.eups.spi.vo.SignInOutDomain;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
import com.bocom.jump.bp.core.Executable;

public class ChlAutoSignInOutServiceActionExt extends ServiceDelegatorAction {
	public ChlAutoSignInOutServiceActionExt()
	{
	}

	public void signInOutDeal(Context context) throws CoreException, CoreRuntimeException {
		execute(context, "eups.signInOutDealStrategyAction");
		System.out.println("(((((((((((((((((((((((((((((((((context="+context);
		if (StringUtils.isNotBlank(beanName))
		{
			Object signInOutDealImpl = ServiceRegistry.findService(beanName);
			if (signInOutDealImpl instanceof ChlAutoSignInOutService)
			{
				SignInOutDomain signInOutDomain = (SignInOutDomain) BeanUtils.toObject(context.getDataMap(), SignInOutDomain.class);
				Map<String, Object> returnMap = new HashMap<String, Object>();
				((ChlAutoSignInOutService) signInOutDealImpl).signInOutDeal(signInOutDomain, context);
				if (returnMap != null)
					context.setDataMap(returnMap);
			} else if (signInOutDealImpl instanceof Executable)
				((Executable) signInOutDealImpl).execute(context);
		} else
		{
			log.info((new StringBuilder("Did not find a service or strategy implementation for action [")).append(beanName).append("].").toString());
		}
	}

}
