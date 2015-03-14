package com.bocom.bbip.gdeupsb.action.efek;

import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
/**
 * @author liyawei
 */
public class CusAgentServiceAction extends BaseAction{
	@Autowired
	BBIPPublicService bbipPublicService;
		/**
		 * 协议新增修改注销
		 */
		public void execute(Context context)throws CoreException,CoreRuntimeException{
			log.info("============Start  CusAgentServiceAction ");
				context.setData(GDParamKeys.SVRCOD, "30");
				String oprTyp=context.getData("oprTyp").toString();
				String mothed="";
				if("0".equals(oprTyp)){
					mothed="eups.commInsertCusAgent";
				}else if("1".equals(oprTyp)){
					mothed="eups.commUpdateCusAgent";
				}else {
					mothed="eups.commDelCusAgent";
				}
				context.setData(GDParamKeys.SVRCOD, "30");
				bbipPublicService.synExecute(mothed, context);
				
				log.info("============End  CusAgentServiceAction");
		}
}