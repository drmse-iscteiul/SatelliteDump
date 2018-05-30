package com.iscte.mobileapps.satellitedump;

public class Satellite {

    String satid;
    String satname;
    String intDesignator;
    String launchDate;
    String satlat;
    String satlng;
    String satalt;

    public Satellite(){

    }

    public String getSatid() {
        return satid;
    }

    public void setSatid(String satid) {
        this.satid = satid;
    }

    public String getSatname() {
        return satname;
    }

    public void setSatname(String satname) {
        this.satname = satname;
    }

    public String getIntDesignator() {
        return intDesignator;
    }

    public void setIntDesignator(String intDesignator) {
        this.intDesignator = intDesignator;
    }

    public String getLaunchDate() {
        return launchDate;
    }

    public void setLaunchDate(String launchDate) {
        this.launchDate = launchDate;
    }

    public String getSatlat() {
        return satlat;
    }

    public void setSatlat(String satlat) {
        this.satlat = satlat;
    }

    public String getSatlng() {
        return satlng;
    }

    public void setSatlng(String satlng) {
        this.satlng = satlng;
    }

    public String getSatalt() {
        return satalt;
    }

    public void setSatalt(String satalt) {
        this.satalt = satalt;
    }
}
