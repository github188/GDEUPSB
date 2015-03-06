package com.bocom.bbip.gdeupsb.service.impl.elec02;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsAmountInfo;
import com.bocom.bbip.eups.entity.EupsTransJournal;
import com.bocom.bbip.eups.repository.EupsTransJournalRepository;
import com.bocom.bbip.eups.spi.service.online.PayFeeOnlineService;
import com.bocom.bbip.eups.spi.vo.CommHeadDomain;
import com.bocom.bbip.eups.spi.vo.PayFeeOnlineDomain;
import com.bocom.bbip.eups.spi.vo.PayFeeOnlineRetDomain;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.bbip.utils.NumberUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

/**
 * 联机缴费
 * @author hefengwen
 *
 */
public class PayFeeOnlineServiceActionELEC02 extends BaseAction implements PayFeeOnlineService {
	
	private static Logger logger = LoggerFactory.getLogger(PayFeeOnlineServiceActionELEC02.class);

	@Override
	public Map<String, Object> aftThdDeal(CommHeadDomain commheaddomain,	PayFeeOnlineRetDomain payfeeonlineretdomain, Context context)	throws CoreException {
		// TODO :第三方记账完成后处理
		logger.info("PayFeeOnlineServiceActionELEC02 aftThdDeal start ... ...");
		
		String sqn = context.getData("sqn");
		String bakFld1 = context.getData("ECD");
		EupsTransJournal journal = new EupsTransJournal();
		journal.setSqn(sqn);
		journal.setBakFld1(bakFld1);
		get(EupsTransJournalRepository.class).update(journal);
//		<fixString name="WTC" length="12"/>   <!-- 委托节点代码-->
//		<fixString name="CLZ"  length="16"/>   <!-- 流水-->
//		<fixString name="WD0" length="8"/>   <!-- 工作日期-->
//		<fixString name="OWC" length="12"/>   <!-- 原委托节点代码-->
//		<fixString name="051"   length="8"/>   <!-- 原委托日期-->
//		<fixString name="OLZ" length="16"/>   <!-- 原业务流水号-->
//		<fixString name="CUN" length="3"/>   <!-- 货币符号-->
//		<fixString name="JNA" length="12"/>   <!-- 缴纳金额-->
//		<fixString name="JJM" length="255"/>   <!-- 缴费结果说明-->
		context.setData("WD0", context.getData("WD0"));
		context.setData("OCN", context.getData("OWC"));
		context.setData("O51", context.getData("051"));
		context.setData("OLZ", context.getData("OLZ"));
		context.setData("CUN", context.getData("CUN"));
//		String jna = context.getData("JNA");
//		BigDecimal txnAmt = NumberUtils.centToYuan(jna);
//		context.setData("txnAmt", txnAmt);
		context.setData("JJM", context.getData("JJM"));
		
		logger.info("PayFeeOnlineServiceActionELEC02 aftThdDeal end ... ...");
		return null;
	}

	/**
	 * 交易前特色处理
	 */
	@Override
	public Map<String, Object> preCheckDeal(CommHeadDomain commheaddomain,PayFeeOnlineDomain payfeeonlinedomain, Context context) throws CoreException {
		logger.info("PayFeeOnlineServiceActionELEC02 preCheckDeal start ... ...");
		Date acDte = context.getData(ParamKeys.ACCOUNT_DATE);
		context.setData(ParamKeys.ACCOUNT_DATE, DateUtils.format(acDte, DateUtils.STYLE_yyyyMMdd));
		String busTyp = context.getData(ParamKeys.BUS_TYP);
		context.setData(ParamKeys.BUS_TYP, null);
//		get(BBIPPublicService.class).synExecute("eups.queryThirdFeeOnline", context);
		get(QueryDealServiceActionELEC02.class).qryDeal(null, null, context);
		context.setData(ParamKeys.BUS_TYP, busTyp);
		logger.info("PayFeeOnlineServiceActionELEC02 preCheckDeal end ... ...");
		return null;
	}

	/**
	 * 
	 */
	@Override
	public Map<String, Object> preHostDeal(CommHeadDomain commheaddomain,PayFeeOnlineDomain payfeeonlinedomain, Context context)	throws CoreException {
		// TODO:使用标准版即可，如果前端接口为自定义，需比对接口字段是否一致
		logger.info("PayFeeOnlineServiceActionELEC02 preHostDeal start ... ...");
		context.setData("tmlNo", context.getData("tlrTmlId"));//终端号转换
		logger.info("PayFeeOnlineServiceActionELEC02 preHostDeal end ... ...");
		return null;
	}

	@Override
	public Map<String, Object> preThdDeal(CommHeadDomain commheaddomain,	PayFeeOnlineDomain payfeeonlinedomain,EupsAmountInfo eupsamountinfo, Context context)	throws CoreException {
		logger.info("PayFeeOnlineServiceActionELEC02 preThdDeal start ... ...");
		// TODO:为第三方接口报文字段赋值，发送请求至第三方
		String sqn = context.getData("sqn");
		String date = DateUtils.format(new Date(), "yyyyMMdd");
		String time = DateUtils.format(new Date(),"yyyyMMddHHmmss");
		context.setData("AppTradeCode", "11");//业务交易码
		
		
		context.setData("StartAddr", "301");
		context.setData("DestAddr", "");
		context.setData("MesgID", sqn);
		context.setData("WorkDate", date);
		context.setData("SendTime", time);
		context.setData("mesgPRI", "9");
		context.setData("recordNum", "0");
		context.setData("FileName", "");
		context.setData("zipFlag", "0");
		
		String wd0 = context.getData("WD0");//原委托日期，与前费查询返回中收付费企业代码一致
		String owc = context.getData("ECD");//原委托节点代码
		String olz = context.getData("CLZ");//原流水
		String clm = context.getData("CLM");//客户名称
		String pcf = context.getData("PCF");//部分缴费标志
		context.setData("WTC", "301");
		context.setData("TMN", context.getData(ParamKeys.BR));
		context.setData("STO", context.getData(ParamKeys.TELLER));
		context.setData("CLZ", sqn.substring(4));
		context.setData("WD0", date);
		context.setData("OWC", owc);
		context.setData("051", wd0);
		context.setData("OLZ", olz);
		context.setData("ECD", owc);
		context.setData("8ED", context.getData("EDD"));
		context.setData("JFH", context.getData("thdCusNo"));
		context.setData("KKB", "301");
		context.setData("KAC", context.getData(ParamKeys.CUS_AC));
		context.setData("CUN", "RMB");
		BigDecimal txnAmt = context.getData(ParamKeys.TXN_AMT);
		context.setData("KKA", NumberUtils.yuanToCentString(txnAmt));
		context.setData("CHG", "");
		context.setData("JYF", "0");
		context.setData("PDP", "");
		context.setData("AUN", clm);
		context.setData("MNT", "01");
		context.setData("PCF", pcf);
		context.setData("WYSB", sqn);
		context.setData("JFLIST", context.getData("rspList"));
		
		context.setData("txnSqn", context.getData("sqn"));//保存缴费交易流水号，返回给前端，用于抹帐
		logger.info("PayFeeOnlineServiceActionELEC02 preThdDeal end ... ...");
		return null;
	}

}
