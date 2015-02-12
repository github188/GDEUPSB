package com.bocom.bbip.gdeupsb.entity;

import com.bocom.bbip.data.annotation.GeneratedValue;
import com.bocom.bbip.data.annotation.Id;
import java.util.Date;

public class GdGashBatchTmp {
    @Id
    @GeneratedValue
    private String sqn;

    private String batNo;

    private String batSts;

    private String pkgFlg;

    private String totCnt;

    private String totAmt;

    private String sucTotCnt;

    private String sucTotAmt;

    private Date txnDte;

    private Date txnTme;

    private String thdSqn;

    private String bk;

    private String cusNo;

    private String cusNme;

    private String cusAc;

    private String payMon;

    private String reqTxnAmt;

    private String txnAmt;

    private String tmpFld1;

    private String tmpFld2;

    private String tmpFld3;

    private String tmpFld4;

    private String tmpFld5;

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

    public String getBatSts() {
        return batSts;
    }

    public void setBatSts(String batSts) {
        this.batSts = batSts;
    }

    public String getPkgFlg() {
        return pkgFlg;
    }

    public void setPkgFlg(String pkgFlg) {
        this.pkgFlg = pkgFlg;
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

    public String getSucTotCnt() {
        return sucTotCnt;
    }

    public void setSucTotCnt(String sucTotCnt) {
        this.sucTotCnt = sucTotCnt;
    }

    public String getSucTotAmt() {
        return sucTotAmt;
    }

    public void setSucTotAmt(String sucTotAmt) {
        this.sucTotAmt = sucTotAmt;
    }

    public Date getTxnDte() {
        return txnDte;
    }

    public void setTxnDte(Date txnDte) {
        this.txnDte = txnDte;
    }

    public Date getTxnTme() {
        return txnTme;
    }

    public void setTxnTme(Date txnTme) {
        this.txnTme = txnTme;
    }

    public String getThdSqn() {
        return thdSqn;
    }

    public void setThdSqn(String thdSqn) {
        this.thdSqn = thdSqn;
    }

    public String getBk() {
        return bk;
    }

    public void setBk(String bk) {
        this.bk = bk;
    }

    public String getCusNo() {
        return cusNo;
    }

    public void setCusNo(String cusNo) {
        this.cusNo = cusNo;
    }

    public String getCusNme() {
        return cusNme;
    }

    public void setCusNme(String cusNme) {
        this.cusNme = cusNme;
    }

    public String getCusAc() {
        return cusAc;
    }

    public void setCusAc(String cusAc) {
        this.cusAc = cusAc;
    }

    public String getPayMon() {
        return payMon;
    }

    public void setPayMon(String payMon) {
        this.payMon = payMon;
    }

    public String getReqTxnAmt() {
        return reqTxnAmt;
    }

    public void setReqTxnAmt(String reqTxnAmt) {
        this.reqTxnAmt = reqTxnAmt;
    }

    public String getTxnAmt() {
        return txnAmt;
    }

    public void setTxnAmt(String txnAmt) {
        this.txnAmt = txnAmt;
    }

    public String getTmpFld1() {
        return tmpFld1;
    }

    public void setTmpFld1(String tmpFld1) {
        this.tmpFld1 = tmpFld1;
    }

    public String getTmpFld2() {
        return tmpFld2;
    }

    public void setTmpFld2(String tmpFld2) {
        this.tmpFld2 = tmpFld2;
    }

    public String getTmpFld3() {
        return tmpFld3;
    }

    public void setTmpFld3(String tmpFld3) {
        this.tmpFld3 = tmpFld3;
    }

    public String getTmpFld4() {
        return tmpFld4;
    }

    public void setTmpFld4(String tmpFld4) {
        this.tmpFld4 = tmpFld4;
    }

    public String getTmpFld5() {
        return tmpFld5;
    }

    public void setTmpFld5(String tmpFld5) {
        this.tmpFld5 = tmpFld5;
    }
}