package com.bocom.bbip.gdeupsb.entity;

import java.util.Date;

import com.bocom.bbip.utils.NumberUtils;
import com.bocom.bbip.utils.StringUtils;

public class GDEupsZhAGBatchTemp {
	private String sqn;

	private String batNo;

	private String comNo;

	private String txnTlr;

	private Date subDte;

	private String cusAc;

	private String cusNme;

	private String txnAmt;

	private String thdCusNo;

	private String thdCusNme;

	private String rsvFld1;

	private String rsvFld2;

	private String rsvFld3;
	private String rsvFld4;

	public String getSqn() {
		return sqn;
	}

	public void setSqn(String sqn) {
		this.sqn = sqn;
	}

	public String getBatNo() {
		return batNo;
	}

	public void setBatNo(String batNo) {
		this.batNo = batNo == null ? null : batNo.trim();
	}

	public String getComNo() {
		return comNo= comNo == null ? " " : comNo;
	}

	public void setComNo(String comNo) {
		this.comNo = comNo == null ? null : comNo;
	}

	public String getTxnTlr() {
		return txnTlr= txnTlr == null ? " " : txnTlr;
	}

	public void setTxnTlr(String txnTlr) {
		this.txnTlr = txnTlr == null ? " " : txnTlr;
	}

	public Date getSubDte() {
		return subDte=subDte==null? new Date():subDte;
	}

	public void setSubDte(Date subDte) {
		this.subDte = subDte;
	}

	public String getCusAc() {
		return cusAc= cusAc == null ? " " : cusAc;
	}

	public void setCusAc(String cusAc) {
		this.cusAc = cusAc ;
	}

	public String getCusNme() {
		return cusNme = cusNme == null ? " " :cusNme;
	}

	public void setCusNme(String cusNme) {
		this.cusNme = cusNme ;
	}

	public String getTxnAmt() {
		return txnAmt=txnAmt==null?" ":txnAmt;
	}

	public void setTxnAmt(String txnAmt) {
		this.txnAmt=txnAmt;
		/*final String temp=NumberUtils.yuanToCentString(txnAmt.trim());
		this.txnAmt=StringUtils.leftPad(temp, 18, "0");*/
	}

	public String getThdCusNo() {
		return thdCusNo= thdCusNo == null ? " " : thdCusNo;
	}

	public void setThdCusNo(String thdCusNo) {
		this.thdCusNo = thdCusNo ;
	}

	public String getThdCusNme() {
		return thdCusNme = thdCusNme == null ? " " :thdCusNme;
	}

	public void setThdCusNme(String thdCusNme) {
		this.thdCusNme = thdCusNme ;
	}

	public String getRsvFld1() {
		return rsvFld1= rsvFld1 == null ? " " : rsvFld1;
	}

	public void setRsvFld1(String rsvFld1) {
		this.rsvFld1 = rsvFld1 ;
	}

	public String getRsvFld2() {
		return rsvFld2= rsvFld2 == null ? " " : rsvFld2;
	}

	public void setRsvFld2(String rsvFld2) {
		this.rsvFld2 = rsvFld2 ;
	}

	public String getRsvFld3() {
		return rsvFld3= rsvFld3 == null ? " " : rsvFld3;
	}

	public void setRsvFld3(String rsvFld3) {
		this.rsvFld3 = rsvFld3 ;
	}

	public String getRsvFld4() {
		return rsvFld4= rsvFld4 == null ? " " : rsvFld4;
	}

	public void setRsvFld4(String rsvFld4) {
		this.rsvFld4 = rsvFld4 ;
	}
}
