package com.bocom.bbip.gdeupsb.action.efek;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
/**
 *得到单位编号 
 */
public class QryComNoByComNmeAction extends BaseAction{
		@Override
		public void execute(Context context) throws CoreException,
				CoreRuntimeException {
				log.info("===================Start   QryComNoBycomNmeAction");
				String comNme=context.getData(ParamKeys.COMPANY_NAME).toString();
				String comNo="";
				if(comNme.equals("佛山") || comNme.equals("1")){
						comNo="foshan";
				}else if(comNme.equals("清远") || comNme.equals("2")){
						comNo="qingyuan";
				}else if(comNme.equals("中山") || comNme.equals("3")){
						comNo="GDELEC00";
				}
				context.setData(ParamKeys.COMPANY_NO, comNo);
		}
}
