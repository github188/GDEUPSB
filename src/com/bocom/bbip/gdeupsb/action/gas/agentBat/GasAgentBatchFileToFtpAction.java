package com.bocom.bbip.gdeupsb.action.gas.agentBat;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.action.common.OperateFileAction;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsThdFtpConfig;
import com.bocom.bbip.eups.repository.EupsThdFtpConfigRepository;
import com.bocom.bbip.gdeupsb.action.common.OperateFTPActionExt;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GdGasCusAll;
import com.bocom.bbip.gdeupsb.entity.GdGasCusDay;
import com.bocom.bbip.gdeupsb.repository.GdGasCusAllRepository;
import com.bocom.bbip.gdeupsb.repository.GdGasCusDayRepository;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

/**
 * 用户协议文件上传（0全部，1新增）
 * 生成文件hdCNJTyyyyMMdd.txt、rxyCNJTyyyyMMdd.txt
 * 将两份文件分别上传至FTPA：182.140.200.41，FTPB: 182.53.13.13！！
 * 放进ftp://BANK/银行标志/agrement/ 目录下
 * @author WangMQ
 *
 */
public class GasAgentBatchFileToFtpAction extends BaseAction{
	
	@Autowired
	BBIPPublicService bbipPublicService;
	
	private static Logger logger = LoggerFactory.getLogger(GasAgentBatchFileToFtpAction.class);
	
	public void execute(Context context) throws CoreException {
		logger.info("Enter in GasAgentBatchFileToFtpAction.......");
		logger.info("=================================context:" + context);
		
		String bk = "01491999999";
		String br = "01491001999";
		context.setData(ParamKeys.BR, br);
		context.setData(ParamKeys.BK, bk);
		String tlr = bbipPublicService.getETeller(bk);
		context.setData(ParamKeys.TELLER, tlr);
		context.setData("extFields", br);
		
		//判断是否自动发起
		String fileDte = context.getData("fileDte");
		logger.info("=================txnDte:" + fileDte);
		String dealTyp = null;
		if(StringUtils.isNotEmpty(fileDte)){		//交易日期非空，手动发起
			dealTyp = GDConstants.COM_BATCH_DEAL_TYP_PE; 
		} else {		// 自动
			dealTyp = GDConstants.COM_BATCH_DEAL_TYP_AU; 
			fileDte = DateUtils.format(getYesterDay(), DateUtils.STYLE_SIMPLE_DATE);
			context.setData("fileDte", fileDte);
		}
		logger.info("=================txnDte:" + fileDte);
		context.setData(GDParamKeys.COM_BATCH_DEAL_TYP, dealTyp);
		
		//文件名     
		String fileNameDte = fileDte.replace("-", "");
		String hdFileName = "hdCNJT" + fileNameDte + ".txt";
		String rxyFileName = "rxyCNJT" + fileNameDte + ".txt";
		logger.info("================file name:[hdFileName:" + hdFileName + "][rxyFileName:" + rxyFileName + "]");
		/*
		 * FTP配置
		 * 1、从FTP配置表中获取信息
		 * 2、修改文件名对应字段
		 */
		
		String gasAgtFilCfgA = "PGAS00AgtA";	//FTP_NO
		String gasAgtFilCfgB = "PGAS00AgtB";
		
		EupsThdFtpConfig ftpConfigA = get(EupsThdFtpConfigRepository.class).findOne(gasAgtFilCfgA); 
		EupsThdFtpConfig ftpConfigB = get(EupsThdFtpConfigRepository.class).findOne(gasAgtFilCfgB);
		// TODO 文件路径（本地，远程）
		
		
		// 文件名
		ftpConfigA.setLocFleNme(hdFileName);
		ftpConfigA.setRmtFleNme(hdFileName);
		
		ftpConfigB.setLocFleNme(hdFileName);
		ftpConfigB.setRmtFleNme(hdFileName);

		//拼装hd文件map   
		//生成全量协议文件 hdCNJTyyyyMMdd.txt  FMT: gasAllAgentBatFmt
		//上传 hdCNJTyyyyMMdd.txt至FTP

		Map<String, Object> hdMap = encodeAgtHdFileMap(fileDte);
		
		try {
	            // 生成文件到指定路径
	            get(OperateFileAction.class).createCheckFile(ftpConfigA, "gasAllAgentBatFmt", hdFileName, hdMap);
	            logger.info("hdCNJTyyyyMMdd.txt文件生成成功！");
//	            get(OperateFileAction.class).createCheckFile(ftpConfigB, "gasAllAgentBatFmt", hdFileName, hdMap);
//	            logger.info("hdCNJTyyyyMMdd.txt文件生成成功！ftpConfigB");
	        } catch (Exception e) {
	        	logger.error("File create error : " , e);
	            throw new CoreException(ErrorCodes.EUPS_FILE_CREATE_FAIL);
	        }
		
		get(OperateFTPActionExt.class).putCheckFile(ftpConfigA);
		get(OperateFTPActionExt.class).putCheckFile(ftpConfigB);
        logger.info("hdCNJTyyyyMMdd.txt文件FTP放置成功！");
		
        ////////////////////////////////////////////////////////////    文件 rxyCNJTyyyyMMdd.txt
        
        ftpConfigA.setLocFleNme(rxyFileName);
        ftpConfigA.setRmtFleNme(rxyFileName);
        
        ftpConfigB.setLocFleNme(rxyFileName);
        ftpConfigB.setRmtFleNme(rxyFileName);
                
		//拼装rxy文件map  
        //生成增量协议文件 rxyCNJTyyyyMMdd.txt  FMT: gasDayAgentBatFmt
        //上传 rxyCNJTyyyyMMdd.txt至FTP
		Map<String, Object> rxyMap = encodeAgtRxyMap(fileDte);
		
		try {
            // 生成文件到指定路径
            get(OperateFileAction.class).createCheckFile(ftpConfigA, "gasDayAgentBatFmt", rxyFileName, rxyMap);
            logger.info("rxyCNJTyyyyMMdd.txt文件生成成功！");
//          get(OperateFileAction.class).createCheckFile(ftpConfigB, "gasDayAgentBatFmt", rxyFileName, rxyMap);
//          logger.info("rxyCNJTyyyyMMdd.txt文件生成成功！ftpConfigB");
        } catch (Exception e) {
        	logger.error("File create error : " , e);
            throw new CoreException(ErrorCodes.EUPS_FILE_CREATE_FAIL);
        }
		
		get(OperateFTPActionExt.class).putCheckFile(ftpConfigA);
		get(OperateFTPActionExt.class).putCheckFile(ftpConfigB);
		logger.info("rxyCNJTyyyyMMdd.txt文件FTP放置成功！");
		
		
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
	}
	
	
	
