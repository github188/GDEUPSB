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
		//�������ļ��Ƿ��ظ�¼��
//		SELECT DskNo FROM pubbatinf WHERE ActDat='%s' AND DskNam='%s' AND Status='P'
//		ActDat:�ļ��ύ����
		GdBatchConsoleInfo batchConsoleInfo = new GdBatchConsoleInfo();
		batchConsoleInfo.setSubDte(new Date());
		batchConsoleInfo.setFleNme(fleNme);
		List<GdBatchConsoleInfo> batInfoList = gdBatchConsoleInfoRepository.find(batchConsoleInfo);
		if(!CollectionUtils.isEmpty(batInfoList)){
			context.setData(GDParamKeys.GDEUPSB_RSP_COD, GDConstants.FDPD_RSP_COD);
			context.setData(GDParamKeys.GDEUPSB_RSP_MSG, "�������¼�룬��κ�Ϊ" + batInfoList.get(0).getBatNo());
			throw new CoreRuntimeException(GDErrorCodes.EUPS_CKDB_FAIL);
		}
		logger.info("============���ظ�¼���ļ�");
		context.setData(GDParamKeys.GDEUPSB_RSP_COD, GDConstants.GDEUPSB_TXN_SUCC_CODE);
		
/*     
		//��ȡFTP����	(����Զ�̡������ļ�·���Լ��ļ���)
		EupsThdFtpConfig eupsThdFtpInf = eupsThdFtpConfigRepository.findOne("ZSAG00");
		eupsThdFtpInf.setRmtFleNme(context.getData("fleNme").toString());
		eupsThdFtpInf.setLocFleNme(context.getData("fleNme").toString());
		operateFTPAction.getFileFromFtp(eupsThdFtpInf);
		logger.info("============�ļ��ѻ�ȡ�������ڱ���Ŀ¼");
*/
		
