package com.bocom.bbip.gdeupsb.common;

/**
 * 统一定义公共事业缴费的错误码.
 * 
 * @version 1.0.0,2015-01-04
 * @author q.c
 * @modify
 * @since 1.0.0
 */
public final class GDErrorCodes {

	/** 密码错误 */
	public static final String EUPS_PASSWORD_ERROR = "BBIP4400EU1799";

	/** 批扣文件信息异常 */
	public static final String EUPS_COM_BAT_FILE_ERROR = "BBIP4400EU9900";

	/** 加锁失败 */
	public static final String EUPS_LOCK_FAIL = "BBIP4400EU9901";

	/** 解锁失败 */
	public static final String EUPS_UNLOCK_FAIL = "BBIP4400EU9902";

	/** 批扣文件解析异常 */
	public static final String EUPS_COM_FILE_PARSE_ERROR = "BBIP4400EU993";

	/** 广东电力未知错误 */
	public static final String EUPS_ELE_GZ_UNKNOWN_ERROR = "BBIP4400EU0100";

	/** 广东电力对公对私标志错误 */
	public static final String EUPS_ELE_GZ_PAY_MOD_FLAG_ERROR = "BBIP4400EU0101";

	/** 广东电力凭单号错误 */
	public static final String EUPS_ELE_GZ_TCK_ERROR = "BBIP4400EU0102";

	/** 广东电力原交易流水已抹帐 */
	public static final String EUPS_ELE_GZ_JNL_ALREADY_CANCLE = "BBIP4400EU0103";

	/** 广东电力原交易流水信息错误 */
	public static final String EUPS_ELE_GZ_CANCLE_INFO_ERROR = "BBIP4400EU0104";

	/** 广东电力不存在清算日期参数 */
	public static final String EUPS_ELE_GZ_CLEAR_INFO_ERROR = "BBIP4400EU0105";

	/** 广东电力已清算，不可发起该交易 */
	public static final String EUPS_ELE_GZ_ALREADY_CLEAR_ERROR = "BBIP4400EU0106";

	/** 广东电力不允许自动清算 */
	public static final String EUPS_ELE_GZ_AUTO_CLEAR_ERROR = "BBIP4400EU0107";
	
	/** 广东电力清算时间非法 */
	public static final String EUPS_ELE_GZ_CLEAR_TIME_ERROR = "BBIP4400EU0108";
	
	/** 广东电力不存在清算信息 */
	public static final String EUPS_ELE_GZ_CLEAR_INFO_NOT_EXIST= "BBIP4400EU0109";
	
	/** 广东电力清算错误 */
	public static final String EUPS_ELE_GZ_CLEAR_ERROR= "BBIP4400EU0110";

	/** 供电局系统:无效交易(数据格式) */
	public static final String EUPS_ELE_GZ_DATE_FORMAT_ERROR = "BBIP4400EU0190";

	/** 供电局系统:MAC校验错 */
	public static final String EUPS_ELE_GZ_MAC_ERROR = "BBIP4400EU0191";

	/** 供电局系统:系统超时 */
	public static final String EUPS_ELE_GZ_SYSTEM_TIME_OUT = "BBIP4400EU0192";

	/** 供电局系统:联机交易数据格式错 */
	public static final String EUPS_ELE_GZ_CMU_DATE_ERROR = "BBIP4400EU0193";

	/** 供电局系统:无效月份 */
	public static final String EUPS_ELE_GZ_MONTH_ERROR = "BBIP4400EU0194";

	/** 供电局系统:无欠费 */
	public static final String EUPS_ELE_GZ_NONEED_CHARG_ERROR = "BBIP4400EU0195";

	/** 供电局系统:无该用户编号 */
	public static final String EUPS_ELE_GZ_NO_CUSTMER_INFO = "BBIP4400EU0196";

	/** 供电局系统:冲正交易不成功 */
	public static final String EUPS_ELE_GZ_REVERSAL_ERROR = "BBIP4400EU0197";

	/** 供电局系统:其他故障 */
	public static final String EUPS_ELE_GZ_DEFAULT_ERROR = "BBIP4400EU0199";

