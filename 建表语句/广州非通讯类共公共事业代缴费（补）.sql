drop table  gdeupsb.GDS_AGT_TEL;   --广东有线广播电视网络股份有限公司
create table gdeupsb.GDS_AGT_TEL
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
  Bnk_Nam   char(90)  default '',
--银行行名
  Org_Cod   char(12)  default '',
--单位代码
  Org_Nam   char(90)  default '',
--单位名称
  TBus_Tp   char(5)   default '',
--业务种类
  TCus_Id   char(40)  not null,
--客户标识
  TCus_Nm   char(90)  default '',
--客户标识
  Eff_Dat   char(8)   default '',
--生效日期
  Ivd_Dat   char(8)   default '',
--失效日期
  LEr_Msg   char(90)  default '',
--本地协议提示
  TEr_Msg   char(90)  default '',
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


--广东珠江数码协议子表
drop table  gdeupsb.GDS_AGT_DEG;
create table gdeupsb.GDS_AGT_DEG
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
  Bnk_Nam   char(90)  default '',
--银行行名
  Org_Cod   char(12)  default '',
--单位代码
  Org_Nam   char(90)  default '',
--单位名称
  TBus_Tp   char(5)   default '',
--业务种类
  TCus_Id   char(40)  not null,
--客户标识
  TCus_Nm   char(90)  default '',
--客户标识
  Eff_Dat   char(8)   default '',
--生效日期
  Ivd_Dat   char(8)   default '',
--失效日期
  LEr_Msg   char(90)  default '',
--本地协议提示
  TEr_Msg   char(90)  default '',
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


--广州煤气协议子表
drop table  gdeupsb.GDS_AGT_GAS;
create table gdeupsb.GDS_AGT_GAS
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
  Bnk_Nam   char(90)  default '',
--银行行名
  Org_Cod   char(12)  default '',
--单位代码
  Org_Nam   char(90)  default '',
--单位名称
  TBus_Tp   char(5)   default '',
--业务种类
  TCus_Id   char(40)  not null,
--客户标识
  TCus_Nm   char(90)  default '',
--客户标识
  Eff_Dat   char(8)   default '',
--生效日期
  Ivd_Dat   char(8)   default '',
--失效日期
  LEr_Msg   char(90)  default '',
--本地协议提示
  TEr_Msg   char(90)  default '',
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

--广东电力协议子表
drop table  gdeupsb.GDS_AGT_ELC;
create table gdeupsb.GDS_AGT_ELC
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
  Bnk_Nam   char(90)  default '',
--银行行名
  Org_Cod   char(12)  default '',
--单位代码
  Org_Nam   char(90)  default '',
--单位名称
  TBus_Tp   char(5)   default '',
--业务种类
  TCus_Id   char(40)  not null,
--客户标识
  TCus_Nm   char(90)  default '',
--客户标识
  Eff_Dat   char(8)   default '',
--生效日期
  Ivd_Dat   char(8)   default '',
--失效日期
  LEr_Msg   char(90)  default '',
--本地协议提示
  TEr_Msg   char(90)  default '',
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

--对照表2
DROP TABLE GDEUPSB.ZH_AGT_ACTNO ;
create table GDEUPSB.ZH_AGT_ACTNO 
(
   OLD_ACT   char(21) not null,
--21位新一本通主帐号
   ACT_NO    char(21) not null,
--21位一本通实体帐号
   primary key(OLD_ACT)
)
 IN "BBIP_APP"
INDEX IN  "BBIP_APP_INDEX";

--帐号信息表   对照表1
DROP TABLE GDEUPSB.ZH_ACTNO_INF;
create table GDEUPSB.ZH_ACTNO_INF
(
   OLD_ACT   char(21),
--旧账号
   ACT_NO    char(21) not null,
--新账号
   ACT_SQN   char(05) ,
--序号
   CUS_ID    char(13) ,
--新客户号
   ACT_TYP   char(1) default ' ',
--帐号类型 1 一本通 2 帐号 4 卡号 6 CD户
   ACT_NAM   char(90) default ' ',
--客户姓名
   OPN_NOD   char(11) default ' '
--开户网点 专指对公帐户
)
 IN "BBIP_APP"
INDEX IN  "BBIP_APP_INDEX";

create index ni1_actnoinf on GDEUPSB.ZH_ACTNO_INF(ACT_NO,ACT_TYP);
create index ni2_actnoinf on GDEUPSB.ZH_ACTNO_INF(OLD_ACT);

