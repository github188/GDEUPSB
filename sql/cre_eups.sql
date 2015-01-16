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
'Ƿ����Ϣ��ʱ�����ڴ洢�ӵ�������ѯ���ķ�����Ϣ��ÿ������';

comment on column BBIP_EUPS.EUPS_AMOUNTINFO.SQN is
'��ˮ��';

comment on column BBIP_EUPS.EUPS_AMOUNTINFO.BK is
'���к�';

comment on column BBIP_EUPS.EUPS_AMOUNTINFO.BR is
'�����';

comment on column BBIP_EUPS.EUPS_AMOUNTINFO.TXN_TLR is
'���׹�Ա';

comment on column BBIP_EUPS.EUPS_AMOUNTINFO.CHL_TYP is
'����������(0������;1���绰����;2������;3��ATM/CDM/CRS;4��POS;5����ý��;6���ֻ�;W��������ϵͳ)';

comment on column BBIP_EUPS.EUPS_AMOUNTINFO.TXN_DTE is
'��������';

comment on column BBIP_EUPS.EUPS_AMOUNTINFO.TXN_TME is
'����ʱ��';

comment on column BBIP_EUPS.EUPS_AMOUNTINFO.TXN_CDE is
'������';

comment on column BBIP_EUPS.EUPS_AMOUNTINFO.RAP_TYP is
'�ո����ͣ�0-���գ�1-����';

comment on column BBIP_EUPS.EUPS_AMOUNTINFO.BUS_KND is
'ҵ������';

comment on column BBIP_EUPS.EUPS_AMOUNTINFO.EUPS_BUS_TYP is
'EUPSҵ������';

comment on column BBIP_EUPS.EUPS_AMOUNTINFO.COM_NO is
'��λ���';

comment on column BBIP_EUPS.EUPS_AMOUNTINFO.THD_RGN_NO is
'�������������';

comment on column BBIP_EUPS.EUPS_AMOUNTINFO.BAR_CDE is
'������';

comment on column BBIP_EUPS.EUPS_AMOUNTINFO.THD_CUS_NO is
'�������ͻ���ʶ';

comment on column BBIP_EUPS.EUPS_AMOUNTINFO.THD_CUS_NME is
'�������ͻ�����';

comment on column BBIP_EUPS.EUPS_AMOUNTINFO.THD_CUS_ADR is
'�������ͻ���ַ';

comment on column BBIP_EUPS.EUPS_AMOUNTINFO.CONT_NO is
'��ͬ����';

comment on column BBIP_EUPS.EUPS_AMOUNTINFO.CUS_AC is
'�ͻ��˺�';

comment on column BBIP_EUPS.EUPS_AMOUNTINFO.OWE_PRD is
'��������';

comment on column BBIP_EUPS.EUPS_AMOUNTINFO.OWE_FEE_AMT is
'Ƿ�ѽ��';

comment on column BBIP_EUPS.EUPS_AMOUNTINFO.PBD is
'ΥԼ��';

comment on column BBIP_EUPS.EUPS_AMOUNTINFO.CUR_BAL is
'��ǰ���';

comment on column BBIP_EUPS.EUPS_AMOUNTINFO.BAK_FLD1 is
'�����ֶ�1';

comment on column BBIP_EUPS.EUPS_AMOUNTINFO.BAK_FLD2 is
'�����ֶ�2';

comment on column BBIP_EUPS.EUPS_AMOUNTINFO.RSV_FLD1 is
'Ԥ���ֶ�1';

comment on column BBIP_EUPS.EUPS_AMOUNTINFO.RSV_FLD2 is
'Ԥ���ֶ�2';

comment on column BBIP_EUPS.EUPS_AMOUNTINFO.RSV_FLD3 is
'Ԥ���ֶ�3';

comment on column BBIP_EUPS.EUPS_AMOUNTINFO.RSV_FLD4 is
'Ԥ���ֶ�4';

comment on column BBIP_EUPS.EUPS_AMOUNTINFO.RSV_FLD5 is
'Ԥ���ֶ�5';

