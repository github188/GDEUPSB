package com.bocom.bbip.gdeupsb.action.tbc;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.action.common.OperateFTPAction;
import com.bocom.bbip.eups.action.common.OperateFileAction;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsThdFtpConfig;
import com.bocom.bbip.eups.repository.EupsThdFtpConfigRepository;
import com.bocom.bbip.eups.repository.EupsThdTranCtlInfoRepository;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GdEupsTransJournal;
import com.bocom.bbip.gdeupsb.entity.GdTbcBasInf;
import com.bocom.bbip.gdeupsb.repository.GdEupsTransJournalRepository;
import com.bocom.bbip.gdeupsb.repository.GdTbcBasInfRepository;
import com.bocom.bbip.gdeupsb.utils.CodeSwitchUtils;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.CollectionUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

public class CheckThdFileToBkAction extends BaseAction {
   
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
	    @Autowired
	    GdTbcBasInfRepository tbcBasInfRepository;
	    
    @Override
     public void execute(Context context) throws CoreException {
    	 log.info("CheckFileToThirdAction start!");
         context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
         context.setData("txnTme", DateUtils.parse(context.getData("TRAN_TIME").toString(), DateUtils.STYLE_yyyyMMddHHmmss));
         context.setData("dptId", context.getData("DPT_ID"));
         //上面公共报文头，下面报文体
         context.setData("oLogNo", context.getData("BANK_SEQ"));
         context.setData(ParamKeys.TXN_DATE, DateUtils.parse(context.getData("TRADE_DATE").toString(), DateUtils.STYLE_yyyyMMdd));
         
         context.setData("devId", context.getData("DEV_ID"));
         context.setData("teller", context.getData("TELLER"));

         String cAgtNo = CodeSwitchUtils.codeGenerator("GDYC_DPTID",  context.getData("dptId").toString());
         if (null == cAgtNo) {
             cAgtNo ="4410000560";
         }
         context.setData("cAgtNo", cAgtNo);
         
         GdTbcBasInf resultTbcBasInfo = tbcBasInfRepository.findOne(context.getData("dptId").toString());
         if (resultTbcBasInfo == null) {
             throw new CoreException(ErrorCodes.THD_CHL_NOT_FOUND);
         } 
         if (resultTbcBasInfo.getSigSts().equals(Constants.TXN_CTL_STS_SIGNOUT)) {
             throw new CoreException(ErrorCodes.THD_CHL_ALDEAY_SIGN_OUT);
         } else if (resultTbcBasInfo.getSigSts().equals(Constants.TXN_CTL_STS_CHECKBILL_ING)) {
             throw new CoreException(ErrorCodes.THD_CHL_SIGNIN_NOT_ALLOWWED);
         }
         //生成文件及FTP上传数据准备
         String dateString = DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMdd);
         context.setData("datFil","dat/tbc/jh_"+context.getData("dptId").toString()+"_"+ dateString +".xml");
         context.setData("datFilNam", "jh_"+context.getData("dptId").toString()+"_"+ dateString +".xml");
         EupsThdFtpConfig eupsThdFtpConfig =  eupsThdFtpConfigRepository.findOne("tbcCheckFile");
         String locFileName = context.getData("datFilNam").toString();
         eupsThdFtpConfig.setLocFleNme(locFileName);
        // eupsThdFtpConfig.setFtpDir(context.getData("datFil").toString());没有链接第三方的真实服务器//TODO
         eupsThdFtpConfig.setRmtFleNme(locFileName);

         GdEupsTransJournal transJournal = new GdEupsTransJournal();
         transJournal.setTxnDte(DateUtils.parse(context.getData("TRADE_DATE").toString(), DateUtils.STYLE_yyyyMMdd));
         transJournal.setComNo(cAgtNo);
         transJournal.setSqn(context.getData("oLogNo").toString());
         List<Map<String, Object>>resultList =transJournalRepository.findTbcTransJournal(transJournal);
         if (CollectionUtils.isEmpty(resultList)){
             throw new CoreException("无此交易信息");
         }
         Map<String, Object> fileMap = new HashMap<String, Object>();
         Map<String, Object> fileHeader = new HashMap<String, Object>();
         Map<String, Object> fileBottom = new HashMap<String, Object>();
         
