package com.bocom.bbip.gdeupsb.action.efek;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.entity.EupsThdTranCtlInfo;
import com.bocom.bbip.eups.repository.EupsThdTranCtlInfoRepository;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class EfekStartAction extends BaseAction{
	private final static Log logger = LogFactory.getLog(AdvanceTradeAction.class);
	@Autowired
	EupsThdTranCtlInfoRepository eupsThdTranCtlInfoRepository;
	public void execute(Context context)throws CoreException, CoreRuntimeException{
		logger.info("===========Start  EfekStartAction");
			//状态
	        String txnTyp = (String)context.getData("txnTyp");
	        String comNo=context.getData("comNo").toString().trim();
	        
	        EupsThdTranCtlInfo euspThdTranCtlInfo=eupsThdTranCtlInfoRepository.findOne(comNo);
	        String txnCtlSts=euspThdTranCtlInfo.getTxnCtlSts();
	        if(txnCtlSts.equals(txnTyp)){
	        		throw new CoreException("供电已签到");
	        }else{
	        		euspThdTranCtlInfo.setTxnCtlSts("0");
	        		eupsThdTranCtlInfoRepository.update(euspThdTranCtlInfo);
	        }
	        logger.info("===========End  EfekStartAction");
	  }
}
