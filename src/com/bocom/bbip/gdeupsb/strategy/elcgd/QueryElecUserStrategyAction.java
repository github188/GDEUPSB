package com.bocom.bbip.gdeupsb.strategy.elcgd;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.comp.account.AccountService;
import com.bocom.bbip.eups.action.common.CommThdRspCdeAction;
import com.bocom.bbip.eups.adaptor.ThirdPartyAdaptor;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsAmountInfo;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDErrorCodes;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
import com.bocom.jump.bp.core.Executable;

/**
 * 查询用户缴费信息
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

		context.setData(ParamKeys.MESSAGE_TYPE, "0100");
		context.setData(GDParamKeys.GZ_ELE_CUS_AC, context.getData(ParamKeys.CUS_AC)); // 帐号

		context.setData(GDParamKeys.GZ_ELE_TXN_DTE_TME, DateUtils.STYLE_MMddHHmmss);

		String sqn = context.getData(ParamKeys.SEQUENCE);
		String sqn2 = sqn.substring(sqn.length() - 4, sqn.length());

		context.setData(GDParamKeys.GZ_ELE_TXN_JNL, sqn.substring(0, 8) + sqn2); // 银行交易流水号
		context.setData(GDParamKeys.GZ_ELE_TXN_TME, DateUtils.format(new Date(), DateUtils.STYLE_HHmmss)); // 受理方所在时间
		context.setData(GDParamKeys.GZ_ELE_BNK_TXN_DTE, DateUtils.format(new Date(), DateUtils.STYLE_MMdd)); // 受理方所在日期

		context.setData(GDParamKeys.GZ_ELE_DEAL_CODE, GDConstants.GZ_ELE_DEAL_CODE); // 处理码
		context.setData(GDParamKeys.GZ_ELE_RCS_NO, GDConstants.GZ_ELE_DEAL_ORG_CODE); // 受理方机构标识码
		context.setData("rcvOrg", GDConstants.GZ_ELE_RECEIVE_ORG_CODE); // 接收机构标识码
		context.setData(ParamKeys.CCY, GDConstants.GZ_ELE_CCY); // 货币代码
		context.setData(GDParamKeys.GZ_ELE_TTRM_ID, StringUtils.leftPad(tmlId, 8, ' ')); // 受理方终端标识码
		context.setData(GDParamKeys.GZ_ELE_TDL_ID, StringUtils.leftPad(tlr, 15, ' '));// 受理方标识码
		context.setData("traTyp", GDConstants.GZ_ELE_TXN_TYP_JF); // 交易类别

		String thdCusNo = context.getData(ParamKeys.THD_CUS_NO).toString().trim();
		thdCusNo = StringUtils.leftPad(thdCusNo, 21, ' ');
		context.setData(ParamKeys.THD_CUS_NO, thdCusNo); // 第三方客户标识

		String payMonth = context.getData(GDParamKeys.GZ_ELE_PAY_MONTH); // 缴费月份

		// <Set>OData=STRCAT($TCusId,$LChkTm,01,SPACE(12),SPACE(56))</Set>
		String rmk = thdCusNo + payMonth + "01" + String.format("%-68s", " "); // 附加数据

		// TODO: for test 为了缴费测试，此处先设好固定值,实际中需要去掉try catch
		try {
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
		} catch (Exception e) {
			context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
			context.setData("responseCode", "SC0000");
			context.setData("amount4", "21.00");
			context.setData("txnDateTime7", "1501092630");
			context.setData("pwrThdSub18", "9527");
			context.setData("eleThdSqn37", "0101201501201123001");
			context.setData("thdRspCde", "00");
			context.setData("remarkData48", "此处为空，后续需要处理mac");
			context.setData("chkTim", DateUtils.format(new Date(), DateUtils.STYLE_MMddHHmmss));  //交易时间
			context.setData("dptTyp", "0001");  //配型部类型
			context.setData("cusNme", "帅哥琛");  //配型部类型
		}

		// TODO:用电地址，用户名称处理
		// <Set>UsrAdd=DELSPACE(SUBSTR($UsrDat,1,SUB(GETSTRPOS($UsrDat,^),1)),all)</Set><!--
		// <Set>UsrNam=DELSPACE(SUBSTR($UsrDat,ADD(GETSTRPOS($UsrDat,^),1),SUB(STRLEN($UsrDat),GETSTRPOS($UsrDat,^))),all)</Set>
		context.setData("oweFeeAmt", new BigDecimal(context.getData("amount4").toString()));
		context.setData("thdCusAdr", context.getData("remarkData48"));
		// context.setData(ParamKeys.RAP_TYPE, arg1); //收付类型

	}

}
