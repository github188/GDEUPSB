package com.bocom.bbip.gdeupsb.entity;

import com.bocom.bbip.data.annotation.GeneratedValue;
import com.bocom.bbip.data.annotation.Id;
import com.bocom.bbip.data.annotation.StepSequenceGenerator;
import java.io.Serializable;

public class GdsagtuncaKey implements Serializable {
    private static final long serialVersionUID = 9030684992746065647L;

    @Id
    @GeneratedValue
    @StepSequenceGenerator(type="GdsagtuncaGdsbid")
    private String gdsbid;

    @Id
    @GeneratedValue
    @StepSequenceGenerator(type="GdsagtuncaActno")
    private String actno;

    @Id
    @GeneratedValue
    @StepSequenceGenerator(type="GdsagtuncaGdsaid")
    private String gdsaid;

    @Id
    @GeneratedValue
    @StepSequenceGenerator(type="GdsagtuncaTcusid")
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