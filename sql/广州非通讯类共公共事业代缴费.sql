--ƽ̨������
drop table gdeupsb.lot_sys_cfg;
create table gdeupsb.lot_sys_cfg
(
  Deal_Id    char(10)    not null default ' ',
--��Ӫ��ID
  Usr_Pam    char(10)    not null default ' ',
--�û���
  Usr_Pas    char(20)    not null default ' ',
--�û�����
  Sig_Tim    char(14)    not null default ' ',
--ǩ��ʱ��
  Lcl_Tim    char(14)    not null default ' ',
--����ʱ��
  Lot_Tim    char(14)    not null default ' ',
--����ʱ��
  Diff_Tm    char(20)    not null default ' ',
--ʱ���
  DS_C_Agt_No  char(10)    not null default ' ',
--���յ�λ���
  DF_C_Agt_No  char(10)    not null default ' ',
--������λ���
  HS_Act_No   char(21)    not null default ' ',
--����Թ����˺�
  Log_Seq    char(12)    not null default ' ',
--������־��ˮ���
  WH_Phone   char(11)    not null default ' '
--ά����Ա�ֻ��� 
)
 IN "BBIP_APP"
INDEX IN  "BBIP_APP_INDEX";

--�����û���
drop table gdeupsb.lot_cus_inf;
create table gdeupsb.lot_cus_inf
(
  Br_No        char(6)     not null default ' ',
--���к�
  Cus_Nam      char(60)    not null default ' ',
--�ͻ�����
  Crd_No       char(30)    not null default ' ',
--�ͻ����п���
  Act_No       char(21)    not null default ' ',
--�ͻ������ʺ�
  Act_Nod       char(6)     default ' ',
--��������
  Id_Typ       char(2)     not null default ' ',
--֤������
  Id_No        char(30)    not null default ' ',
--֤������
  Mob_Tel      char(15)    not null default ' ',
--�ƶ���ϵ�绰
  Fix_Tel      char(20)    default ' ',
--�̶���ϵ�绰
  Lot_Nam      char(30)    default ' ',
--�����ʶ
  Lot_Psw      char(30)    default ' ',
--��������
  Reg_Tim      char(20)    default ' ',
--ע��ʱ��
  Email       char(30)    default ' ',
--��������
  City_Id      char(2)     not null default ' ',
--���б���
  SEX         char(1)     not null default ' ',
--�Ա�
  BthDay      char(8)     not null default ' ',
--����
  Status      char(1)     not null default ' ',
--״̬ 1����;2����;3ע��
primary key (Crd_No)
)
 IN "BBIP_APP"
INDEX IN  "BBIP_APP_INDEX";

--��Ͷ���Ʊ�
drop table gdeupsb.lot_pln_ctl;
create table gdeupsb.lot_pln_ctl
(
   Game_Id    char(2)     not null default ' ',
--��Ϸ��� 5:˫ɫ��
   Draw_Id    char(6)     not null default ' ',
--��ǰ�ں�
   Draw_Nm    char(10)     not null default ' ',
--��ǰ����
   Bet_Dat    char(8)    not null default ' ',
--��Ͷ����
   Beg_Tim    char(14)    not null default ' ',
--��Ͷ��ʼʱ��
   End_Tim    char(14)    not null default ' ',
--��Ͷ����ʱ��
   Txn_Sts    char(1)    not null default 'U'
--��ǰ�ں�ִ����� U��ʼ��Sִ����ɣ�Fִ��ʧ��
) IN "BBIP_APP"
INDEX IN  "BBIP_APP_INDEX";


--��Ͷ�ƻ���
drop table gdeupsb.lot_aut_pln;
create table gdeupsb.lot_aut_pln
(
   Plan_No    char(20)    not null default ' ',
--��Ͷ�ƻ���� 99+���ڣ�YYYYMMDD��+ 10λ˳��
   Plan_Nm    char(30)    not null default ' ',
--��Ͷ�ƻ�����
   Game_Id    char(2)     not null default ' ',
--��Ϸ��� 5:˫ɫ��
   Gam_Nam    char(20)     not null default ' ',
--��Ϸ���� 5:˫ɫ��
   Play_Id    char(5)     not null default ' ',
--�淨���
   Bet_Per    char(5)     not null default ' ',
--Ͷע���� ��һ��153�ڡ�����75�ڣ�
   Bet_Met    char(5)     not null default ' ',
--Ͷע����  0��ѡ��1��ѡ
   Bet_Mod    char(5)     not null default ' ',
--Ͷע��ʽ  1��ʽ��12��ʽ��13����
   Bet_Mul    char(3)     not null default ' ',
--Ͷע����
   Bet_Amt    char(15)     not null default ' ',
--Ͷע���
   Bet_Lin    char(128)     not null default ' ',
--Ͷע����
   Lot_Nam    char(30)    default ' ',
--�����ʶ
   Crd_No      char(30)    not null default ' ',
--�ͻ����п���
   Mob_Tel      char(15)    not null default ' ',
--�ƶ���ϵ�绰
   Bet_Dat      char(8)    not null default ' ',
--��Ͷ����
   Bet_Tim      char(14)    not null default ' ',
--��Ͷʱ��
   Ccl_Tim      char(14)    not null default ' ',
--ȡ�����߽�����Ͷʱ��
   Cur_Per      char(6)    not null default ' ',
--��ǰ�ں�
   Cur_Fal      char(2)    not null default '0',
--��ǰ�ں�ʧ�ܴ���
   Con_Fal      char(2)    not null default '0',
--�����ں�ʧ�ܴ���
   Do_Per       char(5)    not null default '0',
--��ִ�е�����
   Txn_Cnl     char(3)     default ' ',
--��Ͷ��������
   Status      char(1)    not null default '0',
--״̬ 0�ƻ�����;1�ͻ�����;2ϵͳ����;3�ƻ����
primary key (Plan_No)
) IN "BBIP_APP"
INDEX IN  "BBIP_APP_INDEX";


--Ͷע��ˮ��
drop table gdeupsb.lot_txn_jnl;
create table gdeupsb.lot_txn_jnl
(
   Br_No     char(6)     not null default ' ',
--���к�
   Log_No     char(14)   not null,
--ǰ����ˮ��
   T_Txn_Cd      char(10)    not null,
--������������
   Txn_Cod      char(6)     not null,
--������
   Sch_Typ    char(1)     not null default ' ',
--��������
   Sch_Tit    char(16)     not null default ' ',
--��������
   Sec_Lev    char(1)     not null default ' ',
--�����ȼ�
   City_Id    char(2)     not null default ' ',
--���б���
   Draw_Id    char(5)     not null default ' ',
--��ǰ�ں�
   Keno_Id    char(5)     not null default ' ',
--keno�ں�
   Game_Id    char(2)     not null default ' ',
--��Ϸ��� 5:˫ɫ��
   Gam_Nam    char(20)    not null default ' ',
--��Ϸ���� 5:˫ɫ��
   Play_Id    char(5)     not null default ' ',
--�淨���
   Bet_Met    char(5)     not null default ' ',
--Ͷע����  0��ѡ��1��ѡ
   Bet_Mod    char(5)     not null default ' ',
--Ͷע��ʽ  1��ʽ��12��ʽ��13����
   Bet_Mul    char(3)     not null default ' ',
--Ͷע����
   Txn_Amt    char(15)     not null default ' ',
--Ͷע���
   Bet_Lin    char(128)     not null default ' ',
--Ͷע����
   Lot_Nam    char(30)     not null default ' ',
--�����ʶ
   Bet_Dat    char(8)    not null default ' ',
--Ͷע����
   Txn_Tim    char(14)   not null default ' ',
--Ͷעʱ��
   Txn_Log    char(32)   not null default ' ',
--Ͷע������ˮ��
   Sch_Id        char(32)   not null default ' ',
--ϵͳ���ɵķ������
   T_Log_No      char(30)    default ' ',
--��Ʊ��ˮ��
   Cipher      char(30)    default ' ',
--��Ʊ����
   Verify      char(30)    default ' ',
--��ƱУ����
   Cus_Nam      char(60)    not null default ' ',
--�ͻ�����
   Crd_No       char(30)    not null default ' ',
--�ͻ����п���
   H_Txn_Cd      char(6)  default ' ' not null,
--����������
   H_Txn_Sb      char(3)  default ' ' not null,
--��������
   H_Log_No      char(9)     default ' ',
--������ˮ��
   H_Rsp_Cd      char(6)     default ' ',
--������Ӧ��
   H_Txn_St      char(1)     default 'U',
--��������״̬
   T_Rsp_Cd      char(10)    default ' ',
--��������Ӧ��
   T_Txn_St      char(1)     default 'U',
--����������״̬
   Thd_Chk      char(1)  default '0',
--����������ʱ�־,0δ���� 1�Ѷ���
   T_Chk_No      char(12)   default '00000000000',
--���������������
   Chk_Tim      char(14)   default ' ',
--����ʱ��
   Chk_Flg      char(1)    Default '0',
--���˱�־(0:δ����;1:�˶Գɹ�;2: �˶�ʧ�ܣ��ҷ�����;)
   Awd_Flg      char(1)    Default '0',
--�н���־(0:δ�н�;1:�н�)
   Awd_Rtn      char(1)    Default '0',
--������־(0:δ����;1:�ѷ���)
   C_Agt_No      char(10)    default ' ' not null,
--�̻�Э����
   Tck_No     char(12)      default ' ',
--�����ˮ��
   Txn_Cnl     char(3)     default ' ',
--��������
   Bet_Typ     char(1)     default ' ',
--Ͷע���� 0:ʵʱͶע��1����ͶͶע
   L_Chk_Tm       char(10)   default ' ',
--1970���������
   Txn_Sts      char(1)     default 'U'
--����״̬  S�ɹ���Fʧ�ܣ�T��ʱ
--U:��ʼ
--S:���ʳɹ�
--F:����ʧ��
--T:��ʱ
--A:�����������ʱ״̬
--E:����ʧ�ܺ����ʧ��
) IN "BBIP_APP"
INDEX IN  "BBIP_APP_INDEX";

