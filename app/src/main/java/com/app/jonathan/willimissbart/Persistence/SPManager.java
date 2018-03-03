package com.app.jonathan.willimissbart.Persistence;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.app.jonathan.willimissbart.Misc.Constants;
import com.app.jonathan.willimissbart.Persistence.Models.UserStationData;
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

    public static ArrayList<UserStationData> getUserData(Context context) {
        String userData = getInstance(context).getSp()
            .getString(Constants.USER_DATA, "");
        if (userData.isEmpty()) {
            return null;
        } else {
            return gson.fromJson(userData,
                new TypeToken<ArrayList<UserStationData>>(){}.getType());
        }
    }

    public static String getPersistedStations(Context context) {
        return getInstance(context).getSp()
            .getString(Constants.STATION_LIST_KEY, "");
    }

    public static Single<String> ayncGetPersistedStations(Context context) {
        return Single.just(getInstance(context).getSp()
            .getString(Constants.STATION_LIST_KEY, ""))
            .subscribeOn(Schedulers.io());
    }

    public static String getString(Context context, String key) {
        return getInstance(context).getSp().getString(key, "");
    }

    public static void putString(Context context, String key, String value) {
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

    public static boolean getIncludeReturnRoute(Context context) {
        return getInstance(context).getSp().getBoolean(Constants.INCLUDE_RETURN, false);
    }
}
