package com.bocom.bbip.gdeupsb.action.common;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.action.common.OperateFTPAction;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsBatchConsoleInfo;
import com.bocom.bbip.eups.entity.EupsBatchInfoDetail;
import com.bocom.bbip.eups.entity.EupsThdFtpConfig;
import com.bocom.bbip.eups.repository.EupsBatchConsoleInfoRepository;
import com.bocom.bbip.eups.repository.EupsBatchInfoDetailRepository;
import com.bocom.bbip.eups.repository.EupsThdFtpConfigRepository;
import com.bocom.bbip.file.reporting.impl.VelocityTemplatedReportRender;
import com.bocom.bbip.file.transfer.ftp.FTPTransfer;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GDEupsBatchConsoleInfo;
import com.bocom.bbip.gdeupsb.repository.GDEupsBatchConsoleInfoRepository;
import com.bocom.bbip.utils.CollectionUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
/**
 *录入批次清单打印 
 */
public class PrintBatchInfoAction extends BaseAction{
	@Autowired
	GDEupsBatchConsoleInfoRepository gdEupsBatchConsoleInfoRepository;
	@Autowired
	EupsBatchInfoDetailRepository eupsBatchInfoDetailRepository;
	@Autowired
	EupsBatchConsoleInfoRepository eupsBatchConsoleInfoRepository;
	@Autowired
	EupsThdFtpConfigRepository eupsThdFtpConfigRepository;
	@Autowired
	OperateFTPAction operateFTPAction;
		@Override
		public void execute(Context context) throws CoreException,
				CoreRuntimeException {
				String batNo=context.getData(ParamKeys.BAT_NO).toString();
				GDEupsBatchConsoleInfo gdEupsBatchConsoleInfo=gdEupsBatchConsoleInfoRepository.findOne(batNo);
				if(gdEupsBatchConsoleInfo==null){
						throw new CoreException("没有"+batNo+"批次信息");
				}	
				context.setData("eupsBusTyp", gdEupsBatchConsoleInfo.getEupsBusTyp());
				String fileName=gdEupsBatchConsoleInfo.getRsvFld1();
				context.setData("printResult", fileName);
				EupsBatchConsoleInfo eupsBatchConsoleInfos=new EupsBatchConsoleInfo();
				eupsBatchConsoleInfos.setRsvFld1(batNo);
				EupsBatchConsoleInfo eupsBatchConsoleInfo=eupsBatchConsoleInfoRepository.find(eupsBatchConsoleInfos).get(0);
				String eupsBatNo=eupsBatchConsoleInfo.getBatNo();
				//入库信息
				EupsBatchInfoDetail eupsBatchInfoDetail=new EupsBatchInfoDetail();
				eupsBatchInfoDetail.setBatNo(eupsBatNo);
				List<EupsBatchInfoDetail> list=eupsBatchInfoDetailRepository.find(eupsBatchInfoDetail);				
				if(CollectionUtils.isEmpty(list)){
						throw new CoreException(batNo+"没有返回，不能打印清单");
				}
				for (EupsBatchInfoDetail eupsBatchInfoDetails : list) {
						eupsBatchInfoDetails.setRmk2("T");
						if(eupsBatchInfoDetails.getSts().equals("S")){
								eupsBatchInfoDetails.setErrMsg("扣款成功");
						}
				}
				//获取日期 
				Date date=null;
				if(gdEupsBatchConsoleInfo.getExeDte()!=null){
						date=gdEupsBatchConsoleInfo.getExeDte();
				}else{
						date=new Date();
				}
				//清单首尾信息
				context.setData("exeDte", DateUtils.formatAsSimpleDate(date));
				context.setData("comNo", eupsBatchConsoleInfo.getComNo());
				context.setData(ParamKeys.TOT_CNT, eupsBatchConsoleInfo.getTotCnt());
				context.setData(ParamKeys.TOT_AMT, eupsBatchConsoleInfo.getTotAmt());
				context.setData(GDParamKeys.SUC_TOT_CNT, eupsBatchConsoleInfo.getSucTotCnt());
				context.setData(GDParamKeys.SUC_TOT_AMT, eupsBatchConsoleInfo.getSucTotAmt());
				context.setData(GDParamKeys.FAL_TOT_CNT, eupsBatchConsoleInfo.getFalTotCnt());
				context.setData("Tlr", eupsBatchConsoleInfo.getTxnTlr());
				context.setData("falTotAmt", eupsBatchConsoleInfo.getFalTotAmt());
				context.setData("txnDte",DateUtils.formatAsSimpleDate(new Date()));
				log.info("~~~~~~~~~~~"+context);
				//清单文件
				VelocityTemplatedReportRender render = new VelocityTemplatedReportRender();
				try {
					render.afterPropertiesSet();
				} catch (Exception e) {
					log.info("===========ErrMsg=",e);
				}
				//拼装文件
				Map<String, String> map = new HashMap<String, String>();
				map.put("printBatch", "config/report/common/printBatch.vm");
				if(context.getData("eupsBusTyp").toString().trim().equals("ZHAG00")){
					map.put("printBatch", "config/report/common/printBatchZHAG.vm");
				}
				render.setReportNameTemplateLocationMapping(map);
				context.setData("eles", list);
				String result = render.renderAsString("printBatch", context);
				log.info("~~~~~~~~~~~~~~~~~~~~~"+result);
				
				//生成文件路径
				EupsThdFtpConfig sendFileToBBOSConfig = get(EupsThdFtpConfigRepository.class).findOne("sendFileToBBOS");
				StringBuffer batNoFile=new StringBuffer();
				batNoFile.append(sendFileToBBOSConfig.getLocDir());
				File file =new File(batNoFile.toString());
				if(!file.exists()){
						file.mkdirs();
				}
				
				//生成文件
					try {
						FileOutputStream	fileOutputStream = new FileOutputStream(batNoFile.append(fileName).toString());
						OutputStreamWriter outputStreamWriter=new OutputStreamWriter(fileOutputStream,"GBK");
						BufferedWriter bufferedWriter =new BufferedWriter(outputStreamWriter);
						PrintWriter printWriter = new PrintWriter(bufferedWriter);
						printWriter.write(result);
						//关闭
						printWriter.close();
						bufferedWriter.close();
						outputStreamWriter.close();
						fileOutputStream.close();
					} catch (FileNotFoundException e) {
						log.info("===========ErrMsg=",e);
					} catch (IOException e) {
						log.info("===========ErrMsg=",e);
					}
					//报表		
					log.info("=============Start   Send   File==========");
					// FTP上传设置
					FTPTransfer tFTPTransfer = new FTPTransfer();
					tFTPTransfer.setHost(sendFileToBBOSConfig.getThdIpAdr());
					tFTPTransfer.setPort(Integer.parseInt(sendFileToBBOSConfig.getBidPot()));
					tFTPTransfer.setUserName(sendFileToBBOSConfig.getOppNme());
					tFTPTransfer.setPassword(sendFileToBBOSConfig.getOppUsrPsw());
					 try {
					       	tFTPTransfer.logon();
					        Resource tResource = new FileSystemResource(sendFileToBBOSConfig.getLocDir()+fileName);
					        tFTPTransfer.putResource(tResource, "/home/weblogic/JumpServer/WEB-INF/data/mftp_recv/", fileName);
					} catch (Exception e) {
					       	throw new CoreException("文件上传失败");
					} finally {
					       	tFTPTransfer.logout();
					}
					log.info("=============放置报表文件");
			        //反盘文件
					String path="/home/weblogic/JumpServer/WEB-INF/save/tfiles/" + context.getData(ParamKeys.BR)+ "/" ;
					 try {
					       	tFTPTransfer.logon();
					        Resource tResource = new FileSystemResource(sendFileToBBOSConfig.getLocDir()+fileName);
					        tFTPTransfer.putResource(tResource, path, fileName);
					 } catch (Exception e) {
					       	throw new CoreException("文件上传失败");
					 } finally {
					       	tFTPTransfer.logout();
					 }
					log.info("=============放置反盘文件");
				log.info("======================printResult"+context.getData("printResult"));
		}
}
