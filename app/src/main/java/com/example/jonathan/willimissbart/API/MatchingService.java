package com.example.jonathan.willimissbart.API;

import com.example.jonathan.willimissbart.API.Models.StationModels.StationsResp;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MatchingService {
    @GET("stn.aspx")
    Call<StationsResp> getStations(@Query("cmd") String cmd,
                                   @Query("key") String key,
                                   @Query("json") String isJson);
}
