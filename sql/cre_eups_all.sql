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
'ϵͳ���';

comment on column BBIP_EUPS.EUPS_ACTI_INFO.EUPS_BUS_TYP is
'EUPSҵ������';

comment on column BBIP_EUPS.EUPS_ACTI_INFO.ACT_NO is
'����';

comment on column BBIP_EUPS.EUPS_ACTI_INFO.ACT_NME is
'�����';

comment on column BBIP_EUPS.EUPS_ACTI_INFO.ACT_IDU is
'�����';

comment on column BBIP_EUPS.EUPS_ACTI_INFO.ACT_SNM is
'����';

comment on column BBIP_EUPS.EUPS_ACTI_INFO.SDT is
'��ʼ����';

comment on column BBIP_EUPS.EUPS_ACTI_INFO.EDT is
'��������';

comment on column BBIP_EUPS.EUPS_ACTI_INFO.WEK_LST is
'�����б�';

comment on column BBIP_EUPS.EUPS_ACTI_INFO.CRE_DTE is
'��������';

comment on column BBIP_EUPS.EUPS_ACTI_INFO.CRE_TLR is
'������Ա';

comment on column BBIP_EUPS.EUPS_ACTI_INFO.MOD_DTE is
'�޸�����';

comment on column BBIP_EUPS.EUPS_ACTI_INFO.MOD_TLR is
'�޸Ĺ�Ա';

comment on column BBIP_EUPS.EUPS_ACTI_INFO.USE_STS is
'ʹ��״̬��0��������2��ͣ��';

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
'����Ϣ�������̻�';

comment on column BBIP_EUPS.EUPS_ACTI_MER_INFO.SYS_NO is
'ϵͳ���';

comment on column BBIP_EUPS.EUPS_ACTI_MER_INFO.EUPS_BUS_TYP is
'EUPSҵ������';

comment on column BBIP_EUPS.EUPS_ACTI_MER_INFO.ACT_NO is
'����';

comment on column BBIP_EUPS.EUPS_ACTI_MER_INFO.SPL_NO is
'�̻����';

comment on column BBIP_EUPS.EUPS_ACTI_MER_INFO.SDT is
'��ʼ����';

comment on column BBIP_EUPS.EUPS_ACTI_MER_INFO.EDT is
'��������';

comment on column BBIP_EUPS.EUPS_ACTI_MER_INFO.WEK_LST is
'�����б�';

comment on column BBIP_EUPS.EUPS_ACTI_MER_INFO.PEL_LIM is
'��Ա����';

comment on column BBIP_EUPS.EUPS_ACTI_MER_INFO.CRE_DTE is
'��������';

comment on column BBIP_EUPS.EUPS_ACTI_MER_INFO.CRE_TLR is
'������Ա';

comment on column BBIP_EUPS.EUPS_ACTI_MER_INFO.MOD_DTE is
'�޸�����';

comment on column BBIP_EUPS.EUPS_ACTI_MER_INFO.MOD_TLR is
'�޸Ĺ�Ա';

comment on column BBIP_EUPS.EUPS_ACTI_MER_INFO.USE_STS is
'ʹ��״̬��0��������2��ͣ��';

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
'����ۿۻ�������飬��������������Ϣ';

comment on column BBIP_EUPS.EUPS_ACTI_ORDER.ODE_NO is
'������';

comment on column BBIP_EUPS.EUPS_ACTI_ORDER.ACT_NO is
'����';

comment on column BBIP_EUPS.EUPS_ACTI_ORDER.ACT_DTE is
'�����';

comment on column BBIP_EUPS.EUPS_ACTI_ORDER.SPL_NO is
'�̻����';

comment on column BBIP_EUPS.EUPS_ACTI_ORDER.SPL_NME is
'�̻�����';

comment on column BBIP_EUPS.EUPS_ACTI_ORDER.REG_AC is
'�Ǽ��˺�';

comment on column BBIP_EUPS.EUPS_ACTI_ORDER.CSM_TME is
'����ʱ��';

comment on column BBIP_EUPS.EUPS_ACTI_ORDER.ICM_PRT_FLG is
'������ӡ��־';

comment on column BBIP_EUPS.EUPS_ACTI_ORDER.FUL_PRT_FLG is
'ȫ����ӡ��־';

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
'Ƿ����Ϣ��ʱ�����ڴ洢�ӵ�������ѯ���ķ�����Ϣ��ÿ������';

comment on column BBIP_EUPS.EUPS_AMOUNTINFO.SQN is
'��ˮ��';

comment on column BBIP_EUPS.EUPS_AMOUNTINFO.BK is
'���к�';

comment on column BBIP_EUPS.EUPS_AMOUNTINFO.BR is
'�����';

comment on column BBIP_EUPS.EUPS_AMOUNTINFO.TXN_TLR is
'���׹�Ա';

comment on column BBIP_EUPS.EUPS_AMOUNTINFO.TXN_CHL is
'��������(0������;1���绰����;2������;3��ATM/CDM/CRS;4��POS;5����ý��;6���ֻ�;W��������ϵͳ)';

comment on column BBIP_EUPS.EUPS_AMOUNTINFO.TXN_DTE is
'��������';

comment on column BBIP_EUPS.EUPS_AMOUNTINFO.TXN_TME is
'����ʱ��';

comment on column BBIP_EUPS.EUPS_AMOUNTINFO.TXN_CDE is
'������';

comment on column BBIP_EUPS.EUPS_AMOUNTINFO.RAP_TYP is
'�ո����� 0-���գ�1-����';

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
   SYS_NO               CHAR(20)               not null,
   BK                   CHAR(12)               not null,
   BUS_KND              CHAR(3)                not null,
   USE_STS              CHAR(1)                not null
      constraint C_USE_STS check (USE_STS in ('0','2')) default '0',
   constraint "P_BR_BUSKND_Idx1" primary key (SYS_NO),
   constraint "A_BR_BUSKND_Idx2" unique (BK, BUS_KND)
);

comment on table BBIP_EUPS.EUPS_BRANCH_BUSKND is
'���ڴ洢���п��ŵ�ҵ�����ࡣ';

comment on column BBIP_EUPS.EUPS_BRANCH_BUSKND.SYS_NO is
'ϵͳ���';

