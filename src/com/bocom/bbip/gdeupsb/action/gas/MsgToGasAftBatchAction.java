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
import org.springframework.beans.factory.annotation.Qualifier;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.action.common.OperateFTPAction;
import com.bocom.bbip.eups.action.common.OperateFileAction;
import com.bocom.bbip.eups.adaptor.ThirdPartyAdaptor;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsBatchConsoleInfo;
import com.bocom.bbip.eups.entity.EupsBatchInfoDetail;
import com.bocom.bbip.eups.entity.EupsThdFtpConfig;
import com.bocom.bbip.eups.repository.EupsBatchConsoleInfoRepository;
import com.bocom.bbip.eups.repository.EupsBatchInfoDetailRepository;
import com.bocom.bbip.eups.repository.EupsThdFtpConfigRepository;
import com.bocom.bbip.eups.spi.service.batch.AfterBatchAcpService;
import com.bocom.bbip.eups.spi.vo.AfterBatchAcpDomain;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GDEupsBatchConsoleInfo;
import com.bocom.bbip.gdeupsb.entity.GdGashBatchTmp;
import com.bocom.bbip.gdeupsb.repository.GDEupsBatchConsoleInfoRepository;
import com.bocom.bbip.gdeupsb.repository.GdGashBatchTmpRepository;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.CollectionUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

/**
 * 惠州燃气 文件批量托收完成通知燃气
 * 
 * @author WangMQ
 * 
 */
public class MsgToGasAftBatchAction extends BaseAction implements AfterBatchAcpService {
	@Autowired
	OperateFTPAction operateFTPAction;
	@Autowired
	OperateFileAction operateFileAction;
	@Autowired
	EupsBatchConsoleInfoRepository eupsBatchConsoleInfoRepository;
	@Autowired
	EupsThdFtpConfigRepository eupsThdFtpConfigRepository;
	@Autowired
	GdGashBatchTmpRepository gdGashBatchTmpRepository;
	@Autowired
	GDEupsBatchConsoleInfoRepository gdeupsBatchConsoleInfoRepository;
	@Autowired
	EupsBatchInfoDetailRepository eupsBatchInfoDetailRepository;
	@Autowired
	@Qualifier("callThdTradeManager")
	ThirdPartyAdaptor callThdTradeManager;

	private Logger logger = LoggerFactory
			.getLogger(MsgToGasAftBatchAction.class);

	public void afterBatchDeal(AfterBatchAcpDomain afterbatchacpdomain, Context context) throws CoreException {
		logger.info("===============Start  BatchDataResultFileAction  afterBatchDeal");	
		logger.info(">>>>>Start  Down  AGTS  FileResult <<<<<<");
		//文件下载
//		String path=context.getData("dir").toString();
//		EupsThdFtpConfig eupsThdFtpConfigs = get(EupsThdFtpConfigRepository.class).findOne(ParamKeys.FTPID_BATCH_PAY_FILE_TO_ACP);
//		String fileNames=context.getData("batNo")+".result";
//		eupsThdFtpConfigs.setLocDir(fileNames);
//		eupsThdFtpConfigs.setRmtFleNme(fileNames);
//		eupsThdFtpConfigs.setFtpDir("1");
//		eupsThdFtpConfigs.setRmtWay(path);
//		operateFTPAction.getFileFromFtp(eupsThdFtpConfigs);
//		logger.info(">>>>>Down Result File Success<<<<<<");
	
		//第三方 rsvFld7
		String batNo=context.getData(ParamKeys.BAT_NO).toString();
		EupsBatchConsoleInfo eupsBatchConsoleInfo=eupsBatchConsoleInfoRepository.findOne(batNo);
		String sqns=eupsBatchConsoleInfo.getRsvFld2();
		GDEupsBatchConsoleInfo  Info=new GDEupsBatchConsoleInfo();
		Info.setRsvFld7(sqns);
		GDEupsBatchConsoleInfo  gdeupsBatchConsoleInfo = gdeupsBatchConsoleInfoRepository.find(Info).get(0);
		String gdBatNo = gdeupsBatchConsoleInfo.getBatNo();
		context.setData("gdBatNo", gdBatNo);
		//更改控制表
		GDEupsBatchConsoleInfo gdEupsBatchConsoleInfoUpdate=updateInfo(context,gdeupsBatchConsoleInfo ,eupsBatchConsoleInfo);
		//文件名
//			String fileName = context.getData("fleNmeBak");
//			EupsThdFtpConfig ftpCfg = eupsThdFtpConfigRepository.findOne("batchPayFileToAcp");
//			ftpCfg.setLocFleNme(fileName);
//			ftpCfg.setRmtFleNme(fileName);
			
			String fileName = "filecnjt" + DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMdd) + ".txt";
			EupsThdFtpConfig gasFtpCfg = eupsThdFtpConfigRepository.findOne("PGAS00Bat");
			gasFtpCfg.setLocFleNme(fileName);
			gasFtpCfg.setRmtFleNme(fileName);
			
//			EupsThdFtpConfig ftpCfg = eupsThdFtpConfigRepository.findOne("batchPayFileToAcp");
//			String name = context.getData(ParamKeys.BAT_NO) + ".result";
//			ftpCfg.setLocFleNme(fileName);
//			ftpCfg.setRmtFleNme(fileName);
//			ftpCfg.setLocDir("C:/home/bbipadm/data/mftp/BBIP/GDEUPSB/01491800999/EFC0000/20150323/");
//			ftpCfg.setRmtWay("/home/bbipadm/data/mftp/BBIP/GDEUPSB/01491800999/EFC0000/20150323/");

