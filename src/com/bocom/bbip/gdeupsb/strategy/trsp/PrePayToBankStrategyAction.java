package com.bocom.bbip.gdeupsb.strategy.trsp;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.bocom.bbip.comp.CommonRequest;
import com.bocom.bbip.comp.account.AccountService;
import com.bocom.bbip.comp.account.support.CusActInfResult;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDErrorCodes;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GDEupsbTrspFeeInfo;
import com.bocom.bbip.gdeupsb.entity.GDEupsbTrspPayInfo;
import com.bocom.bbip.gdeupsb.entity.GDEupsbTrspTxnJnl;
import com.bocom.bbip.gdeupsb.repository.GDEupsbTrspFeeInfoRepository;
import com.bocom.bbip.gdeupsb.repository.GDEupsbTrspPayInfoRepository;
import com.bocom.bbip.gdeupsb.repository.GDEupsbTrspTxnJnlRepository;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
import com.bocom.jump.bp.core.Executable;

public class PrePayToBankStrategyAction implements Executable{
	
	private final static Log log = LogFactory.getLog(PrePayToBankStrategyAction.class);
	
	@Autowired
	GDEupsbTrspPayInfoRepository gdEupsTrspPayInfoRepository;
	
	@Autowired
	GDEupsbTrspTxnJnlRepository gdEupsbTrspTxnJnlRepository;
	
	@Autowired
	GDEupsbTrspFeeInfoRepository gdEupsbTrspFeeInfoRepository;
	
	@Autowired
	AccountService accountService;

	@SuppressWarnings("unchecked")
	public void execute(Context ctx) throws CoreException,CoreRuntimeException{
		log.info("PrePayToBankStrategyAction start.....");
//		TODO: <Call package="BRBFJUD" function="JudAreNo">    <!--检查是否珠海借记卡-->
//        <Input name="ActNo|"/>
//      </Call>
		CommonRequest comReq = new CommonRequest();
		ctx.setData(ParamKeys.BUS_TYP, Constants.BUS_TYP_2); //待缴
		//TODO:此处把密码校验标志设为1，以后还要改为0
		ctx.setData("pswCekFlg", "1");
		System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!actNo="+ctx.getData(GDParamKeys.ACT_NO));
	//TODO:待验证卡号是否属于珠海分行
//		try {
//			CusActInfResult cusAcInf=accountService.getAcInf(comReq, ctx.getData(GDParamKeys.ACT_NO).toString());
//		} catch (Exception e) {
//		}

//		查询该用户是否存在已缴费未打发票的记录
		GDEupsbTrspFeeInfo gdEupsbTrspFeeInfo = new GDEupsbTrspFeeInfo();
		gdEupsbTrspFeeInfo.setBrNo(ctx.getData(ParamKeys.BR).toString());
		gdEupsbTrspFeeInfo.setCarNo(ctx.getData(GDParamKeys.CAR_NO).toString());
		gdEupsbTrspFeeInfo.setCarTyp(ctx.getData(GDParamKeys.CAR_TYP).toString());
		gdEupsbTrspFeeInfo.setStatus(GDConstants.JF);
		List<GDEupsbTrspFeeInfo> feeInfoList = gdEupsbTrspFeeInfoRepository.find(gdEupsbTrspFeeInfo);
		if(!CollectionUtils.isEmpty(feeInfoList)){
			ctx.setData(ParamKeys.RSP_CDE, GDErrorCodes.TRSP_INVOIC_NOT_EMPTY);
			ctx.setData(ParamKeys.RSP_MSG, "用户存在已自助缴费但未打发票的记录，请先处理");
//			TODO:<Set>MsgTyp=E</Set>
//            <Set>RspCod=RBF999</Set>
//            <Set>RspMsg=用户存在已自助缴费但未打发票的记录，请先处理</Set>
			ctx.setState("BUSINESS_PROCESSNIG_STATE_TRANS_FAIL");
			throw new CoreException(ErrorCodes.EUPS_CHECK_FAIL);
		}
		
//		TODO:

//	         <Set>VchChk=1</Set><!--监督标志由业务上确定-->
//	       
//		<Set>HTxnCd=@PARA.HTxnCd_C2P</Set>
		String card = "4";
		ctx.setData(GDParamKeys.ACT_TYP, card);
		ctx.setData(GDParamKeys.PAY_MOD, "1");
		
//		检查本地费用表是否有待缴费用
		GDEupsbTrspPayInfo gdEupsbTrspPayInfo = new GDEupsbTrspPayInfo();
		 
		gdEupsbTrspPayInfo.setBrNo(ctx.getData(ParamKeys.BR).toString());    
		gdEupsbTrspPayInfo.setCarNo(ctx.getData(GDParamKeys.CAR_NO).toString());
		gdEupsbTrspPayInfo.setCarTyp(ctx.getData(GDParamKeys.CAR_TYP).toString());
		gdEupsbTrspPayInfo.setPayMon((String)ctx.getData(GDParamKeys.PAY_MON));
		String tactDte=(String)ctx.getData(GDParamKeys.TACT_DT);
//		TODO:此处需要确认业务流程，是否只有在查询费用当天才可缴费。
//		if(!tactDte.equals(null)){
//			gdEupsbTrspPayInfo.setTactDt(DateUtils.parse(tactDte));
//		}
		
		gdEupsbTrspPayInfo.setFlg("0");
		
		List<GDEupsbTrspPayInfo> payInfoList = gdEupsTrspPayInfoRepository.find(gdEupsbTrspPayInfo);
		if(CollectionUtils.isEmpty(payInfoList)){
			ctx.setData(ParamKeys.RSP_CDE, "478613s");
			ctx.setData(ParamKeys.RSP_MSG, "无待缴费数据");
			log.info("222222222222222222222222222222222222222222222222222222222222222222222222222");
			ctx.setState("BUSINESS_PROCESSNIG_STATE_TRANS_FAIL");
			throw new CoreException(ErrorCodes.EUPS_CHECK_FAIL);
		}else{
		
			ctx.setDataMap(BeanUtils.toMap(payInfoList.get(0)));
			log.info("555555555555555555555555555555555555555555555555555555555555555555555555555555555555");
		}
		
		

//		检查路桥方流水是否重复  TODO:
		GDEupsbTrspTxnJnl gdEupsbTrspTxnJnl = new GDEupsbTrspTxnJnl();
		gdEupsbTrspTxnJnl.setThdKey(ctx.getData(GDParamKeys.THD_KEY).toString());
		List<GDEupsbTrspTxnJnl> txnJnlList = gdEupsbTrspTxnJnlRepository.find(gdEupsbTrspTxnJnl);
		if(!CollectionUtils.isEmpty(txnJnlList)){
			ctx.setData(ParamKeys.RSP_CDE, "478608");
			ctx.setData(ParamKeys.RSP_MSG, "交易重复");
			ctx.setState("BUSINESS_PROCESSNIG_STATE_TRANS_FAIL");
			throw new CoreException(ErrorCodes.EUPS_CHECK_FAIL);
		}
		
		
//		TODO:
//			 <Set>Mask=STRCAT(9,$BBusTyp)</Set>

		
//		ctx.setData(ParamKeys.CCY, "0");
//		ctx.setData(GDParamKeys.TXN_MOD, "4");
//		
//        ctx.setData("brNo", "443999");
		
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"+ctx);
		gdEupsbTrspTxnJnl = BeanUtils.toObject(ctx.getDataMap(),GDEupsbTrspTxnJnl.class);
		
		gdEupsbTrspTxnJnlRepository.insert(gdEupsbTrspTxnJnl);

	}
}
