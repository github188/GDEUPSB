package com.bocom.bbip.gdeupsb.action.efek;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.action.common.CommThdRspCdeAction;
import com.bocom.bbip.eups.action.common.OperateFTPAction;
import com.bocom.bbip.eups.action.common.OperateFileAction;
import com.bocom.bbip.eups.adaptor.ThirdPartyAdaptor;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsThdFtpConfig;
import com.bocom.bbip.eups.repository.EupsThdFtpConfigRepository;
import com.bocom.bbip.file.Marshaller;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.EupsStreamNo;
import com.bocom.bbip.gdeupsb.repository.EupsStreamNoRepository;
import com.bocom.bbip.thd.org.apache.commons.collections.CollectionUtils;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
/**
 * @author liyawei
 */
public class LogServiceAction extends BaseAction{
	@Autowired
	 EupsStreamNoRepository eupsStreamNoRepository;
	@Autowired
	@Qualifier("callThdTradeManager")
	ThirdPartyAdaptor callThdTradeManager;
	@Autowired
	OperateFTPAction operateFTPAction;
	@Autowired
	OperateFileAction operateFileAction;
	@Autowired
	Marshaller marshaller;
	@Autowired
	EupsThdFtpConfigRepository eupsThdFtpConfigRepository;
	@Autowired
	BBIPPublicService bbipPublicService;
	/**
	 * 代收付日志服务
	 */
	public void execute(Context context)throws CoreException,CoreRuntimeException{
			log.info("===============Start  LogServiceAction");
			String eupsBusTyp="ELEC00";
			context.setData(ParamKeys.EUPS_BUSS_TYPE, eupsBusTyp);
			context.setData(GDParamKeys.SVRCOD, "90");
			//得到分组
			Map<String, Object> maps=new HashMap<String, Object>();
			Date txnDte=new Date();
			context.setData(ParamKeys.TXN_DTE, txnDte);
			Date txnDate=DateUtils.parse(DateUtils.formatAsSimpleDate(new Date()));
			maps.put("txnDte", txnDate);
			List<Map<String, Object>> mapList=eupsStreamNoRepository.findComNoGroup(maps);
			for(Map<String, Object> map:mapList){
					String comNo=map.get("COM_NO").toString();
					String bankNo=map.get("THD_OBK_CDE").toString();
					
					String fileName=comNo+bankNo+DateUtils.format(txnDate, DateUtils.STYLE_yyyyMMdd)+"_05.txt";
					context.setData(GDParamKeys.FILE_NAME,fileName);
					
					//得到日志集合
					EupsStreamNo eupsStreamNos=new EupsStreamNo();
					eupsStreamNos.setComNo(comNo);
					eupsStreamNos.setEupsBusTyp(eupsBusTyp);
					//TODO 判断txnSts是否需要‘C’
					List<EupsStreamNo> list=eupsStreamNoRepository.findLogService(eupsStreamNos);
					if(CollectionUtils.isEmpty(list)){
						context.setData(ParamKeys.RSP_CDE, "000000");
						context.setData(ParamKeys.RSP_MSG, "无错误日志");
						return;
					}
					
					EupsThdFtpConfig eupsThdFtpConfig=eupsThdFtpConfigRepository.findOne("elecLogService");
					log.info("~~~~~~~~~eupsThdFtpConfig~~~~"+eupsThdFtpConfig);
					try{
							//拼装文件
							String sqn=bbipPublicService.getBBIPSequence();
							Map<String, Object>resultMap=createFileMap(sqn,list,map);
							//生成文件
							eupsThdFtpConfig.setLocFleNme(fileName);
							operateFileAction.createCheckFile(eupsThdFtpConfig, "logService", fileName, resultMap);
							//TODO 上传至服务器
							operateFTPAction.putCheckFile(eupsThdFtpConfig);   
					}catch(Exception e){
			            context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
			            log.info("File create error : " + e.getMessage());          
			            throw new CoreException(e.getMessage());
					}
					//外发第三方
					callThd(context);
					//交易成功删除流水信息
					if(GDConstants.SUCCESS_CODE.equals(context.getData(ParamKeys.RESPONSE_CODE).toString())){
			//				eupsStreamNoRepository.delete(eupsStreamNo);
							System.out.println("~~~~~~~~~~~~~~delete                 "+comNo);
					}
			}
			log.info("===============End  LogServiceAction");
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
				
				//外发第三方 时间
				Date txnDate=(Date)context.getData(ParamKeys.TXN_DTE);
				context.setData(ParamKeys.TXN_DTE, DateUtils.format(txnDate, DateUtils.STYLE_yyyyMMdd));
				context.setData(ParamKeys.TXN_TME, DateUtils.format(txnDate, DateUtils.STYLE_HHmmss));
				
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
	
/**
 * 拼装文件Map
*/
	public Map<String, Object> createFileMap(String sqn,List<EupsStreamNo> list,Map<String, Object> maps)throws CoreException,CoreRuntimeException{
		log.info("==================Start  LogServiceAction createFileMap ");
		//头部
		Map<String, Object> mapHeader=new HashMap<String, Object>();
		mapHeader.put(ParamKeys.SEQUENCE, sqn);
		mapHeader.put(ParamKeys.BANK_NO, maps.get("THD_OBK_CDE"));
		mapHeader.put(ParamKeys.COMPANY_NO, maps.get("COM_NO"));
		mapHeader.put(GDParamKeys.TOT_COUNT,maps.get("TOT_COUNT"));
		
		//主体
		List<EupsStreamNo> mapList=new ArrayList<EupsStreamNo>();
		for(EupsStreamNo eupsStreamNo:list){
			System.out.println();
			String txnDte=DateUtils.format(eupsStreamNo.getTxnDte(),DateUtils.STYLE_yyyyMMdd);
			String txnTme=DateUtils.formatAsHHmmss(eupsStreamNo.getTxnTme());
			eupsStreamNo.setRsvFld2(txnDte);
			eupsStreamNo.setRsvFld3(txnTme);
				mapList.add(eupsStreamNo);
		}
		log.info("==========mapList===="+mapList);
		Map<String, Object> resultMap=new HashMap<String, Object>();
		resultMap.put(ParamKeys.EUPS_FILE_HEADER, mapHeader);
		resultMap.put(ParamKeys.EUPS_FILE_DETAIL, BeanUtils.toMaps(mapList));
		log.info("==================End  LogServiceAction createFileMap ");
		return resultMap;
	}
}
