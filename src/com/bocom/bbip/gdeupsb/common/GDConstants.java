package com.bocom.bbip.gdeupsb.common;

/**
 * 广东特色系统中的常量定义.
 * <p>
 * 
 * @version 1.0.0,2015-1-4
 * @author q.c
 * @since 1.0.0
 */
public final class GDConstants {

	/********************* 公共start **********************/

	/** 授权原因 **/
	public static final String AUTH_REASON = "authResn";

	/** 检查签到状态 **/
	public static final String COM_CHECK_FLAG_YES = "0";

	/** 不检查签到状态 **/
	public static final String COM_CHECK_FLAG_NO = "1";

	/** 发起标志-手工 **/
	public static final String COM_BATCH_DEAL_TYP_PE = "0";

	/** 发起标志-自动 **/
	public static final String COM_BATCH_DEAL_TYP_AU = "1";

	/** 代收付文本格式 **/
	public static final String COM_AGT_BATCH_FMT = "agtFileBatchFmt";

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

	/** 交易类别-缴费 **/
	public static final String GZ_ELE_TXN_TYP_JF = "JF";

	/** 交易类别-划账 **/
	public static final String GZ_ELE_TXN_TYP_HZ = "HZ";

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
	public static final String GZ_ELE_BUS_KND_ELEC = "CRP";
	
	/** 收费类型:10现金*/
	public static final String THD_PAY_TYP_CASH = "10";
	
	/** 收费类型:11支票预交*/
	public static final String THD_PAY_TYP_PRCK = "11";
	
	/** 收费类型:12支票实交*/
	public static final String THD_PAY_TYP_RLCK = "12";
	
	/** 广州电力，广州分行行号标志441999*/
	public static final String GZ_ELE_BK_GZ = "441999";
	

	/********************* 广州电力end **********************/

	/********************* 签约一站通 start **********************/
	/** func1：查询汇总 */
	public static final String SIGN_STATION_FUNC_ALL = "1";

	/** func2：查询明细 */
	public static final String SIGN_STATION_FUNC_DETAIL = "2";

	/** func3：明细 */
	public static final String SIGN_STATION_FUNC_PRINT = "3";

	/** 协议维护func0:新增 */
	public static final String SIGN_STATION_AGT_FUNC_INSERT = "0";

	/** 协议维护func2:查询 */
	public static final String SIGN_STATION_AGT_FUNC_QUERY = "2";

	/** 协议维护func1:修改 */
	public static final String SIGN_STATION_AGT_FUNC_UPDATE = "1";

	/** 协议维护-返回拓展标志Y */
	public static final String SIGN_STATION_OEXTFLG_Y = "Y";

	/** 协议维护-返回拓展标志N */
	public static final String SIGN_STATION_OEXTFLG_N = "N";

	/** 协议状态正常：0 */
	public static final String SIGN_STATION_AGT_STATUS_0 = "0";

	/** 协议第三方验证成功：1 */
	public static final String SIGN_STATION_THIRD_CHECK_SUC = "1";
	/********************* 签约一站通 end **********************/

	/********************* 福彩 相关 **********************/
	/** 运营商编号：141 */
	public static final String LOT_DEAL_ID = "141";

	/*** 返回交易失败码 */
	public static final String RSP_FAIL_COD = "9999";

	/********************* 公共end **********************/

	/********************* 广东烟草Start ***************************/
	/*** 返回码内容 */
	public static final String TBC_RSP_COD = "TBC999";

	/********************* 广东烟草end ***************************/

