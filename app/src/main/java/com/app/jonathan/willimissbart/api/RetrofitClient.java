package com.app.jonathan.willimissbart.api;

import android.support.annotation.IntDef;
import android.util.Log;

import com.app.jonathan.willimissbart.api.Models.BSA.BsaResp;
import com.app.jonathan.willimissbart.api.Models.Etd.EtdRespWrapper;
import com.app.jonathan.willimissbart.api.Models.Routes.Trip;
import com.app.jonathan.willimissbart.api.Models.Station.Station;
import com.app.jonathan.willimissbart.api.Models.StationInfo.StationInfoResp;
import com.app.jonathan.willimissbart.misc.NotGuava;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.lang.annotation.Retention;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static java.lang.annotation.RetentionPolicy.SOURCE;

public class RetrofitClient {

    @Retention(SOURCE)
    @IntDef(StatusCode.HTTP_STATUS_OK)
    public @interface StatusCode {
        int HTTP_STATUS_OK = 200;
    }

    private static final String LOG_TAG = "RetrofitClient";

    private static final String BASE_URL = "http://api.bart.gov/api/";
    private static final String API_KEY = "QSZS-5BU6-9U6T-DWE9";

    private static final String FAILED_DEPARTURES_REQ_TEMPLATE =
        "Departures request failed for from %s to %s";
    private static final String FAILED_STATIONS_REQ = "Failed to fetch stations";
    private static final String FAIL_ETDS_REQ_TEMPLATE = "Failed to fetch etds for %s";

    private static RetrofitClient instance;
    private MatchingService matchingService;

    public static RetrofitClient getInstance() {
        if (instance == null) {
            instance = new RetrofitClient();
        }
        return instance;
    }

    private RetrofitClient() {
        Gson gson = new GsonBuilder().serializeNulls().create();

        Dispatcher dispatcher = new Dispatcher();
        dispatcher.setMaxRequests(6);

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .dispatcher(dispatcher)
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        matchingService = retrofit.create(MatchingService.class);
    }

    public MatchingService getMatchingService() {
        return matchingService;
    }

    public static Single<EtdRespWrapper> getRealTimeEstimates(String origin,
                                                              Set<String> trainHeadSet) {
        EtdRespWrapper failedEtdResp = new EtdRespWrapper(origin, null, trainHeadSet);
        return RetrofitClient.getInstance()
            .getMatchingService()
            .getEtd("etd", API_KEY, 'y', origin)
            .doOnError(e  -> Log.w(LOG_TAG, String.format(FAIL_ETDS_REQ_TEMPLATE, origin)))
            .flatMap(etdResp -> {
                if (etdResp.code() == StatusCode.HTTP_STATUS_OK) {
                    return Single.just(new EtdRespWrapper(origin,
                        etdResp.body().getRoot(), trainHeadSet));
                } else {
                    return Single.just(failedEtdResp);
                }
            })
            .onErrorReturnItem(failedEtdResp)
            .subscribeOn(Schedulers.io());
    }

    public static Single<Response<BsaResp>> getBsas() {
        return RetrofitClient.getInstance()
            .getMatchingService()
            .getBsa("bsa", API_KEY, 'y')
            .subscribeOn(Schedulers.io());
    }

    public static Single<List<Station>> getStations() {
        return RetrofitClient.getInstance()
            .getMatchingService()
            .getStations("stns", API_KEY, 'y')
            .flatMap(stationsResp -> Single.just(stationsResp.body()
                .getStationsRoot().getStations().getStationList()))
            .doOnError(e -> Log.w(LOG_TAG, FAILED_STATIONS_REQ))
            .subscribeOn(Schedulers.io());
    }

    public static Single<Response<StationInfoResp>> getStationInfo(String abbr) {
        return RetrofitClient.getInstance()
            .getMatchingService()
            .getStationInfo("stninfo", API_KEY, abbr, 'y')
            .subscribeOn(Schedulers.io());
    }

    /**
     * There are 2 cases I'm representing:
     * - Body exists => return the trips from the Response<DeparturesResp>
     * - Body is null => return a list containg a single null
     * As of now, I'll treat pure failure (no network) and failed requests the same
     * In the future, I can tell the difference by checking the status code (200 & null body) =
     * pure failure
     */
    public static Single<List<Trip>> getTrips(String orig,
                                              String dest) {
        return RetrofitClient.getInstance()
            .getMatchingService()
            .getDepartures("depart", orig, dest,
                "now", 0, 2, API_KEY, 'y')
            .doOnError(e -> Log.w(LOG_TAG,
                String.format(FAILED_DEPARTURES_REQ_TEMPLATE, orig, dest)))
            .flatMap(departuresResp -> {
                if (departuresResp.body() != null) {
                    return Single.just(departuresResp.body()
                        .getRoot().getSchedule().getRequest().getTrips());
                } else {
                    return Single.just(NotGuava.newArrayList((Trip) null));
                }
            })
            .onErrorReturnItem(NotGuava.newArrayList((Trip) null))
            .subscribeOn(Schedulers.io());
    }
}
