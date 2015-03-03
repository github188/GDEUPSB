package com.bocom.bbip.gdeupsb.entity;

import java.util.Date;

import com.bocom.bbip.utils.NumberUtils;
import com.bocom.bbip.utils.StringUtils;

public class GDEupsZhAGBatchTemp {
	private Integer sqn;

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

	public Integer getSqn() {
		return sqn;
	}

	public void setSqn(Integer sqn) {
		this.sqn = sqn;
	}

	public String getBatNo() {
		return batNo;
	}

	public void setBatNo(String batNo) {
		this.batNo = batNo == null ? null : batNo.trim();
	}

	public String getComNo() {
		return comNo;
	}

	public void setComNo(String comNo) {
		this.comNo = comNo == null ? null : comNo.trim();
	}

	public String getTxnTlr() {
		return txnTlr;
	}

	public void setTxnTlr(String txnTlr) {
		this.txnTlr = txnTlr == null ? null : txnTlr.trim();
	}

	public Date getSubDte() {
		return subDte;
	}

	public void setSubDte(Date subDte) {
		this.subDte = subDte;
	}

	public String getCusAc() {
		return cusAc;
	}

	public void setCusAc(String cusAc) {
		this.cusAc = cusAc == null ? null : cusAc.trim();
	}

	public String getCusNme() {
		return cusNme;
	}

	public void setCusNme(String cusNme) {
		this.cusNme = cusNme == null ? null : cusNme.trim();
	}

	public String getTxnAmt() {
		return txnAmt;
	}

	public void setTxnAmt(String txnAmt) {
		final String temp=NumberUtils.yuanToCentString(txnAmt.trim());
		this.txnAmt=StringUtils.leftPad(temp, 18, "0");
	}

	public String getThdCusNo() {
		return thdCusNo;
	}

	public void setThdCusNo(String thdCusNo) {
		this.thdCusNo = thdCusNo == null ? null : thdCusNo.trim();
	}

	public String getThdCusNme() {
		return thdCusNme;
	}

	public void setThdCusNme(String thdCusNme) {
		this.thdCusNme = thdCusNme == null ? null : thdCusNme.trim();
	}

	public String getRsvFld1() {
		return rsvFld1;
	}

	public void setRsvFld1(String rsvFld1) {
		this.rsvFld1 = rsvFld1 == null ? null : rsvFld1.trim();
	}

	public String getRsvFld2() {
		return rsvFld2;
	}

	public void setRsvFld2(String rsvFld2) {
		this.rsvFld2 = rsvFld2 == null ? null : rsvFld2.trim();
	}

	public String getRsvFld3() {
		return rsvFld3;
	}

	public void setRsvFld3(String rsvFld3) {
		this.rsvFld3 = rsvFld3 == null ? null : rsvFld3.trim();
	}

	public String getRsvFld4() {
		return rsvFld4;
	}

	public void setRsvFld4(String rsvFld4) {
		this.rsvFld4 = rsvFld4 == null ? null : rsvFld4.trim();
	}
}
