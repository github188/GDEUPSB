drop table  gdeupsb.GDS_AGT_TEL;   --�㶫���߹㲥��������ɷ����޹�˾
create table gdeupsb.GDS_AGT_TEL
(
  Sub_Sts   char(1)   default '0',
--״̬( 0-��Ч 1-��Ч )
  LAgt_St   char(1)   default 'U',
--����Э��״̬( U-δУ�� C-��� F-У��ʧ�� S-У��ɹ� )
  TAgt_St   char(1)   default 'U',
--������Э��״̬( U-δУ�� C-��� F-У��ʧ�� S-У��ɹ� )
  Txn_Cnl   char(3)   not null,
--ǩԼ;��( TRM-��̨ WEB-��վ ... )
  Gds_BId   char(5)   not null,
--����ҵ��ID
  Act_No    char(21)  not null,
--Э��ҵ�����˺�
  Gds_AId   char(55)  not null,
--Э���
  Bnk_Typ   char(2)   default '',
--��������
  Bnk_No    char(12)  default '',
--�����к�
  Bnk_Nam   char(90)  default '',
--��������
  Org_Cod   char(12)  default '',
--��λ����
  Org_Nam   char(90)  default '',
--��λ����
  TBus_Tp   char(5)   default '',
--ҵ������
  TCus_Id   char(40)  not null,
--�ͻ���ʶ
  TCus_Nm   char(90)  default '',
--�ͻ���ʶ
  Eff_Dat   char(8)   default '',
--��Ч����
  Ivd_Dat   char(8)   default '',
--ʧЧ����
  LEr_Msg   char(90)  default '',
--����Э����ʾ
  TEr_Msg   char(90)  default '',
--������Э����ʾ
  Area_Id  char(5),
--��������
  batch_Id  char(12),
--���κ�
  usb_flg  char(1) default 'Y',
--�Ƿ�����̱�־
  PRIMARY KEY( Gds_BId, Act_No, Gds_AId, TCus_Id )
) IN "BBIP_APP"
INDEX IN  "BBIP_APP_INDEX";


--�㶫�齭����Э���ӱ�
drop table  gdeupsb.GDS_AGT_DEG;
create table gdeupsb.GDS_AGT_DEG
(
  Sub_Sts   char(1)   default '0',
--״̬( 0-��Ч 1-��Ч )
  LAgt_St   char(1)   default 'U',
--����Э��״̬( U-δУ�� C-��� F-У��ʧ�� S-У��ɹ� )
  TAgt_St   char(1)   default 'U',
--������Э��״̬( U-δУ�� C-��� F-У��ʧ�� S-У��ɹ� )
  Txn_Cnl   char(3)   not null,
--ǩԼ;��( TRM-��̨ WEB-��վ ... )
  Gds_BId   char(5)   not null,
--����ҵ��ID
  Act_No    char(21)  not null,
--Э��ҵ�����˺�
  Gds_AId   char(55)  not null,
--Э���
  Bnk_Typ   char(2)   default '',
--��������
  Bnk_No    char(12)  default '',
--�����к�
  Bnk_Nam   char(90)  default '',
--��������
  Org_Cod   char(12)  default '',
--��λ����
  Org_Nam   char(90)  default '',
--��λ����
  TBus_Tp   char(5)   default '',
--ҵ������
  TCus_Id   char(40)  not null,
--�ͻ���ʶ
  TCus_Nm   char(90)  default '',
--�ͻ���ʶ
  Eff_Dat   char(8)   default '',
--��Ч����
  Ivd_Dat   char(8)   default '',
--ʧЧ����
  LEr_Msg   char(90)  default '',
--����Э����ʾ
  TEr_Msg   char(90)  default '',
--������Э����ʾ
  Area_Id  char(5),
--��������
  batch_Id  char(12),
--���κ�
  usb_flg  char(1) default 'Y',
--�Ƿ�����̱�־
  PRIMARY KEY( Gds_BId, Act_No, Gds_AId, TCus_Id )
) IN "BBIP_APP"
INDEX IN  "BBIP_APP_INDEX";


