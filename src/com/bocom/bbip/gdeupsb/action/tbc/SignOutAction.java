package com.bocom.bbip.gdeupsb.action.tbc;

import java.util.Date;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.gdeupsb.common.GDErrorCodes;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GdTbcBasInf;
import com.bocom.bbip.gdeupsb.repository.GdTbcBasInfRepository;
import com.bocom.bbip.gdeupsb.utils.CodeSwitchUtils;
import com.bocom.bbip.utils.BeanUtils;
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

        String cAgtNo = CodeSwitchUtils.codeGenerator("GDYC_DPTID",  context.getData("dptId").toString());
        if (null == cAgtNo) {
            cAgtNo ="4410000560";
        }
        context.setData("cAgtNo", cAgtNo);
//        GdTbcBasInf tbcBasInfo = get(GdTbcBasInfRepository.class).findOne(context.getData("dptId").toString());
//        if (tbcBasInfo == null) {
//            context.setData(GDParamKeys.RSP_CDE,"9999");
//            context.setData(GDParamKeys.RSP_MSG,GDErrorCodes.TBC_OFF_NOT_EXIST);
//            throw new CoreException(GDErrorCodes.TBC_OFF_NOT_EXIST);
//        } 
//        if (tbcBasInfo.getSigSts().equals(Constants.TXN_CTL_STS_SIGNOUT)) {
//            context.setData(GDParamKeys.RSP_CDE,"9999");
//            context.setData(GDParamKeys.RSP_MSG,ErrorCodes.THD_CHL_ALDEAY_SIGN_OUT);
//            throw new CoreException(ErrorCodes.THD_CHL_ALDEAY_SIGN_OUT);
//        } else if (tbcBasInfo.getSigSts().equals(Constants.TXN_CTL_STS_CHECKBILL_ING)) {
//            context.setData(GDParamKeys.RSP_CDE,"9999");
//            context.setData(GDParamKeys.RSP_MSG,ErrorCodes.THD_CHL_SIGNIN_NOT_ALLOWWED);
//            throw new CoreException(ErrorCodes.THD_CHL_SIGNIN_NOT_ALLOWWED);
//        } else {
            try {
                GdTbcBasInf putTbcBasInfo = BeanUtils.toObject(context.getDataMap(), GdTbcBasInf.class);
                Date date =DateUtils.parse(context.getData("txnTme").toString().substring(0,8), DateUtils.STYLE_yyyyMMdd);
                putTbcBasInfo.setTranDt(DateUtils.format(date, DateUtils.STYLE_yyyyMMdd));
                putTbcBasInfo.setSigSts(Constants.TXN_CTL_STS_SIGNOUT);
                get (GdTbcBasInfRepository.class).update(putTbcBasInfo);
            } catch (Exception e) {
                throw new CoreException(GDErrorCodes.TBC_DB_ERROR);
            }
 //       }
        context.setData(GDParamKeys.RSP_CDE,Constants.RESPONSE_CODE_SUCC);
        context.setData(GDParamKeys.RSP_MSG,Constants.RESPONSE_MSG);
        context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
    }
}
