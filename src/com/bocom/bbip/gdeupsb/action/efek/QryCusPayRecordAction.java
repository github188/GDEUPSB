package com.bocom.bbip.gdeupsb.action.efek;

import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.action.common.CommThdRspCdeAction;
import com.bocom.bbip.eups.adaptor.ThirdPartyAdaptor;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
/**
 *银行查询客户交费记录 
 */
public class QryCusPayRecordAction extends BaseAction{
	@Autowired
    @Qualifier("callThdTradeManager")
    ThirdPartyAdaptor callThdTradeManager;
		public void execute(Context context)throws CoreException,CoreRuntimeException{
				log.info("=============Start QryCusPayRecordAction");
//				String payNo=context.getData(GDParamKeys.PAY_NO).toString();
//				Date startDate=DateUtils.parse(context.getData(GDParamKeys.ELECTRIC_START_YEARMONTH).toString());
//				String eupsBusTyp=context.getData(ParamKeys.EUPS_BUSS_TYPE).toString();
				String endDate=(String)context.getData(GDParamKeys.ELECTRIC_END_YEARMONTH);
				if(StringUtils.isEmpty(endDate)){
						endDate=DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMdd);
				}
				/*
				EupsStreamNoList eupsStreamNoList=new EupsStreamNoList();
				eupsStreamNoList.setEndDate(endDate);
				eupsStreamNoList.setStartDate(startDate);
				eupsStreamNoList.setPayNo(payNo);
				eupsStreamNoList.setEupsBusTyp(eupsBusTyp);
				List<EupsStreamNo> list=get(EupsStreamNoRepository.class).findList(eupsStreamNoList);

				if(CollectionUtils.isEmpty(list)){
						context.setData("ApCode", "SC");
						context.setData("OFmtCd", "D04");
						context.setData(ParamKeys.RSP_CDE, "");
						context.setData("InPos", "0001");
						context.setData(ParamKeys.RESPONSE_MESSAGE, "在【"+startDate+"】~【"+endDate+"】没有记录");
				}else{
					List<DetailInformation> streamNoList=new ArrayList<DetailInformation>();
						for (EupsStreamNo eupsStreamNo : list) {
								DetailInformation detailInformation=new DetailInformation();
								detailInformation.setApCode("46");
								detailInformation.setOFmtCd("999");
								String bakFld2=eupsStreamNo.getBakFld2();
								if(StringUtils.isNotEmpty(bakFld2)){
									String[] s=bakFld2.split("&&");
//									detailInformation.setBusType(s[0]); //收费方式 RAP_TYP
									detailInformation.setBusType(s[1]); //费用类型
								}
								String electricityYearMonth=DateUtils.format(eupsStreamNo.getTxnDte(),DateUtils.STYLE_yyyyMMdd).substring(0, 6);
								detailInformation.setElectricityYearMonth(electricityYearMonth);
								detailInformation.setParticularNo(eupsStreamNo.getAccSeq());//明细序号
//								context.setData(arg0, arg1);           缺失字段，内容未知	null
								detailInformation.setCopyListDate( eupsStreamNo.getBakFld4());//抄表日期
								detailInformation.setCopyListDate( eupsStreamNo.getBakFld5());//本次预存
								detailInformation.setCopyListDate( eupsStreamNo.getBakFld6());//本月示数
								detailInformation.setCopyListDate( eupsStreamNo.getRsvFld1());//上月示数
								String rsvFld2=eupsStreamNo.getRsvFld2();
								if(StringUtils.isNotEmpty(rsvFld2)){
										String s[]=rsvFld2.split("&&");
										detailInformation.setUseElectric(s[0]);//实用电量
										detailInformation.setUseElectric(s[1]);//增减电量
								}
								String rsvFld3=eupsStreamNo.getRsvFld3();
								if(StringUtils.isNotEmpty(rsvFld3)){
										String s[]=rsvFld3.split("&&");
										detailInformation.setUseElectric(s[0]);//单价
										detailInformation.setUseElectric(s[1]);//违约金
								}
								detailInformation.setPaymentMoney(eupsStreamNo.getTxnAmt());
								String paymentTime=DateUtils.formatAsHHmmss(eupsStreamNo.getTxnTme());
								detailInformation.setPaymentTime(paymentTime);
								streamNoList.add(detailInformation);
						}
						context.setData("DetailInformation", BeanUtils.toMaps(streamNoList));
				}
				*/
				context.setData(GDParamKeys.SVRCOD, "40");
				callThd(context);
				
		}
		
		/**
		 *外发第三方
		 */
		public void callThd(Context context){  
			
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
					
					try{
						Map<String, Object> rspMap = callThdTradeManager.trade(context);
						
							if(BPState.isBPStateNormal(context)){
									if(null !=rspMap){
										 	context.setDataMap(rspMap);
							                context.setData(ParamKeys.THIRD_RETURN_MESSAGE, rspMap);
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
