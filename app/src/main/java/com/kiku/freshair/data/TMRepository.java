package com.kiku.freshair.data;

import com.kiku.freshair.tmLink.GetTMLink;
import com.kiku.freshair.tmMaterial.GetTM;

import retrofit2.Callback;

public class TMRepository implements GetTMRepository{
    private final GetTMLink getTMLink;
    private String umdName;

    public TMRepository() { getTMLink = new GetTMLink(); }
    public TMRepository(String umdName){
        this();
        this.umdName = umdName;
    }

    @Override
    public boolean isAvailable() {
        return umdName != null;
    }

    @Override
    public void getTMData(Callback<GetTM> callback) {
            getTMLink.getApi().getTMCall(umdName).enqueue(callback);
    }
}
