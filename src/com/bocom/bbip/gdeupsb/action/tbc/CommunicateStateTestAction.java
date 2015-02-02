package com.bocom.bbip.gdeupsb.action.tbc;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

/**
 * 通讯状态测试
 * Date 2015-1-13
 * @author GuiLin.Li
 * @version 1.0.0
 */
public class CommunicateStateTestAction extends BaseAction {
   
    @Override
     public void execute(Context context) throws CoreException {
         log.info("CommunicateStateTest Action start!");
         context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
         context.setData(GDParamKeys.RSP_CDE,Constants.RESPONSE_CODE_SUCC);
         context.setData(GDParamKeys.RSP_MSG,Constants.RESPONSE_MSG);
         context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
    }
}
