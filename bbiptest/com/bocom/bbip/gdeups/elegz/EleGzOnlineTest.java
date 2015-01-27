package com.bocom.bbip.gdeups.elegz;

import org.junit.Test;

import com.bocom.bbip.eups.BaseTest;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class EleGzOnlineTest extends BaseTest{
	
	@Test
	public void executeTest() throws CoreRuntimeException, CoreException{
		Context ctx = createContext("eups.queryThirdFeeOnline");
		ctx.setData("eupsBusTyp", "ELEC01");
		ctx.setData("thdCusNo", "test1");
		ctx.setData("eleMth", "20150199");
		
		
		
		ctx.setData("txnTlr", "AFCM065");
		ctx.setData(ParamKeys.BR, "01443999999");
		
		execute(ctx);
	}

}