comment on column BBIP_EUPS.EUPS_BRANCH_BUSKND.BK is
'���к�';

comment on column BBIP_EUPS.EUPS_BRANCH_BUSKND.BUS_KND is
'ҵ������';

comment on column BBIP_EUPS.EUPS_BRANCH_BUSKND.USE_STS is
'ʹ��״̬��0��������2��ͣ��';

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
'��������ҵ�ɷѾ���Ľɷ����͡���ҵ�������Ƕ��һ�Ĺ�ϵ��';

comment on column BBIP_EUPS.EUPS_BUSTYPE.EUPS_BUS_TYP is
'EUPSҵ������';

comment on column BBIP_EUPS.EUPS_BUSTYPE.COM_NO is
'��λ���';

comment on column BBIP_EUPS.EUPS_BUSTYPE.BUS_KND is
'ҵ������';

comment on column BBIP_EUPS.EUPS_BUSTYPE.EUPS_BUS_TYP_NAME is
'EUPSҵ����������';

comment on column BBIP_EUPS.EUPS_BUSTYPE.USE_STS is
'ʹ��״̬��0��������2��ͣ��';

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
'�������Ʊ�������飬��������������Ϣ';

comment on column BBIP_EUPS.EUPS_BUS_TICKET_ORDER.ODE_NO is
'������';

comment on column BBIP_EUPS.EUPS_BUS_TICKET_ORDER.LEV_CIY is
'��������';

comment on column BBIP_EUPS.EUPS_BUS_TICKET_ORDER.REA_CIY is
'�ﵽ����';

comment on column BBIP_EUPS.EUPS_BUS_TICKET_ORDER.REF_NOM_AMT is
'�ο�Ʊ��';

comment on column BBIP_EUPS.EUPS_BUS_TICKET_ORDER.LEV_TME is
'����ʱ��';

comment on column BBIP_EUPS.EUPS_BUS_TICKET_ORDER.REA_TME is
'��վʱ��';

comment on column BBIP_EUPS.EUPS_BUS_TICKET_ORDER.RUN_TME is
'����ʱ��';

comment on column BBIP_EUPS.EUPS_BUS_TICKET_ORDER.CAR_TYP is
'��������';

comment on column BBIP_EUPS.EUPS_BUS_TICKET_ORDER.CAR_TIC_TYP is
'����Ʊ��λ����';

comment on column BBIP_EUPS.EUPS_BUS_TICKET_ORDER.CAR_TIC_NO is
'����Ʊ��λ����';

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
'��λ���';

comment on column BBIP_EUPS.EUPS_CORP_AGENT.BUS_KND is
'ҵ������';

comment on column BBIP_EUPS.EUPS_CORP_AGENT.EUPS_BUS_TYP is
'EUPSҵ������';

comment on column BBIP_EUPS.EUPS_CORP_AGENT.AGR_BOK_NO is
'Э�����';

comment on column BBIP_EUPS.EUPS_CORP_AGENT.CUS_MGR is
'���οͻ�����';

comment on column BBIP_EUPS.EUPS_CORP_AGENT.LIS_ACC is
'�����˺� ����λ�˺�/�ڲ��˺�';

comment on column BBIP_EUPS.EUPS_CORP_AGENT.BIL_VOU_FLG is
'Ʊ���ؿձ�־  Y-�ǣ�N-��;Ĭ��N
��Կ�Ƿ�ͬ������ȫϵͳ Y-�ǣ�N-��Ĭ��N
';

comment on column BBIP_EUPS.EUPS_CORP_AGENT.USE_STS is
'ʹ��״̬��0��������2��ͣ��';

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
'����Э����';

comment on column BBIP_EUPS.EUPS_CUS_AGENT.CUS_AC is
'�ͻ��˺�';

comment on column BBIP_EUPS.EUPS_CUS_AGENT.RLH_AGR_TPY is
'����Э������';

comment on column BBIP_EUPS.EUPS_CUS_AGENT.AGD_AGR_NO is
'����Э����';

comment on column BBIP_EUPS.EUPS_CUS_AGENT.CRE_DTE is
'��������';

comment on column BBIP_EUPS.EUPS_CUS_AGENT.AGR_STS is
'Э��״̬:0-���ã�1-ͣ��';

comment on column BBIP_EUPS.EUPS_CUS_AGENT.USE_STS is
'ʹ��״̬��0��������2��ͣ��';

comment on column BBIP_EUPS.EUPS_CUS_AGENT.CON_COL_DAY is
'��������';

comment on column BBIP_EUPS.EUPS_CUS_AGENT.AGD_DTE is
'��������';

comment on column BBIP_EUPS.EUPS_CUS_AGENT.AGD_AMT is
'���۽��';

comment on column BBIP_EUPS.EUPS_CUS_AGENT.CHL_CTL_WRD is
'����������';

comment on column BBIP_EUPS.EUPS_CUS_AGENT.BAK_FLD1 is
'�����ֶ�1';

comment on column BBIP_EUPS.EUPS_CUS_AGENT.BAK_FLD2 is
'�����ֶ�2';

comment on column BBIP_EUPS.EUPS_CUS_AGENT.BAK_FLD3 is
'�����ֶ�3';

comment on column BBIP_EUPS.EUPS_CUS_AGENT.BAK_FLD4 is
'�����ֶ�4';

comment on column BBIP_EUPS.EUPS_CUS_AGENT.RSV_FLD1 is
'Ԥ���ֶ�1';

comment on column BBIP_EUPS.EUPS_CUS_AGENT.RSV_FLD2 is
'Ԥ���ֶ�2';

comment on column BBIP_EUPS.EUPS_CUS_AGENT.RSV_FLD3 is
'Ԥ���ֶ�3';

comment on column BBIP_EUPS.EUPS_CUS_AGENT.RSV_FLD4 is
'Ԥ���ֶ�4';

comment on column BBIP_EUPS.EUPS_CUS_AGENT.RSV_FLD5 is
'Ԥ���ֶ�5';

comment on column BBIP_EUPS.EUPS_CUS_AGENT.RSV_FLD6 is
'Ԥ���ֶ�6';

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
'�����Ԥ���������飬��������������Ϣ';

comment on column BBIP_EUPS.EUPS_FIELD_ORDER.ODE_NO is
'������';

comment on column BBIP_EUPS.EUPS_FIELD_ORDER.VNE_TYP_NME is
'������������';

