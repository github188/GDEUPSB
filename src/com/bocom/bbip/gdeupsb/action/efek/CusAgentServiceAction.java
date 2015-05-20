package com.bocom.bbip.gdeupsb.action.efek;

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
import com.bocom.bbip.comp.CommonRequest;
import com.bocom.bbip.comp.account.AccountService;
import com.bocom.bbip.comp.account.support.CusActInfResult;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.action.common.CommThdRspCdeAction;
import com.bocom.bbip.eups.adaptor.ThirdPartyAdaptor;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsActSysPara;
import com.bocom.bbip.eups.entity.EupsThdBaseInfo;
import com.bocom.bbip.eups.repository.EupsActSysParaRepository;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDErrorCodes;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.strategy.efek.agent.UpdateCusAgentServiceAction;
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
public class CusAgentServiceAction extends BaseAction{
	@Autowired
	BBIPPublicService bbipPublicService;
	@Autowired
	BGSPServiceAccessObject bgspServiceAccessObject;
	@Autowired
	EupsActSysParaRepository eupsActSysParaRepository;

	@Qualifier("callThdTradeManager")
    ThirdPartyAdaptor callThdTradeManager;
	private final static Log logger=LogFactory.getLog(UpdateCusAgentServiceAction.class);
		/**
		 * 协议新增修改注销
		 */
		public void execute(Context context)throws CoreException,CoreRuntimeException{
				logger.info("============Start  CusAgentServiceAction ");
				
				String agtSts=(String)context.getData("agtSts");
				if(null != agtSts){
					if("3".equals(agtSts.toString().trim())){
							throw new CoreException(GDErrorCodes.EUPS_ELEC00_82_ERROR);
					}
				}
				if(("20").equals((String)context.getData("chlTyp")) || ("50").equals((String)context.getData("chlTyp")) || ("20").equals((String)context.getData("chn")) || ("50").equals((String)context.getData("chn"))){
					logger.info("=======================适配器");
					context.setData("br", "01441800999");
					context.setData("bk", "01441999999");
					String pwd=context.getData("area").toString().trim();
					while(pwd.length()<20){
						pwd=pwd+" ";
					}
					context.setData("pwd", pwd);
				}
				if(context.getData(ParamKeys.THD_SQN)==null){
					if(null != context.getData("area")){
						String pwd=context.getData("area").toString().trim();
						while(pwd.length()<20){
							pwd=pwd+" ";
						}
						context.setData("pwd", pwd);
					}
				}
				checkCusNmeAndPwd(context);
				if(context.getData(ParamKeys.THD_SQN)!=null){
					context.setData("br", "01441800999");
					context.setData("bk", "01441999999");
				}
				
				String cusAc=context.getData("cusAc");
				String comNo=context.getData("comNos").toString();
				context.setData("comNo", comNo);
				String comNoSub=comNo.substring(0,4);
				//代收付单位编号
				EupsActSysPara eupsActSysPara=new EupsActSysPara();
				eupsActSysPara.setComNo(comNoSub);
				String sqlNo=eupsActSysParaRepository.find(eupsActSysPara).get(0).getSplNo();
				context.setData("comNo", sqlNo);
				logger.info("~~~~~~~~~comNo="+context.getData(ParamKeys.COMPANY_NO));
				
				context.setData("obkBk", "301");
				context.setData(ParamKeys.TRACE_NO, bbipPublicService.getTraceNo());
				context.setData("ccy", "CNY");
				context.setData("agrVldDte", DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMdd));
				context.setData("agrExpDte", "99991231");
				context.setData("agrChl","01");
				context.setData("agtSrvCusId",context.getData("cusNo"));
				context.setData("bvCde", "009");

