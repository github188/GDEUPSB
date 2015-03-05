package com.bocom.bbip.gdeupsb.entity;

import com.bocom.bbip.data.annotation.GeneratedValue;
import com.bocom.bbip.data.annotation.Id;
import java.math.BigDecimal;
import java.util.Date;

public class GDEupsBatchConsoleInfo {
    @Id
    @GeneratedValue
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
        this.batNo = batNo;
    }

    public String getTxnMde() {
        return txnMde;
    }

    public void setTxnMde(String txnMde) {
        this.txnMde = txnMde;
    }

    public String getRapTyp() {
        return rapTyp;
    }

    public void setRapTyp(String rapTyp) {
        this.rapTyp = rapTyp;
    }

    public String getComNo() {
        return comNo;
    }

    public void setComNo(String comNo) {
        this.comNo = comNo;
    }

    public String getComNme() {
        return comNme;
    }

    public void setComNme(String comNme) {
        this.comNme = comNme;
    }

    public String getBusKnd() {
        return busKnd;
    }

    public void setBusKnd(String busKnd) {
        this.busKnd = busKnd;
    }

    public String getTxnOrgCde() {
        return txnOrgCde;
    }

    public void setTxnOrgCde(String txnOrgCde) {
        this.txnOrgCde = txnOrgCde;
    }

    public String getTxnTlr() {
        return txnTlr;
    }

    public void setTxnTlr(String txnTlr) {
        this.txnTlr = txnTlr;
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
        this.batSts = batSts;
    }

    public String getFleNme() {
        return fleNme;
    }

    public void setFleNme(String fleNme) {
        this.fleNme = fleNme;
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
        this.rsvFld1 = rsvFld1;
    }

    public String getRsvFld2() {
        return rsvFld2;
    }

    public void setRsvFld2(String rsvFld2) {
        this.rsvFld2 = rsvFld2;
    }

    public String getRsvFld3() {
        return rsvFld3;
    }

    public void setRsvFld3(String rsvFld3) {
        this.rsvFld3 = rsvFld3;
    }

    public String getRsvFld4() {
        return rsvFld4;
    }

    public void setRsvFld4(String rsvFld4) {
        this.rsvFld4 = rsvFld4;
    }

    public String getRsvFld5() {
        return rsvFld5;
    }

    public void setRsvFld5(String rsvFld5) {
        this.rsvFld5 = rsvFld5;
    }

    public String getRsvFld6() {
        return rsvFld6;
    }

    public void setRsvFld6(String rsvFld6) {
        this.rsvFld6 = rsvFld6;
    }

    public String getRsvFld7() {
        return rsvFld7;
    }

    public void setRsvFld7(String rsvFld7) {
        this.rsvFld7 = rsvFld7;
    }

    public String getRsvFld8() {
        return rsvFld8;
    }

    public void setRsvFld8(String rsvFld8) {
        this.rsvFld8 = rsvFld8;
    }

    public String getRsvFld9() {
        return rsvFld9;
    }

    public void setRsvFld9(String rsvFld9) {
        this.rsvFld9 = rsvFld9;
    }
}