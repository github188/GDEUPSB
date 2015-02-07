package com.bocom.bbip.gdeupsb.entity;

import com.bocom.bbip.data.annotation.GeneratedValue;
import com.bocom.bbip.data.annotation.Id;

public class GdsRunCtl {
    @Id
    @GeneratedValue
    private String gdsBid;

    private String gdsBnm;

    private String agtMtb;

    private String agtStb;

    private String jnlMtb;

    private String jnlStb;

    private String athFlg;

    private String namChk;

    private String pinChk;

    private String pswChk;

    private String lclChk;

    private String lclSvr;

    private String lclCod;

    private String thdChk;

    private String thdSvr;

    private String thdCod;

    private String agtBr;

    public String getGdsBid() {
        return gdsBid;
    }

    public void setGdsBid(String gdsBid) {
        this.gdsBid = gdsBid;
    }

    public String getGdsBnm() {
        return gdsBnm;
    }

    public void setGdsBnm(String gdsBnm) {
        this.gdsBnm = gdsBnm;
    }

    public String getAgtMtb() {
        return agtMtb;
    }

    public void setAgtMtb(String agtMtb) {
        this.agtMtb = agtMtb;
    }

    public String getAgtStb() {
        return agtStb;
    }

    public void setAgtStb(String agtStb) {
        this.agtStb = agtStb;
    }

    public String getJnlMtb() {
        return jnlMtb;
    }

    public void setJnlMtb(String jnlMtb) {
        this.jnlMtb = jnlMtb;
    }

    public String getJnlStb() {
        return jnlStb;
    }

    public void setJnlStb(String jnlStb) {
        this.jnlStb = jnlStb;
    }

    public String getAthFlg() {
        return athFlg;
    }

    public void setAthFlg(String athFlg) {
        this.athFlg = athFlg;
    }

    public String getNamChk() {
        return namChk;
    }

    public void setNamChk(String namChk) {
        this.namChk = namChk;
    }

    public String getPinChk() {
        return pinChk;
    }

    public void setPinChk(String pinChk) {
        this.pinChk = pinChk;
    }

    public String getPswChk() {
        return pswChk;
    }

    public void setPswChk(String pswChk) {
        this.pswChk = pswChk;
    }

    public String getLclChk() {
        return lclChk;
    }

    public void setLclChk(String lclChk) {
        this.lclChk = lclChk;
    }

    public String getLclSvr() {
        return lclSvr;
    }

    public void setLclSvr(String lclSvr) {
        this.lclSvr = lclSvr;
    }

    public String getLclCod() {
        return lclCod;
    }

    public void setLclCod(String lclCod) {
        this.lclCod = lclCod;
    }

    public String getThdChk() {
        return thdChk;
    }

    public void setThdChk(String thdChk) {
        this.thdChk = thdChk;
    }

    public String getThdSvr() {
        return thdSvr;
    }

    public void setThdSvr(String thdSvr) {
        this.thdSvr = thdSvr;
    }

    public String getThdCod() {
        return thdCod;
    }

    public void setThdCod(String thdCod) {
        this.thdCod = thdCod;
    }

    public String getAgtBr() {
        return agtBr;
    }

    public void setAgtBr(String agtBr) {
        this.agtBr = agtBr;
    }
}