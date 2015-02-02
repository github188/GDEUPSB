package com.bocom.bbip.gdeupsb.entity;

public class GdLotPlnCtl extends GdLotPlnCtlKey {
    private static final long serialVersionUID = -8998131016845156610L;

    private String drawNm;

    private String betDat;

    private String begTim;

    private String endTim;

    private String txnSts;

    public String getDrawNm() {
        return drawNm;
    }

    public void setDrawNm(String drawNm) {
        this.drawNm = drawNm;
    }

    public String getBetDat() {
        return betDat;
    }

    public void setBetDat(String betDat) {
        this.betDat = betDat;
    }

    public String getBegTim() {
        return begTim;
    }

    public void setBegTim(String begTim) {
        this.begTim = begTim;
    }

    public String getEndTim() {
        return endTim;
    }

    public void setEndTim(String endTim) {
        this.endTim = endTim;
    }

    public String getTxnSts() {
        return txnSts;
    }

    public void setTxnSts(String txnSts) {
        this.txnSts = txnSts;
    }
}