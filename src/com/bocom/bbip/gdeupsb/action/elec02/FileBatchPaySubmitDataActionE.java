package com.bocom.bbip.gdeupsb.action.elec02;

import java.io.File;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsActSysPara;
import com.bocom.bbip.eups.entity.EupsBatchConsoleInfo;
import com.bocom.bbip.eups.entity.EupsThdFtpConfig;
import com.bocom.bbip.eups.repository.EupsActSysParaRepository;
import com.bocom.bbip.eups.repository.EupsBatchConsoleInfoRepository;
import com.bocom.bbip.eups.repository.EupsThdFtpConfigRepository;
import com.bocom.bbip.service.BGSPServiceAccessObject;
import com.bocom.bbip.service.Result;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.CollectionUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.SystemConfig;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class FileBatchPaySubmitDataActionE extends BaseAction {
	public void execute(Context context)
    throws CoreException, CoreRuntimeException
  {
    this.log.info("=======>>>>>>>>> start FileBatchPaySubmitDataAction....");
    context.setState("BP_STATE_FAIL");

    //checkBatchFileIsOrNotExist(context);
    String comNoAcps = "";
    String comNo = (String)context.getData("comNo");
    String busTyp = (String)context.getData("busTyp");
    EupsActSysPara eupsActSysPara = new EupsActSysPara();
    eupsActSysPara.setActSysTyp("0");
    eupsActSysPara.setComNo(comNo);
    List resultList = ((EupsActSysParaRepository)get(EupsActSysParaRepository.class)).find(eupsActSysPara);
    if (CollectionUtils.isNotEmpty(resultList)) {
      comNoAcps = ((EupsActSysPara)resultList.get(0)).getSplNo();

      context.setData("comNoAcps", comNoAcps);
      this.log.info("========>>>>>>the comNo is ：{" + comNo + "}");
      this.log.info("========>>>>>>the comNoAcps is ：{" + comNoAcps + "}");
    } else {
      throw new CoreException("BBIP0004EU0137");
    }
    insertBatchConsoleInfo(context);

    context.setState("BP_STATE_NORMAL");
    this.log.info("=======>>>>>>>>>>>> end FileBatchPaySubmitDataAction....");
  }

  private void checkBatchFileIsOrNotExist(Context context)
    throws CoreException
  {
    String comNoAcps = "";
    String comNo = (String)context.getData("comNo");
    String busTyp = (String)context.getData("busTyp");
    EupsActSysPara eupsActSysPara = new EupsActSysPara();
    eupsActSysPara.setActSysTyp("0");
    eupsActSysPara.setComNo(comNo);
    List resultList = ((EupsActSysParaRepository)get(EupsActSysParaRepository.class)).find(eupsActSysPara);
    if (CollectionUtils.isNotEmpty(resultList)) {
      comNoAcps = ((EupsActSysPara)resultList.get(0)).getSplNo();

      context.setData("comNoAcps", comNoAcps);
      this.log.info("========>>>>>>the comNo is ：{" + comNo + "}");
      this.log.info("========>>>>>>the comNoAcps is ：{" + comNoAcps + "}");
    } else {
      throw new CoreException("BBIP0004EU0137");
    }

    String systemCode = ((SystemConfig)get(SystemConfig.class)).getSystemCode();

    String pstDte = DateUtils.format(((BBIPPublicService)get(BBIPPublicService.class)).getAcDate(), "yyyyMMdd");

    EupsThdFtpConfig eupsThdFtpConfig = (EupsThdFtpConfig)((EupsThdFtpConfigRepository)get(EupsThdFtpConfigRepository.class)).findOne(ParamKeys.FTPID_BATCH_PAY_FILE_TO_ACP);
    String toAcpPath = eupsThdFtpConfig.getRmtWay().trim() + systemCode + "/" + context.getData("br").toString() + "/" + context.getData("tlr").toString() + "/" + pstDte + "/";
    this.log.info("========>>>>>>the toAcpPath is ：{" + toAcpPath + "}");

    String fleNme = "BATC" + comNoAcps + busTyp + ".txt";
    context.setData("fleNme", fleNme);
    this.log.info("========>>>>>>the fleNme is ：{" + fleNme + "}");

    File file = new File(toAcpPath + fleNme);
    if (!file.exists())
      throw new CoreException("BBIP0004EU0705");
  }

  private void insertBatchConsoleInfo(Context context)
    throws CoreException
  {
    this.log.info(">>>>>>>>>>>>DataMap is:{" + context.getDataMap() + "}");

    validateBatchData(context);

    Integer totCnt = (Integer)context.getData("totCnt");
    BigDecimal totAmt = (BigDecimal)context.getData("totAmt");
    this.log.info(">>>>>>> totCnt is :{" + totCnt + "},>>>>>>>>>>>>totAmt is :{" + totAmt + "}");

    EupsBatchConsoleInfo eupsBatchConsoleInfo = (EupsBatchConsoleInfo)BeanUtils.toObject(context.getDataMap(), EupsBatchConsoleInfo.class);
    eupsBatchConsoleInfo.setTxnMde("0");
    eupsBatchConsoleInfo.setRapTyp((String)context.getData("busTyp"));
    eupsBatchConsoleInfo.setTxnOrgCde(context.getData("br").toString());
    eupsBatchConsoleInfo.setTxnTlr(context.getData("tlr").toString());
    eupsBatchConsoleInfo.setSubDte(new Date());
    eupsBatchConsoleInfo.setBatSts("U");
    eupsBatchConsoleInfo.setTotAmt(totAmt);
    eupsBatchConsoleInfo.setTotCnt(totCnt);
    eupsBatchConsoleInfo.setRsvFld1((String)context.getData(ParamKeys.THD_BAT_NO));
    this.log.info("========>>>>>>start insert into EupsBatchConsoleInfo");
    ((EupsBatchConsoleInfoRepository)get(EupsBatchConsoleInfoRepository.class)).insert(eupsBatchConsoleInfo);

    context.setData("comNo", context.getData("comNoAcps"));
    context.setData("totNop", totCnt);
    context.setData("totAmt", totAmt);
  }

  private void validateBatchData(Context context)
    throws CoreException
  {
    String comNo = (String)context.getData("comNo");
    String fleNme = (String)context.getData("fleNme");
    String thdBatNo = (String)context.getData(ParamKeys.THD_BAT_NO);

    EupsBatchConsoleInfo eupsBatchConsoleInfo = new EupsBatchConsoleInfo();
    eupsBatchConsoleInfo.setComNo(comNo);
    eupsBatchConsoleInfo.setFleNme(fleNme);
    eupsBatchConsoleInfo.setRsvFld1(thdBatNo);
    List eupsBatchConsoleInfoList = ((EupsBatchConsoleInfoRepository)get(EupsBatchConsoleInfoRepository.class)).findByInfo(eupsBatchConsoleInfo);
    if (CollectionUtils.isNotEmpty(eupsBatchConsoleInfoList))
      throw new CoreException("BBIP0004EU0707", "数据已导入，不能重复提交！");
  }

  public void callHost(Context context)
    throws CoreException
  {
    String hostOpr = "submitAcpBatchData";
    this.log.info("========>>>>>>start call AGTS======AcpCoreService(submitAcpBatchData)..");
    context.setState("BP_STATE_FAIL");
    Map rspMap = new HashMap();
    Result respData = ((BGSPServiceAccessObject)get(BGSPServiceAccessObject.class)).callServiceFlatting(hostOpr, context.getDataMap());
    rspMap = respData.getPayload();
    this.log.info("========the response map: " + rspMap);
    if ((respData.isSuccess()) && (respData.getStatus() == 0)) {
      this.log.info("Call acp AcpCoreService(submitAcpBatchData) success!!!!");
      context.setData("batNo", rspMap.get("batNo"));
      context.setData("batPssSts", rspMap.get("batPssSts"));
      context.setState("BP_STATE_NORMAL");
    } else {
      if (-10 == respData.getStatus()) {
        this.log.info("Call acp AcpCoreService(submitAcpBatchData)- send error !!!!");
        context.setState("BP_STATE_TRANS_FAIL");
        throw new CoreException("BBIP0004EU0042");
      }if (-2 == respData.getStatus()) {
        this.log.info("Call acp AcpCoreService(submitAcpBatchData) system error !!!! ");
        context.setState("BP_STATE_SYSTEM_ERROR");
        throw new CoreException("BBIP0004EU0042");
      }if (-1 == respData.getStatus()) {
        this.log.info("Call acp AcpCoreService(submitAcpBatchData) Timeout !!!! ");
        context.setState("BP_STATE_OVERTIME");
        throw new CoreException("BBIP0004EU0044");
      }if (3 == respData.getStatus()) {
        String responseCode = (String)rspMap.get("responseCode");
        String responseMsg = (String)rspMap.get("responseMessage");
        context.setState("BP_STATE_FAIL");
        this.log.info("Call acp AcpCoreService(submitAcpBatchData) Failed!!!! ");
        throw new CoreException(responseCode, responseMsg);
      }
      context.setState("BP_STATE_UNKOWN_FAIL");
      this.log.info("Call acp AcpCoreService(submitAcpBatchData) Other error!!!!");
      throw new CoreException("BBIP0004EU0045");
    }

    this.log.info(">>>>>>>>>>>>DataMap is:{" + context.getDataMap() + "}");
  }

  public void updateBatchConsoleState(Context context)
  {
    this.log.info("========>>>>>>start updateBatchConsoleState ...");
    String batNo = (String)context.getData("batNo");
    String state = context.getState();
    EupsBatchConsoleInfo eupsBatchConsoleInfo = new EupsBatchConsoleInfo();
    eupsBatchConsoleInfo.setBatNo(batNo);
    if ("BP_STATE_NORMAL".equals(state))
      eupsBatchConsoleInfo.setBatSts("S");
    else if ("BP_STATE_FAIL".equals(state))
      eupsBatchConsoleInfo.setBatSts("F");
    else if ("BP_STATE_OVERTIME".equals(state))
      eupsBatchConsoleInfo.setBatSts("T");
    else {
      eupsBatchConsoleInfo.setBatSts("F");
    }
    ((EupsBatchConsoleInfoRepository)get(EupsBatchConsoleInfoRepository.class)).update(eupsBatchConsoleInfo);
  }
}
