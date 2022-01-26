package com.example.freshair.GetDust;

import android.view.View;

import com.example.freshair.data.GetDustRepository;
import com.example.freshair.dust_material.GetDust;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetDustPresenter implements GetDustContract.UserActionsListener{
    private final GetDustRepository repository;
    private final GetDustContract.View view;

    public GetDustPresenter(GetDustRepository repository, GetDustContract.View view) {
        this.repository = repository;
        this.view = view;
    }

    @Override
    public void loadGetDustData() {
        if(repository.isAvailable()){

            repository.getDustData(new Callback<GetDust>() {
                @Override
                public void onResponse(Call<GetDust> call, Response<GetDust> response) {
                    view.showGetDustResult(response.body());
                }

                @Override
                public void onFailure(Call<GetDust> call, Throwable t) {
                    view.showLoadError(t.getLocalizedMessage());
                }
            });
        }
    }

}
