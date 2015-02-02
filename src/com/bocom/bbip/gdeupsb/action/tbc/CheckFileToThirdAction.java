package com.bocom.bbip.gdeupsb.action.tbc;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.action.common.OperateFTPAction;
import com.bocom.bbip.eups.action.common.OperateFileAction;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsThdFtpConfig;
import com.bocom.bbip.eups.entity.EupsThdTranCtlInfo;
import com.bocom.bbip.eups.entity.EupsTransJournal;
import com.bocom.bbip.eups.repository.EupsThdFtpConfigRepository;
import com.bocom.bbip.eups.repository.EupsThdTranCtlInfoRepository;
import com.bocom.bbip.eups.repository.EupsTransJournalRepository;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
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

    private final String TBC_BACK_FILE_FORNAME = "TBC";
    private final String TBC_BACK_FILE_AFTTYPE = ".tmp";
    private final String TBC_ORGFILE_AFTTYPE = ".txt";

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
        String sqn = context.getData("oLogNo").toString();
        if ("000000".equals(sqn)) {
            EupsTransJournal eupsTransJournal = new EupsTransJournal();
            Date date = DateUtils.parse(context.getData("txnDte").toString(),DateUtils.STYLE_yyyyMMdd);
            eupsTransJournal.setTxnDte(date);
            eupsTransJournal.setComNo(context.getData(ParamKeys.COMPANY_NO).toString());
            eupsTransJournal.setMfmTxnSts("S");
            eupsTransJournal.setMfmRspCde("SC0000");
            eupsTransJournal.setTxnCde("483805");
            List<EupsTransJournal> eupsTransJournals = get(EupsTransJournalRepository.class).find(eupsTransJournal);
            if (CollectionUtils.isEmpty(eupsTransJournals)){
                throw new CoreException("无此交易信息！");
            }

            BigDecimal sumTxnAmt = new BigDecimal(0.0);
            for(EupsTransJournal transJournal:eupsTransJournals) {
                sumTxnAmt = sumTxnAmt.add(transJournal.getTxnAmt());
            }
            context.setData("totSum", eupsTransJournals.size());
            context.setData("totAmt", sumTxnAmt);
            context.setData("datFil","dat/tbc/jh_"+context.getData("dptId").toString()+",_,"+context.getData("txnDte").toString()+".xml");
            context.setData("datFilNam", "jh_"+context.getData("dptId").toString()+",_,"+context.getData("txnDte").toString()+".xml");
            
            // TODO;<!--生成对账明细文件开始-->
            
            getTbcRspFileInfo(context);
        }
        context.setData(GDParamKeys.RSP_CDE,Constants.RESPONSE_CODE_SUCC);
        context.setData(GDParamKeys.RSP_MSG,Constants.RESPONSE_MSG);
        context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
    }

    /**
     * 生成广东烟草文件
     * 
     * @param proDat
     * @param batch
     * @throws CoreException
     */
     public void getTbcRspFileInfo(Context context) throws CoreException {
       /* String proDat = context.getData("proDat").toString().trim();
        List<String> batchList = context.getData("batchList");

        for (String batch : batchList) {
            log.info("makeTbcRspFile start!..proDat=" + proDat + "batch=" + batch);
            Map<String, Object> map = new HashMap<String, Object>();

            // 查询,设置回盘信息头信息
            Map<String, Object> headerMap = null;
            EupsTbcAccountDetail tbcAccountDetail = new EupsTbcAccountDetail();
            tbcAccountDetail.setBatch(batch);
            tbcAccountDetail.setProDat(proDat);

            List<Map<String, Object>> headList = get(EupsTbcAccountDetailRepository.class).findRspFileHead(
                    tbcAccountDetail);
            if (CollectionUtils.isNotEmpty(headList)) {
                headerMap = headList.get(0);
                headerMap.put("PRODAT", proDat);
                headerMap.put("BATCH", batch);
                BigDecimal totAmt = (BigDecimal) headerMap.get("TOTAMT");
                BigDecimal sucAmt = (BigDecimal) headerMap.get("SUCAMT");
                headerMap.put("TOTAMT", totAmt.toString().replace(".", ""));
                headerMap.put("SUCAMT", sucAmt.toString().replace(".", ""));
            } else {
                log.error("get response file head info error!");
                throw new CoreException(ErrorCodes.EUPS_FAIL);
            }
            //获取当日明细
            List<EupsTbcAccountDetail> detailList = get(EupsTbcAccountDetailRepository.class).findRspFileDetail(
                    tbcAccountDetail);
            map.put(ParamKeys.EUPS_FILE_HEADER, headerMap);
            map.put(ParamKeys.EUPS_FILE_DETAIL, BeanUtils.toMaps(detailList));

            makeTbcRspFile(proDat, batch, map);
        }*/
    }

    /**
     * 生成相应文件
     * 
     * @param proDat
     * @param batch
     * @param map
     * @throws CoreException
     */
    public void makeTbcRspFile(String proDat, String batch, Map<String, Object> map) throws CoreException {
        log.info("makeJlzfRspFile start!..");
        EupsThdFtpConfig eupsThdFtpConfig = get(EupsThdFtpConfigRepository.class).findOne("TBC");
        StringBuffer file = new StringBuffer();
        file.append(TBC_BACK_FILE_FORNAME).append(proDat).append(batch).append(TBC_BACK_FILE_AFTTYPE);
        String fileName = file.toString();
        eupsThdFtpConfig.setLocFleNme(fileName);
        eupsThdFtpConfig.setRmtFleNme(fileName);

        // 生成对账文件到指定路径
        get(OperateFileAction.class).createCheckFile(eupsThdFtpConfig, "tbcRspFileImpFmt", fileName, map);
        log.info("create file end!..");

        try {
            EnCryptFile(eupsThdFtpConfig.getLocDir().replace("/tmp", ""), fileName,
                    fileName.replace(TBC_BACK_FILE_AFTTYPE, TBC_ORGFILE_AFTTYPE));
        } catch (IOException e) {
            log.error("maketbcRspFile error!batch=" + batch);
        }
        eupsThdFtpConfig.setLocFleNme(fileName.replace(TBC_BACK_FILE_AFTTYPE, TBC_ORGFILE_AFTTYPE));
        eupsThdFtpConfig.setRmtFleNme(fileName.replace(TBC_BACK_FILE_AFTTYPE, TBC_ORGFILE_AFTTYPE));

        // 上传到指定服务器
        get(OperateFTPAction.class).putCheckFile(eupsThdFtpConfig);
        log.info("ftp put file end!..");
    }
    
    /**
     * 调用加密程序
     * @param excPath
     * @param srcFile
     * @param objFile
     * @throws IOException
     */
    private Process EnCryptFile(String excPath, String srcFile, String objFile) throws IOException {
        String cmd = excPath + "bin/JlzfDesFile" + " " + excPath + "tmp/" + srcFile + " " + excPath + "tmp/" + objFile + " 0";
        log.info("cmd=" + cmd);
        String[] command = new String[] { "/bin/sh", "-c", cmd };
        Process proc = Runtime.getRuntime().exec(command);
        log.info("en-file success!");
        return proc;
    }
}
