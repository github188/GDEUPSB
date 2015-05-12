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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.entity.EupsThdFtpConfig;
import com.bocom.bbip.eups.repository.EupsThdFtpConfigRepository;
import com.bocom.bbip.file.MftpTransfer;
import com.bocom.bbip.file.reporting.impl.VelocityTemplatedReportRender;
import com.bocom.bbip.file.transfer.ftp.FTPTransfer;
import com.bocom.bbip.gdeupsb.entity.GDEupsEleTmp;
import com.bocom.bbip.gdeupsb.repository.GDEupsEleTmpRepository;
import com.bocom.bbip.utils.CollectionUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
/**
 *电网 批次清单打印
 */
public class PrintEfekBatchAction extends BaseAction{
		private static Log logger=LogFactory.getLog(PrintEfekBatchAction.class);
		@Autowired
		GDEupsEleTmpRepository gdEupsEleTmpRepository;
		@Autowired
		BBIPPublicService bbipPublicService;
		@Override
		public void execute(Context context) throws CoreException,CoreRuntimeException {
				logger.info("====================Start    PrintEfekBatchAction");
				List<Map<String, Object>> fileNameList=new ArrayList<Map<String,Object>>();
				String mothed=context.getData("mothed").toString();
				Date txnDte=(Date)context.getData("printDate");
				
				Map<String, Object> maps=new HashMap<String, Object>();
				maps.put("txnDte", txnDte);
				//地区分组
				List<Map<String, Object>> mapList=gdEupsEleTmpRepository.findAllGroupByComNo(maps);
				if(CollectionUtils.isEmpty(mapList)){
						throw new CoreException(txnDte+"没有进行批量交易");
				}
				for (Map<String, Object> map : mapList) {
						String comNo=map.get("COM_NO").toString();
						context.setData("comNo", comNo);
						maps.put("comNo", comNo);
						//全部清单   该地区总笔数 总金额
						context.setData("totCnt", map.get("TOTCNT"));
						context.setData("totAmt", map.get("TOTAMT"));
						
						GDEupsEleTmp gdEupsEleTmp=new GDEupsEleTmp();
						gdEupsEleTmp.setTxnDte(txnDte);
						gdEupsEleTmp.setComNo(comNo);
						List<GDEupsEleTmp> list=new ArrayList<GDEupsEleTmp>();
						Map<String, Object> mapTot=new HashMap<String, Object>();
						if(mothed.equals("0")){
								list=gdEupsEleTmpRepository.findByComNo(gdEupsEleTmp);
								if(CollectionUtils.isEmpty(list)){
										throw new CoreException(txnDte+"没有进行批量交易");
								}
								context.setData("printType", "全部清单");
						}else if(mothed.equals("1")){
								gdEupsEleTmp.setPaymentResult("00");
								list=gdEupsEleTmpRepository.findByComNo(gdEupsEleTmp);
								if(CollectionUtils.isEmpty(list)){
									throw new CoreException(txnDte+"没有成功批量交易");
								}
								mapTot=gdEupsEleTmpRepository.findByComNoSucTot(maps).get(0);
								//成功清单   该地区总笔数 总金额
								context.setData("totCnt", mapTot.get("COMNOTOTCNT"));
								context.setData("totAmt", mapTot.get("COMNOTOTAMT"));
								context.setData("printType", "成功清单");
						}else{
								list=gdEupsEleTmpRepository.findFail(maps);
								if(CollectionUtils.isEmpty(list)){
									throw new CoreException(txnDte+"没有失败批量交易");
								}
								mapTot=gdEupsEleTmpRepository.findByComNoFailTot(maps).get(0);
								//失败清单   该地区总笔数 总金额
								context.setData("totCnt", mapTot.get("COMNOTOTCNT"));
								context.setData("totAmt", mapTot.get("COMNOTOTAMT"));
								context.setData("printType", "失败清单");
						}

						context.setData("txnDate", DateUtils.format(new Date(), DateUtils.STYLE_SIMPLE_DATE));
						if(CollectionUtils.isNotEmpty(list)){
								//清单文件
								VelocityTemplatedReportRender render = new VelocityTemplatedReportRender();
								try {
									render.afterPropertiesSet();
								} catch (Exception e) {
									log.info("===========ErrMsg=",e);
								}
								//拼装文件
								Map<String, String> mapFile = new HashMap<String, String>();
								mapFile.put("printEfekBatch", "config/report/common/printEfekBatch.vm");
								render.setReportNameTemplateLocationMapping(mapFile);
								context.setData("eles", list);
								String result = render.renderAsString("printEfekBatch", context);
								log.info("~~~~~~~~~~~~~~~~~~~~~"+result);
								
								//文件名
								String fileName=comNo+"_"+DateUtils.format(txnDte, DateUtils.STYLE_yyyyMMdd)+".txt";
								Map<String, Object> fileNameMap=new HashMap<String, Object>();
								fileNameMap.put("printFile", fileName);
								fileNameList.add(fileNameMap);
															
								EupsThdFtpConfig sendFileToBBOSConfig = get(EupsThdFtpConfigRepository.class).findOne("sendFileToBBOS");
								StringBuffer batNoFile=new StringBuffer();
								batNoFile.append(sendFileToBBOSConfig.getLocDir());
								File file =new File(batNoFile.toString());
								if(!file.exists()){
										file.mkdirs();
								}
								
									 //生成报表文件
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
									
									 try {
										 	bbipPublicService.sendFileToBBOS(new File(sendFileToBBOSConfig.getRmtWay(),fileName), fileName, MftpTransfer.FTYPE_NORMAL);		
									} catch (Exception e) {
									       	throw new CoreException("文件上传失败");
									}
									log.info("=============放置报表文件");
							}
					}
				context.setData("fileNameList", fileNameList);
				logger.info("====================End     PrintEfekBatchAction");
		}
}
