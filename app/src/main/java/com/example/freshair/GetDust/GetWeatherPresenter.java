package com.example.freshair.GetDust;

import com.example.freshair.data.GetDustRepository;
import com.example.freshair.data.GetWeatherRepository;
import com.example.freshair.dust_material.GetDust;
import com.example.freshair.weather_material.GetWeather;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetWeatherPresenter implements GetWeatherContract.UserActionsListener{
    private final GetWeatherRepository repository;
    private final GetWeatherContract.View view;

    public GetWeatherPresenter(GetWeatherRepository repository, GetWeatherContract.View view) {
        this.repository = repository;
        this.view = view;
    }

    @Override
    public void loadGetWeatherData() {
        if(repository.isAvailable()){

            repository.getWeatherData(new Callback<GetWeather>() {
                @Override
                public void onResponse(Call<GetWeather> call, Response<GetWeather> response) {
                    view.showGetWeatherResult(response.body());
                }

                @Override
                public void onFailure(Call<GetWeather> call, Throwable t) {
                    view.showLoadError(t.getLocalizedMessage());
                }
            });
        }
    }
}
