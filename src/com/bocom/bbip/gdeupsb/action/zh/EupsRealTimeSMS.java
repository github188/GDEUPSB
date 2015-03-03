package com.bocom.bbip.gdeupsb.action.zh;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsMsgStore;
import com.bocom.bbip.eups.repository.EupsMsgStoreRepository;
import com.bocom.bbip.utils.ContextUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

public class EupsRealTimeSMS extends BaseAction {
	private static final Log logger = LogFactory.getLog(EupsRealTimeSMS.class);
   
	public void execute(Context context)throws CoreException {
		logger.info("send sms start");
		/** 手机号码 ****/
		final String mobile=ContextUtils.assertDataHasLengthAndGetNNR(context, "", ErrorCodes.EUPS_FIELD_EMPTY);
		/**  短信内容****/
		final String msg=ContextUtils.assertDataHasLengthAndGetNNR(context,  "", ErrorCodes.EUPS_FIELD_EMPTY);
	    logger.info("mobileNo :"+mobile);
		EupsMsgStore msgStore = new EupsMsgStore();
		msgStore.setSysNo((String) context.getRequestId().substring(0, 18));
		msgStore.setMobNum(mobile);
		msgStore.setBr((String) context.getData(ParamKeys.BR));
		msgStore.setTemNo("1");// 模板编号，由于数据库定义非空，temNo必输
		msgStore.setMsgTyp("2");// 短信类型 1-模板短信，2-转发短信
		msgStore.setSmsCtn(msg);
		msgStore.setComNo(" ");
		msgStore.setBusKnd(" ");
		msgStore.setEupsBusTyp(" ");
		msgStore.setSndFlg("0");
		Date nowTime = new Date(context.getTimestamp());
		msgStore.setSndDte(nowTime);
		msgStore.setSndTme(nowTime);
		msgStore.setCreDte(nowTime);
		get(EupsMsgStoreRepository.class) .insert(msgStore);
		logger.info("send sms success!!!");
	}
}
