package com.bocom.bbip.gdeupsb.strategy.elcgd;

import java.math.BigDecimal;
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
import com.bocom.bbip.utils.NumberUtils;
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

	@Override
	public Map<String, Object> preCheckDeal(CommHeadDomain commheaddomain, PayFeeOnlineDomain payfeeonlinedomain, Context context)
			throws CoreException {
		log.info("PayFeeOnlineServiceAction preCheckDeal start!..");
		
		context.setData("extFields", "01441800999");

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
		
		log.info("当前要处理的vchTyp=["+vchTyp+"]");
		String payMde = new String();
		if ("000".equals(vchTyp)) {
			payMde = Constants.PAY_MDE_0; // 现金
			context.setData(ParamKeys.AC_TYP, "00");
		} else if ("007".equals(vchTyp)) {
			payMde = Constants.PAY_MDE_4; // 卡
		}else if("115".equals(vchTyp)){   //支票业务
			log.info("当前的vchTyp为115，为支票业务!..");
			context.setData("rsvFld3", vchTyp);
			payMde = Constants.PAY_MDE_8; //对公结算账户 
		}
		else if("704".equals(vchTyp)){
			payMde = Constants.PAY_MDE_2; //对公结算账户 
		}

		context.setData(ParamKeys.PAY_MDE, payMde);

		context.setData("bvNo", context.getData("vchNo")); // 凭证号码
		context.setData(ParamKeys.BAK_FLD5, context.getData("bilDte")); // 凭证日期

		Date bnkTxnDate = new Date();
		context.setData("bnkTxnTime", DateUtils.format(bnkTxnDate, DateUtils.STYLE_HHmmss));
		context.setData("bnkTxnDate", DateUtils.format(bnkTxnDate, DateUtils.STYLE_MMdd));
		
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
		// || GDConstants.GddZ_ELE_PAY_KND_ALVC.equals(vchTyp) ||
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
		return null;
	}

	@Override
	public Map<String, Object> preThdDeal(CommHeadDomain commheaddomain, PayFeeOnlineDomain payfeeonlinedomain, EupsAmountInfo eupsamountinfo,
			Context context) throws CoreException {
		log.info("PreThdElecStrategyAction start!..");

		context.setData(ParamKeys.MESSAGE_TYPE, "0200");
		context.setData(GDParamKeys.GZ_ELE_CUS_AC, context.getData(ParamKeys.CUS_AC)); // 客户帐号
		context.setData("thdPayTyp", "020000");
		
		context.setData("amount", NumberUtils.yuanToCent(context.getData(ParamKeys.TXN_AMOUNT))); // 交易金额

		String mfmJrnNo = context.getData("acJrnNo"); // 获取主机流水号
		context.setData("mfmJrnNo", mfmJrnNo); // 主机流水号,标准版未计入流水表
		context.setData("bakFld7", mfmJrnNo); 
		
		String sqn = context.getData(ParamKeys.SEQUENCE);
		String sqn2 = sqn.substring(sqn.length() - 6, sqn.length());
		context.setData("transJournal", sqn.substring(2, 8) + sqn2); // 银行交易流水号
		context.setData("rsvFld2", sqn.substring(2, 8) + sqn2); // 银行交易流水号,存在rsvFld2中，用于进行抹帐等交易
		
		if(null!=context.getData(ParamKeys.FEE)){
			context.setData("pwrFee", NumberUtils.yuanToCent(context.getData(ParamKeys.FEE))); // 手续费
		}else{
			context.setData("pwrFee","0");
		}
		
		String ttrmId = (String) context.getData(ParamKeys.TERMINAL);
		if(StringUtils.isEmpty(ttrmId)){
			ttrmId=(String) context.getData("tlrTmlId");
		}
		String crpID = context.getData(ParamKeys.TELLER);
		
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
			lchkTm = "99999999";
		}
		String vchNo = context.getData("vchNo"); // 凭证号码

		rmkDte.append(StringUtils.leftPad(thdCusNo, 21)).append(lchkTm).append("01")
		.append(StringUtils.leftPad(" ", 12))
				.append(context.getData(GDParamKeys.GZ_ELE_FEE_WAY)).append(StringUtils.leftPad(vchNo, 25));

		context.setData("remarkData", rmkDte.toString());
		//TODO:mac
		context.setData("msgIdfCde", "11111111");
		
//		context.setData("isAutoReversalThirdAndCB", "N");   //关键字段，设置第三方超时时不进行任何冲正处理，否则会默认冲主机
		
		return null;
	}

	@Override
	public Map<String, Object> aftThdDeal(CommHeadDomain commheaddomain, PayFeeOnlineRetDomain payfeeonlineretdomain, Context context)
			throws CoreException {
		log.info("PayFeeOnlineServiceAction aftThdDeal start!..");
		
		//jump自动将48域前置空格截取，此处做特殊处理，补回原来的空格
		String thdRemark=context.getData("remarkData");
		
		String subStr=thdRemark.substring(0,thdRemark.indexOf("9901"));
		subStr=StringUtils.leftPad(subStr, 31-4, ' ');
		String aftStr=thdRemark.substring(thdRemark.indexOf("9901"));
		context.setData("remarkData", subStr+aftStr);
		context.setData(ParamKeys.RSV_FLD1, subStr+aftStr);

		context.setData(ParamKeys.THD_SEQUENCE, context.getData("eleThdSqn"));

		String clrYear = DateUtils.format(new Date(), "yyyy");

		// 第三方清算日期处理-将第三方清算日期作为第三方交易日期存储
		String eleClrDte = context.getData("bnkTxnDate");
		if (StringUtils.isNotEmpty(eleClrDte)) {
			eleClrDte = eleClrDte.trim();
			String thdTxnDteStr = clrYear + eleClrDte;
			context.setData(ParamKeys.THD_TXN_DATE, DateUtils.parse(thdTxnDteStr,"yyyyMMdd"));
		}

		// 第三方交易时间处理
		String eleTxnTme = context.getData("txnDateTime");
		if (StringUtils.isNotEmpty(eleTxnTme)) {
//			String thdTxnTmeStr = clrYear + eleTxnTme;
			context.setData(ParamKeys.THD_TXN_TIME, DateUtils.parse(eleTxnTme, DateUtils.STYLE_MMddHHmmss));
		}

		// 将附加数据保存到数据库
		context.setData("eleClrDte", eleClrDte.substring(4)); // 供电公司清算日期

		return null;
	}

}
