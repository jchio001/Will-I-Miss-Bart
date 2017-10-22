package com.app.jonathan.willimissbart.API.Models.Station;

import com.app.jonathan.willimissbart.API.Models.Generic.CDataSection;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class StationsRoot implements Serializable {
    @SerializedName("uri")
    @Expose
    private CDataSection uri;

    @SerializedName("stations")
    @Expose
    private Stations stations;

    public CDataSection getUri() {
        return uri;
    }

    public Stations getStations() {
        return stations;
    }
}
