package com.bocom.bbip.gdeupsb.entity;

import com.bocom.bbip.data.annotation.GeneratedValue;
import com.bocom.bbip.data.annotation.Id;
import java.math.BigDecimal;
import java.util.Date;

public class GDEupsbTrspTxnJnl {
    @Id
    @GeneratedValue
    private String sqn;

    private String brNo;

    private String ttxnCd;

    private String txnCod;

    private String txnTyp;

    private String busTyp;

    private String crpCod;

    private Date actDat;

    private BigDecimal txnAmt;

    private BigDecimal fee;

    private String actTyp;

    private String actNo;

    private String actSqn;

    private String nodNo;

    private String tlrId;

    private String trmNo;

    private String nodTrc;

    private String txnCnl;

    private String itgTyp;

    private Date ftxnTm;

    private String frspCd;

    private String hlogNo;

    private String hrspCd;

    private String htxnSt;

    private String tckNo;

    private String htxnCd;

    private String htxnSb;

    private String txnSrc;

    private String carTyp;

    private String carNo;

    private String payMon;

    private String tcusNm;

    private Date tactDt;

    private String tlogNo;

    private String thdKey;

    private String trspCd;

    private String ttxnSt;

    private String txnSt;

    private String txnAtr;

    private String txnMod;

    private String payMod;

    private String cagtNo;

    private String tactNo;

    private Date txnDat;

    private String rvsRsp;

    private String invNo;

    private String ctblNm;

    private Long extKey;

    private String bakFld1;

    private String bakFld2;

    private String bakFld3;

    private String rsvFld1;

    public String getSqn() {
        return sqn;
    }

    public void setSqn(String sqn) {
        this.sqn = sqn;
    }

    public String getBrNo() {
        return brNo;
    }

    public void setBrNo(String brNo) {
        this.brNo = brNo;
    }

    public String getTtxnCd() {
        return ttxnCd;
    }

    public void setTtxnCd(String ttxnCd) {
        this.ttxnCd = ttxnCd;
    }

    public String getTxnCod() {
        return txnCod;
    }

    public void setTxnCod(String txnCod) {
        this.txnCod = txnCod;
    }

    public String getTxnTyp() {
        return txnTyp;
    }

    public void setTxnTyp(String txnTyp) {
        this.txnTyp = txnTyp;
    }

    public String getBusTyp() {
        return busTyp;
    }

    public void setBusTyp(String busTyp) {
        this.busTyp = busTyp;
    }

    public String getCrpCod() {
        return crpCod;
    }

    public void setCrpCod(String crpCod) {
        this.crpCod = crpCod;
    }

    public Date getActDat() {
        return actDat;
    }

    public void setActDat(Date actDat) {
        this.actDat = actDat;
    }

    public BigDecimal getTxnAmt() {
        return txnAmt;
    }