				Map<String, Object> map=setAgentCollectAgreementMap(context);
				String oprTyp=context.getData("oprTyp").toString();
				String mothed="";
				String agdAgrNo="";
				int i=0;
				context.setData("oprTypeBank", "0");
				if("0".equals(oprTyp)){
					logger.info("~~~~~~~~~~~~~~~~~Enter  eups.commInsertCusAgenteELEC00 ");
					mothed="eups.commInsertCusAgenteELEC00";
					context.setData(GDParamKeys.NEWBANKNO, "301");
					map.put("cusAc", context.getData("newCusAc"));
					map.put("cusNme", context.getData("newCusName"));
					cusAc=context.getData("newCusAc");
					
				}else if("1".equals(oprTyp)){
					//先删除协议，然后再添加 
					context.setData("oprTyp", "2");
					context.setData("oprTypeBank", "1");
					logger.info("~~~~~~~~~~~~~~~~~Enter  eups.commUpdateCusAgentELEC00 ");
					mothed="eups.commDelCusAgentELEC00";
					agdAgrNo=selectList(context);
					map.put("agdAgrNo", agdAgrNo);
					i=1;
				}else {
					agdAgrNo=selectList(context);
					map.put("agdAgrNo", agdAgrNo);
					logger.info("~~~~~~~~~~~~~~~~~Enter  eups.commDelCusAgentELEC00 ");
					mothed="eups.commDelCusAgentELEC00";
				}
				if(context.getData(ParamKeys.THD_SQN)!=null){										
					context.setData("sqns", context.getData("sqns"));
					context.setData("thdToBank", "thdToBank");
					if(context.getData("oprTyp").toString().trim().equals("1")){
							context.setData("callThd", "callThd");
					}
				}else{
						context.setData("bankToThd", "bankToThd");	
						context.setData("sqns", context.getData("sqn"));
				}
				
				context.setData("cusAc", cusAc);
				//添加 agentCollectAgreement
				List<Map<String, Object>> list=new ArrayList<Map<String,Object>>();
				list.add(map);
				context.setData(ParamKeys.AGENT_COLLECT_AGREEMENT, list);
				//第三方如果空 设定一个值判断返回报文
				if(context.getData(ParamKeys.THD_SQN)==null){
						constantOfSoapUI(context,comNo);
						context.setData(GDParamKeys.SVRCOD, "30");
				}
				context.setData("agtSrvCusPnm", context.getData("settleAccountsName"));
				
				
				//添加 customerInfo
				Map<String, Object> cusMap=setCustomerInfoMap(context);
				cusMap.put("cusMap", agdAgrNo);
				List<Map<String, Object>> cusList=new ArrayList<Map<String,Object>>();
				cusList.add(cusMap);
				
				context.setData("customerInfo", cusList);
				
				bbipPublicService.synExecute(mothed, context);
				if(i==1){
					mothed="eups.commInsertCusAgenteELEC00";
					context.setData(GDParamKeys.NEWBANKNO, "301");
					context.setData("oprTyp", "0");
					map.put("cusAc", context.getData("newCusAc"));
					map.put("cusNme", context.getData("newCusName"));
					map.put("agdAgrNo", null);
					context.setData("agdAgrNo", null);
					
					context.setData(ParamKeys.AGENT_COLLECT_AGREEMENT, list);
					context.setData("customerInfo", cusList);
					bbipPublicService.synExecute(mothed, context);
					context.setData("oprTyp", "1");
					cusAc=context.getData("newCusAc");
				}
				if(context.getData(ParamKeys.THD_SQN)!=null){
							context.setData("PKGCNT", "000000");
				}
				log.info("============End  CusAgentServiceAction");
		}
		
	    /**
		 *报文信息 
		*/
		public void constantOfSoapUI(Context context,String comNo){
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
				if(context.getData(GDParamKeys.TRADE_RECEIVE) == null){
					if(comNo.length()>4){
							comNo=comNo.substring(0,4)+"00";
					}else{
						while(comNo.length()<6){
							comNo=comNo+"0";
						}
					}
					context.setData(GDParamKeys.TRADE_RECEIVE, comNo);//交易接收方
				}
				context.setData(GDParamKeys.TRADE_SOURCE_ADD, GDConstants.TRADE_SOURCE_ADD);//交易源地址
				context.setData(GDParamKeys.TRADE_AIM_ADD, GDConstants.TRADE_AIM_ADD);//交易目标地址
				context.setData("PKGCNT", "000001");
				context.setData(GDParamKeys.BUS_IDENTIFY, "YDLW07");		
				context.setData("sqns", context.getData(ParamKeys.SEQUENCE));
			}
