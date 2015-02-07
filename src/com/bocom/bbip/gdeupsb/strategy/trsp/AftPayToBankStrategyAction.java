package com.bocom.bbip.gdeupsb.strategy.trsp;

import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.bocom.bbip.comp.account.AccountService;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GDEupsbPubInvInfo;
import com.bocom.bbip.gdeupsb.entity.GDEupsbTrspFeeInfo;
import com.bocom.bbip.gdeupsb.entity.GDEupsbTrspPayInfo;
import com.bocom.bbip.gdeupsb.repository.GDEupsbPubInvInfoRepository;
import com.bocom.bbip.gdeupsb.repository.GDEupsbTrspFeeInfoRepository;
import com.bocom.bbip.gdeupsb.repository.GDEupsbTrspPayInfoRepository;
import com.bocom.bbip.gdeupsb.repository.GDEupsbTrspTxnJnlRepository;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
import com.bocom.jump.bp.core.Executable;

public class AftPayToBankStrategyAction implements Executable{
	
	@Autowired
	GDEupsbTrspPayInfoRepository gdEupsbTrspPayInfoRepository;
	
	@Autowired
	GDEupsbPubInvInfoRepository gdEupsbPubInvInfoRepository;
	
	@Autowired
	GDEupsbTrspFeeInfoRepository gdEupsbTrspFeeInfoRepository;
	
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
		GDEupsbPubInvInfo gdEupsbPubInvInfo = new GDEupsbPubInvInfo();
		gdEupsbPubInvInfo.setTmp01(carNo);
		gdEupsbPubInvInfo.setTmp02(car_typ);
		gdEupsbPubInvInfo.setActNo(actNo);
		List<GDEupsbPubInvInfo> invInfoList = gdEupsbPubInvInfoRepository.find(gdEupsbPubInvInfo);
		if(!CollectionUtils.isEmpty(invInfoList)){
			gdEupsbPubInvInfoRepository.delete(gdEupsbPubInvInfo);
		}
		
		

		gdEupsbPubInvInfo = BeanUtils.toObject(ctx.getDataMap(), GDEupsbPubInvInfo.class);
		gdEupsbPubInvInfoRepository.insert(gdEupsbPubInvInfo);
		
		

		ctx.setData(GDParamKeys.TCUS_NM, otCusNm);
		

//		<!--登记缴费记录表-->
//		<Exec func="PUB:ExecSql" error="IGNORE">
//        <Arg name="SqlCmd" value="Inittxnbok"/>  rbfbtxnbok444找不到
//    </Exec>
//		 INSERT INTO rbfbtxnbok444(BrNo,ThdKey,CarTyp,TCusId,TCusNm,PayMon,PayDat,PayLog,TxnAmt,TxnCnl,ActTyp,ActNo,PayNod,PayTlr,PayTck,Status)
//         VALUES('%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','0')
		GDEupsbTrspFeeInfo gdEupsbTrspFeeInfo = new GDEupsbTrspFeeInfo();
		gdEupsbTrspFeeInfo = BeanUtils.toObject(ctx.getDataMap(), GDEupsbTrspFeeInfo.class);
		gdEupsbTrspFeeInfoRepository.insert(gdEupsbTrspFeeInfo);
	}
}
