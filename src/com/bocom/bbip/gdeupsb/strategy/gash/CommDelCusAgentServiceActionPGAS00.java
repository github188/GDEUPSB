package com.bocom.bbip.gdeupsb.strategy.gash;

import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.adaptor.ThirdPartyAdaptor;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.spi.service.agent.CommDelCusAgentService;
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

public class CommDelCusAgentServiceActionPGAS00 extends BaseAction implements CommDelCusAgentService{

	private Logger logger = LoggerFactory.getLogger(CommDelCusAgentServiceActionPGAS00.class);
	
	@Autowired
	@Qualifier("callThdTradeManager")
	ThirdPartyAdaptor callThdTradeManager;
	
	@Override
	public Map<String, Object> callThd(CustomerDomain customerDomain, Context context)
			throws CoreException {
		
		logger.info("CommDelCusAgentServiceActionPGAS00 callThd start ... ...");
		
		String date = DateUtils.format(new Date(), DateUtils.STYLE_SIMPLE_DATE);
		context.setData("TransCode", "Stop");
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

		if ("StopOK".equals(context.getData("TransCode").toString().trim())) {
			//动态协议表
			GdGasCusDay insCusInfo = new GdGasCusDay();
			insCusInfo.setSequence(get(BBIPPublicService.class).getBBIPSequence());
			insCusInfo.setCusNo((String) context.getData(ParamKeys.CUS_NO));;
			insCusInfo.settCommd((String) context.getData("tCommd"));
			insCusInfo.setCusAc((String) context.getData(ParamKeys.CUS_AC));
			insCusInfo.setCusNme((String) context.getData(ParamKeys.CUS_NME));
			insCusInfo.setAccTyp((String) context.getData("cusTyp"));
			insCusInfo.setOptNod((String) context.getData(ParamKeys.OBK_BR));
			insCusInfo.setIdTyp((String)context.getData(ParamKeys.ID_TYPE));
			insCusInfo.setIdNo((String)context.getData(ParamKeys.ID_NO));
			insCusInfo.setThdCusNam((String)context.getData(ParamKeys.THD_CUS_NME));
			insCusInfo.setCmuTel((String)context.getData(ParamKeys.CMU_TEL));
			insCusInfo.setThdCusAdr((String)context.getData(ParamKeys.THD_CUSTOMER_ADDR));
			get(GdGasCusDayRepository.class).insert(insCusInfo);

			//燃气协议表
			GdGasCusAll delGasCusAll = new GdGasCusAll();
			delGasCusAll.setCusAc((String)context.getData(ParamKeys.CUS_AC));
			delGasCusAll.setCusNo((String)context.getData(ParamKeys.CUS_NO));
			get(GdGasCusAllRepository.class).delete(delGasCusAll);

			// context.setData("msgTyp", "N");
			// context.setData("rspCod", GDConstants.GDEUPSB_TXN_SUCC_CODE);
			// context.setData("rspMsg", "新增成功");

			logger.info("============del本地协议，新增动态协议成功");
		}
		else{
			throw new CoreException(GDErrorCodes.GAS_CUS_AGT_STOPNO);
		}
		logger.info("CommDelCusAgentServiceActionPGAS00 callThd end ... ...");
		return null;
	}

	@Override
	public Map<String, Object> preDelCusAgent(CustomerDomain customerDomain, Context context)
			throws CoreException {		
		logger.info("=======CommDelCusAgentServiceActionPGAS00@preDelCusAgent start!....");
		logger.info("=======CommDelCusAgentServiceActionPGAS00@preDelCusAgent end!....");
		return null;
	}

}