comment on column BBIP_EUPS.EUPS_FIELD_ORDER.VNE_CIY_NME is
'���س�������';

comment on column BBIP_EUPS.EUPS_FIELD_ORDER.VNE_NO is
'���ر��';

comment on column BBIP_EUPS.EUPS_FIELD_ORDER.VNE_NME is
'��������';

comment on column BBIP_EUPS.EUPS_FIELD_ORDER.VNE_ADR is
'���ص�ַ';

comment on column BBIP_EUPS.EUPS_FIELD_ORDER.EST_ARY_TME is
'Ԥ�Ƶ���ʱ��';

comment on column BBIP_EUPS.EUPS_FIELD_ORDER.EST_ARY_NUM is
'Ԥ�Ƶ�������';

comment on column BBIP_EUPS.EUPS_FIELD_ORDER.REG_CUS_INF is
'�Ǽǿͻ���Ϣ';

comment on column BBIP_EUPS.EUPS_FIELD_ORDER.FUL_PRT_FLG is
'ȫ����ӡ��־';

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
'ϵͳ���';

comment on column BBIP_EUPS.EUPS_INVOICE_EXT.SQN is
'��ˮ��';

comment on column BBIP_EUPS.EUPS_INVOICE_EXT.THD_RGN_NO is
'�������������';

comment on column BBIP_EUPS.EUPS_INVOICE_EXT.THD_CUS_NO is
'�������ͻ���ʶ';

comment on column BBIP_EUPS.EUPS_INVOICE_EXT.TXN_ORG_CDE is
'���׻�����';

comment on column BBIP_EUPS.EUPS_INVOICE_EXT.TXN_TLR is
'���׹�Ա';

comment on column BBIP_EUPS.EUPS_INVOICE_EXT.TXN_CHL is
'��������(0������;1���绰����;2������;3��ATM/CDM/CRS;4��POS;5����ý��;6���ֻ�;W��������ϵͳ)';

comment on column BBIP_EUPS.EUPS_INVOICE_EXT.BUS_KND is
'ҵ������';

comment on column BBIP_EUPS.EUPS_INVOICE_EXT.EUPS_BUS_TYP is
'EUPSҵ������';

comment on column BBIP_EUPS.EUPS_INVOICE_EXT.TXN_DTE is
'��������';

comment on column BBIP_EUPS.EUPS_INVOICE_EXT.TXN_TME is
'����ʱ��';

comment on column BBIP_EUPS.EUPS_INVOICE_EXT.COM_NO is
'��λ���';

comment on column BBIP_EUPS.EUPS_INVOICE_EXT.THD_CUS_AC_NO is
'�������ͻ��˻�����';

comment on column BBIP_EUPS.EUPS_INVOICE_EXT.IVO_CRW_NO is
'��Ʊ���ֺ�';

comment on column BBIP_EUPS.EUPS_INVOICE_EXT.IVO_NO is
'��Ʊ����';

comment on column BBIP_EUPS.EUPS_INVOICE_EXT.IVO_PRD is
'��Ʊ����';

comment on column BBIP_EUPS.EUPS_INVOICE_EXT.BAK_FLD1 is
'�����ֶ�1';

comment on column BBIP_EUPS.EUPS_INVOICE_EXT.BAK_FLD2 is
'�����ֶ�2';

comment on column BBIP_EUPS.EUPS_INVOICE_EXT.RSV_FLD1 is
'Ԥ���ֶ�1';

comment on column BBIP_EUPS.EUPS_INVOICE_EXT.RSV_FLD2 is
'Ԥ���ֶ�2';

comment on column BBIP_EUPS.EUPS_INVOICE_EXT.RSV_FLD3 is
'Ԥ���ֶ�3';

comment on column BBIP_EUPS.EUPS_INVOICE_EXT.RSV_FLD4 is
'Ԥ���ֶ�4';

comment on column BBIP_EUPS.EUPS_INVOICE_EXT.RSV_FLD5 is
'Ԥ���ֶ�5';

comment on column BBIP_EUPS.EUPS_INVOICE_EXT.RSV_FLD6 is
'Ԥ���ֶ�6';

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
'ϵͳ���';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.SQN is
'��ˮ��';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.IVO_PRD is
'��Ʊ����';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.BK is
'���к�';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.TXN_ORG_CDE is
'���׻�����';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.BR is
'�����';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.TML_NO is
'�ն˺�';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.TXN_TLR is
'���׹�Ա';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.CHL_TYP is
'�������ͣ�(0������;1���绰����;2������;3��ATM/CDM/CRS;4��POS;5����ý��;6���ֻ�;W��������ϵͳ)';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.BUS_KND is
'ҵ������';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.EUPS_BUS_TYP is
'EUPSҵ������';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.AC_DTE is
'�������';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.MFM_VCH_NO is
'�����ˮ��';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.TXN_CDE is
'������';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.TXN_DTE is
'��������';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.TXN_TME is
'����ʱ��';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.THD_TXN_CDE is
'������������';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.THD_TXN_DTE is
'��������������';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.THD_SQN is
'��������ˮ��';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.OLD_PFE_MFM_AC_DTE is
'ԭ�ɷ������������';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.OLD_PFE_MFM_VCH_NO is
'ԭ�ɷѻ����ˮ��';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.OLD_PFE_PLF_SQN is
'ԭ�ɷ�EUPS��ˮ��';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.COM_NO is
'��λ���';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.THD_RGN_NO is
'�������������';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.THD_CUS_NO is
'�������ͻ���ʶ';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.THD_CUS_NME is
'�������ͻ�����';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.THD_CUS_ADR is
'�������ͻ���ַ';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.THD_CUS_AC_NO is
'�������ͻ��˻�����';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.PAY_TYP is
'��������';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.PAY_MDE is
'֧����ʽ';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.THD_BIL_TYP is
'��������Ʊ����';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.IVO_CRW_NO is
'��Ʊ���ֺ�';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.IVO_NO is
'��Ʊ����';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.IVO_BEG_PRD is
'��Ʊ��ʼ����';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.IVO_END_PRD is
'��Ʊ��������';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.IVO_AMT is
'��Ʊ���';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.IVO_PRT_FLG is
'��Ʊ��ӡ��־';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.SUP_PRT_FLG is
'�Ƿ񲹴�';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.PRT_CNT is
'��ӡ����';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.IVO_STS is
'��Ʊ״̬';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.CMU_TEL is
'��ϵ�绰';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.BAK_FLD1 is
'�����ֶ�1';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.BAK_FLD2 is
'�����ֶ�2';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.RSV_FLD1 is
'Ԥ���ֶ�1';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.RSV_FLD2 is
'Ԥ���ֶ�2';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.RSV_FLD3 is
'Ԥ���ֶ�3';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.RSV_FLD4 is
'Ԥ���ֶ�4';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.FIL_FLG is
'��չ��־';

