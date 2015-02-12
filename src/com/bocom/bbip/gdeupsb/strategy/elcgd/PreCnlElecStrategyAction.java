package com.bocom.bbip.gdeupsb.strategy.elcgd;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsTransJournal;
import com.bocom.bbip.eups.repository.EupsTransJournalRepository;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDErrorCodes;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.utils.CollectionUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.bbip.utils.NumberUtils;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
import com.bocom.jump.bp.core.Executable;

/**
 * 抹帐
 * 
 * @author qc.w
 * 
 */
public class PreCnlElecStrategyAction implements Executable {

	private final static Logger log = LoggerFactory.getLogger(PreCnlElecStrategyAction.class);

	@Autowired
	EupsTransJournalRepository eupsTransJournalRepository;

	@Override
	public void execute(Context context) throws CoreException, CoreRuntimeException {
		log.info("PreCnlElecStrategyAction start!..");
		
		//TODO:配型部类型使用第三方地区编号，有问题
		String dptTyp = context.getData(GDParamKeys.GZ_ELE_DPT_TYP);
		// String comNo = CodeSwitchUtils.codeGenerator("eleTable", dptTyp);

		// TODO: vipQc for test:codeSwitchUtils不能用！ 待修改完善
		String comNo = "ELEC01";

		// String comNo=context.getData(ParamKeys.COMPANY_NO);
		String bk = context.getData(ParamKeys.BK);

		// 授权原因码并校验是否已授权
		String authTlr = context.getData(ParamKeys.AUTHOR_LEVEL);
		if (StringUtils.isEmpty(authTlr)) {
			throw new CoreException(ErrorCodes.EUPS_CANCEL_CHECK_AUTH_FAIL);
		}
		context.setData(GDConstants.AUTH_REASON, "EFE000");// 授权原因码 EFE000

		// 校验凭单号是否存在，此处用THD_SUB_CUS_NO作为凭单号
		String tckNo = context.getData(ParamKeys.THD_SUB_CUS_NO);
		if (StringUtils.isEmpty(tckNo)) {
			throw new CoreException(GDErrorCodes.EUPS_ELE_GZ_TCK_ERROR);
		}
		EupsTransJournal eupsTransJournal = new EupsTransJournal();
		eupsTransJournal.setThdSubCusNo(tckNo);
		eupsTransJournal.setComNo(comNo);
		eupsTransJournal.setTxnDte(new Date());
		eupsTransJournal.setBk(bk);
		eupsTransJournal.setSqn((String)context.getData(ParamKeys.OLD_TXN_SEQUENCE));
		List<EupsTransJournal> resultInf = eupsTransJournalRepository.find(eupsTransJournal);
		if (CollectionUtils.isNotEmpty(resultInf)) {
			eupsTransJournal = resultInf.get(0);
			if (Constants.TRADE_STATUS_CANCEL_SUCCESS.equals(eupsTransJournal.getTxnSts())) {
				throw new CoreException(GDErrorCodes.EUPS_ELE_GZ_JNL_ALREADY_CANCLE); // 已抹帐
			}
		} else {
			throw new CoreException(ErrorCodes.BIZ_ERROR_CANCEL_PAYMENT_ORIGINAL_PAYMENT_JOURNAL_NOT_FOUND); // 已抹帐
		}

		String thdCusNo = context.getData(ParamKeys.THD_CUS_NO);
		if (StringUtils.isNotEmpty(thdCusNo)) {
			if (!eupsTransJournal.getThdCusNo().trim().equals(thdCusNo.trim())) {
				throw new CoreException(GDErrorCodes.EUPS_ELE_GZ_CANCLE_INFO_ERROR); 
			}
		}
		String thdSubCusNo = context.getData(ParamKeys.THD_SUB_CUS_NO);
		if (StringUtils.isNotEmpty(thdSubCusNo)) {
			if (!eupsTransJournal.getThdSubCusNo().trim().equals(thdSubCusNo.trim()) ) {
				throw new CoreException(GDErrorCodes.EUPS_ELE_GZ_CANCLE_INFO_ERROR); 
			}
		}
		BigDecimal fTxnAmt = (BigDecimal) context.getData(ParamKeys.TXN_AMT);
		if (null != fTxnAmt) {
			if (0!=eupsTransJournal.getTxnAmt().compareTo(fTxnAmt)) {
				throw new CoreException(GDErrorCodes.EUPS_ELE_GZ_CANCLE_INFO_ERROR); 
			}
		}

		// TODO：清算信息表生成，校验清算时间是否为当日，若是当日则不允许继续交易（已清算,不能抹帐）
		// TODO：抹帐账户信息设置(此处待考虑，标准版本中冲正通过原交易流水号直接冲正，不需要进行帐号等信息设置，等于本策略只处理通知第三方的操作)

		context.setData(GDParamKeys.GZ_ELE_DEAL_CODE, GDConstants.GZ_ELE_DEAL_CODE);
		context.setData(GDParamKeys.GZ_ELE_RCS_NO, GDConstants.GZ_ELE_DEAL_ORG_CODE);
		context.setData(GDParamKeys.GZ_ELE_RSC_ORG, GDConstants.GZ_ELE_RECEIVE_ORG_CODE);
		context.setData(GDParamKeys.GZ_ELE_CCY_COD, GDConstants.GZ_ELE_CCY);

		String ttrmId = (String) context.getData(ParamKeys.BBIP_TERMINAL_NO);
		ttrmId = StringUtils.rightPad(ttrmId, 8, ' ');

		String crpID = context.getData(ParamKeys.TELLER);
		crpID = StringUtils.rightPad(crpID, 15, ' ');

		context.setData(ParamKeys.MESSAGE_TYPE, "0410");
		context.setData(GDParamKeys.GZ_ELE_CUS_AC, eupsTransJournal.getCusAc());
		context.setData(GDParamKeys.GZ_ELE_THD_PAY_TYP, GDConstants.GZ_ELE_DEAL_CODE);
		context.setData("amount4", eupsTransJournal.getTxnAmt());
		
		String sqn=context.getData(ParamKeys.SEQUENCE);
		context.setData("transJournal11", sqn.substring(sqn.length()-12));
		
		Date bnkTxnTime=context.getData(ParamKeys.TXN_TIME);
		
		String bnkTxnTme=DateUtils.format(bnkTxnTime, DateUtils.STYLE_HHmmss);
		context.setData("bnkTxnTime12", bnkTxnTme);
		
		
		String bnkTxnDte=DateUtils.format(bnkTxnTime, DateUtils.STYLE_MMdd);
		context.setData("bnkTxnDate13", bnkTxnDte);
		
		
		context.setData(GDParamKeys.GZ_ELE_THD_DPT_TYP, eupsTransJournal.getThdRgnNo());
		
		BigDecimal fee=(BigDecimal)eupsTransJournal.getHfe();
		if(null==fee){
			fee=new BigDecimal("0.00");
		}
		context.setData("pwrFee28", NumberUtils.yuanToCentString(fee));
		
		
		context.setData(GDParamKeys.GZ_ELE_TTRM_ID, ttrmId);
		context.setData(GDParamKeys.GZ_ELE_TDL_ID, crpID);
		
		
		context.setData(GDParamKeys.GZ_ELE_TRA_TYP, GDConstants.GZ_ELE_TXN_TYP_JF);

		BigDecimal txnAmt = (BigDecimal) context.getData(ParamKeys.TXN_AMOUNT);

		String ttxnAmt = NumberUtils.yuanToCentString(txnAmt); // 金额转化为元
		ttxnAmt = StringUtils.leftPad(ttxnAmt, 12, '0'); // 左拼接0

		// <Set>ChkTim=GETDATETIME(MMDDHHMISS)</Set> <!--交易日期时间-->
		// <If condition="IS_EQUAL_STRING(SUBSTR($RsFld1,44,2),10)">
		// <Set>OData=STRCAT(SUBSTR($RsFld1,1,31),SUBSTR($OLogNo,3,12),SUBSTR($RsFld1,44,2),SPACE(25))</Set>
		// <!--附加数据-->
		// </If>
		// <Else>
		// <Set>OData=STRCAT(SUBSTR($RsFld1,1,31),SUBSTR($OLogNo,3,12),SUBSTR($RsFld1,44,2),ADDCHAR(SUBSTR($RsFld1,46,25),25,
		// ,1))</Set> <!--附加数据-->
		// </Else>

		String tFee = NumberUtils.yuanToCentString(eupsTransJournal.getHfe());
		tFee = StringUtils.leftPad(tFee, 12, '0');

		context.setData("pwrFee", tFee);
		context.setData("txnDateTime", DateUtils.formatAsMMddHHmmss(new Date()));

		// TODO:备用字段及mac处理
		// String rmk1=oldJnlInf.getRsvFld1();
		// if("10".equals(rmk1.substring(43, 45))){
		//
		// }
		// <If condition="IS_EQUAL_STRING(SUBSTR($RsFld1,44,2),10)">
		// <Set>OData=STRCAT(SUBSTR($RsFld1,1,31),SUBSTR($OLogNo,3,12),SUBSTR($RsFld1,44,2),SPACE(25))</Set>
		// <!--附加数据-->
		// </If>
		// <Else>
		// <Set>OData=STRCAT(SUBSTR($RsFld1,1,31),SUBSTR($OLogNo,3,12),SUBSTR($RsFld1,44,2),ADDCHAR(SUBSTR($RsFld1,46,25),25,
		// ,1))</Set> <!--附加数据-->
		// </Else>
	}
}
