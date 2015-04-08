package com.bocom.bbip.gdeupsb.entity;

import com.bocom.bbip.data.annotation.GeneratedValue;
import com.bocom.bbip.data.annotation.Id;

public class GdElecClrTbl {
    @Id
    @GeneratedValue
    private String sqn;

    private String brNo;

    private String nodNo;

    private String clrDat;

    private String clrTim;

    private String clrSts;

    private String autFlg;

    private String tlrId;

    private String logDat;

    private String logTim;

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

    public String getClrSts() {
        return clrSts;
    }

    public void setClrSts(String clrSts) {
        this.clrSts = clrSts;
    }

    public String getAutFlg() {
        return autFlg;
    }

    public void setAutFlg(String autFlg) {
        this.autFlg = autFlg;
    }

    public String getTlrId() {
        return tlrId;
    }

    public void setTlrId(String tlrId) {
        this.tlrId = tlrId;
    }

    public String getLogDat() {
        return logDat;
    }

    public void setLogDat(String logDat) {
        this.logDat = logDat;
    }

    public String getLogTim() {
        return logTim;
    }

    public void setLogTim(String logTim) {
        this.logTim = logTim;
    }
}