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
import com.bocom.bbip.eups.repository.EupsTransJournalRepository;
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
public class InsertCusAgentServiceAction extends BaseAction {
	private final static Log logger=LogFactory.getLog(InsertCusAgentServiceAction.class);
	@Autowired
	@Qualifier("callThdTradeManager")
    ThirdPartyAdaptor callThdTradeManager;
	@Autowired
	EupsTransJournalRepository eupsTransJournalRepository;
	@Autowired
	EupsCusAgentJournalRepository eupsCusAgentJournalRepository;
	@Autowired
	BGSPServiceAccessObject bgspServiceAccessObject;
	@Override
	public void execute(Context context) throws CoreException,
			CoreRuntimeException {
		logger.info("=============Start   InsertCusAgentServiceAction");
		
		//更改账户类型  代收付
		String cusTyp=context.getData("cusTyp").toString();
		//第三方
		if(context.getData(ParamKeys.THD_SQN)!=null){
				String cusType="";
				if(cusTyp.equals("0")){
					cusType="1";
				}else if(cusTyp.equals("1")){
					cusType="0";
				}
				context.setData("cusType", cusType);
		}else{
			String cusType=context.getData("cusTyp").toString();
			if(cusType.equals("0")){
					cusTyp="1";
			}else if(cusType.equals("1")){
					cusTyp="0";
			}
			context.setData("cusTyp", cusTyp);
		}
		//调用代收付'
		String cusAc=context.getData("cusAc").toString();		
		String newCusAc=context.getData("newCusAc").toString();		
		context.setData(ParamKeys.COMPANY_NO, context.getData("comNo"));
		context.setData(ParamKeys.CUS_AC, context.getData(GDParamKeys.NEWCUSAC));
		
		context.setData("traceNo", context.getData(ParamKeys.TRACE_NO));
		context.setData("traceSrc", context.getData(ParamKeys.TRACE_SOURCE));
		context.setData("version", context.getData(ParamKeys.VERSION));
		context.setData("reqTme", new Date());
		context.setData("reqJrnNo", get(BBIPPublicService.class).getBBIPSequence());
		context.setData("reqSysCde", context.getData(ParamKeys.REQ_SYS_CDE));
		context.setData("tlr", context.getData(ParamKeys.TELLER));
		context.setData("chn", context.getData(ParamKeys.CHANNEL));
		context.setData("bk", context.getData(ParamKeys.BK));
		context.setData("br", context.getData(ParamKeys.BR));
		
		context.setData("ageBr", context.getData(ParamKeys.BK));
		context.setData("agrBr", context.getData(ParamKeys.BR));
		if(context.getData(ParamKeys.THD_SQN)!=null){
			context.setData("bk", "01441999999");
			context.setData("br", "01441131999");
		}
		context.setData("oprTyp", "0");
		Result editCusAgtResult = bgspServiceAccessObject.callServiceFlatting("maintainAgentCollectAgreement",context.getDataMap());
		logger.info("===========editCusAgtResult："+editCusAgtResult);
		if(editCusAgtResult.isSuccess() && editCusAgtResult.getResponseType().toString().equals("N") ){
			if(context.getData("bankToThd")!=null){
					Date txnDte=(Date)context.getData(ParamKeys.TXN_DTE);
					Date txnTme=DateUtils.parse(context.getData("txnTme").toString());
						context.setData(ParamKeys.CUS_AC, cusAc);
						context.setData(ParamKeys.THD_CUS_NO,  context.getData("cusNo"));
						context.setData(ParamKeys.TXN_DTE, DateUtils.format(txnDte,DateUtils.STYLE_yyyyMMdd));
						context.setData(ParamKeys.TXN_TME, DateUtils.format(txnTme,DateUtils.STYLE_HHmmss));
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
								                    context.setData(ParamKeys.RESPONSE_MESSAGE, "交易成功");
								                    
								    				//保存到EupsCusAgentJournal表中
								    				log.info("============insert   EupsCusAgentJournal");
								    				EupsCusAgentJournal eupsCusAgentJournal=new EupsCusAgentJournal();
								    				eupsCusAgentJournal.setSqn(context.getData("sqn").toString());
								    				eupsCusAgentJournal.setEupsBusTyp("ELEC00");
								    				String rsvFld3=DateUtils.format(new Date(),DateUtils.STYLE_yyyyMMddHHmmss);
								    				eupsCusAgentJournal.setRsvFld3(rsvFld3);
								    				String oprTypeBank=(String)context.getData("oprTypeBank");
								    				if(oprTypeBank.equals("1")){
								    					context.setData("oprTyp", "1");
								    				}
								    				String rsvFld1=context.getData("oprTyp").toString().trim()+context.getData("agtSts").toString().trim();
								    				eupsCusAgentJournal.setRsvFld1(rsvFld1);
								    				eupsCusAgentJournal.setThdCusNo((String)context.getData("cusNo"));
								    				eupsCusAgentJournal.setCusAc(newCusAc);
								    				eupsCusAgentJournal.setCusNme((String)context.getData("cusNme"));
								    				eupsCusAgentJournal.setIdTyp((String)context.getData("idTyp"));
								    				eupsCusAgentJournal.setIdNo((String)context.getData("idNo"));
								    				eupsCusAgentJournal.setTel((String)context.getData("cmuTel"));
								    				eupsCusAgentJournal.setTxnDte(new Date());
								    				eupsCusAgentJournal.setRsvFld2("301");
								    				eupsCusAgentJournal.setAgrBr(context.getData("cusTyp").toString());
								    				eupsCusAgentJournal.setComNo(context.getData("comNos").toString());
								    				eupsCusAgentJournalRepository.insert(eupsCusAgentJournal);
								    				
								    				log.info("============End  insert   EupsCusAgentJournal");
								    				//Date  String
								    				context.setData(ParamKeys.TXN_DTE,txnDte);
								    				context.setData(ParamKeys.TXN_TME,DateUtils.parse(DateUtils.format(txnDte, DateUtils.STYLE_yyyyMMdd)+context.getData(ParamKeys.TXN_TME).toString()));
								                }else{
								                	
								                	//第三方失败  协议删除
													String agdAgrNo=selectAgent(context,cusAc);
													context.setData("agdAgrNo", agdAgrNo);
													Map<String, Object> map=createMap(context);
													logger.info("~~~~~~~~~~~~~~map~~~~~ "+map);
													Result delResult = bgspServiceAccessObject.callServiceFlatting("deleteAgentCollectAgreement",context.getDataMap());
													logger.info("=======新增协议，第三方失败后，删除协议===delResult："+delResult);
								              
													context.setData(ParamKeys.THD_TXN_STS,Constants.THD_TXNSTS_FAIL);
								                	context.setData(GDParamKeys.MSGTYP, "E");
								                	context.setData(ParamKeys.RSP_CDE, "EFE999");
								                	context.setData(ParamKeys.RESPONSE_MESSAGE, "交易失败");
								                	throw new CoreException(responseCode);
								                }
									}
							}else{
									logger.info("~~~~~~~~~~~发送失败");
									//第三方失败  协议删除
									
									String agdAgrNo=selectAgent(context,cusAc);
									context.setData("agdAgrNo", agdAgrNo);
									Map<String, Object> map=createMap(context);
									logger.info("~~~~~~~~~~~~~~map~~~~~ "+map);
									Result delResult = bgspServiceAccessObject.callServiceFlatting("deleteAgentCollectAgreement",context.getDataMap());
									logger.info("=======新增协议，第三方失败后，删除协议===delResult："+delResult);
								  
									context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
					                context.setData(ParamKeys.TXN_STS, Constants.TXNSTS_REVERSE);
					                context.setData(ParamKeys.THD_TXN_STS, Constants.TXNSTS_FAIL);
					                context.setData(ParamKeys.RESPONSE_TYPE, Constants.RESPONSE_TYPE_FAIL);
					                context.setData(ParamKeys.RESPONSE_CODE, ErrorCodes.EUPS_THD_SYS_ERROR);
					                context.setData(ParamKeys.RESPONSE_MESSAGE, Constants.RESPONSE_MSG_FAIL);
					                context.setData(ParamKeys.THD_RSP_MSG,Constants.RESPONSE_MSG_FAIL);
							}
						context.setData("PKGCNT", "000000");
			}else{
				//保存到EupsCusAgentJournal表中
				log.info("============insert   EupsCusAgentJournal");
				EupsCusAgentJournal eupsCusAgentJournal=new EupsCusAgentJournal();
				eupsCusAgentJournal.setSqn(context.getData("sqn").toString());
				eupsCusAgentJournal.setEupsBusTyp("ELEC00");
				String rsvFld3=DateUtils.format(new Date(),DateUtils.STYLE_yyyyMMddHHmmss);
				eupsCusAgentJournal.setRsvFld3(rsvFld3);
				String oprTypeBank=(String)context.getData("oprTypeBank");
				if(oprTypeBank.equals("1")){
					context.setData("oprTyp", "1");
				}
				String rsvFld1=context.getData("oprTyp").toString().trim()+context.getData("agtSts").toString().trim();
				eupsCusAgentJournal.setRsvFld1(rsvFld1);
				eupsCusAgentJournal.setThdCusNo((String)context.getData("cusNo"));
				eupsCusAgentJournal.setCusAc(newCusAc);
				eupsCusAgentJournal.setAgrBr(context.getData("cusTyp").toString());
				eupsCusAgentJournal.setCusNme((String)context.getData("cusNme"));
				eupsCusAgentJournal.setIdTyp((String)context.getData("idTyp"));
				eupsCusAgentJournal.setIdNo((String)context.getData("idNo"));
				eupsCusAgentJournal.setTel((String)context.getData("cmuTel"));
				eupsCusAgentJournal.setTxnDte(new Date());
				eupsCusAgentJournal.setRsvFld2("301");
				eupsCusAgentJournal.setComNo(context.getData("comNos").toString());
				eupsCusAgentJournalRepository.insert(eupsCusAgentJournal);
				
				log.info("============End  insert   EupsCusAgentJournal");
			}
		}else{
			context.setData("thdRspCde", "83");
			throw new CoreException("签约失败");
		}
		if(context.getData(ParamKeys.THD_SQN) !=null){
				String thdTxnDte=context.getData("thdTxnDate").toString();
				String thdTxnTme=context.getData("thdTxnTime").toString();
				context.setData(ParamKeys.THD_TXN_DATE, DateUtils.parse(thdTxnDte));
				context.setData(ParamKeys.THD_TXN_TIME, DateUtils.parse((thdTxnDte+thdTxnTme),DateUtils.STYLE_yyyyMMddHHmmss));
				if(editCusAgtResult.isSuccess() && editCusAgtResult.getResponseType().toString().equals("N") ){
				}else{
						context.setData("thdRspCde", "83");
				}
				context.setData("PKGCNT", "000000");
		}
		
		logger.info("=============End    InsertCusAgentServiceAction  ");
	}
	public String  selectAgent(Context context,String cusAc){
		
		//列表查询 获得协议编号
		Map<String, Object> map=new HashMap<String, Object>();
		map.put("cusAc", cusAc);
		//header 设定
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
		map.put("obkBk", "301");
		if(context.getData(ParamKeys.THD_SQN)!=null){
			map.put("bk", "01441999999");
			map.put("br", "01441131999");
			context.setData("tlr", "ABIR148");
			context.setData("bk", "01441999999");
			context.setData("br", "01441131999");
		}
		map.put("cusAc", cusAc);
		logger.info("~~~~~~~~~~requestHeader~~~~map~~~~~ "+map);
		logger.info("~~~~~~~~~~列表查询开始 ");
		//上代收付取协议编号
		Result accessObjList = bgspServiceAccessObject.callServiceFlatting("queryListAgentCollectAgreement",map);
		if(!accessObjList.isSuccess()){
//					throw new CoreException(accessObjList.getPayload().get("responseMessage").toString());
		}
		logger.info("~~~~~~~~~~列表查询结束~~~~"+accessObjList);
		if(accessObjList.getPayload().get("agentCollectAgreement") ==null	){
			context.setData("thdRspCde", "80");
		}
		List<Map<String,Object>> list=(List<Map<String, Object>>)accessObjList.getPayload().get("agentCollectAgreement");
		String agdAgrNo=list.get(0).get("agdAgrNo").toString();
		logger.info("~~~~~~~~~~~~~~~协议编号： "+agdAgrNo);
		context.setData("agdAgrNo", agdAgrNo);
		return agdAgrNo;
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
}