/*
 * ��㾭����Ա�ӵ�ί�е�λ�����Ĵ�������̣�����ָ�����豸��ڣ�
 * ������481201 �ļ�¼�뼰��顱�����뵥λ��š��ܱ����ܽ�ѡ�� U �̶����ȡ��ݣ����׳ɹ����ӡ����ҵ������֪ͨ�飬 ��ӡ��κš�
 */
		//TODO �ļ�·��
		 String srcFilName= "D:/fbpdTest/recv/"+fleNme;
	     String objFilName ="D:/fbpdTest/fbpd/"+fleNme;
	       
	        File srcFil =new File(srcFilName);
	        File objFil =new File(objFilName);
	        
	        try {
	            FileUtils.copyFile(srcFil, objFil);
	            logger.info("============�ļ��ѻ�ȡ��������ָ��Ŀ¼������⡣����������");
	        } catch (IOException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
		
	    //��ͬҵ���в�ͬ�ļ���ʽ��ˮ��ȼú�����ߵ��ӡ��ƶ�pos�������������õ�λ������
		//�����ļ��������⣨�Ž���ʱ�?
	    //����ʱ����ȡ��ݣ����ָ����ʽ���ļ�
	         
	        //Resource resource = new FileSystemResource(TransferUtils.resolveFilePath(eupsThdFtpInf.getLocDir().trim(), eupsThdFtpInf.getLocFleNme().trim()));
	        Resource resource = new FileSystemResource(objFil);
	        Map<String, List<Map<String, Object>>> map = new HashMap<String, List<Map<String, Object>>>(); 
	        FileMarshaller fileMarshaller = new FileMarshaller();
	        
	        if("WATR".equals(context.getData("comNo"))){	//��ɽˮ�� WATR   fbpdWaterFmtIn
	        	try {
					map=fileMarshaller.unmarshal("fbpdWaterFmtIn", resource, Map.class);
				} catch (JumpException e) {
					e.printStackTrace();
				}
	        	String fileName = "waterFile";
	        	parseOthrMapList(map, payOthrDetailLst, context, fileName);
	        }
	        if("NELE1".equals(context.getData("comNo"))){	//NELE_in_484999_1 ��� fbpdNele1FmtIn
	        	try {
					map=fileMarshaller.unmarshal("fbpdNele1FmtIn", resource, Map.class);
				} catch (JumpException e) {
					e.printStackTrace();
				}
	        	String fileName = "nele1File";
	        	parseOthrMapList(map, payOthrDetailLst, context, fileName);
	        }
	        if("NELE".equals(context.getData("comNo"))){	//NELE_in_484999 ���	fbpdNeleFmtIn
	        	try {
					map=fileMarshaller.unmarshal("fbpdNeleFmtIn", resource, Map.class);
				} catch (JumpException e) {
					e.printStackTrace();
				}
	        	String fileName = "neleFile";
	        	parseNeleMapList(map, payNeleDetailLst, context, fileName);
	        }
	        if("GGAS".equals(context.getData("comNo"))){	//GGAS_in_484999 ��ɽú���	fbpdGgasFmtIn
	        	try {
					map=fileMarshaller.unmarshal("fbpdGgasFmtIn", resource, Map.class);
				} catch (JumpException e) {
					e.printStackTrace();
				}
	        	String fileName = "ggasFile";
	        	parseOthrMapList(map, payOthrDetailLst, context, fileName);
	        }
	        if("VANK".equals(context.getData("comNo"))){	//VANK_out_484999��ɽ��ҵ����� fbpdVankFmtIn
	        	try {
					map=fileMarshaller.unmarshal("fbpdVankFmtIn", resource, Map.class);
				} catch (JumpException e) {
					e.printStackTrace();
				}
	        	String fileName = "vankFile";
	        	parseOthrMapList(map, payOthrDetailLst, context, fileName);
	        }
	        if("TTOM".equals(context.getData("comNo"))){	//TTOM_in_484999��ɽ��ͨ fbpdTtomFmtIn
	        	try {
					map=fileMarshaller.unmarshal("fbpdTtomFmtIn", resource, Map.class);
				} catch (JumpException e) {
					e.printStackTrace();
				}
	        	String fileName = "ttomFile";
	        	parseOthrMapList(map, payOthrDetailLst, context, fileName);
	        }
	        if("CTTV".equals(context.getData("comNo"))){	//CTTV_in_484999 ��ɽ���ߵ��� fbpdCttvFmtIn
	        	try {
					map=fileMarshaller.unmarshal("fbpdCttvFmtIn", resource, Map.class);
				} catch (JumpException e) {
					e.printStackTrace();
				}
	        	String fileName = "cttvFile";
	        	parseOthrMapList(map, payOthrDetailLst, context, fileName);
	        }
	        if("NQTV".equals(context.getData("comNo"))){	//NQTV_in_484999�������ߵ��� fbpdNqtvFmtIn
	        	try {
					map=fileMarshaller.unmarshal("fbpdNqtvFmtIn", resource, Map.class);
				} catch (JumpException e) {
					e.printStackTrace();
				}
	        	String fileName = "nqtvFile";
	        	parseOthrMapList(map, payOthrDetailLst, context, fileName);
	        }
	        if("XIND".equals(context.getData("comNo"))){	//XIND_in_484999 �¶���ҵ���� fbpdXindFmtIn
	        	try {
					map=fileMarshaller.unmarshal("fbpdXindFmtIn", resource, Map.class);
				} catch (JumpException e) {
					e.printStackTrace();
				}
	        	String fileName = "xindFile";
	        	parseOthrMapList(map, payOthrDetailLst, context, fileName);
	        }
	        if("MPOS".equals(context.getData("comNo"))){	//MPOS_in_484999 ��ɽ�ƶ�POS fbpdMposFmtIn
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
	 * ���������ļ�����⣬������������ļ������set��context--���NELE
	 * @param map
	 * @param payNeleDetailLst
	 * @param context
	 * @param fileName 
	 * @return
	 */
	private List<Map<String, Object>> parseNeleMapList( Map<String, List<Map<String, Object>>> map,	List<GdFbpdNeleBatchTmp> payNeleDetailLst, Context context, String fileName) {
		Map<String, Object> orgHeadMap = (Map<String, Object>) map.get("header");

		// ѭ��ƴװ���ո��ļ���ϸmap
		List<Map<String, Object>> agtDeatil = new ArrayList<Map<String, Object>>(); // ���ո���ϸ��
		
		List<Map<String, Object>> parseMap = (List<Map<String, Object>>) map.get("detail"); // �ļ���
		
		List<GdFbpdNeleBatchTmp> bthTmpSession = new ArrayList<GdFbpdNeleBatchTmp>();
		
		for (Map<String, Object> orgMap : parseMap) {
			
			GdFbpdNeleBatchTmp eupsFbpdNeleBatchTmp = BeanUtils.toObject(orgMap, GdFbpdNeleBatchTmp.class);
			eupsFbpdNeleBatchTmp.setSqn(bbipPublicService.getBBIPSequence());
			
			
			
			
			
		}
		
		// ƴװ���ո�Map
				Map<String, Object> agtMap = new HashMap<String, Object>();
				Map<String, Object> agtHeaderMap = new HashMap<String, Object>(); // ͷ

				String totCnt = (String) orgHeadMap.get("TotCnt"); // �ܱ���
				String totAmt = (String) orgHeadMap.get("TotAmt"); // �ܽ��
				BigDecimal totAmtD = NumberUtils.centToYuan(totAmt); // �ܽ��

				// ƴװ���ո�ͷ�ļ�
				agtHeaderMap.put("totCount", totCnt); // �ܱ���
				agtHeaderMap.put("totAmt", totAmtD.toString()); // �ܽ��
//				agtHeaderMap.put("comNo", comNo); // ��λ���

				// ƴװ������ո��ļ�
				agtMap.put("header", agtHeaderMap);
				agtMap.put("detail", agtDeatil);

				context.setVariable(GDParamKeys.BATCH_COM_FILE_NAME, "neleFile");
				context.setVariable("agtFileMap", agtMap);
		return null;
		
	}

	
	/**
	 * ���������ļ�����⣬������������ļ������set��context--��ɽ�ƶ�POS
	 * @param map
	 * @param payMposDetailLst
	 * @param context
	 * @return
	 */
	private List<Map<String, Object>> parseMposMapList( Map<String, List<Map<String, Object>>> map,
			List<GdFbpdMposBatchTmp> payMposDetailLst, Context context, String fileName, String formatName) {
		  // Map<String,Object> orgHeadMap=(Map<String, Object>) map.get("header");
        List<Map<String, Object>> parseMap = (List<Map<String, Object>>) map.get("detail");  //�ļ���
        // List<Map<String, Object>> parseMap = operateFile.pareseFile(eupsThdFtpInf, "eleGzBatFmt"); // ����ֻ��detail�ļ�
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
        	batchTmp.setPosFld1(formatName);	//�����ֶ�1	����ļ���ʽ��
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
	 * ���������ļ�����⣬������������ļ������set��context--����
	 * @param map
	 * @param payOthrDetailLst
	 * @param context
	 * @return
	 */
	private List<Map<String, Object>> parseOthrMapList( Map<String, List<Map<String, Object>>> map, List<GdFbpdObusBatchTmp> payOthrDetailLst, Context context, String fileName){
		  // Map<String,Object> orgHeadMap=(Map<String, Object>) map.get("header");
        List<Map<String, Object>> parseMap = (List<Map<String, Object>>) map.get("detail");  //�ļ���
        // List<Map<String, Object>> parseMap = operateFile.pareseFile(eupsThdFtpInf, "eleGzBatFmt"); // ����ֻ��detail�ļ�
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