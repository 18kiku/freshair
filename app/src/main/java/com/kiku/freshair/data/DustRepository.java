package com.kiku.freshair.data;

import com.kiku.freshair.dustLink.GetDustLink;
import com.kiku.freshair.dustMaterial.GetDust;

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