	/** 签约一站通-数据库查询失败或代理业务GdsBId不存在 */
	public static final String EUPS_SIGN_GDSBID_NOT_EXIST = "BBIP4400EU1701";

	/** 签约一站通-操作选项错误 */
	public static final String EUPS_SIGN_DEAL_TYPE_ERROR = "BBIP4400EU1702";

	/** 签约一站通-数据记录错误 */
	public static final String EUPS_SIGN_RECORD_ERROR = "BBIP4400EU1703";

	/** 签约一站通-该卡不支持签约该业务 */
	public static final String EUPS_SIGN_CARD_NOT_SUPPORT = "BBIP4400EU1704";

	/** 签约一站通-用户编号长度不足10位 */
	public static final String EUPS_SIGN_CUSNO_LENGTH_ERROR = "BBIP4400EU1705";

	/** 签约一站通-用户已签约该业务种类 */
	public static final String EUPS_SIGN_AGT_EXIST = "BBIP4400EU1706";

	/** 签约一站通-系统故障 */
	public static final String EUPS_SIGN_DEFAULT_ERROR = "BBIP4400EU1707";

	/** 签约一站通-没有满足条件的记录 */
	public static final String EUPS_SIGN_NO_RECORD_FOUND = "BBIP4400EU1708";

	/** 文件名不符合规范 */
	public static final String EUPS_FILE_RULE_ERROR = "BBIP0004EU0137";
	/** 文件名中的业务类型不正确 */
	public static final String EUPS_FILE_BUSTYPE_ERROR = "BBIP0004EU0138";
	/** 检查文件头中的网点号 */
	public static final String EUPS_FILE_HEADBR_ERROR = "BBIP0004EU0139";
	/** 文件头汇总笔数与明细不一致 */
	public static final String EUPS_FILE_HEADCNT_ERROR = "BBIP0004EU0140";
	/** 文件明细第 */
	public static final String EUPS_FILE_DETAIL_ERROR = "BBIP0004EU0141";
	/** 打印报错 **/
	public static final String PRINT_REPORT_ERROR = "BBIP0004EU0142";
	/** 无打印模板 **/
	public static final String PRINT_FMTFIL_ERROR = "BBIP0004EU0143";
	/** 生成签约成功清单失败 **/
	public static final String PRINT_SUCC_ERROR = "BBIP0004EU0144";
	/** 生成签约失败清单失败 **/
	public static final String PRINT_FAIL_ERROR = "BBIP0004EU01445";
	/** 文件头的业务类型不对 **/
	public static final String EUPS_FILE_HEADSBID_ERROR = "BBIP0004EU01446";
	/** 文件头的日期不对 **/
	public static final String EUPS_FILE_HEADDATE_ERROR = "BBIP0004EU01447";

