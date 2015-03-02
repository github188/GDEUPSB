package com.bocom.bbip.gdeupsb.action;

import java.util.List;
import java.util.Map;

import com.bocom.bbip.comp.account.AccountService;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.action.common.OperateFileAction;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsThdFtpConfig;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

/**
 * 批扣-批扣文件入库，待清算批次生成
 * 
 * 
 * @author qc.w
 * 
 */
public class BatchFileDealBthNoGenAction extends BaseAction {


	@Override
	public void execute(Context context) throws CoreException, CoreRuntimeException {
		log.info("COM_BATCH_DEAL_TYP start!..");
		String cusAc = context.getData(ParamKeys.CUS_AC);
		AccountService  accountService=new AccountService();
		accountService.getAcInf(null, cusAc);
		
	}

}
