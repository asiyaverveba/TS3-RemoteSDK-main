package com.enhancell.remotesample;

import androidx.annotation.NonNull;

public class LteModel {

    private double ltersrp,ltersrq;
    private double ltersrp0;
    private double ltersrp1;
    private double ltersrq0;
    private double ltersrq1;
    private double ltesinr0,ltesinr1;
    private double nrssrsrp, nrssrsrq, nrsssinr;

    public double getNrSSrsrp() {
        return nrssrsrp;
    }

    public void setNrSSrsrp(double rsrp) {
        this.nrssrsrp = rsrp;
    }

    public double getNrSSrsrq() {
        return nrssrsrq;
    }

    public void setNrSSrsrq(double rsrq) {
        this.nrssrsrq = rsrq;
    }

    public double getNrSSsinr() {
        return nrsssinr;
    }

    public void setNrSSsinr(double sinr) {
        this.nrsssinr = sinr;
    }

    private long ltess;
    private long lterssnr;
    private long ltecqi;
    private long lteasu;
    private long ltedbm;
    private long ltelevel;
    private long ltetac;
    private long lteci;
    private long ltepci;
    private long nrPci;
    private long nrChannel;
    private String nrBand;
    private long lteearfcn;
    private long prvearfcn;
    private long ltetiming;
    private long ltebandwidth; //in MHz
    private boolean cellid_matched;
    private boolean pci_matched;
    private double lterssi;

    public double getLterssi0() {
        return lterssi0;
    }

    public void setLterssi0(double lterssi0) {
        this.lterssi0 = lterssi0;
    }

    public double getLterssi1() {
        return lterssi1;
    }

    public void setLterssi1(double lterssi1) {
        this.lterssi1 = lterssi1;
    }

    private double lterssi0;
    private double lterssi1;
    private long prvpci;
    private long prvLteci;

    public double getLtersrp0() {
        return ltersrp0;
    }

    public void setLtersrp0(double ltersrp0) {
        this.ltersrp0 = ltersrp0;
    }

    public double getLtersrq0() {
        return ltersrq0;
    }

    public void setLtersrq0(double ltersrq0) {
        this.ltersrq0 = ltersrq0;
    }



    public double getLtersrp1() {
        return ltersrp1;
    }

    public void setLtersrp1(double ltersrp1) {
        this.ltersrp1 = ltersrp1;
    }

    public double getLtersrq1() {
        return ltersrq1;
    }

    public void setLtersrq1(double ltersrq1) {
        this.ltersrq1 = ltersrq1;
    }



    public long getDlfpci() {
        return dlfpci;
    }

    public void setDlfpci(long dlfpci) {
        this.dlfpci = dlfpci;
    }

    private long dlfpci;

    public long getPrvLteci() {
        return prvLteci;
    }

    public void setPrvLteci(long prvLteci) {
        this.prvLteci = prvLteci;
    }


    private long ecio;
    private boolean isCellPresnt;
    private String lteband;


    public String getLteband() {
        return lteband;
    }

    public void setLteband(String ltebandx) {
        this.lteband = ltebandx;
    }

    public boolean isCellPresnt() {
        return isCellPresnt;
    }

    public void setCellPresnt(boolean cellPresnt) {
        isCellPresnt = cellPresnt;
    }

    public double getLtersrp() {
        return ltersrp;
    }

    public void setLtersrp(double ltersrp) {
        this.ltersrp = ltersrp;
    }

    public double getLtersrq() {
        return ltersrq;
    }

    public void setLtersrq(double ltersrq) {
        this.ltersrq = ltersrq;
    }

    public long getLtess() {
        return ltess;
    }

    public void setLtess(long ltess) {
        this.ltess = ltess;
    }

    public long getLterssnr() {
        return lterssnr;
    }

    public void setLterssnr(long lterssnr) {
        this.lterssnr = lterssnr;
        ltesinr0 = lterssnr / 10;
    }
    public double getLtesinr0() {
        return ltesinr0;
    }
    public void setLtesinr0(double ltesinr) {
        this.ltesinr0 = ltesinr;
    }

    public double getLtesinr1() {
        return ltesinr1;
    }

    public void setLtesinr1(double ltesinr1) {
        this.ltesinr1 = ltesinr1;
    }

    public long getLtecqi() {
        return ltecqi;
    }

    public void setLtecqi(long ltecqi) {
        this.ltecqi = ltecqi;
    }

    public long getLteasu() {
        return lteasu;
    }

    public void setLteasu(long lteasu) {
        this.lteasu = lteasu;
    }

    public long getLtedbm() {
        return ltedbm;
    }

    public void setLtedbm(long ltedbm) {
        this.ltedbm = ltedbm;
    }

    public long getLtelevel() {
        return ltelevel;
    }

    public void setLtelevel(long ltelevel) {
        this.ltelevel = ltelevel;
    }

    public long getLtetac() {
        return ltetac;
    }

    public void setLtetac(long ltetac) {
        this.ltetac = ltetac;
    }

    public long getLteci() {
        return lteci;
    }

    public void setLteci(long lteci) {
        this.lteci = lteci;
    }

    public long getLtepci() {
        return ltepci;
    }

    public void setLtepci(long ltepci) {
        this.ltepci = ltepci;
    }

    public long getNrPci() {
        return nrPci;
    }

    public void setNrPci(long pci) {
        nrPci = pci;
    }

    public long getNrChannel() {
        return nrChannel;
    }

    public void setNrChannel(long channel) {
        nrChannel = channel;
    }

    public String getNrBand() {
        return nrBand;
    }

    public void setNrBand(@NonNull String band) {
        nrBand = band;
    }

    public long getPrvpci() {
        return prvpci;
    }

    public void setPrvpci(long prvpci) {
        this.prvpci = prvpci;
    }

    public long getLteearfcn() {
        return lteearfcn;
    }

    public void setLteearfcn(long lteearfcn) {
        this.lteearfcn = lteearfcn;
        //Timber.d("earfcn set:" + lteearfcn);
    }

    public long getLtetiming() {
        return ltetiming;
    }

    public void setLtetiming(long ltetiming) {
        this.ltetiming = ltetiming;
    }

    public long getLtebandwidth() {
        return ltebandwidth;
    }

    public void setLtebandwidth(long ltebandwidth) {
        this.ltebandwidth = ltebandwidth;
    }

    public double getLterssi() {
        return lterssi;
    }

    public void setLterssi(double lterssi) {
        this.lterssi = lterssi;
    }

    public boolean isCellid_matched() {
        return cellid_matched;
    }

    public void setCellid_matched(boolean cellid_matched) {
        this.cellid_matched = cellid_matched;
    }

    public boolean isPci_matched() {
        return pci_matched;
    }

    public void setPci_matched(boolean pci_matched) {
        this.pci_matched = pci_matched;
    }

    public long getEcio() {
        return ecio;
    }

    public void setEcio(long ecio) {
        this.ecio = ecio;
    }

    public long getPrvearfcn() {
        return prvearfcn;
    }

    public void setPrvearfcn(long prvearfcn) {
        this.prvearfcn = prvearfcn;
    }

}