			try{
				Map<String, Object> resultMap=createFileMap(context,gdEupsBatchConsoleInfoUpdate);
				gasFtpCfg.setFtpDir("0");
				
//				gasFtpCfg.setLocDir("/home/bbipadm/data/mftp/BBIP/GDEUPSB/01441800999/EFC0000/20150323/");
//				ftpCfg.setRmtWay("/home/bbipadm/data/mftp/BBIP/GDEUPSB/01441800999/EFC0000/20150323/");
				operateFileAction.createCheckFile(gasFtpCfg, "msgToGasFileFmt", fileName, resultMap);
		}catch(CoreException e){
				logger.info("~~~~~~~~~~~Error  Message",e);
		}
			
			gasFtpCfg.setFtpDir("0");
			operateFTPAction.putCheckFile(gasFtpCfg);
			
			//TODO 通知第三方
			callThd(context);
			logger.info("===============End  BatchDataResultFileAction  afterBatchDeal");	
		}
	private void callThd(Context context) throws CoreException {
		logger.info("=================start MsgToGasAftBatchAction callThd with context=======" + context);
		
		String oldProcessId = context.getProcessId();
		
		context.setProcessId("eups.fileBatchPayCreateDataProcess");
		String gdBatNo = context.getData("gdBatNo");
		GDEupsBatchConsoleInfo gdbat = new GDEupsBatchConsoleInfo();
		gdbat.setBatNo(gdBatNo);
		List<GDEupsBatchConsoleInfo> gdbatBatchConsoleInfos = gdeupsBatchConsoleInfoRepository.find(gdbat);
		String fleNme = gdbatBatchConsoleInfos.get(0).getFleNme();
		context.setData("fleNmeBak", fleNme);
		context.setData("TransCode", "SMPCPAYTXT");
		
		Map<String, Object> rspMap = callThdTradeManager.trade(context);
		
		context.setProcessId(oldProcessId);
		logger.info("=================end MsgToGasAftBatchAction callThd with context=======" + context);
	}
	/**
	 * 把信息保存到控制表
	 */
	public GDEupsBatchConsoleInfo  updateInfo(Context context,GDEupsBatchConsoleInfo gdeupsBatchConsoleInfo,EupsBatchConsoleInfo eupsBatchConsoleInfo){
			logger.info("===============Start  BatchDataResultFileAction  updateInfo");	
			Integer totCnt=Integer.parseInt(eupsBatchConsoleInfo.getTotCnt().toString());
			//成功笔数
			Integer sucTotCnt=Integer.parseInt(context.getData(GDParamKeys.SUC_TOT_CNT).toString());
			gdeupsBatchConsoleInfo.setSucTotCnt(sucTotCnt);
			//失败笔数
			Integer falTotCnt=totCnt-sucTotCnt;
			gdeupsBatchConsoleInfo.setFalTotCnt(falTotCnt);
			
			BigDecimal totAmt=eupsBatchConsoleInfo.getTotAmt();
			//成功总金额
			BigDecimal sucTotAmt=new BigDecimal(context.getData("sucTotAmt").toString());
			gdeupsBatchConsoleInfo.setSucTotAmt(sucTotAmt);
			//失败总金额
			BigDecimal falTotAmt=totAmt.subtract(sucTotAmt);
			gdeupsBatchConsoleInfo.setFalTotAmt(falTotAmt);
			//更改状态
			gdeupsBatchConsoleInfo.setBatSts("S");
			//更新批次表
			gdeupsBatchConsoleInfoRepository.updateConsoleInfo(gdeupsBatchConsoleInfo);
			logger.info("===============End  BatchDataResultFileAction  updateInfo");	
			return gdeupsBatchConsoleInfo;
	}
	/**
	 * 拼装map文件
	 */
	public Map<String, Object> createFileMap(Context context,GDEupsBatchConsoleInfo gdEupsBatchConsoleInfoUpdate){
			logger.info("===============Start  BatchDataResultFileAction  createFileMap");	
			//代收付文件内容
			List<EupsBatchInfoDetail> mapList=context.getVariable("detailList");
//			
			 logger.info(">>>>>>>>>>>>>>>>>>>>> mapList.size(): " + CollectionUtils.isEmpty(mapList));
			 logger.info(">>>>>>>>>>>>>>>>>>>>> mapList.size(): " + context.getVariable("detailList"));
			
//			List<GDEupsEleTmp> gdEupsEleTmpList = gdEupsEleTmpRepository.findAllOrderBySqn();
//			List<GdGashBatchTmp> gasbatTmps = gdGashBatchTmpRepository.findAll(); //TODO 
			//内容主体
			List<GdGashBatchTmp> list=new ArrayList<GdGashBatchTmp>();
			
			String gdBatNo = context.getData("gdBatNo");
			String cusAc = null;
			String cusNo = null;
			String thdSts = null;
			String sts = null;
			
			GdGashBatchTmp findInfo = new GdGashBatchTmp();
			List<GdGashBatchTmp> listTmps = new ArrayList<GdGashBatchTmp>();
			for(EupsBatchInfoDetail detail : mapList){
				GdGashBatchTmp gdGashBatchTmp=new GdGashBatchTmp();
				
//				detail.setTxnDte(new Date());
//				
//				get(EupsBatchInfoDetailRepository.class).update(detail);
				
				// B0,B1,B2,B3   根据detail.getSts()/detail.getErrMsg()设定状态thdSts
				sts = detail.getSts();
				if("S".equals(sts)){
					thdSts = "B0";
					gdGashBatchTmp.setBatSts("S");
					
				}else{
					String errCode = detail.getErrMsg().toString().substring(0, 6);
					if(errCode.equals("TPM050")){
						thdSts = "B1";
					}
					else if(errCode.equals("CB1004") || "不存在代扣协议".contains(detail.getErrMsg())){
						thdSts = "B2";
				}
					else{
						thdSts = "B3";
					}
					gdGashBatchTmp.setBatSts("F");
				}
				
				gdGashBatchTmp.setBk("cnjt");
				
				cusAc = detail.getCusAc();
				cusNo = detail.getAgtSrvCusId();
				findInfo.setThdSqn((String)detail.getRmk1());
				findInfo.setCusAc(cusAc);
				findInfo.setCusNo(cusNo);
				findInfo.setBatNo(gdBatNo);
				listTmps = gdGashBatchTmpRepository.find(findInfo);
				
				gdGashBatchTmp.setSqn(listTmps.get(0).getSqn());
				gdGashBatchTmp.setThdSqn(listTmps.get(0).getThdSqn());
				gdGashBatchTmp.setCusNo(cusNo);
				gdGashBatchTmp.setPayMon(listTmps.get(0).getPayMon());
				gdGashBatchTmp.setTxnAmt(String.valueOf(detail.getTxnAmt()));
				gdGashBatchTmp.setTxnDte(listTmps.get(0).getTxnDte());
				gdGashBatchTmp.setTmpFld5(thdSts);
				
				//Update 临时表，为对账做准备
				gdGashBatchTmpRepository.update(gdGashBatchTmp);
				list.add(gdGashBatchTmp);
			}
			Map<String, Object> resultMap=new HashMap<String, Object>(); 
//			resultMap.put(ParamKeys.EUPS_FILE_HEADER, headMap);
//			System.out.println(resultMap.get(ParamKeys.EUPS_FILE_HEADER));
			resultMap.put(ParamKeys.EUPS_FILE_DETAIL, BeanUtils.toMaps(list));
			logger.info("===============End  BatchDataResultFileAction  createFileMap");	
			return resultMap;
	}
	
	
