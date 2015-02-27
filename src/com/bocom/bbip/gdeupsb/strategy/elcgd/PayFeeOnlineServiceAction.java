package com.bocom.bbip.gdeupsb.strategy.elcgd;

import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsAmountInfo;
import com.bocom.bbip.eups.entity.EupsThdTranCtlInfo;
import com.bocom.bbip.eups.repository.EupsThdTranCtlInfoRepository;
import com.bocom.bbip.eups.spi.service.online.PayFeeOnlineService;
import com.bocom.bbip.eups.spi.vo.CommHeadDomain;
import com.bocom.bbip.eups.spi.vo.PayFeeOnlineDomain;
import com.bocom.bbip.eups.spi.vo.PayFeeOnlineRetDomain;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.utils.CodeSwitchUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

/**
 * 银行实时缴费交易-单笔缴费
 * 
 * @author qc.w
 * 
 */
public class PayFeeOnlineServiceAction implements PayFeeOnlineService {

	private final static Logger log = LoggerFactory.getLogger(PayFeeOnlineServiceAction.class);

	@Autowired
	EupsThdTranCtlInfoRepository eupsThdTranCtlInfoRepository;

	@Override
	public Map<String, Object> preCheckDeal(CommHeadDomain commheaddomain, PayFeeOnlineDomain payfeeonlinedomain, Context context)
			throws CoreException {
		log.info("PayFeeOnlineServiceAction preCheckDeal start!..");

		// 设置缴费方式为1-缴费
		if (null != context.getData(ParamKeys.OLD_TXN_SEQUENCE)) {
			context.setData(ParamKeys.PAYFEE_TYPE, Constants.TXN_PAYFEE_TYPE_PAYMENT);
		}

		// 设置备用字段2为电费月份，保存入库
		context.setData(ParamKeys.BAK_FLD2, context.getData("eleMonth"));

		// TODO:该字段待考虑从哪来
		// 设置备用字段3为产品代码，保存入库
		context.setData(ParamKeys.BAK_FLD3, context.getData("prdCde"));

		String dpTyp = context.getData(GDParamKeys.GZ_ELE_DPT_TYP);
		String comNo = CodeSwitchUtils.codeGenerator("eleGzComNoGen", dpTyp);

		log.info("after codeSwitch, dptTyp change from [" + dpTyp + "],to [" + comNo + "]");

		// TODO:支付方式paymde判断:目前根据是否传客户帐号分为现金和卡
		String cusAc = context.getData(ParamKeys.CUS_AC); // 客户帐号判断
		String payMde = new String();
		if (StringUtils.isEmpty(cusAc)) {
			payMde = Constants.PAY_MDE_0; // 现金
		} else {
			payMde = Constants.PAY_MDE_4; // 卡
		}
		context.setData(ParamKeys.PAY_MDE, payMde);

		// 检查签到签退
		EupsThdTranCtlInfo eupsThdTranCtlInfo = eupsThdTranCtlInfoRepository.findOne(comNo);
		if (!eupsThdTranCtlInfo.isTxnCtlStsSignin()) {
			throw new CoreException(ErrorCodes.THD_CHL_TRADE_NOT_ALLOWWED);
		}
		log.info("已签到，可以进行业务");

		return null;
	}

	@Override
	public Map<String, Object> preHostDeal(CommHeadDomain commheaddomain, PayFeeOnlineDomain payfeeonlinedomain, Context context)
			throws CoreException {

		log.info("PayFeeOnlineServiceAction preHostDeal start!..");

		String realSplNo = null;
		// 查找单位编号
		// EupsActSysPara eupsActSysPara = new EupsActSysPara();
		// eupsActSysPara.setComNo((String)
		// context.getData(ParamKeys.COMPANY_NO));
		// List<EupsActSysPara> resultList =
		// eupsActSysParaRepository.find(eupsActSysPara);
		// if (CollectionUtils.isNotEmpty(resultList)) {
		// eupsActSysPara = resultList.get(0);
		// realSplNo = eupsActSysPara.getSplNo();
		// } else {
		// throw new CoreException(ErrorCodes.EUPS_COM_NO_NOTEXIST);
		// }

		context.setData(ParamKeys.THD_TXN_CDE, "JF"); // 第三方交易码设置为缴费，用于对账
		String vchTyp = context.getData(ParamKeys.BV_KIND); // 凭证种类
		// TODO:需要支持以下支付方式:现金缴费,银行卡,活期存折,本外活本,个人支票,本票,现金支票,转账支票,划线支票

		// 对私
		if (GDConstants.GZ_ELE_PAY_KND_CASH.equals(vchTyp) || GDConstants.GZ_ELE_PAY_KND_CARD.equals(vchTyp)
				|| GDConstants.GZ_ELE_PAY_KND_ALVC.equals(vchTyp) || GDConstants.GZ_ELE_PAY_KND_AOVC.equals(vchTyp)
				|| GDConstants.GZ_ELE_PAY_KND_PRCK.equals(vchTyp))
		{
			context.setData("rmkCde", "9102"); // 备注
		}
		else { // 对公
			context.setData(ParamKeys.BUSS_KIND, GDConstants.GZ_ELE_BUS_KND_ELEC); // TODO:业务种类：电费,此处需要修改，老的是CRP51，数据库中长度不足
			context.setData(ParamKeys.BUS_TYP, Constants.BUS_TYP_2); // 业务类型：代缴
			// context.setData("rmkCde", "代扣电费"); //备注
			context.setData("bakFld1", "代扣电费"); // TODO:以前的摘要码是代扣电费，现在的摘要码为4位，故使用备注
		}
		context.setData(ParamKeys.CCY, Constants.CCY_CDE_CNY); // 币种

		// TODO: 用第三方地区编号当作配型部类型，存入数据库
		context.setData("dptTyp", context.getData(ParamKeys.THD_REGION_NO));

		// 原代码中的渠道号转换不需要做

		// TODO:TRM和D441渠道启动完整性检查，其他渠道是否超时不冲正？目前标准版超时都会去冲正，请确认 vipQc
		// <Set>inTxnCnl=STRCAT(|,$TxnCnl,|)</Set>
		// <If
		// condition="AND(IS_EQUAL_STRING(@PARA.RollBack,1),INTCMP(GETSTRPOS(@PARA.RollBackCnls,$inTxnCnl),5,0))">
		// <Exec func="PUB:BeginWork"/> <!--启动完整性控制-->
		// </If>

		return null;
	}

