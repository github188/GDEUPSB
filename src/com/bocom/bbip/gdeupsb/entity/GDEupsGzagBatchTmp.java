package com.bocom.bbip.gdeupsb.entity;

import com.bocom.bbip.data.annotation.GeneratedValue;
import com.bocom.bbip.data.annotation.Id;
import java.math.BigDecimal;
import java.util.Date;

public class GDEupsGzagBatchTmp {
    @Id
    @GeneratedValue
    private String sqn;

    private String lendtypeOrGameno;

    private String thdCusNo;

    private String txnDte;

    private Date txnTme;

    private String cusAc;

    private String thdCusNme;

    private BigDecimal txnAmt;

    private String bvNo;

    private String bakFld;

    private String rspCode;

    private String lendStartDate;

    private String lendEndDate;

    private String number;

    private String totCnt;

    private BigDecimal totAmt;

    private String sucTotCnt;

    private BigDecimal sucTotAmt;

    private String falTotCnt;

    private BigDecimal falTotAmt;

    private String sitrLogicNo;

    private BigDecimal pourAmt;

    private String lottSqn;

    private String bankSqn;

    private String eupsBusTyp;

    private String awardDate;

    private BigDecimal buyLottAmt;

    private String telephone;

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

    public String getLendtypeOrGameno() {
        return lendtypeOrGameno;
    }

    public void setLendtypeOrGameno(String lendtypeOrGameno) {
        this.lendtypeOrGameno = lendtypeOrGameno;
    }

    public String getThdCusNo() {
        return thdCusNo;
    }

    public void setThdCusNo(String thdCusNo) {
        this.thdCusNo = thdCusNo;
    }

    public String getTxnDte() {
        return txnDte;
    }

    public void setTxnDte(String txnDte) {
        this.txnDte = txnDte;
    }

    public Date getTxnTme() {
        return txnTme;
    }

    public void setTxnTme(Date txnTme) {
        this.txnTme = txnTme;
    }

    public String getCusAc() {
        return cusAc;
    }

    public void setCusAc(String cusAc) {
        this.cusAc = cusAc;
    }

    public String getThdCusNme() {
        return thdCusNme;
    }

    public void setThdCusNme(String thdCusNme) {
        this.thdCusNme = thdCusNme;
    }

    public BigDecimal getTxnAmt() {
        return txnAmt;
    }

    public void setTxnAmt(BigDecimal txnAmt) {
        this.txnAmt = txnAmt;
    }

    public String getBvNo() {
        return bvNo;
    }

    public void setBvNo(String bvNo) {
        this.bvNo = bvNo;
    }

    public String getBakFld() {
        return bakFld;
    }

    public void setBakFld(String bakFld) {
        this.bakFld = bakFld;
    }

    public String getRspCode() {
        return rspCode;
    }

    public void setRspCode(String rspCode) {
        this.rspCode = rspCode;
    }

    public String getLendStartDate() {
        return lendStartDate;
    }

    public void setLendStartDate(String lendStartDate) {
        this.lendStartDate = lendStartDate;
    }

    public String getLendEndDate() {
        return lendEndDate;
    }

    public void setLendEndDate(String lendEndDate) {
        this.lendEndDate = lendEndDate;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
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

    public void setTotamt(BigDecimal totAmt) {
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

    public String getSitrLogicNo() {
        return sitrLogicNo;
    }

    public void setSitrLogicNo(String sitrLogicNo) {
        this.sitrLogicNo = sitrLogicNo;
    }

    public BigDecimal getPourAmt() {
        return pourAmt;
    }

    public void setPourAmt(BigDecimal pourAmt) {
        this.pourAmt = pourAmt;
    }

    public String getLottSqn() {
        return lottSqn;
    }

    public void setLottSqn(String lottSqn) {
        this.lottSqn = lottSqn;
    }

    public String getBankSqn() {
        return bankSqn;
    }

    public void setBankSqn(String bankSqn) {
        this.bankSqn = bankSqn;
    }

    public String getEupsBusTyp() {
        return eupsBusTyp;
    }

    public void setEupsBusTyp(String eupsBusTyp) {
        this.eupsBusTyp = eupsBusTyp;
    }

    public String getAwardDate() {
        return awardDate;
    }

    public void setAwardDate(String awardDate) {
        this.awardDate = awardDate;
    }

    public BigDecimal getBuyLottAmt() {
        return buyLottAmt;
    }

    public void setBuyLottAmt(BigDecimal buyLottAmt) {
        this.buyLottAmt = buyLottAmt;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
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
}