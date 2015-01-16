--====================================================================
-- DBMS name:      IBM DB2 Version 10.x for Linux, UNIX, and Windows 
-- Created on:     2014/2/18 14:15:50
--====================================================================


drop table BBIP_EUPS.EUPS_ACTI_INFO;

drop table BBIP_EUPS.EUPS_ACTI_MER_INFO;

drop table BBIP_EUPS.EUPS_ACTI_ORDER;

drop table BBIP_EUPS.EUPS_AMOUNTINFO;

drop table BBIP_EUPS.EUPS_BRANCH_BUSKND;

drop table BBIP_EUPS.EUPS_BUSTYPE;

drop table BBIP_EUPS.EUPS_BUS_TICKET_ORDER;

drop table BBIP_EUPS.EUPS_CORP_AGENT;

drop table BBIP_EUPS.EUPS_CUS_AGENT;

drop table BBIP_EUPS.EUPS_FIELD_ORDER;

drop table BBIP_EUPS.EUPS_INVOICE_EXT;

drop table BBIP_EUPS.EUPS_INVOICE_INFO;

drop table BBIP_EUPS.EUPS_MSGSND_DETAIL;

drop table BBIP_EUPS.EUPS_MSG_STORE;

drop table BBIP_EUPS.EUPS_ORDER_BUSTYPE;

drop table BBIP_EUPS.EUPS_ORDER_INFO;

drop table BBIP_EUPS.EUPS_ORDER_MER;

drop table BBIP_EUPS.EUPS_ORDER_TERM_INFO;

drop table BBIP_EUPS.EUPS_ORDER_TYPE;

drop table BBIP_EUPS.EUPS_ORDER_USER_INFO;

drop table BBIP_EUPS.EUPS_PLANE_TICKET_ORDER;

drop table BBIP_EUPS.EUPS_SPORTS_LOTTERY_ORDER;

drop table BBIP_EUPS.EUPS_THD_AREA_INFO;

drop table BBIP_EUPS.EUPS_THD_BASE_INFO;

drop table BBIP_EUPS.EUPS_THD_FTP_CONFIG;

drop table BBIP_EUPS.EUPS_THD_TRANCTL_DETAIL;

drop table BBIP_EUPS.EUPS_THD_TRANCTL_INFO;

drop table BBIP_EUPS.EUPS_TRAIN_TICKET_ORDER;

drop table BBIP_EUPS.EUPS_TRANS_JOURNAL;

drop table BBIP_EUPS.EUPS_TRANS_JOURNAL_EXT;

drop table BBIP_EUPS.EUPS_TRANS_ROLLBACK_CTL;

drop table BBIP_EUPS.EUPS_TYPE_MAP;

drop table BBIP_EUPS.EUPS_WELFARE_LOTTERY_ORDER;

drop table BBIP_EUPS.EUPS_ZERO_ACT_INFO;

drop table BBIP_EUPS.EUPS_ZERO_JOURNAL;

--==============================================================
-- User: BBIP_EUPS
--==============================================================
--==============================================================
-- Table: EUPS_ACTI_INFO
--==============================================================
create table BBIP_EUPS.EUPS_ACTI_INFO
(
   SYS_NO               CHAR(20)               not null,
   EUPS_BUS_TYP         CHAR(10)               not null,
   ACT_NO               CHAR(20)               not null,
   ACT_NME              VARCHAR(300),
   ACT_IDU              VARCHAR(4000),
   ACT_SNM              VARCHAR(512),
   SDT                  DATE,
   EDT                  DATE,
   WEK_LST              CHAR(7),
   CRE_DTE              DATE                   not null,
   CRE_TLR              CHAR(7)                not null,
   MOD_DTE              DATE,
   MOD_TLR              CHAR(7),
   USE_STS              CHAR(1)               
      constraint C_USE_STS check (USE_STS is null or (USE_STS in ('0','2'))) default '0',
   constraint "P_T_ACT_INF_Idx1" primary key (SYS_NO)
);

comment on column BBIP_EUPS.EUPS_ACTI_INFO.SYS_NO is
'系统编号';

comment on column BBIP_EUPS.EUPS_ACTI_INFO.EUPS_BUS_TYP is
'EUPS业务类型';

comment on column BBIP_EUPS.EUPS_ACTI_INFO.ACT_NO is
'活动编号';

comment on column BBIP_EUPS.EUPS_ACTI_INFO.ACT_NME is
'活动名称';

comment on column BBIP_EUPS.EUPS_ACTI_INFO.ACT_IDU is
'活动介绍';

comment on column BBIP_EUPS.EUPS_ACTI_INFO.ACT_SNM is
'活动简称';

comment on column BBIP_EUPS.EUPS_ACTI_INFO.SDT is
'开始日期';

comment on column BBIP_EUPS.EUPS_ACTI_INFO.EDT is
'结束日期';

comment on column BBIP_EUPS.EUPS_ACTI_INFO.WEK_LST is
'星期列表';

comment on column BBIP_EUPS.EUPS_ACTI_INFO.CRE_DTE is
'创建日期';

comment on column BBIP_EUPS.EUPS_ACTI_INFO.CRE_TLR is
'创建柜员';

comment on column BBIP_EUPS.EUPS_ACTI_INFO.MOD_DTE is
'修改日期';

comment on column BBIP_EUPS.EUPS_ACTI_INFO.MOD_TLR is
'修改柜员';

comment on column BBIP_EUPS.EUPS_ACTI_INFO.USE_STS is
'使用状态，0：正常，2：停用';

--==============================================================
-- Table: EUPS_ACTI_MER_INFO
--==============================================================
create table BBIP_EUPS.EUPS_ACTI_MER_INFO
(
   SYS_NO               CHAR(20)               not null,
   EUPS_BUS_TYP         CHAR(10)               not null,
   ACT_NO               CHAR(20)               not null,
   SPL_NO               CHAR(16)               not null,
   SDT                  DATE,
   EDT                  DATE,
   WEK_LST              CHAR(7),
   PEL_LIM              INTEGER,
   CRE_DTE              DATE                   not null,
   CRE_TLR              CHAR(7)                not null,
   MOD_DTE              DATE,
   MOD_TLR              CHAR(7),
   USE_STS              CHAR(1)               
      constraint C_USE_STS check (USE_STS is null or (USE_STS in ('0','2'))) default '0',
   constraint "P_T_ACT_MER_INF_Id" primary key (SYS_NO)
);

comment on table BBIP_EUPS.EUPS_ACTI_MER_INFO is
'与活动信息关联的商户';

comment on column BBIP_EUPS.EUPS_ACTI_MER_INFO.SYS_NO is
'系统编号';

comment on column BBIP_EUPS.EUPS_ACTI_MER_INFO.EUPS_BUS_TYP is
'EUPS业务类型';

comment on column BBIP_EUPS.EUPS_ACTI_MER_INFO.ACT_NO is
'活动编号';

comment on column BBIP_EUPS.EUPS_ACTI_MER_INFO.SPL_NO is
'商户编号';

comment on column BBIP_EUPS.EUPS_ACTI_MER_INFO.SDT is
'开始日期';

comment on column BBIP_EUPS.EUPS_ACTI_MER_INFO.EDT is
'结束日期';

comment on column BBIP_EUPS.EUPS_ACTI_MER_INFO.WEK_LST is
'星期列表';

comment on column BBIP_EUPS.EUPS_ACTI_MER_INFO.PEL_LIM is
'人员上限';

comment on column BBIP_EUPS.EUPS_ACTI_MER_INFO.CRE_DTE is
'创建日期';

comment on column BBIP_EUPS.EUPS_ACTI_MER_INFO.CRE_TLR is
'创建柜员';

comment on column BBIP_EUPS.EUPS_ACTI_MER_INFO.MOD_DTE is
'修改日期';

comment on column BBIP_EUPS.EUPS_ACTI_MER_INFO.MOD_TLR is
'修改柜员';

comment on column BBIP_EUPS.EUPS_ACTI_MER_INFO.USE_STS is
'使用状态，0：正常，2：停用';

--==============================================================
-- Table: EUPS_ACTI_ORDER
--==============================================================
create table BBIP_EUPS.EUPS_ACTI_ORDER
(
   ODE_NO               CHAR(40)               not null,
   ACT_NO               CHAR(20)               not null,
   ACT_DTE              DATE                   not null,
   SPL_NO               CHAR(16)               not null,
   SPL_NME              VARCHAR(300),
   REG_AC               CHAR(24),
   CSM_TME              TIMESTAMP,
   ICM_PRT_FLG          CHAR(1),
   FUL_PRT_FLG          CHAR(1),
   constraint "P_T_Act_OD_Idx1" primary key (ODE_NO)
);

comment on table BBIP_EUPS.EUPS_ACTI_ORDER is
'存放折扣活动订单详情，关联订单基本信息';

comment on column BBIP_EUPS.EUPS_ACTI_ORDER.ODE_NO is
'订单号';

comment on column BBIP_EUPS.EUPS_ACTI_ORDER.ACT_NO is
'活动编号';

comment on column BBIP_EUPS.EUPS_ACTI_ORDER.ACT_DTE is
'活动日期';

comment on column BBIP_EUPS.EUPS_ACTI_ORDER.SPL_NO is
'商户编号';

comment on column BBIP_EUPS.EUPS_ACTI_ORDER.SPL_NME is
'商户名称';

comment on column BBIP_EUPS.EUPS_ACTI_ORDER.REG_AC is
'登记账号';

comment on column BBIP_EUPS.EUPS_ACTI_ORDER.CSM_TME is
'消费时间';

comment on column BBIP_EUPS.EUPS_ACTI_ORDER.ICM_PRT_FLG is
'增量打印标志';

comment on column BBIP_EUPS.EUPS_ACTI_ORDER.FUL_PRT_FLG is
'全量打印标志';

--==============================================================
-- Table: EUPS_AMOUNTINFO
--==============================================================
create table BBIP_EUPS.EUPS_AMOUNTINFO
(
   SQN                  CHAR(20)               not null,
   BK                   CHAR(12)               not null,
   BR                   CHAR(11)               not null,
   TXN_TLR              CHAR(7)                not null,
   TXN_CHL              CHAR(1)                not null,
   TXN_DTE              DATE                   not null,
   TXN_TME              TIMESTAMP              not null,
   TXN_CDE              CHAR(6),
   RAP_TYP              CHAR(1)                not null,
   BUS_KND              CHAR(3)                not null,
   EUPS_BUS_TYP         CHAR(10)               not null,
   COM_NO               CHAR(15)               not null,
   THD_RGN_NO           CHAR(20),
   BAR_CDE              CHAR(40),
   THD_CUS_NO           CHAR(30),
   THD_CUS_NME          VARCHAR(300),
   THD_CUS_ADR          VARCHAR(762),
   CONT_NO              CHAR(30),
   CUS_AC               CHAR(24),
   OWE_PRD              CHAR(20),
   OWE_FEE_AMT          DECIMAL(18,2)          not null,
   PBD                  DECIMAL(18,2),
   CUR_BAL              DECIMAL(18,2),
   BAK_FLD1             CHAR(30),
   BAK_FLD2             CHAR(30),
   RSV_FLD1             VARCHAR(300),
   RSV_FLD2             VARCHAR(300),
   RSV_FLD3             VARCHAR(1024),
   RSV_FLD4             VARCHAR(1024),
   RSV_FLD5             VARCHAR(1024),
   RSV_FLD6             VARCHAR(1024),
   constraint "P_AMTINFO_Idx1" primary key (SQN)
);

comment on table BBIP_EUPS.EUPS_AMOUNTINFO is
'欠费信息临时表，用于存储从第三方查询到的费用信息。每日清理。';

comment on column BBIP_EUPS.EUPS_AMOUNTINFO.SQN is
'流水号';

comment on column BBIP_EUPS.EUPS_AMOUNTINFO.BK is
'分行号';

comment on column BBIP_EUPS.EUPS_AMOUNTINFO.BR is
'网点号';

comment on column BBIP_EUPS.EUPS_AMOUNTINFO.TXN_TLR is
'交易柜员';

comment on column BBIP_EUPS.EUPS_AMOUNTINFO.TXN_CHL is
'交易渠道(0：柜面;1：电话银行;2：网银;3：ATM/CDM/CRS;4：POS;5：多媒体;6：手机;W：第三方系统)';

comment on column BBIP_EUPS.EUPS_AMOUNTINFO.TXN_DTE is
'交易日期';

comment on column BBIP_EUPS.EUPS_AMOUNTINFO.TXN_TME is
'交易时间';

comment on column BBIP_EUPS.EUPS_AMOUNTINFO.TXN_CDE is
'交易码';

comment on column BBIP_EUPS.EUPS_AMOUNTINFO.RAP_TYP is
'收付类型 0-代收；1-代付';

