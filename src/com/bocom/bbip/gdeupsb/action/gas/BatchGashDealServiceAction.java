package com.bocom.bbip.gdeupsb.action.gas;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.taglibs.standard.tag.el.sql.SetDataSourceTag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDErrorCodes;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.bbip.utils.NumberUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

/**
 * 惠州燃气文件批扣数据准备
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

	@Override
	public List<EupsBatchPayEntity> prepareBatchDeal(PrepareBatchAcpDomain preparebatchacpdomain, Context context) throws CoreException {

		logger.info("BatchGashDealServiceAction initDeal start!");
		context.setData(ParamKeys.BR, GDConstants.GAS_BAT_BR);
//		String comNo = "PGAS00";
		String comNo = context.getData(ParamKeys.COMPANY_NO);
		
//		查询单位信息，查签到状态？ 单位编号，使用状态
//		SELECT TActNo,BBusTyp,CrpCod FROM savcrpagr where CAgtNo='%s' and SvrSts='1'
		
		EupsThdBaseInfo eupsThdBaseInfo = new EupsThdBaseInfo();
		eupsThdBaseInfo.setComNo(comNo);
		eupsThdBaseInfo.setUseSts("0");
		 List<EupsThdBaseInfo> eupsThdBaseInfoList =  eupsThdBaseInfoRepository.find(eupsThdBaseInfo);
		if (CollectionUtils.isEmpty(eupsThdBaseInfoList)) {
			throw new CoreException(ErrorCodes.THD_CHL_TRADE_NOT_ALLOWWED);
		}
		logger.info("可进行业务");
		
		//批量日期
		String txnDatTmp = DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMdd);
		//批量文件名
		context.setData("FFilNam", "file" + context.getData("bk") + txnDatTmp + ".txt");
		context.setData(GDParamKeys.GAS_DSK_NAM, context.getData("filNam"));
		
		context.setData(ParamKeys.THD_TXN_CDE, "SMPCPAYTXT");
		
		// <Set>Pkgflg=1</Set> <!-- 批量扣收标志0表示单笔1表示批量未发送2表示批量扣收已发送 --> 批次状态
//		context.setData(ParamKeys.BAT_STS, "1");
		
		//取电子柜员
		String ETeller = bbipPublicService.getETeller(GDConstants.PGAS00);
		//<Set>TlrId=$TlrId</Set>
		context.setData(ParamKeys.TELLER, ETeller);
			
		
		// 判断文件是否重复录入
		// SELECT DskNo FROM pubbatinf WHERE BrNo='%s' AND ActDat='%s' AND DskNam='%s' AND Status='P'
		EupsBatchConsoleInfo eupsBatchConsoleInfo = new EupsBatchConsoleInfo();
		eupsBatchConsoleInfo.setComNo(comNo);
		eupsBatchConsoleInfo.setSubDte(DateUtils.parse(txnDatTmp));
		logger.info("============提交日期====" + eupsBatchConsoleInfo.getSubDte() );
		eupsBatchConsoleInfo.setFleNme(context.getData("FFilNam").toString().trim());
		
		List<EupsBatchConsoleInfo> ebciList = eupsBatchConsoleInfoRepository.find(eupsBatchConsoleInfo);
		if(!CollectionUtils.isEmpty(ebciList)){
			String batNo = ebciList.get(0).getBatNo();
			context.setData(ParamKeys.RSP_CDE, GDConstants.GAS_ERROR_CODE);
			context.setData(ParamKeys.RSP_MSG, "该批次已录入，批次号为" + batNo);
			throw new CoreRuntimeException(GDErrorCodes.EUPS_CKDB_FILE_EXIST);
		}
		
		logger.info("无重复录入文件");
		
		// 根据第三方配置信息获取文件
			//1,根据文件名获得FTP配置
			//2,取文件
		EupsThdFtpConfig eupsThdFtpInf = eupsThdFtpConfigRepository.findOne("PGAS00");
		eupsThdFtpInf.setRmtFleNme(context.getData("FFilNam").toString());
		eupsThdFtpInf.setLocFleNme(context.getData("FFilNam").toString());
			
		operateFTPAction.getFileFromFtp(eupsThdFtpInf);
		
		List<EupsBatchPayEntity> batchDetailLst = new ArrayList<EupsBatchPayEntity>();

		List<Map<String, Object>> parseMap = operateFile.pareseFile(eupsThdFtpInf, "PGAS00BatFmt"); // 解析文件
		
		for (Map<String, Object> map : parseMap) {
		
//			<fixString name="cusNo" length="12"   /> 
//			<fixString name="cusAc" length="32"   />
//			<fixString name="reqTxnAmt" length="10"   />
			
			EupsBatchPayEntity batchPayDtl = new EupsBatchPayEntity();
		
			batchPayDtl.setCusAc((String) map.get("cusAc")); 
			BigDecimal txnAmt = NumberUtils.centToYuan((String) map.get("reqTxnAmt")); 
			batchPayDtl.setTxnAmt(txnAmt);
			batchPayDtl.setAgtSrvCusId((String) map.get("cusNo")); 
			batchDetailLst.add(batchPayDtl);
		}
		
		
		context.setData(ParamKeys.EUPS_BATCH_PAY_ENTITY_LIST, batchDetailLst);
		
		context.setData(ParamKeys.COMPANY_NO, comNo);
		context.setData(ParamKeys.TXN_MDE, Constants.TXN_MDE_FILE);  //文件批扣
		
		
		context.setData("flePreInf", BeanUtils.toMaps(batchDetailLst));
		
		logger.info("=============context="+context);
		
		return batchDetailLst;
		
	}
}
