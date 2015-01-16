package com.bocom.bbip.gdeups.common;

/**
 * 广东特色系统中的常量定义.
 * <p>
 * 
 * @version 1.0.0,2015-1-4
 * @author q.c
 * @since 1.0.0
 */
public class GDConstants {

	/********************* 公共start **********************/

	/** 授权原因 **/
	public static final String AUTH_REASON = "authResn";

	/********************* 公共end **********************/

	/********************* 广州电力start **********************/
	/** 处理码:查询020000 **/
	public static final String GZ_ELE_DEAL_CODE = "020000";

	/** 受理方机构标识码 **/
	public static final String GZ_ELE_DEAL_ORG_CODE = "315810";

	/** 接收机构标识码 **/
	public static final String GZ_ELE_RECEIVE_ORG_CODE = "333333";

	/** ccy **/
	public static final String GZ_ELE_CCY = "001";

	/** 交易类别 **/
	public static final String GZ_ELE_TXN_TYP = "JF";

	// /** 0:对私 **/
	// public static final String GZ_ELE_PAY_MDE_0 = "0";
	//
	// /** 1：对公 **/
	// public static final String GZ_ELE_PAY_MDE_1 = "1";

	/** 结算凭证：000：现金 **/
	public static final String GZ_ELE_PAY_KND_CASH = "000";

	/** 结算凭证：007：银行卡 **/
	public static final String GZ_ELE_PAY_KND_CARD = "007";

	/** 结算凭证：021：个人支票 **/
	public static final String GZ_ELE_PAY_KND_PRCK = "021";

	/** 结算凭证：704：活期存折 **/
	public static final String GZ_ELE_PAY_KND_ALVC = "704";

	/** 结算凭证：722：本外活本 **/
	public static final String GZ_ELE_PAY_KND_AOVC = "722";

	/** 结算凭证：117：本票 **/
	public static final String GZ_ELE_PAY_KND_OVTC = "117";

	/** 结算凭证：105：现金支票 **/
	public static final String GZ_ELE_PAY_KND_CACK = "105";

	/** 结算凭证：113：转账支票 **/
	public static final String GZ_ELE_PAY_KND_PYCK = "113";

	/** 结算凭证：115：划线支票 **/
	public static final String GZ_ELE_PAY_KND_HLCK = "115";

	/** 业务种类-代扣电费 */
	public static final String GZ_ELE_BUS_KND_ELEC = "CRP51";

	/********************* 广州电力end **********************/
}
