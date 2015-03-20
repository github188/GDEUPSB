package com.bocom.bbip.gdeupsb.action.gzag.batch;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.comp.account.AccountService;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.action.common.OperateFTPAction;
import com.bocom.bbip.eups.action.common.OperateFileAction;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsThdFtpConfig;
import com.bocom.bbip.eups.repository.EupsThdBaseInfoRepository;
import com.bocom.bbip.eups.repository.EupsThdFtpConfigRepository;
import com.bocom.bbip.eups.spi.service.batch.BatchAcpService;
import com.bocom.bbip.eups.spi.vo.PrepareBatchAcpDomain;
import com.bocom.bbip.gdeupsb.action.common.BatchFileCommon;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.AgtFileBatchDetail;
import com.bocom.bbip.gdeupsb.entity.GDEupsGzagBatchTmp;
import com.bocom.bbip.gdeupsb.repository.GDEupsBatchConsoleInfoRepository;
import com.bocom.bbip.gdeupsb.repository.GDEupsGzagBatchTmpRepository;
import com.bocom.bbip.thd.org.apache.commons.collections.CollectionUtils;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

public class GzagBatchDataFileAction extends BaseAction implements BatchAcpService{
	private final static Log logger=LogFactory.getLog(GzagBatchDataFileAction.class);
		/**
		 * 广州文本  数据准备
		 */
	@Autowired 
	GDEupsBatchConsoleInfoRepository gdEupsBatchConsoleInfoRepository;
	@Autowired
	GDEupsGzagBatchTmpRepository gdEupsGzagBatchTmpRepository;
	@Autowired
	EupsThdBaseInfoRepository eupsThdBaseInfoRepository;
	@Autowired
	EupsThdFtpConfigRepository eupsThdFtpConfigRepository;
	@Autowired
	BBIPPublicService bbipPublicService;
	@Autowired
	OperateFileAction operateFileAction;
	@Autowired
	OperateFTPAction operateFTPAction;
	@Override
	public void prepareBatchDeal(PrepareBatchAcpDomain preparebatchacpdomain,
			Context context) throws CoreException {
		
			logger.info("=============Start  BatchDataFileAction");
			String comNo=context.getData(ParamKeys.COMPANY_NO).toString();
			String fileName=context.getData(ParamKeys.FLE_NME).toString();
			//获取批次号
			((BatchFileCommon)get(GDConstants.BATCH_FILE_COMMON_UTILS)).BeforeBatchProcess(context);
			context.setData(ParamKeys.WS_TRANS_CODE, "99");
			//文件名称
			String fileId="";
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
					throw new CoreException("没有该单位");
			}
			logger.info("~~~~~~~~~~~~~fileId=["+fileId+"]");
			context.setData("fileId", fileId);
			//得到文件
			EupsThdFtpConfig eupsThdFtpConfig=eupsThdFtpConfigRepository.findOne(fileId);
			eupsThdFtpConfig.setFtpDir("1");
			eupsThdFtpConfig.setLocFleNme(fileName);
			eupsThdFtpConfig.setRmtFleNme(fileName);
			operateFTPAction.getFileFromFtp(eupsThdFtpConfig);
			logger.info("==============Get file Success ");
			//获取文件并解析入库
			List<Map<String, Object>> mapList=operateFileAction.pareseFile(eupsThdFtpConfig, fileId);
			if(CollectionUtils.isEmpty(mapList)){
					throw new CoreException("处理状态异常");
			}
			context.setData("listTotCnt", mapList.size());
			BigDecimal listTotAmt=new BigDecimal("0.00");
			for (Map<String, Object> map : mapList) {
						if(fileId.equals("lottBatchFile") || fileId.equals("sptltBatchFile")){
								map.put(ParamKeys.EUPS_FILE_HEADER, BeanUtils.toMap(context));
						}
						map.put(ParamKeys.SEQUENCE, bbipPublicService.getBBIPSequence());
						GDEupsGzagBatchTmp gdEupsGzagBatchTmp=BeanUtils.toObject(map,GDEupsGzagBatchTmp.class);
						BigDecimal txnAmt=gdEupsGzagBatchTmp.getTxnAmt().scaleByPowerOfTen(-2);
						gdEupsGzagBatchTmp.setTxnAmt(txnAmt);
						gdEupsGzagBatchTmp.setBakFld(comNo);
						gdEupsGzagBatchTmpRepository.insert(gdEupsGzagBatchTmp);
						listTotAmt=listTotAmt.add(txnAmt);
			}
			logger.info("~~~~~~~~~~~~~End  insert  (获取文件并解析入库)"+listTotAmt);
			//TODO 判断 总金额 总笔数是否相等
			context.setData("listTotAmt", listTotAmt);

