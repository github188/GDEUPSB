package com.bocom.bbip.gdeupsb.entity;

import com.bocom.bbip.data.annotation.GeneratedValue;
import com.bocom.bbip.data.annotation.Id;

public class GdTbcBasInf {
    @Id
    @GeneratedValue
    private String dptId;

    private String brNo;

    private String dptNam;

    private String devId;

    private String teller;

    private String tranDt;

    private String comKey;

    private String sigSts;

    private String rsvFld1;

    private String rsvFld2;

    public String getDptId() {
        return dptId;
    }

    public void setDptId(String dptId) {
        this.dptId = dptId;
    }

    public String getBrNo() {
        return brNo;
    }

    public void setBrNo(String brNo) {
        this.brNo = brNo;
    }

    public String getDptNam() {
        return dptNam;
    }

    public void setDptNam(String dptNam) {
        this.dptNam = dptNam;
    }

    public String getDevId() {
        return devId;
    }

    public void setDevId(String devId) {
        this.devId = devId;
    }

    public String getTeller() {
        return teller;
    }

    public void setTeller(String teller) {
        this.teller = teller;
    }

    public String getTranDt() {
        return tranDt;
    }

    public void setTranDt(String tranDt) {
        this.tranDt = tranDt;
    }

    public String getComKey() {
        return comKey;
    }

    public void setComKey(String comKey) {
        this.comKey = comKey;
    }

    public String getSigSts() {
        return sigSts;
    }

    public void setSigSts(String sigSts) {
        this.sigSts = sigSts;
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
}