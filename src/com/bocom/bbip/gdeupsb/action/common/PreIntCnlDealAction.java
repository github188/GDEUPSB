package com.bocom.bbip.gdeupsb.action.common;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.taglibs.standard.tag.el.sql.SetDataSourceTag;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsThdTranCtlInfo;
import com.bocom.bbip.eups.entity.EupsTransJournal;
import com.bocom.bbip.eups.repository.EupsThdTranCtlInfoRepository;
import com.bocom.bbip.eups.repository.EupsTransJournalRepository;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDErrorCodes;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GdElecClrInf;
import com.bocom.bbip.gdeupsb.repository.GdElecClrInfRepository;
import com.bocom.bbip.gdeupsb.utils.CodeSwitchUtils;
import com.bocom.bbip.utils.CollectionUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

/**
 * 抹帐初始化处理，根据会计流水号查询原交易流水号
 * 
 * @author qc.w
 * 
 */
public class PreIntCnlDealAction extends BaseAction {

	@Autowired
	EupsTransJournalRepository eupsTransJournalRepository;

	@Autowired
	BBIPPublicService bbipPublicService;

	@Autowired
	EupsThdTranCtlInfoRepository eupsThdTranCtlInfoRepository;

	@Autowired
	GdElecClrInfRepository gdElecClrInfRepository;

	@Override
	public void execute(Context context) throws CoreException, CoreRuntimeException {
		log.info("PreIntCnlDealAction start!..");

		String dptTyp = context.getData(GDParamKeys.GZ_ELE_DPT_TYP);
		String comNo = CodeSwitchUtils.codeGenerator("eleGzComNoGen", dptTyp);

		log.info("comNo change from [" + dptTyp + "] to[" + comNo + "]");

		// 检查签到签退
		EupsThdTranCtlInfo eupsThdTranCtlInfo = eupsThdTranCtlInfoRepository.findOne(comNo);
		if (!eupsThdTranCtlInfo.isTxnCtlStsSignin()) {
			throw new CoreException(ErrorCodes.THD_CHL_TRADE_NOT_ALLOWWED);
		}
		log.info("已签到，可以进行业务");

		// 获取电力清算信息表信息
		String clrDatStr = new String();

		GdElecClrInf gdElecClrInf = new GdElecClrInf();
		gdElecClrInf.setBrNo(GDConstants.GZ_ELE_BK_GZ);
		List<GdElecClrInf> gdElecClrInfList = gdElecClrInfRepository.find(gdElecClrInf);
		if (CollectionUtils.isEmpty(gdElecClrInfList)) {
			log.error("不存在清算参数信息");
			throw new CoreException(GDErrorCodes.EUPS_ELE_GZ_CLEAR_INFO_ERROR); // 不存在清算参数信息
		}
		else {
			gdElecClrInf = gdElecClrInfList.get(0);
			// 获取与第三方约定的第三方会计日期
			clrDatStr = gdElecClrInf.getClrDat();
			context.setData(ParamKeys.BAK_FLD1, clrDatStr);
		}

		// 若当前日期与第三方清算日期不一致，表示已进行清算，不允许发起交易
		if (!DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMdd).equals(clrDatStr)) {
			throw new CoreException(GDErrorCodes.EUPS_ELE_GZ_ALREADY_CLEAR_ERROR);
		}

		String bk = context.getData(ParamKeys.BK);

		// TODO:为了测试，先注释，授权原因码并校验是否已授权
		// String authTlr = context.getData(ParamKeys.AUTHOR_LEVEL);
		// if (StringUtils.isEmpty(authTlr)) {
		// throw new CoreException(ErrorCodes.EUPS_CANCEL_CHECK_AUTH_FAIL);
		// }
		// 授权原因码 EFE000
		context.setData(GDConstants.AUTH_REASON, "EFE000");

		String authTlr = "4413913";
		context.setData("athTlrNo", authTlr);
		context.setData("athTlr", authTlr);

		// 校验凭单号是否存在，此处用THD_SUB_CUS_NO作为凭单号
		// String tckNo = context.getData(ParamKeys.THD_SUB_CUS_NO);
		// if (StringUtils.isEmpty(tckNo)) {
		// throw new CoreException(GDErrorCodes.EUPS_ELE_GZ_TCK_ERROR);
		// }

		log.info("new third clrDatStr=" + clrDatStr);

		EupsTransJournal eupsTransJournal = new EupsTransJournal();
		eupsTransJournal.setComNo(comNo); // 单位编号
		eupsTransJournal.setTxnDte(DateUtils.parse(clrDatStr)); // 与第三方约定的清算日期
		eupsTransJournal.setBk(bk); // 分行号
		eupsTransJournal.setMfmVchNo((String) context.getData("oldMfmVchNo")); // 原会计流水号

		List<EupsTransJournal> resultInf = eupsTransJournalRepository.find(eupsTransJournal);
		if (CollectionUtils.isNotEmpty(resultInf)) {
			eupsTransJournal = resultInf.get(0);
			context.setData(ParamKeys.OLD_TXN_SEQUENCE, eupsTransJournal.getSqn());

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

		BigDecimal fTxnAmt = (BigDecimal) context.getData(ParamKeys.TXN_AMT);
		if (null != fTxnAmt) {
			if (0 != eupsTransJournal.getTxnAmt().compareTo(fTxnAmt)) {
				throw new CoreException(GDErrorCodes.EUPS_ELE_GZ_CANCLE_INFO_ERROR);
			}
		}
		context.setVariable(GDParamKeys.GZ_ELE_CANCLE_OLD_JNL, eupsTransJournal);

		// context.setData(GDParamKeys.GZ_ELE_THD_DPT_TYP, comNo);
		// tradeTxnDir
		context.setData("tradeTxnDir", "OL");
	}

}
