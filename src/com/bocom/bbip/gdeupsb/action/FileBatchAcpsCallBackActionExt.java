package com.bocom.bbip.gdeupsb.action;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.action.eupsbatch.FileLineProcessCallbackImpl;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsBatchConsoleInfo;
import com.bocom.bbip.eups.entity.EupsThdBaseInfo;
import com.bocom.bbip.eups.entity.EupsThdFtpConfig;
import com.bocom.bbip.eups.repository.EupsBatchConsoleInfoRepository;
import com.bocom.bbip.eups.repository.EupsThdBaseInfoRepository;
import com.bocom.bbip.eups.repository.EupsThdFtpConfigRepository;
import com.bocom.bbip.file.fmt.FileMarshaller;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.JumpException;
import com.bocom.jump.bp.SystemConfig;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

public class FileBatchAcpsCallBackActionExt extends BaseAction
{
  public void callBackFunction(Context context)
    throws JumpException
  {
    this.log.info("========Enter in FileBatchAcpsCallBackAction>>>>>>>> callBackFunction...");
    context.setData("batNo", "150319TPO300300002");
    String batNo = (String)context.getData("batNo");
    this.log.info("========>>>>>>>>Acp Return BatNo:" + batNo);
    EupsBatchConsoleInfo eupsBatchConsoleInfo = (EupsBatchConsoleInfo)((EupsBatchConsoleInfoRepository)get(EupsBatchConsoleInfoRepository.class)).findOne(batNo);
    if (eupsBatchConsoleInfo == null) {
      throw new CoreException("BBIP0004EU0706", "代收付返回的批次号不存在");
    }
    String comNo = eupsBatchConsoleInfo.getComNo();
    EupsThdBaseInfo eupsThdBaseInfo = (EupsThdBaseInfo)((EupsThdBaseInfoRepository)get(EupsThdBaseInfoRepository.class)).findOne(comNo);
    if (eupsThdBaseInfo != null) {
      context.setData("eupsBusTyp", eupsThdBaseInfo.getEupsBusTyp());
    }

    Integer totCnt = eupsBatchConsoleInfo.getTotCnt();
    BigDecimal totAmt = eupsBatchConsoleInfo.getTotAmt();
    context.setData("totCnt", totCnt);
    context.setData("totAmt", totAmt);
    String txnOrgCde = eupsBatchConsoleInfo.getTxnOrgCde();
    String txnTlr = eupsBatchConsoleInfo.getTxnTlr();

    String systemCode = ((SystemConfig)get(SystemConfig.class)).getSystemCode();

    String pstDte = DateUtils.format(((BBIPPublicService)get(BBIPPublicService.class)).getAcDate(), "yyyyMMdd");

    EupsThdFtpConfig eupsThdFtpConfig = (EupsThdFtpConfig)((EupsThdFtpConfigRepository)get(EupsThdFtpConfigRepository.class)).findOne(ParamKeys.FTPID_BATCH_PAY_FILE_TO_ACP);
    StringBuffer rmtWayDir = new StringBuffer();
    rmtWayDir.append(eupsThdFtpConfig.getRmtWay()).append(systemCode).append("/").append(txnOrgCde).append("/");
    rmtWayDir.append(txnTlr).append("/").append(pstDte).append("/").append(batNo).append(".result");
    this.log.info("========>>>>>>the toAcpPath is ：{" + rmtWayDir.toString() + "}");

    this.log.info(">>>>>>>>>>>>Starting Analysis File<<<<<<<<<<<<<<<<<<<<<");
    FileMarshaller marshaller = (FileMarshaller)get(FileMarshaller.class);
    Resource resource = new ClassPathResource("config/fmt/FileFormatConfig.xml");
    marshaller.setResources(new Resource[] { resource });
    marshaller.unmarshal(ParamKeys.EUPS_BATCH_PAY_AGTS_FILE, new FileSystemResource(rmtWayDir.toString()), new FileLineProcessCallbackImpl(), context);

    Integer sucTotCnt = (Integer)context.getData("sucTotCnt");
    BigDecimal sucTotAmt = (BigDecimal)context.getData("sucTotAmt");

    
    //TODO:不能使用double!...............
    Integer falTotCnt = Integer.valueOf(totCnt.intValue() - sucTotCnt.intValue());
    double falTotAmtdouble = totAmt.doubleValue() - sucTotAmt.doubleValue();
    BigDecimal falTotAmt = new BigDecimal(falTotAmtdouble);

    eupsBatchConsoleInfo.setSucTotCnt(sucTotCnt);
    eupsBatchConsoleInfo.setSucTotAmt(sucTotAmt);
    eupsBatchConsoleInfo.setFalTotCnt(falTotCnt);
    eupsBatchConsoleInfo.setFalTotAmt(falTotAmt);
    eupsBatchConsoleInfo.setExeDte(new Date());
    eupsBatchConsoleInfo.setBatSts("S");

    context.setDataMap(BeanUtils.toMap(eupsBatchConsoleInfo));
    context.setData(ParamKeys.THD_BAT_NO, eupsBatchConsoleInfo.getRsvFld1());
    this.log.info("========>>>>>>start update EupsBatchConsoleInfo ...");
    ((EupsBatchConsoleInfoRepository)get(EupsBatchConsoleInfoRepository.class)).update(eupsBatchConsoleInfo);
    this.log.info(">>>>>>>>>>>>DataMap is:{" + context.getDataMap() + "}");
    this.log.info("========>>>>>> end update EupsBatchConsoleInfo ...");
  }
}