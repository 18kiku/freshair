package com.kiku.freshair.GetDust;

import com.kiku.freshair.weatherMaterial.GetWeather;

public class GetWeatherContract {
    interface View{
        void showGetWeatherResult(GetWeather getWeather);
        void showLoadError(String message);
    }
    interface UserActionsListener{
        void loadGetWeatherData();
    }
}
