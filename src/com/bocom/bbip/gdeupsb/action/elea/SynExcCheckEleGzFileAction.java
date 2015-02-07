package com.bocom.bbip.gdeupsb.action.elea;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

/**
 * 银行发起银行代扣代收电费对帐交易
 * 
 * 
 * @author qc.w
 * @since 1.0.0
 */
public class SynExcCheckEleGzFileAction extends BaseAction {

	private final String ELEC_EUPS_BUS_TYP = "ELEC01";

	private final String ELEC_CHECK_PROCESS = "eups.checkBankFileToThird";

	@Override
	public void execute(Context context) throws CoreException, CoreRuntimeException {
		log.info("SynTransferJlzfDeal start!..");
		context.setData(ParamKeys.EUPS_BUSS_TYPE, this.ELEC_EUPS_BUS_TYP);
		context.setData(ParamKeys.FTP_ID, "eleGzCheckFileToThird"); // ftpID
		get(BBIPPublicService.class).synExecute(this.ELEC_CHECK_PROCESS, context);
	}

}
