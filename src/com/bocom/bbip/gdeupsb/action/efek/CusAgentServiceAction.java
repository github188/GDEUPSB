package com.bocom.bbip.gdeupsb.action.efek;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.CollectionUtils;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.action.common.CommThdRspCdeAction;
import com.bocom.bbip.eups.adaptor.ThirdPartyAdaptor;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsCusAgent;
import com.bocom.bbip.eups.repository.EupsCusAgentRepository;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.service.BGSPServiceAccessObject;
import com.bocom.bbip.service.Result;
import com.bocom.bbip.utils.BeanUtils;
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
	@Qualifier("callThdTradeManager")
    ThirdPartyAdaptor callThdTradeManager;
	@Autowired
	EupsCusAgentRepository eupsCusAgentRepository;
		/**
		 * 协议新增修改注销
		 */
		public void execute(Context context)throws CoreException,CoreRuntimeException{
			log.info("============Start  CusAgentServiceAction ");
				context.setData(GDParamKeys.SVRCOD, "30");
				String conSign=context.getData(GDParamKeys.CONSIGN).toString();
				
				String payNo=context.getData(GDParamKeys.PAY_NO).toString();
				context.setData(ParamKeys.AGD_AGR_NO,payNo);
				//证件类型	
				String IdTpCd=context.getData(ParamKeys.ECIF_ID_TYPE_CD).toString();
				context.setData(ParamKeys.ID_TYPE,IdTpCd);
				//代理服务客户标识
				context.setData("agtSrvCusId", context.getData(ParamKeys.CUS_AC));
				//代收付种类
				context.setData(ParamKeys.BUS_TYP, "2");
				//TODO  业务种类
				context.setData(ParamKeys.BUSS_KIND, "A087");
				//代理服务客户标识
				context.setData(ParamKeys.AGT_SRV_CUS_PNM, context.getData(ParamKeys.BR));
				//协议生效日期
				context.setData("agrVldDte", DateUtils.parse(DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMdd)));
				//0:有效,1:无效,默认:0
				context.setData("pedAgrSts", "0");
				//签约时间
				context.setData("agrTme",DateUtils.parse(DateUtils.formatAsTranstime(new Date())));
				//0-个人;1-单位
				context.setData("cusTyp", context.getData(ParamKeys.ACC_TYPE));
				//证件号码
				context.setData(ParamKeys.ID_NO, context.getData(ParamKeys.ECIF_REF_NUM));
				//第三方客户标识
				context.setData(ParamKeys.THD_CUS_NO, context.getData(GDParamKeys.NEWCUSAC));
				//创建日期
				context.setData(ParamKeys.CRE_DTE, DateUtils.parse(DateUtils.formatAsSimpleDate(new Date())));
				//扩展标志
				context.setData(ParamKeys.FIL_FLG, "0");
				
				//电话邮箱签约状态
				context.setData(ParamKeys.ORDER_TEL_NO, context.getData(GDParamKeys.MOBPHONE));
				context.setData("eml", context.getData(GDParamKeys.EMAIL));
				context.setData(ParamKeys.TPS_STS, context.getData(GDParamKeys.AGT_STS));
				
				Map<String, Object> agentMap = new HashMap<String, Object>();
				agentMap.putAll(context.getDataMap());
				
//				Result agentResult= get(BGSPServiceAccessObject.class).callServiceFlatting("queryCorporInfo",agentMap);
//				boolean bo=true;
//				if(!CollectionUtils.isEmpty(agentResult.getPayload())){
//					bo=false;
//				}
				EupsCusAgent eupsCusAgent=BeanUtils.toObject(context.getDataMap(), EupsCusAgent.class);
				if("0".equals(conSign)){//新增
//					if(bo==true){
//							throw new CoreException("该账户已签约");
//					}else{
							eupsCusAgentRepository.insert(eupsCusAgent);
//					}
				}else if("1".equals(conSign)){//修改
//					if(bo==false){
//							throw new CoreException("该账户没有签约，不能修改");
//					}else{
							eupsCusAgentRepository.update(eupsCusAgent);
//					}
				}else if("2".equals(conSign)){//注销
//					if(bo==true){
//							throw new CoreException("该账户没有签约，不能注销");
//					}else{
							eupsCusAgentRepository.delete(eupsCusAgent);
//					}
				}else{
					throw new CoreException("没有该功能");
				}
				callThd(context);
				log.info("============End  CusAgentServiceAction");
		}
		/**
		 * 外发第三方
		 */
		public void callThd(Context context){
			log.info("============Start  CusAgentServiceAction callThd ");
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
