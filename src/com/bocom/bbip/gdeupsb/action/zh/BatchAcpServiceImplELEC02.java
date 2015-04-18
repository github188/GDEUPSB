package com.bocom.bbip.gdeupsb.action.zh;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsBatchConsoleInfo;
import com.bocom.bbip.eups.entity.EupsThdFtpConfig;
import com.bocom.bbip.eups.repository.EupsBatchConsoleInfoRepository;
import com.bocom.bbip.eups.repository.EupsThdFtpConfigRepository;
import com.bocom.bbip.eups.spi.service.batch.BatchAcpService;
import com.bocom.bbip.eups.spi.vo.PrepareBatchAcpDomain;
import com.bocom.bbip.file.Marshaller;
import com.bocom.bbip.file.transfer.TransferUtils;
import com.bocom.bbip.gdeupsb.action.common.BatchFileCommon;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.entity.GDEupsBatchConsoleInfo;
import com.bocom.bbip.gdeupsb.entity.GDEupsbElecstBatchTmp;
import com.bocom.bbip.gdeupsb.repository.GDEupsBatchConsoleInfoRepository;
import com.bocom.bbip.gdeupsb.repository.GDEupsbElecstBatchTmpRepository;
import com.bocom.bbip.utils.Assert;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.ContextUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.JumpException;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
import com.bocom.jump.bp.support.CollectionUtils;
import com.bocom.bbip.eups.action.common.OperateFTPAction;
public class BatchAcpServiceImplELEC02 extends BaseAction implements BatchAcpService {
	
	@Autowired
	BBIPPublicService bbipPublicService;
	
