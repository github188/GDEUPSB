package com.bocom.bbip.gdeupsb.entity;

import com.bocom.bbip.data.annotation.GeneratedValue;
import com.bocom.bbip.data.annotation.Id;

public class GdGasCusAll {
    @Id
    @GeneratedValue
    private String cusNo;

    private String cusAc;

    private String cusNme;

    private String cusTyp;

    private String optDat;

    private String optNod;

    private String idTyp;

    private String idNo;

    private String thdCusNme;

    private String cmuTel;

    private String thdCusAdr;

    public String getCusNo() {
        return cusNo;
    }

    public void setCusNo(String cusNo) {
        this.cusNo = cusNo;
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

    public String getCusTyp() {
        return cusTyp;
    }

    public void setCusTyp(String cusTyp) {
        this.cusTyp = cusTyp;
    }

    public String getOptDat() {
        return optDat;
    }

    public void setOptDat(String optDat) {
        this.optDat = optDat;
    }

    public String getOptNod() {
        return optNod;
    }

    public void setOptNod(String optNod) {
        this.optNod = optNod;
    }

    public String getIdTyp() {
        return idTyp;
    }

    public void setIdTyp(String idTyp) {
        this.idTyp = idTyp;
    }

    public String getIdNo() {
        return idNo;
    }

    public void setIdNo(String idNo) {
        this.idNo = idNo;
    }

    public String getThdCusNme() {
        return thdCusNme;
    }

    public void setThdCusNme(String thdCusNme) {
        this.thdCusNme = thdCusNme;
    }

    public String getCmuTel() {
        return cmuTel;
    }

    public void setCmuTel(String cmuTel) {
        this.cmuTel = cmuTel;
    }

    public String getThdCusAdr() {
        return thdCusAdr;
    }

    public void setThdCusAdr(String thdCusAdr) {
        this.thdCusAdr = thdCusAdr;
    }
}