comment on column BBIP_EUPS.EUPS_AMOUNTINFO.BUS_KND is
'业务种类';

comment on column BBIP_EUPS.EUPS_AMOUNTINFO.EUPS_BUS_TYP is
'EUPS业务类型';

comment on column BBIP_EUPS.EUPS_AMOUNTINFO.COM_NO is
'单位编号';

comment on column BBIP_EUPS.EUPS_AMOUNTINFO.THD_RGN_NO is
'第三方地区编号';

comment on column BBIP_EUPS.EUPS_AMOUNTINFO.BAR_CDE is
'条形码';

comment on column BBIP_EUPS.EUPS_AMOUNTINFO.THD_CUS_NO is
'第三方客户标识';

comment on column BBIP_EUPS.EUPS_AMOUNTINFO.THD_CUS_NME is
'第三方客户姓名';

comment on column BBIP_EUPS.EUPS_AMOUNTINFO.THD_CUS_ADR is
'第三方客户地址';

comment on column BBIP_EUPS.EUPS_AMOUNTINFO.CONT_NO is
'合同号码';

comment on column BBIP_EUPS.EUPS_AMOUNTINFO.CUS_AC is
'客户账号';

comment on column BBIP_EUPS.EUPS_AMOUNTINFO.OWE_PRD is
'费用账期';

comment on column BBIP_EUPS.EUPS_AMOUNTINFO.OWE_FEE_AMT is
'欠费金额';

comment on column BBIP_EUPS.EUPS_AMOUNTINFO.PBD is
'违约金';

comment on column BBIP_EUPS.EUPS_AMOUNTINFO.CUR_BAL is
'当前余额';

comment on column BBIP_EUPS.EUPS_AMOUNTINFO.BAK_FLD1 is
'备用字段1';

comment on column BBIP_EUPS.EUPS_AMOUNTINFO.BAK_FLD2 is
'备用字段2';

comment on column BBIP_EUPS.EUPS_AMOUNTINFO.RSV_FLD1 is
'预留字段1';

comment on column BBIP_EUPS.EUPS_AMOUNTINFO.RSV_FLD2 is
'预留字段2';

comment on column BBIP_EUPS.EUPS_AMOUNTINFO.RSV_FLD3 is
'预留字段3';

comment on column BBIP_EUPS.EUPS_AMOUNTINFO.RSV_FLD4 is
'预留字段4';

comment on column BBIP_EUPS.EUPS_AMOUNTINFO.RSV_FLD5 is
'预留字段5';

comment on column BBIP_EUPS.EUPS_AMOUNTINFO.RSV_FLD6 is
'预留字段6';

--==============================================================
-- Table: EUPS_BRANCH_BUSKND
--==============================================================
create table BBIP_EUPS.EUPS_BRANCH_BUSKND
(
   SYS_NO               CHAR(20)               not null,
   BK                   CHAR(12)               not null,
   BUS_KND              CHAR(3)                not null,
   USE_STS              CHAR(1)                not null
      constraint C_USE_STS check (USE_STS in ('0','2')) default '0',
   constraint "P_BR_BUSKND_Idx1" primary key (SYS_NO),
   constraint "A_BR_BUSKND_Idx2" unique (BK, BUS_KND)
);

comment on table BBIP_EUPS.EUPS_BRANCH_BUSKND is
'用于存储分行开放的业务种类。';

comment on column BBIP_EUPS.EUPS_BRANCH_BUSKND.SYS_NO is
'系统编号';

comment on column BBIP_EUPS.EUPS_BRANCH_BUSKND.BK is
'分行号';

comment on column BBIP_EUPS.EUPS_BRANCH_BUSKND.BUS_KND is
'业务种类';

comment on column BBIP_EUPS.EUPS_BRANCH_BUSKND.USE_STS is
'使用状态，0：正常，2：停用';

--==============================================================
-- Table: EUPS_BUSTYPE
--==============================================================
create table BBIP_EUPS.EUPS_BUSTYPE
(
   EUPS_BUS_TYP         CHAR(10)               not null,
   COM_NO               CHAR(15)               not null,
   BUS_KND              CHAR(3)                not null,
   EUPS_BUS_TYP_NAME    VARCHAR(300)           not null,
   USE_STS              CHAR(1)               
      constraint C_USE_STS check (USE_STS is null or (USE_STS in ('0','2'))) default '0',
   constraint "P_EUPS_BUSTYPE_Idx" primary key (EUPS_BUS_TYP)
);

comment on table BBIP_EUPS.EUPS_BUSTYPE is
'管理公共事业缴费具体的缴费类型。与业务种类是多对一的关系。';

comment on column BBIP_EUPS.EUPS_BUSTYPE.EUPS_BUS_TYP is
'EUPS业务类型';

comment on column BBIP_EUPS.EUPS_BUSTYPE.COM_NO is
'单位编号';

comment on column BBIP_EUPS.EUPS_BUSTYPE.BUS_KND is
'业务种类';

comment on column BBIP_EUPS.EUPS_BUSTYPE.EUPS_BUS_TYP_NAME is
'EUPS业务类型名称';

comment on column BBIP_EUPS.EUPS_BUSTYPE.USE_STS is
'使用状态，0：正常，2：停用';

--==============================================================
-- Table: EUPS_BUS_TICKET_ORDER
--==============================================================
create table BBIP_EUPS.EUPS_BUS_TICKET_ORDER
(
   ODE_NO               CHAR(40)               not null,
   LEV_CIY              CHAR(20)               not null,
   REA_CIY              CHAR(20)               not null,
   REF_NOM_AMT          DECIMAL(18,2)          not null,
   LEV_TME              TIMESTAMP              not null,
   REA_TME              TIMESTAMP,
   RUN_TME              INTEGER,
   CAR_TYP              CHAR(20)               not null,
   CAR_TIC_TYP          CHAR(20),
   CAR_TIC_NO           CHAR(20)               not null,
   constraint "P_T_BS_TIC_OD_Idx1" primary key (ODE_NO)
);

comment on table BBIP_EUPS.EUPS_BUS_TICKET_ORDER is
'存放汽车票订单详情，关联订单基本信息';

comment on column BBIP_EUPS.EUPS_BUS_TICKET_ORDER.ODE_NO is
'订单号';

comment on column BBIP_EUPS.EUPS_BUS_TICKET_ORDER.LEV_CIY is
'出发城市';

comment on column BBIP_EUPS.EUPS_BUS_TICKET_ORDER.REA_CIY is
'达到城市';

comment on column BBIP_EUPS.EUPS_BUS_TICKET_ORDER.REF_NOM_AMT is
'参考票价';

comment on column BBIP_EUPS.EUPS_BUS_TICKET_ORDER.LEV_TME is
'发车时间';

comment on column BBIP_EUPS.EUPS_BUS_TICKET_ORDER.REA_TME is
'到站时间';

comment on column BBIP_EUPS.EUPS_BUS_TICKET_ORDER.RUN_TME is
'运行时间';

comment on column BBIP_EUPS.EUPS_BUS_TICKET_ORDER.CAR_TYP is
'汽车类型';

comment on column BBIP_EUPS.EUPS_BUS_TICKET_ORDER.CAR_TIC_TYP is
'汽车票座位类型';

comment on column BBIP_EUPS.EUPS_BUS_TICKET_ORDER.CAR_TIC_NO is
'汽车票座位号码';

--==============================================================
-- Table: EUPS_CORP_AGENT
--==============================================================
create table BBIP_EUPS.EUPS_CORP_AGENT
(
   COM_NO               CHAR(15)               not null,
   BUS_KND              CHAR(3)                not null,
   EUPS_BUS_TYP         CHAR(10)               not null,
   AGR_BOK_NO           CHAR(20),
   CUS_MGR              CHAR(15),
   LIS_ACC              CHAR(24),
   BIL_VOU_FLG          CHAR(1),
   USE_STS              CHAR(1)               
      constraint C_USE_STS check (USE_STS is null or (USE_STS in ('0','2'))) default '0',
   constraint P_EUPS_CORP_AGENT_ primary key (COM_NO)
);

comment on table BBIP_EUPS.EUPS_CORP_AGENT is
'
';

comment on column BBIP_EUPS.EUPS_CORP_AGENT.COM_NO is
'单位编号';

comment on column BBIP_EUPS.EUPS_CORP_AGENT.BUS_KND is
'业务种类';

comment on column BBIP_EUPS.EUPS_CORP_AGENT.EUPS_BUS_TYP is
'EUPS业务类型';

comment on column BBIP_EUPS.EUPS_CORP_AGENT.AGR_BOK_NO is
'协议书号';

comment on column BBIP_EUPS.EUPS_CORP_AGENT.CUS_MGR is
'责任客户经理';

comment on column BBIP_EUPS.EUPS_CORP_AGENT.LIS_ACC is
'清算账号 代理单位账号/内部账号';

comment on column BBIP_EUPS.EUPS_CORP_AGENT.BIL_VOU_FLG is
'票据重空标志  Y-是；N-否;默认N
密钥是否同步到安全系统 Y-是；N-否；默认N
';

comment on column BBIP_EUPS.EUPS_CORP_AGENT.USE_STS is
'使用状态，0：正常，2：停用';

--==============================================================
-- Table: EUPS_CUS_AGENT
--==============================================================
create table BBIP_EUPS.EUPS_CUS_AGENT
(
   RLH_AGR_NO           CHAR(23)               not null,
   CUS_AC               CHAR(24)               not null,
   RLH_AGR_TPY          CHAR(3),
   AGD_AGR_NO           CHAR(20)               not null,
   CRE_DTE              DATE,
   AGR_STS              CHAR(1),
   USE_STS              CHAR(1)               
      constraint C_USE_STS check (USE_STS is null or (USE_STS in ('0','2'))) default '0',
   CON_COL_DAY          CHAR(3),
   AGD_DTE              DATE,
   AGD_AMT              DECIMAL(18,2),
   CHL_CTL_WRD          CHAR(20),
   BAK_FLD1             CHAR(30),
   BAK_FLD2             CHAR(30),
   BAK_FLD3             CHAR(30),
   BAK_FLD4             CHAR(30),
   RSV_FLD1             VARCHAR(300),
   RSV_FLD2             VARCHAR(300),
   RSV_FLD3             VARCHAR(1024),
   RSV_FLD4             VARCHAR(1024),
   RSV_FLD5             VARCHAR(1024),
   RSV_FLD6             VARCHAR(1024),
   constraint P_CUS_AGENT_IDX1 primary key (RLH_AGR_NO)
);

comment on column BBIP_EUPS.EUPS_CUS_AGENT.RLH_AGR_NO is
'补充协议编号';

comment on column BBIP_EUPS.EUPS_CUS_AGENT.CUS_AC is
'客户账号';

comment on column BBIP_EUPS.EUPS_CUS_AGENT.RLH_AGR_TPY is
'补充协议种类';

comment on column BBIP_EUPS.EUPS_CUS_AGENT.AGD_AGR_NO is
'代扣协议编号';

comment on column BBIP_EUPS.EUPS_CUS_AGENT.CRE_DTE is
'创建日期';

comment on column BBIP_EUPS.EUPS_CUS_AGENT.AGR_STS is
'协议状态:0-启用；1-停用';

comment on column BBIP_EUPS.EUPS_CUS_AGENT.USE_STS is
'使用状态，0：正常，2：停用';

comment on column BBIP_EUPS.EUPS_CUS_AGENT.CON_COL_DAY is
'连扣天数';

comment on column BBIP_EUPS.EUPS_CUS_AGENT.AGD_DTE is
'代扣日期';

comment on column BBIP_EUPS.EUPS_CUS_AGENT.AGD_AMT is
'代扣金额';

comment on column BBIP_EUPS.EUPS_CUS_AGENT.CHL_CTL_WRD is
'渠道控制字';

comment on column BBIP_EUPS.EUPS_CUS_AGENT.BAK_FLD1 is
'备用字段1';

comment on column BBIP_EUPS.EUPS_CUS_AGENT.BAK_FLD2 is
'备用字段2';

comment on column BBIP_EUPS.EUPS_CUS_AGENT.BAK_FLD3 is
'备用字段3';

comment on column BBIP_EUPS.EUPS_CUS_AGENT.BAK_FLD4 is
'备用字段4';

comment on column BBIP_EUPS.EUPS_CUS_AGENT.RSV_FLD1 is
'预留字段1';

comment on column BBIP_EUPS.EUPS_CUS_AGENT.RSV_FLD2 is
'预留字段2';

comment on column BBIP_EUPS.EUPS_CUS_AGENT.RSV_FLD3 is
'预留字段3';

comment on column BBIP_EUPS.EUPS_CUS_AGENT.RSV_FLD4 is
'预留字段4';