	private static final Log logger=LogFactory.getLog(BatchAcpServiceImplELEC02.class);
	@Override
	public void prepareBatchDeal(PrepareBatchAcpDomain domain, Context context)throws CoreException {
		final String comNo=ContextUtils.assertDataHasLengthAndGetNNR(context, ParamKeys.COMPANY_NO, ErrorCodes.EUPS_FIELD_EMPTY);
        /**上锁*/
		((BatchFileCommon)get(GDConstants.BATCH_FILE_COMMON_UTILS)).Lock(comNo);
		/**批量前检查和初始化批量控制表 生成批次号batNo*/
		((BatchFileCommon)get(GDConstants.BATCH_FILE_COMMON_UTILS)).BeforeBatchProcess(context);
		logger.info("开始解析批量文件-------------");
		/*try {
			FileUtils.copyFile(null, null);
		} catch (IOException e) {
			throw new CoreException("", "");
		}*/
		EupsThdFtpConfig config = get(EupsThdFtpConfigRepository.class).findOne(ParamKeys.FTPID_BATCH_PAY_FILE_TO_ACP);
		Assert.isFalse(null==config, ErrorCodes.EUPS_FTP_INFO_NOTEXIST,"代收付FTP配置信息不存在");
		config.setRmtWay("/home/bbipadm/home");
		config.setRmtFleNme("batch.txt");
	
		((OperateFTPAction)get("opeFTP")).getFileFromFtp(config);
		Map<String,Object> result=pareseFileByPath("D:\\batch.txt", "", "ELEC02Batch");
		Assert.isFalse(null==result||0==result.size(), ErrorCodes.EUPS_FILE_PARESE_FAIL);
		Map head=(Map)result.get("header");
		List lst=(List)result.get("detail");
		context.getDataMapDirectly().putAll(head);
		GDEupsBatchConsoleInfo info=ContextUtils.getDataAsObject(context, GDEupsBatchConsoleInfo.class);
		get(GDEupsBatchConsoleInfoRepository.class).updateConsoleInfo(info);
		List <GDEupsbElecstBatchTmp>list=(List<GDEupsbElecstBatchTmp>) BeanUtils.toObjects(lst, GDEupsbElecstBatchTmp.class);
        for(GDEupsbElecstBatchTmp tmp:list){
        	tmp.setBatNo((String)context.getData(ParamKeys.BAT_NO));
        }
		/**插入临时表中*/
//		((SqlMap)get("sqlMap")).insert("com.bocom.bbip.gdeupsb.entity.GDEupsbElecstBatchTmp.batchInsert", list);;
		
        for (GDEupsbElecstBatchTmp tmp:list){
        	 get(GDEupsbElecstBatchTmpRepository.class).insert(tmp);
        }
        
		List <GDEupsbElecstBatchTmp>lt=get(GDEupsbElecstBatchTmpRepository.class).findByBatNo((String)context.getData(ParamKeys.BAT_NO));
		
		List<Map<String,Object>> detail=(List<Map<String, Object>>) BeanUtils.toMaps(lt);
		Map<String, Object> header = CollectionUtils.createMap();
		context.setData(ParamKeys.COMPANY_NO, (String)context.getData("comNoAcps"));
		Map<String, Object> temp = CollectionUtils.createMap();
		temp.put(ParamKeys.EUPS_FILE_HEADER, context.getDataMapDirectly());
		temp.put(ParamKeys.EUPS_FILE_DETAIL, detail);
		context.setVariable("agtFileMap", temp);
		GDEupsBatchConsoleInfo console=new GDEupsBatchConsoleInfo();
		console.setBatNo((String)context.getData(ParamKeys.BAT_NO));
		/**更新批次状态为待提交*/
		console.setBatSts(GDConstants.BATCH_STATUS_WAIT);
		get(GDEupsBatchConsoleInfoRepository.class).updateConsoleInfo(console);
		((BatchFileCommon)get(GDConstants.BATCH_FILE_COMMON_UTILS)).sendBatchFileToACP(context);
		
		((BatchFileCommon)get(GDConstants.BATCH_FILE_COMMON_UTILS)).unLock(comNo);
		
		logger.info("批量文件数据准备结束-------------");

		// 同步提交
		
		Date date=new Date();
		context.setData("reqTme",DateUtils.formatAsSimpleDate(date)+"T"+DateUtils.format(date, "HH:mm:ss"));
		
	
		//判空todo
		String totCnt = context.getData("totCnt").toString();
		String totAmt =  context.getData("totAmt").toString();
		context.setData("totCnt", Integer.valueOf(totCnt));
		context.setData("totAmt", new BigDecimal(totAmt));
		
		//提交
		String mothed="eups.batchPaySubmitDataProcess";
		bbipPublicService.synExecute(mothed, context);
		
		
		
		
	}
	  private  Map<String, Object> pareseFileByPath(String filePath, String fileName, String fileId)
	    throws CoreException, CoreRuntimeException
	  {
		  Resource resource;
		  Map map=new HashMap();
	    logger.info("this is path:" + TransferUtils.resolveFilePath(filePath, fileName));
	    try {
	      resource = new FileSystemResource(TransferUtils.resolveFilePath(filePath, fileName));
	       map = (Map)((Marshaller)get(Marshaller.class)).unmarshal(fileId, resource, Map.class);
	    } catch (JumpException e) {
	      logger.error("BBIP0004EU0015");
	      throw new CoreException("BBIP0004EU0015");
	    }
	    return map;
	  }
	  
	  /**
		 *  同步调用process  批量代扣数据提交
		 */
			public void userProcessToSubmit(Context context)throws CoreException{
				logger.info("==========Start  BatchDataFileAction  userProcessToSubmit");

				Date date=new Date();
				context.setData("reqTme",DateUtils.formatAsSimpleDate(date)+"T"+DateUtils.format(date, "HH:mm:ss"));
				//提交
				String mothed="eups.batchPaySubmitDataProcess";
//				context.setData(ParamKeys.RSV_FLD3, context.getData(ParamKeys.THD_SQN));
//				context.setData(ParamKeys.RSV_FLD2, context.getData(ParamKeys.SEQUENCE));
				bbipPublicService.synExecute(mothed, context);
				String	rsvFld3=context.getData(ParamKeys.THD_SQN).toString();
				EupsBatchConsoleInfo eupsBatchConsoleInfo =new EupsBatchConsoleInfo();
				eupsBatchConsoleInfo.setRsvFld3(rsvFld3);
				String batNo=get(EupsBatchConsoleInfoRepository.class).find(eupsBatchConsoleInfo).get(0).getBatNo();
				context.setData(ParamKeys.BAT_NO, batNo);
				context.setData("PKGCNT", "000000");
				logger.info("==========End  BatchDataFileAction  userProcessToSubmit");

			}
			
	
		
			
			public static void main(String args[]){
				
				String a = "2";
				Object b = "2";
				Object c = 2;
				
//				Integer aa = (Integer) a;
				Integer d = (Integer) b;
				

				
			}
}
