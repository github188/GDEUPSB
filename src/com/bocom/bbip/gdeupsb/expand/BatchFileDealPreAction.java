package com.bocom.bbip.gdeupsb.expand;

import java.util.HashMap;
import java.util.Map;

import com.bocom.bbip.eups.spi.support.ServiceDelegatorAction;
import com.bocom.bbip.eups.spi.support.ServiceRegistry;
import com.bocom.bbip.gdeupsb.expand.BatchAccountDealService;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
import com.bocom.jump.bp.core.Executable;

/**
 * 文件批扣-数据准备
 * 
 * @author qc.w
 * 
 */

// extends ServiceDelegatorAction

public class BatchFileDealPreAction extends ServiceDelegatorAction {
	public BatchFileDealPreAction()
	{
	}

	/**
	 * 初始化，信息校验，初始化等
	 * 
	 * @param context
	 * @throws CoreException
	 * @throws CoreRuntimeException
	 */
	public void initCheck(Context context) throws CoreException, CoreRuntimeException {
		execute(context, "eups.batFilDelInitStrategyAction");
		if (StringUtils.isNotBlank(beanName))
		{
			Object batchPreDeal = ServiceRegistry.findService(beanName);
			if (batchPreDeal instanceof BatchAccountDealService)
			{
				Map<String, Object> returnMap = new HashMap<String, Object>();
				returnMap = ((BatchAccountDealService) batchPreDeal).initDeal(context);
				if (returnMap != null)
					context.setDataMap(returnMap);
			} else if (batchPreDeal instanceof Executable)
				((Executable) batchPreDeal).execute(context);
		} else
		{
			log.info((new StringBuilder("Did not find a service or strategy implementation for action [")).append(beanName).append("].").toString());
		}

	}
	
	/**
	 * 获取批扣文件
	 * @param context
	 * @throws CoreException
	 */
	public void getAcFile(Context context) throws CoreException {
		execute(context, "eups.batFilDelGetStrategyAction");
		if (StringUtils.isNotBlank(beanName))
		{
			Object batchPreDeal = ServiceRegistry.findService(beanName);
			if (batchPreDeal instanceof BatchAccountDealService)
			{
				Map<String, Object> returnMap = new HashMap<String, Object>();
				returnMap = ((BatchAccountDealService) batchPreDeal).initDeal(context);
				if (returnMap != null)
					context.setDataMap(returnMap);
			} else if (batchPreDeal instanceof Executable)
				((Executable) batchPreDeal).execute(context);
		} else
		{
			log.info((new StringBuilder("Did not find a service or strategy implementation for action [")).append(beanName).append("].").toString());
		}
		
	}

}
