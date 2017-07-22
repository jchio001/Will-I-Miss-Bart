package com.example.jonathan.willimissbart.API.Models.StationModels;

import com.example.jonathan.willimissbart.API.Models.Meta.XMLData;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class StationsResp implements Serializable {
    @SerializedName("root")
    @Expose
    private StationsRoot stationsRoot;

    public StationsRoot getStationsRoot() {
        return stationsRoot;
    }
}
