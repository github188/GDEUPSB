package com.bocom.bbip.gdeupsb.action.efek;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
import com.bocom.bbip.eups.entity.EupsTransJournal;
import com.bocom.bbip.eups.repository.EupsAmountInfoRepository;
import com.bocom.bbip.eups.repository.EupsTransJournalRepository;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.QryCusInformation;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

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
			context.setData(ParamKeys.TXN_DTE, DateUtils.parse(DateUtils.formatAsSimpleDate(new Date())));
			context.setData(ParamKeys.TXN_TME, DateUtils.parse(DateUtils.formatAsTranstime(new Date())));
			if(null != context.getData(GDParamKeys.NET_NAME)){
					context.setData(ParamKeys.BR, context.getData(GDParamKeys.NET_NAME));
			}
			context.setData(GDParamKeys.SVRCOD, "45");
			context.setData(GDParamKeys.TOTNUM, "1");
			String payNo=context.getData(GDParamKeys.PAY_NO).toString();
			EupsTransJournal eupsTransJournals=new EupsTransJournal();
			eupsTransJournals.setThdCusNo(payNo);
			eupsTransJournals.setEupsBusTyp(context.getData(ParamKeys.EUPS_BUSS_TYPE).toString());
			List<EupsTransJournal> eupsTransJournalList=eupsTransJournalRepository.find(eupsTransJournals);
			List<QryCusInformation> list=new ArrayList<QryCusInformation>(); 
			for (EupsTransJournal eupsTransJournal : eupsTransJournalList) {
					String[] s=null;
				 	QryCusInformation qryCusInformation=new QryCusInformation();
				 	qryCusInformation.setAccountsSerialNo(eupsTransJournals.getMfmVchNo());//账务流水
				 	qryCusInformation.setBankNo(eupsTransJournals.getBk());
				 	if(StringUtils.isNotEmpty(eupsTransJournal.getBakFld2())){
				 		s=eupsTransJournal.getBakFld2().split("&&");
				 		qryCusInformation.setBusType(s[0]);
				 		qryCusInformation.setPfeType(s[1]);
				 	}
				 	if(StringUtils.isNotEmpty(eupsTransJournal.getRsvFld3())){
				 			s=eupsTransJournal.getRsvFld3().split("&&");
						 	qryCusInformation.setCapital(new BigDecimal(s[1]));
				 	}
				 	qryCusInformation.setCapital(eupsTransJournal.getReqTxnAmt());
				 	qryCusInformation.setComNo(eupsTransJournal.getComNo());
				 	qryCusInformation.setCusAc(eupsTransJournal.getCusAc());
				 	qryCusInformation.setCusNme(eupsTransJournal.getCusNme());
				 	String electricityYearMonth=DateUtils.format(eupsTransJournal.getTxnDte(),DateUtils.STYLE_yyyyMMdd).substring(0, 6);
				 	qryCusInformation.setElectricityYearMonth(electricityYearMonth);
				 	qryCusInformation.setFulDedFlg(eupsTransJournal.getFulDedFlg());
				 	qryCusInformation.setOldTxnSqn(eupsTransJournal.getOldTxnSqn());
				 	qryCusInformation.setPayNo(payNo);
				 	qryCusInformation.setRsvFld6(eupsTransJournal.getRsvFld6());
				 	qryCusInformation.setSqn(eupsTransJournal.getSqn());
				 	qryCusInformation.setThdCusNme(eupsTransJournal.getThdCusNme());
				 	qryCusInformation.setThdTxnDte(eupsTransJournal.getThdTxnDte());
				 	qryCusInformation.setThdTxnTme(eupsTransJournal.getThdTxnTme());
				 	qryCusInformation.setTxnDte(eupsTransJournal.getTxnDte());
				 	qryCusInformation.setTxnTme(eupsTransJournal.getTxnTme());
				 	
				 	list.add(qryCusInformation);
			}
					context.setData(ParamKeys.THD_TXN_DATE, DateUtils.format(new Date(), DateUtils.STYLE_SIMPLE_DATE));
					context.setData("qryCusInformation", list);
					callThd(context);
			log.info("============End   QryCusBaseMsgAction");
		}
	/**
	 *报文信息  外发第三方
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
