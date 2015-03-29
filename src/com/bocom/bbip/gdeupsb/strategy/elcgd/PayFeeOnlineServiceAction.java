package com.bocom.bbip.gdeupsb.strategy.elcgd;

import java.util.Date;
import java.util.List;
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

	@Autowired
	GdElecClrInfRepository gdElecClrInfRepository;

	// @Autowired
	// CallHostByComNo callHostByComNo;

	@Override
	public Map<String, Object> preCheckDeal(CommHeadDomain commheaddomain, PayFeeOnlineDomain payfeeonlinedomain, Context context)
			throws CoreException {
		log.info("PayFeeOnlineServiceAction preCheckDeal start!..");

		context.setData("extFields", "01441800999");

		// 根据单位编号查询记账网店
		// callHostByComNo.callHost(context);

		context.setData(ParamKeys.BUS_TYP, Constants.BUS_TYP_2); // 2-代缴

		String dpTyp = context.getData(GDParamKeys.GZ_ELE_DPT_TYP);
		String comNo = CodeSwitchUtils.codeGenerator("eleGzComNoGen", dpTyp);
		log.info("after codeSwitch, dptTyp change from [" + dpTyp + "],to [" + comNo + "]");
		if (StringUtils.isEmpty(comNo)) {
			throw new CoreException(ErrorCodes.EUPS_COM_NO_NOTEXIST);
		}

		// 检查签到签退
		EupsThdTranCtlInfo eupsThdTranCtlInfo = eupsThdTranCtlInfoRepository.findOne(comNo);
		if (!eupsThdTranCtlInfo.isTxnCtlStsSignin()) {
			throw new CoreException(ErrorCodes.THD_CHL_TRADE_NOT_ALLOWWED);
		}
		log.info("已签到，可以进行业务");

		// 获取电力清算信息表信息
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
			String clrDatStr = gdElecClrInf.getClrDat();
			context.setData(ParamKeys.BAK_FLD1, clrDatStr);
		}

		// 缴费方式为充值
		// if
		// (StringUtils.isNotEmpty((String)context.getData(ParamKeys.OLD_TXN_SEQUENCE)))
		// {
		// context.setData(ParamKeys.PAYFEE_TYPE,
		// Constants.TXN_PAYFEE_TYPE_PAYMENT);
		// } else {
		// TODO:for test，真实环境应校验当日该缴费号是否已完成缴费，不允许重复缴费
		context.setData(ParamKeys.PAYFEE_TYPE, Constants.TXN_PAYFEE_TYPE_RECHARGE);
		// }

		// 设置备用字段2为电费月份，保存入库
		context.setData(ParamKeys.BAK_FLD2, context.getData("lChkTm"));

		// 设置备用字4为产品代码，保存入库,此处设置为01
		context.setData(ParamKeys.BAK_FLD4, "01");

		context.setData(ParamKeys.CCY, Constants.CCY_CDE_CNY); // 币种

		// 结算凭证处理,根据结算凭证转换对应的收费类型
		String thdFeeWay = new String();
		String vchTyp = context.getData("vchTyp");
		String actFlg = new String();

		if ("000".equals(vchTyp) || "007".equals(vchTyp) || "704".equals("vchTyp") || "722".equals(vchTyp)) {
			thdFeeWay = GDConstants.THD_PAY_TYP_CASH; // 收费类型:10现金;11支票预交;12支票实交
			actFlg = "DS"; // 对私
			log.info("该帐号为对私帐号");
		} else if ("021".equals(vchTyp) || "117".equals(vchTyp) || "105".equals(vchTyp) || "113".equals(vchTyp) || "115".equals(vchTyp)) {
			thdFeeWay = GDConstants.THD_PAY_TYP_RLCK;
			actFlg = "DG"; // 对公
			log.info("该帐号为对公帐号");
		}

		// 将对公对私标志设置为备用字段6
		context.setData(ParamKeys.BAK_FLD6, actFlg);
		// 凭证种类截取后两位
		if (StringUtils.isNotEmpty(vchTyp)) {
			context.setData("bvKnd", vchTyp.substring(1, 3));
		}

		String payMde = new String();
		if ("000".equals(vchTyp)) {
			payMde = Constants.PAY_MDE_0; // 现金
			context.setData(ParamKeys.AC_TYP, "00");
		} else if ("007".equals(vchTyp)) {
			payMde = Constants.PAY_MDE_4; // 卡
		} else {
			// TODO:默认为现金
			payMde = Constants.PAY_MDE_0; // 现金
			context.setData(ParamKeys.AC_TYP, "00");
		}

		context.setData(ParamKeys.PAY_MDE, payMde);

		context.setData("bvNo", context.getData("vchNo")); // 凭证号码
		context.setData(ParamKeys.BAK_FLD5, context.getData("bilDte")); // 凭证日期

		log.info("====================收费类型为：" + thdFeeWay);
		context.setData(GDParamKeys.GZ_ELE_FEE_WAY, thdFeeWay); // 收费类型

		context.setData(ParamKeys.COMPANY_NO, comNo); // 单位编号

		return null;
	}

	@Override
	public Map<String, Object> preHostDeal(CommHeadDomain commheaddomain, PayFeeOnlineDomain payfeeonlinedomain, Context context)
			throws CoreException {

		log.info("PayFeeOnlineServiceAction preHostDeal start!..");

		String comNo = context.getData(ParamKeys.COMPANY_NO); // 获取单位编号

		context.setData(ParamKeys.THD_TXN_CDE, "JF"); // 第三方交易码设置为缴费，用于对账
		String vchTyp = context.getData(ParamKeys.BV_KIND); // 凭证种类
		// TODO:需要支持以下支付方式:现金缴费,银行卡,活期存折,本外活本,个人支票,本票,现金支票,转账支票,划线支票
		// 对私
		// if (GDConstants.GZ_ELE_PAY_KND_CASH.equals(vchTyp) ||
		// GDConstants.GZ_ELE_PAY_KND_CARD.equals(vchTyp)
		// || GDConstants.GZ_ELE_PAY_KND_ALVC.equals(vchTyp) ||
		// GDConstants.GZ_ELE_PAY_KND_AOVC.equals(vchTyp)
		// || GDConstants.GZ_ELE_PAY_KND_PRCK.equals(vchTyp))
		// {
		// context.setData("rmkCde", "9102"); // 备注
		// }
		// else { // 对公
		// // TODO:业务种类：电费,此处需要修改，老的是CRP51，数据库中长度不足
		// //context.setData(ParamKeys.BUSS_KIND,
		// GDConstants.GZ_ELE_BUS_KND_ELEC);
		// context.setData(ParamKeys.BUS_TYP, Constants.BUS_TYP_2); // 业务类型：代缴
		// // context.setData("rmkCde", "代扣电费"); //备注
		// }

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

		context.setData(ParamKeys.MESSAGE_TYPE, "0200");
		context.setData(GDParamKeys.GZ_ELE_CUS_AC, context.getData(ParamKeys.CUS_AC)); // 客户帐号
		context.setData("thdPayTyp3", "020000");
		context.setData("amount4", context.getData(ParamKeys.TXN_AMOUNT)); // 交易金额

		String mfmJrnNo = context.getData("acJrnNo"); // 获取主机流水号
		context.setData("mfmJrnNo", mfmJrnNo); // 主机流水号,标准版未计入流水表
		context.setData("transJournal11", mfmJrnNo); // 银行交易流水号,此处用主机流水号代替银行方交易流水号

		Date nowTme = new Date();
		Date bnkTxnDate = context.getData("acDte");
		context.setData("bnkTxnTime12", DateUtils.format(nowTme, DateUtils.STYLE_HHmmss));

		if (null != bnkTxnDate) {
			context.setData("bnkTxnDate13", DateUtils.format(bnkTxnDate, DateUtils.STYLE_MMdd));
		} else {
			context.setData("bnkTxnDate13", DateUtils.format(nowTme, DateUtils.STYLE_MMdd));
		}
		context.setData("pwrFee28", context.getData(ParamKeys.FEE)); // 手续费

		String ttrmId = (String) context.getData(ParamKeys.TERMINAL);
		if (StringUtils.isNotEmpty(ttrmId)) {
			ttrmId = StringUtils.rightPad(ttrmId, 8, ' ');
		} else {
			ttrmId = "        ";
		}

		String crpID = context.getData(ParamKeys.TELLER);
		crpID = StringUtils.rightPad(crpID, 15, ' ');

		context.setData(GDParamKeys.GZ_ELE_RCS_NO, GDConstants.GZ_ELE_DEAL_ORG_CODE);
		context.setData(GDParamKeys.GZ_ELE_CCY_COD, GDConstants.GZ_ELE_CCY);
		context.setData(GDParamKeys.GZ_ELE_TTRM_ID, ttrmId);
		context.setData(GDParamKeys.GZ_ELE_TDL_ID, crpID);

		context.setData(GDParamKeys.GZ_ELE_THD_DPT_TYP, context.getData(GDParamKeys.GZ_ELE_DPT_TYP)); // 配型部类型
		context.setData(GDParamKeys.GZ_ELE_RSC_ORG, GDConstants.GZ_ELE_RECEIVE_ORG_CODE); // 电费接收机构标识码
																							// :333333
		context.setData(GDParamKeys.GZ_ELE_TRA_TYP, "JF");

		StringBuffer rmkDte = new StringBuffer();
		String thdCusNo = context.getData(ParamKeys.THD_CUS_NO); // 客户编号

		String lchkTm = context.getData("lChkTm");
		if (StringUtils.isEmpty(lchkTm)) {
			lchkTm = "999999";
		}
		String vchNo = context.getData("vchNo"); // 凭证号码

		rmkDte.append(StringUtils.leftPad(thdCusNo, 21)).append(lchkTm).append("01").append(StringUtils.leftPad(" ", 12))
				.append(context.getData(GDParamKeys.GZ_ELE_FEE_WAY)).append(StringUtils.leftPad(vchNo, 25));

		context.setData("remarkData48", rmkDte.toString());

		return null;
	}

	@Override
	public Map<String, Object> aftThdDeal(CommHeadDomain commheaddomain, PayFeeOnlineRetDomain payfeeonlineretdomain, Context context)
			throws CoreException {
		log.info("PayFeeOnlineServiceAction aftThdDeal start!..");

		context.setData(ParamKeys.THD_SEQUENCE, context.getData("eleThdSqn37"));

		String clrYear = DateUtils.format(new Date(), "yyyy");

		// 第三方清算日期处理-将第三方清算日期作为第三方交易日期存储
		String eleClrDte = context.getData("pwrtxnDate15");
		if (StringUtils.isNotEmpty(eleClrDte)) {
			eleClrDte = eleClrDte.trim();
			String thdTxnDteStr = clrYear + eleClrDte;
			context.setData(ParamKeys.THD_TXN_DATE, DateUtils.parse(thdTxnDteStr));
		}

		// 第三方交易时间处理
		String eleTxnTme = context.getData("txnDateTime7");
		if (StringUtils.isNotEmpty(eleTxnTme)) {
			String thdTxnTmeStr = clrYear + eleTxnTme;
			context.setData(ParamKeys.THD_TXN_TIME, DateUtils.parse(thdTxnTmeStr, DateUtils.STYLE_yyyyMMddHHmmss));
		}

		// 将附加数据保存到数据库
		context.setData(ParamKeys.RSV_FLD1, context.getData("remarkData48"));

		context.setData("eleClrDte", eleClrDte); // 供电公司清算日期

		return null;
	}

}
