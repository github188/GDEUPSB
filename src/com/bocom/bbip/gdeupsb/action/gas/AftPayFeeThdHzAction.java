package com.bocom.bbip.gdeupsb.action.gas;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsTransJournal;
import com.bocom.bbip.eups.repository.EupsTransJournalRepository;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
/**
 * 
 * @author WangMingQin
 *
 */
public class AftPayFeeThdHzAction extends BaseAction{
	private static final Log logger=LogFactory.getLog(AftPayFeeThdHzAction.class);

	public void execute(Context context) throws CoreException, CoreRuntimeException{
	logger.info("Enter in AftPayFeeThdHzAction.....");
	context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL); //预置状态为失败
    
/*	
	//TODO
//    <Exec func="PUB:CallHostOther">   <!-- 外发同步主机非金融交易 -->
//   <Arg name="HTxnCd" value="$HTxnCd"/>   <!-- 主机交易码（必需） -->
//   <Arg name="ObjSvr" value="SHSTPUB1"/>	 <!-- 交易目标服务器名（可选） -->
// </Exec>
// <If condition="$RspCod=000000">
	
	//TEST 		TEST 		TEST 		TEST 		TEST 		TEST
	context.setData(ParamKeys.RSP_CDE, "000000");
	
	if("000000".equals(context.getData(ParamKeys.RSP_CDE))){
		
// 	<Set>OptAmt=$PayAmt</Set>
		context.setData(ParamKeys.TXN_AMOUNT, context.getData(ParamKeys.REQUEST_TXN_AMOUNT));
// TODO <Set>OptAmt1=ADDCHAR(MUL(100,$PayAmt),12,0,1)</Set>

//   <Set>ThdSts=B0</Set>
		context.setData(ParamKeys.BAK_FLD4, GDConstants.GAS_THDSTS_TYP_B0);
//   <Set>Status=1</Set>
		context.setData(ParamKeys.BAK_FLD5, "1");
//   <Set>HTxnSt=S</Set>
		context.setData(ParamKeys.MFM_TXN_STS, GDConstants.MFM_TXN_STS_S);
//   <Set>HRspCd=SC0000</Set>
		context.setData(ParamKeys.MFM_RSP_CDE, GDConstants.GAS_MFM_RSP_CD);
//   <Set>RspMsg=扣款成功</Set>
		context.setData(ParamKeys.RSP_MSG, GDConstants.PAY_SUCC);
//   <Set>ErrMsg=扣款成功</Set>
		context.setData(ParamKeys.BAK_FLD2, GDConstants.PAY_SUCC);







//       <Arg name="SqlCmd"   value="SucGastxnjnl491"/>	<!-- 更新成功流水 -->
//     </Exec>
//SQL:
//update Gastxnjnl491 set
//	TxnTyp='%s',ActNam='%s',OptAmt='%s',OptAmt1='%s',ThdSts='%s',Status='%s',HTxnCd='%s',
//	HRspCd='%s',HTxnSt='%s',HLogNo='%s',TckNo='%s',ErrMsg='%s' 
//where LogNo='%s' 
	
//	etj.setTxnTyp(context.getData(ParamKeys.TXN_TYP).toString());
//	etj.setCusNme(context.getData(ParamKeys.CUS_NME).toString());
//	etj.setTxnAmt((BigDecimal)context.getData(ParamKeys.TXN_AMOUNT));
//	etj.setBakFld3(context.getData(ParamKeys.BAK_FLD3).toString());
//	etj.setBakFld4(context.getData(ParamKeys.BAK_FLD4).toString());
//	etj.setMfmTxnCde(context.getData(GDParamKeys.MFM_TXN_CDE).toString());
//	etj.setMfmRspCde(context.getData(ParamKeys.MFM_RSP_CDE).toString());
//	etj.setMfmTxnSts(context.getData(ParamKeys.MFM_TXN_STS).toString());
	//TODO 主机流水号HLogNo 
//	
//	etj.setMfmVchNo(context.getData(ParamKeys.MFM_VCH_NO).toString());
//	etj.setBakFld2(context.getData(ParamKeys.BAK_FLD2).toString());

		EupsTransJournal etj = BeanUtils.toObject(context.getDataMap(), EupsTransJournal.class);
		logger.info("============================================");
		get(EupsTransJournalRepository.class).update(etj);

		context.setData(ParamKeys.MESSAGE_TYPE, "E");
		context.setData(ParamKeys.RSP_CDE, GDConstants.GAS_ERROR_CODE);
		context.setData(ParamKeys.RSP_MSG, "数据库处理失败!");
	}
//   <If condition="~RetCod=-1">
//     <Exec func="PUB:RollbackWork" error="IGNORE" />
//     <Set>MsgTyp=E</Set>
//     <Set>RspCod=GAS999</Set>
//     <Set>RspMsg=数据库处理失败!</Set>
//     <Return/>
//   </If>
//  </If>
	
//	}else{
//		context.setData(ParamKeys.RSP_MSG, "扣款失败");
//		throw new CoreRuntimeException("扣款失败");
//	}
	
// <Else>
// 	<Set>RspMsg=扣款失败</Set>
// 	 </Else>
// 
//</FlowCtrl>
//</Transaction>
	context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);	*/
	}
}