	// 汕头水费
	/** 登录数据库错 */
	public static final String EUPS_WATR00_0001_ERROR = "BBIP4400EU0401";
	/** 查询不到户号 */
	public static final String EUPS_WATR00_0002_ERROR = "BBIP4400EU0402";
	/** 该户号无欠费 */
	public static final String EUPS_WATR00_0003_ERROR = "BBIP4400EU0403";
	/** 交易时间非法 */
	public static final String EUPS_WATR00_0004_ERROR = "BBIP4400EU0404";
	/** 数据包格式错误 */
	public static final String EUPS_WATR00_0005_ERROR = "BBIP4400EU0405";
	/** 户号有非法字符 */
	public static final String EUPS_WATR00_0006_ERROR = "BBIP4400EU0406";
	/** 交易流水号非法 */
	public static final String EUPS_WATR00_0007_ERROR = "BBIP4400EU0407";
	/** 费用金额有非法字符 */
	public static final String EUPS_WATR00_0008_ERROR = "BBIP4400EU0408";
	/** 业务代码非法 */
	public static final String EUPS_WATR00_0009_ERROR = "BBIP4400EU0409";
	/** 银行代码非法 */
	public static final String EUPS_WATR00_0010_ERROR = "BBIP4400EU0410";
	/** 连接超时 */
	public static final String EUPS_WATR00_0011_ERROR = "BBIP4400EU0411";
	/** 实交金额不能小于0 */
	public static final String EUPS_WATR00_0012_ERROR = "BBIP4400EU0412";
	/** 反销金额与实交金额不符 */
	public static final String EUPS_WATR00_0013_ERROR = "BBIP4400EU0413";
	/** 未找到需反销记录 */
	public static final String EUPS_WATR00_0014_ERROR = "BBIP4400EU0414";
	/** 实交金额大于欠费 */
	public static final String EUPS_WATR00_0015_ERROR = "BBIP4400EU0415";
	/** 该记录已反销 */
	public static final String EUPS_WATR00_0016_ERROR = "BBIP4400EU0416";
	/** 实交金额小于欠费 */
	public static final String EUPS_WATR00_0017_ERROR = "BBIP4400EU0417";
	/** 委托关系已存在 */
	public static final String EUPS_WATR00_0018_ERROR = "BBIP4400EU0418";
	/** 不存在委托关系 */
	public static final String EUPS_WATR00_0019_ERROR = "BBIP4400EU0419";
	/** 该户号已缴费 */
	public static final String EUPS_WATR00_0020_ERROR = "BBIP4400EU0420";
	/** 该户号没缴费历史 */
	public static final String EUPS_WATR00_0021_ERROR = "BBIP4400EU0421";
	/** 该户号已在其它银行委托 */
	public static final String EUPS_WATR00_0022_ERROR = "BBIP4400EU0422";
	/** 该户号已销户请到供水营业厅办理缴费 */
	public static final String EUPS_WATR00_0023_ERROR = "BBIP4400EU0423";
	/** 该户已办理自来水公司同城托收业务 */
	public static final String EUPS_WATR00_0024_ERROR = "BBIP4400EU0424";
	/** 日结锁定 */
	public static final String EUPS_WATR00_9996_ERROR = "BBIP4400EU0425";
	/** 扣款锁定 */
	public static final String EUPS_WATR00_9997_ERROR = "BBIP4400EU0426";
	/** 月结锁定 */
	public static final String EUPS_WATR00_9998_ERROR = "BBIP4400EU0427";
	/** 数据库或系统错误 */
	public static final String EUPS_WATR00_9999_ERROR = "BBIP4400EU0428";

	// 汕头电力
	/** 调用服务出错 */
	public static final String EUPS_ELEC02_0a_ERROR = "BBIP4400EU0301";
	/** 银行数据包长度小于交易代码长度 */
	public static final String EUPS_ELEC02_0b_ERROR = "BBIP4400EU0302";
	/** 非法交易代码 */
	public static final String EUPS_ELEC02_0c_ERROR = "BBIP4400EU0303";
	/** 无法打开银行传来文件 */
	public static final String EUPS_ELEC02_0d_ERROR = "BBIP4400EU0304";
	/** 电力端服务发生内部错误 */
	public static final String EUPS_ELEC02_0e_ERROR = "BBIP4400EU0305";

	/** 协议以外错误 */
	public static final String EUPS_ELEC02_0f_ERROR = "BBIP4400EU0306";
	/** 解析银行文件错误 */
	public static final String EUPS_ELEC02_0g_ERROR = "BBIP4400EU0307";
	/** 解析包头错误 */
	public static final String EUPS_ELEC02_0h_ERROR = "BBIP4400EU0308";
	/** 解析包体或文件错误 */
	public static final String EUPS_ELEC02_0i_ERROR = "BBIP4400EU0309";
	/** 数据包长度错误 */
	public static final String EUPS_ELEC02_0j_ERROR = "BBIP4400EU0310";

	/** 数据字段个数错误 */
	public static final String EUPS_ELEC02_0k_ERROR = "BBIP4400EU0311";
	/** 字段类型错误 */
	public static final String EUPS_ELEC02_0l_ERROR = "BBIP4400EU0312";
	/** 数据库操作错误 */
	public static final String EUPS_ELEC02_0m_ERROR = "BBIP4400EU0313";
	/** 电力方生成文件错误 */
	public static final String EUPS_ELEC02_0n_ERROR = "BBIP4400EU0314";
	/** 产生返回包错误 */
	public static final String EUPS_ELEC02_0p_ERROR = "BBIP4400EU0315";

