package com.kiku.freshair.data;

import com.kiku.freshair.stationMaterial.GetStation;

import retrofit2.Callback;

public interface GetStationRepository {
    boolean isAvailable();
    void getStationData(Callback<GetStation> callback);
}
