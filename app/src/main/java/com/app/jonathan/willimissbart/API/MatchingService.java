package com.app.jonathan.willimissbart.API;

import com.app.jonathan.willimissbart.API.Models.BSA.BsaResp;
import com.app.jonathan.willimissbart.API.Models.Etd.EtdResp;
import com.app.jonathan.willimissbart.API.Models.Routes.DeparturesResp;
import com.app.jonathan.willimissbart.API.Models.Station.StationsResp;
import com.app.jonathan.willimissbart.API.Models.StationInfo.StationInfoResp;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MatchingService {
    @GET("stn.aspx")
    Call<StationsResp> getStations(@Query("cmd") String cmd,
                                   @Query("key") String key,
                                   @Query("json") Character json);

    @GET("etd.aspx")
    Call<EtdResp> getEtd(@Query("cmd") String cmd,
                         @Query("key") String key,
                         @Query("json") Character json,
                         @Query("orig") String orig,
                         @Query("dir") Character dir);

    @GET("bsa.aspx")
    Call<BsaResp> getBsa(@Query("cmd") String cmd,
                         @Query("key") String key,
                         @Query("json") Character json);

    @GET("stn.aspx")
    Call<StationInfoResp> getStationInfo(@Query("cmd") String cmd,
                                         @Query("key") String key,
                                         @Query("orig") String orig,
                                         @Query("json") Character json);

    @GET("sched.aspx")
    Call<DeparturesResp> getDepartures(@Query("cmd") String cmd,
                                       @Query("orig") String orig,
                                       @Query("dest") String dest,
                                       @Query("date") String date,
                                       @Query("key") String key,
                                       @Query("json") Character json);
}
