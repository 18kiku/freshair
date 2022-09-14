package com.kiku.freshair.data;

import com.kiku.freshair.weatherLink.GetWeatherLink;
import com.kiku.freshair.weatherMaterial.GetWeather;

import retrofit2.Callback;

public class WeatherRepository implements GetWeatherRepository{
    private final GetWeatherLink getWeatherLink;
    private String nx, ny, base_date, base_time;

    public WeatherRepository() { getWeatherLink = new GetWeatherLink(); }
    public WeatherRepository(String nx, String ny, String base_date, String base_time){
        this();
        this.nx = nx;
        this.ny = ny;
        this.base_date = base_date;
        this.base_time = base_time;
    }

    @Override
    public boolean isAvailable() {
        return nx != null && ny != null;
    }

    @Override
    public void getWeatherData(Callback<GetWeather> callback) {
        getWeatherLink.getApi().getWeatherCall(nx, ny, base_date, base_time).enqueue(callback);
    }
}