	/** 数据溢出错误 */
	public static final String EUPS_ELEC02_0q_ERROR = "BBIP4400EU0316";
	/** 对方程序失效，应答方处理 */
	public static final String EUPS_ELEC02_01_ERROR = "BBIP4400EU0317";
	/** 供电无此户号 */
	public static final String EUPS_ELEC02_03_ERROR = "BBIP4400EU0318";
	/** 发起工作站到发起方前置机网络不通 */
	public static final String EUPS_ELEC02_04_ERROR = "BBIP4400EU0319";
	/** 双方前置机之间无法通讯，网络连接故障 */
	public static final String EUPS_ELEC02_05_ERROR = "BBIP4400EU0320";

	/** 用户无欠费信息 */
	public static final String EUPS_ELEC02_06_ERROR = "BBIP4400EU0321";
	/** 用户所在营业区域没在该银行开户 */
	public static final String EUPS_ELEC02_07_ERROR = "BBIP4400EU0322";
	/** 用户需到供电交费，在供电有其它业务 */
	public static final String EUPS_ELEC02_09_ERROR = "BBIP4400EU0323";
	/** 银行方交易码错误，银行端数据格式或内容错误 */
	public static final String EUPS_ELEC02_13_ERROR = "BBIP4400EU0324";
	/** 抹帐业务,抹帐日期非当天日期 */
	public static final String EUPS_ELEC02_14_ERROR = "BBIP4400EU0325";

	/** 抹帐业务,处理失败,供电无此银行的收费记录 */
	public static final String EUPS_ELEC02_15_ERROR = "BBIP4400EU0326";
	/** 正在进行批量代扣业务，需等本业务完成后才能办理本手续 */
	public static final String EUPS_ELEC02_18_ERROR = "BBIP4400EU0327";
	/** 签订电费代扣协议，该用户没有委托代扣业务 */
	public static final String EUPS_ELEC02_19_ERROR = "BBIP4400EU0328";
	/** 签订电费代扣协议，该用户没有在该行办理委托代扣业务，但在其他行有办理 */
	public static final String EUPS_ELEC02_20_ERROR = "BBIP4400EU0329";
	/** 开户操作时，开户不成功 */
	public static final String EUPS_ELEC02_25_ERROR = "BBIP4400EU0330";

	/** 签退操作时，签退不成功 */
	public static final String EUPS_ELEC02_26_ERROR = "BBIP4400EU0331";
	/** 用户欠费需全部缴清，不允许部分缴费 */
	public static final String EUPS_ELEC02_27_ERROR = "BBIP4400EU0332";
	/** 该户是欠费停电户，交费后，须到供电办理复电手续 */
	public static final String EUPS_ELEC02_28_ERROR = "BBIP4400EU0333";
	/** 供电方执行入帐失败 */
	public static final String EUPS_ELEC02_29_ERROR = "BBIP4400EU0334";
	/** 数据库处理数据出错 */
	public static final String EUPS_ELEC02_50_ERROR = "BBIP4400EU0335";

	/** 供电尚未开工 */
	public static final String EUPS_ELEC02_51_ERROR = "BBIP4400EU0336";
	/** 未知交易码 */
	public static final String EUPS_ELEC02_59_ERROR = "BBIP4400EU0337";
	/** 用户帐户挂失 */
	public static final String EUPS_ELEC02_62_ERROR = "BBIP4400EU0338";
	/** 用户帐号冻结 */
	public static final String EUPS_ELEC02_63_ERROR = "BBIP4400EU0339";
	/** 批量代扣,产生扣款文件错，银行应扣款文件未处理完 */
	public static final String EUPS_ELEC02_70_ERROR = "BBIP4400EU0340";

