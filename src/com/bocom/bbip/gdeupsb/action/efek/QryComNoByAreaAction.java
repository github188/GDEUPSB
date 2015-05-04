package com.bocom.bbip.gdeupsb.action.efek;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
/**
 *得到单位编号  不用的交易，由查询客户基本信息获取
 */
public class QryComNoByAreaAction extends BaseAction{
		@Override
		public void execute(Context context) throws CoreException,
				CoreRuntimeException {
				log.info("===================Start   QryComNoByAreaAction");
				log.info("===================End   QryComNoByAreaAction");
		}
}
