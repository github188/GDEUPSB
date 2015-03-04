package com.bocom.bbip.gdeupsb.entity;

import com.bocom.bbip.data.annotation.GeneratedValue;
import com.bocom.bbip.data.annotation.Id;

public class GdElecClrInf {
    @Id
    @GeneratedValue
    private String cAgtNo;

    private String brNo;

    private String clrDat;

    private String clrTim;

    private String clrSts;

    private String autFlg;

    public String getcAgtNo() {
        return cAgtNo;
    }

    public void setcAgtNo(String cAgtNo) {
        this.cAgtNo = cAgtNo;
    }

    public String getBrNo() {
        return brNo;
    }

    public void setBrNo(String brNo) {
        this.brNo = brNo;
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
}