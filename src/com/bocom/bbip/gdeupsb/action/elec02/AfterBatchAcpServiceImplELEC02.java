package com.bocom.bbip.gdeupsb.action.elec02;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bocom.bbip.comp.BBIPPublicServiceImpl;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.action.common.OperateFTPAction;
import com.bocom.bbip.eups.action.common.OperateFileAction;
import com.bocom.bbip.eups.adaptor.support.CallThdService;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsBatchInfoDetail;
import com.bocom.bbip.eups.entity.EupsThdFtpConfig;
import com.bocom.bbip.eups.repository.EupsThdFtpConfigRepository;
import com.bocom.bbip.eups.spi.service.batch.AfterBatchAcpService;
import com.bocom.bbip.eups.spi.vo.AfterBatchAcpDomain;
import com.bocom.bbip.gdeupsb.action.common.BatchFileCommon;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.entity.GDEupsbElecstBatchTmp;
import com.bocom.bbip.gdeupsb.repository.GDEupsbElecstBatchTmpRepository;
import com.bocom.bbip.utils.Assert;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
/**
 * @author wuyh
 * @date 2015-3-07 上午09:06:35
 * @Company TD
 */
public class AfterBatchAcpServiceImplELEC02 extends BaseAction implements AfterBatchAcpService {
	private static final Log logger = LogFactory.getLog(AfterBatchAcpServiceImplELEC02.class);

	@Override
	public void afterBatchDeal(AfterBatchAcpDomain arg0, Context context)throws CoreException {
		logger.info("电力返盘文件处理开始");
		((BatchFileCommon)get(GDConstants.BATCH_FILE_COMMON_UTILS)).afterBatchProcess(context);
		Map<String,Object>ret=new HashMap<String,Object>();
        final List result=(List<EupsBatchInfoDetail>)context.getVariable("detailList");
        Assert.isNotEmpty(result, ErrorCodes.EUPS_QUERY_NO_DATA);
        EupsThdFtpConfig config=get(EupsThdFtpConfigRepository.class).findOne((String)context.getData(ParamKeys.FTP_ID));
		Assert.isFalse(null == config, ErrorCodes.EUPS_THD_FTP_CONFIG_NOTEXIST);
        List<Map<String,String>>resultMap=(List<Map<String, String>>) BeanUtils.toMaps(result);
		List <GDEupsbElecstBatchTmp>lt=get(GDEupsbElecstBatchTmpRepository.class).findByBatNo((String)context.getData(ParamKeys.BAT_NO));
		List<Map<String,Object>>tempMap=(List<Map<String, Object>>) BeanUtils.toMaps(lt);
		for(int i=0;i<tempMap.size();i++){
			tempMap.get(i).putAll(resultMap.get(i));
		}
		
		ret.put("header", context.getDataMapDirectly());
		ret.put("detail", tempMap);
        ((OperateFileAction)get("opeFile")).createCheckFile(config, "ELEC02BatchBack", null, ret);

		((OperateFTPAction)get("opeFTP")).putCheckFile(config);
		/**通知第三方*/
		 context.setData("TransCode", "23");
		 context.setData("WD0", DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMdd));
		 String logNo=((BBIPPublicServiceImpl)get(GDConstants.BBIP_PUBLIC_SERVICE)).getBBIPSequence();
		 context.setData("LogNo", StringUtils.substring(logNo, 4));
		 String etlr=StringUtils.substring(logNo, 15);
		 String tmn=StringUtils.substring(logNo, 8);
		 context.setData("TMN", tmn);
		 context.setData("FileName", "");
		 context.setData("recordNum", (String)context.getData("totCnt"));
		 Map<String,Object>thdResult= get(CallThdService.class).callTHD(context);
		 logger.info("电力返盘文件处理结束");
	}
  
}
