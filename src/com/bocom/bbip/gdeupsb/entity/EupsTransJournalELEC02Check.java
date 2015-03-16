package com.bocom.bbip.gdeupsb.entity;

import java.math.BigDecimal;
import java.util.Date;

import com.bocom.bbip.data.annotation.GeneratedValue;
import com.bocom.bbip.data.annotation.Id;
import com.bocom.bbip.eups.entity.EupsTransJournal;
import com.bocom.bbip.utils.ContextUtils;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.core.Context;

public class EupsTransJournalELEC02Check {
	  @Id
	  @GeneratedValue
	  private String sqn;
	  private String bk;
	  private String pfeTyp;
	  private String itgTyp;
	  private String qrySqn;
	  private String traceNo;
	  private String reqSysCde;
	  private String reqJrnNo;
	  private Date reqTme;
	  private String br;
	  private String orgCde;
	  private String tmlNo;
	  private String txnTlr;
	  private String svrNme;
	  private String comNo;
	  private String eupsBusTyp;
	  private String rapTyp;
	  private Date txnDte;
	  private Date txnTme;
	  private String txnCde;
	  private String txnSts;
	  private String rspMsg;
	  private String oldTxnSqn;
	  private String thdObkCde;
	  private String rvsTraceNo;
	  private String thdSubCusNo;
	  private String thdFeePrd;
	  private String bkCdTyp;
	  private BigDecimal txnAmt;
	  private String ccy;
	  private String chlTyp;
	  private String athTlrNo;
	  private String chkTlr;
	  private String reqVchNo;
	  private String busKnd;
	  private String rspCde;
	  private String mfmTxnCde;
	  private String mfmTxnSubCde;
	  private Date acDte;
	  private String mfmVchNo;
	  private String mfmJrnNo;
	  private String mfmTxnSts;
	  private String mfmRspCde;
	  private String mfmRspMsg;
	  private String thdTxnCde;
	  private Date thdTxnDte;
	  private Date thdTxnTme;
	  private String thdSqn;
	  private String thdTxnSts;
	  private String thdRspCde;
	  private String thdRspMsg;
	  private String rcnFlg;
	  private String mfmDytRcnFlg;
	  private String mfmEodRcnFlg;
	  private String mfmRcnBat;
	  private Date mfmRcnTme;
	  private String thdRcnFlg;
	  private Date thdRcnTme;
	  private String thdRcnBat;
	  private String isBatFlg;
	  private String batNo;
	  private String extRcnFlg;
	  private String ourOthFlg;
	  private String thdRgnNo;
	  private String thdCusNo;
	  private String thdCusNme;
	  private String payChl;
	  private String splNo;
	  private String splTerNo;
	  private String txnTyp;
	  private String cusAc;
	  private String cusNme;
	  private String idTyp;
	  private String idNo;
	  private String cmuTel;
	  private String accTyp;
	  private String accSeq;
	  private String bvKnd;
	  private String bvNo;
	  private String lisAcc;
	  private String choNo;
	  private String rmkCde;
	  private String cshNo;
	  private String drwMde;
	  private BigDecimal reqTxnAmt;
	  private String fulDedFlg;
	  private BigDecimal hfe;
	  private String hfeItmSeq;
	  private String hfePstBr;
	  private String prtFlg;
	  private Integer prtCnt;
	  private String bakFld1;
	  private String bakFld2;
	  private String bakFld3;
	  private String bakFld4;
	  private String bakFld5;
	  private String bakFld6;
	  private String rsvFld1;
	  private String rsvFld2;
	  private String rsvFld3;
	  private String rsvFld4;
	  private String rsvFld5;
	  private String rsvFld6;
	  private String rsvFld7;
	  private String rsvFld8;
	  private String filFlg;
	  private BigDecimal totalAmt;
	  private Integer totalCount;
	  private BigDecimal ASumAmt;
	  private Integer ASumCnt;
	  private BigDecimal NSumAmt;
	  private Integer NSumCnt;
	  private BigDecimal RSumAmt;
	  private Integer RSumCnt;
	  private String tradeSts;
	  private String fmtTxnDte;
	  private String fmtTxnTme;
	  private String fmtAcDte;
	  private String fmtHTxnDte;
	  private String fmtHTxnTme;
	  private String fmtTTxnDte;
	  private String fmtTTxnTme;
	  private Date beginDate;
	  private Date endDate;
	  private Date rcnBegDte;
	  private Date rcnEndDte;

