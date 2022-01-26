package com.example.freshair.data;

import com.example.freshair.dust_material.GetDust;

import retrofit2.Callback;

public interface GetDustRepository {
    boolean isAvailable();
    void getDustData(Callback<GetDust> callback);
}
