package com.bocom.bbip.gdeupsb.action.gzag.batch;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.eups.action.common.OperateFTPAction;
import com.bocom.bbip.eups.action.common.OperateFileAction;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsThdFtpConfig;
import com.bocom.bbip.eups.repository.EupsThdFtpConfigRepository;
import com.bocom.bbip.eups.spi.service.batch.AfterBatchAcpService;
import com.bocom.bbip.eups.spi.vo.AfterBatchAcpDomain;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GDEupsBatchConsoleInfo;
import com.bocom.bbip.gdeupsb.entity.GDEupsGzagBatchTmp;
import com.bocom.bbip.gdeupsb.repository.GDEupsBatchConsoleInfoRepository;
import com.bocom.bbip.gdeupsb.repository.GDEupsGzagBatchTmpRepository;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

public class GzagBatchDataResultFileAction implements AfterBatchAcpService{
	private final static Log logger=LogFactory.getLog(GzagBatchDataResultFileAction.class);
	@Autowired
	OperateFileAction operateFile;
	@Autowired
	OperateFTPAction operateFTP;
	@Autowired
	GDEupsBatchConsoleInfoRepository gdEupsBatchConsoleInfoRepository;
	@Autowired
	GDEupsGzagBatchTmpRepository gdEupsGzagBatchTmpRepository;
	@Autowired
	EupsThdFtpConfigRepository eupsThdFtpConfigRepository;
	/**
	 * 广州文本  结果文件处理
	 */
	public void afterBatchDeal(AfterBatchAcpDomain afterbatchacpdomain, Context context) throws CoreException {
		logger.info("===============Start  GzagBatchDataResultFileAction");
			
			
			String batNo=context.getData(ParamKeys.BAT_NO).toString();
			GDEupsBatchConsoleInfo gdeupsBatchConsoleInfo = gdEupsBatchConsoleInfoRepository.findOne(batNo);
			
			//更改控制表 并得到控制信息
			GDEupsBatchConsoleInfo info=updateInfo(context,gdeupsBatchConsoleInfo); 
			//文件名
			String fileName=info.getFleNme();
			//拼装Map文件
			Map<String, Object> resultMap = createFileMap(context,info);
			String fileIDResult=context.getData("fileID").toString()+"Result";
			EupsThdFtpConfig eupsThdFtpConfig =eupsThdFtpConfigRepository.findOne(fileIDResult);
			// 生成文件
			operateFile.createCheckFile(eupsThdFtpConfig, fileIDResult, fileName, resultMap);
			// 将生成的文件上传至指定服务器
			eupsThdFtpConfig.setLocFleNme(fileName);
			eupsThdFtpConfig.setRmtFleNme(fileName);
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
			Map<String, Object> resultMapHead = BeanUtils.toMap(info);
			resultMap.put(ParamKeys.EUPS_FILE_HEADER, resultMapHead);
			//文件内容 
			GDEupsGzagBatchTmp gdEupsGzagBatchTmps =new  GDEupsGzagBatchTmp();
			gdEupsGzagBatchTmps.setRsvFld5(info.getBatNo());
			List<GDEupsGzagBatchTmp> detailList=gdEupsGzagBatchTmpRepository.find(gdEupsGzagBatchTmps);
			resultMap.put(ParamKeys.EUPS_FILE_DETAIL, detailList);
			logger.info("===============End   BatchDataResultFileAction  createFileMap");	
			return resultMap;
	}
}