	  public static EupsTransJournal newDefaultEupsTransJournal(Context context)
	  {
	    EupsTransJournal eupsTransJournal = (EupsTransJournal)ContextUtils.getDataAsObject(context, EupsTransJournal.class);
	    eupsTransJournal.setOrgCde((String)context.getData("br"));
	    eupsTransJournal.setItgTyp("0");
	    eupsTransJournal.setFilFlg("0");
	    eupsTransJournal.setPrtFlg("0");
	    eupsTransJournal.setTxnSts("U");
	    eupsTransJournal.setMfmTxnSts("U");
	    eupsTransJournal.setThdTxnSts("U");
	    if (StringUtils.isNotBlank((CharSequence)context.getData("rapTyp"))) {
	      context.setData("rapTyp", "0");
	    }

	    if (StringUtils.isNotBlank((CharSequence)context.getData("txnTyp"))) {
	      context.setData("txnTyp", "N");
	    }

	    if (StringUtils.isEmpty((String)context.getData("ccy"))) {
	      context.setData("ccy", "CNY");
	    }
	    return eupsTransJournal;
	  }

	  public String getSqn()
	  {
	    return this.sqn;
	  }

	  public void setSqn(String sqn) {
	    this.sqn = sqn;
	  }

	  public String getBk() {
	    return this.bk;
	  }

	  public void setBk(String bk) {
	    this.bk = bk;
	  }

	  public String getPfeTyp() {
	    return this.pfeTyp;
	  }

	  public void setPfeTyp(String pfeTyp) {
	    this.pfeTyp = pfeTyp;
	  }

	  public String getItgTyp() {
	    return this.itgTyp;
	  }

	  public void setItgTyp(String itgTyp) {
	    this.itgTyp = itgTyp;
	  }

	  public String getQrySqn() {
	    return this.qrySqn;
	  }

	  public void setQrySqn(String qrySqn) {
	    this.qrySqn = qrySqn;
	  }

	  public String getTraceNo() {
	    return this.traceNo;
	  }

	  public void setTraceNo(String traceNo) {
	    this.traceNo = traceNo;
	  }

	  public String getReqSysCde() {
	    return this.reqSysCde;
	  }

	  public void setReqSysCde(String reqSysCde) {
	    this.reqSysCde = reqSysCde;
	  }

	  public String getReqJrnNo() {
	    return this.reqJrnNo;
	  }

	  public void setReqJrnNo(String reqJrnNo) {
	    this.reqJrnNo = reqJrnNo;
	  }

	  public Date getReqTme() {
	    return this.reqTme;
	  }

	  public void setReqTme(Date reqTme) {
	    this.reqTme = reqTme;
	  }

	  public String getBr() {
	    return this.br;
	  }

	  public void setBr(String br) {
	    this.br = br;
	  }

	  public String getOrgCde() {
	    return this.orgCde;
	  }

	  public void setOrgCde(String orgCde) {
	    this.orgCde = orgCde;
	  }

	  public String getTmlNo() {
	    return this.tmlNo;
	  }

	  public void setTmlNo(String tmlNo) {
	    this.tmlNo = tmlNo;
	  }

	  public String getTxnTlr() {
	    return this.txnTlr;
	  }

	  public void setTxnTlr(String txnTlr) {
	    this.txnTlr = txnTlr;
	  }

	  public String getSvrNme() {
	    return this.svrNme;
	  }

	  public void setSvrNme(String svrNme) {
	    this.svrNme = svrNme;
	  }

	  public String getComNo() {
	    return this.comNo;
	  }

	  public void setComNo(String comNo) {
	    this.comNo = comNo;
	  }

	  public String getEupsBusTyp() {
	    return this.eupsBusTyp;
	  }

	  public void setEupsBusTyp(String eupsBusTyp) {
	    this.eupsBusTyp = eupsBusTyp;
	  }

	  public String getRapTyp() {
	    return this.rapTyp;
	  }

	  public void setRapTyp(String rapTyp) {
	    this.rapTyp = rapTyp;
	  }

