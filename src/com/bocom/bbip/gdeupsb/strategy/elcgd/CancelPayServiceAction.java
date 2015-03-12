package com.bocom.bbip.gdeupsb.strategy.elcgd;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsTransJournal;
import com.bocom.bbip.eups.spi.service.online.AutomaticCancelService;
import com.bocom.bbip.eups.spi.vo.CancelDomain;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.bbip.utils.NumberUtils;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

/**
 * 联机抹帐
 * 
 * @author qc.w
 * 
 */
public class CancelPayServiceAction implements AutomaticCancelService {

	private final static Logger log = LoggerFactory.getLogger(CancelPayServiceAction.class);

	@Override
	public Map<String, Object> aftCancel(CancelDomain arg0, Context context) throws CoreException {

		log.info("CancelPayServiceAction  aftCancel  start!..");

		context.setData("eleClrDte", context.getData("pwrtxnDate15")); // 供电公司清算日期
		context.setData("thdSqn", context.getData("eleThdSqn37")); // 第三方流水号
		context.setData("rsvFld1", context.getData("remarkData48")); // 将第三方返回信息保存入库

		return null;
	}

	@Override
	public Map<String, Object> preCancel(CancelDomain arg0, Context context) throws CoreException {
		log.info("CancelPayServiceAction preCancel start!..");

		EupsTransJournal eupsTransJournal = context.getVariable(GDParamKeys.GZ_ELE_CANCLE_OLD_JNL);

		context.setData(GDParamKeys.GZ_ELE_DEAL_CODE, GDConstants.GZ_ELE_DEAL_CODE); // 处理码020000
		context.setData(GDParamKeys.GZ_ELE_RCS_NO, GDConstants.GZ_ELE_DEAL_ORG_CODE);
		context.setData(GDParamKeys.GZ_ELE_RSC_ORG, GDConstants.GZ_ELE_RECEIVE_ORG_CODE);
		context.setData(GDParamKeys.GZ_ELE_CCY_COD, GDConstants.GZ_ELE_CCY);

		String ttrmId = (String) context.getData(ParamKeys.BBIP_TERMINAL_NO);
		ttrmId = StringUtils.rightPad(ttrmId, 8, ' ');

		String crpID = context.getData(ParamKeys.TELLER);
		crpID = StringUtils.rightPad(crpID, 15, ' ');

		context.setData(ParamKeys.MESSAGE_TYPE, "0400");
		context.setData(GDParamKeys.GZ_ELE_CUS_AC, eupsTransJournal.getCusAc());
		context.setData(GDParamKeys.GZ_ELE_THD_PAY_TYP, GDConstants.GZ_ELE_DEAL_CODE);

		String sqn = context.getData(ParamKeys.SEQUENCE);
		context.setData("transJournal11", sqn.substring(sqn.length() - 12));

		Date bnkTxnTime = context.getData(ParamKeys.TXN_TIME);

		String bnkTxnTme = DateUtils.format(bnkTxnTime, DateUtils.STYLE_HHmmss);
		context.setData("bnkTxnTime12", bnkTxnTme);

		String bnkTxnDte = DateUtils.format(bnkTxnTime, DateUtils.STYLE_MMdd);
		context.setData("bnkTxnDate13", bnkTxnDte);

		context.setData(GDParamKeys.GZ_ELE_TTRM_ID, ttrmId);
		context.setData(GDParamKeys.GZ_ELE_TDL_ID, crpID);

		context.setData(GDParamKeys.GZ_ELE_TRA_TYP, GDConstants.GZ_ELE_TXN_TYP_JF);

		BigDecimal txnAmt = (BigDecimal) context.getData(ParamKeys.TXN_AMOUNT);

		String ttxnAmt = NumberUtils.yuanToCentString(txnAmt); // 金额转化为元
		ttxnAmt = StringUtils.leftPad(ttxnAmt, 12, '0'); // 左拼接0

		context.setData("amount4", ttxnAmt); // 交易金额

		BigDecimal fee = (BigDecimal) eupsTransJournal.getHfe();
		if (null == fee) {
			fee = new BigDecimal("0.00");
		}

		context.setData("pwrFee28", StringUtils.leftPad(NumberUtils.yuanToCentString(fee), 12, '0')); // 手续费

		// 附加字段处理
		String oldRmkDte = eupsTransJournal.getRsvFld1();
		String rmkNew = new String();
		String feeWay = oldRmkDte.substring(43, 45);

		if ("10".equals(feeWay)) {
			rmkNew = oldRmkDte.substring(0, 31) + eupsTransJournal.getThdSqn() + feeWay + StringUtils.left("", 25);
		} else {
			rmkNew = oldRmkDte.substring(0, 31) + eupsTransJournal.getThdSqn() + feeWay + oldRmkDte.substring(45, 70);
		}
		context.setData("remarkData48", rmkNew);

		log.info("==============start to call third cancle,dateMap=" + context.getDataMap());
		return null;
	}
}
