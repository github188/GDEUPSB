package com.bocom.bbip.gdeupsb.entity;

import com.bocom.bbip.data.annotation.GeneratedValue;
import com.bocom.bbip.data.annotation.Id;
import java.math.BigDecimal;
import java.util.Date;

public class GDEupsbAmountInfo {
    @Id
    @GeneratedValue
    private String sqn;

    private String bk;

    private String br;

    private String txnTlr;

    private String chlTyp;

    private Date txnDte;

    private Date txnTme;

    private String txnCde;

    private String rapTyp;

    private String busKnd;

    private String eupsBusTyp;

    private String comNo;

    private String thdRgnNo;

    private String barCde;

    private String thdCusNo;

    private String thdSubCusNo;

    private String thdCusNme;

    private String thdCusAdr;

    private String contNo;

    private String cusAc;

    private String owePrd;

    private BigDecimal oweFeeAmt;

    private BigDecimal pbd;

    private BigDecimal curBal;

    private String bakFld1;

    private String bakFld2;

    private String rsvFld1;

    private String rsvFld2;

    private String rsvFld3;

    private String rsvFld4;

    private String rsvFld5;

    private String rsvFld6;

    public String getSqn() {
        return sqn;
    }

    public void setSqn(String sqn) {
        this.sqn = sqn;
    }

    public String getBk() {
        return bk;
    }

    public void setBk(String bk) {
        this.bk = bk;
    }

    public String getBr() {
        return br;
    }

    public void setBr(String br) {
        this.br = br;
    }

    public String getTxnTlr() {
        return txnTlr;
    }

    public void setTxnTlr(String txnTlr) {
        this.txnTlr = txnTlr;
    }

    public String getChlTyp() {
        return chlTyp;
    }

    public void setChlTyp(String chlTyp) {
        this.chlTyp = chlTyp;
    }

    public Date getTxnDte() {
        return txnDte;
    }

    public void setTxnDte(Date txnDte) {
        this.txnDte = txnDte;
    }

    public Date getTxnTme() {
        return txnTme;
    }

    public void setTxnTme(Date txnTme) {
        this.txnTme = txnTme;
    }

    public String getTxnCde() {
        return txnCde;
    }

    public void setTxnCde(String txnCde) {
        this.txnCde = txnCde;
    }

    public String getRapTyp() {
        return rapTyp;
    }

    public void setRapTyp(String rapTyp) {
        this.rapTyp = rapTyp;
    }

    public String getBusKnd() {
        return busKnd;
    }

    public void setBusKnd(String busKnd) {
        this.busKnd = busKnd;
    }

    public String getEupsBusTyp() {
        return eupsBusTyp;
    }

    public void setEupsBusTyp(String eupsBusTyp) {
        this.eupsBusTyp = eupsBusTyp;
    }

    public String getComNo() {
        return comNo;
    }

    public void setComNo(String comNo) {
        this.comNo = comNo;
    }

    public String getThdRgnNo() {
        return thdRgnNo;
    }

    public void setThdRgnNo(String thdRgnNo) {
        this.thdRgnNo = thdRgnNo;
    }

    public String getBarCde() {
        return barCde;
    }

    public void setBarCde(String barCde) {
        this.barCde = barCde;
    }

    public String getThdCusNo() {
        return thdCusNo;
    }

    public void setThdCusNo(String thdCusNo) {
        this.thdCusNo = thdCusNo;
    }

    public String getThdSubCusNo() {
        return thdSubCusNo;
    }

    public void setThdSubCusNo(String thdSubCusNo) {
        this.thdSubCusNo = thdSubCusNo;
    }

    public String getThdCusNme() {
        return thdCusNme;
    }

    public void setThdCusNme(String thdCusNme) {
        this.thdCusNme = thdCusNme;
    }

    public String getThdCusAdr() {
        return thdCusAdr;
    }

    public void setThdCusAdr(String thdCusAdr) {
        this.thdCusAdr = thdCusAdr;
    }

    public String getContNo() {
        return contNo;
    }

    public void setContNo(String contNo) {
        this.contNo = contNo;
    }

    public String getCusAc() {
        return cusAc;
    }

    public void setCusAc(String cusAc) {
        this.cusAc = cusAc;
    }

    public String getOwePrd() {
        return owePrd;
    }

    public void setOwePrd(String owePrd) {
        this.owePrd = owePrd;
    }

    public BigDecimal getOweFeeAmt() {
        return oweFeeAmt;
    }

    public void setOweFeeAmt(BigDecimal oweFeeAmt) {
        this.oweFeeAmt = oweFeeAmt;
    }

    public BigDecimal getPbd() {
        return pbd;
    }

    public void setPbd(BigDecimal pbd) {
        this.pbd = pbd;
    }

    public BigDecimal getCurBal() {
        return curBal;
    }

