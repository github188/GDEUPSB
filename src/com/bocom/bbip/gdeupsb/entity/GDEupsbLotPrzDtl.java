package com.bocom.bbip.gdeupsb.entity;

import com.bocom.bbip.data.annotation.GeneratedValue;
import com.bocom.bbip.data.annotation.Id;

public class GDEupsbLotPrzDtl {
    @Id
    @GeneratedValue
    private String tLogNo;

    private String gameId;

    private String drawId;

    private String kenoId;

    private String txnLog;

    private String betMod;

    private String betMul;

    private String classNo;

    private String przAmt;

    private String betLin;

	public String gettLogNo() {
		return tLogNo;
	}

	public void settLogNo(String tLogNo) {
		this.tLogNo = tLogNo;
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

	public String getTxnLog() {
		return txnLog;
	}

	public void setTxnLog(String txnLog) {
		this.txnLog = txnLog;
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

	public String getClassNo() {
		return classNo;
	}

	public void setClassNo(String classNo) {
		this.classNo = classNo;
	}

	public String getPrzAmt() {
		return przAmt;
	}

	public void setPrzAmt(String przAmt) {
		this.przAmt = przAmt;
	}

	public String getBetLin() {
		return betLin;
	}

	public void setBetLin(String betLin) {
		this.betLin = betLin;
	}


}