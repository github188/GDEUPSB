//package com.bocom.bbip.gdeupsb.strategy.elcgd;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import com.bocom.bbip.eups.common.Constants;
//import com.bocom.bbip.eups.common.ErrorCodes;
//import com.bocom.bbip.eups.common.ParamKeys;
//import com.bocom.bbip.eups.entity.EupsThdTranCtlInfo;
//import com.bocom.bbip.eups.repository.EupsThdTranCtlInfoRepository;
//import com.bocom.bbip.gdeupsb.common.GDParamKeys;
//import com.bocom.bbip.gdeupsb.utils.CodeSwitchUtils;
//import com.bocom.bbip.utils.StringUtils;
//import com.bocom.jump.bp.core.Context;
//import com.bocom.jump.bp.core.CoreException;
//import com.bocom.jump.bp.core.CoreRuntimeException;
//import com.bocom.jump.bp.core.Executable;
//
///**
// * 单笔缴费初始化策略
// * 
// * @author qc.w
// * 
// */
//public class PreInitElecStrategyAction implements Executable {
//
//	private final static Logger log = LoggerFactory.getLogger(PreInitElecStrategyAction.class);
//
//	@Autowired
//	EupsThdTranCtlInfoRepository eupsThdTranCtlInfoRepository;
//
//	@Override
//	public void execute(Context context) throws CoreException, CoreRuntimeException {
//
//		// 设置缴费方式为1-缴费
//		if(null!=context.getData(ParamKeys.OLD_TXN_SEQUENCE)){
//			context.setData(ParamKeys.PAYFEE_TYPE, Constants.TXN_PAYFEE_TYPE_PAYMENT);
//		}
//
//		// 设置备用字段2为电费月份，保存入库
//		context.setData(ParamKeys.BAK_FLD2, context.getData("eleMonth"));
//		
//		//TODO:该字段待考虑从哪来
//		// 设置备用字段3为产品代码，保存入库
//		context.setData(ParamKeys.BAK_FLD3, context.getData("prdCde"));
//
//		String dpTyp=context.getData(GDParamKeys.GZ_ELE_DPT_TYP);
//		String comNo=CodeSwitchUtils.codeGenerator("eleGzComNoGen",dpTyp);
//		
//		log.info("after codeSwitch, dptTyp change from ["+dpTyp+"],to ["+comNo+"]");
//		
//		//TODO:支付方式paymde判断:目前根据是否传客户帐号分为现金和卡
//		String cusAc=context.getData(ParamKeys.CUS_AC);  //客户帐号判断
//		String payMde=new String();
//		if(StringUtils.isEmpty(cusAc)){
//			payMde=Constants.PAY_MDE_0;  //现金
//		}else{
//			payMde=Constants.PAY_MDE_4;  //卡
//		}
//		context.setData(ParamKeys.PAY_MDE, payMde);
//		
//		// 检查签到签退
//		EupsThdTranCtlInfo eupsThdTranCtlInfo = eupsThdTranCtlInfoRepository.findOne(comNo);
//		if (!eupsThdTranCtlInfo.isTxnCtlStsSignin()) {
//			throw new CoreException(ErrorCodes.THD_CHL_TRADE_NOT_ALLOWWED);
//		}
//		log.info("已签到，可以进行业务");
//
//	}
//}