	/** 人民币 */
	public final static String RENMINBI = "RMB";
	/**第三方响应成功*/
	public final static String SUCCESS_CODE="000000";
	// ~~~~~~~~~~~~~~~银行代码~~~~~~~~~~~~~~~~~~~~~~~~~~~
	/** 102 中国工商银行 */
	public final static String Industrial_Commercial_Bank = "102";
	/** 103 中国农业银行 */
	public final static String Agricultural_Bank = "103";
	/** 104 中国银行 */
	public final static String China_Bank = "104";
	/** 105 中国建设银行 */
	public final static String Build_Bank = "105";
	/** 201 国家开发银行 */
	public final static String Country_Develop_Bank = "201";
	/** 202 中国进出口银行 */
	public final static String Imports_Exports_Bank = "202";
	/** 203 中国农业发展银行 */
	public final static String Agriculture_Develop_Bank = "203";
	/** 301 交通银行 */
	public final static String Communications_Bank = "301";
	/** 302 中信实业银行 */
	public final static String Industry_Bank = "302";
	/** 303 中国光大银行 */
	public final static String Light_Big_Bank = "303";
	/** 304 华夏银行 */
	public final static String HuaXia_Bank = "304";
	/** 305 中国民生银行 */
	public final static String National_Bank = "305";
	/** 306 广东发展银行 */
	public final static String GDdevelop_Bank = "306";
	/** 307 深圳发展银行 */
	public final static String SZdevelop_Bank = "307";
	/** 308 招商银行 */
	public final static String Canvass_Business_Bank = "308";
	/** 309 兴业银行 */
	public final static String XingYe_Bank = "309";
	/** 310 上海浦东发展银行 */
	public final static String SHPDdevelop_Bank = "310";
	/** 313 城市商业银行 */
	public final static String City_Commercial_Bank = "313";
	/** 314 农村商业银行 */
	public final static String Village_Commercial_Bank = "314";
	/** 401 城市信用社 */
	public final static String City_Credit = "401";
	/** 402 农村信用社 */
	public final static String Village_Credit = "402";

	// ~~~~~~~~~~~~~~~收费方式代码编码~~~~~~~~~~~~~~~~~~~~~~~~~·
	/** 供电柜台现金 */
	public final static String Electric_Cash = "010";
	/** 供电支票 */
	public final static String Electric_Check = "020";
	/** 供电汇票 */
	public final static String Electric_Draft = "030";
	/** 供电POS机 */
	public final static String Electric_POS = "040";
	/** 供电第三方支付 */
	public final static String Electric_Thd_Pay = "050";
	/** 供电发起单笔实扣 */
	public final static String ElectricOne_Withhold_Money = "060";
	/** 供电发起批量代扣 */
	public final static String ElectricLots_Withhold_Money = "070";
	/** 银行单笔代收 */
	public final static String Bank_CollectionOne = "110";
	/** 银行支票 */
	public final static String Bank_Check = "120";
	/** 银行发起托收 */
	public final static String Bank_Collection = "130";
	/** 银行发起代扣 */
	public final static String Bank_Withhold = "140";
	/** 银行第三方支付 */
	public final static String Bank_Thd_Pay = "150";

	// ~~~~~~~~~~~~~~费用类型~~~~~~~~~~~~~~~~~~~~
	/** 电费 */
	public final static String Electricity_Money = "010";
	/** 违约使用电费 */
	public final static String Electricity_DeditMoney = "020";
	/** 业扩费用 */
	public final static String BUS_Add_Cost = "030";

	// ~~~~~~~~~~~~文件类型代码~~~~~~~~~~~~~~·~~~~~~~~~~~~~~~~·
	/** 01 供电批量扣款文件 */
	/** 02 银行已扣款文件 */
	/** 03 银行对明细账文件 */
	/** 04 银行客户协议核对文件 */
	/** 05 银行查询客户信息文件 */
	/** 06 供电欠费客户文件 */
	/** 07 供电托收扣款文件 */
	/** 08 供电跨行托收扣款文件 */
	/** 09 银行代收失败日志文件 */
	/** 10 银行批量托收已扣文件 */
	/** 11 银行批量跨行托收已扣文件 */
	//~~~~~~~~~~~~~~~~~~~南方电网 报文常量~~~~~~~~~~~~~~~~~~~~~~·
	