comment on column BBIP_EUPS.EUPS_INVOICE_INFO.ATH_TLR is
'��Ȩ��Ա';

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
'���ڼ�¼�ѷ�����';

comment on column BBIP_EUPS.EUPS_MSGSND_DETAIL.SYS_NO is
'ϵͳ���';

comment on column BBIP_EUPS.EUPS_MSGSND_DETAIL.SEQ is
'���';

comment on column BBIP_EUPS.EUPS_MSGSND_DETAIL.COM_NO is
'��λ���';

comment on column BBIP_EUPS.EUPS_MSGSND_DETAIL.BUS_KND is
'ҵ������';

comment on column BBIP_EUPS.EUPS_MSGSND_DETAIL.EUPS_BUS_TYP is
'EUPSҵ������';

comment on column BBIP_EUPS.EUPS_MSGSND_DETAIL.MOB_NUM is
'�ֻ�����';

comment on column BBIP_EUPS.EUPS_MSGSND_DETAIL.SMS_CTN is
'��������';

comment on column BBIP_EUPS.EUPS_MSGSND_DETAIL.SND_DTE is
'��������';

comment on column BBIP_EUPS.EUPS_MSGSND_DETAIL.SND_TME is
'����ʱ��';

comment on column BBIP_EUPS.EUPS_MSGSND_DETAIL.CRE_DTE is
'��������';

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
'���ڶ�ʱ�������ŷ��ͣ����ͺ�ת�Ƶ��ѷ����ż�¼��';

comment on column BBIP_EUPS.EUPS_MSG_STORE.SYS_NO is
'ϵͳ���';

comment on column BBIP_EUPS.EUPS_MSG_STORE.MOB_NUM is
'�ֻ�����';

comment on column BBIP_EUPS.EUPS_MSG_STORE.SMS_CTN is
'��������';

comment on column BBIP_EUPS.EUPS_MSG_STORE.SND_DTE is
'��������';

comment on column BBIP_EUPS.EUPS_MSG_STORE.SND_TME is
'����ʱ��';

comment on column BBIP_EUPS.EUPS_MSG_STORE.COM_NO is
'��λ���';

comment on column BBIP_EUPS.EUPS_MSG_STORE.BUS_KND is
'ҵ������';

comment on column BBIP_EUPS.EUPS_MSG_STORE.EUPS_BUS_TYP is
'EUPSҵ������';

comment on column BBIP_EUPS.EUPS_MSG_STORE.CRE_DTE is
'��������';

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
'ά������Ĵ���ҵ��������Ϣ';

comment on column BBIP_EUPS.EUPS_ORDER_BUSTYPE.SYS_NO is
'ϵͳ���';

comment on column BBIP_EUPS.EUPS_ORDER_BUSTYPE.EUPS_BUS_TYP is
'EUPSҵ������';

comment on column BBIP_EUPS.EUPS_ORDER_BUSTYPE.PUR_TYP_NUM is
'�������ͱ��';

comment on column BBIP_EUPS.EUPS_ORDER_BUSTYPE.BUS_SNM is
'ҵ����';

comment on column BBIP_EUPS.EUPS_ORDER_BUSTYPE.BUS_KND is
'ҵ������';

comment on column BBIP_EUPS.EUPS_ORDER_BUSTYPE.BUS_RMK is
'ҵ��˵��';

comment on column BBIP_EUPS.EUPS_ORDER_BUSTYPE.SMS_FLG is
'���ű�־';

comment on column BBIP_EUPS.EUPS_ORDER_BUSTYPE.CRE_TLR is
'������Ա';

comment on column BBIP_EUPS.EUPS_ORDER_BUSTYPE.CRE_DTE is
'��������';

comment on column BBIP_EUPS.EUPS_ORDER_BUSTYPE.MOD_TLR is
'�޸Ĺ�Ա';

comment on column BBIP_EUPS.EUPS_ORDER_BUSTYPE.MOD_DTE is
'�޸�����';

comment on column BBIP_EUPS.EUPS_ORDER_BUSTYPE.USE_STS is
'ʹ��״̬��0��������2��ͣ��';

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
'���涩����������';

comment on column BBIP_EUPS.EUPS_ORDER_INFO.SQN is
'��ˮ��';

comment on column BBIP_EUPS.EUPS_ORDER_INFO.ODE_NO is
'������';

comment on column BBIP_EUPS.EUPS_ORDER_INFO.EUPS_BUS_TYP is
'EUPSҵ������';

comment on column BBIP_EUPS.EUPS_ORDER_INFO.PUR_TYP_NUM is
'�������ͱ��';

comment on column BBIP_EUPS.EUPS_ORDER_INFO.SPL_TER_NO is
'�̻��ն˺�';

comment on column BBIP_EUPS.EUPS_ORDER_INFO.SPL_NO is
'�̻����';

comment on column BBIP_EUPS.EUPS_ORDER_INFO.ACT_NO is
'����';

comment on column BBIP_EUPS.EUPS_ORDER_INFO.CUS_AC is
'�ͻ��˺�';

comment on column BBIP_EUPS.EUPS_ORDER_INFO.CUS_NME is
'�ͻ�����';

comment on column BBIP_EUPS.EUPS_ORDER_INFO.ID_NO is
'֤������';

comment on column BBIP_EUPS.EUPS_ORDER_INFO.ID_TYP is
'֤������';

comment on column BBIP_EUPS.EUPS_ORDER_INFO.ODE_AMT is
'�������';

comment on column BBIP_EUPS.EUPS_ORDER_INFO.TXN_TME is
'����ʱ��';

comment on column BBIP_EUPS.EUPS_ORDER_INFO.CRE_DTE is
'��������';

