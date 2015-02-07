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
public class AgtMdyDealImlServiceAction extends ServiceDelegatorAction {
	public AgtMdyDealImlServiceAction()
	{
	}

	/**
	 * 根据不同的业务类型分别进行特色处理
	 * 
	 * @param context
	 * @throws CoreException
	 * @throws CoreRuntimeException
	 */
	public Map<String,Object> agtDelByGdsIdService(Context context) throws CoreException, CoreRuntimeException {
		 execute(context, "eups.batFilDelInitStrategyAction");
		if (StringUtils.isNotBlank(beanName))
		{
			Object agtMdyDeal = ServiceRegistry.findService(beanName);
			if (agtMdyDeal instanceof AgtMdyDealImlService)
			{
				Map<String, Object> returnMap = new HashMap<String, Object>();
				 ((AgtMdyDealImlService) agtMdyDeal).agtDelByGdsIdService(context);
				if (returnMap != null)
					context.setDataMap(returnMap);
			} else if (agtMdyDeal instanceof Executable)
				((Executable) agtMdyDeal).execute(context);
		} else
		{
			log.info((new StringBuilder("Did not find a service or strategy implementation for action [")).append(beanName).append("].").toString());
		}
		return null;
	}
}
