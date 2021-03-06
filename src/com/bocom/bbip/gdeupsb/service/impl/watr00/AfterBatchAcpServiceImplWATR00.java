package com.bocom.bbip.gdeupsb.service.impl.watr00;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.action.common.OperateFTPAction;
import com.bocom.bbip.eups.action.common.OperateFileAction;
import com.bocom.bbip.eups.adaptor.ThirdPartyAdaptor;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsBatchConsoleInfo;
import com.bocom.bbip.eups.entity.EupsBatchInfoDetail;
import com.bocom.bbip.eups.entity.EupsThdFtpConfig;
import com.bocom.bbip.eups.repository.EupsBatchConsoleInfoRepository;
import com.bocom.bbip.eups.repository.EupsBatchInfoDetailRepository;
import com.bocom.bbip.eups.repository.EupsThdFtpConfigRepository;
import com.bocom.bbip.eups.spi.service.batch.AfterBatchAcpService;
import com.bocom.bbip.eups.spi.vo.AfterBatchAcpDomain;
import com.bocom.bbip.gdeupsb.action.common.FileFtpUtils;
import com.bocom.bbip.gdeupsb.action.common.OperateFTPActionExt;
import com.bocom.bbip.gdeupsb.entity.GdeupsWatBatInfTmp;
import com.bocom.bbip.gdeupsb.repository.GDEupsEleTmpRepository;
import com.bocom.bbip.gdeupsb.repository.GdeupsWatBatInfTmpRepository;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.bbip.utils.NumberUtils;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.service.id.seq.StepSequenceFactory;
/**
 * 汕头水费批量代扣后处理
 * @author hefengwen
 *
 */
public class AfterBatchAcpServiceImplWATR00 implements AfterBatchAcpService{
	
	private static Logger logger = LoggerFactory.getLogger(AfterBatchAcpServiceImplWATR00.class);
	@Autowired
	OperateFileAction operateFile;
	
	@Autowired
	OperateFTPAction operateFTPAction;
	
	@Autowired
	BBIPPublicService service;
	@Autowired
	EupsBatchConsoleInfoRepository eupsBatchConsoleInfoRepository;
	@Autowired
	GDEupsEleTmpRepository gdEupsEleTmpRepository;
	@Autowired
	EupsThdFtpConfigRepository eupsThdFtpConfigRepository;
	@Autowired
	EupsBatchInfoDetailRepository eupsBatchInfoDetailRepository;
	@Autowired
	ThirdPartyAdaptor callThdTradeManager;
	
	@Override
	public void afterBatchDeal(AfterBatchAcpDomain domain, Context context)	throws CoreException {
		logger.info("AfterBatchAcpServiceImplWATR00 start ... ...");
		
		
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String sqn = service.getBBIPSequence();
		String date = DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMdd);
		
		String batNo=context.getData(ParamKeys.BAT_NO).toString();
		EupsBatchConsoleInfo eupsBatchConsoleInfo = eupsBatchConsoleInfoRepository.findOne(batNo);
		//头部
		Map<String, Object> resultMapHead = BeanUtils.toMap(eupsBatchConsoleInfo);
		resultMapHead.put("abc", "HDR2");
//		BigDecimal b = new BigDecimal(resultMapHead.get("totAmt").toString());
//		Double b = Double.parseDouble(resultMapHead.get("totAmt").toString());
		
//		b = b*100;
//		int totAmt = b.scaleByPowerOfTen(2).intValue();
		resultMapHead.put("totAmt", NumberUtils.yuanToCentString(resultMapHead.get("totAmt").toString()));
		resultMap.put(ParamKeys.EUPS_FILE_HEADER, resultMapHead);
		Map<String, Object> hdr1 = new HashMap<String, Object>();
		hdr1.put("HDR1", "HDR1");
		resultMap.put("head", hdr1);
		//文件名
		String fileName="BJH"+StringUtils.substring(sqn, 0, 8)+StringUtils.substring(sqn, 15, 20)+".dat";
		//文件内容 
//		GDEupsEleTmp gdEupsEleTmps =new GDEupsEleTmp();
//		gdEupsEleTmps.setRsvFld5(batNo);
//		List<GDEupsEleTmp> detailList=gdEupsEleTmpRepository.find(gdEupsEleTmps);
//		resultMap.put(ParamKeys.EUPS_FILE_DETAIL, detailList);
		EupsBatchInfoDetail eupsBatchInfoDetail = new EupsBatchInfoDetail();
		eupsBatchInfoDetail.setBatNo(batNo);
		List<EupsBatchInfoDetail> detailList = eupsBatchInfoDetailRepository.find(eupsBatchInfoDetail);
		
		
		
		List<Object> list1 = new ArrayList<Object>();
		Map<String, Object> map = new HashMap<String, Object>();
		int i=0;
		int txnAmtA;
		for(i=0;i< detailList.size();i++){
			map = BeanUtils.toMap(detailList.get(i));
			map.put("sj", date);
			
			
//			Double d = Double.parseDouble(map.get(ParamKeys.TXN_AMT).toString());
//			d = d *  100;
//			txnAmtA = d.intValue();
//			BigDecimal d = new BigDecimal(map.get(ParamKeys.TXN_AMT).toString());
//			txnAmtA = d.scaleByPowerOfTen(2).intValue();
			map.put(ParamKeys.TXN_AMT, NumberUtils.yuanToCentString(map.get(ParamKeys.TXN_AMT).toString()));
			if("S".equals(detailList.get(i).getSts())){
				map.put("sts", "1");
			}else{
				map.put("sts", "4");
			}
			list1.add(map);
		}
		resultMap.put(ParamKeys.EUPS_FILE_DETAIL, list1);
		
