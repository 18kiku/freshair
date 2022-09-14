package com.kiku.freshair.db;

public class StationNameRealmObject extends io.realm.RealmObject {

    private String stationName;

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }
}

