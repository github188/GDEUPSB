package com.bocom.bbip.gdeupsb.action.elec02;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bocom.bbip.comp.BBIPPublicServiceImpl;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.spi.service.single.PayUnilateralToBankService;
import com.bocom.bbip.eups.spi.vo.CommHeadDomain;
import com.bocom.bbip.eups.spi.vo.PayFeeOnlineDomain;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.jump.bp.channel.http.support.JsonUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

public class PayUnilateralToBankServiceImplELEC02 extends BaseAction implements PayUnilateralToBankService {
	private static final Log logger = LogFactory.getLog(PayUnilateralToBankServiceImplELEC02.class);

	@Override
	public Map<String, Object> aftPayToBank(CommHeadDomain arg0,
			PayFeeOnlineDomain arg1, Context context) throws CoreException {
		logger.info("aftPayToBank");
		context.setData("test", "12345");
		return null;
	}

	@Override
	public Map<String, Object> prePayToBank(CommHeadDomain arg0,
			PayFeeOnlineDomain arg1, Context context) throws CoreException {
		logger.info("prePayToBank");
		List <Map<String,String>>list=new ArrayList<Map<String,String>>();
		Map<String,String> map=new HashMap<String,String>();
		map.put("MON", (String)context.getData("MON"));
		map.put("MNS", (String)context.getData("MNS"));
		map.put("JLD", (String)context.getData("JLD"));
		map.put("YSH", (String)context.getData("YSH"));
		map.put("KAT", (String)context.getData("KAT"));
		map.put("OAN", (String)context.getData("OAN"));
		map.put("ECD", (String)context.getData("ECD"));
		map.put("WDO", (String)context.getData("WDO"));
		list.add(map);
		/**保存到流水表中的rsvFld3中，生成对账文件时需要上述字段*/
		context.setData("rsvFld3", new String(JsonUtils.jsonFromObject(list, "UTF8")));
		
		double i=Double.parseDouble(context.getData(ParamKeys.TXN_AMT).toString());
		double d=i/100;
		DecimalFormat df=new DecimalFormat("#.00");
		BigDecimal txnAmt=new BigDecimal(df.format(d));
		context.setData(ParamKeys.TXN_AMT,txnAmt );
		
		return null;
	}

	@Override
	public Map<String, Object> prepareCheckDeal(CommHeadDomain arg0,PayFeeOnlineDomain arg1, Context context) throws CoreException {
		logger.info("prepareCheckDeal");
		//TODO:for test
		context.setData(ParamKeys.BR, "01441131999");
		context.setData(ParamKeys.BK, "01441999999");
		context.setData(ParamKeys.TELLER, "ABIR148");
		context.setData(ParamKeys.EUPS_BUSS_TYPE, "ELEC02");
		/*context.setData(ParamKeys.CUS_AC, "6222620780003804748");
		context.setData(ParamKeys.TXN_AMT, 0.01);
		context.setData(ParamKeys.THD_CUS_NO, "123456789");
		String thdsql=((BBIPPublicServiceImpl)get(GDConstants.BBIP_PUBLIC_SERVICE)).getBBIPSequence();
		context.setData(ParamKeys.THD_SEQUENCE, thdsql);*/
		return null;
	}

}
