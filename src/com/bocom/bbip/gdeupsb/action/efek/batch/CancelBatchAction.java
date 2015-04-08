package com.bocom.bbip.gdeupsb.action.efek.batch;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.repository.EupsBatchConsoleInfoRepository;
import com.bocom.bbip.gdeupsb.entity.GDEupsBatchConsoleInfo;
import com.bocom.bbip.gdeupsb.repository.GDEupsBatchConsoleInfoRepository;
import com.bocom.bbip.gdeupsb.repository.GDEupsEleTmpRepository;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

public class CancelBatchAction extends BaseAction {
	private static final Log logger = LogFactory.getLog(CancelBatchAction.class);
    @Autowired
    GDEupsBatchConsoleInfoRepository gdEupsBatchConsoleInfoRepository;
    @Autowired
    GDEupsEleTmpRepository gdEupsEleTmpRepository;
    @Autowired
    EupsBatchConsoleInfoRepository eupsBatchConsoleInfoRepository;
    /**
     * 南方电网 批量撤销  删除临时表信息 更改控制表状态
     */
    public void execute(Context context)throws CoreException{
	   logger.info("===========Start  CancelBatchAction");
	   GDEupsBatchConsoleInfo gdEupsBatchConsoleInfos = new GDEupsBatchConsoleInfo();
	   gdEupsBatchConsoleInfos.setFleNme(context.getData(ParamKeys.FLE_NME).toString());
	   List<GDEupsBatchConsoleInfo> gdEupsBatchConsoleInfoList=gdEupsBatchConsoleInfoRepository.find(gdEupsBatchConsoleInfos);
	   GDEupsBatchConsoleInfo gdEupsBatchConsoleInfo=gdEupsBatchConsoleInfoList.get(0);
	   logger.info("~~~~~~GDEupsBatchConsoleInfo~~~~~"+gdEupsBatchConsoleInfo);
	   String totCnt=gdEupsBatchConsoleInfo.getTotCnt()+"";
	   if(!context.getData(ParamKeys.TOT_CNT).toString().trim().equals(totCnt.trim())){
		   		throw new CoreException("获取笔数与交易笔数不相等");
	   }
	   String totAmt=gdEupsBatchConsoleInfo.getTotAmt().scaleByPowerOfTen(2)+"";
	   if(!context.getData(ParamKeys.TOT_AMT).toString().trim().equals(totAmt.trim())){
		   		throw new CoreException("获取金额与交易金额不相等");
	   }
	   //批量 判断状态
//	   String batNo=gdEupsBatchConsoleInfo.getBatNo();
//	   GDEupsBatchConsoleInfo Infos=gdEupsBatchConsoleInfoRepository.findOne(batNo);
//	   String thdSqn=Infos.getRsvFld2();
//	   EupsBatchConsoleInfo eupsBatchConsoleInfo=new EupsBatchConsoleInfo();
//	   eupsBatchConsoleInfo.setRsvFld2(thdSqn);
//	   EupsBatchConsoleInfo eupsBatchConsoleInfoOne=eupsBatchConsoleInfoRepository.find(eupsBatchConsoleInfo).get(0);
//	   String eupsBatSts=eupsBatchConsoleInfoOne.getBatSts();
//	   if(eupsBatSts.equals(GDConstants.BATCH_STATUS_INIT) || eupsBatSts.equals(GDConstants.BATCH_STATUS_WAIT)){
//			   //上锁
//			   Result result = ((BBIPPublicServiceImpl)get(GDConstants.BBIP_PUBLIC_SERVICE)).tryLock(batNo, 60*1000L, 6000L);
//			   Assert.isTrue(result.isSuccess(), GDErrorCodes.EUPS_LOCK_FAIL);
//			   //更改批次控制表状态
//			   eupsBatchConsoleInfoOne.setBatSts("C");
//			   eupsBatchConsoleInfoRepository.update(eupsBatchConsoleInfoOne);
//			   
//			   GDEupsBatchConsoleInfo info=new GDEupsBatchConsoleInfo();
//			   info.setBatNo(batNo);
//			   info.setBatSts("C");
//			   gdEupsBatchConsoleInfoRepository.updateConsoleInfo(info);
//			   context.setData("cancelSign","C");
//			   //解锁
//			   result = ((BBIPPublicServiceImpl)get(GDConstants.BBIP_PUBLIC_SERVICE)).unlock(batNo);
//			   Assert.isTrue(result.isSuccess(), GDErrorCodes.EUPS_UNLOCK_FAIL);
//	   }else{
//		   		throw new CoreException(GDErrorCodes.EUPS_BATCH_STATUS_ERROR);
//	   }
	   
	   context.setData("cancelSign", "2");
	   context.setData("thdTxnDate", DateUtils.format((Date)context.getData(ParamKeys.THD_TXN_DATE), DateUtils.STYLE_yyyyMMdd));
	   context.setData("thdTxnTime", DateUtils.formatAsHHmmss((Date)context.getData(ParamKeys.THD_TXN_TIME)));
	   context.setData(ParamKeys.TXN_DTE, DateUtils.formatAsTranstime((Date)context.getData(ParamKeys.TXN_DTE)));
	   logger.info("===========End  CancelBatchAction");
   }
}
