package com.bocom.bbip.gdeupsb.entity;

import com.bocom.bbip.data.annotation.GeneratedValue;
import com.bocom.bbip.data.annotation.Id;
import java.util.Date;

public class GDEupsbTrspNpManag {
    @Id
    @GeneratedValue
    private String idNo;

    private String nodNo;

    private String sqn;

    private String carNo;

    private String invNo;

    private Date prtDat;

    private Date begDat;

    private Date endDat;

    private String status;

    private String tlrId;

    public String getIdNo() {
        return idNo;
    }

    public void setIdNo(String idNo) {
        this.idNo = idNo;
    }

    public String getNodNo() {
        return nodNo;
    }

    public void setNodNo(String nodNo) {
        this.nodNo = nodNo;
    }

    public String getSqn() {
        return sqn;
    }

    public void setSqn(String sqn) {
        this.sqn = sqn;
    }

    public String getCarNo() {
        return carNo;
    }

    public void setCarNo(String carNo) {
        this.carNo = carNo;
    }

    public String getInvNo() {
        return invNo;
    }

    public void setInvNo(String invNo) {
        this.invNo = invNo;
    }

    public Date getPrtDat() {
        return prtDat;
    }

    public void setPrtDat(Date prtDat) {
        this.prtDat = prtDat;
    }

    public Date getBegDat() {
        return begDat;
    }

    public void setBegDat(Date begDat) {
        this.begDat = begDat;
    }

    public Date getEndDat() {
        return endDat;
    }

    public void setEndDat(Date endDat) {
        this.endDat = endDat;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTlrId() {
        return tlrId;
    }

    public void setTlrId(String tlrId) {
        this.tlrId = tlrId;
    }
}