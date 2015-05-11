package com.bocom.bbip.gdeupsb.service.impl.vech;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsAmountInfo;
import com.bocom.bbip.eups.spi.service.online.PayFeeOnlineService;
import com.bocom.bbip.eups.spi.vo.CommHeadDomain;
import com.bocom.bbip.eups.spi.vo.PayFeeOnlineDomain;
import com.bocom.bbip.eups.spi.vo.PayFeeOnlineRetDomain;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GDVechIndentInfo;
import com.bocom.bbip.gdeupsb.repository.GDVechIndentInfoRepository;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

public class PayFeeOnlineServiceActionVECH00 implements PayFeeOnlineService{
	private final static Log log=LogFactory.getLog(PayFeeOnlineServiceActionVECH00.class);
	@Autowired
	GDVechIndentInfoRepository gdVechIndentInfoRepository;
			@Override
			public Map<String, Object> preCheckDeal(CommHeadDomain commheaddomain,
					PayFeeOnlineDomain payfeeonlinedomain, Context context)
					throws CoreException {
				log.info("===========Start   PayFeeOnlineServiceActionVECH00  preCheckDeal");
				String sqn=context.getData(GDParamKeys.ORDER_ID);
				context.setData(ParamKeys.SEQUENCE, sqn);
				GDVechIndentInfo gdVechIndentInfo=gdVechIndentInfoRepository.findOne(sqn);
				String cusNme=gdVechIndentInfo.getUserNam();
				context.setData(ParamKeys.CUS_NME, cusNme);
				String userId=gdVechIndentInfo.getUserId();
				context.setData(ParamKeys.ID_NO,userId);
				String mobile=gdVechIndentInfo.getMobile();
				context.setData(ParamKeys.CMU_TEL,mobile);
				log.info("===========End    PayFeeOnlineServiceActionVECH00  preCheckDeal");
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
				log.info("===========End   PayFeeOnlineServiceActionVECH00  preThdDeal");
				return null;
			}
			@Override
			public Map<String, Object> aftThdDeal(CommHeadDomain commheaddomain,
					PayFeeOnlineRetDomain payfeeonlineretdomain, Context context)
					throws CoreException {
				log.info("===========Start   PayFeeOnlineServiceActionVECH00  aftThdDeal");
				String orderId=context.getData(ParamKeys.SEQUENCE).toString();
				if(Constants.RESPONSE_CODE_SUCC.equals(context.getData(ParamKeys.RESPONSE_CODE).toString())){
						log.info("===========update  GDEUPS_VECH_INDENT ");
						GDVechIndentInfo gdVechIndentInfo=gdVechIndentInfoRepository.findOne(orderId);
						gdVechIndentInfo.setOrdSta("1");
						gdVechIndentInfoRepository.update(gdVechIndentInfo);
				}
				log.info("===========End   PayFeeOnlineServiceActionVECH00  aftThdDeal");
				return null;
			}
}
