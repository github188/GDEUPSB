--平台参数表
drop table gdeupsb.gdeups_lot_sys_cfg;
create table gdeupsb.gdeups_lot_sys_cfg
(
  Deal_Id    char(10)    not null default ' ',
--运营商ID
  Usr_Pam    char(30)    not null default ' ',
--用户名
  Usr_Pas    char(20)    not null default ' ',
--用户密码
  Sig_Tim    char(14)    not null default ' ',
--签到时间
  Lcl_Tim    char(14)    not null default ' ',
--本地时间
  Lot_Tim    char(14)    not null default ' ',
--福彩时间
  Diff_Tm    char(20)    not null default ' ',
--时间差
  DS_C_Agt_No  char(10)    not null default ' ',
--代收单位编号
  DF_C_Agt_No  char(10)    not null default ' ',
--代发单位编号
  HS_Act_No   char(21)    not null default ' ',
--华祥对公账账号
  Log_Seq    char(12)    not null default ' ',
--交易日志流水序号
  WH_Phone   char(11)    not null default ' ',
--维护人员手机号 
primary key (Deal_Id)
)
 IN "BBIP_APP"
INDEX IN  "BBIP_APP_INDEX";

--福彩用户表
drop table gdeupsb.gdeups_lot_cus_inf;
create table gdeupsb.gdeups_lot_cus_inf
(
  Br_No        char(11)     not null default ' ',
--分行号
  Cus_Nam      char(90)   default ' ',
--客户姓名
  Crd_No       char(30)    not null default ' ',
--客户银行卡号
  Act_No       char(21)    not null default ' ',
--客户银行帐号
  Act_Nod       char(11)     default ' ',
--开户网点
  Id_Typ       char(2)     not null default ' ',
--证件类型
  Id_No        char(30)    not null default ' ',
--证件号码
  Mob_Tel      char(15)    not null default ' ',
--移动联系电话
  Fix_Tel      char(20)    default ' ',
--固定联系电话
  Lot_Nam      char(30)    default ' ',
--彩民标识
  Lot_Psw      char(30)    default ' ',
--彩民密码
  Reg_Tim      char(20)    default ' ',
--注册时间
  Email       char(30)    default ' ',
--电子邮箱
  City_Id      char(2)     not null default ' ',
--地市编码
  SEX         char(1)     default ' ',
--性别
  BthDay      char(8)     default ' ',
--生日
  Status      char(1)     not null default ' ',
--状态 1正常;2锁定;3注销
primary key (Crd_No)
)
 IN "BBIP_APP"
INDEX IN  "BBIP_APP_INDEX";

--定投控制表
drop table gdeupsb.gdeups_lot_pln_ctl;
create table gdeupsb.gdeups_lot_pln_ctl
(
   Game_Id    char(2)     not null default ' ',
--游戏编号 5:双色球
   Draw_Id    char(6)     not null default ' ',
--当前期号
   Draw_Nm    char(30)     not null default ' ',
--当前期名
   Bet_Dat    char(8)    not null default ' ',
--定投日期
   Beg_Tim    char(14)    not null default ' ',
--定投开始时间
   End_Tim    char(14)    not null default ' ',
--定投结束时间
   Txn_Sts    char(1)    not null default 'U'
--当前期号执行情况 U初始，S执行完成，F执行失败
) IN "BBIP_APP"
INDEX IN  "BBIP_APP_INDEX";


--定投计划表
drop table gdeupsb.gdeups_lot_aut_pln;
create table gdeupsb.gdeups_lot_aut_pln
(
   Plan_No    char(20)    not null default ' ',
--定投计划编号 99+日期（YYYYMMDD）+ 10位顺号
   Plan_Nm    char(45)    not null default ' ',
--定投计划名称
   Game_Id    char(2)     not null default ' ',
--游戏编号 5:双色球
   Gam_Nam    char(30)     not null default ' ',
--游戏名称 5:双色球
   Play_Id    char(5)     not null default ' ',
--玩法编号
   Bet_Per    char(5)     not null default ' ',
--投注期数 （一年153期、半年75期）
   Bet_Met    char(5)     not null default ' ',
--投注方法  0机选，1自选
   Bet_Mod    char(5)     not null default ' ',
--投注方式  1单式，12复式，13胆托
   Bet_Mul    char(3)     not null default ' ',
--投注倍数
   Bet_Amt    char(15)     not null default ' ',
--投注金额
   Bet_Lin    char(128)     not null default ' ',
--投注号码
   Lot_Nam    char(30)    default ' ',
--彩民标识
   Crd_No      char(30)    not null default ' ',
--客户银行卡号
   Mob_Tel      char(15)    not null default ' ',
--移动联系电话
   Bet_Dat      char(8)    not null default ' ',
--定投日期
   Bet_Tim      char(14)    not null default ' ',
--定投时间
   Ccl_Tim      char(14)    not null default ' ',
--取消或者结束定投时间
   Cur_Per      char(6)    not null default ' ',
--当前期号
   Cur_Fal      char(2)    not null default '0',
--当前期号失败次数
   Con_Fal      char(2)    not null default '0',
--连续期号失败次数
   Do_Per       char(5)    not null default '0',
--已执行的期数
   Txn_Cnl     char(3)     default ' ',
--定投交易渠道
   Status      char(1)    not null default '0',
--状态 0计划正常;1客户撤消;2系统撤消;3计划完成
primary key (Plan_No)
) IN "BBIP_APP"
INDEX IN  "BBIP_APP_INDEX";

--投注流水表
drop table gdeupsb.GDEUPS_lot_txn_jnl;
create table gdeupsb.GDEUPS_lot_txn_jnl
(
    SQN                  BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1 ),
   Br_No     char(11)     not null default ' ',
--分行号
   Log_No     char(14)   not null,
--前置流水号
   T_Txn_Cd      char(10)    not null,
--第三方交易码
   Txn_Cod      char(6)     not null,
--交易码
   Sch_Typ    char(1)     not null default ' ',
--方案类型
   Sch_Tit    char(30)     not null default ' ',
--方案标题
   Sec_Lev    char(1)     not null default ' ',
--方案等级
   City_Id    char(2)     not null default ' ',
--地市编码
   Draw_Id    char(5)     not null default ' ',
--当前期号
   Keno_Id    char(11)     not null default ' ',
--keno期号
   Game_Id    char(2)     not null default ' ',
--游戏编号 5:双色球
   Gam_Nam    char(30)    not null default ' ',
--游戏名称 5:双色球
   Play_Id    char(5)     not null default ' ',
--玩法编号
   Bet_Met    char(5)     not null default ' ',
--投注方法  0机选，1自选
   Bet_Mod    char(5)     not null default ' ',
--投注方式  1单式，12复式，13胆托
   Bet_Mul    char(3)     not null default ' ',
--投注倍数
   Txn_Amt    char(15)     not null default ' ',
--投注金额
   Bet_Lin    char(128)     not null default ' ',
--投注号码
   Lot_Nam    char(30)     not null default ' ',
--彩民标识
   Bet_Dat    char(8)    not null default ' ',
--投注日期
   Txn_Tim    char(14)   not null default ' ',
--投注时间
   Txn_Log    char(32)   not null default ' ',
--投注交易流水号
   Sch_Id        char(32)   not null default ' ',
--系统生成的方案编号
   T_Log_No      char(30)    default ' ',
--彩票流水号
   Cipher      char(30)    default ' ',
--彩票密码
   Verify      char(30)    default ' ',
--彩票校验码
   Cus_Nam      char(90)    not null default ' ',
--客户姓名
   Crd_No       char(30)    not null default ' ',
--客户银行卡号
   H_Txn_Cd      char(6)  default ' ' not null,
--主机交易码
   H_Txn_Sb      char(3)  default ' ' not null,
--交易子码
   H_Log_No      char(16)     default ' ',
--主机流水号
   H_Rsp_Cd      char(6)     default ' ',
--主机响应码
   H_Txn_St      char(1)     default 'U',
--主机交易状态
   T_Rsp_Cd      char(10)    default ' ',
--第三方响应码
   T_Txn_St      char(1)     default 'U',
--第三方交易状态
   Thd_Chk      char(1)  default '0',
--与第三方对帐标志,0未对账 1已对账
   T_Chk_No      char(12)   default '00000000000',
--与第三方对帐批次
   Chk_Tim      char(14)   default ' ',
--对帐时间
   Chk_Flg      char(1)    Default '0',
--对账标志(0:未处理;1:核对成功;2: 核对失败，我方多账;)
   Awd_Flg      char(1)    Default '0',
--中奖标志(0:未中奖;1:中奖)
   Awd_Rtn      char(1)    Default '0',
--返奖标志(0:未返奖;1:已返奖)
   C_Agt_No      char(10)    default ' ' not null,
--商户协议编号
   Tck_No     char(16)      default ' ',
--会计流水号
   Txn_Cnl     char(3)     default ' ',
--交易渠道
   Bet_Typ     char(1)     default ' ',
--投注类型 0:实时投注；1：定投投注
   L_Chk_Tm       char(10)   default ' ',
--1970年距今的秒数
   Txn_Sts      char(1)     default 'U',
   primary key (SQN)
--交易状态  S成功；F失败；T超时
--U:初始
--S:购彩成功
--F:购彩失败
--T:超时
--A:账务处理完的临时状态
--E:交易失败后冲正失败
) IN "BBIP_APP"
INDEX IN  "BBIP_APP_INDEX";

--对账控制表
drop table gdeupsb.GDEUPS_lot_chk_ctl;
create table gdeupsb.GDEUPS_lot_chk_ctl
(
   SQN                  BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1 ),
   Chk_Dat      char(8)   default ' ',
--对帐日期
   Game_Id    char(2)     not null default ' ',
--游戏编号 5:双色球
   Draw_Id    char(5)     not null default ' ',
--期号
   Keno_Id    char(11)     not null default ' ',
--keno期号
   Tot_Num    char(5)     not null default ' ',
