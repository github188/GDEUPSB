package com.bocom.bbip.gdeupsb.service.impl.watr00;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsTransJournal;
import com.bocom.bbip.eups.spi.service.online.AutomaticCancelService;
import com.bocom.bbip.eups.spi.vo.CancelDomain;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.bbip.utils.NumberUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.service.id.seq.StepSequenceFactory;

/**
 * 自动区分抹账：根据流水表第三方状态和主机状态
 * @author hefengwen
 *
 */
public class AutomaticCancelServiceActionWATR00 implements	AutomaticCancelService {
	
	private static Logger logger = LoggerFactory.getLogger(AutomaticCancelServiceActionWATR00.class);


	/**
	 * 第三方抹账前处理
	 */
	@Override
	public Map<String, Object> preCancel(CancelDomain canceldomain,Context context) throws CoreException {
		logger.info("AutomaticCancelServiceActionWATR00 preCancel  start ... ...");
		// TODO :第三方抹帐前报文接口字段处理
		context.setData("type", "Y003");
		context.setData("accountdate", DateUtils.format((Date)context.getData(ParamKeys.AC_DATE), DateUtils.STYLE_yyyyMMdd));
		
		StepSequenceFactory s = context.getService("logNoService");
		String logNo = s.create().toString();
		context.setData("waterno", "JH"+logNo);//流水号生成
		
		context.setData("bankcode", "JT");
		context.setData("salesdepart",((String)context.getData(ParamKeys.BR)).substring(2, 8));
		context.setData("salesperson", ((String)context.getData(ParamKeys.TELLER)).substring(4, 7));
		context.setData("busitime", DateUtils.format(new Date(),DateUtils.STYLE_yyyyMMddHHmmss));
		context.setData("thdRspCde", "0");
		context.setData("zprice", "");
		context.setData("months", "");
		context.setData("operano", "");
		context.setData("password", "        ");
		context.setData("md5digest", " ");
		
		
		context.setData("hno", context.getData("thdCusNo"));
		BigDecimal txnAmt = context.getData("txnAmt");
		context.setData("je", NumberUtils.yuanToCentString(txnAmt));
		context.setData("lsh", context.getData("thdSqn"));
		logger.info("AutomaticCancelServiceActionWATR00 preCancel  end ... ...");
		return null;
	}
	/**
	 * 第三方抹账后处理
	 */
	@Override
	public Map<String, Object> aftCancel(CancelDomain canceldomain,Context context) throws CoreException {
		logger.info("AutomaticCancelServiceActionWATR00 aftCancel  start ... ...");
		EupsTransJournal eupsTransJournal = context.getData("lclJnlList");
		if(eupsTransJournal==null){
			eupsTransJournal = new EupsTransJournal();
			String oldTxnSqn = (String)context.getData("oldTxnSqn");
			eupsTransJournal.setOldTxnSqn(oldTxnSqn.trim());
	        context.setData("lclJnlList", eupsTransJournal);
		}
		context.setData("txnSqn", context.getData("sqn"));
		logger.info("AutomaticCancelServiceActionWATR00 aftCancel  end ... ...");
		return null;
	}

}
