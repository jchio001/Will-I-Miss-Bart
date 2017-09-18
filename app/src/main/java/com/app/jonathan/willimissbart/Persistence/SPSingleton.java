package com.app.jonathan.willimissbart.Persistence;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.app.jonathan.willimissbart.API.Models.StationModels.Station;
import com.app.jonathan.willimissbart.Misc.Constants;
import com.app.jonathan.willimissbart.Persistence.Models.UserStationData;
import com.google.gson.Gson;

import java.util.List;

public class SPSingleton {
    private static SPSingleton instance = null;
    private SharedPreferences sp;

    private SPSingleton(Context context) {
        sp = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static SPSingleton getInstance(Context context) {
        if (instance == null) {
            instance = new SPSingleton(context);
        }

        return instance;
    }

    public SharedPreferences getSp() {
        return sp;
    }

    public static UserStationData[] getUserData(Context context) {
        String userData = getInstance(context).getSp()
            .getString(Constants.USER_DATA, "");
        return !userData.isEmpty() ? new Gson().fromJson(userData, UserStationData[].class) : null;
    }

    public String getPersistedStations() {
        return sp.getString(Constants.STATION_LIST_KEY, "");
    }

    public static String getString(Context context, String key) {
        return SPSingleton.getInstance(context).getSp().getString(key, null);
    }

    public void persistUserData(UserStationData[] userData) {
        sp.edit()
            .putString(Constants.USER_DATA, new Gson().toJson(userData))
            .apply();
    }

    public void persistStations(String stationsJsonArr) {
        sp.edit()
            .putString(Constants.STATION_LIST_KEY, stationsJsonArr).apply();
    }
}
