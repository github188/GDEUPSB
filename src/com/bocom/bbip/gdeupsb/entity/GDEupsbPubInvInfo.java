package com.bocom.bbip.gdeupsb.entity;

import com.bocom.bbip.data.annotation.GeneratedValue;
import com.bocom.bbip.data.annotation.Id;
import java.math.BigDecimal;
import java.util.Date;

public class GDEupsbPubInvInfo {
    @Id
    @GeneratedValue
    private String sqn;

    private String trType;

    private String recCnt;

    private String actNo;

    private String tcusId;

    private String fcusId;

    private Date trDate;

    private String tcusNm;

    private String iprnCnt;

    private String billDate;

    private BigDecimal txnAmt;

    private BigDecimal lastBal;

    private BigDecimal thisBal;

    private BigDecimal ipayAmt;

    private BigDecimal itotAmt;

    private String einvNo;

    private String staMon;

    private String endMon;

    private String tmp01;

    private String tmp02;

    private String mxCount;

    private String fpInf;

    public String getSqn() {
        return sqn;
    }

    public void setSqn(String sqn) {
        this.sqn = sqn;
    }

    public String getTrType() {
        return trType;
    }

    public void setTrType(String trType) {
        this.trType = trType;
    }

    public String getRecCnt() {
        return recCnt;
    }

    public void setRecCnt(String recCnt) {
        this.recCnt = recCnt;
    }

    public String getActNo() {
        return actNo;
    }

    public void setActNo(String actNo) {
        this.actNo = actNo;
    }

    public String getTcusId() {
        return tcusId;
    }

    public void setTcusId(String tcusId) {
        this.tcusId = tcusId;
    }

    public String getFcusId() {
        return fcusId;
    }

    public void setFcusId(String fcusId) {
        this.fcusId = fcusId;
    }

    public Date getTrDate() {
        return trDate;
    }

    public void setTrDate(Date trDate) {
        this.trDate = trDate;
    }

    public String getTcusNm() {
        return tcusNm;
    }

    public void setTcusNm(String tcusNm) {
        this.tcusNm = tcusNm;
    }

    public String getIprnCnt() {
        return iprnCnt;
    }

    public void setIprnCnt(String iprnCnt) {
        this.iprnCnt = iprnCnt;
    }

    public String getBillDate() {
        return billDate;
    }

    public void setBillDate(String billDate) {
        this.billDate = billDate;
    }

    public BigDecimal getTxnAmt() {
        return txnAmt;
    }

    public void setTxnAmt(BigDecimal txnAmt) {
        this.txnAmt = txnAmt;
    }

    public BigDecimal getLastBal() {
        return lastBal;
    }

    public void setLastBal(BigDecimal lastBal) {
        this.lastBal = lastBal;
    }

    public BigDecimal getThisBal() {
        return thisBal;
    }

    public void setThisBal(BigDecimal thisBal) {
        this.thisBal = thisBal;
    }

    public BigDecimal getIpayAmt() {
        return ipayAmt;
    }

    public void setIpayAmt(BigDecimal ipayAmt) {
        this.ipayAmt = ipayAmt;
    }

    public BigDecimal getItotAmt() {
        return itotAmt;
    }

    public void setItotAmt(BigDecimal itotAmt) {
        this.itotAmt = itotAmt;
    }

    public String getEinvNo() {
        return einvNo;
    }

    public void setEinvNo(String einvNo) {
        this.einvNo = einvNo;
    }

    public String getStaMon() {
        return staMon;
    }

    public void setStaMon(String staMon) {
        this.staMon = staMon;
    }

    public String getEndMon() {
        return endMon;
    }

    public void setEndMon(String endMon) {
        this.endMon = endMon;
    }

    public String getTmp01() {
        return tmp01;
    }

    public void setTmp01(String tmp01) {
        this.tmp01 = tmp01;
    }

    public String getTmp02() {
        return tmp02;
    }

    public void setTmp02(String tmp02) {
        this.tmp02 = tmp02;
    }

    public String getMxCount() {
        return mxCount;
    }

    public void setMxCount(String mxCount) {
        this.mxCount = mxCount;
    }

    public String getFpInf() {
        return fpInf;
    }

    public void setFpInf(String fpInf) {
        this.fpInf = fpInf;
    }
}