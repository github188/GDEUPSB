package com.bocom.bbip.gdeupsb.action.tbc;

import java.util.Date;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsThdTranCtlInfo;
import com.bocom.bbip.eups.repository.EupsThdTranCtlInfoRepository;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
/**
 * 烟草方签退  8911
 * Date 2015-1-12
 * @author GuiLin.Li
 * @version 1.0.0
 */
public class SignOutAction  extends BaseAction {
    
    @Override
    public void execute(Context context) throws CoreException {
        log.info("SignOutAction Action start!");
        context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
        //转换
        context.setData("txnTme", context.getData("TRAN_TIME"));
        context.setData("bk", context.getData("BANK_ID"));
        context.setData("dptId", context.getData("DPT_ID"));
        //TODO;以此确定comNo 现在测试直接传的是comNo
        context.setData("DevId", context.getData("DEV_ID"));
        context.setData("teller", context.getData("TELLER"));
        
        EupsThdTranCtlInfo resultThdTranCtlInfo = get(EupsThdTranCtlInfoRepository.class).findOne(context.getData(ParamKeys.COMPANY_NO).toString());
        if (resultThdTranCtlInfo == null) {
            throw new CoreException(ErrorCodes.THD_CHL_NOT_FOUND);
        } 
        if (resultThdTranCtlInfo.getTxnCtlSts().equals(Constants.TXN_CTL_STS_SIGNOUT)) {
            throw new CoreException(ErrorCodes.THD_CHL_ALDEAY_SIGN_OUT);
        } else if (resultThdTranCtlInfo.getTxnCtlSts().equals(Constants.TXN_CTL_STS_CHECKBILL_ING)) {
            throw new CoreException(ErrorCodes.THD_CHL_SIGNIN_NOT_ALLOWWED);
        } else {
            try {
                EupsThdTranCtlInfo eupsThdTranCtlInfo = new EupsThdTranCtlInfo();
                eupsThdTranCtlInfo.setTxnCtlSts(Constants.TXN_CTL_STS_SIGNOUT);
                eupsThdTranCtlInfo.setTxnDte(new Date());
                eupsThdTranCtlInfo.setComNo(context.getData(ParamKeys.COMPANY_NO).toString());
                get (EupsThdTranCtlInfoRepository.class).update(eupsThdTranCtlInfo);
            } catch (Exception e) {
                throw new CoreException("数据库处理错误 !"+ e);
            }
        }
        context.setData(GDParamKeys.RSP_CDE,Constants.RESPONSE_CODE_SUCC);
        context.setData(GDParamKeys.RSP_MSG,Constants.RESPONSE_MSG);
        context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
    }

}
