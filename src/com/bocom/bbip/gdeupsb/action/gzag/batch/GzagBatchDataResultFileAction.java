package com.bocom.bbip.gdeupsb.action.gzag.batch;

import java.math.BigDecimal;
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
import com.bocom.bbip.eups.entity.EupsThdFtpConfig;
import com.bocom.bbip.eups.repository.EupsBatchConsoleInfoRepository;
import com.bocom.bbip.eups.repository.EupsThdFtpConfigRepository;
import com.bocom.bbip.eups.spi.service.batch.AfterBatchAcpService;
import com.bocom.bbip.eups.spi.vo.AfterBatchAcpDomain;
import com.bocom.bbip.gdeupsb.action.common.BatchFileCommon;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GDEupsBatchConsoleInfo;
import com.bocom.bbip.gdeupsb.entity.GDEupsGzagBatchTmp;
import com.bocom.bbip.gdeupsb.repository.GDEupsBatchConsoleInfoRepository;
import com.bocom.bbip.gdeupsb.repository.GDEupsGzagBatchTmpRepository;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.SystemConfig;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

public class GzagBatchDataResultFileAction extends BaseAction implements AfterBatchAcpService{
	private final static Log logger=LogFactory.getLog(GzagBatchDataResultFileAction.class);
	@Autowired
	OperateFileAction operateFile;
	@Autowired
	OperateFTPAction operateFTP;
	@Autowired
	GDEupsBatchConsoleInfoRepository gdEupsBatchConsoleInfoRepository;
	@Autowired
	EupsBatchConsoleInfoRepository eupsBatchConsoleInfoRepository;
	@Autowired
	GDEupsGzagBatchTmpRepository gdEupsGzagBatchTmpRepository;
	@Autowired
	EupsThdFtpConfigRepository eupsThdFtpConfigRepository;
	@Autowired
	BBIPPublicService bbipPublicService;
	@Autowired
	SystemConfig systemConfig;
	/**
	 * 广州文本  结果文件处理
	 */
	public void afterBatchDeal(AfterBatchAcpDomain afterbatchacpdomain, Context context) throws CoreException {
		logger.info("===============Start  GzagBatchDataResultFileAction");
	
		//更改状态
		GDEupsBatchConsoleInfo gdeupsBatchConsoleInfo =get(BatchFileCommon.class).eupsBatchConSoleInfoAndgdEupsBatchConSoleInfo(context);
		final String tlr=(String)context.getData(ParamKeys.TELLER);
		final String br=(String)context.getData(ParamKeys.BR);
        final String AcDate=DateUtils.format(bbipPublicService.getAcDate(),DateUtils.STYLE_yyyyMMdd);
        final String systemCode=systemConfig.getSystemCode();
        final String dir=get(BBIPPublicService.class).getParam("batchPath")+systemCode+"/"+br+"/"+tlr+"/"+AcDate+"/";
			//得到文件
//			EupsThdFtpConfig eupsThdFtpConfigFile = eupsThdFtpConfigRepository.findOne(ParamKeys.FTPID_BATCH_PAY_FILE_TO_ACP);
//			String fileNme=context.getData("batNo")+".result";
//			eupsThdFtpConfigFile.setRmtFleNme(fileNme);
//			eupsThdFtpConfigFile.setThdIpAdr("182.53.15.219");
//			eupsThdFtpConfigFile.setRmtWay(dir);
//			eupsThdFtpConfigFile.setFtpDir("1");
//			operateFTP.getFileFromFtp(eupsThdFtpConfigFile);
			logger.info("============得到文件成功");
			
			
			//更改控制表 并得到控制信息
//			GDEupsBatchConsoleInfo info=updateInfo(context,gdeupsBatchConsoleInfo); 
			String fileId="";
			String comNo=gdeupsBatchConsoleInfo.getComNo();
			if(comNo.equals("4410000578")){
				fileId="lottBatchFile";   		//彩票返奖
			}else if(comNo.equals("4410000560")){
					fileId="insuBatchFile";		//广州电话银行保险
			}else if(comNo.equals("4410000561")){
					fileId="insuBatchFile";		//广州电话银行保险
			}else if(comNo.equals("4410001102")){
					fileId="yctBatchFile";			//羊城通
			}else if(comNo.equals("4410001274")){
					fileId="yktBatchFile";			//粤通卡
			}else if(comNo.equals("4410001882")){
					fileId="sptltBatchFile";		//体育彩票
			}else{
					throw new CoreException("Error  ");
			}
			//文件名
			String locName=DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMdd)+fileId+".txt";
			logger.info(">>>>>>>>>>>>>>>>>locName:"+locName);
			//拼装Map文件
			Map<String, Object> resultMap = createFileMap(context,gdeupsBatchConsoleInfo);
			String fileIDResult=fileId+"Result";
			EupsThdFtpConfig eupsThdFtpConfig =eupsThdFtpConfigRepository.findOne(fileId);
			// 生成文件
			
