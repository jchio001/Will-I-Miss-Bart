package com.app.jonathan.willimissbart.Misc;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

import com.app.jonathan.willimissbart.API.APIConstants;
import com.app.jonathan.willimissbart.API.Callbacks.EtdCallback;
import com.app.jonathan.willimissbart.API.Models.StationModels.Station;
import com.app.jonathan.willimissbart.API.RetrofitClient;
import com.app.jonathan.willimissbart.Persistence.Models.UserBartData;
import com.app.jonathan.willimissbart.Persistence.SPSingleton;
import com.app.jonathan.willimissbart.Persistence.StationsSingleton;
import com.app.jonathan.willimissbart.R;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Utils {
    public static boolean noDaysSelected(boolean[] days) {
        for (boolean b : days) {
            if (b) {
                return false;
            }
        }
        return true;
    }

    public static Character directionToUrlParam(String direction) {
        return direction.equals("Both") ? null : direction.charAt(0);
    }

    public static String getUserBartData(Bundle b, Context context) {
        return (b == null) ? SPSingleton.getInstance(context).getUserData() :
                b.getString(Constants.USER_DATA, "");
    }

    public static void loadStations(String stationsJSON) {
        if (StationsSingleton.getInstance().getStationElems().isEmpty()) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Station>>() {
            }.getType();
            List<Station> stations = gson.fromJson(stationsJSON, listType);
            StationsSingleton.getInstance().setStationElems(stations);
        }
    }

    public static List<UserBartData> convertToList(String serializedUserData) {
        if (!serializedUserData.isEmpty()) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<UserBartData>>() {}.getType();
            return gson.fromJson(serializedUserData, listType);
        } else {
            return new ArrayList<>();
        }
    }

    //usually filtered
    public static void fetchEtds(List<UserBartData> userBartData) {
        for (int i = 0; i < userBartData.size(); ++i) {
            UserBartData data = userBartData.get(i);
            RetrofitClient.getInstance()
                    .getMatchingService()
                    .getEtd("etd", APIConstants.API_KEY, 'y', data.getAbbr(),
                            Utils.directionToUrlParam(data.getDirection())
                    )
                    .clone()
                    .enqueue(
                            new EtdCallback()
                                    .setData(userBartData.get(i))
                                    .setIndex(i)
                    );
        }
    }

    public static List<UserBartData> filterBadData(List<UserBartData> userData) {
        Set<String> stationAbbrSet = Sets.newHashSet();
        List<UserBartData> filteredList = Lists.newArrayList();
        for (UserBartData dataElem : userData) {
            if (!dataElem.getAbbr().equals("Select a station")) {
                if (stationAbbrSet.contains(dataElem.getAbbr())) {
                    //can't have duplicates
                    throw new IllegalArgumentException();
                } else {
                    stationAbbrSet.add(dataElem.getAbbr());
                    filteredList.add(dataElem);
                }
            }
        }

        return filteredList;
    }

    public static boolean didDataChange(List<UserBartData> oldUserData,
                                        List<UserBartData> newUserData) {
        if (oldUserData.size() != newUserData.size()) {
            return true;
        }

        for (int i = 0; i < oldUserData.size(); ++i) {

            if (!oldUserData.get(i).equals(newUserData.get(i))) {
                return true;
            }
        }

        return false;
    }

    public static Snackbar showSnackBar(Context context, View parent, int colorId, String message) {
        Snackbar snackbar = Snackbar.make(parent, message, Snackbar.LENGTH_LONG);
        View rootView = snackbar.getView();
        snackbar.getView().setBackgroundColor(context.getResources().getColor(colorId));
        TextView tv = (TextView) rootView.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(context.getResources().getColor(R.color.white));
        snackbar.show();
        return snackbar;
    }
}