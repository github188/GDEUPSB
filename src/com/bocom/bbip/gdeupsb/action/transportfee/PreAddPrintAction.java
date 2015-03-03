package com.bocom.bbip.gdeupsb.action.transportfee;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.adaptor.ThirdPartyAdaptor;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDErrorCodes;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GDEupsbTrspFeeInfo;
import com.bocom.bbip.gdeupsb.entity.GDEupsbTrspInvChgInfo;
import com.bocom.bbip.gdeupsb.repository.GDEupsbTrspFeeInfoRepository;
import com.bocom.bbip.gdeupsb.repository.GDEupsbTrspInvChgInfoRepository;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.CollectionUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class PreAddPrintAction extends BaseAction{
	private final static Log log = LogFactory.getLog(PreAddPrintAction.class);
	
	@Autowired
	GDEupsbTrspFeeInfoRepository gdeupsbtrspfFeeInfoRepository;
	
	@Autowired
	GDEupsbTrspInvChgInfoRepository gdEupsbTrspInvChgInfoRepository;
	
	@Autowired
    ThirdPartyAdaptor callThdTradeManager;
	
	
	public void execute(Context ctx) throws CoreException,CoreRuntimeException{
		ctx.setState("fail");
		log.info("PreAddPrintAction start.......");
		ctx.setData(GDParamKeys.NOD_NO, "123");
		String oinvNo = "oinvNo"; //原发票号
		String prtFlg = "prtFlg"; //打印标志
		String oprtFlg = "oprtFlg"; //前端输入的打印标志
		ctx.setData("transCode", "484005");
		//TODO:for test:前端要传柜员号，若没有则取虚拟柜员
		ctx.setData(GDParamKeys.TLR_ID, "0356");
//		TODO:
//			<!-- 通过加锁控制批次状态 -->
//        <Set>LckKey=STRCAT(rbfb:484005,:,$TCusId,:,$CarTyp)</Set>
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
		
		Date tactDt = new Date();
		tactDt= DateUtils.parse(DateUtils.format(tactDt, "yyyy-MM-dd"));
		ctx.setData(GDParamKeys.TACT_DT, tactDt);
		
		//查询缴费记录
		GDEupsbTrspFeeInfo gdEupsbTrspFeeInfo = new GDEupsbTrspFeeInfo();
		gdEupsbTrspFeeInfo.setBrNo(GDConstants.BR_NO);
		gdEupsbTrspFeeInfo.setCarNo(ctx.getData(GDParamKeys.CAR_NO).toString());
		gdEupsbTrspFeeInfo.setCarTyp(ctx.getData(GDParamKeys.CAR_TYP).toString());
		gdEupsbTrspFeeInfo.setTactDt(tactDt);
		List<GDEupsbTrspFeeInfo> feeInfoList = gdeupsbtrspfFeeInfoRepository.findLogNo(gdEupsbTrspFeeInfo);
		System.out.println("111111111111111111111111111111111111111111111111111111111111");
//		ctx.setDataMap(BeanUtils.toMap(feeInfoList.get(0)));
		GDEupsbTrspFeeInfo a=feeInfoList.get(0);
		ctx.setData(GDParamKeys.TLOG_NO, a.getTlogNo());
		ctx.setData(oinvNo, a.getOinvNo());
		ctx.setData(GDParamKeys.STATUS, a.getStatus());
		ctx.setData(GDParamKeys.THD_KEY, a.getThdKey());
		ctx.setData(GDParamKeys.PAY_LOG, a.getPayLog());
		
		ctx.setData(GDParamKeys.PRT_NOD, a.getPrtNod());
		
		System.out.println("22222222222222222222222222222222222222222222222222222222222222222222222");
		System.out.println(ctx.getData(GDParamKeys.TACT_DT).toString());
		
		
		if(CollectionUtils.isEmpty(feeInfoList)){
			ctx.setData(ParamKeys.RSP_MSG, "无该车主的当日缴费打发票记录");
			throw new  CoreRuntimeException(ErrorCodes.EUPS_FIND_ISEMPTY);
		}else if(!GDConstants.DP.equals(ctx.getData(GDParamKeys.STATUS))){
			ctx.setData(ParamKeys.RSP_MSG, "状态信息错,此笔费用状态非打票");
			throw new CoreRuntimeException(ErrorCodes.EUPS_CHECK_TXN_STS_FAIL);
		}else if(!ctx.getData(GDParamKeys.PRT_NOD).equals(ctx.getData(GDParamKeys.NOD_NO))){
			ctx.setData(ParamKeys.RSP_MSG, "交易检查错，非原发票打印网点");
			throw new CoreRuntimeException(GDErrorCodes.EUPS_TXN_CHECK_ERROR);
		}
		
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@context="+ctx);
		String invNoValue = ctx.getData(GDParamKeys.INV_NO).toString().trim();

		String oinvNoValue = ctx.getData(oinvNo).toString().trim();
		if(!invNoValue.equals(oinvNoValue)){
			ctx.setData(prtFlg, "1");  //新发票号码
		}else{
			ctx.setData(prtFlg, "0");  //原发票号码
		}
		
		if(!ctx.getData(oprtFlg).equals(ctx.getData(prtFlg))){
			ctx.setData(ParamKeys.RSP_MSG, "发票号对应的发票标志应该为" + ctx.getData(prtFlg));
			throw new CoreRuntimeException(GDErrorCodes.EUPS_TXN_CHECK_ERROR);
		}
		ctx.setState("complete");
		if("1".equals(ctx.getData(prtFlg))){

			ctx.setState("fail");
			GDEupsbTrspInvChgInfo gdEupsbTrspInvChgInfo = BeanUtils.toObject(ctx.getDataMap(), GDEupsbTrspInvChgInfo.class);
			gdEupsbTrspInvChgInfoRepository.insert(gdEupsbTrspInvChgInfo);
			ctx.setState("callThd");
		}
		
		
	}

}