--���˿��Ʊ�
drop table gdeupsb.lot_chk_ctl;
create table gdeupsb.lot_chk_ctl
(
   Chk_Dat      char(8)   default ' ',
--��������
   Game_Id    char(2)     not null default ' ',
--��Ϸ��� 5:˫ɫ��
   Draw_Id    char(5)     not null default ' ',
--�ں�
   Keno_Id    char(5)     not null default ' ',
--keno�ں�
   Tot_Num    char(5)     not null default ' ',
--Ͷע��Ʊ��
   Tot_Amt    char(14)    not null default ' ',
--Ͷע�ܽ��
   Chk_Flg    char(1)    not null default '0',
--���˱�־ 0:δ���ˣ�1:���ֶ��ˣ�2:ȫ������
   Chk_Tim    char(14)   default ' ',
--����ʱ��
primary key (Game_Id, Draw_Id)
) IN "BBIP_APP"
INDEX IN  "BBIP_APP_INDEX";

--������ϸ��
drop table gdeupsb.lot_chk_dtl;
create table gdeupsb.lot_chk_dtl
(
   Chk_Dat      char(8)   default ' ',
--��������
   Game_Id    char(2)     not null default ' ',
--��Ϸ��� 5:˫ɫ��
   Draw_Id    char(5)     not null default ' ',
--�ں�
   Keno_Id    char(5)     not null default ' ',
--keno�ں�
   Seq_No     char(10)    not null default ' ',
--���
   Sch_Id     char(32)    not null default ' ',
--�������
   Lot_Nam    char(30)    default ' ',
--�����ʶ
   Txn_Log    char(25)    not null default ' ',
--Ͷע������ˮ��
   Play_Id    char(5)     not null default ' ',
--�淨���
   Txn_Tim    char(14)    not null default ' ',
--Ͷעʱ��
   Txn_Amt    char(15)    not null default ' ',
--Ͷע���
   Chk_Flg    char(1)    not null default '0',
--���˱�־ 0:δ���ˣ�1:�Ѷ��ˣ�
   Chk_Tim    char(14)   default ' ',
--����ʱ��
primary key (Txn_Log)
) IN "BBIP_APP"
INDEX IN  "BBIP_APP_INDEX";


--������Ϣ��
drop table gdeupsb.lot_drw_tbl;
create table gdeupsb.lot_drw_tbl
(
   Game_Id    char(2)     not null default ' ',
--��Ϸ��� 5:˫ɫ��7������ʮ��
   Draw_Id    char(5)     not null default ' ',
--�ں�
   Draw_Nm    char(15)     not null default ' ',
--����
   Sal_Str    char(14)     not null default ' ',
--���ۿ�ʼʱ��
   Sal_End    char(14)     not null default ' ',
--���۽���ʱ��
   Csh_Str    char(14)     not null default ' ',
--�ҽ���ʼʱ��
   Csh_End    char(14)     not null default ' ',
--�ҽ�����ʱ��
   Keno_Id    char(5)     not null default ' ',
--keno�ں�(�ڿ���ʮ��ʱ��Ҫ����һ����¼�ں�ΪAAAAA��Ϊ��ͳ�ƿ쿪��Ϸ���ܽ��)
   Keno_Nm    char(15)     not null default ' ',
--keno����
   K_Sal_St    char(14)     not null default ' ',
--Keno���ۿ�ʼʱ��
   K_Sal_Ed    char(14)     not null default ' ',
--Keno���۽���ʱ��
   Chk_Flg    char(1)      not null default '0',
--���������־  0û����,1���ֶ���,2�Ѷ���,3������
   Chk_Tim    char(14)   default ' ',
--��������ʱ��
   Dow_Prz    char(1)    not null default '0',
--�Ƿ��������н��ļ�  0δ����,1������
   Prz_Amt    char(15)    not null default ' ',
--�����ܽ�� 
   Tot_Amt    char(15)    not null default ' ',
--�����ܽ��
   Dif_Flg    char(1)    not null default ' ',
--�����־��1�跽�������ܽ����ڷ����ܽ�,0�����������ܽ��С�ڽ��ܽ�
   Dif_Amt    char(15)    not null default ' ',
--�����ܽ���뷵���ܽ��������
   Pay_Flg    char(1)     not null default ' ',
--�����渶��־: 0û�渶,1����渶,2�ѷ��渶
   Pay_Amt    char(15)    not null default ' ',
--�����渶���
   Flw_Ctl    char(2)     not null default '0',
--�������̿��Ʊ�־
--1�����������ؿ�ʼ, 2���������������
--3�н��ļ����ؿ�ʼ, 4�н��ļ��������
--5���뷵����ϸ��ʼ, 6���뷵����ϸ����
--7�����ʽ𻮲���ʼ��8�����ʽ𻮲����
--9������ʼ��10������� 
primary key (Game_Id,Draw_Id)
) IN "BBIP_APP"
INDEX IN  "BBIP_APP_INDEX";

--���������
drop table gdeupsb.lot_prz_inf;
create table gdeupsb.lot_prz_inf
(
   Game_Id    char(2)     not null default ' ',
--��Ϸ��� 5:˫ɫ��
   Game_Nm    char(10)    not null default ' ',
--��Ϸ��
   Draw_Id    char(5)     not null default ' ',
--�ں�
   Draw_Nm    char(15)     not null default ' ',
--����
   Keno_Id    char(5)     not null default ' ',
--keno�ں�
   Keno_Nm    char(15)     not null default ' ',
--keno����
   Str_Tim    char(14)     not null default ' ',
--�ڿ�ʼʱ��
   Stp_Tim    char(14)     not null default ' ',
--�ڽ���ʱ��
   Tot_Prz    char(15)     not null default ' ',
--���н����
   Jac_Pot    char(18)     not null default ' ',
--����
   Opn_Tot    char(5)     not null default ' ',
--�����ܴ���
   Opn_Num    char(3)     not null default ' ',
--��������
   Bon_Cod    char(20)     not null default ' ',
--��������
   Cls_Num    char(3)     not null default ' ',
--��������
   Cls_Nam    char(20)     not null default ' ',
--����
   Bon_Amt    char(15)     not null default ' ',
--�н����
   Bon_Num    char(10)     not null default ' '
--�н�ע��
) IN "BBIP_APP"
INDEX IN  "BBIP_APP_INDEX";

--�н���¼���Ʊ�
drop table gdeupsb.lot_prz_ctl;
create table gdeupsb.lot_prz_ctl
(
   Game_Id    char(2)     not null default ' ',
--��Ϸ��� 5:˫ɫ��
   Draw_Id    char(5)     not null default ' ',
--�ں�
   Keno_Id    char(5)     not null default ' ',
--keno�ں�
   Cipher    char(30)    default ' ',
--��Ʊ����
   Big_Bon    char(5)     default ' ',
--�󽱱��
   Tot_Prz    char(15)    not null default ' ',
--���н����
   TxnLog    char(25)    not null default ' ',
--Ͷע������ˮ��
   T_Log_No    char(15)    not null,
--��Ʊ��ˮ��
   Term_ID    char(10)    not null default ' ',
--ϵͳͶע�ն˺�
primary key (T_Log_No)
) IN "BBIP_APP"
INDEX IN  "BBIP_APP_INDEX";

--�н���¼��ϸ��
drop table gdeupsb.lot_prz_dtl;
create table gdeupsb.lot_prz_dtl
(
   Game_Id    char(2)     not null default ' ',
--��Ϸ��� 5:˫ɫ��
   Draw_Id    char(5)     not null default ' ',
--�ں�
   Keno_Id    char(5)     not null default ' ',
--keno�ں�
   Txn_Log    char(25)    not null default ' ',
--Ͷע������ˮ��
   T_Log_No    char(15)    not null,
--��Ʊ��ˮ��
   Bet_Mod    char(5)     not null default ' ',
--Ͷע��ʽ  1��ʽ��12��ʽ��13����
   Bet_Mul    char(3)     not null default ' ',
--Ͷע����
   class_No    char(16)     not null default ' ',
--���ȱ��
   Prz_Amt    char(15)    not null default ' ',
--�н����
   Bet_Lin    char(128)   not null default ' ',
--Ͷע����
primary key (T_Log_No)
) IN "BBIP_APP"
INDEX IN  "BBIP_APP_INDEX";

