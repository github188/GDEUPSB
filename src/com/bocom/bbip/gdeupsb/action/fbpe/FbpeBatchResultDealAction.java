package com.bocom.bbip.gdeupsb.action.fbpe;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.action.common.OperateFTPAction;
import com.bocom.bbip.eups.action.common.OperateFileAction;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsBatchConsoleInfo;
import com.bocom.bbip.eups.entity.EupsBatchInfoDetail;
import com.bocom.bbip.eups.entity.EupsThdFtpConfig;
import com.bocom.bbip.eups.repository.EupsBatchConsoleInfoRepository;
import com.bocom.bbip.eups.repository.EupsBatchInfoDetailRepository;
import com.bocom.bbip.eups.repository.EupsThdFtpConfigRepository;
import com.bocom.bbip.eups.spi.service.batch.AfterBatchAcpService;
import com.bocom.bbip.eups.spi.vo.AfterBatchAcpDomain;
import com.bocom.bbip.file.MftpTransfer;
import com.bocom.bbip.gdeupsb.action.common.BatchFileCommon;
import com.bocom.bbip.gdeupsb.entity.GDEupsBatchConsoleInfo;
import com.bocom.bbip.gdeupsb.entity.GdFbpeFileBatchTmp;
import com.bocom.bbip.gdeupsb.repository.GDEupsBatchConsoleInfoRepository;
import com.bocom.bbip.gdeupsb.repository.GdFbpeFileBatchTmpRepository;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

/**
 * 佛山文件-批扣结果处理
 * @version 1.0.0
 * @author Guilin.Li
 * Date 2015-02-12
 */
public class FbpeBatchResultDealAction extends BaseAction implements AfterBatchAcpService {

	@Autowired
	EupsBatchInfoDetailRepository eupsBatchInfoDetailRepository;
    @Autowired
    EupsBatchConsoleInfoRepository eupsBatchConsoleInfoRepository;
    @Autowired
    GdFbpeFileBatchTmpRepository gdFbpeFileBatchTmpRepository;
    @Autowired
    GDEupsBatchConsoleInfoRepository gdEupsBatchConsoleInfoRepository;
    @Autowired
    BBIPPublicService bbipPublicService;
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
       String batNos=context.getData("batNo").toString();
    	//保存到控制表 
        GDEupsBatchConsoleInfo gdEupsBatchConsoleInfo=batchFileCommon.eupsBatchConSoleInfoAndgdEupsBatchConSoleInfo(context);
        Map<String, Object> resultMap = new HashMap<String, Object>();
         
        Map<String, Object> resultMapHead = new HashMap<String, Object>();
        resultMapHead.put("rsvFld1", gdEupsBatchConsoleInfo.getSucTotCnt());
        resultMapHead.put("rsvFld2", gdEupsBatchConsoleInfo.getSucTotAmt());
        resultMap.put(ParamKeys.EUPS_FILE_HEADER, resultMapHead);

        
        EupsBatchInfoDetail eupsBatchInfoDetails=new EupsBatchInfoDetail();
        eupsBatchInfoDetails.setBatNo(batNos);
        List<EupsBatchInfoDetail> eupsBatchInfoDetailList=eupsBatchInfoDetailRepository.find(eupsBatchInfoDetails);
        for (EupsBatchInfoDetail eupsBatchInfoDetail : eupsBatchInfoDetailList) {
				String sqn=eupsBatchInfoDetail.getRmk1();
				GdFbpeFileBatchTmp gdFbpeFileBatchTmps=new GdFbpeFileBatchTmp();
				gdFbpeFileBatchTmps.setSqn(sqn);
				gdFbpeFileBatchTmps.setRsvFld7(eupsBatchInfoDetail.getSts());
				gdFbpeFileBatchTmps.setRsvFld8(eupsBatchInfoDetail.getErrMsg());
				gdFbpeFileBatchTmpRepository.updateFbpe(gdFbpeFileBatchTmps);
		}
        
