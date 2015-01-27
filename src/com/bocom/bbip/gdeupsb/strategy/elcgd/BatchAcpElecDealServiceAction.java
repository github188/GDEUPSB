package com.bocom.bbip.gdeupsb.strategy.elcgd;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.eups.action.common.OperateFTPAction;
import com.bocom.bbip.eups.action.common.OperateFileAction;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsBatchConsoleInfo;
import com.bocom.bbip.eups.entity.EupsBatchPayEntity;
import com.bocom.bbip.eups.entity.EupsThdFtpConfig;
import com.bocom.bbip.eups.entity.EupsThdTranCtlInfo;
import com.bocom.bbip.eups.repository.EupsBatchConsoleInfoRepository;
import com.bocom.bbip.eups.repository.EupsBatchInfoDetailRepository;
import com.bocom.bbip.eups.repository.EupsThdFtpConfigRepository;
import com.bocom.bbip.eups.repository.EupsThdTranCtlInfoRepository;
import com.bocom.bbip.eups.spi.service.batch.BatchAcpService;
import com.bocom.bbip.eups.spi.vo.PrepareBatchAcpDomain;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.CollectionUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.bbip.utils.NumberUtils;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

/**
 * 文件批扣-数据准备-广州电力
 * 
 * @author qc.w
 * 
 */

public class BatchAcpElecDealServiceAction implements BatchAcpService {

	private final static Logger log = LoggerFactory.getLogger(BatchAcpElecDealServiceAction.class);

	@Autowired
	OperateFTPAction operateFTPAction;

	@Autowired
	OperateFileAction operateFile;

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

		log.info("BatchFileDealPreAction initDeal start!hahahahaha..");

		// TODO：单位编号根据配型部类型转换获得
		String comNo = "ELEC01";

		// 检查签到签退
		EupsThdTranCtlInfo eupsThdTranCtlInfo = eupsThdTranCtlInfoRepository.findOne(comNo);
		if (!eupsThdTranCtlInfo.isTxnCtlStsSignin()) {
			throw new CoreException(ErrorCodes.THD_CHL_TRADE_NOT_ALLOWWED);
		}
		log.info("可以正常进行业务");

		// 根据是否含有文件名判断是手工发起的还是定时发起的
		String fileName = context.getData("filNam");
		String dealTyp = null;
		if (StringUtils.isNotEmpty(fileName)) {
			dealTyp = GDConstants.COM_BATCH_DEAL_TYP_PE; // 手工
		} else {
			dealTyp = GDConstants.COM_BATCH_DEAL_TYP_AU; // 自动
		}
		context.setData(GDParamKeys.COM_BATCH_DEAL_TYP, dealTyp);

		log.info("start get file!..");

		String dptTyp = null;
		
		List<EupsBatchPayEntity> payDetailLst = new ArrayList<EupsBatchPayEntity>();

