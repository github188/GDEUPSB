package com.bocom.bbip.gdeupsb.service.impl.watr00;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
import com.bocom.bbip.eups.entity.EupsTransJournal;
import com.bocom.bbip.eups.entity.MFTPConfigInfo;
import com.bocom.bbip.eups.repository.EupsThdFtpConfigRepository;
import com.bocom.bbip.file.reporting.impl.VelocityTemplatedReportRender;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GDEupsbTrspFeeInfo;
import com.bocom.bbip.utils.Assert;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
import com.bocom.jump.bp.service.sqlmap.SqlMap;
import com.bocom.jump.bp.support.CollectionUtils;
/**
 * 水费对公记账回执打印
 * @author hefengwen
 *
 */
public class PrintPublicMsgServiceActionWATR00 extends BaseAction {
	
	private static Logger logger = LoggerFactory.getLogger(PrintPublicMsgServiceActionWATR00.class);
	
	@Override
	public void execute(Context context) throws CoreException,	CoreRuntimeException {
		logger.info("PrintPublicMsgServiceActionWATR00 start ... ...");
		String beginDate = context.getData("beginDate");
		String endDate = context.getData("endDate");
		String cusAc = context.getData("cusAc");
		logger.info("beginDate["+beginDate+"]endDate["+endDate+"]cusAc["+cusAc+"]");
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("beginDate", beginDate);
		param.put("endDate", endDate);
		if(cusAc==null||"".equals(cusAc.toString().trim())){
			param.put("cusAc", null);
		}else{
			param.put("cusAc", cusAc);
		}
		param.put("br", context.getData("br"));
		param.put("eupsBusTyp", context.getData("eupsBusTyp"));
//		
//		EupsThdFtpConfigRepository eupsThdFtpConfigRepository = get(EupsThdFtpConfigRepository.class);
//		ReportHelper reportHelper = get(ReportHelper.class);
//		MFTPConfigInfo mftpConfigInfo = reportHelper.getMFTPConfigInfo(eupsThdFtpConfigRepository);
//		logger.info((new StringBuilder("mftpConfigInfo:>>>>").append(BeanUtils.toMap(mftpConfigInfo))).toString());
//		
		List<Map<String,Object>> eupsTransJournals = ((SqlMap)get("sqlMap")).queryForList("watr00.queryPublicMsg", param);
		for(Map<String,Object> m:eupsTransJournals){
			m.put("cusAc", m.get("CUS_AC"));
			m.put("cusNme", m.get("CUS_NMES"));
			m.put("ccy", m.get("CCY"));
			m.put("txnAmt", m.get("TXN_AMT"));
			m.put("txnAmtInWord", StringUtils.amountInWords(String.valueOf(m.get("txnAmt"))));
			m.put("thdCusNo", m.get("THD_CUS_NO"));
			m.put("actDat", String.valueOf(m.get("AC_DTE")));
			m.put("txnTlr", m.get("TXN_TLR"));
			m.put("sqn", m.get("SQN"));
		}
		context.setData("actNo", "445007012620194007699");
		context.setData("actNm", "汕头自来水公司");
		
//		VelocityTemplatedReportRender render = new VelocityTemplatedReportRender();
//		try {
//			render.afterPropertiesSet();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		Map<String,String> map = new HashMap<String,String>();
//		map.put("sample", "config/report/watr00/watr00_printPublicMsg.vm");
//		render.setReportNameTemplateLocationMapping(map);
//		context.setData("eles", eupsTransJournals);
//		String result = render.renderAsString("sample", context);
//		logger.info(result);
//		String date = DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMdd);
//		StringBuffer fileName = new StringBuffer((new StringBuilder("").append(date).toString()));
//		reportHelper.createFileAndSendMFTP(context, result, fileName, mftpConfigInfo);
		logger.info("PrintPublicMsgServiceActionWATR00 end ... ...");
		
		
		
		
		Map<String, String> mapping = CollectionUtils.createMap();
		VelocityTemplatedReportRender render = new VelocityTemplatedReportRender();
		String sampleFile="config/report/zhTransport/transportInv.vm";
//		GDEupsbTrspFeeInfo gdEupsbTrspFeeInfo = new GDEupsbTrspFeeInfo();
//		gdEupsbTrspFeeInfo.setPayLog(context.getData(ParamKeys.OLD_TXN_SEQUENCE).toString());
//		List<GDEupsbTrspFeeInfo> feeInfoList = gdEupsbTrspFeeInfoRepository.find(gdEupsbTrspFeeInfo);
//		Assert.isNotEmpty(feeInfoList, ErrorCodes.EUPS_QUERY_NO_DATA);

		mapping.put("sample", sampleFile);
		try {
			render.afterPropertiesSet();
		} catch (Exception e) {
			e.printStackTrace();
		}
//
//		context.setDataMap(BeanUtils.toMap(feeInfoList.get(0)));
//		System.out.println("@@@@@@@@@@@@@@@@@@@@"+context);
		render.setReportNameTemplateLocationMapping(mapping);
		String result = render.renderAsString("sample", context);
		try {
			log.info("generate report content:****"+new String(result.getBytes(GDConstants.CHARSET_ENCODING_GBK)));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String fileName = context.getData(GDParamKeys.INV_NO).toString()+context.getData(ParamKeys.TELLER)+"00";
		BufferedOutputStream outStream = null;
		try {

			outStream = new BufferedOutputStream(new FileOutputStream(
					"D:\\test"+fileName));
			outStream.write(result.getBytes(GDConstants.CHARSET_ENCODING_GBK));
			outStream.close();
		} catch (IOException e) {
			throw new CoreException("BBIP0004EU0128");
		}

	


	}
}
