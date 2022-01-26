package com.example.freshair.GetDust;

import com.example.freshair.weather_material.GetWeather;

public class GetWeatherContract {
    interface View{
        void showGetWeatherResult(GetWeather getWeather);
        void showLoadError(String message);
    }
    interface UserActionsListener{
        void loadGetWeatherData();
    }
}
