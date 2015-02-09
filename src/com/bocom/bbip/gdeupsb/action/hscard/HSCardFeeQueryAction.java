package com.bocom.bbip.gdeupsb.action.hscard;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bocom.bbip.comp.gems.GIA;
import com.bocom.bbip.comp.gems.GemsResult;
import com.bocom.bbip.comp.gems.service.DefaultGemsServiceAccessObject;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class HSCardFeeQueryAction extends BaseAction{
	
	private final static Log log = LogFactory.getLog(HSCardFeeQueryAction.class);
	
	public void execute(Context ctx) throws CoreException,CoreRuntimeException{
		log.info("HSCardFeeQueryAction start......");
		// 查询内部账户余额
				// 判断账户是否余额不足
				// TODO: for test,先注释掉
 DefaultGemsServiceAccessObject gemsServiceAccessObject = get(DefaultGemsServiceAccessObject.class);
				 GIA gia = gemsServiceAccessObject.build(ctx, "TP9000");
				 GemsResult gemsResult = gemsServiceAccessObject.callService(gia,
				 ctx.getDataMap());
				 Map<String, Object> balQry = gemsResult.getPayload();
				 String curBal = (String) balQry.get("bal"); // 余额

	}

}
