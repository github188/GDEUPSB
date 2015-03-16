package com.bocom.bbip.gdeupsb.action.tbc;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;



import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsTransJournal;
import com.bocom.bbip.eups.repository.EupsTransJournalRepository;
import com.bocom.bbip.file.reporting.impl.VelocityTemplatedReportRender;
import com.bocom.bbip.utils.CollectionUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

/**
 * 广东烟草清算报表打印
 * Date 2015-03-10
 * @author Guilin.Li
 *
 */
public class PrintClearAcAction extends BaseAction {
	@Autowired
	VelocityTemplatedReportRender batchReport;
	@Autowired
	EupsTransJournalRepository eupstransjournalRepository;
	public void execute(Context context) throws CoreException,
			CoreRuntimeException {
		log.info("--------begin----------");
		String tlr = context.getData(ParamKeys.TELLER);
		String br =  context.getData(ParamKeys.BR);
		String prtFlg = context.getData("prtFlg");
		String txndat = context.getData("txnDat");
		log.info("-------------------------- request date：" + txndat);
		txndat = txndat.replaceAll("T", " ");
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
		String currentDat = df.format(new Date());// new Date()为获取当前系统时间
		context.setData("prtDat", currentDat); // 打印日期
		context.setData("eupsBusTyp", "烟草");
		context.setData("tlr", tlr);
		context.setData("br", br);
		
		
	if("1".equals(prtFlg)){

		EupsTransJournal  jnl=new EupsTransJournal();
		jnl.setMfmTxnSts("S");
//		jnl.setAcDte(d);
		List<EupsTransJournal> entityList = eupstransjournalRepository.find(jnl);
		context.setData("eles",entityList);
		
		log.info("TOMCAT----------------"+"Start--report");
		log.info("TOMCAT----------------"+"End--report");
		if (CollectionUtils.isNotEmpty(entityList)) {
			String result = batchReport.renderAsString("tobaccoPrintReport",
					context);
			log.info("TOMCAT----------------" + result);
			PrintWriter printWriter = null;
			String mftploca = "E:\\Report\\";
			String reportFileName = "tbc.txt";
			try {
				File file = new File(mftploca);
				if (!file.exists()) {
					file.mkdirs();
				}
				printWriter = new PrintWriter(new BufferedWriter(
						new OutputStreamWriter(new FileOutputStream(
								mftploca + reportFileName), "GBK")));
				printWriter.write(result);
			} catch (IOException e) {
				log.info("---io异常");
			} finally {
				if (null != printWriter) {
					try {
						printWriter.close();
					} catch (Exception e) {
						log.info("关闭流出错");
						return;
					}
				}
			}	
	}
	
	}
	}
}
