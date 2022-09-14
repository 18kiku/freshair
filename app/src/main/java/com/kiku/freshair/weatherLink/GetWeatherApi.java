package com.kiku.freshair.weatherLink;

import com.kiku.freshair.weatherMaterial.GetWeather;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GetWeatherApi {
    String BASE_URL = "http://apis.data.go.kr/";

    @GET("1360000/VilageFcstInfoService_2.0/getVilageFcst?serviceKey=5RgBcNGSw2M0ezM0Q4WQVCnvtza%2FYoxTViaTrnagFqAfQgOPLV6C0AS5mxWMUwMJWU%2BKdV9OevGejyHzqUS8Hw%3D%3D&pageNo=1&numOfRows=398&dataType=json")
    Call<GetWeather> getWeatherCall(@Query("nx") String nx,
                                    @Query("ny") String ny,
                                    @Query("base_date") String base_date,
                                    @Query("base_time") String base_time);
}
