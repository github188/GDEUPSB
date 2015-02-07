package com.bocom.bbip.gdeupsb.action.fbpd;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import com.bocom.bbip.eups.action.common.OperateFTPAction;
import com.bocom.bbip.eups.action.common.OperateFileAction;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.entity.EupsBatchPayEntity;
import com.bocom.bbip.eups.entity.EupsThdFtpConfig;
import com.bocom.bbip.eups.repository.EupsThdFtpConfigRepository;
import com.bocom.bbip.eups.spi.service.batch.BatchAcpService;
import com.bocom.bbip.eups.spi.vo.PrepareBatchAcpDomain;
import com.bocom.bbip.file.fmt.FileMarshaller;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDErrorCodes;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GdBatchConsoleInfo;
import com.bocom.bbip.gdeupsb.repository.GdBatchConsoleInfoRepository;
import com.bocom.bbip.utils.FileUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

/**
 * 中山文件批量系统——文件导入及检查
 * 目标：数据准备及生成批量文件
 * 网点经办人员接到委托单位交来的代收数据盘，插入指定的设备插口，
 * 启动“481201 文件录入及检查”，输入单位编号、总笔数、总金额。选择 U 盘读入获取数据，交易成功后打印个人业务受理通知书， 打印批次号。
 * @author WangMQ
 *
 */
public class CheckAndImportFileFbpdAction implements BatchAcpService {

	private static Logger logger = LoggerFactory.getLogger(CheckAndImportFileFbpdAction.class);
	
	@Autowired
	GdBatchConsoleInfoRepository gdBatchConsoleInfoRepository;
	
	@Autowired
	EupsThdFtpConfigRepository eupsThdFtpConfigRepository;
	
	@Autowired
	OperateFileAction operateFileAction;
	
