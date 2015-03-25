package com.bocom.bbip.gdeupsb.strategy.vech;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bocom.bbip.eups.common.BPState;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;


public class CancelVECHTicketAction {
	
	private Logger logger = LoggerFactory.getLogger(CancelVECHTicketAction.class);
	
	
	public void execute(Context context) throws CoreException{
		logger.info("Enter in CancelVECHTicketAction!.....");
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);

		//TODO 判断可操作时间，必须在班次时间前（发车前） 
		// 取消车票则执行以下操作
		//TODO 抹帐（不一定是购票当天， how to do ?）	 【每个工作日18:00 为日切时间，系统会自动发起日终清算】
		//TODO 更新EUPS流水表	BAK_FLD5为车票状态订单状态  '订单状态（0:未支付，1已支付(订单成功) 2,已作废 3：已提交支付请求）'; lyw:无需更新该状态
		//TODO 长途汽车订单表对应订单信息作废	ORDER_STATE改为2
		//TODO 班次信息表对应班次剩余票数+1	REMAIN_NUMBER +1
		
		
		
		
		
		
	}
	
	
	
}
