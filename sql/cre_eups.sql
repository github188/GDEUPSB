--====================================================================
-- DBMS name:      IBM DB2 Version 10.x for Linux, UNIX, and Windows 
-- Created on:     2014/1/15 19:02:14
--====================================================================


drop table BBIP_EUPS.EUPS_AMOUNTINFO;

drop table BBIP_EUPS.EUPS_BRANCH_BUSKND;

drop table BBIP_EUPS.EUPS_BUSTYPE;

drop table BBIP_EUPS.EUPS_THD_BASE_INFO;

drop table BBIP_EUPS.EUPS_THD_COMMCTL_INFO;

drop table BBIP_EUPS.EUPS_THD_FTP_CONFIG;

drop table BBIP_EUPS.EUPS_THD_TRANCTL_DETAIL;

drop table BBIP_EUPS.EUPS_THD_TRANCTL_INFO;

drop table BBIP_EUPS.EUPS_TRANS_JOURNAL;

drop table BBIP_EUPS.EUPS_TRANS_JOURNAL_EXT;

drop table BBIP_EUPS.EUPS_TRANS_ROLLBACK_CTL;

--==============================================================
-- User: BBIP_EUPS
--==============================================================
--==============================================================
-- Table: EUPS_AMOUNTINFO
--==============================================================
create table BBIP_EUPS.EUPS_AMOUNTINFO
(
   SQN                  CHAR(20)               not null,
   BK                   CHAR(12)               not null,
   BR                   CHAR(11)               not null,
   TXN_TLR              CHAR(7)                not null,
   CHL_TYP              CHAR(2)                not null,
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
   OWE_FEE_AMT          DECIMAL(13,2)          not null,
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

comment on column BBIP_EUPS.EUPS_AMOUNTINFO.CHL_TYP is
'交易渠道：(0：柜面;1：电话银行;2：网银;3：ATM/CDM/CRS;4：POS;5：多媒体;6：手机;W：第三方系统)';

comment on column BBIP_EUPS.EUPS_AMOUNTINFO.TXN_DTE is
'交易日期';

comment on column BBIP_EUPS.EUPS_AMOUNTINFO.TXN_TME is
'交易时间';

comment on column BBIP_EUPS.EUPS_AMOUNTINFO.TXN_CDE is
'交易码';

comment on column BBIP_EUPS.EUPS_AMOUNTINFO.RAP_TYP is
'收付类型：0-代收；1-代付';

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
   BK                   CHAR(12)               not null,
   BUS_KND              CHAR(3)                not null,
   USE_STS              CHAR(1)                not null,
   constraint "P_BR_BUSKND_Idx1" primary key (BK, BUS_KND)
);

comment on table BBIP_EUPS.EUPS_BRANCH_BUSKND is
'用于存储分行开放的业务种类。';

comment on column BBIP_EUPS.EUPS_BRANCH_BUSKND.BK is
'分行号';

comment on column BBIP_EUPS.EUPS_BRANCH_BUSKND.BUS_KND is
'业务种类';

comment on column BBIP_EUPS.EUPS_BRANCH_BUSKND.USE_STS is
'使用状态：0正常，2停用';

--==============================================================
-- Table: EUPS_BUSTYPE
--==============================================================
create table BBIP_EUPS.EUPS_BUSTYPE
(
   EUPS_BUS_TYP         CHAR(10)               not null,
   COM_NO               CHAR(15)               not null,
   BUS_KND              CHAR(3)                not null,
   EUPS_BUS_TYP_NME     VARCHAR(300)           not null,
   USE_STS              CHAR(1),
   constraint "P_BUSTYPE_Idx1" primary key (EUPS_BUS_TYP)
);

comment on table BBIP_EUPS.EUPS_BUSTYPE is
'管理公共事业缴费具体的缴费类型。与业务种类是多对一的关系。';

