package com.bocom.bbip.gdeupsb.entity;

import com.bocom.bbip.data.annotation.GeneratedValue;
import com.bocom.bbip.data.annotation.Id;

public class GDEupsbLotPrzCtl {
    @Id
    @GeneratedValue
    private String tLogNo;

    private String gameId;

    private String drawId;

    private String kenoId;

    private String cipher;

    private String bigBon;

    private String totPrz;

    private String txnLog;

    private String termId;

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

	public String getTxnLog() {
		return txnLog;
	}

	public void setTxnLog(String txnLog) {
		this.txnLog = txnLog;
	}

	public String getTermId() {
		return termId;
	}

	public void setTermId(String termId) {
		this.termId = termId;
	}

    
}