         fileHeader.put("top","<?xml version='1.0' encoding='UTF-8'?>\n<DLMAPS>\n<PUB>\n");
         fileHeader.put("TRADE_ID", "<TRADE_ID>8918</TRADE_ID>\n");
         fileHeader.put("TRAN_TIME", "<TRAN_TIME>"+context.getData("TRADE_DATE").toString()+"</TRAN_TIME>\n");
         fileHeader.put("BANK_ID", "<BANK_ID>"+context.getData("BANK_ID")+"</BANK_ID>\n");
         fileHeader.put("DPT_ID", "<DPT_ID>"+context.getData("DPT_ID")+"</DPT_ID>\n");
         fileHeader.put("TRADE_SEQ", "<TRADE_SEQ>"+context.getData("oLogNo").toString()+"</TRADE_SEQ>\n");
         fileHeader.put("APP_TYPE", "<APP_TYPE>"+context.getData("APP_TYPE").toString()+"</APP_TYPE>\n");
         fileHeader.put("pubEnd","</PUB>\n<OUT>\n");
         fileHeader.put("RET_CODE", "<RET_CODE>000000</RET_CODE>\n");
         fileHeader.put("MSG", "<MSG>交易成功</MSG>\n<RE>\n");
         fileMap.put("tops", fileHeader);
         GdEupsTransJournal eupsTransJournal = new GdEupsTransJournal();
         eupsTransJournal.setTxnDte( DateUtils.parse(context.getData("TRADE_DATE").toString(), DateUtils.STYLE_yyyyMMdd));
         eupsTransJournal.setComNo(cAgtNo);
         eupsTransJournal.setSqn(context.getData("oLogNo").toString());
         List<GdEupsTransJournal> transJournalList =transJournalRepository.findTbcTransJournalDetails(eupsTransJournal);
         fileMap.put(ParamKeys.EUPS_FILE_DETAIL, BeanUtils.toMaps(transJournalList));
         fileBottom.put("REEND", "\n</RE>\n<TOTAL>\n");
         fileBottom.put("DEV_ID", "<DEV_ID>"+context.getData("devId")+"</DEV_ID>\n");
         fileBottom.put("Teller", "<Teller>"+context.getData("teller") +"</Teller>\n");
         fileBottom.put("SUCC_COUNT", "<SUCC_COUNT>"+resultList.get(0).get("TOTSUM").toString()+"</SUCC_COUNT>\n");
         BigDecimal length=new BigDecimal(resultList.get(0).get("TOTAMT").toString());
         length=length.divide(new BigDecimal("100"));
 		 System.out.println("》》》》》》》》》》》》length《《《《《《《《《"+length);
         fileBottom.put("SUCC_AMT", "<SUCC_AMT>"+length.toString()+"</SUCC_AMT>\n</TOTAL>\n</OUT>\n</DLMAPS>");
         fileMap.put("bottom", fileBottom);
         try {
             operateFile.createCheckFile(eupsThdFtpConfig, "tbcCheckFile", locFileName, fileMap);
         } catch (Exception e){
             context.setData(GDParamKeys.RSP_CDE,"9999");
             context.setData(GDParamKeys.RSP_MSG,"生成对账文件失败！");
             throw new CoreException("生成对账文件失败！");
         }
         try {
             operateFTPAction.putCheckFile(eupsThdFtpConfig);
         } catch (Exception e){
             context.setData(GDParamKeys.RSP_CDE,"9999");
             context.setData(GDParamKeys.RSP_MSG,"上传对账文件失败！");
             throw new CoreException("上传对账文件失败！");
         }
         context.setData(GDParamKeys.RSP_CDE,Constants.RESPONSE_CODE_SUCC);
         context.setData(GDParamKeys.RSP_MSG,Constants.RESPONSE_MSG);
         context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
     }
}