comment on column BBIP_EUPS.EUPS_BUSTYPE.EUPS_BUS_TYP is
'EUPS业务类型';

comment on column BBIP_EUPS.EUPS_BUSTYPE.COM_NO is
'单位编号';

comment on column BBIP_EUPS.EUPS_BUSTYPE.BUS_KND is
'业务种类';

comment on column BBIP_EUPS.EUPS_BUSTYPE.EUPS_BUS_TYP_NME is
'EUPS业务类型名称';

comment on column BBIP_EUPS.EUPS_BUSTYPE.USE_STS is
'使用状态：0正常，2停用';

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
   TXN_TME_CTL1         CHAR(13),
   TXN_TME_CTL2         CHAR(13),
   TXN_DTE_CTL1         CHAR(5),
   TXN_DTE_CTL2         CHAR(5),
   TXN_DTE_CTL3         CHAR(5),
   CMT_FLG              CHAR(1),
   BIL_VOU_FLG          CHAR(1),
   USE_STS              CHAR(1),
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
'合同号码：是否同步到安全系统 Y-是；N-否';

comment on column BBIP_EUPS.EUPS_THD_BASE_INFO.BIL_VOU_FLG is
'票据重空标志 ：Y-是；N-否;默认N

';

comment on column BBIP_EUPS.EUPS_THD_BASE_INFO.USE_STS is
'使用状态：0正常，2停用';

--==============================================================
-- Table: EUPS_THD_COMMCTL_INFO
--==============================================================
create table BBIP_EUPS.EUPS_THD_COMMCTL_INFO
(
   COM_NO               CHAR(15)               not null,
   EUPS_BUS_TYP         CHAR(10)               not null,
   CMU_MDE              CHAR(2)                not null,
   THD_IP_ADR           CHAR(20)               not null,
   BID_POT              CHAR(20)               not null,
   CMU_UNM              CHAR(20),
   CMU_PSW              CHAR(32),
   TME_OUT              DECIMAL(18,3),
   CMU_STS              CHAR(1),
   CMU_KEY              CHAR(32),
   constraint "P_THD_CM_CTL_Idx1" primary key (COM_NO)
);

comment on column BBIP_EUPS.EUPS_THD_COMMCTL_INFO.COM_NO is
'单位编号';

comment on column BBIP_EUPS.EUPS_THD_COMMCTL_INFO.EUPS_BUS_TYP is
'EUPS业务类型';

comment on column BBIP_EUPS.EUPS_THD_COMMCTL_INFO.CMU_MDE is
'通讯方式';

comment on column BBIP_EUPS.EUPS_THD_COMMCTL_INFO.THD_IP_ADR is
'IP地址';

comment on column BBIP_EUPS.EUPS_THD_COMMCTL_INFO.BID_POT is
'绑定端口';

comment on column BBIP_EUPS.EUPS_THD_COMMCTL_INFO.CMU_UNM is
'通讯用户';

comment on column BBIP_EUPS.EUPS_THD_COMMCTL_INFO.CMU_PSW is
'通讯口令';

comment on column BBIP_EUPS.EUPS_THD_COMMCTL_INFO.TME_OUT is
'超时时间';

comment on column BBIP_EUPS.EUPS_THD_COMMCTL_INFO.CMU_STS is
'通讯状态';

comment on column BBIP_EUPS.EUPS_THD_COMMCTL_INFO.CMU_KEY is
'通讯密钥';

--==============================================================
-- Table: EUPS_THD_FTP_CONFIG
--==============================================================
create table BBIP_EUPS.EUPS_THD_FTP_CONFIG
(
   FTPID                CHAR(20)               not null,
   COM_NO               CHAR(15)               not null,
   EUPS_BUS_TYP         CHAR(10),
   TYP                  CHAR(1)                not null,
   THD_IP_ADR           CHAR(20)               not null,
   BID_POT              CHAR(20)               not null,
   OPP_NME              CHAR(20)               not null,
   OPP_USR_PSW          CHAR(32)               not null,
   LOC_FLE_NME          CHAR(60)               not null,
   LOC_DIR              CHAR(128)              not null,
   RMT_FLE_NME          CHAR(60)               not null,
   RMT_WAY              CHAR(128)              not null,
   TME_OUT              DECIMAL(18,3),
   constraint "P_THD_FTP_CFG_Idx1" primary key (FTPID)
);

