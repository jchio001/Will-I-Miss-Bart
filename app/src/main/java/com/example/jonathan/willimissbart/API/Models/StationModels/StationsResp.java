package com.example.jonathan.willimissbart.API.Models.StationModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class StationsResp implements Serializable {
    @SerializedName("?xml")
    @Expose
    private XMLData xmlData;

    @SerializedName("root")
    @Expose
    private StationsRoot stationsRoot;

    public XMLData getXmlData() {
        return xmlData;
    }

    public StationsRoot getStationsRoot() {
        return stationsRoot;
    }
}
