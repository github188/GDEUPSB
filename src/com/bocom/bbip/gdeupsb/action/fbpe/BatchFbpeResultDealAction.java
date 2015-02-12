package com.bocom.bbip.gdeupsb.action.fbpe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.eups.action.common.OperateFTPAction;
import com.bocom.bbip.eups.action.common.OperateFileAction;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsThdFtpConfig;
import com.bocom.bbip.eups.repository.EupsThdFtpConfigRepository;
import com.bocom.bbip.eups.spi.service.batch.AfterBatchAcpService;
import com.bocom.bbip.eups.spi.vo.AfterBatchAcpDomain;
import com.bocom.bbip.gdeupsb.entity.GDEupsBatchConsoleInfo;
import com.bocom.bbip.gdeupsb.entity.GdFbpeFileBatchTmp;
import com.bocom.bbip.gdeupsb.repository.GDEupsBatchConsoleInfoRepository;
import com.bocom.bbip.gdeupsb.repository.GdFbpeFileBatchTmpRepository;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

/**
 * 佛山文件批扣-批扣结果处理
 * @version 1.0.0
 * @author Guilin.Li
 * Date 2015-02-12
 */
public class BatchFbpeResultDealAction implements AfterBatchAcpService {

    @Autowired
    GDEupsBatchConsoleInfoRepository eupsBatchConsoleInfoRepository;

    @Autowired
    GdFbpeFileBatchTmpRepository fileRepository;

    @Autowired
    EupsThdFtpConfigRepository eupsThdFtpConfigRepository;

    @Autowired
    OperateFileAction operateFile;

    @Autowired
    OperateFTPAction operateFTP;

    private final static Logger log = LoggerFactory.getLogger(BatchFbpeResultDealAction.class);

  
    @Override
    public void afterBatchDeal(AfterBatchAcpDomain arg0, Context context) throws CoreException {
        log.info("BatchFbpeResultDealAction Start! ");
        String batNo = (String) context.getData("dskNo");
        GDEupsBatchConsoleInfo eupsBatchConsoleInfo = eupsBatchConsoleInfoRepository.findOne(batNo);
        if ("S" !=eupsBatchConsoleInfo.getBatSts()) {
            context.setData(ParamKeys.RSP_CDE, "481299");
            context.setData(ParamKeys.RSP_MSG, "批次未处理完毕，请稍后!");
        }
        Map<String, Object> resultMap = new HashMap<String, Object>();
        // 生成返回头信息
        if("mob" ==eupsBatchConsoleInfo.getComNo()) { // 只有佛山移动有返回报文头
            Map<String, Object> resultMapHead = new HashMap<String, Object>();
            resultMapHead.put("totCnt", eupsBatchConsoleInfo.getTotCnt());
            resultMapHead.put("sTotAmt", eupsBatchConsoleInfo.getSucTotAmt());
            resultMapHead.put("fTotAmt", eupsBatchConsoleInfo.getFalTotAmt());
            
            resultMap.put(ParamKeys.EUPS_FILE_HEADER, resultMapHead);
        }
        // 生成返回明细信息
        List<Map<String, Object>> detailList = new ArrayList<Map<String, Object>>();
        GdFbpeFileBatchTmp fileBatchTmp = new GdFbpeFileBatchTmp();
        fileBatchTmp.setRsvFld8(batNo);
        List<GdFbpeFileBatchTmp> batchDetailList = fileRepository.find(fileBatchTmp);
        for (GdFbpeFileBatchTmp batchDetail : batchDetailList) {
            Map<String, Object> detailMap = new HashMap<String, Object>();
            
            detailMap.put("txnNo", batchDetail.getTxnNo());
            detailMap.put("orgCde", batchDetail.getOrgCde());
            detailMap.put("smsSqn", batchDetail.getRsvFld6());
            detailMap.put("sqn", batchDetail.getSqn());
            detailMap.put("tlrNo", batchDetail.getTlrNo());
            detailMap.put("txnTme", batchDetail.getTxnTim());
            detailMap.put("sucFlg", batchDetail.getTxnTim()); //TODO 如何获得此数值
            detailMap.put("accNo", batchDetail.getAccNo());
            detailMap.put("cusNo", batchDetail.getCusNo());
            detailMap.put("cusAc", batchDetail.getCusAc());
            detailMap.put("cusNam", batchDetail.getCusNam());
            detailMap.put("txnAmt", batchDetail.getTxnAmt());
            detailMap.put("actNo", batchDetail.getActNo());
            if("mob" ==eupsBatchConsoleInfo.getComNo()) { // 只有佛山移动有此字段
            detailMap.put("rsvFld1", batchDetail.getRsvFld1());//备用字段1 代表手机号码
            detailMap.put("months", batchDetail.getCosMon());
            detailMap.put("bankNam", batchDetail.getBankNam());
            detailMap.put("mobComNam", "广东移动通信有限责任公司佛山分公司");
            detailMap.put("mobComAc", "4267000012014017715");
            detailMap.put("AAC", "交通银行佛山分行");
            }
            
          
            detailList.add(detailMap);
        }
        resultMap.put("detail", detailList);

        //根据单位编号寻找返盘格式文件解析
        String comNo = batchDetailList.get(0).getRsvFld7();
        String fmtFileName =null;
        if(comNo.equals("tv")) {  //TODO comNo 确定后更改
            fmtFileName="tvFbpeBatResultFmt";
        } else if (comNo.equals("gas")) {
            fmtFileName="gasFbpeBatResultFmt";
        }  else if (comNo.equals("mob")) {
            fmtFileName="mobFbpeBatResultFmt";
        } else if (comNo.equals("tel")) {
            fmtFileName="telFbpeBatResultFmt";
        }
        EupsThdFtpConfig eupsThdFtpConfig = eupsThdFtpConfigRepository.findOne("fbpeBathReturnFmt");

        String fileName = context.getData("filNam")+"_"+batNo+"_"+ context.getData("bk");
        eupsThdFtpConfig.setLocDir("dat/fbp/"+fileName);
        eupsThdFtpConfig.setFtpDir("dat/term/send/"+fileName);
        eupsThdFtpConfig.setLocFleNme(fileName);

        // 生成文件
        operateFile.createCheckFile(eupsThdFtpConfig, fmtFileName, fileName, resultMap);

        // 将生成的文件上传至指定服务器
        eupsThdFtpConfig.setLocFleNme(fileName);
        eupsThdFtpConfig.setRmtFleNme(fileName);
        operateFTP.putCheckFile(eupsThdFtpConfig);
    }

}