	/** 批量代扣,产生扣款文件错,没有可扣款电费数据 */
	public static final String EUPS_ELEC02_71_ERROR = "BBIP4400EU0341";
	/** 回填收费信息或冲帐时,电量(电费)发生变动 */
	public static final String EUPS_ELEC02_73_ERROR = "BBIP4400EU0342";
	/** 单笔扣款,供电方提供用户帐号不存在 */
	public static final String EUPS_ELEC02_74_ERROR = "BBIP4400EU0343";
	/** 单笔扣款,用户在银行存款余额不足 */
	public static final String EUPS_ELEC02_75_ERROR = "BBIP4400EU0344";
	/** 单笔扣款,用户帐户存在，但已销户 */
	public static final String EUPS_ELEC02_76_ERROR = "BBIP4400EU0345";

	/** 单笔扣款,用户帐户存在，但未与银行方签扣款协议 */
	public static final String EUPS_ELEC02_77_ERROR = "BBIP4400EU0346";
	/** 银行提供交易字符串错误，与联网协议约定不符 */
	public static final String EUPS_ELEC02_78_ERROR = "BBIP4400EU0347";
	/** 单笔扣款，银行自定义返回错误提示 */
	public static final String EUPS_ELEC02_79_ERROR = "BBIP4400EU0348";
	/** 供电签订代扣协议,用户帐号不存在或已销户 */
	public static final String EUPS_ELEC02_81_ERROR = "BBIP4400EU0349";
	/** 供电签订代扣协议,银行自定义异常信息 */
	public static final String EUPS_ELEC02_83_ERROR = "BBIP4400EU0350";
	/** 前置机无法登录服务器，登录失败 */
	public static final String EUPS_ELEC02_99_ERROR = "BBIP4400EU0351";

	// 珠海自助通
	/** 需要授权信息 */
	public static final String EUPS_PROF00_00_ERROR = "BBIP4400EU1200";
	/** 授权级别不够 */
	public static final String EUPS_PROF00_01_ERROR = "BBIP4400EU1201";
	/** 存在下发凭证，不允许多次发放 */
	public static final String EUPS_PROF00_02_ERROR = "BBIP4400EU1202";
	/** 凭证已登记 */
	public static final String EUPS_PROF00_03_ERROR = "BBIP4400EU1203";
	/** 凭证信息不存在 */
	public static final String EUPS_PROF00_04_ERROR = "BBIP4400EU1204";
	/** 凭证已领用，不能撤销 */
	public static final String EUPS_PROF00_05_ERROR = "BBIP4400EU1205";
	/** 终端有未结账凭证 */
	public static final String EUPS_PROF00_06_ERROR = "BBIP4400EU1206";
	/** 凭证数量不能为0 */
	public static final String EUPS_PROF00_07_ERROR = "BBIP4400EU1207";
	/** 作废数量大于剩余数量 */
	public static final String EUPS_PROF00_08_ERROR = "BBIP4400EU1208";
	/** 凭证数量不足 */
	public static final String EUPS_PROF00_09_ERROR = "BBIP4400EU1209";
	/** 凭证剩余，不能结账 */
	public static final String EUPS_PROF00_10_ERROR = "BBIP4400EU1210";

	// 福彩
	/** 用户未注册 */
	public static final String EUPS_LOTR01_00_ERROR = "BBIP4400EU1000";
	/** 非双色球玩法 */
	public static final String EUPS_LOTR01_01_ERROR = "BBIP4400EU1001";
	/** 投注方式错误 */
	public static final String EUPS_LOTR01_02_ERROR = "BBIP4400EU1002";
	/** 定投信息不存在 */
	public static final String EUPS_LOTR01_03_ERROR = "BBIP4400EU1003";
	/** 系统未登陆 */
	public static final String EUPS_LOTR01_04_ERROR = "BBIP4400EU1004";
	/** 期号已经执行 */
	public static final String EUPS_LOTR01_05_ERROR = "BBIP4400EU1005";
	/** 非购彩时间 */
	public static final String EUPS_LOTR01_06_ERROR = "BBIP4400EU1006";
	/** 更新定投计划表失败 */
	public static final String EUPS_LOTR01_07_ERROR = "BBIP4400EU1007";
	/** 定投计划执行失败 */
	public static final String EUPS_LOTR01_08_ERROR = "BBIP4400EU1008";
	/** 新增开奖公告记录失败 */
	public static final String EUPS_LOTR01_09_ERROR = "BBIP4400EU1009";
	/** 下载开奖公告失败 */
	public static final String EUPS_LOTR01_10_ERROR = "BBIP4400EU1010";
	/** 新增中奖明细失败 */
	public static final String EUPS_LOTR01_11_ERROR = "BBIP4400EU1011";
	/** 下载快乐十分中奖信息失败 */
	public static final String EUPS_LOTR01_12_ERROR = "BBIP4400EU1012";
	/** 返奖记录表有返奖记录存在异常 */
	public static final String EUPS_LOTR01_13_ERROR = "BBIP4400EU1013";

