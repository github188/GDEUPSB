package com.bocom.bbip.gdeupsb.entity;

import java.util.Date;

public class EupsStreamNoList {
	
		private String payNo;
		private String eupsBusTyp;
		private Date startDate;
		private Date endDate;

		public String getPayNo() {
			return payNo;
		}
		public void setPayNo(String payNo) {
			this.payNo = payNo;
		}
		public String getEupsBusTyp() {
			return eupsBusTyp;
		}
		public void setEupsBusTyp(String eupsBusTyp) {
			this.eupsBusTyp = eupsBusTyp;
		}
		public Date getStartDate() {
			return startDate;
		}
		public void setStartDate(Date startDate) {
			this.startDate = startDate;
		}
		public Date getEndDate() {
			return endDate;
		}
		public void setEndDate(Date endDate) {
			this.endDate = endDate;
		}
}
