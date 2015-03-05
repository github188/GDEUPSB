package com.bocom.bbip.gdeupsb.strategy.efek.cancelFeeOnline;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.repository.EupsThdTranCtlDetailRepository;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
import com.bocom.jump.bp.core.Executable;
/**
 * @author liyawei 
 */
public class AftCclToTHDAction implements Executable {
	private final static Log logger=LogFactory.getLog(AftCclToTHDAction.class);
	@Autowired
	EupsThdTranCtlDetailRepository eupsThdTranCtlDetailRepository;
	/**
	 * 联机单边抹账  后处理
	 */
	@Override
	public void execute(Context context) throws CoreException,
			CoreRuntimeException {
			logger.info("=========Start AftCclToTHDAction");
			 context.setData(ParamKeys.TXN_DTE, DateUtils.format((Date)context.getData(ParamKeys.TXN_DTE),DateUtils.STYLE_yyyyMMdd));
			 context.setData(GDParamKeys.BAG_TYPE, "1");
	}
}
