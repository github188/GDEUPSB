package com.bocom.bbip.gdeupsb.action.elec02;

import java.io.FileOutputStream;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.file.reporting.impl.VelocityTemplatedReportRender;
import com.bocom.bbip.gdeupsb.utils.SwitchCodeUtils;
import com.bocom.bbip.thd.org.apache.commons.io.IOUtils;
import com.bocom.bbip.utils.ContextUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
import com.bocom.jump.bp.support.CollectionUtils;
/** 460251 电力对公缴费记账回执打印*/
public class EupsPrintElecBackAction  extends BaseAction {
	private static final Log logger=LogFactory.getLog(EupsPrintElecBackAction.class);
	
	public void process(Context context)throws CoreException,CoreRuntimeException, java.io.IOException{
		logger.info("开始打印回执!!");context.setData(ParamKeys.BR, "666666");
		context.setData(ParamKeys.BEGIN_DATE, "2015-02-03");context.setData(ParamKeys.END_DATE, "2015-02-03");
		context.setData(ParamKeys.ACT_NO, "20158989799");context.setData(ParamKeys.TELLER, "EP90989");
		context.setData("appNm", "GZ_EFE1");context.setData(ParamKeys.END_DATE, "2015-02-03");
		context.setData("actNm", "交行广东分行");context.setData(ParamKeys.TXN_AMT, "2345666.99");
		final String beginDate=ContextUtils.assertDataHasLengthAndGetNNR(context, ParamKeys.BEGIN_DATE, ErrorCodes.EUPS_FIELD_EMPTY);
		final String endDate=ContextUtils.assertDataHasLengthAndGetNNR(context, ParamKeys.END_DATE, ErrorCodes.EUPS_FIELD_EMPTY);
		/** 付款人单位账号 */
		final String actNo=ContextUtils.assertDataHasLengthAndGetNNR(context, ParamKeys.ACT_NO, ErrorCodes.EUPS_FIELD_EMPTY);
		/** 单位类型 */
		final String appNm=ContextUtils.assertDataHasLengthAndGetNNR(context, "appNm", ErrorCodes.EUPS_FIELD_EMPTY);
		/** 收款人账号 */
		final String IActNo = SwitchCodeUtils.getIActNo(appNm);
		/** 收款人名称 */
		final String IActNm = SwitchCodeUtils.getIActNm(appNm);
		context.setData("IActNo", IActNo);
		context.setData("IActNm", IActNm);
		/** 开始生成打印文件 */
		Map<String, String> mapping = CollectionUtils.createMap();
		VelocityTemplatedReportRender render = new VelocityTemplatedReportRender();
		String sampleFile = "config/report/elec02/printBack.vm";
		mapping.put("sample", sampleFile);
		try {
			render.afterPropertiesSet();
		} catch (Exception e) {
			e.printStackTrace();
		}
		render.setReportNameTemplateLocationMapping(mapping);
		String result = render.renderAsString("sample", context);
		logger.info("Generate report content:****"
				+ new String(result.getBytes("gbk")));
		IOUtils.write(result.getBytes("gbk"), new FileOutputStream(
				"D:\\template.txt"));
		logger.info("打印回执成功!!");
		
	}

	
	
	
}
