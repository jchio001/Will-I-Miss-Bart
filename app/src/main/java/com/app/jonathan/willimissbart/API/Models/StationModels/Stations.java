package com.app.jonathan.willimissbart.API.Models.StationModels;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Stations {
    @SerializedName("station")
    @Expose
    private List<Station> stationList;

    public List<Station> getStationList() {
        return stationList;
    }
}
