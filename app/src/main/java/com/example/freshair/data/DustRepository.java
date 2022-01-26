package com.example.freshair.data;

import com.example.freshair.dust_link.GetDustLink;
import com.example.freshair.dust_material.GetDust;

import retrofit2.Callback;

public class DustRepository implements GetDustRepository{
    private final GetDustLink getDustLink;
    private String stationName;

    public DustRepository(){
        getDustLink = new GetDustLink();
    }

    public DustRepository(String stationName){
        this();
        this.stationName = stationName;
    }

    @Override
    public boolean isAvailable() {
        return stationName != null;
    }

    @Override
    public void getDustData(Callback<GetDust> callback) {
        getDustLink.getApi().getDustCall(stationName)
                .enqueue(callback);
    }
}
