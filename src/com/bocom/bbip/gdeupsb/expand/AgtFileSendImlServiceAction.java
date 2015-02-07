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
 * 签约一站通-特色协议维护处理，根据特色业务分别找到对应的拓展实现
 * 
 * @author qc.w
 * 
 */
public class AgtFileSendImlServiceAction extends ServiceDelegatorAction {
	public AgtFileSendImlServiceAction()
	{
	}

	public Map<String, Object> agtFleSndDelService(Context context) throws CoreException, CoreRuntimeException {
		execute(context, "eups.batFilDelInitStrategyAction");
		if (StringUtils.isNotBlank(beanName))
		{
			Object agtFleSndImpl = ServiceRegistry.findService(beanName);
			if (agtFleSndImpl instanceof AgtFileSendImlService)
			{
				Map<String, Object> returnMap = new HashMap<String, Object>();
				((AgtFileSendImlService) agtFleSndImpl).agtFleSndDelService(context);
				if (returnMap != null)
					context.setDataMap(returnMap);
			} else if (agtFleSndImpl instanceof Executable)
				((Executable) agtFleSndImpl).execute(context);
		} else
		{
			log.info((new StringBuilder("Did not find a service or strategy implementation for action [")).append(beanName).append("].").toString());
		}
		return null;
	}
}