--投注总票数
   Tot_Amt    char(14)    not null default ' ',
--投注总金额
   Chk_Flg    char(1)    not null default '0',
--对账标志 0:未对账；1:部分对账；2:全部对账
   Chk_Tim    char(14)   default ' ',
--对帐时间
primary key (SQN)
) IN "BBIP_APP"
INDEX IN  "BBIP_APP_INDEX";

--对账明细表
drop table gdeupsb.gdeups_lot_chk_dtl;
create table gdeupsb.gdeups_lot_chk_dtl
(
   Chk_Dat      char(8)   default ' ',
--对帐日期
   Game_Id    char(2)     not null default ' ',
--游戏编号 5:双色球
   Draw_Id    char(5)     not null default ' ',
--期号
   Keno_Id    char(11)     not null default ' ',
--keno期号
   Seq_No     char(10)    not null default ' ',
--序号
   Sch_Id     char(32)    not null default ' ',
--方案编号
   Lot_Nam    char(30)    default ' ',
--彩民标识
   Txn_Log    char(25)    not null default ' ',
--投注交易流水号
   Play_Id    char(5)     not null default ' ',
--玩法编号
   Txn_Tim    char(14)    not null default ' ',
--投注时间
   Txn_Amt    char(15)    not null default ' ',
--投注金额
   Chk_Flg    char(1)    not null default '0',
--对账标志 0:未对账；1:已对账；
   Chk_Tim    char(14)   default ' ',
--对帐时间
primary key (Txn_Log)
) IN "BBIP_APP"
INDEX IN  "BBIP_APP_INDEX";


--奖期信息表
drop table gdeupsb.GDEUPS_lot_drw_tbl;
create table gdeupsb.GDEUPS_lot_drw_tbl
(
   SQN                  BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1 ),
   Game_Id    char(2)     not null default ' ',
--游戏编号 5:双色球，7：快乐十分
   Draw_Id    char(5)     not null default ' ',
--期号
   Draw_Nm    char(30)     not null default ' ',
--期名
   Sal_Str    char(14)     not null default ' ',
--销售开始时间
   Sal_End    char(14)     not null default ' ',
--销售结束时间
   Csh_Str    char(14)     not null default ' ',
--兑奖开始时间
   Csh_End    char(14)     not null default ' ',
--兑奖结束时间
   Keno_Id    char(11)     not null default ' ',
--keno期号(在快乐十分时需要增加一条记录期号为AAAAA，为了统计快开游戏的总金额)
   Keno_Nm    char(30)     not null default ' ',
--keno期名
   K_Sal_St    char(14)     not null default ' ',
--Keno销售开始时间
   K_Sal_Ed    char(14)     not null default ' ',
--Keno销售结束时间
   Chk_Flg    char(1)      not null default '0',
--对账清算标志  0没对账,1部分对账,2已对账,3已清算
   Chk_Tim    char(14)   default ' ',
--对帐清算时间
   Dow_Prz    char(1)    not null default '0',
--是否已下载中奖文件  0未下载,1已下载
   Prz_Amt    char(15)    not null default ' ',
--返奖总金额 
   Tot_Amt    char(15)    not null default ' ',
--购彩总金额
   Dif_Flg    char(1)    not null default ' ',
--扎差标志：1借方（购彩总金额大于返奖总金额）,0贷方（购彩总金额小于奖总金额）
   Dif_Amt    char(15)    not null default ' ',
--购彩总金额与返奖总金额扎差金额
   Pay_Flg    char(1)     not null default ' ',
--返奖垫付标志: 0没垫付,1华祥垫付,2已返垫付
   Pay_Amt    char(15)    not null default ' ',
--返奖垫付金额
   Flw_Ctl    char(2)     not null default '0',
--返奖流程控制标志
--1开奖公告下载开始, 2开奖公告下载完成
--3中奖文件下载开始, 4中奖文件下载完成
--5插入返奖明细开始, 6插入返奖明细结束
--7返奖资金划拨开始，8返奖资金划拨完成
--9返奖开始，10返奖完成 
  RTN_TIM char(14) default ' ',
  CLR_TIM char(14)  default ' ',
  XFE_FLG char(1)  default ' ',
primary key (SQN)
) IN "BBIP_APP"
INDEX IN  "BBIP_APP_INDEX";

--开奖公告表
drop table gdeupsb.gdeups_lot_prz_inf;
create table gdeupsb.gdeups_lot_prz_inf
(
   Game_Id    char(2)     not null default ' ',
--游戏编号 5:双色球
   Game_Nm    char(30)    not null default ' ',
--游戏名
   Draw_Id    char(5)     not null default ' ',
--期号
   Draw_Nm    char(30)     not null default ' ',
--期名
   Keno_Id    char(11)     not null default ' ',
--keno期号
   Keno_Nm    char(30)     not null default ' ',
--keno期名
   Str_Tim    char(14)     not null default ' ',
--期开始时间
   Stp_Tim    char(14)     not null default ' ',
--期结束时间
   Tot_Prz    char(15)     not null default ' ',
--总中奖金额
   Jac_Pot    char(18)     not null default ' ',
--奖池
   Opn_Tot    char(5)     not null default ' ',
--开奖总次数
   Opn_Num    char(3)     not null default ' ',
--开奖次数
   Bon_Cod    char(20)     not null default ' ',
--开奖号码
   Cls_Num    char(3)     not null default ' ',
--奖级个数
   Cls_Nam    char(20)     not null default ' ',
--奖级
   Bon_Amt    char(15)     not null default ' ',
--中奖金额
   Bon_Num    char(10)     not null default ' '
--中奖注数
) IN "BBIP_APP"
INDEX IN  "BBIP_APP_INDEX";

--中奖记录控制表
drop table gdeupsb.GDEUPS_lot_prz_ctl;
create table gdeupsb.GDEUPS_lot_prz_ctl
(
   SQN                  BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1 ),
   Game_Id    char(2)     not null default ' ',
--游戏编号 5:双色球
   Draw_Id    char(5)     not null default ' ',
--期号
   Keno_Id    char(11)     not null default ' ',
--keno期号
   Cipher    char(30)    default ' ',
--彩票密码
   Big_Bon    char(5)     default ' ',
--大奖标记
   Tot_Prz    char(15)    not null default ' ',
--总中奖金额
   TxnLog    char(25)    not null default ' ',
--投注交易流水号
   T_Log_No    char(15)    not null,
--彩票流水号
   Term_ID    char(10)    not null default ' ',
--系统投注终端号
primary key (SQN)
) IN "BBIP_APP"
INDEX IN  "BBIP_APP_INDEX";

--中奖记录明细表
drop table gdeupsb.gdeups_lot_prz_dtl;
create table gdeupsb.gdeups_lot_prz_dtl
(
   Game_Id    char(2)     not null default ' ',
--游戏编号 5:双色球
   Draw_Id    char(5)     not null default ' ',
--期号
   Keno_Id    char(11)     not null default ' ',
--keno期号
   Txn_Log    char(25)    not null default ' ',
--投注交易流水号
   T_Log_No    char(15)    not null,
--彩票流水号
   Bet_Mod    char(5)     not null default ' ',
--投注方式  1单式，12复式，13胆托
   Bet_Mul    char(3)     not null default ' ',
--投注倍数
   class_No    char(16)     not null default ' ',
--奖等编号
   Prz_Amt    char(15)    not null default ' ',
--中奖金额
   Bet_Lin    char(128)   not null default ' ',
--投注号码
primary key (T_Log_No)
) IN "BBIP_APP"
INDEX IN  "BBIP_APP_INDEX";

--返奖记录明细表
drop table gdeupsb.gdeups_lot_awd_dtl;
create table gdeupsb.gdeups_lot_awd_dtl
(
   Br_No        char(11)     not null default ' ',
--分行号
   Log_No      char(14)     default ' ',
--前置流水号
   Txn_Cod      char(6)     default ' ',
--交易码
   Game_Id      char(2)     not null default ' ',
--游戏编号 5:双色球
   Draw_Id      char(5)     not null default ' ',
--期号
   Keno_Id      char(11)     not null default ' ',
--keno期号
   Lot_Nam      char(30)    default ' ',
--彩民标识
   Txn_Log      char(25)   not null default ' ',
--投注交易流水号
   T_Log_No      char(15)    not null default ' ',
--彩票流水号
   Cus_Nam      char(90)    not null default ' ',
--客户姓名
   Crd_No       char(30)    not null default ' ',
--客户银行卡号
   Awd_Amt      char(15)    not null default ' ',
--返奖金额
   H_Txn_Cd      char(6)     not null default ' ',
--主机交易码
   H_Log_No      char(16)     default ' ',
--主机流水号
   H_Rsp_Cd      char(6)     default ' ',
--主机响应码
   H_Txn_St      char(1)     default 'U',
--主机交易状态
   Awd_Dat      char(8)     default ' ',
--返奖日期
   Awd_Tim      char(14)    default ' ',
--返奖时间
   Awd_Rtn      char(1)     default '0',
--返奖标志(0:未返奖;1:已返奖)
   Tck_No      char(16)      default ' '
--会计流水号
) IN "BBIP_APP"
INDEX IN  "BBIP_APP_INDEX";

