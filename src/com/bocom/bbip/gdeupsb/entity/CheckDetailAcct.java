package com.bocom.bbip.gdeupsb.entity;

import java.math.BigDecimal;
import java.util.Date;

public class CheckDetailAcct {
		private String sqn;
		private String comNo;
		private String electricityYearMonth;
		private String payNo;
		private String thdCusNme;
		private String bankNo;
		private String cusAc;
		private String cusNme;
		private Date txnDte;
		public String getThdCusNme() {
			return thdCusNme;
		}
		public void setThdCusNme(String thdCusNme) {
			this.thdCusNme = thdCusNme;
		}
		private BigDecimal txnAmt;
		private String bakFld1;
		private String txnTlr;
		private String onlySignCode;
		public String getSqn() {
			return sqn;
		}
		public void setSqn(String sqn) {
			this.sqn = sqn;
		}
		public String getComNo() {
			return comNo;
		}
		public void setComNo(String comNo) {
			this.comNo = comNo;
		}
		public String getElectricityYearMonth() {
			return electricityYearMonth;
		}
		public void setElectricityYearMonth(String electricityYearMonth) {
			this.electricityYearMonth = electricityYearMonth;
		}
		public String getPayNo() {
			return payNo;
		}
		public void setPayNo(String payNo) {
			this.payNo = payNo;
		}
		public String getBankNo() {
			return bankNo;
		}
		public void setBankNo(String bankNo) {
			this.bankNo = bankNo;
		}
		public String getCusAc() {
			return cusAc;
		}
		public void setCusAc(String cusAc) {
			this.cusAc = cusAc;
		}
		public String getCusNme() {
			return cusNme;
		}
		public void setCusNme(String cusNme) {
			this.cusNme = cusNme;
		}

		public Date getTxnDte() {
			return txnDte;
		}
		public void setTxnDte(Date txnDte) {
			this.txnDte = txnDte;
		}
		public BigDecimal getTxnAmt() {
			return txnAmt;
		}
		public void setTxnAmt(BigDecimal txnAmt) {
			this.txnAmt = txnAmt;
		}
		public String getBakFld1() {
			return bakFld1;
		}
		public void setBakFld1(String bakFld1) {
			this.bakFld1 = bakFld1;
		}
		public String getTxnTlr() {
			return txnTlr;
		}
		public void setTxnTlr(String txnTlr) {
			this.txnTlr = txnTlr;
		}
		public String getOnlySignCode() {
			return onlySignCode;
		}
		public void setOnlySignCode(String onlySignCode) {
			this.onlySignCode = onlySignCode;
		}
	
}
