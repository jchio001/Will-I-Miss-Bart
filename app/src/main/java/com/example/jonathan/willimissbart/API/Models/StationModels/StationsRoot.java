package com.example.jonathan.willimissbart.API.Models.StationModels;


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

    @SerializedName("message")
    @Expose
    private String message;

    public UriData getUri() {
        return uri;
    }

    public Stations getStations() {
        return stations;
    }

    public String getMessage() {
        return message;
    }
}
