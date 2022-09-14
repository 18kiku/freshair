package com.kiku.freshair.GetDust;

public class Weather {
    private String fcstTime;
    private String fcstValue;
    private String skyImage;
    private String pop;
    private String reh;

    public Weather(String fcstTime, String fcstValue, String skyImage, String reh){
        this.fcstTime = fcstTime;
        this.fcstValue = fcstValue;
        this.skyImage = skyImage;
        this.reh = reh;
    }

    public String getFcstValue() {
        return fcstValue;
    }

    public void setFcstValue(String fcstValue) {
        this.fcstValue = fcstValue;
    }

    public String getFcstTime() {
        return fcstTime;
    }

    public void setFcstTime(String fcstTime) {
        this.fcstTime = fcstTime;
    }

    public String getSkyImage() {
        return skyImage;
    }

    public void setSkyImage(String skyImage) {
        this.skyImage = skyImage;
    }

    public String getPop() {
        return pop;
    }

    public void setPop(String pop) {
        this.pop = pop;
    }

    public String getReh() {
        return reh;
    }

    public void setReh(String reh) {
        this.reh = reh;
    }
}