--������¼��ϸ��
drop table gdeupsb.lot_awd_dtl;
create table gdeupsb.lot_awd_dtl
(
   Br_No        char(6)     not null default ' ',
--���к�
   Log_No      char(14)     default ' ',
--ǰ����ˮ��
   Txn_Cod      char(6)     default ' ',
--������
   Game_Id      char(2)     not null default ' ',
--��Ϸ��� 5:˫ɫ��
   Draw_Id      char(5)     not null default ' ',
--�ں�
   Keno_Id      char(5)     not null default ' ',
--keno�ں�
   Lot_Nam      char(30)    default ' ',
--�����ʶ
   Txn_Log      char(25)   not null default ' ',
--Ͷע������ˮ��
   T_Log_No      char(15)    not null default ' ',
--��Ʊ��ˮ��
   Cus_Nam      char(60)    not null default ' ',
--�ͻ�����
   Crd_No       char(30)    not null default ' ',
--�ͻ����п���
   Awd_Amt      char(15)    not null default ' ',
--�������
   H_Txn_Cd      char(6)     not null default ' ',
--����������
   H_Log_No      char(9)     default ' ',
--������ˮ��
   H_Rsp_Cd      char(6)     default ' ',
--������Ӧ��
   H_Txn_St      char(1)     default 'U',
--��������״̬
   Awd_Dat      char(8)     default ' ',
--��������
   Awd_Tim      char(14)    default ' ',
--����ʱ��
   Awd_Rtn      char(1)     default '0',
--������־(0:δ����;1:�ѷ���)
   Tck_No      char(12)      default ' '
--�����ˮ��
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

--��Ա�����
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
DF_MON char(8),  --�����
Tot_Cnt  char(5),   --�ܼ�¼��
Tot_Amt char(18),   --�ܽ��
Rs_Fld1   char(20),   --���������
thd_Cus_No char(30),   --�������ͻ���ʶ
cus_Ac  char(25),    -- �ͻ��ʺ�
tTxn_Amt  char(18),  --���
ele_Mon  char(8), --����·�
rs_Fld2   char(10),   --����
com_Ac  char(25), --������ҵ�˺�
flag  char(1), --�ɷѱ�־
t_ComNo  char(25),  --��λ���
TXN_DTE   char(8),  --��������
tlr     char(8),--�����Ա
primary key (sqn)
)
 IN "BBIP_APP"
INDEX IN  "BBIP_APP_INDEX";

drop table GDEUPSB.PUB_INV_INFO;
------------ͨ�÷�Ʊ��¼��
CREATE TABLE GDEUPSB.PUB_INV_INFO                     
(
   TR_TYPE      CHAR(1)        ,                                                       
                                                        
   REC_CNT      CHAR(2)        ,                               
                                                        
   SQN      CHAR(25)     not null  ,                                   
                                                        
   ACT_NO       CHAR(25)       ,                               
                                                        
   TCUS_ID      CHAR(14)       ,                               
                                                        
   FCUS_ID      CHAR(14)       ,                               
                                                        
   TR_DATE      DATE       ,                               
                                                        
   TCUS_NM      CHAR(30)       ,                               
                                                        
   IPRN_CNT     CHAR(2)           ,                            
                                                        
   BILL_DATE     CHAR(30)         ,                            
                                                        
   TXN_AMT      DECIMAL(18,2)         ,                            
                                                        
   LAST_BAL     DECIMAL(18,2)         ,                            
                                                        
   THIS_BAL      DECIMAL(18,2)         ,                            
                                                        
   IPAY_AMT      DECIMAL(18,2)         ,                            
                                                        
   ITOT_AMT     DECIMAL(18,2)         ,                            
                                                        
   EINV_NO      CHAR(30)      ,                                
                                                        
   STA_MON      CHAR(14)      ,                                
                                                        
   END_MON      CHAR(14)      ,                                
                                                        
   TMP01       CHAR(30)      ,                                 
                                                        
   TMP02       CHAR(30)      ,                                 
                                                        
   MX_COUNT     CHAR(10)      ,                                
                                                        
   FP_INF     VARCHAR(1024),
   PRIMARY KEY (SQN)                                             
)
 IN "BBIP_APP"
INDEX IN  "BBIP_APP_INDEX";                                                                                                                                                  
                                                           
       comment on column GDEUPSB.PUB_INV_INFO.TR_TYPE   is             
     '�ɷѵ�λ (1-���ţ�2-��ͨ��3-�ƶ�)';                               
       comment on column GDEUPSB.PUB_INV_INFO.REC_CNT   is             
     '��Ʊ�ڼ���';                                                      
      comment on column GDEUPSB.PUB_INV_INFO.SQN       is             
     '��ˮ��';                                                        
       comment on column GDEUPSB.PUB_INV_INFO.ACT_NO     is            
     '�ʺ�';                                                            
       comment on column GDEUPSB.PUB_INV_INFO.TCUS_ID   is             
     '�ɷѵ绰����';                                                    
       comment on column GDEUPSB.PUB_INV_INFO.FCUS_ID   is             
     '���ѵ绰����';                                                    
   comment on column GDEUPSB.PUB_INV_INFO.TR_DATE   is                 
     '��������';                                                        
       comment on column GDEUPSB.PUB_INV_INFO.TCUS_NM   is             
     '�û�����';                                                        
       comment on column GDEUPSB.PUB_INV_INFO.IPRN_CNT  is             
     '��Ʊ��ӡ������0�α�ʾδ��ӡ��';                                   
       comment on column GDEUPSB.PUB_INV_INFO.BILL_DATE is             
     '�����·�';                                                        
           comment on column GDEUPSB.PUB_INV_INFO.TXN_AMT   is         
     '�˴ν����ܽ��';                                                  
           comment on column GDEUPSB.PUB_INV_INFO.LAST_BAL  is         
     '���ŷ�Ʊ�ϴνɷѽ���';                                            
            comment on column GDEUPSB.PUB_INV_INFO.THIS_BAL  is        
     '���ŷ�Ʊ���νɷѽ���';                                            
            comment on column GDEUPSB.PUB_INV_INFO.IPAY_AMT  is        
     '���ŷ�Ʊ���ν����ܽ��';                                          
           comment on column GDEUPSB.PUB_INV_INFO.ITOT_AMT  is         
     '���ŷ�Ʊ���η�Ʊ�ܽ��';                                          
       comment on column GDEUPSB.PUB_INV_INFO.EINV_NO   is             
     '���ӷ�Ʊ��';                                                      
       comment on column GDEUPSB.PUB_INV_INFO.STA_MON   is             
     '������ʼ�·�';                                                    
       comment on column GDEUPSB.PUB_INV_INFO.END_MON   is             
     '���ѽ����·�';                                                    
       comment on column GDEUPSB.PUB_INV_INFO.TMP01     is             
     '��ʱ�ֶ�';                                                        
       comment on column GDEUPSB.PUB_INV_INFO.TMP02     is             
     '��ʱ�ֶ�';                                                        
       comment on column GDEUPSB.PUB_INV_INFO.MX_COUNT  is             
     '��Ʊ��ϸ��¼��';                                                  
       comment on column GDEUPSB.PUB_INV_INFO.FP_INF    is             
     '��Ʊ��ϸ';   

