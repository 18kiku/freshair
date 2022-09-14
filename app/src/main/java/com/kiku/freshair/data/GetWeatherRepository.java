package com.kiku.freshair.data;

import com.kiku.freshair.weatherMaterial.GetWeather;

import retrofit2.Callback;

public interface GetWeatherRepository {
    boolean isAvailable();
    void getWeatherData(Callback<GetWeather> callback);
}
