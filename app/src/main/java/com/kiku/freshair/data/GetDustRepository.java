package com.kiku.freshair.data;

import com.kiku.freshair.dustMaterial.GetDust;

import retrofit2.Callback;

public interface GetDustRepository {
    boolean isAvailable();
    void getDustData(Callback<GetDust> callback);
}
