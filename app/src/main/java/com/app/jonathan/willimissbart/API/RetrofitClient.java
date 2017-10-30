package com.app.jonathan.willimissbart.API;

import com.app.jonathan.willimissbart.API.Callbacks.BsaCallback;
import com.app.jonathan.willimissbart.API.Callbacks.DeparturesCallback;
import com.app.jonathan.willimissbart.API.Callbacks.StationInfoCallback;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
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
                .baseUrl(APIConstants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        matchingService = retrofit.create(MatchingService.class);
    }

    public MatchingService getMatchingService() {
        return matchingService;
    }

    // TODO: return callback from each API call

    public static void getBsas() {
        RetrofitClient.getInstance()
            .getMatchingService()
            .getBsa("bsa", APIConstants.API_KEY, 'y')
            .enqueue(new BsaCallback());
    }

    public static void getStationInfo(String abbr) {
        RetrofitClient.getInstance()
            .getMatchingService()
            .getStationInfo("stninfo", APIConstants.API_KEY, abbr, 'y')
            .enqueue(new StationInfoCallback());
    }

    public static void getCurrentDepartures(String orig,
                                            String dest,
                                            boolean isReturnRoute) {
        RetrofitClient.getInstance()
            .getMatchingService()
            .getDepartures("depart", orig, dest, "now", APIConstants.API_KEY, 'y')
            .enqueue(new DeparturesCallback(isReturnRoute));
    }
}