			operateFile.createCheckFile(eupsThdFtpConfig, fileIDResult, locName, resultMap);
			logger.info("===============生成反盘文件成功");
			// 将生成的文件上传至指定服务器
			eupsThdFtpConfig.setFtpDir("0");
			eupsThdFtpConfig.setLocFleNme(locName);
			eupsThdFtpConfig.setRmtFleNme(locName);
			operateFTP.putCheckFile(eupsThdFtpConfig);
			
			logger.info("===============End  GzagBatchDataResultFileAction");
		}
	/**
	 * 把信息保存到控制表
	 */
	public GDEupsBatchConsoleInfo  updateInfo(Context context,GDEupsBatchConsoleInfo gdeupsBatchConsoleInfo){
			logger.info("===============Start  BatchDataResultFileAction  updateInfo");	
			Integer totCnt=Integer.parseInt(gdeupsBatchConsoleInfo.getTotCnt().toString());
			//成功笔数
			Integer sucTotCnt=Integer.parseInt(context.getData(GDParamKeys.SUC_TOT_CNT).toString());
			gdeupsBatchConsoleInfo.setSucTotCnt(sucTotCnt);
			//失败笔数
			Integer falTotCnt=totCnt-sucTotCnt;
			gdeupsBatchConsoleInfo.setFalTotCnt(falTotCnt);
			
			BigDecimal totAmt=gdeupsBatchConsoleInfo.getTotAmt();
			//成功总金额
			BigDecimal sucTotAmt=new BigDecimal(context.getData("sucTotAmt").toString());
			gdeupsBatchConsoleInfo.setSucTotAmt(sucTotAmt);
			//失败总金额
			BigDecimal falTotAmt=totAmt.subtract(sucTotAmt);
			gdeupsBatchConsoleInfo.setFalTotAmt(falTotAmt);
			
			//更新批次表
			gdEupsBatchConsoleInfoRepository.updateConsoleInfo(gdeupsBatchConsoleInfo);
			logger.info("===============End  BatchDataResultFileAction  updateInfo");	
			return gdeupsBatchConsoleInfo;
	}
	/**
	 * 拼装Map返回文件
	 */
	public Map<String, Object> createFileMap(Context context,GDEupsBatchConsoleInfo info){
			logger.info("===============Start  BatchDataResultFileAction  createFileMap");	
			Map<String, Object> resultMap=new HashMap<String, Object>();
			//header
			info.setRsvFld6("exeDte");
			Map<String, Object> resultMapHead = BeanUtils.toMap(info);
			resultMap.put(ParamKeys.EUPS_FILE_HEADER, resultMapHead);
			//文件内容 
			GDEupsGzagBatchTmp gdEupsGzagBatchTmps =new  GDEupsGzagBatchTmp();
			gdEupsGzagBatchTmps.setRsvFld1(info.getBatNo());
			List<GDEupsGzagBatchTmp> detailList=gdEupsGzagBatchTmpRepository.find(gdEupsGzagBatchTmps);
			resultMap.put(ParamKeys.EUPS_FILE_DETAIL, BeanUtils.toMaps(detailList));
			logger.info("===============End   BatchDataResultFileAction  createFileMap");	
			return resultMap;
	}
}
