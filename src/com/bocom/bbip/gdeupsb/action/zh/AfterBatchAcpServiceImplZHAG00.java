package com.bocom.bbip.gdeupsb.action.zh;

import java.io.IOException;
import java.io.InputStream;
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
import com.bocom.bbip.gdeupsb.entity.GDEupsZhAGBatchTemp;
import com.bocom.bbip.gdeupsb.repository.GDEupsZHAGBatchTempRepository;
import com.bocom.bbip.utils.Assert;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

public class AfterBatchAcpServiceImplZHAG00 extends BaseAction implements AfterBatchAcpService {
	private static final Log logger = LogFactory.getLog(AfterBatchAcpServiceImplZHAG00.class);

	@Override
	public void afterBatchDeal(AfterBatchAcpDomain arg0, Context context)
			throws CoreException {
		logger.info("返盘文件处理开始");
		((BatchFileCommon)get(GDConstants.BATCH_FILE_COMMON_UTILS)).eupsBatchConSoleInfoAndgdEupsBatchConSoleInfo(context);
		Map<String,Object>ret=new HashMap<String,Object>();
        final List result=(List<EupsBatchInfoDetail>)context.getVariable("detailList");
        Assert.isNotEmpty(result, ErrorCodes.EUPS_QUERY_NO_DATA);
        EupsThdFtpConfig config=get(EupsThdFtpConfigRepository.class).findOne((String)context.getData(ParamKeys.FTP_ID));
		Assert.isFalse(null == config, ErrorCodes.EUPS_THD_FTP_CONFIG_NOTEXIST);
        List<Map<String,String>>resultMap=(List<Map<String, String>>) BeanUtils.toMaps(result);
		List <GDEupsZhAGBatchTemp>lt=get(GDEupsZHAGBatchTempRepository.class)
		.findByBatNo((String)context.getData(ParamKeys.BAT_NO));
		List<Map<String,Object>>tempMap=(List<Map<String, Object>>) BeanUtils.toMaps(lt);
		
		for(int i=0;i<tempMap.size();i++){
			tempMap.get(i).putAll(resultMap.get(i));
		}
		ret.put("header", context.getDataMapDirectly());
		ret.put("detail", tempMap);
		String formatOut=findFormat((String)context.getData("comNo"));
        ((OperateFileAction)get("opeFile")).createCheckFile(config, formatOut, null, ret);

		((OperateFTPAction)get("opeFTP")).putCheckFile(config);
	
		 logger.info("返盘文件处理结束");

	}
	  private String findFormat(final String comNo) {
		  InputStream location=null;
		try {
			location = getClass().getClassLoader().
			getResourceAsStream("config/fmt/fileFmt/zh/transOut.properties");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		  Properties prop = new Properties();
		    Map<String,String> propMap = new HashMap<String,String>();
		    try {
		      prop.load(location);
		    } catch (IOException e) {
		      e.printStackTrace();
		    }
		    for (Iterator localIterator = prop.keySet().iterator(); localIterator.hasNext(); ) { Object key = localIterator.next();
		      String keyStr = key.toString();
		      String value = prop.getProperty(keyStr);
		      propMap.put(keyStr, value);
		    }
		  
		  return propMap.get(comNo);
	  }
}
