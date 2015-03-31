package com.bocom.bbip.gdeupsb.action.efek;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ParamKeys;
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
				Map<String, Object> cusMap=new HashMap<String, Object>();
				cusMap.put("agtCllCusId", context.getData("cusNo"));
				cusMap.put("cusTyp", "0");
				cusMap.put("cusAc", context.getData("cusAc"));
				cusMap.put("ccy", "RMB");
				cusMap.put("idTyp", context.getData("idTyp"));
				cusMap.put("idNo", context.getData("idNo"));
				//添加 customerInfo
				List<Map<String, Object>> cusList=new ArrayList<Map<String,Object>>();
				cusList.add(cusMap);
				context.setData("customerInfo", cusList);				
				
				Map<String, Object> map=new HashMap<String, Object>();
				map.put(ParamKeys.BUS_TYP, "0");
				map.put("comNo", context.getData("comNo"));
				map.put("cusAc", context.getData("cusAc"));
				map.put("cusNme", context.getData("cusNme"));
				map.put("pwd", context.getData("pwd"));
				map.put("cmuTel", context.getData("cmuTel"));
				map.put("thdCusNo", context.getData("cusNo"));
				map.put("buyTyp", "0");
				map.put("busKnd", "A089");
				map.put("agrVldDte", DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMdd));
				map.put("agrExpDte", "99991231");
				map.put("agrTlr",context.getData(ParamKeys.TXN_TLR));
				//TODO 
				map.put("cusFeeDerFlg", "0");
				map.put("ccy","RMB");
				map.put("agtSrvCusPnm",context.getData("thdCusNme"));
				map.put("agtSrvCusId",context.getData("cusNo"));
				String oprTyp=context.getData("oprTyp").toString();
				String mothed="";
				if("0".equals(oprTyp)){
					mothed="eups.commInsertCusAgent";
					context.setData(GDParamKeys.NEWBANKNO, "301");
				}else if("1".equals(oprTyp)){
					mothed="eups.commUpdateCusAgent";
					context.setData(GDParamKeys.NEWBANKNO, "301");
					map.put("cusAc", context.getData("newCusAc"));
					map.put("cusNme", context.getData("newCusName"));
				}else {
					mothed="eups.commDelCusAgent";
				}
				//添加 agentCollectAgreement
				List<Map<String, Object>> list=new ArrayList<Map<String,Object>>();
				list.add(map);
				context.setData(ParamKeys.AGENT_COLLECT_AGREEMENT, list);
				constantOfSoapUI(context);
				context.setData(ParamKeys.THD_CUS_NME, context.getData("settleAccountsName"));
				String traceNo=bbipPublicService.getTraceNo();
				context.setData(ParamKeys.TRACE_NO, traceNo);
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
				context.setData(GDParamKeys.BUS_IDENTIFY, "YDLW04");		
				context.setData("callThd", "callThd");
			}
}