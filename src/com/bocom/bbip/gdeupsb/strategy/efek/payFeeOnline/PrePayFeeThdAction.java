package com.bocom.bbip.gdeupsb.strategy.efek.payFeeOnline;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.repository.EupsThdTranCtlDetailRepository;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
import com.bocom.jump.bp.core.Executable;

public class PrePayFeeThdAction implements Executable{
	private final static Log logger=LogFactory.getLog(PrePayFeeThdAction.class);
	@Autowired
	EupsThdTranCtlDetailRepository eupsThdTranCtlDetailRepository;

	/**
	 * 记账前，第三方处理
	 */
	@Override
	public void execute(Context context) throws CoreException,
			CoreRuntimeException {
		logger.info("==============Start   PrePayFeeThdAction");
		
		String txnSts=(String)context.getData(ParamKeys.TXN_STS);
		if("B".equals(txnSts) ||  "s".equals(txnSts)){           
			context.setData(GDParamKeys.SVRCOD, "11");             //GDConstants 常量
			constantOfSoapUI(context);
			
		} 	
	}
	/**
	 *报文信息 
	 */
	public void constantOfSoapUI(Context context){  
		
		context.setData(GDParamKeys.TREATY_VERSION, "1.0.0");//协议版本
		context.setData(GDParamKeys.TRADE_PERSON_IDENTIFY, "301_030600");//交易人标识
		context.setData(GDParamKeys.BAG_TYPE, "0");//数据包类型
		context.setData(GDParamKeys.TRADE_START, "301");//交易发起方
		
				context.setData(GDParamKeys.TRADE_SEND_DATE,DateUtils.format(new Date(),DateUtils.STYLE_yyyyMMdd));//交易发送日期
				context.setData(GDParamKeys.TRADE_SEND_TIME, DateUtils.formatAsHHmmss(new Date()));//交易发送时间
				context.setData(GDParamKeys.TRADE_PRIORITY, "2");//交易优先
				context.setData(GDParamKeys.REDUCE_SIGN, "0");//压缩标志
				context.setData(GDParamKeys.TRADE_RETURN_CODE, "00");//交易返回代码
		
				context.setData(GDParamKeys.NET_NAME, "@BCFG.BrNam");//网点名称
				context.setData(GDParamKeys.SECRETKEY_INDEX, "0");//密钥索引
				context.setData(GDParamKeys.SECRETKEY_INIT, "");//密钥初始向量
				context.setData(GDParamKeys.TRADE_RECEIVE, "030600");//交易接收方
				context.setData(GDParamKeys.TRADE_SOURCE_ADD, "");//交易源地址
				context.setData(GDParamKeys.TRADE_AIM_ADD, "");//交易目标地址
				
	}
}
