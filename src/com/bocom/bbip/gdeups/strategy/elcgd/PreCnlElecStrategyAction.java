package com.bocom.bbip.gdeups.strategy.elcgd;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsTransJournal;
import com.bocom.bbip.eups.repository.EupsTransJournalRepository;
import com.bocom.bbip.gdeups.common.GDConstants;
import com.bocom.bbip.gdeups.common.GDErrorCodes;
import com.bocom.bbip.gdeups.common.GDParamKeys;
import com.bocom.bbip.gdeups.utils.CodeSwitchUtils;
import com.bocom.bbip.utils.CollectionUtils;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
import com.bocom.jump.bp.core.Executable;

/**
 * 抹帐
 * 
 * @author qc.w
 *
 */
public class PreCnlElecStrategyAction implements Executable{

	private final static Logger log = LoggerFactory.getLogger(PreCnlElecStrategyAction.class);
	
	@Autowired
	EupsTransJournalRepository eupsTransJournalRepository;
	
	
	@Override
	public void execute(Context context) throws CoreException, CoreRuntimeException {
		log.info("PreCnlElecStrategyAction start!..");
		
		String dptTyp=context.getData(GDParamKeys.GZ_ELE_DPT_TYP);  
		String comNo=CodeSwitchUtils.codeGenerator("eleTable", dptTyp);
		
//		String comNo=context.getData(ParamKeys.COMPANY_NO);
		String bk=context.getData(ParamKeys.BK);
		
		//授权原因码并校验是否已授权
		String authTlr=context.getData(ParamKeys.AUTH_TLR_ID);
		 if(StringUtils.isEmpty(authTlr)){
			 throw new CoreException(ErrorCodes.EUPS_CANCEL_CHECK_AUTH_FAIL);
		 }
		 context.setData(GDConstants.AUTH_REASON, "EFE000");// 授权原因码 EFE000
		
		 //校验凭单号是否存在，此处用THD_SUB_CUS_NO作为凭单号
		 String tckNo=context.getData(ParamKeys.THD_SUB_CUS_NO);
		 if(StringUtils.isEmpty(tckNo)){
			 throw new CoreException(GDErrorCodes.EUPS_ELE_GZ_TCK_ERROR);
		 }
		 else{
			 EupsTransJournal eupsTransJournal=new EupsTransJournal();
			 eupsTransJournal.setThdSubCusNo(tckNo);
			 eupsTransJournal.setComNo(comNo);
			 eupsTransJournal.setTxnDte(new Date());
			 eupsTransJournal.setBk(bk);
			 List<EupsTransJournal> resultInf=eupsTransJournalRepository.find(eupsTransJournal);
			 if(CollectionUtils.isNotEmpty(resultInf)){
				 EupsTransJournal oldJnlInf=resultInf.get(0);
				 if(Constants.TRADE_STATUS_CANCEL_SUCCESS.equals(oldJnlInf.getTxnSts())){
					 throw new CoreException(GDErrorCodes.EUPS_ELE_GZ_JNL_ALREADY_CANCLE);   //已抹帐
				 }
			 }else{
				 throw new CoreException(ErrorCodes.BIZ_ERROR_CANCEL_PAYMENT_ORIGINAL_PAYMENT_JOURNAL_NOT_FOUND);   //已抹帐
			 }
			 
		 }
		 //TODO：清算信息表生成，校验清算时间是否为当日，若是当日则不允许继续交易（已清算,不能抹帐）
		 
		 
		 
		 
	}
}