	  public Date getTxnDte() {
	    return this.txnDte;
	  }

	  public void setTxnDte(Date txnDte) {
	    this.txnDte = txnDte;
	  }

	  public Date getTxnTme() {
	    return this.txnTme;
	  }

	  public void setTxnTme(Date txnTme) {
	    this.txnTme = txnTme;
	  }

	  public String getTxnCde() {
	    return this.txnCde;
	  }

	  public void setTxnCde(String txnCde) {
	    this.txnCde = txnCde;
	  }

	  public String getTxnSts() {
	    return this.txnSts;
	  }

	  public void setTxnSts(String txnSts) {
	    this.txnSts = txnSts;
	  }

	  public String getRspMsg() {
	    return this.rspMsg;
	  }

	  public void setRspMsg(String rspMsg) {
	    this.rspMsg = rspMsg;
	  }

	  public String getOldTxnSqn() {
	    return this.oldTxnSqn;
	  }

	  public void setOldTxnSqn(String oldTxnSqn) {
	    this.oldTxnSqn = oldTxnSqn;
	  }

	  public String getThdObkCde() {
	    return this.thdObkCde;
	  }

	  public void setThdObkCde(String thdObkCde) {
	    this.thdObkCde = thdObkCde;
	  }

	  public String getRvsTraceNo() {
	    return this.rvsTraceNo;
	  }

	  public void setRvsTraceNo(String rvsTraceNo) {
	    this.rvsTraceNo = rvsTraceNo;
	  }

	  public String getThdSubCusNo() {
	    return this.thdSubCusNo;
	  }

	  public void setThdSubCusNo(String thdSubCusNo) {
	    this.thdSubCusNo = thdSubCusNo;
	  }

	  public String getThdFeePrd() {
	    return this.thdFeePrd;
	  }

	  public void setThdFeePrd(String thdFeePrd) {
	    this.thdFeePrd = thdFeePrd;
	  }

	  public String getBkCdTyp() {
	    return this.bkCdTyp;
	  }

	  public void setBkCdTyp(String bkCdTyp) {
	    this.bkCdTyp = bkCdTyp;
	  }

	  public BigDecimal getTxnAmt() {
	    return this.txnAmt;
	  }

	  public void setTxnAmt(BigDecimal txnAmt) {
	    this.txnAmt = txnAmt;
	  }

	  public String getCcy() {
	    return this.ccy;
	  }

	  public void setCcy(String ccy) {
	    this.ccy = ccy;
	  }

	  public String getChlTyp() {
	    return this.chlTyp;
	  }

	  public void setChlTyp(String chlTyp) {
	    this.chlTyp = chlTyp;
	  }

	  public String getAthTlrNo() {
	    return this.athTlrNo;
	  }

	  public void setAthTlrNo(String athTlrNo) {
	    this.athTlrNo = athTlrNo;
	  }

	  public String getChkTlr() {
	    return this.chkTlr;
	  }

	  public void setChkTlr(String chkTlr) {
	    this.chkTlr = chkTlr;
	  }

	  public String getReqVchNo() {
	    return this.reqVchNo;
	  }

	  public void setReqVchNo(String reqVchNo) {
	    this.reqVchNo = reqVchNo;
	  }

	  public String getBusKnd() {
	    return this.busKnd;
	  }

	  public void setBusKnd(String busKnd) {
	    this.busKnd = busKnd;
	  }

	  public String getRspCde() {
	    return this.rspCde;
	  }

	  public void setRspCde(String rspCde) {
	    this.rspCde = rspCde;
	  }

	  public String getMfmTxnCde() {
	    return this.mfmTxnCde;
	  }

	  public void setMfmTxnCde(String mfmTxnCde) {
	    this.mfmTxnCde = mfmTxnCde;
	  }

	  public String getMfmTxnSubCde() {
	    return this.mfmTxnSubCde;
	  }

	  public void setMfmTxnSubCde(String mfmTxnSubCde) {
	    this.mfmTxnSubCde = mfmTxnSubCde;
	  }

	  public Date getAcDte() {
	    return this.acDte;
	  }

	  public void setAcDte(Date acDte) {
	    this.acDte = acDte;
	  }

	  public String getMfmVchNo() {
	    return this.mfmVchNo;
	  }

