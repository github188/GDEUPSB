package com.bocom.bbip.gdeupsb.entity;

import com.bocom.bbip.data.annotation.GeneratedValue;
import com.bocom.bbip.data.annotation.Id;
import com.bocom.bbip.data.annotation.StepSequenceGenerator;
import java.io.Serializable;

public class GdsAgtInfKey implements Serializable {
    private static final long serialVersionUID = -4366601404471380790L;

    @Id
    @GeneratedValue
    @StepSequenceGenerator(type="GdsAgtInfGdsBid")
    private String gdsBid;

    @Id
    @GeneratedValue
    @StepSequenceGenerator(type="GdsAgtInfActNo")
    private String actNo;

    public String getGdsBid() {
        return gdsBid;
    }

    public void setGdsBid(String gdsBid) {
        this.gdsBid = gdsBid;
    }

    public String getActNo() {
        return actNo;
    }

    public void setActNo(String actNo) {
        this.actNo = actNo;
    }
}