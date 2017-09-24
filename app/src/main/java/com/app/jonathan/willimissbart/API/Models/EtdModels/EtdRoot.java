package com.app.jonathan.willimissbart.API.Models.EtdModels;

import com.app.jonathan.willimissbart.API.Models.Generic.CDataSection;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class EtdRoot implements Serializable {
    @SerializedName("@id")
    @Expose
    private String id;

    @SerializedName("uri")
    @Expose
    private CDataSection uri;

    @SerializedName("date")
    @Expose
    private String date;

    @SerializedName("station")
    @Expose
    private List<EtdStation> station;

    public String getId() {
        return id;
    }

    public CDataSection getUri() {
        return uri;
    }

    public String getDate() {
        return date;
    }

    public List<EtdStation> getStation() {
        return station;
    }
}
