package com.example.jonathan.willimissbart.API.Models.StationModels;


import com.example.jonathan.willimissbart.API.Models.Generic.Message;
import com.example.jonathan.willimissbart.API.Models.Meta.UriData;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class StationsRoot implements Serializable {
    @SerializedName("uri")
    @Expose
    private UriData uri;

    @SerializedName("stations")
    @Expose
    private Stations stations;


    public UriData getUri() {
        return uri;
    }

    public Stations getStations() {
        return stations;
    }
}