drop table GDEUPSB.GDEUPS_CIM_TLR_TBL;
create table GDEUPSB.GDEUPS_CIM_TLR_TBL(
	TLR_ID		CHAR(7)		NOT NULL,
	TLR_ORG		CHAR(6),
	TLR_DPT		CHAR(3),
	EFF_DAT		CHAR(10),
	EPR_DAT		CHAR(10),
	UPD_DAT		CHAR(10),
	TLR_NAM		CHAR(35),
	TLR_TYP		CHAR(1),
	TLR_LVL		CHAR(1),
	TLR_CSH		CHAR(1),
	TLR_VCH		CHAR(1),
	TXN_MOD		CHAR(1),
	SUP_LVL		CHAR(1),
	ATH_LVL		CHAR(1),
	UPG_TLR		CHAR(7),
	ATH_ARE		CHAR(1),
	CARD_NO		CHAR(6),
	CRD_PWD		CHAR(6),
	CRD_DAT		CHAR(10),
	KB_PSW		CHAR(6),
	KB_PW_DT	CHAR(10),
	LST_PWD		CHAR(6),
	PSW_CHG		CHAR(2),
	SGN_STS		CHAR(1),
	SGN_DAT		CHAR(10),
	SGN_ARE		CHAR(1),
	SGN_CNT		CHAR(2),
	TRM_ID		CHAR(7),
	PRT_ADDR	CHAR(4),
	FIN_FLG		CHAR(1),
	V_BOX_FL	CHAR(1),
	BVLT_IND	CHAR(1),
	V_BOX_CHK	CHAR(1),
	BVLT_CHK	CHAR(1),
	L_BOX_CHK	CHAR(1),
	LVLT_CHK	CHAR(1),
	UGRP_FL		CHAR(8),
	STF_NO		CHAR(7),
	SAV_TLR		CHAR(7),
	BK_NO		CHAR(6),
	SUB_BK		CHAR(6),
	HQ_BK_NO	CHAR(6)
)
 IN "BBIP_APP"
INDEX IN  "BBIP_APP_INDEX";

--柜员管理表
DROP TABLE GDEUPSB.GDEUPS_TL_NO_MANAGER;
create table GDEUPSB.GDEUPS_TL_NO_MANAGER
(
  gydm   char(20)  not null,
  passwd char(14)  not null
)
 IN "BBIP_APP"
INDEX IN  "BBIP_APP_INDEX";

drop table GDEUPSB.GD_ELEC_File_BATCH_TMP;
create table GDEUPSB.GD_ELEC_File_BATCH_TMP
(
sqn char(20) not null,
DF_MON char(8),  --电费月
Tot_Cnt  char(5),   --总记录数
Tot_Amt char(18),   --总金额
Rs_Fld1   char(20),   --供电局批号
thd_Cus_No char(30),   --第三方客户标识
cus_Ac  char(25),    -- 客户帐号
tTxn_Amt  char(18),  --金额
ele_Mon  char(8), --电费月份
rs_Fld2   char(10),   --区号
com_Ac  char(25), --供电企业账号
flag  char(1), --缴费标志
t_ComNo  char(25),  --单位编号
TXN_DTE   char(8),  --交易日期
tlr     char(8),--处理柜员
primary key (sqn)
)
 IN "BBIP_APP"
INDEX IN  "BBIP_APP_INDEX";


--通用发票记录表
DROP TABLE GDEUPSB.GDEUPS_TYF_INV_REC;
CREATE TABLE GDEUPSB.GDEUPS_TYF_INV_REC
(
   TR_TYPE      char(1)        ,--缴费单位 (1-电信，2-联通，3-移动)
   REC_CNT      char(2)        ,--发票第几张
   THD_KEY      char(25)       ,--前置流水
   ACT_NO       char(25)       ,--帐号
   TCUS_ID      char(14)       ,--缴费电话号码
   FCUS_ID      char(14)       ,--付费电话号码
   TR_DATE      char(14)       ,--交易日期
   TCUS_NM      char(90)       ,--用户名称
   IPRN_CNT     char(2)           ,--发票打印次数（0次表示未打印）
   BILL_DATE    char(30)         ,--交费月份
   TXN_AMT      char(14)         ,--此次交易总金额
   LAST_BAL     char(14)         ,--本张发票上次缴费结余
   THIS_BAL     char(14)         ,--本张发票本次缴费结余
   IPAY_AMT     char(14)         ,--本张发票本次交费总金额
   ITOT_AMT     char(14)         ,--本张发票本次发票总金额
   EINV_NO      char(30)      ,--电子发票号
   STA_MON      char(14)      ,--话费起始月份
   END_MON      char(14)      ,--话费结束月份
   TMP01        char(30)      ,--临时字段
   TMP02        char(30)      ,--临时字段
   MX_COUNT     char(10)      ,--发票明细记录数
   FP_INF       varchar(1024)--发票明细
)IN "BBIP_APP"
INDEX IN "BBIP_APP_INDEX";
CREATE INDEX GDEUPS_TYF_INV_REC_N1 on GDEUPSB.GDEUPS_TYF_INV_REC(TCUS_ID,TR_DATE,BILL_DATE);
GRANT SELECT ON GDEUPSB.GDEUPS_TYF_INV_REC TO GDEUPSA;
GRANT INSERT ON GDEUPSB.GDEUPS_TYF_INV_REC TO GDEUPSA;
GRANT UPDATE ON GDEUPSB.GDEUPS_TYF_INV_REC TO GDEUPSA;
GRANT DELETE ON GDEUPSB.GDEUPS_TYF_INV_REC TO GDEUPSA;
GRANT REFERENCES ON GDEUPSB.GDEUPS_TYF_INV_REC TO GDEUPSA;
GRANT ALTER ON GDEUPSB.GDEUPS_TYF_INV_REC TO GDEUPSA;
GRANT INDEX ON GDEUPSB.GDEUPS_TYF_INV_REC TO GDEUPSA;

