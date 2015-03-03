package com.bocom.bbip.gdeupsb.entity;

public class GdeupsInvTxnInf extends GdeupsInvTxnInfKey {
    private static final long serialVersionUID = -7788487750964860092L;

    private String stlNum;

    private String stlFlg;

    private String actDat;

    private String tlrId;

    private String nodno;

    private String qyNo;

    private String oldSeq;

    private String oldTrDate;

    public String getStlNum() {
        return stlNum;
    }

    public void setStlNum(String stlNum) {
        this.stlNum = stlNum;
    }

    public String getStlFlg() {
        return stlFlg;
    }

    public void setStlFlg(String stlFlg) {
        this.stlFlg = stlFlg;
    }

    public String getActDat() {
        return actDat;
    }

    public void setActDat(String actDat) {
        this.actDat = actDat;
    }

    public String getTlrId() {
        return tlrId;
    }

    public void setTlrId(String tlrId) {
        this.tlrId = tlrId;
    }

    public String getNodno() {
        return nodno;
    }

    public void setNodno(String nodno) {
        this.nodno = nodno;
    }

    public String getQyNo() {
        return qyNo;
    }

    public void setQyNo(String qyNo) {
        this.qyNo = qyNo;
    }

    public String getOldSeq() {
        return oldSeq;
    }

    public void setOldSeq(String oldSeq) {
        this.oldSeq = oldSeq;
    }

    public String getOldTrDate() {
        return oldTrDate;
    }

    public void setOldTrDate(String oldTrDate) {
        this.oldTrDate = oldTrDate;
    }
}