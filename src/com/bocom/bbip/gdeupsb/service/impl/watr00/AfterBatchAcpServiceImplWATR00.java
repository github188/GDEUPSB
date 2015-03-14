package com.bocom.bbip.gdeupsb.service.impl.watr00;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.bocom.bbip.gdeupsb.entity.GDEupsEleTmp;
import com.bocom.bbip.gdeupsb.repository.GDEupsEleTmpRepository;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.bbip.utils.NumberUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.service.id.seq.StepSequenceFactory;
/**
 * 汕头水费批量代扣后处理
 * @author hefengwen
 *
 */
public class AfterBatchAcpServiceImplWATR00 implements AfterBatchAcpService{
	
	private static Logger logger = LoggerFactory.getLogger(AfterBatchAcpServiceImplWATR00.class);
	@Autowired
	OperateFileAction operateFile;
	@Autowired
	OperateFTPAction operateFTP;
	@Autowired
	EupsBatchConsoleInfoRepository eupsBatchConsoleInfoRepository;
	@Autowired
	GDEupsEleTmpRepository gdEupsEleTmpRepository;
	@Autowired
	EupsThdFtpConfigRepository eupsThdFtpConfigRepository;

	@Override
	public void afterBatchDeal(AfterBatchAcpDomain domain, Context context)	throws CoreException {
		logger.info("AfterBatchAcpServiceImplWATR00 start ... ...");
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		String batNo=context.getData(ParamKeys.BAT_NO).toString();
		EupsBatchConsoleInfo eupsBatchConsoleInfo = eupsBatchConsoleInfoRepository.findOne(batNo);
		//头部
		Map<String, Object> resultMapHead = BeanUtils.toMap(eupsBatchConsoleInfo);
		resultMap.put(ParamKeys.EUPS_FILE_HEADER, resultMapHead);
		//文件名
		String fileName=eupsBatchConsoleInfo.getFleNme();
		//文件内容 
		GDEupsEleTmp gdEupsEleTmps =new GDEupsEleTmp();
		gdEupsEleTmps.setRsvFld5(batNo);
		List<GDEupsEleTmp> detailList=gdEupsEleTmpRepository.find(gdEupsEleTmps);
		resultMap.put(ParamKeys.EUPS_FILE_DETAIL, detailList);
		
		EupsThdFtpConfig eupsThdFtpConfig =eupsThdFtpConfigRepository.findOne("watr00BatchResulf");
		
		// 生成文件
		operateFile.createCheckFile(eupsThdFtpConfig, "watr00BatchResulf", fileName, resultMap);
		// 将生成的文件上传至指定服务器
		eupsThdFtpConfig.setLocFleNme(fileName);
		eupsThdFtpConfig.setRmtFleNme(fileName);
		operateFTP.putCheckFile(eupsThdFtpConfig);
		
		
		
		context.setData("type", "Y004");
		context.setData("accountdate", DateUtils.format((Date)context.getData(ParamKeys.AC_DATE), DateUtils.STYLE_yyyyMMdd));
		
		StepSequenceFactory s = context.getService("logNoService");
		String logNo = s.create().toString();
		context.setData("waterno", "JH"+logNo);//流水号生成
		
		context.setData("bankcode", "JT");
		context.setData("salesdepart",((String)context.getData(ParamKeys.BR)).substring(2, 8));
		context.setData("salesperson", ((String)context.getData(ParamKeys.TELLER)).substring(4, 7));
		context.setData("busitime", DateUtils.format(new Date(),DateUtils.STYLE_yyyyMMddHHmmss));
		context.setData("thdRspCde", "0");
		context.setData("zprice", "");
		context.setData("months", "");
		context.setData("operano", "");
		context.setData("password", "        ");
		context.setData("md5digest", " ");
		
		
		context.setData("path", context.getData("path"));
		context.setData("filename", context.getData("filename"));
		context.setData("filesize", context.getData("filesize"));
		logger.info("AfterBatchAcpServiceImplWATR00 end ... ...");
	}

}
