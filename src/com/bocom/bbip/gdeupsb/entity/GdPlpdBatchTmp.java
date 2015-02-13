package com.bocom.bbip.gdeupsb.entity;

import com.bocom.bbip.data.annotation.GeneratedValue;
import com.bocom.bbip.data.annotation.Id;

public class GdPlpdBatchTmp {
    @Id
    @GeneratedValue
    private String sqn;

    private String cusAc;

    private String del;

    private String txnAmt;

    private String loaAct;

    private String loaNo;

    private String stlAct;

    private String capAmt;

    private String txnFlg;

    private String txnStg;

    private String rsvFld1;

    private String rsvFld2;

    private String rsvFld3;

    private String rsvFld4;

    public String getSqn() {
        return sqn;
    }

    public void setSqn(String sqn) {
        this.sqn = sqn;
    }

    public String getCusAc() {
        return cusAc;
    }

    public void setCusAc(String cusAc) {
        this.cusAc = cusAc;
    }

    public String getDel() {
        return del;
    }

    public void setDel(String del) {
        this.del = del;
    }

    public String getTxnAmt() {
        return txnAmt;
    }

    public void setTxnAmt(String txnAmt) {
        this.txnAmt = txnAmt;
    }

    public String getLoaAct() {
        return loaAct;
    }

    public void setLoaAct(String loaAct) {
        this.loaAct = loaAct;
    }

    public String getLoaNo() {
        return loaNo;
    }

    public void setLoaNo(String loaNo) {
        this.loaNo = loaNo;
    }

    public String getStlAct() {
        return stlAct;
    }

    public void setStlAct(String stlAct) {
        this.stlAct = stlAct;
    }

    public String getCapAmt() {
        return capAmt;
    }

    public void setCapAmt(String capAmt) {
        this.capAmt = capAmt;
    }

    public String getTxnFlg() {
        return txnFlg;
    }

    public void setTxnFlg(String txnFlg) {
        this.txnFlg = txnFlg;
    }

    public String getTxnStg() {
        return txnStg;
    }

    public void setTxnStg(String txnStg) {
        this.txnStg = txnStg;
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
}