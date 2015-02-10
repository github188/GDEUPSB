package com.bocom.bbip.gdeupsb.action.tbc;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsThdFtpConfig;
import com.bocom.bbip.eups.entity.EupsThdTranCtlInfo;
import com.bocom.bbip.eups.repository.EupsThdTranCtlInfoRepository;
import com.bocom.bbip.gdeupsb.entity.GdEupsTransJournal;
import com.bocom.bbip.gdeupsb.repository.GdEupsTransJournalRepository;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.CollectionUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

/**
 * 烟草公司发出的第三方对账
 * @version 1.0.0
 * @author GuiLin.Li
 */
public class CheckFileToThirdAction extends BaseAction {

    @Override
    public void execute(Context context) throws CoreException {
        log.info("CheckFileToThirdAction start!");
        context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
        context.setData("txnTme", context.getData("TRAN_TIME"));
        context.setData("bk", context.getData("BANK_ID"));
        context.setData("dptId", context.getData("DPT_ID"));
        //上面公共报文头，下面报文体
        context.setData("oLogNo", context.getData("BANK_SEQ"));
        context.setData("txnDte", context.getData("TRADE_DATE"));
        //TODO;以此确定comNo 现在测试直接传的是comNo
        context.setData("DevId", context.getData("DEV_ID"));
        context.setData("teller", context.getData("TELLER"));

        //检查用户状态
        EupsThdTranCtlInfo thdTranCtlInfo = get(EupsThdTranCtlInfoRepository.class).findOne(context.getData(ParamKeys.COMPANY_NO).toString());
        if (thdTranCtlInfo == null) {
            throw new CoreException(ErrorCodes.THD_CHL_NOT_FOUND);
        } 
        if (thdTranCtlInfo.getTxnCtlSts().equals(Constants.TXN_CTL_STS_SIGNOUT)) {
            throw new CoreException(ErrorCodes.THD_CHL_ALDEAY_SIGN_OUT);
        }
        //生成文件及FTP上传数据准备
        context.setData("datFil","dat/tbc/jh_"+context.getData("dptId").toString()+",_,"+context.getData("txnDte").toString()+".xml");
        context.setData("datFilNam", "jh_"+context.getData("dptId").toString()+",_,"+context.getData("txnDte").toString()+".xml");
        EupsThdFtpConfig eupsThdFtpConfig = context.getData(ParamKeys.CONSOLE_THD_FTP_CONFIG_LIST);
        String locFileName = context.getData("datFilNam").toString();
        eupsThdFtpConfig.setLocFleNme(locFileName);
        eupsThdFtpConfig.setRmtFleNme(locFileName);
        
        
        String sqn = context.getData("oLogNo").toString();
        if ("000000".equals(sqn)) {
            GdEupsTransJournal transJournal = new GdEupsTransJournal();
            Date date = DateUtils.parse(context.getData("txnDte").toString(),DateUtils.STYLE_yyyyMMdd);
            transJournal.setTxnDte((date));
            transJournal.setComNo(context.getData(ParamKeys.COMPANY_NO).toString());
            transJournal.setMfmTxnSts("S");
            transJournal.setMfmRspCde("SC0000");
            transJournal.setTxnCde("483805");
            
            List<GdEupsTransJournal> eupsTransJournals = get(GdEupsTransJournalRepository.class).findTbcTransJournals(transJournal);
            if (CollectionUtils.isEmpty(eupsTransJournals)){
                throw new CoreException("无此交易信息！");
            }
            Map<String, Object> map = new HashMap<String, Object>();
            map.put(ParamKeys.EUPS_FILE_DETAIL, BeanUtils.toMaps(eupsTransJournals));

        } else {
            GdEupsTransJournal transJournal = new GdEupsTransJournal();
            Date date = DateUtils.parse(context.getData("txnDte").toString(),DateUtils.STYLE_yyyyMMdd);
            transJournal.setTxnDte((date));
            transJournal.setComNo(context.getData(ParamKeys.COMPANY_NO).toString());
            transJournal.setThdSqn(sqn);
            transJournal.setMfmTxnSts("S");
            transJournal.setMfmRspCde("SC0000");
            transJournal.setTxnCde("483805");

            List<GdEupsTransJournal> eupsTransJournals = get(GdEupsTransJournalRepository.class).findTbcTransJnls(transJournal);
            if (CollectionUtils.isEmpty(eupsTransJournals)){
                throw new CoreException("无此交易信息！");
            }
            Map<String, Object> map = new HashMap<String, Object>();
            map.put(ParamKeys.EUPS_FILE_DETAIL, BeanUtils.toMaps(eupsTransJournals));
        }
    }
}
