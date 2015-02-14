package com.bocom.bbip.gdeupsb.action.gas.agentBat;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.action.common.OperateFTPAction;
import com.bocom.bbip.eups.action.common.OperateFileAction;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsThdFtpConfig;
import com.bocom.bbip.eups.repository.EupsThdFtpConfigRepository;
import com.bocom.bbip.eups.spi.service.batch.BatchAcpService;
import com.bocom.bbip.eups.spi.vo.PrepareBatchAcpDomain;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GdGasCusDay;
import com.bocom.bbip.gdeupsb.repository.GdGasCusDayRepository;
import com.bocom.bbip.service.BGSPServiceAccessObject;
import com.bocom.bbip.service.Result;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

/**
 * 用户协议文件上传（0全部，1新增）
 * 生成文件hdCNJTyyyyMMdd.txt、rxyCNJTyyyyMMdd.txt
 * 放进ftp://BANK/银行标志/agrement/ 目录下
 * @author WangMQ
 *
 */
public class GasAgentBatchFileToFtpAction implements BatchAcpService{
	private static Logger logger = LoggerFactory.getLogger(GasAgentBatchFileToFtpAction.class);
	
	@Autowired
	EupsThdFtpConfigRepository eupsThdFtpConfigRepository;
	
	@Autowired
	GdGasCusDayRepository gdGasCusDayRepository;
	
	@Autowired
	BGSPServiceAccessObject bgspServiceAccessObject;
	
	@Autowired
	OperateFTPAction operateFTPAction;
	
	@Autowired
	OperateFileAction operateFileAction;
	
	@Override
	public void prepareBatchDeal(PrepareBatchAcpDomain prepareBatchAcpDomain, Context context)
			throws CoreException {
		logger.info("Enter in GasAgentBatchFileToFtpAction.......");
		
//		String comNo = context.getData(ParamKeys.COMPANY_NO);
//		String bk = context.getData(ParamKeys.BK);
		
		String comNo = "PGAS00";
		String bk = "CNJT";
		//判断是否自动发起
		String txnDte = context.getData("txnDte");
		String dealTyp = null;
		if(StringUtils.isNotEmpty(txnDte)){		//交易日期非空，手动发起
			dealTyp = GDConstants.COM_BATCH_DEAL_TYP_PE; 
		} else {		// 自动
			dealTyp = GDConstants.COM_BATCH_DEAL_TYP_AU; 
			txnDte = DateUtils.format(new Date(), DateUtils.STYLE_SIMPLE_DATE);
		}
		context.setData(GDParamKeys.COM_BATCH_DEAL_TYP, dealTyp);
		
		
		//文件名     
		txnDte = txnDte.replace("-", "");
		String hdFileName = "hd" + bk + txnDte + ".txt";
		String rxyFileName = "rxy" + bk + txnDte + ".txt";
		
		/*
		 * FTP配置
		 * 1、从FTP配置表中获取信息
		 * 2、修改文件名对应字段
		 */
		EupsThdFtpConfig ftpConfig = eupsThdFtpConfigRepository.findOne(comNo);
		// TODO 文件路径（本地，远程）
		
		
		// 文件名
		ftpConfig.setLocFleNme(hdFileName);
		ftpConfig.setRmtFleNme(hdFileName);
		
		
		//拼装hd文件map   
		//生成全量协议文件 hdCNJTyyyyMMdd.txt  FMT: gasAllAgentBatFmt
		//上传 hdCNJTyyyyMMdd.txt至FTP

		Map<String, Object> hdMap = encodeAgtHdFileMap(bk, comNo);
		
		try {
	            // 生成文件到指定路径
	            operateFileAction.createCheckFile(ftpConfig, "gasAllAgentBatFmt", hdFileName, hdMap);
	            logger.info("hdCNJTyyyyMMdd.txt文件生成成功！");
	        } catch (Exception e) {
	        	logger.error("File create error : " + e.getMessage());
	            throw new CoreException(ErrorCodes.EUPS_FILE_CREATE_FAIL);
	        }
		
		operateFTPAction.putCheckFile(ftpConfig);
        logger.info("hdCNJTyyyyMMdd.txt文件FTP放置成功！");
		
        
        ////////////////////////////////////////////////////////////    文件 rxyCNJTyyyyMMdd.txt
        
        
        ftpConfig.setLocFleNme(rxyFileName);
        ftpConfig.setRmtFleNme(rxyFileName);
                
		//拼装rxy文件map  
        //生成增量协议文件 rxyCNJTyyyyMMdd.txt  FMT: gasDayAgentBatFmt
        //上传 rxyCNJTyyyyMMdd.txt至FTP
		Map<String, Object> rxyMap = encodeAgtRxyMap(txnDte);
		
		try {
            // 生成文件到指定路径
            operateFileAction.createCheckFile(ftpConfig, "gasDayAgentBatFmt", rxyFileName, rxyMap);
            logger.info("rxyCNJTyyyyMMdd.txt文件生成成功！");
        } catch (Exception e) {
        	logger.error("File create error : " + e.getMessage());
            throw new CoreException(ErrorCodes.EUPS_FILE_CREATE_FAIL);
        }
		
		operateFTPAction.putCheckFile(ftpConfig);
		logger.info("rxyCNJTyyyyMMdd.txt文件FTP放置成功！");
		
		
		
	}
	
	
	
	/**
	 * 调代收接口查询燃气用户协议拼装待生成文件map
	 * @param context
	 * @param comNo
	 * @return
	 * @throws CoreException
	 */
    @SuppressWarnings("null")
	private Map<String, Object> encodeAgtHdFileMap(String bk, String comNo) throws CoreException, CoreRuntimeException {
    	Map<String, Object> map = new HashMap<String, Object>();
        Map<String, Object> agtMap = new HashMap<String, Object>();
        agtMap.put(ParamKeys.BK, bk);
        agtMap.put(ParamKeys.COMPANY_NO, comNo);
        Result accessObject = bgspServiceAccessObject.callServiceFlatting("queryDetailAgentCollectAgreement",agtMap);
        if (CollectionUtils.isEmpty(accessObject.getPayload())) {
            logger.info("There are no records for select check trans journal ");
            throw new CoreException(ErrorCodes.EUPS_QUERY_NO_DATA);
        }
        map.put(ParamKeys.EUPS_FILE_DETAIL, accessObject.getPayload());
        logger.info("燃气用户协议拼装待生成文件map完成>>>>>>>>>" + map.toString());
        return map;
    }
    
    /**
     * 查询每天动态协议表信息拼装待生成文件map
     * @return
     * @throws CoreException
     * @throws CoreRuntimeException
     */
    private Map<String, Object> encodeAgtRxyMap(String optDat) throws CoreException, CoreRuntimeException{
    	Map<String, Object> map = new HashMap<String, Object>();
    	GdGasCusDay cusDayAgt = new GdGasCusDay();
    	cusDayAgt.setOptDat(optDat);
    	List<GdGasCusDay> rxyCusDayList = gdGasCusDayRepository.find(cusDayAgt);
    	if (CollectionUtils.isEmpty(rxyCusDayList)) {
            logger.info("There are no records for select check trans journal ");
            throw new CoreException(ErrorCodes.EUPS_QUERY_NO_DATA);
        }
    	map.put(ParamKeys.EUPS_FILE_DETAIL, BeanUtils.toMaps(rxyCusDayList));
    	logger.info("查询每天动态协议表信息拼装待生成文件map完成>>>>>>>>>" + map.toString());
		return map;
    }



	    
}
