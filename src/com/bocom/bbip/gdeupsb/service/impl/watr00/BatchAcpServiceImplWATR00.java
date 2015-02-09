package com.bocom.bbip.gdeupsb.service.impl.watr00;

import java.io.File;
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
import com.bocom.bbip.eups.action.common.OperateFTPAction;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsBatchPayEntity;
import com.bocom.bbip.eups.entity.EupsThdFtpConfig;
import com.bocom.bbip.eups.repository.EupsThdFtpConfigRepository;
import com.bocom.bbip.eups.spi.service.batch.BatchAcpService;
import com.bocom.bbip.eups.spi.vo.PrepareBatchAcpDomain;
import com.bocom.bbip.file.fmt.FileMarshaller;
import com.bocom.bbip.utils.Assert;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.ContextUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.JumpException;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.support.CollectionUtils;
/**
 * 汕头水费批量扣款数据准备
 * @author hefengwen
 *
 */
public class BatchAcpServiceImplWATR00 implements BatchAcpService {
	
	private static Logger logger = LoggerFactory.getLogger(BatchAcpServiceImplWATR00.class);
	/**代扣文件指定存放路径前缀*/
	private static final String FILE_DIR_PREFIX = "/home/bbipadm/data/mftp/BBIP/GDEUPS/";
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
	
	@Override
	public List<EupsBatchPayEntity> prepareBatchDeal(	PrepareBatchAcpDomain domain, Context context) throws CoreException {
		logger.info("BatchAcpServiceImplWATR00 prepareBatchDeal start ... ...");
		String br = ContextUtils.assertDataHasLengthAndGetNNR(context, ParamKeys.BR, ErrorCodes.EUPS_FIELD_EMPTY);//机构号
		String tlr = ContextUtils.assertDataHasLengthAndGetNNR(context, ParamKeys.TELLER, ErrorCodes.EUPS_FIELD_EMPTY);//柜员号
		String comNo = ContextUtils.assertDataHasLengthAndGetNNR(context, ParamKeys.COMPANY_NO, ErrorCodes.EUPS_FIELD_EMPTY);//代理单位号
		String acDate = DateUtils.format(service.getAcDate(), DateUtils.STYLE_SIMPLE_DATE);//会计日期
		//代扣文件名称 BATC+单位编号(代收付)+0(代收付类型，代扣0)+.txt
		String filNam = "BATC"+comNo+"0"+".txt";
		//代扣文件放置目录
		// /home/bbipadm/data/mftp/BBIP/请求系统/机构号/柜员号/会计日期
		String dir = FILE_DIR_PREFIX+br+File.separator+tlr+File.separator+acDate+File.separator;
		
		Map<String,Object> map = buildBatchFile(context,filNam,dir,comNo);
		
		//设置接下来context中必须的值
		context.setData(ParamKeys.TXN_MDE, Constants.TXN_MDE_FILE);
		context.setData(ParamKeys.FLE_NME, filNam);
		context.setData("thdBatNo", BTPservice.applyBatchNo(ParamKeys.BUSINESS_CODE_COLLECTION)+"_"+filNam);
		//自己的eupsBusTyp
		context.setData(ParamKeys.EUPS_BUSS_TYPE, "WATR00");
		context.setData(ParamKeys.TOT_CNT, map.get(ParamKeys.TOT_CNT));
		context.setData(ParamKeys.TOT_AMT, map.get(ParamKeys.TOT_AMT));
		logger.info("BatchAcpServiceImplWATR00 prepareBatchDeal end ... ...");
		return null;
	}
	
	private Map<String,Object> buildBatchFile(Context context,String filNam,String dir,String comNo) throws CoreException{
		Map<String,Object> ret = CollectionUtils.createMap();
		Map<String,Object> header = CollectionUtils.createMap();
		List<Object> detail = CollectionUtils.createList();
		
		//根据ftpNo查找第三方FTP配置信息
		EupsThdFtpConfig eupsThdFtpConfig = eupsThdFtpConfigRepository.findOne("");
		Assert.isNotNull(eupsThdFtpConfig, ErrorCodes.EUPS_FTP_INFO_NOTEXIST);
		eupsThdFtpConfig.setRmtFleNme(filNam);
		eupsThdFtpConfig.setLocFleNme(filNam);
		logger.info("start get batch file now,thd ftp info=["+BeanUtils.toFlatMap(eupsThdFtpConfig)+"]");
		
		operateFTPAction.getFileFromFtp(eupsThdFtpConfig);
		
		//根据模板解析第三方的文件，模板ID为"parseBatchFile"
		Map<String,Object> srcFile = parseFileByPath(eupsThdFtpConfig.getLocDir(), 	filNam, "watr00FmtIn");
		Assert.isFalse(null==srcFile||0==srcFile.size(), ErrorCodes.EUPS_FILE_PARESE_FAIL, "解析文件出错");
		Map<String,Object> srcHeader = (Map)srcFile.get(ParamKeys.EUPS_FILE_HEADER);
		
		//生成代扣文件头信息
		header.put(ParamKeys.COMPANY_NO, comNo);
		header.put(ParamKeys.TOT_CNT, srcHeader.get(ParamKeys.TOT_CNT));
		header.put(ParamKeys.TOT_AMT, srcHeader.get(ParamKeys.TOT_AMT));
		//生成代扣文件体信息
		List<Map<String,Object>> srcDetail = (List<Map<String, Object>>) srcFile.get(ParamKeys.EUPS_FILE_DETAIL);
		for(Map<String,Object> map:srcDetail){
			Map<String,Object> temp = CollectionUtils.createMap();
			temp.put(ParamKeys.CUS_AC, map.get(ParamKeys.CUS_AC));
			temp.put(ParamKeys.CUS_NME, map.get(ParamKeys.CUS_NME));
			temp.put(ParamKeys.TXN_AMT, map.get(ParamKeys.TXN_AMT));
			temp.put(ParamKeys.THD_CUS_NO, map.get(ParamKeys.THD_CUS_NO));
			temp.put(ParamKeys.THD_CUS_NME, map.get(ParamKeys.THD_CUS_NME));
			//本行标志
			temp.put("outOthFlg", true==accountService.isOurBankCard((String) map.get(ParamKeys.CUS_AC))?"0":"1");
			temp.put(ParamKeys.OBK_BR, map.get("KKB"));
			temp.put(ParamKeys.BAK_FLD1, "");
			temp.put(ParamKeys.BAK_FLD2, "");
			detail.add(temp);
		}
		//生成批扣文件并放到指定的存放路径，模板id为"buildBatchFile"
		createBatchFileByPath(detail,dir,filNam,"buildBatchFile",header);
		ret.put(ParamKeys.TOT_CNT, srcHeader.get(ParamKeys.TOT_CNT));
		ret.put(ParamKeys.TOT_AMT, srcHeader.get(ParamKeys.TOT_AMT));
		return ret;
	}
	
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
	private void createBatchFileByPath(List<Object> object,String path,String createFileNam,
			String fileId,Map<String,Object> header){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("header", header);
		map.put("detail", object);
		Resource resource = new ClassPathResource("config/fmt/format.xml");
		FileMarshaller marshaller = new FileMarshaller();
		marshaller.setResources(new Resource[]{resource});
		try {
			marshaller.afterPropertiesSet();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			marshaller.marshal(fileId, map,path+createFileNam.trim());
		} catch (JumpException e) {
			e.printStackTrace();
		}
	}

}