	/** 协议版本 */
	public final static String TREATY_VERSION = "1.0.0";
	/** 交易人标识 */
	public final static String TRADE_PERSON_IDENTIFY = "301_030600";
	/** 数据包类型 */
	public final static String BAG_TYPE = "0";
	/** 交易发起方 */
	public final static String TRADE_START = "301";
	/** 交易优先 */
	public final static String TRADE_PRIORITY = "2";
	/** 压缩标志 */
	public final static String REDUCE_SIGN = "0";
	/** 交易返回代码 */
	public final static String TRADE_RETURN_CODE = "00";
	/** 网点名称 */
	public final static String NET_NAME = "@BCFG.BrNam";
	/** 密钥索引 */
	public final static String SECRETKEY_INDEX = "0";
	/** 密钥初始向量 */
	public final static String SECRETKEY_INIT = "";
	/** 交易接收方 */
	public final static String TRADE_RECEIVE = "030600";
	/** 交易源地址 */
	public final static String TRADE_SOURCE_ADD = "";
	/** 交易目标地址 */
	public final static String TRADE_AIM_ADD = "";
	
	// ~~~~~~~~~~~~~~~~~~证件类型~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	/** 01 居民身份证 */
	/** 02 临时身份证 */
	/** 03 军人身份证 */
	/** 04 护照 */
	/** 05 户口薄 */
	/** 06 营业执照 */
	/** 07 驾驶执照 */
	/** 08 法人代码证 */
	/** 09 武装警察身份证 */
	/** 10 外交人员身份证 */
	/** 11 外国人居留许可证 */
	/** 12 边民出入境通行证 */
	/** 13 港澳居民来往内地通行证-香港 */
	/** 14 港澳居民来往内地通行证-澳门 */
	/** 15 台湾居民来往大陆通行证 */
	/** 16 纳税人识别号（TIN） */
	/** 99 其他 */

	/** 路桥第三方交易码 **/
	public static final String QRY_CAR = "QryCar";

	/** 路桥缴费状态为缴费 **/
	public static final String JF = "0";
	/** 路桥缴费状态 为打票 **/
	public static final String DP = "1";

	/** 路桥缴费状态 为退费 **/
	public static final String TF = "2";
	/** 广东分行号 **/
	public static final String BR_NO = "443999";
	/** 对账标志为未处理 **/
	public static final String WCL = "0";
	
	/** 燃气扣款流水返回第三方状态 B0:扣款成功 */
	public static final String GAS_THDSTS_TYP_B0 = "B0";
	/** 燃气扣款流水返回第三方状态 B1:金额不足扣费失败 */
	public static final String GAS_THDSTS_TYP_B1 = "B1";
	/** 燃气扣款流水返回第三方状态 B2:无此帐号或账号与用户编号匹配错误扣费失败 */
	public static final String GAS_THDSTS_TYP_B2 = "B2";
	/** 燃气扣款流水返回第三方状态 B3:其它原因扣费失败 */
	public static final String GAS_THDSTS_TYP_B3 = "B3";

	public static final String GDEUPSB_TXN_SUCC_CODE = "000000";

	public static final String HZGAS_CUSDAY_FILPFX = "PLKS";

	public static final String GAS_ERROR_CODE = "GAS999";
	/** 往来标志 1：接收 */
	public static final String HZ_GAS_BNK_OIFLAG_1 = "1";
	/** 往来标志 0：发出 */
	public static final String HZ_GAS_BNK_OIFLAG_0 = "0";
	public static final String GAS_THD_ACT_NO = "491800012620190029499";
	public static final String GAS_PAY_CHL_0 = "0";
	public static final String GAS_TXN_TYP_N = "N";
	public static final String GAS_TLR_ID = "N";
	public static final String GAS_BR = "491800";
	public static final String GAS_BAT_BR = "491999";
	public static final String GAS_SMPC_TXN_CDE = "460710";
	public static final String MFM_TXN_STS_S = "S";
	public static final String GAS_MFM_RSP_CD = "SC0000";
	public static final String PAY_SUCC = "扣款成功";
	public static final String GAS_THD_TXN_CDE_SMPCPAY = "SMPCPAY";

