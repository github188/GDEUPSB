package com.bocom.bbip.gdeupsb.entity;

import com.bocom.bbip.data.annotation.GeneratedValue;
import com.bocom.bbip.data.annotation.Id;

public class GDEupsbLotCusInf {
    @Id
    @GeneratedValue
    private String crdno;

    private String brno;

    private String cusnam;

    private String actno;

    private String actnod;

    private String idtyp;

    private String idno;

    private String mobtel;

    private String fixtel;

    private String lotnam;

    private String lotpsw;

    private String regtim;

    private String email;

    private String cityid;

    private String sex;

    private String bthday;

    private String status;

    public String getCrdno() {
        return crdno;
    }

    public void setCrdno(String crdno) {
        this.crdno = crdno;
    }

    public String getBrno() {
        return brno;
    }

    public void setBrno(String brno) {
        this.brno = brno;
    }

    public String getCusnam() {
        return cusnam;
    }

    public void setCusnam(String cusnam) {
        this.cusnam = cusnam;
    }

    public String getActno() {
        return actno;
    }

    public void setActno(String actno) {
        this.actno = actno;
    }

    public String getActnod() {
        return actnod;
    }

    public void setActnod(String actnod) {
        this.actnod = actnod;
    }

    public String getIdtyp() {
        return idtyp;
    }

    public void setIdtyp(String idtyp) {
        this.idtyp = idtyp;
    }

    public String getIdno() {
        return idno;
    }

    public void setIdno(String idno) {
        this.idno = idno;
    }

    public String getMobtel() {
        return mobtel;
    }

    public void setMobtel(String mobtel) {
        this.mobtel = mobtel;
    }

    public String getFixtel() {
        return fixtel;
    }

    public void setFixtel(String fixtel) {
        this.fixtel = fixtel;
    }

    public String getLotnam() {
        return lotnam;
    }

    public void setLotnam(String lotnam) {
        this.lotnam = lotnam;
    }

    public String getLotpsw() {
        return lotpsw;
    }

    public void setLotpsw(String lotpsw) {
        this.lotpsw = lotpsw;
    }

    public String getRegtim() {
        return regtim;
    }

    public void setRegtim(String regtim) {
        this.regtim = regtim;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCityid() {
        return cityid;
    }

    public void setCityid(String cityid) {
        this.cityid = cityid;
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