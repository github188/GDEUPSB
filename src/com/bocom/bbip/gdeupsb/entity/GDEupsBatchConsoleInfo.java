package com.bocom.bbip.gdeupsb.entity;

import java.math.BigDecimal;
import java.util.Date;

public class GDEupsBatchConsoleInfo {
	private String batNo;

    private String txnMde;

    private String rapTyp;

    private String comNo;

    private String comNme;

    private String busKnd;

    private String txnOrgCde;

    private String txnTlr;

    private Date subDte;

    private Date exeDte;

    private String batSts;

    private String fleNme;

    private Integer totCnt;

    private BigDecimal totAmt;

    private Integer sucTotCnt;

    private BigDecimal sucTotAmt;

    private Integer falTotCnt;

    private BigDecimal falTotAmt;

    private Integer payCnt;

    private String rsvFld1;

    private String rsvFld2;

    private String rsvFld3;

    private String rsvFld4;

    private String rsvFld5;

    private String rsvFld6;

    private String rsvFld7;

    private String rsvFld8;

    private String rsvFld9;

    public String getBatNo() {
        return batNo;
    }

    public void setBatNo(String batNo) {
        this.batNo = batNo == null ? null : batNo.trim();
    }

    public String getTxnMde() {
        return txnMde;
    }

    public void setTxnMde(String txnMde) {
        this.txnMde = txnMde == null ? null : txnMde.trim();
    }

    public String getRapTyp() {
        return rapTyp;
    }

    public void setRapTyp(String rapTyp) {
        this.rapTyp = rapTyp == null ? null : rapTyp.trim();
    }

    public String getComNo() {
        return comNo;
    }

    public void setComNo(String comNo) {
        this.comNo = comNo == null ? null : comNo.trim();
    }

    public String getComNme() {
        return comNme;
    }

    public void setComNme(String comNme) {
        this.comNme = comNme == null ? null : comNme.trim();
    }

    public String getBusKnd() {
        return busKnd;
    }

    public void setBusKnd(String busKnd) {
        this.busKnd = busKnd == null ? null : busKnd.trim();
    }

    public String getTxnOrgCde() {
        return txnOrgCde;
    }

    public void setTxnOrgCde(String txnOrgCde) {
        this.txnOrgCde = txnOrgCde == null ? null : txnOrgCde.trim();
    }

    public String getTxnTlr() {
        return txnTlr;
    }

    public void setTxnTlr(String txnTlr) {
        this.txnTlr = txnTlr == null ? null : txnTlr.trim();
    }

    public Date getSubDte() {
        return subDte;
    }

    public void setSubDte(Date subDte) {
        this.subDte = subDte;
    }

    public Date getExeDte() {
        return exeDte;
    }

    public void setExeDte(Date exeDte) {
        this.exeDte = exeDte;
    }

    public String getBatSts() {
        return batSts;
    }

    public void setBatSts(String batSts) {
        this.batSts = batSts == null ? null : batSts.trim();
    }

    public String getFleNme() {
        return fleNme;
    }

    public void setFleNme(String fleNme) {
        this.fleNme = fleNme == null ? null : fleNme.trim();
    }

    public Integer getTotCnt() {
        return totCnt;
    }

    public void setTotCnt(Integer totCnt) {
        this.totCnt = totCnt;
    }

    public BigDecimal getTotAmt() {
        return totAmt;
    }

    public void setTotAmt(BigDecimal totAmt) {
        this.totAmt = totAmt;
    }

    public Integer getSucTotCnt() {
        return sucTotCnt;
    }

    public void setSucTotCnt(Integer sucTotCnt) {
        this.sucTotCnt = sucTotCnt;
    }

    public BigDecimal getSucTotAmt() {
        return sucTotAmt;
    }

    public void setSucTotAmt(BigDecimal sucTotAmt) {
        this.sucTotAmt = sucTotAmt;
    }

    public Integer getFalTotCnt() {
        return falTotCnt;
    }

    public void setFalTotCnt(Integer falTotCnt) {
        this.falTotCnt = falTotCnt;
    }

    public BigDecimal getFalTotAmt() {
        return falTotAmt;
    }

    public void setFalTotAmt(BigDecimal falTotAmt) {
        this.falTotAmt = falTotAmt;
    }

    public Integer getPayCnt() {
        return payCnt;
    }

    public void setPayCnt(Integer payCnt) {
        this.payCnt = payCnt;
    }

    public String getRsvFld1() {
        return rsvFld1;
    }

    public void setRsvFld1(String rsvFld1) {
        this.rsvFld1 = rsvFld1 == null ? null : rsvFld1.trim();
    }

    public String getRsvFld2() {
        return rsvFld2;
    }

    public void setRsvFld2(String rsvFld2) {
        this.rsvFld2 = rsvFld2 == null ? null : rsvFld2.trim();
    }

    public String getRsvFld3() {
        return rsvFld3;
    }

    public void setRsvFld3(String rsvFld3) {
        this.rsvFld3 = rsvFld3 == null ? null : rsvFld3.trim();
    }

    public String getRsvFld4() {
        return rsvFld4;
    }

    public void setRsvFld4(String rsvFld4) {
        this.rsvFld4 = rsvFld4 == null ? null : rsvFld4.trim();
    }

    public String getRsvFld5() {
        return rsvFld5;
    }

    public void setRsvFld5(String rsvFld5) {
        this.rsvFld5 = rsvFld5 == null ? null : rsvFld5.trim();
    }

    public String getRsvFld6() {
        return rsvFld6;
    }

    public void setRsvFld6(String rsvFld6) {
        this.rsvFld6 = rsvFld6 == null ? null : rsvFld6.trim();
    }

    public String getRsvFld7() {
        return rsvFld7;
    }

    public void setRsvFld7(String rsvFld7) {
        this.rsvFld7 = rsvFld7 == null ? null : rsvFld7.trim();
    }

    public String getRsvFld8() {
        return rsvFld8;
    }

    public void setRsvFld8(String rsvFld8) {
        this.rsvFld8 = rsvFld8 == null ? null : rsvFld8.trim();
    }

    public String getRsvFld9() {
        return rsvFld9;
    }

    public void setRsvFld9(String rsvFld9) {
        this.rsvFld9 = rsvFld9 == null ? null : rsvFld9.trim();
    }
}