    public void setTxnAmt(BigDecimal txnAmt) {
        this.txnAmt = txnAmt;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
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

    public String getActSqn() {
        return actSqn;
    }

    public void setActSqn(String actSqn) {
        this.actSqn = actSqn;
    }

    public String getNodNo() {
        return nodNo;
    }

    public void setNodNo(String nodNo) {
        this.nodNo = nodNo;
    }

    public String getTlrId() {
        return tlrId;
    }

    public void setTlrId(String tlrId) {
        this.tlrId = tlrId;
    }

    public String getTrmNo() {
        return trmNo;
    }

    public void setTrmNo(String trmNo) {
        this.trmNo = trmNo;
    }

    public String getNodTrc() {
        return nodTrc;
    }

    public void setNodTrc(String nodTrc) {
        this.nodTrc = nodTrc;
    }

    public String getTxnCnl() {
        return txnCnl;
    }

    public void setTxnCnl(String txnCnl) {
        this.txnCnl = txnCnl;
    }

    public String getItgTyp() {
        return itgTyp;
    }

    public void setItgTyp(String itgTyp) {
        this.itgTyp = itgTyp;
    }

    public Date getFtxnTm() {
        return ftxnTm;
    }

    public void setFtxnTm(Date ftxnTm) {
        this.ftxnTm = ftxnTm;
    }

    public String getFrspCd() {
        return frspCd;
    }

    public void setFrspCd(String frspCd) {
        this.frspCd = frspCd;
    }

    public String getHlogNo() {
        return hlogNo;
    }

    public void setHlogNo(String hlogNo) {
        this.hlogNo = hlogNo;
    }

    public String getHrspCd() {
        return hrspCd;
    }

    public void setHrspCd(String hrspCd) {
        this.hrspCd = hrspCd;
    }

    public String getHtxnSt() {
        return htxnSt;
    }

    public void setHtxnSt(String htxnSt) {
        this.htxnSt = htxnSt;
    }

    public String getTckNo() {
        return tckNo;
    }

    public void setTckNo(String tckNo) {
        this.tckNo = tckNo;
    }

    public String getHtxnCd() {
        return htxnCd;
    }

    public void setHtxnCd(String htxnCd) {
        this.htxnCd = htxnCd;
    }

    public String getHtxnSb() {
        return htxnSb;
    }

    public void setHtxnSb(String htxnSb) {
        this.htxnSb = htxnSb;
    }

    public String getTxnSrc() {
        return txnSrc;
    }

    public void setTxnSrc(String txnSrc) {
        this.txnSrc = txnSrc;
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

    public String getPayMon() {
        return payMon;
    }

    public void setPayMon(String payMon) {
        this.payMon = payMon;
    }

    public String getTcusNm() {
        return tcusNm;
    }

    public void setTcusNm(String tcusNm) {
        this.tcusNm = tcusNm;
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

    public String getThdKey() {
        return thdKey;
    }

    public void setThdKey(String thdKey) {
        this.thdKey = thdKey;
    }

    public String getTrspCd() {
        return trspCd;
    }

    public void setTrspCd(String trspCd) {
        this.trspCd = trspCd;
    }

    public String getTtxnSt() {
        return ttxnSt;
    }

    public void setTtxnSt(String ttxnSt) {
        this.ttxnSt = ttxnSt;
    }

    public String getTxnSt() {
        return txnSt;
    }

    public void setTxnSt(String txnSt) {
        this.txnSt = txnSt;
    }

    public String getTxnAtr() {
        return txnAtr;
    }

    public void setTxnAtr(String txnAtr) {
        this.txnAtr = txnAtr;
    }

    public String getTxnMod() {
        return txnMod;
    }

    public void setTxnMod(String txnMod) {
        this.txnMod = txnMod;
    }

    public String getPayMod() {
        return payMod;
    }

    public void setPayMod(String payMod) {
        this.payMod = payMod;
    }

    public String getCagtNo() {
        return cagtNo;
    }

    public void setCagtNo(String cagtNo) {
        this.cagtNo = cagtNo;
    }

    public String getTactNo() {
        return tactNo;
    }

    public void setTactNo(String tactNo) {
        this.tactNo = tactNo;
    }

    public Date getTxnDat() {
        return txnDat;
    }

    public void setTxnDat(Date txnDat) {
        this.txnDat = txnDat;
    }

    public String getRvsRsp() {
        return rvsRsp;
    }

    public void setRvsRsp(String rvsRsp) {
        this.rvsRsp = rvsRsp;
    }

    public String getInvNo() {
        return invNo;
    }

    public void setInvNo(String invNo) {
        this.invNo = invNo;
    }

    public String getCtblNm() {
        return ctblNm;
    }

    public void setCtblNm(String ctblNm) {
        this.ctblNm = ctblNm;
    }

    public Long getExtKey() {
        return extKey;
    }

    public void setExtKey(Long extKey) {
        this.extKey = extKey;
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

    public String getBakFld3() {
        return bakFld3;
    }

    public void setBakFld3(String bakFld3) {
        this.bakFld3 = bakFld3;
    }

    public String getRsvFld1() {
        return rsvFld1;
    }

    public void setRsvFld1(String rsvFld1) {
        this.rsvFld1 = rsvFld1;
    }
}