comment on column BBIP_EUPS.EUPS_ORDER_INFO.CRE_TME is
'����ʱ��';

comment on column BBIP_EUPS.EUPS_ORDER_INFO.MOD_DTE is
'�޸�����';

comment on column BBIP_EUPS.EUPS_ORDER_INFO.MOD_TME is
'�޸�ʱ��';

comment on column BBIP_EUPS.EUPS_ORDER_INFO.USE_DTE is
'ʹ������';

comment on column BBIP_EUPS.EUPS_ORDER_INFO.USE_TME is
'ʹ��ʱ��';

comment on column BBIP_EUPS.EUPS_ORDER_INFO.CMU_TEL is
'��ϵ�绰';

comment on column BBIP_EUPS.EUPS_ORDER_INFO.CMU_ADR is
'��ϵ��ַ';

comment on column BBIP_EUPS.EUPS_ORDER_INFO.EML is
'��������';

comment on column BBIP_EUPS.EUPS_ORDER_INFO.ODE_NUM is
'��������';

comment on column BBIP_EUPS.EUPS_ORDER_INFO.ODE_STS is
'����״̬';

comment on column BBIP_EUPS.EUPS_ORDER_INFO.FIL_FLG is
'��չ��־';

comment on column BBIP_EUPS.EUPS_ORDER_INFO.FIL_TAB is
'��չ����';

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
'��������̻���Ϣ';

comment on column BBIP_EUPS.EUPS_ORDER_MER.SYS_NO is
'ϵͳ���';

comment on column BBIP_EUPS.EUPS_ORDER_MER.EUPS_BUS_TYP is
'EUPSҵ������';

comment on column BBIP_EUPS.EUPS_ORDER_MER.SPL_NO is
'�̻����';

comment on column BBIP_EUPS.EUPS_ORDER_MER.SPL_NME is
'�̻�����';

comment on column BBIP_EUPS.EUPS_ORDER_MER.LIS_ACC is
'�����˺� ����λ�˺�/�ڲ��˺�';

comment on column BBIP_EUPS.EUPS_ORDER_MER.LIS_ACC_NME is
'�����˺Ż���';

comment on column BBIP_EUPS.EUPS_ORDER_MER.CRE_DTE is
'��������';

comment on column BBIP_EUPS.EUPS_ORDER_MER.CRE_TLR is
'������Ա';

comment on column BBIP_EUPS.EUPS_ORDER_MER.MOD_DTE is
'�޸�����';

comment on column BBIP_EUPS.EUPS_ORDER_MER.MOD_TLR is
'�޸Ĺ�Ա';

comment on column BBIP_EUPS.EUPS_ORDER_MER.USE_STS is
'ʹ��״̬��0��������2��ͣ��';

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
'���������������ն�';

comment on column BBIP_EUPS.EUPS_ORDER_TERM_INFO.SYS_NO is
'ϵͳ���';

comment on column BBIP_EUPS.EUPS_ORDER_TERM_INFO.EUPS_BUS_TYP is
'EUPSҵ������';

comment on column BBIP_EUPS.EUPS_ORDER_TERM_INFO.THD_RGN_NO is
'�������������';

comment on column BBIP_EUPS.EUPS_ORDER_TERM_INFO.SPL_TER_NO is
'�̻��ն˺�';

comment on column BBIP_EUPS.EUPS_ORDER_TERM_INFO.TML_NME is
'�ն�����';

comment on column BBIP_EUPS.EUPS_ORDER_TERM_INFO.CRE_DTE is
'��������';

comment on column BBIP_EUPS.EUPS_ORDER_TERM_INFO.CRE_TLR is
'������Ա';

comment on column BBIP_EUPS.EUPS_ORDER_TERM_INFO.MOD_DTE is
'�޸�����';

comment on column BBIP_EUPS.EUPS_ORDER_TERM_INFO.MOD_TLR is
'�޸Ĺ�Ա';

comment on column BBIP_EUPS.EUPS_ORDER_TERM_INFO.USE_STS is
'ʹ��״̬��0��������2��ͣ��';

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
'������������ͣ��磺00���ɻ�Ʊ;01����Ʊ;02������Ʊ;10����Ԥ��;11���ۿۻ;12������';

comment on column BBIP_EUPS.EUPS_ORDER_TYPE.SYS_NO is
'ϵͳ���';

comment on column BBIP_EUPS.EUPS_ORDER_TYPE.PUR_TYP_NUM is
'�������ͱ��';

comment on column BBIP_EUPS.EUPS_ORDER_TYPE.PUR_TYP_NME is
'������������';

comment on column BBIP_EUPS.EUPS_ORDER_TYPE.USE_STS is
'ʹ��״̬��0��������2��ͣ��';

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
'����Ա������Ա��ϵͳʹ���û�';

comment on column BBIP_EUPS.EUPS_ORDER_USER_INFO.SYS_NO is
'ϵͳ���';

comment on column BBIP_EUPS.EUPS_ORDER_USER_INFO.EUPS_BUS_TYP is
'EUPSҵ������';

comment on column BBIP_EUPS.EUPS_ORDER_USER_INFO.THD_RGN_NO is
'�������������';

comment on column BBIP_EUPS.EUPS_ORDER_USER_INFO.USR_NUM is
'�û����';

comment on column BBIP_EUPS.EUPS_ORDER_USER_INFO.USR_NME is
'�û�����';

comment on column BBIP_EUPS.EUPS_ORDER_USER_INFO.COM_NME is
'��˾����';

comment on column BBIP_EUPS.EUPS_ORDER_USER_INFO.TEL is
'�绰����';

comment on column BBIP_EUPS.EUPS_ORDER_USER_INFO.PSW is
'�û�����';

comment on column BBIP_EUPS.EUPS_ORDER_USER_INFO.CRE_DTE is
'��������';

comment on column BBIP_EUPS.EUPS_ORDER_USER_INFO.CRE_TLR is
'������Ա';

comment on column BBIP_EUPS.EUPS_ORDER_USER_INFO.MOD_DTE is
'�޸�����';

comment on column BBIP_EUPS.EUPS_ORDER_USER_INFO.MOD_TLR is
'�޸Ĺ�Ա';