drop table GDEUPSB.TRSP_FEE_INFO;
CREATE TABLE GDEUPSB.TRSP_FEE_INFO
(
   BR_NO       CHAR(11)     NOT NULL,                                                            
   THD_KEY     CHAR(20)     NOT NULL,                                        
   CAR_TYP     CHAR(03)     NOT NULL,                                             
   CAR_NO     CHAR(20)     NOT NULL,                                           
   TCUS_NM     CHAR(90)     DEFAULT '',                                          
   PAY_MON     CHAR(02)     NOT NULL,                                            
   PAY_DAT     DATE     NOT NULL,                                          
   PAY_LOG     CHAR(20)      DEFAULT '',                               
   PAY_NOD     CHAR(11)      DEFAULT '',                                        
   PAY_TLR     CHAR(7)      DEFAULT '',                                      
   PAY_TCK     CHAR(20)     DEFAULT '',                                      
   TXN_AMT     DECIMAL(18,2)     DEFAULT '000000000000000000',                       
   TXN_CNL     CHAR(3)     DEFAULT ' ',                                      
   ACT_TYP     CHAR(01)     DEFAULT ' ',                            
   ACT_NO      CHAR(21)      DEFAULT ' ',                            
   TACT_DT     DATE     ,                                     
   TLOG_NO     CHAR(20)     DEFAULT ' ',                            
   PRT_NOD     CHAR(11)      DEFAULT '',                                      
   PRT_TLR     CHAR(7)      DEFAULT '',                                      
   RVS_LOG     CHAR(20)      DEFAULT ' ',                            
   RVS_DAT     DATE     ,                                                 
   RVS_NOD     CHAR(11)      DEFAULT '',                                      
   RVS_TLR     CHAR(7)      DEFAULT '',                                      
   RVS_TCK     CHAR(20)     DEFAULT '',                                      
   TCHK_NO     CHAR(20)      DEFAULT '00000000000000',               
   CHK_TIM     TIME    ,                                     
   CHK_FLG     CHAR(1)      DEFAULT '0',                                     
   INV_NO      CHAR(30)      DEFAULT ' ',                            
   BEG_DAT     DATE     ,                                     
   END_DAT     DATE     ,                                     
   CAR_NAME    CHAR(30)     DEFAULT ' ',                                     
   CAR_DZS     CHAR(10)     DEFAULT ' ',                                     
   CNT_STD     CHAR(10)     DEFAULT ' ',                                     
   FEE_STD     DECIMAL(18,2)     DEFAULT '000000000000000000',                       
   CORPUS     DECIMAL(18,2)     DEFAULT '000000000000000000',                        
   LATE_FEE    DECIMAL(18,2)     DEFAULT '000000000000000000',                           
   CLGS       CHAR(10)     DEFAULT ' ',                                           
   YYBZ       CHAR(10)     DEFAULT ' ',                                           
   STATUS     CHAR(01)      DEFAULT '0',                                  
   PRIMARY KEY (THD_KEY)                                                         
)IN "BBIP_APP"
INDEX IN  "BBIP_APP_INDEX";                                                                                                                                                                
                                                            
  comment on column GDEUPSB.TRSP_FEE_INFO.BR_NO   is                                                                                                             
 '分行号';                                                                                                                                                                                                                                                                                               
  comment on column GDEUPSB.TRSP_FEE_INFO.THD_KEY is                                                                                                             
 '查询键值';                                                                                                                                                                                                                                                                                           
  comment on column GDEUPSB.TRSP_FEE_INFO.CAR_TYP is                                                                                                             
 '车牌类别';                                                                                                                                                                                                                                                                                              
  comment on column GDEUPSB.TRSP_FEE_INFO.CAR_NO   is                                                                                                            
 '车牌号码';                                                                                                                                                                                                                                                                                            
  comment on column GDEUPSB.TRSP_FEE_INFO.TCUS_NM is                                                                                                             
 '车主';                                                                                                                                                                                                                                                                                             
  comment on column GDEUPSB.TRSP_FEE_INFO.PAY_MON is                                                                                                             
 '缴费月数';                                                                                                                                                                                                                                                                                           
  comment on column GDEUPSB.TRSP_FEE_INFO.PAY_DAT is                                                                                                             
 '主机交易日期';                                                                                                                                                                                                                                                                                       
  comment on column GDEUPSB.TRSP_FEE_INFO.PAY_LOG is                                                                                                             
 '主机交易流水号';                                                                                                                                                  
  comment on column GDEUPSB.TRSP_FEE_INFO.PAY_NOD is                                                                                                             
 '支付网点号';                                                                                                                                                                                                                                                                                            
  comment on column GDEUPSB.TRSP_FEE_INFO.PAY_TLR is                                                                                                             
 '支付柜员号';                                                                                                                                                                                                                                                                                            
  comment on column GDEUPSB.TRSP_FEE_INFO.PAY_TCK is                                                                                                             
 '支付主机流水';                                                                                                                                                                                                                                                                                                                                                                                             
  comment on column GDEUPSB.TRSP_FEE_INFO.TXN_AMT is                                                                                      
 '收费金额';                                                                                                                                                                                                                                                                      
  comment on column GDEUPSB.TRSP_FEE_INFO.TXN_CNL is                                                                                      
 '交易渠道';                                                                                                                                                                                                                                                                      
  comment on column GDEUPSB.TRSP_FEE_INFO.ACT_TYP is                                                                                      
 '账号类型 (1－一本通，2－普通存折，4－卡)';                                                                                                                                                                                                                                      
  comment on column GDEUPSB.TRSP_FEE_INFO.ACT_NO  is                                                                                      
 '客户帐号';                                                                                                                                                                                                                                                                      
  comment on column GDEUPSB.TRSP_FEE_INFO.TACT_DT is                                                                                      
 '企业日期(打印日期)';                                                                                                                                                                                                                                                            
  comment on column GDEUPSB.TRSP_FEE_INFO.TLOG_NO is                                                                                      
 '打印发票流水号';                                                                                                                                                                                                                                                                
  comment on column GDEUPSB.TRSP_FEE_INFO.PRT_NOD is                                                                                      
 '打印网点号';                                                                                                                                                                                                                                                                    
  comment on column GDEUPSB.TRSP_FEE_INFO.PRT_TLR is                                                                                      
 '打印柜员号';                                                                                                                                                                                                                                                                    
  comment on column GDEUPSB.TRSP_FEE_INFO.RVS_LOG is                                                                                      
 '退费流水号';                                                                                                                                                                                                                                                                    
  comment on column GDEUPSB.TRSP_FEE_INFO.RVS_DAT is                                                                                      
 '退费日期(退费核心交易日期)';                                                                                                                                                                                                                                                    
  comment on column GDEUPSB.TRSP_FEE_INFO.RVS_NOD is                                                                                      
 '退费网点号';                                                                                                                                                                                                                                                                    
  comment on column GDEUPSB.TRSP_FEE_INFO.RVS_TLR is                                                                                      
 '退费柜员号';                                                                                                                                                                                                                                                                    
  comment on column GDEUPSB.TRSP_FEE_INFO.RVS_TCK is                                                                                      
 '退费主机流水';                                                                                                                                                                                                                                                                 
  comment on column GDEUPSB.TRSP_FEE_INFO.TCHK_NO is                                                                                      
 '对账批次';                                                                                                                                                                                                                                                                      
  comment on column GDEUPSB.TRSP_FEE_INFO.CHK_TIM is                                                                                      
 '对帐时间';                                                                                                                                                                                                                                                                      
  comment on column GDEUPSB.TRSP_FEE_INFO.CHK_FLG is                                                                                      
 '对账标志(0:未处理;1:核对成功;2: 核对失败，关键信息不一致,3：银行单边帐)';                                                                         
  comment on column GDEUPSB.TRSP_FEE_INFO.INV_NO   is                                                                                     
 '发票号';                                                                                                                                                                                                                                                                        
  comment on column GDEUPSB.TRSP_FEE_INFO.BEG_DAT  is                                                                                     
 '有效起始日期';                                                                                                                                                                                                                                                                  
  comment on column GDEUPSB.TRSP_FEE_INFO.END_DAT  is                                                                                     
 '有效结束日期';                                                                                                                                                                                                                                                                  
  comment on column GDEUPSB.TRSP_FEE_INFO.CAR_NAME is                                                                                     
 '车型说明';                                                                                                                                                                                                                                                                       
  comment on column GDEUPSB.TRSP_FEE_INFO.CAR_DZS  is                                                                                     
 '吨数或座数';                                                                                                                                                                                                                                                                     
  comment on column GDEUPSB.TRSP_FEE_INFO.CNT_STD  is                                                                                     
 '计费标准';                                                                                                                                                                                                                                                                       
  comment on column GDEUPSB.TRSP_FEE_INFO.FEE_STD  is                                                                                     
 '标准年收费额';                                                                                                                                                                                                                                                                  
  comment on column GDEUPSB.TRSP_FEE_INFO.CORPUS   is                                                                                     
 '本金';                                                                                                                                                                                                                                                                          
  comment on column GDEUPSB.TRSP_FEE_INFO.LATE_FEE is                                                                                     
 '滞纳金额';                                                                                                                                                                                                                                                                       
  comment on column GDEUPSB.TRSP_FEE_INFO.CLGS      is                                                                                    
 '车辆归属';                                                                                                                                                                                                                                                                       
  comment on column GDEUPSB.TRSP_FEE_INFO.YYBZ      is                                                                                    
 '营运标志';                                                                                                                                                                                                                                                                       
  comment on column GDEUPSB.TRSP_FEE_INFO.STATUS    is                                                                                    
 '缴费状态--0-缴费,1-打票,2-退费,3-退票  ';                  
 
 ------------------路桥代收费欠费信息表
 drop table GDEUPSB.TRSP_PAY_INFO;
create table GDEUPSB.TRSP_PAY_INFO
(
    BR_NO       CHAR(11)     NOT NULL,

   CAR_TYP     CHAR(03)     ,

   CAR_NO     CHAR(20)     NOT NULL,

   TCUS_NM     CHAR(90)     DEFAULT '',

   PAY_MON     CHAR(02)     ,

   ACT_DAT     DATE    ,

   THD_KEY     CHAR(20)   NOT NULL  ,

   TACT_DT    DATE    ,

   TXN_AMT     DECIMAL(18,2)     DEFAULT '000000000000000000',

   FLG        CHAR(01)      DEFAULT '0',

  BAK_FLD1             CHAR(30),
   BAK_FLD2             CHAR(30),
   BAK_FLD3             CHAR(30),
   RSV_FLD1             VARCHAR(300),
   PRIMARY KEY (THD_KEY)
)IN "BBIP_APP"
INDEX IN  "BBIP_APP_INDEX";

comment on column GDEUPSB.TRSP_PAY_INFO.BR_NO is
'分行号';

comment on column GDEUPSB.TRSP_PAY_INFO.CAR_TYP is
'车牌类别';

comment on column GDEUPSB.TRSP_PAY_INFO.CAR_NO is
'车牌号码';

comment on column GDEUPSB.TRSP_PAY_INFO.TCUS_NM is
'车主';

comment on column GDEUPSB.TRSP_PAY_INFO.PAY_MON is
'缴费月数';

comment on column GDEUPSB.TRSP_PAY_INFO.ACT_DAT is
'交易日期';

comment on column GDEUPSB.TRSP_PAY_INFO.THD_KEY is
'查询键值';

comment on column GDEUPSB.TRSP_PAY_INFO.TACT_DT is
'企业日期';

comment on column GDEUPSB.TRSP_PAY_INFO.TXN_AMT is
'收费金额';

comment on column GDEUPSB.TRSP_PAY_INFO.FLG is
'是否缴费状态:0-未缴费,1-已缴费';


comment on column GDEUPSB.TRSP_PAY_INFO.BAK_FLD1 is
'备用字段1';

comment on column GDEUPSB.TRSP_PAY_INFO.BAK_FLD2 is
'备用字段2';

comment on column GDEUPSB.TRSP_PAY_INFO.BAK_FLD3 is
'备用字段3';

comment on column GDEUPSB.TRSP_PAY_INFO.RSV_FLD1 is
'预留字段1';


----------路桥代收费交易流水表
drop table GDEUPSB.TRSP_TXN_JNL;
create table GDEUPSB.TRSP_TXN_JNL                      
(
   BR_NO        CHAR(11)     NOT NULL,                   
   SQN      CHAR(20)    NOT NULL,                       
   TTXN_CD      CHAR(10)    ,                   
   TXN_COD      CHAR(6)     ,                   
   TXN_TYP      CHAR(1)    ,                   
   BUS_TYP      CHAR(6)     ,                   
   CRP_COD      CHAR(10)    ,                   
   ACT_DAT      DATE     ,                   
   TXN_AMT      DECIMAL(18,2)   ,                   
   FEE         DECIMAL(18,2)    ,                 
   ACT_TYP      CHAR(1)      ,       
   ACT_NO       CHAR(21)    ,       
   ACT_SQN      CHAR(20)      ,       
   NOD_NO       CHAR(11)     DEFAULT ' ',                
   TLR_ID       CHAR(7)     DEFAULT ' ',                
   TRM_NO       CHAR(8)     DEFAULT ' ',                
   NOD_TRC      CHAR(20)    DEFAULT ' ',                
   TXN_CNL      CHAR(3)     DEFAULT ' ',                
   ITG_TYP      CHAR(1)    ,                   
   FTXN_TM      TIME    ,                   
   FRSP_CD      CHAR(6)     ,                   
   HLOG_NO      CHAR(20)     DEFAULT ' ',                
   HRSP_CD      CHAR(6)     DEFAULT ' ',                
   HTXN_ST      CHAR(1)     DEFAULT 'U',                
   TCK_NO       CHAR(20)    DEFAULT ' ',                
   HTXN_CD      CHAR(6)     DEFAULT ' ' ,       
   HTXN_SB      CHAR(3)     DEFAULT ' ' ,       
   TXN_SRC      CHAR(5)     DEFAULT ' ' ,       
   CAR_TYP      CHAR(03)    ,                  
   CAR_NO      CHAR(20)    DEFAULT ' ',                 
   PAY_MON      CHAR(02)     ,                  
   TCUS_NM      CHAR(90)    DEFAULT ' ',                
   TACT_DT      DATE    ,                
   TLOG_NO      CHAR(30)    DEFAULT ' ',                
   THD_KEY      CHAR(20)    DEFAULT ' ',                
   TRSP_CD      CHAR(6)    DEFAULT ' ',                 
   TTXN_ST      CHAR(1)     DEFAULT 'U',                
   TXN_ST      CHAR(1)     DEFAULT 'U',                 
   TXN_ATR      CHAR(32)    ,                   
   TXN_MOD      CHAR(1)     DEFAULT ' ' ,       
   PAY_MOD      CHAR(1)     DEFAULT ' ' ,       
   CAGT_NO      CHAR(10)    DEFAULT ' ' ,       
   TACT_NO      CHAR(21)    DEFAULT ' ' ,       
   TXN_DAT     DATE        ,    
   RVS_RSP      CHAR(6)         DEFAULT ' ',    
   INV_NO      CHAR(30)     DEFAULT ' ',       
   CTBL_NM      CHAR(30)      DEFAULT ' ',      
   EXT_KEY      BIGINT ,    
   BAK_FLD1             CHAR(30),
   BAK_FLD2             CHAR(30),
   BAK_FLD3             CHAR(30),
   RSV_FLD1             VARCHAR(300),                            
   PRIMARY KEY (SQN)                                    
)IN "BBIP_APP"
INDEX IN  "BBIP_APP_INDEX";                                                      
                            
