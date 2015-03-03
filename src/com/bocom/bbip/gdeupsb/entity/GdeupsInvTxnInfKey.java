package com.bocom.bbip.gdeupsb.entity;

import com.bocom.bbip.data.annotation.GeneratedValue;
import com.bocom.bbip.data.annotation.Id;
import com.bocom.bbip.data.annotation.StepSequenceGenerator;
import java.io.Serializable;

public class GdeupsInvTxnInfKey implements Serializable {
    private static final long serialVersionUID = -1523841179770388159L;

    @Id
    @GeneratedValue
    @StepSequenceGenerator(type="GdeupsInvTxnInfInvTyp")
    private String invTyp;

    @Id
    @GeneratedValue
    @StepSequenceGenerator(type="GdeupsInvTxnInfIvBegNo")
    private String ivBegNo;

    @Id
    @GeneratedValue
    @StepSequenceGenerator(type="GdeupsInvTxnInfIvEndNo")
    private String ivEndNo;

    @Id
    @GeneratedValue
    @StepSequenceGenerator(type="GdeupsInvTxnInfUseSeq")
    private String useSeq;

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

    public String getUseSeq() {
        return useSeq;
    }

    public void setUseSeq(String useSeq) {
        this.useSeq = useSeq;
    }
}