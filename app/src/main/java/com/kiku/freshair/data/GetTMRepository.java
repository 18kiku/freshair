package com.kiku.freshair.data;

import com.kiku.freshair.tmMaterial.GetTM;

import retrofit2.Callback;

public interface GetTMRepository {
    boolean isAvailable();
    void getTMData(Callback<GetTM> callback);
}