comment on column BBIP_EUPS.EUPS_CUS_AGENT.RSV_FLD5 is
'预留字段5';

comment on column BBIP_EUPS.EUPS_CUS_AGENT.RSV_FLD6 is
'预留字段6';

--==============================================================
-- Table: EUPS_FIELD_ORDER
--==============================================================
create table BBIP_EUPS.EUPS_FIELD_ORDER
(
   ODE_NO               CHAR(40)               not null,
   VNE_TYP_NME          CHAR(60),
   VNE_CIY_NME          CHAR(60),
   VNE_NO               CHAR(20),
   VNE_NME              CHAR(60),
   VNE_ADR              CHAR(120),
   EST_ARY_TME          DATE,
   EST_ARY_NUM          INTEGER,
   REG_CUS_INF          LONG VARCHAR,
   FUL_PRT_FLG          CHAR(1),
   constraint "P_T_FLD_OD_Idx1" primary key (ODE_NO)
);

comment on table BBIP_EUPS.EUPS_FIELD_ORDER is
'存放球场预定订单详情，关联订单基本信息';

comment on column BBIP_EUPS.EUPS_FIELD_ORDER.ODE_NO is
'订单号';

comment on column BBIP_EUPS.EUPS_FIELD_ORDER.VNE_TYP_NME is
'场地类型名称';

comment on column BBIP_EUPS.EUPS_FIELD_ORDER.VNE_CIY_NME is
'场地城市名称';

comment on column BBIP_EUPS.EUPS_FIELD_ORDER.VNE_NO is
'场地编号';

comment on column BBIP_EUPS.EUPS_FIELD_ORDER.VNE_NME is
'场地名称';

comment on column BBIP_EUPS.EUPS_FIELD_ORDER.VNE_ADR is
'场地地址';

comment on column BBIP_EUPS.EUPS_FIELD_ORDER.EST_ARY_TME is
'预计到场时间';

comment on column BBIP_EUPS.EUPS_FIELD_ORDER.EST_ARY_NUM is
'预计到场人数';

comment on column BBIP_EUPS.EUPS_FIELD_ORDER.REG_CUS_INF is
'登记客户信息';

comment on column BBIP_EUPS.EUPS_FIELD_ORDER.FUL_PRT_FLG is
'全量打印标志';

--==============================================================
-- Table: EUPS_INVOICE_EXT
--==============================================================
create table BBIP_EUPS.EUPS_INVOICE_EXT
(
   SYS_NO               CHAR(20)               not null,
   SQN                  CHAR(20)               not null,
   THD_RGN_NO           CHAR(20),
   THD_CUS_NO           CHAR(30),
   TXN_ORG_CDE          CHAR(11),
   TXN_TLR              CHAR(7),
   TXN_CHL              CHAR(1),
   BUS_KND              CHAR(3),
   EUPS_BUS_TYP         CHAR(10),
   TXN_DTE              DATE,
   TXN_TME              TIMESTAMP,
   COM_NO               CHAR(15),
   THD_CUS_AC_NO        CHAR(20),
   IVO_CRW_NO           CHAR(20),
   IVO_NO               CHAR(20),
   IVO_PRD              CHAR(20),
   BAK_FLD1             CHAR(30),
   BAK_FLD2             CHAR(30),
   RSV_FLD1             VARCHAR(300),
   RSV_FLD2             VARCHAR(300),
   RSV_FLD3             VARCHAR(1024),
   RSV_FLD4             VARCHAR(1024),
   RSV_FLD5             VARCHAR(1024),
   RSV_FLD6             VARCHAR(1024),
   constraint "P_T_INV_EXT_Idx1" primary key (SYS_NO)
--  constraint A_IDX_1 unique (SQN, THD_RGN_NO, THD_CUS_NO),
--   constraint A_IDX_2 unique (SQN, IVO_CRW_NO, IVO_NO),
--   constraint A_IDX_3 unique (BUS_KND, EUPS_BUS_TYP, THD_RGN_NO, THD_CUS_NO, IVO_PRD),
--   constraint A_IDX_4 unique (BUS_KND, EUPS_BUS_TYP, IVO_CRW_NO, IVO_NO, IVO_PRD)
);

comment on column BBIP_EUPS.EUPS_INVOICE_EXT.SYS_NO is
'系统编号';

comment on column BBIP_EUPS.EUPS_INVOICE_EXT.SQN is
'流水号';

comment on column BBIP_EUPS.EUPS_INVOICE_EXT.THD_RGN_NO is
'第三方地区编号';

comment on column BBIP_EUPS.EUPS_INVOICE_EXT.THD_CUS_NO is
'第三方客户标识';

comment on column BBIP_EUPS.EUPS_INVOICE_EXT.TXN_ORG_CDE is
'交易机构号';

comment on column BBIP_EUPS.EUPS_INVOICE_EXT.TXN_TLR is
'交易柜员';

comment on column BBIP_EUPS.EUPS_INVOICE_EXT.TXN_CHL is
'交易渠道(0：柜面;1：电话银行;2：网银;3：ATM/CDM/CRS;4：POS;5：多媒体;6：手机;W：第三方系统)';

comment on column BBIP_EUPS.EUPS_INVOICE_EXT.BUS_KND is
'业务种类';

comment on column BBIP_EUPS.EUPS_INVOICE_EXT.EUPS_BUS_TYP is
'EUPS业务类型';

comment on column BBIP_EUPS.EUPS_INVOICE_EXT.TXN_DTE is
'交易日期';

comment on column BBIP_EUPS.EUPS_INVOICE_EXT.TXN_TME is
'交易时间';

comment on column BBIP_EUPS.EUPS_INVOICE_EXT.COM_NO is
'单位编号';

comment on column BBIP_EUPS.EUPS_INVOICE_EXT.THD_CUS_AC_NO is
'第三方客户账户编码';

comment on column BBIP_EUPS.EUPS_INVOICE_EXT.IVO_CRW_NO is
'发票冠字号';

comment on column BBIP_EUPS.EUPS_INVOICE_EXT.IVO_NO is
'发票号码';

comment on column BBIP_EUPS.EUPS_INVOICE_EXT.IVO_PRD is
'发票账期';

comment on column BBIP_EUPS.EUPS_INVOICE_EXT.BAK_FLD1 is
'备用字段1';

comment on column BBIP_EUPS.EUPS_INVOICE_EXT.BAK_FLD2 is
'备用字段2';

comment on column BBIP_EUPS.EUPS_INVOICE_EXT.RSV_FLD1 is
'预留字段1';

comment on column BBIP_EUPS.EUPS_INVOICE_EXT.RSV_FLD2 is
'预留字段2';

comment on column BBIP_EUPS.EUPS_INVOICE_EXT.RSV_FLD3 is
'预留字段3';

comment on column BBIP_EUPS.EUPS_INVOICE_EXT.RSV_FLD4 is
'预留字段4';

comment on column BBIP_EUPS.EUPS_INVOICE_EXT.RSV_FLD5 is
'预留字段5';

comment on column BBIP_EUPS.EUPS_INVOICE_EXT.RSV_FLD6 is
'预留字段6';

--==============================================================
-- Table: EUPS_INVOICE_INFO
--==============================================================
create table BBIP_EUPS.EUPS_INVOICE_INFO
(
   SYS_NO               CHAR(20)               not null,
   SQN                  CHAR(20)               not null,
   IVO_PRD              CHAR(20),
   BK                   CHAR(12),
   TXN_ORG_CDE          CHAR(11),
   BR                   CHAR(11),
   TML_NO               CHAR(8),
   TXN_TLR              CHAR(7),
   CHL_TYP              CHAR(2),
   BUS_KND              CHAR(3),
   EUPS_BUS_TYP         CHAR(10),
   AC_DTE               DATE,
   MFM_VCH_NO           CHAR(20),
   TXN_CDE              CHAR(6),
   TXN_DTE              DATE,
   TXN_TME              TIMESTAMP,
   THD_TXN_CDE          CHAR(10),
   THD_TXN_DTE          DATE,
   THD_SQN              CHAR(20),
   OLD_PFE_MFM_AC_DTE   DATE,
   OLD_PFE_MFM_VCH_NO   CHAR(20),
   OLD_PFE_PLF_SQN      CHAR(20),
   COM_NO               CHAR(15),
   THD_RGN_NO           CHAR(20),
   THD_CUS_NO           CHAR(30),
   THD_CUS_NME          VARCHAR(300),
   THD_CUS_ADR          VARCHAR(762),
   THD_CUS_AC_NO        CHAR(20),
   PAY_TYP              CHAR(1),
   PAY_MDE              CHAR(1),
   THD_BIL_TYP          CHAR(10),
   IVO_CRW_NO           CHAR(20),
   IVO_NO               CHAR(20),
   IVO_BEG_PRD          CHAR(20),
   IVO_END_PRD          CHAR(20),
   IVO_AMT              DECIMAL(18,2)          not null,
   IVO_PRT_FLG          CHAR(1)                default '0',
   SUP_PRT_FLG          CHAR(1)               
      constraint C_SUP_PRT_FLG check (SUP_PRT_FLG is null or (SUP_PRT_FLG in ('0','1'))) default '0',
   PRT_CNT              INTEGER,
   IVO_STS              CHAR(1)               
      constraint C_IVO_STS check (IVO_STS is null or (IVO_STS in ('0','1','2'))) default '0',
   CMU_TEL              VARCHAR(90),
   BAK_FLD1             CHAR(30),
   BAK_FLD2             CHAR(30),
   RSV_FLD1             VARCHAR(300),
   RSV_FLD2             VARCHAR(300),
   RSV_FLD3             VARCHAR(1024),
   RSV_FLD4             VARCHAR(1024),
   FIL_FLG              CHAR(1)               
      constraint C_FIL_FLG check (FIL_FLG is null or (FIL_FLG in ('0','1'))) default '0',
   ATH_TLR              CHAR(7),
   constraint "P_T_INV_Idx1" primary key (SYS_NO)
--   constraint A_IDX_1 unique (SQN, IVO_PRD),
--   constraint A_IDX_2 unique (SQN, IVO_CRW_NO, IVO_NO),
--   constraint A_IDX_3 unique (BUS_KND, EUPS_BUS_TYP, THD_RGN_NO, THD_CUS_NO, IVO_PRD, IVO_STS),
--   constraint A_IDX_4 unique (BUS_KND, EUPS_BUS_TYP, IVO_CRW_NO, IVO_NO, IVO_STS)
);

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.SYS_NO is
'系统编号';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.SQN is
'流水号';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.IVO_PRD is
'发票账期';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.BK is
'分行号';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.TXN_ORG_CDE is
'交易机构号';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.BR is
'网点号';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.TML_NO is
'终端号';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.TXN_TLR is
'交易柜员';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.CHL_TYP is
'渠道类型：(0：柜面;1：电话银行;2：网银;3：ATM/CDM/CRS;4：POS;5：多媒体;6：手机;W：第三方系统)';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.BUS_KND is
'业务种类';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.EUPS_BUS_TYP is
'EUPS业务类型';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.AC_DTE is
'会计日期';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.MFM_VCH_NO is
'会计流水号';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.TXN_CDE is
'交易码';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.TXN_DTE is
'交易日期';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.TXN_TME is
'交易时间';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.THD_TXN_CDE is
'第三方交易码';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.THD_TXN_DTE is
'第三方交易日期';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.THD_SQN is
'第三方流水号';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.OLD_PFE_MFM_AC_DTE is
'原缴费主机会计日期';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.OLD_PFE_MFM_VCH_NO is
'原缴费会计流水号';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.OLD_PFE_PLF_SQN is
'原缴费EUPS流水号';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.COM_NO is
'单位编号';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.THD_RGN_NO is
'第三方地区编号';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.THD_CUS_NO is
'第三方客户标识';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.THD_CUS_NME is
'第三方客户姓名';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.THD_CUS_ADR is
'第三方客户地址';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.THD_CUS_AC_NO is
'第三方客户账户编码';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.PAY_TYP is
'付款类型';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.PAY_MDE is
'支付方式';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.THD_BIL_TYP is
'第三方发票类型';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.IVO_CRW_NO is
'发票冠字号';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.IVO_NO is
'发票号码';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.IVO_BEG_PRD is
'发票开始账期';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.IVO_END_PRD is
'发票结束账期';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.IVO_AMT is
'发票金额';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.IVO_PRT_FLG is
'发票打印标志';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.SUP_PRT_FLG is
'是否补打';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.PRT_CNT is
'打印次数';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.IVO_STS is
'发票状态';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.CMU_TEL is
'联系电话';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.BAK_FLD1 is
'备用字段1';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.BAK_FLD2 is
'备用字段2';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.RSV_FLD1 is
'预留字段1';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.RSV_FLD2 is
'预留字段2';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.RSV_FLD3 is
'预留字段3';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.RSV_FLD4 is
'预留字段4';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.FIL_FLG is
'扩展标志';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.ATH_TLR is
'授权柜员';

