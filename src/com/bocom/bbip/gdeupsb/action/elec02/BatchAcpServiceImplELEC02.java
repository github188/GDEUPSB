package com.bocom.bbip.gdeupsb.action.elec02;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import com.bocom.bbip.comp.CommonRequest;
import com.bocom.bbip.comp.account.AccountService;
import com.bocom.bbip.comp.account.support.CusActInfResult;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.action.common.OperateFTPAction;
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
import com.bocom.bbip.gdeupsb.entity.GDEupsBatchConsoleInfo;
import com.bocom.bbip.gdeupsb.repository.GDEupsBatchConsoleInfoRepository;
import com.bocom.bbip.utils.Assert;
import com.bocom.bbip.utils.ContextUtils;
import com.bocom.jump.bp.JumpException;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
/**
 * @author wuyh
 * @date 2015-3-03 上午09:06:35
 * @Company TD
 */
public class BatchAcpServiceImplELEC02 extends BaseAction implements BatchAcpService {
	private static final Log logger = LogFactory.getLog(BatchAcpServiceImplELEC02.class);

	@Override
	public void prepareBatchDeal(PrepareBatchAcpDomain arg0, Context context)
			throws CoreException {
		logger.info("批次前准备数据");
		/**加锁*/
		((BatchFileCommon)get(GDConstants.BATCH_FILE_COMMON_UTILS)).
		Lock((String)context.getData(ParamKeys.COMPANY_NO));
		((BatchFileCommon)get(GDConstants.BATCH_FILE_COMMON_UTILS)).BeforeBatchProcess(context);
		EupsThdFtpConfig config=get(EupsThdFtpConfigRepository.class).findOne((String)context.getData(ParamKeys.FTP_ID));
		Assert.isFalse(null == config, ErrorCodes.EUPS_THD_FTP_CONFIG_NOTEXIST);
		((OperateFTPAction)get("opeFTP")).getFileFromFtp(config);
		Map result = pareseFile(config, "BatchFmtELEC02");
		Assert.isFalse(null==result||0==result.size(), ErrorCodes.EUPS_FILE_PARESE_FAIL);
		Map header = (Map) result.get("header");
		List<Map> detail = (List<Map>) result.get("detail");
		GDEupsBatchConsoleInfo info=ContextUtils.getDataAsObject(context, GDEupsBatchConsoleInfo.class);
		get(GDEupsBatchConsoleInfoRepository.class).updateConsoleInfo(info);
		/**保存批次详细信息*/
		saveBatchDetails(context,detail);
		/**生成代收付格式的批扣文件*/
		generateBatchFile(context);
		
		((BatchFileCommon) get(GDConstants.BATCH_FILE_COMMON_UTILS)).sendBatchFileToACP(context);
		/** 解锁 */
		((BatchFileCommon) get(GDConstants.BATCH_FILE_COMMON_UTILS))
				.unLock((String) context.getData(ParamKeys.COMPANY_NO));
	}
	 private void generateBatchFile(Context context)throws CoreException, CoreRuntimeException{
	    Map batchFile=new HashMap();
	    Map header=new HashMap();
	    List<Map>details=new ArrayList<Map>();
	    EupsActSysPara eupsActSysPara = new EupsActSysPara();
        eupsActSysPara.setActSysTyp("0");
        eupsActSysPara.setComNo((String)context.getData(ParamKeys.COMPANY_NO));
        List resultList = ((EupsActSysParaRepository)get(EupsActSysParaRepository.class)).find(eupsActSysPara);
        Assert.isNotEmpty(resultList, ErrorCodes.EUPS_QUERY_NO_DATA);
        final String comNoAcps =((EupsActSysPara)resultList.get(0)).getSplNo();
        /**产生代收付文件头*/
        header.put("comNo",comNoAcps);
        header.put(ParamKeys.TOT_CNT, null);
        header.put(ParamKeys.TOT_AMT, null);
	    batchFile.put("header", header);
	    /**产生代收付文件体*/
	    List <Map>list=null;
	    for(Map temp:list){
	    	CommonRequest request=CommonRequest.build(context);
	    	CusActInfResult result=((AccountService)get("cardBINService")).
	    	getAcInf(request, (String)temp.get("cusAc"));
	    	
	    	temp.put(ParamKeys.OBK_BR,result.getOpnBr());
	    }
	    batchFile.put("detail", details);
	    context.setVariable("agtFileMap", batchFile);
	 }
	 private void saveBatchDetails(Context context,List<Map> detail)throws CoreException, CoreRuntimeException{
		 
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
