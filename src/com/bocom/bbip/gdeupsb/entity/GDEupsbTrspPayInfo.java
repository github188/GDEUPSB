package com.bocom.bbip.gdeupsb.entity;

import com.bocom.bbip.data.annotation.GeneratedValue;
import com.bocom.bbip.data.annotation.Id;
import java.math.BigDecimal;
import java.util.Date;

public class GDEupsbTrspPayInfo {
    @Id
    @GeneratedValue
    private String thdKey;

    private String brNo;

    private String carTyp;

    private String carNo;

    private String tcusNm;

    private String payMon;

    private Date actDat;

    private Date tactDt;

    private BigDecimal txnAmt;

    private String flg;

    private String bakFld1;

    private String bakFld2;

    private String bakFld3;

    private String rsvFld1;

    public String getThdKey() {
        return thdKey;
    }

    public void setThdKey(String thdKey) {
        this.thdKey = thdKey;
    }

    public String getBrNo() {
        return brNo;
    }

    public void setBrNo(String brNo) {
        this.brNo = brNo;
    }

    public String getCarTyp() {
        return carTyp;
    }

    public void setCarTyp(String carTyp) {
        this.carTyp = carTyp;
    }

    public String getCarNo() {
        return carNo;
    }

    public void setCarNo(String carNo) {
        this.carNo = carNo;
    }

    public String getTcusNm() {
        return tcusNm;
    }

    public void setTcusNm(String tcusNm) {
        this.tcusNm = tcusNm;
    }

    public String getPayMon() {
        return payMon;
    }

    public void setPayMon(String payMon) {
        this.payMon = payMon;
    }

    public Date getActDat() {
        return actDat;
    }

    public void setActDat(Date actDat) {
        this.actDat = actDat;
    }

    public Date getTactDt() {
        return tactDt;
    }

    public void setTactDt(Date tactDt) {
        this.tactDt = tactDt;
    }

    public BigDecimal getTxnAmt() {
        return txnAmt;
    }

    public void setTxnAmt(BigDecimal txnAmt) {
        this.txnAmt = txnAmt;
    }

    public String getFlg() {
        return flg;
    }

    public void setFlg(String flg) {
        this.flg = flg;
    }

    public String getBakFld1() {
        return bakFld1;
    }

    public void setBakFld1(String bakFld1) {
        this.bakFld1 = bakFld1;
    }

    public String getBakFld2() {
        return bakFld2;
    }

    public void setBakFld2(String bakFld2) {
        this.bakFld2 = bakFld2;
    }

    public String getBakFld3() {
        return bakFld3;
    }

    public void setBakFld3(String bakFld3) {
        this.bakFld3 = bakFld3;
    }

    public String getRsvFld1() {
        return rsvFld1;
    }

    public void setRsvFld1(String rsvFld1) {
        this.rsvFld1 = rsvFld1;
    }
}