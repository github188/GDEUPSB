package com.bocom.bbip.gdeupsb.entity;

import com.bocom.bbip.data.annotation.GeneratedValue;
import com.bocom.bbip.data.annotation.Id;
import java.math.BigDecimal;

public class GDEupsbStuFeeInfo {
    @Id
    @GeneratedValue
    private String stuCod;

    private String stuNam;

    private String schCod;

    private String schNam;

    private String payYea;

    private String payTem;

    private BigDecimal xzfAmt;

    private BigDecimal romAmt;

    private BigDecimal lrnAmt;

    private String othAmt;

    private BigDecimal txnAmt;

    private String status;

    private String flag;

    public String getStuCod() {
        return stuCod;
    }

    public void setStuCod(String stuCod) {
        this.stuCod = stuCod;
    }

    public String getStuNam() {
        return stuNam;
    }

    public void setStuNam(String stuNam) {
        this.stuNam = stuNam;
    }

    public String getSchCod() {
        return schCod;
    }

    public void setSchCod(String schCod) {
        this.schCod = schCod;
    }

    public String getSchNam() {
        return schNam;
    }

    public void setSchNam(String schNam) {
        this.schNam = schNam;
    }

    public String getPayYea() {
        return payYea;
    }

    public void setPayYea(String payYea) {
        this.payYea = payYea;
    }

    public String getPayTem() {
        return payTem;
    }

    public void setPayTem(String payTem) {
        this.payTem = payTem;
    }

    public BigDecimal getXzfAmt() {
        return xzfAmt;
    }

    public void setXzfAmt(BigDecimal xzfAmt) {
        this.xzfAmt = xzfAmt;
    }

    public BigDecimal getRomAmt() {
        return romAmt;
    }

    public void setRomAmt(BigDecimal romAmt) {
        this.romAmt = romAmt;
    }

    public BigDecimal getLrnAmt() {
        return lrnAmt;
    }

    public void setLrnAmt(BigDecimal lrnAmt) {
        this.lrnAmt = lrnAmt;
    }

    public String getOthAmt() {
        return othAmt;
    }

    public void setOthAmt(String othAmt) {
        this.othAmt = othAmt;
    }

    public BigDecimal getTxnAmt() {
        return txnAmt;
    }

    public void setTxnAmt(BigDecimal txnAmt) {
        this.txnAmt = txnAmt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }
}