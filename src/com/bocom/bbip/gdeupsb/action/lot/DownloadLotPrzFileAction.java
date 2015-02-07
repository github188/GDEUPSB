package com.bocom.bbip.gdeupsb.action.lot;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.BPState;
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
//		<Item name="GameId"   length="2"   expression="TRIM($GameId,both)" desc="游戏编码"/>
//		<Item name="DrawId"   length="5"   expression="TRIM($DrawId,both)" desc="当前大期"/>
		
		context.setData("filTyp", "2"); //文件类型2：中奖文件
		context.setData("dealId", "LOTR01");//运营商ID
		context.setData("hTxnCd", "237");
		
		
		logger.info("=================即将进入文件下载地址查询============");
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
	}
	
}