	/** 福彩系统配置信息不存在 */
	public static final String EUPS_LOTR01_14_ERROR = "BBIP4400EU1014";
	/** 代收内部户不存在 */
	public static final String EUPS_LOTR01_15_ERROR = "BBIP4400EU1015";
	/** 代发内部户不存在 */
	public static final String EUPS_LOTR01_16_ERROR = "BBIP4400EU1016";
	/** 福彩系统登录失败 */
	public static final String EUPS_LOTR01_17_ERROR = "BBIP4400EU1017";
	/** 奖期信息下载失败 */
	public static final String EUPS_LOTR01_18_ERROR = "BBIP4400EU1018";
	/** 不存在未返奖奖期 */
	public static final String EUPS_LOTR01_19_ERROR = "BBIP4400EU1019";
	/** 系统角色登陆失败 */
	public static final String EUPS_LOT_LOGIN_ERROR = "BBIP4400EU1020";
	/** 系统对时失败 */
	public static final String EUPS_LOT_CHECK_TIME_ERROR = "BBIP4400EU1021";
	/** 更新参数表失败! */
	public static final String EUPS_LOT_UPDATE_SYS_ERROR = "BBIP4400EU1022";
	/** 此卡号已注册! */
	public static final String EUPS_LOT_CAR_HAV_REG = "BBIP4400EU1023";
	/** 此手机已注册! */
	public static final String EUPS_LOT_MOB_HAV_REG = "BBIP4400EU1024";
	/** 身份证号码不符! */
	public static final String EUPS_LOT_IDEN_NOEXIST = "BBIP4400EU1025";
	/** 地市编码转换出错 */
	public static final String EUPS_LOT_SWIC_CITYNO_ERROR = "BBIP4400EU1026";
	/** 登记福彩用户表失败 */
	public static final String EUPS_LOT_INSERT_USERINFO_FAIL = "BBIP4400EU1027";
	/** 彩民注册失败! */
	public static final String EUPS_LOT_REG_FAIL = "BBIP4400EU1028";
	/** 此卡号未注册! */
	public static final String EUPS_LOT_CAR_NOT_REG = "BBIP4400EU1029";
	/** 彩民查询失败! */
	public static final String EUPS_LOT_QRY_CUSINFO_FAIL = "BBIP4400EU1030";
	/** 彩民注销失败! */
	public static final String EUPS_LOT_LOGOUT_FAIL = "BBIP4400EU1031";
	/** 无对账信息! */
	public static final String EUPS_LOT_NO_CHECK_INFO = "BBIP4400EU1032";
	/** 对账更新状态失败! */
	public static final String EUPS_LOT_UPDATE_CHECK_INFO_FAIL = "BBIP4400EU1033";
	/** 对账更新状态失败!（updateMatchLotTxnJnl）对成功账 */
	public static final String EUPS_LOT_UPDATE_TXN_JNL_FAIL = "BBIP4400EU1034";

	/** 对账更新状态失败!（updateMatchLotChkDtl） 对成功账 */
	public static final String EUPS_LOT_UPDATE_CHK_DTL_FAIL = "BBIP4400EU1035";

