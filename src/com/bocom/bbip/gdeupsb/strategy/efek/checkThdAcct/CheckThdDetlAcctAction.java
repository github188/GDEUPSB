package com.bocom.bbip.gdeupsb.strategy.efek.checkThdAcct;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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
 * @author liyawei
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
			
	        //TODO 
	        context.setData(ParamKeys.TXN_TLR, "ABIR148");
	        context.setData(ParamKeys.CHL_TYP, "90");
	        
			//日期
			Date txnDte=DateUtils.calDate(DateUtils.parse(DateUtils.formatAsSimpleDate(new Date())),-1);
			txnDte=DateUtils.parse("2015-04-22");
			context.setData(ParamKeys.TXN_DTE, txnDte);
			//一些常量
			context.setData(GDParamKeys.TOTNUM, "1");
			context.setData(ParamKeys.BANK_NO, "301");
			context.setData(ParamKeys.TELLER_ID, "301_030600");  //收款人代码
			context.setData("allCheckNumber", "1");
			//对账类型  明细
			context.setData("checkTyp", "03");
			
			Map<String, Object> listMap=new HashMap<String, Object>();
			listMap.put("txnDte", txnDte);
			
			List<Map<String, Object>> mapList=eupsStreamNoRepository.findMsgToChkTot(listMap);
			if(CollectionUtils.isEmpty(mapList)){
						context.setData(ParamKeys.MESSAGE_TYPE, "E");
						context.setData(ParamKeys.RSP_CDE,"EFE999");
						context.setData(ParamKeys.RSP_MSG, "统计明细记录总数出错");
						logger.info("~~~~~~~~~~~统计明细记录总数出错");
						throw new CoreException("统计明细记录总数出错");
			}
			List<Map<String, Object>>  detailList=new ArrayList<Map<String,Object>>();
			int i=0;
			for(Map<String, Object> maps:mapList){
					i++;
					//流水
					String sqn =bbipPublicService.getBBIPSequence();
					context.setData(ParamKeys.SEQUENCE, sqn);
					//对账时间
					Date txnTme=DateUtils.parse(DateUtils.formatAsTranstime(new Date()));
					context.setData(ParamKeys.TXN_TME, txnTme);
					
					//对账唯一标识码
					context.setData(ParamKeys.COMPANY_NO, maps.get("COM_NO").toString());
					String checkOneCode=maps.get("COM_NO").toString().substring(0,6)+DateUtils.format(txnDte, DateUtils.STYLE_yyyyMMdd)+maps.get("RSV_FLD4".toString());
					context.setData("checkOneCode", checkOneCode);
					
					//初始化对账控制类型
					context.setData(ParamKeys.TXN_CTL_TYP, Constants.TXN_CTL_TYP_CHKBANKFILE_THD);  
					try{
							//生成对账文件
							Map<String, Object> map= encodeFileMap(context,maps);
							//获取FTP信息
		//					String ftpId=context.getData()
							String ftpId="elecCheckFile";
							EupsThdFtpConfig eupsThdFtpConfig = eupsThdFtpConfigRepository.findOne(ftpId);
							
							//设置本地对账文件名称
		//					对账文件名称
							String	comNo  =(String)maps.get("COM_NO");
							String  busType=(String)maps.get("RSV_FLD4");
							String  payType=(String)maps.get("RSV_FLD5");
							String xuhao=i+"";
							while(xuhao.length()<3){
								xuhao="0"+xuhao;
							}
							String bankNo=context.getData(ParamKeys.BANK_NO).toString();
							String fileName = "DZ"+busType+payType+"_"+bankNo+comNo+DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMdd)+xuhao+".txt";

							context.setData("fileName", fileName);
							context.setData("batNo", sqn);
							logger.info("~~~~~~~~eupsThdFtpConfig~~~~~~~"+eupsThdFtpConfig);
							logger.info("CheckBkFileMbusCardStrategy fileName is :"+ fileName);
							
							//生成对账文件到指定路径
							eupsThdFtpConfig.setLocFleNme(fileName);
							//TODO  sftp
							operateFileAction.createCheckFile(eupsThdFtpConfig, "elecCheckFile", fileName, map);
							 logger.info("=============对账文件生成成功==========");
							 
							//向指定FTP路径放文件  上传
				            operateFTPAction.putCheckFile(eupsThdFtpConfig);
				            
				            RecvEnCryptFile(eupsThdFtpConfig.getLocDir(), fileName, fileName,context);
				            logger.info("=============对账文件上传成功==========");	
				            
				            context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
							
							Map<String, Object> mapCallThd=new HashMap<String, Object>();
							mapCallThd.put("xH", i);
							mapCallThd.put("fileName", fileName);
							mapCallThd.put("fleMD5", context.getData("fleMD5"));
							detailList.add(mapCallThd);
					} catch (Exception e) {
			            context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
			            logger.info("File create error : " + e.getMessage());          
			            throw new CoreException(e.getMessage());
			        }  
					context.setData(GDParamKeys.CHECKDATE, DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMdd));
					context.setData(GDParamKeys.CHECKTIME, DateUtils.formatAsHHmmss(txnTme));
					context.setData(GDParamKeys.SVRCOD, "51");
			}
			String number=i+"";
			while(number.length()<6){
				number="0"+number;
			}
			context.setData("number", number);
			context.setData("detailList", detailList);
			callThd(context);
			logger.info("====================End   CheckThdDetlAcctAction");
	}
	/**
	 * 生成对账文件
	 */
	public Map<String, Object> encodeFileMap(Context context,Map<String, Object> maps) throws CoreException,CoreRuntimeException{
		logger.info("=======Start CheckThdDetlAcctAction encodeFileMap");
		String	comNo  =(String)maps.get("COM_NO");
		String  busType=(String)maps.get("RSV_FLD4");
		String  payType=(String)maps.get("RSV_FLD5");
		
		context.setData("busType", busType);
		context.setData("payType", payType);
		//总笔数 总金额
		long acount=0;
		if(null != maps.get("TOT_COUNT")){
				acount=Long.parseLong(maps.get("TOT_COUNT").toString());
		}
		BigDecimal allMoney=new BigDecimal("0.00");
		if(null != maps.get("ALL_MONEY")){			
			allMoney=allMoney.add(new BigDecimal(maps.get("ALL_MONEY").toString()));
		}
		allMoney=allMoney.scaleByPowerOfTen(2);
		//header  部分
		Map<String, Object> headerMap=new HashMap<String, Object>();
		headerMap.put(ParamKeys.SEQUENCE, context.getData(ParamKeys.SEQUENCE));
		headerMap.put(ParamKeys.BANK_NO,context.getData(ParamKeys.BANK_NO));
        headerMap.put(ParamKeys.COMPANY_NO, comNo.substring(0, 6));
        headerMap.put(GDParamKeys.BUS_TYPE, busType);
        headerMap.put(GDParamKeys.PAY_TYPE, payType);
        headerMap.put(GDParamKeys.TOT_COUNT, acount);
        headerMap.put(GDParamKeys.ALL_MONEY, allMoney);
        headerMap.put(GDParamKeys.CHECKDATE, DateUtils.format((Date)context.getData(ParamKeys.TXN_DTE),DateUtils.STYLE_yyyyMMdd));
        headerMap.put(GDParamKeys.CHECKTIME,DateUtils.formatAsHHmmss((Date)context.getData(ParamKeys.TXN_TME)));
		
		//查找对应数据
		EupsStreamNo eupsStreamNos=new EupsStreamNo();
		eupsStreamNos.setEupsBusTyp(context.getData(ParamKeys.EUPS_BUSS_TYPE).toString());
		eupsStreamNos.setComNo(comNo);
		eupsStreamNos.setTxnDte((Date)context.getData(ParamKeys.TXN_DTE));
		eupsStreamNos.setTxnSts("S");
		eupsStreamNos.setThdTxnSts("S");
		eupsStreamNos.setRsvFld4(busType);
		eupsStreamNos.setRsvFld5(payType);
		List<EupsStreamNo> eupsStreamNoList=eupsStreamNoRepository.find(eupsStreamNos);
		List<CheckDetailAcct> list=new ArrayList<CheckDetailAcct>();
		//内容
		for (EupsStreamNo eupsStreamNo : eupsStreamNoList) {
			CheckDetailAcct checkDetailAcct =new CheckDetailAcct();
			checkDetailAcct.setSqn(eupsStreamNo.getSqn());
			checkDetailAcct.setComNo(comNo);
			checkDetailAcct.setElectricityYearMonth(eupsStreamNo.getRsvFld6());
			checkDetailAcct.setPayNo(eupsStreamNo.getThdCusNo());
			//结算户名称
			checkDetailAcct.setThdCusNme(eupsStreamNo.getThdCusNme());
			checkDetailAcct.setCusAc(eupsStreamNo.getCusAc());
			checkDetailAcct.setCusNme(eupsStreamNo.getCusNme());
			checkDetailAcct.setTxnDte(DateUtils.format(eupsStreamNo.getTxnTme(),DateUtils.STYLE_FULL));
			BigDecimal txnAmt=eupsStreamNo.getTxnAmt().scaleByPowerOfTen(2);
			checkDetailAcct.setBankNo(eupsStreamNo.getThdObkCde());
			checkDetailAcct.setTxnAmt(txnAmt);
			String str="";
				if("010".equals(busType)){
					str="010   供电柜台现金";
				}else if("020".equals(busType)){
					str="020   供电支票";
				}else if("030".equals(busType)){
					str="030   供电汇票";
				}else if("040".equals(busType)){
					str="040   供电POS机";
				}else if("050".equals(busType)){
					str="050   供电第三方支付";
				}else if("060".equals(busType)){
					str="060   供电发起单笔实扣";
				}else if("070".equals(busType)){
					str="070   供电发起批量代扣";
				}else if("110".equals(busType)){
					str="110   银行单笔代收";
				}else if("120".equals(busType)){
					str="120   银行支票";
				}else if("130".equals(busType)){
					str="130   银行发起托收";
				}else if("140".equals(busType)){
					str="140   银行发起代扣";
				}else{
					str="费用类型 Error";
				}
			checkDetailAcct.setBakFld1(str);
			checkDetailAcct.setRsvFld1(context.getData("checkOneCode").toString());
			checkDetailAcct.setTxnTlr(eupsStreamNo.getTxnTlr());
			list.add(checkDetailAcct);
		}
		logger.info("~~~~~~~~~~~~list~~~~"+list);
        
		Map<String, Object> map=new HashMap<String, Object>();
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
				context.setData("PKGCNT", "000001");
				context.setData(GDParamKeys.BUS_IDENTIFY, "YDLW18");
				context.setData("sqns",context.getData(ParamKeys.SEQUENCE));
				context.setData("WJS", "1");
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
				}catch(CoreException e){
					logger.info("Bypass call THIRD response failed or unknow error.");
					context.setData(ParamKeys.TXN_STS, Constants.TXNSTS_REVERSE);
					context.setData(ParamKeys.THD_TXN_STS, Constants.TXNSTS_FAIL);
					context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
				}				
	}
    public  Process RecvEnCryptFile(String excPath, String srcFile, String objFile,Context context) throws IOException, InterruptedException, CoreRuntimeException, CoreException {
    	logger.info("================Start BatchDataFileActiion  RecvEnCryptFile");	    	
        String cmd="ssh icsadm@182.53.15.200 /app/ics/app/efek/bin/EfeFilSend.sh 182.53.201.46 bcm exchange dat/efek/send "+srcFile+" "+DateUtils.formatAsHHmmss(new Date());
        logger.info("cmd=" + cmd);
        Process proc = Runtime.getRuntime().exec(cmd);
        proc.waitFor();
        logger.info("en-file success!");
        logger.info("================End BatchDataFileActiion  RecvEnCryptFile");
        
        //获取MD5
        logger.info("================Start Get  FileMD5");
        EupsThdFtpConfig eupsThdFtpConfig=eupsThdFtpConfigRepository.findOne("efekMD5");
        eupsThdFtpConfig.setLocDir("/home/bbipadm/data/GDEUPSB/efek/");
        eupsThdFtpConfig.setRmtWay("/app/ics/dat/efek/send");
        eupsThdFtpConfig.setLocFleNme(srcFile+".MD5");
        eupsThdFtpConfig.setRmtFleNme(srcFile+".MD5");
        operateFTPAction.getFileFromFtp(eupsThdFtpConfig);
        FileReader fileReader=new FileReader(eupsThdFtpConfig.getLocDir()+eupsThdFtpConfig.getLocFleNme());
        BufferedReader bufferedReader=new BufferedReader(fileReader);
        String firstLine=null;
        String rsvFld3="";
        while((firstLine=bufferedReader.readLine())!=null){
        		rsvFld3=firstLine;
        }
        logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>fleMD5="+rsvFld3);
        if(StringUtils.isEmpty(rsvFld3)){
        		throw new CoreException("获取文件MD5失败");
        }
        context.setData("fleMD5", rsvFld3);
        logger.info("================End Get  FileMD5");
        return proc;
    }
	
}
