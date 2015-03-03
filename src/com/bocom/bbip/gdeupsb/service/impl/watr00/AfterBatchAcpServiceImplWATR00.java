package com.bocom.bbip.gdeupsb.service.impl.watr00;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.spi.service.batch.AfterBatchAcpService;
import com.bocom.bbip.eups.spi.vo.AfterBatchAcpDomain;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
/**
 * 汕头水费批量代扣后处理
 * @author hefengwen
 *
 */
public class AfterBatchAcpServiceImplWATR00 implements AfterBatchAcpService{
	
	private static Logger logger = LoggerFactory.getLogger(AfterBatchAcpServiceImplWATR00.class);

	@Override
	public void afterBatchDeal(AfterBatchAcpDomain domain, Context context)	throws CoreException {
		logger.info("AfterBatchAcpServiceImplWATR00 start ... ...");
		
		logger.info("AfterBatchAcpServiceImplWATR00 end ... ...");
	}

}