	@Autowired
	OperateFTPAction  operateFTPAction;
	
	
	@Override
	public List<EupsBatchPayEntity> prepareBatchDeal(PrepareBatchAcpDomain prepareBatchAcpDomain, Context context) throws CoreException {
		logger.info("Enter in CheckAndImportFileFbpdAction!!!!");
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
		
		context.setData(GDParamKeys.FBPD_TXN_OBJ, "OFTDFBPA");
		String fleNme = context.getData("fleNme").toString().trim();
		//检查批次文件是否重复录入
//		SELECT DskNo FROM pubbatinf WHERE ActDat='%s' AND DskNam='%s' AND Status='P'
//		ActDat:文件提交日期
		GdBatchConsoleInfo batchConsoleInfo = new GdBatchConsoleInfo();
		batchConsoleInfo.setSubDte(new Date());
		batchConsoleInfo.setFleNme(fleNme);
		List<GdBatchConsoleInfo> batInfoList = gdBatchConsoleInfoRepository.find(batchConsoleInfo);
		if(!CollectionUtils.isEmpty(batInfoList)){
			context.setData(GDParamKeys.GDEUPSB_RSP_COD, GDConstants.FDPD_RSP_COD);
			context.setData(GDParamKeys.GDEUPSB_RSP_MSG, "该批次已录入，批次号为" + batInfoList.get(0).getBatNo());
			throw new CoreRuntimeException(GDErrorCodes.EUPS_CKDB_FAIL);
		}
		logger.info("============无重复录入文件");
		context.setData(GDParamKeys.GDEUPSB_RSP_COD, GDConstants.GDEUPSB_TXN_SUCC_CODE);
		
/*     
		//获取FTP配置	(含有远程、本地文件路径以及文件名)
		EupsThdFtpConfig eupsThdFtpInf = eupsThdFtpConfigRepository.findOne("ZSAG00");
		eupsThdFtpInf.setRmtFleNme(context.getData("fleNme").toString());
		eupsThdFtpInf.setLocFleNme(context.getData("fleNme").toString());
		operateFTPAction.getFileFromFtp(eupsThdFtpInf);
		logger.info("============文件已获取并放置于本地目录");
*/
		
/*
 * 网点经办人员接到委托单位交来的代收数据盘，插入指定的设备插口，
 * 启动“481201 文件录入及检查”，输入单位编号、总笔数、总金额。选择 U 盘读入获取数据，交易成功后打印个人业务受理通知书， 打印批次号。
 */
		//TODO 文件路径
		 String srcFilName= "D:/fbpdTest/recv/"+fleNme;
	     String objFilName ="D:/fbpdTest/fbpd/"+fleNme;
	       
	        File srcFil =new File(srcFilName);
	        File objFil =new File(objFilName);
	        
	        try {
	            FileUtils.copyFile(srcFil, objFil);
	            logger.info("============文件已获取并放置于指定目录，待入库。。。。。。");
	        } catch (IOException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
		
	    //不同业务有不同文件格式，水电燃煤、有线电视、移动pos，，，，，，用单位编号区分
		//解析文件，拆分入库（放进临时表）
	    //从临时表中取数据，生成指定格式的文件
	         
	        //Resource resource = new FileSystemResource(TransferUtils.resolveFilePath(eupsThdFtpInf.getLocDir().trim(), eupsThdFtpInf.getLocFleNme().trim()));
	        Resource resource = new FileSystemResource(objFil);
	        Map<String,Object> map = new HashMap<String, Object>(); 
	        FileMarshaller fileMarshaller = new FileMarshaller();
	        
	        if("WATR".equals(context.getData("comNo"))){	//中山水费 WATR   fbpdWaterFmtIn
//	        	map=fileMarshaller.unmarshal("fbpdWaterFmtIn", resource, Map.class);
	        }
	        if("NELE1".equals(context.getData("comNo"))){	//NELE_in_484999_1 电费 fbpdNele1FmtIn
	        	
	        }
	        if("NELE".equals(context.getData("comNo"))){	//NELE_in_484999 电费	fbpdNeleFmtIn
	        	
	        }
	        if("GGAS".equals(context.getData("comNo"))){	//GGAS_in_484999 中山煤气费	fbpdGgasFmtIn
	        	
	        }
	        if("VANK".equals(context.getData("comNo"))){	//VANK_out_484999中山物业管理费 fbpdVankFmtIn
	        	
	        }
	        if("TTOM".equals(context.getData("comNo"))){	//TTOM_in_484999中山铁通 fbpdTtomFmtIn
	        	
	        }
	        if("CTTV".equals(context.getData("comNo"))){	//CTTV_in_484999 中山有线电视 fbpdCttvFmtIn
	        
	        }
	        if("NQTV".equals(context.getData("comNo"))){	//NQTV_in_484999南区有线电视 fbpdNqtvFmtIn
	        	
	        }
	        if("XIND".equals(context.getData("comNo"))){	//XIND_in_484999 新都物业管理 fbpdXindFmtIn
	        	
	        }
	        if("MPOS".equals(context.getData("comNo"))){	//MPOS_in_484999 中山移动POS fbpdMposFmtIn
	        	
	        }

	        Map<String,Object> orgHeadMap=(Map<String, Object>) map.get("header");
	        List<Map<String, Object>> parseMap = (List<Map<String, Object>>) map.get("detail");  //文件体
	       // List<Map<String, Object>> parseMap = operateFile.pareseFile(eupsThdFtpInf, "eleGzBatFmt"); // 解析只有detail文件
	        
	        /*for (Map<String, Object> orgMap : parseMap) {
	             batchPayDtl = new ();
	            batchPayDtl.setCusAc((String) orgMap.get("cusAc")); // 客户帐号
	            BigDecimal txnAmt = NumberUtils.centToYuan((String) orgMap.get("tTxnAmt")); // 交易金额
	            batchPayDtl.setTxnAmt(txnAmt);
	            batchPayDtl.setAgtSrvCusId((String) orgMap.get("thdCusNo")); // 第三方客户标志
	            payDetailLst.add(batchPayDtl);
	        }*/
//	        context.setData(ParamKeys.EUPS_BATCH_PAY_ENTITY_LIST, payDetailLst);

		
		
		
		
		
		
		
		
		
		return null;
	}

}
