package com.bocom.bbip.gdeupsb.action.fbpd;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
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

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.action.common.OperateFTPAction;
import com.bocom.bbip.eups.action.common.OperateFileAction;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.ParamKeys;
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
import com.bocom.bbip.gdeupsb.entity.GdFbpdMposBatchTmp;
import com.bocom.bbip.gdeupsb.entity.GdFbpdNeleBatchTmp;
import com.bocom.bbip.gdeupsb.entity.GdFbpdObusBatchTmp;
import com.bocom.bbip.gdeupsb.repository.GdBatchConsoleInfoRepository;
import com.bocom.bbip.gdeupsb.repository.GdFbpdMposBatchTmpRepository;
import com.bocom.bbip.gdeupsb.repository.GdFbpdNeleBatchTmpRepository;
import com.bocom.bbip.gdeupsb.repository.GdFbpdObusBatchTmpRepository;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.FileUtils;
import com.bocom.bbip.utils.NumberUtils;
import com.bocom.jump.bp.JumpException;
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
	GdFbpdObusBatchTmpRepository eupsFbpdObusBatchTmpRepository;
	
	@Autowired
	GdFbpdMposBatchTmpRepository eupsFbpdMposBatchTmpRepository;
	
	@Autowired
	GdFbpdNeleBatchTmpRepository eupsFbpdNeleBatchTmpRepository;
	
	@Autowired
	OperateFileAction operateFileAction;
	
	@Autowired
	OperateFTPAction  operateFTPAction;
	
	@Autowired
	BBIPPublicService bbipPublicService;
	
	
	@Override
	public void prepareBatchDeal(PrepareBatchAcpDomain prepareBatchAcpDomain, Context context) throws CoreException {
		logger.info("Enter in CheckAndImportFileFbpdAction!!!!");
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
		
		List<GdFbpdObusBatchTmp> payOthrDetailLst = new ArrayList<GdFbpdObusBatchTmp>();
		
		List<GdFbpdMposBatchTmp> payMposDetailLst = new ArrayList<GdFbpdMposBatchTmp>();
		
		List<GdFbpdNeleBatchTmp> payNeleDetailLst = new ArrayList<GdFbpdNeleBatchTmp>();
		
		
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
	        Map<String, List<Map<String, Object>>> map = new HashMap<String, List<Map<String, Object>>>(); 
	        FileMarshaller fileMarshaller = new FileMarshaller();
	        
	        if("WATR".equals(context.getData("comNo"))){	//中山水费 WATR   fbpdWaterFmtIn
	        	try {
					map=fileMarshaller.unmarshal("fbpdWaterFmtIn", resource, Map.class);
				} catch (JumpException e) {
					e.printStackTrace();
				}
	        	String fileName = "waterFile";
	        	parseOthrMapList(map, payOthrDetailLst, context, fileName);
	        }
	        if("NELE1".equals(context.getData("comNo"))){	//NELE_in_484999_1 电费 fbpdNele1FmtIn
	        	try {
					map=fileMarshaller.unmarshal("fbpdNele1FmtIn", resource, Map.class);
				} catch (JumpException e) {
					e.printStackTrace();
				}
	        	String fileName = "nele1File";
	        	parseOthrMapList(map, payOthrDetailLst, context, fileName);
	        }
	        if("NELE".equals(context.getData("comNo"))){	//NELE_in_484999 电费	fbpdNeleFmtIn
	        	try {
					map=fileMarshaller.unmarshal("fbpdNeleFmtIn", resource, Map.class);
				} catch (JumpException e) {
					e.printStackTrace();
				}
	        	String fileName = "neleFile";
	        	parseNeleMapList(map, payNeleDetailLst, context, fileName);
	        }
	        if("GGAS".equals(context.getData("comNo"))){	//GGAS_in_484999 中山煤气费	fbpdGgasFmtIn
	        	try {
					map=fileMarshaller.unmarshal("fbpdGgasFmtIn", resource, Map.class);
				} catch (JumpException e) {
					e.printStackTrace();
				}
	        	String fileName = "ggasFile";
	        	parseOthrMapList(map, payOthrDetailLst, context, fileName);
	        }
	        if("VANK".equals(context.getData("comNo"))){	//VANK_out_484999中山物业管理费 fbpdVankFmtIn
	        	try {
					map=fileMarshaller.unmarshal("fbpdVankFmtIn", resource, Map.class);
				} catch (JumpException e) {
					e.printStackTrace();
				}
	        	String fileName = "vankFile";
	        	parseOthrMapList(map, payOthrDetailLst, context, fileName);
	        }
	        if("TTOM".equals(context.getData("comNo"))){	//TTOM_in_484999中山铁通 fbpdTtomFmtIn
	        	try {
					map=fileMarshaller.unmarshal("fbpdTtomFmtIn", resource, Map.class);
				} catch (JumpException e) {
					e.printStackTrace();
				}
	        	String fileName = "ttomFile";
	        	parseOthrMapList(map, payOthrDetailLst, context, fileName);
	        }
	        if("CTTV".equals(context.getData("comNo"))){	//CTTV_in_484999 中山有线电视 fbpdCttvFmtIn
	        	try {
					map=fileMarshaller.unmarshal("fbpdCttvFmtIn", resource, Map.class);
				} catch (JumpException e) {
					e.printStackTrace();
				}
	        	String fileName = "cttvFile";
	        	parseOthrMapList(map, payOthrDetailLst, context, fileName);
	        }
	        if("NQTV".equals(context.getData("comNo"))){	//NQTV_in_484999南区有线电视 fbpdNqtvFmtIn
	        	try {
					map=fileMarshaller.unmarshal("fbpdNqtvFmtIn", resource, Map.class);
				} catch (JumpException e) {
					e.printStackTrace();
				}
	        	String fileName = "nqtvFile";
	        	parseOthrMapList(map, payOthrDetailLst, context, fileName);
	        }
	        if("XIND".equals(context.getData("comNo"))){	//XIND_in_484999 新都物业管理 fbpdXindFmtIn
	        	try {
					map=fileMarshaller.unmarshal("fbpdXindFmtIn", resource, Map.class);
				} catch (JumpException e) {
					e.printStackTrace();
				}
	        	String fileName = "xindFile";
	        	parseOthrMapList(map, payOthrDetailLst, context, fileName);
	        }
	        if("MPOS".equals(context.getData("comNo"))){	//MPOS_in_484999 中山移动POS fbpdMposFmtIn
	        	try {
					map=fileMarshaller.unmarshal("fbpdMposFmtIn", resource, Map.class);
				} catch (JumpException e) {
					e.printStackTrace();
				}
	        	String formatName = "fbpdMposFmtIn";
	        	String fileName = "mposFile";
	        	parseMposMapList(map, payMposDetailLst, context, fileName, formatName);
	        }
	        
	    }
	
	
	/**
	 * 解析批量文件并入库，将带生成批量文件的数据set进context--电费NELE
	 * @param map
	 * @param payNeleDetailLst
	 * @param context
	 * @param fileName 
	 * @return
	 */
	private List<Map<String, Object>> parseNeleMapList( Map<String, List<Map<String, Object>>> map,	List<GdFbpdNeleBatchTmp> payNeleDetailLst, Context context, String fileName) {
		Map<String, Object> orgHeadMap = (Map<String, Object>) map.get("header");

		// 循环拼装代收付文件明细map
		List<Map<String, Object>> agtDeatil = new ArrayList<Map<String, Object>>(); // 代收付明细体
		
		List<Map<String, Object>> parseMap = (List<Map<String, Object>>) map.get("detail"); // 文件体
		
		List<GdFbpdNeleBatchTmp> bthTmpSession = new ArrayList<GdFbpdNeleBatchTmp>();
		
		for (Map<String, Object> orgMap : parseMap) {
			
			GdFbpdNeleBatchTmp eupsFbpdNeleBatchTmp = BeanUtils.toObject(orgMap, GdFbpdNeleBatchTmp.class);
			eupsFbpdNeleBatchTmp.setSqn(bbipPublicService.getBBIPSequence());
			
			
			
			
			
		}
		
		// 拼装代收付Map
				Map<String, Object> agtMap = new HashMap<String, Object>();
				Map<String, Object> agtHeaderMap = new HashMap<String, Object>(); // 头

				String totCnt = (String) orgHeadMap.get("TotCnt"); // 总笔数
				String totAmt = (String) orgHeadMap.get("TotAmt"); // 总金额
				BigDecimal totAmtD = NumberUtils.centToYuan(totAmt); // 总金额

				// 拼装代收付头文件
				agtHeaderMap.put("totCount", totCnt); // 总比数
				agtHeaderMap.put("totAmt", totAmtD.toString()); // 总金额
//				agtHeaderMap.put("comNo", comNo); // 单位编号

				// 拼装整个代收付文件
				agtMap.put("header", agtHeaderMap);
				agtMap.put("detail", agtDeatil);

				context.setVariable(GDParamKeys.BATCH_COM_FILE_NAME, "neleFile");
				context.setVariable("agtFileMap", agtMap);
		return null;
		
	}

	
	/**
	 * 解析批量文件并入库，将带生成批量文件的数据set进context--中山移动POS
	 * @param map
	 * @param payMposDetailLst
	 * @param context
	 * @return
	 */
	private List<Map<String, Object>> parseMposMapList( Map<String, List<Map<String, Object>>> map,
			List<GdFbpdMposBatchTmp> payMposDetailLst, Context context, String fileName, String formatName) {
		  // Map<String,Object> orgHeadMap=(Map<String, Object>) map.get("header");
        List<Map<String, Object>> parseMap = (List<Map<String, Object>>) map.get("detail");  //文件体
        // List<Map<String, Object>> parseMap = operateFile.pareseFile(eupsThdFtpInf, "eleGzBatFmt"); // 解析只有detail文件
        for (Map<String, Object> orgMap : parseMap) {
        	
        	GdFbpdMposBatchTmp batchTmp = new GdFbpdMposBatchTmp();
        	batchTmp.setThdSqn((String) orgMap.get("thdSqn"));
        	batchTmp.setPosNo((String) orgMap.get("posNo"));
        	batchTmp.setComAc((String) orgMap.get("comAc"));
        	batchTmp.setComNo((String) orgMap.get("comNo"));
        	batchTmp.setCusAc((String) orgMap.get("cusAc"));
        	batchTmp.setTxnDte((String) orgMap.get("txnDte"));
        	batchTmp.setTxnTme((String) orgMap.get("txnTme"));
        	batchTmp.setTxnFee((String) orgMap.get("txnFee"));
        	batchTmp.setTxnAmt((String) orgMap.get("txnAmt"));
        	batchTmp.setTxnChr((String) orgMap.get("txnChr"));
        	batchTmp.setTxnRnt((String) orgMap.get("txnRnt"));
        	batchTmp.setChkDate((String) orgMap.get("chkDate"));
        	batchTmp.setSeqNo((String) orgMap.get("seqNo"));
        	batchTmp.setPosFld1(formatName);	//备用字段1	生成文件格式名
        	batchTmp.setPosFld2((String) orgMap.get("posFld2"));
        	batchTmp.setPosFld3((String) orgMap.get("posFld3"));
        	batchTmp.setPosFld4((String) orgMap.get("posFld4"));
        	batchTmp.setPosFld5((String) orgMap.get("posFld5"));
        	
//        	GdFbpdMposBatchTmp batchTmp = BeanUtils.toObject(orgMap, GdFbpdMposBatchTmp.class);
        	eupsFbpdMposBatchTmpRepository.insert(batchTmp);
            payMposDetailLst.add(batchTmp);
        }
        context.setData(ParamKeys.EUPS_BATCH_PAY_ENTITY_LIST, payMposDetailLst);
				
		return null;
		
	}

	
	/**
	 * 解析批量文件并入库，将带生成批量文件的数据set进context--其他
	 * @param map
	 * @param payOthrDetailLst
	 * @param context
	 * @return
	 */
	private List<Map<String, Object>> parseOthrMapList( Map<String, List<Map<String, Object>>> map, List<GdFbpdObusBatchTmp> payOthrDetailLst, Context context, String fileName){
		  // Map<String,Object> orgHeadMap=(Map<String, Object>) map.get("header");
        List<Map<String, Object>> parseMap = (List<Map<String, Object>>) map.get("detail");  //文件体
        // List<Map<String, Object>> parseMap = operateFile.pareseFile(eupsThdFtpInf, "eleGzBatFmt"); // 解析只有detail文件
        for (Map<String, Object> orgMap : parseMap) {
        	
//        	GdFbpdObusBatchTmp batchTmp = new GdFbpdObusBatchTmp();
//        	batchTmp.setComNo((String)orgMap.get("comNo"));
//        	batchTmp.setCusAc((String)orgMap.get("cusAc"));
//        	batchTmp.setCusNo((String)orgMap.get("cusNo"));;
//        	batchTmp.setCusNme((String)orgMap.get("cusNme"));
//        	batchTmp.setThdCusNo((String)orgMap.get("thdCusNo"));
//        	batchTmp.setCcy((String)orgMap.get("ccy"));
//        	batchTmp.setTxnAmt((String)orgMap.get("txnAmt"));
//        	batchTmp.setSucFlg((String)orgMap.get("sucFlg"));
//        	batchTmp.setChrDte((String)orgMap.get("chrDte"));
//        	batchTmp.setSubDte((String)orgMap.get("subDte"));
//        	batchTmp.setSeqNo((String)orgMap.get("seqNo"));
//        	batchTmp.setTmpFld1((String)orgMap.get("tmpFld1"));
//        	batchTmp.setTmpFld2((String)orgMap.get("tmpFld2"));
//        	batchTmp.setTmpFld3((String)orgMap.get("tmpFld3"));
//        	batchTmp.setTmpFld4((String)orgMap.get("tmpFld4"));
//        	batchTmp.setTmpFld5((String)orgMap.get("tmpFld5"));
        	
        	GdFbpdObusBatchTmp batchTmp = BeanUtils.toObject(orgMap, GdFbpdObusBatchTmp.class);
            eupsFbpdObusBatchTmpRepository.insert(batchTmp);
            payOthrDetailLst.add(batchTmp);
        }
        context.setData(ParamKeys.EUPS_BATCH_PAY_ENTITY_LIST, payOthrDetailLst);
		
		return null;
	}
	
	
	
	}