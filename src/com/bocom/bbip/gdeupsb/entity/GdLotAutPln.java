package com.bocom.bbip.gdeupsb.entity;

import com.bocom.bbip.data.annotation.GeneratedValue;
import com.bocom.bbip.data.annotation.Id;

public class GdLotAutPln {
    @Id
    @GeneratedValue
    private String planNo;

    private String planNm;

    private String gameId;

    private String gamNam;

    private String playId;

    private String betPer;

    private String betMet;

    private String betMod;

    private String betMul;

    private String betAmt;

    private String betLin;

    private String lotNam;

    private String crdNo;

    private String mobTel;

    private String betDat;

    private String betTim;

    private String cclTim;

    private String curPer;

    private String curFal;

    private String conFal;

    private String doPer;

    private String txnCnl;

    private String status;

    public String getPlanNo() {
        return planNo;
    }

    public void setPlanNo(String planNo) {
        this.planNo = planNo;
    }

    public String getPlanNm() {
        return planNm;
    }

    public void setPlanNm(String planNm) {
        this.planNm = planNm;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getGamNam() {
        return gamNam;
    }

    public void setGamNam(String gamNam) {
        this.gamNam = gamNam;
    }

    public String getPlayId() {
        return playId;
    }

    public void setPlayId(String playId) {
        this.playId = playId;
    }

    public String getBetPer() {
        return betPer;
    }

    public void setBetPer(String betPer) {
        this.betPer = betPer;
    }

    public String getBetMet() {
        return betMet;
    }

    public void setBetMet(String betMet) {
        this.betMet = betMet;
    }

    public String getBetMod() {
        return betMod;
    }

    public void setBetMod(String betMod) {
        this.betMod = betMod;
    }

    public String getBetMul() {
        return betMul;
    }

    public void setBetMul(String betMul) {
        this.betMul = betMul;
    }

    public String getBetAmt() {
        return betAmt;
    }

    public void setBetAmt(String betAmt) {
        this.betAmt = betAmt;
    }

    public String getBetLin() {
        return betLin;
    }

    public void setBetLin(String betLin) {
        this.betLin = betLin;
    }

    public String getLotNam() {
        return lotNam;
    }

    public void setLotNam(String lotNam) {
        this.lotNam = lotNam;
    }

    public String getCrdNo() {
        return crdNo;
    }

    public void setCrdNo(String crdNo) {
        this.crdNo = crdNo;
    }

    public String getMobTel() {
        return mobTel;
    }

    public void setMobTel(String mobTel) {
        this.mobTel = mobTel;
    }

    public String getBetDat() {
        return betDat;
    }

    public void setBetDat(String betDat) {
        this.betDat = betDat;
    }

    public String getBetTim() {
        return betTim;
    }

    public void setBetTim(String betTim) {
        this.betTim = betTim;
    }

    public String getCclTim() {
        return cclTim;
    }

    public void setCclTim(String cclTim) {
        this.cclTim = cclTim;
    }

    public String getCurPer() {
        return curPer;
    }

    public void setCurPer(String curPer) {
        this.curPer = curPer;
    }

    public String getCurFal() {
        return curFal;
    }

    public void setCurFal(String curFal) {
        this.curFal = curFal;
    }

    public String getConFal() {
        return conFal;
    }

    public void setConFal(String conFal) {
        this.conFal = conFal;
    }

    public String getDoPer() {
        return doPer;
    }

    public void setDoPer(String doPer) {
        this.doPer = doPer;
    }

    public String getTxnCnl() {
        return txnCnl;
    }

    public void setTxnCnl(String txnCnl) {
        this.txnCnl = txnCnl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}