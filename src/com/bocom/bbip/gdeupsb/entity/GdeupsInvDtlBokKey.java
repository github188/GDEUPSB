package com.bocom.bbip.gdeupsb.entity;

import com.bocom.bbip.data.annotation.GeneratedValue;
import com.bocom.bbip.data.annotation.Id;
import com.bocom.bbip.data.annotation.StepSequenceGenerator;
import java.io.Serializable;

public class GdeupsInvDtlBokKey implements Serializable {
    private static final long serialVersionUID = -1039648919754870647L;

    @Id
    @GeneratedValue
    @StepSequenceGenerator(type="GdeupsInvDtlBokInvTyp")
    private String invTyp;

    @Id
    @GeneratedValue
    @StepSequenceGenerator(type="GdeupsInvDtlBokIvBegNo")
    private String ivBegNo;

    @Id
    @GeneratedValue
    @StepSequenceGenerator(type="GdeupsInvDtlBokIvEndNo")
    private String ivEndNo;

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
}