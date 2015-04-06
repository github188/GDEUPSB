package com.bocom.bbip.gdeupsb.action.zh;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.action.common.OperateFTPAction;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsThdFtpConfig;
import com.bocom.bbip.eups.repository.EupsThdFtpConfigRepository;
import com.bocom.bbip.eups.spi.service.batch.BatchAcpService;
import com.bocom.bbip.eups.spi.vo.PrepareBatchAcpDomain;
import com.bocom.bbip.file.Marshaller;
import com.bocom.bbip.file.transfer.TransferUtils;
import com.bocom.bbip.gdeupsb.action.common.BatchFileCommon;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.entity.GDEupsBatchConsoleInfo;
import com.bocom.bbip.gdeupsb.entity.GDEupsZhAGBatchTemp;
import com.bocom.bbip.gdeupsb.repository.GDEupsBatchConsoleInfoRepository;
import com.bocom.bbip.gdeupsb.repository.GDEupsZHAGBatchTempRepository;
import com.bocom.bbip.utils.Assert;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.ContextUtils;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.JumpException;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
import com.bocom.jump.bp.service.sqlmap.SqlMap;
import com.bocom.jump.bp.support.CollectionUtils;
/**
 * @author wuyh
 * 
 * */
public class BatchAcpServiceImplZHAG00 extends BaseAction implements BatchAcpService {
	private static final Log logger=LogFactory.getLog(BatchAcpServiceImplZHAG00.class);
	@Autowired
	GDEupsZHAGBatchTempRepository gdEupsZHAGBatchTempRepository;
	/**
	 * 珠海文本  数据准备
	 */
	@Override
	public void prepareBatchDeal(PrepareBatchAcpDomain domain, Context context)throws CoreException {
		logger.info("===============Start  BatchAcpServiceImplZHAG00  prepareBatchDeal");
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
		EupsThdFtpConfig config = get(EupsThdFtpConfigRepository.class).findOne("zhag00");
		String fleNme=context.getData(ParamKeys.FLE_NME).toString();
		config.setRmtFleNme(fleNme);
		config.setLocFleNme(fleNme);
		config.setFtpDir("1");
		((OperateFTPAction)get("opeFTP")).getFileFromFtp(config);
		String path=config.getLocDir();
		logger.info("===============获取文件成功");
		final String fileFormat=findFormat(comNo);
		Assert.isNotNull(fileFormat, ErrorCodes.EUPS_QUERY_NO_DATA);
		
		Map<String,Object> result=pareseFileByPath(path, fleNme, fileFormat);
		Assert.isFalse(null==result||0==result.size(), ErrorCodes.EUPS_FILE_PARESE_FAIL, "解析文件出错");
		Map head=(Map)result.get("header");
		List lst=(List)result.get("detail");
		if (null!=head) {
			context.getDataMapDirectly().putAll(head);
		}
		GDEupsBatchConsoleInfo info=ContextUtils.getDataAsObject(context, GDEupsBatchConsoleInfo.class);
		get(GDEupsBatchConsoleInfoRepository.class).updateConsoleInfo(info);
		List <GDEupsZhAGBatchTemp>list=(List<GDEupsZhAGBatchTemp>) BeanUtils.toObjects(lst, GDEupsZhAGBatchTemp.class);
        for(GDEupsZhAGBatchTemp tmp:list){
        	tmp.setBatNo((String)context.getData(ParamKeys.BAT_NO));
        }
		/**插入临时表中*/
        logger.info("~~~~~~~Start ~~~~将数据插入临时表");
//        gdEupsZHAGBatchTempRepository.batchInsert(list);
       ((SqlMap)get("sqlMap")).insert("com.bocom.bbip.gdeupsb.entity.GDEupsZhAGBatchTemp.batchInsert", list); 
        logger.info("~~~~~~~End  ~~~~将数据插入临时表");
		List <GDEupsZhAGBatchTemp>lt=get(GDEupsZHAGBatchTempRepository.class).findByBatNo((String)context.getData(ParamKeys.BAT_NO));
		for(GDEupsZhAGBatchTemp temp:lt){
			temp.setCusAc(StringUtils.isBlank(temp.getCusAc())?temp.getThdCusNo():temp.getCusAc());
			temp.setCusNme(StringUtils.isBlank(temp.getCusNme())?temp.getThdCusNme():temp.getCusNme());
			temp.setThdCusNo(StringUtils.isBlank(temp.getCusAc())?temp.getThdCusNo():temp.getCusAc());
			temp.setThdCusNme(StringUtils.isBlank(temp.getCusNme())?temp.getThdCusNme():temp.getCusNme());
		}
		List<Map<String,Object>> detail=(List<Map<String, Object>>) BeanUtils.toMaps(lt);
		Map<String, Object> header = CollectionUtils.createMap();
		logger.info("~~~~~~~~~~~comNoAcps："+context.getData("comNoAcps"));
		//TODO 
		context.setData(ParamKeys.COMPANY_NO, (String)context.getData("comNoAcps"));
		Map<String, Object> temp = CollectionUtils.createMap();
		temp.put(ParamKeys.EUPS_FILE_HEADER, context.getDataMapDirectly());
		temp.put(ParamKeys.EUPS_FILE_DETAIL, detail);
		context.setVariable("agtFileMap", temp);
		
		((BatchFileCommon)get(GDConstants.BATCH_FILE_COMMON_UTILS)).sendBatchFileToACP(context);
		String batNo=(String)context.getData(ParamKeys.BAT_NO);
		Map<String, Object> selectMap=new HashMap<String, Object>();
		selectMap.put("batNo", batNo);
		Map<String, Object> map=gdEupsZHAGBatchTempRepository.findTot(selectMap).get(0);
		context.setData("totCnt", map.get("TOT_COUNT"));
		BigDecimal bigDecimal=new BigDecimal(map.get("ALL_MONEY").toString());
		context.setData("totAmt", bigDecimal);
		logger.info("===============End  BatchAcpServiceImplZHAG00  prepareBatchDeal");
	}
	  private  Map<String, Object> pareseFileByPath(String filePath, String fileName, String fileId)
	    throws CoreException, CoreRuntimeException
	  {
		  logger.info("===============Start  BatchAcpServiceImplZHAG00  pareseFileByPath");
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
	    logger.info("===============End  BatchAcpServiceImplZHAG00  pareseFileByPath");
	    return map;
	  }
	  private String findFormat(final String comNo) {
		  logger.info("===============Start  BatchAcpServiceImplZHAG00  findFormat");
		  InputStream location=null;
		try {
			location = getClass().getClassLoader().
			getResourceAsStream("config/fmt/fileFmt/zh/transIn.properties");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		  Properties prop = new Properties();
		    Map<String,String> propMap = new HashMap<String,String>();
		    try {
		      prop.load(location);
		    } catch (IOException e) {
		      e.printStackTrace();
		    }
		    for (Iterator localIterator = prop.keySet().iterator(); localIterator.hasNext(); ) { Object key = localIterator.next();
		      String keyStr = key.toString();
		      String value = prop.getProperty(keyStr);
		      propMap.put(keyStr, value);
		    }
		    logger.info("===============End  BatchAcpServiceImplZHAG00  findFormat");
		  return propMap.get(comNo);
	  }
}
