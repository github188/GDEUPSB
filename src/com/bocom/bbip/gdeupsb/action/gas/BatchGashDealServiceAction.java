package com.bocom.bbip.gdeupsb.action.gas;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.CollectionUtils;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.action.common.OperateFTPAction;
import com.bocom.bbip.eups.action.common.OperateFileAction;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsThdFtpConfig;
import com.bocom.bbip.eups.repository.EupsThdFtpConfigRepository;
import com.bocom.bbip.eups.spi.service.batch.BatchAcpService;
import com.bocom.bbip.eups.spi.vo.PrepareBatchAcpDomain;
import com.bocom.bbip.file.Marshaller;
import com.bocom.bbip.file.transfer.TransferUtils;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDErrorCodes;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GDEupsBatchConsoleInfo;
import com.bocom.bbip.gdeupsb.entity.GdGashBatchTmp;
import com.bocom.bbip.gdeupsb.repository.GDEupsBatchConsoleInfoRepository;
import com.bocom.bbip.gdeupsb.repository.GdGashBatchTmpRepository;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.JumpException;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

/**
 * 惠州燃气文件批扣数据准备
 * 燃气发起
 * @author WangMQ
 *
 */
public class BatchGashDealServiceAction implements BatchAcpService  {

	private final static Logger logger = LoggerFactory.getLogger(BatchGashDealServiceAction.class);
	
	@Autowired
	BBIPPublicService bbipPublicService;
	
	@Autowired
	OperateFTPAction operateFTPAction;
	
	@Autowired
	OperateFileAction operateFileAction;

	@Autowired
	GDEupsBatchConsoleInfoRepository gdEupsBatchConsoleInfoRepository;

	@Autowired
	EupsThdFtpConfigRepository eupsThdFtpConfigRepository;

	@Autowired
	GdGashBatchTmpRepository gdGashBatchTmpRepository;
	
	@Autowired
	Marshaller marshaller;

	@Override
	public void prepareBatchDeal(PrepareBatchAcpDomain preparebatchacpdomain, Context context) throws CoreException {
		logger.info("BatchGashDealServiceAction initDeal start!");
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
		
//		context.setData(ParamKeys.BR, GDConstants.GAS_BAT_BR);
		String comNo = "GDPGAS0001";
//		String comNo = context.getData(ParamKeys.COMPANY_NO);
		
		context.setData(ParamKeys.TELLER, "ABIR148");
		context.setData(ParamKeys.BR, "01441131999");
		context.setData(ParamKeys.BK, "01441999999");
		
		List<GdGashBatchTmp> gashBatchTmps = new ArrayList<GdGashBatchTmp>();
		
		//批量日期
//		String txnDatTmp = DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMdd);
		//批量文件名 (燃气端提供)
		String batFileName = context.getData("fileName").toString();
		
		//FTP配置信息
		EupsThdFtpConfig gasBatThdFtpConfig=eupsThdFtpConfigRepository.findOne("PGAS00Bat");
		gasBatThdFtpConfig.setLocDir(batFileName);
		gasBatThdFtpConfig.setRmtFleNme(batFileName);
		
		//取电子柜员
//		String ETeller = bbipPublicService.getETeller(GDConstants.PGAS00);
//		context.setData(ParamKeys.TELLER, ETeller);
			
		Date subDte = DateUtils.parse(batFileName.substring(4, 12), DateUtils.STYLE_yyyyMMdd);
		
		logger.info("===============判断文件是否重复录入=============");
		GDEupsBatchConsoleInfo gdBatchConsoleInfo = new GDEupsBatchConsoleInfo();
		gdBatchConsoleInfo.setComNo(comNo);
		gdBatchConsoleInfo.setSubDte(subDte);
//		gdBatchConsoleInfo.setFleNme(batFileName);
		logger.info("==============subDte:" + gdBatchConsoleInfo.getSubDte());
		List<GDEupsBatchConsoleInfo> ebciList = gdEupsBatchConsoleInfoRepository.find(gdBatchConsoleInfo);
		if(!CollectionUtils.isEmpty(ebciList)){
			String batNo = ebciList.get(0).getBatNo();
			context.setData(ParamKeys.RSP_CDE, GDConstants.GAS_ERROR_CODE);
			context.setData(ParamKeys.RSP_MSG, "该批次已录入，批次号为" + batNo);
			throw new CoreRuntimeException(GDErrorCodes.EUPS_CKDB_FILE_EXIST);
		}
		logger.info("无重复录入文件");
		
		//总笔数、总金额
		int totCnt = 0;
		BigDecimal totAmt = new BigDecimal(0.00);
		
		//获取文件
		operateFTPAction.getFileFromFtp(gasBatThdFtpConfig);
		
		//解析文件
		Resource resource = new FileSystemResource(TransferUtils.resolveFilePath(gasBatThdFtpConfig.getLocDir().trim(), gasBatThdFtpConfig.getLocFleNme().trim()));
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			map = marshaller.unmarshal("PGAS00BatFmt", resource, Map.class);
		} catch (JumpException e) {
			throw new CoreException(GDErrorCodes.EUPS_COM_FILE_PARSE_ERROR); // 解析文件失败
		}
		
		//入库
		List<Map<String, Object>> batDeatil = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> parseMap = (List<Map<String, Object>>) map.get("detail");
		List<GdGashBatchTmp> bthTmpSession = new ArrayList<GdGashBatchTmp>();
		for(Map<String, Object> orgMap : parseMap){
			GdGashBatchTmp gdGashBatchTmp = BeanUtils.toObject(orgMap, GdGashBatchTmp.class);
			gdGashBatchTmp.setSqn(bbipPublicService.getBBIPSequence());
			
			totAmt = totAmt.add(new BigDecimal((String)gdGashBatchTmp.getReqTxnAmt()));
			totCnt++;
			
			bthTmpSession.add(gdGashBatchTmp);
			String isOurBank = new String();
			Map<String, Object> batFileDeatailMap = new HashMap<String, Object>();
			batFileDeatailMap.put("CUSAC", gdGashBatchTmp.getCusAc());
			batFileDeatailMap.put("TXNAMT", gdGashBatchTmp.getReqTxnAmt());
			batFileDeatailMap.put("AGTSRVCUSID", gdGashBatchTmp.getCusNo());
			
			isOurBank = "0";
			batFileDeatailMap.put("OUROTHFLG", isOurBank);
			batDeatil.add(batFileDeatailMap);
		}
		gdGashBatchTmpRepository.insert(bthTmpSession);		
		
		//拼装代收付文件map
		Map<String, Object> gashBatMap = new HashMap<String, Object>();
		Map<String, Object> gasBatHeaderMap = new HashMap<String, Object>(); // 头
		
		gasBatHeaderMap.put("totCount", totCnt); // 总比数
		gasBatHeaderMap.put("totAmt", totAmt.toString()); // 总金额
		gasBatHeaderMap.put("comNo", comNo); // 单位编号
		
		// 拼装整个代收付文件
		gashBatMap.put("header", gasBatHeaderMap);
		gashBatMap.put("detail", batDeatil);

		context.setVariable(GDParamKeys.BATCH_COM_FILE_NAME, "gashFile");
		context.setVariable("gashBatFileMap", gashBatMap);
		
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);

	}
}
