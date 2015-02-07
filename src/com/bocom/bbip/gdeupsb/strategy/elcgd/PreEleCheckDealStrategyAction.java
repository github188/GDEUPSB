package com.bocom.bbip.gdeupsb.strategy.elcgd;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.utils.NumberUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
import com.bocom.jump.bp.core.Executable;

/**
 * 第三方发起的划扣
 * 
 * @author qc.w
 * 
 */
public class PreEleCheckDealStrategyAction implements Executable {
	private final static Logger log = LoggerFactory.getLogger(PreEleCheckDealStrategyAction.class);

	@Override
	public void execute(Context context) throws CoreException, CoreRuntimeException {
		log.info("PreEleCheckDealStrategyAction start!..");

		String txnAmt = context.getData("thdTxnAmt");
		BigDecimal realAmt = NumberUtils.centToYuan(txnAmt);
		context.setData(ParamKeys.TXN_AMOUNT, realAmt);
		
		context.setData(ParamKeys.THD_TXN_CDE, "HK");  //设置第三方交易码为划扣，用于对账
		
		//第48域值分解，获取客户编号，电费月份，产品代码，原系统参考号
		String rmkDte=context.getData("remarkData");
		String thdCusNo=rmkDte.substring(0, 21);
		String eleMonth=rmkDte.substring(22, 28);
		
		context.setData(ParamKeys.THD_CUS_NO, thdCusNo);  //第三方客户标志
		context.setData(ParamKeys.BAK_FLD2, eleMonth);  //设置备用字段为电费月份
		

	}
}
