package com.kiku.freshair.data;

import com.kiku.freshair.stationLink.GetStationLink;
import com.kiku.freshair.stationMaterial.GetStation;

import retrofit2.Callback;

public class StationRepository implements GetStationRepository {
    private final GetStationLink getStationLink;
    private String tmX, tmY;

    public StationRepository() { getStationLink = new GetStationLink(); }
    public StationRepository(String tmX, String tmY){
        this();
        this.tmX = tmX;
        this.tmY = tmY;
    }

    @Override
    public boolean isAvailable() {
        return tmX != null && tmY != null;
    }

    @Override
    public void getStationData(Callback<GetStation> callback) {
        getStationLink.getApi().getStationCall(tmX, tmY).enqueue(callback);
    }
}
