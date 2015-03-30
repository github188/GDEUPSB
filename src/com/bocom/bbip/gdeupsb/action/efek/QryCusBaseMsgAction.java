package com.bocom.bbip.gdeupsb.action.efek;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.action.common.CommThdRspCdeAction;
import com.bocom.bbip.eups.adaptor.ThirdPartyAdaptor;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.repository.EupsAmountInfoRepository;
import com.bocom.bbip.eups.repository.EupsTransJournalRepository;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
/**
 * @author liyawei
 */
public class QryCusBaseMsgAction extends BaseAction{
	@Autowired
	BBIPPublicService bbipPublicService;
	@Autowired
	EupsTransJournalRepository eupsTransJournalRepository;
	@Autowired
	EupsAmountInfoRepository eupsAmountInfoRepository;
	@Autowired
	@Qualifier("callThdTradeManager")
    ThirdPartyAdaptor callThdTradeManager;
	/**
	 * 查询客户基本信息
	 */
		public void execute(Context context) throws CoreException,CoreRuntimeException{
			log.info("============Start   QryCusBaseMsgAction");
			if(context.getData(GDParamKeys.NET_NAME) != null){
					context.setData(ParamKeys.BR, context.getData(GDParamKeys.NET_NAME));
			}
			context.setData(ParamKeys.SEQUENCE, bbipPublicService.getBBIPSequence());
			String txnDte=DateUtils.format((Date)context.getData(ParamKeys.TXN_DTE),DateUtils.STYLE_yyyyMMdd);
			String txnTme=DateUtils.formatAsHHmmss(DateUtils.parse(context.getData(ParamKeys.TXN_TME).toString()));
			context.setData(ParamKeys.TXN_DTE,txnDte);
			context.setData(ParamKeys.TXN_TME, txnTme);
			
			context.setData(ParamKeys.TXN_TLR, context.getData(ParamKeys.TELLER));
			context.setData(GDParamKeys.SVRCOD, "44");
			context.setData(GDParamKeys.TOTNUM, "1");
			callThd(context);
			double i=Double.parseDouble(context.getData(ParamKeys.OWE_FEE_AMT).toString());
			i=i/100;
			DecimalFormat df=new DecimalFormat("#.00");
			BigDecimal oweFeeAmt=new BigDecimal(df.format(i));
			context.setData(ParamKeys.OWE_FEE_AMT, oweFeeAmt);
			log.info("============End   QryCusBaseMsgAction");
		}
	/**
	 *报文信息  外发第三方
	 */
	public void callThd(Context context){  
			
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
					try{
						Map<String, Object> rspMap = callThdTradeManager.trade(context);
						
							if(BPState.isBPStateNormal(context)){
									if(null !=rspMap){
										 	context.setDataMap(rspMap);
							                context.setData(ParamKeys.THIRD_RETURN_MESSAGE, rspMap);
							               context.setData("qryCusInformation", rspMap.get("qryCusInformation"));
							                //第三方返回码
							                CommThdRspCdeAction rspCdeAction = new CommThdRspCdeAction();
							                String responseCode = rspCdeAction.getThdRspCde(rspMap, context.getData(ParamKeys.EUPS_BUSS_TYPE).toString());
							                log.info("third response code="+responseCode);
							                if(StringUtils.isEmpty(responseCode)){
							                	responseCode=ErrorCodes.EUPS_THD_SYS_ERROR;
							                }
							                context.setData(ParamKeys.RESPONSE_CODE, responseCode);
							                
							             // 第三方交易成功
								                if (GDConstants.SUCCESS_CODE.equals(responseCode)) {
								                    log.info("The third process response successful.");
								                    context.setData(ParamKeys.TXN_STS, Constants.TXNSTS_SUCCESS);
								                    context.setData(ParamKeys.THD_TXN_STS, Constants.THD_TXNSTS_SUCCESS);
								                    context.setData(ParamKeys.RSP_CDE, GDConstants.SUCCESS_CODE);
								                    context.setData(ParamKeys.RSP_MSG, "交易成功");
								                }else if(BPState.isBPStateReversalFail(context)){
								                	context.setData(ParamKeys.THD_TXN_STS,Constants.THD_TXNSTS_FAIL);
								                	context.setData(GDParamKeys.MSGTYP, "E");
								                	context.setData(ParamKeys.RSP_CDE, "EFE999");
								                	context.setData(ParamKeys.RSP_MSG, "交易失败");
								                }else if(BPState.isBPStateOvertime(context)){
								                	context.setData(ParamKeys.THD_TXN_STS,Constants.THD_TXNSTS_FAIL);
								                	context.setData(GDParamKeys.MSGTYP, "E");
								                	context.setData(ParamKeys.RSP_CDE, "EFE999");
								                	context.setData(ParamKeys.RSP_MSG, "交易超时");
								                }else if(BPState.isBPStateSystemError(context)){
								                	context.setData(ParamKeys.THD_TXN_STS,Constants.THD_TXNSTS_FAIL);
								                	context.setData(GDParamKeys.MSGTYP, "E");
								                	context.setData(ParamKeys.RSP_CDE, "EFE999");
								                	context.setData(ParamKeys.RSP_MSG, "系统错误");
								                }else if(BPState.isBPStateTransFail(context)){
								                	context.setData(ParamKeys.THD_TXN_STS,Constants.THD_TXNSTS_FAIL);
								                	context.setData(GDParamKeys.MSGTYP, "E");
								                	context.setData(ParamKeys.RSP_CDE, "EFE999");
								                	context.setData(ParamKeys.RSP_MSG, "发送失败");
								                }else{
								                	context.setData(ParamKeys.THD_TXN_STS,Constants.THD_TXNSTS_FAIL);
								                	context.setData(GDParamKeys.MSGTYP, "E");
								                	context.setData(ParamKeys.RSP_CDE, "EFE999");
								                	context.setData(ParamKeys.RSP_MSG, "交易失败，其他未知情况");
								                }
									}
							}else{
									log.info("~~~~~~~~~~~发送失败");
								    context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
					                context.setData(ParamKeys.TXN_STS, Constants.TXNSTS_REVERSE);
					                context.setData(ParamKeys.THD_TXN_STS, Constants.TXNSTS_FAIL);
					                context.setData(ParamKeys.RESPONSE_TYPE, Constants.RESPONSE_TYPE_FAIL);
					                context.setData(ParamKeys.RESPONSE_CODE, ErrorCodes.EUPS_THD_SYS_ERROR);
					                context.setData(ParamKeys.RESPONSE_MESSAGE, Constants.RESPONSE_MSG_FAIL);
					                context.setData(ParamKeys.THD_RSP_MSG,Constants.RESPONSE_MSG_FAIL);
					                throw new CoreException("发送失败");
							}
					}catch(CoreException e){
						log.info("Bypass call THIRD response failed or unknow error.");
						context.setData(ParamKeys.TXN_STS, Constants.TXNSTS_REVERSE);
						context.setData(ParamKeys.THD_TXN_STS, Constants.TXNSTS_FAIL);
						context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
					}	
					
		}
}
