package com.bocom.bbip.gdeupsb.action.common;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.file.reporting.impl.VelocityTemplatedReportRender;
import com.bocom.bbip.gdeupsb.entity.GDEupsBatchConsoleInfo;
import com.bocom.bbip.gdeupsb.entity.GDEupsGzagBatchTmp;
import com.bocom.bbip.gdeupsb.repository.GDEupsBatchConsoleInfoRepository;
import com.bocom.bbip.gdeupsb.repository.GDEupsGzagBatchTmpRepository;
import com.bocom.bbip.utils.BeanUtils;
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
	GDEupsGzagBatchTmpRepository gdEupsGzagBatchTmpRepository;
		@Override
		public void execute(Context context) throws CoreException,
				CoreRuntimeException {
				String batNo=context.getData(ParamKeys.BAT_NO).toString();
				GDEupsBatchConsoleInfo gdEupsBatchConsoleInfo=gdEupsBatchConsoleInfoRepository.findOne(batNo);
				context.setData("batNo", batNo);
				context.setData("comNo", gdEupsBatchConsoleInfo.getComNo());
				context.setData("comNme", gdEupsBatchConsoleInfo.getComNme());
				context.setData("eupsBusTyp", "广州文本");
				//入库信息
				GDEupsGzagBatchTmp gdEupsGzagBatchTmps=new GDEupsGzagBatchTmp();
				gdEupsGzagBatchTmps.setRsvFld1(batNo);
				List<GDEupsGzagBatchTmp> list=gdEupsGzagBatchTmpRepository.find(gdEupsGzagBatchTmps);
				for (GDEupsGzagBatchTmp gdEupsGzagBatchTmp : list) {
						context.setDataMap(BeanUtils.toMap(gdEupsGzagBatchTmp));
						
				}
				context.setData("txnTlr", gdEupsBatchConsoleInfo.getTxnTlr());
				context.setData("exeDte",gdEupsBatchConsoleInfo.getExeDte());
				context.setData("totCnt", gdEupsBatchConsoleInfo.getTotCnt());
				context.setData("totAmt", gdEupsBatchConsoleInfo.getTotAmt());
				
				log.info("~~~~~~~~~~~"+context);
				//清单文件
				VelocityTemplatedReportRender render = new VelocityTemplatedReportRender();
				try {
					render.afterPropertiesSet();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println();
				System.out.println(1);
				//拼装文件
				Map<String, String> map = new HashMap<String, String>();
				System.out.println();
				System.out.println(11);
				map.put("printBatchInfo", "config/report/common/printBatchInfo.vm");
				System.out.println();
				System.out.println(111);
				render.setReportNameTemplateLocationMapping(map);
				System.out.println();
				System.out.println(1111);
				context.setData("eles", list);
				System.out.println();
				System.out.println(11111);
				String result = render.renderAsString("printBatchInfo", context);
				System.out.println();
				System.out.println(111111);
				log.info("~~~~~~~~~~~~~~~~~~~~~"+result);
				
				//文件路径
				StringBuffer batNoFile=new StringBuffer();
				batNoFile.append("E:\\home\\bbipadm\\common\\");
				File file =new File(batNoFile.toString());
				if(!file.exists()){
						file.mkdirs();
				}
				String batNoName=batNo+".txt";
				try {
					FileOutputStream fileOutputStream = new FileOutputStream(batNoFile.append(batNoName).toString());
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
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
		}
}
