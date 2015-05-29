package com.bocom.bbip.gdeupsb.action.gzag.batch;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.comp.BBIPPublicServiceImpl;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.action.common.OperateFileAction;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsThdFtpConfig;
import com.bocom.bbip.eups.repository.EupsThdFtpConfigRepository;
import com.bocom.bbip.file.Marshaller;
import com.bocom.bbip.file.MftpTransfer;
import com.bocom.bbip.file.transfer.TransferUtils;
import com.bocom.bbip.gdeupsb.action.common.BatchFileCommon;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDErrorCodes;
import com.bocom.bbip.gdeupsb.entity.GDEupsBatchConsoleInfo;
import com.bocom.bbip.gdeupsb.entity.GDEupsGzagBatchTmp;
import com.bocom.bbip.gdeupsb.repository.GDEupsBatchConsoleInfoRepository;
import com.bocom.bbip.gdeupsb.repository.GDEupsGzagBatchTmpRepository;
import com.bocom.bbip.gdeupsb.utils.GdExpCommonUtils;
import com.bocom.bbip.utils.Assert;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.ContextUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.JumpException;
import com.bocom.jump.bp.SystemConfig;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

/**
 * 广州文本格式转换
 * 
 * @author WMQ
 * 
 */
public class ChangeFileFmtAction extends BaseAction {

	private Logger logger = LoggerFactory.getLogger(ChangeFileFmtAction.class);

	@Autowired
	BBIPPublicService bbipPublicService;
	@Autowired
	Marshaller marshaller;
	@Autowired
	GDEupsGzagBatchTmpRepository gdEupsGzagBatchTmpRepository;

	@Override
	public void execute(Context context) throws CoreException,
			CoreRuntimeException {
		/**
		 * 不进行协议验证，不提交，只进行文件格式转换 将文件信息插入批次控制表，并获取批次号，作为当前进行的文件转换的唯一标识
		 * 1.从前端服务器取得文件 2.解析文件，入库临时表 GDEupsGzagBatchTmp 计算总笔数，总金额
		 * 判断前端输入的总笔数总金额是否与计算结果一致 3.从临时表取数据，组成代收付格式文件 4.文件发往前端服务器，由前端进行拷盘
		 * 
		 * 输出文件 BATC+comNo+1.txt
		 */
		// 来盘文件FMT : changeFileFmt
		logger.info("ChangeFileFmtAction start with context : " + context);
		int totalCount = Integer.parseInt(context.getData(ParamKeys.CUS_AC)
				.toString().trim());
		BigDecimal totalAmt = (BigDecimal) context
				.getData(ParamKeys.TXN_AMOUNT);

		String comNo = context.getData("thdRgnNo").toString().trim();
		String fileName = context.getData("thdCusNo").toString().trim();
		context.setData(ParamKeys.COMPANY_NO, comNo);
		context.setData(ParamKeys.FLE_NME, fileName);

		((BatchFileCommon) get(GDConstants.BATCH_FILE_COMMON_UTILS))
				.BeforeBatchProcess(context);
		String batNo = context.getData("batNo").toString();

		// u盘文件获取
		EupsThdFtpConfig eupsThdFtpConfig = get(
				EupsThdFtpConfigRepository.class).findOne("zhag00");
		String filPath = eupsThdFtpConfig.getLocDir();
		try {
			bbipPublicService.getFileFromBBOS(new File(filPath, fileName),
					fileName, MftpTransfer.FTYPE_NORMAL);
			logger.info("===============获取文件成功");
		} catch (Exception e) {
			throw new CoreException(ErrorCodes.EUPS_MFTP_FILEDOWN_FAIL);
		}

		String filePath = eupsThdFtpConfig.getLocDir();
		// 自行实现解析文件
		Map<String, List<Map<String, Object>>> map = new HashMap<String, List<Map<String, Object>>>();
		Resource resource = new FileSystemResource(
				TransferUtils.resolveFilePath(filePath, fileName));
		try {
			map = marshaller.unmarshal("changeFileFmt", resource, Map.class);
		} catch (JumpException e) {
			e.printStackTrace();
		}
		logger.info("===============解析文件成功");
//        Map head=(Map)map.get("header");
//		if (null!=head) {
//					context.getDataMapDirectly().putAll(head);
//		}
		List<Map<String, Object>> batchDetailLst = (List<Map<String, Object>>) map
				.get("detail");
		
		/** 插入临时表中 */
		List<GDEupsGzagBatchTmp> listToBatchTmp = (List<GDEupsGzagBatchTmp>) BeanUtils
				.toObjects(batchDetailLst, GDEupsGzagBatchTmp.class);
		int i = 0;
		BigDecimal amtTot = new BigDecimal("0.00");
		BigDecimal tmpTxnAmt = new BigDecimal("0.00");
		String tmpAmt = null;
		
		List<Map<String, Object>> agtFileDetail = new ArrayList<Map<String, Object>>(); // 代收付文件detail
		for(GDEupsGzagBatchTmp tmp : listToBatchTmp){ // 入库  && 统计  &&  准备文件明细数据
			tmp.setSqn(bbipPublicService.getBBIPSequence());
			tmp.setBakFld(batNo);
			
			tmpAmt = GdExpCommonUtils.AMTFMT2(tmp.getRsvFld5().toString().trim());
			tmpTxnAmt = new BigDecimal(tmpAmt);
			tmp.setTxnAmt(tmpTxnAmt);
			
			i++;
			amtTot = amtTot.add(tmpTxnAmt);
			
			gdEupsGzagBatchTmpRepository.insert(tmp);
			
			Map<String, Object> detailMap = new HashMap<String, Object>();	
			detailMap.put("CUSAC", tmp.getCusAc().toString().trim());
			detailMap.put("CUSNME", tmp.getThdCusNme().toString().trim());
			detailMap.put("TXNAMT", tmpTxnAmt);
			detailMap.put("OUROTHFLG", "0");
			agtFileDetail.add(detailMap);
		}
		
		// 校验交易总金额总笔数是否和前端输入一致
		if((i != totalCount) || ( 0 != totalAmt.compareTo(amtTot))){
			throw new CoreException(GDErrorCodes.EUPS_INPUT_ERR);
		}
		
		context.setData("totCnt", i);
		context.setData("totAmt", amtTot.toString());
		
		Map<String, Object> headAgt = new HashMap<String, Object>();
		headAgt.put("totCnt", i);
		headAgt.put("totAmt", amtTot.toString());
		headAgt.put("comNo", comNo);
		
		Map<String, Object> fileTmp = new HashMap<String, Object>();
		fileTmp.put(ParamKeys.EUPS_FILE_HEADER, headAgt);
		fileTmp.put(ParamKeys.EUPS_FILE_DETAIL, agtFileDetail);
		context.setVariable("agtFileMap", fileTmp);

		creatAndSendChangedFileToBBOS(context);
		
		String fileNameToBBOS = context.getData("fleNme");
		context.setData(ParamKeys.MFM_VCH_NO, fileNameToBBOS);
		context.setData(ParamKeys.TXN_AMOUNT, amtTot);
		context.setData("fee", new BigDecimal("0.00"));
		context.setData("status", "S");
		context.setData("retNum", i);
		
		logger.info("ChangeFileFmtAction end with context : " + context);
	}

