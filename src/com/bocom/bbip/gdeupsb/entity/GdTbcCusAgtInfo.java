package com.bocom.bbip.gdeupsb.entity;

import com.bocom.bbip.data.annotation.GeneratedValue;
import com.bocom.bbip.data.annotation.Id;

public class GdTbcCusAgtInfo {
    @Id
    @GeneratedValue
    private String custId;

    private String brNo;

    private String comNo;

    private String dptId;

    private String comId;

    private String cusNm;

    private String cusTyp;

    private String pasTyp;

    private String pasId;

    private String liceId;

    private String accTyp;

    private String actNo;

    private String pasFlg;

    private String pasWrd;

    private String addr;

    private String telNum;

    private String devId;

    private String teller;

    private String opnDat;

    private String clsDat;

    private String status;

    private String rsvFld1;

    private String rsvFld2;

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
    }

    public String getBrNo() {
        return brNo;
    }

    public void setBrNo(String brNo) {
        this.brNo = brNo;
    }

    public String getComNo() {
        return comNo;
    }

    public void setComNo(String comNo) {
        this.comNo = comNo;
    }

    public String getDptId() {
        return dptId;
    }

    public void setDptId(String dptId) {
        this.dptId = dptId;
    }

    public String getComId() {
        return comId;
    }

    public void setComId(String comId) {
        this.comId = comId;
    }

    public String getCusNm() {
        return cusNm;
    }

    public void setCusNm(String cusNm) {
        this.cusNm = cusNm;
    }

    public String getCusTyp() {
        return cusTyp;
    }

    public void setCusTyp(String cusTyp) {
        this.cusTyp = cusTyp;
    }

    public String getPasTyp() {
        return pasTyp;
    }

    public void setPasTyp(String pasTyp) {
        this.pasTyp = pasTyp;
    }

    public String getPasId() {
        return pasId;
    }

    public void setPasId(String pasId) {
        this.pasId = pasId;
    }

    public String getLiceId() {
        return liceId;
    }

    public void setLiceId(String liceId) {
        this.liceId = liceId;
    }

    public String getAccTyp() {
        return accTyp;
    }

    public void setAccTyp(String accTyp) {
        this.accTyp = accTyp;
    }

    public String getActNo() {
        return actNo;
    }

    public void setActNo(String actNo) {
        this.actNo = actNo;
    }

    public String getPasFlg() {
        return pasFlg;
    }

    public void setPasFlg(String pasFlg) {
        this.pasFlg = pasFlg;
    }

    public String getPasWrd() {
        return pasWrd;
    }

    public void setPasWrd(String pasWrd) {
        this.pasWrd = pasWrd;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getTelNum() {
        return telNum;
    }

    public void setTelNum(String telNum) {
        this.telNum = telNum;
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

    public String getOpnDat() {
        return opnDat;
    }

    public void setOpnDat(String opnDat) {
        this.opnDat = opnDat;
    }

    public String getClsDat() {
        return clsDat;
    }

    public void setClsDat(String clsDat) {
        this.clsDat = clsDat;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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