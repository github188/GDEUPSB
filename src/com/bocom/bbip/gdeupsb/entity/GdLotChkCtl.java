package com.bocom.bbip.gdeupsb.entity;

import com.bocom.bbip.data.annotation.GeneratedValue;
import com.bocom.bbip.data.annotation.Id;

public class GdLotChkCtl {
    @Id
    @GeneratedValue
    private Long sqn;

    private String chkDat;

    private String gameId;

    private String drawId;

    private String kenoId;

    private String totNum;

    private String totAmt;

    private String chkFlg;

    private String chkTim;

    public Long getSqn() {
        return sqn;
    }

    public void setSqn(Long sqn) {
        this.sqn = sqn;
    }

    public String getChkDat() {
        return chkDat;
    }

    public void setChkDat(String chkDat) {
        this.chkDat = chkDat;
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