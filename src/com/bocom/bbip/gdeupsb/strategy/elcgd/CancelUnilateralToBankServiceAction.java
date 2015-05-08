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
import com.bocom.bbip.eups.entity.EupsTransJournal;
import com.bocom.bbip.eups.repository.EupsTransJournalRepository;
import com.bocom.bbip.eups.spi.service.single.CancelUnilateralToBankService;
import com.bocom.bbip.eups.spi.vo.CancelDomain;
import com.bocom.bbip.eups.spi.vo.CommHeadDomain;
import com.bocom.bbip.gdeupsb.common.GDErrorCodes;
import com.bocom.bbip.gdeupsb.entity.GdElecClrInf;
import com.bocom.bbip.gdeupsb.repository.GdElecClrInfRepository;
import com.bocom.bbip.utils.CollectionUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.bbip.utils.NumberUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

/**
 * 划扣抹帐
 * 
 * @author qc.w
 * 
 */
public class CancelUnilateralToBankServiceAction implements CancelUnilateralToBankService {

	private final static Logger log = LoggerFactory.getLogger(CancelUnilateralToBankServiceAction.class);
	@Autowired
	EupsTransJournalRepository eupsTransJournalRepository;

	@Autowired
	GdElecClrInfRepository gdElecClrInfRepository;

	@Override
	public Map<String, Object> aftCclToBank(CommHeadDomain commheaddomain, CancelDomain canceldomain, Context context) throws CoreException {
		log.info("AftCnlBnkSglDealStrategyAction start!..");
	
		context.setData("cnlRmk", "    ");

		String thdRspCd = context.getData(ParamKeys.REVERSE_RESULT_CODE); // 冲正返回码
		if (Constants.RESPONSE_CODE_SUCC_HOST.equals(thdRspCd)) {
			context.setData("errRson", null);  //冲正附加数据设置为空
			context.setData(ParamKeys.RESPONSE_CODE, "CNLSC00"); // 冲正成功
		}
		else {
			context.setData(ParamKeys.RESPONSE_CODE, "CNLER00"); // 冲正不成功
		}
		
		log.info("#######thdRspCde= " + thdRspCd);
		return null;
	}

	@Override
	public Map<String, Object> preCclToBank(CommHeadDomain commheaddomain, CancelDomain canceldomain, Context context) throws CoreException {
		
		String remarkRsp = new String();
		String rmkDate = context.getData("rmkTmp");
		rmkDate = rmkDate.trim();
		log.info("当前的remarkDate="+rmkDate);
		remarkRsp = rmkDate.substring(rmkDate.indexOf("start") + 5);
		context.setData("remarkData", remarkRsp);
		
		
		log.info("PreCnlBnkSglDealStrategyAction start!..");
		context.setData("MsgId", "0410"); 
		
		//初始化数据
		context.setData("errRson", "8072");   //初始化错误信息为找不到原流水记录
		Date bnkTxnTime = new Date();

		String bnkTxnTme = DateUtils.format(bnkTxnTime, DateUtils.STYLE_HHmmss);
		context.setData("bnkTxnTime", bnkTxnTme);

		String bnkTxnDte = DateUtils.format(bnkTxnTime, DateUtils.STYLE_MMdd);
		context.setData("bnkTxnDate", bnkTxnDte);
		
		// 获取sqn，经过处理后作为银行方流水号发送给第三方,因为抹帐不生成主机流水号，此处截取sqn
		String transJnl = context.getData(ParamKeys.SEQUENCE);
		transJnl = transJnl.substring(2, 8) + transJnl.substring(transJnl.length() - 4, transJnl.length());

		log.info("after sub deal,transJnl=" + transJnl);
		context.setData("transJournal", transJnl);
		
		
		String ttxnAmt = context.getData("amount");
		context.setData(ParamKeys.TXN_AMOUNT, NumberUtils.centToYuan(ttxnAmt)); // 将金额转化为bigdecimal
		
		//TODO:for test-0415
//		context.setData(ParamKeys.TXN_AMOUNT,new BigDecimal("0.01"));
		
//		String remarkData = context.getData("remarkData"); // 48域附加数据
//		String thdCusNo = remarkData.substring(0, 21).trim(); // 客户编号
//		String eleMonth = remarkData.substring(21, 27); // 电费月份
//		String proCde = remarkData.substring(27, 29); // 产品代码
////		String oldAcSqn = remarkData.substring(29, 41).trim(); // 原系统参考号-对应缴费时送给第三方的会计流水号
//		//TODO:两位待确认，有可能电费月份是8位
//		String tmpCde = remarkData.substring(29, 31); // 产品代码
//		String oldAcSqn = remarkData.substring(31,43).trim(); // 原系统参考号-对应缴费时送给第三方的会计流水号
		
		String rmkDte = context.getData("rmkTmp");
		rmkDte=rmkDte.substring(rmkDte.indexOf("start")+5);
		String thdCusNo = rmkDte.substring(0, 21).trim();
		String eleMonth = rmkDte.substring(21, 27);
		String proCde = rmkDte.substring(27, 29);
		String tmpCde = rmkDte.substring(29, 31); // 产品代码
		String oldAcSqn = rmkDte.substring(31,43).trim(); // 原系统参考号-对应缴费时送给第三方的会计流水号
		
		context.setData("rsvFld1", rmkDte);
		
		context.setData(ParamKeys.THD_CUS_NO, thdCusNo); // 第三方客户标志
		// context.setData("eleMonth", eleMonth);
		context.setData("bakFld2", eleMonth); // 电费月份
		context.setData("bakFld4", proCde); // 产品代码
		// context.setData(ParamKeys.OLD_TXN_SEQUENCE, oldAcSqn); //原会计流水号

		// 根据会计流水号查询原交易流水号
		EupsTransJournal eupsTransJournal = new EupsTransJournal();
		//TODO:原来是匹配会计流水号，0415测试，将其改为原第三方流水号
//		eupsTransJournal.setMfmJrnNo(oldAcSqn);
		eupsTransJournal.setThdSqn(oldAcSqn);
		eupsTransJournal.setThdCusNo(thdCusNo);
		
		eupsTransJournal.setTxnSts(Constants.TRADE_STATUS_SUCCESS); // 交易成功
		eupsTransJournal.setTxnTyp(Constants.EUPS_TXN_TYP_NOMAL);

		List<EupsTransJournal> list = eupsTransJournalRepository.find(eupsTransJournal);
		if (CollectionUtils.isEmpty(list)) {
			context.setData("errRson", "8072");
			throw new CoreException(ErrorCodes.BIZ_ERROR_CANCEL_PAYMENT_ORIGINAL_PAYMENT_JOURNAL_NOT_FOUND);
		}
		else {
			eupsTransJournal = list.get(0);
		}

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

		String oldSqn = eupsTransJournal.getSqn();
		context.setData(ParamKeys.OLD_TXN_SEQUENCE, oldSqn); // 原交易流水号
		
		
		return null;

	}

}
