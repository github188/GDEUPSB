package com.bocom.bbip.gdeupsb.action.lot;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.gdeupsb.entity.GDEupsbLotSysCfgInfo;
import com.bocom.bbip.gdeupsb.repository.GDEupsbLotSysCfgInfoRepository;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
/**
 * 福彩对账文件下载
 * @author WangMQ
 *
 */
public class DownloadLotChkFileAction extends BaseAction{

	private final static Logger logger = LoggerFactory.getLogger(DownloadLotChkFileAction.class);

	
	public void execute (Context context)throws CoreException, CoreRuntimeException{
		logger.info("Enter in DownLoadLotChkFileAction!");
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
//		<Item name="GameId"   length="2"   expression="TRIM($GameId,both)" desc="游戏编码"/>
//		<Item name="DrawId"   length="5"   expression="TRIM($DrawId,both)" desc="当前大期"/>
		
		context.setData("filTyp", "3"); //文件类型3：对账文件
		context.setData("dealId", "LOTR01");
		context.setData("hTxnCd", "237");
		
		
		logger.info("=================即将进入文件下载地址查询============");
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
	}
	
}
