package com.bocom.bbip.gdeupsb.entity;

import com.bocom.bbip.data.annotation.GeneratedValue;
import com.bocom.bbip.data.annotation.Id;

public class GdLotSysCfg {
    @Id
    @GeneratedValue
    private String dealId;

    private String usrPam;

    private String usrPas;

    private String sigTim;

    private String lclTim;

    private String lotTim;

    private String diffTm;

    private String dsCAgtNo;

    private String dfCAgtNo;

    private String hsActNo;

    private String logSeq;

    private String whPhone;

    public String getDealId() {
        return dealId;
    }

    public void setDealId(String dealId) {
        this.dealId = dealId;
    }

    public String getUsrPam() {
        return usrPam;
    }

    public void setUsrPam(String usrPam) {
        this.usrPam = usrPam;
    }

    public String getUsrPas() {
        return usrPas;
    }

    public void setUsrPas(String usrPas) {
        this.usrPas = usrPas;
    }

    public String getSigTim() {
        return sigTim;
    }

    public void setSigTim(String sigTim) {
        this.sigTim = sigTim;
    }

    public String getLclTim() {
        return lclTim;
    }

    public void setLclTim(String lclTim) {
        this.lclTim = lclTim;
    }

    public String getLotTim() {
        return lotTim;
    }

    public void setLotTim(String lotTim) {
        this.lotTim = lotTim;
    }

    public String getDiffTm() {
        return diffTm;
    }

    public void setDiffTm(String diffTm) {
        this.diffTm = diffTm;
    }

    public String getDsCAgtNo() {
        return dsCAgtNo;
    }

    public void setDsCAgtNo(String dsCAgtNo) {
        this.dsCAgtNo = dsCAgtNo;
    }

    public String getDfCAgtNo() {
        return dfCAgtNo;
    }

    public void setDfCAgtNo(String dfCAgtNo) {
        this.dfCAgtNo = dfCAgtNo;
    }

    public String getHsActNo() {
        return hsActNo;
    }

    public void setHsActNo(String hsActNo) {
        this.hsActNo = hsActNo;
    }

    public String getLogSeq() {
        return logSeq;
    }

    public void setLogSeq(String logSeq) {
        this.logSeq = logSeq;
    }

    public String getWhPhone() {
        return whPhone;
    }

    public void setWhPhone(String whPhone) {
        this.whPhone = whPhone;
    }
}