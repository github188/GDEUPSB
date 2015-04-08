package com.bocom.bbip.gdeupsb.action.elea;

import java.math.BigDecimal;
import java.util.List;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsActSysPara;
import com.bocom.bbip.eups.repository.EupsActSysParaRepository;
import com.bocom.bbip.gdeupsb.common.GDErrorCodes;
import com.bocom.bbip.service.BGSPServiceAccessObject;
import com.bocom.bbip.service.Result;
import com.bocom.bbip.utils.CollectionUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class EleAutoClearPayAction extends BaseAction {

	@Override
	public void execute(Context context) throws CoreException, CoreRuntimeException {
		log.info("开始进行资金划转操作!");
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
		// TODO:设置单位编号为真正的单位编号
		context.setData("comNo", "GDELEC01");

		// 查找商户号等信息
		EupsActSysPara eupsActSysPara = new EupsActSysPara();
		eupsActSysPara.setComNo((String) context.getData(ParamKeys.COMPANY_NO));
		eupsActSysPara.setActSysTyp("0"); // 代收付
		List<EupsActSysPara> resultList = get(EupsActSysParaRepository.class).find(eupsActSysPara);
		if (CollectionUtils.isNotEmpty(resultList)) {
			eupsActSysPara = resultList.get(0);
			context.setData("comNo", eupsActSysPara.getSplNo());
			context.setData(ParamKeys.POS_SPL, eupsActSysPara.getSplNo());
			context.setData(ParamKeys.POS_TML, eupsActSysPara.getSplTerNo());
			context.setData(ParamKeys.SPL_NAME, eupsActSysPara.getSplNme());
		} else {
			throw new CoreException(ErrorCodes.EUPS_ACT_SYS_PARA_INFO_ERROR);
		}
		
		context.setData("busTyp", "0"); // 缴费(业务类型)
		context.setData("tfrDir", "0"); // 转出
//		context.setData("busTyp", "1"); // 代付(业务类型)
		context.setData("busTyp", "0"); // 缴费(业务类型)
		context.setData("busKnd", "A089"); // 业务种类
//		context.setData("susSeq", "aaa"); // 内部账户挂账序号
		context.setData("ccy", "CNY");
//		context.setData("susSeq", "");   //挂账序号
		
		context.setData("comNo", "4440000009");
		context.setData("busKnd", "A079");
		context.setData("busTyp", "0");
		
		context.setData("cusAc", null);
		context.setData("tfiCusAc", "441165445018010014634"); // 收款帐号，广州电力的对公账户
		context.setData("tfiCusNme", "aaa"); // 收款帐号户名，广州电力的对公账户名称

		context.setData("fudDir", "1"); // 1-转对公账户；

		context.setData("tfrCcy", "CNY");

		String clrAmt = context.getData("clrAmt"); // 清算金额
		context.setData("tfaAmt", new BigDecimal(clrAmt));

		context.setData("tfaAmt", new BigDecimal("2.00"));
		
		context.setData("oprTyp", "1"); // 不验证违约

		Result dsResult = get(BGSPServiceAccessObject.class).callServiceFlatting("acpFundsTransfer", context.getDataMap());
		log.info("资金划转结果为:" + dsResult);
		if (!dsResult.isSuccess()) {
			context.setData("accStatus", "F");
			context.setData("responseMessage", dsResult.getResponseMessage());
			log.info("清算失败，当前的sqn=" + context.getData(ParamKeys.SEQUENCE) + "失败原因为:" + dsResult.getResponseMessage() + "，错误码为:"
					+ dsResult.getResponseCode());
			throw new CoreException(GDErrorCodes.EUPS_ELE_GZ_CLEAR_ERROR);
		} else {
			context.setData("accStatus", "S");
		}
		log.info("清算账务处理结束，准备更新相关信息");
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
	}

}
