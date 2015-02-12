package com.bocom.bbip.gdeupsb.action.gas;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class MsgToGasAftBatchAction extends BaseAction{
	private Logger logger = LoggerFactory.getLogger(MsgToGasAftBatchAction.class);
	
	public void execute(Context context) throws CoreException, CoreRuntimeException{
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
		String comNo = context.getData("comNo");
		//根据批次号查询批次信息
		String batNo = context.getData("batNo");
		EupsBatchConsoleInfo eupsBatchConsoleInfo = new EupsBatchConsoleInfo();
		eupsBatchConsoleInfo.setBatNo(batNo);
		List<EupsBatchConsoleInfo> batchConsoleInfos = get(EupsBatchConsoleInfoRepository.class).find(eupsBatchConsoleInfo);
		if(CollectionUtils.isEmpty(batchConsoleInfos)){		//无批次信息
			throw new CoreRuntimeException("无该批次信息");
		}
		
		String txnDteTmp = DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMdd);
		String bk = context.getData("bk");
		String locFileName = "file" + bk + txnDteTmp + ".txt";
		
		EupsThdFtpConfig ftpCfg = new EupsThdFtpConfig();
		ftpCfg.setComNo(comNo);
		ftpCfg.setLocFleNme(locFileName);
		List<EupsThdFtpConfig> getFileCfgInfo = get(EupsThdFtpConfigRepository.class).find(ftpCfg);
		if(CollectionUtils.isEmpty(getFileCfgInfo)){
			throw new CoreRuntimeException("无FTP配置信息");
		}
		ftpCfg = getFileCfgInfo.get(0);
		// 拼装批量结果文件Map
        Map<String, Object> map = encodeFileMap(context, batNo);
		
        try {
            // 生成批量结果到指定路径
            get(OperateFileAction.class).createCheckFile(ftpCfg, "msgToGasFileFmt", locFileName, map);
            log.info("批量结果文件生成成功！");
        } catch (Exception e) {
            log.error("File create error : " + e.getMessage());
            throw new CoreException(ErrorCodes.EUPS_FILE_CREATE_FAIL);
        } 
        
        ftpCfg.setRmtFleNme(locFileName);
        
        
     // 向指定FTP路径放文件
        get(OperateFTPAction.class).putCheckFile(ftpCfg);
        log.info("批量结果文件FTP放置成功！");
		
/*      <Exec func="PUB:ExecSql" error="IGNORE">
        <Arg name="SqlCmd" value="UpEfeStatus"/>    <!--更新PkgFlg为批量扣收已发送--> <!--   UPDATE Gastxnjnl491 set PkgFlg='2' where dskno='%s' and PkgFlg='1' -->
        </Exec>*/
        
        
        
        
        
	}
	
	
    /**
     * 拼装文件Map 
     * 
     * @param context
     * @return
     */
    private Map<String, Object> encodeFileMap(Context context, String batNo) throws CoreException {
        Map<String, Object> map = new HashMap<String, Object>();
        EupsTransJournal etj=new EupsTransJournal();
        etj.setBatNo(batNo);
        
        List<EupsTransJournal> chkEupsTransJournalList = get(EupsTransJournalRepository.class).find(etj);
        if (null == chkEupsTransJournalList && CollectionUtils.isEmpty(chkEupsTransJournalList)) {
            log.info("There are no records for select check trans journal ");
            throw new CoreException(ErrorCodes.EUPS_QUERY_NO_DATA);
        }
        map.put(ParamKeys.EUPS_FILE_DETAIL, BeanUtils.toMaps(chkEupsTransJournalList));
        return map;
    }
}
