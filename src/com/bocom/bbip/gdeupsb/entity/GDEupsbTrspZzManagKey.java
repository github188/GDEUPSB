package com.bocom.bbip.gdeupsb.entity;

import com.bocom.bbip.data.annotation.GeneratedValue;
import com.bocom.bbip.data.annotation.Id;
import com.bocom.bbip.data.annotation.StepSequenceGenerator;
import java.io.Serializable;

public class GDEupsbTrspZzManagKey implements Serializable {
    private static final long serialVersionUID = 2804547662048244317L;

    @Id
    @GeneratedValue
    @StepSequenceGenerator(type="GDEupsbTrspZzManagNodNo")
    private String nodNo;

    @Id
    @GeneratedValue
    @StepSequenceGenerator(type="GDEupsbTrspZzManagZzDat")
    private String zzDat;

    public String getNodNo() {
        return nodNo;
    }

    public void setNodNo(String nodNo) {
        this.nodNo = nodNo;
    }

    public String getZzDat() {
        return zzDat;
    }

    public void setZzDat(String zzDat) {
        this.zzDat = zzDat;
    }
}