package com.bocom.bbip.gdeupsb.action.lot;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.adaptor.ThirdPartyAdaptor;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.gdeupsb.entity.GDEupsbLotSysCfgInfo;
import com.bocom.bbip.gdeupsb.repository.GDEupsbLotSysCfgInfoRepository;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;


/**
 * 福彩对账文件下载地址查询
 * @author WangMQ
 *
 */

public class QryFilDownloadAdrAction extends BaseAction{
	private static Logger logger = LoggerFactory.getLogger(QryFilDownloadAdrAction.class);	
	
	public void execute(Context context) throws CoreException, CoreRuntimeException{
		logger.info("Enter in QryFilDownloadAdrAction!.......");
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
		//当交易进行到此处时，context含 gameId， drawid， filTyp
		logger.info("===========context=========="+ context);
		
		//根据运营商ID查平台参数配置   DealId,UsrPam,UsrPas,SigTim,LclTim,LotTim,DiffTm
		GDEupsbLotSysCfgInfo lotSysCfgInfo = BeanUtils.toObject(context.getDataMap(), GDEupsbLotSysCfgInfo.class);
		List<GDEupsbLotSysCfgInfo> cfgList= get(GDEupsbLotSysCfgInfoRepository.class).find(lotSysCfgInfo);
		context.setData("dealId", cfgList.get(0).getDealId());
		context.setData("usrPam", cfgList.get(0).getUsrPam());
		context.setData("usrPas", cfgList.get(0).getUsrPas());
		context.setData("sigTim", cfgList.get(0).getSigTim());
		context.setData("lclTim", cfgList.get(0).getLclTim());
		context.setData("lotTim", cfgList.get(0).getLotTim());
		context.setData("diffTm", cfgList.get(0).getDiffTm());
		
		context.setData("hTxnCd", "237");
		context.setData("objSvr", "STHDLOTA");
		
		Map<String, Object> thdRspMsg = get(ThirdPartyAdaptor.class).trade(context);
		
			
		if(CollectionUtils.isEmpty(thdRspMsg)){
			context.setData("downloadStatus", "-1");
			context.setData("downloadMsg", "查询文件下载地址失败[" + context.getData("rRspCod") + "]");
			throw new CoreRuntimeException("查询文件下载地址失败[" + context.getData("rRspCod") + "]");
		}
		
		context.setDataMap(thdRspMsg);
		logger.info("=================完成文件下载地址查询============");
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
	}
}
