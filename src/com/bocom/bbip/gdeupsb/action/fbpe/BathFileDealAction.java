package com.bocom.bbip.gdeupsb.action.fbpe;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsBatchConsoleInfo;
import com.bocom.bbip.eups.entity.EupsBatchPayEntity;
import com.bocom.bbip.eups.entity.EupsThdTranCtlInfo;
import com.bocom.bbip.eups.repository.EupsBatchConsoleInfoRepository;
import com.bocom.bbip.eups.repository.EupsThdTranCtlInfoRepository;
import com.bocom.bbip.eups.spi.service.batch.BatchAcpService;
import com.bocom.bbip.eups.spi.vo.PrepareBatchAcpDomain;
import com.bocom.bbip.file.Marshaller;
import com.bocom.bbip.gdeupsb.strategy.elcgd.AftCnlBnkSglDealStrategyAction;
import com.bocom.bbip.utils.CollectionUtils;
import com.bocom.bbip.utils.FileUtils;
import com.bocom.bbip.utils.NumberUtils;
import com.bocom.jump.bp.JumpException;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

/**
 * 佛山文本批量
 * Date 2015-01-23
 * @author GuiLin.Li
 * @version 1.0.0
 */
public class BathFileDealAction implements BatchAcpService {

    private final static Logger log = LoggerFactory.getLogger(AftCnlBnkSglDealStrategyAction.class);
    @Autowired
    EupsThdTranCtlInfoRepository thdTranCtlInfoRepository;
    @Autowired
    EupsBatchConsoleInfoRepository batchConsoleInfoRepository;
    @Autowired
    Marshaller marshaller;
    @SuppressWarnings("unchecked")
    @Override
    public List<EupsBatchPayEntity> prepareBatchDeal(PrepareBatchAcpDomain preparebatchacpdomain, Context context)
            throws CoreException {
        log.info("BathFileDealAction start!..");
        List <EupsBatchPayEntity> payDetailLst = new ArrayList<EupsBatchPayEntity>();
        String comNo = context.getData("cAgtNo").toString();
        // 检查签到签退
        EupsThdTranCtlInfo eupsThdTranCtlInfo = thdTranCtlInfoRepository.findOne(comNo);
        if (!eupsThdTranCtlInfo.isTxnCtlStsSignin()) {
            throw new CoreException(ErrorCodes.THD_CHL_TRADE_NOT_ALLOWWED);
        }
        /**  第三方文件名*/
        String fileName = context.getData("dskNam").toString();
        //检查批次是否已录入
        EupsBatchConsoleInfo batchInfo = new EupsBatchConsoleInfo();
        batchInfo.setFleNme(fileName);
        batchInfo.setExeDte(new Date());
        List<EupsBatchConsoleInfo> batchList = batchConsoleInfoRepository.find(batchInfo);
        if (CollectionUtils.isNotEmpty(batchList)) {
            context.setData(ParamKeys.RSP_CDE, "481299");
            context.setData(ParamKeys.RSP_MSG, "该批次已录入，批次号为"+batchList.get(0).getBatNo()+"!");
            return null;
        }
        context.setData(ParamKeys.RSP_CDE, Constants.RESPONSE_CODE_SUCC);
       
        /*  <!--获取业务类型和单位名称  -->
            <Exec func="PUB:ReadRecord">
                <Arg name="SqlCmd" value="GetBatInf"></Arg>
            </Exec>*/
        /*   //交易上锁
        //TODO AplCls ？？？
        String recKey = context.getData("aplCls")+":"+context.getData("txnCod")+":"+fileName;
        
        context.setData(ParamKeys.WS_TRANS_CODE,"GetChk");
    
        Result ret = get(BBIPPublicService.class).tryLock( recKey, (long) 0,(long) 30);
        int status = ret.getStatus();
        if (status != 0) {
            log.info("交易并发，请稍后在做");
            context.setData(ParamKeys.RSP_CDE,"329999");
            context.setData(ParamKeys.RSP_MSG,"交易并发，请稍后在做 !!");
            throw new CoreException("交易并发，请稍后在做 ");
        }*/
        
        String localFileName=fileName+"."+context.getData(ParamKeys.BK);
        // @PARA.RcvMod 为0 磁盘拷贝
        String srcFilName= "dat/term/recv/"+fileName;
        String objFilName ="dat/fbp/"+localFileName;
       
        File srcFil =new File(srcFilName);
        File objFil =new File(objFilName);
        
        try {
            FileUtils.copyFile(srcFil, objFil);
        } catch (IOException e) {
            log.info("cope File Error"+ e);
            context.setData(ParamKeys.RSP_CDE,"478007");
            context.setData(ParamKeys.RSP_MSG,"拷贝文件失败");
            return null;
        }
       //++++++++++++++++++++++++++++++++++++++++++   +++++++++++++++
      //自行实现解析文件
      //Resource resource = new FileSystemResource(TransferUtils.resolveFilePath(eupsThdFtpInf.getLocDir().trim(), eupsThdFtpInf.getLocFleNme().trim()));
        Resource resource = new FileSystemResource(objFil);
        Map<String,Object> map = new HashMap<String, Object>(); 
        if(comNo.equals("tv")) {
            try {
                map= marshaller.unmarshal("tvFbpeBatFmt", resource, Map.class);
            } catch (JumpException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else if (comNo.equals("gas")) {
            //根据单位编号判断解析文件格式   mob ,tel等
        }

//        Map<String,Object> orgHeadMap=(Map<String, Object>) map.get("header");
        List<Map<String, Object>> parseMap = (List<Map<String, Object>>) map.get("detail");  //文件体
       // List<Map<String, Object>> parseMap = operateFile.pareseFile(eupsThdFtpInf, "eleGzBatFmt"); // 解析只有detail文件
        
        for (Map<String, Object> orgMap : parseMap) {
            EupsBatchPayEntity batchPayDtl = new EupsBatchPayEntity();
            batchPayDtl.setCusAc((String) orgMap.get("cusAc")); // 客户帐号
            BigDecimal txnAmt = NumberUtils.centToYuan((String) orgMap.get("tTxnAmt")); // 交易金额
            batchPayDtl.setTxnAmt(txnAmt);
            batchPayDtl.setAgtSrvCusId((String) orgMap.get("thdCusNo")); // 第三方客户标志
            payDetailLst.add(batchPayDtl);
        }
        context.setData(ParamKeys.EUPS_BATCH_PAY_ENTITY_LIST, payDetailLst);

        
        
        
        
        
        
        
        
        
        
        
        return null;
    }
}
