package com.bocom.bbip.gdeupsb.action.tbc;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GdEupsTransJournal;
import com.bocom.bbip.gdeupsb.repository.GdEupsTransJournalRepository;
import com.bocom.bbip.gdeupsb.utils.CodeSwitchUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

/**
 * 银行查询广东烟草公司清算金额
 * 
 * @version 1.0.0 
 * DateTime 2015-02-26
 * @author GuiLin.Li
 * 
 */
public class QryClearAccountAction extends BaseAction {

    @Autowired
    GdEupsTransJournalRepository transJournalRepository;

    
    @Override
    public void execute(Context context) throws CoreException {
        log.info("QryClearAccount Action start!");
        context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
        String cAgtNo = CodeSwitchUtils.codeGenerator("GDYC_DPTID",  context.getData("dptId").toString());
        if (null == cAgtNo) {
            cAgtNo ="4410000560";
        }
        //检查清算分行与选择清算单位是否一致
        String bankNo = context.getData(ParamKeys.BK).toString().substring(2,5);
        String dptId = cAgtNo.substring(0,3);
        if (!bankNo.equals(dptId)) {
            context.setData(ParamKeys.RSP_CDE, "460299");
            context.setData(ParamKeys.RSP_MSG, "清算单位非本分行!");
//            return;
            throw new CoreException((String) context.getData(GDParamKeys.RSP_MSG));
        }
        //查询烟草公司清算信息
        GdEupsTransJournal transJournal = new GdEupsTransJournal();
        Date date = DateUtils.parse(context.getData("txnDat").toString(),DateUtils.STYLE_yyyyMMdd);
        transJournal.setTxnDte(date);
        transJournal.setComNo(cAgtNo);
        List<Map<String, Object>> resultMap = transJournalRepository.qryClearAccount(transJournal);
         if ("0"== resultMap.get(0).get("totCnt")) {
             context.setData(ParamKeys.RSP_CDE, "331012");
             context.setData(ParamKeys.RSP_MSG,"没有清算数据");
//             return;
             throw new CoreException((String) context.getData(GDParamKeys.RSP_MSG));
         }
        context.setData("sumAmt", resultMap.get(0).get("sumAmt"));
        context.setData("totCnt", resultMap.get(0).get("totCnt"));
        
        context.setData(ParamKeys.RSP_CDE, Constants.RESPONSE_CODE_SUCC);
        context.setData(ParamKeys.RSP_MSG, Constants.RESPONSE_MSG);
        context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
    }

}