	/** 对账更新状态失败!（MatchLotTxnJnl） 对失敗账 */
	public static final String EUPS_LOT_UPDATE_FAIL_TXN_JNL = "BBIP4400EU1036";
	/** 统计购彩总金额失败 */
	public static final String EUPS_LOT_CUM_CON_FAIL = "BBIP4400EU1037";

	/** 对账更新状态失败! */
	public static final String EUPS_LOT_CHECK_CON_FAIL = "BBIP4400EU1038";
	/** 查询参数表失败! */
	public static final String EUPS_LOT_QRY_SYS_FAIL = "BBIP4400EU1039";
	/** 用户名已注册！ */
	public static final String EUPS_LOT_USER_HAV_REG = "BBIP4400EU1040";
	/** 用户名未注册，请先注册！ */
	public static final String EUPS_LOT_NOT_REG = "BBIP4400EU1041";

	public static final String EUPS_THD_ERROR_TIMEOUT = "BBIP4400EU0701";
	public static final String EUPS_THD_ERROR_SENDFAILURE = "BBIP4400EU0702";
	public static final String EUPS_THD_SYS_ERROR = "BBIP4400EU0703";
	public static final String TRSP_INVOIC_NOT_EMPTY = "BBIP4400EU0704";

	/** 缴费记录不存在或者已作废 */
	public static final String FEE_INFO_EMPTY = "BBIP4400EU0705";

	/** 缴费状态信息错 */
	public static final String FEE_STATUS_ERROR = "BBIP4400EU0706";

	/** 禁止跨日转账 */
	public static final String DATE_ERROR = "BBIP4400EU0707";

	/** 交易已对账，不能冲正 */
	public static final String REVERS_ERROR = "BBIP4400EU0708";

	/** 路桥方交易超时 */
	public static final String TRSP_CMU_TIME_OUT = "BBIP4400EU0709";
	/** 发票号码错误 */
	public static final String EUPS_INVOIC_NO_ERROR = "BBIP4400EU0710";
	/** 交易检查错误 */
	public static final String EUPS_TXN_CHECK_ERROR = "BBIP4400EU0711";

	/** 该账户未签约 */
	public static final String EUPS_CUSNAME_NEVR_SIGN = "BBIP4400EU0601";
	/** 银行系统出错 */
	public static final String EUPS_CUSNAME_CKDB_FAIL = "BBIP4400EU0602";
	/** 无流水记录 */
	public static final String EUPS_QUERY_NO_DATA = "BBIP4400EU0603";
	/** 出错-数据库ChkFilLst请联系银行中心处理 */
	public static final String EUPS_CKDB_FLST_ERROR = "BBIP4400EU0604";
	/** 提示-该文件前已登记 */
	public static final String EUPS_CKDB_FILE_EXIST = "BBIP4400EU0605";
	/** 提示-数据库GashFiLst文件不存在 */
	public static final String EUPS_CKDB_FILE_NOFD = "BBIP4400EU0606";
	/** 银行系统出错，查询数据库失败! **/
	public static final String EUPS_CKDB_FAIL = "BBIP4400EU0607";

	/************************ 中山文件批量 *********************/
	/******* 批次文件重复录入 *********/
	public static final String GDEUPSB_BAT_FIL_EXIST = "BBIP4400EU1601";

	/************************** 南方电网 ***********************/
	/** 部分与汕头电力相同 ELEC02 */
	/** 银行正在日结中，无法进行缴费 */
	public static final String EUPS_ELEC00_84_ERROR = "BBIP4400EU0001";
	/** 供电账户锁定，无法进行缴费，请致电供电95598 */
	public static final String EUPS_ELEC00_100_ERROR = "BBIP4400EU0002";

	public static final String EUPS_ELEC00_101_ERROR ="BBIP4400EU0003";
	/** 批次状态为不可以撤销 */
	public static final String EUPS_BATCH_STATUS_ERROR = "BBIP4400EU0352";
	/** 批次已经存在 */
	public static final String EUPS_BATCH_INFO_EXIST = "BBIP4400EU0353";