		EupsThdFtpConfig eupsThdFtpConfig =eupsThdFtpConfigRepository.findOne("watr00BatchResulf");
		
		// 生成文件
		operateFile.createCheckFile(eupsThdFtpConfig, "watBatchResult", fileName, resultMap);
		
//-----------------------------------------------------------------
		// 将生成的文件上传至第三方服务器
//		eupsThdFtpConfig.setLocFleNme(fileName);
//		eupsThdFtpConfig.setRmtFleNme(fileName);
//		logger.info("@@@@@@@@@@@@eupsThdFtpConfig=" + eupsThdFtpConfig);
//		OperateFTPActionExt operateFTP = new OperateFTPActionExt();
//		operateFTP.putCheckFile(eupsThdFtpConfig);
		
//-----------------------------------------------------------------------------------
		
		String stwatIp = eupsThdFtpConfig.getThdIpAdr();
		String userName = eupsThdFtpConfig.getOppNme();
		String password = eupsThdFtpConfig.getOppUsrPsw();
		String rmtDir = eupsThdFtpConfig.getRmtWay();
		String locDir = eupsThdFtpConfig.getLocDir();
		String[] shellArg = {"GDEUPSBFtpPutFile.sh",stwatIp,userName,password,rmtDir,fileName,locDir,"passive",fileName}; 
		logger.info("ftp args="+shellArg.toString());
		//ftp获取文件
		try{
			int result = FileFtpUtils.systemAndWait(shellArg,true);
			if(result==0){
				logger.info("put remote file success......");
			}else{
				throw new CoreException(ErrorCodes.EUPS_FAIL);
			}
		} catch (Exception e){
			throw new CoreException(ErrorCodes.EUPS_FAIL);
		}
		
		
		File watFile = new File(eupsThdFtpConfig.getLocDir()+"/"+fileName);
		String fileSize = watFile.length()+"";
		logger.info("filesize=="+fileSize);
		
		context.setData(ParamKeys.BK, service.getParam("GDEUPSB", "stBK"));
		context.setData(ParamKeys.BR, service.getParam("GDEUPSB", "stWatBr"));
		context.setData(ParamKeys.TELLER, service.getETeller(context.getData(ParamKeys.BK).toString()));
		logger.info("#######context="+context);
		context.setData("type", "Y004");
		context.setData("accountdate", DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMdd));
		
		StepSequenceFactory s = context.getService("logNoService");
		String logNo = s.create().toString();
		context.setData("waterno", "JH"+logNo);//流水号生成
		context.setData("bankcode", "交行");
		context.setData("salesdepart",((String)context.getData(ParamKeys.BR)).substring(2, 8));
		context.setData("salesperson", ((String)context.getData(ParamKeys.TELLER)).substring(3));
		context.setData("busitime", DateUtils.format(new Date(),DateUtils.STYLE_yyyyMMddHHmmss));
		context.setData("thdRspCde", "");
		context.setData("zprice", "0");
		context.setData("months", "0");
		context.setData("operano", "");
		context.setData("password", "        ");
		context.setData("md5digest", " ");
		
		
//		context.setData("path", "");
		context.setData("filename", fileName);
		context.setData("filesize", fileSize);
//		context.setData("reserved", "");
		callThdTradeManager.trade(context);
//-------------------------------------------------------------------------------------------------
		
		
		//将生成的文件上传至汕头分行指定服务器
//		EupsThdFtpConfig eupsThdFtpConfigA =eupsThdFtpConfigRepository.findOne("watr00BatchResulfA");
//		eupsThdFtpConfigA.setLocFleNme(fileName);
//		eupsThdFtpConfigA.setRmtFleNme(fileName);
//		operateFTPAction.putCheckFile(eupsThdFtpConfigA);
//		------------------------------------------------
		EupsThdFtpConfig eupsThdFtpConfigA =eupsThdFtpConfigRepository.findOne("watr00BatchResulfA");
		String stwatIpA = eupsThdFtpConfigA.getThdIpAdr();
		String userNameA = eupsThdFtpConfigA.getOppNme();
		String passwordA = eupsThdFtpConfigA.getOppUsrPsw();
		String rmtDirA = eupsThdFtpConfigA.getRmtWay();
		String locDirA = eupsThdFtpConfigA.getLocDir();
		String[] shellArgA = {"GDEUPSBFtpPutFile.sh",stwatIpA,userNameA,passwordA,rmtDirA,fileName,locDirA,"bin",fileName}; 
		logger.info("ftp args="+shellArgA.toString());
		//ftp放文件
		try{
			int result = FileFtpUtils.systemAndWait(shellArgA,true);
			if(result==0){
				logger.info("put remote file success......");
			}else{
				throw new CoreException(ErrorCodes.EUPS_FAIL);
			}
		} catch (Exception e){
			throw new CoreException(ErrorCodes.EUPS_FAIL);
		}
		
		
//		--------------------------------------------------------
		
		logger.info("AfterBatchAcpServiceImplWATR00 end ... ...");
	}

}