    public void setCurBal(BigDecimal curBal) {
        this.curBal = curBal;
    }

    public String getBakFld1() {
        return bakFld1;
    }

    public void setBakFld1(String bakFld1) {
        this.bakFld1 = bakFld1;
    }

    public String getBakFld2() {
        return bakFld2;
    }

    public void setBakFld2(String bakFld2) {
        this.bakFld2 = bakFld2;
    }

    public String getRsvFld1() {
        return rsvFld1;
    }

    public void setRsvFld1(String rsvFld1) {
        this.rsvFld1 = rsvFld1;
    }

    public String getRsvFld2() {
        return rsvFld2;
    }

    public void setRsvFld2(String rsvFld2) {
        this.rsvFld2 = rsvFld2;
    }

    public String getRsvFld3() {
        return rsvFld3;
    }

    public void setRsvFld3(String rsvFld3) {
        this.rsvFld3 = rsvFld3;
    }

    public String getRsvFld4() {
        return rsvFld4;
    }

    public void setRsvFld4(String rsvFld4) {
        this.rsvFld4 = rsvFld4;
    }

    public String getRsvFld5() {
        return rsvFld5;
    }

    public void setRsvFld5(String rsvFld5) {
        this.rsvFld5 = rsvFld5;
    }

    public String getRsvFld6() {
        return rsvFld6;
    }

    public void setRsvFld6(String rsvFld6) {
        this.rsvFld6 = rsvFld6;
    }

	@Override
	public String toString() {
		return "EupsAmountInfo [sqn=" + sqn + ", bk=" + bk + ", br=" + br
				+ ", txnTlr=" + txnTlr + ", chlTyp=" + chlTyp + ", txnDte="
				+ txnDte + ", txnTme=" + txnTme + ", txnCde=" + txnCde
				+ ", rapTyp=" + rapTyp + ", busKnd=" + busKnd + ", eupsBusTyp="
				+ eupsBusTyp + ", comNo=" + comNo + ", thdRgnNo=" + thdRgnNo
				+ ", barCde=" + barCde + ", thdCusNo=" + thdCusNo
				+ ", thdSubCusNo=" + thdSubCusNo + ", thdCusNme=" + thdCusNme
				+ ", thdCusAdr=" + thdCusAdr + ", contNo=" + contNo
				+ ", cusAc=" + cusAc + ", owePrd=" + owePrd + ", oweFeeAmt="
				+ oweFeeAmt + ", pbd=" + pbd + ", curBal=" + curBal
				+ ", bakFld1=" + bakFld1 + ", bakFld2=" + bakFld2
				+ ", rsvFld1=" + rsvFld1 + ", rsvFld2=" + rsvFld2
				+ ", rsvFld3=" + rsvFld3 + ", rsvFld4=" + rsvFld4
				+ ", rsvFld5=" + rsvFld5 + ", rsvFld6=" + rsvFld6
				+ ", getSqn()=" + getSqn() + ", getBk()=" + getBk()
				+ ", getBr()=" + getBr() + ", getTxnTlr()=" + getTxnTlr()
				+ ", getChlTyp()=" + getChlTyp() + ", getTxnDte()="
				+ getTxnDte() + ", getTxnTme()=" + getTxnTme()
				+ ", getTxnCde()=" + getTxnCde() + ", getRapTyp()="
				+ getRapTyp() + ", getBusKnd()=" + getBusKnd()
				+ ", getEupsBusTyp()=" + getEupsBusTyp() + ", getComNo()="
				+ getComNo() + ", getThdRgnNo()=" + getThdRgnNo()
				+ ", getBarCde()=" + getBarCde() + ", getThdCusNo()="
				+ getThdCusNo() + ", getThdSubCusNo()=" + getThdSubCusNo()
				+ ", getThdCusNme()=" + getThdCusNme() + ", getThdCusAdr()="
				+ getThdCusAdr() + ", getContNo()=" + getContNo()
				+ ", getCusAc()=" + getCusAc() + ", getOwePrd()=" + getOwePrd()
				+ ", getOweFeeAmt()=" + getOweFeeAmt() + ", getPbd()="
				+ getPbd() + ", getCurBal()=" + getCurBal() + ", getBakFld1()="
				+ getBakFld1() + ", getBakFld2()=" + getBakFld2()
				+ ", getRsvFld1()=" + getRsvFld1() + ", getRsvFld2()="
				+ getRsvFld2() + ", getRsvFld3()=" + getRsvFld3()
				+ ", getRsvFld4()=" + getRsvFld4() + ", getRsvFld5()="
				+ getRsvFld5() + ", getRsvFld6()=" + getRsvFld6()
				+ ", getClass()=" + getClass() + ", hashCode()=" + hashCode()
				+ ", toString()=" + super.toString() + "]";
	}
    
    
}