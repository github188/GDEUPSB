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
import com.bocom.bbip.utils.StringUtils;
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
		logger.info("=================context before insCusAgt callThd" + context);
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
			GdGasCusDay insCusInfo = new GdGasCusDay();
			insCusInfo.setSequence(get(BBIPPublicService.class).getBBIPSequence());
			insCusInfo.setCusNo((String) context.getData(ParamKeys.CUS_NO));;
			insCusInfo.settCommd((String) context.getData("tCommd"));
			insCusInfo.setCusAc((String) context.getData(ParamKeys.CUS_AC));
			if(StringUtils.isNoneBlank((String) context.getData(ParamKeys.CUS_NME))){
				insCusInfo.setCusNme((String) context.getData(ParamKeys.CUS_NME));
			}
			insCusInfo.setAccTyp((String) context.getData("cusTyp"));
			insCusInfo.setOptDat(date);
			insCusInfo.setOptNod((String) context.getData(ParamKeys.OBK_BR));
			insCusInfo.setIdTyp((String)context.getData(ParamKeys.ID_TYPE));
			insCusInfo.setIdNo((String)context.getData(ParamKeys.ID_NO));
			insCusInfo.setThdCusNam((String)context.getData(ParamKeys.THD_CUS_NME));
			insCusInfo.setCmuTel((String)context.getData(ParamKeys.CMU_TEL));
			insCusInfo.setThdCusAdr((String)context.getData(ParamKeys.THD_CUSTOMER_ADDR));
			get(GdGasCusDayRepository.class).insert(insCusInfo);

			// 燃气协议表新增一条数据
			GdGasCusAll addGasCusAll = BeanUtils.toObject(context.getDataMap(),
					GdGasCusAll.class);
			addGasCusAll.setCusNo((String) context.getData(ParamKeys.CUS_NO));;
			addGasCusAll.setCusAc((String) context.getData(ParamKeys.CUS_AC));
			addGasCusAll.setCusNme((String) context.getData(ParamKeys.CUS_NME));
			addGasCusAll.setCusTyp((String) context.getData("cusTyp"));
			addGasCusAll.setOptDat(date);
			addGasCusAll.setOptNod((String) context.getData(ParamKeys.OBK_BR));
			addGasCusAll.setIdTyp((String)context.getData(ParamKeys.ID_TYPE));
			addGasCusAll.setIdNo((String)context.getData(ParamKeys.ID_NO));
			addGasCusAll.setThdCusNme((String)context.getData(ParamKeys.THD_CUS_NME));
			addGasCusAll.setCmuTel((String)context.getData(ParamKeys.CMU_TEL));
			addGasCusAll.setThdCusAdr((String)context.getData(ParamKeys.THD_CUSTOMER_ADDR));
			
			get(GdGasCusAllRepository.class).insert(addGasCusAll);

			logger.info("============新增本地协议，动态协议成功");
		}
		if("Double".equals(context.getData("TransCode").toString().trim())) {
			throw new CoreException(GDErrorCodes.GAS_CUS_AGT_DOUBLE);
		}
		if("AddNo".equals(context.getData("TransCode").toString().trim())) {
			//冲代收付协议
			get(BBIPPublicService.class).synExecute("eups.commReversalACPSCusAgent", context);
			throw new CoreException(GDErrorCodes.GAS_CUS_AGT_ADDNO);
		}
		logger.info("CommInsertCusAgentServiceActionPGAS00 callThd end ... ...");
		return null;
	}

	@Override
	public Map<String, Object> reverseThd(CustomerDomain customerDomain,
			List<CusAgentCollectDomain> domainList, Context context)
			throws CoreException {
		logger.info("======CommInsertCusAgentServiceActionPGAS00@reverseThd start!....");
		logger.info("======CommInsertCusAgentServiceActionPGAS00@reverseThd end!....");
		return null;
	}

}
