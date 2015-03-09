package com.bocom.bbip.gdeupsb.action.elea;

import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.action.common.CommThdRspCdeAction;
import com.bocom.bbip.eups.adaptor.ThirdPartyAdaptor;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.common.GDErrorCodes;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

/**
 * 
 * 银行发送网管信息
 * 
 * @author qc.w
 * 
 */
public class EleGzBnkCmuTestAction extends BaseAction {

	@Autowired
	ThirdPartyAdaptor thirdPartyAdaptor;

	@Override
	public void execute(Context context) throws CoreException, CoreRuntimeException {
		log.info("EleGzBnkCmuTestAction start!..");

		String sqn = get(BBIPPublicService.class).getBBIPSequence();
		String sqn1 = sqn.substring(0, 8);
		String sqn2 = sqn.substring(sqn.length() - 4, sqn.length());

		context.setData(ParamKeys.MESSAGE_TYPE, "0820");
		context.setData("txnDateTime7", DateUtils.format(new Date(), DateUtils.STYLE_MMddHHmmss)); // txnDateTime7
		context.setData("transJournal11", sqn1 + sqn2); // transJournal11
		context.setData("sndFlg33", "315810"); // 发送机构标识码
		context.setData("cmuCde70", "301"); // 线路测试
		context.setData("eupsBusTyp", "ELEC01");

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
		context.setData("responseCode", Constants.RESPONSE_CODE_SUCC);  //设置返回码为成功
		log.info("action end,dataMap=" + context.getDataMap());
	}

}
