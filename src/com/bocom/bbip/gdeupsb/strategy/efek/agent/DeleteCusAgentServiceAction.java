package com.bocom.bbip.gdeupsb.strategy.efek.agent;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import com.bocom.bbip.eups.entity.EupsCusAgentJournal;
import com.bocom.bbip.eups.repository.EupsCusAgentJournalRepository;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.service.BGSPServiceAccessObject;
import com.bocom.bbip.service.Result;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
/**
 * @author liyawei
 */
public class DeleteCusAgentServiceAction extends BaseAction{
	private final static Log logger=LogFactory.getLog(DeleteCusAgentServiceAction.class);
	@Autowired
	@Qualifier("callThdTradeManager")
    ThirdPartyAdaptor callThdTradeManager;
	@Autowired
	BGSPServiceAccessObject bgspServiceAccessObject;
	@Autowired
	EupsCusAgentJournalRepository eupsCusAgentJournalRepository;
	@Override
	public void execute(Context context) throws CoreException,
			CoreRuntimeException {
			logger.info("=============Start   DeleteCusAgentServiceAction ");
			
			Date txnDte=(Date)context.getData(ParamKeys.TXN_DTE);
			Date txnTme=DateUtils.parse(context.getData("txnTme").toString());
			Map<String, Object> map=createMap(context);
			logger.info("~~~~~~~~~~~~~~map~~~~~ "+map);
			Result delResult = bgspServiceAccessObject.callServiceFlatting("deleteAgentCollectAgreement",context.getDataMap());
			logger.info("==========delResult："+delResult);
			
			if(delResult.isSuccess()){
					if(context.getData("callThd")!=null){
							try{
								constantOfSoapUI(context);
								context.setData(ParamKeys.TXN_DTE, DateUtils.format(txnDte,DateUtils.STYLE_yyyyMMdd));
								context.setData(ParamKeys.TXN_TME, DateUtils.format(txnTme,DateUtils.STYLE_HHmmss));
								context.setData(GDParamKeys.SVRCOD, "30");
								Map<String, Object> rspMap = callThdTradeManager.trade(context);
								
									if(BPState.isBPStateNormal(context)){
											if(null !=rspMap){
												 	context.setDataMap(rspMap);
									                context.setData(ParamKeys.THIRD_RETURN_MESSAGE, rspMap);
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
										                    
										                  //保存到EupsCusAgentJournal表中
										    				log.info("============insert   EupsCusAgentJournal");
										    				EupsCusAgentJournal eupsCusAgentJournal=new EupsCusAgentJournal();
										    				eupsCusAgentJournal.setSqn(context.getData("sqn").toString());
										    				eupsCusAgentJournal.setEupsBusTyp("ELEC00");
										    				String rsvFld3=DateUtils.format(new Date(),DateUtils.STYLE_yyyyMMddHHmmss);
										    				eupsCusAgentJournal.setRsvFld3(rsvFld3);
										    				String rsvFld1=context.getData("oprTyp").toString().trim()+context.getData("agtSts").toString().trim();
										    				eupsCusAgentJournal.setRsvFld1(rsvFld1);
										    				eupsCusAgentJournal.setComNo(context.getData("comNos").toString());
										    				eupsCusAgentJournal.setThdCusNo((String)context.getData("cusNo"));
										    				eupsCusAgentJournal.setCusAc(context.getData("cusAc").toString());
										    				eupsCusAgentJournal.setCusNme((String)context.getData("cusNme"));
										    				eupsCusAgentJournal.setIdTyp((String)context.getData("idTyp"));
										    				eupsCusAgentJournal.setIdNo((String)context.getData("idNo"));
										    				eupsCusAgentJournal.setTel((String)context.getData("cmuTel"));
										    				eupsCusAgentJournal.setTxnDte((Date)context.getData(ParamKeys.TXN_DTE));
										    				eupsCusAgentJournal.setRsvFld2("301");;
										    				eupsCusAgentJournalRepository.insert(eupsCusAgentJournal);
										    				
										    				log.info("============End  insert   EupsCusAgentJournal");
										    				
										                }else if(BPState.isBPStateReversalFail(context)){
										                	context.setData(ParamKeys.THD_TXN_STS,Constants.THD_TXNSTS_FAIL);
										                	context.setData(GDParamKeys.MSGTYP, "E");
										                	context.setData(ParamKeys.RSP_CDE, "EFE999");
										                }else if(BPState.isBPStateOvertime(context)){
										                	context.setData(ParamKeys.THD_TXN_STS,Constants.THD_TXNSTS_FAIL);
										                	context.setData(GDParamKeys.MSGTYP, "E");
										                	context.setData(ParamKeys.RSP_CDE, "EFE999");
										                }else if(BPState.isBPStateSystemError(context)){
										                	context.setData(ParamKeys.THD_TXN_STS,Constants.THD_TXNSTS_FAIL);
										                	context.setData(GDParamKeys.MSGTYP, "E");
										                	context.setData(ParamKeys.RSP_CDE, "EFE999");
										                }else if(BPState.isBPStateTransFail(context)){
										                	context.setData(ParamKeys.THD_TXN_STS,Constants.THD_TXNSTS_FAIL);
										                	context.setData(GDParamKeys.MSGTYP, "E");
										                	context.setData(ParamKeys.RSP_CDE, "EFE999");
										                }else{
										                	context.setData(ParamKeys.THD_TXN_STS,Constants.THD_TXNSTS_FAIL);
										                	context.setData(GDParamKeys.MSGTYP, "E");
										                	context.setData(ParamKeys.RSP_CDE, "EFE999");
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
							}finally{
								context.setData(ParamKeys.TXN_DTE, txnDte);
								context.setData(ParamKeys.TXN_TME, txnTme);
								context.setData("PKGCNT", "000000");
							}
					}
					if(context.getData(ParamKeys.THD_SQN)!=null){
						String thdTxnDte=context.getData("thdTxnDate").toString();
						String thdTxnTme=context.getData("thdTxnTime").toString();
						context.setData(ParamKeys.THD_TXN_DATE, DateUtils.parse(thdTxnDte));
						context.setData(ParamKeys.THD_TXN_TIME, DateUtils.parse((thdTxnDte+thdTxnTme),DateUtils.STYLE_yyyyMMddHHmmss));
						context.setData("PKGCNT", "000000");
					}
			}else{
					context.setData("thdRspCde", "83");
					throw new CoreException("解约失败");
			}
			logger.info("=============End   DeleteCusAgentServiceAction ");
	}
	public Map<String, Object> createMap(Context context){
		logger.info("=============Start   DeleteCusAgentServiceAction  createMap");
		Map<String, Object> map=new HashMap<String, Object>();
		map.put("traceNo", context.getData(ParamKeys.TRACE_NO));
		map.put("traceSrc", context.getData(ParamKeys.TRACE_SOURCE));
		map.put("version", context.getData(ParamKeys.VERSION));
		map.put("reqTme", new Date());
		map.put("reqJrnNo", get(BBIPPublicService.class).getBBIPSequence());
		map.put("reqSysCde", context.getData(ParamKeys.REQ_SYS_CDE));
		map.put("tlr", context.getData(ParamKeys.TELLER));
		map.put("chn", context.getData(ParamKeys.CHANNEL));
		map.put("bk", context.getData(ParamKeys.BK));
		map.put("br", context.getData(ParamKeys.BR));
		//协议编号
		String agdAgrNo=context.getData("agdAgrNo").toString();
		map.put("agdAgrNo", agdAgrNo);
		
		List<String> agdAgrNoList=new ArrayList<String>();
		agdAgrNoList.add(agdAgrNo);
		context.setData("agdAgrNo", agdAgrNoList);
		logger.info("=============End   DeleteCusAgentServiceAction  createMap");
		return map;
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
				context.setData(GDParamKeys.BUS_IDENTIFY, "YDLW07");		
			}

}
