package com.bocom.bbip.gdeupsb.action.transportfee;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GDEupsbTrspFeeInfo;
import com.bocom.bbip.gdeupsb.entity.GDEupsbTrspTxnJnl;
import com.bocom.bbip.gdeupsb.repository.GDEupsbTrspFeeInfoRepository;
import com.bocom.bbip.gdeupsb.repository.GDEupsbTrspTxnJnlRepository;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class AftCancelAction extends BaseAction{
	private final static Log log = LogFactory.getLog(AftCancelAction.class);
	
	@Autowired
	GDEupsbTrspTxnJnlRepository gdEupsbTrspTxnJnlRepository;
	
	@Autowired
	GDEupsbTrspFeeInfoRepository gdEupsbTrspFeeInfoRepository;
	
	public void execute(Context ctx) throws CoreException,CoreRuntimeException{
		log.info("AftCancelAction start......");
		GDEupsbTrspTxnJnl gdEupsbTrspTxnJnl = new GDEupsbTrspTxnJnl();
		if(ctx.getState().equals(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL)){
			gdEupsbTrspTxnJnl = BeanUtils.toObject(ctx.getDataMap(), GDEupsbTrspTxnJnl.class);
			gdEupsbTrspTxnJnlRepository.update(gdEupsbTrspTxnJnl);
//			 UPDATE rbfbtxnbok444
//	           SET    RvsLog='%s',RvsDat='%s',RvsNod='%s',RvsTlr='%s',RvsTck='%s',Status='2'
//	           WHERE  BrNo='%s' and ThdKey='%s' and Status='0'
			//更新退费状态
			GDEupsbTrspFeeInfo gdEupsbTrspFeeInfo = new GDEupsbTrspFeeInfo();
			gdEupsbTrspFeeInfo.setRvsLog(ctx.getData(GDParamKeys.SQN).toString());
//			System.out.println(ctx.getData(GDParamKeys.ACT_DAT).toString());
			
//			gdEupsbTrspFeeInfo.setRvsDat(DateUtils.parse(ctx.getData(GDParamKeys.ACT_DAT).toString(),DateUtils.STYLE_SIMPLE_DATE));
			gdEupsbTrspFeeInfo.setRvsDat(DateUtils.parse("2015-02-02"));
			gdEupsbTrspFeeInfo.setRvsNod(ctx.getData(GDParamKeys.NOD_NO).toString());
			gdEupsbTrspFeeInfo.setRvsTlr(ctx.getData(GDParamKeys.TLR_ID).toString());
			gdEupsbTrspFeeInfo.setRvsTck(ctx.getData(GDParamKeys.TCK_NO).toString());
			gdEupsbTrspFeeInfo.setStatus(GDConstants.TF);
			gdEupsbTrspFeeInfo.setThdKey(ctx.getData(GDParamKeys.THD_KEY).toString());
			gdEupsbTrspFeeInfoRepository.updateCancelSt(gdEupsbTrspFeeInfo);
			
		}else if(ctx.getState().equals(BPState.BUSINESS_PROCESSNIG_STATE_OVERTIME)){
			gdEupsbTrspTxnJnl = BeanUtils.toObject(ctx.getDataMap(), GDEupsbTrspTxnJnl.class);
			gdEupsbTrspTxnJnlRepository.update(gdEupsbTrspTxnJnl);
			ctx.setState("regTransRollBackFlag");
		}else if(ctx.getState().equals(BPState.BUSINESS_PROCESSNIG_STATE_FAIL)){
			gdEupsbTrspTxnJnl = BeanUtils.toObject(ctx.getDataMap(), GDEupsbTrspTxnJnl.class);
			gdEupsbTrspTxnJnlRepository.update(gdEupsbTrspTxnJnl);
			throw new CoreRuntimeException(ErrorCodes.TRANSACTION_ERROR_COMM_ERROR);
		}else{
			gdEupsbTrspTxnJnl = BeanUtils.toObject(ctx.getDataMap(), GDEupsbTrspTxnJnl.class);
			gdEupsbTrspTxnJnlRepository.update(gdEupsbTrspTxnJnl);
			throw new CoreRuntimeException(ErrorCodes.TRANSACTION_ERROR_OTHER_ERROR);
		}
		
	}
	

}