comment on column BBIP_EUPS.EUPS_THD_FTP_CONFIG.FTPID is
'FTPID';

comment on column BBIP_EUPS.EUPS_THD_FTP_CONFIG.COM_NO is
'单位编号';

comment on column BBIP_EUPS.EUPS_THD_FTP_CONFIG.EUPS_BUS_TYP is
'EUPS业务类型';

comment on column BBIP_EUPS.EUPS_THD_FTP_CONFIG.TYP is
'类型：0：发送；1：接收';

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
   TXN_TYP              CHAR(1),
   RCN_BAT              CHAR(12),
   RTN_CDE              CHAR(20),
   KEY_GEN_SDE          CHAR(1),
   ORG_CDE              CHAR(11),
   MAN_KEY              CHAR(32),
   WRK_KEY              CHAR(32),
   SS_KEY               CHAR(32),
   KEY_CHE_VAL1         CHAR(16),
   KEY_CHE_VAL2         CHAR(16),
   constraint "P_THD_TRCTL_DT_Idx" primary key (SQN)
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
'交易渠道：(0：柜面;1：电话银行;2：网银;3：ATM/CDM/CRS;4：POS;5：多媒体;6：手机;W：第三方系统)';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_DETAIL.TXN_STS is
'交易状态';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_DETAIL.TXN_CDE is
'交易码';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_DETAIL.TXN_TYP is
'交易类型：N.正常交易 C.冲正交易 R.重发交易';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_DETAIL.RTN_CDE is
'返回码';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_DETAIL.KEY_GEN_SDE is
'密钥产生方：0-BBIP；1-第三方系统';

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

--==============================================================
-- Table: EUPS_THD_TRANCTL_INFO
--==============================================================
create table BBIP_EUPS.EUPS_THD_TRANCTL_INFO
(
   COM_NO               CHAR(15)               not null,
   EUPS_BUS_TYP         CHAR(10),
   TXN_DTE              DATE                   not null,
   TXN_TME              TIMESTAMP              not null,
   TXN_ORG_CDE          CHAR(11),
   TXN_TLR              CHAR(7)                not null,
   CHL_TYP              CHAR(2)                not null,
   TXN_STS              CHAR(1)                not null,
   TXN_TYP              CHAR(1)                not null,
   KEY_GEN_SDE          CHAR(1),
   ORG_CDE              CHAR(11),
   MAN_KEY              CHAR(32),
   WRK_KEY              CHAR(32),
   SS_KEY               CHAR(32),
   KEY_CHE_VAL1         CHAR(16),
   KEY_CHE_VAL2         CHAR(16),
   constraint "P_THD_TRCTL_Idx1" primary key (COM_NO)
);

