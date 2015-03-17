package com.bocom.bbip.gdeupsb.action.gas;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.comp.BBIPPublicServiceImpl;
import com.bocom.bbip.comp.btp.BTPService;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.action.common.OperateFTPAction;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsActSysPara;
import com.bocom.bbip.eups.entity.EupsThdFtpConfig;
import com.bocom.bbip.eups.repository.EupsActSysParaRepository;
import com.bocom.bbip.eups.repository.EupsThdFtpConfigRepository;
import com.bocom.bbip.eups.spi.service.batch.BatchAcpService;
import com.bocom.bbip.eups.spi.vo.PrepareBatchAcpDomain;
import com.bocom.bbip.file.Marshaller;
import com.bocom.bbip.file.transfer.TransferUtils;
import com.bocom.bbip.gdeupsb.action.common.BatchFileCommon;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDErrorCodes;
import com.bocom.bbip.gdeupsb.entity.GDEupsBatchConsoleInfo;
import com.bocom.bbip.gdeupsb.entity.GdGashBatchTmp;
import com.bocom.bbip.gdeupsb.repository.GDEupsBatchConsoleInfoRepository;
import com.bocom.bbip.gdeupsb.repository.GdGashBatchTmpRepository;
import com.bocom.bbip.utils.Assert;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.CollectionUtils;
import com.bocom.bbip.utils.ContextUtils;
import com.bocom.jump.bp.JumpException;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

/**
 * 惠州燃气文件批扣数据准备
 * 燃气发起
 * @author WangMQ
 *
 */
public class BatchGashDealServiceAction extends BaseAction implements BatchAcpService  {

	private final static Logger logger = LoggerFactory.getLogger(BatchGashDealServiceAction.class);
	
