package com.bocom.bbip.gdeupsb.action.gzag.batch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.eups.action.common.OperateFTPAction;
import com.bocom.bbip.eups.action.common.OperateFileAction;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsBatchConsoleInfo;
import com.bocom.bbip.eups.entity.EupsThdFtpConfig;
import com.bocom.bbip.eups.repository.EupsBatchConsoleInfoRepository;
import com.bocom.bbip.eups.repository.EupsThdFtpConfigRepository;
import com.bocom.bbip.eups.spi.service.batch.AfterBatchAcpService;
import com.bocom.bbip.eups.spi.vo.AfterBatchAcpDomain;
import com.bocom.bbip.gdeupsb.entity.GDEupsGzagBatchTmp;
import com.bocom.bbip.gdeupsb.repository.GDEupsGzagBatchTmpRepository;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

public class GzagBatchResultDataFileAction implements AfterBatchAcpService{
	@Autowired
	OperateFileAction operateFile;
	@Autowired
	OperateFTPAction operateFTP;
	@Autowired
	EupsBatchConsoleInfoRepository eupsBatchConsoleInfoRepository;
	@Autowired
	GDEupsGzagBatchTmpRepository gdEupsGzagBatchTmpRepository;
	@Autowired
	EupsThdFtpConfigRepository eupsThdFtpConfigRepository;
	/**
	 * 广州文本  结果文件处理
	 */
	public void afterBatchDeal(AfterBatchAcpDomain afterbatchacpdomain, Context context) throws CoreException {
			//
			Map<String, Object> resultMap = new HashMap<String, Object>();
			
			String batNo=context.getData(ParamKeys.BAT_NO).toString();
			EupsBatchConsoleInfo eupsBatchConsoleInfo = eupsBatchConsoleInfoRepository.findOne(batNo);
			//header
			Map<String, Object> resultMapHead = BeanUtils.toMap(eupsBatchConsoleInfo);
			resultMap.put(ParamKeys.EUPS_FILE_HEADER, resultMapHead);
			//文件名
			String fileName=eupsBatchConsoleInfo.getFleNme();
			//文件内容 
			GDEupsGzagBatchTmp gdEupsGzagBatchTmps =new  GDEupsGzagBatchTmp();
			gdEupsGzagBatchTmps.setRsvFld5(batNo);
			List<GDEupsGzagBatchTmp> detailList=gdEupsGzagBatchTmpRepository.find(gdEupsGzagBatchTmps);
			resultMap.put(ParamKeys.EUPS_FILE_DETAIL, detailList);
			
			String fileIDResult=context.getData("fileID").toString()+"Result";
			EupsThdFtpConfig eupsThdFtpConfig =eupsThdFtpConfigRepository.findOne(fileIDResult);
			
			// 生成文件
			operateFile.createCheckFile(eupsThdFtpConfig, fileIDResult, fileName, resultMap);
			// 将生成的文件上传至指定服务器
			eupsThdFtpConfig.setLocFleNme(fileName);
			eupsThdFtpConfig.setRmtFleNme(fileName);
			operateFTP.putCheckFile(eupsThdFtpConfig);
		
		}
}
