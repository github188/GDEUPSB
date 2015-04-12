package com.bocom.bbip.gdeupsb.action.fbpd;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.action.common.OperateFTPAction;
import com.bocom.bbip.eups.action.common.OperateFileAction;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsBatchInfoDetail;
import com.bocom.bbip.eups.entity.EupsThdFtpConfig;
import com.bocom.bbip.eups.repository.EupsThdFtpConfigRepository;
import com.bocom.bbip.eups.spi.service.batch.AfterBatchAcpService;
import com.bocom.bbip.eups.spi.vo.AfterBatchAcpDomain;
import com.bocom.bbip.gdeupsb.action.common.BatchFileCommon;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.repository.GdFbpdMposBatchTmpRepository;
import com.bocom.bbip.gdeupsb.repository.GdFbpdNeleBatchTmpRepository;
import com.bocom.bbip.gdeupsb.repository.GdFbpdObusBatchTmpRepository;
import com.bocom.bbip.utils.Assert;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

public class AfterBatchAcpServiceImplZSAG00 extends BaseAction implements
		AfterBatchAcpService {
	private static final Log logger = LogFactory
			.getLog(AfterBatchAcpServiceImplZSAG00.class);

	@Override
	public void afterBatchDeal(AfterBatchAcpDomain arg0, Context context)
			throws CoreException {
		logger.info("返盘文件处理开始");
		logger.info("=============context:" + context);
		String comNo = context.getData(ParamKeys.COMPANY_NO);
		((BatchFileCommon) get(GDConstants.BATCH_FILE_COMMON_UTILS))
				.eupsBatchConSoleInfoAndgdEupsBatchConSoleInfo(context);
		Map<String, Object> ret = new HashMap<String, Object>();
		final List result = (List<EupsBatchInfoDetail>) context
				.getVariable("detailList");
		Assert.isNotEmpty(result, ErrorCodes.EUPS_QUERY_NO_DATA);
		EupsThdFtpConfig config = get(EupsThdFtpConfigRepository.class)
				.findOne((String) context.getData(ParamKeys.FTP_ID));
		Assert.isFalse(null == config, ErrorCodes.EUPS_THD_FTP_CONFIG_NOTEXIST);
		List<Map<String, String>> resultMap = (List<Map<String, String>>) BeanUtils
				.toMaps(result);

		List lt = new ArrayList();

		if ("4840000015".equals(comNo) || "4840000167".equals(comNo)
				|| "4840000018".equals(comNo) || "4840000017".equals(comNo)
				|| "4840000020".equals(comNo) || "4840000019".equals(comNo)
				|| "4840000598".equals(comNo) || "4840000414".equals(comNo) || "4840000374".equals(comNo)) { // OTHER
			lt = get(GdFbpdObusBatchTmpRepository.class).findByBatNo(
					(String) context.getData(ParamKeys.BAT_NO));
		}
		if ("4840000016".equals(comNo) || "4840000484".equals(comNo)
				|| "4840000352".equals(comNo) || "4840000363".equals(comNo)
				|| "4840000475".equals(comNo)) {
			// NELE
			lt = get(GdFbpdNeleBatchTmpRepository.class).findByBatNo(
					(String) context.getData(ParamKeys.BAT_NO));
		}
		if ("4840000416".equals(comNo)) {
			// MPOS
			lt = get(GdFbpdMposBatchTmpRepository.class).findByBatNo(
					(String) context.getData(ParamKeys.BAT_NO));

		}

//		List<GDEupsZhAGBatchTemp> lt = get(GDEupsZHAGBatchTempRepository.class)
//				.findByBatNo((String) context.getData(ParamKeys.BAT_NO));
		List<Map<String, Object>> tempMap = (List<Map<String, Object>>) BeanUtils
				.toMaps(lt);

		for (int i = 0; i < tempMap.size(); i++) {
			tempMap.get(i).putAll(resultMap.get(i));
		}
		ret.put("header", context.getDataMapDirectly());
		ret.put("detail", tempMap);
		String formatOut = findFormat((String) context.getData("comNo"));
		((OperateFileAction) get("opeFile")).createCheckFile(config, formatOut,
				null, ret);

		((OperateFTPAction) get("opeFTP")).putCheckFile(config);

		logger.info("返盘文件处理结束");

	}

	private String findFormat(final String comNo) {
		InputStream location = null;
		try {
			location = getClass().getClassLoader().getResourceAsStream(
					"config/fmt/fileFmt/fbpd/transOut.properties");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		Properties prop = new Properties();
		Map<String, String> propMap = new HashMap<String, String>();
		try {
			prop.load(location);
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (Iterator localIterator = prop.keySet().iterator(); localIterator
				.hasNext();) {
			Object key = localIterator.next();
			String keyStr = key.toString();
			String value = prop.getProperty(keyStr);
			propMap.put(keyStr, value);
		}

		return propMap.get(comNo);
	}
}