	@Override
	public void prepareBatchDeal(PrepareBatchAcpDomain arg0, Context context)
			throws CoreException {
		logger.info("批次前准备数据");
		
		context.setData(ParamKeys.TELLER, "ABIR148");
		context.setData(ParamKeys.BR, "01441131999");
		context.setData(ParamKeys.BK, "01441999999");
		
		context.setData(ParamKeys.EUPS_BUSS_TYPE, "PGAS00");
		context.setData(ParamKeys.COMPANY_NO, "GDPGAS0001");
		context.setData(ParamKeys.FTP_ID, "PGAS00Bat");
		
		logger.info("============context:" + context);
		
		String fleNme = (String)context.getData(ParamKeys.FLE_NME);
		/**加锁*/
		String comNo = (String)context.getData(ParamKeys.COMPANY_NO);
		logger.info("==============comNo : " + comNo);
		((BatchFileCommon)get(GDConstants.BATCH_FILE_COMMON_UTILS)).Lock(comNo);
		
		//检查批次是否重复录入，不是重复录入则插入一条数据进批次控制表
		GDEupsBatchConsoleInfo info = new GDEupsBatchConsoleInfo();
		info.setFleNme(fleNme);
		GDEupsBatchConsoleInfo ret =get(GDEupsBatchConsoleInfoRepository.class).findConsoleInfo(info);
		Assert.isTrue(null==ret, GDErrorCodes.EUPS_BATCH_INFO_EXIST);
		/** 插入批次控制表 */
		String batNo =((BTPService)get("BTPService")).applyBatchNo(ParamKeys.BUSINESS_CODE_COLLECTION);
		info.setBatNo(batNo);
		info.setComNo((String)context.getData(ParamKeys.COMPANY_NO));
		info.setBusKnd((String)context.getData(ParamKeys.BUSS_KIND));
		info.setBatSts(GDConstants.BATCH_STATUS_INIT);
		info.setFleNme(fleNme);
		info.setSubDte(new Date());
		info.setTxnTlr((String) context.getData(ParamKeys.TELLER));
		info.setTxnMde(Constants.TXN_MDE_FILE);
		info.setTxnOrgCde((String) context.getData(ParamKeys.BR));
		/**该字段保存ParamKeys.THD_BAT_NO*/
		info.setRsvFld9(((BBIPPublicServiceImpl)get(GDConstants.BBIP_PUBLIC_SERVICE)).getBBIPSequence());
		context.setData(ParamKeys.THD_BAT_NO, info.getRsvFld9());
		get(GDEupsBatchConsoleInfoRepository.class).insertConsoleInfo(info);
		/**查询代收付ComNo*/
		     String comNoAcps=null;
		    EupsActSysPara eupsActSysPara = new EupsActSysPara();
		    eupsActSysPara.setActSysTyp("0");
		    eupsActSysPara.setComNo((String)context.getData(ParamKeys.COMPANY_NO));
		    List<EupsActSysPara> resultList = ((EupsActSysParaRepository)get(EupsActSysParaRepository.class)).find(eupsActSysPara);
		    if (CollectionUtils.isNotEmpty(resultList)) {
		       comNoAcps = ((EupsActSysPara)resultList.get(0)).getSplNo();
		    }
		context.setData("comNoAcps", comNoAcps);
		context.getDataMapDirectly().putAll(BeanUtils.toFlatMap(info));
		
		EupsThdFtpConfig config=get(EupsThdFtpConfigRepository.class).findOne((String)context.getData(ParamKeys.FTP_ID));
		Assert.isFalse(null == config, ErrorCodes.EUPS_THD_FTP_CONFIG_NOTEXIST);

		config.setRmtFleNme(fleNme);
		config.setLocFleNme(fleNme);
		get(OperateFTPAction.class).getFileFromFtp(config);
		
		Map<String,Map<String, Object>> result = pareseFile(config, "PGAS00BatFmt");
		Assert.isFalse(null==result||0==result.size(), ErrorCodes.EUPS_FILE_PARESE_FAIL);
		Map<String, Object> header =new HashMap<String, Object>();
		
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> detail = (List<Map<String, Object>>) result.get("detail");
		GDEupsBatchConsoleInfo batInfo=ContextUtils.getDataAsObject(context, GDEupsBatchConsoleInfo.class);
		get(GDEupsBatchConsoleInfoRepository.class).updateConsoleInfo(batInfo);
		
		//将解析文件得到的数据放入临时表
		List <GdGashBatchTmp>list=(List<GdGashBatchTmp>) BeanUtils.toObjects(detail, GdGashBatchTmp.class);
        for(GdGashBatchTmp tmp:list){
        	tmp.setSqn(get(BBIPPublicService.class).getBBIPSequence());
        	tmp.setBatNo((String)context.getData(ParamKeys.BAT_NO));
        	get(GdGashBatchTmpRepository.class).insert(tmp);
        }
        
        List <GdGashBatchTmp>lt=get(GdGashBatchTmpRepository.class).findByBatNo((String)context.getData(ParamKeys.BAT_NO));
        //  cusNo ----->  thdCusNo
        
        List<Map<String,Object>> gasBatDetail=new ArrayList<Map<String,Object>>();
        
    	String isOurBnk = new String(); // 初始化
    	
        // to sum totAmt
        BigDecimal totAmt = new BigDecimal(0.0);
        
        for(GdGashBatchTmp tmp : lt){
        	Map<String, Object> tmpMap = new HashMap<String, Object>();
//        	<varString name="cusAc" type="D|" />
//    		<varString name="cusNme" type="D|" />
//    		<varString name="txnAmt" type="D|" />
//    		<varString name="thdCusNo" type="D|" />
//    		<varString name="thdCusNme" type="D|" />
//    		<varString name="OUROTHFLG" type="D|" />
//    		<varString name="OBKBK" type="D|" />
//    		<varString name="RMK1" type="D|" />
//    		<varString name="RMK2" type="D|" />
			
        	//TODO：我行卡查询，开户行查询
//        			if (accountService.isOurBankCard(tmp.getCusAc())) {
        				isOurBnk = "0"; // 我行卡
//        			} else {
//        				isOurBnk = "1"; // 他行卡
//        				CusActInfResult actInf = accountService.getAcInf(CommonRequest.build(context), cusAc);
//        				agtFileDeatailMap.put("OBKBK", actInf.getOpnBk());
//        			}
        	
        	tmpMap.put("cusAc", tmp.getCusAc());
        	tmpMap.put("cusNme", tmp.getCusNme());
        	tmpMap.put("txnAmt", tmp.getTxnAmt());
        	tmpMap.put("thdCusNo", tmp.getCusNo());
        	tmpMap.put("OUROTHFLG", isOurBnk);
//        	tmpMap.put("thdCusNme", value);
//        	tmpMap.put("OBKBK", value);
//        	tmpMap.put("RMK1", value);
//        	tmpMap.put("RMK2", value);
        	totAmt = totAmt.add(new BigDecimal(tmp.getTxnAmt()));
        	gasBatDetail.add(tmpMap);
        }
        
		Map<String, Object> temp = new HashMap<String, Object>();
		
		header.put("comNo", comNo);
		header.put("totCnt", lt.size());
		header.put("totAmt", totAmt);
		
		temp.put(ParamKeys.EUPS_FILE_HEADER, header);
		temp.put(ParamKeys.EUPS_FILE_DETAIL, gasBatDetail);
		context.setVariable("agtFileMap", temp);
		GDEupsBatchConsoleInfo console=new GDEupsBatchConsoleInfo();
		console.setBatNo((String)context.getData(ParamKeys.BAT_NO));
		/**更新批次状态为待提交*/
		console.setBatSts(GDConstants.BATCH_STATUS_WAIT);
		get(GDEupsBatchConsoleInfoRepository.class).updateConsoleInfo(console);
		((BatchFileCommon)get(GDConstants.BATCH_FILE_COMMON_UTILS)).sendBatchFileToACP(context);
		
		((BatchFileCommon)get(GDConstants.BATCH_FILE_COMMON_UTILS)).unLock(comNo);
		logger.info("批量文件数据准备结束-------------");
		
	}
	
	  private Map<String,Map<String, Object>> pareseFile(EupsThdFtpConfig eupsThdFtpConfig, String fileId)
	    throws CoreException, CoreRuntimeException
	  {
		  Map map =null;
	    logger.info("this is path:" + TransferUtils.resolveFilePath(eupsThdFtpConfig.getLocDir().trim(), eupsThdFtpConfig.getLocFleNme().trim()));
	    try {
	    	Resource resource = new FileSystemResource(TransferUtils.resolveFilePath(eupsThdFtpConfig.getLocDir().trim(), 
	        eupsThdFtpConfig.getLocFleNme().trim()));
	      map = (Map)((Marshaller)get(Marshaller.class)).unmarshal(fileId, resource, Map.class);
	    } catch (JumpException e) {
	      logger.error("文件解析错误");
	      throw new CoreException("BBIP0004EU0015");
	    }
	    return map;
	  }
	
}
