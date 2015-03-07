package com.bocom.bbip.gdeupsb.action.tbc;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.entity.EupsThdTranCtlInfo;
import com.bocom.bbip.eups.repository.EupsThdTranCtlInfoRepository;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.utils.DateUtils;
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
        context.setData("devId", context.getData("DEV_ID"));
        context.setData("teller", context.getData("TELLER"));

        String comNo =context.getData("dptId").toString();
        EupsThdTranCtlInfo resultThdTranCtlInfo = get(EupsThdTranCtlInfoRepository.class).findOne(comNo);
        if (resultThdTranCtlInfo == null) {
            context.setData(GDParamKeys.RSP_CDE,"9999");
            context.setData(GDParamKeys.RSP_MSG,"数据不存在!");
            return;
        } 
        if (resultThdTranCtlInfo.getTxnCtlSts().equals(Constants.TXN_CTL_STS_SIGNOUT)) {
            context.setData(GDParamKeys.RSP_CDE,"9999");
            context.setData(GDParamKeys.RSP_MSG,"第三方渠道已签退!");
            return;
        } else if (resultThdTranCtlInfo.getTxnCtlSts().equals(Constants.TXN_CTL_STS_CHECKBILL_ING)) {
            context.setData(GDParamKeys.RSP_CDE,"9999");
            context.setData(GDParamKeys.RSP_MSG,"不是签退时间不允许第三方签退!");
            return;
        } else {
            try {
                EupsThdTranCtlInfo eupsThdTranCtlInfo = new EupsThdTranCtlInfo();
                eupsThdTranCtlInfo.setTxnCtlSts(Constants.TXN_CTL_STS_SIGNOUT);
                eupsThdTranCtlInfo.setTxnDte(DateUtils.parse(context.getData("txnTme").toString(),DateUtils.STYLE_yyyyMMddHHmmss));
                eupsThdTranCtlInfo.setComNo(comNo);
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
