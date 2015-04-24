package com.bocom.bbip.gdeupsb.action.fbpe;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.comp.BBIPPublicServiceImpl;
import com.bocom.bbip.eups.action.BaseAction;
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
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.entity.GDEupsBatchConsoleInfo;
import com.bocom.bbip.gdeupsb.entity.GdFbpeFileBatchTmp;
import com.bocom.bbip.gdeupsb.repository.GDEupsBatchConsoleInfoRepository;
import com.bocom.bbip.gdeupsb.repository.GdFbpeFileBatchTmpRepository;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.bbip.utils.FileUtils;
import com.bocom.jump.bp.SystemConfig;
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
         //返回头  只有移动有
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
				System.out.println(">>>>>>>>>>>>>>>>>rsvFld7"+gdFbpeFileBatchTmps.getRsvFld7()+"<<<<<<<<<<<");
				gdFbpeFileBatchTmpRepository.updateFbpe(gdFbpeFileBatchTmps);
		}
        
        //根据单位编号寻找返盘格式文件解析
        String fmtFileName =null;
        String comNo=gdEupsBatchConsoleInfo.getComNo();
        int i=0;
        if(comNo.equals("4460000011")) { 
            fmtFileName="tvFbpeBatResultFmt";
            i=1;
        } else if (comNo.equals("4460002194")) {
        	 i=2;
            //TODO 燃气  自己写
     		String tlr=(String)context.getData(ParamKeys.TELLER);
    		final String br=(String)context.getData(ParamKeys.BR);
    		context.setData(ParamKeys.BR, br);
            final String AcDate=DateUtils.format(((BBIPPublicServiceImpl)get(GDConstants.BBIP_PUBLIC_SERVICE)).getAcDate(),DateUtils.STYLE_yyyyMMdd);
            final String systemCode=((SystemConfig)get(SystemConfig.class)).getSystemCode();
            final String dir="/home/bbipadm/data/mftp/BBIP/"+systemCode+"/"+br+"/"+tlr+"/"+AcDate+"/";
            
        	String pathName=dir+batNos+".result";
        	
         	File file=new File(pathName);
        	if(!file.exists()){
        			throw  new CoreException("反盘文件获取错误");
        	}
        	 createGasFile(context,eupsBatchInfoDetailList,pathName,file);
        }  else if (comNo.equals("4460000010")) {
        	 i=3;
            fmtFileName="mobFbpeBatResultFmt";
        } else if (comNo.equals("4460000014")) {
        	 i=4;
            fmtFileName="telFbpeBatResultFmt";
        }

        List<Map<String, Object>> detailList = new ArrayList<Map<String, Object>>();
        String batNo=gdEupsBatchConsoleInfo.getBatNo();
        GdFbpeFileBatchTmp gdFbpeFileBatchTmp=new GdFbpeFileBatchTmp();
        gdFbpeFileBatchTmp.setRsvFld5(batNo);
        // 生成返回明细信息
        List<GdFbpeFileBatchTmp> batchDetailList = fileRepository.find(gdFbpeFileBatchTmp);
        for (GdFbpeFileBatchTmp batchDetail : batchDetailList) {
            Map<String, Object> detailMap = new HashMap<String, Object>();            
            detailMap.put("txnNo", batchDetail.getTxnNo());
            detailMap.put("orgCde", batchDetail.getOrgCde());
            detailMap.put("smsSqn", batchDetail.getRsvFld6());
            detailMap.put("sqn", batchDetail.getSqn());
            detailMap.put("tlrNo", batchDetail.getTlrNo());
            detailMap.put("txnTim", batchDetail.getTxnTim());
            String sts=batchDetail.getRsvFld7();
            if(i==1){
	            	if(sts.equals("S")){
	                		sts="00";
	            	}
            }else if(i==2){
            	//TODO 
	            	if(sts.equals("S")){
	                		sts="1";
	            	}else{
	            			sts="0";
	            	}
            }else if(i==3){
	            	if(sts.equals("S")){
	                		sts="1";
	            	}else{
	            			sts="0";
	            	}
            }else if(i==4){
	            	if(sts.equals("S")){
	                		sts="1";
	            	}else{
	            			sts="0";
	            	}
            }
            detailMap.put("rsvFld7", sts);     //状态
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

        EupsThdFtpConfig eupsThdFtpConfig = eupsThdFtpConfigRepository.findOne("fbpeBathReturnFmt");
        //文件名
        String fileName = gdEupsBatchConsoleInfo.getComNo()+"_"+DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMdd)+".txt";
        eupsThdFtpConfig.setFtpDir("0");
        eupsThdFtpConfig.setLocDir("/home/bbipadm/data/GDEUPSB/batch/"+fileName);
        eupsThdFtpConfig.setLocFleNme(fileName);

        // 生成文件
        operateFile.createCheckFile(eupsThdFtpConfig, fmtFileName, fileName, resultMap);

        //TODO  将生成的文件上传至指定服务器
//        eupsThdFtpConfig.setRmtWay("/home/bbipadm/data/GDEUPSB/batch/");
//        eupsThdFtpConfig.setRmtFleNme(fileName);
//        operateFTP.putCheckFile(eupsThdFtpConfig);
    }
    public void createGasFile(Context context,List<EupsBatchInfoDetail> eupsBatchInfoDetailList,String pathName,File file){
    	List<String> list=new ArrayList<String>();
    	for (EupsBatchInfoDetail eupsBatchInfoDetail : eupsBatchInfoDetailList) {
				 	String cusNo=eupsBatchInfoDetail.getAgtSrvCusId();
				 	String cusAc=eupsBatchInfoDetail.getCusAc();
				 	String cusNme=eupsBatchInfoDetail.getCusNme();
				 	String txnAmt=eupsBatchInfoDetail.getTxnAmt().scaleByPowerOfTen(2)+"";
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
				 	int length3=cusNme.length();
				 	String cusNmeLength=length3+"";
				 	while(cusNmeLength.length()<3){
				 			cusNmeLength="0"+cusNmeLength;
				 	}
				 	int length4=txnAmt.length();
				 	String txnAmtLength=length4+"";
				 	while(txnAmtLength.length()<3){
				 			txnAmtLength="0"+txnAmtLength;
				 	}
				 	String sts=eupsBatchInfoDetail.getSts();
				 	String stsLength="001";
				 	String errMsg=eupsBatchInfoDetail.getErrMsg();
				 	String errMsgLength=errMsg.length()+"";
				 	while(errMsgLength.length()<3){
				 			errMsgLength="0"+errMsgLength;
				 	}
				 	//写文件
				 	String line=cusNoLength+cusNo+cusAcLength+cusAc+cusNmeLength+cusNme+txnAmtLength+txnAmt+stsLength+sts+errMsgLength+errMsg;
				 	list.add(line);
			}
    	try {
			FileUtils.writeLines(file, list);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	//放置文件
    }
}
