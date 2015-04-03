package com.bocom.bbip.gdeupsb.action.efek.batch;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
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
import com.bocom.bbip.eups.entity.EupsBatchConsoleInfo;
import com.bocom.bbip.eups.entity.EupsThdFtpConfig;
import com.bocom.bbip.eups.repository.EupsBatchConsoleInfoRepository;
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
import com.bocom.bbip.utils.DateUtils;
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
		
				logger.info("==========Start  BatchDataFileAction  prepareBatchDeal");
				String totAmt=context.getData("totAmt").toString();
				String totCnt=context.getData("totCnt").toString();
				String comNo=context.getData("comNo").toString();
				context.setData(ParamKeys.WS_TRANS_CODE, "20");
				//文件名
				String fleNme=context.getData(ParamKeys.FLE_NME).toString();
				//批量前检查和初始化批量控制表 生成批次号batNo
				((BatchFileCommon)get(GDConstants.BATCH_FILE_COMMON_UTILS)).BeforeBatchProcess(context);
					//文件下载
					EupsThdFtpConfig eupsThdFtpConfig=eupsThdFtpConfigRepository.findOne("elecBatch");
					eupsThdFtpConfig.setRmtFleNme(fleNme);
					eupsThdFtpConfig.setLocFleNme(fleNme);
					operateFTPAction.getFileFromFtp(eupsThdFtpConfig);
					String batNo=context.getData(ParamKeys.BAT_NO).toString();
					//该更控制表
					updateInfo(context, eupsThdFtpConfig, batNo,totAmt ,totCnt);
					//获取文件并解析入库   数据库文件名
					List<Map<String, Object>> mapList=operateFileAction.pareseFile(eupsThdFtpConfig, "batchFile");
					if(CollectionUtils.isEmpty(mapList)){
								throw new CoreException("处理状态异常或获取数据异常");
					}
						context.setData(ParamKeys.COMPANY_NO, comNo);
						for (int i=0;i<mapList.size();i++) {
							Map<String, Object> map=mapList.get(i);
									 map.put(ParamKeys.SEQUENCE, bbipPublicService.getBBIPSequence());
									 GDEupsEleTmp gdEupsEleTmp=BeanUtils.toObject(map, GDEupsEleTmp.class);
									 if(!map.get("capital").toString().equals("")){
										 	map.put("capital", (new BigDecimal(map.get("capital").toString()).scaleByPowerOfTen(-2)));
									 }
									 if(!map.get("dedit").toString().equals("")){
										 	map.put("dedit", (new BigDecimal(map.get("capital").toString()).scaleByPowerOfTen(-2)));
									 }
									 BigDecimal txnAmt=gdEupsEleTmp.getPaymentMoney().scaleByPowerOfTen(-2);
									 gdEupsEleTmp.setTxnAmt(txnAmt);
									 gdEupsEleTmp.setRsvFld5(batNo);
									 logger.info("~~~~~gdEupsEleTmp~~~~"+gdEupsEleTmp);
									 gdEupsEleTmpRepository.insert(gdEupsEleTmp);
						}
						String createFileName=context.getData(ParamKeys.FLE_NME).toString();
						//文件内容
						Map<String, Object> resultMap=createFileMap(context,comNo,totAmt,totCnt);
						context.setVariable(GDParamKeys.COM_BATCH_AGT_FILE_NAME, createFileName);
						context.setVariable(GDParamKeys.COM_BATCH_AGT_FILE_MAP, resultMap);
						//提交代收付
						userProcessToSubmit(context);
						//得到反盘文件 
