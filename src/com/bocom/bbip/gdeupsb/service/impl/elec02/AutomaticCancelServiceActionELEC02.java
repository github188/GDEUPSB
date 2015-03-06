package com.bocom.bbip.gdeupsb.service.impl.elec02;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.spi.service.online.AutomaticCancelService;
import com.bocom.bbip.eups.spi.vo.CancelDomain;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.bbip.utils.NumberUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

/**
 * 自动区分抹账：根据流水表第三方状态和主机状态
 * @author hefengwen
 *
 */
public class AutomaticCancelServiceActionELEC02 implements	AutomaticCancelService {
	
	private static Logger logger = LoggerFactory.getLogger(AutomaticCancelServiceActionELEC02.class);


	/**
	 * 第三方抹账前处理
	 */
	@Override
	public Map<String, Object> preCancel(CancelDomain canceldomain,Context context) throws CoreException {
		logger.info("AutomaticCancelServiceActionWATR00 preCancel  start ... ...");
		// TODO :第三方抹帐前报文接口字段处理
		String sqn = context.getData("sqn");
		String date = DateUtils.format(new Date(), "yyyyMMdd");
		String time = DateUtils.format(new Date(),"yyyyMMddHHmmss");
		String oldTxnSqn = context.getData("oldTxnSqn");
		context.setData("AppTradeCode", "12");//业务交易码
		context.setData("StartAddr", "301");
		context.setData("DestAddr", "");
		context.setData("MesgID", sqn);
		context.setData("WorkDate", date);
		context.setData("SendTime", time);
		context.setData("mesgPRI", "9");
		context.setData("recordNum", "0");
		context.setData("FileName", "");
		context.setData("zipFlag", "0");
		
		context.setData("WTC", "301");
		context.setData("TMN", context.getData("brno"));
		context.setData("STO", context.getData("tlr"));
		context.setData("CLZ", sqn.substring(4));
		context.setData("WD0", date);
		context.setData("OJN", "301");
		context.setData("OJD", date);
		context.setData("OLZ", oldTxnSqn.substring(4));
		context.setData("OWK", date);
		context.setData("OED", "301");
		context.setData("OJF", context.getData(ParamKeys.THD_CUS_NO));
		context.setData("OJB", "301");
		context.setData("OJC", context.getData(ParamKeys.CUS_AC));
		context.setData("OCU", "RMB");
		BigDecimal txnAmt = context.getData(ParamKeys.TXN_AMT);
		context.setData("OJA", NumberUtils.yuanToCent(txnAmt));
		context.setData("CXA", NumberUtils.yuanToCent(txnAmt));
		context.setData("OCN", context.getData(ParamKeys.BAK_FLD1));//供电公司编号
		context.setData("SCR", context.getData("scr"));
		context.setData("AUN", "");
		logger.info("AutomaticCancelServiceActionWATR00 preCancel  end ... ...");
		return null;
	}
	/**
	 * 第三方抹账后处理
	 */
	@Override
	public Map<String, Object> aftCancel(CancelDomain canceldomain,Context context) throws CoreException {
		logger.info("AutomaticCancelServiceActionWATR00 aftCancel  start ... ...");
		context.setData("txnSqn", context.getData("sqn"));
//		<fixString name="WTC" length="12"/>   <!-- 委托节点代码-->
//		<fixString name="WD0" length="8"/>   <!-- 工作日期-->
//		<fixString name="CLZ" length="16"/>   <!-- 流水-->
//		<fixString name="OCN"   length="12"/>   <!-- 原冲销业务委托节点代码-->
//		<fixString name="OCD" length="8"/>   <!-- 原冲销业务委托日期-->
//		<fixString name="OLZ" length="16"/>   <!-- 原冲销业务流水-->
		context.setData("WTC", context.getData("WTC"));
		context.setData("WD0", context.getData("WD0"));
		context.setData("thdSqn", context.getData("CLZ"));
		context.setData("OCN", context.getData("OCN"));
		context.setData("OCD", context.getData("OCD"));
		context.setData("OLZ", context.getData("OLZ"));
		logger.info("AutomaticCancelServiceActionWATR00 aftCancel  end ... ...");
		return null;
	}

}
