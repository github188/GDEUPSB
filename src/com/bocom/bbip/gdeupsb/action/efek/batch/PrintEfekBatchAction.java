package com.bocom.bbip.gdeupsb.action.efek.batch;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JSpinner.DateEditor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.file.reporting.impl.VelocityTemplatedReportRender;
import com.bocom.bbip.file.transfer.ftp.FTPTransfer;
import com.bocom.bbip.gdeupsb.entity.GDEupsEleTmp;
import com.bocom.bbip.gdeupsb.repository.GDEupsEleTmpRepository;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
/**
 *电网 批次清单打印
 */
public class PrintEfekBatchAction extends BaseAction{
		@Autowired
		GDEupsEleTmpRepository gdEupsEleTmpRepository;
		@Override
		public void execute(Context context) throws CoreException,CoreRuntimeException {
				String mothed=context.getData("mothed").toString();
				Date txnDte=(Date)context.getData("printDate");
				
				Map<String, Object> maps=new HashMap<String, Object>();
				maps.put("txnDte", txnDte);
				List<Map<String, Object>> mapList=gdEupsEleTmpRepository.findAllGroupByComNo(maps);
				for (Map<String, Object> map : mapList) {
						String comNo=map.get("COM_NO").toString();
						context.setData("comNo", comNo);
						context.setData("totCnt", map.get("TOTCNT"));
						context.setData("totAmt", map.get("TOTAMT"));
						GDEupsEleTmp gdEupsEleTmp=new GDEupsEleTmp();
						gdEupsEleTmp.setTxnDte(txnDte);
						List<GDEupsEleTmp> list=new ArrayList<GDEupsEleTmp>();
						if(mothed.equals("0")){
								list=gdEupsEleTmpRepository.find(gdEupsEleTmp);
								context.setData("printType", "全部清单");
						}else if(mothed.equals("1")){
								gdEupsEleTmp.setPaymentResult("00");
								list=gdEupsEleTmpRepository.find(gdEupsEleTmp);
								context.setData("printType", "成功清单");
						}else{
								list=gdEupsEleTmpRepository.findFail(maps);
								context.setData("printType", "失败清单");
						}
						context.setData("txnDate", DateUtils.format(new Date(), DateUtils.STYLE_SIMPLE_DATE));
						//清单文件
						VelocityTemplatedReportRender render = new VelocityTemplatedReportRender();
						try {
							render.afterPropertiesSet();
						} catch (Exception e) {
							log.info("===========ErrMsg=",e);
						}
						//拼装文件
						Map<String, String> mapFile = new HashMap<String, String>();
						map.put("printEfekBatch", "config/report/common/printEfekBatch.vm");
						render.setReportNameTemplateLocationMapping(mapFile);
						context.setData("eles", list);
						String result = render.renderAsString("printEfekBatch", context);
						log.info("~~~~~~~~~~~~~~~~~~~~~"+result);
						
						String fileName=comNo+"_"+DateUtils.format(txnDte, DateUtils.STYLE_yyyyMMdd);
						StringBuffer batNoFile=new StringBuffer();
						batNoFile.append("/home/bbipadm/data/GDEUPSB/report/");
						File file =new File(batNoFile.toString());
						if(!file.exists()){
								file.mkdirs();
						}
						
							 
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
							
							FTPTransfer tFTPTransfer = new FTPTransfer();
					        tFTPTransfer.setHost("182.53.15.187");
							tFTPTransfer.setPort(21);
							tFTPTransfer.setUserName("weblogic");
							tFTPTransfer.setPassword("123456");
							 try {
							       	tFTPTransfer.logon();
							        Resource tResource = new FileSystemResource("/home/bbipadm/data/GDEUPSB/report/"+fileName);
							        tFTPTransfer.putResource(tResource, "/home/weblogic/JumpServer/WEB-INF/data/mftp_recv/", fileName);
							} catch (Exception e) {
							       	throw new CoreException("文件上传失败");
							} finally {
							       	tFTPTransfer.logout();
							}
							log.info("=============放置报表文件");
						}
		}
}
