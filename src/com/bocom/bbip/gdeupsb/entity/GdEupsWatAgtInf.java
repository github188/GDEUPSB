package com.bocom.bbip.gdeupsb.entity;

import com.bocom.bbip.data.annotation.GeneratedValue;
import com.bocom.bbip.data.annotation.Id;

public class GdEupsWatAgtInf {
    @Id
    @GeneratedValue
    private String agdAgrNo;

    private String cusAc;

    private String cusNme;

    private String pwd;

    private String thdCusNo;

    private String thdCusNme;

    private String idTyp;

    private String idNo;

    private String blNme;

    private String addr;

    private String hphone;

    private String lphone;

    private String post;

    private String sjman;

    private String postno;

    private String taddr;

    private String agtSts;

    public String getAgdAgrNo() {
        return agdAgrNo;
    }

    public void setAgdAgrNo(String agdAgrNo) {
        this.agdAgrNo = agdAgrNo;
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

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getThdCusNo() {
        return thdCusNo;
    }

    public void setThdCusNo(String thdCusNo) {
        this.thdCusNo = thdCusNo;
    }

    public String getThdCusNme() {
        return thdCusNme;
    }

    public void setThdCusNme(String thdCusNme) {
        this.thdCusNme = thdCusNme;
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

    public String getBlNme() {
        return blNme;
    }

    public void setBlNme(String blNme) {
        this.blNme = blNme;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getHphone() {
        return hphone;
    }

    public void setHphone(String hphone) {
        this.hphone = hphone;
    }

    public String getLphone() {
        return lphone;
    }

    public void setLphone(String lphone) {
        this.lphone = lphone;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getSjman() {
        return sjman;
    }

    public void setSjman(String sjman) {
        this.sjman = sjman;
    }

    public String getPostno() {
        return postno;
    }

    public void setPostno(String postno) {
        this.postno = postno;
    }

    public String getTaddr() {
        return taddr;
    }

    public void setTaddr(String taddr) {
        this.taddr = taddr;
    }

    public String getAgtSts() {
        return agtSts;
    }

    public void setAgtSts(String agtSts) {
        this.agtSts = agtSts;
    }
}