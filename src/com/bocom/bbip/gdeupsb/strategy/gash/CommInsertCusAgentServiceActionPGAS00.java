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
import com.bocom.bbip.eups.spi.service.agent.CommInsertCusAgentService;
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

public class CommInsertCusAgentServiceActionPGAS00 extends BaseAction implements
		CommInsertCusAgentService {

	private Logger logger = LoggerFactory
			.getLogger(CommInsertCusAgentServiceActionPGAS00.class);

	@Autowired
	@Qualifier("callThdTradeManager")
	ThirdPartyAdaptor callThdTradeManager;

	@Override
	public Map<String, Object> preInsertCusAgent(CustomerDomain customerDomain,
			List<CusAgentCollectDomain> domainList, Context context)
			throws CoreException {
		logger.info("CommInsertCusAgentServiceActionPGAS00 preInsertCusAgent start ... ...");

		logger.info("CommInsertCusAgentServiceActionPGAS00 preInsertCusAgent end ... ...");
		return null;
	}

	@Override
	public Map<String, Object> callThd(CustomerDomain customerDomain,
			List<CusAgentCollectDomain> domainList, Context context)
			throws CoreException {
		logger.info("CommInsertCusAgentServiceActionPGAS00 callThd start ... ...");
		
		String date = DateUtils.format(new Date(), DateUtils.STYLE_SIMPLE_DATE);
		context.setData("TransCode", "Add");
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

		if ("AddOK".equals(context.getData("TransCode").toString().trim())) {
			
			//动态协议表新增一条数据
			GdGasCusDay insCusInfo = BeanUtils.toObject(context.getDataMap(),
					GdGasCusDay.class);
			String sqn = get(BBIPPublicService.class).getBBIPSequence();
			insCusInfo.setSequence(sqn);
			get(GdGasCusDayRepository.class).insert(insCusInfo);

			// 燃气协议表新增一条数据
			GdGasCusAll addGasCusAll = BeanUtils.toObject(context.getDataMap(),
					GdGasCusAll.class);
			addGasCusAll.setOptDat(date);
			addGasCusAll.setOptNod((String) context.getData(ParamKeys.OBK_BR));
			get(GdGasCusAllRepository.class).insert(addGasCusAll);

			// context.setData("msgTyp", "N");
			// context.setData("rspCod", GDConstants.GDEUPSB_TXN_SUCC_CODE);
			// context.setData("rspMsg", "新增成功");

			logger.info("============新增本地协议，动态协议成功");
		}
		if("Double".equals(context.getData("TransCode").toString().trim())) {
			throw new CoreException(GDErrorCodes.GAS_CUS_AGT_DOUBLE);
		}
		if("AddNo".equals(context.getData("TransCode").toString().trim())) {
			throw new CoreException(GDErrorCodes.GAS_CUS_AGT_ADDNO);
		}
		logger.info("CommInsertCusAgentServiceActionPGAS00 callThd end ... ...");
		return null;
	}

	@Override
	public Map<String, Object> reverseThd(CustomerDomain customerDomain,
			List<CusAgentCollectDomain> domainList, Context context)
			throws CoreException {

		return null;
	}

}
