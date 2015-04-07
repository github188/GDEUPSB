package com.bocom.bbip.gdeupsb.action.efek;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsActSysPara;
import com.bocom.bbip.eups.entity.EupsThdBaseInfo;
import com.bocom.bbip.eups.repository.EupsActSysParaRepository;
import com.bocom.bbip.gdeupsb.common.GDConstants;
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
	private final static Log logger=LogFactory.getLog(UpdateCusAgentServiceAction.class);
		/**
		 * 协议新增修改注销
		 */
		public void execute(Context context)throws CoreException,CoreRuntimeException{
				logger.info("============Start  CusAgentServiceAction ");
				context.setData("sqns", context.getData("sqn"));
				context.setData(GDParamKeys.SVRCOD, "30");
				String comNo=context.getData("comNo").toString();
				//代收付单位编号
				EupsActSysPara eupsActSysPara=new EupsActSysPara();
				eupsActSysPara.setComNo(comNo);
				String sqlNo=eupsActSysParaRepository.find(eupsActSysPara).get(0).getSplNo();
				context.setData("comNo", sqlNo);
				logger.info("~~~~~~~~~comNo="+context.getData(ParamKeys.COMPANY_NO));
				
				
				context.setData(ParamKeys.TRACE_NO, bbipPublicService.getTraceNo());
				//TODO 
				context.setData("idTyp", "01");
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
				if("0".equals(oprTyp)){
					logger.info("~~~~~~~~~~~~~~~~~Enter  eups.commInsertCusAgenteELEC00 ");
					mothed="eups.commInsertCusAgenteELEC00";
					context.setData(GDParamKeys.NEWBANKNO, "301");
					map.put("cusAc", context.getData("newCusAc"));
					map.put("cusNme", context.getData("newCusName"));
					bbipPublicService.synExecute(mothed, context);
				}else if("1".equals(oprTyp)){
					//先删除协议，然后再添加 
					logger.info("~~~~~~~~~~~~~~~~~Enter  eups.commUpdateCusAgentELEC00 ");
					context.setData(GDParamKeys.NEWBANKNO, "301");
					map.put("cusAc", context.getData("newCusAc"));
					map.put("cusNme", context.getData("newCusName"));
					agdAgrNo=selectList(context);
					map.put("agdAgrNo", agdAgrNo);
					
					mothed="eups.commDelCusAgentELEC00";
					bbipPublicService.synExecute("eups.commDelCusAgentELEC00", context);
					i=1;
				}else {
					agdAgrNo=selectList(context);
					map.put("agdAgrNo", agdAgrNo);
					logger.info("~~~~~~~~~~~~~~~~~Enter  eups.commDelCusAgentELEC00 ");
					mothed="eups.commDelCusAgentELEC00";
					bbipPublicService.synExecute("eups.commDelCusAgentELEC00", context);
				}
				//添加 agentCollectAgreement
				List<Map<String, Object>> list=new ArrayList<Map<String,Object>>();
				list.add(map);
				context.setData(ParamKeys.AGENT_COLLECT_AGREEMENT, list);
				constantOfSoapUI(context);
				context.setData("agtSrvCusPnm", context.getData("settleAccountsName"));
				
				
				//添加 customerInfo
				Map<String, Object> cusMap=setCustomerInfoMap(context);
				cusMap.put("cusMap", agdAgrNo);
				List<Map<String, Object>> cusList=new ArrayList<Map<String,Object>>();
				cusList.add(cusMap);
				context.setData("customerInfo", cusList);			
				
						
				if(i==1){
					mothed="eups.commInsertCusAgenteELEC00";
					bbipPublicService.synExecute(mothed, context);
				}
				
				log.info("============End  CusAgentServiceAction");
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
				context.setData("callThd", "callThd");
			}

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
			//TODO
			map.put("bvCde", "009");
			
			if (StringUtils.isNotBlank((String) context.getData("bvNo"))) {
				map.put("bvNo", (String) context.getData("bvNo"));
			}
			map.put("ourOthFlg", "0");
			return map;
		}

		private Map<String, Object> setAgentCollectAgreementMap(Context context) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put(ParamKeys.BUS_TYP, "0");
			map.put("cusNo", context.getData("cusNo"));
			map.put("agtCllCusId", context.getData("cusNo"));
			map.put("cusNme", context.getData("cusNme"));
			map.put("agtSrvCusId", context.getData("cusNo"));
			map.put("bvCde", "009");
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
			//TODO 
			map.put("agtSrvCusPnm",context.getData("thdCusNme"));
			map.put("agtSrvCusId",context.getData("cusNo"));
			
			map.put("comNo", context.getData(ParamKeys.COMPANY_NO));
			EupsThdBaseInfo baseInfo = new EupsThdBaseInfo();
			baseInfo.setEupsBusTyp((String) context.getData(ParamKeys.EUPS_BUSS_TYPE));
			if (StringUtils.isNotBlank((String) context.getData(ParamKeys.COMPANY_NO))) {
				baseInfo.setComNo((String) context.getData(ParamKeys.COMPANY_NO));
			}
			//TODO 调用代收付接口  获得单位名称
//			String comNme=get(EupsThdBaseInfoRepository.class).findOne(context.getData("comNo").toString()).getComNme();
//			map.put("comNum", comNme);
//			context.setData("comNum", comNme);
			
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
			map.put("cusAc", context.getData("cusAc"));
			logger.info("~~~~~~~~~~requestHeader~~~~map~~~~~ "+map);
			logger.info("~~~~~~~~~~列表查询开始 ");
			//上代收付取协议编号
			Result accessObjList = bgspServiceAccessObject.callServiceFlatting("queryListAgentCollectAgreement",map);
			logger.info("~~~~~~~~~~列表查询结束~~~~"+accessObjList);
			List<Map<String,Object>> list=(List<Map<String, Object>>)accessObjList.getPayload().get("agentCollectAgreement");
			String agdAgrNo=list.get(0).get("agdAgrNo").toString();
			logger.info("~~~~~~~~~~~~~~~协议编号： "+agdAgrNo);
			context.setData("agdAgrNo", agdAgrNo);
			
			return agdAgrNo;
		}
}