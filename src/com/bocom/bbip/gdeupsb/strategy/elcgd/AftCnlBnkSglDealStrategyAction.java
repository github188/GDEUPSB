package com.bocom.bbip.gdeupsb.strategy.elcgd;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
import com.bocom.jump.bp.core.Executable;

/**
 * 银行方单边抹帐，抹帐后处理
 * 
 * @author qc.w
 * 
 */
public class AftCnlBnkSglDealStrategyAction implements Executable {

	private final static Logger log = LoggerFactory.getLogger(AftCnlBnkSglDealStrategyAction.class);

	@Override
	public void execute(Context context) throws CoreException, CoreRuntimeException {
		log.info("AftCnlBnkSglDealStrategyAction start!..");

		// 获取sqn，经过处理后作为银行方流水号发送给第三方
		String transJnl = context.getData(ParamKeys.SEQUENCE);
		transJnl = transJnl.substring(2, 8) + transJnl.substring(transJnl.length() - 4, transJnl.length());

		log.info("after sub deal,transJnl=" + transJnl);
		context.setData("transJournal11", transJnl);

		Date bnkTxnTime = new Date();

		String bnkTxnTme = DateUtils.format(bnkTxnTime, DateUtils.STYLE_HHmmss);
		context.setData("bnkTxnTime12", bnkTxnTme);

		String bnkTxnDte = DateUtils.format(bnkTxnTime, DateUtils.STYLE_MMdd);
		context.setData("bnkTxnDate13", bnkTxnDte);

		String thdRspCd = context.getData(ParamKeys.REVERSE_RESULT_CODE); // 冲正返回码
		if (Constants.RESPONSE_CODE_SUCC_HOST.equals(thdRspCd)) {
			context.setData(ParamKeys.RESPONSE_CODE, "CNLSC00"); // 冲正成功
		}
		else {
			context.setData(ParamKeys.RESPONSE_CODE, "CNLER00"); // 冲正不成功
		}
		log.info("#######thdRspCde= " + thdRspCd);

	}
}