drop table GDEUPSB.TRSP_FEE_INFO;
-------------·�Ŵ��շѽɷѼ�¼��
CREATE TABLE GDEUPSB.TRSP_FEE_INFO
(
   BR_NO       CHAR(06)     NOT NULL,                                                            
                                                                             
   THD_KEY     CHAR(20)     NOT NULL,                                        
                                                                             
   CAR_TYP     CHAR(03)     NOT NULL,                                             
                                                                             
   CAR_NO     CHAR(20)     NOT NULL,                                           
                                                                             
   TCUS_NM     CHAR(60)     DEFAULT '',                                          
                                                                             
   PAY_MON     CHAR(02)     NOT NULL,                                            
                                                                             
   PAY_DAT     DATE     NOT NULL,                                          
                                                                             
   PAY_LOG     CHAR(20)      DEFAULT '',                               
                                                                             
   PAY_NOD     CHAR(6)      DEFAULT '',                                        
                                                                             
   PAY_TLR     CHAR(7)      DEFAULT '',                                      
                                                                             
   PAY_TCK     CHAR(12)     DEFAULT '',                                      
                                                                             
   TXN_AMT     DECIMAL(18,2)     DEFAULT '000000000000000000',                       
                                                                             
   TXN_CNL     CHAR(3)     DEFAULT ' ',                                      
                                                                             
   ACT_TYP     CHAR(01)     DEFAULT ' ',                            
                                                                             
   ACT_NO      CHAR(21)      DEFAULT ' ',                            
                                                                             
   TACT_DT     DATE     ,                                     
                                                                             
   TLOG_NO     CHAR(20)     DEFAULT ' ',                            
                                                                             
   PRT_NOD     CHAR(6)      DEFAULT '',                                      
                                                                             
   PRT_TLR     CHAR(7)      DEFAULT '',                                      
                                                                             
   RVS_LOG     CHAR(20)      DEFAULT ' ',                            
                                                                             
   RVS_DAT     DATE     ,                                                 
                                                                             
   RVS_NOD     CHAR(6)      DEFAULT '',                                      
                                                                             
   RVS_TLR     CHAR(7)      DEFAULT '',                                      
                                                                             
   RVS_TCK     CHAR(12)     DEFAULT '',                                      
                                                                             
   TCHK_NO     CHAR(20)      DEFAULT '00000000000000',               
                                                                             
   CHK_TIM     TIME    ,                                     
                                                                             
   CHK_FLG     CHAR(1)      DEFAULT '0',                                     
                                                                             
   INV_NO      CHAR(30)      DEFAULT ' ',                            
                                                                             
   BEG_DAT     DATE     ,                                     
                                                                             
   END_DAT     DATE     ,                                     
                                                                             
   CAR_NAME    CHAR(20)     DEFAULT ' ',                                     
                                                                             
   CAR_DZS     CHAR(10)     DEFAULT ' ',                                     
                                                                             
   CNT_STD     CHAR(10)     DEFAULT ' ',                                     
                                                                             
   FEE_STD     DECIMAL(18,2)     DEFAULT '000000000000000000',                       
                                                                             
   CORPUS     DECIMAL(18,2)     DEFAULT '000000000000000000',                        
                                                                             
   LATE_FEE    DECIMAL(18,2)     DEFAULT '000000000000000000',                           
                                                                             
   CLGS       CHAR(10)     DEFAULT ' ',                                           
                                                                             
   YYBZ       CHAR(10)     DEFAULT ' ',                                           
                                                                             
   STATUS     CHAR(01)      DEFAULT '0',                                  
                                                                                                                                                                    
   PRIMARY KEY (THD_KEY)                                                         
) IN "BBIP_APP"
INDEX IN  "BBIP_APP_INDEX";                                                                                                                                                               
                                                            
  comment on column GDEUPSB.TRSP_FEE_INFO.BR_NO   is                                                                                                             
 '���к�';                                                                                                                                                                                                                                                                                               
  comment on column GDEUPSB.TRSP_FEE_INFO.THD_KEY is                                                                                                             
 '��ѯ��ֵ';                                                                                                                                                                                                                                                                                           
  comment on column GDEUPSB.TRSP_FEE_INFO.CAR_TYP is                                                                                                             
 '�������';                                                                                                                                                                                                                                                                                              
  comment on column GDEUPSB.TRSP_FEE_INFO.CAR_NO   is                                                                                                            
 '���ƺ���';                                                                                                                                                                                                                                                                                            
  comment on column GDEUPSB.TRSP_FEE_INFO.TCUS_NM is                                                                                                             
 '����';                                                                                                                                                                                                                                                                                             
  comment on column GDEUPSB.TRSP_FEE_INFO.PAY_MON is                                                                                                             
 '�ɷ�����';                                                                                                                                                                                                                                                                                           
  comment on column GDEUPSB.TRSP_FEE_INFO.PAY_DAT is                                                                                                             
 '������������';                                                                                                                                                                                                                                                                                       
  comment on column GDEUPSB.TRSP_FEE_INFO.PAY_LOG is                                                                                                             
 '����������ˮ��';                                                                                                                                                  
  comment on column GDEUPSB.TRSP_FEE_INFO.PAY_NOD is                                                                                                             
 '֧�������';                                                                                                                                                                                                                                                                                            
  comment on column GDEUPSB.TRSP_FEE_INFO.PAY_TLR is                                                                                                             
 '֧����Ա��';                                                                                                                                                                                                                                                                                            
  comment on column GDEUPSB.TRSP_FEE_INFO.PAY_TCK is                                                                                                             
 '֧��������ˮ';                                                                                                                                                                                                                                                                                                                                                                                             
  comment on column GDEUPSB.TRSP_FEE_INFO.TXN_AMT is                                                                                      
 '�շѽ��';                                                                                                                                                                                                                                                                      
  comment on column GDEUPSB.TRSP_FEE_INFO.TXN_CNL is                                                                                      
 '��������';                                                                                                                                                                                                                                                                      
  comment on column GDEUPSB.TRSP_FEE_INFO.ACT_TYP is                                                                                      
 '�˺����� (1��һ��ͨ��2����ͨ���ۣ�4����)';                                                                                                                                                                                                                                      
  comment on column GDEUPSB.TRSP_FEE_INFO.ACT_NO  is                                                                                      
 '�ͻ��ʺ�';                                                                                                                                                                                                                                                                      
  comment on column GDEUPSB.TRSP_FEE_INFO.TACT_DT is                                                                                      
 '��ҵ����(��ӡ����)';                                                                                                                                                                                                                                                            
  comment on column GDEUPSB.TRSP_FEE_INFO.TLOG_NO is                                                                                      
 '��ӡ��Ʊ��ˮ��';                                                                                                                                                                                                                                                                
  comment on column GDEUPSB.TRSP_FEE_INFO.PRT_NOD is                                                                                      
 '��ӡ�����';                                                                                                                                                                                                                                                                    
  comment on column GDEUPSB.TRSP_FEE_INFO.PRT_TLR is                                                                                      
 '��ӡ��Ա��';                                                                                                                                                                                                                                                                    
  comment on column GDEUPSB.TRSP_FEE_INFO.RVS_LOG is                                                                                      
 '�˷���ˮ��';                                                                                                                                                                                                                                                                    
  comment on column GDEUPSB.TRSP_FEE_INFO.RVS_DAT is                                                                                      
 '�˷�����(�˷Ѻ��Ľ�������)';                                                                                                                                                                                                                                                    
  comment on column GDEUPSB.TRSP_FEE_INFO.RVS_NOD is                                                                                      
 '�˷������';                                                                                                                                                                                                                                                                    
  comment on column GDEUPSB.TRSP_FEE_INFO.RVS_TLR is                                                                                      
 '�˷ѹ�Ա��';                                                                                                                                                                                                                                                                    
  comment on column GDEUPSB.TRSP_FEE_INFO.RVS_TCK is                                                                                      
 '�˷�������ˮ';                                                                                                                                                                                                                                                                 
  comment on column GDEUPSB.TRSP_FEE_INFO.TCHK_NO is                                                                                      
 '��������';                                                                                                                                                                                                                                                                      
  comment on column GDEUPSB.TRSP_FEE_INFO.CHK_TIM is                                                                                      
 '����ʱ��';                                                                                                                                                                                                                                                                      
  comment on column GDEUPSB.TRSP_FEE_INFO.CHK_FLG is                                                                                      
 '���˱�־(0:δ����;1:�˶Գɹ�;2: �˶�ʧ�ܣ��ؼ���Ϣ��һ��,3�����е�����)';                                                                         
  comment on column GDEUPSB.TRSP_FEE_INFO.INV_NO   is                                                                                     
 '��Ʊ��';                                                                                                                                                                                                                                                                        
  comment on column GDEUPSB.TRSP_FEE_INFO.BEG_DAT  is                                                                                     
 '��Ч��ʼ����';                                                                                                                                                                                                                                                                  
  comment on column GDEUPSB.TRSP_FEE_INFO.END_DAT  is                                                                                     
 '��Ч��������';                                                                                                                                                                                                                                                                  
  comment on column GDEUPSB.TRSP_FEE_INFO.CAR_NAME is                                                                                     
 '����˵��';                                                                                                                                                                                                                                                                       
  comment on column GDEUPSB.TRSP_FEE_INFO.CAR_DZS  is                                                                                     
 '����������';                                                                                                                                                                                                                                                                     
  comment on column GDEUPSB.TRSP_FEE_INFO.CNT_STD  is                                                                                     
 '�Ʒѱ�׼';                                                                                                                                                                                                                                                                       
  comment on column GDEUPSB.TRSP_FEE_INFO.FEE_STD  is                                                                                     
 '��׼���շѶ�';                                                                                                                                                                                                                                                                  
  comment on column GDEUPSB.TRSP_FEE_INFO.CORPUS   is                                                                                     
 '����';                                                                                                                                                                                                                                                                          
  comment on column GDEUPSB.TRSP_FEE_INFO.LATE_FEE is                                                                                     
 '���ɽ��';                                                                                                                                                                                                                                                                       
  comment on column GDEUPSB.TRSP_FEE_INFO.CLGS      is                                                                                    
 '��������';                                                                                                                                                                                                                                                                       
  comment on column GDEUPSB.TRSP_FEE_INFO.YYBZ      is                                                                                    
 'Ӫ�˱�־';                                                                                                                                                                                                                                                                       
  comment on column GDEUPSB.TRSP_FEE_INFO.STATUS    is                                                                                    
 '�ɷ�״̬--0-�ɷ�,1-��Ʊ,2-�˷�,3-��Ʊ  '; 
 
 ------------------·�Ŵ��շ�Ƿ����Ϣ��
drop table  GDEUPSB.TRSP_PAY_INFO;
 create table GDEUPSB.TRSP_PAY_INFO
