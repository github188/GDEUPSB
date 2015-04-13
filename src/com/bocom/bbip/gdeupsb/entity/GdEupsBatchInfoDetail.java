package com.bocom.bbip.gdeupsb.entity;

import com.bocom.bbip.data.annotation.GeneratedValue;
import com.bocom.bbip.data.annotation.Id;
import java.math.BigDecimal;
import java.util.Date;

public class GdEupsBatchInfoDetail {
    @Id
    @GeneratedValue
    private String sqn;

    private String batNo;

    private String cusAc;

    private String cusNme;

    private BigDecimal txnAmt;

    private String agtSrvCusId;

    private String agtSrvCusNme;

    private String ourOthFlg;

    private String obkBk;

    private String rmk1;

    private String rmk2;

    private String sts;

    private String errMsg;

    private Date txnDte;

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

    public BigDecimal getTxnAmt() {
        return txnAmt;
    }

    public void setTxnAmt(BigDecimal txnAmt) {
        this.txnAmt = txnAmt;
    }

    public String getAgtSrvCusId() {
        return agtSrvCusId;
    }

    public void setAgtSrvCusId(String agtSrvCusId) {
        this.agtSrvCusId = agtSrvCusId;
    }

    public String getAgtSrvCusNme() {
        return agtSrvCusNme;
    }

    public void setAgtSrvCusNme(String agtSrvCusNme) {
        this.agtSrvCusNme = agtSrvCusNme;
    }

    public String getOurOthFlg() {
        return ourOthFlg;
    }

    public void setOurOthFlg(String ourOthFlg) {
        this.ourOthFlg = ourOthFlg;
    }

    public String getObkBk() {
        return obkBk;
    }

    public void setObkBk(String obkBk) {
        this.obkBk = obkBk;
    }

    public String getRmk1() {
        return rmk1;
    }

    public void setRmk1(String rmk1) {
        this.rmk1 = rmk1;
    }

    public String getRmk2() {
        return rmk2;
    }

    public void setRmk2(String rmk2) {
        this.rmk2 = rmk2;
    }

    public String getSts() {
        return sts;
    }

    public void setSts(String sts) {
        this.sts = sts;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public Date getTxnDte() {
        return txnDte;
    }

    public void setTxnDte(Date txnDte) {
        this.txnDte = txnDte;
    }
}