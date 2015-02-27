//package com.bocom.bbip.gdeupsb.strategy.elcgd;
//
//import java.math.BigDecimal;
//import java.util.Date;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import com.bocom.bbip.eups.common.ParamKeys;
//import com.bocom.bbip.utils.DateUtils;
//import com.bocom.bbip.utils.NumberUtils;
//import com.bocom.jump.bp.core.Context;
//import com.bocom.jump.bp.core.CoreException;
//import com.bocom.jump.bp.core.CoreRuntimeException;
//import com.bocom.jump.bp.core.Executable;
//
//public class AftPayDealStrategyAction implements Executable {
//
//	private final static Logger log = LoggerFactory.getLogger(AftPayDealStrategyAction.class);
//
//	@Override
//	public void execute(Context context) throws CoreException, CoreRuntimeException {
//		log.info("AftPayDealStrategyAction start!..");
//		context.setData("eleBkNo", context.getData(ParamKeys.MFM_VCH_NO)); // TODO:此处用会计流水号代替银行方交易流水号，确认是否大于11位，若大于的话，则考虑截取sqn！
//		Date bnkTxnDate = context.getData("acDte");
//		context.setData("bnkTxnTime12", DateUtils.format(new Date(), DateUtils.STYLE_HHmmss));
//		context.setData("bnkTxnDate13", DateUtils.format(bnkTxnDate, DateUtils.STYLE_MMdd));
//
//		// TODO:第三方返回码转换(将主机的返回码转化为第三方需要的返回码)
//		context.setData("thdRspCde", "00");
//
//	}
//
//}