comment on table BBIP_EUPS.EUPS_THD_TRANCTL_INFO is
'第三方实时交易控制信息，如：签到、签退、对账';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_INFO.COM_NO is
'单位编号';

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
'交易渠道：(0：柜面;1：电话银行;2：网银;3：ATM/CDM/CRS;4：POS;5：多媒体;6：手机;W：第三方系统)';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_INFO.TXN_STS is
'交易状态';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_INFO.TXN_TYP is
'交易类型：N.正常交易 C.冲正交易 R.重发交易';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_INFO.KEY_GEN_SDE is
'密钥产生方：0-BBIP；1-第三方系统';

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
   MFM_JRN_NO           CHAR(20),
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
   HFE                  DECIMAL(15,2),
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
   RSV_FLD5             VARCHAR(1024),
   RSV_FLD6             VARCHAR(1024),
   FIL_FLG              CHAR(1)                not null,
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
'交易渠道：(0：柜面;1：电话银行;2：网银;3：ATM/CDM/CRS;4：POS;5：多媒体;6：手机;W：第三方系统)';

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
'收付类型：0-代收；1-代付';

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
'主机流水号';

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
'外部对账标志：(0：未对账;1：正在对账;2：已对账)';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.OUR_OTH_FLG is
'本他行标志：0-本行;1-他行';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.THD_RGN_NO is
'第三方地区编号';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.THD_CUS_NO is
'第三方客户标识';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.THD_CUS_NME is
'第三方客户姓名';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.PAY_CHL is
'支付渠道（0-核心系统；1-全球支付系统；2-总银联；3-地方银联。当他行标志为“1-他行”时必输）';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.SPL_NO is
'商户编号';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.SPL_TER_NO is
'商户终端号';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.TXN_TYP is
'交易类型：N.正常交易 C.冲正交易 R.重发交易';

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
'账户类型：1--本通,2--账号,4--卡号,
';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.ACC_SEQ is
'账户顺序号';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.BV_KND is
'凭证种类';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.BV_NO is
'凭证号码';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.LIS_ACC is
'清算账号（代理单位账号/内部账号）';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.CHO_NO is
'销账号';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.RMK_CDE is
'摘要码';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.CSH_NO is
'现金分析号:代缴-‘121’;代付-‘252’';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.DRW_MDE is
'支取方式：（ 1 凭密支取 2 凭印支取 3 凭证支取 4 任意支取 5 签字支取 6 支付密码 7 电子验印 0 不验密）';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.CCY is
'币种';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.TXN_AMT is
'交易金额';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.REQ_TXN_AMT is
'请求交易金额';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.FUL_DED_FLG is
'全额扣款标志：0-允许部分扣款;1-全额扣款(默认)';

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

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.RSV_FLD5 is
'预留字段5';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.RSV_FLD6 is
'预留字段6';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.FIL_FLG is
'扩展标志：0，扩展；1，不扩张';

--==============================================================
-- Table: EUPS_TRANS_JOURNAL_EXT
--==============================================================
create table BBIP_EUPS.EUPS_TRANS_JOURNAL_EXT
(
   SQN                  CHAR(20)               not null,
   REC_NO               INTEGER                not null,
   RSV_FLD1             VARCHAR(300),
   RSV_FLD2             VARCHAR(300),
   RSV_FLD3             VARCHAR(1024),
   RSV_FLD4             VARCHAR(1024),
   RSV_FLD5             VARCHAR(1024),
   RSV_FLD6             VARCHAR(1024),
   constraint "P_T_JL_EXT_Idx1" primary key (SQN, REC_NO)
);

comment on table BBIP_EUPS.EUPS_TRANS_JOURNAL_EXT is
'交易流水表的扩展';

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
   SQN                  CHAR(20)               not null,
   RVS_DIR              CHAR(1)                not null,
   TXN_TME              TIMESTAMP              not null,
   TXN_DTE              DATE                   not null,
   PSS_CNT              INTEGER,
   MAX_PSS_CNT          INTEGER,
   RVS_STS              CHAR(1)                not null,
   RVS_TXN_CDE          CHAR(6),
   RVS_RET_CDE          CHAR(20),
   constraint "P_T_RBK_CTL_Idx1" primary key (SQN, RVS_DIR)
);

comment on table BBIP_EUPS.EUPS_TRANS_ROLLBACK_CTL is
'冲正控制表';

comment on column BBIP_EUPS.EUPS_TRANS_ROLLBACK_CTL.SQN is
'流水号';

comment on column BBIP_EUPS.EUPS_TRANS_ROLLBACK_CTL.RVS_DIR is
'冲正方向：0－冲主机；1－冲企业';

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

