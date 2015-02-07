package com.bocom.bbip.gdeupsb.entity;

import com.bocom.bbip.data.annotation.GeneratedValue;
import com.bocom.bbip.data.annotation.Id;

public class GdElecFileBatchTmp {
    @Id
    @GeneratedValue
    private String sqn;

    private String dfMon;

    private String totCnt;

    private String totAmt;

    private String rsFld1;

    private String thdCusNo;

    private String cusAc;

    private String ttxnAmt;

    private String eleMon;

    private String rsFld2;

    private String comAc;

    private String flag;

    private String tComno;

    private String txnDte;

    private String tlr;

    public String getSqn() {
        return sqn;
    }

    public void setSqn(String sqn) {
        this.sqn = sqn;
    }

    public String getDfMon() {
        return dfMon;
    }

    public void setDfMon(String dfMon) {
        this.dfMon = dfMon;
    }

    public String getTotCnt() {
        return totCnt;
    }

    public void setTotCnt(String totCnt) {
        this.totCnt = totCnt;
    }

    public String getTotAmt() {
        return totAmt;
    }

    public void setTotAmt(String totAmt) {
        this.totAmt = totAmt;
    }

    public String getRsFld1() {
        return rsFld1;
    }

    public void setRsFld1(String rsFld1) {
        this.rsFld1 = rsFld1;
    }

    public String getThdCusNo() {
        return thdCusNo;
    }

    public void setThdCusNo(String thdCusNo) {
        this.thdCusNo = thdCusNo;
    }

    public String getCusAc() {
        return cusAc;
    }

    public void setCusAc(String cusAc) {
        this.cusAc = cusAc;
    }

    public String getTtxnAmt() {
        return ttxnAmt;
    }

    public void setTtxnAmt(String ttxnAmt) {
        this.ttxnAmt = ttxnAmt;
    }

    public String getEleMon() {
        return eleMon;
    }

    public void setEleMon(String eleMon) {
        this.eleMon = eleMon;
    }

    public String getRsFld2() {
        return rsFld2;
    }

    public void setRsFld2(String rsFld2) {
        this.rsFld2 = rsFld2;
    }

    public String getComAc() {
        return comAc;
    }

    public void setComAc(String comAc) {
        this.comAc = comAc;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String gettComno() {
        return tComno;
    }

    public void settComno(String tComno) {
        this.tComno = tComno;
    }

    public String getTxnDte() {
        return txnDte;
    }

    public void setTxnDte(String txnDte) {
        this.txnDte = txnDte;
    }

    public String getTlr() {
        return tlr;
    }

    public void setTlr(String tlr) {
        this.tlr = tlr;
    }
}