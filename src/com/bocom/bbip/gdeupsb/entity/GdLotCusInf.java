package com.bocom.bbip.gdeupsb.entity;

import com.bocom.bbip.data.annotation.GeneratedValue;
import com.bocom.bbip.data.annotation.Id;

public class GdLotCusInf {
    @Id
    @GeneratedValue
    private String crdNo;

    private String brNo;

    private String cusNam;

    private String actNo;

    private String actNod;

    private String idTyp;

    private String idNo;

    private String mobTel;

    private String fixTel;

    private String lotNam;

    private String lotPsw;

    private String regTim;

    private String email;

    private String cityId;

    private String sex;

    private String bthday;

    private String status;

    public String getCrdNo() {
        return crdNo;
    }

    public void setCrdNo(String crdNo) {
        this.crdNo = crdNo;
    }

    public String getBrNo() {
        return brNo;
    }

    public void setBrNo(String brNo) {
        this.brNo = brNo;
    }

    public String getCusNam() {
        return cusNam;
    }

    public void setCusNam(String cusNam) {
        this.cusNam = cusNam;
    }

    public String getActNo() {
        return actNo;
    }

    public void setActNo(String actNo) {
        this.actNo = actNo;
    }

    public String getActNod() {
        return actNod;
    }

    public void setActNod(String actNod) {
        this.actNod = actNod;
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

    public String getMobTel() {
        return mobTel;
    }

    public void setMobTel(String mobTel) {
        this.mobTel = mobTel;
    }

    public String getFixTel() {
        return fixTel;
    }

    public void setFixTel(String fixTel) {
        this.fixTel = fixTel;
    }

    public String getLotNam() {
        return lotNam;
    }

    public void setLotNam(String lotNam) {
        this.lotNam = lotNam;
    }

    public String getLotPsw() {
        return lotPsw;
    }

    public void setLotPsw(String lotPsw) {
        this.lotPsw = lotPsw;
    }

    public String getRegTim() {
        return regTim;
    }

    public void setRegTim(String regTim) {
        this.regTim = regTim;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getBthday() {
        return bthday;
    }

    public void setBthday(String bthday) {
        this.bthday = bthday;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}