	/** 交易状态 ： U-预计,S-成功,F-失败,T-超时,R-已冲正,C-抹账 */
	public static final String GAS_TXN_STS_U = "U";
	/** 交易状态 ： U-预计,S-成功,F-失败,T-超时,R-已冲正,C-抹账 */
	public static final String GAS_TXN_STS_S = "S";
	/** 交易状态 ： U-预计,S-成功,F-失败,T-超时,R-已冲正,C-抹账 */
	public static final String GAS_TXN_STS_F = "F";
	/** 交易状态 ： U-预计,S-成功,F-失败,T-超时,R-已冲正,C-抹账 */
	public static final String GAS_TXN_STS_T = "T";
	/** 交易状态 ： U-预计,S-成功,F-失败,T-超时,R-已冲正,C-抹账 */
	public static final String GAS_TXN_STS_R = "R";
	/** 交易状态 ： U-预计,S-成功,F-失败,T-超时,R-已冲正,C-抹账 */
	public static final String GAS_TXN_STS_C = "C";
	public static final String PGAS00 = "PGAS00";

	/*********** 中山批量文件系统 *******************/
	public static final String FDPD_RSP_COD = "481299";

	/** 燃气返回第三方状态： (B0为扣费成功 B1为金额不足扣费失败 B2为无此帐号或账号与用户编号匹配错误扣费失败 B3其它原因扣费失败) */
	public static final String THD_STS_B0 = "B0";
	public static final String THD_STS_B1 = "B1";
	public static final String THD_STS_B2 = "B2";
	public static final String THD_STS_B3 = "B3";
	/**公共批量 批次撤销调用processID*/
	public static final String BATCH_CANCEL_PROCESSID="gdeups.cancelBatch";
	/**公共批量 公共方法*/
	public static final String BATCH_FILE_COMMON_UTILS="BatchFileCommon";
	/**公共批量 代收付文件格式*/
	public static final String BATCH_FILE_FORMAT="agtFileBatchFmt";
	/**公共批量 批次状态初始状态*/
	 public static final String BBIP_PUBLIC_SERVICE="bbipPublicService";
	/**公共批量 批次状态初始状态*/
	 public static final String BATCH_STATUS_INIT="I";
	 /**公共批量 批次状态待提交状态*/
	 public static final String BATCH_STATUS_WAIT="W";
	 /**公共批量 批次状态完成状态*/
	 public static final String BATCH_STATUS_COMPLETE="S";
	 /**公共批量 批次状态已撤销状态*/
	 public static final String BATCH_STATUS_CANCEL="C";
	 /**福彩GameId 类型 双色球-5 快乐十分-7*/
	 public static final String LOTR_GAMEID_DOUBLE_COLOUR_BALL="5";
	 public static final String LOTR_GAMEID_HAPPY_TEN_MINUTE="7";
	 /** gbk编码  */
	 public static final String CHARSET_ENCODING_GBK="gbk"; 
	 
	 /*********** elec02 *******************/
		public static final String GD_ELE_APPNAM_GZ="GZ_EFE1";
		public static final String GD_ELE_APPNAM_ZH="ZH_EFE1";
		public static final String GD_ELE_APPNAM_FS="FS_EFE1";
		public static final String GD_ELE_APPNAM_ST="ST_EFE1";
		public static final String GD_ELE_APPNAM_DG="DG_EFE1";
		public static final String GD_ELE_APPNAM_ZS="ZS_EFE1";
		public static final String GD_ELE_APPNAM_JY="JY_EFE1";
		public static final String GD_ELE_APPNAM_JY2="JY_EFE2";
		public static final String GD_ELE_APPNAM_HZ="HZ_EFE1";
		public static final String GD_ELE_APPNAM_JM="JM_EFE1";
		