comment on column BBIP_EUPS.EUPS_AMOUNTINFO.RSV_FLD6 is
'Ԥ���ֶ�6';

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
'���ڴ洢���п��ŵ�ҵ�����ࡣ';

comment on column BBIP_EUPS.EUPS_BRANCH_BUSKND.BK is
'���к�';

comment on column BBIP_EUPS.EUPS_BRANCH_BUSKND.BUS_KND is
'ҵ������';

comment on column BBIP_EUPS.EUPS_BRANCH_BUSKND.USE_STS is
'ʹ��״̬��0������2ͣ��';

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
'��������ҵ�ɷѾ���Ľɷ����͡���ҵ�������Ƕ��һ�Ĺ�ϵ��';

comment on column BBIP_EUPS.EUPS_BUSTYPE.EUPS_BUS_TYP is
'EUPSҵ������';

comment on column BBIP_EUPS.EUPS_BUSTYPE.COM_NO is
'��λ���';

comment on column BBIP_EUPS.EUPS_BUSTYPE.BUS_KND is
'ҵ������';

comment on column BBIP_EUPS.EUPS_BUSTYPE.EUPS_BUS_TYP_NME is
'EUPSҵ����������';

comment on column BBIP_EUPS.EUPS_BUSTYPE.USE_STS is
'ʹ��״̬��0������2ͣ��';

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
'����������Ļ�����Ϣ';

comment on column BBIP_EUPS.EUPS_THD_BASE_INFO.COM_NO is
'��λ���';

comment on column BBIP_EUPS.EUPS_THD_BASE_INFO.COM_NME is
'��˾����';

comment on column BBIP_EUPS.EUPS_THD_BASE_INFO.BUS_KND is
'ҵ������';

comment on column BBIP_EUPS.EUPS_THD_BASE_INFO.EUPS_BUS_TYP is
'EUPSҵ������';

comment on column BBIP_EUPS.EUPS_THD_BASE_INFO.SIG_SET_TYP is
'ǩ��ǩ�����÷�ʽ';

comment on column BBIP_EUPS.EUPS_THD_BASE_INFO.SIG_TME is
'ǩ��ʱ��';

comment on column BBIP_EUPS.EUPS_THD_BASE_INFO.SIG_OUT_TME is
'ǩ��ʱ��';

comment on column BBIP_EUPS.EUPS_THD_BASE_INFO.TXN_TME_CTL1 is
'����ʱ�����1';

comment on column BBIP_EUPS.EUPS_THD_BASE_INFO.TXN_TME_CTL2 is
'����ʱ�����2';

comment on column BBIP_EUPS.EUPS_THD_BASE_INFO.TXN_DTE_CTL1 is
'�������ڿ���1';

comment on column BBIP_EUPS.EUPS_THD_BASE_INFO.TXN_DTE_CTL2 is
'�������ڿ���2';

comment on column BBIP_EUPS.EUPS_THD_BASE_INFO.TXN_DTE_CTL3 is
'�������ڿ���3';

comment on column BBIP_EUPS.EUPS_THD_BASE_INFO.CMT_FLG is
'��ͬ���룺�Ƿ�ͬ������ȫϵͳ Y-�ǣ�N-��';

comment on column BBIP_EUPS.EUPS_THD_BASE_INFO.BIL_VOU_FLG is
'Ʊ���ؿձ�־ ��Y-�ǣ�N-��;Ĭ��N

';

comment on column BBIP_EUPS.EUPS_THD_BASE_INFO.USE_STS is
'ʹ��״̬��0������2ͣ��';

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
'��λ���';

comment on column BBIP_EUPS.EUPS_THD_COMMCTL_INFO.EUPS_BUS_TYP is
'EUPSҵ������';

comment on column BBIP_EUPS.EUPS_THD_COMMCTL_INFO.CMU_MDE is
'ͨѶ��ʽ';

comment on column BBIP_EUPS.EUPS_THD_COMMCTL_INFO.THD_IP_ADR is
'IP��ַ';

