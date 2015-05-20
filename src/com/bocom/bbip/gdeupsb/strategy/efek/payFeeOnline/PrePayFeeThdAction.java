package com.bocom.bbip.gdeupsb.strategy.efek.payFeeOnline;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.adaptor.ThirdPartyAdaptor;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
import com.bocom.jump.bp.core.Executable;
/**
 * @author liyawei
 */
public class PrePayFeeThdAction extends BaseAction implements Executable{
	private final static Log logger=LogFactory.getLog(PrePayFeeThdAction.class);
	@Autowired
	BBIPPublicService bbipPublicService;
	@Autowired
	@Qualifier("callThdTradeManager")
	ThirdPartyAdaptor callThdTradeManager;
	/**
	 * 记账前，第三方处理
	 */
	@Override
	public void execute(Context context) throws CoreException,
			CoreRuntimeException {
		logger.info("==============Start   PrePayFeeThdAction");
		
			context.setData(GDParamKeys.BAG_TYPE, "1");
			constantOfSoapUI(context);
			
			BigDecimal txnAmt=new BigDecimal(context.getData(ParamKeys.TXN_AMT).toString());
			context.setData(ParamKeys.TXN_AMT, txnAmt.scaleByPowerOfTen(2));
			BigDecimal oweFeeAmt=new BigDecimal(context.getData(ParamKeys.OWE_FEE_AMT).toString());
			context.setData(ParamKeys.OWE_FEE_AMT, oweFeeAmt.scaleByPowerOfTen(2));
			BigDecimal bj=new BigDecimal(context.getData(GDParamKeys.CAPITIAL).toString());
			context.setData("capital", bj.scaleByPowerOfTen(2));
			BigDecimal wyj=new BigDecimal(context.getData(GDParamKeys.DEDIT).toString());
			context.setData("dedit", wyj.scaleByPowerOfTen(2));
			
			context.setData(GDParamKeys.SVRCOD, "11");             //GDConstants 常量
			context.setData("accountsSerialNo", context.getData("accountsSerialNos"));
			context.setData("comNo", context.getData("company"));
			context.setData("rsvFld3", context.getData("accountsSerialNos"));
			
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
				String comNo=(String)context.getData("company");
				if(!StringUtils.isEmpty(comNo)){
					if(comNo.length()>4){
							comNo=comNo.substring(0,4)+"00";
					}else{
						while(comNo.length()<6){
							comNo=comNo+"0";
						}
					}
				}else{
					comNo="030000";
				}
				context.setData(GDParamKeys.TRADE_RECEIVE, comNo);//交易接收方
				context.setData(GDParamKeys.TRADE_RECEIVE, GDConstants.TRADE_RECEIVE);//交易接收方
				context.setData(GDParamKeys.TRADE_SOURCE_ADD, GDConstants.TRADE_SOURCE_ADD);//交易源地址
				context.setData(GDParamKeys.TRADE_AIM_ADD, GDConstants.TRADE_AIM_ADD);//交易目标地址
				context.setData("PKGCNT", "000001");
				context.setData("sqns", context.getData("sqn"));
				
				Date txnDte=context.getData(ParamKeys.TXN_DATE);
				Date txnTme=context.getData(ParamKeys.TXN_TIME);
				String txnDate=DateUtils.format(txnDte,DateUtils.STYLE_yyyyMMdd);
				String txnTime=DateUtils.formatAsHHmmss(txnTme);
				context.setData(ParamKeys.TXN_DATE, txnDate);
				context.setData(ParamKeys.TXN_TIME, txnTime);
				
				context.setData(GDParamKeys.BUS_IDENTIFY, "YDLW04");
//				context.setData(ParamKeys.PAY_TYPE, "110");
				
	}
}