	@Override
	public Map<String, Object> preThdDeal(CommHeadDomain commheaddomain, PayFeeOnlineDomain payfeeonlinedomain, EupsAmountInfo eupsamountinfo,
			Context context) throws CoreException {
		log.info("PreThdElecStrategyAction start!..");

		// <Set>TraTyp=JF</Set>
		// <Set>TxnAmt=ADDCHAR(DELSPACE($TxnAmt,all),12,0,1)</Set> <!--交易金额-->
		// <Set>Fee=ADDCHAR(DELSPACE($Fee,all),12,0,1)</Set> <!--手续费-->
		// <Set>TCusId=ADDCHAR(DELSPACE($TCusId,all),21, ,1)</Set> <!--客户编号-->
		// <Set>ChkNum=ADDCHAR(DELSPACE($ChkNum,all),25, ,1)</Set> <!--支票号码-->
		// <Set>OData=STRCAT(ADDCHAR($TCusId,21,
		// ,1),$LChkTm,01,SPACE(12),$PayTyp,ADDCHAR($ChkNum,25, ,1))</Set>
		// <!--附加数据-->
		// <Set>RsFld1=$OData</Set> <!--数据备用-->
		// <Set>MacFlag=0</Set> <!--发送MAC生成-->
		// <Quote name="MakeMac"/>
		// <Set>MsgMac=$MAC</Set>

		context.setData(GDParamKeys.GZ_ELE_CUS_AC, context.getData(ParamKeys.CUS_AC)); // 客户帐号
		context.setData(ParamKeys.MESSAGE_TYPE, "0200");
		context.setData("amount4", context.getData(ParamKeys.TXN_AMOUNT)); // 交易金额
		String sqn = context.getData(ParamKeys.SEQUENCE);

		context.setData("transJournal11", sqn.substring(sqn.length() - 11)); // 银行方流水号
		context.setData("bnkTxnTime12", DateUtils.format(new Date(), DateUtils.STYLE_HHmmss)); // 银行方流水号

		String ttrmId = (String) context.getData(ParamKeys.TERMINAL);
		ttrmId = StringUtils.rightPad(ttrmId, 8, ' ');

		String crpID = context.getData(ParamKeys.TELLER);
		crpID = StringUtils.rightPad(crpID, 15, ' ');

		context.setData(GDParamKeys.GZ_ELE_DEAL_CODE, GDConstants.GZ_ELE_DEAL_CODE);
		context.setData(GDParamKeys.GZ_ELE_RCS_NO, GDConstants.GZ_ELE_DEAL_ORG_CODE);
		context.setData(GDParamKeys.GZ_ELE_CCY_COD, GDConstants.GZ_ELE_CCY);
		context.setData(GDParamKeys.GZ_ELE_TTRM_ID, ttrmId);
		context.setData(GDParamKeys.GZ_ELE_TDL_ID, crpID);

		// TODO:电力方清算日期
		String eleTxnDteStr = DateUtils.format(new Date(), "MMDD");
		context.setData(GDParamKeys.GZ_ELE_TXN_DTE, eleTxnDteStr);

		// TODO
		// 48域附加数据设置
		// <Set>OData=STRCAT(ADDCHAR($TCusId,21,
		// ,1),$LChkTm,01,SPACE(12),$PayTyp,ADDCHAR($ChkNum,25, ,1))</Set>
		// <!--附加数据-->
		// <Set>RsFld1=$OData</Set> <!--数据备用-->
		// <Set>MacFlag=0</Set> <!--发送MAC生成-->
		// <Quote name="MakeMac"/>
		// <Set>MsgMac=$MAC</Set>
		return null;
	}

	@Override
	public Map<String, Object> aftThdDeal(CommHeadDomain commheaddomain, PayFeeOnlineRetDomain payfeeonlineretdomain, Context context)
			throws CoreException {

		log.info("PayFeeOnlineServiceAction aftThdDeal start!..");
		// TODO:确认是否还有其他功能，字段设置等

		return null;
	}

}
