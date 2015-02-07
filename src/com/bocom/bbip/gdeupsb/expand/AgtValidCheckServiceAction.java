package com.bocom.bbip.gdeupsb.expand;

import java.util.HashMap;
import java.util.Map;

import com.bocom.bbip.eups.spi.support.ServiceDelegatorAction;
import com.bocom.bbip.eups.spi.support.ServiceRegistry;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
import com.bocom.jump.bp.core.Executable;

/**
 * 签约一站通-协议验证
 * 
 * @author qc.w
 * 
 */
public class AgtValidCheckServiceAction extends ServiceDelegatorAction {
	public AgtValidCheckServiceAction()
	{
	}

	public Map<String, Object> agtValidCheckService(Context context) throws CoreException, CoreRuntimeException {
		execute(context, "eups.batFilDelInitStrategyAction");
		if (StringUtils.isNotBlank(beanName))
		{
			Object agtValidCheckImpl = ServiceRegistry.findService(beanName);
			if (agtValidCheckImpl instanceof AgtValidCheckService)
			{
				Map<String, Object> returnMap = new HashMap<String, Object>();
				((AgtValidCheckService) agtValidCheckImpl).agtValidCheckService(context);
				if (returnMap != null)
					context.setDataMap(returnMap);
			} else if (agtValidCheckImpl instanceof Executable)
				((Executable) agtValidCheckImpl).execute(context);
		} else
		{
			log.info((new StringBuilder("Did not find a service or strategy implementation for action [")).append(beanName).append("].").toString());
		}
		return null;
	}
}
