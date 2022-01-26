package com.example.freshair.data;

import com.example.freshair.station_material.GetStation;

import retrofit2.Callback;

public interface GetStationRepository {
    boolean isAvailable();
    void getStationData(Callback<GetStation> callback);
}
