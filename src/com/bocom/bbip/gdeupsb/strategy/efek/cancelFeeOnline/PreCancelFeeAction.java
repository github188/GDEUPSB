package com.bocom.bbip.gdeupsb.strategy.efek.cancelFeeOnline;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsThdTranCtlDetail;
import com.bocom.bbip.eups.entity.EupsThdTranCtlInfo;
import com.bocom.bbip.eups.repository.EupsThdTranCtlDetailRepository;
import com.bocom.bbip.eups.repository.EupsThdTranCtlInfoRepository;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
import com.bocom.jump.bp.core.Executable;

public class PreCancelFeeAction implements Executable{
	private final static Log logger=LogFactory.getLog(PreCancelFeeAction.class);
	@Autowired
	EupsThdTranCtlDetailRepository eupsThdTranCtlDetailRepository;
	@Autowired
	EupsThdTranCtlInfoRepository eupsThdTranCtlInfoRepository;
	@Autowired
	BBIPPublicService bbipPublicService;
	/**
	 *抹账前处理 
	 */
	@Override
	public void execute(Context context) throws CoreException,CoreRuntimeException{
		logger.info("==========Start  PreCancelFeeAction");
		context.setData(ParamKeys.TRADE_TXN_DIR, "O");   //交易方向
		if(context.getData(GDParamKeys.NET_NAME) != null){
				context.setData(ParamKeys.BR, context.getData(GDParamKeys.NET_NAME));
		}
		context.setData(GDParamKeys.SVRCOD, "12");
		String sqn=bbipPublicService.getBBIPSequence();
		context.setData(ParamKeys.SEQUENCE, sqn);
		context.setData(ParamKeys.THD_CUS_NO, context.getData(GDParamKeys.PAY_NO));
		
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
//TODO					resultAboutCallThdAction.callthdresult1(context);
				
				if(Constants.HOST_RESPONSE_CODE_SUCC.equals(context.getData(ParamKeys.RSP_CDE))){
						context.setData(ParamKeys.TXN_STS,"C");
				}

				EupsThdTranCtlDetail eupsThdTranCtlDetail=eupsThdTranCtlDetailRepository.findOne(context.getData(ParamKeys.SEQUENCE).toString());
				if(null == eupsThdTranCtlDetail){
						String txnSts=context.getData(ParamKeys.TXN_STS).toString();
						if("c".equals(txnSts)){
								logger.info("~~~~~~~~~~正在抹账");
						}else if("C".equals(txnSts)){
								context.setData(GDParamKeys.MSGTYP, "N");
								context.setData(ParamKeys.RSP_CDE, Constants.HOST_RESPONSE_CODE_SUCC);
								context.setData(ParamKeys.RSP_MSG, "原记录【"+context.getData(ParamKeys.OLD_TXN_SQN).toString()+"】已经抹账");
								logger.info("~~~~~~~~~~已经抹账");
						}else if("b".equals(txnSts)){
								logger.info("~~~~~~~~~~正在记账");
						}else if("B".equals(txnSts)){
								logger.info("~~~~~~~~~~记账成功");
						}else if("s".equals(txnSts)){
								logger.info("~~~~~~~~~~正在成功");
						}else if("S".equals(txnSts)){
								logger.info("~~~~~~~~~~发送成功");
								context.setData(ParamKeys.RSP_MSG, "原记录【"+context.getData(ParamKeys.OLD_TXN_SQN).toString()+"】准备抹账");
						}else{
							context.setData(GDParamKeys.MSGTYP, "E");
							context.setData(ParamKeys.RSP_CDE, "EFE999");
							context.setData(ParamKeys.RSP_MSG, "原记录【"+context.getData(ParamKeys.OLD_TXN_SQN).toString()+"】状态【"+txnSts+"】未明，不进行抹账");
							throw new CoreRuntimeException("原记录【"+context.getData(ParamKeys.OLD_TXN_SQN).toString()+"】状态【"+txnSts+"】未明，不进行抹账");
						}
				}else{
						context.setData(GDParamKeys.MSGTYP, "N");
						context.setData(ParamKeys.RSP_CDE, Constants.HOST_RESPONSE_CODE_SUCC);
						context.setData(ParamKeys.RSP_MSG, "原记录【"+context.getData(ParamKeys.OLD_TXN_SQN).toString()+"】不存在");
						throw new CoreRuntimeException("原记录【"+context.getData(ParamKeys.OLD_TXN_SQN).toString()+"】不存在");
					}
		 }
/**
 *报文信息
 */
	public void callThd(Context context){
				context.setData(GDParamKeys.TREATY_VERSION, "1.0.0");//协议版本
				context.setData(GDParamKeys.TRADE_PERSON_IDENTIFY, "301_030600");//交易人标识
				context.setData(GDParamKeys.BAG_TYPE, "0");//数据包类型
				context.setData(GDParamKeys.TRADE_START, "301");//交易发起方
				
				context.setData(GDParamKeys.TRADE_SEND_DATE,DateUtils.format(new Date(),DateUtils.STYLE_yyyyMMdd));//交易发送日期
				context.setData(GDParamKeys.TRADE_SEND_TIME, DateUtils.formatAsHHmmss(new Date()));//交易发送时间
				context.setData(GDParamKeys.REDUCE_SIGN, "0");//压缩标志
				context.setData(GDParamKeys.TRADE_RETURN_CODE, "00");//交易返回代码
		
				context.setData(GDParamKeys.NET_NAME, "@BCFG.BrNam");//网点名称
				context.setData(GDParamKeys.SECRETKEY_INDEX, "0");//密钥索引
				context.setData(GDParamKeys.SECRETKEY_INIT, "");//密钥初始向量
				context.setData(GDParamKeys.TRADE_RECEIVE, "030600");//交易接收方
				context.setData(GDParamKeys.TRADE_SOURCE_ADD, "");//交易源地址
				context.setData(GDParamKeys.TRADE_AIM_ADD, "");//交易目标地址
				
			    String thdTxnDte = context.getData(ParamKeys.THD_TXN_DATE);
			    String thdTxnTme =context.getData(ParamKeys.THD_TXN_TIME);
		        if (null != thdTxnDte && null != thdTxnTme) {
		        	context.setData(ParamKeys.THD_TXN_DATE, DateUtils.parse(thdTxnDte, DateUtils.STYLE_SIMPLE_DATE));
		            context.setData(ParamKeys.THD_TXN_TIME, DateUtils.parse(thdTxnTme));
		        	context.setData(ParamKeys.THD_RSP_TME,DateUtils.formatAsTranstime(new Date()));
		        }
		        Date rspTime = DateUtils.parse((String)context.getData(ParamKeys.TXN_DATE));
		        if (null != rspTime) {
		            context.setData(ParamKeys.RESPONSE_TIME, rspTime);
		        } else {
		            context.setData(ParamKeys.RESPONSE_TIME, new Date());
		        }
		        
	}
}
