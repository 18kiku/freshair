package com.example.freshair.data;

import com.example.freshair.weather_material.GetWeather;

import retrofit2.Callback;

public interface GetWeatherRepository {
    boolean isAvailable();
    void getWeatherData(Callback<GetWeather> callback);
}
