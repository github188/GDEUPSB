package com.bocom.bbip.gdeupsb.strategy.efek.queryFeeOnline;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
import com.bocom.jump.bp.core.Executable;

public class QueryFeeAction implements Executable{
	private final static Log logger=LogFactory.getLog(QueryFeeAction.class);

	/**
	 * 欠费查询 处理前
	 */
	@Override
	public void execute(Context context) throws CoreException,
			CoreRuntimeException {
			logger.info("=========Start  CheckFeeAction ");
			context.setData(GDParamKeys.TOTNUM, "1");
			context.setData(GDParamKeys.SVRCOD, "10");
			context.setData(ParamKeys.BUS_TYP, context.getData(GDParamKeys.BUS_TYPE));
			context.setData(ParamKeys.RSV_FLD1, context.getData(GDParamKeys.ELECTRICITY_YEARMONTH));
			context.setData(ParamKeys.THD_CUS_NO, context.getData(GDParamKeys.PAY_NO));
			context.setData("PKGCNT", "000001");
//			context.setData(ParamKeys.BAK_FLD1, context.getData(GDParamKeys.PAY_NO));
	}
}
