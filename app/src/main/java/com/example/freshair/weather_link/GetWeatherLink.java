package com.example.freshair.weather_link;

import com.example.freshair.dust_link.GetDustApi;
import com.example.freshair.tm_link.GetTMApi;
import com.example.freshair.weather_material.GetWeather;

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
