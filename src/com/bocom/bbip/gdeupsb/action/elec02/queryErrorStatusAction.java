package com.bocom.bbip.gdeupsb.action.elec02;
import java.util.List;

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
import com.bocom.bbip.utils.DateUtils;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.JumpException;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreRuntimeException;

/** 460263 对可疑交易进行状态查询 */
public class queryErrorStatusAction extends BaseAction {
	private static final Log logger = LogFactory.getLog(queryErrorStatusAction.class);
	
    public void process(Context context) throws CoreRuntimeException, JumpException {
		logger.info("query start!!");
		final String bk=ContextUtils.assertDataHasLengthAndGetNNR(context, ParamKeys.BK, ErrorCodes.EUPS_FIELD_EMPTY);
		Assert.isTrue(bk.equalsIgnoreCase(GDConstants.MANAGE_ORG_445012), ErrorCodes.EUPS_OPR_TYP_ERR);

		final String TxnDat=ContextUtils.assertDataHasLengthAndGetNNR(context, ParamKeys.TXN_DAT, ErrorCodes.EUPS_FIELD_EMPTY);
		/**单位类型*/
		final String AppNm=ContextUtils.assertDataHasLengthAndGetNNR(context, "AppNm", ErrorCodes.EUPS_FIELD_EMPTY);
		/**会计流水*/
		final String TckNo=ContextUtils.assertDataHasLengthAndGetNNR(context, ParamKeys.SEQ_NO, ErrorCodes.EUPS_FIELD_EMPTY);
		
		final String brNo=ContextUtils.assertDataHasLengthAndGetNNR(context, ParamKeys.BR, ErrorCodes.EUPS_FIELD_EMPTY);

		/** 单位类型转换为单位代码cmo */
		final String CAgtNo = switchCode(AppNm);
		Assert.isFalse(StringUtils.isBlank(CAgtNo), ErrorCodes.EUPS_FIELD_EMPTY);
		/** 检查单位协议是否存在 */
		//TODO：不对！！！！！
		EupsCorpAgent corpAgent=get(EupsCorpAgentRepository.class).findOne(CAgtNo);
		Assert.isFalse(null==corpAgent, ErrorCodes.EUPS_FIELD_EMPTY, "单位协议不存在");
		EupsTransJournal journal = new EupsTransJournal();
		journal.setComNo(CAgtNo);
		journal.setSqn(TckNo);
		journal.setTxnDte(DateUtils.parse(TxnDat));
		List<EupsTransJournal> list=get(EupsTransJournalRepository.class).find(journal);
		Assert.isEmpty(list, ErrorCodes.EUPS_FIELD_EMPTY);
		EupsTransJournal ret=list.get(0);
		context.setData(ParamKeys.TXN_AMT, ret.getTxnAmt());
		context.setData(ParamKeys.THD_CUS_NO, ret.getThdCusNo());
		context.setData(ParamKeys.THD_CUS_NME, ret.getThdCusNme());
		context.setData(ParamKeys.ACT_NO, ret.getCusAc());

		logger.info("query success!!");

	}
private String switchCode(final String appName){
	String ret="";
	try{
		 ret=SwitchCodeUtils.getCAgtNo(appName);
	}catch(Exception e){
	}
	return ret;
}
	

}
