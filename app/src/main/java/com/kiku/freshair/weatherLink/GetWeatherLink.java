package com.kiku.freshair.weatherLink;

import com.kiku.freshair.dustLink.GetDustApi;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GetWeatherLink {
    private GetWeatherApi mGetApi;

    public GetWeatherLink(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GetDustApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mGetApi = retrofit.create(GetWeatherApi.class);
    }
    public GetWeatherApi getApi(){
        return mGetApi;
    }
}
