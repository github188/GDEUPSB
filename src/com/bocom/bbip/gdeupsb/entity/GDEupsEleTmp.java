package com.bocom.bbip.gdeupsb.entity;

import com.bocom.bbip.data.annotation.GeneratedValue;
import com.bocom.bbip.data.annotation.Id;
import java.math.BigDecimal;
import java.util.Date;

public class GDEupsEleTmp {
    @Id
    @GeneratedValue
    private String sqn;

    private String bankNo;

    private String comNo;

    private String fileDte;

    private String ccy;

    private String busKnd;

    private String totCnt;

    private BigDecimal totAmt;

    private String sucTotCnt;

    private BigDecimal sucTotAmt;

    private String falTotCnt;

    private BigDecimal falTotAmt;

    private String number;

    private String thdCusNo;

    private String thdCusNme;

    private String cusAc;

    private String cusNme;

    private String fullDedFlag;

    private String payType;

    private String accountNo;

    private String electricityYearmonth;

    private BigDecimal paymentMoney;

    private BigDecimal capitial;

    private BigDecimal dedit;

    private String paymentResult;

    private BigDecimal txnAmt;

    private String bankSqn;

    private Date txnDte;

    private Date txnTme;

    private String bakFld;

    private String rsvFld1;

    private String rsvFld2;

    private String rsvFld3;

    private String rsvFld4;

    private String rsvFld5;

    public String getSqn() {
        return sqn;
    }

    public void setSqn(String sqn) {
        this.sqn = sqn;
    }

    public String getBankNo() {
        return bankNo;
    }

    public void setBankNo(String bankNo) {
        this.bankNo = bankNo;
    }

    public String getComNo() {
        return comNo;
    }

    public void setComNo(String comNo) {
        this.comNo = comNo;
    }

    public String getFileDte() {
        return fileDte;
    }

    public void setFileDte(String fileDte) {
        this.fileDte = fileDte;
    }

    public String getCcy() {
        return ccy;
    }

    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    public String getBusKnd() {
        return busKnd;
    }

    public void setBusKnd(String busKnd) {
        this.busKnd = busKnd;
    }

    public String getTotCnt() {
        return totCnt;
    }

    public void setTotCnt(String totCnt) {
        this.totCnt = totCnt;
    }

    public BigDecimal getTotAmt() {
        return totAmt;
    }

    public void setTotAmt(BigDecimal totAmt) {
        this.totAmt = totAmt;
    }

    public String getSucTotCnt() {
        return sucTotCnt;
    }

    public void setSucTotCnt(String sucTotCnt) {
        this.sucTotCnt = sucTotCnt;
    }

    public BigDecimal getSucTotAmt() {
        return sucTotAmt;
    }

    public void setSucTotAmt(BigDecimal sucTotAmt) {
        this.sucTotAmt = sucTotAmt;
    }

    public String getFalTotCnt() {
        return falTotCnt;
    }

    public void setFalTotCnt(String falTotCnt) {
        this.falTotCnt = falTotCnt;
    }

    public BigDecimal getFalTotAmt() {
        return falTotAmt;
    }

    public void setFalTotAmt(BigDecimal falTotAmt) {
        this.falTotAmt = falTotAmt;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getThdCusNo() {
        return thdCusNo;
    }

    public void setThdCusNo(String thdCusNo) {
        this.thdCusNo = thdCusNo;
    }

    public String getThdCusNme() {
        return thdCusNme;
    }

    public void setThdCusNme(String thdCusNme) {
        this.thdCusNme = thdCusNme;
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

    public String getFullDedFlag() {
        return fullDedFlag;
    }

    public void setFullDedFlag(String fullDedFlag) {
        this.fullDedFlag = fullDedFlag;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getElectricityYearmonth() {
        return electricityYearmonth;
    }

    public void setElectricityYearmonth(String electricityYearmonth) {
        this.electricityYearmonth = electricityYearmonth;
    }

    public BigDecimal getPaymentMoney() {
        return paymentMoney;
    }

    public void setPaymentMoney(BigDecimal paymentMoney) {
        this.paymentMoney = paymentMoney;
    }

    public BigDecimal getCapitial() {
        return capitial;
    }

    public void setCapitial(BigDecimal capitial) {
        this.capitial = capitial;
    }

    public BigDecimal getDedit() {
        return dedit;
    }

    public void setDedit(BigDecimal dedit) {
        this.dedit = dedit;
    }

    public String getPaymentResult() {
        return paymentResult;
    }

    public void setPaymentResult(String paymentResult) {
        this.paymentResult = paymentResult;
    }

    public BigDecimal getTxnAmt() {
        return txnAmt;
    }

    public void setTxnAmt(BigDecimal txnAmt) {
        this.txnAmt = txnAmt;
    }

    public String getBankSqn() {
        return bankSqn;
    }

    public void setBankSqn(String bankSqn) {
        this.bankSqn = bankSqn;
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

    public String getBakFld() {
        return bakFld;
    }

    public void setBakFld(String bakFld) {
        this.bakFld = bakFld;
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
}