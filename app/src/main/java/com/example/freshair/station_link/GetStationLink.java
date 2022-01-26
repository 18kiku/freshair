package com.example.freshair.station_link;

import com.example.freshair.dust_link.GetDustApi;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GetStationLink {
    private GetStationApi mGetApi;
    Gson gson = new GsonBuilder().setLenient().create();
    public GetStationLink(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GetDustApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        mGetApi = retrofit.create(GetStationApi.class);
    }
    public GetStationApi getApi(){
        return mGetApi;
    }
}
