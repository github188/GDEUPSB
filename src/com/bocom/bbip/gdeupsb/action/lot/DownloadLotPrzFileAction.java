package com.bocom.bbip.gdeupsb.action.lot;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
/**
 * 福彩返奖（中奖）文件下载
 * @author WangMQ
 *
 */
public class DownloadLotPrzFileAction extends BaseAction{

	private final static Logger logger = LoggerFactory.getLogger(DownloadLotPrzFileAction.class);

	
	public void execute (Context context)throws CoreException, CoreRuntimeException{
		logger.info("Enter in DownloadLotPrzFileAction!");
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
		
		context.setData(ParamKeys.FILE_TYPE, "2"); //文件类型2：中奖文件
		context.setData("dealId", "LOTR01");// TODO 待确认 运营商ID
		
		String gameId = context.getData("gameId").toString();
        String drawId = context.getData("drawId").toString();
        
        context.setData("GameId", gameId);
        context.setData("DrawId", drawId);
        
		 CommonLotAction commAction = new CommonLotAction();
		 commAction.downloadFile(context);
		
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
	}
	
}
