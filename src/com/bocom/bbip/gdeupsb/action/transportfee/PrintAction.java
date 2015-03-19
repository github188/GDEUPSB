package com.bocom.bbip.gdeupsb.action.transportfee;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

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
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
import com.bocom.jump.bp.support.CollectionUtils;

public class PrintAction extends BaseAction{
	private final static Log log = LogFactory.getLog(PrintAction.class);
	@Autowired
	GDEupsbTrspFeeInfoRepository gdEupsbTrspFeeInfoRepository;

	public void execute(Context ctx) throws CoreRuntimeException,CoreException{
		log.info("PrintAction start.......");
		ctx.setData(GDParamKeys.OINV_NO, ctx.getData(GDParamKeys.INV_NO));
		//TODO:待考虑！！！！
		if(!ctx.getData(GDParamKeys.OINV_NO).toString().trim().equals(ctx.getData(GDParamKeys.INV_NO).toString().trim())){
			ctx.setData(ParamKeys.RSP_MSG, "发票号码改变，请补打");
			throw new CoreRuntimeException(GDErrorCodes.EUPS_INVOIC_NO_ERROR);
		}
		System.out.println("hahhahahahahahhahah");

		Date printDate = new Date();
		ctx.setData("printYear", DateUtils.format(printDate, DateUtils.STYLE_yyyy));
		ctx.setData("printMon", DateUtils.format(printDate, DateUtils.STYLE_MM));
		ctx.setData("printDay", DateUtils.format(printDate, DateUtils.STYLE_dd));
		
		Map<String, String> mapping = CollectionUtils.createMap();
		VelocityTemplatedReportRender render = new VelocityTemplatedReportRender();
		String sampleFile="config/report/zhTransport/transportInv.vm";
		GDEupsbTrspFeeInfo gdEupsbTrspFeeInfo = new GDEupsbTrspFeeInfo();
		gdEupsbTrspFeeInfo.setPayLog(ctx.getData(ParamKeys.OLD_TXN_SEQUENCE).toString());
		List<GDEupsbTrspFeeInfo> feeInfoList = gdEupsbTrspFeeInfoRepository.find(gdEupsbTrspFeeInfo);
		Assert.isNotEmpty(feeInfoList, ErrorCodes.EUPS_QUERY_NO_DATA);
//		List<Map<String,Object>>eles=(List<Map<String,Object>>)BeanUtils.toMaps(list);
//		ctx.setData("SumCnt", list.size());
//		ctx.setData(ParamKeys.TELLER, "EP88888");
		mapping.put("sample", sampleFile);
		try {
			render.afterPropertiesSet();
		} catch (Exception e) {
			e.printStackTrace();
		}
//		ctx.setData("eles", eles);
		ctx.setDataMap(BeanUtils.toMap(feeInfoList.get(0)));
		render.setReportNameTemplateLocationMapping(mapping);
		String result = render.renderAsString("sample", ctx);
		try {
			log.info("generate report content:****"+new String(result.getBytes(GDConstants.CHARSET_ENCODING_GBK)));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		BufferedOutputStream outStream = null;
		try {

			outStream = new BufferedOutputStream(new FileOutputStream(
					"D:\\template.txt"));
			outStream.write(result.getBytes(GDConstants.CHARSET_ENCODING_GBK));
			outStream.close();
		} catch (IOException e) {
			throw new CoreException("BBIP0004EU0128");
		}
		
         
	}
}
