package com.bocom.bbip.gdeupsb.entity;

public class GdLotChkCtl extends GdLotChkCtlKey {
    private static final long serialVersionUID = 3534030432296360894L;

    private String chkDat;

    private String kenoId;

    private String totNum;

    private String totAmt;

    private String chkFlg;

    private String chkTim;

    public String getChkDat() {
        return chkDat;
    }

    public void setChkDat(String chkDat) {
        this.chkDat = chkDat;
    }

    public String getKenoId() {
        return kenoId;
    }

    public void setKenoId(String kenoId) {
        this.kenoId = kenoId;
    }

    public String getTotNum() {
        return totNum;
    }

    public void setTotNum(String totNum) {
        this.totNum = totNum;
    }

    public String getTotAmt() {
        return totAmt;
    }

    public void setTotAmt(String totAmt) {
        this.totAmt = totAmt;
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
}