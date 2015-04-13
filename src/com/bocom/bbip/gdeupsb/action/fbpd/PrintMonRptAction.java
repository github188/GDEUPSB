package com.bocom.bbip.gdeupsb.action.fbpd;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.action.eupsreport.ReportHelper;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.MFTPConfigInfo;
import com.bocom.bbip.eups.repository.EupsThdBaseInfoRepository;
import com.bocom.bbip.eups.repository.EupsThdFtpConfigRepository;
import com.bocom.bbip.file.reporting.impl.VelocityTemplatedReportRender;
import com.bocom.bbip.gdeupsb.entity.GDEupsBatchConsoleInfo;
import com.bocom.bbip.gdeupsb.repository.GDEupsBatchConsoleInfoRepository;
import com.bocom.bbip.gdeupsb.repository.GdEupsBatchInfoDetailRepository;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

public class PrintMonRptAction extends BaseAction {
	private Logger logger = LoggerFactory.getLogger(PrintMonRptAction.class);

	public void execute(Context context) throws CoreException {
		logger.info("============ Enter in PrintMonRptAction ! ================");

		String prtDate = DateUtils.format(new Date(),
				DateUtils.STYLE_SIMPLE_DATE);

		Date beginDate = DateUtils.parse((String) context.getData("beginDate"),
				DateUtils.STYLE_SIMPLE_DATE);
		Date endDate = DateUtils.parse((String) context.getData("endDate"),
				DateUtils.STYLE_SIMPLE_DATE);

		String comNo = context.getData(ParamKeys.COMPANY_NO);
		String comNme = (String) get(EupsThdBaseInfoRepository.class).findOne(
				comNo).getComNme();

		Map<String, Object> map = new HashMap<String, Object>();
		map.put(ParamKeys.COMPANY_NO, comNo);
		map.put(ParamKeys.COMPANY_NAME, comNme);
		map.put("beginDate", beginDate);
		map.put("endDate", endDate);
		map.put("ParamKeys.TELLER", context.getData(ParamKeys.TELLER));
		map.put("prtDate", prtDate);

		GDEupsBatchConsoleInfo batchConsoleInfo = new GDEupsBatchConsoleInfo();
		batchConsoleInfo.setComNo(comNo);

		// 取总金额总笔数
		List<Map<String, Object>> totalInfo = get(
				GDEupsBatchConsoleInfoRepository.class).findTotalInfo(map);
		map.put("TotCnt", totalInfo.get(0).get("BATTOTCNT"));
		map.put("TotAmt", totalInfo.get(0).get("BATTOTAMT"));

		// 取批量成功的 对公总笔数总金额 对私存折总笔数总金额 卡总笔数总金额
		List<Map<String, Object>> firmBatDetail = get(
				GdEupsBatchInfoDetailRepository.class).findFirmBatDetail(map);
		List<Map<String, Object>> bankBookBatDetail = get(
				GdEupsBatchInfoDetailRepository.class).findBangBookBatDetail(
				map);
		List<Map<String, Object>> cardBatDetail = get(
				GdEupsBatchInfoDetailRepository.class).findCardBatDetail(map);

		map.put("TotCnt1", firmBatDetail.get(0).get("TOTCNT1"));
		map.put("TotAmt1", firmBatDetail.get(0).get("TOTAMT1"));

		map.put("TotCnt2", bankBookBatDetail.get(0).get("TOTCNT2"));
		map.put("TotAmt2", bankBookBatDetail.get(0).get("TOTAMT2"));

		map.put("TotCnt3", cardBatDetail.get(0).get("TOTCNT3"));
		map.put("TotAmt3", cardBatDetail.get(0).get("TOTAMT3"));

		List<Map<String, Object>> prtList = new ArrayList<Map<String, Object>>();
		prtList.add(map);

		StringBuffer fileName = new StringBuffer("中山批量月报打印");
		logger.info("==============fileName:" + fileName.toString());

		// 配置生成文件名字路径，其他信息从ftpCfg中获取
		EupsThdFtpConfigRepository eupsThdFtpConfigRepository = get(EupsThdFtpConfigRepository.class);
		ReportHelper reportHelper = get(ReportHelper.class);
		MFTPConfigInfo mftpConfigInfo = reportHelper
				.getMFTPConfigInfo(eupsThdFtpConfigRepository); // TODO
		logger.info((new StringBuilder("mftpConfigInfo:>>>>").append(BeanUtils
				.toMap(mftpConfigInfo))).toString());

		VelocityTemplatedReportRender render = new VelocityTemplatedReportRender();
		try {
			render.afterPropertiesSet();
		} catch (Exception e) {
			e.printStackTrace();
		}

		String result = null;
		Map<String, String> prtMap = new HashMap<String, String>();
		prtMap.put("fbpdPrtMonRpt", "config/report/fbpd/printMonReport_all.vm");
		render.setReportNameTemplateLocationMapping(prtMap);
		context.setData("eles", prtList);
		result = render.renderAsString("fbpdPrtMonRpt", context);
		logger.info(result);

		logger.info("=============ready to print report list=============");

		// 本地生成报表文件并发送到mftp服务器,打印机自动打印
		// reportHelper.createFileAndSendMFTP(context,result,fileName,mftpConfigInfo);
		// //TODO

		/*****************************************************************************************************************************/

		// 拼装本地路径 本地测试
		PrintWriter printWriter = null;
		StringBuffer sbLocDir = new StringBuffer();
		sbLocDir.append("D:/testGash/checkFilTest/")
				.append(DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMdd))
				.append("/");
		// sbLocDir.append(ftpCfg.getLocDir()).append("/").append(context.getData(ParamKeys.TELLER)).append("/").append(DateUtils.format(new
		// Date(), DateUtils.STYLE_yyyyMMdd)).append("/");

		try {
			File file = new File(sbLocDir.toString());
			if (!file.exists()) {
				file.mkdirs();
			}
			printWriter = new PrintWriter(new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(sbLocDir
							.append(fileName).toString()), "GBK")));
			printWriter.write(result);

		} catch (IOException e) {
			throw new CoreException(ErrorCodes.EUPS_FILE_CREATE_FAIL);
		} finally {
			if (null != printWriter) {
				try {
					printWriter.close();
				} catch (Exception e) {
					throw new CoreException(ErrorCodes.EUPS_FILE_CREATE_FAIL);
				}
			}
		}
		//
		// try {
		// get(MftpTransfer.class).send(sbLocDir.toString(),
		// fileName.toString(), ftpCfg.getThdIpAdr());
		// log.info("mftp send success!");
		// } catch (Exception e) {
		// log.error("mftp send fail!");
		// throw new CoreException(ErrorCodes.EUPS_MFTP_FILEPUT_FAIL);
		// }

		/*****************************************************************************************************************************/

		// bbipPublicService.sendFileToBBOS(new
		// File(TransferUtils.resolveFilePath(mftploca, reportFileName)),
		// reportFileName, MftpTransfer.FTYPE_NORMAL);

		// reportHelper.createFileAndSendMFTP(context, result, fileName,
		// mftpConfigInfo);
		// context.setData("filName", fileName);
		
		String br = context.getData(ParamKeys.BR);
		String teller = (String) context.getData("tlr");
		fileName.append("_p")
				.append((new StringBuilder("_")).append(br).toString())
				.append((new StringBuilder("_")).append(teller).toString());
		context.setData(ParamKeys.FLE_NME, fileName.toString());

		logger.info("PrintReportServiceActionPGAS00 execute end ... ...");

	}
}
