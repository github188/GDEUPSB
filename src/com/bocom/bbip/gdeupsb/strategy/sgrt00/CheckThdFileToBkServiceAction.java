package com.bocom.bbip.gdeupsb.strategy.sgrt00;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.eups.action.common.OperateFTPAction;
import com.bocom.bbip.eups.action.common.OperateFileAction;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsThdFtpConfig;
import com.bocom.bbip.eups.entity.EupsThdTranCtlInfo;
import com.bocom.bbip.eups.repository.EupsThdFtpConfigRepository;
import com.bocom.bbip.eups.repository.EupsThdTranCtlInfoRepository;
import com.bocom.bbip.eups.spi.service.check.CheckThdFileToBkService;
import com.bocom.bbip.eups.spi.vo.CheckDomain;
import com.bocom.bbip.gdeupsb.entity.GdEupsTransJournal;
import com.bocom.bbip.gdeupsb.repository.GdEupsTransJournalRepository;
import com.bocom.bbip.gdeupsb.utils.CodeSwitchUtils;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

public class CheckThdFileToBkServiceAction  implements CheckThdFileToBkService {

    private final static Logger log = LoggerFactory.getLogger(CheckThdFileToBkServiceAction.class);
   
    @Autowired
    OperateFileAction operateFile;
    @Autowired
    OperateFTPAction operateFTPAction;
    @Autowired
    EupsThdFtpConfigRepository eupsThdFtpConfigRepository;
    @Autowired
    EupsThdTranCtlInfoRepository thdTranCtlInfoRepository;
    @Autowired
    GdEupsTransJournalRepository transJournalRepository;

    @Override
    public Map<String, Object> checkThdFileToBk(CheckDomain checkdomain, Context context) throws CoreException {
        log.info("CheckFileToThirdAction start!");
        context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
        context.setData("txnTme", DateUtils.parse(context.getData("TRAN_TIME").toString(), DateUtils.STYLE_yyyyMMddHHmmss));
        context.setData("bk", context.getData("BANK_ID"));
        context.setData("dptId", context.getData("DPT_ID"));
        //上面公共报文头，下面报文体
        context.setData("oLogNo", context.getData("BANK_SEQ"));
       // context.setData(ParamKeys.TXN_DATE, DateUtils.parse(context.getData("TRADE_DATE").toString(), DateUtils.STYLE_yyyyMMdd));
        
        context.setData("devId", context.getData("DEV_ID"));
        context.setData("teller", context.getData("TELLER"));

        String cAgtNo = CodeSwitchUtils.codeGenerator("GDYC_DPTID",  context.getData("dptId").toString());
        if (null == cAgtNo) {
            cAgtNo ="4410000560";
        }
        context.setData("cAgtNo", cAgtNo);
        
        //检查用户状态
        EupsThdTranCtlInfo thdTranCtlInfo = thdTranCtlInfoRepository.findOne(cAgtNo);
        if (thdTranCtlInfo == null) {
            throw new CoreException(ErrorCodes.THD_CHL_NOT_FOUND);
        } 
        if (thdTranCtlInfo.getTxnCtlSts().equals(Constants.TXN_CTL_STS_SIGNOUT)) {
            throw new CoreException(ErrorCodes.THD_CHL_ALDEAY_SIGN_OUT);
        }
        //生成文件及FTP上传数据准备
        String dateString = DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMdd);
        context.setData("datFil","dat/tbc/jh_"+context.getData("dptId").toString()+"_"+ dateString +".xml");
        context.setData("datFilNam", "jh_"+context.getData("dptId").toString()+"_"+ dateString +".xml");
        EupsThdFtpConfig eupsThdFtpConfig =  eupsThdFtpConfigRepository.findOne("tbcCheckFile");
        String locFileName = context.getData("datFilNam").toString();
        eupsThdFtpConfig.setLocFleNme(locFileName);
        eupsThdFtpConfig.setFtpDir(context.getData("datFil").toString());
        eupsThdFtpConfig.setRmtFleNme(locFileName);

        GdEupsTransJournal transJournal = new GdEupsTransJournal();
        transJournal.setTxnDte(DateUtils.parse(context.getData("TRADE_DATE").toString(), DateUtils.STYLE_yyyyMMdd));
        transJournal.setComNo(cAgtNo);
        transJournal.setSqn(context.getData("oLogNo").toString());
        List<Map<String, Object>>resultList =transJournalRepository.findTbcTransJournal(transJournal);
        if (null == resultList){
            throw new CoreException("无此交易信息");
        }
        Map<String, Object> fileMap = new HashMap<String, Object>();
        Map<String, Object> fileHeader = new HashMap<String, Object>();
        Map<String, Object> fileBottom = new HashMap<String, Object>();
        
        fileHeader.put("top","<?xml version='1.0' encoding='UTF-8'?>\n<DLMAPS>\n<PUB>\n");
        fileHeader.put("TRADE_ID", "<TRADE_ID>8918</TRADE_ID>\n");
        fileHeader.put("TRAN_TIME", "<TRAN_TIME>"+context.getData("txnDte").toString()+"</TRAN_TIME>\n");
        fileHeader.put("BANK_ID", "<BANK_ID>"+context.getData("BANK_ID")+"</BANK_ID>\n");
        fileHeader.put("DPT_ID", "<DPT_ID>"+context.getData("DPT_ID")+"</DPT_ID>\n");
        fileHeader.put("TRADE_SEQ", "<TRADE_SEQ>"+resultList.get(0).get("tLogNo")+"</TRADE_SEQ>\n");
        fileHeader.put("APP_TYPE", "<APP_TYPE> </APP_TYPE>\n");
        fileHeader.put("pubEnd","</PUB>\n<OUT>\n");
        fileHeader.put("RET_CODE", "<RET_CODE>000000</RET_CODE>\n");
        fileHeader.put("MSG", "<MSG>交易成功</MSG>\n<RE>\n");
        fileMap.put("tops", fileHeader);
        GdEupsTransJournal eupsTransJournal = new GdEupsTransJournal();
        eupsTransJournal.setTxnDte( DateUtils.parse(context.getData("TRADE_DATE").toString(), DateUtils.STYLE_yyyyMMdd));
        eupsTransJournal.setComNo(context.getData(ParamKeys.COMPANY_NO).toString());
        eupsTransJournal.setSqn(context.getData("oLogNo").toString());
        List<GdEupsTransJournal> transJournalList =transJournalRepository.findTbcTransJournalDetails(eupsTransJournal);
        fileMap.put(ParamKeys.EUPS_FILE_DETAIL, BeanUtils.toMaps(transJournalList));
        fileBottom.put("REEND", "\n</RE>\n<TOTAL>\n");
        fileBottom.put("DEV_ID", "<DEV_ID>"+context.getData("devId")+"</DEV_ID>\n");
        fileBottom.put("Teller", "<Teller>"+context.getData("teller") +"</Teller>\n");
        fileBottom.put("SUCC_COUNT", "<SUCC_COUNT>"+resultList.get(0).get("totSum")+"</SUCC_COUNT>\n");
        fileBottom.put("SUCC_AMT", "<SUCC_AMT>"+resultList.get(0).get("totAmt")+"</SUCC_AMT>\n</TOTAL>\n</OUT>\n</DLMAPS>");
        fileMap.put("bottom", fileBottom);
        operateFile.createCheckFile(eupsThdFtpConfig, "tbcCheckFile", locFileName, fileMap);

        operateFTPAction.putCheckFile(eupsThdFtpConfig);
        return null;
    }
}
