package com.bocom.bbip.gdeupsb.action;

import java.util.Date;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;

public class CreateResponseSqnAction extends BaseAction{
		public String creatresponseSqn(Context context){
			log.info("==============Start  CreateResponseSqnAction ");
			String reqDate=context.getData(GDParamKeys.TRADE_SEND_DATE).toString();
			context.setData(ParamKeys.TXN_DAT, DateUtils.formatAsSimpleDate(new Date()));
			context.setData(ParamKeys.TXN_TME, DateUtils.parse(DateUtils.formatAsTranstime(new Date())));
			System.out.println(context.getData(ParamKeys.TXN_TME));
			String reqBankNo=context.getData(ParamKeys.BANK_NO).toString();
			//TODO  确定字段SelVal
//			String reqSelVal=context.getData("SelVal");
			String sqn = reqDate+"0301"+reqBankNo.substring(1, 4);
			log.info("~~~~~~~~~~~~~~~sqn:"+sqn);
			context.setData(ParamKeys.SEQUENCE, sqn);
			context.setData(ParamKeys.TXN_STS, "0");
			
			//第三方信息   不知道在哪块
			context.setData(ParamKeys.THD_SEQUENCE, sqn);
			context.setData(ParamKeys.THD_CUSTOMER_NO,context.getData(ParamKeys.COMPANY_NO));
			context.setData(ParamKeys.PAYFEE_TYPE, "0");
			context.setData(ParamKeys.RAP_TYPE, "0");
			return sqn;
		}
}