--==============================================================
-- Table: EUPS_MSGSND_DETAIL
--==============================================================
create table BBIP_EUPS.EUPS_MSGSND_DETAIL
(
   SYS_NO               CHAR(20)               not null,
   SEQ                  CHAR(20)               not null,
   COM_NO               CHAR(15)               not null,
   BUS_KND              CHAR(3)                not null,
   EUPS_BUS_TYP         CHAR(10)               not null,
   MOB_NUM              VARCHAR(90)            not null,
   SMS_CTN              VARCHAR(2048),
   SND_DTE              DATE                   not null,
   SND_TME              TIMESTAMP              not null,
   CRE_DTE              DATE                   not null,
   constraint "P_T_MS_DT_Idx1" primary key (SYS_NO)
);

comment on table BBIP_EUPS.EUPS_MSGSND_DETAIL is
'用于记录已发短信';

comment on column BBIP_EUPS.EUPS_MSGSND_DETAIL.SYS_NO is
'系统编号';

comment on column BBIP_EUPS.EUPS_MSGSND_DETAIL.SEQ is
'序号';

comment on column BBIP_EUPS.EUPS_MSGSND_DETAIL.COM_NO is
'单位编号';

comment on column BBIP_EUPS.EUPS_MSGSND_DETAIL.BUS_KND is
'业务种类';

comment on column BBIP_EUPS.EUPS_MSGSND_DETAIL.EUPS_BUS_TYP is
'EUPS业务类型';

comment on column BBIP_EUPS.EUPS_MSGSND_DETAIL.MOB_NUM is
'手机号码';

comment on column BBIP_EUPS.EUPS_MSGSND_DETAIL.SMS_CTN is
'短信内容';

comment on column BBIP_EUPS.EUPS_MSGSND_DETAIL.SND_DTE is
'发送日期';

comment on column BBIP_EUPS.EUPS_MSGSND_DETAIL.SND_TME is
'发送时间';

comment on column BBIP_EUPS.EUPS_MSGSND_DETAIL.CRE_DTE is
'创建日期';

--==============================================================
-- Table: EUPS_MSG_STORE
--==============================================================
create table BBIP_EUPS.EUPS_MSG_STORE
(
   SYS_NO               CHAR(20)               not null,
   MOB_NUM              VARCHAR(90)            not null,
   SMS_CTN              VARCHAR(2048)          not null,
   SND_DTE              DATE,
   SND_TME              TIMESTAMP,
   COM_NO               CHAR(15)               not null,
   BUS_KND              CHAR(3)                not null,
   EUPS_BUS_TYP         CHAR(10)               not null,
   CRE_DTE              DATE                   not null,
   constraint "P_T_MS_STE_Idx1" primary key (SYS_NO)
);

comment on table BBIP_EUPS.EUPS_MSG_STORE is
'用于定时触发短信发送，后送后转移到已发短信记录表';

comment on column BBIP_EUPS.EUPS_MSG_STORE.SYS_NO is
'系统编号';

comment on column BBIP_EUPS.EUPS_MSG_STORE.MOB_NUM is
'手机号码';

comment on column BBIP_EUPS.EUPS_MSG_STORE.SMS_CTN is
'短信内容';

comment on column BBIP_EUPS.EUPS_MSG_STORE.SND_DTE is
'发送日期';

comment on column BBIP_EUPS.EUPS_MSG_STORE.SND_TME is
'发送时间';

comment on column BBIP_EUPS.EUPS_MSG_STORE.COM_NO is
'单位编号';

comment on column BBIP_EUPS.EUPS_MSG_STORE.BUS_KND is
'业务种类';

comment on column BBIP_EUPS.EUPS_MSG_STORE.EUPS_BUS_TYP is
'EUPS业务类型';

comment on column BBIP_EUPS.EUPS_MSG_STORE.CRE_DTE is
'创建日期';

--==============================================================
-- Table: EUPS_ORDER_BUSTYPE
--==============================================================
create table BBIP_EUPS.EUPS_ORDER_BUSTYPE
(
   SYS_NO               CHAR(20)               not null,
   EUPS_BUS_TYP         CHAR(10)               not null,
   PUR_TYP_NUM          CHAR(2)                not null,
   BUS_SNM              VARCHAR(180),
   BUS_KND              CHAR(3),
   BUS_RMK              CHAR(60),
   SMS_FLG              CHAR(1),
   CRE_TLR              CHAR(7)                not null,
   CRE_DTE              DATE                   not null,
   MOD_TLR              CHAR(7),
   MOD_DTE              DATE,
   USE_STS              CHAR(1)                not null
      constraint C_USE_STS check (USE_STS in ('0','2')) default '0',
   constraint "P_T_OD_BT_Idx1" primary key (SYS_NO)
);

comment on table BBIP_EUPS.EUPS_ORDER_BUSTYPE is
'维护具体的代购业务类型信息';

comment on column BBIP_EUPS.EUPS_ORDER_BUSTYPE.SYS_NO is
'系统编号';

comment on column BBIP_EUPS.EUPS_ORDER_BUSTYPE.EUPS_BUS_TYP is
'EUPS业务类型';

comment on column BBIP_EUPS.EUPS_ORDER_BUSTYPE.PUR_TYP_NUM is
'代购类型编号';

comment on column BBIP_EUPS.EUPS_ORDER_BUSTYPE.BUS_SNM is
'业务简称';

comment on column BBIP_EUPS.EUPS_ORDER_BUSTYPE.BUS_KND is
'业务种类';

comment on column BBIP_EUPS.EUPS_ORDER_BUSTYPE.BUS_RMK is
'业务说明';

comment on column BBIP_EUPS.EUPS_ORDER_BUSTYPE.SMS_FLG is
'短信标志';

comment on column BBIP_EUPS.EUPS_ORDER_BUSTYPE.CRE_TLR is
'创建柜员';

comment on column BBIP_EUPS.EUPS_ORDER_BUSTYPE.CRE_DTE is
'创建日期';

comment on column BBIP_EUPS.EUPS_ORDER_BUSTYPE.MOD_TLR is
'修改柜员';

comment on column BBIP_EUPS.EUPS_ORDER_BUSTYPE.MOD_DTE is
'修改日期';

comment on column BBIP_EUPS.EUPS_ORDER_BUSTYPE.USE_STS is
'使用状态，0：正常，2：停用';

--==============================================================
-- Table: EUPS_ORDER_INFO
--==============================================================
create table BBIP_EUPS.EUPS_ORDER_INFO
(
   SQN                  CHAR(20)               not null,
   ODE_NO               CHAR(40)               not null,
   EUPS_BUS_TYP         CHAR(10)               not null,
   PUR_TYP_NUM          CHAR(2)                not null,
   SPL_TER_NO           CHAR(8),
   SPL_NO               CHAR(16),
   ACT_NO               CHAR(20),
   CUS_AC               CHAR(24),
   CUS_NME              VARCHAR(300),
   ID_NO                VARCHAR(96),
   ID_TYP               CHAR(4),
   ODE_AMT              DECIMAL(18,2),
   TXN_TME              TIMESTAMP              not null,
   CRE_DTE              DATE                   not null,
   CRE_TME              TIMESTAMP              not null,
   MOD_DTE              DATE,
   MOD_TME              TIMESTAMP,
   USE_DTE              DATE,
   USE_TME              TIMESTAMP,
   CMU_TEL              VARCHAR(90),
   CMU_ADR              VARCHAR(762),
   EML                  VARCHAR(120),
   ODE_NUM              CHAR(8)                not null,
   ODE_STS              CHAR(1)                not null,
   FIL_FLG              CHAR(1)               
      constraint C_FIL_FLG check (FIL_FLG is null or (FIL_FLG in ('0','1'))) default '0',
   FIL_TAB              CHAR(128),
   constraint "P_T_OD_INF_Idx1" primary key (SQN)
);

comment on table BBIP_EUPS.EUPS_ORDER_INFO is
'保存订单公共数据';

comment on column BBIP_EUPS.EUPS_ORDER_INFO.SQN is
'流水号';

comment on column BBIP_EUPS.EUPS_ORDER_INFO.ODE_NO is
'订单号';

comment on column BBIP_EUPS.EUPS_ORDER_INFO.EUPS_BUS_TYP is
'EUPS业务类型';

comment on column BBIP_EUPS.EUPS_ORDER_INFO.PUR_TYP_NUM is
'代购类型编号';

comment on column BBIP_EUPS.EUPS_ORDER_INFO.SPL_TER_NO is
'商户终端号';

comment on column BBIP_EUPS.EUPS_ORDER_INFO.SPL_NO is
'商户编号';

comment on column BBIP_EUPS.EUPS_ORDER_INFO.ACT_NO is
'活动编号';

comment on column BBIP_EUPS.EUPS_ORDER_INFO.CUS_AC is
'客户账号';

comment on column BBIP_EUPS.EUPS_ORDER_INFO.CUS_NME is
'客户名称';

comment on column BBIP_EUPS.EUPS_ORDER_INFO.ID_NO is
'证件号码';

comment on column BBIP_EUPS.EUPS_ORDER_INFO.ID_TYP is
'证件类型';

comment on column BBIP_EUPS.EUPS_ORDER_INFO.ODE_AMT is
'订单金额';

comment on column BBIP_EUPS.EUPS_ORDER_INFO.TXN_TME is
'交易时间';

comment on column BBIP_EUPS.EUPS_ORDER_INFO.CRE_DTE is
'创建日期';

comment on column BBIP_EUPS.EUPS_ORDER_INFO.CRE_TME is
'创建时间';

comment on column BBIP_EUPS.EUPS_ORDER_INFO.MOD_DTE is
'修改日期';

comment on column BBIP_EUPS.EUPS_ORDER_INFO.MOD_TME is
'修改时间';

comment on column BBIP_EUPS.EUPS_ORDER_INFO.USE_DTE is
'使用日期';

comment on column BBIP_EUPS.EUPS_ORDER_INFO.USE_TME is
'使用时间';

comment on column BBIP_EUPS.EUPS_ORDER_INFO.CMU_TEL is
'联系电话';

comment on column BBIP_EUPS.EUPS_ORDER_INFO.CMU_ADR is
'联系地址';

comment on column BBIP_EUPS.EUPS_ORDER_INFO.EML is
'电子邮箱';

comment on column BBIP_EUPS.EUPS_ORDER_INFO.ODE_NUM is
'订购数量';

comment on column BBIP_EUPS.EUPS_ORDER_INFO.ODE_STS is
'订单状态';

comment on column BBIP_EUPS.EUPS_ORDER_INFO.FIL_FLG is
'扩展标志';

comment on column BBIP_EUPS.EUPS_ORDER_INFO.FIL_TAB is
'扩展表名';

--==============================================================
-- Table: EUPS_ORDER_MER
--==============================================================
create table BBIP_EUPS.EUPS_ORDER_MER
(
   SYS_NO               CHAR(20)               not null,
   EUPS_BUS_TYP         CHAR(10)               not null,
   SPL_NO               CHAR(16)               not null,
   SPL_NME              VARCHAR(300),
   LIS_ACC              CHAR(24),
   LIS_ACC_NME          CHAR(120),
   CRE_DTE              DATE                   not null,
   CRE_TLR              CHAR(7)                not null,
   MOD_DTE              DATE,
   MOD_TLR              CHAR(7),
   USE_STS              CHAR(1)               
      constraint C_USE_STS check (USE_STS is null or (USE_STS in ('0','2'))) default '0',
   constraint "P_T_OD_MER_Idx1" primary key (SYS_NO)
);

comment on table BBIP_EUPS.EUPS_ORDER_MER is
'管理代购商户信息';

comment on column BBIP_EUPS.EUPS_ORDER_MER.SYS_NO is
'系统编号';

comment on column BBIP_EUPS.EUPS_ORDER_MER.EUPS_BUS_TYP is
'EUPS业务类型';

comment on column BBIP_EUPS.EUPS_ORDER_MER.SPL_NO is
'商户编号';

comment on column BBIP_EUPS.EUPS_ORDER_MER.SPL_NME is
'商户名称';

comment on column BBIP_EUPS.EUPS_ORDER_MER.LIS_ACC is
'清算账号 代理单位账号/内部账号';

comment on column BBIP_EUPS.EUPS_ORDER_MER.LIS_ACC_NME is
'清算账号户名';

comment on column BBIP_EUPS.EUPS_ORDER_MER.CRE_DTE is
'创建日期';

comment on column BBIP_EUPS.EUPS_ORDER_MER.CRE_TLR is
'创建柜员';

comment on column BBIP_EUPS.EUPS_ORDER_MER.MOD_DTE is
'修改日期';