--发票凭证状态登记表(针对发票登记，使用，结帐情况)
DROP TABLE GDEUPSB.GDEUPS_INV_DTL_BOK;
CREATE TABLE GDEUPSB.GDEUPS_INV_DTL_BOK(
	INV_TYP		CHAR(3)   not null,--凭证类型 F17-自助设备发票
	IV_BEG_NO	CHAR(30)   not null,--凭证开始号码
	IV_END_NO	CHAR(30)   not null,--凭证结束号码
	TOL_NUM		CHAR(8)    not null,--张数
	OPR_TLR		CHAR(7)    not null,--领用柜员
	REG_TLR		CHAR(7)    not null default '',--发放柜员，登记真实柜员
	NODNO		CHAR(11)   not null default '',--网点号
	TCK_NO		CHAR(20)   not null default '',--非会计流水
	REG_DAT		CHAR(10)   not null default '',--登记日期
	SEQ_NO		CHAR(8)    not null default '',--启用流水号
	USE_DAT		CHAR(10)   not null default '',--启用日期
	CHK_DAT		CHAR(10)   not null default '',--结账日期
	USE_NUM		CHAR(10)   not null default '',--总共使用张数
	CLR_NUM		CHAR(8)    not null default '',--总共废除张数
	STATUS		CHAR(1)    not null default '',--状态0-发放状态1-领用状态2-结账状态
	primary key (INV_TYP,IV_BEG_NO,IV_END_NO)
)IN "BBIP_APP"
INDEX IN "BBIP_APP_INDEX";
CREATE INDEX GDEUPS_INV_DTL_BOK_N1 on GDEUPSB.GDEUPS_INV_DTL_BOK(INV_TYP,OPR_TLR,STATUS);
GRANT SELECT ON GDEUPSB.GDEUPS_INV_DTL_BOK TO GDEUPSA;
GRANT INSERT ON GDEUPSB.GDEUPS_INV_DTL_BOK TO GDEUPSA;
GRANT UPDATE ON GDEUPSB.GDEUPS_INV_DTL_BOK TO GDEUPSA;
GRANT REFERENCES ON GDEUPSB.GDEUPS_INV_DTL_BOK TO GDEUPSA;
GRANT DELETE ON GDEUPSB.GDEUPS_INV_DTL_BOK TO GDEUPSA;
GRANT ALTER ON GDEUPSB.GDEUPS_INV_DTL_BOK TO GDEUPSA;
GRANT INDEX ON GDEUPSB.GDEUPS_INV_DTL_BOK TO GDEUPSA;



--自助终端发票使用情况表(每个终端对应一条记录)
DROP TABLE GDEUPSB.GDEUPS_INV_TERM_INF;
CREATE TABLE GDEUPSB.GDEUPS_INV_TERM_INF(
	TLR_ID		CHAR(7)    not null,--领用柜员
	NODNO		CHAR(11)   not null default '',--网点号
	INV_TYP		CHAR(3)    not null,--凭证类型 F17-自助设备发票
	IV_BEG_NO	CHAR(30)   not null,--凭证开始号码
	IV_END_NO	CHAR(30)   not null,--凭证结束号码
	SEQ_NO		CHAR(8)    not null default '',--领用流水号
	INV_NUM		CHAR(8)    not null,--可用的发票数量
	USE_DAT		CHAR(10)   not null default '',--启用日期
	USE_NUM		CHAR(10)   not null default '0',--使用张数
	CLR_NUM		CHAR(8)    not null default '0',--作废张数
	primary key (TLR_ID)
)IN "BBIP_APP"
INDEX IN "BBIP_APP_INDEX";
CREATE INDEX GDEUPS_INV_TERM_INF_N1 on GDEUPSB.GDEUPS_INV_TERM_INF(INV_TYP,IV_BEG_NO,IV_END_NO);
GRANT SELECT ON GDEUPSB.GDEUPS_INV_TERM_INF TO GDEUPSA;
GRANT INSERT ON GDEUPSB.GDEUPS_INV_TERM_INF TO GDEUPSA;
GRANT UPDATE ON GDEUPSB.GDEUPS_INV_TERM_INF TO GDEUPSA;
GRANT DELETE ON GDEUPSB.GDEUPS_INV_TERM_INF TO GDEUPSA;
GRANT REFERENCES ON GDEUPSB.GDEUPS_INV_TERM_INF TO GDEUPSA;
GRANT ALTER ON GDEUPSB.GDEUPS_INV_TERM_INF TO GDEUPSA;
GRANT INDEX ON GDEUPSB.GDEUPS_INV_TERM_INF TO GDEUPSA;


