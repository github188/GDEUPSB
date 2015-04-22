package com.bocom.bbip.gdeupsb.action.sign;

import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.comp.BBIPPublicService;
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
	@Autowired
	BBIPPublicService bbipPublicService;
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
			//tandun add by
			String func = context.getData(GDParamKeys.SIGN_STATION_FUNC); // 功能码
			if (GDConstants.SIGN_STATION_AGT_FUNC_UPDATE.equals(func)||GDConstants.SIGN_STATION_AGT_FUNC_INSERT.equals(func)) {
				context.setData(GDParamKeys.SIGN_STATION_BID, gdsBid);
				context.setData(GDParamKeys.CCYCOD, "CNY");
				context.setData("PinTyp", "2");
				context=bbipPublicService.synExecute("gdeups.agtValidCheckProcess",context);
			}

		}

		log.info("all deal end!");

	}

}
