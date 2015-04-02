package com.bocom.bbip.gdeupsb.action.efek;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
/**
 *得到单位编号 
 */
public class QryComNoByAreaAction extends BaseAction{
		@Override
		public void execute(Context context) throws CoreException,
				CoreRuntimeException {
				log.info("===================Start   QryComNoByAreaAction");
				String area=context.getData("area").toString();
				String comNo="";
//				if(area.equals("佛山") || area.equals("1")){
//						comNo="foshan";
//				}else if(area.equals("清远") || area.equals("2")){
//						comNo="qingyuan";
//				}else if(area.equals("中山") || area.equals("3")){
//						comNo="GDELEC00";
//				}
				context.setData(ParamKeys.COMPANY_NO, comNo);
				log.info("===================End   QryComNoByAreaAction");
		}
}
