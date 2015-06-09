package com.bocom.bbip.gdeupsb.action.sign;

import com.bocom.bbip.comp.CommonRequest;
import com.bocom.bbip.comp.account.AccountService;
import com.bocom.bbip.comp.account.support.CusActInfResult;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class AgtQryActInf extends BaseAction {

	@Override
	public void execute(Context context) throws CoreException, CoreRuntimeException {
		log.info("开始查询账户信息");
		String actTyp = context.getData("actTyp");// 账户性质
		if (StringUtils.isEmpty(actTyp)) { // 若账户类型不存在，则默认为卡(默认为手机银行发起)
			log.info("未检测到账户类型，默认为手机银行(个人网银)发起");
			actTyp = "4";
			context.setData(ParamKeys.ACT_NO, context.getData(ParamKeys.CUS_AC));
		}
		if ("0".equals(actTyp) || "A".equals(actTyp) || "B".equals(actTyp)) { //

		} else if ("9".equals(actTyp)) {// 内部账户

		} else if ("1".equals(actTyp) || "2".equals(actTyp) || "4".equals(actTyp)) { // 一本通,存折,卡
			String cusAc = context.getData(ParamKeys.ACT_NO);
//			SwitchActInfo sa = new SwitchActInfo();
			CusActInfResult actInf = get(AccountService.class).getAcInf(CommonRequest.build(context), cusAc);
			log.info("返回的信息为:actInf" + actInf);
			context.setData("actNo", cusAc); // 卡号
			context.setData("ccyCod", "CNY"); // 币种
			context.setData("actTyp", "4"); // 卡类型
			context.setData("bal", actInf.getActBal()); // 余额
			context.setData("HldAmt", cusAc);
			context.setData("actNo", cusAc);
			context.setData("cusNme", actInf.getAcNme().trim()); // 户名
			context.setData("idNo", actInf.getIdNo().trim()); // 证件号
//			context.setData("idType", actInf.getIdTyp()); // 证件类型
			log.info("=======this idType aft getAcInf By CusAc : " + actInf.getIdTyp().trim());
			context.setData("idType", get(SwitchActInfo.class).getIdTypeInMap(actInf.getIdTyp().trim()));
			
			String bCusNo = actInf.getCusNo().trim();
			if(bCusNo.length()>13){ //截取后13位
				bCusNo = bCusNo.substring(bCusNo.length()-13);
			}
			context.setData("bCusNo", bCusNo); // 客户号
		}
	}
}
