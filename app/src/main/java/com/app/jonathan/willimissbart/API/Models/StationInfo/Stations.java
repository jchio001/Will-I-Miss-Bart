package com.app.jonathan.willimissbart.API.Models.StationInfo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class Stations implements Serializable {
    @SerializedName("station")
    @Expose
    private Station station;

    public Station getStation() {
        return station;
    }
}
