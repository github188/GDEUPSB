package com.bocom.bbip.gdeups.strategy.elcgd;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.eups.action.common.CommThdRspCdeAction;
import com.bocom.bbip.eups.adaptor.ThirdPartyAdaptor;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeups.common.GDConstants;
import com.bocom.bbip.gdeups.common.GDErrorCodes;
import com.bocom.bbip.gdeups.common.GDParamKeys;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
import com.bocom.jump.bp.core.Executable;

/**
 * 
 * @author qc.w
 *
 */
public class QueryElecUserStrategyAction implements Executable {

	private final static Logger log = LoggerFactory.getLogger(QueryElecUserStrategyAction.class);

	@Autowired
	ThirdPartyAdaptor thirdPartyAdaptor;

	@Override
	public void execute(Context context) throws CoreException, CoreRuntimeException {
		log.info("QueryElecUserStrategyAction start!..");

		String tlr = context.getData(ParamKeys.TELLER); // 柜员号
		String tmlId = context.getData(ParamKeys.TERMINAL); // 终端号
		context.setData("dealCod", GDConstants.GZ_ELE_DEAL_CODE);
		context.setData("rcsNo", GDConstants.GZ_ELE_DEAL_ORG_CODE); // 受理方机构标识码
		context.setData("rcvOrg", GDConstants.GZ_ELE_RECEIVE_ORG_CODE); // 接收机构标识码
		context.setData("ccyCod", GDConstants.GZ_ELE_CCY); // 货币代码
		context.setData("tTrmId", StringUtils.leftPad(tmlId, 8, ' ')); // 受理方终端标识码
		StringUtils.leftPad(tlr, 15, ' '); // 受理方标识码
		context.setData("traTyp", GDConstants.GZ_ELE_TXN_TYP); // 交易类别

		String thdCusNo = context.getData(ParamKeys.THD_CUS_NO).toString().trim();
		thdCusNo = StringUtils.leftPad(thdCusNo, 21, ' ');
		context.setData(ParamKeys.THD_CUS_NO, thdCusNo); // 第三方客户标识

		String payMonth = context.getData(GDParamKeys.GZ_ELE_PAY_MONTH); // 缴费月份

		// <Set>OData=STRCAT($TCusId,$LChkTm,01,SPACE(12),SPACE(56))</Set>
		String rmk = thdCusNo + payMonth + "01" + String.format("%-68s", " "); // 附加数据

		Map<String, Object> resultInfo = thirdPartyAdaptor.trade(context);
		// 获取返回码
		CommThdRspCdeAction rspCdeAction = new CommThdRspCdeAction();
		String responseCode = rspCdeAction.getThdRspCde(resultInfo, context.getData(ParamKeys.EUPS_BUSS_TYPE).toString());
		if (BPState.isBPStateOvertime(context)) {
			throw new CoreException(ErrorCodes.TRANSACTION_ERROR_TIMEOUT);
		} else if (!Constants.RESPONSE_CODE_SUCC.equals(responseCode)) {
			if (StringUtils.isEmpty(responseCode)) {
				// 未知错误
				throw new CoreException(GDErrorCodes.EUPS_ELE_GZ_UNKNOWN_ERROR);
			}
			throw new CoreException(responseCode);
		}

		// TODO:用电地址，用户名称处理
		// <Set>UsrAdd=DELSPACE(SUBSTR($UsrDat,1,SUB(GETSTRPOS($UsrDat,^),1)),all)</Set><!--
		// <Set>UsrNam=DELSPACE(SUBSTR($UsrDat,ADD(GETSTRPOS($UsrDat,^),1),SUB(STRLEN($UsrDat),GETSTRPOS($UsrDat,^))),all)</Set>

	}

}
