package com.bocom.bbip.gdeupsb.action.elea;

import java.util.Date;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

/**
 * 
 * 电力付费网关发出网管信息
 * 
 * @author qc.w
 * 
 */
public class EleGzCmuTestAction extends BaseAction {
	@Override
	public void execute(Context context) throws CoreException, CoreRuntimeException {
		log.info("EleGzCmuTestAction start!..");

		String sqn = context.getData(ParamKeys.SEQUENCE);
		String sqn1 = sqn.substring(0, 8);
		String sqn2 = sqn.substring(sqn.length() - 4, sqn.length());
		context.setData("txnDateTime7", DateUtils.format(new Date(), DateUtils.STYLE_MMddHHmmss)); // txnDateTime7
		context.setData("transJournal11", sqn1 + sqn2); // transJournal11

		context.setData("sndFlg33", "33"); // 发送机构标识码
		context.setData("thdRspCde", "000000"); // 返回码
		context.setData("cmuCde70", "301"); // 线路测试
	}

}
