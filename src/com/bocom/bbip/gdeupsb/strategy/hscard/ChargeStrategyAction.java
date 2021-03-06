package com.bocom.bbip.gdeupsb.strategy.hscard;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsTransJournal;
import com.bocom.bbip.eups.repository.EupsTransJournalRepository;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.bbip.utils.NumberUtils;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
import com.bocom.jump.bp.core.Executable;

public class ChargeStrategyAction implements Executable {

	private final static Log logger = LogFactory
			.getLog(ChargeStrategyAction.class);

	@Autowired
	BBIPPublicService bbipPublicService;

	@Autowired
	EupsTransJournalRepository eupsTransJournalRepository;

	public void execute(Context context) throws CoreException,
			CoreRuntimeException {
		logger.info("charge strategy start......");

		Date tTxnDte = DateUtils.parse(context.getData("tTxnDte").toString(),
				DateUtils.STYLE_yyyyMMdd);// 第三方交易日期

		context.setData(ParamKeys.THD_TXN_DATE, tTxnDte);

		context.setData(ParamKeys.BK,
				bbipPublicService.getParam("GDEUPSB", "THD_BR")); // 分行号
		context.setData(ParamKeys.BR,
				bbipPublicService.getParam("GDEUPSB", "HSBR")); // 机构号
		String teller = bbipPublicService.getETeller(context.getData(
				ParamKeys.BK).toString());
		context.setData(ParamKeys.TELLER, teller);

		logger.info("get eTeller after:" + teller);
		context.setData(ParamKeys.BUS_TYP, Constants.BUS_TYP_2); // 待缴
		// 检查报文是否重复
		EupsTransJournal eups = new EupsTransJournal();
		eups.setBakFld1((String) context.getData(ParamKeys.BAK_FLD1));// 备用字段1，学校代号。
		eups.setThdTxnDte(tTxnDte); // 第三方交易日期
		eups.setThdSqn((String) context.getData(ParamKeys.THD_SEQUENCE));// 此处的第三方流水号即校方流水号，请求方流水号也赋值为校方流水号。
		List<EupsTransJournal> journal = eupsTransJournalRepository.find(eups);
		if (!CollectionUtils.isEmpty(journal)) {

			context.setData(ParamKeys.RSP_CDE, ErrorCodes.EUPS_SQN_IS_EXIST);
			context.setData("responseMessage", "交易流水已存在");
			throw new CoreException(ErrorCodes.EUPS_SQN_IS_EXIST);
		}
		
		// 金额控制，不能超过400.但标准版交易流水表中有交易金额和请求交易金额两个字段，应该用交易金额。
//		double i = Double.parseDouble(context.getData(ParamKeys.TXN_AMT)
//				.toString());
//		double d = i / 100;
//		DecimalFormat df = new DecimalFormat("#.00");
//		BigDecimal txnAmt = new BigDecimal(df.format(d));
//		BigDecimal d = new BigDecimal(context.getData(ParamKeys.TXN_AMT).toString());
		BigDecimal txnAmt = NumberUtils.centToYuan(context.getData(ParamKeys.TXN_AMT).toString());
		context.setData(ParamKeys.TXN_AMT, txnAmt);
		if ((txnAmt.compareTo(new BigDecimal(400))) > 0) {

			context.setData(ParamKeys.RSP_CDE,
					ErrorCodes.EUPS_CHECK_TXN_AMT_FAIL);
			context.setData(ParamKeys.RSP_MSG, "金额超限");
			throw new CoreException(ErrorCodes.EUPS_LIM_AMT_FULL);
		}
	}
}
