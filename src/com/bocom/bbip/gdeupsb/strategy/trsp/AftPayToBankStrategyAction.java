package com.bocom.bbip.gdeupsb.strategy.trsp;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GDEupsbTrspFeeInfo;
import com.bocom.bbip.gdeupsb.entity.GDEupsbTrspPayInfo;
import com.bocom.bbip.gdeupsb.entity.GdeupsTyfInvRec;
import com.bocom.bbip.gdeupsb.repository.GDEupsbTrspFeeInfoRepository;
import com.bocom.bbip.gdeupsb.repository.GDEupsbTrspPayInfoRepository;
import com.bocom.bbip.gdeupsb.repository.GdeupsTyfInvRecRepository;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.CollectionUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
import com.bocom.jump.bp.core.Executable;

public class AftPayToBankStrategyAction implements Executable{
	
	@Autowired
	GDEupsbTrspPayInfoRepository gdEupsbTrspPayInfoRepository;
	
	
	@Autowired
	GDEupsbTrspFeeInfoRepository gdEupsbTrspFeeInfoRepository;
	
	@Autowired
	GdeupsTyfInvRecRepository gdeupsTyfInvRecRepository;
	
	private final static Log log = LogFactory.getLog(AftPayToBankStrategyAction.class); 
	public void execute(Context ctx) throws CoreException,CoreRuntimeException{
		log.info("AftPayToBankStrategyAction start......");
		
//        更新缴费标志
		GDEupsbTrspPayInfo gdEupsbTrspPayInfo = new GDEupsbTrspPayInfo();
		gdEupsbTrspPayInfo.setThdKey((GDParamKeys.THD_KEY).toString());
		gdEupsbTrspPayInfo.setFlg("1");
		gdEupsbTrspPayInfoRepository.update(gdEupsbTrspPayInfo);
		
		
//		TODO: 

//        <Set>mxcount=$PayMon</Set>！！！！！！！！！！！！！！！！！！！！！！！


        String otCusNm = (String)ctx.getData(GDParamKeys.TCUS_NM);
		String car_typ = ctx.getData(GDParamKeys.CAR_TYP).toString();
		String carNo = ctx.getData(GDParamKeys.CAR_NO).toString();
		
		String actNo = ctx.getData(GDParamKeys.ACT_NO).toString();
		String trDate = (String)ctx.getData(GDParamKeys.TR_DATE);
		String sqn = ctx.getData(GDParamKeys.SQN).toString();
//		String tCusNam = ctx.getData(GDParamKeys.TCUS_NM).toString().substring(1, 30);
		
		
		ctx.setData(GDParamKeys.TR_TYP, "6");
		ctx.setData(GDParamKeys.TMP02, car_typ);
		ctx.setData(GDParamKeys.TMP01, carNo);
		ctx.setData(GDParamKeys.ACT_NO, actNo);
		ctx.setData(GDParamKeys.TR_DATE, trDate);
		ctx.setData(GDParamKeys.SQN, sqn);
//		ctx.setData(GDParamKeys.TCUS_NM, tCusNam);
		
//		查询是否存在发票记录
//		GDEupsbPubInvInfo gdEupsbPubInvInfo = new GDEupsbPubInvInfo();
//		gdEupsbPubInvInfo.setTmp01(carNo);
//		gdEupsbPubInvInfo.setTmp02(car_typ);
//		gdEupsbPubInvInfo.setActNo(actNo);
//		List<GDEupsbPubInvInfo> invInfoList = gdEupsbPubInvInfoRepository.find(gdEupsbPubInvInfo);
//		if(!CollectionUtils.isEmpty(invInfoList)){
//			gdEupsbPubInvInfoRepository.delete(gdEupsbPubInvInfo);
//		}
		
		GdeupsTyfInvRec gdeupsTyfInvRec = new GdeupsTyfInvRec();
		gdeupsTyfInvRec.setTmp01(carNo);
		gdeupsTyfInvRec.setTmp02(car_typ);
		gdeupsTyfInvRec.setActNo(actNo);
		List<GdeupsTyfInvRec> invRecList = gdeupsTyfInvRecRepository.find(gdeupsTyfInvRec);
		if(!CollectionUtils.isEmpty(invRecList)){
			gdeupsTyfInvRecRepository.delete1(gdeupsTyfInvRec);
		}

		gdeupsTyfInvRec = BeanUtils.toObject(ctx.getDataMap(), GdeupsTyfInvRec.class);
		gdeupsTyfInvRecRepository.insert(gdeupsTyfInvRec);
		
		

		
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"+ctx);
		GDEupsbTrspFeeInfo gdEupsbTrspFeeInfo = new GDEupsbTrspFeeInfo();
//		INSERT INTO rbfbtxnbok444(BrNo,ThdKey,CarTyp,TCusId,TCusNm,PayMon,PayDat,PayLog,TxnAmt,TxnCnl,ActTyp,ActNo,PayNod,PayTlr,PayTck,Status)
//        VALUES('%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','0')
//   </Sentence>
//   <Fields>BrNo|ThdKey|CarTyp|TCusId|TCusNm|PayMon|ActDat|LogNo|TxnAmt|TxnCnl|ActFlg|ActNo|NodNo|TlrId|TckNo|</Fields>
		gdEupsbTrspFeeInfo.setBrNo((String)ctx.getData(GDParamKeys.BR_NO));
		gdEupsbTrspFeeInfo.setThdKey((String)ctx.getData(GDParamKeys.THD_KEY));
		gdEupsbTrspFeeInfo.setCarTyp((String)ctx.getData(GDParamKeys.CAR_TYP));
		gdEupsbTrspFeeInfo.setCarNo((String)ctx.getData(GDParamKeys.CAR_NO));
		gdEupsbTrspFeeInfo.setTcusNm((String)ctx.getData(GDParamKeys.TCUS_NM));
		gdEupsbTrspFeeInfo.setPayMon((String)ctx.getData(GDParamKeys.PAY_MON));
		gdEupsbTrspFeeInfo.setPayDat((Date)ctx.getData(ParamKeys.AC_DATE));
		gdEupsbTrspFeeInfo.setPayLog((String)ctx.getData(ParamKeys.SEQUENCE));
		gdEupsbTrspFeeInfo.setTxnAmt((BigDecimal)ctx.getData(GDParamKeys.TXN_AMT));
		gdEupsbTrspFeeInfo.setTxnCnl((String)ctx.getData(GDParamKeys.TXN_CNL));
		gdEupsbTrspFeeInfo.setActTyp((String)ctx.getData(GDParamKeys.ACT_TYP));//1111111111
		gdEupsbTrspFeeInfo.setActNo((String)ctx.getData(GDParamKeys.ACT_NO));
		gdEupsbTrspFeeInfo.setPayNod((String)ctx.getData(GDParamKeys.NOD_NO));
		gdEupsbTrspFeeInfo.setPayTlr((String)ctx.getData(GDParamKeys.TLR_ID));
		gdEupsbTrspFeeInfo.setPayTck((String)ctx.getData(GDParamKeys.TCK_NO));//银行流水号
		gdEupsbTrspFeeInfo.setStatus(GDConstants.JF);
		
		gdEupsbTrspFeeInfoRepository.insert(gdEupsbTrspFeeInfo);
	}
}
