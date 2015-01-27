package com.bocom.bbip.gdeupsb.utils.actswitch;

import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.core.Context;

/**
 * 电力actNo转换
 * 
 * @author qc.w
 * 
 */
public class EleActSwitch {

	/**
	 * 帐号转换
	 * 
	 * @param actNo
	 * @param brNo
	 * @param inputMap
	 * @return
	 */
	public static Context eleActSwitch(Context context) {
		String brNo = context.getData(ParamKeys.BR); // 网点号

		Integer br = Integer.valueOf(brNo);

		switch (br) {
		case 441999: // 广东
			context = gdActDeal(context);
			break;
		case 484999: // 中山
			context = zsActDeal(context);
			break;
		case 444999: // 珠海
			context = zhActDeal(context);
			break;
		case 446999: // 佛山
			context = fsActDeal(context);
			break;
		case 485999: // 揭阳
			context = jyActDeal(context);

		default:
			break;
		}

		return context;
	}

	/**
	 * 广东分行帐号处理 0对公新账号外部户 1对公新账号内部户 2对私新账号 3对私卡号 4对公旧账号 5对私旧账号 6其他(可受理账号)
	 * 7其他(不可受理账号)
	 * 
	 * @param inputMap
	 * @return
	 */
	private static Context gdActDeal(Context context) {
		String actNo = context.getData("actNo"); // 帐号
		int len = actNo.length();
		String actCls = null;
		String responseCode = null;
		switch (len) {
		case 21: { // 新账号
			if ("9".equals(actNo.substring(3, 4))) {
				actCls = "2";
			}
			else {
				if ("4411".equals(actNo.substring(0, 3))) {
					actCls = "0";
				} else {
					actCls = "1";
				}
			}
			responseCode = Constants.RESPONSE_CODE_SUCC;
			break;
		}
		case 19: // 新卡号
		case 17: // 旧卡号
		case 16: { // 旧卡号
			actCls = "3";
			responseCode = Constants.RESPONSE_CODE_SUCC;
			break;
		}
		case 18: {
			actCls = "4";
			responseCode = Constants.RESPONSE_CODE_SUCC;
			break;
		}
		default: {
			actCls = "7";
			responseCode = "478617";
			break;
		}
		}
		context.setData(GDParamKeys.GZ_ELE_ACT_CLS, actCls);
		context.setData(ParamKeys.RESPONSE_CODE, responseCode);
		return context;
	}

	/**
	 * 中山分行帐号处理
	 * 
	 * @param context
	 * @return
	 */
	private static Context zsActDeal(Context context) {
		String actNo = context.getData("actNo"); // 帐号
		String newFlg = context.getData("newFlag"); // 转换标志

		int len = actNo.length();
		String actCls = null;
		String responseCode = null;
		switch (len) {
		case 21: { // 新账号
			if ("9".equals(actNo.substring(3, 4))) {
				actCls = "2";
			}
			else {
				if ("4846".equals(actNo.substring(0, 3))) {
					actCls = "0";
				} else {
					actCls = "1";
				}
			}
			responseCode = Constants.RESPONSE_CODE_SUCC;
			break;
		}
		case 20: { // 对私旧帐号
			responseCode = Constants.RESPONSE_CODE_SUCC;
			if ("075".equals(actNo.substring(0, 2))) {
				actCls = "5";
				if (StringUtils.isNotBlank(newFlg)) {
					// TODO：查询新旧帐号对账表 :savoldact
					// actNo="savoldact表中记录";
					if (true) {
						actCls = "7";
						responseCode = "478617";
					}
					if ("6".equals(actNo.substring(0, 1))) {
						actCls = "3";
						responseCode = "478617";
					}
				}
			} else {
				actCls = "7";
				responseCode = "478617";
			}
			break;
		}
		case 18: {
			actCls = "0";
			if ("60".equals(actNo.substring(0, 1))) {
				actNo = "484".concat(actNo);
			}
			responseCode = Constants.RESPONSE_CODE_SUCC;
			break;
		}

		case 19: // 新卡号
		case 17: // 旧卡号
		case 16: { // 旧卡号
			actCls = "3";
			responseCode = Constants.RESPONSE_CODE_SUCC;
			break;
		}
		default: {
			actCls = "7";
			responseCode = "478617";
			break;
		}
		}
		context.setData(GDParamKeys.GZ_ELE_ACT_CLS, actCls);
		context.setData(ParamKeys.RESPONSE_CODE, responseCode);
		return context;
	}

