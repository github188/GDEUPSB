package com.bocom.bbip.gdeupsb.utils.actswitch;

import java.util.HashMap;
import java.util.Map;

import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.utils.StringUtils;

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
	public static Map<String, Object> eleActSwitch(Map<String, Object> inpara) {
		String brNo = (String) inpara.get(ParamKeys.BR); // 分行号
		
		brNo=brNo.substring(2,8);
		Integer br = Integer.valueOf(brNo);
		Map<String, Object> resultMap = new HashMap<String, Object>();

		switch (br) {
		case 441999: // 广东
			resultMap = gdActDeal(inpara);
			break;
		case 484999: // 中山
			resultMap = zsActDeal(inpara);
			break;
		case 444999: // 珠海
			resultMap = zhActDeal(inpara);
			break;
		case 446999: // 佛山
			resultMap = fsActDeal(inpara);
			break;
		case 485999: // 揭阳
			resultMap = jyActDeal(inpara);

		default:
			break;
		}

		return resultMap;
	}

	/**
	 * 广东分行帐号处理 0对公新账号外部户 1对公新账号内部户 2对私新账号 3对私卡号 4对公旧账号 5对私旧账号
	 * 6其他(可受理账号)7其他(不可受理账号); 输入参数actNo(卡号)
	 * 
	 * @param inputMap
	 * @return
	 */
	private static Map<String, Object> gdActDeal(Map<String, Object> inpara) {
		String actNo = (String) inpara.get("actNo");
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
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put(GDParamKeys.GZ_ELE_ACT_CLS, actCls);
		resultMap.put(ParamKeys.RESPONSE_CODE, responseCode);
		return resultMap;
	}

	/**
	 * 中山分行帐号处理 输入actNo帐号，newFlag转换标志
	 * 
	 * @param context
	 * @return
	 */
	private static Map<String, Object> zsActDeal(Map<String, Object> inpara) {
		String actNo = (String) inpara.get("actNo"); // 帐号
		String newFlg = (String) inpara.get("newFlag"); // 转换标志

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
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put(GDParamKeys.GZ_ELE_ACT_CLS, actCls);
		resultMap.put(ParamKeys.RESPONSE_CODE, responseCode);
		return resultMap;
	}

	/**
	 * 珠海分行账号判断: 输入参数actNo帐号
	 * 
	 * @param context
	 * @return
	 */
	private static Map<String, Object> zhActDeal(Map<String, Object> inpara) {
		String actNo = inpara.get("actNo").toString().trim(); // 帐号
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
		Map<String, Object> resusltMap = new HashMap<String, Object>();

		return resusltMap;
	}

	/**
	 * 佛山分行账号判断:输入参数actNo帐号，newFlag转换标志
	 * 
	 * @param context
	 * @return
	 */
	private static Map<String, Object> fsActDeal(Map<String, Object> inpara) {

		String actNo = (String) inpara.get("actNo"); // 帐号
		String newFlg = (String) inpara.get("newFlag"); // 转换标志

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
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put(GDParamKeys.GZ_ELE_ACT_CLS, actCls);
		resultMap.put(ParamKeys.RESPONSE_CODE, responseCode);
		return resultMap;
	}

	/**
	 * 揭阳分行账号判断
	 * 
	 * @param context
	 * @return
	 */
	private static Map<String, Object> jyActDeal(Map<String, Object> inpara) {
		String actNo = (String) inpara.get("actNo"); // 帐号

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
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put(GDParamKeys.GZ_ELE_ACT_CLS, actCls);
		resultMap.put(ParamKeys.RESPONSE_CODE, responseCode);
		return resultMap;
	}

}
