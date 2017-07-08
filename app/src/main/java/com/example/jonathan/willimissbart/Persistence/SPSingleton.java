package com.example.jonathan.willimissbart.Persistence;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;

import com.example.jonathan.willimissbart.Misc.Constants;

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

    public String getUserData() {
        return sp.getString(Constants.USER_DATA, "");
    }

    public String getPersistedStations() {
        return sp.getString(Constants.STATION_LIST_KEY, "");
    }

    public void persistStations(String stationsJsonArr) {
        sp.edit().putString(Constants.STATION_LIST_KEY, stationsJsonArr).commit();
    }

}