comment on column BBIP_EUPS.EUPS_THD_COMMCTL_INFO.BID_POT is
'�󶨶˿�';

comment on column BBIP_EUPS.EUPS_THD_COMMCTL_INFO.CMU_UNM is
'ͨѶ�û�';

comment on column BBIP_EUPS.EUPS_THD_COMMCTL_INFO.CMU_PSW is
'ͨѶ����';

comment on column BBIP_EUPS.EUPS_THD_COMMCTL_INFO.TME_OUT is
'��ʱʱ��';

comment on column BBIP_EUPS.EUPS_THD_COMMCTL_INFO.CMU_STS is
'ͨѶ״̬';

comment on column BBIP_EUPS.EUPS_THD_COMMCTL_INFO.CMU_KEY is
'ͨѶ��Կ';

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
'��λ���';

comment on column BBIP_EUPS.EUPS_THD_FTP_CONFIG.EUPS_BUS_TYP is
'EUPSҵ������';

comment on column BBIP_EUPS.EUPS_THD_FTP_CONFIG.TYP is
'���ͣ�0�����ͣ�1������';

comment on column BBIP_EUPS.EUPS_THD_FTP_CONFIG.THD_IP_ADR is
'IP��ַ';

comment on column BBIP_EUPS.EUPS_THD_FTP_CONFIG.BID_POT is
'�󶨶˿�';

comment on column BBIP_EUPS.EUPS_THD_FTP_CONFIG.OPP_NME is
'�Է��û���';

comment on column BBIP_EUPS.EUPS_THD_FTP_CONFIG.OPP_USR_PSW is
'�Է��û�����';

comment on column BBIP_EUPS.EUPS_THD_FTP_CONFIG.LOC_FLE_NME is
'�����ļ���';

comment on column BBIP_EUPS.EUPS_THD_FTP_CONFIG.LOC_DIR is
'����·��';

comment on column BBIP_EUPS.EUPS_THD_FTP_CONFIG.RMT_FLE_NME is
'Զ���ļ���';

comment on column BBIP_EUPS.EUPS_THD_FTP_CONFIG.RMT_WAY is
'Զ��·��';

