package com.bocom.bbip.gdeupsb.action.plpd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.eups.action.common.OperateFTPAction;
import com.bocom.bbip.eups.action.common.OperateFileAction;
import com.bocom.bbip.eups.entity.EupsThdFtpConfig;
import com.bocom.bbip.eups.repository.EupsThdFtpConfigRepository;
import com.bocom.bbip.eups.spi.service.batch.AfterBatchAcpService;
import com.bocom.bbip.eups.spi.vo.AfterBatchAcpDomain;
import com.bocom.bbip.gdeupsb.entity.GdPlpdBatchTmp;
import com.bocom.bbip.gdeupsb.repository.GDEupsBatchConsoleInfoRepository;
import com.bocom.bbip.gdeupsb.repository.GdPlpdBatchTmpRepository;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

/**
 * 个人代扣系统批扣结果处理
 * @version 1.0.0
 * @author Guilin.Li
 * Date 2015-02-12
 */
public class PlpdBatchResultDealAction implements AfterBatchAcpService {

    @Autowired
    GDEupsBatchConsoleInfoRepository eupsBatchConsoleInfoRepository;

    @Autowired
    GdPlpdBatchTmpRepository fileRepository;

    @Autowired
    EupsThdFtpConfigRepository eupsThdFtpConfigRepository;

    @Autowired
    OperateFileAction operateFile;

    @Autowired
    OperateFTPAction operateFTP;

    private final static Logger log = LoggerFactory.getLogger(PlpdBatchResultDealAction.class);

    @Override
    public void afterBatchDeal(AfterBatchAcpDomain arg0, Context context) throws CoreException {
        log.info("BatchFbpeResultDealAction Start! ");
        String batNo = (String) context.getData("dskNo");
        Map<String, Object> resultMap = new HashMap<String, Object>();
        // 生成返回明细信息
        List<Map<String, Object>> detailList = new ArrayList<Map<String, Object>>();
        GdPlpdBatchTmp fileBatchTmp = new GdPlpdBatchTmp();
        fileBatchTmp.setRsvFld1(batNo);
        List<GdPlpdBatchTmp> batchDetailList = fileRepository.find(fileBatchTmp);
        for (GdPlpdBatchTmp batchDetail : batchDetailList) {
            Map<String, Object> detailMap = new HashMap<String, Object>();
            detailMap.put("cusAct", batchDetail.getCusAc());
            detailMap.put("del", batchDetail.getDel());
            detailMap.put("txnAmt", batchDetail.getTxnAmt());
            String txnFlg = batchDetail.getTxnFlg();
            detailMap.put("txnFlg", txnFlg);
           /* if (txnFlg.equals("Y")){
                detailMap.put("txnStg","Y");
            }else {
                detailMap.put("txnStg", "E");
            }*/
            detailList.add(detailMap);
        }
        resultMap.put("detail", detailList);

      
        EupsThdFtpConfig eupsThdFtpConfig = eupsThdFtpConfigRepository.findOne("fbpeBathReturnFmt");

        String fileName = context.getData("filNam")+"_"+batNo+"_"+ context.getData("bk");
        eupsThdFtpConfig.setLocDir("dat/fbp/"+fileName);
        eupsThdFtpConfig.setFtpDir("dat/term/send/"+fileName);
        eupsThdFtpConfig.setLocFleNme(fileName);

        // 生成文件
        operateFile.createCheckFile(eupsThdFtpConfig, "plpdBatchResultDeatil", fileName, resultMap);

        // 将生成的文件上传至指定服务器
        eupsThdFtpConfig.setLocFleNme(fileName);
        eupsThdFtpConfig.setRmtFleNme(fileName);
        operateFTP.putCheckFile(eupsThdFtpConfig);
    }

}
