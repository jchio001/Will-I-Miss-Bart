package com.app.jonathan.willimissbart.persistence;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.app.jonathan.willimissbart.api.Models.Station.Station;
import com.app.jonathan.willimissbart.misc.Constants;
import com.app.jonathan.willimissbart.misc.Utils;
import com.app.jonathan.willimissbart.persistence.Models.UserStationData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public class SPManager {

    private static SPManager instance = null;
    private SharedPreferences sp;
    private static Gson gson = new Gson();

    private SPManager(Context context) {
        sp = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static SPManager getInstance(Context context) {
        if (instance == null) {
            instance = new SPManager(context);
        }

        return instance;
    }

    private SharedPreferences getSp() {
        return sp;
    }

    public static boolean containsUserData(Context context) {
        return getInstance(context).getSp().contains(Constants.USER_DATA);
    }

    public static ArrayList<UserStationData> fetchUserData(Context context) {
        String userData = getInstance(context).getSp()
            .getString(Constants.USER_DATA, "");
        if (userData.isEmpty()) {
            return null;
        } else {
            return gson.fromJson(userData,
                new TypeToken<ArrayList<UserStationData>>(){}.getType());
        }
    }

    public static Single<String> fetchStationsJson(Context context) {
        return Single.just(getInstance(context).getSp()
            .getString(Constants.STATION_LIST_KEY, ""))
            .subscribeOn(Schedulers.io());
    }

    // Only use this method if you're 100% sure that the stations JSON is persisted in
    // SharedPreferences
    public static Single<List<Station>> fetchStations(Context context) {
        return Single.just(Utils.stationsJsonToList(getInstance(context).getSp()
            .getString(Constants.STATION_LIST_KEY, "")))
            .subscribeOn(Schedulers.io());
    }

    public static String fetchString(Context context, String key) {
        return getInstance(context).getSp().getString(key, "");
    }

    public static void persistString(Context context, String key, String value) {
        getInstance(context).getSp().edit().putString(key, value).apply();
    }

    public static void persistUserData(Context context, List<UserStationData> userData) {
        getInstance(context).getSp().edit()
            .putString(Constants.USER_DATA, gson.toJson(userData))
            .apply();
    }

    public void persistStations(String stationsJsonArr) {
        sp.edit().putString(Constants.STATION_LIST_KEY, stationsJsonArr).apply();
    }

    public static void persistIncludeReturnRoute(Context context, boolean includeReturnRoute) {
        getInstance(context).getSp().edit()
            .putBoolean(Constants.INCLUDE_RETURN, includeReturnRoute).apply();
    }

    public static boolean fetchIncludeReturnRoute(Context context) {
        return getInstance(context).getSp().getBoolean(Constants.INCLUDE_RETURN, false);
    }
}