comment on column GDEUPSB.TRSP_TXN_JNL.BR_NO is                                                                                                                                
'分行号';                                                                                                                                                                       
                                                                                                                                                                                                                  
comment on column GDEUPSB.TRSP_TXN_JNL.SQN is                                                                                                                                             
'流水号';                                                                                                                                                                       
                                                                                                                                                                                                                  
comment on column GDEUPSB.TRSP_TXN_JNL.TTXN_CD is                                                                                                                             
'渠道交易码';                                                                                                                                                                     
                                                                                                                                                                                                                  
comment on column GDEUPSB.TRSP_TXN_JNL.TXN_COD is                                                                                                                                       
 '交易码';                                                                                                                                                                      
                                                                                                                                                                                                                  
comment on column GDEUPSB.TRSP_TXN_JNL.TXN_TYP is                                                                                                                             
 '交易类型';                                                                                                                                                                        
                                                                                                                                                                                                                  
comment on column GDEUPSB.TRSP_TXN_JNL.BUS_TYP is                                                                                                                                          
'业务类型';                                                                                                                                                                    
                                                                                                                                                                                                                  
comment on column GDEUPSB.TRSP_TXN_JNL.CRP_COD is                                                                                                                             
'单位代码';                                                                                                                                                                                
                                                                                                                                                                                                                  
comment on column GDEUPSB.TRSP_TXN_JNL.ACT_DAT is                                                                                                                                            
 '会计日期';                                                                                                                                                                    
                                                                                                                                                                                                                  
comment on column GDEUPSB.TRSP_TXN_JNL.TXN_AMT is                                                                                                                             
'交易金额';                                                                                                                                                                                   
                                                                                                                                                                                                                  
comment on column GDEUPSB.TRSP_TXN_JNL.FEE is                                                                                                                                                 
'交易手续费';                                                                                                                                                                  
                                                                                                                                                                                                                  
comment on column GDEUPSB.TRSP_TXN_JNL.ACT_TYP is                                                                                                                              
'账号类型 (1－一本通，2－普通存折，4－卡)';                                                                                                                                                     
                                                                                                                                                                                                                  
comment on column GDEUPSB.TRSP_TXN_JNL.ACT_NO is                                                                                                                               
'客户帐号';                                                                                                                                                                    
 comment on column GDEUPSB.TRSP_TXN_JNL.ACT_SQN is                                                                                                                                            
'账号顺序号';                                                                                                                                                                  
comment on column GDEUPSB.TRSP_TXN_JNL.NOD_NO is                                                                                                                                         
'银行网点号';                                                                                                                                                                  
 comment on column GDEUPSB.TRSP_TXN_JNL.TLR_ID is                                                                                                                                              
 '银行柜员号';                                                                                                                                                                  
                                                                                                                                                                                                                  
comment on column GDEUPSB.TRSP_TXN_JNL.TRM_NO is                                                                                                                              
'终端号';                                                                                                                                                                                    
comment on column GDEUPSB.TRSP_TXN_JNL.NOD_TRC is                                                                                                                             
 '网点流水号';                                                                                                                                                                                   
comment on column GDEUPSB.TRSP_TXN_JNL.TXN_CNL is                                                                                                                             
'交易渠道';                                                                                                                                                                                  
comment on column GDEUPSB.TRSP_TXN_JNL.ITG_TYP is                                                                                                                             
 '完整性类型';                                                                                                                                                                                
comment on column GDEUPSB.TRSP_TXN_JNL.FTXN_TM is                                                                                                                             
'前置交易时间';                                                                                                                                                                               
comment on column GDEUPSB.TRSP_TXN_JNL.FRSP_CD is                                                                                                                             
 '前置响应码';                                                                                                                                                                         
comment on column GDEUPSB.TRSP_TXN_JNL.HLOG_NO is                                                                                                                             
 '主机流水号';                                                                                                                                                                                 
comment on column GDEUPSB.TRSP_TXN_JNL.HRSP_CD is                                                                                                                             
'主机响应码';                                                                                                                                                                       
comment on column GDEUPSB.TRSP_TXN_JNL.HTXN_ST is                                                                                                                             
'主机交易状态:U-初始,T-超时,F-失败,S-成功,C-抹账';                                                                                                                                       
comment on column GDEUPSB.TRSP_TXN_JNL.TCK_NO  is                                                                                                                             
'会计流水号';                                                                                                                                                                                       
comment on column GDEUPSB.TRSP_TXN_JNL.HTXN_CD is                                                                                                                             
 '主机交易码';                                                                                                                                                                               
comment on column GDEUPSB.TRSP_TXN_JNL.HTXN_SB is                                                                                                                             
 '交易子码';                                                                                                                                                                                  
 comment on column GDEUPSB.TRSP_TXN_JNL.TXN_SRC is                                                                                                                             
'第三方系统标识';                                                                                                                                                                           
comment on column GDEUPSB.TRSP_TXN_JNL.CAR_TYP is                                                                                                                             
'车牌类别';                                                                                                                                                                               
 comment on column GDEUPSB.TRSP_TXN_JNL.CAR_NO  is                                                                                                                             
'车牌号码';                                                                                                                                                                                        
comment on column GDEUPSB.TRSP_TXN_JNL.PAY_MON is                                                                                                                             
'缴费月数';                                                                                                                                                                                              
comment on column GDEUPSB.TRSP_TXN_JNL. TCUS_NM  is                                                                                                                           
 '车主名';                                                                                                                                                                                      
comment on column GDEUPSB.TRSP_TXN_JNL. TACT_DT  is                                                                                                                           
'第三方帐务日期';                                                                                                                                                                               
comment on column GDEUPSB.TRSP_TXN_JNL. TLOG_NO  is                                                                                                                           
'第三方流水号';                                                                                                                                                                                     
comment on column GDEUPSB.TRSP_TXN_JNL. THD_KEY  is                                                                                                                           
'第三方键值';                                                                                                                                                                      
comment on column GDEUPSB.TRSP_TXN_JNL. TRSP_CD  is                                                                                                                           
'第三方响应码';                                                                                                                                                                             
comment on column GDEUPSB.TRSP_TXN_JNL.TTXN_ST is                                                                                                                           
 '第三方交易状态';                                                                                                                                                                     
comment on column GDEUPSB.TRSP_TXN_JNL.TXN_ST is                                                                                                                            
 '交易状态 U:初始状态；S 成功；F 失败；C 抹账；T 超时；X 未发送';                                                                                                                             
comment on column GDEUPSB.TRSP_TXN_JNL.TXN_ATR is                                                                                                                           
 '交易属性';                                                                                                                                                                      
comment on column GDEUPSB.TRSP_TXN_JNL.TXN_MOD is                                                                                                                           
 '交易方式(0－现金，1－一本通，2－普通存折，4－卡，5－支票)';                                                                                                                                 
comment on column GDEUPSB.TRSP_TXN_JNL.PAY_MOD is                                                                                                                           
 '支付方式(1－凭密支取,2－凭印支取,3－凭证支取,4－任意支取,5－签字支取,6－支付密码,7－电子验印,0－不验密)';                                                                                    
comment on column GDEUPSB.TRSP_TXN_JNL.CAGT_NO is                                                                                                                           
 '商户协议编号';                                                                                                                                                                                
comment on column GDEUPSB.TRSP_TXN_JNL.TACT_NO is                                                                                                                              
'单位代理账号(代收时为入帐帐户；代付时为出帐帐户；) ';                                                                                                                         
comment on column GDEUPSB.TRSP_TXN_JNL.TXN_DAT is                                                                                                                            
'交易日期';                                                                                                                                                                    
comment on column GDEUPSB.TRSP_TXN_JNL.RVS_RSP is                                                                                                                            
 '主机抹账结果(最后一次有返回的抹账结果)';                                                                                                                                      
comment on column GDEUPSB.TRSP_TXN_JNL.INV_NO  is                                                                                                                            
 '发票号';                                                                                                                                                                      
comment on column GDEUPSB.TRSP_TXN_JNL.CTBL_NM is                                                                                                                            
 '扩展子表名';                                                                                                                                                                  
 comment on column GDEUPSB.TRSP_TXN_JNL.EXT_KEY is                                                                                                                            
 '扩展子表键值';         
 comment on column GDEUPSB.TRSP_TXN_JNL.BAK_FLD1 is
'备用字段1';
comment on column GDEUPSB.TRSP_TXN_JNL.BAK_FLD2 is
'备用字段2';
comment on column GDEUPSB.TRSP_TXN_JNL.BAK_FLD3 is
'备用字段3';
comment on column GDEUPSB.TRSP_TXN_JNL.RSV_FLD1 is
'预留字段1';                                                                                                                                                           
                                                                                                                                                                                                                  
