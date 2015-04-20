package com.bocom.bbip.gdeupsb.action.transportfee;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.config.TxNamespaceHandler;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.comp.BBIPPublicServiceImpl;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.file.reporting.impl.VelocityTemplatedReportRender;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDErrorCodes;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GDEupsbTrspFeeInfo;
import com.bocom.bbip.gdeupsb.entity.GdLotTxnJnl;
import com.bocom.bbip.gdeupsb.repository.GDEupsbTrspFeeInfoRepository;
import com.bocom.bbip.gdeupsb.repository.GdLotTxnJnlRepository;
import com.bocom.bbip.utils.Assert;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
import com.bocom.jump.bp.support.CollectionUtils;

public class PrintAction extends BaseAction{
	private final static Log log = LogFactory.getLog(PrintAction.class);
	@Autowired
	GDEupsbTrspFeeInfoRepository gdEupsbTrspFeeInfoRepository;
	
	@Autowired
	BBIPPublicServiceImpl bbipPublicService;

	public void execute(Context ctx) throws CoreRuntimeException,CoreException{
		log.info("PrintAction start.......");
//		ctx.setData(GDParamKeys.OINV_NO, ctx.getData(GDParamKeys.INV_NO));
//		//TODO:待考虑！！！！
//		if(!ctx.getData(GDParamKeys.OINV_NO).toString().trim().equals(ctx.getData(GDParamKeys.INV_NO).toString().trim())){
//			ctx.setData(ParamKeys.RSP_MSG, "发票号码改变，请补打");
//			throw new CoreRuntimeException(GDErrorCodes.EUPS_INVOIC_NO_ERROR);
//		}
		

//		Date printDate = new Date();
//		Date begDat = DateUtils.parse((String)ctx.getData(GDParamKeys.BEG_DAT));
//		Date endDat = DateUtils.parse((String)ctx.getData(GDParamKeys.END_DAT));
//		ctx.setData("printYear", DateUtils.format(printDate, DateUtils.STYLE_yyyy));
//		ctx.setData("printMon", DateUtils.format(printDate, DateUtils.STYLE_MM));
//		ctx.setData("printDay", DateUtils.format(printDate, DateUtils.STYLE_dd));
//		ctx.setData("begYear", DateUtils.format(begDat, DateUtils.STYLE_yyyy));
//		ctx.setData("begMon", DateUtils.format(begDat, DateUtils.STYLE_MM));
//		ctx.setData("begDay", DateUtils.format(begDat, DateUtils.STYLE_dd));
//		ctx.setData("endYear", DateUtils.format(endDat, DateUtils.STYLE_yyyy));
//		ctx.setData("endMon", DateUtils.format(endDat, DateUtils.STYLE_MM));
//		ctx.setData("endDay", DateUtils.format(endDat, DateUtils.STYLE_dd));
//		ctx.setData("bigTxnAmt", StringUtils.amountInWords((String)ctx.getData(GDParamKeys.TXN_AMT)));
//		Map<String, String> mapping = CollectionUtils.createMap();
//		VelocityTemplatedReportRender render = new VelocityTemplatedReportRender();
//		String sampleFile="config/report/zhTransport/transportInv.vm";
		GDEupsbTrspFeeInfo gdEupsbTrspFeeInfo = new GDEupsbTrspFeeInfo();
		gdEupsbTrspFeeInfo.setPayLog(ctx.getData(ParamKeys.OLD_TXN_SEQUENCE).toString());
		List<GDEupsbTrspFeeInfo> feeInfoList = gdEupsbTrspFeeInfoRepository.find(gdEupsbTrspFeeInfo);
		Assert.isNotEmpty(feeInfoList, ErrorCodes.EUPS_QUERY_NO_DATA);

//		mapping.put("sample", sampleFile);
//		try {
//			render.afterPropertiesSet();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

		ctx.setDataMap(BeanUtils.toMap(feeInfoList.get(0)));
		
//		render.setReportNameTemplateLocationMapping(mapping);
//		String result = render.renderAsString("sample", ctx);
//		try {
//			log.info("generate report content:****"+new String(result.getBytes(GDConstants.CHARSET_ENCODING_GBK)));
//		} catch (UnsupportedEncodingException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		String fileName = ctx.getData(GDParamKeys.INV_NO).toString()+ctx.getData(ParamKeys.TELLER)+"00";
//		BufferedOutputStream outStream = null;
//		try {
//
//			outStream = new BufferedOutputStream(new FileOutputStream(
//					"/home/bbipadm/data/mftp/BBIP/GDEUPSB/trsp/"+fileName));
//			outStream.write(result.getBytes(GDConstants.CHARSET_ENCODING_GBK));
//			outStream.close();
//			
//		} catch (IOException e) {
//			throw new CoreException("BBIP0004EU0128");
//		}
//		
//		String path = "/home/weblogic/JumpServer/WEB-INF/data/mftp_recv/";
//		
//		String FilNam = "/home/bbipadm/data/mftp/" +fileName;
//		try {
//			
//			log.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
//			log.info("FilNam=" + FilNam);
//			log.info("path + filname=" + path + fileName);
//			bbipPublicService.sendFileToBBOS(new File(FilNam), path + fileName, "p");
//			
//		}catch (Exception e) {
//			throw new CoreException(ErrorCodes.EUPS_FAIL);
//		}
         
	}
}
