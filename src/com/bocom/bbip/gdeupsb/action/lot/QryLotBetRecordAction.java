package com.bocom.bbip.gdeupsb.action.lot;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.adaptor.ThirdPartyAdaptor;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.entity.GdLotCusInf;
import com.bocom.bbip.gdeupsb.repository.GdLotCusInfRepository;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
/**
 * 福彩实时单笔投注查询
 * @author WangMQ
 *
 */
public class QryLotBetRecordAction extends BaseAction{
	
	private final static Logger logger = LoggerFactory.getLogger(QryLotBetRecordAction.class);


	
	public void execute(Context context)throws CoreException, CoreRuntimeException{
		logger.info("Enter in QryLotBetRecordAction!!!.....");
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
		
		//<!-- 检查客户注册信息 -->
		GdLotCusInf lotCusInfo = new GdLotCusInf();
		lotCusInfo = BeanUtils.toObject(context.getDataMap(), GdLotCusInf.class);
		lotCusInfo.setStatus("1");
		
		List<GdLotCusInf> lotCusInfoList = get(GdLotCusInfRepository.class).find(lotCusInfo);
		if(CollectionUtils.isEmpty(lotCusInfoList)){ 	//客户未注册或状态异常，返回错误
			context.setData(ParamKeys.MESSAGE_TYPE, "E");
			context.setData(ParamKeys.RSP_CDE, "LOT999");
			context.setData(ParamKeys.RSP_MSG, "客户未注册或状态异常");
			throw new CoreRuntimeException("客户未注册或状态异常");
		}
		
		context.setData("TTXNCD", "232");
		context.setData("hTxnCd", context.getData("TTXNCD"));
		context.setData("objSvr", "STHDLOTA");

		Map<String, Object> thdRspMsgMap = get(ThirdPartyAdaptor.class).trade(context);
		context.setDataMap(thdRspMsgMap);
		
		logger.info("============thdRspMsgMap=======" + thdRspMsgMap.size());
		logger.info("=========context:" + context);
		
 		if(CollectionUtils.isEmpty(thdRspMsgMap )){
 			throw new CoreRuntimeException("交易不成功");
 		}
			context.setData("msgTyp", "N");
			context.setData("rspCod", "000000");
			context.setData(ParamKeys.RSP_MSG, "交易成功");
		
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);

		
		
}
}
