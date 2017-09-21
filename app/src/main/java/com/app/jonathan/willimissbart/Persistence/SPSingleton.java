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

    public static ArrayList<UserStationData> getUserData(Context context) {
        String userData = getInstance(context).getSp()
            .getString(Constants.USER_DATA, "");
        if (userData.isEmpty()) {
            return null;
        } else {
            return new Gson().fromJson(userData,
                new TypeToken<ArrayList<UserStationData>>(){}.getType());
        }
    }

    public String getPersistedStations() {
        return sp.getString(Constants.STATION_LIST_KEY, "");
    }

    public static String getString(Context context, String key) {
        return SPSingleton.getInstance(context).getSp().getString(key, null);
    }

    public static void persistUserData(Context context, List<UserStationData> userData) {
        getInstance(context).getSp().edit()
            .putString(Constants.USER_DATA, new Gson().toJson(userData))
            .apply();
    }

    public void persistStations(String stationsJsonArr) {
        sp.edit()
            .putString(Constants.STATION_LIST_KEY, stationsJsonArr).apply();
    }
}
