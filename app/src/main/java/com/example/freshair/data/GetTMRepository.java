package com.example.freshair.data;

import com.example.freshair.tm_material.GetTM;

import retrofit2.Callback;

public interface GetTMRepository {
    boolean isAvailable();
    void getTMData(Callback<GetTM> callback);
}