comment on column BBIP_EUPS.EUPS_ORDER_USER_INFO.USE_STS is
'ʹ��״̬��0��������2��ͣ��';

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
'��Ż�Ʊ�������飬��������������Ϣ';

comment on column BBIP_EUPS.EUPS_PLANE_TICKET_ORDER.ODE_NO is
'������';

comment on column BBIP_EUPS.EUPS_PLANE_TICKET_ORDER.AIN_COM is
'���չ�˾';

comment on column BBIP_EUPS.EUPS_PLANE_TICKET_ORDER.SCH_FLY_NO is
'�����';

comment on column BBIP_EUPS.EUPS_PLANE_TICKET_ORDER.LEV_CIY is
'��������';

comment on column BBIP_EUPS.EUPS_PLANE_TICKET_ORDER.REA_CIY is
'�ﵽ����';

comment on column BBIP_EUPS.EUPS_PLANE_TICKET_ORDER.TRS_CIY is
'��ת����';

comment on column BBIP_EUPS.EUPS_PLANE_TICKET_ORDER.ELE_TIK_NO is
'���ӿ�Ʊ��';

comment on column BBIP_EUPS.EUPS_PLANE_TICKET_ORDER.FLI_TIC_LVL is
'��Ʊ��Ʊ����';

comment on column BBIP_EUPS.EUPS_PLANE_TICKET_ORDER.VLD_DTE is
'��Ч����';

comment on column BBIP_EUPS.EUPS_PLANE_TICKET_ORDER.END_DTE is
'��ֹ����';

comment on column BBIP_EUPS.EUPS_PLANE_TICKET_ORDER.CHA_CDE is
'��֤��';

comment on column BBIP_EUPS.EUPS_PLANE_TICKET_ORDER.ALE_INF is
'��ʾ��Ϣ';

comment on column BBIP_EUPS.EUPS_PLANE_TICKET_ORDER.FLY_TME is
'���ʱ��';

comment on column BBIP_EUPS.EUPS_PLANE_TICKET_ORDER.FUL_PRT_FLG is
'ȫ����ӡ��־';

comment on column BBIP_EUPS.EUPS_PLANE_TICKET_ORDER.ICM_PRT_FLG is
'������ӡ��־';

comment on column BBIP_EUPS.EUPS_PLANE_TICKET_ORDER.REF_NOM_AMT is
'�ο�Ʊ��';

comment on column BBIP_EUPS.EUPS_PLANE_TICKET_ORDER.AIR_BLD_AMT is
'���������';

comment on column BBIP_EUPS.EUPS_PLANE_TICKET_ORDER.FUE_SUR_AMT is
'ȼ�͸��ӷ�';

comment on column BBIP_EUPS.EUPS_PLANE_TICKET_ORDER.PAY_PRM is
'���շ�';

comment on column BBIP_EUPS.EUPS_PLANE_TICKET_ORDER.DRA_UNT is
'��Ʊ��λ';

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
'���������Ʊ�������飬��������������Ϣ';

comment on column BBIP_EUPS.EUPS_SPORTS_LOTTERY_ORDER.ODE_NO is
'������';

comment on column BBIP_EUPS.EUPS_SPORTS_LOTTERY_ORDER.ACT_NO is
'����';

comment on column BBIP_EUPS.EUPS_SPORTS_LOTTERY_ORDER.AWA_PER_NO is
'���ڱ��';

comment on column BBIP_EUPS.EUPS_SPORTS_LOTTERY_ORDER.PCG_TYP is
'�ײ�����';

comment on column BBIP_EUPS.EUPS_SPORTS_LOTTERY_ORDER.OTL_NUM_MDE is
'ѡ�ŷ�ʽ';

comment on column BBIP_EUPS.EUPS_SPORTS_LOTTERY_ORDER.LTY_NUM is
'����ע��';

comment on column BBIP_EUPS.EUPS_SPORTS_LOTTERY_ORDER.BET_NO is
'Ͷע����';

comment on column BBIP_EUPS.EUPS_SPORTS_LOTTERY_ORDER.BET_MTP is
'Ͷע����';

comment on column BBIP_EUPS.EUPS_SPORTS_LOTTERY_ORDER.ATO_IVN_TRM is
'��Ͷ����';

comment on column BBIP_EUPS.EUPS_SPORTS_LOTTERY_ORDER.ATO_IVN_AMT is
'��Ͷ���';

comment on column BBIP_EUPS.EUPS_SPORTS_LOTTERY_ORDER.PGM_STS is
'����״̬';

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
'ϵͳ���';

comment on column BBIP_EUPS.EUPS_THD_AREA_INFO.COM_NO is
'��λ���';

comment on column BBIP_EUPS.EUPS_THD_AREA_INFO.EUPS_BUS_TYP is
'EUPSҵ������';

comment on column BBIP_EUPS.EUPS_THD_AREA_INFO.THD_RGN_NO is
'�������������';

comment on column BBIP_EUPS.EUPS_THD_AREA_INFO.THD_RGN_NME is
'��������������';

comment on column BBIP_EUPS.EUPS_THD_AREA_INFO.USE_STS is
'ʹ��״̬��0��������2��ͣ��';

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

comment on column BBIP_EUPS.EUPS_THD_BASE_INFO.THD_RCN_TME is
'�����������ʱ��';

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
'ͬ����ʶ:�Ƿ�ͬ������ȫϵͳ Y-�ǣ�N-��';

comment on column BBIP_EUPS.EUPS_THD_BASE_INFO.BIL_VOU_FLG is
'Ʊ���ؿձ�־  Y-�ǣ�N-��;Ĭ��N
��Կ�Ƿ�ͬ������ȫϵͳ Y-�ǣ�N-��Ĭ��N
';

comment on column BBIP_EUPS.EUPS_THD_BASE_INFO.CMU_MDE is
'ͨѶ��ʽ';

comment on column BBIP_EUPS.EUPS_THD_BASE_INFO.THD_IP_ADR is
'IP��ַ';

comment on column BBIP_EUPS.EUPS_THD_BASE_INFO.BID_POT is
'�󶨶˿�';

comment on column BBIP_EUPS.EUPS_THD_BASE_INFO.CMU_UNM is
'ͨѶ�û�';

comment on column BBIP_EUPS.EUPS_THD_BASE_INFO.CMU_PSW is
'ͨѶ����';

