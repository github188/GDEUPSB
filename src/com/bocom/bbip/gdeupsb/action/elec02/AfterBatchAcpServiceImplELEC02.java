package com.bocom.bbip.gdeupsb.action.elec02;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.entity.GDEupsbElecstBatchTmp;
import com.bocom.bbip.gdeupsb.repository.GDEupsbElecstBatchTmpRepository;
import com.bocom.bbip.utils.Assert;
import com.bocom.bbip.utils.BeanUtils;
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
		Map<String,Object>ret=new HashMap<String,Object>();
        final List result=(List<EupsBatchInfoDetail>)context.getVariable("detailList");
        Assert.isNotEmpty(result, ErrorCodes.EUPS_QUERY_NO_DATA);
        EupsThdFtpConfig config=get(EupsThdFtpConfigRepository.class).findOne((String)context.getData(ParamKeys.FTP_ID));
		Assert.isFalse(null == config, ErrorCodes.EUPS_THD_FTP_CONFIG_NOTEXIST);
        List<Map<String,String>>resultMap=(List<Map<String, String>>) BeanUtils.toMaps(result);
		List <GDEupsbElecstBatchTmp>lt=get(GDEupsbElecstBatchTmpRepository.class).findByBatNo((String)context.getData(ParamKeys.BAT_NO));
		List<Map<String,Object>>tempMap=(List<Map<String, Object>>) BeanUtils.toMaps(lt);
		for (Map m : tempMap) {
			for (Map mm :resultMap) {
                   if(((String)m.get("cusAc")).equals((String)mm.get("cusAc"))){
                	    m.putAll(mm);
                   }
			}
		}
		
		ret.put("header", context.getDataMapDirectly());
		ret.put("detail", tempMap);
        ((OperateFileAction)get("opeFile")).createCheckFile(config, "ELEC02BatchBack", null, ret);

		((OperateFTPAction)get("opeFTP")).putCheckFile(config);
		/**通知第三方*/
		 context.setData("", "");
		 Map<String,Object>thdResult= get(CallThdService.class).callTHD(context);
		 logger.info("电力返盘文件处理结束");
	}
  
}