//						userProcessToGet(context);
						logger.info("==========End  BatchDataFileAction  prepareBatchDeal");
	}
	/**
	 * 文件map拼装
	 */
		public Map<String, Object> createFileMap(Context context,String comNo,String totAmt,String totCnt){
			logger.info("=================Start  BatchDataFileAction  createFileMap ");
			BigDecimal totAmts=(new BigDecimal(totAmt)).scaleByPowerOfTen(-2);
			//header
			Map<String, Object> headMap=new HashMap<String, Object>();
			headMap.put(ParamKeys.COMPANY_NO, comNo);
			headMap.put(ParamKeys.COMPANY_NO, "4840000703");
			headMap.put(GDParamKeys.TOT_COUNT, totCnt);
			headMap.put(ParamKeys.TOT_AMT, totAmts);
			//detail
			GDEupsEleTmp gdEupsEleTmps=new GDEupsEleTmp();
			gdEupsEleTmps.setComNo(comNo);
			List<GDEupsEleTmp> gdEupsEleTmpList=gdEupsEleTmpRepository.findAllOrderBySqn();
			
			List<AgtFileBatchDetail> detailList=new ArrayList<AgtFileBatchDetail>();
			for (GDEupsEleTmp gdEupsEleTmp : gdEupsEleTmpList) {
				AgtFileBatchDetail agtFileBatchDetail=new AgtFileBatchDetail();
				String cusAc=gdEupsEleTmp.getCusAc();
				agtFileBatchDetail.setCUSAC(cusAc);
				agtFileBatchDetail.setTXNAMT(gdEupsEleTmp.getTxnAmt());
				agtFileBatchDetail.setCUSNME(gdEupsEleTmp.getCusNme());
				agtFileBatchDetail.setAGTSRVCUSID(gdEupsEleTmp.getThdCusNo());
				agtFileBatchDetail.setAGTSRVCUSNME(gdEupsEleTmp.getThdCusNme());
				agtFileBatchDetail.setRMK2(gdEupsEleTmp.getThdCusNo());
				//本行标志
				if(get(AccountService.class).isOurBankCard(cusAc)){
					agtFileBatchDetail.setOUROTHFLG("0");
				}else{
					agtFileBatchDetail.setOUROTHFLG("1");
				}
				agtFileBatchDetail.setOBKBK(gdEupsEleTmp.getBankNo());
				
				if(gdEupsEleTmp.getDedit() !=null){
					agtFileBatchDetail.setTXNAMT(gdEupsEleTmp.getTxnAmt().add(gdEupsEleTmp.getDedit()));
				}
				//备注 不用
				detailList.add(agtFileBatchDetail);
			}
			Map<String, Object> resultMap=new HashMap<String, Object>();
			resultMap.put(ParamKeys.EUPS_FILE_HEADER, headMap);
			resultMap.put(ParamKeys.EUPS_FILE_DETAIL, BeanUtils.toMaps(detailList));
			
			logger.info("~~~~~~~~resultMap~~~~~~~~"+resultMap.get(ParamKeys.EUPS_FILE_DETAIL));
			logger.info("=================End  BatchDataFileAction  createFileMap ");
			return resultMap;
		}
	/**
	 *  同步调用process  批量代扣数据提交
	 */
		public void userProcessToSubmit(Context context)throws CoreException{
			logger.info("==========Start  BatchDataFileAction  userProcessToSubmit");
			//生成代收付文件
			get(BatchFileCommon.class).sendBatchFileToACP(context);
			Date date=new Date();
			context.setData("reqTme",DateUtils.formatAsSimpleDate(date)+"T"+DateUtils.format(date, "HH:mm:ss"));
			//提交
			String mothed="eups.batchPaySubmitDataProcess";
			context.setData(ParamKeys.RSV_FLD2, context.getData(ParamKeys.THD_SQN));
			bbipPublicService.synExecute(mothed, context);
			String	rsvFld2=context.getData(ParamKeys.THD_SQN).toString();
			EupsBatchConsoleInfo eupsBatchConsoleInfo =new EupsBatchConsoleInfo();
			eupsBatchConsoleInfo.setRsvFld2(rsvFld2);
			String batNo=get(EupsBatchConsoleInfoRepository.class).find(eupsBatchConsoleInfo).get(0).getBatNo();
			context.setData(ParamKeys.BAT_NO, batNo);
			
			logger.info("==========End  BatchDataFileAction  userProcessToSubmit");

		}
