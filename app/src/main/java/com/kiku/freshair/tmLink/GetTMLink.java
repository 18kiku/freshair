package com.kiku.freshair.tmLink;

import com.kiku.freshair.dustLink.GetDustApi;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GetTMLink {
    private GetTMApi mGetApi;

    public GetTMLink(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GetDustApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mGetApi = retrofit.create(GetTMApi.class);
    }
    public GetTMApi getApi(){
        return mGetApi;
    }
}
