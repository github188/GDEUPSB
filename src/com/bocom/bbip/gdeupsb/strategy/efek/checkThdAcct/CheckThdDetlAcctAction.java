package com.bocom.bbip.gdeupsb.strategy.efek.checkThdAcct;

import java.math.BigDecimal;
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
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.CheckDetailAcct;
import com.bocom.bbip.gdeupsb.entity.EupsStreamNo;
import com.bocom.bbip.gdeupsb.repository.EupsStreamNoRepository;
import com.bocom.bbip.thd.org.apache.commons.collections.CollectionUtils;
import com.bocom.bbip.thd.org.apache.commons.lang.StringUtils;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
import com.bocom.jump.bp.core.Executable;
/**
 * @author lyw_i7iiiiii
 */
public class CheckThdDetlAcctAction implements Executable {
	@Autowired
	BBIPPublicService bbipPublicService;
	@Autowired
	EupsStreamNoRepository eupsStreamNoRepository;
	@Autowired
	OperateFileAction operateFileAction;
	@Autowired
	OperateFTPAction operateFTPAction;
	@Autowired
	EupsThdFtpConfigRepository eupsThdFtpConfigRepository;
	@Autowired
	@Qualifier("callThdTradeManager")
	ThirdPartyAdaptor callThdTradeManager;
	private final static Log logger=LogFactory.getLog(CheckThdDetlAcctAction.class);
	/**
	 * 对明细 
	 */
	@Override
	public void execute(Context context) throws CoreException,
			CoreRuntimeException {
			logger.info("=======Start CheckThdDetlAcctAction");
			//流水日期时间
			String sqn =bbipPublicService.getBBIPSequence();
			Date txnDte=DateUtils.parse(DateUtils.formatAsSimpleDate(new Date()));
			Date txnTme=DateUtils.parse(DateUtils.formatAsTranstime(new Date()));
			context.setData(ParamKeys.SEQUENCE, sqn);
			context.setData(ParamKeys.TXN_DTE, txnDte);
			context.setData(ParamKeys.TXN_TME, txnTme);
			
			context.setData(GDParamKeys.TOTNUM, "1");
			context.setData(ParamKeys.BANK_NO, "0301");
			context.setData(ParamKeys.TELLER_ID, "301_030600");  //收款人代码
			context.setData(GDParamKeys.CHECKTIME, txnTme);
			
			context.setData("allCheckNumber", "1");
			context.setData("checkTyp", "03");
			context.setData("number", "000001");
			context.setData("xH", "1");
			
			//初始化对账控制类型
			context.setData(ParamKeys.TXN_CTL_TYP, Constants.TXN_CTL_TYP_CHKBANKFILE_THD);  
			//对账日期
			String chkDte=null;
			if(StringUtils.isNotEmpty(context.getData(GDParamKeys.CHECKDATE).toString())){
					chkDte=context.getData(GDParamKeys.CHECKDATE).toString();
			}else{
					chkDte=DateUtils.format(com.bocom.bbip.thd.org.apache.commons.lang.time.DateUtils.addDays(new Date(), -1),DateUtils.STYLE_yyyyMMdd);
			}
			Date acDate=DateUtils.parse(chkDte);
			
			context.setData(ParamKeys.AC_DATE, acDate);  
			try{
					//生成对账文件
					Map<String, Object> map= encodeFileMap(context);
					//获取FTP信息
//					String ftpId=context.getData()
					String ftpId="elecCheckFile";
//					eupsThdFtpConfig.setFtpNo();  
					EupsThdFtpConfig eupsThdFtpConfig = eupsThdFtpConfigRepository.findOne(ftpId);
					
					//设置本地对账文件名称
//					对账文件名称
					String busType=context.getData(GDParamKeys.BUS_TYPE).toString().substring(0, 2);
					String payType=context.getData(ParamKeys.PAY_TYPE).toString();
					String bankNo=context.getData(ParamKeys.BANK_NO).toString().substring(0,4);
					bankNo="123";
//					String companyNo=context.getData(ParamKeys.COMPANY_NO).toString().substring(0,8);
					String companyNo=context.getData(ParamKeys.COMPANY_NO).toString();
//					DZ+对账文件类型(2位)+收费方式（3位）+
//					费用类型（3位）_+银行代码(4位)+单位编码（8位）+日期（yyyymmdd）
					String fileName = "DZ05"+busType+payType+"_"+bankNo+companyNo+chkDte+".txt";
					eupsThdFtpConfig.setLocFleNme(fileName);
					logger.info("~~~~~~~~eupsThdFtpConfig~~~~~~~"+eupsThdFtpConfig);
					logger.info("CheckBkFileMbusCardStrategy fileName is :"+ fileName);
					
					//生成对账文件到指定路径
					operateFileAction.createCheckFile(eupsThdFtpConfig, "elecCheckFile", fileName, map);

					//向指定FTP路径放文件  上传
//		            operateFTPAction.putCheckFile(eupsThdFtpConfig);

		            context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);

		            logger.info("=============放置成功==========End   CheckThdDetlAcctAction");
			} catch (Exception e) {
	            context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
	            logger.info("File create error : " + e.getMessage());          
	            throw new CoreException(e.getMessage());
	        }  
			context.setData(GDParamKeys.CHECKTIME, DateUtils.formatAsHHmmss(txnTme));
			context.setData(GDParamKeys.SVRCOD, "51");
			callThd(context);
			
	}
	/**
	 * 生成对账文件
	 */
	public Map<String, Object> encodeFileMap(Context context) throws CoreException,CoreRuntimeException{
		logger.info("=======Start CheckThdDetlAcctAction encodeFileMap");
		Map<String, Object> map=new HashMap<String, Object>();
		//查找对应数据
		EupsStreamNo eupsStreamNos=new EupsStreamNo();
		
		eupsStreamNos.setEupsBusTyp(context.getData(ParamKeys.EUPS_BUSS_TYPE).toString());
		eupsStreamNos.setComNo(context.getData(ParamKeys.COMPANY_NO).toString());
		
		String busTyp=(String)context.getData(GDParamKeys.BUS_TYPE);
		String payType=context.getData(ParamKeys.PAY_TYPE).toString();
		
//		String bakFld2=busTyp+"&&"+payType;
//		eupsStreamNos.setBakFld2(bakFld2);
		String chkDte=DateUtils.format((Date)context.getData(ParamKeys.AC_DATE),DateUtils.STYLE_yyyyMMdd);
		eupsStreamNos.setTxnDte((Date)context.getData(ParamKeys.AC_DATE));
		eupsStreamNos.setTxnSts("S");
		
//		eupsStreamNos.setIsBatFlg("S");
		
		List<EupsStreamNo> eupsStreamNoList=eupsStreamNoRepository.find(eupsStreamNos);
		if(CollectionUtils.isEmpty(eupsStreamNoList)){
				context.setData(ParamKeys.MESSAGE_TYPE, "E");
				context.setData(ParamKeys.RSP_CDE,"EFE999");
				context.setData(ParamKeys.RSP_MSG, "统计明细记录总数出错");
				logger.info("~~~~~~~~~~~统计明细记录总数出错");
				throw new CoreException("~~~~~~~~~~~统计明细记录总数出错");
		}
		
		BigDecimal allmoney=new BigDecimal("0.00");
		List<CheckDetailAcct> list=new ArrayList<CheckDetailAcct>();
		
		for (EupsStreamNo eupsStreamNo : eupsStreamNoList) {
			
			BigDecimal txnAmt=eupsStreamNo.getTxnAmt();
			allmoney=allmoney.add(txnAmt);
			eupsStreamNo.setTxnAmt(allmoney);
			
			CheckDetailAcct checkDetailAcct =new CheckDetailAcct();
			//BK						
			checkDetailAcct.setBankNo(eupsStreamNo.getBk().substring(0,4));			
			checkDetailAcct.setComNo(eupsStreamNo.getComNo());
			checkDetailAcct.setCusAc(eupsStreamNo.getCusAc());
			
			checkDetailAcct.setCusNme(eupsStreamNo.getCusNme());
			checkDetailAcct.setBakFld1(eupsStreamNo.getBakFld1());
			String electricityYearMonth=chkDte.substring(0, 6);
			checkDetailAcct.setElectricityYearMonth(electricityYearMonth);
			checkDetailAcct.setPayNo(eupsStreamNo.getThdCusNo());
			checkDetailAcct.setSqn(context.getData(ParamKeys.SEQUENCE).toString());
			
			Date date=eupsStreamNo.getTxnTme();
			String txnDte=DateUtils.format(date, "yyyy-MM-dd HH:mm:ss");
			checkDetailAcct.setTxnDte(txnDte);
			checkDetailAcct.setTxnAmt(eupsStreamNo.getTxnAmt());
			checkDetailAcct.setTxnTlr(eupsStreamNo.getTxnTlr());
			checkDetailAcct.setThdCusNme(eupsStreamNo.getThdCusNme());
			//唯一标识
			checkDetailAcct.setOnlySignCode(context.getData(ParamKeys.SEQUENCE).toString());
			list.add(checkDetailAcct);
		}
		logger.info("~~~~~~~~~~~~list~~~~"+list);
		//header  部分
		Map<String, Object> headerMap=new HashMap<String, Object>();
		headerMap.put("SQN", context.getData(ParamKeys.SEQUENCE));
		headerMap.put("enterBankNo",context.getData(ParamKeys.BANK_NO));
		
        headerMap.put("comNo", context.getData(ParamKeys.COMPANY_NO));
        headerMap.put("busTyp", busTyp);
        headerMap.put("payType", payType);
        
        headerMap.put("amount", eupsStreamNoList.size());
        headerMap.put("allMoney", allmoney);
        String checkDate=DateUtils.format((Date)context.getData(ParamKeys.AC_DATE), DateUtils.STYLE_yyyyMMdd);
        
        headerMap.put("checkDate", checkDate);
        String checkTime=DateUtils.formatAsHHmmss((Date)context.getData(GDParamKeys.CHECKTIME));
        
        headerMap.put("checkTime",checkTime);
        
        map.put(ParamKeys.EUPS_FILE_HEADER, headerMap);
		map.put(ParamKeys.EUPS_FILE_DETAIL, BeanUtils.toMaps(list));
		logger.info("~~~~~map~~~~~~"+map);
		return map;
	}
	
	/**
	 *报文信息 外发第三方
	 */
	public void callThd(Context context){  
		logger.info("=======Start  CheckDetailAcctAction     callThd");
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
