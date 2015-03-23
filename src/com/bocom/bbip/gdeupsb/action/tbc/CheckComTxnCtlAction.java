package com.bocom.bbip.gdeupsb.action.tbc;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.utils.CommonUtil;
import com.bocom.bbip.gdeupsb.entity.GdTbcBasInf;
import com.bocom.bbip.gdeupsb.repository.GdTbcBasInfRepository;
import com.bocom.bbip.utils.Assert;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
/**
 * 烟草公司签到检查
 * @version 1.0.0
 * @date 2015-03-05
 * @author GuiLin.Li
 */
public class CheckComTxnCtlAction  extends BaseAction {

    /**
     * 判断是否第三方交易控制是否可发起正常业务,第三方签到状态可发起正常业务.
     */
    public void checkThdTxnCtlNormal(Context context) throws Exception {
        context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
        GdTbcBasInf info = qryComTxnCtlInf(context);
        if (!info.getSigSts().equals("0")) {
            throw new CoreException(ErrorCodes.THD_CHL_TRADE_NOT_ALLOWWED);
        }
        context.setData(ParamKeys.CONSOLE_THD_TRANS_CONTROL_INFO_LIST, info);
        //添加密钥到context中，用于表达式获取
        //主密钥
        if(null != info.getComKey()){
            context.setData(ParamKeys.CONSOLE_THD_MAIN_KEY, info.getComKey());
        }

        context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
    }

    /**
     * 判断是否第三方交易控制是否可发起签到业务 默认只有在第三方对账完成后才可发起签到交易
     */
    public void checkThdTxnCtlSignIn(Context context) throws Exception {
        GdTbcBasInf info = qryComTxnCtlInf(context);
        if (info.getSigSts().equals("0")) {
            throw new CoreException(ErrorCodes.THD_CHL_SIGNIN_NOT_ALLOWWED);
        }
    }

    /**
     * 判断是否第三方交易控制是否可发起对账业务 当日对账允许在非签到和非对账中状态（即对账失败和签退状态）进行 隔日对账允许在非对账中状态进行
     */
    public void checkThdTxnCtlCheck(Context context) throws Exception {
        GdTbcBasInf info = qryComTxnCtlInf(context);
        String state = info.getSigSts();
        if (!Constants.TXN_CTL_STS_CHECKBILL_ING.equals(state)
            && ((context.getData(ParamKeys.TXN_DATE).equals(context.getData(ParamKeys.RCN_DATE)) && !Constants.TXN_CTL_STS_SIGNIN.equals(state)) || !context.getData(
                ParamKeys.TXN_DATE).equals(context.getData(ParamKeys.RCN_DATE)))) {
            throw new CoreException(ErrorCodes.THD_CHL_CHECK_NOT_ALLOWWED);
        }
    }

    /**
     * 判断是否第三方交易控制是否可发起签退业务 第三方签到状态可签退
     */
    public void checkThdTxnCtlSignOut(Context context) throws Exception {
        GdTbcBasInf info = qryComTxnCtlInf(context);
        if (info.getSigSts().equals("1")) {
            throw new CoreException(ErrorCodes.THD_CHL_SIGNOUT_NOT_ALLOWWED);
        }
    }

    /**
     * 查询第三方控制信息
     */
    private GdTbcBasInf qryComTxnCtlInf(Context context) throws Exception {
        Assert.hasLengthInData(context, ParamKeys.EUPS_BUSS_TYPE, ErrorCodes.EUPS_BUS_TYP_ISEMPTY);
        CommonUtil.isEmpty("交易日期", context.getData(ParamKeys.TXN_DTE));
        GdTbcBasInf resultTbcBasInfo = get(GdTbcBasInfRepository.class).findOne(context.getData("dptId").toString());
        
        Assert.isNotNull(resultTbcBasInfo, ErrorCodes.EUPS_THD_TRANS_CTLINFO_NOTEXIST);
        return resultTbcBasInfo;
    }
}
