package com.bocom.bbip.gdeupsb.action.sign;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

/**
 * 签约一站通 -签约数据维护后处理及返回
 * 
 * @author qc.w
 * 
 */
public class AgtComPackDealAction extends BaseAction {

	@Override
	public void execute(Context context) throws CoreException, CoreRuntimeException {
		log.info("AgtComPackDealAction start!..");

		String oExtFg = context.getData(GDParamKeys.SIGN_STATION_OEXTFLG); // 扩展标志
		log.info("oExtFg=" + oExtFg);
		if (GDConstants.SIGN_STATION_OEXTFLG_Y.equals(oExtFg)) {
			// TODO:<Quote name="PackPrvData"/>
		}

		String gdsBid = context.getData(GDParamKeys.SIGN_STATION_BID); // 业务标志
		if ("44103".equals(gdsBid) || "44102".equals(gdsBid)) {
			String func = context.getData(GDParamKeys.SIGN_STATION_FUNC); // 功能码
			if (GDConstants.SIGN_STATION_AGT_FUNC_UPDATE.equals(func)) {
				// TODO:异步调用本地代理协议校验交易
				// <Set>CcyCod=CNY</Set>
				// <Set>PinTyp=2</Set>
				// <Exec func="PUB:DeleteGroup" error="IGNORE">
				// <Arg name="GroupName" value="InRec"/>
				// </Exec>
				// <Exec func="PUB:CallLocal" error="IGNORE">
				// <Arg name="TxnCod" value="469911"/>
				// <Arg name="ObjSvr" value="OFRTGDS1"/>
				// </Exec>
				// <Break/>
			}

		}

		log.info("all deal end!");

	}

}
