package com.bocom.bbip.gdeupsb.entity;

import com.bocom.bbip.data.annotation.GeneratedValue;
import com.bocom.bbip.data.annotation.Id;
import java.math.BigDecimal;
import java.util.Date;

public class EupsStreamNo {
    @Id
    @GeneratedValue
    private String sqn;

    private Date txnDte;

    private Date txnTme;

    private String bk;

    private String busKnd;

    private String eupsBusTyp;

    private String rapTyp;

    private String txnTyp;

    private String pfeTyp;

    private String itgTyp;

    private String txnSts;

    private String qrySqn;

    private String traceNo;

    private String reqSysCde;

    private String reqJrnNo;

    private Date reqTme;

    private String br;

    private String orgCde;

    private String tmlNo;

    private String txnTlr;

    private String chlTyp;

    private String athTlrNo;

    private String chkTlr;

    private String reqVchNo;

    private String txnCde;

    private String svrNme;

    private String comNo;

    private String rspCde;

    private String rspMsg;

    private String oldTxnSqn;

    private String thdObkCde;

    private String rvsTraceNo;

    private String thdRgnNo;

    private String thdCusNo;

    private String thdSubCusNo;

    private String thdCusNme;

    private String thdFeePrd;

    private String ourOthFlg;

    private String bkCdTyp;

    private String accTyp;

    private String drwMde;

    private String cusAc;

    private String cusNme;

    private String accSeq;

    private String bvKnd;

    private String bvNo;

    private String cshNo;

    private String choNo;

    private String payChl;

    private String ccy;

    private BigDecimal txnAmt;

    private BigDecimal reqTxnAmt;

    private BigDecimal hfe;

    private String hfeItmSeq;

    private String hfePstBr;

    private String fulDedFlg;

    private String rmkCde;

    private String lisAcc;

    private String splNo;

    private String splTerNo;

    private String idTyp;

    private String idNo;

    private String cmuTel;

    private String mfmTxnCde;

    private String mfmTxnSubCde;

    private Date acDte;

    private String mfmVchNo;

    private String mfmJrnNo;

    private String mfmTxnSts;

    private String mfmRspCde;

    private String mfmRspMsg;

    private String thdTxnCde;

    private Date thdTxnDte;

    private Date thdTxnTme;

    private String thdSqn;

    private String thdTxnSts;

    private String thdRspCde;

    private String thdRspMsg;

    private String rcnFlg;

    private String mfmDytRcnFlg;

    private String mfmEodRcnFlg;

    private String mfmRcnBat;

    private Date mfmRcnTme;

    private String extRcnFlg;

    private String thdRcnFlg;

    private Date thdRcnTme;

    private String thdRcnBat;

    private String isBatFlg;

    private String batNo;

    private String prtFlg;

    private Integer prtCnt;

    private String bakFld1;

    private String bakFld2;

    private String bakFld3;

    private String bakFld4;

    private String bakFld5;

    private String bakFld6;

    private String rsvFld1;

    private String rsvFld2;

    private String rsvFld3;

    private String rsvFld4;

    private String rsvFld5;

    private String rsvFld6;

    private String filFlg;

    public String getSqn() {
        return sqn;
    }