	  public void setMfmVchNo(String mfmVchNo) {
	    this.mfmVchNo = mfmVchNo;
	  }

	  public String getMfmJrnNo() {
	    return this.mfmJrnNo;
	  }

	  public void setMfmJrnNo(String mfmJrnNo) {
	    this.mfmJrnNo = mfmJrnNo;
	  }

	  public String getMfmTxnSts() {
	    return this.mfmTxnSts;
	  }

	  public void setMfmTxnSts(String mfmTxnSts) {
	    this.mfmTxnSts = mfmTxnSts;
	  }

	  public String getMfmRspCde() {
	    return this.mfmRspCde;
	  }

	  public void setMfmRspCde(String mfmRspCde) {
	    this.mfmRspCde = mfmRspCde;
	  }

	  public String getMfmRspMsg() {
	    return this.mfmRspMsg;
	  }

	  public void setMfmRspMsg(String mfmRspMsg) {
	    this.mfmRspMsg = mfmRspMsg;
	  }

	  public String getThdTxnCde() {
	    return this.thdTxnCde;
	  }

	  public void setThdTxnCde(String thdTxnCde) {
	    this.thdTxnCde = thdTxnCde;
	  }

	  public Date getThdTxnDte() {
	    return this.thdTxnDte;
	  }

	  public void setThdTxnDte(Date thdTxnDte) {
	    this.thdTxnDte = thdTxnDte;
	  }

	  public Date getThdTxnTme() {
	    return this.thdTxnTme;
	  }

	  public void setThdTxnTme(Date thdTxnTme) {
	    this.thdTxnTme = thdTxnTme;
	  }

	  public String getThdSqn() {
	    return this.thdSqn;
	  }

	  public void setThdSqn(String thdSqn) {
	    this.thdSqn = thdSqn;
	  }

	  public String getThdTxnSts() {
	    return this.thdTxnSts;
	  }

	  public void setThdTxnSts(String thdTxnSts) {
	    this.thdTxnSts = thdTxnSts;
	  }

	  public String getThdRspCde() {
	    return this.thdRspCde;
	  }

	  public void setThdRspCde(String thdRspCde) {
	    this.thdRspCde = thdRspCde;
	  }

	  public String getThdRspMsg() {
	    return this.thdRspMsg;
	  }

	  public void setThdRspMsg(String thdRspMsg) {
	    this.thdRspMsg = thdRspMsg;
	  }

	  public String getRcnFlg() {
	    return this.rcnFlg;
	  }

	  public void setRcnFlg(String rcnFlg) {
	    this.rcnFlg = rcnFlg;
	  }

	  public String getMfmDytRcnFlg() {
	    return this.mfmDytRcnFlg;
	  }

	  public void setMfmDytRcnFlg(String mfmDytRcnFlg) {
	    this.mfmDytRcnFlg = mfmDytRcnFlg;
	  }

	  public String getMfmEodRcnFlg() {
	    return this.mfmEodRcnFlg;
	  }

	  public void setMfmEodRcnFlg(String mfmEodRcnFlg) {
	    this.mfmEodRcnFlg = mfmEodRcnFlg;
	  }

	  public String getMfmRcnBat() {
	    return this.mfmRcnBat;
	  }

	  public void setMfmRcnBat(String mfmRcnBat) {
	    this.mfmRcnBat = mfmRcnBat;
	  }

	  public Date getMfmRcnTme() {
	    return this.mfmRcnTme;
	  }

	  public void setMfmRcnTme(Date mfmRcnTme) {
	    this.mfmRcnTme = mfmRcnTme;
	  }

	  public String getThdRcnFlg() {
	    return this.thdRcnFlg;
	  }

	  public void setThdRcnFlg(String thdRcnFlg) {
	    this.thdRcnFlg = thdRcnFlg;
	  }

	  public Date getThdRcnTme() {
	    return this.thdRcnTme;
	  }

	  public void setThdRcnTme(Date thdRcnTme) {
	    this.thdRcnTme = thdRcnTme;
	  }

	  public String getThdRcnBat() {
	    return this.thdRcnBat;
	  }

	  public void setThdRcnBat(String thdRcnBat) {
	    this.thdRcnBat = thdRcnBat;
	  }

