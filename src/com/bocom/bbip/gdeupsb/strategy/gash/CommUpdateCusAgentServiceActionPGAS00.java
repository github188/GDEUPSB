package com.bocom.bbip.gdeupsb.strategy.gash;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.adaptor.ThirdPartyAdaptor;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.spi.service.agent.CommUpdateCusAgentService;
import com.bocom.bbip.eups.spi.vo.CusAgentCollectDomain;
import com.bocom.bbip.eups.spi.vo.CustomerDomain;
import com.bocom.bbip.gdeupsb.common.GDErrorCodes;
import com.bocom.bbip.gdeupsb.entity.GdGasCusAll;
import com.bocom.bbip.gdeupsb.entity.GdGasCusDay;
import com.bocom.bbip.gdeupsb.repository.GdGasCusAllRepository;
import com.bocom.bbip.gdeupsb.repository.GdGasCusDayRepository;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

public class CommUpdateCusAgentServiceActionPGAS00 extends BaseAction implements CommUpdateCusAgentService{
	
	private Logger logger = LoggerFactory.getLogger(CommUpdateCusAgentServiceActionPGAS00.class);
	
	@Autowired
	@Qualifier("callThdTradeManager")
	ThirdPartyAdaptor callThdTradeManager;

	@Override
	public Map<String, Object> callThd(CustomerDomain customerDomain,
			List<CusAgentCollectDomain> domainList, Context context)
			throws CoreException {
		
		logger.info("CommUpdateCusAgentServiceActionPGAS00 callThd start ... ...");
		
		String date = DateUtils.format(new Date(), DateUtils.STYLE_SIMPLE_DATE);
		context.setData("TransCode", "Edit");
		context.setData("regDat", date);
		context.setData("txnCod", "GASHXY");
		context.setData("objSvr", "STDHGAS1");

		context.setProcessId("gdeupsb.oprGasCusAgent1");

		Map<String, Object> tradeMap1 = callThdTradeManager.trade(context);
		context.setDataMap(tradeMap1);

		context.setData("optDat", date);
		context.setData("optNod", context.getData("NodNo"));
		logger.info("===oprGasCusAgentAction1=====context=" + context);

		context.setProcessId("gdeupsb.oprGasCusAgentAction");
		logger.info("===oprGasCusAgentAction=====context=" + context);

		if ("EditOK".equals(context.getData("TransCode").toString().trim())) {
			
			//动态协议表
			GdGasCusDay insCusInfo = BeanUtils.toObject(context.getDataMap(),
					GdGasCusDay.class);
			String sqn = get(BBIPPublicService.class).getBBIPSequence();
			insCusInfo.setSequence(sqn);
			get(GdGasCusDayRepository.class).insert(insCusInfo);

			//燃气协议表
			GdGasCusAll addGasCusAll = BeanUtils.toObject(context.getDataMap(),
					GdGasCusAll.class);
			addGasCusAll.setOptDat(date);
			addGasCusAll.setOptNod((String) context.getData(ParamKeys.OBK_BR));
			get(GdGasCusAllRepository.class).update(addGasCusAll);

			// context.setData("msgTyp", "N");
			// context.setData("rspCod", GDConstants.GDEUPSB_TXN_SUCC_CODE);
			// context.setData("rspMsg", "新增成功");

			logger.info("============修改本地协议，新增动态协议成功");
		}
		else{
			throw new CoreException(GDErrorCodes.GAS_CUS_AGT_EDITNO);
		}
		
		logger.info("CommUpdateCusAgentServiceActionPGAS00 callThd end ... ...");
		return null;
	}

	@Override
	public Map<String, Object> preUpdateCusAgent(CustomerDomain arg0,
			List<CusAgentCollectDomain> arg1, Context arg2)
			throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}

}
