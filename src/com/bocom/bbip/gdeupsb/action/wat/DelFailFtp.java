package com.bocom.bbip.gdeupsb.action.wat;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.action.common.OperateFTPAction;
import com.bocom.bbip.eups.action.common.OperateFileAction;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsThdFtpConfig;
import com.bocom.bbip.eups.repository.EupsThdFtpConfigRepository;
import com.bocom.bbip.gdeupsb.action.common.FileFtpUtils;
import com.bocom.bbip.gdeupsb.entity.GdEupsWatAgtInf;
import com.bocom.bbip.gdeupsb.repository.GdEupsWatAgtInfRepository;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class DelFailFtp extends BaseAction{
	
	@Autowired
	BBIPPublicService service;
	
	@Autowired
	GdEupsWatAgtInfRepository gdEupsWatAgtInfRepository;
	
	@Autowired
	EupsThdFtpConfigRepository eupsThdFtpConfigRepository;
	
	@Autowired
	OperateFileAction operateFile;
	
	@Autowired
	OperateFTPAction operateFTPAction;
	
	private final static Log log = LogFactory.getLog(DelFailFtp.class);
	public void execute(Context ctx) throws CoreRuntimeException,CoreException{
		log.info("DelFailFtp start......");
		
		
		
		
		Map<String,Object> inmap=ctx.getData("jopSchedulingData");
        if(null!=inmap){
        	String clrDat = (String)ctx.getData("clrDat");
        	if(clrDat==null|"".equals(clrDat)){
    			Map<String, Object> resultMap = new HashMap<String, Object>();
    			String sqn = service.getBBIPSequence();
    			String date = DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMdd);
    			
    			
    			
    			//文件名
    			String fileName=date;
    			//文件内容 
//    			GDEupsEleTmp gdEupsEleTmps =new GDEupsEleTmp();
//    			gdEupsEleTmps.setRsvFld5(batNo);
//    			List<GDEupsEleTmp> detailList=gdEupsEleTmpRepository.find(gdEupsEleTmps);
//    			resultMap.put(ParamKeys.EUPS_FILE_DETAIL, detailList);
//    			GdEupsWatAgtInf gdEupsWatAgtInf = new GdEupsWatAgtInf();
//    			gdEupsWatAgtInf.setAgtSts("F");
    			Map<String, Object> map = new HashMap<String, Object>();
    			map.put("agtSts", "F");
    			map.put("agrExpDte", new Date());
    			map.put("pedAgrSts", "1");
    			List<GdEupsWatAgtInf> detailList = gdEupsWatAgtInfRepository.findDelFail(map);
    			
    			
    			
    			List<Object> list1 = new ArrayList<Object>();
    			
    			resultMap.put(ParamKeys.EUPS_FILE_DETAIL, detailList);
    			
    			EupsThdFtpConfig eupsThdFtpConfig =eupsThdFtpConfigRepository.findOne("watDelAgtFail");
    			
    			// 生成文件
    			operateFile.createCheckFile(eupsThdFtpConfig, "watAgtDelFail", fileName, resultMap);
    			eupsThdFtpConfig.setLocFleNme(fileName);
    			eupsThdFtpConfig.setRmtFleNme(fileName);
    			log.info("start to put......"+eupsThdFtpConfig);
    			
    			
    			
    			String stwatIpA = eupsThdFtpConfig.getThdIpAdr();
    			String userNameA = eupsThdFtpConfig.getOppNme();
    			String passwordA = eupsThdFtpConfig.getOppUsrPsw();
    			String rmtDirA = eupsThdFtpConfig.getRmtWay();
    			String locDirA = eupsThdFtpConfig.getLocDir();
    			String[] shellArgA = {"GDEUPSBFtpPutFile.sh",stwatIpA,userNameA,passwordA,rmtDirA,fileName,locDirA,"bin",fileName}; 
    			log.info("ftp args="+shellArgA.toString());
    			//ftp放文件
    			try{
    				int result = FileFtpUtils.systemAndWait(shellArgA,true);
    				if(result==0){
    					log.info("put remote file success......");
    				}else{
    					throw new CoreException(ErrorCodes.EUPS_FAIL);
    				}
    			} catch (Exception e){
    				throw new CoreException(ErrorCodes.EUPS_FAIL);
    			}
    		}else{
    			Map<String, Object> resultMap = new HashMap<String, Object>();
    			String sqn = service.getBBIPSequence();
    			String date = clrDat;
    			Date selDat = DateUtils.parse(date);
    			
    			
    			//文件名
    			String fileName=date;
    			//文件内容 
//    			GDEupsEleTmp gdEupsEleTmps =new GDEupsEleTmp();
//    			gdEupsEleTmps.setRsvFld5(batNo);
//    			List<GDEupsEleTmp> detailList=gdEupsEleTmpRepository.find(gdEupsEleTmps);
//    			resultMap.put(ParamKeys.EUPS_FILE_DETAIL, detailList);
//    			GdEupsWatAgtInf gdEupsWatAgtInf = new GdEupsWatAgtInf();
//    			gdEupsWatAgtInf.setAgtSts("F");
    			Map<String, Object> map = new HashMap<String, Object>();
    			map.put("agtSts", "F");
    			map.put("agrExpDte", selDat);
    			map.put("pedAgrSts", "1");
    			List<GdEupsWatAgtInf> detailList = gdEupsWatAgtInfRepository.findDelFail(map);
    			
    			
    			
    			List<Object> list1 = new ArrayList<Object>();
    			
    			resultMap.put(ParamKeys.EUPS_FILE_DETAIL, detailList);
    			
    			EupsThdFtpConfig eupsThdFtpConfig =eupsThdFtpConfigRepository.findOne("watDelAgtFail");
    			
    			// 生成文件
    			operateFile.createCheckFile(eupsThdFtpConfig, "watAgtDelFail", fileName, resultMap);
    			eupsThdFtpConfig.setLocFleNme(fileName);
    			eupsThdFtpConfig.setRmtFleNme(fileName);
    			log.info("start to put......"+eupsThdFtpConfig);
    			
    			
    			
    			String stwatIpA = eupsThdFtpConfig.getThdIpAdr();
    			String userNameA = eupsThdFtpConfig.getOppNme();
    			String passwordA = eupsThdFtpConfig.getOppUsrPsw();
    			String rmtDirA = eupsThdFtpConfig.getRmtWay();
    			String locDirA = eupsThdFtpConfig.getLocDir();
    			String[] shellArgA = {"GDEUPSBFtpPutFile.sh",stwatIpA,userNameA,passwordA,rmtDirA,fileName,locDirA,"bin",fileName}; 
    			log.info("ftp args="+shellArgA.toString());
    			//ftp放文件
    			try{
    				int result = FileFtpUtils.systemAndWait(shellArgA,true);
    				if(result==0){
    					log.info("put remote file success......");
    				}else{
    					throw new CoreException(ErrorCodes.EUPS_FAIL);
    				}
    			} catch (Exception e){
    				throw new CoreException(ErrorCodes.EUPS_FAIL);
    			}
    		}
        }

	
	}

}
