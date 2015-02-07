package com.bocom.bbip.gdeupsb.action.transportfee;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.adaptor.ThirdPartyAdaptor;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDErrorCodes;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GDEupsbTrspFeeInfo;
import com.bocom.bbip.gdeupsb.entity.GDEupsbTrspTxnJnl;
import com.bocom.bbip.gdeupsb.repository.GDEupsbTrspFeeInfoRepository;
import com.bocom.bbip.gdeupsb.repository.GDEupsbTrspTxnJnlRepository;
import com.bocom.bbip.utils.BeanUtils;
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
	
	@Autowired
    ThirdPartyAdaptor callThdTradeManager;
	
	public void execute(Context ctx) throws CoreException,CoreRuntimeException{
		log.info("PrePrintAction start.......");
		ctx.setState("fail");
//		TODO:
//			<!-- 通过加锁控制批次状态 -->
//        <Set>LckKey=STRCAT(rbfbtxnjnl444,:,$OLogNo)</Set>
//        <Exec func="PUB:Lock" error="IGNORE"><!-- 通过加锁控制批次状态 -->
//           <Arg name="RecKey" value="$LckKey"/>
//           <Arg name="TimOut" value="60"/>
//           <Arg name="AutoUnlock" value="yes"/>
//        </Exec>
//        <If condition="INTCMP(~RetCod,4,0)"><!--加锁失败-->
//           <Set>MsgTyp=E</Set>
//           <Set>RspCod=329999</Set>
//           <Set>RspMsg=交易不允许并发</Set>
//           <Return/>
//        </If>
		ctx.setData("transCode", "484004");
		ctx.setData(GDParamKeys.NOD_NO, "123");
		ctx.setData(GDParamKeys.TLR_ID, "123");
		ctx.setState("fail");
        //查询缴费记录
		String payDat = "payDat";
		GDEupsbTrspTxnJnl gdEupsbTrspTxnJnl = new GDEupsbTrspTxnJnl();
		gdEupsbTrspTxnJnl.setBrNo(GDConstants.BR_NO);
		gdEupsbTrspTxnJnl.setCarNo(ctx.getData(GDParamKeys.CAR_NO).toString());
		gdEupsbTrspTxnJnl.setSqn(ctx.getData(ParamKeys.OLD_TXN_SQN).toString());
		gdEupsbTrspTxnJnl.setActDat((Date)ctx.getData(payDat));
		List<Map<String, Object>> txnJnlList = gdEupsbTrspTxnJnlRepository.findPayInfo(gdEupsbTrspTxnJnl);
		
		ctx.setDataMap(txnJnlList.get(0));
		if(CollectionUtils.isEmpty(txnJnlList)){
			ctx.setData(ParamKeys.RSP_MSG, "缴费记录不存在");
			throw new CoreRuntimeException(GDErrorCodes.FEE_INFO_EMPTY);
		}else if(!"S".equals(ctx.getData(GDParamKeys.TTXN_ST))){
//			 <Set>TLogNo=$LogNo</Set>
			Date tactDt = new Date();
			
			ctx.setData(GDParamKeys.TACT_DT, tactDt);
			//更新路桥方流水号
			gdEupsbTrspTxnJnl.setThdKey(ctx.getData(ParamKeys.OLD_TXN_SQN).toString());
			gdEupsbTrspTxnJnl.setTlogNo(ctx.getData(ParamKeys.SEQUENCE).toString());
			gdEupsbTrspTxnJnl.setTactDt(tactDt);
			gdEupsbTrspTxnJnlRepository.update(gdEupsbTrspTxnJnl);
			
			
//			ctx.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
			
//			TODO:
//			<Arg name="HTxnCd" value="@PARA.TTxnCd"/>
//	        <Arg name="ObjSvr" value="@PARA.ThdSvr"/>
			//调用路桥方交易
			System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"+ctx.getDataMap());
			ctx.setState("callThd");
			
			
		}
		
		
		
		


		
		

		
	}
}
