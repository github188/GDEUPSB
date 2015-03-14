package com.bocom.bbip.gdeupsb.action.efek;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.utils.DateUtils;
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
					context.setData(GDParamKeys.NEWBANKNO, "301");
				}else if("1".equals(oprTyp)){
					mothed="eups.commUpdateCusAgent";
					context.setData(GDParamKeys.NEWBANKNO, "301");
				}else {
					mothed="eups.commDelCusAgent";
				}
				constantOfSoapUI(context);
				bbipPublicService.synExecute(mothed, context);
				
				log.info("============End  CusAgentServiceAction");
		}
		
	    /**
		 *报文信息 
		*/
		public void constantOfSoapUI(Context context){
				//报文头常量
			context.setData(GDParamKeys.TREATY_VERSION, GDConstants.TREATY_VERSION);//协议版本
			context.setData(GDParamKeys.TRADE_PERSON_IDENTIFY, GDConstants.TRADE_PERSON_IDENTIFY);//交易人标识
			context.setData(GDParamKeys.BAG_TYPE, GDConstants.BAG_TYPE);//数据包类型
			context.setData(GDParamKeys.TRADE_START,GDConstants.TRADE_START);//交易发起方
				
				context.setData(GDParamKeys.TRADE_SEND_DATE,DateUtils.format(new Date(),DateUtils.STYLE_yyyyMMdd));//交易发送日期
				context.setData(GDParamKeys.TRADE_SEND_TIME, DateUtils.formatAsHHmmss(new Date()));//交易发送时间
				context.setData(GDParamKeys.TRADE_PRIORITY, GDConstants.TRADE_PRIORITY);//交易优先
				context.setData(GDParamKeys.REDUCE_SIGN, GDConstants.REDUCE_SIGN);//压缩标志
				context.setData(GDParamKeys.TRADE_RETURN_CODE, GDConstants.TRADE_RETURN_CODE);//交易返回代码

				
				context.setData(GDParamKeys.NET_NAME, GDConstants.NET_NAME);//网点名称
				context.setData(GDParamKeys.SECRETKEY_INDEX, GDConstants.SECRETKEY_INDEX);//密钥索引
				context.setData(GDParamKeys.SECRETKEY_INIT, GDConstants.SECRETKEY_INIT);//密钥初始向量
				context.setData(GDParamKeys.TRADE_RECEIVE, GDConstants.TRADE_RECEIVE);//交易接收方
				context.setData(GDParamKeys.TRADE_SOURCE_ADD, GDConstants.TRADE_SOURCE_ADD);//交易源地址
				context.setData(GDParamKeys.TRADE_AIM_ADD, GDConstants.TRADE_AIM_ADD);//交易目标地址
				context.setData("PKGCNT", "000001");
			}
}