comment on column BBIP_EUPS.EUPS_THD_BASE_INFO.TME_OUT is
'��ʱʱ��';

comment on column BBIP_EUPS.EUPS_THD_BASE_INFO.CMU_STS is
'ͨѶ״̬';

comment on column BBIP_EUPS.EUPS_THD_BASE_INFO.CMU_KEY is
'ͨѶ��Կ';

comment on column BBIP_EUPS.EUPS_THD_BASE_INFO.USE_STS is
'ʹ��״̬��0��������2��ͣ��';

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
'��λ���';

comment on column BBIP_EUPS.EUPS_THD_FTP_CONFIG.EUPS_BUS_TYP is
'EUPSҵ������';

comment on column BBIP_EUPS.EUPS_THD_FTP_CONFIG.TYP is
'����  0�����ͣ�1������';

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

comment on column BBIP_EUPS.EUPS_THD_FTP_CONFIG.USE_STS is
'ʹ��״̬��0��������2��ͣ��';

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
'�������ͣ�(0������;1���绰����;2������;3��ATM/CDM/CRS;4��POS;5����ý��;6���ֻ�;W��������ϵͳ)';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_DETAIL.TXN_STS is
'����״̬';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_DETAIL.TXN_CDE is
'������';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_DETAIL.TXN_CTL_TYP is
'0��ǩ����1��ǩ�ˣ�2�����ڶ����ˣ�3�����ڶ���ϸ�ʣ�4�������������ˣ�5������������ϸ�ˣ�6���������ļ����ˣ�7�������ļ�����';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_DETAIL.COM_NO is
'��λ���';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_DETAIL.BUS_KND is
'ҵ������';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_DETAIL.EUPS_BUS_TYP is
'EUPSҵ������';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_DETAIL.RCN_BAT is
'��������';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_DETAIL.RCN_DTE is
'��������';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_DETAIL.THD_TXN_DTE is
'��������������';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_DETAIL.THD_TXN_STS is
'����������״̬';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_DETAIL.THD_RSP_CDE is
'��������Ӧ��';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_DETAIL.RTN_CDE is
'������';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_DETAIL.KEY_GEN_SDE is
'��Կ������:0-BBIP��1-������ϵͳ';

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

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_DETAIL.BAK_FLD1 is
'�����ֶ�1';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_DETAIL.BAK_FLD2 is
'�����ֶ�2';

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
'������ʵʱ���׿�����Ϣ���磺ǩ����ǩ�ˡ�����';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_INFO.COM_NO is
'��λ���';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_INFO.BUS_KND is
'ҵ������';

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
'�������ͣ�(0������;1���绰����;2������;3��ATM/CDM/CRS;4��POS;5����ý��;6���ֻ�;W��������ϵͳ)';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_INFO.TXN_CTL_STS is
'0��ǩ����1��ǩ�ˣ�2�������У�3���������';

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_INFO.KEY_GEN_SDE is
'��Կ������:0-BBIP��1-������ϵͳ';

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

comment on column BBIP_EUPS.EUPS_THD_TRANCTL_INFO.USE_STS is
'ʹ��״̬��0��������2��ͣ��';

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
'��Ż�Ʊ�������飬��������������Ϣ';

comment on column BBIP_EUPS.EUPS_TRAIN_TICKET_ORDER.ODE_NO is
'������';

comment on column BBIP_EUPS.EUPS_TRAIN_TICKET_ORDER.TRA_TYP is
'�г�����';

comment on column BBIP_EUPS.EUPS_TRAIN_TICKET_ORDER.TRA_TIC_LVL is
'��Ʊ��λ�ȼ�';

comment on column BBIP_EUPS.EUPS_TRAIN_TICKET_ORDER.TRA_TIC_TYP is
'��Ʊ��λ����';

comment on column BBIP_EUPS.EUPS_TRAIN_TICKET_ORDER.TRA_TIC_NO is
'��Ʊ��λ����';

comment on column BBIP_EUPS.EUPS_TRAIN_TICKET_ORDER.LEV_CIY is
'��������';

comment on column BBIP_EUPS.EUPS_TRAIN_TICKET_ORDER.REA_CIY is
'�ﵽ����';

comment on column BBIP_EUPS.EUPS_TRAIN_TICKET_ORDER.TRS_CIY is
'��ת����';

comment on column BBIP_EUPS.EUPS_TRAIN_TICKET_ORDER.ICM_PRT_FLG is
'������ӡ��־';

comment on column BBIP_EUPS.EUPS_TRAIN_TICKET_ORDER.FUL_PRT_FLG is
'ȫ����ӡ��־';

comment on column BBIP_EUPS.EUPS_TRAIN_TICKET_ORDER.REF_NOM_AMT is
'�ο�Ʊ��';

comment on column BBIP_EUPS.EUPS_TRAIN_TICKET_ORDER.LEV_TME is
'����ʱ��';

comment on column BBIP_EUPS.EUPS_TRAIN_TICKET_ORDER.REA_TME is
'��վʱ��';

comment on column BBIP_EUPS.EUPS_TRAIN_TICKET_ORDER.RUN_TME is
'����ʱ��';

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
'�������ͣ�(0������;1���绰����;2������;3��ATM/CDM/CRS;4��POS;5����ý��;6���ֻ�;W��������ϵͳ)';

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
'�ո����� 0-���գ�1-����';

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
'�����ˮ��';

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
'�ⲿ���˱�־(0��δ����;1�����ڶ���;2���Ѷ���)';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.OUR_OTH_FLG is
'�����б�־ 0-����;1-����';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.THD_RGN_NO is
'�������������';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.THD_CUS_NO is
'�������ͻ���ʶ';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.THD_CUS_NME is
'�������ͻ�����';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.PAY_CHL is
'֧������ 0-����ϵͳ��1-ȫ��֧��ϵͳ��2-��������3-�ط������������б�־Ϊ��1-���С�ʱ���䡣';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.SPL_NO is
'�̻����';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.SPL_TER_NO is
'�̻��ն˺�';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.TXN_TYP is
'��������:N.�������� C.�������� R.�ط�����';

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
'�˻�����  1--��ͨ,2--�˺�,4--����,
';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.ACC_SEQ is
'�˻�˳���';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.BV_KND is
'ƾ֤����';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.BV_NO is
'ƾ֤����';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.LIS_ACC is
'�����˺� ����λ�˺�/�ڲ��˺�';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.CHO_NO is
'���˺�';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.RMK_CDE is
'ժҪ��';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.CSH_NO is
'�ֽ������ ����-��121��;����-��252��';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.DRW_MDE is
'֧ȡ��ʽ  1 ƾ��֧ȡ 2 ƾӡ֧ȡ 3 ƾ֤֧ȡ 4 ����֧ȡ 5 ǩ��֧ȡ 6 ֧������ 7 ������ӡ 0 ������';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.CCY is
'����';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.TXN_AMT is
'���׽��';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.REQ_TXN_AMT is
'�����׽��';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.FUL_DED_FLG is
'ȫ��ۿ��־:0-�����ֿۿ�;1-ȫ��ۿ�(Ĭ��)';

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

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.RSV_FLD7 is
'Ԥ���ֶ�5';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.RSV_FLD8 is
'Ԥ���ֶ�6';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL.FIL_FLG is
'��չ��־';

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
'������ˮ�����չ';