(
    BR_NO       CHAR(06)     NOT NULL,
   CAR_TYP     CHAR(03)     ,
   CAR_NO     CHAR(20)     NOT NULL,
   TCUS_NM     CHAR(60)     DEFAULT '',
   PAY_MON     CHAR(02)     ,
   ACT_DAT     DATE    ,
   THD_KEY     CHAR(20)  not null,
   TACT_DT    DATE    ,
   TXN_AMT     DECIMAL(18,2)     DEFAULT '000000000000000000',
   FLG        CHAR(01)      DEFAULT '0',
  BAK_FLD1             CHAR(30),
   BAK_FLD2             CHAR(30),
   BAK_FLD3             CHAR(30),
   RSV_FLD1             VARCHAR(300),
   PRIMARY KEY (THD_KEY)
)
 IN "BBIP_APP"
INDEX IN  "BBIP_APP_INDEX";

comment on column GDEUPSB.TRSP_PAY_INFO.BR_NO is
'���к�';

comment on column GDEUPSB.TRSP_PAY_INFO.CAR_TYP is
'�������';

comment on column GDEUPSB.TRSP_PAY_INFO.CAR_NO is
'���ƺ���';

comment on column GDEUPSB.TRSP_PAY_INFO.TCUS_NM is
'����';

comment on column GDEUPSB.TRSP_PAY_INFO.PAY_MON is
'�ɷ�����';

comment on column GDEUPSB.TRSP_PAY_INFO.ACT_DAT is
'��������';

comment on column GDEUPSB.TRSP_PAY_INFO.THD_KEY is
'��ѯ��ֵ';

comment on column GDEUPSB.TRSP_PAY_INFO.TACT_DT is
'��ҵ����';

comment on column GDEUPSB.TRSP_PAY_INFO.TXN_AMT is
'�շѽ��';

comment on column GDEUPSB.TRSP_PAY_INFO.FLG is
'�Ƿ�ɷ�״̬:0-δ�ɷ�,1-�ѽɷ�';


comment on column GDEUPSB.TRSP_PAY_INFO.BAK_FLD1 is
'�����ֶ�1';

comment on column GDEUPSB.TRSP_PAY_INFO.BAK_FLD2 is
'�����ֶ�2';

comment on column GDEUPSB.TRSP_PAY_INFO.BAK_FLD3 is
'�����ֶ�3';

comment on column GDEUPSB.TRSP_PAY_INFO.RSV_FLD1 is
'Ԥ���ֶ�1';

----------·�Ŵ��շѽ�����ˮ��

drop table GDEUPSB.TRSP_TXN_JNL;
create table GDEUPSB.TRSP_TXN_JNL                      
(
   BR_NO        CHAR(6)     NOT NULL,                   
                                                        
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
                                                        
   ACT_SQN      CHAR(5)      ,       
                                                        
   NOD_NO       CHAR(6)     DEFAULT ' ',                
                                                  
   TLR_ID       CHAR(7)     DEFAULT ' ',                
                                                        
   TRM_NO       CHAR(7)     DEFAULT ' ',                
                                                        
   NOD_TRC      CHAR(20)    DEFAULT ' ',                
                                                        
   TXN_CNL      CHAR(3)     DEFAULT ' ',                
                                                        
   ITG_TYP      CHAR(1)    ,                   
                                                        
   FTXN_TM      TIME    ,                   
                                                        
   FRSP_CD      CHAR(6)     ,                   
                                                        
   HLOG_NO      CHAR(9)     DEFAULT ' ',                
                                                        
   HRSP_CD      CHAR(6)     DEFAULT ' ',                
                                                        
   HTXN_ST      CHAR(1)     DEFAULT 'U',                
                                                        
   TCK_NO       CHAR(12)    DEFAULT ' ',                
                                                        
   HTXN_CD      CHAR(6)     DEFAULT ' ' ,       
                                                        
   HTXN_SB      CHAR(3)     DEFAULT ' ' ,       
                                                        
   TXN_SRC      CHAR(5)     DEFAULT ' ' ,       
                                                        
   CAR_TYP      CHAR(03)    ,                  
                                                        
   CAR_NO      CHAR(20)    DEFAULT ' ',                 
                                                        
   PAY_MON      CHAR(02)     ,                  
                                                        
   TCUS_NM      CHAR(60)    DEFAULT ' ',                
                                                        
   TACT_DT      DATE    ,                
                                                        
   TLOG_NO      CHAR(30)    DEFAULT ' ',                
                                                        
   THD_KEY      CHAR(20)    DEFAULT ' ',                
                                                        
   TRSP_CD      CHAR(3)    DEFAULT ' ',                 
                                                        
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
) IN "BBIP_APP"
INDEX IN  "BBIP_APP_INDEX";                                                      
                            
comment on column GDEUPSB.TRSP_TXN_JNL.BR_NO is                                                                                                                                
'���к�';                                                                                                                                                                       
                                                                                                                                                                                                                  
comment on column GDEUPSB.TRSP_TXN_JNL.SQN is                                                                                                                                             
'��ˮ��';                                                                                                                                                                       
                                                                                                                                                                                                                  
comment on column GDEUPSB.TRSP_TXN_JNL.TTXN_CD is                                                                                                                             
'����������';                                                                                                                                                                     
                                                                                                                                                                                                                  
comment on column GDEUPSB.TRSP_TXN_JNL.TXN_COD is                                                                                                                                       
 '������';                                                                                                                                                                      
                                                                                                                                                                                                                  
comment on column GDEUPSB.TRSP_TXN_JNL.TXN_TYP is                                                                                                                             
 '��������';                                                                                                                                                                        
                                                                                                                                                                                                                  
comment on column GDEUPSB.TRSP_TXN_JNL.BUS_TYP is                                                                                                                                          
'ҵ������';                                                                                                                                                                    
                                                                                                                                                                                                                  
comment on column GDEUPSB.TRSP_TXN_JNL.CRP_COD is                                                                                                                             
'��λ����';                                                                                                                                                                                
                                                                                                                                                                                                                  
comment on column GDEUPSB.TRSP_TXN_JNL.ACT_DAT is                                                                                                                                            
 '�������';                                                                                                                                                                    
                                                                                                                                                                                                                  
comment on column GDEUPSB.TRSP_TXN_JNL.TXN_AMT is                                                                                                                             
'���׽��';                                                                                                                                                                                   
                                                                                                                                                                                                                  
comment on column GDEUPSB.TRSP_TXN_JNL.FEE is                                                                                                                                                 
'����������';                                                                                                                                                                  
                                                                                                                                                                                                                  
comment on column GDEUPSB.TRSP_TXN_JNL.ACT_TYP is                                                                                                                              
'�˺����� (1��һ��ͨ��2����ͨ���ۣ�4����)';                                                                                                                                                     
                                                                                                                                                                                                                  
comment on column GDEUPSB.TRSP_TXN_JNL.ACT_NO is                                                                                                                               
'�ͻ��ʺ�';                                                                                                                                                                    
 comment on column GDEUPSB.TRSP_TXN_JNL.ACT_SQN is                                                                                                                                            
'�˺�˳���';                                                                                                                                                                  
comment on column GDEUPSB.TRSP_TXN_JNL.NOD_NO is                                                                                                                                         
'���������';                                                                                                                                                                  
 comment on column GDEUPSB.TRSP_TXN_JNL.TLR_ID is                                                                                                                                              
 '���й�Ա��';                                                                                                                                                                  
                                                                                                                                                                                                                  
comment on column GDEUPSB.TRSP_TXN_JNL.TRM_NO is                                                                                                                              
'�ն˺�';                                                                                                                                                                                    
comment on column GDEUPSB.TRSP_TXN_JNL.NOD_TRC is                                                                                                                             
 '������ˮ��';                                                                                                                                                                                   
comment on column GDEUPSB.TRSP_TXN_JNL.TXN_CNL is                                                                                                                             
'��������';                                                                                                                                                                                  
comment on column GDEUPSB.TRSP_TXN_JNL.ITG_TYP is                                                                                                                             
 '����������';                                                                                                                                                                                
comment on column GDEUPSB.TRSP_TXN_JNL.FTXN_TM is                                                                                                                             
'ǰ�ý���ʱ��';                                                                                                                                                                               
comment on column GDEUPSB.TRSP_TXN_JNL.FRSP_CD is                                                                                                                             
 'ǰ����Ӧ��';                                                                                                                                                                         
comment on column GDEUPSB.TRSP_TXN_JNL.HLOG_NO is                                                                                                                             
 '������ˮ��';                                                                                                                                                                                 
comment on column GDEUPSB.TRSP_TXN_JNL.HRSP_CD is                                                                                                                             
'������Ӧ��';                                                                                                                                                                       
comment on column GDEUPSB.TRSP_TXN_JNL.HTXN_ST is                                                                                                                             
'��������״̬:U-��ʼ,T-��ʱ,F-ʧ��,S-�ɹ�,C-Ĩ��';                                                                                                                                       
comment on column GDEUPSB.TRSP_TXN_JNL.TCK_NO  is                                                                                                                             
'�����ˮ��';                                                                                                                                                                                       
comment on column GDEUPSB.TRSP_TXN_JNL.HTXN_CD is                                                                                                                             
 '����������';                                                                                                                                                                               
