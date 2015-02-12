package com.bocom.bbip.gdeupsb.action.efek.batch;

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
import com.bocom.bbip.eups.repository.EupsThdFtpConfigRepository;
import com.bocom.bbip.eups.spi.service.batch.BatchAcpService;
import com.bocom.bbip.eups.spi.vo.PrepareBatchAcpDomain;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.AgtFileBatchDetail;
import com.bocom.bbip.gdeupsb.entity.GDEupsBatchConsoleInfo;
import com.bocom.bbip.gdeupsb.entity.GDEupsEleTmp;
import com.bocom.bbip.gdeupsb.repository.GDEupsBatchConsoleInfoRepository;
import com.bocom.bbip.gdeupsb.repository.GDEupsEleTmpRepository;
import com.bocom.bbip.thd.org.apache.commons.collections.CollectionUtils;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

public class BatchDataFileAction implements BatchAcpService{
	private final static Log logger=LogFactory.getLog(BatchDataFileAction.class);
		/**
		 * 批量代收付  数据准备
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
	@Override
	public void prepareBatchDeal(PrepareBatchAcpDomain preparebatchacpdomain,
			Context context) throws CoreException {
		
			logger.info("==========Start  BatchDataFileAction");
			context.setData(ParamKeys.WS_TRANS_CODE, "20");
			//判断是否处理过
			//文件名
			String fleNme=context.getData(ParamKeys.FLE_NME).toString();
					
					EupsThdFtpConfig eupsThdFtpConfig=eupsThdFtpConfigRepository.findOne("elecBatch");
					//文件下载
//					downloadFileToThird(eupsThdFtpConfig);
					String fileId="batchFile";
					//获取文件并解析入库   数据库文件名
					List<Map<String, Object>> mapList=operateFileAction.pareseFile(eupsThdFtpConfig, fileId);
						if(CollectionUtils.isEmpty(mapList)){
								throw new CoreException("~~~~~~~~~~~~处理状态异常");
						}
						String batNo=context.getData(ParamKeys.BAT_NO).toString();
						for (int i=0;i<mapList.size();i++) {
							Map<String, Object> map=new HashMap<String, Object>();
							if(i==0){
									GDEupsBatchConsoleInfo gdEupsBatchConsoleInfo=BeanUtils.toObject(map, GDEupsBatchConsoleInfo.class);
									gdEupsBatchConsoleInfoRepository.insert(gdEupsBatchConsoleInfo);
							}else{
									map.put(ParamKeys.SEQUENCE, bbipPublicService.getBBIPSequence());
									 GDEupsEleTmp gdEupsEleTmp=BeanUtils.toObject(map, GDEupsEleTmp.class);
									 gdEupsEleTmp.setRsvFld5(batNo);
									 logger.info("~~~~~map~~~~"+map);
									 gdEupsEleTmpRepository.insert(gdEupsEleTmp);
							}
						}
				//生成文件
						
						//	文件名		账户类型(2位,PT普通账户)+FS_银行代码(4位)+单位编码（8位）+ 日期（yyyymmdd）(8位)＋批次号（5位）.txt
						String cusType=context.getData(ParamKeys.RSV_FLD3).toString();
						String bankNo=context.getData(ParamKeys.RSV_FLD2).toString();
						String comNo=context.getData(ParamKeys.COMPANY_NO).toString()	;
						String date=DateUtils.format(DateUtils.parse(context.getData(GDParamKeys.SUB_DATE).toString()),DateUtils.STYLE_yyyyMMdd);
						//批次号
						String createFileName=cusType+bankNo+comNo+date+batNo.substring(0, 5)+".txt";
						
						//文件内容
						Map<String, Object> resultMap=createFileMap(context);
						context.setVariable(GDParamKeys.COM_BATCH_AGT_FILE_NAME, createFileName);
						context.setVariable(GDParamKeys.COM_BATCH_AGT_FILE_MAP, resultMap);
						logger.info("==========End  BatchDataFileAction");
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
			System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~");
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
//				if(get(AccountService.class).isOurBankCard(cusAc)){
				agtFileBatchDetail.setOUROTHFLG("0");
//				}else{
//					agtFileBatchDetail.setOUROTHFLG("1");
//				}
				agtFileBatchDetail.setCUSNME(gdEupsEleTmp.getCusNme());
				agtFileBatchDetail.setOBKBK(gdEupsEleTmp.getBankNo());
				
				agtFileBatchDetail.setTXNAMT(gdEupsEleTmp.getTxnAmt());
				//备注 不用
				detailList.add(agtFileBatchDetail);
			}
			for(int i=0;i<detailList.size();i++){
			System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~"+detailList.get(i));
			}
			resultMap.put(ParamKeys.EUPS_FILE_HEADER, headMap);
			resultMap.put(ParamKeys.EUPS_FILE_DETAIL, BeanUtils.toMap(detailList));
			
			System.out.println("================================="+BeanUtils.toMaps(detailList));
			System.out.println("~~~~~~~~resultMap~~~~~~~~"+resultMap.get(ParamKeys.EUPS_FILE_DETAIL));
			logger.info("=================End  BatchDataFileAction  createFileMap ");
			return resultMap;
		}

}

