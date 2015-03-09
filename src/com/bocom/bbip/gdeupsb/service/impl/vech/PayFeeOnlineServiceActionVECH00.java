package com.bocom.bbip.gdeupsb.service.impl.vech;

import java.util.Date;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsAmountInfo;
import com.bocom.bbip.eups.spi.service.online.PayFeeOnlineService;
import com.bocom.bbip.eups.spi.vo.CommHeadDomain;
import com.bocom.bbip.eups.spi.vo.PayFeeOnlineDomain;
import com.bocom.bbip.eups.spi.vo.PayFeeOnlineRetDomain;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

public class PayFeeOnlineServiceActionVECH00 implements PayFeeOnlineService{
	private final static Log log=LogFactory.getLog(PayFeeOnlineServiceActionVECH00.class);
//	@Autowired
//	GDEupsVechIndentRepository  gdEupsVechIndentRepository;
			@Override
			public Map<String, Object> preCheckDeal(CommHeadDomain commheaddomain,
					PayFeeOnlineDomain payfeeonlinedomain, Context context)
					throws CoreException {
				log.info("===========Start   PayFeeOnlineServiceActionVECH00  preCheckDeal");
				//TODO 短信验证码
//				Map<String, Object> map=callService
				return null;
			}
			@Override
			public Map<String, Object> preHostDeal(CommHeadDomain commheaddomain,
					PayFeeOnlineDomain payfeeonlinedomain, Context context)
					throws CoreException {
				log.info("===========Start   PayFeeOnlineServiceActionVECH00  preHostDeal");
				Date txnDte=DateUtils.parse(DateUtils.formatAsSimpleDate(new Date()));
				Date txnTme=DateUtils.parse(DateUtils.formatAsTranstime(new Date()));
				
				context.setData(ParamKeys.TXN_DTE, txnDte);
				context.setData(ParamKeys.TXN_TME, txnTme);
				context.setData(ParamKeys.SEQUENCE, context.getData(GDParamKeys.ORDER_ID));
				log.info("===========End   PayFeeOnlineServiceActionVECH00  preHostDeal");
				return null;
			}
			@Override
			public Map<String, Object> preThdDeal(CommHeadDomain commheaddomain,
					PayFeeOnlineDomain payfeeonlinedomain,
					EupsAmountInfo eupsamountinfo, Context context)
					throws CoreException {
				log.info("===========Start   PayFeeOnlineServiceActionVECH00  preThdDeal");
				context.setData(ParamKeys.THD_SEQUENCE, context.getData(GDParamKeys.STORE_SEQ));
				context.setData(ParamKeys.BAK_FLD1, context.getData(GDParamKeys.STATION_ARRIVE));
				context.setData(ParamKeys.BAK_FLD2, context.getData(GDParamKeys.STATION_GET_ON));
				context.setData(ParamKeys.BAK_FLD3, context.getData(ParamKeys.PAY_TYPE));
				context.setData(ParamKeys.BAK_FLD4, context.getData(GDParamKeys.BUY_NUMBER));
				context.setData(ParamKeys.ID_NO, context.getData(GDParamKeys.CUS_ID));
				context.setData(ParamKeys.RSV_FLD1, context.getData(GDParamKeys.LINE_NO));
				context.setData(ParamKeys.TXN_AMT, context.getData(GDParamKeys.ALL_MONEY));
				context.setData(ParamKeys.BV_KIND, context.getData(GDParamKeys.VOUCHER));
				context.setData(ParamKeys.CMU_TEL, context.getData(GDParamKeys.TEL));
				log.info("===========End   PayFeeOnlineServiceActionVECH00  preThdDeal");
				return null;
			}
			@Override
			public Map<String, Object> aftThdDeal(CommHeadDomain commheaddomain,
					PayFeeOnlineRetDomain payfeeonlineretdomain, Context context)
					throws CoreException {
				log.info("===========Start   PayFeeOnlineServiceActionVECH00  aftThdDeal");
				String orderId=context.getData(ParamKeys.SEQUENCE).toString();
				String lineNo=context.getData(GDParamKeys.LINE_NO).toString();
				String cusName=context.getData(ParamKeys.CUS_NME).toString();
				String cusId=context.getData(GDParamKeys.CUS_ID).toString();
				String tel=context.getData(GDParamKeys.TEL).toString();
				if(Constants.RESPONSE_CODE_SUCC.equals(context.getData(ParamKeys.RESPONSE_CODE).toString())){
//						GDEupsVechIndent gdEupsVechIndent=gdEupsVechIndentRepository.findOne(orderId);
//				 		gdEupsVechIndent.setCusNme(cusName);
//					 	gdEupsVechIndent.setCusId(cusId);
//						gdEupsVechIndent.setOrderState(context.getDate(GDParamKeys.ORDER_STATE));
//						gdEupsVechIndent.setPayType(context.getDate(ParamKeys.PAY_TYPE));
//						gdEupsVechIndentRepository.update(gdEupsVechIndents);
				}
				return null;
			}
}
