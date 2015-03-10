package com.bocom.bbip.gdeupsb.strategy.hscard;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.bocom.bbip.eups.action.common.OperateFTPAction;
import com.bocom.bbip.eups.action.common.OperateFileAction;
import com.bocom.bbip.eups.adaptor.ThirdPartyAdaptor;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsThdFtpConfig;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;

import com.bocom.bbip.gdeupsb.entity.GdEupsTransJournal;
import com.bocom.bbip.gdeupsb.repository.GdEupsTransJournalRepository;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
import com.bocom.jump.bp.core.Executable;

public class CheckBkFileToThirdStrategyAction implements Executable{
	
	@Autowired
	GdEupsTransJournalRepository gdEupsTransJournalRepository;
	
	@Autowired
	OperateFTPAction operateFTPAction;
	
	@Autowired
	ThirdPartyAdaptor callThdTradeManager;
	
	@Autowired
	OperateFileAction operateFileAction;
	public final static Log logger = LogFactory.getLog(CheckBkFileToThirdStrategyAction.class);
	public void execute(Context context) throws CoreException,CoreRuntimeException{
		logger.info("CheckBkFileToThirdStrategyAction start.......");
		
		GdEupsTransJournal eups = new GdEupsTransJournal();
		eups.setThdTxnDte((Date)context.getData(GDParamKeys.TTXN_DT));
		eups.setTxnSts(GDConstants.GAS_TXN_STS_S);
		eups.setMfmTxnSts(GDConstants.GAS_TXN_STS_S);
		List<Map<String, Object>> list= gdEupsTransJournalRepository.findSumTxnAmt(eups);
		Map<String, Object> totMap = list.get(0);
		context.setData("totNum", totMap.get("totNum"));
		context.setData("totAmt", totMap.get("totAmt"));
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"+totMap.get("totNum")+totMap.get("totAmt"));
		
		String schId = context.getData(ParamKeys.BAK_FLD1);
		String tTxnDt = context.getData(ParamKeys.THD_RCN_DATE);//此处的对账日期字段需确认。
		String txt = ".txt";
		context.setData(ParamKeys.LOCAL_FILE_NAME, schId + tTxnDt + txt);//文件名字段需确认。
//		TODO:String datFil;//此字段不确定
//		String path = "dat/cpla/";//不确定
//		datFil = path + context.getData(ParamKeys.LOCAL_FILE_NAME).toString();
		
		//拼装对账文件map
		Map<String, Object> map= encodeFileMap(context);
		
		// 获取FTP信息,发送文件到指定路径
		EupsThdFtpConfig eupsThdFtpConfig = context.getData(ParamKeys.CONSOLE_THD_FTP_CONFIG_LIST);

		
		try{
		//生成对账文件到指定路径
		operateFileAction.createCheckFile(eupsThdFtpConfig,"HSCardFile","HSCardFile",map);
		logger.info("对账文件已生成");
		}catch(Exception e){
			 logger.error("File create error : " + e.getMessage());
	            throw new CoreException(ErrorCodes.EUPS_FILE_CREATE_FAIL,e);
		}
		
		//向指定FTP路径放文件
		
		operateFTPAction.putCheckFile(eupsThdFtpConfig);
		 logger.info("对账文件FTP放置成功！");
	}
	
	/**
     * 拼装对账文件Map
     * 
     * @param context
     * @return
     */
	private Map<String,Object> encodeFileMap(Context context) throws CoreException{
		Map<String,Object> map = new HashMap<String, Object>();
		
		GdEupsTransJournal gdeups = new GdEupsTransJournal();
		//TODO:时间需要改成当前日期前一天
		gdeups.setThdTxnDte(new Date());
		gdeups.setTxnSts("S");
		gdeups.setMfmTxnSts("S");
		
		List<GdEupsTransJournal> chkGDEupsTransJournal = gdEupsTransJournalRepository.findCheck(gdeups);

		if( CollectionUtils.isEmpty(chkGDEupsTransJournal)){
			 logger.info("There are no records for select check trans journal ");
	            throw new CoreException(ErrorCodes.EUPS_QUERY_NO_DATA);
		}
		map.put(ParamKeys.EUPS_FILE_DETAIL, BeanUtils.toMaps(chkGDEupsTransJournal));
		return map;
	}
}
