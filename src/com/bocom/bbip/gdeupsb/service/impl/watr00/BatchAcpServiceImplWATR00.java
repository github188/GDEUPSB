
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
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.comp.account.AccountService;
import com.bocom.bbip.comp.btp.BTPService;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.action.common.OperateFTPAction;
import com.bocom.bbip.eups.action.common.OperateFileAction;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsActSysPara;
import com.bocom.bbip.eups.entity.EupsThdFtpConfig;
import com.bocom.bbip.eups.repository.EupsActSysParaRepository;
import com.bocom.bbip.eups.repository.EupsThdFtpConfigRepository;
import com.bocom.bbip.eups.spi.service.batch.BatchAcpService;
import com.bocom.bbip.eups.spi.vo.PrepareBatchAcpDomain;
import com.bocom.bbip.file.fmt.FileMarshaller;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.entity.GDEupsBatchConsoleInfo;
import com.bocom.bbip.gdeupsb.entity.GdeupsWatBatInfTmp;
import com.bocom.bbip.gdeupsb.repository.GDEupsBatchConsoleInfoRepository;
import com.bocom.bbip.gdeupsb.repository.GdeupsWatBatInfTmpRepository;
import com.bocom.bbip.thd.org.apache.commons.collections.CollectionUtils;
import com.bocom.bbip.utils.Assert;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.ContextUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.bbip.utils.NumberUtils;
import com.bocom.jump.bp.JumpException;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
/**
 * 汕头水费批量扣款数据准备
 * @author hefengwen
 *
 */
public class BatchAcpServiceImplWATR00 extends BaseAction implements BatchAcpService{
	
	private static Logger logger = LoggerFactory.getLogger(BatchAcpServiceImplWATR00.class);
	/**代扣文件指定存放路径前缀*/
	private static final String FILE_DIR_PREFIX = "/home/bbipadm/data/mftp/BBIP/GDEUPSB/";
	/**批量加锁KEY*/
	private static final String lockKey = "BatchAcpServiceImplWATR00";
	@Autowired
	EupsThdFtpConfigRepository eupsThdFtpConfigRepository;
	@Autowired
	OperateFTPAction operateFTPAction;
	@Autowired
	BBIPPublicService service;
	@Autowired
	AccountService accountService;
	@Autowired
	BTPService BTPservice;
	
	@Autowired
	EupsActSysParaRepository eupsActSysParaRepository;
	
