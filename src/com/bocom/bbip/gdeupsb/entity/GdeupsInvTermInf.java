package com.bocom.bbip.gdeupsb.entity;

import com.bocom.bbip.data.annotation.GeneratedValue;
import com.bocom.bbip.data.annotation.Id;

public class GdeupsInvTermInf {
    @Id
    @GeneratedValue
    private String tlrId;

    private String nodno;

    private String invTyp;

    private String ivBegNo;

    private String ivEndNo;

    private String seqNo;

    private String invNum;

    private String useDat;

    private String useNum;

    private String clrNum;

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

    public String getInvTyp() {
        return invTyp;
    }

    public void setInvTyp(String invTyp) {
        this.invTyp = invTyp;
    }

    public String getIvBegNo() {
        return ivBegNo;
    }

    public void setIvBegNo(String ivBegNo) {
        this.ivBegNo = ivBegNo;
    }

    public String getIvEndNo() {
        return ivEndNo;
    }

    public void setIvEndNo(String ivEndNo) {
        this.ivEndNo = ivEndNo;
    }

    public String getSeqNo() {
        return seqNo;
    }

    public void setSeqNo(String seqNo) {
        this.seqNo = seqNo;
    }

    public String getInvNum() {
        return invNum;
    }

    public void setInvNum(String invNum) {
        this.invNum = invNum;
    }

    public String getUseDat() {
        return useDat;
    }

    public void setUseDat(String useDat) {
        this.useDat = useDat;
    }

    public String getUseNum() {
        return useNum;
    }

    public void setUseNum(String useNum) {
        this.useNum = useNum;
    }

    public String getClrNum() {
        return clrNum;
    }

    public void setClrNum(String clrNum) {
        this.clrNum = clrNum;
    }
}