comment on column BBIP_EUPS.EUPS_ORDER_MER.MOD_TLR is
'修改柜员';

comment on column BBIP_EUPS.EUPS_ORDER_MER.USE_STS is
'使用状态，0：正常，2：停用';

--==============================================================
-- Table: EUPS_ORDER_TERM_INFO
--==============================================================
create table BBIP_EUPS.EUPS_ORDER_TERM_INFO
(
   SYS_NO               CHAR(20)               not null,
   EUPS_BUS_TYP         CHAR(10)               not null,
   THD_RGN_NO           CHAR(20),
   SPL_TER_NO           CHAR(8)                not null,
   TML_NME              VARCHAR(300),
   CRE_DTE              DATE                   not null,
   CRE_TLR              CHAR(7)                not null,
   MOD_DTE              DATE,
   MOD_TLR              CHAR(7),
   USE_STS              CHAR(1)               
      constraint C_USE_STS check (USE_STS is null or (USE_STS in ('0','2'))) default '0',
   constraint "P_T_OD_TM_IF_Idx1" primary key (SYS_NO)
);

comment on table BBIP_EUPS.EUPS_ORDER_TERM_INFO is
'管理发起代购请求的终端';

comment on column BBIP_EUPS.EUPS_ORDER_TERM_INFO.SYS_NO is
'系统编号';

comment on column BBIP_EUPS.EUPS_ORDER_TERM_INFO.EUPS_BUS_TYP is
'EUPS业务类型';

comment on column BBIP_EUPS.EUPS_ORDER_TERM_INFO.THD_RGN_NO is
'第三方地区编号';

comment on column BBIP_EUPS.EUPS_ORDER_TERM_INFO.SPL_TER_NO is
'商户终端号';

comment on column BBIP_EUPS.EUPS_ORDER_TERM_INFO.TML_NME is
'终端名称';

comment on column BBIP_EUPS.EUPS_ORDER_TERM_INFO.CRE_DTE is
'创建日期';

comment on column BBIP_EUPS.EUPS_ORDER_TERM_INFO.CRE_TLR is
'创建柜员';

comment on column BBIP_EUPS.EUPS_ORDER_TERM_INFO.MOD_DTE is
'修改日期';

comment on column BBIP_EUPS.EUPS_ORDER_TERM_INFO.MOD_TLR is
'修改柜员';

comment on column BBIP_EUPS.EUPS_ORDER_TERM_INFO.USE_STS is
'使用状态，0：正常，2：停用';

--==============================================================
-- Table: EUPS_ORDER_TYPE
--==============================================================
create table BBIP_EUPS.EUPS_ORDER_TYPE
(
   SYS_NO               CHAR(20)               not null,
   PUR_TYP_NUM          CHAR(2)                not null,
   PUR_TYP_NME          VARCHAR(300),
   USE_STS              CHAR(1)               
      constraint C_USE_STS check (USE_STS is null or (USE_STS in ('0','2'))) default '0',
   constraint "P_T_OD_BT_Idx1" primary key (SYS_NO)
);

comment on table BBIP_EUPS.EUPS_ORDER_TYPE is
'管理代购的类型，如：00—飞机票;01—火车票;02—汽车票;10—球场预定;11—折扣活动;12—福彩';

comment on column BBIP_EUPS.EUPS_ORDER_TYPE.SYS_NO is
'系统编号';

comment on column BBIP_EUPS.EUPS_ORDER_TYPE.PUR_TYP_NUM is
'代购类型编号';

comment on column BBIP_EUPS.EUPS_ORDER_TYPE.PUR_TYP_NME is
'代购类型名称';

comment on column BBIP_EUPS.EUPS_ORDER_TYPE.USE_STS is
'使用状态，0：正常，2：停用';

--==============================================================
-- Table: EUPS_ORDER_USER_INFO
--==============================================================
create table BBIP_EUPS.EUPS_ORDER_USER_INFO
(
   SYS_NO               CHAR(20)               not null,
   EUPS_BUS_TYP         CHAR(10)               not null,
   THD_RGN_NO           CHAR(20),
   USR_NUM              CHAR(16)               not null,
   USR_NME              VARCHAR(300),
   COM_NME              VARCHAR(300),
   TEL                  VARCHAR(90),
   PSW                  VARCHAR(32),
   CRE_DTE              DATE                   not null,
   CRE_TLR              CHAR(7)                not null,
   MOD_DTE              DATE,
   MOD_TLR              CHAR(7),
   USE_STS              CHAR(1)               
      constraint C_USE_STS check (USE_STS is null or (USE_STS in ('0','2'))) default '0',
   constraint "P_T_OD_USR_INF_Idx" primary key (SYS_NO)
);

comment on table BBIP_EUPS.EUPS_ORDER_USER_INFO is
'管理员、操作员等系统使用用户';

comment on column BBIP_EUPS.EUPS_ORDER_USER_INFO.SYS_NO is
'系统编号';

comment on column BBIP_EUPS.EUPS_ORDER_USER_INFO.EUPS_BUS_TYP is
'EUPS业务类型';

comment on column BBIP_EUPS.EUPS_ORDER_USER_INFO.THD_RGN_NO is
'第三方地区编号';

comment on column BBIP_EUPS.EUPS_ORDER_USER_INFO.USR_NUM is
'用户编号';

comment on column BBIP_EUPS.EUPS_ORDER_USER_INFO.USR_NME is
'用户名称';

comment on column BBIP_EUPS.EUPS_ORDER_USER_INFO.COM_NME is
'公司名称';

comment on column BBIP_EUPS.EUPS_ORDER_USER_INFO.TEL is
'电话号码';

comment on column BBIP_EUPS.EUPS_ORDER_USER_INFO.PSW is
'用户密码';

comment on column BBIP_EUPS.EUPS_ORDER_USER_INFO.CRE_DTE is
'创建日期';

comment on column BBIP_EUPS.EUPS_ORDER_USER_INFO.CRE_TLR is
'创建柜员';

comment on column BBIP_EUPS.EUPS_ORDER_USER_INFO.MOD_DTE is
'修改日期';

comment on column BBIP_EUPS.EUPS_ORDER_USER_INFO.MOD_TLR is
'修改柜员';

comment on column BBIP_EUPS.EUPS_ORDER_USER_INFO.USE_STS is
'使用状态，0：正常，2：停用';

--==============================================================
-- Table: EUPS_PLANE_TICKET_ORDER
--==============================================================
create table BBIP_EUPS.EUPS_PLANE_TICKET_ORDER
(
   ODE_NO               CHAR(40)               not null,
   AIN_COM              CHAR(20),
   SCH_FLY_NO           CHAR(20)               not null,
   LEV_CIY              CHAR(20)               not null,
   REA_CIY              CHAR(20)               not null,
   TRS_CIY              CHAR(20),
   ELE_TIK_NO           CHAR(20),
   FLI_TIC_LVL          CHAR(3),
   VLD_DTE              DATE,
   END_DTE              DATE,
   CHA_CDE              CHAR(10),
   ALE_INF              CHAR(120),
   FLY_TME              TIMESTAMP,
   FUL_PRT_FLG          CHAR(1),
   ICM_PRT_FLG          CHAR(1),
   REF_NOM_AMT          DECIMAL(18,2),
   AIR_BLD_AMT          DECIMAL(18,2),
   FUE_SUR_AMT          DECIMAL(18,2),
   PAY_PRM              DECIMAL(18,2),
   DRA_UNT              CHAR(20),
   constraint "P_T_PL_TIC_OD_Idx1" primary key (ODE_NO)
);

comment on table BBIP_EUPS.EUPS_PLANE_TICKET_ORDER is
'存放机票订单详情，关联订单基本信息';

comment on column BBIP_EUPS.EUPS_PLANE_TICKET_ORDER.ODE_NO is
'订单号';

comment on column BBIP_EUPS.EUPS_PLANE_TICKET_ORDER.AIN_COM is
'航空公司';

comment on column BBIP_EUPS.EUPS_PLANE_TICKET_ORDER.SCH_FLY_NO is
'航班号';

comment on column BBIP_EUPS.EUPS_PLANE_TICKET_ORDER.LEV_CIY is
'出发城市';

comment on column BBIP_EUPS.EUPS_PLANE_TICKET_ORDER.REA_CIY is
'达到城市';

comment on column BBIP_EUPS.EUPS_PLANE_TICKET_ORDER.TRS_CIY is
'中转城市';

comment on column BBIP_EUPS.EUPS_PLANE_TICKET_ORDER.ELE_TIK_NO is
'电子客票号';

comment on column BBIP_EUPS.EUPS_PLANE_TICKET_ORDER.FLI_TIC_LVL is
'机票客票级别';

comment on column BBIP_EUPS.EUPS_PLANE_TICKET_ORDER.VLD_DTE is
'生效日期';

comment on column BBIP_EUPS.EUPS_PLANE_TICKET_ORDER.END_DTE is
'截止日期';

comment on column BBIP_EUPS.EUPS_PLANE_TICKET_ORDER.CHA_CDE is
'验证码';

comment on column BBIP_EUPS.EUPS_PLANE_TICKET_ORDER.ALE_INF is
'提示信息';

comment on column BBIP_EUPS.EUPS_PLANE_TICKET_ORDER.FLY_TME is
'起飞时间';

comment on column BBIP_EUPS.EUPS_PLANE_TICKET_ORDER.FUL_PRT_FLG is
'全量打印标志';

comment on column BBIP_EUPS.EUPS_PLANE_TICKET_ORDER.ICM_PRT_FLG is
'增量打印标志';

comment on column BBIP_EUPS.EUPS_PLANE_TICKET_ORDER.REF_NOM_AMT is
'参考票价';

comment on column BBIP_EUPS.EUPS_PLANE_TICKET_ORDER.AIR_BLD_AMT is
'机场建设费';

comment on column BBIP_EUPS.EUPS_PLANE_TICKET_ORDER.FUE_SUR_AMT is
'燃油附加费';

comment on column BBIP_EUPS.EUPS_PLANE_TICKET_ORDER.PAY_PRM is
'保险费';

comment on column BBIP_EUPS.EUPS_PLANE_TICKET_ORDER.DRA_UNT is
'出票单位';

--==============================================================
-- Table: EUPS_SPORTS_LOTTERY_ORDER
--==============================================================
create table BBIP_EUPS.EUPS_SPORTS_LOTTERY_ORDER
(
   ODE_NO               CHAR(40)               not null,
   ACT_NO               CHAR(20),
   AWA_PER_NO           CHAR(10),
   PCG_TYP              CHAR(5),
   OTL_NUM_MDE          CHAR(4),
   LTY_NUM              INTEGER                not null,
   BET_NO               CHAR(200)              not null,
   BET_MTP              INTEGER                not null,
   ATO_IVN_TRM          INTEGER,
   ATO_IVN_AMT          DECIMAL(18,2)          not null,
   PGM_STS              CHAR(1),
   constraint "P_T_SP_LT_OD_Idx1" primary key (ODE_NO)
);

comment on table BBIP_EUPS.EUPS_SPORTS_LOTTERY_ORDER is
'存放体育彩票订单详情，关联订单基本信息';

comment on column BBIP_EUPS.EUPS_SPORTS_LOTTERY_ORDER.ODE_NO is
'订单号';

comment on column BBIP_EUPS.EUPS_SPORTS_LOTTERY_ORDER.ACT_NO is
'活动编号';

comment on column BBIP_EUPS.EUPS_SPORTS_LOTTERY_ORDER.AWA_PER_NO is
'奖期编号';

comment on column BBIP_EUPS.EUPS_SPORTS_LOTTERY_ORDER.PCG_TYP is
'套餐类型';

comment on column BBIP_EUPS.EUPS_SPORTS_LOTTERY_ORDER.OTL_NUM_MDE is
'选号方式';

comment on column BBIP_EUPS.EUPS_SPORTS_LOTTERY_ORDER.LTY_NUM is
'号码注数';

comment on column BBIP_EUPS.EUPS_SPORTS_LOTTERY_ORDER.BET_NO is
'投注号码';

comment on column BBIP_EUPS.EUPS_SPORTS_LOTTERY_ORDER.BET_MTP is
'投注倍数';

comment on column BBIP_EUPS.EUPS_SPORTS_LOTTERY_ORDER.ATO_IVN_TRM is
'定投期数';

comment on column BBIP_EUPS.EUPS_SPORTS_LOTTERY_ORDER.ATO_IVN_AMT is
'定投金额';

comment on column BBIP_EUPS.EUPS_SPORTS_LOTTERY_ORDER.PGM_STS is
'方案状态';

