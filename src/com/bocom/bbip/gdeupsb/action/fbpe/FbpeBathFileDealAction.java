package com.bocom.bbip.gdeupsb.action.fbpe;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mvel2.sh.command.file.FileCommandSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsThdBaseInfo;
import com.bocom.bbip.eups.entity.EupsThdTranCtlInfo;
import com.bocom.bbip.eups.repository.EupsThdBaseInfoRepository;
import com.bocom.bbip.eups.repository.EupsThdTranCtlInfoRepository;
import com.bocom.bbip.eups.spi.service.batch.BatchAcpService;
import com.bocom.bbip.eups.spi.vo.PrepareBatchAcpDomain;
import com.bocom.bbip.file.Marshaller;
import com.bocom.bbip.gdeupsb.action.common.BatchFileCommon;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GdFbpeFileBatchTmp;
import com.bocom.bbip.gdeupsb.repository.GDEupsBatchConsoleInfoRepository;
import com.bocom.bbip.gdeupsb.repository.GdFbpeFileBatchTmpRepository;
import com.bocom.bbip.utils.FileUtils;
import com.bocom.jump.bp.JumpException;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

/**
 * 佛山文本批量文件准备
 * Date 2015-01-23
 * @author GuiLin.Li
 * @version 1.0.0
 */
public class FbpeBathFileDealAction extends BaseAction implements BatchAcpService {

    private final static Logger log = LoggerFactory.getLogger(FbpeBathFileDealAction.class);
    @Autowired
    EupsThdTranCtlInfoRepository thdTranCtlInfoRepository;
    @Autowired
    GDEupsBatchConsoleInfoRepository batchConsoleInfoRepository;
    @Autowired
    EupsThdBaseInfoRepository thdBaseInfoRepository;
    @Autowired
    Marshaller marshaller;
    @Autowired
    GdFbpeFileBatchTmpRepository fileBatchTmpRepository;
    @SuppressWarnings("unchecked")
    @Override
    public void prepareBatchDeal(PrepareBatchAcpDomain arg0, Context context) throws CoreException {

        log.info("BathFileDealAction start!..");
        get(BatchFileCommon.class).BeforeBatchProcess(context);
        List <GdFbpeFileBatchTmp> payDetailLst = new ArrayList<GdFbpeFileBatchTmp>();
        String comNo = context.getData("comNo").toString();
        // 检查签到签退
        EupsThdTranCtlInfo eupsThdTranCtlInfo = thdTranCtlInfoRepository.findOne(comNo);
        if (!eupsThdTranCtlInfo.isTxnCtlStsSignin()) {
            throw new CoreException(ErrorCodes.THD_CHL_TRADE_NOT_ALLOWWED);
        }
        /**  第三方文件名*/
        String fileName = context.getData("fleNme").toString();
        context.setData(ParamKeys.RSP_CDE, Constants.RESPONSE_CODE_SUCC);
      
        String localFileName=fileName+".dec";
        // @PARA.RcvMod 为0 磁盘拷贝
        String srcFilName= "dat/term/recv/"+fileName;
        String objFilName ="dat/fbp/"+localFileName;
       
        File srcFile =new File(srcFilName);
        File objFile =new File(objFilName);
        
        try {
            FileUtils.copyFile(srcFile, objFile);
        } catch (IOException e) {
            log.info("cope File Error"+ e);
            context.setData(ParamKeys.RSP_CDE,"478007");
            context.setData(ParamKeys.RSP_MSG,"拷贝文件失败");
        }
      //自行实现解析文件
      //Resource resource = new FileSystemResource(TransferUtils.resolveFilePath(eupsThdFtpInf.getLocDir().trim(), eupsThdFtpInf.getLocFleNme().trim()));
        Resource resource = new FileSystemResource(objFile);
        Map<String, List<Map<String, Object>>> map= new HashMap<String, List<Map<String,Object>>>();

        //根据单位编号寻找格式文件解析
        if(comNo.equals("4460000011")) {
            try { 
                map= marshaller.unmarshal("tvFbpeBatFmt", resource, Map.class);
            } catch (JumpException e) {
                e.printStackTrace();
            }
        } else if (comNo.equals("4460002194")) {
            try {
                map= marshaller.unmarshal("gasFbpeBatFmt", resource, Map.class);
            } catch (JumpException e) {
                e.printStackTrace();
            }
        }  else if (comNo.equals("4460000010")) {
            try {
                map= marshaller.unmarshal("mobFbpeBatFmt", resource, Map.class);
            } catch (JumpException e) {
                e.printStackTrace();
            }
        } else if (comNo.equals("4460000014")) {
            try {
                map= marshaller.unmarshal("telFbpeBatFmt", resource, Map.class);
            } catch (JumpException e) {
                e.printStackTrace();
            }
        }

        // Map<String,Object> orgHeadMap=(Map<String, Object>) map.get("header");
        List<Map<String, Object>> parseMap = (List<Map<String, Object>>) map.get("detail");  //文件体
        // List<Map<String, Object>> parseMap = operateFile.pareseFile(eupsThdFtpInf, "eleGzBatFmt"); // 解析只有detail文件
        for (Map<String, Object> orgMap : parseMap) {
            GdFbpeFileBatchTmp batchTem = new GdFbpeFileBatchTmp();
            batchTem.setSqn(context.getData(ParamKeys.SEQUENCE).toString());
            batchTem.setAccAmt(orgMap.get("accAmt").toString());
            batchTem.setAccNo(orgMap.get("accNo").toString());
            batchTem.setActNo(orgMap.get("actNo").toString());
            batchTem.setRsvFld6(orgMap.get("smsSqn").toString()); //SMS交易流水号
            batchTem.setTxnNo(orgMap.get("txnNo").toString());
            batchTem.setOrgCde(orgMap.get("orgCde").toString());
            batchTem.setTlrNo(orgMap.get("tlrNo").toString());
            batchTem.setTxnTim(orgMap.get("txnTme").toString());
            batchTem.setCusNo(orgMap.get("cusNo").toString());
            batchTem.setCusAc(orgMap.get("cusAc").toString());
            batchTem.setCusNam(orgMap.get("cusNam").toString());
            batchTem.setTxnAmt(orgMap.get("txnAmt").toString());
            batchTem.setCosMon(orgMap.get("months").toString());
            batchTem.setRsvFld1(orgMap.get("rsvFld1").toString());
            batchTem.setBankNo(orgMap.get("bankNo").toString());
            batchTem.setBankNam(orgMap.get("bankNam").toString());
            
            fileBatchTmpRepository.insert(batchTem);
            payDetailLst.add(batchTem);
        }
        context.setVariable(GDParamKeys.COM_BATCH_AGT_FILE_NAME, localFileName);
        context.setVariable(GDParamKeys.COM_BATCH_AGT_FILE_MAP, payDetailLst);
    }


}
