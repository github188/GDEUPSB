package com.bocom.bbip.gdeupsb.entity;

import java.math.BigDecimal;

public class AgtFileBatchDetail {
	private String CUSAC;
	private String CUSNME;
	private BigDecimal TXNAMT;	
	private String AGTSRVCUSID;
	private String AGTSRVCUSNME;
	private String OUROTHFLG;
	private String OBKBK;
	private String RMK1;
	private String RMK2;
	public String getCUSAC() {
		return CUSAC;
	}
	public void setCUSAC(String cUSAC) {
		CUSAC = cUSAC;
	}
	public String getCUSNME() {
		return CUSNME;
	}
	public void setCUSNME(String cUSNME) {
		CUSNME = cUSNME;
	}
	public BigDecimal getTXNAMT() {
		return TXNAMT;
	}
	public void setTXNAMT(BigDecimal tXNAMT) {
		TXNAMT = tXNAMT;
	}
	public String getAGTSRVCUSID() {
		return AGTSRVCUSID;
	}
	public void setAGTSRVCUSID(String aGTSRVCUSID) {
		AGTSRVCUSID = aGTSRVCUSID;
	}
	public String getAGTSRVCUSNME() {
		return AGTSRVCUSNME;
	}
	public void setAGTSRVCUSNME(String aGTSRVCUSNME) {
		AGTSRVCUSNME = aGTSRVCUSNME;
	}
	public String getOUROTHFLG() {
		return OUROTHFLG;
	}
	public void setOUROTHFLG(String oUROTHFLG) {
		OUROTHFLG = oUROTHFLG;
	}
	public String getOBKBK() {
		return OBKBK;
	}
	public void setOBKBK(String oBKBK) {
		OBKBK = oBKBK;
	}
	public String getRMK1() {
		return RMK1;
	}
	public void setRMK1(String rMK1) {
		RMK1 = rMK1;
	}
	public String getRMK2() {
		return RMK2;
	}
	public void setRMK2(String rMK2) {
		RMK2 = rMK2;
	}
	
}