//	@Override
//	public void afterBatchDeal(AfterBatchAcpDomain afterbatchacpdomain,
//			Context context) throws CoreException {
//		logger.info("Enter in MsgToGasAftBatchAction!");
//		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
//
//		/*
//		 * plan: step1: 查询批次表中的批次信息 SELECT BrNo,SndTlr,ChkTlr,NodNo,DskNam From
//		 * pubbatinf WHERE DskNo='%s' !!!!!!!!!1Done! step2：从流水表中查询该批次流水信息|批次号
//		 * select * from 流水表 where batNo=#{batNo} !!!!!!!!!!!!!!!1Done!!
//		 * step3：根据批次号流水信息拼装文件map !!!!!!!Done! step4:
//		 * 从FTP配置信息表中获取(或配置)批次文件的信息，包含本地、远程文件名，FTP路径等 !!!!!!!!!!!!Done!! step5:
//		 * 生成文件 Done!! step6：上传文至燃气FTP Done!! step7: 更新流水表 step8: 信息处理 rspMsg
//		 * rspCod......
//		 */
//		String comNo = context.getData(ParamKeys.COMPANY_NO);
//		// 根据批次号查询批次信息
//		String batNo = context.getData(ParamKeys.BAT_NO);
//		EupsBatchConsoleInfo eupsBatchConsoleInfo = new EupsBatchConsoleInfo();
//		eupsBatchConsoleInfo.setBatNo(batNo);
//		List<EupsBatchConsoleInfo> batchConsoleInfos = eupsBatchConsoleInfoRepository
//				.find(eupsBatchConsoleInfo);
//		if (CollectionUtils.isEmpty(batchConsoleInfos)) { // 无批次信息
//			throw new CoreRuntimeException("无该批次信息");
//		}
//
////		String txnDteTmp = DateUtils.format(new Date(),
////				DateUtils.STYLE_yyyyMMdd);
////		String locFileName = "fileCNJT" + txnDteTmp + ".txt";
//		String locFileName = context.getData("fleNmeBak");
//		EupsThdFtpConfig ftpCfg = eupsThdFtpConfigRepository.findOne("PGAS00BatToThd");
//		ftpCfg.setLocFleNme(locFileName);
//		ftpCfg.setRmtFleNme(locFileName);
//		
//		// 拼装批量结果文件Map
//		Map<String, Object> map = encodeFileMap(context, batNo);
//
//		try {
//			// 生成批量结果到指定路径
//			operateFileAction.createCheckFile(ftpCfg, "msgToGasFileFmt",
//					locFileName, map);
//			logger.info("批量结果文件生成成功！");
//		} catch (Exception e) {
//			logger.error("File create error : " + e.getMessage());
//			throw new CoreException(ErrorCodes.EUPS_FILE_CREATE_FAIL);
//		}
//
//		// 向指定FTP路径放文件
//		operateFTPAction.putCheckFile(ftpCfg);
//		logger.info("批量结果文件FTP放置成功！");
//
//		/*
//		 * <Exec func="PUB:ExecSql" error="IGNORE"> <Arg name="SqlCmd"
//		 * value="UpEfeStatus"/> <!--更新PkgFlg为批量扣收已发送--> <!-- UPDATE
//		 * Gastxnjnl491 set PkgFlg='2' where dskno='%s' and PkgFlg='1' -->
//		 * </Exec>
//		 */
//
//	}

	/**
	 * 拼装文件Map
	 * 
	 * @param context
	 * @return
	 */
