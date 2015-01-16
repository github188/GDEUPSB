package com.bocom.bbip.gdeups.utils.actswitch;

import java.util.HashMap;
import java.util.Map;

import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeups.common.GDParamKeys;


/**
 * 电力actNo转换
 * 
 * @author qc.w
 * 
 */
public class EleActSwitch {

	public static Map<String, Object> eleActSwitch(String actNo, String brNo) {
		return eleActSwitch(actNo, brNo, null);
	}

	/**
	 * 帐号转换
	 * 
	 * @param actNo
	 * @param brNo
	 * @param inputMap
	 * @return
	 */
	public static Map<String, Object> eleActSwitch(String actNo, String brNo, Map<String, Object> inputMap) {
		Map<String, Object> outputMap = new HashMap<String, Object>();
		Integer br = Integer.valueOf(brNo);

		switch (br) {
		case 441999:
			outputMap = gdActDeal(actNo, inputMap);

			break;

		default:
			break;
		}

		return outputMap;
	}

	/**
	 * 广东分行帐号处理
	 * 
	 * @param inputMap
	 * @return
	 */
	private static Map<String, Object> gdActDeal(String actNo, Map<String, Object> inputMap) {
		int len=actNo.length();
		String actCls=null;
		String responseCode=null;
		Map<String,Object> resultMap=new HashMap<String, Object>();
		switch (len) {
		case 21:{   //新账号
			if("9".equals(actNo.substring(3, 4))){
				actCls="2";
			}
			else{
				if("4411".equals(actNo.substring(0,3))){
					actCls="0";
				}else{
					actCls="1";
				}
			}
			responseCode=Constants.RESPONSE_CODE_SUCC;
			break;
		}
		case 19:	//新卡号
		case 17:	//旧卡号
		case 16:{	//旧卡号
			actCls="3";
			responseCode=Constants.RESPONSE_CODE_SUCC;
			break;
		}	
		case 18:{
			actCls="4";
			responseCode=Constants.RESPONSE_CODE_SUCC;
			break;
		}	
		default:{
			actCls="7";
			responseCode="478617";
		}
			break;
		}	
		
		resultMap.put(GDParamKeys.GZ_ELE_ACT_CLS, actCls);
		resultMap.put(ParamKeys.RESPONSE_CODE, Constants.RESPONSE_CODE_SUCC);
		return resultMap;

	}

}
