package com.bocom.bbip.gdeupsb.action.elec02;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsCorpAgent;
import com.bocom.bbip.eups.entity.EupsTransJournal;
import com.bocom.bbip.eups.repository.EupsCorpAgentRepository;
import com.bocom.bbip.eups.repository.EupsTransJournalRepository;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.utils.SwitchCodeUtils;
import com.bocom.bbip.utils.Assert;
import com.bocom.bbip.utils.ContextUtils;
import com.bocom.jump.bp.JumpException;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreRuntimeException;


/** 460262 可疑交易进行状态修改 */
public class updateErrorStatusAction extends BaseAction {
	private static final Log logger = LogFactory.getLog(updateErrorStatusAction.class);

    public void process(Context context) throws CoreRuntimeException, JumpException {
		logger.info("updateErrorStatus start!!");
		final String bk = ContextUtils.assertDataHasLengthAndGetNNR(context,ParamKeys.BK, ErrorCodes.EUPS_FIELD_EMPTY);
		Assert.isFalse(bk.equalsIgnoreCase(GDConstants.MANAGE_ORG_445012),  ErrorCodes.EUPS_OPR_TYP_ERR,"非管理机构不能执行本交易");
		/**单位类型*/
		final String AppNm=ContextUtils.assertDataHasLengthAndGetNNR(context, "comNo", ErrorCodes.EUPS_FIELD_EMPTY);
		/**修改的交易状态*/
		final String HTxnS=ContextUtils.assertDataHasLengthAndGetNNR(context, "HTxnSt", ErrorCodes.EUPS_FIELD_EMPTY);
		final String sqn=ContextUtils.assertDataHasLengthAndGetNNR(context, "TckNo", ErrorCodes.EUPS_FIELD_EMPTY);

		/**单位类型转换为单位代码cmo*/		
		//final String CAgtNo=switchCode(AppNm);

		EupsTransJournal journal=new EupsTransJournal();
		journal.setMfmTxnSts(HTxnS);
		journal.setSqn(sqn);
		get(EupsTransJournalRepository.class).update(journal);
		
		logger.info("updateErrorStatus success!!");

	}
    private String switchCode(final String appName){
    	String ret="";
    	try{
    		 ret=SwitchCodeUtils.getCAgtNo(appName);
    	}catch(Exception e){
    		ret="";
    	}
    	return ret;
	
    }
}
