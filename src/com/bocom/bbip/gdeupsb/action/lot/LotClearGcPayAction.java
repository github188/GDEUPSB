package com.bocom.bbip.gdeupsb.action.lot;

import java.util.HashMap;
import java.util.Map;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GdLotSysCfg;
import com.bocom.bbip.service.BGSPServiceAccessObject;
import com.bocom.bbip.service.Result;
import com.bocom.bbip.service.Status;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

/**
 * 福彩清算-垫款资金轧差处理
 * 
 * @author qc.w
 * 
 */
public class LotClearGcPayAction extends BaseAction {

	@Override
	public void execute(Context context) throws CoreException, CoreRuntimeException {
		log.info("LotClearGcPayAction start!..");

		GdLotSysCfg gdLotSysCfg = context.getVariable(GDParamKeys.GD_LOT_SYS_CFG);
		String hsActNo = gdLotSysCfg.getHsActNo(); // 华祥对公账账号
		String dscActNo = gdLotSysCfg.getDsCAgtNo(); // 代收单位编号
		String dfcActNo = gdLotSysCfg.getDfCAgtNo(); // 代发单位编号
		String fcActNo = context.getVariable(GDParamKeys.LOT_FC_ACT_NO); // 轧差入账帐号

		String difAmt = context.getData(GDParamKeys.LOT_CLEAR_DIF_AMT); // 轧差金额
		if (!"0".equals(difAmt)) {
			// 调用资金划转接口，借购彩内部户 贷福彩对公户
			Map<String, Object> inmap = new HashMap<String, Object>();
			// TODO:for test:业务种类先用测试
			inmap.put("busKnd", "test"); // 业务种类

			inmap.put("comNo", dfcActNo); // 单位编号
			inmap.put("tfrDir", "1"); // 代付
			inmap.put("tfiCusAc", fcActNo); // 收款帐号-轧差入账帐号
			inmap.put("tfiCusNme", " "); // 收款帐号户名
			inmap.put("fudDir", "1"); // 转出方向为转对公账户
			inmap.put("tfaAmt", difAmt); // 转账金额
			inmap.put("rma", "清算"); // 备注
			try {
//				Result dsResult = get(BGSPServiceAccessObject.class).callServiceFlatting("acpFundsTransfer", inmap);
//				Map<String, Object> rspMap = dsResult.getPayload();

//				if (dsResult.isSuccess()) {
					context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
					context.setData(ParamKeys.RESPONSE_CODE, Constants.RESPONSE_CODE_SUCC);
//				} else if (Status.FAIL == dsResult.getStatus()) {
//					context.setData(ParamKeys.RESPONSE_TYPE, Constants.RESPONSE_TYPE_FAIL);
//					context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
//					throw new CoreException("清算轧差失败[返回失败]");
//				} else if (Status.SEND_ERROR == dsResult.getStatus() || Status.SYSTEM_ERROR == dsResult.getStatus()) {
//					context.setData(ParamKeys.RESPONSE_TYPE, Constants.RESPONSE_TYPE_FAIL);
//					context.setData(ParamKeys.RESPONSE_CODE, ErrorCodes.TRANSACTION_ERROR_OTHER_ERROR);
//					context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
//
//					throw new CoreException("清算轧差失败[系统错误]");
//				} else if (Status.TIMEOUT == dsResult.getStatus()) {
//					context.setData(ParamKeys.RESPONSE_TYPE, Constants.RESPONSE_TYPE_FAIL);
//					context.setData(ParamKeys.RESPONSE_CODE, ErrorCodes.TRANSACTION_ERROR_TIMEOUT);
//					context.setState(BPState.BUSINESS_PROCESSNIG_STATE_UNKOWN_FAIL);
//					throw new CoreException("清算轧差失败[发送超时],请及时查询会计流水!");
//				} else {
//					context.setData(ParamKeys.RESPONSE_TYPE, Constants.RESPONSE_TYPE_FAIL);
//					context.setData(ParamKeys.RESPONSE_CODE, ErrorCodes.TRANSACTION_RESPONSE_ISNULL);
//					context.setState(BPState.BUSINESS_PROCESSNIG_STATE_UNKOWN_FAIL);
//					throw new CoreException("清算轧差失败[其他错误]");
//				}

			} catch (Exception e) {
				log.info("为了测试，此处 先不进行任何异常处理");

			}
		}

	}

}
