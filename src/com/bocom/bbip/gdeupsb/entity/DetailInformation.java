package com.bocom.bbip.gdeupsb.entity;

import java.math.BigDecimal;

public class DetailInformation {
		private String  ApCode;
		private String  OFmtCd;
		private String  busType;
		private String  payType;
		private String  electricityYearMonth;
		private String  particularNo;
		private String  copyListDate;
		private String  beforeSaveThisTime;
		private String  showNumberThisMonth;
		private String  showNumberLastMonth;
		private String  useElectric;
		private String  addReduceElectric;
		private BigDecimal  price;
		private BigDecimal  dedit;
		private BigDecimal  paymentMoney;
		
		
		
		public BigDecimal getPrice() {
			return price;
		}
		public void setPrice(BigDecimal price) {
			this.price = price;
		}
		public BigDecimal getDedit() {
			return dedit;
		}
		public void setDedit(BigDecimal dedit) {
			this.dedit = dedit;
		}
		public BigDecimal getPaymentMoney() {
			return paymentMoney;
		}
		public void setPaymentMoney(BigDecimal paymentMoney) {
			this.paymentMoney = paymentMoney;
		}
		private String  paymentTime;
		private String  nullField;
		public String getApCode() {
			return ApCode;
		}
		public void setApCode(String apCode) {
			ApCode = apCode;
		}
		public String getOFmtCd() {
			return OFmtCd;
		}
		public void setOFmtCd(String oFmtCd) {
			OFmtCd = oFmtCd;
		}
		public String getBusType() {
			return busType;
		}
		public void setBusType(String busType) {
			this.busType = busType;
		}
		public String getPayType() {
			return payType;
		}
		public void setPayType(String payType) {
			this.payType = payType;
		}
		public String getElectricityYearMonth() {
			return electricityYearMonth;
		}
		public void setElectricityYearMonth(String electricityYearMonth) {
			this.electricityYearMonth = electricityYearMonth;
		}
		public String getParticularNo() {
			return particularNo;
		}
		public void setParticularNo(String particularNo) {
			this.particularNo = particularNo;
		}
		public String getCopyListDate() {
			return copyListDate;
		}
		public void setCopyListDate(String copyListDate) {
			this.copyListDate = copyListDate;
		}
		public String getBeforeSaveThisTime() {
			return beforeSaveThisTime;
		}
		public void setBeforeSaveThisTime(String beforeSaveThisTime) {
			this.beforeSaveThisTime = beforeSaveThisTime;
		}
		public String getShowNumberThisMonth() {
			return showNumberThisMonth;
		}
		public void setShowNumberThisMonth(String showNumberThisMonth) {
			this.showNumberThisMonth = showNumberThisMonth;
		}
		public String getShowNumberLastMonth() {
			return showNumberLastMonth;
		}
		public void setShowNumberLastMonth(String showNumberLastMonth) {
			this.showNumberLastMonth = showNumberLastMonth;
		}
		public String getUseElectric() {
			return useElectric;
		}
		public void setUseElectric(String useElectric) {
			this.useElectric = useElectric;
		}
		public String getAddReduceElectric() {
			return addReduceElectric;
		}
		public void setAddReduceElectric(String addReduceElectric) {
			this.addReduceElectric = addReduceElectric;
		}
	
		public String getPaymentTime() {
			return paymentTime;
		}
		public void setPaymentTime(String paymentTime) {
			this.paymentTime = paymentTime;
		}
		public String getNullField() {
			return nullField;
		}
		public void setNullField(String nullField) {
			this.nullField = nullField;
		}
		
		
}