			//文件内容
			Map<String, Object> resultMap=createFileMap(context,comNo);
			context.setVariable(GDParamKeys.COM_BATCH_AGT_FILE_NAME, fileName);
			context.setVariable(GDParamKeys.COM_BATCH_AGT_FILE_MAP, resultMap);
			context.setData(ParamKeys.COMPANY_NO, comNo);
			get(BatchFileCommon.class).sendBatchFileToACP(context);
			
			logger.info("=================End  BatchDataFileAction");
	}
	/**
	 * 文件map拼装
	 */
		public Map<String, Object> createFileMap(Context context,String comNo){
			logger.info("=================Start  BatchDataFileAction  createFileMap ");
			Map<String, Object> resultMap=new HashMap<String, Object>();
			//header
			Map<String, Object> headMap=new HashMap<String, Object>();
			headMap.put(ParamKeys.COMPANY_NO,comNo);
			headMap.put(GDParamKeys.TOT_COUNT, context.getData("listTotCnt"));
			headMap.put(ParamKeys.TOT_AMT, context.getData("listTotAmt"));

			//detail
			GDEupsGzagBatchTmp gdEupsGzagBatchTmps=new GDEupsGzagBatchTmp();
			gdEupsGzagBatchTmps.setBakFld(comNo);
			List<GDEupsGzagBatchTmp> gdEupsGzagBatchTmpList=gdEupsGzagBatchTmpRepository.find(gdEupsGzagBatchTmps);
			
			List<AgtFileBatchDetail> detailList=new ArrayList<AgtFileBatchDetail>();
			for (GDEupsGzagBatchTmp gdEupsGzagBatchTmp : gdEupsGzagBatchTmpList) {
				AgtFileBatchDetail agtFileBatchDetail=new AgtFileBatchDetail();
				agtFileBatchDetail.setAGTSRVCUSID(gdEupsGzagBatchTmp.getThdCusNo());
				agtFileBatchDetail.setAGTSRVCUSNME(gdEupsGzagBatchTmp.getThdCusNme());
				String cusAc=gdEupsGzagBatchTmp.getCusAc();
				agtFileBatchDetail.setCUSAC(cusAc);
				//本行标志
				if(get(AccountService.class).isOurBankCard(cusAc)){
				agtFileBatchDetail.setOUROTHFLG("0");
				}else{
					agtFileBatchDetail.setOUROTHFLG("1");
				}
				agtFileBatchDetail.setCUSNME(gdEupsGzagBatchTmp.getThdCusNme());
//				agtFileBatchDetail.setOBKBK("443999");
				
				agtFileBatchDetail.setTXNAMT(gdEupsGzagBatchTmp.getTxnAmt());
				//备注 不用
				detailList.add(agtFileBatchDetail);
			}
			resultMap.put(ParamKeys.EUPS_FILE_HEADER, headMap);
			resultMap.put(ParamKeys.EUPS_FILE_DETAIL, BeanUtils.toMaps(detailList));
			
			System.out.println("~~~~~~~~resultMap~~~~~~~~"+resultMap.get(ParamKeys.EUPS_FILE_DETAIL));
			logger.info("=================End  BatchDataFileAction  createFileMap ");
			return resultMap;
		}
}

