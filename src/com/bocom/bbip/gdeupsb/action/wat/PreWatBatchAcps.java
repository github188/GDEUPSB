package com.bocom.bbip.gdeupsb.action.wat;

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
import com.bocom.bbip.comp.CommonRequest;
import com.bocom.bbip.comp.account.AccountService;
import com.bocom.bbip.comp.btp.BTPService;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsActSysPara;
import com.bocom.bbip.eups.entity.EupsThdFtpConfig;
import com.bocom.bbip.eups.repository.EupsActSysParaRepository;
import com.bocom.bbip.eups.repository.EupsThdFtpConfigRepository;
import com.bocom.bbip.file.fmt.FileMarshaller;
import com.bocom.bbip.gdeupsb.action.common.OperateFTPActionExt;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.entity.GDEupsBatchConsoleInfo;
import com.bocom.bbip.gdeupsb.entity.GdeupsWatBatInfTmp;
import com.bocom.bbip.gdeupsb.repository.GDEupsBatchConsoleInfoRepository;
import com.bocom.bbip.gdeupsb.repository.GdeupsWatBatInfTmpRepository;
import com.bocom.bbip.gdeupsb.service.impl.watr00.BatchAcpServiceImplWATR00;
import com.bocom.bbip.thd.org.apache.commons.collections.CollectionUtils;
import com.bocom.bbip.utils.Assert;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.ContextUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.JumpException;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class PreWatBatchAcps extends BaseAction{

	private static Logger logger = LoggerFactory.getLogger(BatchAcpServiceImplWATR00.class);
	
	/**批量加锁KEY*/
	private static final String lockKey = "BatchAcpServiceImplWATR00";
	@Autowired
	EupsThdFtpConfigRepository eupsThdFtpConfigRepository;
	
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
	
	public void execute(Context context) throws CoreException,CoreRuntimeException{
		logger.info("PreWatBatchAcps prepareBatchDeal start ... ..."+context);
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
		service.tryLock(lockKey, 60*1000L, 600L);//加锁
//		String br = ContextUtils.assertDataHasLengthAndGetNNR(context, ParamKeys.BR, ErrorCodes.EUPS_FIELD_EMPTY);//机构号
//		String tlr = ContextUtils.assertDataHasLengthAndGetNNR(context, ParamKeys.TELLER, ErrorCodes.EUPS_FIELD_EMPTY);//柜员号
		//虚拟柜员
//		context.setData("br", "01441800999");
//		
//		
//		context.setData("tlr", service.getETeller("01445999999"));
//		context.setData("extFields", "01441800999");
		
		
//		context.setData("br", "01445007999");
//		context.setData("tlr","AFBM013");
//		String br = context.getData("br");
//		String tlr = context.getData("tlr");
		
		context.setData(ParamKeys.BK, service.getParam("GDEUPSB", "stBK"));
		context.setData(ParamKeys.BR, service.getParam("GDEUPSB", "stWatBr"));
		context.setData(ParamKeys.TELLER, service.getETeller(context.getData(ParamKeys.BK).toString()));
		
		String br = context.getData("br");
		String tlr = context.getData("tlr");
		String comNo = ContextUtils.assertDataHasLengthAndGetNNR(context, ParamKeys.COMPANY_NO, ErrorCodes.EUPS_FIELD_EMPTY);//代理单位号
		Date date=get(BBIPPublicService.class).getAcDate();
		
		
		String acDate = DateUtils.format(date, DateUtils.STYLE_yyyyMMdd);//会计日期
		log.info("@@@@@@@@@@@@@@acDate="+ acDate);
		
		
		EupsActSysPara eupsActSysPara = new EupsActSysPara();
		eupsActSysPara.setComNo(comNo);
		eupsActSysPara.setUseSts("0");
		List<EupsActSysPara> eupsActSysParas = get(EupsActSysParaRepository.class).find(eupsActSysPara);
		if(CollectionUtils.isEmpty(eupsActSysParas)){
			
			throw new CoreException(ErrorCodes.EUPS_BUS_TYP_NOEXIST);
		}
		eupsActSysPara = eupsActSysParas.get(0);
		String splNo = eupsActSysPara.getSplNo();
		//代扣文件名称 BATC+单位编号(代收付)+0(代收付类型，代扣0)+.txt
		String filNam = "BATC"+splNo+"0"+".txt";
		context.setVariable(ParamKeys.FLE_NME, filNam);
		//代扣文件放置目录
		// /home/bbipadm/data/mftp/BBIP/请求系统/机构号/柜员号/会计日期
//		String dir = "/home/bbipadm/data/mftp/BBIP/GDEUPSB"+"/"+br+"/"+tlr+"/"+acDate+"/";
//		String dir1 = "/home/bbipadm/data/mftp/BBIP/GDEUPSB/wat";
		
		
		//获取第三方批量文件并解析
		buildBatchFile(context);
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
		logger.info("PreWatBatchAcps prepareBatchDeal end ... ..."+context);
				
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
			throw new CoreException("BBIP4400EU0431");
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
		info.setRsvFld9(batNo);
		context.setData(ParamKeys.THD_BAT_NO, batNo);
		context.setVariable("infoTmp", info);
		get(GDEupsBatchConsoleInfoRepository.class).insert(info);
		context.setDataMap(BeanUtils.toMap(info));
//		context.getDataMapDirectly().putAll(BeanUtils.toFlatMap(info));
	
	}
		
		//生成批扣文件并放到指定的存放路径，模板id为"buildBatchFile"
//		createBatchFileByPath(detail,dir,filNam,"buildBatchFile",header);
//		context.setData(ParamKeys.TOT_CNT, srcHeader.get(ParamKeys.TOT_CNT));
//		context.setData(ParamKeys.TOT_AMT, srcHeader.get(ParamKeys.TOT_AMT));
}
