package com.bocom.bbip.gdeupsb.entity;

import com.bocom.bbip.data.annotation.GeneratedValue;
import com.bocom.bbip.data.annotation.Id;
import com.bocom.bbip.data.annotation.StepSequenceGenerator;
import java.io.Serializable;

public class GdsagtmobKey implements Serializable {
    private static final long serialVersionUID = 3755374033374115239L;

    @Id
    @GeneratedValue
    @StepSequenceGenerator(type="GdsagtmobGdsbid")
    private String gdsbid;

    @Id
    @GeneratedValue
    @StepSequenceGenerator(type="GdsagtmobActno")
    private String actno;

    @Id
    @GeneratedValue
    @StepSequenceGenerator(type="GdsagtmobGdsaid")
    private String gdsaid;

    @Id
    @GeneratedValue
    @StepSequenceGenerator(type="GdsagtmobTcusid")
    private String tcusid;

    public String getGdsbid() {
        return gdsbid;
    }

    public void setGdsbid(String gdsbid) {
        this.gdsbid = gdsbid;
    }

    public String getActno() {
        return actno;
    }

    public void setActno(String actno) {
        this.actno = actno;
    }

    public String getGdsaid() {
        return gdsaid;
    }

    public void setGdsaid(String gdsaid) {
        this.gdsaid = gdsaid;
    }

    public String getTcusid() {
        return tcusid;
    }

    public void setTcusid(String tcusid) {
        this.tcusid = tcusid;
    }
}