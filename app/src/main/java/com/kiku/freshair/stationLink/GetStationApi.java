package com.kiku.freshair.stationLink;

import com.kiku.freshair.stationMaterial.GetStation;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GetStationApi {
    String BASE_URL = "http://apis.data.go.kr/";

    @GET("B552584/MsrstnInfoInqireSvc/getNearbyMsrstnList?serviceKey=5RgBcNGSw2M0ezM0Q4WQVCnvtza%2FYoxTViaTrnagFqAfQgOPLV6C0AS5mxWMUwMJWU%2BKdV9OevGejyHzqUS8Hw%3D%3D&returnType=json")
    Call<GetStation> getStationCall(@Query("tmX") String tmX,
                                    @Query("tmY") String tmY);
}
