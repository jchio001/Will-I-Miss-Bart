package com.app.jonathan.willimissbart.persistence;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.app.jonathan.willimissbart.api.Models.Station.Station;
import com.app.jonathan.willimissbart.misc.Constants;
import com.app.jonathan.willimissbart.misc.Utils;
import com.app.jonathan.willimissbart.persistence.models.UserStationData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public class SPManager {

    private SharedPreferences sp;
    private static Gson gson = new Gson();

    public SPManager(Context context) {
        sp = PreferenceManager.getDefaultSharedPreferences(context);
    }

    private SharedPreferences getSp() {
        return sp;
    }

    public boolean containsUserData() {
        return sp.contains(Constants.USER_DATA);
    }

    public ArrayList<UserStationData> fetchUserData() {
        String userData = sp.getString(Constants.USER_DATA, "");
        if (userData.isEmpty()) {
            return null;
        } else {
            return gson.fromJson(userData,
                new TypeToken<ArrayList<UserStationData>>(){}.getType());
        }
    }

    public Single<String> fetchStationsJson(Context context) {
        return Single.just(sp.getString(Constants.STATION_LIST_KEY, ""))
            .subscribeOn(Schedulers.io());
    }

    // Only use this method if you're 100% sure that the stations JSON is persisted in
    // SharedPreferences
    public Single<List<Station>> fetchStations(Context context) {
        return Single.just(Utils.stationsJsonToList(
            sp.getString(Constants.STATION_LIST_KEY, "")))
            .subscribeOn(Schedulers.io());
    }

    public String fetchString(String key) {
        return getSp().getString(key, "");
    }

    public void persistString(String key, String value) {
        sp.edit().putString(key, value).apply();
    }

    public void persistUserData(List<UserStationData> userData) {
        sp.edit()
            .putString(Constants.USER_DATA, gson.toJson(userData))
            .apply();
    }

    public void persistStations(String stationsJsonArr) {
        sp.edit().putString(Constants.STATION_LIST_KEY, stationsJsonArr).apply();
    }

    public void persistIncludeReturnRoute(boolean includeReturnRoute) {
        sp.edit()
            .putBoolean(Constants.INCLUDE_RETURN, includeReturnRoute).apply();
    }

    public boolean fetchIncludeReturnRoute() {
        return sp.getBoolean(Constants.INCLUDE_RETURN, false);
    }

    public int incrementUsageCounter() {
        int counter = sp.getInt(Constants.USAGE_COUNTER, 0) + 1;
        sp.edit().putInt(Constants.USAGE_COUNTER, counter).apply();
        return counter;
    }
}
