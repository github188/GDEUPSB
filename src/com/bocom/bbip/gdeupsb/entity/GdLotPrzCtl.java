package com.bocom.bbip.gdeupsb.entity;

import com.bocom.bbip.data.annotation.GeneratedValue;
import com.bocom.bbip.data.annotation.Id;

public class GdLotPrzCtl {
    @Id
    @GeneratedValue
    private Long sqn;

    private String gameId;

    private String drawId;

    private String kenoId;

    private String cipher;

    private String bigBon;

    private String totPrz;

    private String txnlog;

    private String tLogNo;

    private String termId;

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

    public String getKenoId() {
        return kenoId;
    }

    public void setKenoId(String kenoId) {
        this.kenoId = kenoId;
    }

    public String getCipher() {
        return cipher;
    }

    public void setCipher(String cipher) {
        this.cipher = cipher;
    }

    public String getBigBon() {
        return bigBon;
    }

    public void setBigBon(String bigBon) {
        this.bigBon = bigBon;
    }

    public String getTotPrz() {
        return totPrz;
    }

    public void setTotPrz(String totPrz) {
        this.totPrz = totPrz;
    }

    public String getTxnlog() {
        return txnlog;
    }

    public void setTxnlog(String txnlog) {
        this.txnlog = txnlog;
    }

    public String gettLogNo() {
        return tLogNo;
    }

    public void settLogNo(String tLogNo) {
        this.tLogNo = tLogNo;
    }

    public String getTermId() {
        return termId;
    }

    public void setTermId(String termId) {
        this.termId = termId;
    }
}