	@SuppressWarnings("unchecked")
	@Override
	public void prepareBatchDeal(PrepareBatchAcpDomain domain, Context context) throws CoreException {
		logger.info("BatchAcpServiceImplWATR00 prepareBatchDeal start ... ..."+context);
		service.tryLock(lockKey, 60*1000L, 600L);//加锁
		String br = ContextUtils.assertDataHasLengthAndGetNNR(context, ParamKeys.BR, ErrorCodes.EUPS_FIELD_EMPTY);//机构号
		String tlr = ContextUtils.assertDataHasLengthAndGetNNR(context, ParamKeys.TELLER, ErrorCodes.EUPS_FIELD_EMPTY);//柜员号
		String comNo = ContextUtils.assertDataHasLengthAndGetNNR(context, ParamKeys.COMPANY_NO, ErrorCodes.EUPS_FIELD_EMPTY);//代理单位号
		Date date=get(BBIPPublicService.class).getAcDate();
		
		String acDate = DateUtils.format(date, DateUtils.STYLE_yyyyMMdd);//会计日期
		System.out.println("@@@@@@@@@@@@"+acDate);
		
		
		EupsActSysPara eupsActSysPara = new EupsActSysPara();
		eupsActSysPara.setComNo(comNo);
		eupsActSysPara.setUseSts("0");
		List<EupsActSysPara> eupsActSysParas = get(EupsActSysParaRepository.class).find(eupsActSysPara);
		if(CollectionUtils.isEmpty(eupsActSysParas)){
			
			throw new CoreException("");
		}
		eupsActSysPara = eupsActSysParas.get(0);
		String splNo = eupsActSysPara.getSplNo();
		//代扣文件名称 BATC+单位编号(代收付)+0(代收付类型，代扣0)+.txt
		String filNam = "BATC"+splNo+"0"+".txt";
		context.setVariable(ParamKeys.FLE_NME, filNam);
		//代扣文件放置目录
		// /home/bbipadm/data/mftp/BBIP/请求系统/机构号/柜员号/会计日期
		String dir = "/home/bbipadm/data/mftp/BBIP/GDEUPSB"+"/"+br+"/"+tlr+"/"+acDate+"/";
		logger.info("dir_filNam["+dir+filNam+"]");
		
		
		
		//获取第三方批量文件并解析
		buildBatchFile(context);
		
		String fileName = ContextUtils.assertVariableNotEmptyAndGet(context,ParamKeys.FLE_NME, ErrorCodes.EUPS_FILE_CREATE_NAME_ISEMPTY);
		
		
		
		
		
		Map<String, Object> fileMap = (Map<String, Object>) ContextUtils.assertVariableNotNullAndGet(context, "agtFileMap","agtFileMap不能为空");
		EupsThdFtpConfig config = get(EupsThdFtpConfigRepository.class).findOne("BatchFileFtpNo");
		Assert.isNotNull(config, ErrorCodes.EUPS_FTP_INFO_NOTEXIST,"第三方配置信息不存在");
		config.setLocFleNme(fileName);
		config.setRmtFleNme(fileName);
		config.setRmtWay(dir);
		config.setLocDir(dir);
		/** 产生代收付格式文件 */
		((OperateFileAction)get("opeFile")).createCheckFile(config, GDConstants.BATCH_FILE_FORMAT, fileName, fileMap);
		/** 发送到指定路径 */
		((OperateFTPAction)get("opeFTP")).putCheckFile(config);
		
		context.setData(ParamKeys.TXN_MDE, "0");
		context.setData(ParamKeys.TXN_CHL, "1");
		context.setData(ParamKeys.THD_BAT_NO, context.getData("sqn"));
		context.setData(ParamKeys.BUS_TYP, "0");
		context.setData("reqTyp", "");
		context.setData("sup1Id", "");
		context.setData("sup2Id", "");
		context.setData("authResnTbl", "");
		context.setData("reqTme", new Date());
		
		get(BBIPPublicService.class).synExecute("eups.batchPaySubmitDataProcess", context);
//		Result result = get(BGSPServiceAccessObject.class).callServiceFlatting("", context.getDataMap());
//		get(BatchFileCommon.class).sendBatchFileToACP(context);
//		BatchFileCommon com = get("eups.batchFileCommon");
//		com.sendBatchFileToACP(context);
//		//设置接下来context中必须的值
//		context.setData(ParamKeys.TXN_MDE, Constants.TXN_MDE_FILE);
//		context.setData(ParamKeys.FLE_NME, filNam);
//		context.setData("thdBatNo", BTPservice.applyBatchNo(ParamKeys.BUSINESS_CODE_COLLECTION)+"_"+filNam);
//		//自己的eupsBusTyp
//		context.setData(ParamKeys.EUPS_BUSS_TYPE, "WATR00");
//		context.setData(ParamKeys.TOT_CNT, map.get(ParamKeys.TOT_CNT));
//		context.setData(ParamKeys.TOT_AMT, map.get(ParamKeys.TOT_AMT));
		service.unlock(lockKey);
		logger.info("BatchAcpServiceImplWATR00 prepareBatchDeal end ... ..."+context);
	}
	/**
	 * 创建批扣文件
	 * @param context
	 * @return
	 * @throws CoreException
	 */
	@SuppressWarnings("unchecked")
	private void buildBatchFile(Context context) throws CoreException{
		
		String path = context.getData("path");
		String filename = context.getData("filename");
		
		/** 检查批次是否存在 */
		GDEupsBatchConsoleInfo info = new GDEupsBatchConsoleInfo();
		info.setRsvFld1(filename);//第三方文件名
		info.setComNo((String)context.getData(ParamKeys.COMPANY_NO));
		List<GDEupsBatchConsoleInfo> infos =get(GDEupsBatchConsoleInfoRepository.class).find(info);
//		Assert.isNotNull(infos, "批次信息已经存在");
		if(CollectionUtils.isNotEmpty(infos)){
			//已存在，报错
			logger.error("批次信息已存在");
			throw new CoreException("");
		}
		/** 插入批次控制表 */
		String batNo =((BTPService)get("BTPService")).applyBatchNo(ParamKeys.BUSINESS_CODE_COLLECTION);//申请代收批次号
		
		info.setBatNo(batNo);//批次号
		info.setBatSts(GDConstants.BATCH_STATUS_INIT);//批次状态
		info.setFleNme((String)context.getVariable(ParamKeys.FLE_NME));//代收付批量文件名称
		info.setRsvFld1(filename);//第三方文件名
//		info.setComNo((String) context.getData(ParamKeys.COMPANY_NO));
		info.setSubDte(new Date());
		info.setTxnTlr((String) context.getData(ParamKeys.TELLER));
		info.setTxnMde(Constants.TXN_MDE_FILE);
		info.setRapTyp("0");//代收
		info.setComNme((String)context.getData(ParamKeys.COMPANY_NAME));
		info.setBusKnd((String)context.getData(ParamKeys.BUSS_KIND));
		info.setTxnOrgCde((String) context.getData(ParamKeys.BR));
		info.setEupsBusTyp((String)context.getData(ParamKeys.EUPS_BUSS_TYPE));
		get(GDEupsBatchConsoleInfoRepository.class).insert(info);
		context.setDataMap(BeanUtils.toMap(info));
//		context.getDataMapDirectly().putAll(BeanUtils.toFlatMap(info));
		Map<String,Object> ret = new HashMap<String,Object>();
		Map<String,Object> header = new HashMap<String,Object>();
		List<Object> detail = new ArrayList<Object>();
		
		//根据ftpNo查找第三方FTP配置信息
		EupsThdFtpConfig eupsThdFtpConfig = eupsThdFtpConfigRepository.findOne("waterBatchFile");
		if(eupsThdFtpConfig==null){
			//第三方FTP信息不存在
			logger.error("第三方FTP配置信息不存在");
			throw new CoreException(ErrorCodes.EUPS_FTP_INFO_NOTEXIST);
		}
//		Assert.isNotNull(eupsThdFtpConfig, ErrorCodes.EUPS_FTP_INFO_NOTEXIST);
		
		eupsThdFtpConfig.setRmtFleNme(filename);
		eupsThdFtpConfig.setRmtWay(path);
		logger.info("start get batch file now,thd ftp info=["+BeanUtils.toFlatMap(eupsThdFtpConfig)+"]");
		
		operateFTPAction.getFileFromFtp(eupsThdFtpConfig);
		
		//根据模板解析第三方的文件，模板ID为"parseBatchFile"
		Map<String,Object> srcFile = parseFileByPath(eupsThdFtpConfig.getLocDir(), 	filename, "watr00FmtIn");
		Assert.isFalse(null==srcFile||0==srcFile.size(), ErrorCodes.EUPS_FILE_PARESE_FAIL, "解析文件出错");
		logger.info("srcFile header=["+srcFile.get(ParamKeys.EUPS_FILE_HEADER)+"]");
		logger.info("srcFile detail=["+srcFile.get(ParamKeys.EUPS_FILE_DETAIL)+"]");
		Map<String,Object> srcHeader = (Map<String,Object>)srcFile.get(ParamKeys.EUPS_FILE_HEADER);
		
		System.out.println();
		System.out.println();
		System.out.println("~~~~~~~~~~~~"+srcHeader);
		
		//生成代扣文件头信息
		header.put(ParamKeys.COMPANY_NO, eupsThdFtpConfig.getComNo());
		header.put("totCount", srcHeader.get(ParamKeys.TOT_CNT));
		header.put(ParamKeys.TOT_AMT, new BigDecimal(srcHeader.get(ParamKeys.TOT_AMT).toString().trim()).scaleByPowerOfTen(-2));
		GDEupsBatchConsoleInfo newInfo = new GDEupsBatchConsoleInfo();
		newInfo.setBatNo(info.getBatNo());
		String totCnt = (String)srcHeader.get(ParamKeys.TOT_CNT);
		String totAmt = ((String)srcHeader.get(ParamKeys.TOT_AMT)).trim();//分为单位
		
		context.setData(ParamKeys.TOT_CNT, Integer.parseInt(totCnt));
		context.setData(ParamKeys.TOT_AMT, new BigDecimal(totAmt).scaleByPowerOfTen(-2));
		
		newInfo.setTotCnt(Integer.parseInt(totCnt));
		newInfo.setTotAmt(new BigDecimal(totAmt).scaleByPowerOfTen(-2));//转换为以元为单位
		get(GDEupsBatchConsoleInfoRepository.class).updateConsoleInfo(newInfo);
		//生成代扣文件体信息
		List<Map<String,Object>> srcDetail = (List<Map<String, Object>>) srcFile.get(ParamKeys.EUPS_FILE_DETAIL);
		GdeupsWatBatInfTmp tmp = new GdeupsWatBatInfTmp();
		tmp.setBatNo(info.getBatNo());
		tmp.setBkNo(info.getTxnOrgCde());
		tmp.setComNo(info.getComNo());
		tmp.setActDat(info.getSubDte());
		tmp.setStatus("U");
		int i = 0;
		for(Map<String,Object> map:srcDetail){
			if(i==0){
				i++;
			}else{
			Map<String,Object> temp = new HashMap<String,Object>();
			temp.put("CUSAC", map.get("bcount"));
			temp.put("CUSNME", "");
			temp.put("TXNAMT", new BigDecimal(map.get("je").toString().trim()).scaleByPowerOfTen(-2));
			temp.put("AGTSRVCUSID", map.get("hno"));
			temp.put("AGTSRVCUSNME", "");
			//本行标志
			temp.put("OUROTHFLG", true==accountService.isOurBankCard((String) map.get(ParamKeys.CUS_AC))?"0":"1");
			temp.put("OBKBK", map.get("KKB"));
			temp.put("RMK1", "");
			temp.put("RMK2", "");
			detail.add(temp);
			
			String sqn = get(BBIPPublicService.class).getBBIPSequence();
			tmp.setSqn(sqn);
			tmp.setSeqNo(i);
			tmp.setHno((String)map.get("hno"));
			
			tmp.setSj((String)map.get("sj"));
			tmp.setJe((String)map.get("je"));
			BigDecimal bigDecimal=new BigDecimal(map.get("bcount").toString().trim()).scaleByPowerOfTen(-2);
			
			tmp.setBcount(bigDecimal+"");
			get(GdeupsWatBatInfTmpRepository.class).insert(tmp);
			i++;
		}
		}
		ret.put("header", header);
		ret.put("detail", detail);
		context.setVariable("agtFileMap", ret);
		//生成批扣文件并放到指定的存放路径，模板id为"buildBatchFile"
//		createBatchFileByPath(detail,dir,filNam,"buildBatchFile",header);
//		context.setData(ParamKeys.TOT_CNT, srcHeader.get(ParamKeys.TOT_CNT));
//		context.setData(ParamKeys.TOT_AMT, srcHeader.get(ParamKeys.TOT_AMT));
	}
	/**
	 * 解析第三方批扣文件
	 * @param filePath
	 * @param fileName
	 * @param fileId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Map<String,Object> parseFileByPath(String filePath,String fileName,String fileId){
		Map<String,Object> map = null;
		Resource resource = new ClassPathResource("config/fmt/FileFormatConfig.xml");
		FileMarshaller marshaller = new FileMarshaller();
		marshaller.setResources(new Resource[]{resource});
		Resource fileResource = new FileSystemResource(filePath+fileName);
		try {
			marshaller.afterPropertiesSet();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			map = marshaller.unmarshal(fileId, fileResource, Map.class);
		} catch (JumpException e) {
			e.printStackTrace();
		}
		return map;
	}
	/**
	 * 生成代收付批扣文件
	 * @param object
	 * @param path
	 * @param createFileNam
	 * @param fileId
	 * @param header
	 */
//	private void createBatchFileByPath(List<Object> object,String path,String createFileNam,
//			String fileId,Map<String,Object> header){
//		Map<String,Object> map = new HashMap<String,Object>();
//		map.put("header", header);
//		map.put("detail", object);
//		Resource resource = new ClassPathResource("config/fmt/format.xml");
//		FileMarshaller marshaller = new FileMarshaller();
//		marshaller.setResources(new Resource[]{resource});
//		try {
//			marshaller.afterPropertiesSet();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		try {
//			marshaller.marshal(fileId, map,path+createFileNam.trim());
//		} catch (JumpException e) {
//			e.printStackTrace();
//		}
//	}

}
