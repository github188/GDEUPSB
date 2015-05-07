package com.bocom.bbip.gdeupsb.action.efek;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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
import com.bocom.bbip.eups.repository.EupsCusAgentJournalRepository;
import com.bocom.bbip.eups.repository.EupsThdFtpConfigRepository;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GDEupsCusAgentJournal;
import com.bocom.bbip.gdeupsb.repository.GDEupsCusAgentJournalRepository;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class AgentFileToThdAction extends BaseAction{
	@Autowired
	@Qualifier("callThdTradeManager")
	ThirdPartyAdaptor callThdTradeManager;
	@Autowired
	GDEupsCusAgentJournalRepository gdEupsCusAgentJournalRepository;
	@Autowired
	EupsCusAgentJournalRepository eupsCusAgentJournalRepository;
	@Autowired
	OperateFileAction operateFileAction;
	@Autowired
	OperateFTPAction operateFTPAction;
	@Autowired
	EupsThdFtpConfigRepository eupsThdFtpConfigRepository;
		@Override
		public void execute(Context context) throws CoreException,
				CoreRuntimeException {
				log.info("==============Start  AgentFileToThdAction");
				context.setData(ParamKeys.EUPS_BUSS_TYPE, "ELEC00");
				context.setData("TransCode", "31");
				Date date=new Date();
				//日期
				Date txnDate=DateUtils.calDate(DateUtils.parse(DateUtils.formatAsSimpleDate(date)),-1);
				Map<String, Object> map=new HashMap<String, Object>();
				txnDate=DateUtils.parse("2015-05-07");
				map.put("txnDte", txnDate);
				//分组
				List<Map<String, Object>> comNoList=gdEupsCusAgentJournalRepository.findAllGroupByComNo(map);
				int i=0;
				for(Map<String, Object> mapList:comNoList){
						i++;
						String comNo=mapList.get("COM_NO").toString().trim();
//						String comNo="";
//						if(comNos.length()>6){
//								comNo=comNos.substring(0,6);
//						}else{
//								while(comNos.length()<6){
//									comNo=comNos;
//									comNos=comNos+"0";
//								}
//						}
						//得到今日协议变更
						GDEupsCusAgentJournal eupsCusAgentJournal=new GDEupsCusAgentJournal();
						eupsCusAgentJournal.setComNo(comNo);
						eupsCusAgentJournal.setTxnDte(txnDate);
						eupsCusAgentJournal.setEupsBusTyp("ELEC00");
						List<GDEupsCusAgentJournal> list=gdEupsCusAgentJournalRepository.findBySubComNo(eupsCusAgentJournal);
						
						for (GDEupsCusAgentJournal eupsCusAgentJournals : list) {
							eupsCusAgentJournals.setIdNo(eupsCusAgentJournals.getIdNo().trim());
							if(eupsCusAgentJournals.getTel()!=null){
									eupsCusAgentJournals.setTel(eupsCusAgentJournals.getTel().trim());
							}
							System.out.println(eupsCusAgentJournals.getCusNme());
						}
						//首行
						Map<String, Object> headerMap=new HashMap<String, Object>();
						headerMap.put("comNo", comNo);
						headerMap.put("bankNo", "0301");
						headerMap.put("count", list.size());
						headerMap.put("totCnt", list.size());
						Map<String, Object> resultMap=new HashMap<String, Object>();
						resultMap.put("header", headerMap);
						resultMap.put("detail", BeanUtils.toMaps(list));
						//生成文件
						String string=i+"";
						while(string.length()<3){
								string="0"+string;
						}
						String locName="HDXY0301"+comNo+DateUtils.format(txnDate, DateUtils.STYLE_yyyyMMdd)+string+".txt";
						context.setData("fleNme", locName);
						context.setData("fleTyp", "04");
						EupsThdFtpConfig eupsThdFtpConfig=eupsThdFtpConfigRepository.findOne("efekAgent");
						eupsThdFtpConfig.setLocFleNme(locName);
						operateFileAction.createCheckFile(eupsThdFtpConfig, "efekAgent", locName, resultMap);	
						eupsThdFtpConfig.setFtpDir("0"); 
						eupsThdFtpConfig.setRmtFleNme(locName);
						eupsThdFtpConfig.setRmtWay("/app/ics/dat/efek/send");
						operateFTPAction.putCheckFile(eupsThdFtpConfig);
						//sftp
						try {
							RecvEnCryptFile(eupsThdFtpConfig.getLocDir(), locName, locName,context);
						} catch (IOException e) {
							log.info("===========ErrMsg=",e);
						} catch (InterruptedException e) {
							log.info("===========ErrMsg=",e);
						}
						callThd(context);
				}
				log.info("==============End   AgentFileToThdAction");
		}
		
		/**
		 *报文信息  外发第三方
		 * @throws CoreException 
		 */
		public void callThd(Context context) throws CoreException{  
			log.info("========Start QryCusMsgAction callThd");	
			
			context.setData(GDParamKeys.TREATY_VERSION, GDConstants.TREATY_VERSION);//协议版本
			context.setData(GDParamKeys.TRADE_PERSON_IDENTIFY, GDConstants.TRADE_PERSON_IDENTIFY);//交易人标识
			context.setData(GDParamKeys.BAG_TYPE, GDConstants.BAG_TYPE);//数据包类型
			context.setData(GDParamKeys.TRADE_START,GDConstants.TRADE_START);//交易发起方
				
				context.setData(GDParamKeys.TRADE_SEND_DATE,DateUtils.format(new Date(),DateUtils.STYLE_yyyyMMdd));//交易发送日期
				context.setData(GDParamKeys.TRADE_SEND_TIME, DateUtils.formatAsHHmmss(new Date()));//交易发送时间
				context.setData(GDParamKeys.TRADE_PRIORITY, GDConstants.TRADE_PRIORITY);//交易优先
				context.setData(GDParamKeys.REDUCE_SIGN, GDConstants.REDUCE_SIGN);//压缩标志
				context.setData(GDParamKeys.TRADE_RETURN_CODE, GDConstants.TRADE_RETURN_CODE);//交易返回代码
				String sqn=get(BBIPPublicService.class).getBBIPSequence();
				context.setData("sqns", sqn);
				context.setData("sqn", sqn);
				
				context.setData(GDParamKeys.NET_NAME, GDConstants.NET_NAME);//网点名称
				context.setData(GDParamKeys.SECRETKEY_INDEX, GDConstants.SECRETKEY_INDEX);//密钥索引
				context.setData(GDParamKeys.SECRETKEY_INIT, GDConstants.SECRETKEY_INIT);//密钥初始向量
				context.setData(GDParamKeys.TRADE_RECEIVE, GDConstants.TRADE_RECEIVE);//交易接收方
				context.setData(GDParamKeys.TRADE_SOURCE_ADD, GDConstants.TRADE_SOURCE_ADD);//交易源地址
				context.setData(GDParamKeys.TRADE_AIM_ADD, GDConstants.TRADE_AIM_ADD);//交易目标地址
				context.setData("PKGCNT", "000001");	
				context.setData("fleTyp", "04");	
				context.setData(GDParamKeys.BUS_IDENTIFY, "YDLW08");				
							Map<String, Object> rspMap = callThdTradeManager.trade(context);
							
								if(BPState.isBPStateNormal(context)){
										if(null !=rspMap){
											 	context.setDataMap(rspMap);
								                context.setData(ParamKeys.THIRD_RETURN_MESSAGE, rspMap);
								               context.setData("qryCusInformation", rspMap.get("qryCusInformation"));
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
						
			}

		 public  Process RecvEnCryptFile(String excPath, String srcFile, String objFile,Context context) throws IOException, InterruptedException, CoreRuntimeException, CoreException {
		    	log.info("================Start BatchDataFileActiion  RecvEnCryptFile");	    	
		        String cmd="ssh icsadm@182.53.15.200 /app/ics/app/efek/bin/EfeFilSend.sh 182.53.201.45 bcm exchange dat/efek/send "+srcFile+" "+DateUtils.formatAsHHmmss(new Date());
		        log.info("cmd=" + cmd);
		        Process proc = Runtime.getRuntime().exec(cmd);
		        proc.waitFor();

		        log.info("en-file success!");
		        log.info("================End BatchDataFileActiion  RecvEnCryptFile");
		        
		        //获取MD5
		        log.info("================Start Get  FileMD5");
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
		        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>fleMD5="+rsvFld3);
		        if(StringUtils.isEmpty(rsvFld3)){
		        		throw new CoreException("获取文件MD5失败");
		        }
		        context.setData("fleMD5", rsvFld3);
		        log.info("================End Get  FileMD5");
		        return proc;
		    }
}
