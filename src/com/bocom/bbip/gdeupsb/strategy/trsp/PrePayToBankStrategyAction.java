package com.bocom.bbip.gdeupsb.strategy.trsp;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.bocom.bbip.comp.CommonRequest;
import com.bocom.bbip.comp.account.AccountService;
import com.bocom.bbip.comp.account.support.CusActInfResult;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GDEupsbTrspFeeInfo;
import com.bocom.bbip.gdeupsb.entity.GDEupsbTrspPayInfo;
import com.bocom.bbip.gdeupsb.entity.GDEupsbTrspTxnJnl;
import com.bocom.bbip.gdeupsb.repository.GDEupsbTrspFeeInfoRepository;
import com.bocom.bbip.gdeupsb.repository.GDEupsbTrspPayInfoRepository;
import com.bocom.bbip.gdeupsb.repository.GDEupsbTrspTxnJnlRepository;
import com.bocom.bbip.service.BGSPServiceAccessObject;
import com.bocom.bbip.service.Result;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
import com.bocom.jump.bp.core.Executable;

public class PrePayToBankStrategyAction implements Executable{
	
	private final static Log log = LogFactory.getLog(PrePayToBankStrategyAction.class);
	
	@Autowired
	GDEupsbTrspPayInfoRepository gdEupsTrspPayInfoRepository;
	
	@Autowired
	BGSPServiceAccessObject bgspServiceAccessObject;
	
	@Autowired
	GDEupsbTrspTxnJnlRepository gdEupsbTrspTxnJnlRepository;
	
	@Autowired
	GDEupsbTrspFeeInfoRepository gdEupsbTrspFeeInfoRepository;
	
