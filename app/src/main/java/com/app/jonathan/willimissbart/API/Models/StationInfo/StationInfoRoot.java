package com.app.jonathan.willimissbart.API.Models.StationInfo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

// Contains specific information about a particular station
public class StationInfoRoot implements Serializable {
    @SerializedName("stations")
    @Expose
    private Stations stations;

    public Stations getStations() {
        return stations;
    }
}