//	/**
//	 * 同步调用process  代收付回调函数：解析回盘文件并入库
//	 */
//		public void userProcessToGet(Context context)throws CoreException{
//			logger.info("==========Start  BatchDataFileAction  userProcessToGet");
//			log.info(">>>>>Start  Down  AGTS  FileResult <<<<<<");
//			//文件下载
//			EupsThdFtpConfig eupsThdFtpConfig = get(EupsThdFtpConfigRepository.class).findOne(ParamKeys.FTPID_BATCH_PAY_FILE_TO_ACP);
//			String fileName=context.getData("batNo")+".result";
//			eupsThdFtpConfig.setLocDir(fileName);
//			eupsThdFtpConfig.setRmtFleNme(fileName);
//			eupsThdFtpConfig.setFtpDir("1");
//			operateFTPAction.getFileFromFtp(eupsThdFtpConfig);
//			log.info(">>>>>Down Result File Success<<<<<<");
//			String mothed="eups.commNotifyBatchStatus";
//			bbipPublicService.synExecute(mothed, context);
//			logger.info("==========End  BatchDataFileAction  userProcessToGet");
//		}
	/**
	 * 第一行数据保存到控制表中
	 */
		public void updateInfo(Context context,EupsThdFtpConfig eupsThdFtpConfig,String batNo,String totAmt,String totCnt) throws CoreException{
				logger.info("==========Start  BatchDataFileAction  updateInfo");
				String fleNme=context.getData(ParamKeys.FLE_NME).toString();
				String filePath=eupsThdFtpConfig.getLocDir();
				try {
						FileReader fileReader=new FileReader(filePath+"//"+fleNme);
						BufferedReader bufferedReader=new BufferedReader(fileReader);
						String firstLine=null;
						int i=0;
						while((firstLine=bufferedReader.readLine())!=null && i<1){
									GDEupsBatchConsoleInfo gdEupsBatchConsoleInfo=new GDEupsBatchConsoleInfo();
									String rsvFld5=firstLine.substring(0,27);   //27
									gdEupsBatchConsoleInfo.setRsvFld5(rsvFld5);
									String rsvFld8=firstLine.substring(27,31); //4
									gdEupsBatchConsoleInfo.setRsvFld8(rsvFld8);
									String comNo=firstLine.substring(31,39); //8
									gdEupsBatchConsoleInfo.setComNo(comNo);
									String subDte=firstLine.substring(39,47); //8
									gdEupsBatchConsoleInfo.setSubDte(DateUtils.parse(subDte));
									String rsvFld7=firstLine.substring(47,52); //5
									gdEupsBatchConsoleInfo.setRsvFld7(rsvFld7);
									String rsvFld6=firstLine.substring(52,55); //3
									gdEupsBatchConsoleInfo.setRsvFld6(rsvFld6);
									String rsvFld4=firstLine.substring(55,58); //3
									gdEupsBatchConsoleInfo.setRsvFld4(rsvFld4);
									String rTotCnt=firstLine.substring(58,69); //11

									if(!rTotCnt.trim().equals(totCnt.trim())){
											throw new CoreException("总笔数不相同");
									}
									gdEupsBatchConsoleInfo.setTotCnt(Integer.parseInt(totCnt));
									String rTotAmt=firstLine.substring(69,85); //16
									if(!rTotAmt.trim().equals(totAmt.trim())){
										throw new CoreException("总金额不相同");
									}
									BigDecimal totTxnAmt=(new BigDecimal(totAmt)).scaleByPowerOfTen(-2);
									gdEupsBatchConsoleInfo.setTotAmt(totTxnAmt);
									context.setData("totAmt", totTxnAmt);
									String rsvFld1=firstLine.substring(85);
									String rsvFld2=context.getData(ParamKeys.THD_SQN).toString();
									gdEupsBatchConsoleInfo.setRsvFld1(rsvFld1);
									gdEupsBatchConsoleInfo.setRsvFld2(rsvFld2);
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
				context.setData(ParamKeys.TOT_CNT, Integer.parseInt(totCnt));
				logger.info("==========End  BatchDataFileAction  updateInfo");
		}

}