--==============================================================
-- Table: EUPS_THD_AREA_INFO
--==============================================================
create table BBIP_EUPS.EUPS_THD_AREA_INFO
(
   SYS_NO               CHAR(20)               not null,
   COM_NO               CHAR(15)               not null,
   EUPS_BUS_TYP         CHAR(10)               not null,
   THD_RGN_NO           CHAR(20)               not null,
   THD_RGN_NME          VARCHAR(300),
   USE_STS              CHAR(1)               
      constraint C_USE_STS check (USE_STS is null or (USE_STS in ('0','2'))) default '0',
   constraint "P_THD_A_IF_Idx1" primary key (SYS_NO),
   constraint "A_THD_A_IF_Idx2" unique (COM_NO, THD_RGN_NO)
);

comment on column BBIP_EUPS.EUPS_THD_AREA_INFO.SYS_NO is
'系统编号';

comment on column BBIP_EUPS.EUPS_THD_AREA_INFO.COM_NO is
'单位编号';

comment on column BBIP_EUPS.EUPS_THD_AREA_INFO.EUPS_BUS_TYP is
'EUPS业务类型';

comment on column BBIP_EUPS.EUPS_THD_AREA_INFO.THD_RGN_NO is
'第三方地区编号';

comment on column BBIP_EUPS.EUPS_THD_AREA_INFO.THD_RGN_NME is
'第三方地区名称';

comment on column BBIP_EUPS.EUPS_THD_AREA_INFO.USE_STS is
'使用状态，0：正常，2：停用';

--==============================================================
-- Table: EUPS_THD_BASE_INFO
--==============================================================
create table BBIP_EUPS.EUPS_THD_BASE_INFO
(
   COM_NO               CHAR(15)               not null,
   COM_NME              VARCHAR(300),
   BUS_KND              CHAR(3)                not null,
   EUPS_BUS_TYP         CHAR(10)               not null,
   SIG_SET_TYP          CHAR(1),
   SIG_TME              TIMESTAMP,
   SIG_OUT_TME          TIMESTAMP,
   THD_RCN_TME          TIMESTAMP,
   TXN_TME_CTL1         CHAR(13),
   TXN_TME_CTL2         CHAR(13),
   TXN_DTE_CTL1         CHAR(5),
   TXN_DTE_CTL2         CHAR(5),
   TXN_DTE_CTL3         CHAR(5),
   CMT_FLG              CHAR(1),
   BIL_VOU_FLG          CHAR(1),
   CMU_MDE              CHAR(2)                not null,
   THD_IP_ADR           CHAR(20)               not null,
   BID_POT              CHAR(20)               not null,
   CMU_UNM              CHAR(20),
   CMU_PSW              CHAR(32),
   TME_OUT              DECIMAL(18,3),
   CMU_STS              CHAR(1),
   CMU_KEY              CHAR(32),
   USE_STS              CHAR(1)               
      constraint C_USE_STS check (USE_STS is null or (USE_STS in ('0','2'))) default '0',
   constraint "P_THD_BS_IF_Idx1" primary key (COM_NO)
);

comment on table BBIP_EUPS.EUPS_THD_BASE_INFO is
'保存第三方的基本信息';

comment on column BBIP_EUPS.EUPS_THD_BASE_INFO.COM_NO is
'单位编号';

comment on column BBIP_EUPS.EUPS_THD_BASE_INFO.COM_NME is
'公司名称';

comment on column BBIP_EUPS.EUPS_THD_BASE_INFO.BUS_KND is
'业务种类';

comment on column BBIP_EUPS.EUPS_THD_BASE_INFO.EUPS_BUS_TYP is
'EUPS业务类型';

comment on column BBIP_EUPS.EUPS_THD_BASE_INFO.SIG_SET_TYP is
'签到签退设置方式';

comment on column BBIP_EUPS.EUPS_THD_BASE_INFO.SIG_TME is
'签到时间';

comment on column BBIP_EUPS.EUPS_THD_BASE_INFO.SIG_OUT_TME is
'签退时间';

comment on column BBIP_EUPS.EUPS_THD_BASE_INFO.THD_RCN_TME is
'与第三方对账时间';

comment on column BBIP_EUPS.EUPS_THD_BASE_INFO.TXN_TME_CTL1 is
'交易时间控制1';

comment on column BBIP_EUPS.EUPS_THD_BASE_INFO.TXN_TME_CTL2 is
'交易时间控制2';

comment on column BBIP_EUPS.EUPS_THD_BASE_INFO.TXN_DTE_CTL1 is
'交易日期控制1';

comment on column BBIP_EUPS.EUPS_THD_BASE_INFO.TXN_DTE_CTL2 is
'交易日期控制2';

comment on column BBIP_EUPS.EUPS_THD_BASE_INFO.TXN_DTE_CTL3 is
'交易日期控制3';

comment on column BBIP_EUPS.EUPS_THD_BASE_INFO.CMT_FLG is
'同步标识:是否同步到安全系统 Y-是；N-否';

comment on column BBIP_EUPS.EUPS_THD_BASE_INFO.BIL_VOU_FLG is
'票据重空标志  Y-是；N-否;默认N
密钥是否同步到安全系统 Y-是；N-否；默认N
';

comment on column BBIP_EUPS.EUPS_THD_BASE_INFO.CMU_MDE is
'通讯方式';

comment on column BBIP_EUPS.EUPS_THD_BASE_INFO.THD_IP_ADR is
'IP地址';

comment on column BBIP_EUPS.EUPS_THD_BASE_INFO.BID_POT is
'绑定端口';

comment on column BBIP_EUPS.EUPS_THD_BASE_INFO.CMU_UNM is
'通讯用户';

comment on column BBIP_EUPS.EUPS_THD_BASE_INFO.CMU_PSW is
'通讯口令';

comment on column BBIP_EUPS.EUPS_THD_BASE_INFO.TME_OUT is
'超时时间';

comment on column BBIP_EUPS.EUPS_THD_BASE_INFO.CMU_STS is
'通讯状态';

comment on column BBIP_EUPS.EUPS_THD_BASE_INFO.CMU_KEY is
'通讯密钥';

comment on column BBIP_EUPS.EUPS_THD_BASE_INFO.USE_STS is
'使用状态，0：正常，2：停用';

--==============================================================
-- Table: EUPS_THD_FTP_CONFIG
--==============================================================
create table BBIP_EUPS.EUPS_THD_FTP_CONFIG
(
   FTPID                CHAR(32)               not null,
   COM_NO               CHAR(15)               not null,
   EUPS_BUS_TYP         CHAR(10),
   TYP                  CHAR(1)                not null,
   THD_IP_ADR           CHAR(20)               not null,
   BID_POT              CHAR(20)               not null,
   OPP_NME              VARCHAR(20)            not null,
   OPP_USR_PSW          VARCHAR(32)            not null,
   LOC_FLE_NME          CHAR(60)               not null,
   LOC_DIR              CHAR(128)              not null,
   RMT_FLE_NME          CHAR(60)               not null,
   RMT_WAY              CHAR(128)              not null,
   TME_OUT              DECIMAL(18,3),
   USE_STS              CHAR(1)               
      constraint C_USE_STS check (USE_STS is null or (USE_STS in ('0','2'))) default '0',
   constraint "P_THD_FTP_CFG_Idx1" primary key (FTPID)
);

comment on column BBIP_EUPS.EUPS_THD_FTP_CONFIG.FTPID is
'FTPID';

comment on column BBIP_EUPS.EUPS_THD_FTP_CONFIG.COM_NO is
'单位编号';

comment on column BBIP_EUPS.EUPS_THD_FTP_CONFIG.EUPS_BUS_TYP is
'EUPS业务类型';

comment on column BBIP_EUPS.EUPS_THD_FTP_CONFIG.TYP is
'类型  0：发送；1：接收';

comment on column BBIP_EUPS.EUPS_THD_FTP_CONFIG.THD_IP_ADR is
'IP地址';

comment on column BBIP_EUPS.EUPS_THD_FTP_CONFIG.BID_POT is
'绑定端口';

comment on column BBIP_EUPS.EUPS_THD_FTP_CONFIG.OPP_NME is
'对方用户名';

comment on column BBIP_EUPS.EUPS_THD_FTP_CONFIG.OPP_USR_PSW is
'对方用户口令';

comment on column BBIP_EUPS.EUPS_THD_FTP_CONFIG.LOC_FLE_NME is
'本机文件名';

comment on column BBIP_EUPS.EUPS_THD_FTP_CONFIG.LOC_DIR is
'本机路径';

comment on column BBIP_EUPS.EUPS_THD_FTP_CONFIG.RMT_FLE_NME is
'远程文件名';

comment on column BBIP_EUPS.EUPS_THD_FTP_CONFIG.RMT_WAY is
'远程路径';

comment on column BBIP_EUPS.EUPS_THD_FTP_CONFIG.TME_OUT is
'超时时间';

comment on column BBIP_EUPS.EUPS_THD_FTP_CONFIG.USE_STS is
'使用状态，0：正常，2：停用';

--==============================================================
-- Table: EUPS_THD_TRANCTL_DETAIL
--==============================================================
create table BBIP_EUPS.EUPS_THD_TRANCTL_DETAIL
(
   SQN                  CHAR(20)               not null,
   TXN_DTE              DATE                   not null,
   TXN_TME              TIMESTAMP              not null,
   TXN_ORG_CDE          CHAR(11),
   TXN_TLR              CHAR(7)                not null,
   CHL_TYP              CHAR(2)                not null,
   TXN_STS              CHAR(1)                not null,
   TXN_CDE              CHAR(6),
   TXN_CTL_TYP          CHAR(1),
   COM_NO               CHAR(15),
   BUS_KND              CHAR(3),
   EUPS_BUS_TYP         CHAR(10),
   RCN_BAT              CHAR(12),
   RCN_DTE              DATE,
   THD_TXN_DTE          DATE,
   THD_TXN_STS          CHAR(1),
   THD_RSP_CDE          CHAR(20),
   RTN_CDE              CHAR(20),
   KEY_GEN_SDE          CHAR(1),
   ORG_CDE              CHAR(11),
   MAN_KEY              CHAR(32),
   WRK_KEY              CHAR(32),
   SS_KEY               CHAR(32),
   KEY_CHE_VAL1         CHAR(16),
   KEY_CHE_VAL2         CHAR(16),
   BAK_FLD1             CHAR(30),
   BAK_FLD2             CHAR(30),
   constraint P_EUPS_THD_LOGIN_I primary key (SQN)
);

comment on table BBIP_EUPS.EUPS_THD_TRANCTL_DETAIL is
'第三方交易控制明细信息表';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_DETAIL.SQN is
'流水号';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_DETAIL.TXN_DTE is
'交易日期';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_DETAIL.TXN_TME is
'交易时间';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_DETAIL.TXN_ORG_CDE is
'交易机构号';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_DETAIL.TXN_TLR is
'交易柜员';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_DETAIL.CHL_TYP is
'渠道类型：(0：柜面;1：电话银行;2：网银;3：ATM/CDM/CRS;4：POS;5：多媒体;6：手机;W：第三方系统)';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_DETAIL.TXN_STS is
'交易状态';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_DETAIL.TXN_CDE is
'交易码';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_DETAIL.TXN_CTL_TYP is
'0：签到；1：签退；2：行内对总账；3：行内对明细帐：4：第三方对总账；5：第三方对明细账；6：第三方文件对账；7：银行文件对账';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_DETAIL.COM_NO is
'单位编号';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_DETAIL.BUS_KND is
'业务种类';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_DETAIL.EUPS_BUS_TYP is
'EUPS业务类型';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_DETAIL.RCN_BAT is
'对账批次';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_DETAIL.RCN_DTE is
'对账日期';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_DETAIL.THD_TXN_DTE is
'第三方交易日期';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_DETAIL.THD_TXN_STS is
'第三方交易状态';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_DETAIL.THD_RSP_CDE is
'第三方响应码';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_DETAIL.RTN_CDE is
'返回码';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_DETAIL.KEY_GEN_SDE is
'密钥产生方:0-BBIP；1-第三方系统';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_DETAIL.ORG_CDE is
'机构号';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_DETAIL.MAN_KEY is
'主密钥';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_DETAIL.WRK_KEY is
'工作密钥';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_DETAIL.SS_KEY is
'会话密钥';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_DETAIL.KEY_CHE_VAL1 is
'密钥检验值1';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_DETAIL.KEY_CHE_VAL2 is
'密钥检验值2';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_DETAIL.BAK_FLD1 is
'备用字段1';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_DETAIL.BAK_FLD2 is
'备用字段2';

