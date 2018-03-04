package com.app.jonathan.willimissbart.API;

import com.app.jonathan.willimissbart.API.Models.BSA.BsaResp;
import com.app.jonathan.willimissbart.API.Models.Etd.EtdResp;
import com.app.jonathan.willimissbart.API.Models.Routes.DeparturesResp;
import com.app.jonathan.willimissbart.API.Models.Station.StationsResp;
import com.app.jonathan.willimissbart.API.Models.StationInfo.StationInfoResp;

import io.reactivex.Single;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MatchingService {
    @GET("stn.aspx")
    Single<Response<StationsResp>> getStations(@Query("cmd") String cmd,
                                               @Query("key") String key,
                                               @Query("json") Character json);

    @GET("etd.aspx")
    Single<Response<EtdResp>> getEtd(@Query("cmd") String cmd,
                                     @Query("key") String key,
                                     @Query("json") Character json,
                                     @Query("orig") String orig);

    @GET("bsa.aspx")
    Single<Response<BsaResp>> getBsa(@Query("cmd") String cmd,
                                     @Query("key") String key,
                                     @Query("json") Character json);

    @GET("stn.aspx")
    Single<Response<StationInfoResp>> getStationInfo(@Query("cmd") String cmd,
                                                     @Query("key") String key,
                                                     @Query("orig") String orig,
                                                     @Query("json") Character json);

    @GET("sched.aspx")
    Single<Response<DeparturesResp>> getDepartures(@Query("cmd") String cmd,
                                                   @Query("orig") String orig,
                                                   @Query("dest") String dest,
                                                   @Query("date") String date,
                                                   @Query("b") int before,
                                                   @Query("a") int after,
                                                   @Query("key") String key,
                                                   @Query("json") Character json);
}
