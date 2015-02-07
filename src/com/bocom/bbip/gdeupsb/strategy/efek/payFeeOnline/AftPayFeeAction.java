package com.bocom.bbip.gdeupsb.strategy.efek.payFeeOnline;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
import com.bocom.jump.bp.core.Executable;

public class AftPayFeeAction implements Executable{
	private final static Log logger=LogFactory.getLog(AftPayFeeAction.class);
	/**
	 * 记账后处理
	 */
	@Override
	public void execute(Context context) throws CoreException,
			CoreRuntimeException {
		// TODO Auto-generated method stub
		logger.info("============Start AftPayFeeAction");

		String rspCod=context.getData(ParamKeys.RSP_CDE).toString().trim();
		if(rspCod.equals(Constants.HOST_RESPONSE_CODE_SUCC)){
					context.setData(ParamKeys.TXN_STS, "S");
					context.setData("ApCode", "46");
					context.setData("OFmtCd", "999");
		}else{
					throw new CoreException("~~~~~~~~~~失败~~~"+context.getData(GDParamKeys.SUCFLG));
		}
	}
}
