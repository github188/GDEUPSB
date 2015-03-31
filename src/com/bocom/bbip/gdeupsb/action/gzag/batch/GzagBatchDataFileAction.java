package com.bocom.bbip.gdeupsb.action.gzag.batch;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
import com.bocom.bbip.eups.entity.EupsActSysPara;
import com.bocom.bbip.eups.entity.EupsThdFtpConfig;
import com.bocom.bbip.eups.repository.EupsActSysParaRepository;
import com.bocom.bbip.eups.repository.EupsThdBaseInfoRepository;
import com.bocom.bbip.eups.repository.EupsThdFtpConfigRepository;
import com.bocom.bbip.eups.spi.service.batch.BatchAcpService;
import com.bocom.bbip.eups.spi.vo.PrepareBatchAcpDomain;
import com.bocom.bbip.gdeupsb.action.common.BatchFileCommon;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.AgtFileBatchDetail;
import com.bocom.bbip.gdeupsb.entity.GDEupsBatchConsoleInfo;
import com.bocom.bbip.gdeupsb.entity.GDEupsGzagBatchTmp;
import com.bocom.bbip.gdeupsb.repository.GDEupsBatchConsoleInfoRepository;
import com.bocom.bbip.gdeupsb.repository.GDEupsGzagBatchTmpRepository;
import com.bocom.bbip.thd.org.apache.commons.collections.CollectionUtils;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.DateUtils;
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
		
			logger.info("=============Start  BatchDataFileAction  prepareBatchDeal");
			String comNo=context.getData(ParamKeys.COMPANY_NO).toString();
			String fileName=context.getData(ParamKeys.FLE_NME).toString();
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
			//传入控制表 选择翻盘文件是使用
			context.setData("fileId", fileId);
			logger.info("~~~~~~~~~~~~~fileId=["+fileId+"]");
			
			//获取批次号
			((BatchFileCommon)get(GDConstants.BATCH_FILE_COMMON_UTILS)).BeforeBatchProcess(context);
			
			//得到文件
			EupsThdFtpConfig eupsThdFtpConfig=eupsThdFtpConfigRepository.findOne(fileId);
			eupsThdFtpConfig.setFtpDir("1");
			eupsThdFtpConfig.setLocFleNme(fileName);
			eupsThdFtpConfig.setRmtFleNme(fileName);
			operateFTPAction.getFileFromFtp(eupsThdFtpConfig);
			logger.info("==============Get file Success ");
			String batNo=context.getData(ParamKeys.BAT_NO).toString();
			//获取文件并解析入库
			List<Map<String, Object>> mapList=operateFileAction.pareseFile(eupsThdFtpConfig, fileId);
			if(CollectionUtils.isEmpty(mapList)){
					throw new CoreException("处理状态异常");
			}
			if(fileId.equals("lottBatchFile")){
				updateInfoAboutLott(context,eupsThdFtpConfig,batNo);
			}else if(fileId.equals("sptltBatchFile")){
				
			}
			context.setData("listTotCnt", mapList.size());
			BigDecimal listTotAmt=new BigDecimal("0.00");
			for (Map<String, Object> map : mapList) {
						map.put(ParamKeys.SEQUENCE, bbipPublicService.getBBIPSequence());
						GDEupsGzagBatchTmp gdEupsGzagBatchTmp=BeanUtils.toObject(map,GDEupsGzagBatchTmp.class);
						BigDecimal txnAmt=gdEupsGzagBatchTmp.getTxnAmt().scaleByPowerOfTen(-2);
						gdEupsGzagBatchTmp.setTxnAmt(txnAmt);
						gdEupsGzagBatchTmp.setBakFld(comNo);
						gdEupsGzagBatchTmp.setRsvFld1(batNo);
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
			
			context.setData(ParamKeys.TOT_CNT, mapList.size());
			context.setData(ParamKeys.TOT_AMT, listTotAmt);
			logger.info("=================End  BatchDataFileAction  prepareBatchDeal");
	}
	/**
	 * 文件map拼装
	 */
		public Map<String, Object> createFileMap(Context context,String comNo){
			logger.info("=================Start  BatchDataFileAction  createFileMap ");
			Map<String, Object> resultMap=new HashMap<String, Object>();
			EupsActSysPara eupsActSysPara=new EupsActSysPara();
			eupsActSysPara.setComNo(comNo);
			eupsActSysPara.setActSysTyp("0");
			EupsActSysPara eupsActSysParaInfo=get(EupsActSysParaRepository.class).find(eupsActSysPara).get(0);
			String sqlNo=eupsActSysParaInfo.getSplNo();
			//header
			Map<String, Object> headMap=new HashMap<String, Object>();
			headMap.put(ParamKeys.COMPANY_NO,sqlNo);
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
				agtFileBatchDetail.setRMK2(gdEupsGzagBatchTmp.getThdCusNo());
				//备注 不用
				detailList.add(agtFileBatchDetail);
			}
			resultMap.put(ParamKeys.EUPS_FILE_HEADER, headMap);
			resultMap.put(ParamKeys.EUPS_FILE_DETAIL, BeanUtils.toMaps(detailList));
			
			logger.info("=================End  BatchDataFileAction  createFileMap ");
			return resultMap;
		}

/**
 * lottBatchFile 写入第一行
 */
		public void updateInfoAboutLott(Context context,EupsThdFtpConfig eupsThdFtpConfig,String batNo) throws CoreException{
				logger.info("=================Start  BatchDataFileAction  updateInfoAboutLott");
				String fleName=context.getData(ParamKeys.FLE_NME).toString();
				String filePath=eupsThdFtpConfig.getLocDir();
				try {
					FileReader fileReader = new FileReader(filePath+"//"+fleName);
					BufferedReader bufferedReader=new BufferedReader(fileReader);
					String firstLine=null;
					int i=0;
					while((firstLine=bufferedReader.readLine())!=null && i<1){
							GDEupsBatchConsoleInfo gdEupsBatchConsoleInfo=new GDEupsBatchConsoleInfo();
							String subDte=firstLine.substring(0,8);
							String totCnt=firstLine.substring(8,16).trim();
							BigDecimal totAmt=(new BigDecimal(firstLine.substring(16).trim())).scaleByPowerOfTen(-2);
							
							gdEupsBatchConsoleInfo.setSubDte(DateUtils.parse(subDte,DateUtils.STYLE_yyyyMMdd));
							gdEupsBatchConsoleInfo.setTotCnt(Integer.parseInt(totCnt));
							gdEupsBatchConsoleInfo.setTotAmt(totAmt);
							gdEupsBatchConsoleInfo.setBatNo(batNo);
							gdEupsBatchConsoleInfoRepository.updateConsoleInfo(gdEupsBatchConsoleInfo);
							logger.info("==========Successful to update GDEupsBatchConsoleInfo");
							i++;
					}
					bufferedReader.close();
					fileReader.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				logger.info("=================End  BatchDataFileAction  updateInfoAboutLott");
		}

}

