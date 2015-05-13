package com.bocom.bbip.gdeupsb.action.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.entity.EupsBatchConsoleInfo;
import com.bocom.bbip.eups.repository.EupsBatchConsoleInfoRepository;
import com.bocom.bbip.file.MftpTransfer;
import com.bocom.bbip.gdeupsb.entity.GDEupsBatchConsoleInfo;
import com.bocom.bbip.gdeupsb.repository.GDEupsBatchConsoleInfoRepository;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class CommNotifyBatchStatusAction extends BaseAction{
		@Autowired
		EupsBatchConsoleInfoRepository eupsBatchConsoleInfoRepository;
		@Autowired
		GDEupsBatchConsoleInfoRepository gdEupsBatchConsoleInfoRepository;
		@Override
		public void execute(Context context) throws CoreException,
				CoreRuntimeException {
				log.info("==============Start  CommNotifyBatchStatusAction");
				
				BBIPPublicService bbipPublicService=get(BBIPPublicService.class);
				String path="/home/bbipadm/data/mftp/";
				String batNo=context.getData("batNo").toString();
				String fileNme=batNo+".result";
				EupsBatchConsoleInfo eupsBatchConsoleInfo=eupsBatchConsoleInfoRepository.findOne(batNo);
				if(eupsBatchConsoleInfo !=null){
					String rsvFld2=eupsBatchConsoleInfo.getRsvFld1();
					GDEupsBatchConsoleInfo gdEupsBatchConsoleInfo=gdEupsBatchConsoleInfoRepository.findOne(rsvFld2);
					if(gdEupsBatchConsoleInfo.getEupsBusTyp().toString().trim().equals("FSAG00")){
						String returnFileName=gdEupsBatchConsoleInfo.getComNo()+"_"+DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMdd);
						eupsBatchConsoleInfo.setPayCnt(null);
						eupsBatchConsoleInfoRepository.update(eupsBatchConsoleInfo);
						log.info("==============update  eupsBatchConsoleInfo's  payCnt   success");
						//同步调用
						String mothed="eups.commNotifyBatchStatus";
						bbipPublicService.synExecute(mothed, context);
				        context.setData("ApFmt",  "48211");
				        context.setData("batNo", null);
				        context.setData("subDte",  null);
				        context.setData("comNme", returnFileName );
				        context.setData("batSts",  null);
				        context.setData("totCnt",  null);
				        context.setData("totAmt",  null);
				        context.setData("sucTotCnt"  ,null);
				        context.setData("sucTotAmt", null);
				        log.info("==============End  CommNotifyBatchStatusAction");
				        return;
					}
				}
				try {			
					bbipPublicService.getFileFromBBOS(new File(path,fileNme), fileNme, MftpTransfer.FTYPE_NORMAL);	
				}catch (Exception e) {
					throw new CoreException(ErrorCodes.EUPS_MFTP_FILEDOWN_FAIL);
				}
				get(BBIPPublicService.class).asynExecute("gdeupsb.batchFileZHAG", context);
				//读取第一行 获取单位编号
				String comNo="";
				try {
					FileReader fileReader = new FileReader(path+"//"+fileNme);
					BufferedReader bufferedReader=new BufferedReader(fileReader);
					String firstLine=null;
					int i=0;
					while((firstLine=bufferedReader.readLine())!=null && i<1){
						//TODO 确认 | 可行
						comNo=firstLine.split("|")[0];
					}
					bufferedReader.close();
					fileReader.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				context.setData("comNo",  comNo);
				//异步调用 反盘文件
				String mothed="eups.commNotifyBatchStatus";
				bbipPublicService.asynExecute(mothed, context);
				String returnFileName=comNo+"_"+DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMdd);
				//返回文件名
		        context.setData("ApFmt",  "48211");
		        context.setData("batNo", null);
		        context.setData("subDte",  null);
		        context.setData("comNme", returnFileName );
		        context.setData("batSts",  null);
		        context.setData("totCnt",  null);
		        context.setData("totAmt",  null);
		        context.setData("sucTotCnt"  ,null);
		        context.setData("sucTotAmt", null);
		        log.info("==============End  CommNotifyBatchStatusAction");
		}
}
