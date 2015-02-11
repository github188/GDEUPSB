package com.bocom.bbip.gdeupsb.action.transportfee;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GDEupsbTrspTxnJnl;
import com.bocom.bbip.gdeupsb.repository.GDEupsbTrspTxnJnlRepository;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class TrspWyFeeChargeAction extends BaseAction{

	private final static Log log = LogFactory.getLog(TrspWyFeeChargeAction.class);
	
	@Autowired
	GDEupsbTrspTxnJnlRepository gdEupsbTrspTxnJnlRepository;
	
	public void execute(Context ctx) throws CoreException,CoreRuntimeException{
		log.info("TrspWyFeeChargeAction start......");
		String cardNo = "cardNo";  //前端输入的银行卡号
		String totalAmt = "totalAmt"; //交易金额
		String monthCount = "monthCount";
		String plateType = "plateType";
		String CHARGE_PROCESS = "eups.payUnilateralToBank";
		ctx.setData(GDParamKeys.FTXN_TM, new Date());
		ctx.setData(GDParamKeys.BR_NO, "443999");
		ctx.setData(GDParamKeys.CAR_NO, ctx.getData("plateNo"));
		ctx.setData(GDParamKeys.ACT_NO, ctx.getData(cardNo));
		ctx.setData(GDParamKeys.TXN_AMT, ctx.getData(totalAmt));
//		<Set>BrNo=444999</Set>
//        <Set>CCSCod=TLU6</Set>
//        <Set>TTxnCd=484002</Set>
//        <Set>FeCod=484002</Set>
//        <Set>TrmNo=DVID</Set>
//
//        <Set>NodTrc=$tranFlowNo</Set>
//        <Set>TIATyp=T</Set>
//        <Set>AthLvl=00</Set>
//        <Set>CprInd=0</Set>
//        <Set>EnpInd=0</Set>
//        <Set>NodNo=444800</Set>
//        <Set>TrmVer=v0000001</Set>
//
//        <!--发送包体-->
//        <Set>number=$number</Set>    <!--缴费号码-->
//        <Set>payAmont=$payAmont</Set>    <!--缴纳金额 -->
//        <Set>ActTyp=4</Set>          <!--账号类型 -->
//        <Set>cardNo=$cardNo</Set>    <!--账号类型 -->
//        <Set>tranPwd=$password</Set><!--卡交易密码 -->
//
//        <!--记录流水-->
//        <!--
//        BrNo|ActDat|FTxnTm|CrdNo|TxnAmt|Fee|PCusId|TxnCnl|TxnCod|PayTyp|TxnSts|NodTrc|PmpTrc|TckNo|ThdTrc|
//        -->
//        <Set>FTxnTm=GETDATETIME()</Set>
//        <Set>CrdNo=$cardNo</Set>      <!--缴费卡号-->
//        <Set>TxnAmt=$TotalAmt</Set>     <!--交易金额-->
//        <Set>totalBal=$TotalAmt</Set>     <!--交易金额-->
//        <Set>Fee= </Set>              <!--手续费-->
//        <Set>PCusId=$pmpCustId</Set>  <!--收付通宝客户编号-->
//        <Set>TxnCnl=$channel</Set>    <!--渠道标志-->
//        <Set>PayTyp=11</Set>          <!--缴费类型: 11-路桥缴费-->
//        <Set>TxnSts=U</Set>           <!--交易状态-->
//        <Quote name="GetpmpFlowNo"></Quote>  <!--获取流水号-->
//        <Set>TckNo= </Set>            <!--会计流水号-->
//        <Set>ThdTrc= </Set>     <!--中间业务流水号-->

//		 INSERT INTO wbgtxnjnl444 VALUES('%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s')
//	       </Sentence>
//	       <Fields>BrNo|ActDat|FTxnTm|CrdNo|TxnAmt|Fee|PCusId|TxnCnl|TxnSrc|TxnCod|PayTyp|TxnSts|NodTrc|PmpTrc|TckNo|ThdTrc|</Fields>
		GDEupsbTrspTxnJnl gdEupsbTrspTxnJnl = new GDEupsbTrspTxnJnl();
		gdEupsbTrspTxnJnl.setFtxnTm((Date)ctx.getData(GDParamKeys.FTXN_TM));
		gdEupsbTrspTxnJnl.setBrNo(ctx.getData(GDParamKeys.BR_NO).toString());
		gdEupsbTrspTxnJnl.setActNo(ctx.getData(GDParamKeys.ACT_NO).toString());
		gdEupsbTrspTxnJnl.setTxnAmt((BigDecimal)ctx.getData(GDParamKeys.TXN_AMT));
		gdEupsbTrspTxnJnl.setPayMon(ctx.getData(monthCount).toString());
		gdEupsbTrspTxnJnl.setCarTyp(ctx.getData(plateType).toString());
		gdEupsbTrspTxnJnl.setSqn(ctx.getData(GDParamKeys.SQN).toString());
		
//		TODO: <Set>PCusId=$pmpCustId</Set>  <!--收付通宝客户编号-->
		gdEupsbTrspTxnJnlRepository.insert(gdEupsbTrspTxnJnl);
		ctx.setData(ParamKeys.ACCOUNT_DATE, ctx.getData(ParamKeys.ACCOUNT_DATE).toString());
		ctx.setData(ParamKeys.AC_DATE, ctx.getData(ParamKeys.AC_DATE).toString());
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@"+ctx.getDataMap());
		get(BBIPPublicService.class).synExecute(CHARGE_PROCESS, ctx);
		
//		TODO:
//		<Exec func="PUB:ExecSql" error="IGNORE">
//        <Arg name="SqlCmd" value="Updwbgjnl" />
//      </Exec>
	}
}
