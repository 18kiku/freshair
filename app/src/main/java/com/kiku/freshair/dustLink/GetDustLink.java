package com.kiku.freshair.dustLink;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GetDustLink {
    private GetDustApi mGetApi;

    public GetDustLink(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GetDustApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mGetApi = retrofit.create(GetDustApi.class);
    }
    public GetDustApi getApi(){
        return mGetApi;
    }
}
