package com.bocom.bbip.gdeupsb.action.elec02;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bocom.bbip.comp.BBIPPublicServiceImpl;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.entity.GDEupsBatchConsoleInfo;
import com.bocom.bbip.gdeupsb.repository.GDEupsBatchConsoleInfoRepository;
import com.bocom.bbip.utils.Assert;
import com.bocom.bbip.utils.CollectionUtils;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

public class EupsCancelBatchAction extends BaseAction {
	private static final Log logger = LogFactory.getLog(EupsCancelBatchAction.class);

	public void process(Context context) throws CoreException {
        logger.info("begin batch cancel!!!!!");
        final String fileName=context.getData("FilNm");
        GDEupsBatchConsoleInfo info = new GDEupsBatchConsoleInfo();
		info.setFleNme(fileName);
//		GDEupsBatchConsoleInfo ret = get(GDEupsBatchConsoleInfoRepository.class).findConsoleInfo(info);
//		if(null==ret){
//			context.setData("QSFlg", "4");
//			logger.info("批次控制信息不存在");
//			throw new CoreException(ErrorCodes.EUPS_BAT_CTL_INFO_NOT_EXIST);
//		}
		List<Map<String, Object>> ret =get(GDEupsBatchConsoleInfoRepository.class).findConsoleInfo(info);
		if(CollectionUtils.isEmpty(ret)){
			context.setData("QSFlg", "4");
			logger.info("批次控制信息不存在");
			throw new CoreException(ErrorCodes.EUPS_BAT_CTL_INFO_NOT_EXIST);
		}
		else{
			Map retMap=ret.get(0);
			String batNo=(String)retMap.get("BAT_NO");
			if(StringUtils.isEmpty(batNo)){
				 batNo=(String)retMap.get("BATNO");
			}
			context.setData(ParamKeys.BAT_NO, batNo);
		}
		
        /**批次撤销公共process*/
       	((BBIPPublicServiceImpl)get(GDConstants.BBIP_PUBLIC_SERVICE)).
       	synExecute(GDConstants.BATCH_CANCEL_PROCESSID, context);
        
	}
}