	  public String getIsBatFlg() {
	    return this.isBatFlg;
	  }

	  public void setIsBatFlg(String isBatFlg) {
	    this.isBatFlg = isBatFlg;
	  }

	  public String getBatNo() {
	    return this.batNo;
	  }

	  public void setBatNo(String batNo) {
	    this.batNo = batNo;
	  }

	  public String getExtRcnFlg() {
	    return this.extRcnFlg;
	  }

	  public void setExtRcnFlg(String extRcnFlg) {
	    this.extRcnFlg = extRcnFlg;
	  }

	  public String getOurOthFlg() {
	    return this.ourOthFlg;
	  }

	  public void setOurOthFlg(String ourOthFlg) {
	    this.ourOthFlg = ourOthFlg;
	  }

	  public String getThdRgnNo() {
	    return this.thdRgnNo;
	  }

	  public void setThdRgnNo(String thdRgnNo) {
	    this.thdRgnNo = thdRgnNo;
	  }

	  public String getThdCusNo() {
	    return this.thdCusNo;
	  }

	  public void setThdCusNo(String thdCusNo) {
	    this.thdCusNo = thdCusNo;
	  }

	  public String getThdCusNme() {
	    return this.thdCusNme;
	  }

	  public void setThdCusNme(String thdCusNme) {
	    this.thdCusNme = thdCusNme;
	  }

	  public String getPayChl() {
	    return this.payChl;
	  }

	  public void setPayChl(String payChl) {
	    this.payChl = payChl;
	  }

	  public String getSplNo() {
	    return this.splNo;
	  }

	  public void setSplNo(String splNo) {
	    this.splNo = splNo;
	  }

	  public String getSplTerNo() {
	    return this.splTerNo;
	  }

	  public void setSplTerNo(String splTerNo) {
	    this.splTerNo = splTerNo;
	  }

	  public String getTxnTyp() {
	    return this.txnTyp;
	  }

	  public void setTxnTyp(String txnTyp) {
	    this.txnTyp = txnTyp;
	  }

	  public String getCusAc() {
	    return this.cusAc;
	  }

	  public void setCusAc(String cusAc) {
	    this.cusAc = cusAc;
	  }

	  public String getCusNme() {
	    return this.cusNme;
	  }

	  public void setCusNme(String cusNme) {
	    this.cusNme = cusNme;
	  }

	  public String getIdTyp() {
	    return this.idTyp;
	  }

	  public void setIdTyp(String idTyp) {
	    this.idTyp = idTyp;
	  }

	  public String getIdNo() {
	    return this.idNo;
	  }

	  public void setIdNo(String idNo) {
	    this.idNo = idNo;
	  }

	  public String getCmuTel() {
	    return this.cmuTel;
	  }

	  public void setCmuTel(String cmuTel) {
	    this.cmuTel = cmuTel;
	  }

	  public String getAccTyp() {
	    return this.accTyp;
	  }

	  public void setAccTyp(String accTyp) {
	    this.accTyp = accTyp;
	  }

	  public String getAccSeq() {
	    return this.accSeq;
	  }

	  public void setAccSeq(String accSeq) {
	    this.accSeq = accSeq;
	  }

	  public String getBvKnd() {
	    return this.bvKnd;
	  }

	  public void setBvKnd(String bvKnd) {
	    this.bvKnd = bvKnd;
	  }

	  public String getBvNo() {
	    return this.bvNo;
	  }

	  public void setBvNo(String bvNo) {
	    this.bvNo = bvNo;
	  }

	  public String getLisAcc() {
	    return this.lisAcc;
	  }

	  public void setLisAcc(String lisAcc) {
	    this.lisAcc = lisAcc;
	  }

	  public String getChoNo() {
	    return this.choNo;
	  }

	  public void setChoNo(String choNo) {
	    this.choNo = choNo;
	  }

	  public String getRmkCde() {
	    return this.rmkCde;
	  }

	  public void setRmkCde(String rmkCde) {
	    this.rmkCde = rmkCde;
	  }

	  public String getCshNo() {
	    return this.cshNo;
	  }

	  public void setCshNo(String cshNo) {
	    this.cshNo = cshNo;
	  }

	  public String getDrwMde() {
	    return this.drwMde;
	  }

