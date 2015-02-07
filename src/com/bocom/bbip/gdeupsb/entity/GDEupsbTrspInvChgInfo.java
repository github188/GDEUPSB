package com.bocom.bbip.gdeupsb.entity;

import com.bocom.bbip.data.annotation.GeneratedValue;
import com.bocom.bbip.data.annotation.Id;
import java.util.Date;

public class GDEupsbTrspInvChgInfo {
    @Id
    @GeneratedValue
    private String sqn;

    private String thdKey;

    private String tlogNo;

    private String invNo;

    private String oinvNo;

    private String carTyp;

    private String carNo;

    private Date tactDt;

    private Date actDat;

    private String nodNo;

    private String tlrId;

    public String getSqn() {
        return sqn;
    }

    public void setSqn(String sqn) {
        this.sqn = sqn;
    }

    public String getThdKey() {
        return thdKey;
    }

    public void setThdKey(String thdKey) {
        this.thdKey = thdKey;
    }

    public String getTlogNo() {
        return tlogNo;
    }

    public void setTlogNo(String tlogNo) {
        this.tlogNo = tlogNo;
    }

    public String getInvNo() {
        return invNo;
    }

    public void setInvNo(String invNo) {
        this.invNo = invNo;
    }

    public String getOinvNo() {
        return oinvNo;
    }

    public void setOinvNo(String oinvNo) {
        this.oinvNo = oinvNo;
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

    public Date getTactDt() {
        return tactDt;
    }

    public void setTactDt(Date tactDt) {
        this.tactDt = tactDt;
    }

    public Date getActDat() {
        return actDat;
    }

    public void setActDat(Date actDat) {
        this.actDat = actDat;
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
}