--����ú��Э���ӱ�
drop table  gdeupsb.GDS_AGT_GAS;
create table gdeupsb.GDS_AGT_GAS
(
  Sub_Sts   char(1)   default '0',
--״̬( 0-��Ч 1-��Ч )
  LAgt_St   char(1)   default 'U',
--����Э��״̬( U-δУ�� C-��� F-У��ʧ�� S-У��ɹ� )
  TAgt_St   char(1)   default 'U',
--������Э��״̬( U-δУ�� C-��� F-У��ʧ�� S-У��ɹ� )
  Txn_Cnl   char(3)   not null,
--ǩԼ;��( TRM-��̨ WEB-��վ ... )
  Gds_BId   char(5)   not null,
--����ҵ��ID
  Act_No    char(21)  not null,
--Э��ҵ�����˺�
  Gds_AId   char(55)  not null,
--Э���
  Bnk_Typ   char(2)   default '',
--��������
  Bnk_No    char(12)  default '',
--�����к�
  Bnk_Nam   char(90)  default '',
--��������
  Org_Cod   char(12)  default '',
--��λ����
  Org_Nam   char(90)  default '',
--��λ����
  TBus_Tp   char(5)   default '',
--ҵ������
  TCus_Id   char(40)  not null,
--�ͻ���ʶ
  TCus_Nm   char(90)  default '',
--�ͻ���ʶ
  Eff_Dat   char(8)   default '',
--��Ч����
  Ivd_Dat   char(8)   default '',
--ʧЧ����
  LEr_Msg   char(90)  default '',
--����Э����ʾ
  TEr_Msg   char(90)  default '',
--������Э����ʾ
  Area_Id  char(5),
--��������
  batch_Id  char(12),
--���κ�
  usb_flg  char(1) default 'Y',
--�Ƿ�����̱�־
  PRIMARY KEY( Gds_BId, Act_No, Gds_AId, TCus_Id )
) IN "BBIP_APP"
INDEX IN  "BBIP_APP_INDEX";

--�㶫����Э���ӱ�
drop table  gdeupsb.GDS_AGT_ELC;
create table gdeupsb.GDS_AGT_ELC
(
  Sub_Sts   char(1)   default '0',
--״̬( 0-��Ч 1-��Ч )
  LAgt_St   char(1)   default 'U',
--����Э��״̬( U-δУ�� C-��� F-У��ʧ�� S-У��ɹ� )
  TAgt_St   char(1)   default 'U',
--������Э��״̬( U-δУ�� C-��� F-У��ʧ�� S-У��ɹ� )
  Txn_Cnl   char(3)   not null,
--ǩԼ;��( TRM-��̨ WEB-��վ ... )
  Gds_BId   char(5)   not null,
--����ҵ��ID
  Act_No    char(21)  not null,
--Э��ҵ�����˺�
  Gds_AId   char(55)  not null,
--Э���
  Bnk_Typ   char(2)   default '',
--��������
  Bnk_No    char(12)  default '',
--�����к�
  Bnk_Nam   char(90)  default '',
--��������
  Org_Cod   char(12)  default '',
--��λ����
  Org_Nam   char(90)  default '',
--��λ����
  TBus_Tp   char(5)   default '',
--ҵ������
  TCus_Id   char(40)  not null,
--�ͻ���ʶ
  TCus_Nm   char(90)  default '',
--�ͻ���ʶ
  Eff_Dat   char(8)   default '',
--��Ч����
  Ivd_Dat   char(8)   default '',
--ʧЧ����
  LEr_Msg   char(90)  default '',
--����Э����ʾ
  TEr_Msg   char(90)  default '',
--������Э����ʾ
  Area_Id  char(5),
--��������
  batch_Id  char(12),
--���κ�
  usb_flg  char(1) default 'Y',
--�Ƿ�����̱�־
  PRIMARY KEY( Gds_BId, Act_No, Gds_AId, TCus_Id )
) IN "BBIP_APP"
INDEX IN  "BBIP_APP_INDEX";

--���ձ�2
DROP TABLE GDEUPSB.ZH_AGT_ACTNO ;
create table GDEUPSB.ZH_AGT_ACTNO 
(
   OLD_ACT   char(21) not null,
--21λ��һ��ͨ���ʺ�
   ACT_NO    char(21) not null,
--21λһ��ͨʵ���ʺ�
   primary key(OLD_ACT)
)
 IN "BBIP_APP"