	  public void setDrwMde(String drwMde) {
	    this.drwMde = drwMde;
	  }

	  public BigDecimal getReqTxnAmt() {
	    return this.reqTxnAmt;
	  }

	  public void setReqTxnAmt(BigDecimal reqTxnAmt) {
	    this.reqTxnAmt = reqTxnAmt;
	  }

	  public String getFulDedFlg() {
	    return this.fulDedFlg;
	  }

	  public void setFulDedFlg(String fulDedFlg) {
	    this.fulDedFlg = fulDedFlg;
	  }

	  public BigDecimal getHfe() {
	    return this.hfe;
	  }

	  public void setHfe(BigDecimal hfe) {
	    this.hfe = hfe;
	  }

	  public String getHfeItmSeq() {
	    return this.hfeItmSeq;
	  }

	  public void setHfeItmSeq(String hfeItmSeq) {
	    this.hfeItmSeq = hfeItmSeq;
	  }

	  public String getHfePstBr() {
	    return this.hfePstBr;
	  }

	  public void setHfePstBr(String hfePstBr) {
	    this.hfePstBr = hfePstBr;
	  }

	  public String getPrtFlg() {
	    return this.prtFlg;
	  }

	  public void setPrtFlg(String prtFlg) {
	    this.prtFlg = prtFlg;
	  }

	  public Integer getPrtCnt() {
	    return this.prtCnt;
	  }

	  public void setPrtCnt(Integer prtCnt) {
	    this.prtCnt = prtCnt;
	  }

	  public String getBakFld1() {
	    return this.bakFld1;
	  }

	  public void setBakFld1(String bakFld1) {
	    this.bakFld1 = bakFld1;
	  }

	  public String getBakFld2() {
	    return this.bakFld2;
	  }

	  public void setBakFld2(String bakFld2) {
	    this.bakFld2 = bakFld2;
	  }

	  public String getBakFld3() {
	    return this.bakFld3;
	  }

	  public void setBakFld3(String bakFld3) {
	    this.bakFld3 = bakFld3;
	  }

	  public String getBakFld4() {
	    return this.bakFld4;
	  }

	  public void setBakFld4(String bakFld4) {
	    this.bakFld4 = bakFld4;
	  }

	  public String getBakFld5() {
	    return this.bakFld5;
	  }

	  public void setBakFld5(String bakFld5) {
	    this.bakFld5 = bakFld5;
	  }

	  public String getBakFld6() {
	    return this.bakFld6;
	  }

	  public void setBakFld6(String bakFld6) {
	    this.bakFld6 = bakFld6;
	  }

	  public String getRsvFld1() {
	    return this.rsvFld1;
	  }

	  public void setRsvFld1(String rsvFld1) {
	    this.rsvFld1 = rsvFld1;
	  }

	  public String getRsvFld2() {
	    return this.rsvFld2;
	  }

	  public void setRsvFld2(String rsvFld2) {
	    this.rsvFld2 = rsvFld2;
	  }

	  public String getRsvFld3() {
	    return this.rsvFld3;
	  }

	  public void setRsvFld3(String rsvFld3) {
	    this.rsvFld3 = rsvFld3;
	  }

	  public String getRsvFld4() {
	    return this.rsvFld4;
	  }

	  public void setRsvFld4(String rsvFld4) {
	    this.rsvFld4 = rsvFld4;
	  }

	  public String getRsvFld5() {
	    return this.rsvFld5;
	  }

	  public void setRsvFld5(String rsvFld5) {
	    this.rsvFld5 = rsvFld5;
	  }

	  public String getRsvFld6() {
	    return this.rsvFld6;
	  }

	  public void setRsvFld6(String rsvFld6) {
	    this.rsvFld6 = rsvFld6;
	  }

	  public String getRsvFld7() {
	    return this.rsvFld7;
	  }

	  public void setRsvFld7(String rsvFld7) {
	    this.rsvFld7 = rsvFld7;
	  }

	  public String getRsvFld8() {
	    return this.rsvFld8;
	  }

	  public void setRsvFld8(String rsvFld8) {
	    this.rsvFld8 = rsvFld8;
	  }

	  public String getFilFlg() {
	    return this.filFlg;
	  }