--==============================================================
-- Table: EUPS_THD_TRANCTL_INFO
--==============================================================
create table BBIP_EUPS.EUPS_THD_TRANCTL_INFO
(
   COM_NO               CHAR(15)               not null,
   BUS_KND              CHAR(3),
   EUPS_BUS_TYP         CHAR(10),
   TXN_DTE              DATE                   not null,
   TXN_TME              TIMESTAMP              not null,
   TXN_ORG_CDE          CHAR(11),
   TXN_TLR              CHAR(7)                not null,
   CHL_TYP              CHAR(2)                not null,
   TXN_CTL_STS          CHAR(1)                not null,
   KEY_GEN_SDE          CHAR(1),
   ORG_CDE              CHAR(11),
   MAN_KEY              CHAR(32),
   WRK_KEY              CHAR(32),
   SS_KEY               CHAR(32),
   KEY_CHE_VAL1         CHAR(16),
   KEY_CHE_VAL2         CHAR(16),
   USE_STS              CHAR(1)               
      constraint C_USE_STS check (USE_STS is null or (USE_STS in ('0','2'))) default '0',
   constraint "P_THD_TRCTL_Idx1" primary key (COM_NO)
);

comment on table BBIP_EUPS.EUPS_THD_TRANCTL_INFO is
'第三方实时交易控制信息，如：签到、签退、对账';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_INFO.COM_NO is
'单位编号';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_INFO.BUS_KND is
'业务种类';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_INFO.EUPS_BUS_TYP is
'EUPS业务类型';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_INFO.TXN_DTE is
'交易日期';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_INFO.TXN_TME is
'交易时间';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_INFO.TXN_ORG_CDE is
'交易机构号';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_INFO.TXN_TLR is
'交易柜员';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_INFO.CHL_TYP is
'渠道类型：(0：柜面;1：电话银行;2：网银;3：ATM/CDM/CRS;4：POS;5：多媒体;6：手机;W：第三方系统)';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_INFO.TXN_CTL_STS is
'0：签到；1：签退；2：对账中；3：对账完成';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_INFO.KEY_GEN_SDE is
'密钥产生方:0-BBIP；1-第三方系统';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_INFO.ORG_CDE is
'机构号';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_INFO.MAN_KEY is
'主密钥';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_INFO.WRK_KEY is
'工作密钥';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_INFO.SS_KEY is
'会话密钥';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_INFO.KEY_CHE_VAL1 is
'密钥检验值1';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_INFO.KEY_CHE_VAL2 is
'密钥检验值2';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_INFO.USE_STS is
'使用状态，0：正常，2：停用';

--==============================================================
-- Table: EUPS_TRAIN_TICKET_ORDER
--==============================================================
create table BBIP_EUPS.EUPS_TRAIN_TICKET_ORDER
(
   ODE_NO               CHAR(40)               not null,
   TRA_TYP              CHAR(20)               not null,
   TRA_TIC_LVL          CHAR(1),
   TRA_TIC_TYP          CHAR(20),
   TRA_TIC_NO           CHAR(20),
   LEV_CIY              CHAR(20)               not null,
   REA_CIY              CHAR(20)               not null,
   TRS_CIY              CHAR(20),
   ICM_PRT_FLG          CHAR(1),
   FUL_PRT_FLG          CHAR(1),
   REF_NOM_AMT          DECIMAL(18,2)          not null,
   LEV_TME              TIMESTAMP              not null,
   REA_TME              TIMESTAMP,
   RUN_TME              INTEGER,
   constraint "P_T_TRN_TIC_OD_Idx" primary key (ODE_NO)
);

comment on table BBIP_EUPS.EUPS_TRAIN_TICKET_ORDER is
'存放火车票订单详情，关联订单基本信息';

comment on column BBIP_EUPS.EUPS_TRAIN_TICKET_ORDER.ODE_NO is
'订单号';

comment on column BBIP_EUPS.EUPS_TRAIN_TICKET_ORDER.TRA_TYP is
'列车类型';

comment on column BBIP_EUPS.EUPS_TRAIN_TICKET_ORDER.TRA_TIC_LVL is
'火车票座位等级';

comment on column BBIP_EUPS.EUPS_TRAIN_TICKET_ORDER.TRA_TIC_TYP is
'火车票座位类型';

comment on column BBIP_EUPS.EUPS_TRAIN_TICKET_ORDER.TRA_TIC_NO is
'火车票座位号码';

comment on column BBIP_EUPS.EUPS_TRAIN_TICKET_ORDER.LEV_CIY is
'出发城市';

comment on column BBIP_EUPS.EUPS_TRAIN_TICKET_ORDER.REA_CIY is
'达到城市';

comment on column BBIP_EUPS.EUPS_TRAIN_TICKET_ORDER.TRS_CIY is
'中转城市';

comment on column BBIP_EUPS.EUPS_TRAIN_TICKET_ORDER.ICM_PRT_FLG is
'增量打印标志';

comment on column BBIP_EUPS.EUPS_TRAIN_TICKET_ORDER.FUL_PRT_FLG is
'全量打印标志';

comment on column BBIP_EUPS.EUPS_TRAIN_TICKET_ORDER.REF_NOM_AMT is
'参考票价';

comment on column BBIP_EUPS.EUPS_TRAIN_TICKET_ORDER.LEV_TME is
'发车时间';

comment on column BBIP_EUPS.EUPS_TRAIN_TICKET_ORDER.REA_TME is
'到站时间';

comment on column BBIP_EUPS.EUPS_TRAIN_TICKET_ORDER.RUN_TME is
'运行时间';

--==============================================================
-- Table: EUPS_TRANS_JOURNAL
--==============================================================
create table BBIP_EUPS.EUPS_TRANS_JOURNAL
(
   SQN                  CHAR(20)               not null,
   BK                   CHAR(12)               not null,
   BR                   CHAR(11)               not null,
   ORG_CDE              CHAR(11)               not null,
   CHL_TYP              CHAR(2),
   TML_NO               CHAR(8)                not null,
   TXN_TLR              CHAR(7)                not null,
   ATH_TLR_NO           CHAR(7),
   CHK_TLR              CHAR(7),
   BR_VCH_NO            CHAR(20),
   BUS_KND              CHAR(3),
   EUPS_BUS_TYP         CHAR(10)               not null,
   RAP_TYP              CHAR(1)                not null,
   TXN_DTE              DATE                   not null,
   TXN_TME              TIMESTAMP              not null,
   TXN_CDE              CHAR(6)                not null,
   TXN_STS              CHAR(1)                not null,
   RTN_CDE              CHAR(20),
   MFM_TXN_CDE          CHAR(6),
   MFM_TXN_SUB_CDE      CHAR(3),
   AC_DTE               DATE,
   MFM_VCH_NO           CHAR(20),
   MFM_JRN_NO           VARCHAR(20),
   MFM_TXN_STS          CHAR(1),
   MFM_RSP_CDE          CHAR(20),
   COM_NO               CHAR(15)               not null,
   THD_TXN_CDE          CHAR(10),
   THD_AC_DTE           DATE,
   THD_SQN              CHAR(20),
   THD_TXN_STS          CHAR(1),
   THD_RSP_CDE          CHAR(20),
   OLD_PLF_SQN          CHAR(20),
   RCN_FLG              CHAR(1),
   MFM_DYT_RCN_FLG      CHAR(1),
   MFM_EOD_RCN_FLG      CHAR(1),
   MFM_RCN_BAT          CHAR(12),
   MFM_RCN_TME          TIMESTAMP,
   THD_RCN_FLG          CHAR(1),
   THD_RCN_TME          TIMESTAMP,
   THD_RCN_BAT          CHAR(12),
   EXT_RCN_FLG          CHAR(1),
   OUR_OTH_FLG          CHAR(1),
   THD_RGN_NO           CHAR(20),
   THD_CUS_NO           CHAR(30),
   THD_CUS_NME          VARCHAR(300),
   PAY_CHL              CHAR(1),
   SPL_NO               CHAR(16),
   SPL_TER_NO           CHAR(8),
   TXN_TYP              CHAR(1),
   CUS_AC               CHAR(24),
   CUS_NME              VARCHAR(300),
   ID_TYP               CHAR(4),
   ID_NO                VARCHAR(96),
   CMU_TEL              VARCHAR(90),
   ACC_TYP              CHAR(4),
   ACC_SEQ              CHAR(5),
   BV_KND               CHAR(2),
   BV_NO                CHAR(12),
   LIS_ACC              CHAR(24),
   CHO_NO               CHAR(12),
   RMK_CDE              CHAR(4),
   CSH_NO               CHAR(3),
   DRW_MDE              CHAR(1),
   CCY                  CHAR(3)                not null,
   TXN_AMT              DECIMAL(18,2)          not null,
   REQ_TXN_AMT          DECIMAL(18,2),
   FUL_DED_FLG          CHAR(1),
   HFE                  DECIMAL(18,2),
   HFE_ITM_SEQ          CHAR(5),
   HFE_PST_BR           CHAR(11),
   PRT_FLG              CHAR(1),
   PRT_CNT              INTEGER,
   BAK_FLD1             CHAR(30),
   BAK_FLD2             CHAR(30),
   BAK_FLD3             CHAR(30),
   BAK_FLD4             CHAR(30),
   BAK_FLD5             CHAR(30),
   BAK_FLD6             CHAR(30),
   RSV_FLD1             VARCHAR(300),
   RSV_FLD2             VARCHAR(300),
   RSV_FLD3             VARCHAR(1024),
   RSV_FLD4             VARCHAR(1024),
   RSV_FLD7             VARCHAR(1024),
   RSV_FLD8             VARCHAR(1024),
   FIL_FLG              CHAR(1)                not null
      constraint C_FIL_FLG check (FIL_FLG in ('0','1')) default '0',
   constraint "P_T_JL_Idx1" primary key (SQN)
);

comment on table BBIP_EUPS.EUPS_TRANS_JOURNAL is
'代缴费流水表';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.SQN is
'流水号';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.BK is
'分行号';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.BR is
'网点号';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.ORG_CDE is
'机构号';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.CHL_TYP is
'渠道类型：(0：柜面;1：电话银行;2：网银;3：ATM/CDM/CRS;4：POS;5：多媒体;6：手机;W：第三方系统)';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.TML_NO is
'终端号';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.TXN_TLR is
'交易柜员';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.ATH_TLR_NO is
'授权柜员号';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.CHK_TLR is
'复核柜员号';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.BR_VCH_NO is
'网点流水号';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.BUS_KND is
'业务种类';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.EUPS_BUS_TYP is
'EUPS业务类型';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.RAP_TYP is
'收付类型 0-代收；1-代付';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.TXN_DTE is
'交易日期';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.TXN_TME is
'交易时间';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.TXN_CDE is
'交易码';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.TXN_STS is
'交易状态';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.RTN_CDE is
'返回码';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.MFM_TXN_CDE is
'主机交易码';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.MFM_TXN_SUB_CDE is
'主机交易子码';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.AC_DTE is
'会计日期';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.MFM_VCH_NO is
'会计流水号';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.MFM_JRN_NO is
'主机日志号';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.MFM_TXN_STS is
'主机交易状态';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.MFM_RSP_CDE is
'主机响应码';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.COM_NO is
'单位编号';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.THD_TXN_CDE is
'第三方交易码';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.THD_AC_DTE is
'第三方账务日期';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.THD_SQN is
'第三方流水号';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.THD_TXN_STS is
'第三方交易状态';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.THD_RSP_CDE is
'第三方响应码';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.OLD_PLF_SQN is
'原EUPS流水号';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.RCN_FLG is
'要求对账标志';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.MFM_DYT_RCN_FLG is
'与主机日间对账标志';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.MFM_EOD_RCN_FLG is
'与主机日终对账标志';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.MFM_RCN_BAT is
'与主机对账批次';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.MFM_RCN_TME is
'与主机对账时间';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.THD_RCN_FLG is
'与第三方对账标志';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.THD_RCN_TME is
'与第三方对账时间';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.THD_RCN_BAT is
'与第三方对账批次';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.EXT_RCN_FLG is
'外部对账标志(0：未对账;1：正在对账;2：已对账)';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.OUR_OTH_FLG is
'本他行标志 0-本行;1-他行';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.THD_RGN_NO is
'第三方地区编号';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.THD_CUS_NO is
'第三方客户标识';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.THD_CUS_NME is
'第三方客户姓名';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.PAY_CHL is
'支付渠道 0-核心系统；1-全球支付系统；2-总银联；3-地方银联。当他行标志为“1-他行”时必输。';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.SPL_NO is
'商户编号';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.SPL_TER_NO is
'商户终端号';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.TXN_TYP is
'交易类型:N.正常交易 C.冲正交易 R.重发交易';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.CUS_AC is
'客户账号';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.CUS_NME is
'客户名称';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.ID_TYP is
'证件类型';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.ID_NO is
'证件号码';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.CMU_TEL is
'联系电话';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.ACC_TYP is
'账户类型  1--本通,2--账号,4--卡号,
';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.ACC_SEQ is
'账户顺序号';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.BV_KND is
'凭证种类';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.BV_NO is
'凭证号码';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.LIS_ACC is
'清算账号 代理单位账号/内部账号';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.CHO_NO is
'销账号';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.RMK_CDE is
'摘要码';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.CSH_NO is
'现金分析号 代缴-‘121’;代付-‘252’';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.DRW_MDE is
'支取方式  1 凭密支取 2 凭印支取 3 凭证支取 4 任意支取 5 签字支取 6 支付密码 7 电子验印 0 不验密';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.CCY is
'币种';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.TXN_AMT is
'交易金额';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.REQ_TXN_AMT is
'请求交易金额';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.FUL_DED_FLG is
'全额扣款标志:0-允许部分扣款;1-全额扣款(默认)';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.HFE is
'手续费';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.HFE_ITM_SEQ is
'手续费科目顺序号';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.HFE_PST_BR is
'手续费入账机构号';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.PRT_FLG is
'打印标志';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.PRT_CNT is
'打印次数';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.BAK_FLD1 is
'备用字段1';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.BAK_FLD2 is
'备用字段2';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.BAK_FLD3 is
'备用字段3';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.BAK_FLD4 is
'备用字段4';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.BAK_FLD5 is
'备用字段5';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.BAK_FLD6 is
'备用字段6';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.RSV_FLD1 is
'预留字段1';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.RSV_FLD2 is
'预留字段2';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.RSV_FLD3 is
'预留字段3';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.RSV_FLD4 is
'预留字段4';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.RSV_FLD7 is
'预留字段5';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.RSV_FLD8 is
'预留字段6';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.FIL_FLG is
'扩展标志';