--  发票交易信息记录表(每张发票对应的交易信息
DROP TABLE GDEUPSB.GDEUPS_INV_TXN_INF;
CREATE TABLE GDEUPSB.GDEUPS_INV_TXN_INF(
	INV_TYP		CHAR(3)    not null,--凭证类型 F17-自助设备发票
	IV_BEG_NO	CHAR(30)   not null,--凭证开始号码
	IV_END_NO	CHAR(30)   not null,--凭证结束号码
	USE_SEQ		CHAR(6)    not null,--使用序号
	STL_NUM		CHAR(8)    not null,--使用张数
	STL_FLG		CHAR(1)    not null,--处理方式:0-使用，1-废弃，U-装入
	ACT_DAT		CHAR(10)   not null,--会计日期
	TLR_ID		CHAR(7)    not null,--领用柜员
	NODNO		CHAR(11)    not null default '',--网点号
	QY_NO		CHAR(4)    not null default '',--企业编号:0002 电信,0542 联通,0003 移动,0643 公交
	OLD_SEQ		CHAR(25)   not null default '',--对应交易流水号
	OLD_TR_DATE	CHAR(10)   not null default '',--对应交易日期
	primary key (INV_TYP,IV_BEG_NO,IV_END_NO,USE_SEQ)
)IN "BBIP_APP"
INDEX IN "BBIP_APP_INDEX";
GRANT SELECT ON GDEUPSB.GDEUPS_INV_TXN_INF TO GDEUPSA;
GRANT INSERT ON GDEUPSB.GDEUPS_INV_TXN_INF TO GDEUPSA;
GRANT UPDATE ON GDEUPSB.GDEUPS_INV_TXN_INF TO GDEUPSA;
GRANT DELETE ON GDEUPSB.GDEUPS_INV_TXN_INF TO GDEUPSA;
GRANT REFERENCES ON GDEUPSB.GDEUPS_INV_TXN_INF TO GDEUPSA;
GRANT ALTER ON GDEUPSB.GDEUPS_INV_TXN_INF TO GDEUPSA;
GRANT INDEX ON GDEUPSB.GDEUPS_INV_TXN_INF TO GDEUPSA;


--柜员管理表（一个网点一条记录，柜员在机具操作发票相关交易使用）
DROP TABLE GDEUPSB.GDEUPS_TL_NO_MANAGER;
CREATE TABLE GDEUPSB.GDEUPS_TL_NO_MANAGER
(
  GYDM   char(20)  not null,--网点号
  PASSWD char(14)  not null--密码
)IN "BBIP_APP"
INDEX IN "BBIP_APP_INDEX";
CREATE INDEX GDEUPS_TL_NO_MANAGER_N1 on GDEUPSB.GDEUPS_TL_NO_MANAGER(GYDM);


drop table GDEUPSB.GD_ELEC_CLR_INF;
CREATE TABLE GDEUPSB.GD_ELEC_CLR_INF
(
  BR_NO          CHAR(11)    DEFAULT ' ',
--分行号
  C_Agt_No        CHAR(10)       not null,
--清算单位协议号
  clr_Dat        CHAR(8)         DEFAULT ' ',
--清算日期
  clr_Tim        CHAR(6)         DEFAULT ' ',
--清算时间
  clr_Sts        CHAR(1)         DEFAULT ' ',
--清算状态  0:交易状态；1:清算状态
  aut_Flg        CHAR(1)         DEFAULT ' ',
--系统定时自动清算标志  0:自动清算生效；1:禁止自动清算
PRIMARY KEY(C_Agt_No)
)
IN "BBIP_APP"
INDEX IN  "BBIP_APP_INDEX";

-- ============================================================
--   电力电费业务
--   Table: efeaclrtbl   (广州电力清算信息修改表)
-- ============================================================
drop table GDEUPSB.GD_ELEC_CLR_TBL;
CREATE TABLE GDEUPSB.GD_ELEC_CLR_TBL
(
  SQN      CHAR(32)    NOT NULL,
  BR_NO          CHAR(11)         DEFAULT ' ',
--分行号 
  Nod_No         CHAR(11)         DEFAULT ' ',
--网点号  
  Clr_Dat        CHAR(8)         DEFAULT ' ',
--修改清算日期
  Clr_Tim        CHAR(6)         DEFAULT ' ',
--修改清算时间
  Clr_Sts        CHAR(1)         DEFAULT ' ',
--修改清算状态  0:交易状态；1:清算状态
  Aut_Flg        CHAR(1)         DEFAULT ' ',
--修改系统定时自动清算标志  0:自动清算生效；1:禁止自动清算  
  Tlr_Id         CHAR(8)         DEFAULT ' ',
--修改柜员
  Log_Dat        CHAR(8)         DEFAULT ' ',
--修改日期时间
  Log_Tim        CHAR(6)         DEFAULT ' ',
--修改清算时间
PRIMARY KEY(SQN)
)IN "BBIP_APP"
INDEX IN  "BBIP_APP_INDEX";

-- ============================================================
--   电力电费业务
--   Table: efeaclrtbl   (广州电力清算流水表)
-- ============================================================
drop table GDEUPSB.GD_ELEC_CLR_JNL;
CREATE TABLE GDEUPSB.GD_ELEC_CLR_JNL
(
  SQN      CHAR(32)    NOT NULL,
  Br_No          CHAR(11)         DEFAULT ' ',
--分行号
  Nod_No         CHAR(11)         DEFAULT ' ',
--网点号 
  Tlr_Id         CHAR(8)         DEFAULT ' ',
--清算柜员
  C_Agt_No        CHAR(10)        DEFAULT ' ',
--清算单位协议号
  Clr_Dat        CHAR(8)         DEFAULT ' ',
--清算日期
  Clr_Tim        CHAR(6)         DEFAULT ' ',
--清算时间
  Clr_Rst        CHAR(1)         DEFAULT ' ',
--清算情况  0:未清算；1:已清算
  Clr_Typ        CHAR(1)         DEFAULT ' ',
--清算类型  0:自动清算；1:手工清算
  Clr_Tot        CHAR(8)         DEFAULT ' ',
--清算笔数
  Clr_Amt        CHAR(15)        DEFAULT ' ',
--清算金额
PRIMARY KEY(SQN)
)IN "BBIP_APP"
INDEX IN  "BBIP_APP_INDEX";


--燃气协议表，一个用户编号只有一条数据，用户编号是主键
--gascusall491
--惠州燃气协议表字段对照：用于查询燃气协议历史，需移植
--	ICS旧表字段					建表使用字段
--========================================
--  UserNo							CUS_NO
--  ActNo								CUS_AC
--  ActNam							CUS_NME
--  ActTyp							CUS_TYP
--  OptDat							OPT_DAT
--  OptNod							OPT_NOD
--  IdType							ID_TYP
--  IdNo								ID_NO
--  CntNam							THD_CUS_NME
--  TelNo								CMU_TEL
--  CntAdr							THD_CUS_ADR
drop table GDEUPSB.GDEUPS_GAS_CUS_ALL;
CREATE TABLE GDEUPSB.GDEUPS_GAS_CUS_ALL
(
  CUS_NO  char(12) not null,
--用户编号
  CUS_AC    char(32) not null,  
--账号
  CUS_NME   char(100) not null,
--付款人名称
  CUS_TYP    char(32) not null,  
--账号类型(0对公1对私活期一本通2对私普通折4对私卡)
  OPT_DAT    char(10),  
--新增或修改签约日期
  OPT_NOD    char(11),  
--操作机构
  ID_TYP   char(2),
--证件类型
  ID_NO     char(32),
--证件号码
  THD_CUS_NME     char(100),
--联系人名称
  CMU_TEL     char(50),
--联系人手机号码
  THD_CUS_ADR     char(100),
--联系人地址
 PRIMARY KEY(CUS_NO)
)
IN "BBIP_APP"
INDEX IN  "BBIP_APP_INDEX";

-- ============================================================
--   水费业务 银水批扣临时表
-- ============================================================
DROP TABLE GDEUPSB.GDEUPS_WAT_BAT_INF_TMP;
CREATE TABLE GDEUPSB.GDEUPS_WAT_BAT_INF_TMP
(  
	SQN			CHAR(20)		NOT NULL,--序列号
	BAT_NO		CHAR(21)		NOT NULL,--批次号
	BK_NO		CHAR(11)		DEFAULT ' ',--分行号
	COM_NO		CHAR(10)		DEFAULT ' ',--代理单位编号
	SEQ_NO		INTEGER,					--序号
	ACT_DAT		DATE,						--处理日期
	HNO			CHAR(9)			DEFAULT ' ',--户号
	SJ			CHAR(8)			DEFAULT ' ',--时间
	JE			CHAR(11)		DEFAULT '0',--金额，以分为单位
	BCOUNT		CHAR(40)		DEFAULT ' ',--银行账号
	STATUS		CHAR(1)			DEFAULT 'U',--处理状态
	ERR_MSG		VARCHAR(300)	DEFAULT ' ',--错误信息
	RMK1		VARCHAR(300)	DEFAULT ' ',--备用字段1
	RMK2		VARCHAR(300)	DEFAULT ' ',--备用字段2
	RMK3		VARCHAR(300)	DEFAULT ' ',--备用字段3
	RMK4		VARCHAR(300)	DEFAULT ' ',--备用字段4
	RMK5		VARCHAR(300)	DEFAULT ' ',--备用字段5
	RMK6		VARCHAR(300)	DEFAULT ' ',--备用字段6
	primary key(SQN)
)IN "BBIP_APP"
INDEX IN  "BBIP_APP_INDEX";

-- ============================================================
--   水费业务 协议信息表
-- ============================================================
DROP TABLE GDEUPSB.GDEUPS_WAT_AGT_INF;
CREATE TABLE GDEUPSB.GDEUPS_WAT_AGT_INF
(  
cus_Ac   char(40),   --客户账号
cus_Nme  char(100),  --客户姓名
pwd     char(20),   --密码
thd_Cus_No  char(40),   --第三方客户标识
thd_Cus_Nme  char(60),   --第三方客户姓名
id_Typ   char(4),  --办理人证件种类
id_No   char(32),  --办理人证件号码
bl_Nme   char(60),   --办理人姓名
addr  char(80),   --地址
hphone   char(15),  --家庭电话	
lphone   char(15),  --手机电话	
post    char(1),  --邮寄标识
sjman    char(60),  --收件人姓名
postno   char(6),   --邮政编码
taddr   char(80),   --邮寄地址
agd_Agr_No   char(23)  NOT NULL,   --协议编号
agt_Sts   char(1),  --协议状态
primary key(agd_Agr_No)
)IN "BBIP_APP"
INDEX IN  "BBIP_APP_INDEX";

drop table GDEUPSB.STU_FEE_INFO;
CREATE TABLE GDEUPSB.STU_FEE_INFO                     
(                                                      
   STU_COD      CHAR(20)    not null,                                       
                                                                     
   STU_NAM      CHAR(30)     not null,                               
                                                                     
   SCH_COD      CHAR(30)     not null  ,                             
                                                                     
   SCH_NAM       CHAR(30)       ,                                    
                                                                     
   PAY_YEA      CHAR(4)       ,                                      
                                                                     
   PAY_TEM      CHAR(10)       ,                                     
                                                                     
   XZF_AMT      DECIMAL(18,2)       ,                                 
                                                                     
   ROM_AMT      DECIMAL(18,2)       ,                                
                                                                     
   LRN_AMT     DECIMAL(18,2)           ,                             
                                                                     
   OTH_AMT     CHAR(30)         ,                                    
                                                                     
   TXN_AMT      DECIMAL(18,2)         ,                              
                                                                     
   STATUS     char(1)         ,                                      
                                                                     
   FLAG     char(1)         ,                                        
                                                                     
   primary key(STU_COD)                                   
                   
                           
)IN "BBIP_APP"
INDEX IN  "BBIP_APP_INDEX";                                                                                                                                                   
       comment on column GDEUPSB.STU_FEE_INFO.STU_COD is             
     '学籍编码';                                                          
       comment on column GDEUPSB.STU_FEE_INFO.STU_NAM is             
     '学生名称';                                                          
      comment on column GDEUPSB.STU_FEE_INFO.SCH_COD is             
     '学校编码(广东财经大学华商学院:001)';                                                      
       comment on column GDEUPSB.STU_FEE_INFO.SCH_NAM  is            
     '学校名称';                                                          
       comment on column GDEUPSB.STU_FEE_INFO.PAY_YEA is             
     '缴费年份';                                                          
       comment on column GDEUPSB.STU_FEE_INFO.PAY_TEM is             
     '缴费学期(一学年:01 第一学期:02 第二学期:03 第三学期:04)';                                                  
   comment on column GDEUPSB.STU_FEE_INFO.XZF_AMT is                 
     '普通(学)书杂费';                                                      
       comment on column GDEUPSB.STU_FEE_INFO.ROM_AMT is             
     '住宿费';                                                            
       comment on column GDEUPSB.STU_FEE_INFO.LRN_AMT is             
     '借读(书)学杂费';                                                    
       comment on column GDEUPSB.STU_FEE_INFO.OTH_AMT is             
     '其他费用';                                                          
           comment on column GDEUPSB.STU_FEE_INFO.TXN_AMT is         
     '总费用';                                                            
           comment on column GDEUPSB.STU_FEE_INFO.STATUS  is         
     '状态(0:正常;1:正在缴费)';                                           
            comment on column GDEUPSB.STU_FEE_INFO.FLAG    is        
     '收费标志(0:未收费;1:已收费)';                                            
         
                                                 
drop table GDEUPSB.GDEUPS_BATCH_CONSOLE_INFO;
CREATE
    TABLE GDEUPSB.GDEUPS_BATCH_CONSOLE_INFO
    (
        BAT_NO CHARACTER(21) NOT NULL,
        TXN_MDE CHARACTER(1),
        RAP_TYP CHARACTER(1),
        COM_NO CHARACTER(15),
        COM_NME VARCHAR(300),
        BUS_KND CHARACTER(4),
        TXN_ORG_CDE CHARACTER(11),
        TXN_TLR CHARACTER(7),
        SUB_DTE DATE,
        EXE_DTE DATE,
        BAT_STS CHARACTER(1),
        FLE_NME CHARACTER(100),
        TOT_CNT INTEGER,
        TOT_AMT DECIMAL(18,2),
        SUC_TOT_CNT INTEGER,
        SUC_TOT_AMT DECIMAL(18,2),
        FAL_TOT_CNT INTEGER,
        FAL_TOT_AMT DECIMAL(18,2),
        PAY_CNT INTEGER,
        RSV_FLD1 VARCHAR(300),
        RSV_FLD2 VARCHAR(300),
        RSV_FLD3 VARCHAR(300),
        RSV_FLD4 VARCHAR(300),
        RSV_FLD5 VARCHAR(300),
        RSV_FLD6 VARCHAR(300),
        RSV_FLD7 VARCHAR(300),
        RSV_FLD8 VARCHAR(300),
        RSV_FLD9 VARCHAR(300),
        EUPS_BUS_TYP VARCHAR(20),
        CONSTRAINT P_BAT_CON_INFO_IDX PRIMARY KEY (BAT_NO)
    )IN "BBIP_APP"
INDEX IN  "BBIP_APP_INDEX";
COMMENT ON TABLE GDEUPSB.GDEUPS_BATCH_CONSOLE_INFO
IS
    '批量代收付控制信息表';
COMMENT ON COLUMN GDEUPSB.GDEUPS_BATCH_CONSOLE_INFO.BAT_NO
IS
    '批次号';
COMMENT ON COLUMN GDEUPSB.GDEUPS_BATCH_CONSOLE_INFO.TXN_MDE
IS
    '0-文件方式,1-轮询方式';
COMMENT ON COLUMN GDEUPSB.GDEUPS_BATCH_CONSOLE_INFO.RAP_TYP
IS
    '0-代收,1-代付';
COMMENT ON COLUMN GDEUPSB.GDEUPS_BATCH_CONSOLE_INFO.COM_NO
IS
    '单位编号';
COMMENT ON COLUMN GDEUPSB.GDEUPS_BATCH_CONSOLE_INFO.COM_NME
IS
    '公司名称';
COMMENT ON COLUMN GDEUPSB.GDEUPS_BATCH_CONSOLE_INFO.BUS_KND
IS
    '业务种类';
COMMENT ON COLUMN GDEUPSB.GDEUPS_BATCH_CONSOLE_INFO.TXN_ORG_CDE
IS
    '交易机构号';
COMMENT ON COLUMN GDEUPSB.GDEUPS_BATCH_CONSOLE_INFO.TXN_TLR
IS
    '交易柜员';
COMMENT ON COLUMN GDEUPSB.GDEUPS_BATCH_CONSOLE_INFO.SUB_DTE
IS
    '提交日期';
COMMENT ON COLUMN GDEUPSB.GDEUPS_BATCH_CONSOLE_INFO.EXE_DTE
IS
    '处理日期';
COMMENT ON COLUMN GDEUPSB.GDEUPS_BATCH_CONSOLE_INFO.BAT_STS
IS
    '批次状态 初始状态为U 待提交状态为W 提交完成状态为S 取消状态为C';
COMMENT ON COLUMN GDEUPSB.GDEUPS_BATCH_CONSOLE_INFO.FLE_NME
IS
    '文件名';
COMMENT ON COLUMN GDEUPSB.GDEUPS_BATCH_CONSOLE_INFO.TOT_CNT
IS
    '交易总笔数';
COMMENT ON COLUMN GDEUPSB.GDEUPS_BATCH_CONSOLE_INFO.TOT_AMT
IS
    '交易总金额';
COMMENT ON COLUMN GDEUPSB.GDEUPS_BATCH_CONSOLE_INFO.SUC_TOT_CNT
IS
    '成功总笔数';
COMMENT ON COLUMN GDEUPSB.GDEUPS_BATCH_CONSOLE_INFO.SUC_TOT_AMT
IS
    '成功总金额';
COMMENT ON COLUMN GDEUPSB.GDEUPS_BATCH_CONSOLE_INFO.FAL_TOT_CNT
IS
    '失败总笔数';
COMMENT ON COLUMN GDEUPSB.GDEUPS_BATCH_CONSOLE_INFO.FAL_TOT_AMT
IS
    '失败总金额';
COMMENT ON COLUMN GDEUPSB.GDEUPS_BATCH_CONSOLE_INFO.PAY_CNT
IS
    '初始为0';
COMMENT ON COLUMN GDEUPSB.GDEUPS_BATCH_CONSOLE_INFO.RSV_FLD1
IS
    '预留字段1';
COMMENT ON COLUMN GDEUPSB.GDEUPS_BATCH_CONSOLE_INFO.RSV_FLD2
IS
    '预留字段2';
COMMENT ON COLUMN GDEUPSB.GDEUPS_BATCH_CONSOLE_INFO.RSV_FLD3
IS
    '预留字段3';
COMMENT ON COLUMN GDEUPSB.GDEUPS_BATCH_CONSOLE_INFO.RSV_FLD4
IS
    '预留字段4';
COMMENT ON COLUMN GDEUPSB.GDEUPS_BATCH_CONSOLE_INFO.RSV_FLD5
IS
    '预留字段5';
COMMENT ON COLUMN GDEUPSB.GDEUPS_BATCH_CONSOLE_INFO.RSV_FLD6
IS
    '预留字段6';
COMMENT ON COLUMN GDEUPSB.GDEUPS_BATCH_CONSOLE_INFO.RSV_FLD7
IS
    '预留字段7';
COMMENT ON COLUMN GDEUPSB.GDEUPS_BATCH_CONSOLE_INFO.RSV_FLD8
IS
    '预留字段8';
COMMENT ON COLUMN GDEUPSB.GDEUPS_BATCH_CONSOLE_INFO.RSV_FLD9
IS
    '预留字段9'                                               

drop table  gdeupsb.GDEUPS_Agt_Elec_TMP; --汕头电力协议输入信息表
create table gdeupsb.GDEUPS_Agt_Elec_TMP(

   BR_NO       CHAR(6)         DEFAULT ' ',
   AGT_NO      CHAR(13)        DEFAULT ' ',
   COM_NO      CHAR(10)        DEFAULT ' ',
   BANK_NO     CHAR(12)        DEFAULT ' ',
   COM_CODE		char(12),
   FEE_CODE		char(3),
   FEE_NUM		char(32),
   USER_NAME		char(96),
   OLD_BANK_NUM		char(12),
   OLD_CARD_NO		char(21),
   NEW_BANK_NUM		char(12),
   ACT_NO		char(21) not null,
   ACOUNT_NAME		char(96),
   ACT_TYPE		char(1),
   PER_COM_FLAG		char(1),
   ID_TYPE		char(2),
   ID_NO		char(32),
   CHECK_SEND_TYPE	char(1),
   INVOICE_SNED_TYPE	char(1),
   POINT_NUM		char(5),
   INVOICE_SNED_MAN	char(60),
   INVOICE_SEND_ZIP	char(6),
   INVOICE_SEND_ADDR	char(60),
   CHECK_SEND_MAN	char(40),
   CHECK_SEND_ZIP	char(6),
   CHECK_SEND_ADDR	char(60),
   NOTIFY_TYPE		char(1),
   EMAIL		char(64),
   PHONE_NUM		char(30),
   TEL_NUM		char(60),
   REMARK		char(64),
   PRCESS_PASSWORD	char(15)                                  
)IN "BBIP_APP"
INDEX IN  "BBIP_APP_INDEX"; 

  comment on table gdeupsb.GDEUPS_Agt_Elec_TMP   is   '汕头电力协议信息表'; 
  comment on column gdeupsb.GDEUPS_Agt_Elec_TMP.COM_CODE   is   '收付费企业代码'; 
  comment on column gdeupsb.GDEUPS_Agt_Elec_TMP.FEE_CODE   is   '费项代码';
  comment on column gdeupsb.GDEUPS_Agt_Elec_TMP.FEE_NUM   is   '缴费号';
  comment on column gdeupsb.GDEUPS_Agt_Elec_TMP.USER_NAME   is   '用户名称'; 
  comment on column gdeupsb.GDEUPS_Agt_Elec_TMP.OLD_BANK_NUM   is   '原开户行行号'; 
  comment on column gdeupsb.GDEUPS_Agt_Elec_TMP.OLD_CARD_NO   is   '原账户/卡号';
  comment on column gdeupsb.GDEUPS_Agt_Elec_TMP.NEW_BANK_NUM   is   '新开户行行号'; 
  comment on column gdeupsb.GDEUPS_Agt_Elec_TMP.ACT_NO   is   '账号';
  comment on column gdeupsb.GDEUPS_Agt_Elec_TMP.ACOUNT_NAME   is   '新客户名称'; 
  comment on column gdeupsb.GDEUPS_Agt_Elec_TMP.ACT_TYPE   is   '帐号类型';
  comment on column gdeupsb.GDEUPS_Agt_Elec_TMP.PER_COM_FLAG   is   '个人/集团标志'; 
  comment on column gdeupsb.GDEUPS_Agt_Elec_TMP.ID_TYPE   is   '证件类型';
  comment on column gdeupsb.GDEUPS_Agt_Elec_TMP.ID_NO   is   '证件号码'; 
  comment on column gdeupsb.GDEUPS_Agt_Elec_TMP.CHECK_SEND_TYPE   is   '账单邮寄类型';
  comment on column gdeupsb.GDEUPS_Agt_Elec_TMP.INVOICE_SNED_TYPE   is   '发票邮寄类型';
  comment on column gdeupsb.GDEUPS_Agt_Elec_TMP.POINT_NUM   is   '计量点号';
  comment on column gdeupsb.GDEUPS_Agt_Elec_TMP.INVOICE_SNED_MAN   is   '发票邮寄人'; 
  comment on column gdeupsb.GDEUPS_Agt_Elec_TMP.INVOICE_SEND_ZIP   is   '发票邮寄邮编';
  comment on column gdeupsb.GDEUPS_Agt_Elec_TMP.INVOICE_SEND_ADDR   is   '发票邮寄地址'; 
  comment on column gdeupsb.GDEUPS_Agt_Elec_TMP.CHECK_SEND_MAN   is   '帐单邮寄人';
  comment on column gdeupsb.GDEUPS_Agt_Elec_TMP.CHECK_SEND_ZIP   is   '帐单邮寄邮编'; 
  comment on column gdeupsb.GDEUPS_Agt_Elec_TMP.INVOICE_SEND_ADDR   is   '帐单邮寄地址';
  comment on column gdeupsb.GDEUPS_Agt_Elec_TMP.NOTIFY_TYPE   is   '余额不足通知方式'; 
  comment on column gdeupsb.GDEUPS_Agt_Elec_TMP.EMAIL   is   'E-MAIL';
  comment on column gdeupsb.GDEUPS_Agt_Elec_TMP.PHONE_NUM   is   '联系手机号'; 
  comment on column gdeupsb.GDEUPS_Agt_Elec_TMP.TEL_NUM   is   '联系电话';
  comment on column gdeupsb.GDEUPS_Agt_Elec_TMP.REMARK   is   '备注'; 
  comment on column gdeupsb.GDEUPS_Agt_Elec_TMP.PRCESS_PASSWORD   is   '交易密码st';

  
--汕头电力临时表
DROP table GDEUPSB.GDEUPS_ELEC_BATCH_TEMP;
CREATE
    TABLE GDEUPSB.GDEUPS_ELEC_BATCH_TEMP
    (
        SQN CHARACTER(50) NOT NULL ,
        BAT_NO CHARACTER(21) NOT NULL,
        COM_NO CHARACTER(15),
        TXN_TLR CHARACTER(7),
        SUB_DTE DATE,
        CUS_AC VARCHAR(300),
        CUS_NME VARCHAR(300),
        TXN_AMT VARCHAR(300),
        THD_CUS_NO VARCHAR(300),
        THD_CUS_NME VARCHAR(300),
        OBK_BR VARCHAR(300),
        OUROTHFLG VARCHAR(300),
        RSV_FLD1 VARCHAR(300),
        RSV_FLD2 VARCHAR(300),
        RSV_FLD3 VARCHAR(300),
        RSV_FLD4 VARCHAR(300),
        RSV_FLD5 VARCHAR(300),
        RSV_FLD6 VARCHAR(300),
        RSV_FLD7 VARCHAR(300),
        RSV_FLD8 VARCHAR(300),
        RSV_FLD9 VARCHAR(300),
        RSV_FLD10 VARCHAR(300),
        RSV_FLD11 VARCHAR(300),
        RSV_FLD12 VARCHAR(300),
        RSV_FLD13 VARCHAR(300),
        RSV_FLD14 VARCHAR(300),
        RSV_FLD15 VARCHAR(300),
        RSV_FLD16 VARCHAR(300),
        RSV_FLD17 VARCHAR(300),
       PRIMARY KEY (SQN)
    )IN "BBIP_APP"
INDEX IN  "BBIP_APP_INDEX";