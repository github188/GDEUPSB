package com.bocom.bbip.gdeupsb.action.transportfee;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.common.GDErrorCodes;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GDEupsbTrspFeeInfo;
import com.bocom.bbip.gdeupsb.entity.GDEupsbTrspTxnJnl;
import com.bocom.bbip.gdeupsb.repository.GDEupsbTrspFeeInfoRepository;
import com.bocom.bbip.gdeupsb.repository.GDEupsbTrspTxnJnlRepository;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.CollectionUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class PrePrintInvCancelAction extends BaseAction{
	private final static Log log = LogFactory.getLog(PrePrintInvCancelAction.class);
	
	@Autowired
	GDEupsbTrspTxnJnlRepository gdEupsbTrspTxnJnlRepository;
	@Autowired
	GDEupsbTrspFeeInfoRepository gdEupsbTrspFeeInfoRepository;
	
	public void execute(Context ctx) throws CoreException,CoreRuntimeException{
		log.info("PrePrintInvCancelAction start......");
		
//		<Set>ToDay=$ActDat</Set>
//        <Set>OLogNo=$LogNo</Set>
//		 TLogNo='%s'
//		         </Sentence>
//		         <Fields>OLogNo|</Fields>
		Date today = ctx.getData(GDParamKeys.ACT_DAT);
		GDEupsbTrspTxnJnl gdEupsbTrspTxnJnl = new GDEupsbTrspTxnJnl();
		gdEupsbTrspTxnJnl.setTlogNo(ctx.getData(GDParamKeys.SQN).toString());
		List<GDEupsbTrspTxnJnl> txnJnlList = gdEupsbTrspTxnJnlRepository.find(gdEupsbTrspTxnJnl);
		ctx.setDataMap(BeanUtils.toMap(txnJnlList.get(0)));

//		TODO:!!!!!!!!    if(!DateUtils.isSameDate((Date)ctx.getData(GDParamKeys.ACT_DAT),today)){
//			ctx.setData(ParamKeys.RSP_MSG, "该笔交易帐务日期与此时帐务日期不等，不能交易");
//			throw new CoreRuntimeException(GDErrorCodes.DATE_ERROR);
//		}
//         <!--检查是否已对账-->
		GDEupsbTrspFeeInfo gdEupsbTrspFeeInfo = new GDEupsbTrspFeeInfo();
		gdEupsbTrspFeeInfo.setTlogNo(ctx.getData("oldTxnSqn").toString());
		List<GDEupsbTrspFeeInfo> feeInfoList = gdEupsbTrspFeeInfoRepository.find(gdEupsbTrspFeeInfo);
		if(!CollectionUtils.isEmpty(feeInfoList)){
			if(!"0".equals(feeInfoList.get(0).getChkFlg())){
				ctx.setData(ParamKeys.RSP_MSG, "该笔交易已经对账");
				throw new CoreRuntimeException(ErrorCodes.EUPS_CANCEL_CHECK_AUTH_FAIL);
			}else{
				ctx.setData(GDParamKeys.CHK_FLG, feeInfoList.get(0).getChkFlg());
			}
			
			
		}


	}

}
