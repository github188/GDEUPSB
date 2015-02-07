package com.bocom.bbip.gdeupsb.entity;

import com.bocom.bbip.data.annotation.GeneratedValue;
import com.bocom.bbip.data.annotation.Id;
import java.math.BigDecimal;
import java.util.Date;

public class GDEupsbTrspFeeInfo {
    @Id
    @GeneratedValue
    private String thdKey;

    private String brNo;

    private String carTyp;

    private String carNo;

    private String tcusNm;

    private String payMon;

    private Date payDat;

    private String payLog;

    private String payNod;

    private String payTlr;

    private String payTck;

    private BigDecimal txnAmt;

    private String txnCnl;

    private String actTyp;

    private String actNo;

    private Date tactDt;

    private String tlogNo;

    private String prtNod;

    private String prtTlr;

    private String rvsLog;

    private Date rvsDat;

    private String rvsNod;

    private String rvsTlr;

    private String rvsTck;

    private String tchkNo;

    private Date chkTim;

    private String chkFlg;

    private String invNo;

    private Date begDat;

    private Date endDat;

    private String carName;

    private String carDzs;

    private String cntStd;

    private BigDecimal feeStd;

    private BigDecimal corpus;

    private BigDecimal lateFee;

    private String clgs;

    private String yybz;

    private String status;
    
    private String oinvNo;

    public String getOinvNo() {
		return oinvNo;
	}

	public void setOinvNo(String oinvNo) {
		this.oinvNo = oinvNo;
	}

	public String getThdKey() {
        return thdKey;
    }

    public void setThdKey(String thdKey) {
        this.thdKey = thdKey;
    }

    public String getBrNo() {
        return brNo;
    }

    public void setBrNo(String brNo) {
        this.brNo = brNo;
    }

    public String getCarTyp() {
        return carTyp;
    }

    public void setCarTyp(String carTyp) {
        this.carTyp = carTyp;
    }

    public String getCarNo() {
        return carNo;
    }

    public void setCarNo(String carNo) {
        this.carNo = carNo;
    }

    public String getTcusNm() {
        return tcusNm;
    }

    public void setTcusNm(String tcusNm) {
        this.tcusNm = tcusNm;
    }

    public String getPayMon() {
        return payMon;
    }

    public void setPayMon(String payMon) {
        this.payMon = payMon;
    }

    public Date getPayDat() {
        return payDat;
    }

    public void setPayDat(Date payDat) {
        this.payDat = payDat;
    }

    public String getPayLog() {
        return payLog;
    }

    public void setPayLog(String payLog) {
        this.payLog = payLog;
    }

    public String getPayNod() {
        return payNod;
    }

    public void setPayNod(String payNod) {
        this.payNod = payNod;
    }

    public String getPayTlr() {
        return payTlr;
    }

    public void setPayTlr(String payTlr) {
        this.payTlr = payTlr;
    }

    public String getPayTck() {
        return payTck;
    }

    public void setPayTck(String payTck) {
        this.payTck = payTck;
    }

    public BigDecimal getTxnAmt() {
        return txnAmt;
    }

    public void setTxnAmt(BigDecimal txnAmt) {
        this.txnAmt = txnAmt;
    }

    public String getTxnCnl() {
        return txnCnl;
    }

    public void setTxnCnl(String txnCnl) {
        this.txnCnl = txnCnl;
    }

    public String getActTyp() {
        return actTyp;
    }

    public void setActTyp(String actTyp) {
        this.actTyp = actTyp;
    }

    public String getActNo() {
        return actNo;
    }

    public void setActNo(String actNo) {
        this.actNo = actNo;
    }

    public Date getTactDt() {
        return tactDt;
    }

    public void setTactDt(Date tactDt) {
        this.tactDt = tactDt;
    }

    public String getTlogNo() {
        return tlogNo;
    }

    public void setTlogNo(String tlogNo) {
        this.tlogNo = tlogNo;
    }

    public String getPrtNod() {
        return prtNod;
    }

    public void setPrtNod(String prtNod) {
        this.prtNod = prtNod;
    }

    public String getPrtTlr() {
        return prtTlr;
    }

    public void setPrtTlr(String prtTlr) {
        this.prtTlr = prtTlr;
    }

    public String getRvsLog() {
        return rvsLog;
    }

    public void setRvsLog(String rvsLog) {
        this.rvsLog = rvsLog;
    }

    public Date getRvsDat() {
        return rvsDat;
    }

    public void setRvsDat(Date rvsDat) {
        this.rvsDat = rvsDat;
    }

    public String getRvsNod() {
        return rvsNod;
    }

    public void setRvsNod(String rvsNod) {
        this.rvsNod = rvsNod;
    }

    public String getRvsTlr() {
        return rvsTlr;
    }

    public void setRvsTlr(String rvsTlr) {
        this.rvsTlr = rvsTlr;
    }

    public String getRvsTck() {
        return rvsTck;
    }

    public void setRvsTck(String rvsTck) {
        this.rvsTck = rvsTck;
    }

    public String getTchkNo() {
        return tchkNo;
    }

    public void setTchkNo(String tchkNo) {
        this.tchkNo = tchkNo;
    }

    public Date getChkTim() {
        return chkTim;
    }

    public void setChkTim(Date chkTim) {
        this.chkTim = chkTim;
    }

    public String getChkFlg() {
        return chkFlg;
    }

    public void setChkFlg(String chkFlg) {
        this.chkFlg = chkFlg;
    }

    public String getInvNo() {
        return invNo;
    }

    public void setInvNo(String invNo) {
        this.invNo = invNo;
    }

    public Date getBegDat() {
        return begDat;
    }

    public void setBegDat(Date begDat) {
        this.begDat = begDat;
    }

    public Date getEndDat() {
        return endDat;
    }

    public void setEndDat(Date endDat) {
        this.endDat = endDat;
    }

    public String getCarName() {
        return carName;
    }

    public void setCarName(String carName) {
        this.carName = carName;
    }

    public String getCarDzs() {
        return carDzs;
    }

    public void setCarDzs(String carDzs) {
        this.carDzs = carDzs;
    }

    public String getCntStd() {
        return cntStd;
    }

    public void setCntStd(String cntStd) {
        this.cntStd = cntStd;
    }

    public BigDecimal getFeeStd() {
        return feeStd;
    }

    public void setFeeStd(BigDecimal feeStd) {
        this.feeStd = feeStd;
    }

    public BigDecimal getCorpus() {
        return corpus;
    }

    public void setCorpus(BigDecimal corpus) {
        this.corpus = corpus;
    }

    public BigDecimal getLateFee() {
        return lateFee;
    }

    public void setLateFee(BigDecimal lateFee) {
        this.lateFee = lateFee;
    }

    public String getClgs() {
        return clgs;
    }

    public void setClgs(String clgs) {
        this.clgs = clgs;
    }

    public String getYybz() {
        return yybz;
    }

    public void setYybz(String yybz) {
        this.yybz = yybz;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}