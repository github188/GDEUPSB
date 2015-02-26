package com.bocom.bbip.gdeupsb.action.plpd;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsThdBaseInfo;
import com.bocom.bbip.eups.repository.EupsThdBaseInfoRepository;
import com.bocom.bbip.eups.repository.EupsThdTranCtlInfoRepository;
import com.bocom.bbip.eups.spi.service.batch.BatchAcpService;
import com.bocom.bbip.eups.spi.vo.PrepareBatchAcpDomain;
import com.bocom.bbip.file.Marshaller;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GdPlpdBatchTmp;
import com.bocom.bbip.gdeupsb.repository.GDEupsBatchConsoleInfoRepository;
import com.bocom.bbip.gdeupsb.repository.GdPlpdBatchTmpRepository;
import com.bocom.bbip.gdeupsb.strategy.elcgd.AftCnlBnkSglDealStrategyAction;
import com.bocom.bbip.utils.FileUtils;
import com.bocom.jump.bp.JumpException;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

/**
 * 个人代扣系统-数据准备
 * Date 2015-01-23
 * @author GuiLin.Li
 * @version 1.0.0
 */
public class PlpdBathFileDealAction implements BatchAcpService {

    private final static Logger log = LoggerFactory.getLogger(AftCnlBnkSglDealStrategyAction.class);
    @Autowired
    EupsThdTranCtlInfoRepository thdTranCtlInfoRepository;
    @Autowired
    GDEupsBatchConsoleInfoRepository batchConsoleInfoRepository;
    @Autowired
    EupsThdBaseInfoRepository thdBaseInfoRepository;
    @Autowired
    Marshaller marshaller;
    @Autowired
    GdPlpdBatchTmpRepository plpdBatchTmpRepository;
    @Autowired
    BBIPPublicService publicService;
    @SuppressWarnings("unchecked")
    @Override
    public void prepareBatchDeal(PrepareBatchAcpDomain arg0, Context context) throws CoreException {
        String batNo=context.getData("batNo");
        log.info("BathFileDealAction start!..");
        List <GdPlpdBatchTmp> payDetailLst = new ArrayList<GdPlpdBatchTmp>();
        String comNo = context.getData("cAgtNo").toString();
        /**  第三方文件名*/
        String fileName = context.getData("dskNam").toString();

        context.setData(ParamKeys.RSP_CDE, Constants.RESPONSE_CODE_SUCC);
        EupsThdBaseInfo thdBaseInfo = thdBaseInfoRepository.findOne(comNo);
        if (null != thdBaseInfo) {
            context.setData("bBusTyp",thdBaseInfo.getEupsBusTyp());
            context.setData("crpNam",thdBaseInfo.getComNme());
        }
        //上锁
        publicService.tryLock(fileName, (long)0, (long)10);
      
        String localFileName=fileName+"."+context.getData(ParamKeys.BK);
        //  磁盘拷贝
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
        Resource resource = new FileSystemResource(objFile);
        Map<String, List<Map<String, Object>>> map= new HashMap<String, List<Map<String,Object>>>();
        //文件解析
        try {
            map= marshaller.unmarshal("plpdBatDeatil", resource, Map.class);
        } catch (JumpException e) {
            e.printStackTrace();
        }
        
        List<Map<String, Object>> parseMap = (List<Map<String, Object>>) map.get("detail");  //文件体
        // List<Map<String, Object>> parseMap = operateFile.pareseFile(eupsThdFtpInf, "eleGzBatFmt"); // 解析只有detail文件
        for (Map<String, Object> orgMap : parseMap) {
            GdPlpdBatchTmp batchTem = new GdPlpdBatchTmp();
            batchTem.setSqn(context.getData(ParamKeys.SEQUENCE).toString());
            batchTem.setCusAc(orgMap.get("cusAct").toString());
            batchTem.setDel("-");
            batchTem.setTxnAmt(orgMap.get("txnAmt").toString());
            batchTem.setLoaAct(orgMap.get("loaAct").toString());
            batchTem.setLoaNo(orgMap.get("loaNo").toString()); 
            batchTem.setStlAct(orgMap.get("stlAct").toString());
            batchTem.setCapAmt(orgMap.get("capAmt").toString());
            batchTem.setTxnFlg(orgMap.get("txnFlg").toString());
            //batchTem.setTxnStg(orgMap.get("txnStg").toString());
            batchTem.setRsvFld1(batNo); //备用字段1 表示批次号；
            plpdBatchTmpRepository.insert(batchTem);
            payDetailLst.add(batchTem);
        }
      //解锁
        publicService.unlock(batNo);
        context.setVariable(GDParamKeys.COM_BATCH_AGT_FILE_NAME, localFileName);
        context.setVariable(GDParamKeys.COM_BATCH_AGT_FILE_MAP, payDetailLst);
    }
}
