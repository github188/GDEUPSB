package com.bocom.bbip.gdeupsb.action.efek.batch;

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
import com.bocom.bbip.eups.repository.EupsThdFtpConfigRepository;
import com.bocom.bbip.eups.spi.service.batch.BatchAcpService;
import com.bocom.bbip.eups.spi.vo.PrepareBatchAcpDomain;
import com.bocom.bbip.gdeupsb.action.common.BatchFileCommon;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.AgtFileBatchDetail;
import com.bocom.bbip.gdeupsb.entity.GDEupsBatchConsoleInfo;
import com.bocom.bbip.gdeupsb.entity.GDEupsEleTmp;
import com.bocom.bbip.gdeupsb.repository.GDEupsBatchConsoleInfoRepository;
import com.bocom.bbip.gdeupsb.repository.GDEupsEleTmpRepository;
import com.bocom.bbip.thd.org.apache.commons.collections.CollectionUtils;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

public class BatchDataFileAction extends BaseAction implements BatchAcpService{
	private final static Log logger=LogFactory.getLog(BatchDataFileAction.class);
		/**
		 * 批量代收付  数据准备  提交
		 */
	@Autowired 
	GDEupsBatchConsoleInfoRepository gdEupsBatchConsoleInfoRepository;
	@Autowired
	GDEupsEleTmpRepository gdEupsEleTmpRepository;
	@Autowired
	EupsThdFtpConfigRepository eupsThdFtpConfigRepository;
	@Autowired
	OperateFileAction operateFileAction;
	@Autowired
	BBIPPublicService bbipPublicService;
	@Autowired
	OperateFTPAction operateFTPAction;
	@Override
	public void prepareBatchDeal(PrepareBatchAcpDomain preparebatchacpdomain,
			Context context) throws CoreException {
		
				logger.info("==========Start  BatchDataFileAction");
				context.setData(ParamKeys.WS_TRANS_CODE, "20");
				//文件名
				String fleNme=context.getData(ParamKeys.FLE_NME).toString();
				//批量前检查和初始化批量控制表 生成批次号batNo
				((BatchFileCommon)get(GDConstants.BATCH_FILE_COMMON_UTILS)).BeforeBatchProcess(context);
					//文件下载
					EupsThdFtpConfig eupsThdFtpConfig=eupsThdFtpConfigRepository.findOne("elecBatch");
					eupsThdFtpConfig.setRmtFleNme(fleNme);
					operateFTPAction.getFileFromFtp(eupsThdFtpConfig);
					//获取文件并解析入库   数据库文件名
					List<Map<String, Object>> mapList=operateFileAction.pareseFile(eupsThdFtpConfig, "batchFile");
						if(CollectionUtils.isEmpty(mapList)){
								throw new CoreException("处理状态异常或获取数据异常");
						}
						String batNo=context.getData(ParamKeys.BAT_NO).toString();
						for (int i=0;i<mapList.size();i++) {
							Map<String, Object> map=mapList.get(i);
							if(i == 0){
									GDEupsBatchConsoleInfo gdEupsBatchConsoleInfo=BeanUtils.toObject(map, GDEupsBatchConsoleInfo.class);
									gdEupsBatchConsoleInfo.setBatNo(batNo);
									//批量代收信息标识  
//									String rsvFld9=context.getData(ParamKeys.TXN_DTE).toString()+context.getData(ParamKeys.TXN_TME).toString()+context.getData(ParamKeys.COMPANY_NO).toString()+context.getData(GDParamKeys.BUS_TYPE).toString()+"00001";
//									gdEupsBatchConsoleInfo.setRsvFld9(rsvFld9);
									gdEupsBatchConsoleInfoRepository.updateConsoleInfo(gdEupsBatchConsoleInfo);
							}else{
									 map.put(ParamKeys.SEQUENCE, bbipPublicService.getBBIPSequence());
									 GDEupsEleTmp gdEupsEleTmp=BeanUtils.toObject(map, GDEupsEleTmp.class);
									 gdEupsEleTmp.setRsvFld5(batNo);
									 logger.info("~~~~~gdEupsEleTmp~~~~"+gdEupsEleTmp);
									 gdEupsEleTmpRepository.insert(gdEupsEleTmp);
							}
						}
						//	文件名		账户类型(2位,PT普通账户)+FS_银行代码(4位)+单位编码（8位）+ 日期（yyyymmdd）(8位)＋批次号（5位）.txt
						//TODO 新文件文件名
						String createFileName="01"+"FS_301"+context.getData(ParamKeys.COMPANY_NO)+context.getData(ParamKeys.TXN_DTE)+batNo.substring(0, 5)+".txt";
						//文件内容
						Map<String, Object> resultMap=createFileMap(context);
						context.setVariable(GDParamKeys.COM_BATCH_AGT_FILE_NAME, createFileName);
						context.setVariable(GDParamKeys.COM_BATCH_AGT_FILE_MAP, resultMap);
						
						//提交代收付
						userProcess(context);
						logger.info("==========End  BatchDataFileAction");
	}
	/**
	 * 文件map拼装
	 */
		public Map<String, Object> createFileMap(Context context){
			logger.info("=================Start  BatchDataFileAction  createFileMap ");
			//header
			Map<String, Object> headMap=new HashMap<String, Object>();
			headMap.put(ParamKeys.COMPANY_NO, context.getData(ParamKeys.COMPANY_NO).toString());
			headMap.put(GDParamKeys.TOT_COUNT, context.getData(GDParamKeys.TOT_CNT));
			headMap.put(ParamKeys.TOT_AMT, context.getData(ParamKeys.TOT_AMT));
			//detail
			GDEupsEleTmp gdEupsEleTmps=new GDEupsEleTmp();
			gdEupsEleTmps.setComNo(context.getData(ParamKeys.COMPANY_NO).toString());
			List<GDEupsEleTmp> gdEupsEleTmpList=gdEupsEleTmpRepository.findAll();
			
			List<AgtFileBatchDetail> detailList=new ArrayList<AgtFileBatchDetail>();
			for (GDEupsEleTmp gdEupsEleTmp : gdEupsEleTmpList) {
				AgtFileBatchDetail agtFileBatchDetail=new AgtFileBatchDetail();
				agtFileBatchDetail.setAGTSRVCUSID(gdEupsEleTmp.getThdCusNo());
				agtFileBatchDetail.setAGTSRVCUSNME(gdEupsEleTmp.getThdCusNme());
				String cusAc=gdEupsEleTmp.getCusAc();
				agtFileBatchDetail.setCUSAC(cusAc);
				//本行标志
				if(get(AccountService.class).isOurBankCard(cusAc)){
							agtFileBatchDetail.setOUROTHFLG("0");
				}else{
							agtFileBatchDetail.setOUROTHFLG("1");
				}
				agtFileBatchDetail.setCUSNME(gdEupsEleTmp.getCusNme());
				agtFileBatchDetail.setOBKBK(gdEupsEleTmp.getBankNo());
				
				agtFileBatchDetail.setTXNAMT(gdEupsEleTmp.getTxnAmt());
				//备注 不用
				detailList.add(agtFileBatchDetail);
			}
			Map<String, Object> resultMap=new HashMap<String, Object>();
			resultMap.put(ParamKeys.EUPS_FILE_HEADER, headMap);
			resultMap.put(ParamKeys.EUPS_FILE_DETAIL, BeanUtils.toMap(detailList));
			
			logger.info("~~~~~~~~resultMap~~~~~~~~"+resultMap.get(ParamKeys.EUPS_FILE_DETAIL));
			logger.info("=================End  BatchDataFileAction  createFileMap ");
			return resultMap;
		}
/**
 * 异步调用process
 */
		public void userProcess(Context context)throws CoreException{
			String mothed="eups.batchPaySubmitDataProcess";
			bbipPublicService.synExecute(mothed, context);
		}
}

