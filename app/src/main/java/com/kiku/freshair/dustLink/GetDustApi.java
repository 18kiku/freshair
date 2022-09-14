package com.kiku.freshair.dustLink;

import com.kiku.freshair.dustMaterial.GetDust;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GetDustApi {
    String BASE_URL = "http://apis.data.go.kr/";
    String serviceKey = "5RgBcNGSw2M0ezM0Q4WQVCnvtza%2FYoxTViaTrnagFqAfQgOPLV6C0AS5mxWMUwMJWU%2BKdV9OevGejyHzqUS8Hw%3D%3D";
    String returnType = "json";
    String numOfRows = "100";
    String dataTerm = "DAILY";
    String ver = "1.3";
    @GET("B552584/ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty"+
            "?serviceKey=" + serviceKey+
            "&returnType=" + returnType+
            "&numOfRows="+ numOfRows+
            "&dataTerm=" + dataTerm+
            "&ver=" + ver)
    Call<GetDust> getDustCall(@Query("stationName") String stationName);
}