	/**
	 * 珠海分行账号判断
	 * 
	 * @param context
	 * @return
	 */
	private static Context zhActDeal(Context context) {
		String actNo = context.getData("actNo"); // 帐号
		actNo = actNo.trim();
		// TODO:珠海分行新旧帐号转换
		// SELECT * FROM ActNoInf444 WHERE OldAct='%s' OR ActNo='%s' FETCH FIRST
		// 1 ROWS ONLY
		if (true) {
			int len = actNo.length();
			if (21 == len) {
				if ("9".equals(actNo.substring(3, 4))) {
					// TODO:对私查询客户账户信息
					// <Set>HTxnCd=476520</Set>
					// <Set>TTxnCd=476520</Set>
					// <Set>ActTyp=2</Set>
					// <Set>CCYCOd=CNY</Set>
				} else {
					// TODO:公帐户余额查询接口
					// <Set>HTxnCd=109000</Set>
					// <Set>TTxnCd=109000</Set>
					// <Set>ActTyp=6</Set>
				}
			} else {
				if ("000".equals(actNo.substring(0, 2)) || ("60".equals(actNo.substring(0, 1))) && 15 == len) { // 旧对公帐号或卡号
					if (18 == len) { // 旧对公帐号
						actNo = "444".concat(actNo);
					} else if (15 == len) { // 老老对公帐号
						actNo = "444000".concat(actNo.substring(2, 5).concat("01").concat(actNo.substring(5, 15)));
					}
					// TODO:公帐户余额查询接口
					// <Set>HTxnCd=109000</Set>
					// <Set>TTxnCd=109000</Set>
					// <Set>ActTyp=6</Set>
				} else {
					// <Set>HTxnCd=476520</Set>
					// <Set>TTxnCd=476520</Set>
					// <Set>ActTyp=4</Set>
					// <Set>CCYCOd=CNY</Set>
				}
			}
			// TODO:上主机查询帐号信息
			// <Exec func="PUB:CallHostOther" error="IGNORE">
			// <Arg name="HTxnCd" value="$HTxnCd"/>
			// <Arg name="ObjSvr" value="SHSTPUB1"/>
			// </Exec>
			// <If condition="~RetCod=0">
			// <Exec func="PUB:InsertRecord">
			// <Arg name="TblNam" value="actnoinf444"/>
			// </Exec>
			// </If>
			// <Else> <!--帐号不存在-->
			// <Set>ActTyp=9</Set> <!--帐号不存在-->
			// </Else>

		}
		return context;
	}

	/**
	 * 佛山分行账号判断
	 * 
	 * @param context
	 * @return
	 */
	private static Context fsActDeal(Context context) {
		String actNo = context.getData("actNo"); // 帐号

		String newFlg = context.getData("newFlag"); // 转换标志

		int len = actNo.length();
		String actCls = null;
		String responseCode = null;
		switch (len) {
		case 21: { // 新账号
			if ("9".equals(actNo.substring(3, 4))) {
				actCls = "2";
			}
			else {
				if ("446".equals(actNo.substring(0, 2))) {
					actCls = "0";
				} else {
					actCls = "1";
				}
			}
			responseCode = Constants.RESPONSE_CODE_SUCC;
			break;
		}
		case 20: {
			responseCode = Constants.RESPONSE_CODE_SUCC;
			if ("371".equals(actNo.substring(0, 2))) {
				actCls = "5";
				if (StringUtils.isNotBlank(newFlg)) {
					// TODO：查询新旧帐号对账表 :savoldact
					// actNo="savoldact表中记录";
					// <If condition="~RetCod!=0">
					if (true) {
						actCls = "7";
						responseCode = "478617";
					}
				}
			} else {
				actCls = "7";
				responseCode = "478617";
			}
			break;
		}
		case 19: // 新卡号
		case 17: // 旧卡号
		case 16: { // 旧卡号
			actCls = "3";
			responseCode = Constants.RESPONSE_CODE_SUCC;
			break;
		}
		default: {
			actCls = "7";
			responseCode = "478617";
			break;
		}
		}
		context.setData(GDParamKeys.GZ_ELE_ACT_CLS, actCls);
		context.setData(ParamKeys.RESPONSE_CODE, responseCode);
		return context;
	}

	/**
	 * 揭阳分行账号判断
	 * 
	 * @param context
	 * @return
	 */
	private static Context jyActDeal(Context context) {
		String actNo = context.getData("actNo"); // 帐号

		int len = actNo.length();
		String actCls = null;
		String responseCode = null;
		switch (len) {
		case 21: { // 新账号
			if ("9".equals(actNo.substring(3, 4))) {
				actCls = "2";
			}
			else {
				if ("99".equals(actNo.substring(19, 21))) {
					actCls = "1";
				} else {
					actCls = "0";
				}
			}
			responseCode = Constants.RESPONSE_CODE_SUCC;
			break;
		}

		case 19: // 新卡号
		case 17: // 旧卡号
		case 16: { // 旧卡号
			actCls = "3";
			responseCode = Constants.RESPONSE_CODE_SUCC;
			break;
		}
		default: {
			actCls = "7";
			responseCode = "478617";
			break;
		}
		}
		context.setData(GDParamKeys.GZ_ELE_ACT_CLS, actCls);
		context.setData(ParamKeys.RESPONSE_CODE, responseCode);
		return context;
	}

}
