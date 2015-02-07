package com.bocom.bbip.gdeupsb.entity;

import com.bocom.bbip.data.annotation.GeneratedValue;
import com.bocom.bbip.data.annotation.Id;

public class GDEupsbLotDrwInf {
    @Id
    @GeneratedValue
    private String drawid;

    private String gameid;

    private String drawnm;

    private String salstr;

    private String salend;

    private String cshstr;

    private String cshend;

    private String kenoid;

    private String kenonm;

    private String ksalst;

    private String ksaled;

    private String chkflg;

    private String chktim;

    private String dowprz;

    private String przamt;

    private String totamt;

    private String difflg;

    private String difamt;

    private String payflg;

    private String payamt;

    private String flwctl;

    public String getDrawid() {
        return drawid;
    }

    public void setDrawid(String drawid) {
        this.drawid = drawid;
    }

    public String getGameid() {
        return gameid;
    }

    public void setGameid(String gameid) {
        this.gameid = gameid;
    }

    public String getDrawnm() {
        return drawnm;
    }

    public void setDrawnm(String drawnm) {
        this.drawnm = drawnm;
    }

    public String getSalstr() {
        return salstr;
    }

    public void setSalstr(String salstr) {
        this.salstr = salstr;
    }

    public String getSalend() {
        return salend;
    }

    public void setSalend(String salend) {
        this.salend = salend;
    }

    public String getCshstr() {
        return cshstr;
    }

    public void setCshstr(String cshstr) {
        this.cshstr = cshstr;
    }

    public String getCshend() {
        return cshend;
    }

    public void setCshend(String cshend) {
        this.cshend = cshend;
    }

    public String getKenoid() {
        return kenoid;
    }

    public void setKenoid(String kenoid) {
        this.kenoid = kenoid;
    }

    public String getKenonm() {
        return kenonm;
    }

    public void setKenonm(String kenonm) {
        this.kenonm = kenonm;
    }

    public String getKsalst() {
        return ksalst;
    }

    public void setKsalst(String ksalst) {
        this.ksalst = ksalst;
    }

    public String getKsaled() {
        return ksaled;
    }

    public void setKsaled(String ksaled) {
        this.ksaled = ksaled;
    }

    public String getChkflg() {
        return chkflg;
    }

    public void setChkflg(String chkflg) {
        this.chkflg = chkflg;
    }

    public String getChktim() {
        return chktim;
    }

    public void setChktim(String chktim) {
        this.chktim = chktim;
    }

    public String getDowprz() {
        return dowprz;
    }

    public void setDowprz(String dowprz) {
        this.dowprz = dowprz;
    }

    public String getPrzamt() {
        return przamt;
    }

    public void setPrzamt(String przamt) {
        this.przamt = przamt;
    }

    public String getTotamt() {
        return totamt;
    }

    public void setTotamt(String totamt) {
        this.totamt = totamt;
    }

    public String getDifflg() {
        return difflg;
    }

    public void setDifflg(String difflg) {
        this.difflg = difflg;
    }

    public String getDifamt() {
        return difamt;
    }

    public void setDifamt(String difamt) {
        this.difamt = difamt;
    }

    public String getPayflg() {
        return payflg;
    }

    public void setPayflg(String payflg) {
        this.payflg = payflg;
    }

    public String getPayamt() {
        return payamt;
    }

    public void setPayamt(String payamt) {
        this.payamt = payamt;
    }

    public String getFlwctl() {
        return flwctl;
    }

    public void setFlwctl(String flwctl) {
        this.flwctl = flwctl;
    }
}