package com.bocom.bbip.gdeupsb.action.lot;

import java.util.List;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.entity.GdLotCusInf;
import com.bocom.bbip.gdeupsb.repository.GdLotCusInfRepository;
import com.bocom.bbip.utils.CollectionUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;


public class CathecticPreAction extends BaseAction {

    CommonLotAction commonLotAction =new CommonLotAction();
    
    @Override
    public void execute(Context context) throws CoreException {
        log.info("Enter in Cathectic Lot Action... ");
        context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
        
        commonLotAction.GetSysCfg(context);
        context.setData("cTTxnCd", context.getData("tTxnCd"));
        context.setData("tzCod", "TZ0000");
        
        //检查用户状态
        GdLotCusInf lotCusInf = new GdLotCusInf();
        lotCusInf.setStatus("1");
        lotCusInf.setCrdNo(context.getData("crdNo").toString());
        List<GdLotCusInf> lotCusInfs = get(GdLotCusInfRepository.class).find(lotCusInf);
        if (CollectionUtils.isEmpty(lotCusInfs)) {
           context.setData("MsgTyp",Constants.RESPONSE_TYPE_FAIL);
           context.setData(ParamKeys.RSP_CDE,"LOT999");
           context.setData(ParamKeys.RSP_MSG,"客户未注册或状态异常");
           context.setData("tzCod", "TZ9001");
           return;
        }
    }
}