	/************************* 公共汽车 ***********************/
	/** 0 成功 */
	public static final String EUPS_VECH00_0_ERROR = "BBIP4400EU0800";
	/** 系统异常 */
	public static final String EUPS_VECH00_99_ERROR = "BBIP4400EU0899";
	/** 签名验证失败 */
	public static final String EUPS_VECH00_1001_ERROR = "BBIP4400EU0811";
	/** 不是本系统的终端 */
	public static final String EUPS_VECH00_1002_ERROR = "BBIP4400EU0812";
	/** 客户端IP验证失败 */
	public static final String EUPS_VECH00_1003_ERROR = "BBIP4400EU0813";
	/** 没有对应查询数据 */
	public static final String EUPS_VECH00_1004_ERROR = "BBIP4400EU0814";
	/** 参数不正确 */
	public static final String EUPS_VECH00_1005_ERROR = "BBIP4400EU0815";
	/** 提交的订单和预提交订单信息不一致 */
	public static final String EUPS_VECH00_1006_ERROR = "BBIP4400EU0816";
	/** 订单信息错误 */
	public static final String EUPS_VECH00_1008_ERROR = "BBIP4400EU0818";
	/** 没有找到确认支付的订单 */
	public static final String EUPS_VECH00_1009_ERROR = "BBIP4400EU0819";

	/** 惠州燃气已存在协议 */
	public static final String GAS_CUS_AGT_DOUBLE = "BBIP4400EU0620";
	/** 惠州燃气协议新增失败 */
	public static final String GAS_CUS_AGT_ADDNO = "BBIP4400EU0621";
	/** 惠州燃气协议修改失败 */
	public static final String GAS_CUS_AGT_EDITNO = "BBIP4400EU0622";
	/** 惠州燃气协议删除失败 */
	public static final String GAS_CUS_AGT_STOPNO = "BBIP4400EU0623";
	/** 惠州燃气查询协议空 */
	public static final String GAS_QRY_AGT_ERR_EMT = "BBIP4400EU0624";
	/** 惠州燃气：代收付已存在相关协议 */
	public static final String GAS_QRY_AGT_ERR_EXIST = "BBIP4400EU0625";
	/** 惠州燃气：不存在协议 */
	public static final String GAS_QRY_AGT_ERR_NO = "BBIP4400EU0626";
	/** 惠州燃气：协议已报停 */
	public static final String GAS_QRY_AGT_ERR_STOP = "BBIP4400EU0627";
	/** 燃气返回存在相关协议 */
	public static final String GAS_QRY_AGT_ERR_DOUBLE = "BBIP4400EU0628";
	/** 燃气返回操作失败 */
	public static final String GAS_CUS_AGT_OPRE = "BBIP4400EU0629";
	/** 操作代收付协议失败 */
	public static final String GAS_OPR_ACP_ERR = "BBIP4400EU0630";
	/** 系统错误，请联系管理员 */
	public static final String GAS_SYS_ERROR = "BBIP4400EU0601";

	/** 零余额：请求超时 */
	public static final String REQUEST_TIME_OUT = "BBIP4400EU0699";

	/** 申请电子柜员，不支持该业务类型 */
	public static final String NO_SUPPORT_BUS_TYP = "BBIP4400EU0698";

	// 烟草
	/** 主密钥不存在 ! */
	public static final String TBC_MAIN_KEY_NOT_EXIST = "BBIP4400EU0500";
	/** 您提供的单位未开户! */
	public static final String TBC_OFF_NOT_EXIST = "BBIP4400EU0501";
	/** 数据库处理错误 ! */
	public static final String TBC_DB_ERROR = "BBIP4400EU0502";
	/** "无此用户信息 !" */
	public static final String TBC_USER_NOT_EXIST = "BBIP4400EU0503";
	/** "通信异常或 其他错误!" */
	public static final String TBC_COM_OTHER_ERROR = "BBIP4400EU0504";
	/** 请求服务超时 */
	public static final String TBC_OUT_TIME_ERROR = "BBIP4400EU0505";
	
	/** 汕头电力未知错误 */
	public static final String EUPS_ELE_ST_UNKNOWN_ERROR = "BBIP4400EU0300";

}