	@Autowired
	AccountService accountService;

	
	public void execute(Context ctx) throws CoreException,CoreRuntimeException{
		log.info("PrePayToBankStrategyAction start.....");

		ctx.setData("extFields", "01444001999");
		CommonRequest comReq = new CommonRequest();
		ctx.setData(ParamKeys.BUS_TYP, Constants.BUS_TYP_2); //待缴
		//密码校验标志,0为验，1为不验
//		ctx.setData("pswCekFlg", "1");
		log.info("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!actNo="+ctx.getData(GDParamKeys.ACT_NO));
	//TODO:待验证卡号是否属于珠海分行
		
//		try {
//			CusActInfResult cusAcInf=accountService.getAcInf(comReq.build(ctx), ctx.getData(GDParamKeys.ACT_NO).toString());
//			log.info("cusAcInf="+cusAcInf);
//			if(!ctx.getData(ParamKeys.BR).equals(cusAcInf.getOpnBr())){
//				ctx.setState("BUSINESS_PROCESSNIG_STATE_TRANS_FAIL");
//				throw new CoreException("BBIP4400EU0743");
//			}
//		} catch (Exception e) {
//		}
		
//		String kkh = (true==accountService.isOurBankCard((String) ctx.getData(GDParamKeys.ACT_NO))?"0":"1");
//		if("1".equals(kkh)){
//			ctx.setState("BUSINESS_PROCESSNIG_STATE_TRANS_FAIL");
//			throw new CoreException("BBIP4400EU0743");
//		}
		
//		查询该用户是否存在已缴费未打发票的记录
		GDEupsbTrspFeeInfo gdEupsbTrspFeeInfo = new GDEupsbTrspFeeInfo();
		gdEupsbTrspFeeInfo.setBrNo(ctx.getData(ParamKeys.BK).toString());
		gdEupsbTrspFeeInfo.setCarNo(ctx.getData(GDParamKeys.CAR_NO).toString());
		gdEupsbTrspFeeInfo.setCarTyp(ctx.getData(GDParamKeys.CAR_TYP).toString());
		gdEupsbTrspFeeInfo.setStatus(GDConstants.JF);
		List<GDEupsbTrspFeeInfo> feeInfoList = gdEupsbTrspFeeInfoRepository.find(gdEupsbTrspFeeInfo);
		if(!CollectionUtils.isEmpty(feeInfoList)){
//			ctx.setData(ParamKeys.RSP_CDE, GDErrorCodes.TRSP_INVOIC_NOT_EMPTY);
//			ctx.setData(ParamKeys.RSP_MSG, "用户存在已自助缴费但未打发票的记录，请先处理");
////			
////            <Set>RspCod=RBF999</Set>
////            <Set>RspMsg=用户存在已自助缴费但未打发票的记录，请先处理</Set>
			ctx.setState("BUSINESS_PROCESSNIG_STATE_TRANS_FAIL");
			throw new CoreException("BBIP4400EU0727");
		}
		

		String card = "4";
		ctx.setData(GDParamKeys.ACT_TYP, card);
		ctx.setData(GDParamKeys.PAY_MOD, "1");

		
//		检查本地费用表是否有待缴费用
		GDEupsbTrspPayInfo gdEupsbTrspPayInfo = new GDEupsbTrspPayInfo();
		gdEupsbTrspPayInfo.setBrNo(ctx.getData(ParamKeys.BK).toString());    
		gdEupsbTrspPayInfo.setCarNo(ctx.getData(GDParamKeys.CAR_NO).toString());
		gdEupsbTrspPayInfo.setCarTyp(ctx.getData(GDParamKeys.CAR_TYP).toString());
		gdEupsbTrspPayInfo.setPayMon((String)ctx.getData(GDParamKeys.PAY_MON));
		
		gdEupsbTrspPayInfo.setTactDt((Date)ctx.getData(GDParamKeys.TACT_DT));
		
		gdEupsbTrspPayInfo.setFlg("0");
		
		List<GDEupsbTrspPayInfo> payInfoList = gdEupsTrspPayInfoRepository.find(gdEupsbTrspPayInfo);
		if(CollectionUtils.isEmpty(payInfoList)){
//			ctx.setData(ParamKeys.RSP_CDE, "478613");
//			ctx.setData(ParamKeys.RSP_MSG, "无待缴费数据");
//			
			ctx.setState("BUSINESS_PROCESSNIG_STATE_TRANS_FAIL");
			throw new CoreException("BBIP4400EU0728");
		}else{
		
			ctx.setDataMap(BeanUtils.toMap(payInfoList.get(0)));
			
		}
		
		


		GDEupsbTrspTxnJnl gdEupsbTrspTxnJnl = new GDEupsbTrspTxnJnl();
		gdEupsbTrspTxnJnl.setThdKey(ctx.getData(GDParamKeys.THD_KEY).toString());
		List<GDEupsbTrspTxnJnl> txnJnlList = gdEupsbTrspTxnJnlRepository.find(gdEupsbTrspTxnJnl);
		if(!CollectionUtils.isEmpty(txnJnlList)){
//			ctx.setData(ParamKeys.RSP_CDE, "478608");
//			ctx.setData(ParamKeys.RSP_MSG, "交易重复");
			ctx.setState("BUSINESS_PROCESSNIG_STATE_TRANS_FAIL");
			throw new CoreException("BBIP4400EU0729");
		}
		
	
		gdEupsbTrspTxnJnl = BeanUtils.toObject(ctx.getDataMap(),GDEupsbTrspTxnJnl.class);
		
		gdEupsbTrspTxnJnlRepository.insert(gdEupsbTrspTxnJnl);

	}
//	public CusActInfResult getAcInf(CommonRequest commonRequest, String cusAc,Context ctx)
//	  {
//	    Map requestData = CommonRequest.build(ctx);
//	    requestData.put("cusAc", cusAc);
//	    Result result = bgspServiceAccessObject.callServiceFlatting("queryCusActInfoProcess",requestData);
//	    if (!result.isSuccess()) {
//	      CusActInfResult cusActInfResult = new CusActInfResult();
//	      cusActInfResult.setStatus(result.getStatus());
//	      cusActInfResult.setException(result.getException());
//	      cusActInfResult.setPayload(result.getPayload());
//	      return cusActInfResult;
//	    }
//	    CusActInfResult cusActInfResult = (CusActInfResult)BeanUtils.toObject(result.getPayload(), CusActInfResult.class);
//	    return cusActInfResult;
//	  }
}