/**
 * 客户信息
 */
		private Map<String, Object> setCustomerInfoMap(Context context) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("agtCllCusId", context.getData("cusNo"));
			map.put("agtSrvCusId", context.getData("cusNo"));
			map.put("agtSrvCusPnm", context.getData("settleAccountsName"));
			map.put("cusTyp", context.getData("cusTyp"));
			map.put("cusNo", context.getData(ParamKeys.CUS_NO));
			map.put("cusAc", context.getData("newCusAc"));
			map.put("cusNme", context.getData("newCusName"));
			map.put("ccy", "CNY");
			map.put("bvCde", "009");
			map.put("idTyp", context.getData(ParamKeys.ID_TYPE));
			map.put("idNo", context.getData(ParamKeys.ID_NO));
			
			map.put("bvCde", "009");
			
			if (StringUtils.isNotBlank((String) context.getData("bvNo"))) {
				map.put("bvNo", (String) context.getData("bvNo"));
			}
			map.put("ourOthFlg", "0");
			return map;
		}
		/**
		 * 协议信息	
		 */
		private Map<String, Object> setAgentCollectAgreementMap(Context context) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put(ParamKeys.BUS_TYP, "0");
			map.put("cusNo", context.getData("cusNo"));
			map.put("agtCllCusId", context.getData("cusNo"));
			map.put("cusNme", context.getData("cusNme"));
			map.put("agtSrvCusId", context.getData("cusNo"));
			map.put("bvCde", "009");
			map.put("obkBk", "301");
			map.put("agtSrvCusPnm", context.getData("settleAccountsName"));
			map.put("cusAc", context.getData(GDParamKeys.NEWCUSAC));
			map.put("acoAc", context.getData(GDParamKeys.NEWCUSAC));
			if (StringUtils.isNotBlank((String) context.getData("pwd"))) {
				map.put("pwd", (String) context.getData("pwd"));
			}
			map.put("bvCde", context.getData("bvCde"));
			map.put("buyTyp", "0");
			map.put("busKnd", "A089");
			map.put("agrVldDte", DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMdd));
			map.put("agrExpDte", "99991231");
			map.put("agrTlr",context.getData(ParamKeys.TXN_TLR));
			
			map.put("agtSrvCusPnm",context.getData("thdCusNme"));
			map.put("agtSrvCusId",context.getData("cusNo"));
			map.put("ageBr", context.getData(ParamKeys.BK));
			map.put("agrBr", context.getData(ParamKeys.BR));
			map.put("comNo", context.getData(ParamKeys.COMPANY_NO));
			EupsThdBaseInfo baseInfo = new EupsThdBaseInfo();
			baseInfo.setEupsBusTyp((String) context.getData(ParamKeys.EUPS_BUSS_TYPE));
			if (StringUtils.isNotBlank((String) context.getData(ParamKeys.COMPANY_NO))) {
				baseInfo.setComNo((String) context.getData(ParamKeys.COMPANY_NO));
			}
			
			map.put(ParamKeys.CCY, "CNY");
			// TODO 暂用0，待确认
			map.put("cusFeeDerFlg", "0"); 
			map.put("agtSrvCusId", context.getData("agtSrvCusId"));
			map.put("agtSrvCusPnm", context.getData(ParamKeys.THD_CUS_NME));
			// YYYYMMDD默认当日
			map.put("agrVldDte",DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMdd)); 
			// YYYYMMDD默认最大日，99991231
			map.put("agrExpDte", "99991231"); 
			map.put("idNo", context.getData(ParamKeys.ID_NO));
			map.put(ParamKeys.CMU_TEL, context.getData(ParamKeys.CMU_TEL));
			return map;

		}
		/**
		 * 列表查询
		 */
		public String  selectList(Context context) throws CoreException{
			String cusAc=context.getData("cusAc").toString();
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
//			if(context.getData(ParamKeys.THD_SQN)!=null){
//				map.put("bk", "01441999999");
//				map.put("br", "01441131999");
//				context.setData("tlr", "EFC0000");
//				context.setData("bk", "01441999999");
//				context.setData("br", "01441131999");
//			}
			map.put("cusAc", context.getData("cusAc"));
//			map.put("cusAc", "6222620710007282377");
			logger.info("~~~~~~~~~~requestHeader~~~~map~~~~~ "+map);
			logger.info("~~~~~~~~~~列表查询开始 ");
			//上代收付取协议编号
			Result accessObjList = bgspServiceAccessObject.callServiceFlatting("queryListAgentCollectAgreement",map);
			if(!accessObjList.isSuccess()){
						throw new CoreException(accessObjList.getPayload().get("responseMessage").toString());
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

		/**
		 * 外发第三方
		 */
		public void callThd(Context context) throws CoreException{
			constantOfSoapUI(context,(String)context.getData("comNos"));
			Date txnDte=(Date)context.getData(ParamKeys.TXN_DTE);
			Date txnTme=DateUtils.parse(context.getData("txnTme").toString());
			
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
			                    log.info("The third process response successful.");
			                    context.setData(ParamKeys.TXN_STS, Constants.TXNSTS_SUCCESS);
			                    context.setData(ParamKeys.THD_TXN_STS, Constants.THD_TXNSTS_SUCCESS);
			                    context.setData(ParamKeys.RSP_CDE, GDConstants.SUCCESS_CODE);
			                    context.setData(ParamKeys.RSP_MSG, "交易成功");									                
			                }else if(BPState.isBPStateOvertime(context)){
			                	context.setData(ParamKeys.THD_TXN_STS,Constants.THD_TXNSTS_FAIL);
			                	context.setData(GDParamKeys.MSGTYP, "E");
			                	context.setData(ParamKeys.RSP_CDE, "EFE999");
			                	context.setData(ParamKeys.RSP_MSG, "交易超时");
			                	throw new CoreException("交易超时");
			                }else{
			                	context.setData(ParamKeys.THD_TXN_STS,Constants.THD_TXNSTS_FAIL);
			                	context.setData(GDParamKeys.MSGTYP, "E");
			                	context.setData(ParamKeys.RSP_CDE, "EFE999");
			                	context.setData(ParamKeys.RSP_MSG, "交易失败");
			                	throw new CoreException(responseCode);
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
		}
/**
 *查询账号姓名 ,验密
 */
		public void checkCusNmeAndPwd(Context context) throws CoreException{
			log.info("================Start   checkCusNmeAndPwd");
			String cusAc = (String)context.getData("cusAc");
			if(null != cusAc){
				cusAc=cusAc.trim();
			}
			if("0".equals((String)context.getData("oprTyp")) || "1".equals((String)context.getData("oprTyp"))){
				cusAc=context.getData("newCusAc").toString().trim();
				CusActInfResult cusactinfresult = new CusActInfResult();
				try {
					cusactinfresult = get(AccountService.class).getAcInf(CommonRequest.build(context), cusAc);
				} catch (CoreException e) {
					logger.info("查询账号状态失败", e);
				}
				if(!cusactinfresult.isSuccess()){
					throw new CoreException("没有查询到该账户");
				}
				String newCusNme=cusactinfresult.getCusName();
				context.setData("newCusName", newCusNme);
			}
			String chl=(String)context.getData("chl");
			if(null != chl){
				if("00".equals(chl.toString().trim())){
					//验密
					String pwd =(String)context.getData("pwd");
					Result result=get(AccountService.class).auth(CommonRequest.build(context), cusAc, pwd);
					if (!result.isSuccess()) {
							Map<String, Object> ext=new HashMap<String, Object>();
							ext.put("drwMde","1");
							ext.put("pswLvl","1");
							ext.put("txnPsw",context.getData("pwd"));
							CommonRequest commonReq = CommonRequest.build(context, ext);
							CusActInfResult cs = get(AccountService.class).getAcInf(commonReq, context.getData("cusAC").toString());
							if(!cs.isSuccess()){
								throw new CoreException(GDErrorCodes.EUPS_PASSWORD_ERROR);
						}
					}
				}
			}
			context.setData("pwd", null);
			log.info("================End   checkCusNmeAndPwd");
		}
}