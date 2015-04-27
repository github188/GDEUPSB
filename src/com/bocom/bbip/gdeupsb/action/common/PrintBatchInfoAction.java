package com.bocom.bbip.gdeupsb.action.common;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
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
import com.bocom.bbip.utils.BeanUtils;
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
				context.setData("batNo", batNo);
				context.setData("eupsBusTyp", gdEupsBatchConsoleInfo.getEupsBusTyp());
				String fleNme=gdEupsBatchConsoleInfo.getRsvFld8();
				String fileName=gdEupsBatchConsoleInfo.getRsvFld1();
				//eupsBatchConsoleInfo批次号
				EupsBatchConsoleInfo eupsBatchConsoleInfos=new EupsBatchConsoleInfo();
				eupsBatchConsoleInfos.setFleNme(fleNme);
				EupsBatchConsoleInfo eupsBatchConsoleInfo=eupsBatchConsoleInfoRepository.find(eupsBatchConsoleInfos).get(0);
				String eupsBatNo=eupsBatchConsoleInfo.getBatNo();
				//入库信息
				EupsBatchInfoDetail eupsBatchInfoDetail=new EupsBatchInfoDetail();
				eupsBatchInfoDetail.setBatNo(eupsBatNo);
				List<EupsBatchInfoDetail> list=eupsBatchInfoDetailRepository.find(eupsBatchInfoDetail);
				for (EupsBatchInfoDetail eupsBatchInfoDetails : list) {
						if(eupsBatchInfoDetails.getSts().equals("S")){
								eupsBatchInfoDetails.setSts("成功");
						}else{
								eupsBatchInfoDetails.setSts("失败");
						}
						context.setDataMap(BeanUtils.toMap(eupsBatchInfoDetails));
				}
				context.setData("exeDte", DateUtils.formatAsSimpleDate(gdEupsBatchConsoleInfo.getExeDte()));
				context.setData("comNo", eupsBatchConsoleInfo.getComNo());
				context.setData("comNme", eupsBatchConsoleInfo.getComNme());
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
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//拼装文件
				Map<String, String> map = new HashMap<String, String>();
				map.put("printBatch", "config/report/common/printBatch.vm");
				render.setReportNameTemplateLocationMapping(map);
				context.setData("eles", list);
				String result = render.renderAsString("printBatch", context);
				log.info("~~~~~~~~~~~~~~~~~~~~~"+result);
				
				//文件路径
				StringBuffer batNoFile=new StringBuffer();
				batNoFile.append("/home/bbipadm/data/GDEUPSB/report/");
				File file =new File(batNoFile.toString());
				if(!file.exists()){
						file.mkdirs();
				}
				try {
					FileOutputStream fileOutputStream = new FileOutputStream(batNoFile.append(fileName).toString());
					OutputStreamWriter outputStreamWriter=new OutputStreamWriter(fileOutputStream,"GBK");
					BufferedWriter bufferedWriter =new BufferedWriter(outputStreamWriter);
					PrintWriter printWriter = new PrintWriter(bufferedWriter);
					printWriter.write(result);
					//关闭
					printWriter.close();
					bufferedWriter.close();
					outputStreamWriter.close();
					fileOutputStream.close();
					
					FTPTransfer tFTPTransfer = new FTPTransfer();
			        tFTPTransfer.setHost("182.53.15.187");
					tFTPTransfer.setPort(21);
					tFTPTransfer.setUserName("weblogic");
					tFTPTransfer.setPassword("123456");
					tFTPTransfer.logon();
			        Resource tResource = new FileSystemResource("/home/bbipadm/data/GDEUPSB/report/"+fileName);
			        tFTPTransfer.putResource(tResource, "/home/weblogic/JumpServer/WEB-INF/data/mftp_recv/", fileName);
		           
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}finally{
					EupsThdFtpConfig eupsThdFtpConfig=eupsThdFtpConfigRepository.findOne("FSAG00");
					eupsThdFtpConfig.setFtpDir("0");
					eupsThdFtpConfig.setLocFleNme(fileName);
					eupsThdFtpConfig.setLocDir("/home/bbipadm/data/GDEUPSB/batch/"+fileName);
					eupsThdFtpConfig.setRmtFleNme(fileName);
					String path="/home/weblogic/JumpServer/WEB-INF/save/tfiles/" + context.getData(ParamKeys.BR)+ "/" + context.getData(ParamKeys.TELLER) + "/";
					eupsThdFtpConfig.setRmtWay(path);
					operateFTPAction.putCheckFile(eupsThdFtpConfig);
					context.setData("printResult", fileName);
				}
				
		}
}
