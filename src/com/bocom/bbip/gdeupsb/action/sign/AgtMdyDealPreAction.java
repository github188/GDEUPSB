package com.bocom.bbip.gdeupsb.action.sign;

import com.bocom.bbip.comp.CommonRequest;
import com.bocom.bbip.comp.account.AccountService;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDErrorCodes;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GdsRunCtl;
import com.bocom.bbip.gdeupsb.repository.GdsRunCtlRepository;
import com.bocom.bbip.gdeupsb.utils.CodeSwitchUtils;
import com.bocom.bbip.service.Result;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

/**
 * 签约一站通 -协议维护数据公共校验
 * 
 * @author qc.w
 * 
 */
public class AgtMdyDealPreAction extends BaseAction {

	@Override
	public void execute(Context context) throws CoreException, CoreRuntimeException {
		log.info("AgtMdyDealPreAction start!..");

		// br,bk转换
		String oldBr = context.getData("br");
		String oldBk = context.getData("bk");
		context.setData("nodNo", oldBr);
		context.setData("brno", oldBk);

		log.info("当前设值之后的context=" + context.getDataMap());

		GdsRunCtlRepository gdsRunCtlRepository = get(GdsRunCtlRepository.class);
		AccountService accountService = get(AccountService.class);

		String func = context.getData(GDParamKeys.SIGN_STATION_FUNC); // 功能码
		String gdsBid = context.getData(GDParamKeys.SIGN_STATION_BID); // 业务类型
		String actTyp = context.getData("actTyp"); // 账户性质
		context.setData("gdsBid", gdsBid); // 字段转换
		String txnCnl = null;

		String chl = context.getData(ParamKeys.CHANNEL); // 渠道标志
		txnCnl = CodeSwitchUtils.codeGenerator("TxnSrcToTxnCnl", chl);
		if (null == txnCnl) {
			txnCnl = "0";
		}
		GdsRunCtl gdsRunCtl = gdsRunCtlRepository.findOne(gdsBid);
		if (null == gdsRunCtl) {
			throw new CoreException(GDErrorCodes.EUPS_SIGN_GDSBID_NOT_EXIST);
		}
		context.setVariable(GDParamKeys.SIGN_STATION_RUN_CTL_INFO, gdsRunCtl); // 将签约控制信息表信息放到context中，便于后续取值

		// TODO:UnPackPrvData -目测是解包

		if (!GDConstants.SIGN_STATION_AGT_FUNC_QUERY.equals(func)) { // 若不是查询的话，则需要根据类型进行判断

			if ("B".equals(actTyp)) { // 地方财政,
				// TODO:<If
				// condition="AND(INTCMP(STRLEN(TRIM($Txnsrc,all)),3,0),IS_EQUAL_STRING($ActTyp,0),IS_EQUAL_STRING($ActTyp,A),IS_EQUAL_STRING($ActTyp,B),IS_EQUAL_STRING($ActTyp,9))">需要检查授权
				String authTlr = context.getData(ParamKeys.AUTHOR_LEVEL);
				if (StringUtils.isEmpty(authTlr)) {
					throw new CoreException(ErrorCodes.EUPS_CANCEL_CHECK_AUTH_FAIL);
				}
			}
			else if (Constants.AC_TYP_05.equals(actTyp)) { // 卡
				String cardNo = context.getData("actNo"); // 卡号
				String psw = context.getData("pin"); // 密码

				// TODO:AND(INTCMP($TxnCnl,4,6),IS_EQUAL_STRING($TTxnCd,469901))
				// 手机银行时需要验证密码
				if ("6" == txnCnl) {
					// 验密
					log.info("当前为手机银行，进行密码校验！..");
					Result auth = accountService.auth(CommonRequest.build(context), cardNo, psw);
					log.info("check pwd end");
					if (!auth.isSuccess()) {
						log.info("check pwd eroor");
						throw new CoreException(GDErrorCodes.EUPS_PASSWORD_ERROR); // 密码验证错误
					}
				}
			}
		}
		context.setData(GDParamKeys.SIGN_STATION_TXN_CNL, txnCnl);

		// 请求字段设置
		context.setVariable("BnkTyp", "16");

		log.info("all common check end!start impl deal!");
	}

}