comment on column GDEUPSB.TRSP_TXN_JNL.HTXN_SB is                                                                                                                             
 '��������';                                                                                                                                                                                  
 comment on column GDEUPSB.TRSP_TXN_JNL.TXN_SRC is                                                                                                                             
'������ϵͳ��ʶ';                                                                                                                                                                           
comment on column GDEUPSB.TRSP_TXN_JNL.CAR_TYP is                                                                                                                             
'�������';                                                                                                                                                                               
 comment on column GDEUPSB.TRSP_TXN_JNL.CAR_NO  is                                                                                                                             
'���ƺ���';                                                                                                                                                                                        
comment on column GDEUPSB.TRSP_TXN_JNL.PAY_MON is                                                                                                                             
'�ɷ�����';                                                                                                                                                                                              
comment on column GDEUPSB.TRSP_TXN_JNL. TCUS_NM  is                                                                                                                           
 '������';                                                                                                                                                                                      
comment on column GDEUPSB.TRSP_TXN_JNL. TACT_DT  is                                                                                                                           
'��������������';                                                                                                                                                                               
comment on column GDEUPSB.TRSP_TXN_JNL. TLOG_NO  is                                                                                                                           
'��������ˮ��';                                                                                                                                                                                     
comment on column GDEUPSB.TRSP_TXN_JNL. THD_KEY  is                                                                                                                           
'��������ֵ';                                                                                                                                                                      
comment on column GDEUPSB.TRSP_TXN_JNL. TRSP_CD  is                                                                                                                           
'��������Ӧ��';                                                                                                                                                                             
comment on column GDEUPSB.TRSP_TXN_JNL.TTXN_ST is                                                                                                                           
 '����������״̬';                                                                                                                                                                     
comment on column GDEUPSB.TRSP_TXN_JNL.TXN_ST is                                                                                                                            
 '����״̬ U:��ʼ״̬��S �ɹ���F ʧ�ܣ�C Ĩ�ˣ�T ��ʱ��X δ����';                                                                                                                             
comment on column GDEUPSB.TRSP_TXN_JNL.TXN_ATR is                                                                                                                           
 '��������';                                                                                                                                                                      
comment on column GDEUPSB.TRSP_TXN_JNL.TXN_MOD is                                                                                                                           
 '���׷�ʽ(0���ֽ�1��һ��ͨ��2����ͨ���ۣ�4������5��֧Ʊ)';                                                                                                                                 
comment on column GDEUPSB.TRSP_TXN_JNL.PAY_MOD is                                                                                                                           
 '֧����ʽ(1��ƾ��֧ȡ,2��ƾӡ֧ȡ,3��ƾ֤֧ȡ,4������֧ȡ,5��ǩ��֧ȡ,6��֧������,7��������ӡ,0��������)';                                                                                    
comment on column GDEUPSB.TRSP_TXN_JNL.CAGT_NO is                                                                                                                           
 '�̻�Э����';                                                                                                                                                                                
comment on column GDEUPSB.TRSP_TXN_JNL.TACT_NO is                                                                                                                              
'��λ�����˺�(����ʱΪ�����ʻ�������ʱΪ�����ʻ���) ';                                                                                                                         
comment on column GDEUPSB.TRSP_TXN_JNL.TXN_DAT is                                                                                                                            
'��������';                                                                                                                                                                    
comment on column GDEUPSB.TRSP_TXN_JNL.RVS_RSP is                                                                                                                            
 '����Ĩ�˽��(���һ���з��ص�Ĩ�˽��)';                                                                                                                                      
comment on column GDEUPSB.TRSP_TXN_JNL.INV_NO  is                                                                                                                            
 '��Ʊ��';                                                                                                                                                                      
comment on column GDEUPSB.TRSP_TXN_JNL.CTBL_NM is                                                                                                                            
 '��չ�ӱ���';                                                                                                                                                                  
 comment on column GDEUPSB.TRSP_TXN_JNL.EXT_KEY is                                                                                                                            
 '��չ�ӱ��ֵ';         
 comment on column GDEUPSB.TRSP_TXN_JNL.BAK_FLD1 is
'�����ֶ�1';

comment on column GDEUPSB.TRSP_TXN_JNL.BAK_FLD2 is
'�����ֶ�2';

comment on column GDEUPSB.TRSP_TXN_JNL.BAK_FLD3 is
'�����ֶ�3';

comment on column GDEUPSB.TRSP_TXN_JNL.RSV_FLD1 is
'Ԥ���ֶ�1';                                                                                                                                                       
                                                                                                                                                                                                                  
----------------·�Ŵ��շѸ��ķ�Ʊ�ǼǱ�

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
                                           
   NOD_NO      char(6)      default '',         
                                           
   TLR_ID      char(7)      default '',    
                                           
   primary key (SQN)                                       
) IN "BBIP_APP"
INDEX IN  "BBIP_APP_INDEX";                                                           
                                                             
               comment on column GDEUPSB.TRSP_INV_CHG_INFO.SQN         is    
         '��ˮ��';                                                         
             comment on column GDEUPSB.TRSP_INV_CHG_INFO.THD_KEY is        
         '�ɷѼ�ֵ';                                                       
             comment on column GDEUPSB.TRSP_INV_CHG_INFO.TLOG_NO is        
        'ԭ�ɷѵ�������ˮ��';                                              
             comment on column GDEUPSB.TRSP_INV_CHG_INFO.INV_NO   is       
        '�·�Ʊ��';                                                        
             comment on column GDEUPSB.TRSP_INV_CHG_INFO.OINV_NO   is      
     '��Ʊ��';                                                             
             comment on column GDEUPSB.TRSP_INV_CHG_INFO.CAR_TYP   is      
  '��������';                                                              
             comment on column GDEUPSB.TRSP_INV_CHG_INFO.CAR_NO     is     
    '��������';                                                            
             comment on column GDEUPSB.TRSP_INV_CHG_INFO.TACT_DT   is      
  'ԭ������';                                                              
             comment on column GDEUPSB.TRSP_INV_CHG_INFO.ACT_DAT    is     
 '�������';                                                               
             comment on column GDEUPSB.TRSP_INV_CHG_INFO.NOD_NO      is    
    '�����';                                                              
             comment on column GDEUPSB.TRSP_INV_CHG_INFO.TLR_ID       is   
   '��Ա��';    
   
   ---------------��Ʊ�����
   drop table GDEUPSB.TRSP_NP_MANAG;
   CREATE TABLE GDEUPSB.TRSP_NP_MANAG                     
