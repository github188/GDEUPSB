package com.bocom.bbip.gdeupsb.entity;

import com.bocom.bbip.data.annotation.GeneratedValue;
import com.bocom.bbip.data.annotation.Id;

public class GdLotDrwTbl {
    @Id
    @GeneratedValue
    private Long sqn;

    private String gameId;

    private String drawId;

    private String drawNm;

    private String salStr;

    private String salEnd;

    private String cshStr;

    private String cshEnd;

    private String kenoId;

    private String kenoNm;

    private String kSalSt;

    private String kSalEd;

    private String chkFlg;

    private String chkTim;

    private String dowPrz;

    private String przAmt;

    private String totAmt;

    private String difFlg;

    private String difAmt;

    private String payFlg;

    private String payAmt;

    private String flwCtl;

    private String rtnTim;

    private String clrTim;

    private String xfeFlg;

    public Long getSqn() {
        return sqn;
    }

    public void setSqn(Long sqn) {
        this.sqn = sqn;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getDrawId() {
        return drawId;
    }

    public void setDrawId(String drawId) {
        this.drawId = drawId;
    }

    public String getDrawNm() {
        return drawNm;
    }

    public void setDrawNm(String drawNm) {
        this.drawNm = drawNm;
    }

    public String getSalStr() {
        return salStr;
    }

    public void setSalStr(String salStr) {
        this.salStr = salStr;
    }

    public String getSalEnd() {
        return salEnd;
    }

    public void setSalEnd(String salEnd) {
        this.salEnd = salEnd;
    }

    public String getCshStr() {
        return cshStr;
    }

    public void setCshStr(String cshStr) {
        this.cshStr = cshStr;
    }

    public String getCshEnd() {
        return cshEnd;
    }

    public void setCshEnd(String cshEnd) {
        this.cshEnd = cshEnd;
    }

    public String getKenoId() {
        return kenoId;
    }

    public void setKenoId(String kenoId) {
        this.kenoId = kenoId;
    }

    public String getKenoNm() {
        return kenoNm;
    }

    public void setKenoNm(String kenoNm) {
        this.kenoNm = kenoNm;
    }

    public String getkSalSt() {
        return kSalSt;
    }

    public void setkSalSt(String kSalSt) {
        this.kSalSt = kSalSt;
    }

    public String getkSalEd() {
        return kSalEd;
    }

    public void setkSalEd(String kSalEd) {
        this.kSalEd = kSalEd;
    }

    public String getChkFlg() {
        return chkFlg;
    }

    public void setChkFlg(String chkFlg) {
        this.chkFlg = chkFlg;
    }

    public String getChkTim() {
        return chkTim;
    }

    public void setChkTim(String chkTim) {
        this.chkTim = chkTim;
    }

    public String getDowPrz() {
        return dowPrz;
    }

    public void setDowPrz(String dowPrz) {
        this.dowPrz = dowPrz;
    }

    public String getPrzAmt() {
        return przAmt;
    }

    public void setPrzAmt(String przAmt) {
        this.przAmt = przAmt;
    }

    public String getTotAmt() {
        return totAmt;
    }

    public void setTotAmt(String totAmt) {
        this.totAmt = totAmt;
    }

    public String getDifFlg() {
        return difFlg;
    }

    public void setDifFlg(String difFlg) {
        this.difFlg = difFlg;
    }

    public String getDifAmt() {
        return difAmt;
    }

    public void setDifAmt(String difAmt) {
        this.difAmt = difAmt;
    }

    public String getPayFlg() {
        return payFlg;
    }

    public void setPayFlg(String payFlg) {
        this.payFlg = payFlg;
    }

    public String getPayAmt() {
        return payAmt;
    }

    public void setPayAmt(String payAmt) {
        this.payAmt = payAmt;
    }

    public String getFlwCtl() {
        return flwCtl;
    }

    public void setFlwCtl(String flwCtl) {
        this.flwCtl = flwCtl;
    }

    public String getRtnTim() {
        return rtnTim;
    }
    
    public void setRtnTim(String rtnTim) {
        this.rtnTim = rtnTim;
    }

    public String getClrTim() {
        return clrTim;
    }

    public void setClrTim(String clrTim) {
        this.clrTim = clrTim;
    }

    public String getXfeFlg() {
        return xfeFlg;
    }
    public void setXfeFlg(String xfeFlg) {
        this.xfeFlg = xfeFlg;
    }
}