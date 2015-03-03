package com.bocom.bbip.gdeupsb.service.impl.prof00;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.gdeupsb.entity.GdeupsTyfInvRec;
import com.bocom.bbip.gdeupsb.repository.GdeupsTyfInvRecRepository;
import com.bocom.bbip.thd.org.apache.commons.collections.CollectionUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
/**
 * 路桥凭条打印查询
 * @author hefengwen
 *
 */
public class QueryPrintInvoiceServiceActionPROF00 extends BaseAction {
	
	private static Logger logger = LoggerFactory.getLogger(QueryPrintInvoiceServiceActionPROF00.class);
	
	@Override
	public void execute(Context context) throws CoreException,	CoreRuntimeException {
		logger.info("QueryPrintInvoiceServiceActionPROF00 start ... ...");
		String carNo = context.getData("carNo");
		String carType = context.getData("carType");
		String actNo = context.getData("actNo");
		logger.info("carNo["+carNo+"]carType["+carType+"]actNo["+actNo+"]");
		carNo = new StringBuilder("粤").append(carNo).toString();
		GdeupsTyfInvRec gdeupsTyfInvRec = new GdeupsTyfInvRec();
		gdeupsTyfInvRec.setTmp01(carNo);
		gdeupsTyfInvRec.setTmp02(carType);
		gdeupsTyfInvRec.setActNo(actNo);
		List<GdeupsTyfInvRec> gdeupsTyfInvRecs = get(GdeupsTyfInvRecRepository.class).find(gdeupsTyfInvRec);
		if(CollectionUtils.isEmpty(gdeupsTyfInvRecs)){
			logger.error("没有缴费记录");
			throw new CoreException("");
		}
		gdeupsTyfInvRec = gdeupsTyfInvRecs.get(0);
		String trDate = gdeupsTyfInvRec.getTrDate();
		String thdKey = gdeupsTyfInvRec.getThdKey();
		String mxCount = gdeupsTyfInvRec.getMxCount();
		context.setData("trDate", trDate);
		context.setData("thdKey", thdKey);
		context.setData("mxCount", mxCount);
		logger.info("QueryPrintInvoiceServiceActionPROF00 end ... ...");
	}

}
