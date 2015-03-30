package com.bocom.bbip.gdeupsb.action.lot;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.CollectionUtils;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.adaptor.ThirdPartyAdaptor;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDErrorCodes;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GdLotSysCfg;
import com.bocom.bbip.gdeupsb.repository.GdLotSysCfgRepository;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

/**
 * 福彩用户登录 485401
 * 
 * @version 1.0.0 Date 2015-01-26
 * @author GuiLin.Li
 */
public class LoginAction extends BaseAction {
    // dealId 运营商 141

    @Override
    public void execute(Context context) throws CoreException {
        log.info("LoginAction Start !!");
        context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
        context.setData("nodNo", "441800");
        List<GdLotSysCfg> lotSysCfgInfos = get(GdLotSysCfgRepository.class).findSysCfg();
        if (CollectionUtils.isEmpty(lotSysCfgInfos)) {
            context.setData("msgTyp", Constants.RESPONSE_TYPE_FAIL);
            context.setData(ParamKeys.RSP_CDE, "LOT999");
            context.setData(ParamKeys.RSP_MSG, "查询参数表失败!!!");
            return;
        }
        // 向福彩中心发出系统角色登录
        context.setData("eupsBusTyp", "LOTR01");
        context.setData("action", "212");
        context.setData("version", "");
        context.setData("sent_time", DateUtils.format(new Date(), DateUtils.STYLE_FULL));
        context.setData("user", lotSysCfgInfos.get(0).getUsrPam());
        context.setData("pwd", lotSysCfgInfos.get(0).getUsrPas());
        Map<String, Object> resultMap = new HashMap<String, Object>();// 申请当前期号，奖期信息下载
        // 福彩系统登录
        resultMap = get(ThirdPartyAdaptor.class).trade(context);
        String responseCode = resultMap.get(GDParamKeys.LOT_RESULT_CODE).toString();
        if (BPState.isBPStateOvertime(context)) {
            throw new CoreException(ErrorCodes.TRANSACTION_ERROR_TIMEOUT);
        } else if (!"0".equals(responseCode)) {
            if (StringUtils.isEmpty(responseCode)) {
                // 系统角色失败
                throw new CoreException(GDErrorCodes.EUPS_THD_SYS_ERROR);
            }
            throw new CoreException(GDErrorCodes.EUPS_LOT_LOGIN_ERROR);
        }

        // 系统对时
        context.setData("action", "200");
        Map<String, Object> map = new HashMap<String, Object>();// 申请当前期号，奖期信息下载
        map = get(ThirdPartyAdaptor.class).trade(context);

        String responseCod = map.get(GDParamKeys.LOT_RESULT_CODE).toString();
        if (BPState.isBPStateOvertime(context)) {
            throw new CoreException(ErrorCodes.TRANSACTION_ERROR_TIMEOUT);
        } else if (!"0".equals(responseCod)) {
            if (StringUtils.isEmpty(responseCod)) {
                throw new CoreException(GDErrorCodes.EUPS_THD_SYS_ERROR);
            }
            throw new CoreException(GDErrorCodes.EUPS_LOT_CHECK_TIME_ERROR);
        }

        // 获取时间差函数调用，设置时间差
        String sigTim = context.getData("sent_time");
        String brNo = context.getData("brNo");
        Date lotTime = DateUtils.parse(map.get("sys_time").toString(), DateUtils.STYLE_yyyyMMddHHmmss);
        String lotTim = DateUtils.format(lotTime, DateUtils.STYLE_yyyyMMddHHmmss);
        CommonLotAction commonLotAction = new CommonLotAction();
        String diffTm = commonLotAction.difTime(brNo, lotTim, sigTim);
        context.setData("diffTm", diffTm);
        GdLotSysCfg lotSysCfgInfoInput = new GdLotSysCfg();
        lotSysCfgInfoInput.setDealId(GDConstants.LOT_DEAL_ID);
        lotSysCfgInfoInput.setDiffTm(diffTm);
        lotSysCfgInfoInput.setLclTim(sigTim);
        lotSysCfgInfoInput.setLotTim(lotTim);
        lotSysCfgInfoInput.setSigTim(sigTim);
        try {
            get(GdLotSysCfgRepository.class).update(lotSysCfgInfoInput);
        } catch (Exception e) {
        	 throw new CoreException(GDErrorCodes.EUPS_LOT_UPDATE_SYS_ERROR);
        }
        context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
    }
}
