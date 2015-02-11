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

	/** 批量代扣文件名称 */
	public static final String BATCH_COM_FILE_NAME = "batchFileName";

	/** 批量代扣文件名称循环(自动模式) */
	public static final String BATCH_COM_AUTO_FILE_FTP_LIST = "batchFleFtpList";

	/** 批量处理方式 */
	public static final String COM_BATCH_DEAL_TYP = "comBatchDealType";

	/** 批扣文件第三方ftp信息 */
	public static final String COM_BATCH_DEAL_FTP_INF = "thdBatFtpInf";

	/** 代收付批扣文件名称 */
	public static final String COM_BATCH_AGT_FILE_NAME = "agtFileName";

	/** 代收付批扣文件map */
	public static final String COM_BATCH_AGT_FILE_MAP = "agtFileMap";

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

	/** 对账日期 */
	public static final String GZ_ELE_CHECK_DTE = "eleCheckDte";

	/********************* 广州电力end **********************/

	/********************* 签约一站通start **********************/
	/** 业务标识:gdsBId */
	public static final String SIGN_STATION_BID = "gdsBId";

	/** 功能标志:Func */
	public static final String SIGN_STATION_FUNC = "func";

	/** 日期:agtDat */
	public static final String SIGN_STATION_AGT_DAT = "agtDat";

	/** 协议汇总信息返回 */
	public static final String SIGN_STATION_AGT_TOT_LIST = "agtTotList";

	/** 所有业务类型相关协议明细返回 */
	public static final String SIGN_STATION_AGT_DETAIL_LIST = "agtDetailList";

	/** 签约控制信息表信息 */
	public static final String SIGN_STATION_RUN_CTL_INFO = "gdsRunCtl";

	/** 协议信息查询返回 */
	public static final String SIGN_STATION_AGT_QRY_RESULT = "agtDealQyrList";

	/** 协议维护-返回拓展标志 */
	public static final String SIGN_STATION_OEXTFLG = "oExtFg";

	/** 授权主管2 **/
	public static final String SUPER_TELLER2_ID = "sup2Id";

	/** 操作渠道txnCnl **/
	public static final String SIGN_STATION_TXN_CNL = "signTxnCnl";

	/** 签约成功的模板 **/
	public static final String GDS_RPTSUCC_CONFI = "44101_batimp_suc|44102_batimp_suc|44103_batimp_suc|44104_batimp_suc|44105_batimp_suc|44106_batimp_suc|44107_batimp_suc|44108_batimp_suc";
	/** 签约失败的模板 **/
	public static final String GDS_RPTFIAL_CONFI = "44101_batimp_fail|44102_batimp_fail|44103_batimp_fail|44104_batimp_fail|44105_batimp_fail|44106_batimp_fail|44107_batimp_fail|44108_batimp_fail";
	/** 成功的报表名称 ***/
	public static final String GDS_SUCC_REPORT = "gdmodsignsuccbillReport";
	/** 失败的报表名称 ***/
	public static final String GDS_FAIL_REPORT = "gdmodsignfailbillReport";

	public static final String GDS_BATCH_FILE = "batchSignGdsfile";

	/********************* 签约一站通end **********************/

	/********************* 福彩start **********************/

	/** 运营商编号：dealId */
	public static final String LOT_DEAL_ID = "dealId";

	/** 游戏编码:gameId */
	public static final String LOT_GAME_ID = "gameId";

	/** 清算日期:clrDat */
	public static final String LOT_CLR_DAT = "clrDat";

	/** 清算垫付金额 */
	public static final String LOT_CLEAR_PAY_AMT = "payAmt";

	/** 清算轧差金额 */
	public static final String LOT_CLEAR_DIF_AMT = "difAmt";

	/** 系统配置信息:gdLotSysCfg */
	public static final String GD_LOT_SYS_CFG = "gdLotSysCfg";

	/** 系统当前时间：curTim */
	public static final String LOT_CURTIM = "curTim";

	/** 福彩轧差处理入账帐号 */
	public static final String LOT_FC_ACT_NO = "fCActNo";

	/*********************** 广东烟草start *****************************/
	/*** 第三方规定返回码 */
	public static final String RSP_CDE = "RET_CODE";
	/*** 第三方规定返回信息 */
	public static final String RSP_MSG = "MSG";
	/*** 第三方规定原返回码 */
	public static final String RET_CODE_OLD = "RET_CODE_OLD";
	/*** 第三方规定原返回信息 */
	public static final String RSP_MSG_OLD = "MSG_OLD";
	/*** 交易功能选择 */
	public static final String TBC_TXNFLG = "txnFlg";
	/*** 第三方规定原交易码 */
	public static final String BANK_SEQ_OLD = "BANK_SEQ_OLD";

	/*********************** 广东烟草start *****************************/

	/** 缴费号 */
	public final static String PAY_NO = "payNo";
	/** 电费月份 */
	public final static String ELECTRICITY_MONTH = "electricityMonth";
	/** 查询方式 */
	public final static String QUERY_MODE = "queryMode";
	/** 业务类型 */
	public final static String EUPS_BUS_TYP = "eupsBusTyp";
	/** 结算户名称 */
	public final static String SETTLE_ACCOUNTS_NAME = "settleAccountsName";
	/** 用电地址 */
	public final static String ELECTRO_ADDRESS = "electroAddress";
	/** 扣款账户 */
	public final static String WITHHOLD_ACCOUNTS = "withholdAccounts";
	/** 扣款账户名称 */
	public final static String WITHHOLD_ACCOUNTS_NAME = "withholdAccountsName";
	/** 单位编码 */
	public final static String COMPANY_CODE = "companyCode";
	/** 费用类型 */
	public final static String COST_TYPE = "costType";
	/** 代扣银行代码 */
	public final static String WITHHOLD_BANKCODE = "withholdBankCode";
	/** 部分缴费标志 */
	public final static String PART_PAY_LOGO = "partPayLogo";
	/** 账务流水号 */
	public final static String ACCOUNTS_SERIAL_NO = "accountsSerialNo";
	/** 电费年月 */
	public final static String ELECTRICITY_YEARMONTH = "electricityYearMonth";
	/** 欠费金额 */
	public final static String ARREARAGE_MONEY = "arrearageMoney";
	/** 本金 */
	public final static String CAPITIAL = "capital";
	/** 违约金 */
	public final static String DEDIT = "dedit";
	/** 付款方式 */
	public final static String PAYMENT_TYPE = "paymentType";
	/** 实扣金额 */
	public final static String WITHHOLD_MONEY = "withholdMoney";
	/** 账户种类 */
	public final static String ACTFLG = "ActFlg";
	/** 账户凭证种类 */
	public final static String AVCHTP = "AVchTp";
	/** 存折号码 */
	public final static String VCHCOD = "VchCod";
	/** 记账账号 */
	public final static String BOKACT = "BokAct";
	/** 一本通账户序号 */
	public final static String BACTSQ = "BActSq";
	/** 销帐号 */
	public final static String RVSNO = "RvsNo";
	/** 凭证种类 */
	public final static String VCHTYP = "VchTyp";
	/** 凭证号 */
	public final static String VCHNO = "VchNo";
	/** 凭证日期 */
	public final static String BILDAT = "BilDat";
	/** 密码 */
	public final static String PINKLK = "PinBlk";
	/** 交易流水号 */
	public final static String BUS_STREAM_NO = "busStreamNo";
	/** 交易日期 */
	public final static String BUS_DATE = "busDate";
	/** 交易时间 */
	public final static String BUS_TIME = "busTime";
	/** 原交易流水号 */
	public final static String BUS_OLD_STREAM_NO = "busOldStreamNo";
	/** 原交易日期 */
	public final static String BUS_OLD_DATE = "busOldDate";
	/** 原交易时间 */
	public final static String BUS_OLD_TIME = "busOldTime";
	/** 会计流水号 */
	public final static String ACCOUNTANT_STREAM_NO = "accountantStreamNo";
	/** 缴费结果说明 */
	public final static String PAYMENT_RESULT = "paymentResult";
	/** 网点名称 */
	public final static String NET_NAME = "netName";
	/** 电费开始年月 */
	public final static String ELECTRIC_START_YEARMONTH = "electricStartYearMonth";
	/** 电费终止年月 */
	public final static String ELECTRIC_END_YEARMONTH = "electricEndYearMonth";
	/** 入账银行代码 */
	public final static String ENTER_BANKNO = "enterBankNo";
	/** 计量点编号 */
	public final static String MEASUREDOT_NO = "measureDotNo";
	/** 电费次数 */
	public final static String ELECTRIC_NO = "electricNo";
	/** 明细序号 */
	public final static String PARTICULAR_NO = "particularNo";
	/** 抄表日期 */
	public final static String COPY_LIST_DATE = "copyListDate";
	/** 本次预存 */
	public final static String BEFORE_SAVE_THISTIME = "beforeSaveThisTime";
	/** 本月示数 */
	public final static String SHOW_NUMBER_THISMONTH = "showNumberThisMonth";
	/** 上月示数 */
	public final static String SHOW_NUMBER_LASTMONTH = "showNumberLastMonth";
	/** 实用电量 */
	public final static String USE_ELECTRIC = "useElectric";
	/** 增减电量 */
	public final static String ADDREDUCE_ELECTRIC = "addReduceElectric";
	/** 单价 */
	public final static String PRICE = "price";
	/** 交费金额 */
	public final static String PAYMENT_MONEY = "paymentMoney";
	/** 交费日期 */
	public final static String PAYMENT_DATE = "paymentDate";
	/** 交费时间 */
	public final static String PAYMENT_TIME = "paymentTime";
	/** 收费人 */
	public final static String CHARGE_PERSON = "chargePerson";
	/** 总笔数 */
	public final static String AMOUNT = "amount";
	/** 总金额 */
	public final static String ALL_MONEY = "allMoney";
	/** 输入总笔数 */
	public final static String THD_AMOUNT = "thdAmount";
	/** 输入总金额 */
	public final static String THD_ALL_MONEY = "thdAllMoney";

	/** 成功笔数 */
	public final static String SUCCESS_NUMBER = "successNumber";
	/** 成功金额 */
	public final static String SUCCESS_MONEY = "successMoney";
	/** 失败笔数 */
	public final static String FAIL_NUMBER = "failNumber";
	/** 失败金额 */
	public final static String FAIL_MONEY = "failMoney";
	/** 文件名称 */
	public final static String FILE_NAME = "fileName";
	/** 文件类型 */
	public final static String FILE_TYPE = "fileType";
	/** 文件MD5 */
	public final static String FILE_MD5 = "fileMD5";
	/** 原实扣金额 */
	public final static String WITHHOLD_OLD_MONEY = "withholdOldMoney";
	/** 货币符号转换 */
	public final static String CCYCOD = "CcyCod";
	/** 冲销金额 */
	public final static String CX_MONEY = "CXMoney";
	/** 冲销原因 */
	public final static String CX_REASON = "CXReason";
	/** 抹账标志 */
	public final static String TALLY_CANCEL_SIGN = "tallyCancelSign";
	/** 操作 */
	public final static String OPERATE = "Operate";
	/**   */
	public final static String MSGTYP = "MsgTyp";
	/**   */
	public final static String RSPCOD = "RspCod";
	/**   */
	public final static String HTXNST = "HTxnSt";
	/**   */
	public final static String DTLSTS = "DtlSts";
	/**   */
	public final static String SUCFLG = "SucFlg";
	/**   */
	public final static String TOTNUM = "TotNum";
	/**   */
	public final static String RETCOD = "RetCod";
	/** 网点号 */
	public final static String NODNO = "NodNo";
	/** 来往标示 */
	public final static String OIFLAG = "OIFlag";
	/** 渠道交易前不检查 */
	public final static String VCHFLG = "VchFlg";
	/** 计量点号 中间业务代码 */
	public final static String ACCMOD = "AccMod";
	/**   */
	public final static String CCYTYP = "CcyTyp";
	/**   */
	public final static String VCHCHK = "VchChk";

	// ~~~~~~~~~~~~~~~~~报文~~~~~~~~~~~~~~~~~~~~~~~~
	/** 数据包长度 */
	public final static String BAG_LEN = "bagLen";
	/** 密钥索引 */
	public final static String SECRETKEY_INDEX = "seckeyIndex";
	/** 密钥初始向量 */
	public final static String SECRETKEY_INIT = "secKeyInit";
	/** 协议版本号 */
	public final static String TREATY_VERSION = "treVersion";
	/** 业务标识号 */
	public final static String BUS_IDENTIFY = "busIdentitfy";
	/** 交易标识号 */
	public final static String TRADE_IDENTIFY = "traIdentify";
	/** 交易人标识 */
	public final static String TRADE_PERSON_IDENTIFY = "traPerIdenty";
	/** 交易码 */
	public final static String SVRCOD = "TransCode";
	/** 数据包类型 */
	public final static String BAG_TYPE = "bagTyp";
	/** 交易发起方 */
	public final static String TRADE_START = "traStart";
	/** 交易接收方 */
	public final static String TRADE_RECEIVE = "traRec";
	/** 交易源地址 */
	public final static String TRADE_SOURCE_ADD = "traSouAdd";
	/** 交易目标地址 */
	public final static String TRADE_AIM_ADD = "traAimAdd";
	/** 交易发送日期 */
	public final static String TRADE_SEND_DATE = "traSendDate";
	/** 交易发送时间 */
	public final static String TRADE_SEND_TIME = "traSendTime";
	/** 交易优先级 */
	public final static String TRADE_PRIORITY = "traPry";
	/** 交易返回代码 */
	public final static String TRADE_RETURN_CODE = "thdRspCde";
	/** 压缩标志 */
	public final static String REDUCE_SIGN = "redSign";
	/** 预付费标志 */
	public final static String PAYMENT_IN_ADVANCE_SIGN = "payInAdvSign";
	/** 预付费金额 */
	public final static String PAYMENTIN_ADVANCE_MONEY = "payInAdvMoney";

	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~·
	/** 唯一标识码 */
	public final static String ONLYSIGNCODE = "onlySignCode";
	/** 签订标志 */
	public final static String CONSIGN = "conSign";
	/** 新签约银行代码 */
	public final static String NEWBANKNO = "newBankNo";
	/** 新签约账户 */
	public final static String NEWCUSAC = "newCusAc";
	/** 原签约账户名称 */
	public final static String CUSACNAME = "cusAcName";
	/** 新签约账户名称 */
	public final static String NEWCUSACNAME = "newCusAcName";
	/** 手机 */
	public final static String MOBPHONE = "mobPhone";
	/** E-MAIL */
	public final static String EMAIL = "email";
	/** 票据来源 */
	public final static String IVOSOURCE = "ivoSource";
	/** 票据类型 */
	public final static String IVOTYPE = "ivoType";
	/** 发票代码 */
	public final static String IVOCODE = "ivoCode";
	/** 实收金额 */
	public final static String PAYALLMONEY = "payAllMoney";
	/** 文件子包个数 */
	public final static String FILENUM = "fileNum";
	/** 达账日期 */
	public final static String CHECKDATE = "checkDate";
	/** 达账时间 */
	public final static String CHECKTIME = "checkTime";
	/** 明细文件个数 */
	public final static String DETAIL_FILE_NUM = "detailFileNum";
	/** 序号 */
	public final static String NUMBER = "number";
	/** 收费方式 */
	public final static String BUS_TYPE = "busType";

	/** 签约状态 */
	public final static String AGT_STS = "agtSts";
	/** 查询方式 */
	public final static String CHECK_TYPE = "checkType";

	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	/** 对账批次 */
	public final static String CHECK_NO = "chkNo";
	/** 交易日期 */
	public final static String TXN_DAT = "txnDat";
	/** 交易金额 */
	public final static String TXN_AMT = "txnAmt";
	/** 发票号 */
	public final static String INV_NO = "invNo";
	/** 缴费月份 */
	public final static String PAY_MON = "payMon";
	/** 车辆类型 */
	public final static String CARTYP = "carTyp";
	/** 车牌号 */
	public final static String CAR_NO = "carNo";
	/** 状态 */
	public final static String STATUE = "statue";
	/** 报表模式 */
	public final static String JOURNAL_MODEL = "journalModel";
	/** 起始日期 */
	public final static String START_DATE = "startDate";
	/** 终止日期 */
	public final static String END_DATE = "endDate";
	/** 取消标志 */
	public final static String CANCEL_SIGN = "cancelSign";
	/** 提交日期 */
	public final static String SUB_DATE = "subDte";
	/** 总笔数 */
	public final static String TOT_CNT = "totCnt";
	public final static String TOT_COUNT = "totCount";
	/** 成功笔数 */
	public final static String SUC_TOT_CNT = "sucTotCnt";
	/** 失败笔数 */
	public final static String FAL_TOT_CNT = "falTotCnt";
	/** 成功金额 */
	public final static String SUC_TOT_AMT = "sucTotAmt";
	/** 失败金额 */
	public final static String FAL_TOT_AMT = "sTfaltAmt";

	/**************** 珠海路桥字段相关 ****************/
	/** 车牌类型 */
	public final static String CAR_TYP = "carTyp";
	/** 企业日期 */
	public final static String TACT_DT = "tactDt";
	/** 缴费状态 */
	public final static String FLG = "flg";
	/** 第三方键值 */
	public final static String THD_KEY = "thdKey";
	/** 交易方式 */
	public final static String TXN_MOD = "txn_mod";
	/** 缴费单位 */
	public final static String TR_TYP = "tr_typ";
	/** 发票明细记录数 */
	public final static String MX_COUNT = "mx_count";
	/** 临时字段 */
	public final static String TMP02 = "tmp02";
	/** 临时字段 */
	public final static String TMP01 = "tmp01";
	/** 账号 */
	public final static String ACT_NO = "actNo";
	/** 交易日期 */
	public final static String TR_DATE = "tr_date";
	/** 流水号 */
	public final static String SQN = "sqn";
	/** 用户名称 */
	public final static String TCUS_NM = "tcusNm";
	/** 账号类型 */
	public final static String ACT_TYP = "actTyp";
	/** 支付方式 */
	public final static String PAY_MOD = "payMod";
	/** 主机交易流水号 */
	public final static String PAY_LOG = "payLog";
	/** 会计日期(银行核心账务日期) */
	public final static String ACT_DAT = "actDat";
	/** 第三方响应码 */
	public final static String TRSP_CD = "trspCd";
	/** 主机交易状态 */
	public final static String HTXN_ST = "htxnSt";
	/** 交易状态 */
	public final static String TXN_ST = "txnSt";
	/** 主机响应码 */
	public final static String HRSP_CD = "hrspCd";
	/** 主机抹账结果 */
	public final static String RVS_RSP = "rvsRsp";
	/** 交易渠道 */
	public final static String TXN_CNL = "txnCnl";
	/** 渠道交易码 */
	public final static String TTXN_CD = "ttxnCd";
	/** 交易码 */
	public final static String TXN_COD = "txnCod";
	/** 第三方交易状态 */
	public final static String TTXN_ST = "ttxnSt";
	/** 银行网点号 */
	public final static String NOD_NO = "nodNo";
	/** 银行柜员号 */
	public final static String TLR_ID = "tlrId";
	/** 原发票号 */
	public final static String OINV_NO = "oinvNo";
	/** 分行号 */
	public final static String BR_NO = "brNo";
	/** 开始日期 */
	public final static String BEG_DAT = "begDat";
	/** 结束日期 */
	public final static String END_DAT = "endDat";
	/** 缴费状态 */
	public final static String STATUS = "status";
	/** 总记录数 */
	public final static String totalElements = "totalElements";
	/** 打印网点号 */
	public final static String PRT_NOD = "prtNod";
	/** 打印发票流水号 */
	public final static String TLOG_NO = "tlogNo";
	/** 缴费日期 */
	public final static String PAY_DAT = "payDat";
	/** 退费流水号 */
	public final static String RVS_LOG = "rvsLog";
	/** 退费日期 */
	public final static String RVS_DAT = "rvsDat";
	/** 退费网点号 */
	public final static String RVS_NOD = "rvsNod";
	/** 退费柜员号 */
	public final static String RVS_TLR = "rvsTlr";
	/** 退费主机流水 */
	public final static String RVS_TCK = "rvsTck";
	/** 主机流水号 */
	public final static String TCK_NO = "tckNo";
	/** 对账标志(0:未处理;1:核对成功;2: 核对失败，关键信息不一致,3：银行单边帐) */
	public final static String CHK_FLG = "chkFlg";
	/** 轧账日期 */
	public final static String ZZ_DAT = "zzDat";
	/** 标识号 */
	public final static String ID_NO = "idNo";
	/** 前置交易时间 */
	public final static String FTXN_TM = "ftxnTm";

	/**************** 华商一卡通字段相关 ****************/
	public final static String TML_NO = "tmlNo";

	public static final String GDEUPSB_RSP_COD = "rspCod";
	public static final String GDEUPSB_RSP_MSG = "rspMsg";

	public static final String HZGAS_OBJ_DIR = "objDir";
	public static final String HZGAS_LCL_DIR = "lclDir";
	public static final String HZGAS_LCL_FIL = "lclFil";
	public static final String HZGAS_DAT_FIL = "datFil";
	public static final String TXN_MON = "txnMon";
	public static final String THD_RSP_CDE = "thdRspCde";
	public static final String EUPS_BUS_TYP_GAS = "PGAS00";
	public static final String HZGAS_REC_DIR = "recDir";
	public static final String HZ_GAS_BNK_BLDTM = "bldTm";
	public static final String GAS_SMPCPAY = "SMPCPAY";
	public static final String GAS_PAY_YEA = "payYea";
	public static final String MFM_TXN_CDE = "mfmTxnCde";
	public static final String GAS_TXN_AMT_1 = "txnAmt1";
	public static final String GAS_RESULT = "result";
	public static final String GAS_APL_CLS = "aplCls";
	public static final String GAS_MASK = "mask";
	public static final String MST_CHK = "mstChk";
	public static final String ERR_MSG = "errMsg";
	public static final String GAS_DSK_NME = "dskNme";

	public static final String GAS_MSG_TYP = "msgTyp";
	public static final String GAS_MSG_CDE = "msgCde";
	public static final String GAS_OFMT_COD = "oFmtCd";
	public static final String GAS_AP_CDE = "apCode";
	public static final String GAS_IN_POS = "inPos";
	public static final String GAS_RSP_COD = "rspCod";
	public static final String GAS_PAGE_NO = "pageNo";
	public static final String GAS_VAR_SIZE = "varSize";
	public static final String GAS_TXN_TTL = "ttl";
	public static final String GAS_SUB_TTL = "subTtl";
	public static final String GAS_DSK_NAM = "dskNam";

	/**************** 福彩部分 *******************/
	public static final String LOT_BET_TYP = "betTyp";
	public static final String LOT_END_DATE = "endDate";
	public static final String LOT_BEG_DATE = "begDate";
	public static final String LOT_CRD_NO = "crdNo";

	/************** 中山文件批量系统 ************/
	public static final String FBPD_TXN_OBJ = "txnObj";

}