    public void setSqn(String sqn) {
        this.sqn = sqn;
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

    public String getBk() {
        return bk;
    }

    public void setBk(String bk) {
        this.bk = bk;
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

    public String getRapTyp() {
        return rapTyp;
    }

    public void setRapTyp(String rapTyp) {
        this.rapTyp = rapTyp;
    }

    public String getTxnTyp() {
        return txnTyp;
    }

    public void setTxnTyp(String txnTyp) {
        this.txnTyp = txnTyp;
    }

    public String getPfeTyp() {
        return pfeTyp;
    }

    public void setPfeTyp(String pfeTyp) {
        this.pfeTyp = pfeTyp;
    }

    public String getItgTyp() {
        return itgTyp;
    }

    public void setItgTyp(String itgTyp) {
        this.itgTyp = itgTyp;
    }

    public String getTxnSts() {
        return txnSts;
    }

    public void setTxnSts(String txnSts) {
        this.txnSts = txnSts;
    }

    public String getQrySqn() {
        return qrySqn;
    }

    public void setQrySqn(String qrySqn) {
        this.qrySqn = qrySqn;
    }

    public String getTraceNo() {
        return traceNo;
    }

    public void setTraceNo(String traceNo) {
        this.traceNo = traceNo;
    }

    public String getReqSysCde() {
        return reqSysCde;
    }

    public void setReqSysCde(String reqSysCde) {
        this.reqSysCde = reqSysCde;
    }

    public String getReqJrnNo() {
        return reqJrnNo;
    }

    public void setReqJrnNo(String reqJrnNo) {
        this.reqJrnNo = reqJrnNo;
    }

    public Date getReqTme() {
        return reqTme;
    }

    public void setReqTme(Date reqTme) {
        this.reqTme = reqTme;
    }

    public String getBr() {
        return br;
    }

    public void setBr(String br) {
        this.br = br;
    }

    public String getOrgCde() {
        return orgCde;
    }

    public void setOrgCde(String orgCde) {
        this.orgCde = orgCde;
    }

    public String getTmlNo() {
        return tmlNo;
    }

    public void setTmlNo(String tmlNo) {
        this.tmlNo = tmlNo;
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

    public String getAthTlrNo() {
        return athTlrNo;
    }

    public void setAthTlrNo(String athTlrNo) {
        this.athTlrNo = athTlrNo;
    }

    public String getChkTlr() {
        return chkTlr;
    }

    public void setChkTlr(String chkTlr) {
        this.chkTlr = chkTlr;
    }

    public String getReqVchNo() {
        return reqVchNo;
    }

    public void setReqVchNo(String reqVchNo) {
        this.reqVchNo = reqVchNo;
    }

    public String getTxnCde() {
        return txnCde;
    }

    public void setTxnCde(String txnCde) {
        this.txnCde = txnCde;
    }

    public String getSvrNme() {
        return svrNme;
    }

    public void setSvrNme(String svrNme) {
        this.svrNme = svrNme;
    }

    public String getComNo() {
        return comNo;
    }

    public void setComNo(String comNo) {
        this.comNo = comNo;
    }

    public String getRspCde() {
        return rspCde;
    }

    public void setRspCde(String rspCde) {
        this.rspCde = rspCde;
    }

    public String getRspMsg() {
        return rspMsg;
    }

    public void setRspMsg(String rspMsg) {
        this.rspMsg = rspMsg;
    }

    public String getOldTxnSqn() {
        return oldTxnSqn;
    }

    public void setOldTxnSqn(String oldTxnSqn) {
        this.oldTxnSqn = oldTxnSqn;
    }

    public String getThdObkCde() {
        return thdObkCde;
    }

    public void setThdObkCde(String thdObkCde) {
        this.thdObkCde = thdObkCde;
    }

    public String getRvsTraceNo() {
        return rvsTraceNo;
    }

    public void setRvsTraceNo(String rvsTraceNo) {
        this.rvsTraceNo = rvsTraceNo;
    }

    public String getThdRgnNo() {
        return thdRgnNo;
    }

    public void setThdRgnNo(String thdRgnNo) {
        this.thdRgnNo = thdRgnNo;
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

    public String getThdFeePrd() {
        return thdFeePrd;
    }

    public void setThdFeePrd(String thdFeePrd) {
        this.thdFeePrd = thdFeePrd;
    }

    public String getOurOthFlg() {
        return ourOthFlg;
    }

    public void setOurOthFlg(String ourOthFlg) {
        this.ourOthFlg = ourOthFlg;
    }

    public String getBkCdTyp() {
        return bkCdTyp;
    }

    public void setBkCdTyp(String bkCdTyp) {
        this.bkCdTyp = bkCdTyp;
    }

    public String getAccTyp() {
        return accTyp;
    }

    public void setAccTyp(String accTyp) {
        this.accTyp = accTyp;
    }

    public String getDrwMde() {
        return drwMde;
    }

    public void setDrwMde(String drwMde) {
        this.drwMde = drwMde;
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

    public String getAccSeq() {
        return accSeq;
    }

    public void setAccSeq(String accSeq) {
        this.accSeq = accSeq;
    }

    public String getBvKnd() {
        return bvKnd;
    }

    public void setBvKnd(String bvKnd) {
        this.bvKnd = bvKnd;
    }

    public String getBvNo() {
        return bvNo;
    }

    public void setBvNo(String bvNo) {
        this.bvNo = bvNo;
    }

    public String getCshNo() {
        return cshNo;
    }

    public void setCshNo(String cshNo) {
        this.cshNo = cshNo;
    }

    public String getChoNo() {
        return choNo;
    }

    public void setChoNo(String choNo) {
        this.choNo = choNo;
    }

    public String getPayChl() {
        return payChl;
    }

    public void setPayChl(String payChl) {
        this.payChl = payChl;
    }

    public String getCcy() {
        return ccy;
    }

    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    public BigDecimal getTxnAmt() {
        return txnAmt;
    }

    public void setTxnAmt(BigDecimal txnAmt) {
        this.txnAmt = txnAmt;
    }

    public BigDecimal getReqTxnAmt() {
        return reqTxnAmt;
    }

    public void setReqTxnAmt(BigDecimal reqTxnAmt) {
        this.reqTxnAmt = reqTxnAmt;
    }

    public BigDecimal getHfe() {
        return hfe;
    }

    public void setHfe(BigDecimal hfe) {
        this.hfe = hfe;
    }

    public String getHfeItmSeq() {
        return hfeItmSeq;
    }

    public void setHfeItmSeq(String hfeItmSeq) {
        this.hfeItmSeq = hfeItmSeq;
    }

    public String getHfePstBr() {
        return hfePstBr;
    }

    public void setHfePstBr(String hfePstBr) {
        this.hfePstBr = hfePstBr;
    }

    public String getFulDedFlg() {
        return fulDedFlg;
    }

    public void setFulDedFlg(String fulDedFlg) {
        this.fulDedFlg = fulDedFlg;
    }

    public String getRmkCde() {
        return rmkCde;
    }

    public void setRmkCde(String rmkCde) {
        this.rmkCde = rmkCde;
    }

    public String getLisAcc() {
        return lisAcc;
    }

    public void setLisAcc(String lisAcc) {
        this.lisAcc = lisAcc;
    }

    public String getSplNo() {
        return splNo;
    }

    public void setSplNo(String splNo) {
        this.splNo = splNo;
    }

    public String getSplTerNo() {
        return splTerNo;
    }

    public void setSplTerNo(String splTerNo) {
        this.splTerNo = splTerNo;
    }

    public String getIdTyp() {
        return idTyp;
    }

    public void setIdTyp(String idTyp) {
        this.idTyp = idTyp;
    }

    public String getIdNo() {
        return idNo;
    }

    public void setIdNo(String idNo) {
        this.idNo = idNo;
    }

    public String getCmuTel() {
        return cmuTel;
    }

    public void setCmuTel(String cmuTel) {
        this.cmuTel = cmuTel;
    }

    public String getMfmTxnCde() {
        return mfmTxnCde;
    }

    public void setMfmTxnCde(String mfmTxnCde) {
        this.mfmTxnCde = mfmTxnCde;
    }

    public String getMfmTxnSubCde() {
        return mfmTxnSubCde;
    }

    public void setMfmTxnSubCde(String mfmTxnSubCde) {
        this.mfmTxnSubCde = mfmTxnSubCde;
    }

    public Date getAcDte() {
        return acDte;
    }

    public void setAcDte(Date acDte) {
        this.acDte = acDte;
    }

    public String getMfmVchNo() {
        return mfmVchNo;
    }

    public void setMfmVchNo(String mfmVchNo) {
        this.mfmVchNo = mfmVchNo;
    }

    public String getMfmJrnNo() {
        return mfmJrnNo;
    }

    public void setMfmJrnNo(String mfmJrnNo) {
        this.mfmJrnNo = mfmJrnNo;
    }

    public String getMfmTxnSts() {
        return mfmTxnSts;
    }

    public void setMfmTxnSts(String mfmTxnSts) {
        this.mfmTxnSts = mfmTxnSts;
    }

    public String getMfmRspCde() {
        return mfmRspCde;
    }

    public void setMfmRspCde(String mfmRspCde) {
        this.mfmRspCde = mfmRspCde;
    }

    public String getMfmRspMsg() {
        return mfmRspMsg;
    }

    public void setMfmRspMsg(String mfmRspMsg) {
        this.mfmRspMsg = mfmRspMsg;
    }

    public String getThdTxnCde() {
        return thdTxnCde;
    }

    public void setThdTxnCde(String thdTxnCde) {
        this.thdTxnCde = thdTxnCde;
    }

    public Date getThdTxnDte() {
        return thdTxnDte;
    }

    public void setThdTxnDte(Date thdTxnDte) {
        this.thdTxnDte = thdTxnDte;
    }

    public Date getThdTxnTme() {
        return thdTxnTme;
    }

    public void setThdTxnTme(Date thdTxnTme) {
        this.thdTxnTme = thdTxnTme;
    }

    public String getThdSqn() {
        return thdSqn;
    }

    public void setThdSqn(String thdSqn) {
        this.thdSqn = thdSqn;
    }

    public String getThdTxnSts() {
        return thdTxnSts;
    }

    public void setThdTxnSts(String thdTxnSts) {
        this.thdTxnSts = thdTxnSts;
    }

    public String getThdRspCde() {
        return thdRspCde;
    }

    public void setThdRspCde(String thdRspCde) {
        this.thdRspCde = thdRspCde;
    }

    public String getThdRspMsg() {
        return thdRspMsg;
    }

    public void setThdRspMsg(String thdRspMsg) {
        this.thdRspMsg = thdRspMsg;
    }

    public String getRcnFlg() {
        return rcnFlg;
    }

    public void setRcnFlg(String rcnFlg) {
        this.rcnFlg = rcnFlg;
    }

    public String getMfmDytRcnFlg() {
        return mfmDytRcnFlg;
    }

    public void setMfmDytRcnFlg(String mfmDytRcnFlg) {
        this.mfmDytRcnFlg = mfmDytRcnFlg;
    }

    public String getMfmEodRcnFlg() {
        return mfmEodRcnFlg;
    }

    public void setMfmEodRcnFlg(String mfmEodRcnFlg) {
        this.mfmEodRcnFlg = mfmEodRcnFlg;
    }

    public String getMfmRcnBat() {
        return mfmRcnBat;
    }

    public void setMfmRcnBat(String mfmRcnBat) {
        this.mfmRcnBat = mfmRcnBat;
    }

    public Date getMfmRcnTme() {
        return mfmRcnTme;
    }

    public void setMfmRcnTme(Date mfmRcnTme) {
        this.mfmRcnTme = mfmRcnTme;
    }

    public String getExtRcnFlg() {
        return extRcnFlg;
    }

    public void setExtRcnFlg(String extRcnFlg) {
        this.extRcnFlg = extRcnFlg;
    }

    public String getThdRcnFlg() {
        return thdRcnFlg;
    }

    public void setThdRcnFlg(String thdRcnFlg) {
        this.thdRcnFlg = thdRcnFlg;
    }

    public Date getThdRcnTme() {
        return thdRcnTme;
    }

    public void setThdRcnTme(Date thdRcnTme) {
        this.thdRcnTme = thdRcnTme;
    }

    public String getThdRcnBat() {
        return thdRcnBat;
    }

    public void setThdRcnBat(String thdRcnBat) {
        this.thdRcnBat = thdRcnBat;
    }

    public String getIsBatFlg() {
        return isBatFlg;
    }

    public void setIsBatFlg(String isBatFlg) {
        this.isBatFlg = isBatFlg;
    }

    public String getBatNo() {
        return batNo;
    }

    public void setBatNo(String batNo) {
        this.batNo = batNo;
    }

    public String getPrtFlg() {
        return prtFlg;
    }

    public void setPrtFlg(String prtFlg) {
        this.prtFlg = prtFlg;
    }

    public Integer getPrtCnt() {
        return prtCnt;
    }

    public void setPrtCnt(Integer prtCnt) {
        this.prtCnt = prtCnt;
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

    public String getBakFld4() {
        return bakFld4;
    }

    public void setBakFld4(String bakFld4) {
        this.bakFld4 = bakFld4;
    }

    public String getBakFld5() {
        return bakFld5;
    }

    public void setBakFld5(String bakFld5) {
        this.bakFld5 = bakFld5;
    }

    public String getBakFld6() {
        return bakFld6;
    }

    public void setBakFld6(String bakFld6) {
        this.bakFld6 = bakFld6;
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

    public String getFilFlg() {
        return filFlg;
    }

    public void setFilFlg(String filFlg) {
        this.filFlg = filFlg;
    }
}