----------------路桥代收费更改发票登记表
drop table GDEUPSB.TRSP_INV_CHG_INFO;
create table GDEUPSB.TRSP_INV_CHG_INFO
(
   SQN      char(20)     not null,                       
   THD_KEY     char(20)     ,           
   TLOG_NO     char(20)    ,        
   INV_NO      char(20)      ,          
   OINV_NO     char(20)     ,           
   CAR_TYP     char(3)      ,        
   CAR_NO     char(20)    ,            
   TACT_DT     DATE      ,              
   ACT_DAT     DATE      ,                
   NOD_NO      char(11)      default '',         
   TLR_ID      char(7)      default '',    
   primary key (SQN)                                       
) IN "BBIP_APP"
INDEX IN  "BBIP_APP_INDEX";                                                           
               comment on column GDEUPSB.TRSP_INV_CHG_INFO.SQN         is    
         '流水号';                                                         
             comment on column GDEUPSB.TRSP_INV_CHG_INFO.THD_KEY is        
         '缴费键值';                                                       
             comment on column GDEUPSB.TRSP_INV_CHG_INFO.TLOG_NO is        
        '原缴费第三方流水号';                                              
             comment on column GDEUPSB.TRSP_INV_CHG_INFO.INV_NO   is       
        '新发票号';                                                        
             comment on column GDEUPSB.TRSP_INV_CHG_INFO.OINV_NO   is      
     '发票号';                                                             
             comment on column GDEUPSB.TRSP_INV_CHG_INFO.CAR_TYP   is      
  '车辆类型';                                                              
             comment on column GDEUPSB.TRSP_INV_CHG_INFO.CAR_NO     is     
    '车辆类型';                                                            
             comment on column GDEUPSB.TRSP_INV_CHG_INFO.TACT_DT   is      
  '原交易日';                                                              
             comment on column GDEUPSB.TRSP_INV_CHG_INFO.ACT_DAT    is     
 '会计日期';                                                               
             comment on column GDEUPSB.TRSP_INV_CHG_INFO.NOD_NO      is    
    '网店号';                                                              
             comment on column GDEUPSB.TRSP_INV_CHG_INFO.TLR_ID       is   
   '柜员号'; 
   
   ---------------年票管理表
   drop table GDEUPSB.TRSP_NP_MANAG;
   CREATE TABLE GDEUPSB.TRSP_NP_MANAG                     
(
   NOD_NO      CHAR(11)  ,                                                       
   SQN      CHAR(20)   ,                               
   ID_NO      CHAR(10)     not null  ,                                   
   CAR_NO       CHAR(20)       ,                               
   INV_NO      CHAR(30)       ,                               
   PRT_DAT      DATE       ,                               
   BEG_DAT      DATE       ,                               
   END_DAT      DATE       ,                               
   STATUS     CHAR(1)           ,                            
   TLR_ID     CHAR(7)         ,                            
   PRIMARY KEY (ID_NO)                                             
) IN "BBIP_APP"
INDEX IN  "BBIP_APP_INDEX";                                                                                                                                                   
                                                           
  comment on column GDEUPSB.TRSP_NP_MANAG.NOD_NO   is             
'网点号';                               
  comment on column GDEUPSB.TRSP_NP_MANAG.SQN   is             
'自助通缴费流水号';                                                      
 comment on column GDEUPSB.TRSP_NP_MANAG.ID_NO       is             
'年票标识号';                                                        
  comment on column GDEUPSB.TRSP_NP_MANAG.CAR_NO     is            
'车牌号';                                                            
  comment on column GDEUPSB.TRSP_NP_MANAG.INV_NO   is             
'发票号';                                                    
  comment on column GDEUPSB.TRSP_NP_MANAG.PRT_DAT   is             
'打印日期';                                                    
  comment on column GDEUPSB.TRSP_NP_MANAG.BEG_DAT   is                 
'有效日期开始日期';                                                        
  comment on column GDEUPSB.TRSP_NP_MANAG.END_DAT   is             
'有效日期结束日期';                                                        
  comment on column GDEUPSB.TRSP_NP_MANAG.STATUS  is             
'年票状态 U-初始状态 0-打印 1-废弃';                                   
  comment on column GDEUPSB.TRSP_NP_MANAG.TLR_ID is             
'柜员号';         
     
  --------------------轧账管理表
  drop table GDEUPSB.TRSP_ZZ_MANAG;
  CREATE TABLE GDEUPSB.TRSP_ZZ_MANAG                     
(
   NOD_NO      CHAR(6)    not null,                                                       
                                                       
   ZZ_DAT      CHAR(8)    not null,                               
                                                        
   FLG         CHAR(1)    not null,                                   
   PRIMARY KEY (NOD_NO,ZZ_DAT)                                             
) IN "BBIP_APP"
INDEX IN  "BBIP_APP_INDEX";                                                                                                                                                   
                                                           
       comment on column GDEUPSB.TRSP_ZZ_MANAG.NOD_NO   is             
     '网点号';                               
       comment on column GDEUPSB.TRSP_ZZ_MANAG.ZZ_DAT   is             
     '轧账日期';                                                      
      comment on column GDEUPSB.TRSP_ZZ_MANAG.FLG       is             
     '状态标识';                                                        
                                                           
                                                                        
drop table  gdeupsb.Gds_Agt_Water;   --水费协议明细表
create table gdeupsb.Gds_Agt_Water
(
  Sub_Sts   char(1)   default '0',
--状态( 0-有效 1-无效 )
  LAgt_St   char(1)   default 'U',
--本地协议状态( U-未校验 C-变更 F-校验失败 S-校验成功 )
  TAgt_St   char(1)   default 'U',
--第三方协议状态( U-未校验 C-变更 F-校验失败 S-校验成功 )
  Txn_Cnl   char(3)   not null,
--签约途径( TRM-柜台 WEB-网站 ... )
  Gds_BId   char(5)   not null,
--代理业务ID
  Act_No    char(21)  not null,
--协议业务交易账号
  Gds_AId   char(55)  not null,
--协议号
  Bnk_Typ   char(2)   default '',
--银行类型
  Bnk_No    char(12)  default '',
--银行行号
  Bnk_Nam   char(60)  default '',
--银行行名
  Org_Cod   char(12)  default '',
--单位代码
  Org_Nam   char(60)  default '',
--单位名称
  TBus_Tp   char(5)   default '',
--业务种类
  TCus_Id   char(20)  not null,
--客户标识
  TCus_Nm   char(60)  default '',
--客户标识
  Eff_Dat   char(8)   default '',
--生效日期
  Ivd_Dat   char(8)   default '',
--失效日期
  LEr_Msg   char(60)  default '',
--本地协议提示
  TEr_Msg   char(60)  default '',
--第三方协议提示
  Area_Id  char(5),
--所属区所
  batch_Id  char(12),
--批次号
  usb_flg  char(1) default 'Y',
--是否可制盘标志
  PRIMARY KEY( Gds_BId, Act_No, Gds_AId, TCus_Id )
) IN "BBIP_APP"
INDEX IN  "BBIP_APP_INDEX";

--广东省代理业务控制表
drop table GDEUPSB.Gds_Run_Ctl;
create table GDEUPSB.Gds_Run_Ctl
(
  Gds_BId   char(6)   not null,
--代理业务ID( 3位渠道代码+1位分行标识+2位序号，其中分行标识0-全省类 1-广东 2-珠海.....)
  Gds_BNm   char(60)  not null,
--代理业务名称
  Agt_MTb   char(30)  default '',
--协议主表
  Agt_STb   char(30)  default '',
--协议子表
  Jnl_MTb   char(12)  default '',
--流水主表
  Jnl_STb   char(12)  default '',
--流水子表
--流程控制要素
--流程控制要素
--流程控制要素
  Ath_Flg   char(2)   default '00',
--授权及授权级别( 00--不授权  XX--对应授权级别 )
  Nam_Chk   char(1)   default '0',
--户名校验( 0-不校验 1-校验 )
  Pin_Chk   char(1)   default '0',
--交易密码校验( 0-不校验 1-校验个人交易密码 )
  Psw_Chk   char(1)   default '0',
--支付密码校验( 0-不校验 1-校验对公支付密码 )
  Lcl_Chk   char(1)   default '0',
--本地个性化业务处理流程【其他应用验证等】( 0-无  1-同步验证 2-异步验证 )
  Lcl_Svr   char(8)   default '',
--本地服务
  Lcl_Cod   char(6)   default '',
--本地交易
  Thd_Chk   char(1)   default '0',
--第三方个性化业务处理流程【第三方验证等】( 0-无  1-同步验证 2-异步验证 )
  Thd_Svr   char(8)   default '',
--外发服务
  Thd_Cod   char(6)   default '',
--外发交易
  Agt_Br    char(11)   not null,
--主办分行号
  PRIMARY KEY( Gds_BId )
)
 IN "BBIP_APP"
INDEX IN  "BBIP_APP_INDEX";
--
--
--广东省代理业务协议主表
--
drop table GDEUPSB.Gds_Agt_Inf;
create table GDEUPSB.Gds_Agt_Inf
(
  Agt_Sts   char(1)   default '0',
--状态
  Gds_BId   char(5)   not null,
--代理业务ID
  BCus_No   char(13)  default '',
--客户号
  Act_No    char(21)  not null, 
--协议业务交易账号
  Act_Typ   char(1)   not null,
--账户性质
  Act_Nm    char(60)  default '', 
--协议业务账号户名
  Vch_Typ   char(3)   default '',
--凭证种类
  Vch_No    char(8)   default '',
--凭证号码
  Bok_Seq   char(5)   default '',
--一本通序号
  Pfa_Sub   char(3)   default '',
--财政代码
  BCus_Id   char(30)  default '',
--预算单位编码
  Id_Typ    char(2)   default '21',
--证件类型
  Id_No     char(30)  default '',
--证件号码
  Tel_Typ   char(1)   default '',
--固话类型
  Tel_No    char(30)  default '',
--固话号码
  Mob_Typ   char(1)   default '',
--移话类型
  Mob_Tel   char(30)  default '',
--移话号码
  EMail    char(60)  default '',
--邮件
  Addr     char(120) default '',
--地址
  Nod_No    char(11)   default '',
--操作网点
  BrNo     char(11)   not null,
--分行号
  Eff_Dat   char(8)   not null,
--生效日期
  Ivd_Dat   char(8)   default '',
--停用日期
 PRIMARY KEY( Gds_BId,Act_No)
) IN "BBIP_APP"
INDEX IN  "BBIP_APP_INDEX";

