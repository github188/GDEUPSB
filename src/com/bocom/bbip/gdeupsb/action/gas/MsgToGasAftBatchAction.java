package com.bocom.bbip.gdeupsb.action.gas;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mvel2.optimizers.impl.refl.nodes.ArrayLength;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.action.common.OperateFTPAction;
import com.bocom.bbip.eups.action.common.OperateFileAction;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsBatchConsoleInfo;
import com.bocom.bbip.eups.entity.EupsThdFtpConfig;
import com.bocom.bbip.eups.entity.EupsTransJournal;
import com.bocom.bbip.eups.repository.EupsBatchConsoleInfoRepository;
import com.bocom.bbip.eups.repository.EupsThdFtpConfigRepository;
import com.bocom.bbip.eups.repository.EupsTransJournalRepository;
import com.bocom.bbip.eups.spi.service.batch.AfterBatchAcpService;
import com.bocom.bbip.eups.spi.vo.AfterBatchAcpDomain;
import com.bocom.bbip.gdeupsb.entity.GdGashBatchTmp;
import com.bocom.bbip.gdeupsb.repository.GdGashBatchTmpRepository;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

/**
 * 惠州燃气
 * 文件批量托收完成通知燃气
 * @author WangMQ
 *
 */
public class MsgToGasAftBatchAction implements AfterBatchAcpService {
	@Autowired
	EupsTransJournalRepository eupsTransJournalRepository;
	
	@Autowired
	OperateFTPAction operateFTPAction;
	
	@Autowired
	OperateFileAction operateFileAction;

	@Autowired
	EupsBatchConsoleInfoRepository eupsBatchConsoleInfoRepository;
	
	@Autowired
	EupsThdFtpConfigRepository eupsThdFtpConfigRepository;
	
	@Autowired
	GdGashBatchTmpRepository gdGashBatchTmpRepository;
	
	private Logger logger = LoggerFactory.getLogger(MsgToGasAftBatchAction.class);
	
	@Override
	public void afterBatchDeal(AfterBatchAcpDomain afterbatchacpdomain,	Context context) throws CoreException {
		logger.info("Enter in MsgToGasAftBatchAction!");
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
		
		/*
		 * plan:
		 * step1: 查询批次表中的批次信息  SELECT BrNo,SndTlr,ChkTlr,NodNo,DskNam From pubbatinf WHERE DskNo='%s' !!!!!!!!!1Done!
		 * step2：从流水表中查询该批次流水信息|批次号   select * from 流水表 where batNo=#{batNo} !!!!!!!!!!!!!!!1Done!!
		 * step3：根据批次号流水信息拼装文件map !!!!!!!Done!
		 * step4: 从FTP配置信息表中获取(或配置)批次文件的信息，包含本地、远程文件名，FTP路径等    !!!!!!!!!!!!Done!!
		 * step5: 生成文件    Done!!
		 * step6：上传文至燃气FTP    Done!!
		 * step7: 更新流水表
		 * step8: 信息处理 rspMsg rspCod......
		 */
		String comNo = context.getData(ParamKeys.COMPANY_NO);
		//根据批次号查询批次信息
		String batNo = context.getData(ParamKeys.BAT_NO);
		EupsBatchConsoleInfo eupsBatchConsoleInfo = new EupsBatchConsoleInfo();
		eupsBatchConsoleInfo.setBatNo(batNo);
		List<EupsBatchConsoleInfo> batchConsoleInfos = eupsBatchConsoleInfoRepository.find(eupsBatchConsoleInfo);
		if(CollectionUtils.isEmpty(batchConsoleInfos)){		//无批次信息
			throw new CoreRuntimeException("无该批次信息");
		}
		
		String txnDteTmp = DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMdd);
		String locFileName = "fileCNJT"  + txnDteTmp + ".txt";
		
		EupsThdFtpConfig ftpCfg = new EupsThdFtpConfig();
		ftpCfg.setComNo(comNo);
		ftpCfg.setLocFleNme(locFileName);
		List<EupsThdFtpConfig> getFileCfgInfo = eupsThdFtpConfigRepository.find(ftpCfg);
		if(CollectionUtils.isEmpty(getFileCfgInfo)){
			throw new CoreRuntimeException("无FTP配置信息");
		}
		ftpCfg = getFileCfgInfo.get(0);
		// 拼装批量结果文件Map
        Map<String, Object> map = encodeFileMap(context, batNo);
		
        try {
            // 生成批量结果到指定路径
            operateFileAction.createCheckFile(ftpCfg, "msgToGasFileFmt", locFileName, map);
            logger.info("批量结果文件生成成功！");
        } catch (Exception e) {
            logger.error("File create error : " + e.getMessage());
            throw new CoreException(ErrorCodes.EUPS_FILE_CREATE_FAIL);
        } 
        
        ftpCfg.setRmtFleNme(locFileName);
        
        
     // 向指定FTP路径放文件
       operateFTPAction.putCheckFile(ftpCfg);
        logger.info("批量结果文件FTP放置成功！");
		
/*      <Exec func="PUB:ExecSql" error="IGNORE">
        <Arg name="SqlCmd" value="UpEfeStatus"/>    <!--更新PkgFlg为批量扣收已发送--> <!--   UPDATE Gastxnjnl491 set PkgFlg='2' where dskno='%s' and PkgFlg='1' -->
        </Exec>*/
        
	}
	
	
	 /**
     * 拼装文件Map 
     * @param context
     * @return
     */
    private Map<String, Object> encodeFileMap(Context context, String batNo) throws CoreException {
        Map<String, Object> map = new HashMap<String, Object>();
        //从流水表中根据批次号取流水信息
        EupsTransJournal etj=new EupsTransJournal();
        etj.setBatNo(batNo);
        List<EupsTransJournal> chkEupsTransJournalList = eupsTransJournalRepository.find(etj);
        if (null == chkEupsTransJournalList && CollectionUtils.isEmpty(chkEupsTransJournalList)) {
            logger.info("There are no records for select check trans journal ");
            throw new CoreException(ErrorCodes.EUPS_QUERY_NO_DATA);
        }
        
        List<EupsTransJournal> returnList = new ArrayList<EupsTransJournal>();
        GdGashBatchTmp batchTmp = new GdGashBatchTmp();
        batchTmp.setBatNo(batNo);
        List<GdGashBatchTmp> getPayMonTmps = gdGashBatchTmpRepository.find(batchTmp); 
        String payMon =  getPayMonTmps.get(0).getPayMon();
        String rsvFld5 = null;
        for(EupsTransJournal jnl : chkEupsTransJournalList){
        	if("S".equals(jnl.getTxnSts())){
        		jnl.setBakFld2("B0");
        	}
        	if("F".equals(jnl.getTxnSts())){
        		jnl.setBakFld2("B3");
        	}
        	jnl.setBk("cnjt");
        	jnl.setRsvFld6(payMon);
        	
        	rsvFld5 = DateUtils.format((Date)jnl.getTxnDte(), DateUtils.STYLE_SIMPLE_DATE);
        	jnl.setRsvFld5(rsvFld5);
        	
        	returnList.add(jnl);
        }
        map.put(ParamKeys.EUPS_FILE_DETAIL, BeanUtils.toMaps(returnList));
        return map;
    }


}