//	private Map<String, Object> encodeFileMap(Context context, String batNo)
//			throws CoreException {
//		Map<String, Object> map = new HashMap<String, Object>();
//		// 从流水表中根据批次号取流水信息
//		EupsTransJournal etj = new EupsTransJournal();
//		etj.setBatNo(batNo);
//		List<EupsTransJournal> chkEupsTransJournalList = eupsTransJournalRepository
//				.find(etj);
//		if (null == chkEupsTransJournalList
//				&& CollectionUtils.isEmpty(chkEupsTransJournalList)) {
//			logger.info("There are no records for select check trans journal ");
//			throw new CoreException(ErrorCodes.EUPS_QUERY_NO_DATA);
//		}
//
//		List<EupsTransJournal> returnList = new ArrayList<EupsTransJournal>();
//		GdGashBatchTmp batchTmp = new GdGashBatchTmp();
//		batchTmp.setBatNo(batNo);
//		List<GdGashBatchTmp> getPayMonTmps = gdGashBatchTmpRepository
//				.find(batchTmp);
//		String payMon = getPayMonTmps.get(0).getPayMon();
//		String rsvFld5 = null;
//		for (EupsTransJournal jnl : chkEupsTransJournalList) {
//			if ("S".equals(jnl.getTxnSts())) {
//				jnl.setBakFld2("B0");
//			}
//			if ("F".equals(jnl.getTxnSts())) {
//				jnl.setBakFld2("B3");
//			}
//			jnl.setBk("cnjt");
//			jnl.setRsvFld6(payMon);
//
//			rsvFld5 = DateUtils.format((Date) jnl.getTxnDte(),
//					DateUtils.STYLE_SIMPLE_DATE);
//			jnl.setRsvFld5(rsvFld5);
//
//			returnList.add(jnl);
//		}
//		map.put(ParamKeys.EUPS_FILE_DETAIL, BeanUtils.toMaps(returnList));
//		return map;
//	}

}