drop table gdeupsb.Gds_Agt_Trc;
create table gdeupsb.Gds_Agt_Trc
(
  Opr_Tim  char(14)  not null,
--操作时间(机器时间)
  Act_Dat  char(8)   not null,
--会计日期
  Txn_Cnl  char(3)   not null,
--操作渠道
  Tlr_Id   char(7)   not null,
--操作员
  Sup1_Id  char(7)   default '',
--授权员1
  Sup2_Id  char(7)   default '',
--授权员2
  Act_No   char(21)  not null,
--账号
  Nod_No   char(11)   default '',
--网点
  BrNo    char(11)   default '',
--操作分行
  TTxn_Cd  char(25)   default '',
--接入交易码
  Txn_Cod  char(25)   default '',
--处理交易码 
  Prv_Dat clob(3072) default ''
)
 IN "BBIP_APP"
INDEX IN  "BBIP_APP_INDEX";

create index ni1_GdsAgtTrc on gdeupsb.Gds_Agt_Trc( Act_No );


--燃气每天动态协议表，没有主键，每天根据460701加进去
--字段				ICS				EUPS
--==================================
--用户编号		UserNo		CusNo
--指令				TCommd		TCommd
--银行账号		ActNo			CusAc
--付款人名称	ActNam		CusNme
--账号类型		ActTyp		ACC_TYP
--操作日期		optDat		
--操作柜员		optNod		
--证件类型		IdType		IdTyp
--证件号码		IdNo			IdNo
--联系人名称	cntNam		ThdCusNam
--手机号码		TelNo			CmuTel
--联系人地址	CntAdr		ThdCusAdr

drop table GDEUPSB.GDEUPS_GAS_CUS_DAY;
create table GDEUPSB.GDEUPS_GAS_CUS_DAY
(
	SEQUENCE CHAR(32) NOT NULL,
  CUS_NO  char(12) not null,
--用户编号
  T_Commd  char(10) not null,
--指令(Add 为新增用户协议 Edit 为修改用户协议 Stop 为取消用户协议)
  CUS_AC    char(32) not null,  
--银行账号
  CUS_NME   char(32) not null,
--付款人名称
  ACC_TYP    char(32),  
--账号类型(0对公1对私活期一本通2对私普通折4对私卡)
  OPT_DAT    char(10),  
--操作日期
	OPT_NOD    char(8),  
--操作柜员
  ID_TYP   char(2),
--证件类型
  ID_NO     char(32),
--证件号码
  THD_CUS_NAM     char(32),
--联系人名称
  CMU_TEL     char(50),
--联系人手机号码
  THD_CUS_ADR     char(100),
--联系人地址

PRIMARY KEY(SEQUENCE)
)IN "BBIP_APP"
INDEX IN  "BBIP_APP_INDEX";


drop table GDEUPSB.GDEUPS_FBPE_FILE_BATCH_TMP;
create table GDEUPSB.GDEUPS_FBPE_FILE_BATCH_TMP
(
  SQN     char(20)    not null default ' ',
--交易流水号
  Txn_NO    char(3)    not null default ' ',
--交易码
  ORG_CDE    char(3)    default ' ',
--机构号
  TLR_No    char(6)      default ' ',
--操作员号
  Txn_Tim    char(14)    default ' ',
--交易时间
  CUS_NO    char(20)     default ' ',
--客户号
  Acc_No  char(10)      default ' ',
--账单号
  Cus_Ac    char(20)     default ' ',
--客户卡号
  Cus_Nam    char(20)    default ' ',
--客户名称
 Txn_Amt  char(10)     default ' ',
--交易金额
 Acc_Amt  char(10)     default ' ',
--扣账金额
  Act_No  char(20)     default ' ',
--银行账号
  bank_No  char(6)     default ' ',
--银行编号
  bank_Nam  char(30)     default ' ',
--银行名称
  Cos_Mon  char(6)     default ' ',
--话费月份

   RSV_FLD1             VARCHAR(300) default ' ',
   RSV_FLD2             VARCHAR(300) default ' ',
   RSV_FLD3             VARCHAR(300) default ' ',
   RSV_FLD4             VARCHAR(300) default ' ',
   RSV_FLD5             VARCHAR(300) default ' ',
   RSV_FLD6             VARCHAR(300) default ' ',
   RSV_FLD7             VARCHAR(300) default ' ',
   RSV_FLD8             VARCHAR(300) default ' ',
--预留字段
primary key (SQN)
)
 IN "BBIP_APP"
INDEX IN  "BBIP_APP_INDEX";


drop table GDEUPSB.GDEUPS_GZAG_BATCH_TMP;
CREATE
    TABLE GDEUPSB.GDEUPS_GZAG_BATCH_TMP
    (
        SQN CHARACTER(27) NOT NULL,
        LENDTYPE_OR_GAMENO CHARACTER(2),
        THD_CUS_NO CHARACTER(14),
        TXN_DTE CHARACTER(8),
        TXN_TME TIMESTAMP,
        CUS_AC CHARACTER(30),
        THD_CUS_NME CHARACTER(30),
        TXN_AMT DECIMAL(16,2),
        BV_NO CHARACTER(18),
        BAK_FLD CHARACTER(128),
        RSP_CODE CHARACTER(2),
        LEND_START_DATE CHARACTER(8),
        LEND_END_DATE CHARACTER(8),
        NUMBER CHARACTER(15),
        TOT_CNT CHARACTER(8),
        TOT_AMT DECIMAL(16,2),
        SUC_TOT_CNT CHARACTER(8),
        SUC_TOT_AMT DECIMAL(16,2),
        FAL_TOT_CNT CHARACTER(8),
        FAL_TOT_AMT DECIMAL(16,2),
        SITR_LOGIC_NO CHARACTER(8),
        POUR_AMT DECIMAL(16,2),
        LOTT_SQN CHARACTER(5),
        BANK_SQN CHARACTER(20),
        EUPS_BUS_TYP CHARACTER(10),
        AWARD_DATE CHARACTER(8),
        BUY_LOTT_AMT DECIMAL(16,2),
        TELEPHONE CHARACTER(20),
        RSV_FLD1 VARCHAR(300),
        RSV_FLD2 VARCHAR(300),
        RSV_FLD3 VARCHAR(300),
        RSV_FLD4 VARCHAR(300),
        RSV_FLD5 VARCHAR(300),
        RSV_FLD6 VARCHAR(300),
        PRIMARY KEY (SQN)
    )
     IN "BBIP_APP"
INDEX IN  "BBIP_APP_INDEX";
    

drop table GDEUPSB.GDEUPS_FBPD_NELE_BATCH_TMP;
--中山文件批量系统-电费批量NELE临时表
create table GDEUPSB.GDEUPS_FBPD_NELE_BATCH_TMP (
SQN CHAR(32) NOT NULL,
BAT_FLG CHAR(20),
--批量代收信息标识
CCY CHAR(3),
--货币符号
PAY_FLG CHAR(1) default '1',
--代收付标志
FEE_TYP CHAR(3),
--费项代码
TOT_CNT CHAR(10),
--总记录数
TOT_AMT CHAR(14),
--总金额
SUB_DTE CHAR(8),
--送银行日期/提交日期
TXN_DTE CHAR(8),
--应划款日期
THD_SQN CHAR(16),
--流水号
BK CHAR(12),
--银行代码
SER_NO CHAR(10),
--编号
PAY_FEE_NO  CHAR(32),
--缴费号
BR CHAR(12),
--客户开户行行号
CUS_AC CHAR(32),
--帐号
CUS_NME CHAR(40),
--客户账号名称
TXN_AMT CHAR(12),
--金额
TXN_TYP CHAR(1),
--扣款方式
CUS_DPM CHAR(8),
--用户所在部门
FEE_MON CHAR(6),
--电费月份
FEE_CNT CHAR(2),
--电费次数
THD_CUS_NME CHAR(40),
--客户名称
ELE_AC CHAR(32),
--供电企业账号
FRZ_AMT CHAR(12),
--应扣电费
F_SEQ_NO CHAR(120),
--违约金
CRP_COD CHAR(5),
--计量点号
EVID_NO CHAR(16),
--应收凭证号
RS_FLD1 CHAR(128),
--处理说明
RS_FLD2 CHAR(128),
RS_FLD3 CHAR(40),
--说明
RS_FLD4 CHAR(128),
RS_FLD5 CHAR(128),
PRIMARY KEY (SQN)
)
 IN "BBIP_APP"
INDEX IN  "BBIP_APP_INDEX";

drop table GDEUPSB.GDEUPS_FBPD_MPOS_BATCH_TMP;
--中山文件批量系统-移动POS临时表
CREATE TABLE GDEUPSB.GDEUPS_FBPD_MPOS_BATCH_TMP(
SQN CHAR(32) NOT NULL,
THD_SQN CHAR(32) NOT NULL,
--刷卡交易系统跟踪号
POS_NO CHAR(20),
--支付终端编号
COM_AC CHAR(32),
--签约商户卡号
COM_NO CHAR(32),
--刷卡商户编号
CUS_AC CHAR(32),
--刷卡卡号
TXN_DTE CHAR(12),
--刷卡交易日期
TXN_TME CHAR(12),
--刷卡交易时间
TXN_FEE CHAR(32),
--刷卡金额
TXN_AMT CHAR(32),
--本金
TXN_CHR CHAR(32),
--手续费
TXN_RNT CHAR(32),
--租金
CHK_DATE CHAR(12),
--清算日期
SEQ_NO CHAR(32),
--SeqNo
POS_FLD1 CHAR(32),
POS_FLD2 CHAR(64),
POS_FLD3 CHAR(128),
POS_FLD4 CHAR(32),
POS_FLD5 CHAR(32),

PRIMARY KEY (SQN)

)
 IN "BBIP_APP"
