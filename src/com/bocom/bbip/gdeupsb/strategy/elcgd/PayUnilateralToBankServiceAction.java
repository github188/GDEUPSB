package com.bocom.bbip.gdeupsb.strategy.elcgd;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsThdTranCtlInfo;
import com.bocom.bbip.eups.repository.EupsThdTranCtlInfoRepository;
import com.bocom.bbip.eups.spi.service.single.PayUnilateralToBankService;
import com.bocom.bbip.eups.spi.vo.CommHeadDomain;
import com.bocom.bbip.eups.spi.vo.PayFeeOnlineDomain;
import com.bocom.bbip.gdeupsb.common.GDErrorCodes;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GdElecClrInf;
import com.bocom.bbip.gdeupsb.repository.GdElecClrInfRepository;
import com.bocom.bbip.gdeupsb.utils.CodeSwitchUtils;
import com.bocom.bbip.gdeupsb.utils.actswitch.EleActSwitch;
import com.bocom.bbip.utils.CollectionUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.bbip.utils.NumberUtils;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

/**
 * 电力实时划帐
 * 
 * @author qc.w
 * 
 */
public class PayUnilateralToBankServiceAction implements PayUnilateralToBankService {

	private final static Logger log = LoggerFactory.getLogger(PayUnilateralToBankServiceAction.class);

	@Autowired
	EupsThdTranCtlInfoRepository eupsThdTranCtlInfoRepository;

	@Autowired
	GdElecClrInfRepository gdElecClrInfRepository;

	@Override
	public Map<String, Object> prepareCheckDeal(CommHeadDomain commheaddomain, PayFeeOnlineDomain payfeeonlinedomain, Context context)
			throws CoreException {
		log.info("PayUnilateralToBankServiceAction prepareCheckDeal start!");
		context.setData("MsgId", "0210");
		
		String dpTyp = context.getData(GDParamKeys.GZ_ELE_THD_DPT_TYP); // 配型部类型
		String comNo = CodeSwitchUtils.codeGenerator("eleGzComNoGen", dpTyp);

//		context.setData("tlr", "ABIR148");
		context.setData("tlrTmlId", context.getData(ParamKeys.TELLER));
		context.setData("br", "01441800999");
		context.setData("bk", "01441999999");

		log.info("after codeSwitch, dptTyp change from [" + dpTyp + "],to [" + comNo + "]");

		// 检查签到签退
		EupsThdTranCtlInfo eupsThdTranCtlInfo = eupsThdTranCtlInfoRepository.findOne(comNo);
		if (null == eupsThdTranCtlInfo) {
			throw new CoreException(ErrorCodes.THD_CHL_TRADE_NOT_ALLOWWED);
		}
		if (!eupsThdTranCtlInfo.isTxnCtlStsSignin()) {
			throw new CoreException(ErrorCodes.THD_CHL_TRADE_NOT_ALLOWWED);
		}
		log.info("已签到，可以进行业务");

		// 获取电力清算信息表信息
		GdElecClrInf gdElecClrInf = new GdElecClrInf();
		gdElecClrInf.setBrNo("441999");
		List<GdElecClrInf> gdElecClrInfList = gdElecClrInfRepository.find(gdElecClrInf);
		if (CollectionUtils.isEmpty(gdElecClrInfList)) {
			log.error("不存在清算参数信息");
			throw new CoreException(GDErrorCodes.EUPS_ELE_GZ_CLEAR_INFO_ERROR); // 不存在清算参数信息
		}
		else {
			gdElecClrInf = gdElecClrInfList.get(0);
			// 获取与第三方约定的第三方会计日期
			String clrDatStr = gdElecClrInf.getClrDat();
			context.setData("bakFld1", clrDatStr);
		}

		// 设置第三方请求日期及第三方请求时间
		String dtm = context.getData("txnDateTime"); // 第三方请求时间MMDDhhmmss
		String year = DateUtils.format(new Date(), "yyyy");
		String thdTxnTme = year + dtm; // 第三方交易请求时间
		log.info("第三方交易日期时间为：" + thdTxnTme);
		context.setData(ParamKeys.THD_TXN_TIME, DateUtils.parse(thdTxnTme, DateUtils.STYLE_yyyyMMddHHmmss));
		context.setData(ParamKeys.THD_SEQUENCE, context.getData("eleThdSqn"));

		context.setData(ParamKeys.CUS_AC, context.getData("cusAcEx"));

		// TODO:待考虑此处是否对后续有影响
		String sqn = context.getData(ParamKeys.SEQUENCE);
		String sqn2 = sqn.substring(sqn.length() - 6, sqn.length());
		context.setData("transJournal", sqn.substring(2, 8) + sqn2); // 银行交易流水号
		context.setData("rsvFld2", sqn.substring(2, 8) + sqn2); // 银行交易流水号,存在rsvFld2中，用于进行抹帐等交易

		// 第三方交易日期，时间处理
		String eleClrDte = context.getData("pwrtxnDate");
		if (StringUtils.isNotEmpty(eleClrDte)) {
			eleClrDte = eleClrDte.trim();
			context.setData(ParamKeys.THD_TXN_DATE, DateUtils.parse(eleClrDte,"MMDD"));
		}

		// 第三方交易时间处理
		String eleTxnTme = context.getData("txnDateTime");
		if (StringUtils.isNotEmpty(eleTxnTme)) {
			eleTxnTme=eleTxnTme.trim();
			context.setData(ParamKeys.THD_TXN_TIME, DateUtils.parse(eleTxnTme, DateUtils.STYLE_MMddHHmmss));
		}
		
		Date nowDate = new Date();
		context.setData("bnkTxnTime", DateUtils.format(nowDate, DateUtils.STYLE_HHmmss));
		context.setData("bnkTxnDate", DateUtils.format(nowDate, DateUtils.STYLE_MMdd));

		
		return null;
	}

