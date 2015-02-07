package com.bocom.bbip.gdeupsb.entity;

import com.bocom.bbip.data.annotation.GeneratedValue;
import com.bocom.bbip.data.annotation.Id;
import com.bocom.bbip.data.annotation.StepSequenceGenerator;
import java.io.Serializable;

public class GdsAgtWaterKey implements Serializable {
    private static final long serialVersionUID = 2466656730775866117L;

    @Id
    @GeneratedValue
    @StepSequenceGenerator(type="GdsAgtWaterGdsBid")
    private String gdsBid;

    @Id
    @GeneratedValue
    @StepSequenceGenerator(type="GdsAgtWaterActNo")
    private String actNo;

    @Id
    @GeneratedValue
    @StepSequenceGenerator(type="GdsAgtWaterGdsAid")
    private String gdsAid;

    @Id
    @GeneratedValue
    @StepSequenceGenerator(type="GdsAgtWaterTcusId")
    private String tcusId;

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

    public String getGdsAid() {
        return gdsAid;
    }

    public void setGdsAid(String gdsAid) {
        this.gdsAid = gdsAid;
    }

    public String getTcusId() {
        return tcusId;
    }

    public void setTcusId(String tcusId) {
        this.tcusId = tcusId;
    }
}