		if (GDConstants.COM_BATCH_DEAL_TYP_PE.equals(dealTyp)) { // 手工处理方式

			// 判断批量控制表中是否已有当前文件数据，有则报错
			EupsBatchConsoleInfo batchInfo = new EupsBatchConsoleInfo();
			batchInfo.setFleNme(fileName);
			batchInfo.setExeDte(new Date());
			List<EupsBatchConsoleInfo> batchList = eupsBatchConsoleInfoRepository.find(batchInfo);
			if (CollectionUtils.isNotEmpty(batchList)) {
				throw new CoreException(ErrorCodes.EUPS_BATCH_DATA_IS_SUBMIT); // 数据已导入
			}

			// 根据第三方配置信息获取文件
			EupsThdFtpConfig eupsThdFtpInf = eupsThdFtpConfigRepository.findOne("eleGzBchThd");
			eupsThdFtpInf.setRmtFleNme(fileName);
			eupsThdFtpInf.setLocFleNme(fileName);
			
			log.info("start get batch file: now thdftp info=" + eupsThdFtpInf.toString());
			
			
			operateFTPAction.getFileFromFtp(eupsThdFtpInf);
			

			List<Map<String, Object>> parseMap = operateFile.pareseFile(eupsThdFtpInf, "eleGzBatFmt"); // 解析文件
			
			for (Map<String, Object> orgMap : parseMap) {
				EupsBatchPayEntity batchPayDtl = new EupsBatchPayEntity();
				batchPayDtl.setCusAc((String) orgMap.get("cusAc")); // 客户帐号
				BigDecimal txnAmt = NumberUtils.centToYuan((String) orgMap.get("tTxnAmt")); // 交易金额
				batchPayDtl.setTxnAmt(txnAmt);
				batchPayDtl.setAgtSrvCusId((String) orgMap.get("thdCusNo")); // 第三方客户标志
				payDetailLst.add(batchPayDtl);
			}
			context.setData(ParamKeys.EUPS_BATCH_PAY_ENTITY_LIST, payDetailLst);

		} else { // 自动发起方式

			// TODO：此处应该为循环，生产上目前只使用城区，其他几个地区功能暂时不拓展.后期可能需要进行拓展
			String dptCode = "00"; // 城区，其他几个地区功能暂时不拓展
			fileName = 0001 + DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMdd) + dptCode + "001.set";

			// 判断是否重复导入
			EupsBatchConsoleInfo batchInfo = new EupsBatchConsoleInfo();
			batchInfo.setFleNme(fileName);
			batchInfo.setExeDte(new Date());
			List<EupsBatchConsoleInfo> batchList = eupsBatchConsoleInfoRepository.find(batchInfo);
			if (CollectionUtils.isNotEmpty(batchList)) {
				throw new CoreException(ErrorCodes.EUPS_BATCH_DATA_IS_SUBMIT); // 数据已导入
			}

			// 根据第三方配置信息获取文件
			EupsThdFtpConfig eupsThdFtpInf = eupsThdFtpConfigRepository.findOne("eleGzBchThd");
			eupsThdFtpInf.setLocFleNme(fileName);

			log.info("start get batch file: now thdftp info=" + eupsThdFtpInf.toString());
			operateFTPAction.getFileFromFtp(eupsThdFtpInf);

			List<Map<String, Object>> parseMap = operateFile.pareseFile(eupsThdFtpInf, "eleGzBatFmt"); // 解析文件
			 payDetailLst = new ArrayList<EupsBatchPayEntity>();
			for (Map<String, Object> orgMap : parseMap) {
				EupsBatchPayEntity batchPayDtl = new EupsBatchPayEntity();
				batchPayDtl.setCusAc((String) orgMap.get("cusAc")); // 客户帐号
				BigDecimal txnAmt = NumberUtils.centToYuan((String) orgMap.get("tTxnAmt")); // 交易金额
				batchPayDtl.setTxnAmt(txnAmt);
				batchPayDtl.setAgtSrvCusId((String) orgMap.get("thdCusNo")); // 第三方客户标志
				payDetailLst.add(batchPayDtl);
			}
			context.setData(ParamKeys.EUPS_BATCH_PAY_ENTITY_LIST, payDetailLst);

		}
		dptTyp = fileName.substring(14, 16) + "00"; // 配型部类型

		// TODO:根据dptTyp获取单位编号，待考虑，后续交易中直接根据当前的单位编号查找控制信息表
		context.setData(GDParamKeys.GZ_ELE_DPT_TYP, dptTyp);
//		context.setData(ParamKeys.COMPANY_NO, dptTyp);  :根据dptTyp查找对应的单位编号
		context.setData(ParamKeys.COMPANY_NO, comNo);
		context.setData(ParamKeys.TXN_MDE, Constants.TXN_MDE_FILE);  //文件批扣
		
		
		context.setData("flePreInf", BeanUtils.toMaps(payDetailLst));
		
		System.out.println("context="+context);
		
		return payDetailLst;

	}

}
