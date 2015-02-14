package com.bocom.bbip.gdeupsb.action.efek;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

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
	/**
	 * 代收付日志服务
	 */
	public void execute(Context context)throws CoreException,CoreRuntimeException{
			log.info("===============Start  LogServiceAction");
			
			context.setData(GDParamKeys.SVRCOD, "90");
			String comNo=context.getData(ParamKeys.COMPANY_NO).toString();
			String eupsBusTyp=context.getData(ParamKeys.EUPS_BUSS_TYPE).toString();
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
			// 需修改 文件名  时间+类型
			String fileName=DateUtils.format(DateUtils.parse(context.getData(ParamKeys.TXN_TME).toString()),DateUtils.STYLE_yyyyMMddHHmmss)+"_05"+".txt";
			context.setData(GDParamKeys.FILE_NAME,fileName);
			
			for (EupsStreamNo eupsStreamNo : list) {
				Date txnDte=eupsStreamNo.getTxnDte();
				eupsStreamNo.setRsvFld1(DateUtils.format(txnDte, DateUtils.STYLE_yyyyMMdd));
				Date txnTme=eupsStreamNo.getTxnTme();
				eupsStreamNo.setRsvFld2(DateUtils.formatAsHHmmss(txnTme));
				eupsStreamNo.setRsvFld3(fileName);
				//文件类型  明细
				eupsStreamNo.setRsvFld4("05");
				//TODO MD5
				eupsStreamNo.setRsvFld5("加密");
			}
			
			//
			EupsThdFtpConfig eupsThdFtpConfig=eupsThdFtpConfigRepository.findOne("elecLogService");
			eupsThdFtpConfig.setLocFleNme(fileName);
			eupsThdFtpConfig.setRmtFleNme(fileName);
			//拼装文件
//			Map<String, Object>resultMap=createFileMap(context, fileName, comNo,eupsThdFtpConfig);
			Map<String, Object> resultMap=new HashMap<String, Object>();
			resultMap.put(ParamKeys.EUPS_FILE_DETAIL,BeanUtils.toMaps(list));
			//生成文件
			operateFileAction.createCheckFile(eupsThdFtpConfig, "logService", fileName, resultMap);
			//TODO 上传至服务器
//			operateFTPAction.putCheckFile(eupsThdFtpConfig);   
			//外发第三方
			callThd(context);
			//交易成功删除流水信息
			if(GDConstants.SUCCESS_CODE.equals(context.getData(ParamKeys.RESPONSE_CODE).toString())){
				for (EupsStreamNo eupsStreamNo : list) {
	//				eupsStreamNoRepository.delete(eupsStreamNo);
					System.out.println("~~~~~~~~~~~~~~                 "+eupsStreamNo.getSqn());
				}
			}
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
	public Map<String, Object> createFileMap(Context context,String fileName,String comNo,EupsThdFtpConfig eupsThdFtpConfig)throws CoreException,CoreRuntimeException{
		log.info("==================Start  LogServiceAction createFileMap ");
		Map<String, Object> resultMap=new HashMap<String, Object>();
//		operateFTPAction.getFileFromFtp(eupsThdFtpConfig);
		
		//解析文件
		List<Map<String, Object>> mapList=operateFileAction.pareseFile(eupsThdFtpConfig, "logService");
//		List<Map<String, Object>> detailList=new ArrayList<Map<String,Object>>();
//		for (Map<String, Object> map : mapList) {
//				detailList.add(map);
//		} 
		resultMap.put(ParamKeys.EUPS_FILE_DETAIL, BeanUtils.toMaps(mapList));
		
		log.info("==================End  LogServiceAction createFileMap ");
		return resultMap;
	}
}
