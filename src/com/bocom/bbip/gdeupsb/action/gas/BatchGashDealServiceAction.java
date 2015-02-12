package com.bocom.bbip.gdeupsb.action.gas;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
  
import org.apache.taglibs.standard.tag.el.sql.SetDataSourceTag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.CollectionUtils;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.action.common.OperateFTPAction;
import com.bocom.bbip.eups.action.common.OperateFileAction;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsBatchConsoleInfo;
import com.bocom.bbip.eups.entity.EupsBatchPayEntity;
import com.bocom.bbip.eups.entity.EupsThdBaseInfo;
import com.bocom.bbip.eups.entity.EupsThdFtpConfig;
import com.bocom.bbip.eups.entity.EupsThdTranCtlInfo;
import com.bocom.bbip.eups.repository.EupsBatchConsoleInfoRepository;
import com.bocom.bbip.eups.repository.EupsBatchInfoDetailRepository;
import com.bocom.bbip.eups.repository.EupsThdBaseInfoRepository;
import com.bocom.bbip.eups.repository.EupsThdFtpConfigRepository;
import com.bocom.bbip.eups.repository.EupsThdTranCtlInfoRepository;
import com.bocom.bbip.eups.spi.service.batch.BatchAcpService;
import com.bocom.bbip.eups.spi.vo.PrepareBatchAcpDomain;
import com.bocom.bbip.file.Marshaller;
import com.bocom.bbip.file.transfer.TransferUtils;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDErrorCodes;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GdGashBatchTmp;
import com.bocom.bbip.gdeupsb.repository.GdGashBatchTmpRepository;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.bbip.utils.NumberUtils;
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
	OperateFileAction operateFile;
	
	@Autowired
	EupsThdBaseInfoRepository eupsThdBaseInfoRepository;

	@Autowired
	EupsBatchConsoleInfoRepository eupsBatchConsoleInfoRepository;

	@Autowired
	EupsBatchInfoDetailRepository eupsBatchInfoDetailRepository;

	@Autowired
	EupsThdFtpConfigRepository eupsThdFtpConfigRepository;

	@Autowired
	EupsThdTranCtlInfoRepository eupsThdTranCtlInfoRepository;
	
	@Autowired
	GdGashBatchTmpRepository gdGashBatchTmpRepository;
	
	@Autowired
	Marshaller marshaller;

	@Override
	public void prepareBatchDeal(PrepareBatchAcpDomain preparebatchacpdomain, Context context) throws CoreException {

		logger.info("BatchGashDealServiceAction initDeal start!");
		context.setData(ParamKeys.BR, GDConstants.GAS_BAT_BR);
		String comNo = "PGAS00";
//		String comNo = context.getData(ParamKeys.COMPANY_NO);
		
//		查询单位信息| 单位编号，使用状态
//		SELECT TActNo,BBusTyp,CrpCod FROM savcrpagr where CAgtNo='%s' and SvrSts='1'
//		EupsThdBaseInfo eupsThdBaseInfo = new EupsThdBaseInfo();
//		eupsThdBaseInfo.setComNo(comNo);
//		eupsThdBaseInfo.setUseSts("0");
//		 List<EupsThdBaseInfo> eupsThdBaseInfoList =  eupsThdBaseInfoRepository.find(eupsThdBaseInfo);
//		if (CollectionUtils.isEmpty(eupsThdBaseInfoList)) {
//			throw new CoreException(ErrorCodes.THD_CHL_TRADE_NOT_ALLOWWED);
//		}
//		logger.info("存在单位协议，可进行业务交易");
		
		//批量日期
		String txnDatTmp = DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMdd);
		//批量文件名
		String batFileName = "file" + context.getData("bk") + txnDatTmp + ".txt";
		context.setData("FFilNam", batFileName);
		context.setData(GDParamKeys.GAS_DSK_NAM, batFileName);
		
		context.setData(ParamKeys.THD_TXN_CDE, "SMPCPAYTXT");
		context.setData(GDParamKeys.GSS_BAT_PKG_FLG, "1");
		
		//取电子柜员
		String ETeller = bbipPublicService.getETeller(GDConstants.PGAS00);
		context.setData(ParamKeys.TELLER, ETeller);
			
		
		// 判断文件是否重复录入
		// SELECT DskNo FROM pubbatinf WHERE BrNo='%s' AND ActDat='%s' AND DskNam='%s' AND Status='P'
		EupsBatchConsoleInfo eupsBatchConsoleInfo = new EupsBatchConsoleInfo();
		eupsBatchConsoleInfo.setComNo(comNo);
		eupsBatchConsoleInfo.setSubDte(DateUtils.parse(txnDatTmp));
		logger.info("============提交日期====" + eupsBatchConsoleInfo.getSubDte() );
		eupsBatchConsoleInfo.setFleNme(batFileName);
		
		List<EupsBatchConsoleInfo> ebciList = eupsBatchConsoleInfoRepository.find(eupsBatchConsoleInfo);
		if(!CollectionUtils.isEmpty(ebciList)){
			String batNo = ebciList.get(0).getBatNo();
			context.setData(ParamKeys.RSP_CDE, GDConstants.GAS_ERROR_CODE);
			context.setData(ParamKeys.RSP_MSG, "该批次已录入，批次号为" + batNo);
			throw new CoreRuntimeException(GDErrorCodes.EUPS_CKDB_FILE_EXIST);
		}
		
		logger.info("无重复录入文件");
		
		List<EupsBatchPayEntity> batchDetailLst = new ArrayList<EupsBatchPayEntity>();
		
		// 拼装代收付文件map
		makeAgtBatchFileMap(context, batFileName, comNo);
		
		
	}
	
	/**
	 * 拼装代收付文件map--燃气
	 * @param context
	 * @param batFileName
	 * @param comNo
	 * @throws CoreException
	 */
	private void makeAgtBatchFileMap(Context context, String batFileName, String comNo)throws CoreException {
		//总笔数、总金额
		int totCnt = 0;
		BigDecimal totAmt = new BigDecimal(0.00);
		
		//获取当前批次FTP信息
		EupsThdFtpConfig eupsThdFtpInf = eupsThdFtpConfigRepository.findOne("pgasBatchThd");
		eupsThdFtpInf.setRmtFleNme(batFileName);
		eupsThdFtpInf.setLocFleNme(batFileName);
		
		logger.info("start get batch file: now thdftp info=" + eupsThdFtpInf.toString());
		
		operateFTPAction.getFileFromFtp(eupsThdFtpInf);
		
		// 自行实现解析文件
		Resource resource = new FileSystemResource(TransferUtils.resolveFilePath(eupsThdFtpInf.getLocDir().trim(), eupsThdFtpInf.getLocFleNme()
						.trim()));
		Map<String, Object> map = new HashMap<String, Object>();
			try {
				map = marshaller.unmarshal("PGAS00BatFmt", resource, Map.class);
			} catch (JumpException e) {
				throw new CoreException(GDErrorCodes.EUPS_COM_FILE_PARSE_ERROR); // 解析文件失败
			}
		
		//无header
		
		List<Map<String, Object>> agtDeatil = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> parseMap = (List<Map<String, Object>>) map.get("detail");
		List<GdGashBatchTmp> bthTmpSession = new ArrayList<GdGashBatchTmp>();
		for(Map<String, Object> orgMap : parseMap){
			GdGashBatchTmp gdGashBatchTmp = BeanUtils.toObject(orgMap, GdGashBatchTmp.class);
			gdGashBatchTmp.setSqn(bbipPublicService.getBBIPSequence());
			
			
			
			totAmt = totAmt.add(new BigDecimal((String)gdGashBatchTmp.getReqTxnAmt()));
			totCnt++;
			
			bthTmpSession.add(gdGashBatchTmp);
			String isOurBank = new String();
			Map<String, Object> agtFileDeatailMap = new HashMap<String, Object>();
			agtFileDeatailMap.put("CUSAC", gdGashBatchTmp.getCusAc());
			agtFileDeatailMap.put("TXNAMT", gdGashBatchTmp.getReqTxnAmt());
			agtFileDeatailMap.put("AGTSRVCUSID", gdGashBatchTmp.getCusNo());
			
			isOurBank = "0";
			agtFileDeatailMap.put("OUROTHFLG", isOurBank);
			agtDeatil.add(agtFileDeatailMap);
		}
			
		gdGashBatchTmpRepository.insert(bthTmpSession);
		
		
		// 拼装代收付Map
		Map<String, Object> agtMap = new HashMap<String, Object>();
		Map<String, Object> agtHeaderMap = new HashMap<String, Object>(); // 头
		
		agtHeaderMap.put("totCount", totCnt); // 总比数
		agtHeaderMap.put("totAmt", totAmt.toString()); // 总金额
		agtHeaderMap.put("comNo", comNo); // 单位编号
		
		// 拼装整个代收付文件
		agtMap.put("header", agtHeaderMap);
		agtMap.put("detail", agtDeatil);

		context.setVariable(GDParamKeys.BATCH_COM_FILE_NAME, "waterFile");
		context.setVariable("agtFileMap", agtMap);
		
	}
}
