package com.bocom.bbip.gdeupsb.strategy.efek.cancelFeeOnline;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsThdTranCtlInfo;
import com.bocom.bbip.eups.repository.EupsThdTranCtlInfoRepository;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
import com.bocom.jump.bp.core.Executable;
/**
 * @author liyawei
 */
public class PreCancelFeeAction implements Executable{
	private final static Log logger=LogFactory.getLog(PreCancelFeeAction.class);
	@Autowired
	EupsThdTranCtlInfoRepository eupsThdTranCtlInfoRepository;
	/**
	 *抹账前处理 
	 */
	@Override
	public void execute(Context context) throws CoreException,CoreRuntimeException{
		logger.info("==========Start  PreCancelFeeAction");
		context.setData(ParamKeys.TRADE_TXN_DIR, "O");   //交易方向
		
		context.setData(GDParamKeys.SVRCOD, "12");
		context.setData(ParamKeys.THD_CUS_NO, context.getData(GDParamKeys.PAY_NO));
		
		context.setData(ParamKeys.CUS_NME, context.getData("CusNme"));
		context.setData(ParamKeys.BUS_TYP, context.getData(ParamKeys.RSV_FLD4));
		if(null !=context.getData(ParamKeys.RSV_FLD5)){
				context.setData(ParamKeys.PAY_TYPE, context.getData(ParamKeys.RSV_FLD5));
		}else{
				context.setData(ParamKeys.PAY_TYPE, "");
		}
		//TODO 
		context.setData(ParamKeys.BANK_NO, "301");
			 	//TODO   comNo
				String comNo=context.getData(ParamKeys.COMPANY_NO).toString();     
				EupsThdTranCtlInfo eupsThdTranCtlInfo=eupsThdTranCtlInfoRepository.findOne(comNo);
				if(null == eupsThdTranCtlInfo){
							context.setData(GDParamKeys.MSGTYP, "E");                  //  Contants常量   
							context.setData(ParamKeys.RSP_CDE,"EFE999");        //  Contants常量   
							context.setData(ParamKeys.RSP_MSG, "获取单位编码【"+context.getData(ParamKeys.COMPANY_NO).toString()+"】交易参数错");
							throw new CoreException("获取单位编码【"+context.getData(ParamKeys.COMPANY_NO).toString()+"】交易参数错");
				}
					context.setData(GDParamKeys.TOTNUM, "1");
					callThd(context);
				
				context.setData("TIATyp", "C");      //不明 TIATyp 是什么
				//TODO 是否需要	    <Set>TTxnCd=STRCAT(SUBSTR($OTTxnCd,1,5),9)</Set>	    <Set>HTxnCd=STRCAT(SUBSTR($OHTxnCd,1,5),9)</Set>
//				String txnCd=(String)context.getData(ParamKeys.TXN_CODE);
//				context.setData(ParamKeys.THD_TXN_CDE, txnCd.substring(1, 6)+"9");
//				context.setData(ParamKeys.TXN_CODE, txnCd.substring(1, 6)+"9");

				context.setData(GDParamKeys.BAG_TYPE, "0");
				logger.info("==========End  PreCancelFeeAction  ");
		 }
/**
 *报文信息
 */
	public void callThd(Context context){
		logger.info("==========Start  PreCancelFeeAction  callThd");
		context.setData(GDParamKeys.TREATY_VERSION, GDConstants.TREATY_VERSION);//协议版本
		context.setData(GDParamKeys.TRADE_PERSON_IDENTIFY, GDConstants.TRADE_PERSON_IDENTIFY);//交易人标识
		context.setData(GDParamKeys.BAG_TYPE, GDConstants.BAG_TYPE);//数据包类型
		context.setData(GDParamKeys.TRADE_START,GDConstants.TRADE_START);//交易发起方
				
				context.setData(GDParamKeys.TRADE_SEND_DATE,DateUtils.format(new Date(),DateUtils.STYLE_yyyyMMdd));//交易发送日期
				context.setData(GDParamKeys.TRADE_SEND_TIME, DateUtils.formatAsHHmmss(new Date()));//交易发送时间
				context.setData(GDParamKeys.TRADE_PRIORITY, GDConstants.TRADE_PRIORITY);//交易优先
				context.setData(GDParamKeys.REDUCE_SIGN, GDConstants.REDUCE_SIGN);//压缩标志
				context.setData(GDParamKeys.TRADE_RETURN_CODE, GDConstants.TRADE_RETURN_CODE);//交易返回代码

				
//				context.setData(GDParamKeys.NET_NAME, GDConstants.NET_NAME);//网点名称
				context.setData(GDParamKeys.SECRETKEY_INDEX, GDConstants.SECRETKEY_INDEX);//密钥索引
				context.setData(GDParamKeys.SECRETKEY_INIT, GDConstants.SECRETKEY_INIT);//密钥初始向量
				context.setData(GDParamKeys.TRADE_RECEIVE, GDConstants.TRADE_RECEIVE);//交易接收方
				context.setData(GDParamKeys.TRADE_SOURCE_ADD, GDConstants.TRADE_SOURCE_ADD);//交易源地址
				context.setData(GDParamKeys.TRADE_AIM_ADD, GDConstants.TRADE_AIM_ADD);//交易目标地址
				
				context.setData(ParamKeys.THD_TXN_DATE, DateUtils.format((Date)context.getData("thdTxnDte"), DateUtils.STYLE_yyyyMMdd));
				context.setData(ParamKeys.THD_TXN_TIME, DateUtils.format((Date)context.getData("thdTxnTme"), DateUtils.STYLE_HHmmss));
	}
}
