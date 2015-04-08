package com.bocom.bbip.gdeupsb.action.transportfee;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.eups.action.BaseAction;
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
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class PreRegTransRollBackAction extends BaseAction{
	
	private final static Log log = LogFactory.getLog(PreRegTransRollBackAction.class);
	
	@Autowired
	GDEupsbTrspTxnJnlRepository gdEupsbTrspTxnJnlRepository;
	@Autowired
	GDEupsbTrspFeeInfoRepository gdEupsbTrspFeeInfoRepository;
	
	public void execute(Context ctx) throws CoreRuntimeException,CoreException{
		log.info("RegTransRollBackAction start........");
		Date toDay = ctx.getData(GDParamKeys.ACT_DAT);
		ctx.setData("toDay", toDay);
//		TODO:<Set>ToDay=$ActDat</Set>
//		     <Set>OLogNo=$LogNo</Set>
		//根据流水号查询流水表
		GDEupsbTrspTxnJnl gdEupsbTrspTxnJnl = new GDEupsbTrspTxnJnl();
		gdEupsbTrspTxnJnl.setSqn(ctx.getData(GDParamKeys.SQN).toString());
		List<GDEupsbTrspTxnJnl> txnJnlList = gdEupsbTrspTxnJnlRepository.find(gdEupsbTrspTxnJnl);
		if(CollectionUtils.isEmpty(txnJnlList)){
			throw new CoreRuntimeException(ErrorCodes.EUPS_FIND_ISEMPTY);
		}else{
			ctx.setDataMap(BeanUtils.toMap(txnJnlList.get(0)));
		}
		//TODO:for test！！！！！！不能直接用.equals判断date类型是否相同
		if(!toDay.equals(ctx.getData(GDParamKeys.ACT_DAT))){
			ctx.setData(ParamKeys.RSP_MSG, "该笔交易帐务日期与此时帐务日期不等，不能交易");
			throw new CoreRuntimeException(GDErrorCodes.DATE_ERROR);
		}
		//根据流水号查询抹账信息
		GDEupsbTrspFeeInfo gdEupsbTrspFeeInfo = new GDEupsbTrspFeeInfo();
		gdEupsbTrspFeeInfo.setRvsLog(ctx.getData(GDParamKeys.SQN).toString());
		List<GDEupsbTrspFeeInfo> feeInfoList = gdEupsbTrspFeeInfoRepository.find(gdEupsbTrspFeeInfo);
		if(CollectionUtils.isEmpty(feeInfoList)){
//			TODO: <Set>ExiFlg=0</Set>
		}else{
//			 TODO:<Set>ExiFlg=1</Set>

			if(!GDConstants.WCL.equals(feeInfoList.get(0).getChkFlg())){
				ctx.setData(ParamKeys.RSP_MSG, "该笔交易已经对账");
				throw new CoreRuntimeException(ErrorCodes.EUPS_THD_TRAN_CTL_CHECK);
			}
			ctx.setData(GDParamKeys.CHK_FLG, feeInfoList.get(0).getChkFlg());
			ctx.setData(GDParamKeys.STATUS, feeInfoList.get(0).getStatus());
			
		}
		
//		TODO:<Set>LckKey=STRCAT(@PARA.JnlTbl,:,$LogNo)</Set>   LOGNO:oldTxnSqn
		
//		TODO:
//		<Exec func="PUB:Lock" error="IGNORE"><!-- 通过加锁控制批次状态 -->
//        <Arg name="RecKey" value="$LckKey"/>
//        <Arg name="TimOut" value="60"/>
//        <Arg name="AutoUnlock" value="yes"/>
//     </Exec>
//     <If condition="INTCMP(~RetCod,4,0)"><!--加锁失败-->
//        <Set>RspCod=478004</Set>
//        <Return/>
//     </If>
		
		ctx.setData(GDParamKeys.TRSP_CD, null);
//		TODO:
//        <Set>TIATyp=C</Set>
//        <Set>HTxnCd=@PARA.HTxnCd_DSCal</Set>
		String ohTxnSt = "ohTxnSt";
		String oTxnSt = "oTxnSt";
		ctx.setData(ohTxnSt, ctx.getData(GDParamKeys.HTXN_ST).toString());
		ctx.setData(oTxnSt, ctx.getData(GDParamKeys.TXN_ST).toString());
		ctx.setData(GDParamKeys.TTXN_CD, ctx.getData(GDParamKeys.TXN_COD));
		
		log.info("RegTransRollBackAction end........");
	}

}