	@Override
	public Map<String, Object> prePayToBank(CommHeadDomain commheaddomain, PayFeeOnlineDomain payfeeonlinedomain, Context context)
			throws CoreException {
		// 检查单位协议，可以不检查，因为代收付一定会检查单位协议
		log.info("PayUnilateralToBankServiceAction prePayToBank start!..");
		String txnAmt = context.getData("amount");
		BigDecimal realAmt = NumberUtils.centToYuan(txnAmt);
		context.setData(ParamKeys.TXN_AMOUNT, realAmt);

		context.setData(ParamKeys.THD_TXN_CDE, "HK"); // 设置第三方交易码为划扣，用于对账

		// 第48域值分解，获取客户编号，电费月份，产品代码，原系统参考号
		String rmkDte = (String)context.getData("rmkTmp");
		rmkDte = rmkDte.substring(rmkDte.indexOf("start") + 5);
		String thdCusNo = rmkDte.substring(0, 21);
		String eleMonth = rmkDte.substring(21, 27);
		String prdCde = rmkDte.substring(27, 29);

		context.setData("rsvFld1", rmkDte);
		context.setData(ParamKeys.THD_CUS_NO, thdCusNo.trim()); // 第三方客户标志
		context.setData(ParamKeys.BAK_FLD2, eleMonth); // 设置备用字段2为电费月份
		context.setData(ParamKeys.BAK_FLD4, prdCde); // 设置备用字段4为产品代码

		// 校验各分行账号，获取对公或对私标志
		String cusAc = context.getData(ParamKeys.CUS_AC); // 卡号
		// context.getData(ParamKeys.CUS_AC); // 卡号
		log.info("获取第三方发送的帐号，当前帐号为:" + cusAc);
		Map<String, Object> inpara = new HashMap<String, Object>();
		inpara.put("actNo", cusAc);
		inpara.put("br", context.getData(ParamKeys.BK)); // TODO：将网点号赋值给br，用于判断对公对私标志
		Map<String, Object> resultMap = EleActSwitch.eleActSwitch(inpara);
		log.info("对公对私帐号区分，最终的结果为：" + resultMap);

		String actFlg = new String();
		String actCls = (String) resultMap.get(GDParamKeys.GZ_ELE_ACT_CLS); // 对公对私标志
		if ("0".equals(actCls) || "4".equals(actCls)) {
			log.info("该帐号为对公帐号");
			actFlg = "DG";
		} else if ("2".equals(actCls) || "3".equals(actCls) || "5".equals(actCls)) {
			log.info("该帐号为对私帐号");
			actFlg = "DS";
		}
		// 将对公对私标志设置为备用字段6
		context.setData(ParamKeys.BAK_FLD6, actFlg);
		log.info("数据准备完毕，开始调用代收付！");

		return null;
	}

	@Override
	public Map<String, Object> aftPayToBank(CommHeadDomain commheaddomain, PayFeeOnlineDomain payfeeonlinedomain, Context context)
			throws CoreException {

		log.info("PayUnilateralToBankServiceAction aftPayToBank start!..");
		// 返回主机流水号给第三方，以此作为唯一性标志(会计流水号及平台流水号都超长了)
		// String mfmJrnNo = context.getData("acJrnNo"); // 获取主机流水号
		// context.setData("mfmJrnNo", mfmJrnNo); // 主机流水号,标准版未计入流水表
		// context.setData("transJournal", mfmJrnNo); //
		// 银行交易流水号,此处用主机流水号代替银行方交易流水号

		// Date nowTme = new Date();
		// Date bnkTxnDate = context.getData("acDte");
		// context.setData("bnkTxnTime", DateUtils.format(nowTme,
		// DateUtils.STYLE_HHmmss));
		//
		// if (null != bnkTxnDate) {
		// context.setData("bnkTxnDate", DateUtils.format(bnkTxnDate,
		// DateUtils.STYLE_MMdd));
		// } else {
		// context.setData("bnkTxnDate", DateUtils.format(nowTme,
		// DateUtils.STYLE_MMdd));
		// }
		context.setData("reqTme", new Date()); // 设置请求时间

		// TODO:第三方返回码转换(将主机的返回码转化为第三方需要的返回码)
		// context.setData("thdRspCde", "00");
		// 第48域值分解，获取客户编号，电费月份，产品代码，原系统参考号
		String remarkRsp = new String();

		String rmkDte = context.getData("rmkTmp");
		rmkDte = rmkDte.trim();
		log.info("当前的remarkDate="+rmkDte);
		remarkRsp = rmkDte.substring(rmkDte.indexOf("start") + 5);
		context.setData("remarkData", remarkRsp);
		return null;
	}

}
