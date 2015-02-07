package com.bocom.bbip.gdeupsb.strategy.trsp;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.bocom.bbip.common.ErrorCodes;
import com.bocom.bbip.eups.adaptor.ThirdPartyAdaptor;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsAmountInfo;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDErrorCodes;
import com.bocom.bbip.gdeupsb.entity.GDEupsbAmountInfo;
import com.bocom.bbip.gdeupsb.repository.GDEupsbAmountInfoRepository;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
import com.bocom.jump.bp.core.Executable;

public class QryDealStrategyAction implements Executable{

	public final static Log logger = LogFactory.getLog(QryDealStrategyAction.class);
	
	@Qualifier("callThdTradeManager")
    ThirdPartyAdaptor callThdTradeManager;
	
	@Autowired
	GDEupsbAmountInfoRepository gdEupsAmountInfoRepository;
	
	public void execute(Context ctx) throws CoreException,CoreRuntimeException{
		logger.info("qryDealStrategyAction start......");
		ctx.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
		ctx.setData(ParamKeys.THD_TXN_CDE, GDConstants.QRY_CAR);
//		TODO:<Arg name="ObjSvr" value="@PARA.ThdSvr"/>
		Map<String,Object> thdReturnMessage = callThdTradeManager.trade(ctx);
		logger.info("call third start....[the state is" + ctx.getState() + "]");
		String inTRspCde = (String)thdReturnMessage.get(ParamKeys.THIRD_RETURN_CODE);
		if(!Constants.HOST_RESPONSE_CODE_SUCC.equals(inTRspCde)){
			logger.info("call third error");
			throw new CoreException(GDErrorCodes.EUPS_THD_SYS_ERROR);
		}else{
			logger.info("查询汽车费用第三方返回成功");
			ctx.setData(ParamKeys.RESPONSE_CODE, Constants.RESPONSE_CODE_SUCC);
		}
		
		GDEupsbAmountInfo gdEups = new GDEupsbAmountInfo();
		gdEups.setBr(ctx.getData(ParamKeys.BR).toString());  //网点号
		gdEups.setBakFld1(ctx.getData(ParamKeys.BAK_FLD1).toString());  //车牌类型
		gdEups.setBakFld2(ctx.getData(ParamKeys.BAK_FLD2).toString());  //车牌号
		
		gdEupsAmountInfoRepository.deleteByCarNo(gdEups);
		
		
	}
}
