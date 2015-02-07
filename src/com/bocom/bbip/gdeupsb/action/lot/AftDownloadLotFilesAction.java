package com.bocom.bbip.gdeupsb.action.lot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class AftDownloadLotFilesAction extends BaseAction{
	
	public static Logger logger = LoggerFactory.getLogger(AftDownloadLotFilesAction.class);
	
	public void execute(Context context) throws CoreException, CoreRuntimeException{
		
		if(!"0".equals(context.getData("fileImportStatus"))){
			throw new CoreRuntimeException("文件入库失败");
		}
		
		context.setData("msgTyp", "N");;
		context.setData("rspCod", "000000");
		context.setData("rspMsg", "交易成功");
		
		logger.info("+++++++++++对账文件下载结果++++++++++" + context);

		
		
	}
	
	
	
	
}
