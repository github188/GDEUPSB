package com.bocom.bbip.gdeupsb.action.elec02;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bocom.bbip.comp.BBIPPublicServiceImpl;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.entity.EupsBatchConsoleInfo;
import com.bocom.bbip.eups.repository.EupsBatchConsoleInfoRepository;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

public class BatchBackAction extends BaseAction {
	private static final Log logger = LogFactory.getLog(BatchBackAction.class);

	public void execute(Context context) throws CoreException {
       logger.info("----------after batch process---------");
       EupsBatchConsoleInfo console=new EupsBatchConsoleInfo();
		console.setBatSts("S");
		console.setSubDte(new Date());
		console.setComNo("4450000002");
       List<EupsBatchConsoleInfo>ret=get(EupsBatchConsoleInfoRepository.class).find(console);
       context.getDataMapDirectly().putAll(BeanUtils.toMap(ret.get(0)));
       ((BBIPPublicServiceImpl)get(GDConstants.BBIP_PUBLIC_SERVICE)).synExecute("eups.commNotifyBatchStatusExt", context);
	}
}
