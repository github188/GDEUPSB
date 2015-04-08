package com.bocom.bbip.gdeupsb.entity;

import com.bocom.bbip.data.annotation.GeneratedValue;
import com.bocom.bbip.data.annotation.Id;

public class GdElecClrJnl {
    @Id
    @GeneratedValue
    private String sqn;

    private String brNo;

    private String nodNo;

    private String tlrId;

    private String cAgtNo;

    private String clrDat;

    private String clrTim;

    private String clrRst;

    private String clrTyp;

    private String clrTot;

    private String clrAmt;

    public String getSqn() {
        return sqn;
    }

    public void setSqn(String sqn) {
        this.sqn = sqn;
    }

    public String getBrNo() {
        return brNo;
    }

    public void setBrNo(String brNo) {
        this.brNo = brNo;
    }

    public String getNodNo() {
        return nodNo;
    }

    public void setNodNo(String nodNo) {
        this.nodNo = nodNo;
    }

    public String getTlrId() {
        return tlrId;
    }

    public void setTlrId(String tlrId) {
        this.tlrId = tlrId;
    }

    public String getcAgtNo() {
        return cAgtNo;
    }

    public void setcAgtNo(String cAgtNo) {
        this.cAgtNo = cAgtNo;
    }

    public String getClrDat() {
        return clrDat;
    }

    public void setClrDat(String clrDat) {
        this.clrDat = clrDat;
    }

    public String getClrTim() {
        return clrTim;
    }

    public void setClrTim(String clrTim) {
        this.clrTim = clrTim;
    }

    public String getClrRst() {
        return clrRst;
    }

    public void setClrRst(String clrRst) {
        this.clrRst = clrRst;
    }

    public String getClrTyp() {
        return clrTyp;
    }

    public void setClrTyp(String clrTyp) {
        this.clrTyp = clrTyp;
    }

    public String getClrTot() {
        return clrTot;
    }

    public void setClrTot(String clrTot) {
        this.clrTot = clrTot;
    }

    public String getClrAmt() {
        return clrAmt;
    }

    public void setClrAmt(String clrAmt) {
        this.clrAmt = clrAmt;
    }
}