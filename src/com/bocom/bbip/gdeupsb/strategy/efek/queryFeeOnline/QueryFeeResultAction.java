package com.bocom.bbip.gdeupsb.strategy.efek.queryFeeOnline;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.bocom.bbip.eups.action.common.CommThdRspCdeAction;
import com.bocom.bbip.eups.adaptor.ThirdPartyAdaptor;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.repository.EupsAmountInfoRepository;
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
public class QueryFeeResultAction implements Executable{
		private final static Log logger=LogFactory.getLog(QueryFeeResultAction.class);
		@Autowired
		EupsAmountInfoRepository eupsAmountInfoRepository;
		@Autowired
		@Qualifier("callThdTradeManager")
		ThirdPartyAdaptor callThdTradeManager;
		/**
		 * 查询结果
		 */
		@Override
		public void execute(Context context) throws CoreException,
			CoreRuntimeException {
			logger.info("===========Start QueryFeeResultAction");
			/*				
		    String payNo=context.getData(GDParamKeys.PAY_NO).toString();
			String busType=(String)context.getData(GDParamKeys.BUS_TYPE);
			String checkType=context.getData(GDParamKeys.CHECK_TYPE).toString();
			EupsAmountInfo eupsAmountInfos=new EupsAmountInfo();
			eupsAmountInfos.setThdCusNo(payNo);
			if(StringUtils.isNotEmpty(busType)){
				eupsAmountInfos.setBakFld1(busType);
			}
			List<EupsAmountInfo> list=eupsAmountInfoRepository.find(eupsAmountInfos);
			if("0".equals(checkType)){
					context.setDataMap(BeanUtils.toMap(list));
			}else{
				String electricityYearMonth=null;
				//比较月份的
				if(null !=context.getData(GDParamKeys.ELECTRICITY_YEARMONTH)){
					electricityYearMonth=context.getData(GDParamKeys.ELECTRICITY_YEARMONTH).toString();
				}else{
					electricityYearMonth=DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMdd).substring(0, 6);
				}
				List<EupsAmountInfo> eupsAmountInfolist=new ArrayList<EupsAmountInfo>();
				for(EupsAmountInfo eupsAmountInfo:list){
					String txnDte=eupsAmountInfo.getTxnDte().toString().substring(0,6);
					if(txnDte.equals(electricityYearMonth)){
							eupsAmountInfolist.add(eupsAmountInfo);
					}
				}
				context.setDataMap(BeanUtils.toMap(eupsAmountInfolist));
			}
			*/
			callThd(context);
			context.setData(ParamKeys.RAP_TYPE, "2");
			Date thdTxnDate=DateUtils.parse(context.getData(ParamKeys.THD_TXN_DATE).toString(),DateUtils.STYLE_yyyyMMdd);
	        Date thdTxnTme = DateUtils.parse(context.getData(ParamKeys.THD_TXN_TIME).toString(),DateUtils.STYLE_HHmmss);
	        context.setData(ParamKeys.THD_TXN_DATE, thdTxnDate);
	        context.setData(ParamKeys.THD_TXN_TIME, thdTxnTme);
		}
		/**
		 *  报文常量 外发第三方 
		 */
		public void callThd(Context context) {
			logger.info("=============Start  QueryFeeResultAction callThd");
				
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

						
						try{
							Map<String, Object> rspMap = callThdTradeManager.trade(context);
								if(BPState.isBPStateNormal(context)){
										if(null !=rspMap){
											 	context.setDataMap(rspMap);
								                context.setData(ParamKeys.THIRD_RETURN_MESSAGE, rspMap);
								                BigDecimal oweFeeAmt=new BigDecimal("0.00");
								                List<Map<String, Object>> list=(List<Map<String, Object>>)rspMap.get("Information");
								                for (Map<String, Object> map : list) {
								                	double  amt=Double.parseDouble(map.get(ParamKeys.OWE_FEE_AMT).toString());
								                		amt=amt/100;
								                		DecimalFormat df=new DecimalFormat("#.00");
								                		BigDecimal amtAdd=new BigDecimal(df.format(amt));
								                		oweFeeAmt=oweFeeAmt.add(amtAdd);
												}
								                //TODO 怎样得到欠费金额
								                context.setData(ParamKeys.OWE_FEE_AMT, oweFeeAmt);
								                //第三方返回码
								                CommThdRspCdeAction rspCdeAction = new CommThdRspCdeAction();
								                String responseCode = rspCdeAction.getThdRspCde(rspMap, context.getData(ParamKeys.EUPS_BUSS_TYPE).toString());
								                logger.info("third response code="+responseCode);
								                if(StringUtils.isEmpty(responseCode)){
								                	responseCode=ErrorCodes.EUPS_THD_SYS_ERROR;
								                }
								                context.setData(ParamKeys.RESPONSE_CODE, responseCode);
								                
								             // 第三方交易成功
									                if (GDConstants.SUCCESS_CODE.equals(responseCode)) {
									                    logger.info("The third process response successful.");
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
										logger.info("~~~~~~~~~~~发送失败");
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
							logger.info("Bypass call THIRD response failed or unknow error.");
							context.setData(ParamKeys.TXN_STS, Constants.TXNSTS_REVERSE);
							context.setData(ParamKeys.THD_TXN_STS, Constants.TXNSTS_FAIL);
							context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
						}
						
			}
}