(
   NOD_NO      CHAR(6)  ,                                                       
                                                        
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
     '�����';                               
       comment on column GDEUPSB.TRSP_NP_MANAG.SQN   is             
     '����ͨ�ɷ���ˮ��';                                                      
      comment on column GDEUPSB.TRSP_NP_MANAG.ID_NO       is             
     '��Ʊ��ʶ��';                                                        
       comment on column GDEUPSB.TRSP_NP_MANAG.CAR_NO     is            
     '���ƺ�';                                                            
       comment on column GDEUPSB.TRSP_NP_MANAG.INV_NO   is             
     '��Ʊ��';                                                    
       comment on column GDEUPSB.TRSP_NP_MANAG.PRT_DAT   is             
     '��ӡ����';                                                    
   comment on column GDEUPSB.TRSP_NP_MANAG.BEG_DAT   is                 
     '��Ч���ڿ�ʼ����';                                                        
       comment on column GDEUPSB.TRSP_NP_MANAG.END_DAT   is             
     '��Ч���ڽ�������';                                                        
       comment on column GDEUPSB.TRSP_NP_MANAG.STATUS  is             
     '��Ʊ״̬ U-��ʼ״̬ 0-��ӡ 1-����';                                   
       comment on column GDEUPSB.TRSP_NP_MANAG.TLR_ID is             
     '��Ա��';         
     
  --------------------���˹����
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
     '�����';                               
       comment on column GDEUPSB.TRSP_ZZ_MANAG.ZZ_DAT   is             
     '��������';                                                      
      comment on column GDEUPSB.TRSP_ZZ_MANAG.FLG       is             
     '״̬��ʶ';                                                        
                                                           
                                                                        
drop table  gdeupsb.Gds_Agt_Water;   --ˮ��Э����ϸ��
create table gdeupsb.Gds_Agt_Water
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
  Bnk_Nam   char(60)  default '',
--��������
  Org_Cod   char(12)  default '',
--��λ����
  Org_Nam   char(60)  default '',
--��λ����
  TBus_Tp   char(5)   default '',
--ҵ������
  TCus_Id   char(20)  not null,
--�ͻ���ʶ
  TCus_Nm   char(60)  default '',
--�ͻ���ʶ
  Eff_Dat   char(8)   default '',
--��Ч����
  Ivd_Dat   char(8)   default '',
--ʧЧ����
  LEr_Msg   char(60)  default '',
--����Э����ʾ
  TEr_Msg   char(60)  default '',
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

--�㶫ʡ����ҵ����Ʊ�
drop table GDEUPSB.Gds_Run_Ctl;
create table GDEUPSB.Gds_Run_Ctl
(
  Gds_BId   char(6)   not null,
--����ҵ��ID( 3λ��������+1λ���б�ʶ+2λ��ţ����з��б�ʶ0-ȫʡ�� 1-�㶫 2-�麣.....)
  Gds_BNm   char(60)  not null,
--����ҵ������
  Agt_MTb   char(30)  default '',
--Э������
  Agt_STb   char(30)  default '',
--Э���ӱ�
  Jnl_MTb   char(12)  default '',
--��ˮ����
  Jnl_STb   char(12)  default '',
--��ˮ�ӱ�
--���̿���Ҫ��
--���̿���Ҫ��
--���̿���Ҫ��
  Ath_Flg   char(2)   default '00',
--��Ȩ����Ȩ����( 00--����Ȩ  XX--��Ӧ��Ȩ���� )
  Nam_Chk   char(1)   default '0',
--����У��( 0-��У�� 1-У�� )
  Pin_Chk   char(1)   default '0',
--��������У��( 0-��У�� 1-У����˽������� )
  Psw_Chk   char(1)   default '0',
--֧������У��( 0-��У�� 1-У��Թ�֧������ )
  Lcl_Chk   char(1)   default '0',
--���ظ��Ի�ҵ�������̡�����Ӧ����֤�ȡ�( 0-��  1-ͬ����֤ 2-�첽��֤ )
  Lcl_Svr   char(8)   default '',
--���ط���
  Lcl_Cod   char(6)   default '',
--���ؽ���
  Thd_Chk   char(1)   default '0',
--���������Ի�ҵ�������̡���������֤�ȡ�( 0-��  1-ͬ����֤ 2-�첽��֤ )
  Thd_Svr   char(8)   default '',
--�ⷢ����
  Thd_Cod   char(6)   default '',
--�ⷢ����
  Agt_Br    char(6)   not null,
--������к�
  PRIMARY KEY( Gds_BId )
)
 IN "BBIP_APP"
INDEX IN  "BBIP_APP_INDEX";
--
--
--�㶫ʡ����ҵ��Э������
--
drop table GDEUPSB.Gds_Agt_Inf;
create table GDEUPSB.Gds_Agt_Inf
(
  Agt_Sts   char(1)   default '0',
--״̬
  Gds_BId   char(5)   not null,
--����ҵ��ID
  BCus_No   char(13)  default '',
--�ͻ���
  Act_No    char(21)  not null, 
--Э��ҵ�����˺�
  Act_Typ   char(1)   not null,
--�˻�����
  Act_Nm    char(60)  default '', 
--Э��ҵ���˺Ż���
  Vch_Typ   char(3)   default '',
--ƾ֤����
  Vch_No    char(8)   default '',
--ƾ֤����
  Bok_Seq   char(5)   default '',
--һ��ͨ���
  Pfa_Sub   char(3)   default '',
--��������
  BCus_Id   char(30)  default '',
--Ԥ�㵥λ����
  Id_Typ    char(2)   default '21',
--֤������
  Id_No     char(30)  default '',
--֤������
  Tel_Typ   char(1)   default '',
--�̻�����
  Tel_No    char(30)  default '',
--�̻�����
  Mob_Typ   char(1)   default '',
--�ƻ�����
  Mob_Tel   char(30)  default '',
--�ƻ�����
  EMail    char(60)  default '',
--�ʼ�
  Addr     char(120) default '',
--��ַ
  Nod_No    char(6)   default '',
--��������
  BrNo     char(6)   not null,
--���к�
  Eff_Dat   char(8)   not null,
--��Ч����
  Ivd_Dat   char(8)   default '',
--ͣ������
 PRIMARY KEY( Gds_BId,Act_No)
) IN "BBIP_APP"
INDEX IN  "BBIP_APP_INDEX";

drop table gdeupsb.Gds_Agt_Trc;
create table gdeupsb.Gds_Agt_Trc
(
  Opr_Tim  char(14)  not null,
--����ʱ��(����ʱ��)
  Act_Dat  char(8)   not null,
--�������
  Txn_Cnl  char(3)   not null,
--��������
  Tlr_Id   char(7)   not null,
--����Ա
  Sup1_Id  char(7)   default '',
--��ȨԱ1
  Sup2_Id  char(7)   default '',
--��ȨԱ2
  Act_No   char(21)  not null,
--�˺�
  Nod_No   char(6)   default '',
--����
  BrNo    char(6)   default '',
--��������
  TTxn_Cd  char(25)   default '',
--���뽻����
  Txn_Cod  char(25)   default '',
--�������� 
  Prv_Dat clob(3072) default ''
)
 IN "BBIP_APP"
INDEX IN  "BBIP_APP_INDEX";

create index ni1_GdsAgtTrc on gdeupsb.Gds_Agt_Trc( Act_No );


--ȼ��ÿ�춯̬Э���û��������ÿ�����460701�ӽ�ȥ
--�ֶ�				ICS				EUPS
--==================================
--�û����		UserNo		CusNo
--ָ��				TCommd		TCommd
--�����˺�		ActNo			CusAc
--����������	ActNam		CusNme
--�˺�����		ActTyp		ACC_TYP
--��������		optDat		
--������Ա		optNod		
--֤������		IdType		IdTyp
--֤������		IdNo			IdNo
--��ϵ������	cntNam		ThdCusNam
--�ֻ�����		TelNo			CmuTel
--��ϵ�˵�ַ	CntAdr		ThdCusAdr

drop table GDEUPSB.GDEUPS_GAS_CUS_DAY;
create table GDEUPSB.GDEUPS_GAS_CUS_DAY
(
	SEQUENCE CHAR(32) NOT NULL,
  CUS_NO  char(12) not null,
--�û����
  T_Commd  char(10) not null,
--ָ��(Add Ϊ�����û�Э�� Edit Ϊ�޸��û�Э�� Stop Ϊȡ���û�Э��)
  CUS_AC    char(32) not null,  
--�����˺�
  CUS_NME   char(32) not null,
--����������
  ACC_TYP    char(32),  
--�˺�����(0�Թ�1��˽����һ��ͨ2��˽��ͨ��4��˽��)
  OPT_DAT    char(10),  
--��������
	OPT_NOD    char(8),  
--������Ա
  ID_TYP   char(2),
--֤������
  ID_NO     char(32),
--֤������
  THD_CUS_NAM     char(32),
--��ϵ������
  CMU_TEL     char(50),
--��ϵ���ֻ�����
  THD_CUS_ADR     char(100),
--��ϵ�˵�ַ

PRIMARY KEY(SEQUENCE)
)IN "BBIP_APP"
INDEX IN  "BBIP_APP_INDEX";


drop table GDEUPSB.GDEUPS_FBPE_FILE_BATCH_TMP;
create table GDEUPSB.GDEUPS_FBPE_FILE_BATCH_TMP
(
  SQN     char(20)    not null default ' ',
--������ˮ��
  Txn_NO    char(3)    not null default ' ',
--������
  ORG_CDE    char(3)    default ' ',
--������
  TLR_No    char(6)      default ' ',
--����Ա��
  Txn_Tim    char(14)    default ' ',
--����ʱ��
  CUS_NO    char(20)     default ' ',
--�ͻ���
  Acc_No  char(10)      default ' ',
--�˵���
  Cus_Ac    char(20)     default ' ',
--�ͻ�����
  Cus_Nam    char(20)    default ' ',
--�ͻ�����
 Txn_Amt  char(10)     default ' ',
--���׽��
 Acc_Amt  char(10)     default ' ',
--���˽��
  Act_No  char(20)     default ' ',
--�����˺�
  bank_No  char(6)     default ' ',
--���б��
  bank_Nam  char(30)     default ' ',
--��������
  Cos_Mon  char(6)     default ' ',
--�����·�

   RSV_FLD1             VARCHAR(300) default ' ',
   RSV_FLD2             VARCHAR(300) default ' ',
   RSV_FLD3             VARCHAR(300) default ' ',
   RSV_FLD4             VARCHAR(300) default ' ',
   RSV_FLD5             VARCHAR(300) default ' ',
   RSV_FLD6             VARCHAR(300) default ' ',
   RSV_FLD7             VARCHAR(300) default ' ',
   RSV_FLD8             VARCHAR(300) default ' ',
--Ԥ���ֶ�
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
--��ɽ�ļ�����ϵͳ-�������NELE��ʱ��
create table GDEUPSB.GDEUPS_FBPD_NELE_BATCH_TMP (
SQN CHAR(32) NOT NULL,
BAT_FLG CHAR(20),
--����������Ϣ��ʶ
CCY CHAR(3),
--���ҷ���
PAY_FLG CHAR(1) default '1',
--���ո���־
FEE_TYP CHAR(3),
--�������
TOT_CNT CHAR(10),
--�ܼ�¼��
TOT_AMT CHAR(14),
--�ܽ��
SUB_DTE CHAR(8),
--����������/�ύ����
TXN_DTE CHAR(8),
--Ӧ��������
THD_SQN CHAR(16),
--��ˮ��
BK CHAR(12),
--���д���
SER_NO CHAR(10),
--���
PAY_FEE_NO  CHAR(32),
--�ɷѺ�
BR CHAR(12),
--�ͻ��������к�
CUS_AC CHAR(32),
--�ʺ�
CUS_NME CHAR(40),
--�ͻ��˺�����
TXN_AMT CHAR(12),
--���
TXN_TYP CHAR(1),
--�ۿʽ
CUS_DPM CHAR(8),
--�û����ڲ���
FEE_MON CHAR(6),
--����·�
FEE_CNT CHAR(2),
--��Ѵ���
THD_CUS_NME CHAR(40),
--�ͻ�����
ELE_AC CHAR(32),
--������ҵ�˺�
FRZ_AMT CHAR(12),
--Ӧ�۵��
F_SEQ_NO CHAR(120),
--ΥԼ��
CRP_COD CHAR(5),
--�������
EVID_NO CHAR(16),
--Ӧ��ƾ֤��
RS_FLD1 CHAR(128),
--����˵��
RS_FLD2 CHAR(128),
RS_FLD3 CHAR(40),
--˵��
RS_FLD4 CHAR(128),
RS_FLD5 CHAR(128),
PRIMARY KEY (SQN)
)
 IN "BBIP_APP"
INDEX IN  "BBIP_APP_INDEX";

drop table GDEUPSB.GDEUPS_FBPD_MPOS_BATCH_TMP;
--��ɽ�ļ�����ϵͳ-�ƶ�POS��ʱ��
CREATE TABLE GDEUPSB.GDEUPS_FBPD_MPOS_BATCH_TMP(
SQN CHAR(32) NOT NULL,
THD_SQN CHAR(32) NOT NULL,
--ˢ������ϵͳ���ٺ�
POS_NO CHAR(20),
--֧���ն˱��
COM_AC CHAR(32),
--ǩԼ�̻�����
COM_NO CHAR(32),
--ˢ���̻����
CUS_AC CHAR(32),
--ˢ������
TXN_DTE CHAR(12),
--ˢ����������
TXN_TME CHAR(12),
--ˢ������ʱ��
TXN_FEE CHAR(32),
--ˢ�����
TXN_AMT CHAR(32),
--����
TXN_CHR CHAR(32),
--������
TXN_RNT CHAR(32),
--���
CHK_DATE CHAR(12),
--��������
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
--��ɽ�ļ�����ϵͳ-����ҵ����ʱ��
CREATE TABLE GDEUPSB.GDEUPS_FBPD_OBUS_BATCH_TMP(
SQN CHAR(32) NOT NULL,
COM_NO CHAR(20),
--������/��λ����/��λ���
CUS_AC CHAR(26),
--�˺�
CUS_NO CHAR(32),
--����
CUS_NME CHAR(30),
--����
THD_CUS_NO CHAR(20),
--�ͻ����
CCY CHAR(3),
--����
TXN_AMT CHAR(13),
--���׽��
SUC_FLG CHAR(1),
--��־ Y-�ɹ�  ��ʶ 1�ɹ� 2ʧ��???
CHR_DTE CHAR(25),
--��������
SUB_DTE CHAR(10),
--�ύ����
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
--����ȼ������������ʱ��
CREATE TABLE GDEUPSB.GDEUPS_GASH_BATCH_TMP(
 SQN CHAR(32) NOT NULL,
 --��ˮ��
 BAT_NO CHAR(32),
 --���κ�
 BAT_STS CHAR(1),
 --����״̬
 PKG_FLG CHAR(1),
 --�������ձ�־0��ʾ����1��ʾ����δ����2��ʾ���������ѷ���
  TOT_CNT CHAR(12),
 --�ܱ���
 TOT_AMT CHAR(18),
 --�ܽ��
 SUC_TOT_CNT CHAR(12),
 --�ɹ��ܱ���
 SUC_TOT_AMT CHAR(18),
 --�ɹ��ܽ��
 TXN_DTE DATE,
 --��������
 TXN_TME TIMESTAMP,
 --����ʱ��
 THD_SQN CHAR(32),
 --ȼ��������ˮ��
 BK CHAR(4),
 --���б�ʶ
 CUS_NO CHAR(20),
 --�û�����
 CUS_NME CHAR(32),
 --�û���
 CUS_AC CHAR(32),
 --�����˺�
 PAY_MON CHAR(6),
 --��������
 REQ_TXN_AMT CHAR(18),
 --Ӧ�ɷ���
 TXN_AMT CHAR(18),
 --���׽��ѿ۷��ã�
 TMP_FLD1 CHAR(32),
 --�����ֶ�1
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
    '�������ո���ʱ��';
COMMENT ON COLUMN GDEUPSB.GDEUPS_ELE_TMP.SQN
IS
    '��ˮ��';
COMMENT ON COLUMN GDEUPSB.GDEUPS_ELE_TMP.BANK_NO
IS
    '���д���';
COMMENT ON COLUMN GDEUPSB.GDEUPS_ELE_TMP.COM_NO
IS
    '��λ����';
COMMENT ON COLUMN GDEUPSB.GDEUPS_ELE_TMP.FILE_DTE
IS
    '�ļ�����';
COMMENT ON COLUMN GDEUPSB.GDEUPS_ELE_TMP.CCY
IS
    '����';
COMMENT ON COLUMN GDEUPSB.GDEUPS_ELE_TMP.BUS_KND
IS
    '�շѷ�ʽ';
COMMENT ON COLUMN GDEUPSB.GDEUPS_ELE_TMP.NUMBER
IS
    '���';
COMMENT ON COLUMN GDEUPSB.GDEUPS_ELE_TMP.THD_CUS_NO
IS
    '�ɷѺ�';
COMMENT ON COLUMN GDEUPSB.GDEUPS_ELE_TMP.THD_CUS_NME
IS
    '���㻧����';
COMMENT ON COLUMN GDEUPSB.GDEUPS_ELE_TMP.CUS_AC
IS
    '�ۿ��˻�';
COMMENT ON COLUMN GDEUPSB.GDEUPS_ELE_TMP.CUS_NME
IS
    '�ۿ��˻�����';
COMMENT ON COLUMN GDEUPSB.GDEUPS_ELE_TMP.FULL_DED_FLAG
IS
    '���ֽɷѱ�־';
COMMENT ON COLUMN GDEUPSB.GDEUPS_ELE_TMP.PAY_TYPE
IS
    '��������';
COMMENT ON COLUMN GDEUPSB.GDEUPS_ELE_TMP.ACCOUNT_NO
IS
    '������ˮ��';
COMMENT ON COLUMN GDEUPSB.GDEUPS_ELE_TMP.ELECTRICITY_YEARMONTH
IS
    '����·�';
COMMENT ON COLUMN GDEUPSB.GDEUPS_ELE_TMP.PAYMENT_MONEY
IS
    'Ӧ�۽��';
COMMENT ON COLUMN GDEUPSB.GDEUPS_ELE_TMP.CAPITIAL
IS
    '����';
COMMENT ON COLUMN GDEUPSB.GDEUPS_ELE_TMP.DEDIT
IS
    'ΥԼ��';
COMMENT ON COLUMN GDEUPSB.GDEUPS_ELE_TMP.PAYMENT_RESULT
IS
    '�ۿ������� 1 �ѿ�
2 �Ѳ��ֿۿ�
3 ����
4 �˻�����
5 �˻�����
6 ���˻�������, �����ѿۡ��������۲�����
7 ֱ�ӽ��֧���еĽ������ȹ涨�޶�
8 ֱ�ӽ������Ȩ��¼'
    ;
COMMENT ON COLUMN GDEUPSB.GDEUPS_ELE_TMP.TXN_AMT
IS
    'ʵ�۽��';
COMMENT ON COLUMN GDEUPSB.GDEUPS_ELE_TMP.BANK_SQN
IS
    '�����շ���ˮ��';
COMMENT ON COLUMN GDEUPSB.GDEUPS_ELE_TMP.TXN_DTE
IS
    '�ۿ�����';
COMMENT ON COLUMN GDEUPSB.GDEUPS_ELE_TMP.TXN_TME
IS
    '�ۿ�ʱ��';
COMMENT ON COLUMN GDEUPSB.GDEUPS_ELE_TMP.BAK_FLD
IS
    '��ע';
COMMENT ON COLUMN GDEUPSB.GDEUPS_ELE_TMP.RSV_FLD1
IS
    'Ԥ���ֶ�1';
COMMENT ON COLUMN GDEUPSB.GDEUPS_ELE_TMP.RSV_FLD2
IS
    'Ԥ���ֶ�2';
COMMENT ON COLUMN GDEUPSB.GDEUPS_ELE_TMP.RSV_FLD3
IS
    'Ԥ���ֶ�3';
COMMENT ON COLUMN GDEUPSB.GDEUPS_ELE_TMP.RSV_FLD4
IS
    'Ԥ���ֶ�4';
COMMENT ON COLUMN GDEUPSB.GDEUPS_ELE_TMP.RSV_FLD5
IS
    'Ԥ���ֶ�5';

drop table GDEUPSB.TRSP_CHECK_TMP;
CREATE
    TABLE GDEUPSB.TRSP_CHECK_TMP
    (
        SQN CHARACTER(20) NOT NULL,
        TCHK_NO CHARACTER(14),
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

