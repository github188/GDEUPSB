package com.bocom.bbip.gdeupsb.common;

/**
 * 统一定义公共事业缴费的常量.
 * 
 * @version 1.0.0,2015-01-04
 * @author q.c
 * @modify
 * @since 1.0.0
 */
public final class GDParamKeys {

	/** 检查签到状态标志 */
	public static final String GZ_COM_CHECK_SIGN_FLAG = "checkSignFlg";

	/** 批量代扣文件名称  */
	public static final String BATCH_COM_FILE_NAME = "batchFileName";
	
	/** 批量代扣文件名称循环(自动模式)  */
	public static final String BATCH_COM_AUTO_FILE_FTP_LIST = "batchFleFtpList";
	
	/** 批量处理方式  */
	public static final String COM_BATCH_DEAL_TYP = "comBatchDealType";
	
	/** 批扣文件第三方ftp信息  */
	public static final String COM_BATCH_DEAL_FTP_INF = "thdBatFtpInf";
	
	

	/********************* 广州电力start **********************/

	/** 电费月份 **/
	public static final String GZ_ELE_PAY_MONTH = "payMonth";

	/** 电费用户帐号 **/
	public static final String GZ_ELE_CUS_AC = "cusAcEx2";

	/** 电费用户帐号 **/
	public static final String GZ_ELE_THD_PAY_TYP = "thdPayTyp3";

	/** 电费配型部类型 */
	public static final String GZ_ELE_DPT_TYP = "dptTyp";

	/** 电费对公对私标志 **/
	public static final String GZ_ELE_PAY_MDE = "payMdeFt";

	/** 交易日期时间 **/
	public static final String GZ_ELE_TXN_DTE_TME = "txnDateTime7";

	/** 银行交易流水号 **/
	public static final String GZ_ELE_TXN_JNL = "transJournal11";

	/** 受理方所在地时间 **/
	public static final String GZ_ELE_TXN_TME = "bnkTxnTime12";

	/** 受理方所在地日期 **/
	public static final String GZ_ELE_BNK_TXN_DTE = "bnkTxnDate13";

	/** 配型部类型 **/
	public static final String GZ_ELE_THD_DPT_TYP = "pwrThdSub18";

	/** 电费处理标志 **/
	public static final String GZ_ELE_DEAL_CODE = "dealCod";

	/** 电费受理方机构标识码 **/
	public static final String GZ_ELE_RCS_NO = "eleBkNo32";
	/** 电费接收机构标识码 **/
	public static final String GZ_ELE_RSC_ORG = "thdRgnNo100";
	/** 电费货币代码 **/
	public static final String GZ_ELE_CCY_COD = "CCY";
	/** 电费受理方终端标识码 **/
	public static final String GZ_ELE_TTRM_ID = "tTrmId41";
	/** 电费交易类别 **/
	public static final String GZ_ELE_TRA_TYP = "traTyp104";
	/** 电费受理方标识码 **/
	public static final String GZ_ELE_TDL_ID = "delTdlId42";
	/** 电费清算日期 **/
	public static final String GZ_ELE_TXN_DTE = "pwrtxnDate15";
	/** 帐户类型 */
	public static final String GZ_ELE_ACT_CLS = "actCls";

	/********************* 广州电力end **********************/
	
	
	
	/***********************广东烟草start*****************************/
	/***第三方规定返回码*/
    public static final String RSP_CDE = "RET_CODE";
    /***第三方规定返回信息*/
    public static final String RSP_MSG = "MSG";
    /***第三方规定原返回码*/
    public static final String RET_CODE_OLD = "RET_CODE_OLD";
    /***第三方规定原返回信息*/
    public static final String RSP_MSG_OLD = "MSG_OLD";
    /***交易功能选择*/
    public static final String TBC_TXNFLG = "txnFlg";
    /***第三方规定原交易码*/
    public static final String BANK_SEQ_OLD = "BANK_SEQ_OLD";
 
	/***********************广东烟草start*****************************/
}
