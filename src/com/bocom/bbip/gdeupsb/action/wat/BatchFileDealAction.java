package com.bocom.bbip.gdeupsb.action.wat;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.comp.CommonRequest;
import com.bocom.bbip.comp.account.AccountService;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.action.common.OperateFTPAction;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsThdFtpConfig;
import com.bocom.bbip.eups.repository.EupsThdFtpConfigRepository;
import com.bocom.bbip.file.fmt.FileMarshaller;
import com.bocom.bbip.gdeupsb.entity.GDEupsBatchConsoleInfo;
import com.bocom.bbip.gdeupsb.entity.GdeupsWatBatInfTmp;
import com.bocom.bbip.gdeupsb.repository.GDEupsBatchConsoleInfoRepository;
import com.bocom.bbip.gdeupsb.repository.GdeupsWatBatInfTmpRepository;
import com.bocom.bbip.utils.Assert;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.jump.bp.JumpException;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class BatchFileDealAction extends BaseAction{

	@Override
	public void execute(Context context) throws CoreException,
			CoreRuntimeException {
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
		Map<String,Object> ret = new HashMap<String,Object>();
		Map<String,Object> header = new HashMap<String,Object>();
		List<Object> detail = new ArrayList<Object>();
		
		GDEupsBatchConsoleInfo info = context.getVariable("infoTmp");
		
		String filename = context.getData("filename");
		
		//根据ftpNo查找第三方FTP配置信息
		EupsThdFtpConfig eupsThdFtpConfig = get(EupsThdFtpConfigRepository.class).findOne("waterBatchFile");
		if(eupsThdFtpConfig==null){
			//第三方FTP信息不存在
			log.error("第三方FTP配置信息不存在");
			throw new CoreException(ErrorCodes.EUPS_FTP_INFO_NOTEXIST);
		}
//		Assert.isNotNull(eupsThdFtpConfig, ErrorCodes.EUPS_FTP_INFO_NOTEXIST);
		
		eupsThdFtpConfig.setRmtFleNme(filename);
		//eupsThdFtpConfig.setRmtWay("./");
		//eupsThdFtpConfig.setLocDir("/home");
		
		eupsThdFtpConfig.setLocFleNme(filename);
		log.info("start get batch file now,thd ftp info=["+BeanUtils.toFlatMap(eupsThdFtpConfig)+"]");
		
		get(OperateFTPAction.class).getFileFromFtp(eupsThdFtpConfig);
		
		//根据模板解析第三方的文件，模板ID为"parseBatchFile"
		Map<String,Object> srcFile = parseFileByPath(eupsThdFtpConfig.getLocDir(), 	filename, "watr00FmtIn");
		Assert.isFalse(null==srcFile||0==srcFile.size(), ErrorCodes.EUPS_FILE_PARESE_FAIL, "解析文件出错");
		log.info("srcFile header=["+srcFile.get(ParamKeys.EUPS_FILE_HEADER)+"]");
		log.info("srcFile detail=["+srcFile.get(ParamKeys.EUPS_FILE_DETAIL)+"]");
		Map<String,Object> srcHeader = (Map<String,Object>)srcFile.get(ParamKeys.EUPS_FILE_HEADER);
		
		log.info("~~~~~~~~~~~~"+srcHeader);
		
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
		log.info("totAmt="+context.getData(ParamKeys.TOT_AMT));
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
			//temp.put("CUSNME", "郑永军");
			String name=get(AccountService.class).getAcInf(CommonRequest.build(context), map.get("bcount").toString())
					.getCusName();
			temp.put("CUSNME", null==name?"":name.trim());
			temp.put("TXNAMT", new BigDecimal(map.get("je").toString().trim()).scaleByPowerOfTen(-2));
			temp.put("AGTSRVCUSID", map.get("hno"));
			temp.put("AGTSRVCUSNME", "");
			//本行标志全定0。
			temp.put("OUROTHFLG", "0");
//			temp.put("OUROTHFLG", true==accountService.isOurBankCard((String) map.get("bcount"))?"0":"1");
			String kkh = get(AccountService.class).getAcInf(CommonRequest.build(context), map.get("bcount").toString()).getOpnBr();
//			if("1".equals(temp.get("OUROTHFLG"))&&null==kkh){
//				kkh="3355";
//			}
			temp.put("OBKBK",kkh);
			temp.put("RMK1", "");
			temp.put("RMK2", "");
			detail.add(temp);
			
			String sqn = get(BBIPPublicService.class).getBBIPSequence();
			tmp.setSqn(sqn);
			tmp.setSeqNo(i);
			tmp.setHno((String)map.get("hno"));
			
			tmp.setSj((String)map.get("sj"));
			tmp.setJe((String)map.get("je"));
			//BigDecimal bigDecimal=new BigDecimal(map.get("bcount").toString().trim()).scaleByPowerOfTen(-2);
			tmp.setBcount((String)map.get("bcount"));
			//tmp.setBcount(bigDecimal+"");
			get(GdeupsWatBatInfTmpRepository.class).insert(tmp);
			i++;
		}
		}
		ret.put("header", header);
		ret.put("detail", detail);
		context.setVariable("agtFileMap", ret);
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
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
}