--==============================================================
-- Table: EUPS_TRANS_JOURNAL_EXT
--==============================================================
create table BBIP_EUPS.EUPS_TRANS_JOURNAL_EXT
(
   SYS_NO               CHAR(20)               not null,
   SQN                  CHAR(20)               not null,
   REC_NO               INTEGER                not null,
   RSV_FLD1             VARCHAR(300),
   RSV_FLD2             VARCHAR(300),
   RSV_FLD3             VARCHAR(1024),
   RSV_FLD4             VARCHAR(1024),
   RSV_FLD5             VARCHAR(1024),
   RSV_FLD6             VARCHAR(1024),
   constraint "P_T_JL_EXT_Idx1" primary key (SYS_NO),
   constraint "A_T_JL_EXT_Idx2" unique (SQN, REC_NO)
);

comment on table BBIP_EUPS.EUPS_TRANS_JOURNAL_EXT is
'交易流水表的扩展';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL_EXT.SYS_NO is
'系统编号';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL_EXT.SQN is
'流水号';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL_EXT.REC_NO is
'记录号';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL_EXT.RSV_FLD1 is
'预留字段1';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL_EXT.RSV_FLD2 is
'预留字段2';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL_EXT.RSV_FLD3 is
'预留字段3';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL_EXT.RSV_FLD4 is
'预留字段4';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL_EXT.RSV_FLD5 is
'预留字段5';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL_EXT.RSV_FLD6 is
'预留字段6';

--==============================================================
-- Table: EUPS_TRANS_ROLLBACK_CTL
--==============================================================
create table BBIP_EUPS.EUPS_TRANS_ROLLBACK_CTL
(
   SYS_NO               CHAR(20)               not null,
   SQN                  CHAR(20)               not null,
   RVS_DIR              CHAR(1)                not null,
   TXN_TME              TIMESTAMP              not null,
   TXN_DTE              DATE                   not null,
   PSS_CNT              INTEGER,
   MAX_PSS_CNT          INTEGER,
   RVS_STS              CHAR(1)                not null,
   RVS_TXN_CDE          CHAR(6),
   RVS_RET_CDE          CHAR(6),
   constraint "P_T_RBK_CTL_Idx1" primary key (SYS_NO),
   constraint "A_T_RBK_CTL_Idx2" unique (SQN)
);

comment on table BBIP_EUPS.EUPS_TRANS_ROLLBACK_CTL is
'冲正控制表';

comment on column BBIP_EUPS.EUPS_TRANS_ROLLBACK_CTL.SYS_NO is
'系统编号';

comment on column BBIP_EUPS.EUPS_TRANS_ROLLBACK_CTL.SQN is
'流水号';

comment on column BBIP_EUPS.EUPS_TRANS_ROLLBACK_CTL.RVS_DIR is
'冲正方向:0－冲主机；1－冲企业';

comment on column BBIP_EUPS.EUPS_TRANS_ROLLBACK_CTL.TXN_TME is
'交易时间';

comment on column BBIP_EUPS.EUPS_TRANS_ROLLBACK_CTL.TXN_DTE is
'交易日期';

comment on column BBIP_EUPS.EUPS_TRANS_ROLLBACK_CTL.PSS_CNT is
'处理次数';

comment on column BBIP_EUPS.EUPS_TRANS_ROLLBACK_CTL.MAX_PSS_CNT is
'最大处理次数';

comment on column BBIP_EUPS.EUPS_TRANS_ROLLBACK_CTL.RVS_STS is
'冲正状态';

comment on column BBIP_EUPS.EUPS_TRANS_ROLLBACK_CTL.RVS_TXN_CDE is
'冲正交易码';

comment on column BBIP_EUPS.EUPS_TRANS_ROLLBACK_CTL.RVS_RET_CDE is
'冲正返回码';

--==============================================================
-- Table: EUPS_TYPE_MAP
--==============================================================
create table BBIP_EUPS.EUPS_TYPE_MAP
(
   RLH_AGR_TPY          CHAR(3)                not null,
   TAB_NME              CHAR(30),
   constraint "P_T_TP_MAP_Idx1" primary key (RLH_AGR_TPY)
);

comment on column BBIP_EUPS.EUPS_TYPE_MAP.RLH_AGR_TPY is
'补充协议种类';

comment on column BBIP_EUPS.EUPS_TYPE_MAP.TAB_NME is
'表名';

--==============================================================
-- Table: EUPS_WELFARE_LOTTERY_ORDER
--==============================================================
create table BBIP_EUPS.EUPS_WELFARE_LOTTERY_ORDER
(
   ODE_NO               CHAR(40)               not null,
   ACT_NO               CHAR(20),
   PCG_TYP              CHAR(5),
   OTL_NUM_MDE          CHAR(4),
   LTY_NUM              INTEGER                not null,
   BET_NO               CHAR(200)              not null,
   BET_MTP              INTEGER                not null,
   ATO_IVN_TRM          INTEGER,
   ATO_IVN_AMT          DECIMAL(18,2),
   PGM_STS              CHAR(1),
   constraint "P_T_WLF_LT_OD_Idx1" primary key (ODE_NO)
);

comment on table BBIP_EUPS.EUPS_WELFARE_LOTTERY_ORDER is
'存放福利彩票订单详情，关联订单基本信息';

comment on column BBIP_EUPS.EUPS_WELFARE_LOTTERY_ORDER.ODE_NO is
'订单号';

comment on column BBIP_EUPS.EUPS_WELFARE_LOTTERY_ORDER.ACT_NO is
'活动编号';

comment on column BBIP_EUPS.EUPS_WELFARE_LOTTERY_ORDER.PCG_TYP is
'套餐类型';

comment on column BBIP_EUPS.EUPS_WELFARE_LOTTERY_ORDER.OTL_NUM_MDE is
'选号方式';

comment on column BBIP_EUPS.EUPS_WELFARE_LOTTERY_ORDER.LTY_NUM is
'号码注数';

comment on column BBIP_EUPS.EUPS_WELFARE_LOTTERY_ORDER.BET_NO is
'投注号码';

comment on column BBIP_EUPS.EUPS_WELFARE_LOTTERY_ORDER.BET_MTP is
'投注倍数';

comment on column BBIP_EUPS.EUPS_WELFARE_LOTTERY_ORDER.ATO_IVN_TRM is
'定投期数';

comment on column BBIP_EUPS.EUPS_WELFARE_LOTTERY_ORDER.ATO_IVN_AMT is
'定投金额';

comment on column BBIP_EUPS.EUPS_WELFARE_LOTTERY_ORDER.PGM_STS is
'方案状态';

--==============================================================
-- Table: EUPS_ZERO_ACT_INFO
--==============================================================
create table BBIP_EUPS.EUPS_ZERO_ACT_INFO
(
   CUS_AC               CHAR(24)               not null,
   STS                  CHAR(1)                not null,
   OBK_BR               CHAR(6)                not null,
   CUS_NME              VARCHAR(300),
   SMS_RCV_NO           CHAR(11)               not null,
   MOD_DTE              DATE,
   MOD_TLR              CHAR(7),
   DEL_DTE              DATE,
   DEL_TLR              CHAR(7),
   CRE_DTE              DATE,
   CRE_TLR              CHAR(7),
   constraint "P_T_ZeroAct_Idx1" primary key (CUS_AC)
);

comment on table BBIP_EUPS.EUPS_ZERO_ACT_INFO is
'零余额账户登记表';

comment on column BBIP_EUPS.EUPS_ZERO_ACT_INFO.CUS_AC is
'客户账号';

comment on column BBIP_EUPS.EUPS_ZERO_ACT_INFO.STS is
'状态 1正常0删除';

comment on column BBIP_EUPS.EUPS_ZERO_ACT_INFO.OBK_BR is
'开户网点';

comment on column BBIP_EUPS.EUPS_ZERO_ACT_INFO.CUS_NME is
'客户名称';

comment on column BBIP_EUPS.EUPS_ZERO_ACT_INFO.SMS_RCV_NO is
'短信接收号码';

comment on column BBIP_EUPS.EUPS_ZERO_ACT_INFO.MOD_DTE is
'修改日期';

comment on column BBIP_EUPS.EUPS_ZERO_ACT_INFO.MOD_TLR is
'修改柜员';

comment on column BBIP_EUPS.EUPS_ZERO_ACT_INFO.DEL_DTE is
'删除日期';

comment on column BBIP_EUPS.EUPS_ZERO_ACT_INFO.DEL_TLR is
'删除柜员';

comment on column BBIP_EUPS.EUPS_ZERO_ACT_INFO.CRE_DTE is
'创建日期';

comment on column BBIP_EUPS.EUPS_ZERO_ACT_INFO.CRE_TLR is
'创建柜员';

--==============================================================
-- Table: EUPS_ZERO_JOURNAL
--==============================================================
create table BBIP_EUPS.EUPS_ZERO_JOURNAL
(
   SQN                  CHAR(20)               not null,
   BUS_KND              CHAR(3)                not null,
   EUPS_BUS_TYP         CHAR(10)               not null,
   TXN_DTE              DATE                   not null,
   TXN_TME              TIMESTAMP              not null,
   DSK_SEQ_NUM          CHAR(12),
   CUS_AC               CHAR(24)               not null,
   ORI_AC               CHAR(24),
   ORI_NME              VARCHAR(300),
   TXN_AMT              DECIMAL(18,2)          not null,
   PST_AC_STS           CHAR(1),
   EC_STS               CHAR(1),
   constraint "P_T_ZeroJ_Idx1" primary key (SQN)
);

comment on table BBIP_EUPS.EUPS_ZERO_JOURNAL is
'零余额流水表';

comment on column BBIP_EUPS.EUPS_ZERO_JOURNAL.SQN is
'流水号';

comment on column BBIP_EUPS.EUPS_ZERO_JOURNAL.BUS_KND is
'业务种类';

comment on column BBIP_EUPS.EUPS_ZERO_JOURNAL.EUPS_BUS_TYP is
'EUPS业务类型';

comment on column BBIP_EUPS.EUPS_ZERO_JOURNAL.TXN_DTE is
'交易日期';

comment on column BBIP_EUPS.EUPS_ZERO_JOURNAL.TXN_TME is
'交易时间';

comment on column BBIP_EUPS.EUPS_ZERO_JOURNAL.DSK_SEQ_NUM is
'磁盘对应序列号';

comment on column BBIP_EUPS.EUPS_ZERO_JOURNAL.CUS_AC is
'客户账号';

comment on column BBIP_EUPS.EUPS_ZERO_JOURNAL.ORI_AC is
'发起人账号';

comment on column BBIP_EUPS.EUPS_ZERO_JOURNAL.ORI_NME is
'发起人户名';

comment on column BBIP_EUPS.EUPS_ZERO_JOURNAL.TXN_AMT is
'交易金额';

comment on column BBIP_EUPS.EUPS_ZERO_JOURNAL.PST_AC_STS is
'入账状态:U预处理 A入帐待确认 S入帐已确认';

comment on column BBIP_EUPS.EUPS_ZERO_JOURNAL.EC_STS is
'抹账状态 O待抹帐 B抹帐待确认  X抹帐已确认';