INDEX IN  "BBIP_APP_INDEX";

--�ʺ���Ϣ��   ���ձ�1
DROP TABLE GDEUPSB.ZH_ACTNO_INF;
create table GDEUPSB.ZH_ACTNO_INF
(
   OLD_ACT   char(21),
--���˺�
   ACT_NO    char(21) not null,
--���˺�
   ACT_SQN   char(05) ,
--���
   CUS_ID    char(13) ,
--�¿ͻ���
   ACT_TYP   char(1) default ' ',
--�ʺ����� 1 һ��ͨ 2 �ʺ� 4 ���� 6 CD��
   ACT_NAM   char(90) default ' ',
--�ͻ�����
   OPN_NOD   char(11) default ' '
--�������� רָ�Թ��ʻ�
)
 IN "BBIP_APP"
INDEX IN  "BBIP_APP_INDEX";

create index ni1_actnoinf on GDEUPSB.ZH_ACTNO_INF(ACT_NO,ACT_TYP);
create index ni2_actnoinf on GDEUPSB.ZH_ACTNO_INF(OLD_ACT);

--��Ʊƾ֤״̬�ǼǱ�(��Է�Ʊ�Ǽǣ�ʹ�ã��������)
DROP TABLE GDEUPSB.GDEUPS_INV_DTL_BOK;
CREATE TABLE GDEUPSB.GDEUPS_INV_DTL_BOK(
	INV_TYP		CHAR(3)   not null,--ƾ֤���� F17-�����豸��Ʊ
	IV_BEG_NO	CHAR(30)   not null,--ƾ֤��ʼ����
	IV_END_NO	CHAR(30)   not null,--ƾ֤��������
	TOL_NUM		CHAR(8)    not null,--����
	OPR_TLR		CHAR(7)    not null,--���ù�Ա
	REG_TLR		CHAR(7)    not null default '',--���Ź�Ա���Ǽ���ʵ��Ա
	NODNO		CHAR(11)   not null default '',--�����
	TCK_NO		CHAR(20)   not null default '',--�ǻ����ˮ
	REG_DAT		CHAR(10)   not null default '',--�Ǽ�����
	SEQ_NO		CHAR(8)    not null default '',--������ˮ��
	USE_DAT		CHAR(10)   not null default '',--��������
	CHK_DAT		CHAR(10)   not null default '',--��������
	USE_NUM		CHAR(10)   not null default '',--�ܹ�ʹ������
	CLR_NUM		CHAR(8)    not null default '',--�ܹ��ϳ�����
	STATUS		CHAR(1)    not null default '',--״̬0-����״̬1-����״̬2-����״̬
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



--�����ն˷�Ʊʹ�������(ÿ���ն˶�Ӧһ����¼)
DROP TABLE GDEUPSB.GDEUPS_INV_TERM_INF;
CREATE TABLE GDEUPSB.GDEUPS_INV_TERM_INF(
	TLR_ID		CHAR(7)    not null,--���ù�Ա
	NODNO		CHAR(11)   not null default '',--�����
	INV_TYP		CHAR(3)    not null,--ƾ֤���� F17-�����豸��Ʊ
	IV_BEG_NO	CHAR(30)   not null,--ƾ֤��ʼ����
	IV_END_NO	CHAR(30)   not null,--ƾ֤��������
	SEQ_NO		CHAR(8)    not null default '',--������ˮ��
	INV_NUM		CHAR(8)    not null,--���õķ�Ʊ����
	USE_DAT		CHAR(10)   not null default '',--��������
	USE_NUM		CHAR(10)   not null default '0',--ʹ������
	CLR_NUM		CHAR(8)    not null default '0',--��������
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


--  ��Ʊ������Ϣ��¼��(ÿ�ŷ�Ʊ��Ӧ�Ľ�����Ϣ
DROP TABLE GDEUPSB.GDEUPS_INV_TXN_INF;
CREATE TABLE GDEUPSB.GDEUPS_INV_TXN_INF(
	INV_TYP		CHAR(3)    not null,--ƾ֤���� F17-�����豸��Ʊ
	IV_BEG_NO	CHAR(30)   not null,--ƾ֤��ʼ����
	IV_END_NO	CHAR(30)   not null,--ƾ֤��������
	USE_SEQ		CHAR(6)    not null,--ʹ�����
	STL_NUM		CHAR(8)    not null,--ʹ������
	STL_FLG		CHAR(1)    not null,--����ʽ:0-ʹ�ã�1-������U-װ��
	ACT_DAT		CHAR(10)   not null,--�������
	TLR_ID		CHAR(7)    not null,--���ù�Ա
	NODNO		CHAR(11)    not null default '',--�����
	QY_NO		CHAR(4)    not null default '',--��ҵ���:0002 ����,0542 ��ͨ,0003 �ƶ�,0643 ����
	OLD_SEQ		CHAR(25)   not null default '',--��Ӧ������ˮ��
	OLD_TR_DATE	CHAR(10)   not null default '',--��Ӧ��������
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


--��Ա�����һ������һ����¼����Ա�ڻ��߲�����Ʊ��ؽ���ʹ�ã�
DROP TABLE GDEUPSB.GDEUPS_TL_NO_MANAGER;
CREATE TABLE GDEUPSB.GDEUPS_TL_NO_MANAGER
(
  GYDM   char(20)  not null,--�����
  PASSWD char(14)  not null--����
)IN "BBIP_APP"
INDEX IN "BBIP_APP_INDEX";
CREATE INDEX GDEUPS_TL_NO_MANAGER_N1 on GDEUPSB.GDEUPS_TL_NO_MANAGER(GYDM);


drop table GDEUPSB.GD_ELEC_CLR_INF;
CREATE TABLE GDEUPSB.GD_ELEC_CLR_INF
(
  BR_NO          CHAR(11)    DEFAULT ' ',
--���к�
  C_Agt_No        CHAR(10)       not null,
--���㵥λЭ���
  clr_Dat        CHAR(8)         DEFAULT ' ',
--��������
  clr_Tim        CHAR(6)         DEFAULT ' ',
--����ʱ��
  clr_Sts        CHAR(1)         DEFAULT ' ',
--����״̬  0:����״̬��1:����״̬
  aut_Flg        CHAR(1)         DEFAULT ' ',
--ϵͳ��ʱ�Զ������־  0:�Զ�������Ч��1:��ֹ�Զ�����
PRIMARY KEY(C_Agt_No)
)
IN "BBIP_APP"
INDEX IN  "BBIP_APP_INDEX";

-- ============================================================
--   �������ҵ��
--   Table: efeaclrtbl   (���ݵ���������Ϣ�޸ı�)
-- ============================================================
drop table GDEUPSB.GD_ELEC_CLR_TBL;
CREATE TABLE GDEUPSB.GD_ELEC_CLR_TBL
(
  SQN      CHAR(32)    NOT NULL,
  BR_NO          CHAR(11)         DEFAULT ' ',
--���к� 
  Nod_No         CHAR(11)         DEFAULT ' ',
--�����  
  Clr_Dat        CHAR(8)         DEFAULT ' ',
--�޸���������
  Clr_Tim        CHAR(6)         DEFAULT ' ',
--�޸�����ʱ��
  Clr_Sts        CHAR(1)         DEFAULT ' ',
--�޸�����״̬  0:����״̬��1:����״̬
  Aut_Flg        CHAR(1)         DEFAULT ' ',
--�޸�ϵͳ��ʱ�Զ������־  0:�Զ�������Ч��1:��ֹ�Զ�����  
  Tlr_Id         CHAR(8)         DEFAULT ' ',
--�޸Ĺ�Ա
  Log_Dat        CHAR(8)         DEFAULT ' ',
--�޸�����ʱ��
  Log_Tim        CHAR(6)         DEFAULT ' ',
--�޸�����ʱ��
PRIMARY KEY(SQN)
)IN "BBIP_APP"
INDEX IN  "BBIP_APP_INDEX";

-- ============================================================
--   �������ҵ��
--   Table: efeaclrtbl   (���ݵ���������ˮ��)
-- ============================================================
drop table GDEUPSB.GD_ELEC_CLR_JNL;
CREATE TABLE GDEUPSB.GD_ELEC_CLR_JNL
(
  SQN      CHAR(32)    NOT NULL,
  Br_No          CHAR(11)         DEFAULT ' ',
--���к�
  Nod_No         CHAR(11)         DEFAULT ' ',
--����� 
  Tlr_Id         CHAR(8)         DEFAULT ' ',
--�����Ա
  C_Agt_No        CHAR(10)        DEFAULT ' ',
--���㵥λЭ���
  Clr_Dat        CHAR(8)         DEFAULT ' ',
--��������
  Clr_Tim        CHAR(6)         DEFAULT ' ',
--����ʱ��
  Clr_Rst        CHAR(1)         DEFAULT ' ',
--�������  0:δ���㣻1:������
  Clr_Typ        CHAR(1)         DEFAULT ' ',
--��������  0:�Զ����㣻1:�ֹ�����
  Clr_Tot        CHAR(8)         DEFAULT ' ',
--�������
  Clr_Amt        CHAR(15)        DEFAULT ' ',
--������
PRIMARY KEY(SQN)
)IN "BBIP_APP"
INDEX IN  "BBIP_APP_INDEX";


--ȼ��Э���һ���û����ֻ��һ�����ݣ��û����������
--gascusall491
--����ȼ��Э����ֶζ��գ����ڲ�ѯȼ��Э����ʷ������ֲ
--	ICS�ɱ��ֶ�					����ʹ���ֶ�
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
--�û����
  CUS_AC    char(32) not null,  
--�˺�
  CUS_NME   char(100) not null,
--����������
  CUS_TYP    char(32) not null,  
--�˺�����(0�Թ�1��˽����һ��ͨ2��˽��ͨ��4��˽��)
  OPT_DAT    char(10),  
--�������޸�ǩԼ����
  OPT_NOD    char(11),  
--��������
  ID_TYP   char(2),
--֤������
  ID_NO     char(32),
--֤������
  THD_CUS_NME     char(100),
--��ϵ������
  CMU_TEL     char(50),
--��ϵ���ֻ�����
  THD_CUS_ADR     char(100),
--��ϵ�˵�ַ
 PRIMARY KEY(CUS_NO)
)
IN "BBIP_APP"
INDEX IN  "BBIP_APP_INDEX";

-- ============================================================
--   ˮ��ҵ�� ��ˮ������ʱ��
-- ============================================================
DROP TABLE GDEUPSB.GDEUPS_WAT_BAT_INF_TMP;
CREATE TABLE GDEUPSB.GDEUPS_WAT_BAT_INF_TMP
(  
	SQN			CHAR(20)		NOT NULL,--���к�
	BAT_NO		CHAR(21)		NOT NULL,--���κ�
	BK_NO		CHAR(11)		DEFAULT ' ',--���к�
	COM_NO		CHAR(10)		DEFAULT ' ',--����λ���
	SEQ_NO		INTEGER,					--���
	ACT_DAT		DATE,						--��������
	HNO			CHAR(9)			DEFAULT ' ',--����
	SJ			CHAR(8)			DEFAULT ' ',--ʱ��
	JE			CHAR(11)		DEFAULT '0',--���Է�Ϊ��λ
	BCOUNT		CHAR(40)		DEFAULT ' ',--�����˺�
	STATUS		CHAR(1)			DEFAULT 'U',--����״̬
	ERR_MSG		VARCHAR(300)	DEFAULT ' ',--������Ϣ
	RMK1		VARCHAR(300)	DEFAULT ' ',--�����ֶ�1
	RMK2		VARCHAR(300)	DEFAULT ' ',--�����ֶ�2
	RMK3		VARCHAR(300)	DEFAULT ' ',--�����ֶ�3
	RMK4		VARCHAR(300)	DEFAULT ' ',--�����ֶ�4
	RMK5		VARCHAR(300)	DEFAULT ' ',--�����ֶ�5
	RMK6		VARCHAR(300)	DEFAULT ' ',--�����ֶ�6
	primary key(SQN)
)IN "BBIP_APP"
INDEX IN  "BBIP_APP_INDEX";

-- ============================================================
--   ˮ��ҵ�� Э����Ϣ��
-- ============================================================
DROP TABLE GDEUPSB.GDEUPS_WAT_AGT_INF;
CREATE TABLE GDEUPSB.GDEUPS_WAT_AGT_INF
(  
cus_Ac   char(40),   --�ͻ��˺�
cus_Nme  char(100),  --�ͻ�����
pwd     char(20),   --����
thd_Cus_No  char(40),   --�������ͻ���ʶ
thd_Cus_Nme  char(60),   --�������ͻ�����
id_Typ   char(4),  --������֤������
id_No   char(32),  --������֤������
bl_Nme   char(60),   --����������
addr  char(80),   --��ַ
hphone   char(15),  --��ͥ�绰	
lphone   char(15),  --�ֻ��绰	
post    char(1),  --�ʼı�ʶ
sjman    char(60),  --�ռ�������
postno   char(6),   --��������
taddr   char(80),   --�ʼĵ�ַ
agd_Agr_No   char(23)  NOT NULL,   --Э����
agt_Sts   char(1),  --Э��״̬
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
     'ѧ������';                                                          
       comment on column GDEUPSB.STU_FEE_INFO.STU_NAM is             
     'ѧ������';                                                          
      comment on column GDEUPSB.STU_FEE_INFO.SCH_COD is             
     'ѧУ����(�㶫�ƾ���ѧ����ѧԺ:001)';                                                      
       comment on column GDEUPSB.STU_FEE_INFO.SCH_NAM  is            
     'ѧУ����';                                                          
       comment on column GDEUPSB.STU_FEE_INFO.PAY_YEA is             
     '�ɷ����';                                                          
       comment on column GDEUPSB.STU_FEE_INFO.PAY_TEM is             
     '�ɷ�ѧ��(һѧ��:01 ��һѧ��:02 �ڶ�ѧ��:03 ����ѧ��:04)';                                                  
   comment on column GDEUPSB.STU_FEE_INFO.XZF_AMT is                 
     '��ͨ(ѧ)���ӷ�';                                                      
       comment on column GDEUPSB.STU_FEE_INFO.ROM_AMT is             
     'ס�޷�';                                                            
       comment on column GDEUPSB.STU_FEE_INFO.LRN_AMT is             
     '���(��)ѧ�ӷ�';                                                    
       comment on column GDEUPSB.STU_FEE_INFO.OTH_AMT is             
     '��������';                                                          
           comment on column GDEUPSB.STU_FEE_INFO.TXN_AMT is         
     '�ܷ���';                                                            
           comment on column GDEUPSB.STU_FEE_INFO.STATUS  is         
     '״̬(0:����;1:���ڽɷ�)';                                           
            comment on column GDEUPSB.STU_FEE_INFO.FLAG    is        
     '�շѱ�־(0:δ�շ�;1:���շ�)';                                            
         
                                                 
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
    '�������ո�������Ϣ��';
COMMENT ON COLUMN GDEUPSB.GDEUPS_BATCH_CONSOLE_INFO.BAT_NO
IS
    '���κ�';
COMMENT ON COLUMN GDEUPSB.GDEUPS_BATCH_CONSOLE_INFO.TXN_MDE
IS
    '0-�ļ���ʽ,1-��ѯ��ʽ';
COMMENT ON COLUMN GDEUPSB.GDEUPS_BATCH_CONSOLE_INFO.RAP_TYP
IS
    '0-����,1-����';
COMMENT ON COLUMN GDEUPSB.GDEUPS_BATCH_CONSOLE_INFO.COM_NO
IS
    '��λ���';
COMMENT ON COLUMN GDEUPSB.GDEUPS_BATCH_CONSOLE_INFO.COM_NME
IS
    '��˾����';
COMMENT ON COLUMN GDEUPSB.GDEUPS_BATCH_CONSOLE_INFO.BUS_KND
IS
    'ҵ������';
COMMENT ON COLUMN GDEUPSB.GDEUPS_BATCH_CONSOLE_INFO.TXN_ORG_CDE
IS
    '���׻�����';
COMMENT ON COLUMN GDEUPSB.GDEUPS_BATCH_CONSOLE_INFO.TXN_TLR
IS
    '���׹�Ա';
COMMENT ON COLUMN GDEUPSB.GDEUPS_BATCH_CONSOLE_INFO.SUB_DTE
IS
    '�ύ����';
COMMENT ON COLUMN GDEUPSB.GDEUPS_BATCH_CONSOLE_INFO.EXE_DTE
IS
    '��������';
COMMENT ON COLUMN GDEUPSB.GDEUPS_BATCH_CONSOLE_INFO.BAT_STS
IS
    '����״̬ ��ʼ״̬ΪU ���ύ״̬ΪW �ύ���״̬ΪS ȡ��״̬ΪC';
COMMENT ON COLUMN GDEUPSB.GDEUPS_BATCH_CONSOLE_INFO.FLE_NME
IS
    '�ļ���';
COMMENT ON COLUMN GDEUPSB.GDEUPS_BATCH_CONSOLE_INFO.TOT_CNT
IS
    '�����ܱ���';
COMMENT ON COLUMN GDEUPSB.GDEUPS_BATCH_CONSOLE_INFO.TOT_AMT
IS
    '�����ܽ��';
COMMENT ON COLUMN GDEUPSB.GDEUPS_BATCH_CONSOLE_INFO.SUC_TOT_CNT
IS
    '�ɹ��ܱ���';
COMMENT ON COLUMN GDEUPSB.GDEUPS_BATCH_CONSOLE_INFO.SUC_TOT_AMT
IS
    '�ɹ��ܽ��';
COMMENT ON COLUMN GDEUPSB.GDEUPS_BATCH_CONSOLE_INFO.FAL_TOT_CNT
IS
    'ʧ���ܱ���';
COMMENT ON COLUMN GDEUPSB.GDEUPS_BATCH_CONSOLE_INFO.FAL_TOT_AMT
IS
    'ʧ���ܽ��';
COMMENT ON COLUMN GDEUPSB.GDEUPS_BATCH_CONSOLE_INFO.PAY_CNT
IS
    '��ʼΪ0';
COMMENT ON COLUMN GDEUPSB.GDEUPS_BATCH_CONSOLE_INFO.RSV_FLD1
IS
    'Ԥ���ֶ�1';
COMMENT ON COLUMN GDEUPSB.GDEUPS_BATCH_CONSOLE_INFO.RSV_FLD2
IS
    'Ԥ���ֶ�2';
COMMENT ON COLUMN GDEUPSB.GDEUPS_BATCH_CONSOLE_INFO.RSV_FLD3
IS
    'Ԥ���ֶ�3';
COMMENT ON COLUMN GDEUPSB.GDEUPS_BATCH_CONSOLE_INFO.RSV_FLD4
IS
    'Ԥ���ֶ�4';
COMMENT ON COLUMN GDEUPSB.GDEUPS_BATCH_CONSOLE_INFO.RSV_FLD5
IS
    'Ԥ���ֶ�5';
COMMENT ON COLUMN GDEUPSB.GDEUPS_BATCH_CONSOLE_INFO.RSV_FLD6
IS
    'Ԥ���ֶ�6';
COMMENT ON COLUMN GDEUPSB.GDEUPS_BATCH_CONSOLE_INFO.RSV_FLD7
IS
    'Ԥ���ֶ�7';
COMMENT ON COLUMN GDEUPSB.GDEUPS_BATCH_CONSOLE_INFO.RSV_FLD8
IS
    'Ԥ���ֶ�8';
COMMENT ON COLUMN GDEUPSB.GDEUPS_BATCH_CONSOLE_INFO.RSV_FLD9
IS
    'Ԥ���ֶ�9'                                               

drop table  gdeupsb.GDEUPS_Agt_Elec_TMP; --��ͷ����Э��������Ϣ��
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

  comment on table gdeupsb.GDEUPS_Agt_Elec_TMP   is   '��ͷ����Э����Ϣ��'; 
  comment on column gdeupsb.GDEUPS_Agt_Elec_TMP.COM_CODE   is   '�ո�����ҵ����'; 
  comment on column gdeupsb.GDEUPS_Agt_Elec_TMP.FEE_CODE   is   '�������';
  comment on column gdeupsb.GDEUPS_Agt_Elec_TMP.FEE_NUM   is   '�ɷѺ�';
  comment on column gdeupsb.GDEUPS_Agt_Elec_TMP.USER_NAME   is   '�û�����'; 
  comment on column gdeupsb.GDEUPS_Agt_Elec_TMP.OLD_BANK_NUM   is   'ԭ�������к�'; 
  comment on column gdeupsb.GDEUPS_Agt_Elec_TMP.OLD_CARD_NO   is   'ԭ�˻�/����';
  comment on column gdeupsb.GDEUPS_Agt_Elec_TMP.NEW_BANK_NUM   is   '�¿������к�'; 
  comment on column gdeupsb.GDEUPS_Agt_Elec_TMP.ACT_NO   is   '�˺�';
  comment on column gdeupsb.GDEUPS_Agt_Elec_TMP.ACOUNT_NAME   is   '�¿ͻ�����'; 
  comment on column gdeupsb.GDEUPS_Agt_Elec_TMP.ACT_TYPE   is   '�ʺ�����';
  comment on column gdeupsb.GDEUPS_Agt_Elec_TMP.PER_COM_FLAG   is   '����/���ű�־'; 
  comment on column gdeupsb.GDEUPS_Agt_Elec_TMP.ID_TYPE   is   '֤������';
  comment on column gdeupsb.GDEUPS_Agt_Elec_TMP.ID_NO   is   '֤������'; 
  comment on column gdeupsb.GDEUPS_Agt_Elec_TMP.CHECK_SEND_TYPE   is   '�˵��ʼ�����';
  comment on column gdeupsb.GDEUPS_Agt_Elec_TMP.INVOICE_SNED_TYPE   is   '��Ʊ�ʼ�����';
  comment on column gdeupsb.GDEUPS_Agt_Elec_TMP.POINT_NUM   is   '�������';
  comment on column gdeupsb.GDEUPS_Agt_Elec_TMP.INVOICE_SNED_MAN   is   '��Ʊ�ʼ���'; 
  comment on column gdeupsb.GDEUPS_Agt_Elec_TMP.INVOICE_SEND_ZIP   is   '��Ʊ�ʼ��ʱ�';
  comment on column gdeupsb.GDEUPS_Agt_Elec_TMP.INVOICE_SEND_ADDR   is   '��Ʊ�ʼĵ�ַ'; 
  comment on column gdeupsb.GDEUPS_Agt_Elec_TMP.CHECK_SEND_MAN   is   '�ʵ��ʼ���';
  comment on column gdeupsb.GDEUPS_Agt_Elec_TMP.CHECK_SEND_ZIP   is   '�ʵ��ʼ��ʱ�'; 
  comment on column gdeupsb.GDEUPS_Agt_Elec_TMP.INVOICE_SEND_ADDR   is   '�ʵ��ʼĵ�ַ';
  comment on column gdeupsb.GDEUPS_Agt_Elec_TMP.NOTIFY_TYPE   is   '����֪ͨ��ʽ'; 
  comment on column gdeupsb.GDEUPS_Agt_Elec_TMP.EMAIL   is   'E-MAIL';
  comment on column gdeupsb.GDEUPS_Agt_Elec_TMP.PHONE_NUM   is   '��ϵ�ֻ���'; 
  comment on column gdeupsb.GDEUPS_Agt_Elec_TMP.TEL_NUM   is   '��ϵ�绰';
  comment on column gdeupsb.GDEUPS_Agt_Elec_TMP.REMARK   is   '��ע'; 
  comment on column gdeupsb.GDEUPS_Agt_Elec_TMP.PRCESS_PASSWORD   is   '��������st';

  
--��ͷ������ʱ��
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


CREATE
    TABLE GDEUPSB.GDEUPS_ZHAG_BATCH_TEMP
    (
        SQN CHARACTER(24) not null,
        BAT_NO CHARACTER(21) NOT NULL,
        COM_NO CHARACTER(15),
        TXN_TLR CHARACTER(7),
        SUB_DTE DATE,
        CUS_AC VARCHAR(300),
        CUS_NME VARCHAR(300),
        TXN_AMT VARCHAR(300),
        THD_CUS_NO VARCHAR(300),
        THD_CUS_NME VARCHAR(300),
        RSV_FLD1 VARCHAR(300),
        RSV_FLD2 VARCHAR(300),
        RSV_FLD3 VARCHAR(300),
        RSV_FLD4 VARCHAR(300),
        CONSTRAINT BAT_INFO_IDX PRIMARY KEY (SQN)
    )IN "BBIP_APP"
INDEX IN  "BBIP_APP_INDEX";