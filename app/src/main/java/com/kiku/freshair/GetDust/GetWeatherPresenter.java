package com.kiku.freshair.GetDust;

import com.kiku.freshair.data.GetWeatherRepository;
import com.kiku.freshair.weatherMaterial.GetWeather;

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
