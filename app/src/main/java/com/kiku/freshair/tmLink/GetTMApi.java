package com.kiku.freshair.tmLink;

import com.kiku.freshair.tmMaterial.GetTM;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GetTMApi {
    String BASE_URL = "http://apis.data.go.kr/";

    @GET("B552584/MsrstnInfoInqireSvc/getTMStdrCrdnt?serviceKey=5RgBcNGSw2M0ezM0Q4WQVCnvtza%2FYoxTViaTrnagFqAfQgOPLV6C0AS5mxWMUwMJWU%2BKdV9OevGejyHzqUS8Hw%3D%3D&returnType=json&numOfRows=100&pageNo=1")
    Call<GetTM> getTMCall(@Query("umdName") String umdName);
}
