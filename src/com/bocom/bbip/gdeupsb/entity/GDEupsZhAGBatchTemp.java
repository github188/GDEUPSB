package com.bocom.bbip.gdeupsb.entity;

import com.bocom.bbip.data.annotation.GeneratedValue;
import com.bocom.bbip.data.annotation.Id;
import java.util.Date;

public class GDEupsZhAGBatchTemp {
    @Id
    @GeneratedValue
    private String sqn;

    private String batNo;

    private String comNo;

    private String txnTlr;

    private Date subDte;

    private String cusAc;

    private String cusNme;

    private String txnAmt;

    private String thdCusNo;

    private String thdCusNme;

    private String rsvFld1;

    private String rsvFld2;

    private String rsvFld3;

    private String rsvFld4;

    private String rsvFld5;

    private String rsvFld6;

    private String rsvFld7;

    public String getSqn() {
        return sqn;
    }

    public void setSqn(String sqn) {
        this.sqn = sqn;
    }

    public String getBatNo() {
        return batNo;
    }

    public void setBatNo(String batNo) {
        this.batNo = batNo;
    }

    public String getComNo() {
        return comNo;
    }

    public void setComNo(String comNo) {
        this.comNo = comNo;
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

    public String getCusAc() {
        return cusAc;
    }

    public void setCusAc(String cusAc) {
        this.cusAc = cusAc;
    }

    public String getCusNme() {
        return cusNme;
    }

    public void setCusNme(String cusNme) {
        this.cusNme = cusNme;
    }

    public String getTxnAmt() {
        return txnAmt;
    }

    public void setTxnAmt(String txnAmt) {
        this.txnAmt = txnAmt;
    }

    public String getThdCusNo() {
        return thdCusNo;
    }

    public void setThdCusNo(String thdCusNo) {
        this.thdCusNo = thdCusNo;
    }

    public String getThdCusNme() {
        return thdCusNme;
    }

    public void setThdCusNme(String thdCusNme) {
        this.thdCusNme = thdCusNme;
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
}