	private void creatAndSendChangedFileToBBOS(Context context) throws CoreException {
		final String comNo=(String)context.getData(ParamKeys.COMPANY_NO);
		String tlr=(String)context.getData(ParamKeys.TELLER);
		final String br=(String)context.getData(ParamKeys.BR);
        final String AcDate=DateUtils.format(((BBIPPublicServiceImpl)get(GDConstants.BBIP_PUBLIC_SERVICE)).getAcDate(),DateUtils.STYLE_yyyyMMdd);
        final String systemCode=((SystemConfig)get(SystemConfig.class)).getSystemCode();
        //路径
        final String dir=get(BBIPPublicService.class).getParam("batchPath")+systemCode+"/"+br+"/"+tlr+"/"+AcDate+"/";
        context.setData("dir", dir);
        File file=new File(dir);
        if(!file.exists()){
        		file.mkdirs();
        }
        
        final String fleNme="BATC"+comNo+"1.txt";
        context.setData("fleNme", fleNme);
        Map<String, Object> fileMap = (Map<String, Object>) ContextUtils.assertVariableNotNullAndGet(context, "agtFileMap", ErrorCodes.EUPS_FIELD_EMPTY,"agtFileMap");
        EupsThdFtpConfig config = get(EupsThdFtpConfigRepository.class).findOne(ParamKeys.FTPID_BATCH_PAY_FILE_TO_ACP);
		Assert.isFalse(null==config, ErrorCodes.EUPS_FTP_INFO_NOTEXIST);
		//设置文件名 文件路径
		config.setLocFleNme(fleNme);
		config.setRmtFleNme(fleNme);
		config.setRmtWay(dir);
		config.setLocDir(dir);
		
		/** 产生代收付格式文件 */
		try{
				((OperateFileAction)get("opeFile")).createCheckFile(config, "changeFileFmtAcp", fleNme, fileMap);
			logger.info("===============生成代收付文件");
		}catch(Exception e){
			logger.info("==============请检查代收付目录["+config.getLocDir()+"],是否存在该文件["+fleNme+"]");
			throw new CoreException("文件错误或其他异常，请联系系统管理员;",e);
		}
		
		//sendFileToBBOS
		try {
			bbipPublicService.sendFileToBBOS(new File(dir, fleNme),
					fleNme, MftpTransfer.FTYPE_NORMAL);
		} catch (Exception e) {
			throw new CoreException(ErrorCodes.EUPS_FTP_FILEPUT_NFAIL);
		}
		
		//更改状态为待提交
		GDEupsBatchConsoleInfo gdEupsBatchConsoleInfo=new GDEupsBatchConsoleInfo();
		String batNo=context.getData(ParamKeys.BAT_NO).toString();
		gdEupsBatchConsoleInfo.setBatNo(batNo);
		gdEupsBatchConsoleInfo.setBatSts("W");
		Integer totCnt=Integer.parseInt((context.getData("totCnt")+""));
		BigDecimal totAmt=new BigDecimal((context.getData("totAmt")+""));
		gdEupsBatchConsoleInfo.setTotCnt(totCnt);
		gdEupsBatchConsoleInfo.setTotAmt(totAmt);
		gdEupsBatchConsoleInfo.setRsvFld1("文件格式转换");
		get(GDEupsBatchConsoleInfoRepository.class).updateConsoleInfo(gdEupsBatchConsoleInfo);
	
		
		logger.info("==============End sendBatchFileToACP and putCheckFile");
		
	}
}