	/**
	 * 查询燃气用户协议拼装待生成文件map
	 * @param context
	 * @param comNo
	 * @return
	 * @throws CoreException
	 */
	private Map<String, Object> encodeAgtHdFileMap(String optDat) throws CoreException, CoreRuntimeException {
    	Map<String, Object> map = new HashMap<String, Object>();

    	GdGasCusAll cusAllAgt = new GdGasCusAll();
        cusAllAgt.setOptDat(optDat);
    	List<GdGasCusAll> cusAllList = get(GdGasCusAllRepository.class).findDataBeforeOptDat(cusAllAgt);
    	if (CollectionUtils.isEmpty(cusAllList)) {
            logger.info("There are no records for select check trans journal ");
            throw new CoreException(ErrorCodes.EUPS_QUERY_NO_DATA);
        }
    	map.put(ParamKeys.EUPS_FILE_DETAIL, BeanUtils.toMaps(cusAllList));
    	logger.info("查询燃气协议表信息拼装待生成文件map完成>>>>>>>>>" + map.toString());  
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
    	List<GdGasCusDay> cusDayList = get(GdGasCusDayRepository.class).find(cusDayAgt);
    	if (CollectionUtils.isEmpty(cusDayList)) {
            logger.info("There are no records for select check trans journal ");
            throw new CoreException(ErrorCodes.EUPS_QUERY_NO_DATA);
        }
    	map.put(ParamKeys.EUPS_FILE_DETAIL, BeanUtils.toMaps(cusDayList));
    	logger.info("查询每天动态协议表信息拼装待生成文件map完成>>>>>>>>>" + map.toString());
		return map;
    }

	public Date getYesterDay(){
		Calendar canl = Calendar.getInstance();
		Date date = new Date();
		canl.setTime(date);
		canl.add(Calendar.DATE,-1);
		date = canl.getTime();
		return date;
	}
	
}
