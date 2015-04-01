package com.bocom.bbip.gdeupsb.action.efek;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.comp.BBIPPublicServiceImpl;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.adaptor.ThirdPartyAdaptor;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
/**
 *待定是否使用 
 */
public class TallyOneOnlineAction extends BaseAction{
	@Autowired
	BBIPPublicService bbipPublicService;
	@Autowired
	@Qualifier("callThdTradeManager")
	ThirdPartyAdaptor callThdTradeManager;
		@Override
		public void execute(Context context) throws CoreException,
				CoreRuntimeException {
			log.info("=====================Start  TallyOneOnlineAction");
			constantOfSoapUI(context);
			List<Map<String, Object>> list=getAccountsSerialNo(context);
			String listSize=list.size()+"";
			while(listSize.length()<6){
					listSize="0"+listSize;
			}
			context.setData("PKGCNT", listSize);
			
			for (Map<String, Object> map : list) {
					log.info("~~~~~~accountsSerialNo~~~~~~"+map.get("accountsSerialNo"));
				String acDate=DateUtils.format(((BBIPPublicServiceImpl)get(GDConstants.BBIP_PUBLIC_SERVICE)).getAcDate(),DateUtils.STYLE_yyyyMMdd);
				context.setData(ParamKeys.ACCOUNT_DATE, acDate);
				context.setData("sqn", bbipPublicService.getBBIPSequence());
				context.setData("traceNo", bbipPublicService.getTraceNo());
				  context.setData("netName" , GDConstants.NET_NAME);
				  context.setData("traSendDate" , DateUtils.format(new Date(),DateUtils.STYLE_yyyyMMdd));//交易发送日期
				  context.setData("traSendTime", DateUtils.formatAsHHmmss(new Date()));//交易发送时间
				  context.setData("busType" , "110");
				  context.setData("payType" ,map.get("payType"));
				  context.setData("comNo" , map.get("comNo"));
				  context.setData("payNo" , map.get("payNo"));
			      context.setData("bankNo" , "301");
			      context.setData("cusAc" , context.getData("cusAc"));
			      context.setData("CusNme" , context.getData("CusNme"));
			      context.setData("fulDedFlg" , "1" );
			      context.setData("accountsSerialNo" , map.get("accountsSerialNo"));
			      context.setData("rsvFld3" , map.get("accountsSerialNo"));
			      context.setData("electricityYearMonth" , map.get("electricityYearMonth"));
			      context.setData("oweFeeAmt" , context.getData("oweFeeAmt").toString() );
			      context.setData("capital" , context.getData("capital") );
			      context.setData("dedit" , context.getData("dedit") );
			      context.setData("payMde" ,  context.getData("payMde"));
			      context.setData("ccy"  ,  "RMB");
			      context.setData("txnAmt" , context.getData("txnAmt"));
			      context.setData("txnDte" ,  DateUtils.format(new Date(),DateUtils.STYLE_yyyyMMdd));
			      bbipPublicService.synExecute("eups.payFeeOnlineELEC00", context);
			}
			log.info("=====================End  TallyOneOnlineAction");
		}
		/**
		 * 获取账务
		 */
		public List<Map<String, Object>> getAccountsSerialNo(Context context){
			if(context.getData(GDParamKeys.ELECTRICITY_YEARMONTH) !=null){
				context.setData("checkType", "1");
			}else{
				context.setData("checkType", "0");
			}
			Map<String, Object> map=new HashMap<String, Object>();
			map.put(GDParamKeys.NET_NAME, GDConstants.NET_NAME);
			map.put(ParamKeys.SEQUENCE,context.getData(ParamKeys.SEQUENCE));
			map.put(GDParamKeys.TRADE_SEND_DATE,DateUtils.format(new Date(),DateUtils.STYLE_yyyyMMdd));
			map.put(GDParamKeys.TRADE_SEND_TIME, DateUtils.formatAsHHmmss(new Date()));
			map.put("busType",context.getData("busType"));
			map.put("payNo", context.getData("payNo"));
			map.put("electricityYearMonth", context.getData("electricityYearMonth"));
			map.put("checkType", context.getData("checkType"));
			
			context.setData(GDParamKeys.SVRCOD, "10");             //GDConstants 常量
			List<Map<String, Object>> list=new ArrayList<Map<String,Object>>();
			try {
				Map<String, Object> rspMap = callThdTradeManager.trade(context);
				if(BPState.isBPStateNormal(context) && rspMap!=null){
						list=(List<Map<String, Object>>)rspMap.get("Information");
				}
				context.setData(ParamKeys.THD_TXN_DATE, DateUtils.format((Date)context.getData(ParamKeys.THD_TXN_DATE),DateUtils.STYLE_yyyyMMdd));
				context.setData(ParamKeys.THD_TXN_TIME, DateUtils.format((Date)context.getData(ParamKeys.THD_TXN_TIME),DateUtils.STYLE_HHmmss));
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return list;
		}
		/**
		 *报文信息 
		 */
		public void constantOfSoapUI(Context context){  
			context.setData(GDParamKeys.TREATY_VERSION, GDConstants.TREATY_VERSION);//协议版本
			context.setData(GDParamKeys.TRADE_PERSON_IDENTIFY, GDConstants.TRADE_PERSON_IDENTIFY);//交易人标识
			context.setData(GDParamKeys.BAG_TYPE, "0");//数据包类型
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
					
					Date txnDte=context.getData(ParamKeys.TXN_DATE);
					Date txnTme=context.getData(ParamKeys.TXN_TIME);
					String txnDate=DateUtils.format(txnDte,DateUtils.STYLE_yyyyMMdd);
					String txnTime=DateUtils.formatAsHHmmss(txnTme);
					context.setData(ParamKeys.TXN_DATE, txnDate);
					context.setData(ParamKeys.TXN_TIME, txnTime);
					context.setData("comNo", "");
					context.setData("accountsSerialNo", "");
					//TODO 
					context.setData(GDParamKeys.BUS_IDENTIFY, "YDLW04");
//					context.setData(ParamKeys.PAY_TYPE, "110");
		}
}
