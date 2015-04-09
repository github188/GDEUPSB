package com.bocom.bbip.gdeupsb.action.transportfee;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.adaptor.ThirdPartyAdaptor;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GDEupsbTrspFeeInfo;
import com.bocom.bbip.gdeupsb.entity.GDEupsbTrspNpManag;
import com.bocom.bbip.gdeupsb.entity.GDEupsbTrspTxnJnl;
import com.bocom.bbip.gdeupsb.repository.GDEupsbTrspFeeInfoRepository;
import com.bocom.bbip.gdeupsb.repository.GDEupsbTrspNpManagRepository;
import com.bocom.bbip.gdeupsb.repository.GDEupsbTrspTxnJnlRepository;
import com.bocom.bbip.service.BGSPServiceAccessObject;
import com.bocom.bbip.service.Result;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.CollectionUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class PreInvoiceCancelAction extends BaseAction{
	private final static Log log = LogFactory.getLog(PreInvoiceCancelAction.class);
	
	@Autowired
	GDEupsbTrspNpManagRepository gdEupsbTrspNpManagRepository;
	@Autowired
	GDEupsbTrspFeeInfoRepository gdEupsbTrspFeeInfoRepository;
	@Autowired
	GDEupsbTrspTxnJnlRepository gdEupsbTrspTxnJnlRepository;
	@Autowired
	ThirdPartyAdaptor callThdReturnMessage;
	
	public void execute(Context ctx) throws CoreException,CoreRuntimeException{
		log.info("PreInvoiceCancelAction start......");
		ctx.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
		ctx.setData(GDParamKeys.BR_NO, ctx.getData(ParamKeys.BK));
		Date today = new Date();
		ctx.setData(GDParamKeys.NOD_NO, ctx.getData(ParamKeys.BR));
		ctx.setData(GDParamKeys.TLR_ID, ctx.getData(ParamKeys.TELLER));
		
		GDEupsbTrspNpManag gdEupsbTrspNpManag = new GDEupsbTrspNpManag();
		gdEupsbTrspNpManag.setCarNo(ctx.getData(GDParamKeys.CAR_NO).toString());
		gdEupsbTrspNpManag.setInvNo(ctx.getData(GDParamKeys.INV_NO).toString());
		gdEupsbTrspNpManag.setStatus("0");
		List<GDEupsbTrspNpManag> npManagList = gdEupsbTrspNpManagRepository.find(gdEupsbTrspNpManag);
		if(!CollectionUtils.isEmpty(npManagList)){
			ctx.setData(ParamKeys.RSP_MSG, "已打印年票，不能退款");
			throw new CoreRuntimeException(ErrorCodes.EUPS_FAIL);
		}
		
		//检查缴费记录
		GDEupsbTrspFeeInfo gdEupsbTrspFeeInfo =new GDEupsbTrspFeeInfo();
		gdEupsbTrspFeeInfo.setBrNo(ctx.getData(GDParamKeys.BR_NO).toString());
		gdEupsbTrspFeeInfo.setCarNo(ctx.getData(GDParamKeys.CAR_NO).toString());
		gdEupsbTrspFeeInfo.setCarTyp(ctx.getData(GDParamKeys.CAR_TYP).toString());
		gdEupsbTrspFeeInfo.setInvNo(ctx.getData(GDParamKeys.INV_NO).toString());
		List<GDEupsbTrspFeeInfo> feeInfoList = gdEupsbTrspFeeInfoRepository.findOrder(gdEupsbTrspFeeInfo);
		if(CollectionUtils.isEmpty(feeInfoList)){
			ctx.setData(ParamKeys.RSP_MSG, "无该车主的对应的缴费记录");
			throw new CoreRuntimeException(ErrorCodes.EUPS_FIND_ISEMPTY);
		}else if(!GDConstants.DP.equals(feeInfoList.get(0).getStatus())){
			ctx.setData(ParamKeys.RSP_MSG, "状态信息错,此笔费用状态非打票");
			throw new CoreRuntimeException(ErrorCodes.EUPS_CUS_AC_STATUS_CHECK_FAIL);
		}else if(!DateUtils.isSameDate(today, feeInfoList.get(0).getTactDt())){
			ctx.setData(ParamKeys.RSP_MSG, "只允许打票当天作废");
			throw new CoreRuntimeException(ErrorCodes.EUPS_DATE_ERROR);
		}else{
			ctx.setData(GDParamKeys.STATUS, feeInfoList.get(0).getStatus());  //缴费状态
			ctx.setData(ParamKeys.TXN_AMT, feeInfoList.get(0).getTxnAmt());   //交易金额
			ctx.setData(GDParamKeys.ACT_NO, feeInfoList.get(0).getActNo());  //客户账号
			ctx.setData("actFlg", feeInfoList.get(0).getActTyp());  //账号类型
			ctx.setData(GDParamKeys.THD_KEY, feeInfoList.get(0).getThdKey());  //
			ctx.setData(GDParamKeys.CAR_TYP, feeInfoList.get(0).getCarTyp());  //
			ctx.setData(GDParamKeys.PAY_MON, feeInfoList.get(0).getPayMon()); //缴费月数
			ctx.setData("otLogNo", feeInfoList.get(0).getTlogNo());  //打印发票流水号
			ctx.setData(GDParamKeys.INV_NO, feeInfoList.get(0).getInvNo());  //发票号
			ctx.setData("otActDt", feeInfoList.get(0).getTactDt());  //打印日期
			ctx.setData("oRvsLog", feeInfoList.get(0).getRvsLog());  //退费流水号
		}
		
		//查询流水表记录

		GDEupsbTrspTxnJnl gdEupsbTrspTxnJnl = new GDEupsbTrspTxnJnl();
		gdEupsbTrspTxnJnl.setSqn(ctx.getData("oRvsLog").toString());
		List<GDEupsbTrspTxnJnl> txnJnlList = gdEupsbTrspTxnJnlRepository.find(gdEupsbTrspTxnJnl);
		if(CollectionUtils.isEmpty(txnJnlList)){
			ctx.setData("ohTxnSt", "U");  //原主机交易状态设为初始状态
			ctx.setData("otTxnSt", "U");  //原第三方交易状态设为初始状态
			ctx.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
		}else{
			if(!"S".equals(ctx.getData("ohTxnSt"))){
				//TODO:
//				<Exec func="PUB:CallHostOther" error="IGNORE"><!--上主机查询交易结果-->
//                <Arg name="HTxnCd" value="458980"/>
//                <Arg name="ObjSvr" value="SHSTPUB1"/>
//             </Exec>
//             <If condition="~RetCod=0">
//                <If condition="$CrcSts!=Y">
//                   <Set>OHTxnSt=S</Set>
//                </If>
//                <Else>
//                   <Set>OHTxnSt=C</Set>
//                </Else>
//             </If>
//             <ElseIf condition="~RetCod=3">
//                <Switch expression="$HRspCd">
//                   <Case value="AG8001"/>
//                   <Case value="AG8981"/>
//                   <Case value="SC6129">
//                      <Set>OHTxnSt=F</Set>
//                      <Break/>
//                   </Case>
//                   <Case value="SC6034">
//                      <Set>OHTxnSt=C</Set>
//                      <Break/>
//                   </Case>
//                   <Default>
//                      <Set>MsgTyp=E</Set>
//                      <Set>RspCod=$HRspCd</Set>
//                      <Return/>
//                   </Default>
//                </Switch>
//             </ElseIf>
//             <Else condition="~RetCod&lt;0">
//                <Set>RspCod=329999</Set>
//                <Set>RspMsg=上核心查流水失败</Set>
//                <Return/>
//             </Else>
//             <Set>LogNo=$ORvsLog</Set>
//             <Set>TLogNo=$ORvsLog</Set>
				ctx.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
			}
//			ctx.setData("ohTxnSt", txnJnlList.get(0).getHtxnSt());
//			ctx.setData("otTxnSt", txnJnlList.get(0).getTtxnSt());
//			ctx.setData(ParamKeys.OLD_TXN_SQN, txnJnlList.get(0).getSqn());
//		     <ElseIf condition="~RetCod=0">
			ctx.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);

		}


		

		
	}

}