comment on column BBIP_EUPS.EUPS_THD_FTP_CONFIG.TME_OUT is
'��ʱʱ��';

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
'���������׿�����ϸ��Ϣ��';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_DETAIL.SQN is
'��ˮ��';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_DETAIL.TXN_DTE is
'��������';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_DETAIL.TXN_TME is
'����ʱ��';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_DETAIL.TXN_ORG_CDE is
'���׻�����';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_DETAIL.TXN_TLR is
'���׹�Ա';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_DETAIL.CHL_TYP is
'����������(0������;1���绰����;2������;3��ATM/CDM/CRS;4��POS;5����ý��;6���ֻ�;W��������ϵͳ)';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_DETAIL.TXN_STS is
'����״̬';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_DETAIL.TXN_CDE is
'������';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_DETAIL.TXN_TYP is
'�������ͣ�N.�������� C.�������� R.�ط�����';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_DETAIL.RTN_CDE is
'������';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_DETAIL.KEY_GEN_SDE is
'��Կ��������0-BBIP��1-������ϵͳ';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_DETAIL.ORG_CDE is
'������';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_DETAIL.MAN_KEY is
'����Կ';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_DETAIL.WRK_KEY is
'������Կ';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_DETAIL.SS_KEY is
'�Ự��Կ';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_DETAIL.KEY_CHE_VAL1 is
'��Կ����ֵ1';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_DETAIL.KEY_CHE_VAL2 is
'��Կ����ֵ2';

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
'������ʵʱ���׿�����Ϣ���磺ǩ����ǩ�ˡ�����';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_INFO.COM_NO is
'��λ���';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_INFO.EUPS_BUS_TYP is
'EUPSҵ������';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_INFO.TXN_DTE is
'��������';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_INFO.TXN_TME is
'����ʱ��';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_INFO.TXN_ORG_CDE is
'���׻�����';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_INFO.TXN_TLR is
'���׹�Ա';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_INFO.CHL_TYP is
'����������(0������;1���绰����;2������;3��ATM/CDM/CRS;4��POS;5����ý��;6���ֻ�;W��������ϵͳ)';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_INFO.TXN_STS is
'����״̬';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_INFO.TXN_TYP is
'�������ͣ�N.�������� C.�������� R.�ط�����';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_INFO.KEY_GEN_SDE is
'��Կ��������0-BBIP��1-������ϵͳ';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_INFO.ORG_CDE is
'������';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_INFO.MAN_KEY is
'����Կ';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_INFO.WRK_KEY is
'������Կ';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_INFO.SS_KEY is
'�Ự��Կ';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_INFO.KEY_CHE_VAL1 is
'��Կ����ֵ1';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_INFO.KEY_CHE_VAL2 is
'��Կ����ֵ2';

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
'���ɷ���ˮ��';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.SQN is
'��ˮ��';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.BK is
'���к�';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.BR is
'�����';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.ORG_CDE is
'������';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.CHL_TYP is
'����������(0������;1���绰����;2������;3��ATM/CDM/CRS;4��POS;5����ý��;6���ֻ�;W��������ϵͳ)';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.TML_NO is
'�ն˺�';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.TXN_TLR is
'���׹�Ա';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.ATH_TLR_NO is
'��Ȩ��Ա��';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.CHK_TLR is
'���˹�Ա��';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.BR_VCH_NO is
'������ˮ��';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.BUS_KND is
'ҵ������';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.EUPS_BUS_TYP is
'EUPSҵ������';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.RAP_TYP is
'�ո����ͣ�0-���գ�1-����';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.TXN_DTE is
'��������';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.TXN_TME is
'����ʱ��';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.TXN_CDE is
'������';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.TXN_STS is
'����״̬';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.RTN_CDE is
'������';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.MFM_TXN_CDE is
'����������';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.MFM_TXN_SUB_CDE is
'������������';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.AC_DTE is
'�������';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.MFM_VCH_NO is
'������ˮ��';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.MFM_JRN_NO is
'������־��';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.MFM_TXN_STS is
'��������״̬';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.MFM_RSP_CDE is
'������Ӧ��';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.COM_NO is
'��λ���';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.THD_TXN_CDE is
'������������';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.THD_AC_DTE is
'��������������';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.THD_SQN is
'��������ˮ��';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.THD_TXN_STS is
'����������״̬';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.THD_RSP_CDE is
'��������Ӧ��';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.OLD_PLF_SQN is
'ԭEUPS��ˮ��';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.RCN_FLG is
'Ҫ����˱�־';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.MFM_DYT_RCN_FLG is
'�������ռ���˱�־';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.MFM_EOD_RCN_FLG is
'���������ն��˱�־';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.MFM_RCN_BAT is
'��������������';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.MFM_RCN_TME is
'����������ʱ��';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.THD_RCN_FLG is
'����������˱�־';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.THD_RCN_TME is
'�����������ʱ��';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.THD_RCN_BAT is
'���������������';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.EXT_RCN_FLG is
'�ⲿ���˱�־��(0��δ����;1�����ڶ���;2���Ѷ���)';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.OUR_OTH_FLG is
'�����б�־��0-����;1-����';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.THD_RGN_NO is
'�������������';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.THD_CUS_NO is
'�������ͻ���ʶ';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.THD_CUS_NME is
'�������ͻ�����';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.PAY_CHL is
'֧��������0-����ϵͳ��1-ȫ��֧��ϵͳ��2-��������3-�ط������������б�־Ϊ��1-���С�ʱ���䣩';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.SPL_NO is
'�̻����';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.SPL_TER_NO is
'�̻��ն˺�';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.TXN_TYP is
'�������ͣ�N.�������� C.�������� R.�ط�����';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.CUS_AC is
'�ͻ��˺�';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.CUS_NME is
'�ͻ�����';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.ID_TYP is
'֤������';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.ID_NO is
'֤������';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.CMU_TEL is
'��ϵ�绰';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.ACC_TYP is
'�˻����ͣ�1--��ͨ,2--�˺�,4--����,
';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.ACC_SEQ is
'�˻�˳���';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.BV_KND is
'ƾ֤����';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.BV_NO is
'ƾ֤����';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.LIS_ACC is
'�����˺ţ�����λ�˺�/�ڲ��˺ţ�';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.CHO_NO is
'���˺�';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.RMK_CDE is
'ժҪ��';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.CSH_NO is
'�ֽ������:����-��121��;����-��252��';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.DRW_MDE is
'֧ȡ��ʽ���� 1 ƾ��֧ȡ 2 ƾӡ֧ȡ 3 ƾ֤֧ȡ 4 ����֧ȡ 5 ǩ��֧ȡ 6 ֧������ 7 ������ӡ 0 �����ܣ�';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.CCY is
'����';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.TXN_AMT is
'���׽��';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.REQ_TXN_AMT is
'�����׽��';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.FUL_DED_FLG is
'ȫ��ۿ��־��0-�����ֿۿ�;1-ȫ��ۿ�(Ĭ��)';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.HFE is
'������';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.HFE_ITM_SEQ is
'�����ѿ�Ŀ˳���';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.HFE_PST_BR is
'���������˻�����';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.PRT_FLG is
'��ӡ��־';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.PRT_CNT is
'��ӡ����';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.BAK_FLD1 is
'�����ֶ�1';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.BAK_FLD2 is
'�����ֶ�2';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.BAK_FLD3 is
'�����ֶ�3';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.BAK_FLD4 is
'�����ֶ�4';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.BAK_FLD5 is
'�����ֶ�5';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.BAK_FLD6 is
'�����ֶ�6';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.RSV_FLD1 is
'Ԥ���ֶ�1';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.RSV_FLD2 is
'Ԥ���ֶ�2';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.RSV_FLD3 is
'Ԥ���ֶ�3';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.RSV_FLD4 is
'Ԥ���ֶ�4';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.RSV_FLD5 is
'Ԥ���ֶ�5';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.RSV_FLD6 is
'Ԥ���ֶ�6';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.FIL_FLG is
'��չ��־��0����չ��1��������';

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
'������ˮ�����չ';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL_EXT.SQN is
'��ˮ��';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL_EXT.REC_NO is
'��¼��';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL_EXT.RSV_FLD1 is
'Ԥ���ֶ�1';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL_EXT.RSV_FLD2 is
'Ԥ���ֶ�2';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL_EXT.RSV_FLD3 is
'Ԥ���ֶ�3';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL_EXT.RSV_FLD4 is
'Ԥ���ֶ�4';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL_EXT.RSV_FLD5 is
'Ԥ���ֶ�5';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL_EXT.RSV_FLD6 is
'Ԥ���ֶ�6';

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
'�������Ʊ�';

comment on column BBIP_EUPS.EUPS_TRANS_ROLLBACK_CTL.SQN is
'��ˮ��';

comment on column BBIP_EUPS.EUPS_TRANS_ROLLBACK_CTL.RVS_DIR is
'��������0����������1������ҵ';

comment on column BBIP_EUPS.EUPS_TRANS_ROLLBACK_CTL.TXN_TME is
'����ʱ��';

comment on column BBIP_EUPS.EUPS_TRANS_ROLLBACK_CTL.TXN_DTE is
'��������';

comment on column BBIP_EUPS.EUPS_TRANS_ROLLBACK_CTL.PSS_CNT is
'�������';

comment on column BBIP_EUPS.EUPS_TRANS_ROLLBACK_CTL.MAX_PSS_CNT is
'��������';

comment on column BBIP_EUPS.EUPS_TRANS_ROLLBACK_CTL.RVS_STS is
'����״̬';

comment on column BBIP_EUPS.EUPS_TRANS_ROLLBACK_CTL.RVS_TXN_CDE is
'����������';

comment on column BBIP_EUPS.EUPS_TRANS_ROLLBACK_CTL.RVS_RET_CDE is
'����������';