	  public void setFilFlg(String filFlg) {
	    this.filFlg = filFlg;
	  }

	  public BigDecimal getTotalAmt() {
	    return this.totalAmt;
	  }

	  public void setTotalAmt(BigDecimal totalAmt) {
	    this.totalAmt = totalAmt;
	  }

	  public Integer getTotalCount() {
	    return this.totalCount;
	  }

	  public void setTotalCount(Integer totalCount) {
	    this.totalCount = totalCount;
	  }

	  public BigDecimal getASumAmt() {
	    return this.ASumAmt;
	  }

	  public void setASumAmt(BigDecimal ASumAmt) {
	    this.ASumAmt = ASumAmt;
	  }

	  public Integer getASumCnt() {
	    return this.ASumCnt;
	  }

	  public void setASumCnt(Integer ASumCnt) {
	    this.ASumCnt = ASumCnt;
	  }

	  public BigDecimal getNSumAmt() {
	    return this.NSumAmt;
	  }

	  public void setNSumAmt(BigDecimal NSumAmt) {
	    this.NSumAmt = NSumAmt;
	  }

	  public Integer getNSumCnt() {
	    return this.NSumCnt;
	  }

	  public void setNSumCnt(Integer NSumCnt) {
	    this.NSumCnt = NSumCnt;
	  }

	  public BigDecimal getRSumAmt() {
	    return this.RSumAmt;
	  }

	  public void setRSumAmt(BigDecimal RSumAmt) {
	    this.RSumAmt = RSumAmt;
	  }

	  public Integer getRSumCnt() {
	    return this.RSumCnt;
	  }

	  public void setRSumCnt(Integer RSumCnt) {
	    this.RSumCnt = RSumCnt;
	  }

	  public String getTradeSts() {
	    return this.tradeSts;
	  }

	  public void setTradeSts(String tradeSts) {
	    this.tradeSts = tradeSts;
	  }

	  public String getFmtTxnDte() {
	    return this.fmtTxnDte;
	  }

	  public void setFmtTxnDte(String fmtTxnDte) {
	    this.fmtTxnDte = fmtTxnDte;
	  }

	  public String getFmtTxnTme() {
	    return this.fmtTxnTme;
	  }

	  public void setFmtTxnTme(String fmtTxnTme) {
	    this.fmtTxnTme = fmtTxnTme;
	  }

	  public String getFmtAcDte() {
	    return this.fmtAcDte;
	  }

	  public void setFmtAcDte(String fmtAcDte) {
	    this.fmtAcDte = fmtAcDte;
	  }

	  public String getFmtHTxnDte() {
	    return this.fmtHTxnDte;
	  }

	  public void setFmtHTxnDte(String fmtHTxnDte) {
	    this.fmtHTxnDte = fmtHTxnDte;
	  }

	  public String getFmtHTxnTme() {
	    return this.fmtHTxnTme;
	  }

	  public void setFmtHTxnTme(String fmtHTxnTme) {
	    this.fmtHTxnTme = fmtHTxnTme;
	  }

	  public String getFmtTTxnDte() {
	    return this.fmtTTxnDte;
	  }

	  public void setFmtTTxnDte(String fmtTTxnDte) {
	    this.fmtTTxnDte = fmtTTxnDte;
	  }

	  public String getFmtTTxnTme() {
	    return this.fmtTTxnTme;
	  }

	  public void setFmtTTxnTme(String fmtTTxnTme) {
	    this.fmtTTxnTme = fmtTTxnTme;
	  }

	  public Date getBeginDate() {
	    return this.beginDate;
	  }

	  public void setBeginDate(Date beginDate) {
	    this.beginDate = beginDate;
	  }

	  public Date getEndDate() {
	    return this.endDate;
	  }

	  public void setEndDate(Date endDate) {
	    this.endDate = endDate;
	  }

	  public Date getRcnBegDte() {
	    return this.rcnBegDte;
	  }

	  public void setRcnBegDte(Date rcnBegDte) {
	    this.rcnBegDte = rcnBegDte;
	  }

	  public Date getRcnEndDte() {
	    return this.rcnEndDte;
	  }

	  public void setRcnEndDte(Date rcnEndDte) {
	    this.rcnEndDte = rcnEndDte;
	  }
}
