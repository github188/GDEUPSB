package com.bocom.bbip.gdeupsb.action.fbpe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.eups.action.common.OperateFTPAction;
import com.bocom.bbip.eups.action.common.OperateFileAction;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsBatchInfoDetail;
import com.bocom.bbip.eups.entity.EupsThdFtpConfig;
import com.bocom.bbip.eups.repository.EupsBatchConsoleInfoRepository;
import com.bocom.bbip.eups.repository.EupsBatchInfoDetailRepository;
import com.bocom.bbip.eups.repository.EupsThdFtpConfigRepository;
import com.bocom.bbip.eups.spi.service.batch.AfterBatchAcpService;
import com.bocom.bbip.eups.spi.vo.AfterBatchAcpDomain;
import com.bocom.bbip.gdeupsb.action.common.BatchFileCommon;
import com.bocom.bbip.gdeupsb.entity.GDEupsBatchConsoleInfo;
import com.bocom.bbip.gdeupsb.entity.GdFbpeFileBatchTmp;
import com.bocom.bbip.gdeupsb.repository.GDEupsBatchConsoleInfoRepository;
import com.bocom.bbip.gdeupsb.repository.GdFbpeFileBatchTmpRepository;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

/**
 * 佛山文件-批扣结果处理
 * @version 1.0.0
 * @author Guilin.Li
 * Date 2015-02-12
 */
public class FbpeBatchResultDealAction implements AfterBatchAcpService {

	@Autowired
	EupsBatchInfoDetailRepository eupsBatchInfoDetailRepository;
    @Autowired
    EupsBatchConsoleInfoRepository eupsBatchConsoleInfoRepository;
    @Autowired
    GdFbpeFileBatchTmpRepository gdFbpeFileBatchTmpRepository;
    @Autowired
    GDEupsBatchConsoleInfoRepository gdEupsBatchConsoleInfoRepository;
    
    @Autowired
    GdFbpeFileBatchTmpRepository fileRepository;

    @Autowired
    EupsThdFtpConfigRepository eupsThdFtpConfigRepository;

    @Autowired
    OperateFileAction operateFile;

    @Autowired
    OperateFTPAction operateFTP;

    @Autowired
    BatchFileCommon batchFileCommon;
    private final static Log logger =LogFactory.getLog(FbpeBatchResultDealAction.class);

  
    @Override
    public void afterBatchDeal(AfterBatchAcpDomain arg0, Context context) throws CoreException {
    	logger.info("BatchFbpeResultDealAction Start! ");
       String batNos=context.getData("batNos").toString();
    	//保存到控制表 
        GDEupsBatchConsoleInfo gdEupsBatchConsoleInfo=batchFileCommon.eupsBatchConSoleInfoAndgdEupsBatchConSoleInfo(context);
        Map<String, Object> resultMap = new HashMap<String, Object>();
         //返回头  只有移动有
        Map<String, Object> resultMapHead = new HashMap<String, Object>();
        resultMapHead.put("rsvFld1", gdEupsBatchConsoleInfo.getSucTotCnt());
        resultMapHead.put("rsvFld2", gdEupsBatchConsoleInfo.getSucTotAmt());
        resultMap.put(ParamKeys.EUPS_FILE_HEADER, resultMapHead);

        
        EupsBatchInfoDetail eupsBatchInfoDetails=new EupsBatchInfoDetail();
        eupsBatchInfoDetails.setBatNo(batNos);
        List<EupsBatchInfoDetail> eupsBatchInfoDetailList=eupsBatchInfoDetailRepository.find(eupsBatchInfoDetails);
        for (EupsBatchInfoDetail eupsBatchInfoDetail : eupsBatchInfoDetailList) {
				String sqn=eupsBatchInfoDetail.getSqn();
				GdFbpeFileBatchTmp gdFbpeFileBatchTmps=new GdFbpeFileBatchTmp();
				gdFbpeFileBatchTmps.setSqn(sqn);
				gdFbpeFileBatchTmps.setRsvFld7(eupsBatchInfoDetail.getSts());
				gdFbpeFileBatchTmps.setRsvFld8(eupsBatchInfoDetail.getErrMsg());
				gdFbpeFileBatchTmpRepository.updateFbpe(gdFbpeFileBatchTmps);
		}
        
        // 生成返回明细信息
        List<Map<String, Object>> detailList = new ArrayList<Map<String, Object>>();
        String batNo=gdEupsBatchConsoleInfo.getBatNo();
        GdFbpeFileBatchTmp gdFbpeFileBatchTmp=new GdFbpeFileBatchTmp();
        gdFbpeFileBatchTmp.setRsvFld5(batNo);
        List<GdFbpeFileBatchTmp> batchDetailList = fileRepository.find(gdFbpeFileBatchTmp);
        for (GdFbpeFileBatchTmp batchDetail : batchDetailList) {
            Map<String, Object> detailMap = new HashMap<String, Object>();
            
            detailMap.put("txnNo", batchDetail.getTxnNo());
            detailMap.put("orgCde", batchDetail.getOrgCde());
            detailMap.put("smsSqn", batchDetail.getRsvFld6());
            detailMap.put("sqn", batchDetail.getSqn());
            detailMap.put("tlrNo", batchDetail.getTlrNo());
            detailMap.put("txnTim", batchDetail.getTxnTim());
            
            detailMap.put("rsvFld7", batchDetail.getRsvFld7());     //状态
            detailMap.put("rsvFld8",batchDetail.getRsvFld2());     // 失败原因
            
            detailMap.put("accNo", batchDetail.getAccNo());
            detailMap.put("cusNo", batchDetail.getCusNo());
            detailMap.put("cusAc", batchDetail.getCusAc());
            detailMap.put("cusNam", batchDetail.getCusNam());
            detailMap.put("txnAmt", batchDetail.getTxnAmt());
            detailMap.put("actNo", batchDetail.getActNo());
            detailMap.put("rsvFld1", batchDetail.getRsvFld1());//备用字段1 代表手机号码
            detailMap.put("rsvFld2",batchDetail.getRsvFld2());
            detailMap.put("months", batchDetail.getCosMon());
            detailMap.put("bankNam", batchDetail.getBankNam());
            detailMap.put("mobComNam", "广东移动通信有限责任公司佛山分公司");
            detailMap.put("mobComAc", "4267000012014017715");
            detailMap.put("AAC", "交通银行佛山分行");
          
            detailList.add(detailMap);
        }
        resultMap.put("detail", detailList);

        //根据单位编号寻找返盘格式文件解析
        String fmtFileName =null;
        String comNo=gdEupsBatchConsoleInfo.getComNo();
        if(comNo.equals("4460000011")) { 
            fmtFileName="tvFbpeBatResultFmt";
        } else if (comNo.equals("4460002194")) {
            //燃气  自己写
        }  else if (comNo.equals("4460000010")) {
            fmtFileName="mobFbpeBatResultFmt";
        } else if (comNo.equals("4460000014")) {
            fmtFileName="telFbpeBatResultFmt";
        }
        EupsThdFtpConfig eupsThdFtpConfig = eupsThdFtpConfigRepository.findOne("fbpeBathReturnFmt");

        String fileName = context.getData("filNam")+"_"+batNo+"_"+ context.getData("bk");
        eupsThdFtpConfig.setFtpDir("1");
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
