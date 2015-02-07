package com.bocom.bbip.gdeupsb.action.lot;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.adaptor.ThirdPartyAdaptor;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

/**
 * 下载不同的文件并入库
 * 实际上是获取彩票公司返回的数据并等待文件入库（将数据更新至xx控制表、XX明细表）
 * @author WangMQ
 *
 */
public class DownloadLotFilesAction extends BaseAction{
	
	private static Logger logger = LoggerFactory.getLogger(DownloadLotFilesAction.class);
	
	public void execute(Context context) throws CoreException, CoreRuntimeException{
		logger.info("Enter in DownloadLotFilesAction!.....");
			context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
		//发送文件下载报文，下载文件 filTyp filNme
		
		context.setData("hTxnCd", "238_"+context.getData("filTyp"));
		context.setData("objSvr", "STHDLOTA");
		
		logger.info("=====================开始与第三主机通讯=======================");
		// 上第三方主机 
		Map<String, Object> thdRspMsgMap = get(ThirdPartyAdaptor.class).trade(context);
		
		if(CollectionUtils.isEmpty(thdRspMsgMap)){
			context.setData("downloadStatus", "-1");
			context.setData("downloadMsg", "下载文件失败[" + context.getData("rRspCod") + "]");
			context.setData("msgTyp", "E");;
			context.setData("rspCod", "LOT999");
			context.setData("rspMsg", context.getData("downloadMsg").toString());
			throw new CoreRuntimeException("交易失败");
		}
		context.setDataMap(thdRspMsgMap);
		
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
		
	}
	
	
}
