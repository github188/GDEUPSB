package com.bocom.bbip.gdeupsb.entity;

import com.bocom.bbip.data.annotation.GeneratedValue;
import com.bocom.bbip.data.annotation.Id;

import java.math.BigDecimal;
import java.util.Date;

public class TrspCheckTmp {
    @Id
    @GeneratedValue
    private String sqn;

    private String tchkNo;

    private Date txnDat;

    private BigDecimal txnAmt;

    private String invNo;

    private String payMon;

    private String carTyp;

    private String carNo;

    private String statue;

    public String getSqn() {
        return sqn;
    }

    public void setSqn(String sqn) {
        this.sqn = sqn;
    }

    public String getTchkNo() {
        return tchkNo;
    }

    public void setTchkNo(String tchkNo) {
        this.tchkNo = tchkNo;
    }

    public Date getTxnDat() {
        return txnDat;
    }

    public void setTxnDat(Date txnDat) {
        this.txnDat = txnDat;
    }

    public BigDecimal getTxnAmt() {
        return txnAmt;
    }

    public void setTxnAmt(BigDecimal txnAmt) {
        this.txnAmt = txnAmt;
    }

    public String getInvNo() {
        return invNo;
    }

    public void setInvNo(String invNo) {
        this.invNo = invNo;
    }

    public String getPayMon() {
        return payMon;
    }

    public void setPayMon(String payMon) {
        this.payMon = payMon;
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

    public String getStatue() {
        return statue;
    }

    public void setStatue(String statue) {
        this.statue = statue;
    }
}