INDEX IN  "BBIP_APP_INDEX";



drop table GDEUPSB.GDEUPS_FBPD_OBUS_BATCH_TMP;
--中山文件批量系统-其他业务临时表
CREATE TABLE GDEUPSB.GDEUPS_FBPD_OBUS_BATCH_TMP(
SQN CHAR(32) NOT NULL,
COM_NO CHAR(20),
--机构号/单位代码/单位编号
CUS_AC CHAR(26),
--账号
CUS_NO CHAR(32),
--户号
CUS_NME CHAR(30),
--户名
THD_CUS_NO CHAR(20),
--客户编号
CCY CHAR(3),
--币种
TXN_AMT CHAR(13),
--交易金额
SUC_FLG CHAR(1),
--标志 Y-成功  标识 1成功 2失败???
CHR_DTE CHAR(25),
--发单日期
SUB_DTE CHAR(10),
--提交日期
SEQ_NO CHAR(32),
TMP_FLD1 CHAR(32),
TMP_FLD2 CHAR(64),
TMP_FLD3 CHAR(128),
TMP_FLD4 CHAR(32),
TMP_FLD5 CHAR(32),
PRIMARY KEY (SQN)
)
 IN "BBIP_APP"
INDEX IN  "BBIP_APP_INDEX";


drop table GDEUPSB.GDEUPS_GASH_BATCH_TMP;
--惠州燃气批量托收临时表
CREATE TABLE GDEUPSB.GDEUPS_GASH_BATCH_TMP(
 SQN CHAR(32) NOT NULL,
 --流水号
 BAT_NO CHAR(32),
 --批次号
 BAT_STS CHAR(1),
 --批次状态
 PKG_FLG CHAR(1),
 --批量扣收标志0表示单笔1表示批量未发送2表示批量扣收已发送
  TOT_CNT CHAR(12),
 --总笔数
 TOT_AMT CHAR(18),
 --总金额
 SUC_TOT_CNT CHAR(12),
 --成功总笔数
 SUC_TOT_AMT CHAR(18),
 --成功总金额
 TXN_DTE DATE,
 --交易日期
 TXN_TME TIMESTAMP,
 --交易时间
 THD_SQN CHAR(32),
 --燃气托收流水号
 BK CHAR(4),
 --银行标识
 CUS_NO CHAR(20),
 --用户编码
 CUS_NME CHAR(32),
 --用户名
 CUS_AC CHAR(32),
 --银行账号
 PAY_MON CHAR(6),
 --费用年月
 REQ_TXN_AMT CHAR(18),
 --应缴费用
 TXN_AMT CHAR(18),
 --交易金额（已扣费用）
 TMP_FLD1 CHAR(32),
 --备用字段1
 TMP_FLD2 CHAR(32),
 TMP_FLD3 CHAR(64),
 TMP_FLD4 CHAR(128),
 TMP_FLD5 CHAR(128),
 PRIMARY KEY (SQN)
 
) IN "BBIP_APP"
INDEX IN  "BBIP_APP_INDEX";



drop table GDEUPSB.GDEUPS_ELE_TMP;
CREATE
    TABLE GDEUPSB.GDEUPS_ELE_TMP
    (
        SQN CHARACTER(27) NOT NULL,
        BANK_NO CHARACTER(4),
        COM_NO CHARACTER(10),
        FILE_DTE CHARACTER(8),
        CCY CHARACTER(5),
        BUS_KND CHARACTER(3),
        NUMBER CHARACTER(10),
        THD_CUS_NO CHARACTER(20),
        THD_CUS_NME CHARACTER(64),
        CUS_AC CHARACTER(32),
        CUS_NME CHARACTER(128),
        FULL_DED_FLAG CHARACTER(1),
        PAY_TYPE CHARACTER(3),
        ACCOUNT_NO CHARACTER(27),
        ELECTRICITY_YEARMONTH CHARACTER(6),
        PAYMENT_MONEY DECIMAL(16,2),
        CAPITIAL DECIMAL(16,2),
        DEDIT DECIMAL(16,2),
        PAYMENT_RESULT CHARACTER(2),
        TXN_AMT DECIMAL(16,2),
        BANK_SQN CHARACTER(20),
        TXN_DTE DATE,
        TXN_TME TIMESTAMP,
        BAK_FLD VARCHAR(300),
        RSV_FLD1 VARCHAR(300),
        RSV_FLD2 VARCHAR(300),
        RSV_FLD3 VARCHAR(1024),
        RSV_FLD4 VARCHAR(1024),
        RSV_FLD5 VARCHAR(1024),
        PRIMARY KEY (SQN)
    )
     IN "BBIP_APP"
INDEX IN  "BBIP_APP_INDEX";

COMMENT ON TABLE GDEUPSB.GDEUPS_ELE_TMP
IS
    '批量代收付临时表';
COMMENT ON COLUMN GDEUPSB.GDEUPS_ELE_TMP.SQN
IS
    '流水号';
COMMENT ON COLUMN GDEUPSB.GDEUPS_ELE_TMP.BANK_NO
IS
    '银行代码';
COMMENT ON COLUMN GDEUPSB.GDEUPS_ELE_TMP.COM_NO
IS
    '单位编码';
COMMENT ON COLUMN GDEUPSB.GDEUPS_ELE_TMP.FILE_DTE
IS
    '文件日期';
COMMENT ON COLUMN GDEUPSB.GDEUPS_ELE_TMP.CCY
IS
    '币种';
COMMENT ON COLUMN GDEUPSB.GDEUPS_ELE_TMP.BUS_KND
IS
    '收费方式';
COMMENT ON COLUMN GDEUPSB.GDEUPS_ELE_TMP.NUMBER
IS
    '序号';
COMMENT ON COLUMN GDEUPSB.GDEUPS_ELE_TMP.THD_CUS_NO
IS
    '缴费号';
COMMENT ON COLUMN GDEUPSB.GDEUPS_ELE_TMP.THD_CUS_NME
IS
    '结算户名称';
COMMENT ON COLUMN GDEUPSB.GDEUPS_ELE_TMP.CUS_AC
IS
    '扣款账户';
COMMENT ON COLUMN GDEUPSB.GDEUPS_ELE_TMP.CUS_NME
IS
    '扣款账户名称';
COMMENT ON COLUMN GDEUPSB.GDEUPS_ELE_TMP.FULL_DED_FLAG
IS
    '部分缴费标志';
COMMENT ON COLUMN GDEUPSB.GDEUPS_ELE_TMP.PAY_TYPE
IS
    '费用类型';
COMMENT ON COLUMN GDEUPSB.GDEUPS_ELE_TMP.ACCOUNT_NO
IS
    '账务流水号';
COMMENT ON COLUMN GDEUPSB.GDEUPS_ELE_TMP.ELECTRICITY_YEARMONTH
IS
    '电费月份';
COMMENT ON COLUMN GDEUPSB.GDEUPS_ELE_TMP.PAYMENT_MONEY
IS
    '应扣金额';
COMMENT ON COLUMN GDEUPSB.GDEUPS_ELE_TMP.CAPITIAL
IS
    '本金';
COMMENT ON COLUMN GDEUPSB.GDEUPS_ELE_TMP.DEDIT
IS
    '违约金';
COMMENT ON COLUMN GDEUPSB.GDEUPS_ELE_TMP.PAYMENT_RESULT
IS
    '扣款结果代码 1 已扣
2 已部分扣款
3 余额不足
4 账户不符
5 账户已销
6 坏账户及其他, 除“已扣”，其他扣不到款
7 直接借记支付中的金额超过事先规定限额
8 直接借记无授权记录'
    ;
COMMENT ON COLUMN GDEUPSB.GDEUPS_ELE_TMP.TXN_AMT
IS
    '实扣金额';
COMMENT ON COLUMN GDEUPSB.GDEUPS_ELE_TMP.BANK_SQN
IS
    '银行收费流水号';
COMMENT ON COLUMN GDEUPSB.GDEUPS_ELE_TMP.TXN_DTE
IS
    '扣款日期';
COMMENT ON COLUMN GDEUPSB.GDEUPS_ELE_TMP.TXN_TME
IS
    '扣款时间';
COMMENT ON COLUMN GDEUPSB.GDEUPS_ELE_TMP.BAK_FLD
IS
    '备注';
COMMENT ON COLUMN GDEUPSB.GDEUPS_ELE_TMP.RSV_FLD1
IS
    '预留字段1';
COMMENT ON COLUMN GDEUPSB.GDEUPS_ELE_TMP.RSV_FLD2
IS
    '预留字段2';
COMMENT ON COLUMN GDEUPSB.GDEUPS_ELE_TMP.RSV_FLD3
IS
    '预留字段3';
COMMENT ON COLUMN GDEUPSB.GDEUPS_ELE_TMP.RSV_FLD4
IS
    '预留字段4';
COMMENT ON COLUMN GDEUPSB.GDEUPS_ELE_TMP.RSV_FLD5
IS
    '预留字段5';

drop table GDEUPSB.TRSP_CHECK_TMP;
CREATE
    TABLE GDEUPSB.TRSP_CHECK_TMP
    (
        SQN CHARACTER(20) NOT NULL,
        TCHK_NO CHARACTER(20),
        TXN_DAT DATE,
        TXN_AMT DECIMAL(18,2),
        INV_NO CHARACTER(20),
        PAY_MON CHARACTER(2),
        CAR_TYP CHARACTER(3),
        CAR_NO CHARACTER(12),
        STATUE CHARACTER(1),
        PRIMARY KEY (SQN)
    )
     IN "BBIP_APP"
INDEX IN  "BBIP_APP_INDEX";

