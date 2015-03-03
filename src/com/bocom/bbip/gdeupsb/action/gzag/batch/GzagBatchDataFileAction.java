package com.bocom.bbip.gdeupsb.action.gzag.batch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.action.common.OperateFileAction;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsThdFtpConfig;
import com.bocom.bbip.eups.repository.EupsThdBaseInfoRepository;
import com.bocom.bbip.eups.repository.EupsThdFtpConfigRepository;
import com.bocom.bbip.eups.spi.service.batch.BatchAcpService;
import com.bocom.bbip.eups.spi.vo.PrepareBatchAcpDomain;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.AgtFileBatchDetail;
import com.bocom.bbip.gdeupsb.entity.GDEupsGzagBatchTmp;
import com.bocom.bbip.gdeupsb.repository.GDEupsBatchConsoleInfoRepository;
import com.bocom.bbip.gdeupsb.repository.GDEupsEleTmpRepository;
import com.bocom.bbip.gdeupsb.repository.GDEupsGzagBatchTmpRepository;
import com.bocom.bbip.thd.org.apache.commons.collections.CollectionUtils;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

public class GzagBatchDataFileAction implements BatchAcpService{
	private final static Log logger=LogFactory.getLog(GzagBatchDataFileAction.class);
		/**
		 * 广州文本  数据准备
		 */
	@Autowired 
	GDEupsBatchConsoleInfoRepository gdEupsBatchConsoleInfoRepository;
	@Autowired
	GDEupsGzagBatchTmpRepository gdEupsGzagBatchTmpRepository;
	@Autowired
	GDEupsEleTmpRepository gdEupsEleTmpRepository;
	@Autowired
	EupsThdBaseInfoRepository eupsThdBaseInfoRepository;
	@Autowired
	EupsThdFtpConfigRepository eupsThdFtpConfigRepository;
	@Autowired
	BBIPPublicService bbipPublicService;
	@Autowired
	OperateFileAction operateFileAction;
	@Override
	public void prepareBatchDeal(PrepareBatchAcpDomain preparebatchacpdomain,
			Context context) throws CoreException {
		
			logger.info("==========Start  BatchDataFileAction");
			context.setData(ParamKeys.WS_TRANS_CODE, "99");
			String comNo=context.getData(ParamKeys.COMPANY_NO).toString();
			EupsThdFtpConfig eupsThdFtpConfig=eupsThdFtpConfigRepository.findOne("gzagBatch");
			//文件名称
			String fileName=eupsThdFtpConfig.getLocFleNme();
			String fileId=comNo+"BatchFile";
			//TODO  根据单位名称判断fieId 选择生成文件格式
//			if("insu".equals(comNo)){
//					 fileId="insuBatchFile";
//			}else if("lott".equals(comNo)){
//					 fileId="lottBatchFile";
//			} else if("yct".equals(comNo)){
//					 fileId="yctBatchFile";
//			}else if("ykt".equals(comNo)){
//					 fileId="yktBatchFile";
//			}else if("sptlt".equals(comNo)){
//					 fileId="sptltBatchFile";
//			}else{
//				logger.info("没有该单位编号");
//			}
			context.setData("fileId", fileId);
			//获取文件并解析入库
			List<Map<String, Object>> mapList=operateFileAction.pareseFile(eupsThdFtpConfig, fileId);
			if(CollectionUtils.isEmpty(mapList)){
					throw new CoreException("处理状态异常");
			}
			for (Map<String, Object> map : mapList) {
					map.put(ParamKeys.EUPS_FILE_HEADER, BeanUtils.toMap(context));
					map.put(ParamKeys.SEQUENCE, bbipPublicService.getBBIPSequence());
					GDEupsGzagBatchTmp gdEupsGzagBatchTmp=BeanUtils.toObject(map,GDEupsGzagBatchTmp.class);
					gdEupsGzagBatchTmp.setBakFld(comNo);
					gdEupsGzagBatchTmpRepository.insert(gdEupsGzagBatchTmp);
			}
			logger.info("~~~~~~~~~~~~~End  insert  (获取文件并解析入库)");

			//文件内容
			Map<String, Object> resultMap=createFileMap(context);
			context.setVariable(GDParamKeys.COM_BATCH_AGT_FILE_NAME, fileName);
			context.setVariable(GDParamKeys.COM_BATCH_AGT_FILE_MAP, resultMap);
			
			logger.info("=================End  BatchDataFileAction");
	}
	/**
	 * 文件map拼装
	 */
		public Map<String, Object> createFileMap(Context context){
			logger.info("=================Start  BatchDataFileAction  createFileMap ");
			Map<String, Object> resultMap=new HashMap<String, Object>();
			//header
			Map<String, Object> headMap=new HashMap<String, Object>();
			headMap.put(ParamKeys.COMPANY_NO, context.getData(ParamKeys.COMPANY_NO).toString());
			headMap.put(GDParamKeys.TOT_COUNT, context.getData(GDParamKeys.TOT_CNT));
			headMap.put(ParamKeys.TOT_AMT, context.getData(ParamKeys.TOT_AMT));

			//detail
			GDEupsGzagBatchTmp gdEupsGzagBatchTmps=new GDEupsGzagBatchTmp();
			gdEupsGzagBatchTmps.setBakFld(context.getData(ParamKeys.COMPANY_NO).toString());
			List<GDEupsGzagBatchTmp> gdEupsGzagBatchTmpList=gdEupsGzagBatchTmpRepository.find(gdEupsGzagBatchTmps);
			
			List<AgtFileBatchDetail> detailList=new ArrayList<AgtFileBatchDetail>();
			for (GDEupsGzagBatchTmp gdEupsGzagBatchTmp : gdEupsGzagBatchTmpList) {
				AgtFileBatchDetail agtFileBatchDetail=new AgtFileBatchDetail();
				agtFileBatchDetail.setAGTSRVCUSID(gdEupsGzagBatchTmp.getThdCusNo());
				agtFileBatchDetail.setAGTSRVCUSNME(gdEupsGzagBatchTmp.getThdCusNme());
				String cusAc=gdEupsGzagBatchTmp.getCusAc();
				agtFileBatchDetail.setCUSAC(cusAc);
				//本行标志
//				if(get(AccountService.class).isOurBankCard(cusAc)){
				agtFileBatchDetail.setOUROTHFLG("0");
//				}else{
//					agtFileBatchDetail.setOUROTHFLG("1");
//				}
				agtFileBatchDetail.setCUSNME(gdEupsGzagBatchTmp.getThdCusNme());
				agtFileBatchDetail.setOBKBK("443999");
				
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