		public static final String GD_ELE_CAgtNo_GZ="4410000560";
		public static final String GD_ELE_CAgtNo_ZH="xxxxxxxxxx";
		public static final String GD_ELE_CAgtNo_FS="xxxxxxxxxx";
		public static final String GD_ELE_CAgtNo_ST="4450000002";
		public static final String GD_ELE_CAgtNo_DG="xxxxxxxxxx";
		public static final String GD_ELE_CAgtNo_ZS="xxxxxxxxxx";
		public static final String GD_ELE_CAgtNo_JY="4850000060";
		public static final String GD_ELE_CAgtNo_JY2="4850000061";
		public static final String GD_ELE_CAgtNo_HZ="xxxxxxxxxx";
		public static final String GD_ELE_CAgtNo_JM="xxxxxxxxxx";
		
		public static final String GD_ELE_IActNo_GZ="4410000560";
		public static final String GD_ELE_IActNo_ZH="xxxxxxxxxx";
		public static final String GD_ELE_IActNo_FS="xxxxxxxxxx";
		public static final String GD_ELE_IActNo_ST="4450000002";
		public static final String GD_ELE_IActNo_DG="xxxxxxxxxx";
		public static final String GD_ELE_IActNo_ZS="xxxxxxxxxx";
		public static final String GD_ELE_IActNo_JY="4850000060";
		public static final String GD_ELE_IActNo_JY2="4850000061";
		public static final String GD_ELE_IActNo_HZ="xxxxxxxxxx";
		public static final String GD_ELE_IActNo_JM="xxxxxxxxxx";
		
		public static final String GD_ELE_IActNm_GZ="4410000560";
		public static final String GD_ELE_IActNm_ZH="xxxxxxxxxx";
		public static final String GD_ELE_IActNm_FS="xxxxxxxxxx";
		public static final String GD_ELE_IActNm_ST="4450000002";
		public static final String GD_ELE_IActNm_DG="xxxxxxxxxx";
		public static final String GD_ELE_IActNm_ZS="xxxxxxxxxx";
		public static final String GD_ELE_IActNm_JY="4850000060";
		public static final String GD_ELE_IActNm_JY2="4850000061";
		public static final String GD_ELE_IActNm_HZ="xxxxxxxxxx";
		public static final String GD_ELE_IActNm_JM="xxxxxxxxxx";
	 
	 /*********** elec02 *******************/
		
		/*********** 珠海文本 *******************/
		 /** 管理机构代号  */
		public static final String MANAGE_ORG_445012="445012";   
		
		 /** 珠海代发工资新旧账号转换的账号开头  */
		public static final String GD_ZH_WAGE_ACTNO_PREFIX_4449="4449";  
		
		 /** 珠海代发工资新旧账号18位转换的账号开头  */
		public static final String GD_ZH_WAGE_ACTNO_PREFIX_18_63420="63420"; 
		
		 /** 珠海代发工资新旧账号20位转换的账号开头  */
		public static final String GD_ZH_WAGE_ACTNO_PREFIX_20_078="078";   
		
		/** 珠海代发工资新旧账号转换的账号结尾  */
		public static final String GD_ZH_WAGE_ACTNO_SUFFIX_202="202";  
		
		/** 珠海代发工资新旧账号转换的账号结尾  */
		public static final String GD_ZH_WAGE_ACTNO_SUFFIX_20_9999="9999"; 
		
		 /** 发送短信手机号码        */
	    public static final String ZH_FILE_SMS_MOBILE_NO="mobileNo";
	    /**  发送短信内容       */
	    public static final String ZH_FILE_SMS_MOBILE_MSG="msg";
	    /** 文件账号查询  */
		public static final String GD_ZH_OPERATE_QUERY = "0";
		/** 文件账号修改  */
		public static final String GD_ZH_OPERATE_MODIFY = "1";
		/** 文件账号新增  */
		public static final String GD_ZH_OPERATE_ADD = "2";
		/** 文件账号删除  */
		public static final String GD_ZH_OPERATE_DELETE = "3";
		/*********** 珠海文本 *******************/

		public static final String GAS_VCH_COD = "00000000";

		public static final Object GAS_NOD_NO = "491800";

		public static final String GAS_TXN_BY_CASH = "00000000";
		
		
}