comment on column BBIP_EUPS.EUPS_TRANS_JOURNAL_EXT.SYS_NO is
'ϵͳ���';

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
'�������Ʊ�';

comment on column BBIP_EUPS.EUPS_TRANS_ROLLBACK_CTL.SYS_NO is
'ϵͳ���';

comment on column BBIP_EUPS.EUPS_TRANS_ROLLBACK_CTL.SQN is
'��ˮ��';

comment on column BBIP_EUPS.EUPS_TRANS_ROLLBACK_CTL.RVS_DIR is
'��������:0����������1������ҵ';

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
'����Э������';

comment on column BBIP_EUPS.EUPS_TYPE_MAP.TAB_NME is
'����';

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
'��Ÿ�����Ʊ�������飬��������������Ϣ';

comment on column BBIP_EUPS.EUPS_WELFARE_LOTTERY_ORDER.ODE_NO is
'������';

comment on column BBIP_EUPS.EUPS_WELFARE_LOTTERY_ORDER.ACT_NO is
'����';

comment on column BBIP_EUPS.EUPS_WELFARE_LOTTERY_ORDER.PCG_TYP is
'�ײ�����';

comment on column BBIP_EUPS.EUPS_WELFARE_LOTTERY_ORDER.OTL_NUM_MDE is
'ѡ�ŷ�ʽ';

comment on column BBIP_EUPS.EUPS_WELFARE_LOTTERY_ORDER.LTY_NUM is
'����ע��';

comment on column BBIP_EUPS.EUPS_WELFARE_LOTTERY_ORDER.BET_NO is
'Ͷע����';

comment on column BBIP_EUPS.EUPS_WELFARE_LOTTERY_ORDER.BET_MTP is
'Ͷע����';

comment on column BBIP_EUPS.EUPS_WELFARE_LOTTERY_ORDER.ATO_IVN_TRM is
'��Ͷ����';

comment on column BBIP_EUPS.EUPS_WELFARE_LOTTERY_ORDER.ATO_IVN_AMT is
'��Ͷ���';

comment on column BBIP_EUPS.EUPS_WELFARE_LOTTERY_ORDER.PGM_STS is
'����״̬';

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
'������˻��ǼǱ�';

comment on column BBIP_EUPS.EUPS_ZERO_ACT_INFO.CUS_AC is
'�ͻ��˺�';

comment on column BBIP_EUPS.EUPS_ZERO_ACT_INFO.STS is
'״̬ 1����0ɾ��';

comment on column BBIP_EUPS.EUPS_ZERO_ACT_INFO.OBK_BR is
'��������';

comment on column BBIP_EUPS.EUPS_ZERO_ACT_INFO.CUS_NME is
'�ͻ�����';

comment on column BBIP_EUPS.EUPS_ZERO_ACT_INFO.SMS_RCV_NO is
'���Ž��պ���';

comment on column BBIP_EUPS.EUPS_ZERO_ACT_INFO.MOD_DTE is
'�޸�����';

comment on column BBIP_EUPS.EUPS_ZERO_ACT_INFO.MOD_TLR is
'�޸Ĺ�Ա';

comment on column BBIP_EUPS.EUPS_ZERO_ACT_INFO.DEL_DTE is
'ɾ������';

comment on column BBIP_EUPS.EUPS_ZERO_ACT_INFO.DEL_TLR is
'ɾ����Ա';

comment on column BBIP_EUPS.EUPS_ZERO_ACT_INFO.CRE_DTE is
'��������';

comment on column BBIP_EUPS.EUPS_ZERO_ACT_INFO.CRE_TLR is
'������Ա';

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
'�������ˮ��';

comment on column BBIP_EUPS.EUPS_ZERO_JOURNAL.SQN is
'��ˮ��';

comment on column BBIP_EUPS.EUPS_ZERO_JOURNAL.BUS_KND is
'ҵ������';

comment on column BBIP_EUPS.EUPS_ZERO_JOURNAL.EUPS_BUS_TYP is
'EUPSҵ������';

comment on column BBIP_EUPS.EUPS_ZERO_JOURNAL.TXN_DTE is
'��������';

comment on column BBIP_EUPS.EUPS_ZERO_JOURNAL.TXN_TME is
'����ʱ��';

comment on column BBIP_EUPS.EUPS_ZERO_JOURNAL.DSK_SEQ_NUM is
'���̶�Ӧ���к�';

comment on column BBIP_EUPS.EUPS_ZERO_JOURNAL.CUS_AC is
'�ͻ��˺�';

comment on column BBIP_EUPS.EUPS_ZERO_JOURNAL.ORI_AC is
'�������˺�';

comment on column BBIP_EUPS.EUPS_ZERO_JOURNAL.ORI_NME is
'�����˻���';

comment on column BBIP_EUPS.EUPS_ZERO_JOURNAL.TXN_AMT is
'���׽��';

comment on column BBIP_EUPS.EUPS_ZERO_JOURNAL.PST_AC_STS is
'����״̬:UԤ���� A���ʴ�ȷ�� S������ȷ��';

comment on column BBIP_EUPS.EUPS_ZERO_JOURNAL.EC_STS is
'Ĩ��״̬ O��Ĩ�� BĨ�ʴ�ȷ��  XĨ����ȷ��';