        //根据单位编号寻找返盘格式文件解析
        String comNo=gdEupsBatchConsoleInfo.getComNo();
        String fileName = comNo+"_"+DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMdd)+".txt";   	
        context.setData("printResult", fileName);
        //仅有44460002194
         	createGasFile(context, eupsBatchInfoDetailList, comNo,batNos);
            context.setData("ApFmt",  "48211");
            context.setData("batNo",  gdEupsBatchConsoleInfo.getBatNo());
            context.setData("comNo",  gdEupsBatchConsoleInfo.getComNo());
            context.setData("subDte",  gdEupsBatchConsoleInfo.getSubDte());
            context.setData("comNme", fileName );
           context.setData("batSts",  gdEupsBatchConsoleInfo.getBatSts());
            context.setData("totCnt",  gdEupsBatchConsoleInfo.getTotCnt());
            context.setData("totAmt",  gdEupsBatchConsoleInfo.getTotAmt());
            context.setData("sucTotCnt"  ,gdEupsBatchConsoleInfo.getSucTotCnt());
            context.setData("sucTotAmt", gdEupsBatchConsoleInfo.getSucTotAmt());
            
            EupsThdFtpConfig sendFileToBBOSConfig = get(EupsThdFtpConfigRepository.class).findOne("FSAG00");
            //放置文件
            try {
			 	bbipPublicService.sendFileToBBOS(new File(sendFileToBBOSConfig.getLocDir(),fileName), fileName, MftpTransfer.FTYPE_NORMAL);		
			} catch (Exception e) {
			       	throw new CoreException("文件上传失败");
			}
            logger.info("==================End BatchFbpeResultDealAction");
         	return;
    }
    /**
     *生成佛山批扣返回文件 
     * @throws CoreException 
     */
    public void createGasFile(Context context,List<EupsBatchInfoDetail> eupsBatchInfoDetailList,String comNo,String batNos) throws CoreException{
    	logger.info("===============Start  createGasFile ");
    	//文件名
    	String fileName = comNo+"_"+DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMdd)+".txt";   	 
    	EupsBatchConsoleInfo eupsBatchConsoleInfo=eupsBatchConsoleInfoRepository.findOne(batNos);
    	String batNo=eupsBatchConsoleInfo.getRsvFld1();
    	GDEupsBatchConsoleInfo gdEupsBatchConsoleInfos=gdEupsBatchConsoleInfoRepository.findOne(batNo);
    	gdEupsBatchConsoleInfos.setRsvFld1(fileName);
    	//更改表信息
    	gdEupsBatchConsoleInfoRepository.updateConsoleInfo(gdEupsBatchConsoleInfos);
		try {
			//生成文件
			File file=new File("/home/bbipadm/data/GDEUPSB/batch/"+fileName);
			if(!file.exists()){
					file.createNewFile();
			}
			//添加文件内容
			FileWriter fileWriter = new FileWriter(file);
			BufferedWriter bufferedWriter=new BufferedWriter(fileWriter); 	
			for (EupsBatchInfoDetail eupsBatchInfoDetail : eupsBatchInfoDetailList) {
				 	String cusNo=eupsBatchInfoDetail.getAgtSrvCusId();
				 	String cusAc=eupsBatchInfoDetail.getCusAc();
				 	String cusNme=eupsBatchInfoDetail.getCusNme();
				 	String txnAmt=eupsBatchInfoDetail.getTxnAmt().scaleByPowerOfTen(2).intValue()+"";
				 	int length1=cusNo.length();
				 	String cusNoLength=""+length1;
				 	while(cusNoLength.length()<3){
				 			cusNoLength="0"+cusNoLength;
				 	}
				 	int length2=cusAc.length();
				 	String cusAcLength=""+length2;
				 	while(cusAcLength.length()<3){
				 		cusAcLength="0"+cusAcLength;
				 	}
				 	byte[] byCusNme=cusNme.getBytes("GBK");
				 	int length3=byCusNme.length;
				 	String cusNmeLength=length3+"";
				 	while(cusNmeLength.length()<3){
				 			cusNmeLength="0"+cusNmeLength;
				 	}
				 	int length4=txnAmt.length();
				 	String txnAmtLength=length4+"";
				 	while(txnAmtLength.length()<3){
				 			txnAmtLength="0"+txnAmtLength;
				 	}
				 	String sts=eupsBatchInfoDetail.getSts().trim();
				 	String errMsg=eupsBatchInfoDetail.getErrMsg();
				 	String stsLength="003";
				 	
				 	if(sts.equals("S")){
				 			sts="101";
				 			errMsg="扣收成功";
				 	}else{
					 		String errSeeason=errMsg;
					 				if(errMsg.length()>6){
					 						errSeeason=errMsg.substring(0,6);
					 				}else{
					 						errSeeason=errMsg;
					 				}
							 		if(errSeeason.equals("PDM252")){
							 			sts="004";
							 			errMsg="帐号已取消";
							 		}else if(errSeeason.equals("TPM055")){
							 			sts="007";
							 			errMsg="帐号和开户名不对应";
							 		}else if(errSeeason.equals("TPM050")){
							 			sts="002";
							 			errMsg="余额不足";
							 		}else if(errSeeason.equals("CB1004")){
							 			sts="006";
							 			errMsg="帐号不存在";
							 		}else{
							 			sts=errSeeason;
							 			byte[] byErrMsg=errSeeason.getBytes("GBK");
							 			stsLength=byErrMsg.length+"";
							 			while(stsLength.length()<3){
							 					stsLength="0"+stsLength;
							 			}
							 			if(eupsBatchInfoDetail.getErrMsg().length()>6){
							 					errMsg=eupsBatchInfoDetail.getErrMsg().substring(6);
							 			}
							 		}
				 	}
				 	byte[] byErrMsg=errMsg.getBytes("GBK");
				 	String errMsgLength=byErrMsg.length+"";
				 	while(errMsgLength.length()<3){
				 			errMsgLength="0"+errMsgLength;
				 	}
				 	//写文件
				 	String line=cusNoLength+cusNo+cusAcLength+cusAc+cusNmeLength+cusNme+txnAmtLength+txnAmt+stsLength+sts+errMsgLength+errMsg;
				 	bufferedWriter.write(line);				
				 	bufferedWriter.newLine();
			}
			bufferedWriter.close();
			fileWriter.close();			
		} catch (IOException e) {
			logger.info("===============ErrMsg=",e);
		}   
		logger.info("===============End  createGasFile ");
    }
}
