package com.bocom.bbip.gdeupsb.action.efek.batch;

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
import com.bocom.bbip.comp.BBIPPublicServiceImpl;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.action.common.CommThdRspCdeAction;
import com.bocom.bbip.eups.action.common.OperateFTPAction;
import com.bocom.bbip.eups.action.common.OperateFileAction;
import com.bocom.bbip.eups.adaptor.ThirdPartyAdaptor;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsBatchConsoleInfo;
import com.bocom.bbip.eups.entity.EupsBatchInfoDetail;
import com.bocom.bbip.eups.entity.EupsThdFtpConfig;
import com.bocom.bbip.eups.repository.EupsBatchConsoleInfoRepository;
import com.bocom.bbip.eups.repository.EupsBatchInfoDetailRepository;
import com.bocom.bbip.eups.repository.EupsThdFtpConfigRepository;
import com.bocom.bbip.eups.spi.service.batch.AfterBatchAcpService;
import com.bocom.bbip.eups.spi.vo.AfterBatchAcpDomain;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GDEupsBatchConsoleInfo;
import com.bocom.bbip.gdeupsb.entity.GDEupsEleTmp;
import com.bocom.bbip.gdeupsb.repository.GDEupsBatchConsoleInfoRepository;
import com.bocom.bbip.gdeupsb.repository.GDEupsEleTmpRepository;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.SystemConfig;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class BatchDataResultFileAction extends BaseAction implements AfterBatchAcpService{
	private final static Log logger=LogFactory.getLog(BatchDataResultFileAction.class);
	@Autowired
	OperateFileAction operateFile;
	@Autowired
	OperateFTPAction operateFTP;
	@Autowired
	GDEupsBatchConsoleInfoRepository gdeupsBatchConsoleInfoRepository;
	@Autowired
	GDEupsEleTmpRepository gdEupsEleTmpRepository;
	@Autowired
	EupsThdFtpConfigRepository eupsThdFtpConfigRepository;
	@Autowired
	EupsBatchInfoDetailRepository eupsBatchInfoDetailRepository;
	@Autowired
	EupsBatchConsoleInfoRepository eupsBatchConsoleInfoRepository;
	@Autowired
	OperateFTPAction operateFTPAction;
	@Autowired
	@Qualifier("callThdTradeManager")
	ThirdPartyAdaptor callThdTradeManager;
	/**
	 * 南方电网 结果文件处理
	 */
	public void afterBatchDeal(AfterBatchAcpDomain afterbatchacpdomain, Context context) throws CoreException {
			logger.info("===============Start  BatchDataResultFileAction  afterBatchDeal");	

			//第三方 rsvFld9
			String batNo=context.getData(ParamKeys.BAT_NO).toString();
			EupsBatchConsoleInfo eupsBatchConsoleInfo=eupsBatchConsoleInfoRepository.findOne(batNo);
			String sqns=eupsBatchConsoleInfo.getRsvFld2();
			GDEupsBatchConsoleInfo  Info=new GDEupsBatchConsoleInfo();
			Info.setRsvFld7(sqns);
			GDEupsBatchConsoleInfo  gdeupsBatchConsoleInfo = gdeupsBatchConsoleInfoRepository.find(Info).get(0);
			//更改控制表
			GDEupsBatchConsoleInfo gdEupsBatchConsoleInfoUpdate=updateInfo(context,gdeupsBatchConsoleInfo ,eupsBatchConsoleInfo);
			//文件名
			String fileName="PTFH"+gdEupsBatchConsoleInfoUpdate.getFleNme().substring(4);
			//取文件s路径
			String tlr=(String)context.getData(ParamKeys.TELLER);
			String br=(String)context.getData(ParamKeys.BR);
			context.setData(ParamKeys.BR, br);
	        String AcDate=DateUtils.format(((BBIPPublicServiceImpl)get(GDConstants.BBIP_PUBLIC_SERVICE)).getAcDate(),DateUtils.STYLE_yyyyMMdd);
	        String systemCode=((SystemConfig)get(SystemConfig.class)).getSystemCode();
	        String dir="/home/bbipadm/data/mftp/BBIP/"+systemCode+"/"+br+"/"+tlr+"/"+AcDate+"/";
	        						
	        EupsThdFtpConfig eupsThdFtpConfig=eupsThdFtpConfigRepository.findOne("elecBatch");		
			try{
					Map<String, Object> resultMap=createFileMap(context,gdEupsBatchConsoleInfoUpdate);
					eupsThdFtpConfig.setFtpDir("0");
					String name=context.getData(ParamKeys.BAT_NO)+".result";
					eupsThdFtpConfig.setLocFleNme(name);
					eupsThdFtpConfig.setRmtFleNme(name);			        
					eupsThdFtpConfig.setLocDir(dir);
					eupsThdFtpConfig.setRmtWay(dir);
					operateFile.createCheckFile(eupsThdFtpConfig, "efekBatchResult", fileName, resultMap);
			}catch(CoreException e){
					logger.info("~~~~~~~~~~~Error  Message",e);
			}
			// 将生成的文件上传至指定服务器
			eupsThdFtpConfig.setLocFleNme(fileName);
			eupsThdFtpConfig.setRmtFleNme(fileName);
			eupsThdFtpConfig.setRmtWay("/app/ics/dat/efek/send");
			eupsThdFtpConfig.setFtpDir("0");
			operateFTP.putCheckFile(eupsThdFtpConfig);
			
			try {
				RecvEnCryptFile(eupsThdFtpConfig.getLocDir(), fileName, fileName,context);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//TODO 通知第三方
			callThd(context,gdeupsBatchConsoleInfo,fileName);
			logger.info("===============End  BatchDataResultFileAction  afterBatchDeal");	
		}
	/**
	 * 把信息保存到控制表
	 */
	public GDEupsBatchConsoleInfo  updateInfo(Context context,GDEupsBatchConsoleInfo gdeupsBatchConsoleInfo,EupsBatchConsoleInfo eupsBatchConsoleInfo){
			logger.info("===============Start  BatchDataResultFileAction  updateInfo");	
			Integer totCnt=Integer.parseInt(eupsBatchConsoleInfo.getTotCnt().toString());
			//成功笔数
			Integer sucTotCnt=Integer.parseInt(context.getData(GDParamKeys.SUC_TOT_CNT).toString());
			gdeupsBatchConsoleInfo.setSucTotCnt(sucTotCnt);
			//失败笔数
			Integer falTotCnt=totCnt-sucTotCnt;
			gdeupsBatchConsoleInfo.setFalTotCnt(falTotCnt);
			
			BigDecimal totAmt=eupsBatchConsoleInfo.getTotAmt();
			//成功总金额
			BigDecimal sucTotAmt=new BigDecimal(context.getData("sucTotAmt").toString());
			gdeupsBatchConsoleInfo.setSucTotAmt(sucTotAmt);
			//失败总金额
			BigDecimal falTotAmt=totAmt.subtract(sucTotAmt);
			gdeupsBatchConsoleInfo.setFalTotAmt(falTotAmt);
			//更改状态
			gdeupsBatchConsoleInfo.setBatSts("S");
			//更新批次表
			gdeupsBatchConsoleInfoRepository.updateConsoleInfo(gdeupsBatchConsoleInfo);
			logger.info("===============End  BatchDataResultFileAction  updateInfo");	
			return gdeupsBatchConsoleInfo;
	}
	/**
	 * 拼装map文件
	 */
	public Map<String, Object> createFileMap(Context context,GDEupsBatchConsoleInfo gdEupsBatchConsoleInfoUpdate){
			logger.info("===============Start  BatchDataResultFileAction  createFileMap");	
			//代收付文件内容
			List<EupsBatchInfoDetail> mapList=context.getVariable("detailList");
			List<GDEupsEleTmp> gdEupsEleTmpList = gdEupsEleTmpRepository.findAllOrderBySqn();
			//内容主体
			List<GDEupsEleTmp> list=new ArrayList<GDEupsEleTmp>();
			for(int i=0;i<mapList.size();i++){
						GDEupsEleTmp gdEupsEleTmp=gdEupsEleTmpList.get(i);
						EupsBatchInfoDetail eupsBatchInfoDetail=mapList.get(i);
						//<!--客户账号|姓名|金额|代理服务客户标识|代理服务客户姓名|本行标志|开户银行|备注一|备注二|状态|描述  -->
						gdEupsEleTmp.setRsvFld5(eupsBatchInfoDetail.getTxnAmt().scaleByPowerOfTen(2).signum()+"");
						gdEupsEleTmp.setPaymentResult(eupsBatchInfoDetail.getSts());
						gdEupsEleTmp.setBankSqn(gdEupsEleTmp.getSqn());
						gdEupsEleTmp.setBankNo("301");
						//TODO 
						Date date=new Date();
						gdEupsEleTmp.setRsvFld1(DateUtils.format(date, DateUtils.STYLE_yyyyMMdd));
						gdEupsEleTmp.setRsvFld2(DateUtils.formatAsHHmmss(date));
						gdEupsEleTmp.setBakFld(eupsBatchInfoDetail.getRmk1());
						list.add(gdEupsEleTmp);
			}
			Map<String, Object> headMap=new HashMap<String, Object>();
			headMap.put("rsvFld5", gdEupsBatchConsoleInfoUpdate.getRsvFld5());
			headMap.put("comNo", gdEupsBatchConsoleInfoUpdate.getComNo());
			headMap.put("rsvFld13", "301");
			headMap.put("rsvFld12", "RMB");
			headMap.put("rsvFld3", gdEupsBatchConsoleInfoUpdate.getRsvFld3());
			headMap.put("rsvFld4", gdEupsBatchConsoleInfoUpdate.getRsvFld4());
			headMap.put("totCnt", gdEupsBatchConsoleInfoUpdate.getTotCnt());
			headMap.put("totAmt",gdEupsBatchConsoleInfoUpdate.getTotAmt().scaleByPowerOfTen(2).intValue());
			headMap.put("sucTotCnt", gdEupsBatchConsoleInfoUpdate.getSucTotCnt());
			headMap.put("sucTotAmt", gdEupsBatchConsoleInfoUpdate.getSucTotAmt().scaleByPowerOfTen(2).intValue());
			headMap.put("falTotCnt", gdEupsBatchConsoleInfoUpdate.getFalTotCnt());
			headMap.put("falTotAmt", gdEupsBatchConsoleInfoUpdate.getFalTotAmt().scaleByPowerOfTen(2).intValue());
			headMap.put("txnTlr", gdEupsBatchConsoleInfoUpdate.getTxnTlr());
			Map<String, Object> resultMap=new HashMap<String, Object>(); 
			resultMap.put(ParamKeys.EUPS_FILE_HEADER, headMap);
			resultMap.put(ParamKeys.EUPS_FILE_DETAIL, BeanUtils.toMaps(list));
			logger.info("===============End  BatchDataResultFileAction  createFileMap");	
			return resultMap;
	}
/**
 * 外发第三方
 */
	public void callThd(Context context,GDEupsBatchConsoleInfo gdeupsBatchConsoleInfo,String fileName){
		logger.info("========Start BatchDataResultFileAction callThd");	
		
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
			context.setData(GDParamKeys.SVRCOD, "21");
			String sqn= get(BBIPPublicService.class).getBBIPSequence();
			context.setData("sqns",sqn);
			context.setData("busIdentify", "YDLW01");
			
			String txnDte=DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMdd);
			String txnTme=DateUtils.formatAsHHmmss(new Date());
			context.setData("sqn",sqn);
			context.setData("txnDte",txnDte);
			context.setData("txnTme",txnTme);
			  context.setData("rsvFld2",gdeupsBatchConsoleInfo.getRsvFld2());
		       context.setData("comNo",gdeupsBatchConsoleInfo.getComNo());
		       context.setData("busType",gdeupsBatchConsoleInfo.getRsvFld6());
		       context.setData("payType",gdeupsBatchConsoleInfo.getRsvFld4());
		       context.setData("fileNme",fileName);
		       context.setData("fileType","02");
		       context.setData("totCnt" ,gdeupsBatchConsoleInfo.getTotCnt());
		       context.setData("totAmt" ,gdeupsBatchConsoleInfo.getTotAmt().scaleByPowerOfTen(2).intValue());
		       context.setData("sucTotCnt",gdeupsBatchConsoleInfo.getSucTotCnt());
		       context.setData("sucTotAmt",gdeupsBatchConsoleInfo.getSucTotAmt().scaleByPowerOfTen(2).intValue());
		       context.setData("falTotCnt",gdeupsBatchConsoleInfo.getFalTotCnt());
		       context.setData("falTotAmt",gdeupsBatchConsoleInfo.getFalTotAmt().scaleByPowerOfTen(2).intValue());
		       
					try{
						Map<String, Object> rspMap = callThdTradeManager.trade(context);
						
							if(BPState.isBPStateNormal(context)){
									if(null !=rspMap){
										 	context.setDataMap(rspMap);
							                context.setData(ParamKeys.THIRD_RETURN_MESSAGE, rspMap);
							               context.setData("qryCusInformation", rspMap.get("qryCusInformation"));
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
        logger.info("en-file success!");
        logger.info("================End BatchDataFileActiion  RecvEnCryptFile");
        
        //获取MD5
        logger.info("================Start Get  FileMD5");
        EupsThdFtpConfig eupsThdFtpConfig=eupsThdFtpConfigRepository.findOne("efekMD5");
        eupsThdFtpConfig.setLocDir("/home/bbipadm/data/GDEUPSB/efek/");
        eupsThdFtpConfig.setRmtWay("/app/ics/dat/efek/send");
        eupsThdFtpConfig.setLocFleNme(srcFile+".MD5");
        eupsThdFtpConfig.setRmtFleNme(srcFile+".MD5");
        operateFTP.getFileFromFtp(eupsThdFtpConfig);
        FileReader fileReader=new FileReader(eupsThdFtpConfig.getLocDir()+eupsThdFtpConfig.getLocFleNme());
        BufferedReader bufferedReader=new BufferedReader(fileReader);
        String firstLine=null;
        String rsvFld3="";
        while((firstLine=bufferedReader.readLine())!=null){
        		rsvFld3=firstLine;
        }
        if(StringUtils.isEmpty(rsvFld3)){
        		throw new CoreException("获取文件MD5失败");
        }
        context.setData("fleMD5", rsvFld3);
        logger.info("================End Get  FileMD5");
        return proc;
    }
	
}
