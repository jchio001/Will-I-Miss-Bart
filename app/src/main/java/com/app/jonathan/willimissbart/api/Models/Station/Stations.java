package com.app.jonathan.willimissbart.api.Models.Station;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Stations {
    @SerializedName("station")
    @Expose
    private ArrayList<Station> stationList;

    public ArrayList<Station> getStationList() {
        return stationList;
    }
}
