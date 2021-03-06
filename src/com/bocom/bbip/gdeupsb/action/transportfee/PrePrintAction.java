package com.bocom.bbip.gdeupsb.action.transportfee;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.common.GDErrorCodes;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GDEupsbTrspTxnJnl;
import com.bocom.bbip.gdeupsb.repository.GDEupsbTrspFeeInfoRepository;
import com.bocom.bbip.gdeupsb.repository.GDEupsbTrspTxnJnlRepository;
import com.bocom.bbip.utils.CollectionUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class PrePrintAction extends BaseAction{
	
	private final static Log log = LogFactory.getLog(PrePrintAction.class);
	
	@Autowired
	GDEupsbTrspTxnJnlRepository gdEupsbTrspTxnJnlRepository;
	
	@Autowired
	GDEupsbTrspFeeInfoRepository gdEupsbTrspFeeInfoRepository;
	
	
	
	public void execute(Context ctx) throws CoreException,CoreRuntimeException{
		log.info("PrePrintAction start.......");
		ctx.setState("fail");
		
        //查询缴费记录
		
		GDEupsbTrspTxnJnl gdEupsbTrspTxnJnl = new GDEupsbTrspTxnJnl();
		gdEupsbTrspTxnJnl.setBrNo(ctx.getData(ParamKeys.BK).toString());
		gdEupsbTrspTxnJnl.setCarNo(ctx.getData(GDParamKeys.CAR_NO).toString());
		gdEupsbTrspTxnJnl.setSqn(ctx.getData(ParamKeys.OLD_TXN_SQN).toString());
	
		gdEupsbTrspTxnJnl.setActDat((Date)ctx.getData(GDParamKeys.PAY_DAT));
		
		List<Map<String, Object>> txnJnlList = gdEupsbTrspTxnJnlRepository.findPayInfo(gdEupsbTrspTxnJnl);
//		select a.CAR_NO,a.TCUS_NM ,a.TXN_AMT ,a.CAR_TYP,a.PAY_MON,
//	  	a.TTXN_ST,a.TXN_ST,b.BEG_DAT,b.END_DAT,b.CAR_NAME ,b.CAR_DZS,b.CNT_STD,b.FEE_STD,b.CORPUS,b.LATE_FEE,b.CLGS,b.YYBZ
//	           FROM   gdeupsb.TRSP_TXN_JNL as a ,gdeupsb.TRSP_FEE_INFO as b
//	           WHERE  a.SQN=b.PAY_LOG AND a.BR_NO = #{brNo,jdbcType=CHAR} 
//	           AND b.STATUS='0' AND a.CAR_NO = #{carNo,jdbcType=CHAR} AND a.SQN = #{sqn,jdbcType=CHAR}
		if(CollectionUtils.isEmpty(txnJnlList)){
			ctx.setData(ParamKeys.RSP_MSG, "缴费记录不存在");
			throw new CoreRuntimeException(GDErrorCodes.FEE_INFO_EMPTY);
		}
	
		Map<String, Object> txnJnlMap = txnJnlList.get(0);
		log.info("!!!!!!!!!!!!!!!!"+txnJnlList.get(0));
		ctx.setData(GDParamKeys.TCUS_NM, txnJnlMap.get("TCUS_NM"));
		ctx.setData(GDParamKeys.TXN_AMT, txnJnlMap.get("TXN_AMT"));
		ctx.setData(GDParamKeys.CAR_TYP, txnJnlMap.get("CAR_TYP"));
		ctx.setData(GDParamKeys.PAY_MON, txnJnlMap.get("PAY_MON"));
		ctx.setData(GDParamKeys.TTXN_ST, txnJnlMap.get("TTXN_ST"));
		ctx.setData(GDParamKeys.TXN_ST, txnJnlMap.get("TXN_ST"));
		ctx.setData(GDParamKeys.BEG_DAT, txnJnlMap.get("BEG_DAT"));
		ctx.setData(GDParamKeys.END_DAT, txnJnlMap.get("END_DAT"));
		ctx.setData(GDParamKeys.CAR_NAME, txnJnlMap.get("CAR_NAME"));
		ctx.setData(GDParamKeys.CAR_DZS, txnJnlMap.get("CAR_DZS"));
		ctx.setData(GDParamKeys.CNT_STD, txnJnlMap.get("CNT_STD"));
		ctx.setData(GDParamKeys.FEE_STD, txnJnlMap.get("FEE_STD"));
		ctx.setData(GDParamKeys.CORPUS, txnJnlMap.get("CORPUS"));
		ctx.setData(GDParamKeys.LATE_FEE, txnJnlMap.get("LATE_FEE"));
		ctx.setData(GDParamKeys.CLGS, txnJnlMap.get("CLGS"));
		ctx.setData(GDParamKeys.YYBZ, txnJnlMap.get("YYBZ"));
		
		 if(!"S".equals(txnJnlList.get(0).get(GDParamKeys.TTXN_ST))){
//			 <Set>TLogNo=$LogNo</Set>
			ctx.setDataMap(txnJnlList.get(0));
			Date tactDt = new Date();
			Date payDat = (Date)ctx.getData(GDParamKeys.PAY_DAT);
			ctx.setData(GDParamKeys.TACT_DT, DateUtils.format(tactDt, DateUtils.STYLE_yyyyMMdd));
			ctx.setData("payDat", DateUtils.format(payDat, DateUtils.STYLE_yyyyMMdd));
			gdEupsbTrspTxnJnl.setSqn(ctx.getData(ParamKeys.OLD_TXN_SQN).toString());
			gdEupsbTrspTxnJnl.setTlogNo(ctx.getData(ParamKeys.SEQUENCE).toString());
			gdEupsbTrspTxnJnl.setTactDt(tactDt);
			gdEupsbTrspTxnJnlRepository.update1(gdEupsbTrspTxnJnl);
			
			log.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"+ctx.getDataMap());
			ctx.setState("callThd");
		}else{
			ctx.setState("complete");
			ctx.setDataMap(txnJnlList.get(0));
		}
	}
}
